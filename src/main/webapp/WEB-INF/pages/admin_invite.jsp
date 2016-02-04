<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/pages/master.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<title>Lägg till användare</title>
<div class="margin-left">
  <h2>Bjud in en ny användare</h2>
  <label><b>E-postadress</b></label>
  <br>
  <input id="email" type="text" required>
  <br><br>
  <label><b>Användarnivå</b></label>
  <br>
  <select class="invite-select" id="user_level">
    <c:forEach var="user_level" items="${user_levels}">
      <option value="${user_level.getId()}">${user_level.getName()}</option>
    </c:forEach>
  </select>
  <br><br>
  <label><b>Klass</b></label><br>
  <select class="invite-select" id="classes">
    <c:forEach var="user_class" items="${user_classes}">
      <option value="${user_class.getId()}">${user_class.getName()}</option>
    </c:forEach>
  </select>
  <br><br><br>
  <button id="send_invite" onclick="send()" class="invite-button">Skicka Inbjudan</button>
  <h3 id="response-message" style="color:#d00000"></h3>
</div>

</div> <!-- slut div från master.jsp -->
</body>

<script>
  $("#user_level").on('change',function(){
    var x = $('#user_level').val();
    if(x == 1 || x == 2) {
      if(!$("#classes option[value='0']").length) {
        $("#classes").append("<option value='0'>-</option>");
        $('#classes option[value="0"]').prop('selected',true);
      }
      $('#classes').prop("disabled", true);
    }
    else if(x == 3) {
      $("#classes option[value='0']").remove();
      $('#classes').prop( "disabled", false );
    }
  });

</script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script type="text/javascript">
  function send() {

    var user_level = $("#user_level option:selected").attr('value');
    var email = $("#email").val();
    var user_class = $("#classes option:selected").attr('value');
    $.ajax({
      type: "POST",
      url: "${pageContext.request.contextPath}/admin/sendinvite",
      data: {"email": email, "user_level" : user_level, "user_class": user_class},
      success: function(data){
        $("#email").val("");
        $("#user_level").val($("#user_level option:first").val());
        $("#classes").val($("#classes option:first").val());
        $('#response-message').text(data);
      },
      error: function () {
        $('#response-message').text("Det gick tyvärr inte att skicka injudan, Vänligen försök igen senare.");
      }
    });
  };
</script>
<!-- Set active element in menu and add site-nav-link -->
<script>
  document.getElementById("laggtillnyanvandare").className = "MAmenuLink activeLink";
  $('.site-nav-links-div').html('<p><a href="../">Startsida</a><label class="site-nav-space"> > </label><a href="/admin">Admin</a><label class="site-nav-space"> > </label><label class="site-nav-current">Lägg till student</label></p>');
</script>
</html>
