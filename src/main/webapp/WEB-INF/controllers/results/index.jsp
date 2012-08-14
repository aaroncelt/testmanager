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
</style>

<script>
function deleteSelected(setId) {
    var r = confirm("Are you sure?");
    if (r == true) {
        /*$.get(
            "delete",
            { setId: setId },
            function(data) {
                window.location.reload();
            }
        );*/
        window.location.href = "delete?setId=" + setId;
    }
}

function filterSelected(filterLink) {
    var temp = $('#' + filterLink).attr('href') + "?filterKey=" + $('#headFilterSelect').val() + "&filterValue=" + $('#headFilterArea').val();
    $('#' + filterLink).attr('href', temp);
}
</script>

<h1 class="pageName"><fmt:message key="results.table.heading"/></h1>

<table width="95%" border="0" cellspacing="0" cellpadding="5" align="center">
    <tr>
        <td class="infobox" colspan="8">
            <select id="headFilterSelect">
                <c:forEach var="env" items="${envSet}">
                    <option value="${env}">${env}</option>
                </c:forEach>
            </select>
            <textarea id="headFilterArea" rows="1" cols="15"></textarea>
            <a id="filterLink_latest" class="filterLink" href="summary" onclick="filterSelected('filterLink_latest');">Latest Results Summary Page</a>
            / <a id="filterLink_all" class="filterLink" href="all_summary" onclick="filterSelected('filterLink_all');">All Results Summary Page</a>
            / <a id="filterLink_cp" class="filterLink" href="checkpoint_summary" onclick="filterSelected('filterLink_cp');">Checkpoint Summary Page</a>
        </td>
    </tr>

    <tr>
        <th>Set Run</th>
        <th>Action</th>
        <th>Environment</th>
        <th>Running Tests</th>
        <th>Finished Tests</th>
        <th>Passed</th>
        <th>Failed</th>
        <th>N/A</th>
    </tr>
    <c:forEach var="map" items="${map}">
    <tr>
        <td class="sectionHeader" colspan="8">
            ${map.key} - <a href="time_lapse?setName=${map.key}">Time Lapse View</a> - <a href="set_summary?setName=${map.key}">Set Summary View</a>
        </td>
    </tr>
        <c:forEach var="set" items="${map.value}">
        <tr>
            <td><a href="table?setId=${set.id}"><fmt:formatDate value="${set.startDate}" pattern="yyyy/MM/dd HH:mm"/> (${set.displayExecutionTime})</a></td>
            <td><span class="deleteLink" onclick="deleteSelected('${set.id}');">DELETE</span></td>
            <td>
               <c:forEach var="env" items="${set.environment}">
                   ${env.key}=${env.value}
               </c:forEach>
            </td>
            <td>${set.runningTestNumber}</td>
            <td>${set.finishedTestNumber}</td>
            <td class='passed'>${set.resultStatPassed} (${fn:substringBefore(set.resultStatPassed / set.finishedTestNumber * 100, '.')}%)</td>
            <td class='failed'>${set.resultStatFailed} (${fn:substringBefore(set.resultStatFailed / set.finishedTestNumber * 100, '.')}%)</td>
            <td class='notavailable'>${set.resultStatNA} (${fn:substringBefore(set.resultStatNA / set.finishedTestNumber * 100, '.')}%)</td>
        </tr>
        </c:forEach>
    </c:forEach>
</table>
