<%@ page import="java.io.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="${pageContext.request.contextPath}/resources/avtal/Användaravtal för användning av Mölk Utbildning.docx" />

<html>
<head>
    <title>Registrering</title>
    <script src="//code.jquery.com/jquery-1.10.2.js"></script>
    <script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
    <script src="https://code.highcharts.com/highcharts.js"></script>
    <script src="https://code.highcharts.com/modules/exporting.js"></script>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/style.css">


    <!-- TODO: ADD AUTOMATIC UPDATING OF CLASSES? -->
    <title>Registera Användare</title>
</head>
<body>

<!-- Top bar -->
<div class="top-bar-reg">
    <img class="logo-reg" src="${pageContext.request.contextPath}/resources/img/logo.png" width="362" height="65">
</div>

<!-- Bottom bar -->
<div class="bottomBar"></div>

<div class="register-container">
    <h1>Välkommen!</h1>
    <h2>Fyll i forumläret för att registrera dig</h2>
    <h1>${error_count}</h1>
    <h3>${firstname_error_message}</h3>
    <h3>${lastname_error_message}</h3>
    <h3>${pn_error_message}</h3>
    <h3>${phonenumber_error_message}</h3>
    <h3>${error_message}</h3>
    <form method="post" action="/registrationhandler">
        <label>Förnamn ${firstname_error}</label><br>
        <input type="text" required value="${firstname}" class="form-control" name="firstname"><br><br>

        <label>Efternamn ${lastname_error}</label><br>
        <input type="text" required value="${lastname}" class="form-control" name="lastname"><br><br>

        <label><b>E-mail: ${email}</b></label>
        <input type="hidden" name="email" value="${email}">
        <br><br>

        <label>Lösenord ${password_error}</label><br>
        <input type="password" class="form-control" name="password" required><br><br>
        <label>Bekräfta lösenord ${password_error}</label><br>
        <input type="password" class="form-control" name="confirmpassword" required><br><br>
        <label>Personnr ${pn_error}</label><br>

        <input type="text" value="${pn1}" id="pnr-first" class="personnumber" name="personnumber_first" maxlength="6" required>
        -
        <input type="text" value="${pn2}" id="pnr-second" class="personnumber" name="personnumber_last" maxlength="4" required><br><br>

        <label><b>Klass: ${user_class.getName()}</b></label>
        <input type="hidden" name="class" value="${user_class.getId()}">
        <br><br>

        <label>Adress</label><br>
        <input type="text" value="${address}" class="form-control" name="address" required><br><br>

        <label>Telefonnummer ${phonenumber_error}</label><br>
        <input type="text" value="${phonenumber}" class="form-control" name="phonenumber" pattern="\d*" required><br><br>
        <input type="hidden" name="token" value="${token}">
        <label>
            <input type="checkbox" id="agreeCheckbox">
            Jag godkänner <a href="${pageContext.request.contextPath}/terms"
                             onclick="javascript:void window.open('/terms','1432716049191','width=700,height=500,toolbar=0,menubar=0,location=0,status=1,scrollbars=1,resizable=1,left=0,top=0');return false;">användaravtalet för användning av Mölk Utbildning:s nät- och systemresurser.</a>
        </label><br><br>

        <%--<input disabled="disabled" id="registerButton" type="submit" class="ma-button" value="Registrera"/>--%>
        <button disabled="disabled" id="registerButton" type="submit" class="ma-button">Registrera</button>
    </form>
</div>

<script type='text/javascript'>

    $(".personnumber").keyup(function(){
        if(this.value.length == this.maxLength){
            $(this).next(".personnumber").focus();
        }
    });

    $('#agreeCheckbox').click(function(){
        if($(this).is(':checked')){
            console.log("is checked");
            $('#registerButton').removeAttr("disabled");
        }else {
            console.log("is not checked");
            $('#registerButton').attr("disabled", "true");
        }
    });
</script>

</body>
</html>
