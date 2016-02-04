
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ include file="/WEB-INF/pages/master.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="entities.User" %>

<title>Klasslistor</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/font-awesome.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/tables.css">

<!-- Modal -->
<div class="modal" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">

        </div>
    </div>
</div>

<form method="post" action="/findClass">
    <div class="knappe">
            <ul class="center">
                <c:forEach var="user_class" items="${all_classes}">
                        <button style="margin-bottom: 10px;" class="classlist-button" type="submit" name="class_id" value="${user_class.id}">${user_class.name}</button>
                </c:forEach>
            </ul>
    </div>
</form>

<h2 class="classlist-h">${current_class.name}</h2>
<input type="hidden" name="id" value="${user.id}">
<c:if test="${not empty users}" >
<table class="table molk-table">
        <tr>
            <th><i class="fa fa-user"></i>  Namn</th>
            <th><i class="fa fa-envelope"></i>  E-postadress</th>
            <th><i class="fa fa-certificate"></i>  Personnummer</th>
            <th><i class="fa fa-home"></i>  Adress</th>
            <th><i class="fa fa-phone"></i>  Tel.nr</th>
            <c:if test="${user.getLevel() == 1}">
                <th class="center">Ändra</th>
                <th class="center">Ta bort</th>
            </c:if>
        </tr>

        </tr>
    <c:forEach items="${users}" var="student">
        <tr>
            <td>${student.firstName} ${student.lastName}</td>
            <td>${student.email}</td>
            <td>${student.personNumber}</td>
            <td>${student.address}</td>
            <td>${student.phoneNumber}</td>
            <c:if test="${user.getLevel() == 1}">
                <td class="center"><a class="edit-class-icon" data-toggle="modal" data-target="#myModal" href="/admin/admin_students/user/${student.id}"><i class="fa fa-pencil"></i></a></td>
                <td class="center"><a class="edit-class-icon" data-toggle="modal" data-target="#myModal" href="/admin/admin_students/delete/${student.id}"><i class="fa fa-trash"></i></a></td>
            </c:if>
        </tr>
    </c:forEach>
    </table>

</table>
</c:if>
    <c:if test="${not empty inactive_users}" >
        <h2 class="classlist-h">Inactive users</h2>
<table class="table molk-table">
    <tr>
        <th><i class="fa fa-user"></i>  Namn</th>
        <th><i class="fa fa-envelope"></i>  E-postadress</th>
        <th><i class="fa fa-certificate"></i>  Personnummer</th>
        <th><i class="fa fa-home"></i>  Adress</th>
        <th><i class="fa fa-phone"></i>  Tel.nr</th>
        <c:if test="${user.getLevel() == 1}">
            <th class="center"><i class="fa fa-pencil"></i></th>
            <th class="center"><i class="fa fa-trash"></i></th>
        </c:if>
    </tr>

    <c:forEach items="${inactive_users}" var="student">
        <tr>
            <td>${student.firstName} ${student.lastName}</td>
            <td>${student.email}</td>
            <td>${student.personNumber}</td>
            <td>${student.address}</td>
            <td>${student.phoneNumber}</td>
            <c:if test="${user.getLevel() == 1}">
                <td class="center"><a class="edit-class-icon" data-toggle="modal" data-target="#myModal" href="/admin/admin_students/user/${student.id}"><i class="fa fa-pencil"></i></a></td>
                <td class="center"><a class="edit-class-icon" data-toggle="modal" data-target="#myModal" href="/admin/admin_students/delete/${student.id}"><i class="fa fa-trash"></i></a></td>
            </c:if>
        </tr>
    </c:forEach>
</table>
    </c:if>
        <!-- Set active element in menu and add site-nav-link -->
        <script>
            document.getElementById("klasslistor").className = "MAmenuLink activeLink";
            $('.site-nav-links-div').html('<p><a href="../">Startsida</a><label class="site-nav-space"> > </label><c:if test="${user.getLevel() == 1}"><a href="/admin">Admin</a></c:if><c:if test="${user.getLevel() != 1}"><a href="/teacher">Lärare</a></c:if><label class="site-nav-space"> > </label><label class="site-nav-current">Klasslistor</label></p>');
        </script>
    </body>
</html>
