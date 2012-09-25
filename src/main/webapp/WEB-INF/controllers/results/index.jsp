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
</style>

<h1 class="pageName"><fmt:message key="results.table.heading"/></h1>

<table width="95%" border="0" cellspacing="0" cellpadding="5" align="center">
    <tr>
        <td class="infobox" colspan="8">
        	<div id="filterFormText">
        		<img src="<c:url value="/images/plus.png"/>" /><h2>Filter</h2>
        	</div>
        	<div id="filterForm">
        	<ul class="filterForm">
        		<li>
		            <select id="headFilterSelect">
		                <c:forEach var="env" items="${envSet}">
		                    <option value="${env}">${env}</option>
		                </c:forEach>
		            </select>
	            </li>
	            <li>
            		<textarea id="headFilterArea" rows="1" cols="15"></textarea>
            	</li>
            	<li>
	            <a id="filterLink_latest" class="filterLink" href="summary" onclick="filterSelected('filterLink_latest');">Latest Results Summary Page</a>
	            <a id="filterLink_all" class="filterLink" href="all_summary" onclick="filterSelected('filterLink_all');">All Results Summary Page</a>
	            <a id="filterLink_cp" class="filterLink" href="checkpoint_summary" onclick="filterSelected('filterLink_cp');">Checkpoint Summary Page</a>
	            </li>
            </ul>
            </div>
        </td>
    </tr>
    <tr>
	        <td class="links" colspan="8">
		        <ul>
		         	<c:forEach var="map" items="${map}">
			         	<li>
			         		<a href="#${map.key}" >${map.key}</a>
		         		</li>
		         	</c:forEach>
	         	</ul>
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
        <td class="sectionHeader" colspan="8" id="${map.key}">
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
