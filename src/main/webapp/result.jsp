<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute("username") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    List<String> algorithmSteps = (List<String>) request.getAttribute("algorithmSteps");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Algorithm Analyzer - Result</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <header class="topbar">
        <div>
            <h1>Result</h1>
            <p><%= request.getAttribute("algorithmName") %> steps were fetched from the database.</p>
        </div>
        <a class="ghost-btn" href="home.jsp">Dashboard</a>
    </header>

    <main class="result-page">
        <section class="result-box">
            <span class="badge">Analysis Complete</span>
            <h2><%= request.getAttribute("algorithmName") %></h2>

            <div class="result-row">
                <span>Input</span>
                <strong><%= request.getAttribute("originalArray") %></strong>
            </div>

            <div class="result-row highlight">
                <span>Output</span>
                <strong><%= request.getAttribute("result") %></strong>
            </div>

            <div class="result-row">
                <span>Complexity</span>
                <strong><%= request.getAttribute("complexity") %></strong>
            </div>
            <% if (request.getAttribute("complexityNotes") != null) { %>
                <p class="muted"><%= request.getAttribute("complexityNotes") %></p>
            <% } %>

            <div class="steps-block">
                <h3>Database Steps</h3>
                <ol class="steps-list compact">
                    <% if (algorithmSteps != null) {
                        for (String step : algorithmSteps) { %>
                            <li><%= step %></li>
                    <%  }
                    } %>
                </ol>
            </div>

            <div class="actions">
                <a class="primary-btn link-btn" href="home.jsp">Try Another Algorithm</a>
            </div>
        </section>
    </main>
</body>
</html>
