<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Algorithm Analyzer - Login</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body class="login-page">
    <main class="login-wrapper">
        <section class="login-box">
            <h1>Algorithm Analyzer</h1>
            <p class="muted">Sign in to explore algorithm results.</p>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert"><%= request.getAttribute("error") %></div>
            <% } %>

            <form action="login" method="post" class="form">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" placeholder="admin" required>

                <label for="password">Password</label>
                <input type="password" id="password" name="password" placeholder="admin" required>

                <button type="submit" class="primary-btn">Login</button>
            </form>
        </section>
    </main>
</body>
</html>
