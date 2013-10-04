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
@import url("<c:url value='/styles/results/table/charts.css'/>");
@import url("<c:url value='/styles/results/table/setInfo.css'/>");
@import url("<c:url value='/styles/results/table/search_module.css'/>");
@import url("<c:url value='/styles/results/table/bulk_save.css'/>");
@import url("<c:url value='/styles/common/table.css'/>");
@import url("<c:url value='/styles/common/links.css'/>");
@import url("<c:url value='/styles/common/tools.css'/>");
@import url("<c:url value='/styles/common/resultState.css'/>");
#show-hide-labels {
    font-size: x-small;
}
</style>

<script>
var setId = "${setId}";
var datas = [${chartPieData.dataString}];
var cpDatas = [${cpPieChartData.dataString}];

</script>

<h1 class="pageName">
	<fmt:message key="results.table.heading" />
</h1>

<c:if test='${!empty testRunData}'>
	<div class="headingDiv">
		<h3>
			${setRunManager.setName} -
			<fmt:formatDate value="${setRunManager.startDate}"
				pattern="yyyy/MM/dd HH:mm" />
            - <a href="table_scenario?setId=${setId}">Scenario groups</a>
			- <a href="generate_xls?setId=${setId}">Generate XLS</a>
            
		</h3>
        <div id="tools">
	       	<div>
        		<span id="charts">Charts</span>
	        </div>
        </div>
	</div>
</c:if>
<br>
	<div>
		<div class="infobox">
			<div class="report" style="display: none;">
				<div class="charts">
					<%@ include file="/WEB-INF/controllers/results/pie_charts_table.jsp"%>
				</div>
				<div class="setInfo">
		            <%@ include file="/WEB-INF/controllers/results/set_info.jsp"%>
				</div>
			</div>
		</div>
	</div>
<div id="search-module">
    <%@ include file="/WEB-INF/controllers/results/search_module.jsp"%>
</div>
<%@ include file="/WEB-INF/controllers/results/bulk_save_popdiv.jsp"%>
<table class="main">
	<tr class="header-row">
		<th></th>
		<th class="testName">Test Name <span id="show-hide-labels"> show/hide labels</span></th>
		<th>Parameter Name</th>
		<th>Run Time</th>
		<th>CheckPoint Number</th>
		<th>State</th>
		<th>Error Message</th>
		<th>Error Type</th>
		<th>Comment</th>
		<th><input type="checkbox" class="select-all"><span id="saveAllLink" class="saveLink" onclick="saveAll();">SAVE SUGGESTIONS</span><span id="saveAllLinkHidden" class="saveLinkHidden">SAVING...</span>
		</th>
	</tr>
	<c:forEach var="test" items="${testRunData}" varStatus="row">
		<tr class="table-row
			<c:if test="${test.state != 'PASSED' && test.state != 'STARTED' && test.errorComment == null}"> commentless</c:if>">
            <c:set var="failedCp" value="0" />
            <c:forEach var="cp" items="${test.checkPoints}">
                <c:if test="${cp.state != 'PASSED'}">
                    <c:set var="failedCp" value="${failedCp + 1}" />
                 </c:if>
            </c:forEach>
			<td><c:if test="${!empty test.checkPoints}">
					<img src="<c:url value="/images/plus.png"/>" class="link"
                        testname="<c:out value="${test.testName}"/>" paramname='${test.paramName}'
						index='${row.index}' />
				</c:if></td>
			<td class="testName" data-test-name="${test.testName }"><a href="${test.linkToResult}"
				target="_blank">${test.displayTestName}</a>
                <div class="labels">${test.labels}</div></td>
			<td class="paramName" data-param-name="${test.paramName }">${test.displayParamName}</td>
			<td>${test.displayExecutionTime}</td>
			<td class="checkPointNumber">${test.checkPoints.size()} / ${failedCp}</td>
			<c:choose>
				<c:when test="${test.state == 'STARTED'}">
					<td class='resultState started'>${test.state}</td>
				</c:when>
				<c:when test="${test.state == 'PASSED'}">
					<td class='resultState passed'>${test.state}</td>
				</c:when>
				<c:when test="${test.state == 'FAILED'}">
					<td class='resultState failed'>${test.state}</td>
				</c:when>
				<c:when test="${test.state == 'NOT_AVAILABLE'}">
					<td class='resultState notavailable'>${test.state}</td>
				</c:when>
				<c:when test="${test.state == 'ABORTED'}">
					<td class='resultState aborted'>${test.state}</td>
				</c:when>
				<c:otherwise>
					<td class="resultState">${test.state}</td>
				</c:otherwise>
			</c:choose>
			<td class="errorMessage">${test.displayErrorMessage}</td>
			<td class="errorType"><c:if
					test="${test.state != 'PASSED' && test.state != 'STARTED'}">
					<select class="error-type">
						<c:set var="done" value="false" />
						<c:forEach var="type" items="${errorTypes}">
							<c:choose>
								<c:when test="${type == test.errorType && done == false}">
									<c:set var="done" value="true" />
									<option selected="selected" value="${type}">${type}</option>
								</c:when>
								<c:when
									test="${type == test.errorTypeSuggestion && done == false}">
									<c:set var="done" value="true" />
									<option selected="selected" class="suggestion" value="${type}">${type}</option>
								</c:when>
								<c:otherwise>
									<option value="${type}">${type}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
				</c:if></td>
			<td><c:if
					test="${test.state != 'PASSED' && test.state != 'STARTED'}">
					<c:choose>
						<c:when test="${test.errorComment == null}">
							<textarea class="comment suggestion">${test.errorCommentSuggestion}</textarea>
						</c:when>
						<c:otherwise>
							<textarea class="comment">${test.errorComment}</textarea>
						</c:otherwise>
					</c:choose>
				</c:if></td>
			<td><c:if
					test="${test.state != 'PASSED' && test.state != 'STARTED'}">
                    <input type="checkbox" class="bulk-select">
					<span class="saveLink">SAVE</span>
					<span class="saveLinkHidden">SAVING...</span>
				</c:if></td>
		</tr>
		<tr>
			<td colspan="10" style="padding: 0; display: none;"
				class="checkpoints_for_row_${row.index}"></td>
		</tr>
	</c:forEach>
</table>
