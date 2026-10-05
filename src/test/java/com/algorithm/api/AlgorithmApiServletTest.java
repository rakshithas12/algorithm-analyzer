package com.algorithm.api;

import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AlgorithmApiServletTest {
    private final AlgorithmApiServlet servlet = new AlgorithmApiServlet();

    @Test public void getListsAllRegisteredAlgorithms() throws Exception {
        CapturedResponse response = get("/");
        assertEquals(200, response.status);
        JsonArray algorithms = JsonParser.parseString(response.body()).getAsJsonArray();
        assertEquals(6, algorithms.size());
        assertEquals("bubble-sort", algorithms.get(0).getAsJsonObject().get("id").getAsString());
    }

    @Test public void getOneAlgorithmIncludesComplexityAndBinarySearchNote() throws Exception {
        CapturedResponse response = get("/binary-search");
        assertEquals(200, response.status);
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        assertEquals("SEARCHING", json.get("category").getAsString());
        assertTrue(json.get("notes").getAsString().contains("sorts first"));
        assertEquals("O(log n) search", json.getAsJsonObject("complexity").get("worst").getAsString());
    }

    @Test public void unknownAlgorithmReturnsNotFound() throws Exception {
        CapturedResponse response = get("/made-up");
        assertEquals(404, response.status);
        assertFalse(JsonParser.parseString(response.body()).getAsJsonObject().get("success").getAsBoolean());
    }

    @Test public void executeBubbleSortReturnsStructuredResult() throws Exception {
        CapturedResponse response = post("/bubble-sort/execute", "{\"values\":[5,2,8,1]}");
        assertEquals(200, response.status);
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        assertEquals("bubble-sort", json.get("algorithm").getAsString());
        assertEquals("[1,2,5,8]", json.get("output").getAsJsonArray().toString());
    }

    @Test public void executeLinearSearchIncludesIndex() throws Exception {
        CapturedResponse response = post("/linear-search/execute", "{\"values\":[5,2,8,1],\"target\":8}");
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        assertEquals(200, response.status);
        assertEquals(2, json.get("searchIndex").getAsInt());
        assertTrue(json.get("found").getAsBoolean());
    }

    @Test public void executeBinarySearchSortsBeforeSearching() throws Exception {
        CapturedResponse response = post("/binary-search/execute", "{\"values\":[5,2,8,1],\"target\":8}");
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        assertEquals(200, response.status);
        assertEquals("[1,2,5,8]", json.get("sortedValues").getAsJsonArray().toString());
        assertEquals(3, json.get("searchIndex").getAsInt());
    }

    @Test public void executeAdditionReturnsSum() throws Exception {
        CapturedResponse response = post("/addition/execute", "{\"values\":[10,20]}");
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        assertEquals(200, response.status);
        assertEquals(30, json.get("sum").getAsInt());
        assertEquals(30, json.get("output").getAsInt());
    }

    @Test public void invalidValuesAndAdditionArityReturnBadRequest() throws Exception {
        assertEquals(400, post("/bubble-sort/execute", "{\"target\":1}").status);
        assertEquals(400, post("/bubble-sort/execute", "{\"values\":[]}").status);
        assertEquals(400, post("/bubble-sort/execute", "{\"values\":[\"1\"]}").status);
        assertEquals(400, post("/bubble-sort/execute", "{\"values\":[1,1.5]}").status);
        assertEquals(400, post("/addition/execute", "{\"values\":[1,2,3]}").status);
    }

    @Test public void missingTargetAndUnnecessaryTargetReturnBadRequest() throws Exception {
        assertEquals(400, post("/linear-search/execute", "{\"values\":[1,2]}").status);
        assertEquals(400, post("/bubble-sort/execute", "{\"values\":[1,2],\"target\":2}").status);
    }

    @Test public void malformedOrMissingBodyAndUnknownPostAlgorithmReturnErrors() throws Exception {
        assertEquals(400, post("/bubble-sort/execute", "{").status);
        assertEquals(400, post("/bubble-sort/execute", "").status);
        assertEquals(404, post("/unknown/execute", "{\"values\":[1]}").status);
    }

    private CapturedResponse get(String path) throws Exception {
        CapturedResponse response = new CapturedResponse();
        servlet.doGet(request(path, ""), response.proxy());
        return response;
    }

    private CapturedResponse post(String path, String body) throws Exception {
        CapturedResponse response = new CapturedResponse();
        servlet.doPost(request(path, body), response.proxy());
        return response;
    }

    private HttpServletRequest request(final String path, final String body) {
        return (HttpServletRequest) Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(),
                new Class<?>[] { HttpServletRequest.class }, new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if ("getPathInfo".equals(method.getName())) return path;
                        if ("getReader".equals(method.getName())) return new BufferedReader(new StringReader(body));
                        return defaultValue(method.getReturnType());
                    }
                });
    }

    private Object defaultValue(Class<?> type) {
        if (!type.isPrimitive() || type == Void.TYPE) return null;
        if (type == Boolean.TYPE) return false;
        if (type == Character.TYPE) return '\0';
        if (type == Byte.TYPE) return (byte) 0;
        if (type == Short.TYPE) return (short) 0;
        if (type == Integer.TYPE) return 0;
        if (type == Long.TYPE) return 0L;
        if (type == Float.TYPE) return 0F;
        if (type == Double.TYPE) return 0D;
        return null;
    }

    private static final class CapturedResponse implements InvocationHandler {
        private int status = 200;
        private final StringWriter buffer = new StringWriter();
        private final PrintWriter writer = new PrintWriter(buffer);

        HttpServletResponse proxy() {
            return (HttpServletResponse) Proxy.newProxyInstance(HttpServletResponse.class.getClassLoader(),
                    new Class<?>[] { HttpServletResponse.class }, this);
        }
        public Object invoke(Object proxy, Method method, Object[] args) {
            if ("setStatus".equals(method.getName())) { status = (Integer) args[0]; return null; }
            if ("getWriter".equals(method.getName())) return writer;
            return null;
        }
        String body() { writer.flush(); return buffer.toString(); }
    }
}
