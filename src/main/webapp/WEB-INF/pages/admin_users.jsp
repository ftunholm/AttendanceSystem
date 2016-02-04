
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ include file="/WEB-INF/pages/master.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="entities.User" %>

<title>Administrera användare</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/font-awesome.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/tables.css">

<!-- Modal -->
<div class="modal" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">

        </div>
    </div>
</div>

<h2 class="classlist-h">Lärare</h2>
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
    <c:forEach items="${teachers}" var="teacher">
        <c:if test="${teacher.getActive()}">
            <tr>
                <td>${teacher.getName()}</td>
                <td>${teacher.getEmail()}</td>
                <td>${teacher.getPersonNumber()}</td>
                <td>${teacher.getAddress()}</td>
                <td>${teacher.getPhoneNumber()}</td>
                <c:if test="${user.getLevel() == 1}">
                    <td class="center"><a class="edit-class-icon" data-toggle="modal" data-target="#myModal" href="/admin/admin_students/user/${teacher.id}"><i class="fa fa-pencil"></i></a></td>
                    <td class="center"><a class="edit-class-icon" data-toggle="modal" data-target="#myModal" href="/admin/admin_students/delete/${teacher.id}"><i class="fa fa-trash"></i></a></td>
                </c:if>
            </tr>
        </c:if>
    </c:forEach>
</table>

<h2 class="classlist-h">Administratörer</h2>
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
    <c:forEach items="${admins}" var="admin">
        <c:if test="${admin.getActive()}">
            <tr>
                <td>${admin.getName()}</td>
                <td>${admin.getEmail()}</td>
                <td>${admin.getPersonNumber()}</td>
                <td>${admin.getAddress()}</td>
                <td>${admin.getPhoneNumber()}</td>
                <c:if test="${user.getLevel() == 1}">
                    <td class="center"><a class="edit-class-icon" data-toggle="modal" data-target="#myModal" href="/admin/admin_students/user/${admin.id}"><i class="fa fa-pencil"></i></a></td>
                    <td class="center"><a class="edit-class-icon" data-toggle="modal" data-target="#myModal" href="/admin/admin_students/delete/${admin.id}"><i class="fa fa-trash"></i></a></td>
                </c:if>
            </tr>
        </c:if>
    </c:forEach>
</table>

<h2 class="classlist-h">Inaktiva lärare</h2>
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
    <c:forEach items="${teachers}" var="teacher">
        <c:if test="${!teacher.getActive()}">
            <tr>
                <td>${teacher.getName()}</td>
                <td>${teacher.getEmail()}</td>
                <td>${teacher.getPersonNumber()}</td>
                <td>${teacher.getAddress()}</td>
                <td>${teacher.getPhoneNumber()}</td>
                <c:if test="${user.getLevel() == 1}">
                    <td class="center"><a class="edit-class-icon" data-toggle="modal" data-target="#myModal" href="/admin/admin_students/user/${teacher.id}"><i class="fa fa-pencil"></i></a></td>
                    <td class="center"><a class="edit-class-icon" data-toggle="modal" data-target="#myModal" href="/admin/admin_students/delete/${teacher.id}"><i class="fa fa-trash"></i></a></td>
                </c:if>
            </tr>
        </c:if>
    </c:forEach>
</table>

<h2 class="classlist-h">Inaktiva administratörer</h2>
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
    <c:forEach items="${admins}" var="admin">
        <c:if test="${!admin.getActive()}">
            <tr>
                <td>${admin.getName()}</td>
                <td>${admin.getEmail()}</td>
                <td>${admin.getPersonNumber()}</td>
                <td>${admin.getAddress()}</td>
                <td>${admin.getPhoneNumber()}</td>
                <c:if test="${user.getLevel() == 1}">
                    <td class="center"><a class="edit-class-icon" data-toggle="modal" data-target="#myModal" href="/admin/admin_students/user/${admin.id}"><i class="fa fa-pencil"></i></a></td>
                    <td class="center"><a class="edit-class-icon" data-toggle="modal" data-target="#myModal" href="/admin/admin_students/delete/${admin.id}"><i class="fa fa-trash"></i></a></td>
                </c:if>
            </tr>
        </c:if>
    </c:forEach>
</table>

        <!-- Set active element in menu and add site-nav-link -->
        <script>
            document.getElementById("menu_users").className = "MAmenuLink activeLink";
            $('.site-nav-links-div').html('<p><a href="../">Startsida</a><label class="site-nav-space"> > </label><c:if test="${user.getLevel() == 1}"><a href="/admin">Admin</a></c:if><c:if test="${user.getLevel() != 1}"><a href="/teacher">Lärare</a></c:if><label class="site-nav-space"> > </label><label class="site-nav-current">Klasslistor</label></p>');
        </script>
    </body>
</html>
