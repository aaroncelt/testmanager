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
</style>

<h1>DEBUG page for checking the error messages in the memory.</h1>

<table width="95%" border="0" cellspacing="0" cellpadding="5" align="center">
    <tr>
        <th>Error</th>
        <th>ID</th>
        <th>Comment</th>
    </tr>
    <c:forEach var="map" items="${comments}">
    <tr>
        <td class="sectionHeader" colspan="3" width="800px">${fn:substring(map.key, 0, 100)}</td>
    </tr>
        <c:forEach var="set" items="${map.value}">
        <tr>
            <td><a href="deleteComment?errorMessage=${map.key}&id=${set.key}">DELETE</a></td>
            <td>${set.key}</td>
            <td>${set.value}</td>
        </tr>
        </c:forEach>
    </c:forEach>
</table>
