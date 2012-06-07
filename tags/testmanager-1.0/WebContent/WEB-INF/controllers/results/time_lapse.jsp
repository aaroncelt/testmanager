<!--
  TestManager - test tracking and management system.
  Copyright (C) 2012  Istvan Pamer

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
-->
<%@ include file="/WEB-INF/controllers/include.jsp"%>

<style type="text/css">
td {
    border-top: dotted 1px gray;
    text-align: center;
    font-size: small;
}
.started { background-color: white; }
.aborted { background-color: grey; }
.passed { background-color: #64E986; }
.failed { background-color: #F75D59; }
.notavailable { background-color: #77BFC7; }
.pageName {
    padding-left: 50px;
}
.headingDiv {
    padding-left: 100px;
    padding-bottom: 10px;
}
.tableHead {
    font-weight: bold;
    font-size: 1.3em;
}
.testName {
    text-align: left;
}
.filterLink {
    cursor: pointer;
    color: #FD9B11;
}
.failingTest {
    background-color: #E8E8FF;
}
.infobox {
    text-align: left;
    vertical-align: top;
    padding: 30px;
}
</style>

<script>
var passChartCategories = ${chartLineData.categoryString};
var passChartData = ${chartLineData.dataString};
var failChartCategories = ${chartFailData.categoryString};
var failChartData = ${chartFailData.dataString};

$(document).ready(function() {
    createLineChart('chart-pass', '', 'Pass Rate (%)', passChartCategories, passChartData);
    createLineChart('chart-fail', '', 'Issue Number', failChartCategories, failChartData);
});

function filterSelected() {
    var temp = $('#filterLink').attr('href') + "&filterKey=" + $('#headFilterSelect').val() + "&filterValue=" + $('#headFilterArea').val()
             + "&maxColNum=" + $('#maxColNum').val();
    $('#filterLink').attr('href', temp);
}
</script>

<h1 class="pageName"><fmt:message key="results.time_lapse.heading" /></h1>

<table width="95%" border="0" cellspacing="0" cellpadding="5" align="center">
    <tr>
        <td class="infobox">
            <b>${setName}</b><br/>
            <select id="headFilterSelect">
                <c:forEach var="headEnv" items="${tableHeader[0].right}">
                <c:choose>
                    <c:when test="${headEnv.key == param.filterKey}">
                        <option value="${headEnv.key}" selected="selected">${headEnv.key}</option>
                    </c:when>
                    <c:otherwise>
                        <option value="${headEnv.key}">${headEnv.key}</option>
                    </c:otherwise>
                </c:choose>
                </c:forEach>
            </select>
            <textarea id="headFilterArea" rows="1" cols="15">${param.filterValue}</textarea>
            <a id="filterLink" class="filterLink" href="time_lapse?setName=${setName}" onclick="filterSelected();">Filter</a>
            <c:if test="${param.filterValue != null && !empty param.filterValue}"> / <a class="filterLink" href="time_lapse?setName=${setName}">Remove Filter</a></c:if>

            <br/><br/>
            Max Column Number: <input id="maxColNum" value="${maxColNum}"></input>
        </td>
        <td colspan="${colNum}">
            <div id="chart-pass" style="width: 700px; height: 200px; margin: 0 auto"></div>
            <div id="chart-fail" style="width: 700px; height: 200px; margin: 0 auto"></div>
        </td>
    </tr>

    <tr>
        <td class="tableHead">Test Name</td>
        <c:forEach var="head" items="${tableHeader}">
        <td>
            <span class="tableHead">${head.left}</span><br/>
            <c:forEach var="headEnv" items="${head.right}">
            ${headEnv.key}=${headEnv.value}
            </c:forEach>
        </td>
        </c:forEach>
    </tr>

    <c:forEach var="map" items="${map}">
    <c:set var="row" value="${map.value}"/>
    <c:set var="rowStat" value="${row.left}"/>
    <c:set var="rowList" value="${row.right}"/>
    <tr <c:if test="${rowStat == colNum}">class="failingTest"</c:if>>
        <td class="testName">${map.key}</td>
        <c:forEach var="test" items="${rowList}">
            <c:choose>
                <c:when test="${test != null}">
                    <c:choose>
                        <c:when test="${test.state == 'STARTED'}"><td class='started'>${test.state}</c:when>
                        <c:when test="${test.state == 'PASSED'}"><td class='passed'>${test.state}</c:when>
                        <c:when test="${test.state == 'FAILED'}"><td class='failed'>${test.state}</c:when>
                        <c:when test="${test.state == 'NOT_AVAILABLE'}"><td class='notavailable'>${test.state}</c:when>
                        <c:when test="${test.state == 'ABORTED'}"><td class='aborted'>${test.state}</c:when>
                        <c:otherwise><td>${test.state}</c:otherwise>
                    </c:choose>
                    <c:if test="${test.errorType != null}"> - ${test.errorType}</c:if>
                    </td>
                </c:when>
                <c:otherwise><td>&nbsp;</td></c:otherwise>
            </c:choose>
        </c:forEach>
    </tr>
    </c:forEach>
</table>
