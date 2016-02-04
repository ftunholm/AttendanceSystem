<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.io.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/pages/master.jsp" %>
    <title>Uppdatera Användare</title>

    <h1>${error_count}</h1>
    <h3>${firstname_error_message}</h3>
    <h3>${lastname_error_message}</h3>
    <h3>${pn_error_message}</h3>
    <h3>${phonenumber_error_message}</h3>
    <h3>${error_message}</h3>
    <script>
        function validateForm(event){
            var fnmae = document.submitform.firstName.value;
            var lname = document.submitform.lastName.value;
            var email = document.submitform.email.value;
            var pnumber = document.submitform.personNumber.value;
            var phone = document.submitform.phoneNumber.value;

            if(! /^[a-zA-ZåäöÅÄÖ-]{2,30}$/.test(fnmae))
            {
                alert("Ogiltig Förnamn");
                event.preventDefault();
                return false;
            }
            else if(! /^[a-zA-ZåäöÅÄÖ-]{2,30}$/.test(lname))
            {
                alert("Ogiltig Efternamn");
                event.preventDefault();
                return false;
            }
            else if(! /.@./.test(email))
            {
                alert("Ogiltig email-address");
                event.preventDefault();
                return false;
            }
            else if(! /^\d{6}-\d{4}$/.test(pnumber))
            {
                alert("Ogiltig Personnummer");
                event.preventDefault();
                return false;
            }
            else if (! /^\d{10,12}$/.test(phone))
            {
                alert("Ogiltig Telefonnumner");
                event.preventDefault();
                return false;
            }
            else
            return true;


        }</script>
</head>
<body>
<div class="contentContainer">
    <form name="submitform" onsubmit="validateForm(event)" method="post" action="/admin/update_user">
                <label>Förnamn</label><br>
                <input type="hidden" name="id" value="${user_edit.getId()}">
                <input type="text" required value="${user_edit.getFirstName()}" class="form-control" name="firstName"><br><br>
                <label>Efternamn</label><br>
                <input type="text" required value="${user_edit.getLastName()}" class="form-control" name="lastName"><br><br>
                <label>Email</label><br>
                <input type="text" required value="${user_edit.getEmail()}" class="form-control" name="email"><br><br>
                <label>Personnr</label><br>
                <input type="text" value="${user_edit.getPersonNumber()}" class="personNumber" name="personNumber" size="11" maxlength="11" required><br><br>

                <c:if test="${user_edit.getLevel() == 3}">
                <label>Klass </label><br>
                <select name="class">
                    <c:forEach var="user_class" items="${all_classes}">
                        <c:if test="${user_class.getId() != current_class.getId()}">
                            <option name="class_id" value="${user_class.getId()}">${user_class.getName()}</option>
                        </c:if>
                        <c:if test="${user_class.getId() == current_class.getId()}">
                            <option selected name="class_id" value="${user_class.getId()}">${user_class.getName()}</option>
                        </c:if>
                    </c:forEach>
                </select>
                </c:if>
                <c:if test="${user_edit.getLevel() != 3}">
                    <input type="hidden" name="class" value="0"/>
                </c:if>
                <br><br>
                <label>Adress</label><br>
                <input type="text" value="${user_edit.getAddress()}" class="form-control" name="address" required><br><br>
                <label>Telefon nummer</label><br>
                <input type="text" value="${user_edit.getPhoneNumber()}" class="form-control" name="phoneNumber" pattern="\d*" required><br><br>
            <label>Aktiv</label><br>
            <select name="active">
                <c:if test="${user_edit.getActive()}">
                    <option selected value="1">Aktiv</option>
                    <option value="0">Inaktivera</option>
                </c:if>
                <c:if test="${!user_edit.getActive()}">
                    <option value="1">Aktiv</option>
                    <option selected value="0">Inaktivera</option>
                </c:if>
            </select><br><br>
        <button type="submit" class="ma-button">Spara/Stäng</button>
    </form>
</div>
<%--<div class="modal-footer">--%>
    <%--<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>--%>
<%--</div>--%>

<!-- Set active element in menu and add site-nav-link -->
<script>
    document.getElementById("klasslistor").className = "MAmenuLink activeLink";
    $('.site-nav-links-div').html('<p><a href="../">Startsida</a><label class="site-nav-space"> > </label><a href="/admin">Admin</a><label class="site-nav-space"> > </label><a href="/admin/admin_students">Klasslistor</a><label class="site-nav-space"> > </label><label class="site-nav-current">Ändra elev</label></p>');
</script>
</body>

