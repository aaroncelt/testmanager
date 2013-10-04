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
<%! public String getResultClass(Object result){
    return result == null ? null: result.toString().replaceAll("_", "").toLowerCase();
}
%>
<style type="text/css" media="screen">
@import url("<c:url value='/styles/common/table.css'/>");
@import url("<c:url value='/styles/common/links.css'/>");
@import url("<c:url value='/styles/common/tools.css'/>");
@import url("<c:url value='/styles/common/resultState.css'/>");
@import url("<c:url value='/styles/results/table/setInfo.css'/>");
@import url("<c:url value='/styles/results/table/table_scenario.css'/>");


</style>
<script type="text/javascript">
var setId = '${setId}';
document.title="${setRunManager.setName}";
</script>
<div class="heading-container">
    <div class="headingDiv">
    	<h3>
    		${setRunManager.setName} -
    		<fmt:formatDate value="${setRunManager.startDate}"
    			pattern="yyyy/MM/dd HH:mm" />
    	</h3>
    </div>
    <div class="info-container">
        <div class="setInfo">
            <%@ include file="/WEB-INF/controllers/results/set_info.jsp"%>
        </div>
        <div id="label-search">
            <div id="labels-available">
                <select class="sel-labels" multiple>
                    <c:forEach var="label" items="${labels}">
                        <option value="${label}">${label}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="labels-buttons">
                <button class="add-label">Add</button><br>
                <button class="remove-label">Remove</button><br>
                <button class="remove-all-label">Remove all</button><br>
                <input name="search-method" value="all" type="radio" checked>All
                <input name="search-method" value="any" type="radio">Any
            </div>
            <div id="labels-selected">
                <select class="sel-labels" multiple>
                </select>
            </div>
        </div>
    </div>
    <div class="clear-both"></div>
</div>
<table class="main hidden-phases" id="main">
    <thead>
    	<tr>
    		<td class="testName">Test Name <small><a onclick="showHidePhases();">Show/Hide Phases</a></small></td>
            <c:forEach var="checkpointGroup" items="${checkpointGroups}">
                <td class="cp-group rotate"><div>${checkpointGroup}</div></td>
            </c:forEach>
    		<td class="rotate paramName phases"><div>Parameter Name</div></td>
            <c:forEach var="phase" items="${phases}">
                <td class="phases rotate"><div>${phase}</div></td>
            </c:forEach>
    	</tr>
        <tr>
            <td>
                <input autofocus="autofocus" autocomplete="on" id="search-input" />
            </td>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="scenarioResult" items="${scenarioResultMap }">
            <tr class="table-row">
                <c:set var="scenarioState" value="${scenarioResultStateMap[scenarioResult.key]}"></c:set>
                <td><span class="scenario-name <%=getResultClass(pageContext.getAttribute("scenarioState")) %>">${scenarioResult.key } - ${scenarioResultStateMap[scenarioResult.key]}</span>
                    <div class="labels">${scenarioResult.value.values().toArray()[0].labels}</div>
                </td>
                <c:forEach var="checkpointGroup" items="${checkpointGroups}" varStatus="status">
                    <c:set var="cpGroupResult" value="${scenarioCpPrioResultMap[scenarioResult.key][status.index]}"></c:set>
                    <td class="cp-group <%=getResultClass(pageContext.getAttribute("cpGroupResult"))%>">&nbsp;</td>
                </c:forEach>
                <td class="paramName phases">${scenarioResult.value.values().toArray()[0].paramName}</td>
                <c:forEach var="phase" items="${phases}">
                    <c:set var="phaseResult" value="${scenarioResult.value[phase].state }"></c:set>
                    <td class="phases <%=getResultClass(pageContext.getAttribute("phaseResult"))%>">
                        <c:if test="${not empty scenarioResult.value[phase]}"><a href="${scenarioResult.value[phase].linkToResult}" target="_blank">&raquo;</a></c:if>
                    </td>
                </c:forEach>            
            </tr>
        </c:forEach>
    </tbody>
    <tfoot></tfoot>
</table>
