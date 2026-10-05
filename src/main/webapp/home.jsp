<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.algorithm.algorithm.AlgorithmRegistry" %>
<%
    if (session.getAttribute("username") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Algorithm Analyzer - Dashboard</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <header class="topbar">
        <div>
            <h1>Algorithm Analyzer</h1>
            <p>Welcome, <strong><%= session.getAttribute("username") %></strong></p>
        </div>
        <a class="ghost-btn" href="login.jsp">Logout</a>
    </header>

    <main class="page-shell">
        <section class="intro">
            <h2>Select an Algorithm</h2>
            <p>Choose a card. The algorithm steps are loaded from the database.</p>
        </section>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert page-alert"><%= request.getAttribute("error") %></div>
        <% } %>

        <section class="card-grid">
            <a class="algo-card" href="algo?algorithm=bubbleSort">
                <span class="badge">Sort</span>
                <h3>Bubble Sort</h3>
                <p>Repeatedly swaps adjacent values until the array is sorted.</p>
                <strong><%= AlgorithmRegistry.getDefault().get("bubbleSort").getComplexity().getWorstCaseTime() %></strong>
            </a>

            <a class="algo-card" href="algo?algorithm=linearSearch">
                <span class="badge search">Search</span>
                <h3>Linear Search</h3>
                <p>Checks each element from left to right for the target value.</p>
                <strong><%= AlgorithmRegistry.getDefault().get("linearSearch").getComplexity().getWorstCaseTime() %></strong>
            </a>

            <a class="algo-card" href="algo?algorithm=selectionSort">
                <span class="badge">Sort</span>
                <h3>Selection Sort</h3>
                <p>Selects the smallest remaining value and places it next.</p>
                <strong><%= AlgorithmRegistry.getDefault().get("selectionSort").getComplexity().getWorstCaseTime() %></strong>
            </a>

            <a class="algo-card" href="algo?algorithm=insertionSort">
                <span class="badge">Sort</span>
                <h3>Insertion Sort</h3>
                <p>Builds the sorted array one element at a time.</p>
                <strong><%= AlgorithmRegistry.getDefault().get("insertionSort").getComplexity().getWorstCaseTime() %></strong>
            </a>

            <a class="algo-card" href="algo?algorithm=binarySearch">
                <span class="badge search">Search</span>
                <h3>Binary Search</h3>
                <p>Sorts the input, then halves the search range repeatedly.</p>
                <strong>Search <%= AlgorithmRegistry.getDefault().get("binarySearch").getComplexity().getWorstCaseTime() %>; sorts input first</strong>
            </a>

            <a class="algo-card" href="algo?algorithm=addition">
                <span class="badge">Math</span>
                <h3>Addition</h3>
                <p>Adds two numbers using a, b, and sum = a + b steps.</p>
                <strong><%= AlgorithmRegistry.getDefault().get("addition").getComplexity().getWorstCaseTime() %></strong>
            </a>
        </section>
    </main>
</body>
</html>
