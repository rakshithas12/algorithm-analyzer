package com.algorithm.controller;

import com.algorithm.dao.AlgorithmDAO;
import com.algorithm.algorithm.Algorithm;
import com.algorithm.algorithm.AlgorithmRegistry;
import com.algorithm.algorithm.AlgorithmResult;
import com.algorithm.service.AlgorithmService;
import com.algorithm.model.AlgorithmInfo;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AlgorithmServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AlgorithmDAO algorithmDAO;
    private AlgorithmService algorithmService;

    @Override
    public void init() throws ServletException {
        algorithmDAO = new AlgorithmDAO(getServletContext());
        algorithmService = new AlgorithmService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isLoggedIn(request)) {
            response.sendRedirect("login.jsp");
            return;
        }

        String algorithm = request.getParameter("algorithm");
        try {
            AlgorithmInfo info = loadAlgorithmInfo(algorithm);
            setAlgorithmAttributes(request, info);
            request.getRequestDispatcher("input.jsp").forward(request, response);
        } catch (IllegalArgumentException ex) {
            request.setAttribute("error", ex.getMessage());
            request.getRequestDispatcher("home.jsp").forward(request, response);
        } catch (SQLException ex) {
            throw new ServletException("Unable to fetch algorithm steps from database.", ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isLoggedIn(request)) {
            response.sendRedirect("login.jsp");
            return;
        }

        String algorithm = request.getParameter("algorithm");
        AlgorithmInfo info;

        try {
            info = loadAlgorithmInfo(algorithm);
            AlgorithmResult result = algorithmService.execute(algorithm,
                    request.getParameter("numbers"), request.getParameter("searchKey"));

            setAlgorithmAttributes(request, info);
            request.setAttribute("algorithmResult", result);
            request.setAttribute("originalArray", java.util.Arrays.toString(result.getOriginalInput()));
            request.setAttribute("result", result.getDisplayOutput());
        } catch (IllegalArgumentException ex) {
            request.setAttribute("error", ex.getMessage());
            request.setAttribute("algorithm", algorithm);
            try {
                AlgorithmInfo currentInfo = loadAlgorithmInfo(algorithm);
                setAlgorithmAttributes(request, currentInfo);
            } catch (IllegalArgumentException ignored) {
                // The original validation error is clearer for the user.
            } catch (SQLException ignored) {
                // The original validation error is clearer for the user.
            }
            RequestDispatcher dispatcher = request.getRequestDispatcher("input.jsp");
            dispatcher.forward(request, response);
            return;
        } catch (SQLException ex) {
            throw new ServletException("Unable to fetch algorithm steps from database.", ex);
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("result.jsp");
        dispatcher.forward(request, response);
    }

    private boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("username") != null;
    }

    private AlgorithmInfo loadAlgorithmInfo(String algorithm) throws SQLException {
        if (algorithm == null || algorithm.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select an algorithm.");
        }

        AlgorithmInfo info = algorithmDAO.findByKey(algorithm);
        if (info == null) {
            throw new IllegalArgumentException("Please select a valid algorithm.");
        }
        return info;
    }

    private void setAlgorithmAttributes(HttpServletRequest request, AlgorithmInfo info) {
        request.setAttribute("algorithm", info.getAlgorithmKey());
        request.setAttribute("algorithmName", info.getName());
        request.setAttribute("algorithmCategory", info.getCategory());
        request.setAttribute("algorithmDescription", info.getDescription());
        Algorithm algorithm = AlgorithmRegistry.getDefault().get(info.getAlgorithmKey());
        request.setAttribute("complexity", algorithm.getComplexity().getSummary());
        request.setAttribute("complexityNotes", algorithm.getComplexity().getNotes());
        request.setAttribute("algorithmSteps", info.getSteps());
    }
}


