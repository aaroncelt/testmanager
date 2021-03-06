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
@import url("<c:url value='/styles/results/index.css'/>");

@import url("<c:url value='/styles/common/resultState.css'/>");

@import url("<c:url value='/styles/common/filter.css'/>");

@import url("<c:url value='/styles/common/links.css'/>");

@import url("<c:url value='/styles/common/table.css'/>");

@import url("<c:url value='/styles/common/tools.css'/>");

@import url("<c:url value='/styles/results/env_popup.css'/>");
</style>

<h1 class="pageName">
    <fmt:message key="results.table.heading" />
</h1>
<div class="infobox">
    <div id="tools">
        <span id="filterFormText"> Filter </span> <span id="setListText">
            Set list </span>
    </div>
    <br>
    <div id="filterForm">
        <ul class="filterForm">
            <li><select id="headFilterSelect">
                    <c:forEach var="env" items="${envSet}">
                        <option value="${env}">${env}</option>
                    </c:forEach>
            </select> <textarea id="headFilterArea" rows="1" cols="15"></textarea></li>
            <li>
                <button class="filterLink" filterType="summary">Latest Results Summary Page</button>
                <button class="filterLink" filterType="all_summary">All
                    Results Summary Page</button>
                <button class="filterLink"
                    filterType="checkpoint_summary">Checkpoint
                    Summary Page</button>
            </li>
        </ul>
    </div>
    <div class="links" id="setList">
        <span>Show set: </span><br /> <select id="selectedSet"
            multiple="multiple">
            <option value="all" selected="selected">All</option>
            <c:forEach var="map" items="${map}">
                <option value="${map.key}">${map.key}</option>
            </c:forEach>
        </select>
        <!-- 
        <ul>
            <li><span>Jump to:</span></li>
            <c:forEach var="map" items="${map}">
                <li><a href="#${map.key}">${map.key}</a></li>
            </c:forEach>
        </ul>
         -->
    </div>
</div>
<div class="clear-both"></div>
<%@ include file="/WEB-INF/controllers/results/env_popup.jsp"%>
<c:forEach var="map" items="${map}">
    <div class="set-results-box" id="${map.key}">
        <span> ${map.key} - <a
            href="time_lapse?setName=${map.key}">Time Lapse View</a> - <a
            href="set_summary?setName=${map.key}">Set Summary View</a> - <a
            href="table_latest?setName=${map.key}">Latest result</a>
        </span>
        <table>
            <thead>
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
            </thead>
            <c:forEach var="set" items="${map.value}">
                <tr>
                    <td><a href="table?setId=${set.id}"><fmt:formatDate
                                value="${set.startDate}"
                                pattern="yyyy/MM/dd HH:mm" />
                            (${set.displayExecutionTime})</a></td>
                    <td><span class="deleteLink"
                        onclick="deleteSelected('${set.id}');" title="Click here to delete test run"></span></td>
                    <td class="env" title="Click here to see the full environment variables list">
                        <c:forEach var="env"
                            items="${set.environment}">
                            ${env.key}=${env.value}
                        </c:forEach>
                    </td>
                    <td>${set.runningTestNumber}</td>
                    <td>${set.finishedTestNumber}</td>
                    <td class='passed'>${set.resultStatPassed}
                        (${fn:substringBefore(set.resultStatPassed /
                        set.finishedTestNumber * 100, '.')}%)</td>
                    <td class='failed'>${set.resultStatFailed}
                        (${fn:substringBefore(set.resultStatFailed /
                        set.finishedTestNumber * 100, '.')}%)</td>
                    <td class='notavailable'>${set.resultStatNA}
                        (${fn:substringBefore(set.resultStatNA /
                        set.finishedTestNumber * 100, '.')}%)</td>
                </tr>
            </c:forEach>
        </table>
    </div>
</c:forEach>