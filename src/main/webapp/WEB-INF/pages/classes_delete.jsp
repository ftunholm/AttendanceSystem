<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/pages/master.jsp" %>
<%@ page import="entities.Classes" %>
<%@ page import="entities.User" %>
<%@ page import="java.util.ArrayList" %>

<h2 class="margin-left">Du vill radera följande klass:</h2>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/classes_delete.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/tables.css">
<title>Radera klass</title>

<table class="table">
  <thead>
  <tr>
    <th>Namn</th>
    <th>Startdatum</th>
    <th>Slutdatum</th>
  </tr>
  </thead>
  <tbody>
    <tr>
      <td>${klass.getName()}</td>
      <td>${klass.getStartDate()}</td>
      <td>${klass.getEndDate()}</td>
    </tr>
  </tbody>
</table>

<h2 class="margin-left">Detta kommer även att radera följande elever och deras närvaro:</h2>

<table class="table">
  <thead>
  <tr>
    <th>Namn</th>
    <th>Personnummer</th>
    <th>Epost</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach items="${students}" var="student">
    <tr>
      <td>${student.getName()}</td>
      <td>${student.getPersonNumber()}</td>
      <td>${student.getEmail()}</td>
    </tr>
  </c:forEach>
  </tbody>
</table>

<form action="/admin/deleteclass" method="post" class="center">
  <input type="hidden" name="class_id" value="${klass.getId()}">
  <input type="hidden" name="confirm_delete" value="1">
  <button class="create-class-button margin-left" id="del-class-button" type="submit">Bekräfta radering</button>
</form>

</div>
</div>
</body>

<!-- Set active element in menu and add site-nav-link -->
<script>
  document.getElementById("kurser").className = "MAmenuLink activeLink";
  $('.site-nav-links-div').html('<p><a href="../">Startsida</a><label class="site-nav-space"> > </label><a href="/admin">Admin</a><label class="site-nav-space"> > </label><a href="/admin/classes">Redigera klasser</a><label class="site-nav-space"> > </label><label class="site-nav-current">Ta bort klass</label></p>');
</script>
</html>