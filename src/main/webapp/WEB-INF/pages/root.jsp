<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
    <head>
        <title>Databasadministration</title>
    </head>
    <body>
        ${dbadminMessage}
        <c:if test="${!rootLoggedIn}">
            <form action="/dbadminlogin" method="post">
                Användarnamn:
                <input type="text" name="login"/>
                Lösenord:
                <input type="password" name="password"/>
                <input type="submit"/>
            </form>
        </c:if>

        <c:if test="${rootLoggedIn}">
            <h1>Skapa databas</h1>
            <h3>Obs! Kommer att radera all befintlig data!</h3>
            <form action="/dbadmin" method="post">
                Användarnamn:
                <input type="text" name="email"/>
                Lösenord:
                <input type="password" name="password"/>
                Bekräfta lösenord:
                <input type="password" name="password2"/>
                <input type="submit" text="Skapa databas"/>
            </form>
        </c:if>

    </body>
</html>
