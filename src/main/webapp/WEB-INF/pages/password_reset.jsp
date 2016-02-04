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
    <form class="pwd-form" action="/reset_password/${token}" method="post">
        <p class="loginFormLabel">Ange nytt lösenord</p>
        <p class="loginFormMessageLabel">${message}</p>
        <input id="new-pwd-first" class="passwordInput" type="password" name="password1" placeholder = "Lösenord">
        <p class="loginFormLabel">Repetera lösenord</p>
        <input id="new-pwd-second" class="passwordInput" type="password" name="password2" placeholder = "Lösenord">
        <button id="new-pwd-btn" class="signInButton" type="submit" value="Login">Ändra lösenord</button>
    </form>
</div>

<!-- Bottom bar -->
<div class="bottomBar"></div>

</body>
</html>
