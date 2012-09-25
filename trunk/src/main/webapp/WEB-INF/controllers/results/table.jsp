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
@import url("<c:url value='/styles/results/table.css'/>");
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
			- <a href="generate_xls?setId=${setId}">Generate XLS</a>
		</h3>
	</div>
</c:if>

<table id="main">
	<tr>
		<td class="infobox" colspan="4">
			<div id="charts">
				<%@ include file="/WEB-INF/controllers/results/pie_charts_table.jsp"%>
			</div>
		</td>
		<td colspan="3" style="vertical-align: top;"><%@ include
				file="/WEB-INF/controllers/results/set_info.jsp"%>
		</td>
	</tr>
	<tr>
		<th></th>
		<th class="testName">Test Name</th>
		<th>Parameter Name</th>
		<th>Run Time</th>
		<th>CheckPoint Number</th>
		<th>State</th>
		<th>Error Message</th>
		<th>Error Type</th>
		<th>Comment</th>
		<th><span id="saveAllLink" class="saveLink" onclick="saveAll();">SAVE
				SUGGESTIONS</span> <span id="saveAllLinkHidden" class="saveLinkHidden">SAVING...</span>
		</th>
	</tr>
	<c:forEach var="test" items="${testRunData}" varStatus="row">
		<tr
			<c:if test="${test.state != 'PASSED' && test.state != 'STARTED' && test.errorComment == null}">class="commentless"</c:if>>
			<td><c:if test="${!empty test.checkPoints}">
					<img src="<c:url value="/images/plus.png"/>" class="link"
						testname='${test.testName}' paramname='${test.paramName}'
						index='${row.index}' />
				</c:if></td>
			<td class="testName"><a href="${test.linkToResult}"
				target="_blank">${test.displayTestName}</a></td>
			<td>${test.displayParamName}</td>
			<td>${test.displayExecutionTime}</td>
			<td>${test.checkPoints.size()} / <c:set var="failedCp" value="0" />
				<c:forEach var="cp" items="${test.checkPoints}">
					<c:if test="${cp.state != 'PASSED'}">
						<c:set var="failedCp" value="${failedCp + 1 }" />
					</c:if>
				</c:forEach> ${failedCp}
			</td>
			<c:choose>
				<c:when test="${test.state == 'STARTED'}">
					<td class='started'>${test.state}</td>
				</c:when>
				<c:when test="${test.state == 'PASSED'}">
					<td class='passed'>${test.state}</td>
				</c:when>
				<c:when test="${test.state == 'FAILED'}">
					<td class='failed'>${test.state}</td>
				</c:when>
				<c:when test="${test.state == 'NOT_AVAILABLE'}">
					<td class='notavailable'>${test.state}</td>
				</c:when>
				<c:when test="${test.state == 'ABORTED'}">
					<td class='aborted'>${test.state}</td>
				</c:when>
				<c:otherwise>
					<td>${test.state}</td>
				</c:otherwise>
			</c:choose>
			<td>${test.displayErrorMessage}</td>
			<td><c:if
					test="${test.state != 'PASSED' && test.state != 'STARTED'}">
					<select id="errorType-${row.index}">
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
							<textarea id="comment-${row.index}" class="suggestion" cols="25"
								rows="2">${test.errorCommentSuggestion}</textarea>
						</c:when>
						<c:otherwise>
							<textarea id="comment-${row.index}" cols="25" rows="2">${test.errorComment}</textarea>
						</c:otherwise>
					</c:choose>
				</c:if></td>
			<td><c:if
					test="${test.state != 'PASSED' && test.state != 'STARTED'}">
					<span id="saveLink-${row.index}" class="saveLink"
						onclick="saveSelected('${test.testName}', '${test.paramName}', '${row.index}');">SAVE</span>
					<span id="saveLinkHidden-${row.index}" class="saveLinkHidden">SAVING...</span>
				</c:if></td>
		</tr>
		<tr>
			<td colspan="10" style="padding: 0; display: none;"
				class="checkpoints_for_row_${row.index}"></td>
		</tr>
	</c:forEach>
</table>
