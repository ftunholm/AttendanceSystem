<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/pages/master.jsp" %>

            <title>Statistik</title>
            <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/tables.css">

            <div class="content-container-stats">
                <!-- Statistics -->
                <div id="container" style="width:99%; height:400px;"></div>

                <!-- Label -->
                <div class="stats-label">
                    <h3>Antal frånvaro över 9 dagar</h3>
                </div>

                <!-- Tabell -->
                <div class="ma-classlist-table ma-stats-table" id="table-container">
                </div>
            </div>

        </div> <!-- Close div from master.jsp -->

        <script type="text/javascript">
            $(function () {
                var users_statistics = [
                    <c:forEach items="${users}" var="user">
                    {
                        firstName: "<c:out value="${user.getFirstName()}"/>",
                        lastName: "<c:out value="${user.getLastName()}"/>",
                        classId: "<c:out value="${user.getClassId()}"/>",
                        id: "<c:out value="${user.getId()}"/>"
                    },
                    </c:forEach>
                ];

                var attendance_year = [
                    <c:forEach items="${attendance_year}" var="item">
                    {
                        timestamp: "<c:out value="${item.getAttendanceTimestamp()}"/>",
                        userId: "<c:out value="${item.getUserId()}"/>",
                        typeId:"<c:out value="${item.getTypeId()}"/>"
                    },
                    </c:forEach>
                ];

                var all_data = [];
                for (var i = 0; i < users_statistics.length; i++) {
                    var attend = attendance_year.filter(function(obj){
                        return obj.userId == users_statistics[i].id
                    });

                    var temp_months = [];

                    for (var j = 1; j < 13; j++) {
                        var total_away_hours = 0;
                        if (j != 7) {
                            var temp = attend.filter(function(obj){
                                return parseInt(obj.timestamp.slice(5, 7).replace("0", "")) == j;
                            });
                            temp_months.push(temp);
                        }
                    }

                    all_data[i] = {data: [], name: ""};
                    for (var j = 0; j < temp_months.length; j++) {
                        var temp_month = temp_months[j];
                        total_away_hours = 0;
                        for (var k = 0; k < temp_month.length; k++) {
                            if (temp_month[k].typeId > 1) {
                                total_away_hours += 8;
                            }
                        }
                        all_data[i].data.push(total_away_hours);
                    }
                    all_data[i].name = users_statistics[i].firstName + " " + users_statistics[i].lastName;
                }

                $('#container').highcharts({
                    title: {
                        text: 'Frånvarostatistik Elever',
                        x: -20 //center
                    },
                    xAxis: {
                        categories: ['Jan', 'Feb', 'Mar', 'Apr', 'Maj', 'Jun',
                            'Aug', 'Sep', 'Okt', 'Nov', 'Dec']
                    },
                    yAxis: {
                        title: {
                            text: 'Frånvaro (Timmar)'
                        },
                        plotLines: [{
                            value: 0,
                            width: 1,
                            color: '#808080'
                        }]
                    },
                    tooltip: {
                        valueSuffix: 'Timmar'
                    },
                    legend: {
                        layout: 'vertical',
                        align: 'right',
                        verticalAlign: 'middle',
                        borderWidth: 0
                    },
                    series: all_data
                });
            });

            var users = [
                <c:forEach items="${users}" var="user">
                {
                    firstName: "<c:out value="${user.getFirstName()}"/>",
                    lastName: "<c:out value="${user.getLastName()}"/>",
                    email: "<c:out value="${user.getEmail()}"/>",
                    personNumber: "<c:out value="${user.getPersonNumber()}"/>",
                    classId: "<c:out value="${user.getClassId()}"/>",
                    id: "<c:out value="${user.getId()}"/>"
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
            var attendance_warn = [
                <c:forEach items="${attendance_warn}" var="attendance_warn">
                {
                    total: "<c:out value="${attendance_warn.getTotal()}"/>",
                    id: "<c:out value="${attendance_warn.getId()}"/>",
                    userId: "<c:out value="${attendance_warn.getUserId()}"/>"
                },
                </c:forEach>
            ];

            $( document ).ready(function() {
                addTable();
            });

            function addTable() {
                $("#table-container").empty();
                var cell, row, table, header;

                table = document.createElement('table');
                table.setAttribute('border', '1');
                table.setAttribute('class', 'table')
                table.style.width = '100%';

                header = table.createTHead();
                row = header.insertRow(0);
                cell = row.insertCell(0);
                cell.innerHTML = "<b>Förnamn</b>";
                cell = row.insertCell(1);
                cell.innerHTML = "<b>Efternamn</b>";
                cell = row.insertCell(2);
                cell.innerHTML = "<b>E-postadress</b>";
                cell = row.insertCell(3);
                cell.innerHTML = "<b>Total Frånvaro</b>";

                for (var i = 0; i < users.length; i++) {
                    var counter = 1;

                    row = table.insertRow(counter);

                    cell = row.insertCell(0);
                    cell.innerHTML = users[i].firstName;
                    cell = row.insertCell(1);
                    cell.innerHTML = users[i].lastName;
                    cell = row.insertCell(2);
                    cell.innerHTML = users[i].email;
                    cell = row.insertCell(3);
                    for (var j = 0; j < attendance_warn.length; j++) {
                        if (users[i].id == attendance_warn[j].userId) {
                            cell.innerHTML = attendance_warn[j].total;
                        }
                    }
                    counter++;
                }
                document.getElementById("table-container").appendChild(table);
                $("tr:even").css("background-color", "#eeeeee");
                $("tr:odd").css("background-color", "#ffffff");

                //Attendance button onClick listener
                $(".attendance_button").click(function(){
                    send($(this).attr('id'));
                });
            }

        </script>
        <!-- Set active element in menu and add site-nav-link -->
        <script>
            document.getElementById("statistik").className = "MAmenuLink activeLink";
            $('.site-nav-links-div').html('<p><a href="../">Startsida</a><label class="site-nav-space"> > </label><c:if test="${user.getLevel() == 1}"><a href="/admin">Admin</a></c:if><c:if test="${user.getLevel() != 1}"><a href="/teacher">Lärare</a></c:if><label class="site-nav-space"> > </label><label class="site-nav-current">Statistik</label></p>');
        </script>
    </body>
</html>