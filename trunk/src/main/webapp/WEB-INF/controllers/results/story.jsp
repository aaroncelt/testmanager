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
        <th>
            Layer
        </th>
        <th>
            Tests
        </th>
    </tr>
	<c:forEach var="storyentry" items="${storyTable}">
    <c:set var="firstRow" value="${true}" />
    <c:forEach var="layerentry" items="${storyentry.value}">
    <tr>
        <c:if test="${firstRow}">
        <c:set var="firstRow" value="${false}" />
        <td class="testName" rowspan="${storyentry.value.keySet().size()}">
            <c:out value="${storyentry.key}"/>
        </td>
        </c:if>
        <td class="testName">
            <c:out value="${layerentry.key}"/>
        </td>
        <td class="testName">
            <c:forEach var="setValue" items="${layerentry.value}">
            <c:out value="${setValue.getValue()}"/><c:if test="${setValue.isExpired()}"> EXPIRED</c:if><br/>
            </c:forEach>
        </td>
    </tr>
    </c:forEach>
	</c:forEach>
</table>
