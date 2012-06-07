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
    text-align: left;
    font-size: small;
}
.sectionHeader {
    border-top: solid 1px black;
    text-align: left;
}
.deleteLink {
    cursor: pointer;
    color: #FD9B11;
}
.pageName {
    padding-left: 50px;
}
</style>

<script>
function deleteOldTestRuns(daysToKeepInDatabase) {
    var r = confirm("Are you sure?");
    if (r == true) {
        window.location.href = "deleteOldTestRuns?daysToKeepInDatabase=" + daysToKeepInDatabase;
    }
}
</script>

<h1 class="pageName"><fmt:message key="admin.index.heading" /></h1>

<table width="95%" border="0" cellspacing="0" cellpadding="5" align="center">
    <tr>
        <td>
            Delete test run data older then: ${daysToKeepInDatabase} day(s).<br/>
            This will be done automatically by a job.
        </td>
        <td>
            <span class="deleteLink" onclick="deleteOldTestRuns('${daysToKeepInDatabase}');">Force delete</span>
        </td>
    </tr>
</table>
