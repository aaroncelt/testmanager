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

<h1 class="pageName">
	<fmt:message key="results.story.heading" />
</h1>

<table class="main">
    <tr>
        <th>
            <a href="clean-stories">Clean Stories</a>
        </th>
        <c:forEach var="val" items="${columns}">
        <th>
            <c:out value="${val}"/>
        </th>
        </c:forEach>
    </tr>
	<c:forEach var="entry" items="${storyTable}">
    <tr>
        <td class="testName">
            <c:out value="${entry.key}"/>
        </td>
        <c:forEach var="cell" items="${entry.value}">
        <td class="testName">
            <c:forEach var="cellValue" items="${cell.getValues()}">
            <c:out value="${cellValue.getValue()}"/><c:if test="${cellValue.isExpired()}"> EXPIRED</c:if><br/>
            </c:forEach>
        </td>
        </c:forEach>
    </tr>
	</c:forEach>
</table>
