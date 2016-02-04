<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="entities.User" %>

<html>
<meta charset="UTF-8">
<head>
    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />
    <script src="//code.jquery.com/jquery-1.10.2.js"></script>
    <script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
    <script src="https://code.highcharts.com/highcharts.js"></script>
    <script src="https://code.highcharts.com/modules/exporting.js"></script>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>

<!-- Top bar -->
<div class="topBar">
    <img src="${pageContext.request.contextPath}/resources/img/logo.png" class="logo">
    <a href="http://www.osyh.se/" target="_blank"><img src="${pageContext.request.contextPath}/resources/img/icons/ostsvenska.png" class="ostsvenska"></a>
    <a href="http://moodle.molk.se/" target="_blank"><img src="${pageContext.request.contextPath}/resources/img/icons/moodle.png" class="moodle"></a>
    <a href="http://molk.slack.com/" target="_blank"><img src="${pageContext.request.contextPath}/resources/img/icons/slack.png" class="slack"></a>
    <a href="/logout"><img src="${pageContext.request.contextPath}/resources/img/icons/logOut.png" class="logOut"></a>
</div>

<!-- Left menu  -->
    <!-- Set correct background -->
    <div class="MAmenu">
    <img class="MAMenuLogo" src="${pageContext.request.contextPath}/resources/img/<c:if test="${user.getLevel() == 1}">adminMenuLogo.png</c:if><c:if test="${user.getLevel() != 1}">teacherMenuLogo.png</c:if>"
    <!-- Menu items -->
    <!-- Visas både för lärare och admin -->
    <div class="MAmenuLink" id="narvarolista">
        <img src="${pageContext.request.contextPath}/resources/img/icons/narvarolista.png">
        <a href="/admin/classlist">Närvarolista</a>
    </div>
    <div class="MAmenuLink" id="statistik">
        <img src="${pageContext.request.contextPath}/resources/img/icons/statistik.png">
        <a href="/admin/statistics">Statistik</a>
    </div>
        <div class="MAmenuLink" id="statistikAlt">
            <img src="${pageContext.request.contextPath}/resources/img/icons/klasslistor.png">
            <a href="/admin/statisticsAlt">Statistik för export</a>
        </div>
    <div class="MAmenuLink" id="klasslistor">
        <img src="${pageContext.request.contextPath}/resources/img/icons/klasslistor.png">
        <a href="/admin/admin_students">Klasslistor</a>
    </div>
    <br>
    <!-- Visa bara för admin -->
    <c:if test="${user.getLevel() == 1}">
        <div class="MAmenuLink" id="menu_users">
            <img src="${pageContext.request.contextPath}/resources/img/icons/klasslistor.png">
            <a href="/admin/admin_users">Användare</a>
        </div>
        <div class="MAmenuLink" id="laggtillnyanvandare">
            <img src="${pageContext.request.contextPath}/resources/img/icons/laggtillnyanvandare.png">
            <a href="/admin/invite">Lägg till student</a>
        </div>
        <div class="MAmenuLink" id="kurser">
            <img src="${pageContext.request.contextPath}/resources/img/icons/kurser.png">
            <a href="/admin/classes">Redigera klasser</a>
        </div>
    </c:if>
</div>

<!-- Nav links -->
<div class="site-nav-links-div">
    <p>
        <a href="../">Startsida</a>
        <label class="site-nav-space"> > </label>
        <c:if test="${user.getLevel() == 1}">
            <label class="site-nav-current">Admin</label>
        </c:if>
        <c:if test="${user.getLevel() != 1}">
            <label class="site-nav-current">Lärare</label>
        </c:if>
    </p>
</div>

<!-- Bottom bar -->
<div class="bottomBar"></div>

<!-- Main content -->
<div class="mainDiv">
<!-- Starting point for other pages... -->
