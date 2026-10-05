package com.algorithm.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.algorithm.algorithm.Algorithm;
import com.algorithm.algorithm.AlgorithmRegistry;
import com.algorithm.algorithm.AlgorithmResult;
import com.algorithm.service.AlgorithmService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonParseException;

/** Servlet-based JSON API; all executions delegate to AlgorithmService. */
public final class AlgorithmApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int MAX_BODY_CHARS = 65536;
    private final Gson gson = new GsonBuilder().create();
    private final AlgorithmRegistry registry = AlgorithmRegistry.getDefault();
    private final AlgorithmService service = new AlgorithmService(registry);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        prepareResponse(response);
        String path = request.getPathInfo();
        if (path == null || path.isEmpty() || "/".equals(path)) {
            List<AlgorithmMetadataResponse> algorithms = new ArrayList<AlgorithmMetadataResponse>();
            for (Algorithm algorithm : registry.getAll())
                algorithms.add(new AlgorithmMetadataResponse(algorithm, registry));
            writeJson(response, HttpServletResponse.SC_OK, algorithms);
            return;
        }

        String id = singleId(path);
        if (id == null) {
            writeError(response, HttpServletResponse.SC_NOT_FOUND, "API endpoint not found.");
            return;
        }
        try {
            writeJson(response, HttpServletResponse.SC_OK,
                    new AlgorithmMetadataResponse(registry.getByApiId(id), registry));
        } catch (IllegalArgumentException ex) {
            writeError(response, HttpServletResponse.SC_NOT_FOUND, "Unknown algorithm.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        prepareResponse(response);
        String[] route = executeRoute(request.getPathInfo());
        if (route == null) {
            writeError(response, HttpServletResponse.SC_NOT_FOUND, "API endpoint not found.");
            return;
        }

        final Algorithm algorithm;
        try {
            algorithm = registry.getByApiId(route[0]);
        } catch (IllegalArgumentException ex) {
            writeError(response, HttpServletResponse.SC_NOT_FOUND, "Unknown algorithm.");
            return;
        }

        try {
            String body = readBody(request);
            if (body.trim().isEmpty()) throw new ApiInputException("Request body is required.");
            JsonElement parsed;
            try {
                parsed = JsonParser.parseString(body);
            } catch (JsonParseException ex) {
                throw new ApiInputException("Malformed JSON request body.");
            }
            if (!parsed.isJsonObject()) throw new ApiInputException("Request body must be a JSON object.");
            JsonObject object = parsed.getAsJsonObject();
            int[] values = readValues(object);
            Integer target = object.has("target") ? readInteger(object.get("target"), "target") : null;

            AlgorithmResult result = service.execute(algorithm.getId(), values, target);
            writeJson(response, HttpServletResponse.SC_OK,
                    new ApiExecutionResponse(algorithm, result, registry));
        } catch (IllegalArgumentException ex) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        } catch (RuntimeException ex) {
            getServletContext().log("Algorithm API execution failed.", ex);
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
        }
    }

    private int[] readValues(JsonObject object) {
        JsonElement valuesElement = object.get("values");
        if (valuesElement == null || !valuesElement.isJsonArray())
            throw new ApiInputException("Field 'values' must be a non-empty array of integers.");
        JsonArray valuesArray = valuesElement.getAsJsonArray();
        if (valuesArray.size() == 0)
            throw new ApiInputException("Field 'values' must be a non-empty array of integers.");
        int[] values = new int[valuesArray.size()];
        for (int i = 0; i < valuesArray.size(); i++) values[i] = readInteger(valuesArray.get(i), "values[" + i + "]");
        return values;
    }

    private int readInteger(JsonElement element, String field) {
        if (element == null || element.isJsonNull() || !element.isJsonPrimitive()
                || !element.getAsJsonPrimitive().isNumber())
            throw new ApiInputException("Field '" + field + "' must be a 32-bit integer.");
        try {
            return new BigDecimal(element.getAsString()).intValueExact();
        } catch (NumberFormatException | ArithmeticException ex) {
            throw new ApiInputException("Field '" + field + "' must be a 32-bit integer.");
        }
    }

    private String readBody(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder body = new StringBuilder();
        char[] buffer = new char[2048];
        int count;
        while ((count = reader.read(buffer)) != -1) {
            if (body.length() + count > MAX_BODY_CHARS)
                throw new ApiInputException("Request body is too large.");
            body.append(buffer, 0, count);
        }
        return body.toString();
    }

    private String singleId(String path) {
        String normalized = trimSlashes(path);
        if (normalized.isEmpty() || normalized.indexOf('/') >= 0) return null;
        return normalized;
    }

    private String[] executeRoute(String path) {
        String normalized = trimSlashes(path);
        String[] parts = normalized.split("/", -1);
        return parts.length == 2 && !parts[0].isEmpty() && "execute".equals(parts[1])
                ? new String[] { parts[0] } : null;
    }

    private String trimSlashes(String path) {
        if (path == null) return "";
        int start = path.startsWith("/") ? 1 : 0;
        int end = path.endsWith("/") ? path.length() - 1 : path.length();
        return path.substring(start, Math.max(start, end));
    }

    private void prepareResponse(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
    }

    private void writeJson(HttpServletResponse response, int status, Object entity) throws IOException {
        response.setStatus(status);
        response.getWriter().write(gson.toJson(entity));
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        writeJson(response, status, new ApiErrorResponse(message));
    }

    private static final class ApiInputException extends IllegalArgumentException {
        private static final long serialVersionUID = 1L;
        ApiInputException(String message) { super(message); }
    }
}
