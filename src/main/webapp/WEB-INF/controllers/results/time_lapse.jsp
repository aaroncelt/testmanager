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
<style type="text/css" media="screen">
@import url("<c:url value='/styles/results/table/charts.css'/>");
@import url("<c:url value='/styles/common/table.css'/>");
@import url("<c:url value='/styles/common/links.css'/>");
@import url("<c:url value='/styles/common/resultState.css'/>");
@import url("<c:url value='/styles/results/time_lapse.css'/>");
</style>
<script>
var passChartCategories = ${chartLineData.categoryString};
var passChartData = ${chartLineData.dataString};
var failChartCategories = ${chartFailData.categoryString};
var failChartData = ${chartFailData.dataString};
</script>
<h1 class="pageName"><fmt:message key="results.time_lapse.heading" /></h1>
<b>Set name: ${setName}</b>
<div class="charts">
    <table>
        <tr>
            <td>
                <div id="chart-pass"></div>
            </td>
            <td>
                <div id="chart-fail"></div>
            </td>
        </tr>
    </table>
</div>
<div class="infobox">
    <form name="filter" action="time_lapse" method="get">
    <input name="setName" type="hidden" value="${setName}">
    <select name="filterKey">
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
    <input type="text" name="filterValue" value="${param.filterValue}">
    Max Column Number: <input name="maxColNum" value="${maxColNum}">
    <input type="submit">
    </form>
    <div id="time-lapse-order">
    Result order: <span></span>
    </div>
</div>
<table class="main">
    <thead>
        <tr>
            <td>Test Name</td>
            <c:forEach var="head" items="${tableHeader}">
                <td>
                    <span title="<c:forEach var="headEnv" items="${head.right}">
                    ${headEnv.key}=${headEnv.value}
                    </c:forEach>">
                    ${head.left}</span>
                </td>
            </c:forEach>
        </tr>
    </thead>
    <c:forEach var="map" items="${map}">
        <c:set var="row" value="${map.value}"/>
        <c:set var="rowStat" value="${row.left}"/>
        <c:set var="rowList" value="${row.right}"/>
        <tr <c:if test="${rowStat == colNum}">class="failingTest"</c:if>>
            <td>${map.key}</td>
            <c:forEach var="test" items="${rowList}">
                <c:choose>
                    <c:when test="${test != null}">
                        <c:set var="cellClass" value=""/>
                        <c:choose>
                            <c:when test="${test.state == 'STARTED'}"><c:set var="cellClass" value="started"/></c:when>
                            <c:when test="${test.state == 'PASSED'}"><c:set var="cellClass" value="passed"/></c:when>
                            <c:when test="${test.state == 'FAILED'}"><c:set var="cellClass" value="failed"/></c:when>
                            <c:when test="${test.state == 'NOT_AVAILABLE'}"><c:set var="cellClass" value="notavailable"/></c:when>
                            <c:when test="${test.state == 'ABORTED'}"><c:set var="cellClass" value="aborted"/></c:when>
                        </c:choose>
                        <td class="${cellClass }" title="${test.errorMessage }">
                            <c:choose>
                                <c:when test="${test.state == 'PASSED' }">
                                    &nbsp;
                                </c:when>
                                <c:when test="${test.errorType != null}">
                                    ${test.errorType} - ${test.errorComment}
                                </c:when>
                                <c:otherwise>
                                    Unanalyzed
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </c:when>
                    <c:otherwise><td>&nbsp;</td></c:otherwise>
                </c:choose>
            </c:forEach>
        </tr>
    </c:forEach>
</table>
