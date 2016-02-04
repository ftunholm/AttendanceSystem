<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/pages/master.jsp" %>

            <title>Statistik</title>
            <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/tables.css">
            <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/font-awesome.css">
            <script type="text/javascript">
                $(document).ready(function() {
                    $(function () {
                        $(".datepicker").datepicker({dateFormat: 'yy-mm-dd'});
                    });
                });
            </script>
            <div class="margin-left">
            <form action="/admin/statisticsAlt" method="post">
                <br>
                <label><b>Klass</b></label><br>
                <select name="class_id" required>
                    <c:forEach var="user_class" items="${user_classes}">
                        <c:if test="${class_id == user_class.getId()}">
                            <option value="${user_class.getId()}" selected>${user_class.getName()}</option>
                        </c:if>
                        <c:if test="${class_id != user_class.getId()}">
                            <option value="${user_class.getId()}">${user_class.getName()}</option>
                        </c:if>
                    </c:forEach>
                </select>
                <br><br>
                <label><b>Startdatum</b></label><br>
                <input type="date" value="${startDate}" required name="start_date" class="datepicker classes-date"/>
                <br><br>
                <label><b>Slutdatum</b></label><br>
                <input type="date" value="${endDate}" required name="end_date" class="datepicker classes-date"/>
                <br><br>
                <button class="ma-button "type="submit">Visa närvaro</button>
            </form>
            </div>
            <c:if test="${dlLink != null}">
                <a href="${dlLink}"><button class="ma-button margin-left">Ladda ner som Excel-dokument</button></a><br><br>
            </c:if>

            <div id="table-container">
                <table class="table molk-table">
                    <thead>
                        <tr>
                            <td><i class="fa fa-user"></i>  Namn</td>
                            <td><i class="fa fa-envelope"></i>  E-postadress</td>
                            <td><i class="fa fa-certificate"></i>  Personnummer</td>
                            <td class="center"><b>Närvaro</b></td>
                            <td class="center"><b>Sjuk</b></td>
                            <td class="center"><b>VAB</b></td>
                            <td class="center"><b>Övrig frånvaro</b></td>
                            <td class="center"><b>Ogiltig frånvaro</b></td>
                        </tr>
                    </thead>
                    <tbody>
                    <c:set var="counter" value="${0}"/>
                    <c:forEach var="student" items="${students}">
                        <c:if test="${counter % 2 == 0}">
                            <tr>
                        </c:if>
                        <c:if test="${counter % 2 != 0}">
                            <tr>
                        </c:if>
                            <td>${student.getName()}</td>
                            <td>${student.getEmail()}</td>
                            <td>${student.getPersonNumber()}</td>
                            <td class="center">${attArrayList.get(counter)[0]}</td>
                            <td class="center">${attArrayList.get(counter)[1]}</td>
                            <td class="center">${attArrayList.get(counter)[2]}</td>
                            <td class="center">${attArrayList.get(counter)[3]}</td>
                            <td class="center">${attArrayList.get(counter)[4]}</td>
                        <c:set var="counter" value="${counter + 1}"/>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div> <!-- Close div from master.jsp -->
    </body>
    <!-- Set active element in menu and add site-nav-link -->
    <script>
        document.getElementById("statistikAlt").className = "MAmenuLink activeLink";
        $('.site-nav-links-div').html('<p><a href="../">Startsida</a><label class="site-nav-space"> > </label><c:if test="${user.getLevel() == 1}"><a href="/admin">Admin</a></c:if><c:if test="${user.getLevel() != 1}"><a href="/teacher">Lärare</a></c:if><label class="site-nav-space"> > </label><label class="site-nav-current">Statistik</label></p>');
    </script>
</html>