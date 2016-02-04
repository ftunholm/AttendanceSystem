<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>

<html>
<head>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/login-page.css">
    <title>MÖLK Attend</title>
</head>
<body>

<!-- Top bar -->
<div class="topBar">
    <img class="logo" src="${pageContext.request.contextPath}/resources/img/logo.png" width="362" height="65">
</div>

<!-- Login form -->
<div class="loginContainer">
    <form class="loginForm" action="/login" method="post">
        <p class="loginFormLabel">MÖLK Attend Login</p>
        <input class="usernameInput" type="text" name="email" placeholder = "E-mail" autofocus>
        <input class="passwordInput" type="password" name="password" placeholder = "Lösenord">
        <button class="signInButton" type="submit" value="Login">Logga in</button>
    </form>
        <a href="/request_password"/><button class="resetPasswordButton">Glömt Lösenord?</button></a>
</div>

<!-- Bottom bar -->
<div class="bottomBar"></div>

</body>
</html>
