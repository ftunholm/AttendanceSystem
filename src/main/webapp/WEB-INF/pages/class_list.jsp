    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ include file="/WEB-INF/pages/master.jsp" %>
    <title>Närvarolista</title>

        <div class="contentContainer">

            <script type="text/javascript">

                    var selected_date = "";
                    var sick_user_id = 0;

                    var users = [
                        <c:forEach items="${users}" var="student">
                        {
                            firstName: "<c:out value="${student.getFirstName()}"/>",
                            lastName: "<c:out value="${student.getLastName()}"/>",
                            email: "<c:out value="${student.getEmail()}"/>",
                            personNumber: "<c:out value="${student.getPersonNumber()}"/>",
                            classId: "<c:out value="${student.getClassId()}"/>",
                            id: "<c:out value="${student.getId()}"/>"
                        },
                        </c:forEach>
                    ];

                    var attendance = [
                        <c:forEach items="${attendance}" var="attendance">
                        {
                            time: "<c:out value="${attendance.getTime()}"/>",
                            userId: "<c:out value="${attendance.getUserId()}"/>",
                            typeId:"<c:out value="${attendance.getTypeId()}"/>",
                            reason: "<c:out value="${attendance.getReason()}"/>"
                        },
                        </c:forEach>
                    ];
                    var attendance_types = [
                        <c:forEach items="${attendance_types}" var="attendance_type">
                        {
                            id: "<c:out value="${attendance_type.getId()}"/>",
                            attendanceTypeName: "<c:out value="${attendance_type.getAttendanceTypeName()}"/>"
                        },
                        </c:forEach>
                    ];

                    $(document).ready(function() {
                        $(".modal_button").bind("click", modalClickHandler);
                        $("#btnExport").click(function(e){
                            var selected_class = $("#classes option:selected").text();
                            var dt = new Date();
                            var day = dt.getDate();
                            var month = dt.getMonth() + 1;
                            var year = dt.getFullYear();
                            var prefix = day + "-" + month + "-" + year;
                            //creating a temporary HTML link element (they support setting file names)
                            var a = document.createElement('a');
                            //getting data from our div that contains the HTML table
                            var data_type = 'data:application/vnd.ms-excel';
                            $myclone = $('#tblData').clone();
                            $removedContent = $myclone.find("td:first-child").remove();
                            $table_html = $myclone[0].outerHTML.replace(/ /g, '%20');
                            a.href = data_type + ', ' + $table_html;
                            //Filename
                            a.download = selected_class + '_' + prefix + '_Närvarolista' + '.xls';
                            //triggering the function
                            document.body.appendChild(a);
                            a.click();
                            document.body.removeChild(a);
                            //just in case, prevent default behaviour
                            e.preventDefault();
                        });

                        $("#btnExportMonth").click(function(e){
                            var selected_class = $("#classes option:selected").text();
                            var dt = new Date();
                            var month = dt.getMonth() + 1;
                            var year = dt.getFullYear();
                            var prefix = month + "-" + year;
                            //creating a temporary HTML link element (they support setting file names)
                            var a = document.createElement('a');
                            //getting data from our div that contains the HTML table
                            var data_type = 'data:application/vnd.ms-excel';
                            $myclone = $('#tblData').clone();
                            $removedContent = $myclone.find("td:first-child").remove();
                            $table_html = $myclone[0].outerHTML.replace(/ /g, '%20');
                            a.href = data_type + ', ' + $table_html;
                            //Filename
                            a.download = selected_class + '_' + prefix + '_Närvarolista' + '.xls';
                            //triggering the function
                            document.body.appendChild(a);
                            a.click();
                            document.body.removeChild(a);
                            //just in case, prevent default behaviour
                            e.preventDefault();
                        });

                        addTable();

                        $('#classes').on('change', function() {
                            addTable();
                        });
                        $(function(){
                            $("#datepicker").datepicker({dateFormat: 'yyyy-mm-dd'});
                        });
                        $('#datepicker').on('change', function() {
                            selected_date = $("#datepicker").val().substring(4, $("#datepicker").val().length);
                            get_attendance(selected_date)
                        });
                    });

                    function addTable() {
                        $("#ma-table").empty();
                        var selected_class_id = $("#classes option:selected").attr('value');
                        var cell, row, table, header;

                        table = document.createElement('table');
                        table.setAttribute('border', '0');
                        table.style.width = '100%';
                        table.className = 'table-bordered';
                        table.setAttribute('id', 'tblData');

                        header = table.createTHead();
                        row = header.insertRow(0);
                        cell = row.insertCell(0);
                        cell.innerHTML = "<b></b>";
                        cell = row.insertCell(1);
                        cell.innerHTML = "Förnamn";
                        cell = row.insertCell(2);
                        cell.innerHTML = "Efternamn";
                        cell = row.insertCell(3);
                        cell.innerHTML = "Personnummer";
                        cell = row.insertCell(4);
                        cell.innerHTML = "Registrerad närvaro";
                        cell = row.insertCell(5);
                        cell.innerHTML = "Registrerad frånvaro";

                        var counter = 1;

                        for (var i = 0; i < users.length; i++) {

                            var att = attendance.filter(function(obj){
                                return obj.userId == users[i].id;
                            });
                            if (users[i].classId == selected_class_id) {
                                if ((typeof att !== 'undefined' && att.length > 0)) {
                                    att = att[0];
                                }
                                else {
                                    att.id = 0;
                                    att.typeId = 5;
                                    att.reason = "";
                                    att.time = "";
                                }

                                row = table.insertRow(counter);
                                cell = row.insertCell(0);

                                var biohazard = "<img class='biohazard_button' style='margin-left: 10px; cursor: hand; cursor: pointer;' id='" + users[i].id + "' src='${pageContext.request.contextPath}/resources/img/biohazard.png' height='30' width='30'>";

                                if (att.typeId == 1) {
                                    cell.innerHTML = "<img style='cursor: hand; cursor: pointer;' class='attendance_button' id='" + users[i].id + "_1' src='${pageContext.request.contextPath}/resources/img/green-dot.png' height='30' width='30'>" + biohazard;
                                }
                                else if (att.typeId > 1 && att.typeId < 5) {
                                    cell.innerHTML = "<img style='cursor: hand; cursor: pointer;' class='attendance_button' id='" + users[i].id + "_2' src='${pageContext.request.contextPath}/resources/img/yellow-dot.png' height='30' width='30'>" + biohazard;
                                }
                                else {
                                    cell.innerHTML = "<img style='cursor: hand; cursor: pointer;' id='" + users[i].id + "' class='attendance_button' src='${pageContext.request.contextPath}/resources/img/red-dot.png' height='30' width='30'>" + biohazard;
                                }

                                if (att.typeId > 1 && att.typeId < 5) {
                                    console.log(att);
                                    var type = attendance_types.filter(function(obj){
                                        return obj.id == att.typeId;
                                    });
                                    console.log(type);
                                    row.setAttribute("id", users[i].id);
                                    row.setAttribute("data-toggle", "tooltip");
                                    row.setAttribute("data-delay", "{'show':'100', 'hide':'100'}")
                                    row.setAttribute("data-html", "true");
                                    row.setAttribute("data-placement", "top");
                                    row.setAttribute("title", "Frånvaroanledning: " + type[0].attendanceTypeName + "\n\n" + "Frånvarobeskrivning: " +  att.reason);
                                }

                                cell = row.insertCell(1);
                                cell.innerHTML = users[i].firstName;
                                cell = row.insertCell(2);
                                cell.innerHTML = users[i].lastName;
                                cell = row.insertCell(3);
                                cell.innerHTML = users[i].personNumber;
                                cell = row.insertCell(4);

                                if (att.time.indexOf(".0") != -1) {
                                    att.time = att.time.slice(0, att.time.length-2);
                                }

                                if (att.typeId == 1) {
                                    cell.innerHTML = att.time;
                                }

                                cell = row.insertCell(5);
                                if (att.typeId > 1) {
                                    cell.innerHTML = att.time;
                                }
                                counter++;
                            }
                        }

                        document.getElementById("ma-table").appendChild(table);
                        $("tr:even").css("background-color", "#eeeeee");
                        $("tr:odd").css("background-color", "#ffffff");

                        //Attendance button onClick listener
                        $(".attendance_button").click(function(){
                            $(".attendance_button").off("click");
                            var type_and_id = $(this).attr('id').split("_");
                            var user_id = type_and_id[0];
                            var type_id = type_and_id[1];

                            if(type_id == 1){
                                type_id = 5;
                            }
                            else {
                                type_id = 1
                            }

                            send(user_id, type_id, "");
                        });

                        $(document).on("click", ".biohazard_button", function(){
                            var user_id = $(this).attr('id');

                            var userId = users.filter(function(obj){
                                return obj.id == user_id;
                            });
                            sick_user_id = user_id;
                            $("#student-name").text(userId[0].firstName + " " + userId[0].lastName);
                        });
                    }

                    function modalClickHandler() {
                        var type_id = $("#type_id option:selected").attr('value');
                        var reason = $("#reason-textarea").val();

                        if (sick_user_id != 0) {
                            send(sick_user_id, type_id, reason);
                        }
                    }

                    function send(user_id, type_id, reason) {
                        console.log("user_id: " + user_id);
                        console.log("type_id: " + type_id);
                        console.log("reason: " + reason);
                        console.log("selected_date: " + selected_date);

                        $.ajax({
                            type: "POST",
                            url: "${pageContext.request.contextPath}/admin/attendance",
                            data: {"user_id" : user_id, "date" : selected_date, "type_id" : type_id, "reason" : reason},
                            dataType : "json",
                            success: function(data){
                                attendance = data;
                                addTable();
                                sick_user_id = 0;
                                $("#reason-textarea").val("");
                                $("#student-name").text("Klicka på student och välj");
                                $("#type_id").val($("#type_id option:first").val());
                                $(".attendance_button").on( "click");

                            },
                            error: function () {
                            }
                        });
                    }
                    function get_attendance(date) {
                        $.ajax({
                            type: "GET",
                            url: "${pageContext.request.contextPath}/admin/attendance/" + date,
                            dataType : "json",
                            success: function(data){
                                attendance = data;
                                addTable();
                            },
                            error: function () {
                            }
                        });
                    }

                    $(function () {
                        $('[data-toggle="tooltip"]').tooltip()
                    });

                    $('body').tooltip({
                        selector: '[rel=tooltip]'
                    });

                    $('#myModal').on('shown.bs.modal', function () {
                        $('#myInput').focus()
                    });

                </script>


            <!--Left content-->
            <div class="contentLeft">
                <label>Klass:</label>

                <select class="classes-select" id="classes">

                    <c:forEach var="user_class" items="${user_classes}">
                        <option value="${user_class.getId()}">${user_class.getName()}</option>
                    </c:forEach>
                </select>

                <!--Choose date-->
                <div class="col-md-4">
                    <div id="datepicker" style="font-size: 11px;"></div>
                </div>

                <!--Knapp-->
                <div>
                    <button class="ma-button" id="btnExport">Exportera till Excel</button><br>
                    <%--<button class="ma-button" id="btnExportMonth" class="btn">Exportera till Excel(månad)</button><br>--%>
                </div>

                <div id="myModal">
                    <br><label class="modal-title" id="student-name">Klicka på student och välj</label>
                </div>

                <label>Frånvaroanledning:</label>

                <select id="type_id">
                    <c:forEach var="option" items="${option_list}">
                        <c:if test="${option.getId() != 1 && option.getId() != 5}">
                            <option value="${option.getId()}">${option.getAttendanceTypeName()}</option>
                        </c:if>
                    </c:forEach>
                </select>

                <div class="classlist-textarea-div">
                    <textarea class="classlist-textarea" id="reason-textarea" placeholder="Kommentar..."></textarea>
                </div>

                <div>
                    <button id="" class="ma-button ma-absent-button btn btn-danger modal_button">Frånvarande</button><br><br>
                </div>



            </div>

            <!--Right content-->
            <div class="contentRight">

                <div class="table-responsive ma-classlist-table" id="ma-table"></div>
                <br>
                <div class="colorsExplained">
                    <img src="${pageContext.request.contextPath}/resources/img/green-dot-large.png" height="16" width="16"> Närvarande
                    <img src="${pageContext.request.contextPath}/resources/img/yellow-dot-large.png" height="16" width="16"> Giltig frånvaro
                    <img src="${pageContext.request.contextPath}/resources/img/red-dot-large.png" height="16" width="16"> Ogiltig frånvaro
                </div>
            </div>

        </div> <!-- Closing div content -->

    </div> <!-- Closing div from master.jsp -->

    </body>

    <!-- Set active element in menu and add site-nav-link -->
    <script>
        document.getElementById("narvarolista").className = "MAmenuLink activeLink";
        $('.site-nav-links-div').html('<p><a href="../">Startsida</a><label class="site-nav-space"> > </label><c:if test="${user.getLevel() == 1}"><a href="/admin">Admin</a></c:if><c:if test="${user.getLevel() != 1}"><a href="/teacher">Lärare</a></c:if><label class="site-nav-space"> > </label><label class="site-nav-current">Närvarolista</label></p>');
    </script>
</html>