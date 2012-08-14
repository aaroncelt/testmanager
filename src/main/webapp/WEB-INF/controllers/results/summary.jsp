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
.sectionHeader {
    border-top: solid 1px black;
    text-align: left;
    font-weight: bold;
    padding-left: 15px;
}
.started { background-color: white; }
.aborted { background-color: grey; }
.passed { background-color: #64E986; }
.failed { background-color: #F75D59; }
.notavailable { background-color: #77BFC7; }
.deleteLink {
    cursor: pointer;
    color: #FD9B11;
}
.pageName {
    padding-left: 50px;
}
.infobox {
    text-align: left;
    vertical-align: top;
    padding: 30px;
}
.filterLink {
    cursor: pointer;
    color: #FD9B11;
}
.formattedCell {
    text-align: left;
}
.subHeading {
    padding-top: 40px;
}
</style>

<script>
var datas = [${chartPieData.dataString}];

$(document).ready(function() {
    createPieChart('chart-main', '', datas);
});

function filterSelected() {
    var temp = $('#filterLink').attr('href') + "?filterKey=" + $('#headFilterSelect').val() + "&filterValue=" + $('#headFilterArea').val();
    $('#filterLink').attr('href', temp);
}
</script>

<h1 class="pageName"><fmt:message key="results.summary.heading" /></h1>

<table width="95%" border="0" cellspacing="0" cellpadding="5" align="center">
    <tr>
        <td class="infobox" colspan="2">
            <select id="headFilterSelect">
                <c:forEach var="env" items="${envSet}">
                <c:choose>
                    <c:when test="${env == param.filterKey}">
                        <option value="${env}" selected="selected">${env}</option>
                    </c:when>
                    <c:otherwise>
                        <option value="${env}">${env}</option>
                    </c:otherwise>
                </c:choose>
                </c:forEach>
            </select>
            <textarea id="headFilterArea" rows="1" cols="15">${param.filterValue}</textarea>
            <a id="filterLink" class="filterLink" href="summary" onclick="filterSelected();">Filter</a>

            <br/><br/>
            <table>
                <tr>
                    <th>Finished Tests</th>
                    <th>Passed</th>
                    <th>Failed</th>
                    <th>N/A</th>
                </tr>
                <tr>
                    <td>${sumTestNumber}</td>
                    <td class='passed'>${sumPassed} (${fn:substringBefore(sumPassed / sumTestNumber * 100, '.')}%)</td>
                    <td class='failed'>${sumFailed} (${fn:substringBefore(sumFailed / sumTestNumber * 100, '.')}%)</td>
                    <td class='notavailable'>${sumNA} (${fn:substringBefore(sumNA / sumTestNumber * 100, '.')}%)</td>
                </tr>
            </table>

            <br/><br/>
            Sum Execution Time: ${sumTestTime}
        </td>
        <td class="infobox" colspan="4">
            <div id="chart-main" style="width: 500px; height: 250px; margin: 0 auto"></div>
        </td>
    </tr>

    <tr>
        <th>Set Run</th>
        <th>Environment</th>
        <th>Finished Tests</th>
        <th>Passed</th>
        <th>Failed</th>
        <th>N/A</th>
    </tr>
    <c:forEach var="map" items="${map}">
    <tr>
        <td class="sectionHeader" colspan="6">${map.key}</td>
    </tr>
    <c:set var="set" value="${map.value}"/>
    <tr>
        <td class="formattedCell"><a href="table?setId=${set.id}"><fmt:formatDate value="${set.startDate}" pattern="yyyy/MM/dd HH:mm"/> (${set.displayExecutionTime})</a></td>
        <td class="formattedCell">
           <c:forEach var="env" items="${set.environment}">
               ${env.key}=${env.value}
           </c:forEach>
        </td>
        <td>${set.finishedTestNumber}</td>
        <td class='passed'>${set.resultStatPassed} (${fn:substringBefore(set.resultStatPassed / set.finishedTestNumber * 100, '.')}%)</td>
        <td class='failed'>${set.resultStatFailed} (${fn:substringBefore(set.resultStatFailed / set.finishedTestNumber * 100, '.')}%)</td>
        <td class='notavailable'>${set.resultStatNA} (${fn:substringBefore(set.resultStatNA / set.finishedTestNumber * 100, '.')}%)</td>
    </tr>
    </c:forEach>

    <!-- Detailed Failed Info -->
    <tr>
        <th class="subHeading">Comment</th>
        <th class="subHeading">Error Message</th>
        <th class="subHeading">Count</th>
        <th colspan="3"></th>
    </tr>
    <c:forEach var="map" items="${mapFailed}">
    <tr>
        <td class="sectionHeader" colspan="7">${map.key}</td>
    </tr>
    <c:forEach var="commentEntry" items="${map.value}">
    <c:set var="commentPair" value="${commentEntry.key}"/>
    <tr>
        <td class="formattedCell">${commentPair.right}</td>
        <td class="formattedCell">${commentPair.left}</td>
        <td>${commentEntry.value}</td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    </c:forEach>
    </c:forEach>

</table>
