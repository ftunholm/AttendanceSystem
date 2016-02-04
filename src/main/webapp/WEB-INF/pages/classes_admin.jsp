<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/pages/master.jsp" %>
<%@ page import="entities.Classes" %>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/classes_delete.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/tables.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/font-awesome.css">
<title>Redigera klasser</title>
  <h3 class="margin-left" style="color: #d00000">OBSERVERA!<br><br>
    ALLA STUDENTER SOM TILLHÖR DEN SPECIFIKA KLASSEN RADERAS OM DU RADERAR KLASSEN.<br>
      LIKASÅ RADERAS ALLA DESSA STUDENTERS NÄRVARO/FRÅNVARO-STATISTIK.
  </h3>


  <form action="/admin/addclass" method="post">
    <input class="margin-left" type="text" name="name">
    <input type="date" name="start_date" class="datepicker classes-date"/>
    <input type="date" name="end_date" class="datepicker classes-date"/>
    <button class="create-class-button ma-button" type="submit">Skapa klass</button>
  </form>

  <table class="table" id="classes-table">
  <tr>
    <th><i class="fa fa-users"></i> Klass</th>
    <th><i class="fa fa-calendar"></i> Startdatum</th>
    <th><i class="fa fa-calendar"></i> Slutdatum</th>
    <th class="center"><i class="fa fa-trash"></i> Radera klass</th>
  </tr>
  <c:forEach items="${classes}" var="klass">
    <tr>
      <td>${klass.getName()}</td>
      <td>${klass.getStartDate()}</td>
      <td>${klass.getEndDate()}</td>
      <td class="center">
        <form action="/admin/deleteclass" method="post">
          <input type="hidden" name="class_id" value="${klass.getId()}"/>
          <input type="hidden" name="confirm_delete" value="0"/>
          <button class="classes-delete-button" type="submit"><i class="fa fa-trash fa-lg"></i></button>
        </form>
      </td>
    </tr>
  </c:forEach>
</table>

</div>

  <script type="text/javascript">
    $(document).ready(function() {
      $(function () {
        $(".datepicker").datepicker({dateFormat: 'yy-mm-dd'});
      });
    });
  </script>
  <!-- Set active element in menu and add site-nav-link -->
  <script>
    document.getElementById("kurser").className = "MAmenuLink activeLink";
    $('.site-nav-links-div').html('<p><a href="../">Startsida</a><label class="site-nav-space"> > </label><a href="/admin">Admin</a><label class="site-nav-space"> > </label><label class="site-nav-current">Redigera klasser</label></p>');
  </script>
</body>
</html>