<%@ page import="java.util.ArrayList" %>
<%@ page import="entities.AttendanceType" %>
<%@ page import="entities.User" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Anmäl frånvaro</title>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
    <link rel="stylesheet" type="text/css" href="../../resources/css/style.css">

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

<h1>${userId.firstName} ${userId.lastName}</h1>
<label class="message-label">${message}</label>

<div class="student-container">
    <label>Frånvaroanledning</label><br>
    <select id="type_id">
        <c:forEach var="option" items="${option_list}">
            <c:if test="${option.getId() != 1 && option.getId() != 5}">
                <option value="${option.getId()}">${option.getAttendanceTypeName()}</option>
            </c:if>
        </c:forEach>
    </select><br><br>
    <label>Fritext</label><br>
    <textarea class="student-reason-textarea" id="reason-textarea"></textarea><br><br>
    <button id="notattending_button" onclick="notAttending()" class="student-button">Anmäl frånvaro</button><br>
    <a href="/logout"><button class="student-button student-button-logout">Logga ut</button></a>
    <label class="message-label" id="response-message"></label>
</div>

</body>

<script src="//code.jquery.com/jquery-1.10.2.js"></script>
<script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>


<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script type="text/javascript">

    var reason;
    var type_id;

    //Ajax post-request
    function notAttending() {
        reason = $("#reason-textarea").val();
        type_id = $("#type_id option:selected").attr('value');
        send();
    }
    function attending() {
        reason = "";
        type_id = 1;
        send();
    }

    function send() {
        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/attendance",
            data: {"reason" : reason, "type_id" : type_id},
            success: function(data){
                $("#reason-textarea").val("");
                $("#type_id").val($("#type_id option:first").val());
                $('#response-message').text(data);
            },
            error: function () {
                $('#response-message').text("Det gick tyvärr inte att skicka injudan, Vänligen försök igen senare.");
            }
        });
    };
</script>

</html>
