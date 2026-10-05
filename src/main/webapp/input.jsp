<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute("username") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    String algorithm = (String) request.getAttribute("algorithm");
    if (algorithm == null || algorithm.trim().isEmpty()) {
        algorithm = request.getParameter("algorithm");
    }
    if (algorithm == null || algorithm.trim().isEmpty()) {
        response.sendRedirect("home.jsp");
        return;
    }

    String algorithmName = (String) request.getAttribute("algorithmName");
    String algorithmDescription = (String) request.getAttribute("algorithmDescription");
    String complexity = (String) request.getAttribute("complexity");
    List<String> algorithmSteps = (List<String>) request.getAttribute("algorithmSteps");
    boolean isSearch = "linearSearch".equals(algorithm) || "binarySearch".equals(algorithm);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= algorithmName %> - Input</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <header class="topbar">
        <div>
            <h1><%= algorithmName %></h1>
            <p>Steps fetched from database. Complexity: <strong><%= complexity %></strong></p>
            <% if (request.getAttribute("complexityNotes") != null) { %>
                <p class="muted"><%= request.getAttribute("complexityNotes") %></p>
            <% } %>
        </div>
        <a class="ghost-btn" href="home.jsp">Dashboard</a>
    </header>

    <main class="input-layout">
        <section class="panel">
            <% if (request.getAttribute("error") != null) { %>
                <div class="alert"><%= request.getAttribute("error") %></div>
            <% } %>

            <form action="algo" method="post" class="form">
                <input type="hidden" name="algorithm" value="<%= algorithm %>">

                <label for="numbers">Numbers</label>
                <input type="text" id="numbers" name="numbers" placeholder="Example: 8, 3, 5, 1, 9" required>

                <% if (isSearch) { %>
                    <label for="searchKey">Element to Search</label>
                    <input type="number" id="searchKey" name="searchKey" placeholder="Example: 5" required>
                <% } %>

                <button type="submit" class="primary-btn">Analyze</button>
            </form>
        </section>

        <section class="panel steps-panel">
            <span class="badge"><%= complexity %></span>
            <h2>Algorithm Steps</h2>
            <p class="muted"><%= algorithmDescription %></p>

            <ol class="steps-list">
                <% if (algorithmSteps != null) {
                    for (String step : algorithmSteps) { %>
                        <li><%= step %></li>
                <%  }
                } %>
            </ol>
        </section>
    </main>
</body>
</html>
