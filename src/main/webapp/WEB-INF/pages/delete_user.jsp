<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.io.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/pages/master.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: David
  Date: 2016-01-11
  Time: 14:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Ta bort användare</title>
        <script src="//code.jquery.com/jquery-1.10.2.js"></script>
        <script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
        <script src="https://code.highcharts.com/highcharts.js"></script>
        <script src="https://code.highcharts.com/modules/exporting.js"></script>
        <link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/style.css">    </head>
    <body>
    <div class="contentContainer">
        <div class="modal-header">
            <h4 class="modal-title" id="myModalLabel">Ta bort användare</h4>
        </div>

        <div class="modal-body">
            <h3>Är ni säker på att ni vill ta bort ${user_delete.getFirstName()} ${user_delete.getLastName()}?</h3>
            <p><span class="glyphicon glyphicon-alert"></span> Användaren kommer tas bort från databasen <span class="glyphicon glyphicon-alert"></span></p>
            <p>Om ni endast vill inaktivera en användare kan ni klicka på 'Editera'.</p>

        </div>

        <div class="modal-footer">
            <form method="post" action="/admin/delete_user">
                <a href="/admin/admin_students/"><button type="button" class="btn btn-default">Stäng</button></a>
                <input type="hidden" name="id" value="${user_delete.getId()}">
                <button type="submit" class="btn btn-danger">Ta bort</button>
            </form>
        </div>

        <script>
            document.getElementById("klasslistor").className = "MAmenuLink activeLink";
            $('.site-nav-links-div').html('<p><a href="../">Startsida</a><label class="site-nav-space"> > </label><a href="/admin">Admin</a><label class="site-nav-space"> > </label><a href="/admin/admin_students">Klasslistor</a><label class="site-nav-space"> > </label><label class="site-nav-current">Ta bort klass</label></p>');
        </script>
    </body>
</html>
