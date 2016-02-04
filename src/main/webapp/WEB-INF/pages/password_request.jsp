<%@ page import="entities.User" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>

<html>
<head>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/login-page.css" />
    <title>Nytt Lösenord</title>
</head>
<body>

<!-- Top bar -->
<div class="topBar">
    <img class="logo" src="${pageContext.request.contextPath}/resources/img/logo.png" width="362" height="65">
</div>

<!-- Login form -->
<div class="loginContainer">
    <form class="loginForm" action="/request_password" method="post">

        <p class="loginFormLabel">Återställ lösenord</p>
        <p class="loginFormMessageLabel">${message}</p>
        <input class="usernameInput" type="text" name="email" placeholder = "E-mail"><br>
        <button class="signInButton" type="submit" value="Login">Skicka nytt lösenord</button>
    </form>

</div>


<!-- Bottom bar -->
<div class="bottomBar"></div>

</body>
</html>
