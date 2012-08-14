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

<c:choose>
    <c:when test="${!empty param.selecteds}">
        <script>var dataSelecteds = ${param.selecteds};</script>
    </c:when>
    <c:otherwise>
        <script>var dataSelecteds = {};</script>
    </c:otherwise>
</c:choose>
<script>
var datas = [${chartPieData.dataString}];

$(document).ready(function() {
    createPieChart('chart-main', '', datas);
    // select previous filters
    for (var i in dataSelecteds) {
        for (var key in dataSelecteds[i]) {
            $('#' + key + ' .' + i).click(); //.attr('checked', true);
        }
    }
});

function filterSelected() {
    var temp = $('#filterLink').attr('href') + "?filterKey=" + $('#headFilterSelect').val() + "&filterValue=" + $('#headFilterArea').val();
    $('#filterLink').attr('href', temp);
}

var selecteds = {};

function addSelected(addId, removeId, row, col) {
    var key = row + '-' + col;
    var text = $('#' + row + '-' + col + ' > div').text();
    addTextToSelecteds(addId, key, text);
    removeTextFromSelecteds(removeId, key);
    refreshSelecteds(addId);
    refreshSelecteds(removeId);
}
function addTextToSelecteds(id, key, text) {
    if (!(id in selecteds)) {
        selecteds[id] = {};
    }
    if (!(key in selecteds[id])) {
        selecteds[id][key] = text;
    }
}
function removeTextFromSelecteds(id, key) {
    if (id in selecteds) {
        delete selecteds[id][key];
    }
}
function refreshSelecteds(id) {
    $('#' + id).empty();
    for (var i in selecteds[id]) {
       $('#' + id).append(selecteds[id][i] + " <span onclick=\"reset('" + id + "', '" + i + "');\" class='filterLink'><b>(Remove)</b></span><br/>");
    }
}
function reset(id, key) {
    $('#' + key + ' input:radio').removeAttr("checked");
    removeTextFromSelecteds(id, key);
    refreshSelecteds(id);
}

function doCustomFilter() {
    var temp = $('#customFilterLink').attr('href') + "?filterKey=" + $('#headFilterSelect').val() + "&filterValue=" + $('#headFilterArea').val()
            + "&selecteds=" + JSON.stringify(selecteds);
    $('#customFilterLink').attr('href', temp);
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
            <a id="filterLink" class="filterLink" href="all_summary" onclick="filterSelected();">Filter</a>
            <c:if test='${!empty param.filterValue}'>
                / <a class="filterLink" href="all_summary">Remove Filter</a>
            </c:if>

            <br/><br/>
            <table>
                <tr>
                    <th>Finished Tests</th>
                    <th>Passed</th>
                    <th>Failed</th>
                    <th>N/A</th>
                    <th>Custom</th>
                </tr>
                <tr>
                    <td>${sumTestNumber}</td>
                    <td class='passed'>${sumPassed} (${fn:substringBefore(sumPassed / sumTestNumber * 100, '.')}%)</td>
                    <td class='failed'>${sumFailed} (${fn:substringBefore(sumFailed / sumTestNumber * 100, '.')}%)</td>
                    <td class='notavailable'>${sumNA} (${fn:substringBefore(sumNA / sumTestNumber * 100, '.')}%)</td>
                    <td>${sumCustom} (${fn:substringBefore(sumCustom / sumTestNumber * 100, '.')}%)</td>
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
        <th>Custom</th>
    </tr>
    <c:forEach var="map" items="${topTableMap}">
    <tr>
        <td class="sectionHeader" colspan="7">
            ${map.key} - <a href="time_lapse?setName=${map.key}">Time Lapse View</a> - <a href="set_summary?setName=${map.key}">Set Summary View</a>
        </td>
    </tr>
        <c:forEach var="set" items="${map.value}">
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
            <td>${set.customStatistic} (${fn:substringBefore(set.customStatistic / set.finishedTestNumber * 100, '.')}%)</td>
        </tr>
        </c:forEach>
    </c:forEach>

    <!-- ================ Bottom table ================ -->
    <tr>
        <td class="sectionHeader" colspan="7">Custom Filter</td>
    </tr>
    <tr>
        <td><b>Included Messages:</b> <div id="selectedIncm"></div><b>Excluded Messages:</b> <div id="selectedExcm"></div></td>
        <td><b>Included Comments:</b> <div id="selectedIncc"></div><b>Excluded Comments:</b> <div id="selectedExcc"></div></td>
        <td><b>Included Types:</b> <div id="selectedInct"></div><b>Excluded Types:</b> <div id="selectedExct"></div></td>
        <td colspan="4"></td>
    </tr>
    <tr>
        <td colspan="7"><a id="customFilterLink" class="filterLink" href="all_summary" onclick="doCustomFilter();">FILTER</a>
            <br/>(Select filter options from the bottom table. Conditions will join with 'OR' and evaluate only on failed cases.)
        </td>
    </tr>

    <!-- Detailed Failed Info -->
    <tr>
        <th class="subHeading">Error Messages</th>
        <th class="subHeading">Comments</th>
        <th class="subHeading">Types</th>
        <th colspan="4"></th>
    </tr>
    <c:forEach var="row" items="${errorTable}" varStatus="rowStatus">
    <tr>
        <c:forEach var="col" items="${row}" varStatus="colStatus">
        <td class="formattedCell" id="${rowStatus.index}-${colStatus.index}">
            <div>${col}</div>
            <c:choose>
                <c:when test="${!empty col && colStatus.index == 0}">
                    <form>
                        <input type="radio" name="inex" class="selectedIncm" onclick="addSelected('selectedIncm', 'selectedExcm', ${rowStatus.index}, ${colStatus.index});" /> +
                        <input type="radio" name="inex" class="selectedExcm" onclick="addSelected('selectedExcm', 'selectedIncm', ${rowStatus.index}, ${colStatus.index});" /> -
                    </form>
                </c:when>
                <c:when test="${!empty col && colStatus.index == 1}">
                    <form>
                        <input type="radio" name="inex" class="selectedIncc" onclick="addSelected('selectedIncc', 'selectedExcc', ${rowStatus.index}, ${colStatus.index});" /> +
                        <input type="radio" name="inex" class="selectedExcc" onclick="addSelected('selectedExcc', 'selectedIncc', ${rowStatus.index}, ${colStatus.index});" /> -
                    </form>
                </c:when>
                <c:when test="${!empty col && colStatus.index == 2}">
                    <form>
                        <input type="radio" name="inex" class="selectedInct" onclick="addSelected('selectedInct', 'selectedExct', ${rowStatus.index}, ${colStatus.index});" /> +
                        <input type="radio" name="inex" class="selectedExct" onclick="addSelected('selectedExct', 'selectedInct', ${rowStatus.index}, ${colStatus.index});" /> -
                    </form>
                </c:when>
            </c:choose>
        </td>
        </c:forEach>
        <td colspan="4"></td>
    </tr>
    </c:forEach>

</table>
