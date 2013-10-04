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
@import url("<c:url value='/styles/common/table.css'/>");
@import url("<c:url value='/styles/common/links.css'/>");
@import url("<c:url value='/styles/common/resultState.css'/>");

.separate {
    padding-right: 10px;
    border-right: solid 2px black !important;
    width: 5px
}
table.main {
        table-layout: fixed;
}
table.main td{
    vertical-align: bottom;
}
table.main tr:hover{
    /*border-top: solid 2px gray;
    padding-top: -1px;*/
    border-bottom: solid 2px gray;
    padding-bottom: -1px;
}
table.main tr td:first-child {
    white-space: normal !important;
}
.cp-group{
    width: 20px;
    padding: 3px;
    border-right: dotted 1px gray;
}
.rotate {
    overflow: hidden;
    width: 20px;
}
.rotate div {
    display: inline-block;
    white-space: nowrap;
    line-height: 1.5;
    -webkit-transform: rotate(-90deg);
    -moz-transform: rotate(-90deg);
    -ms-transform: rotate(-90deg);
    -o-transform: rotate(-90deg);
    transform: rotate(-90deg);

    /* also accepts left, right, top, bottom coordinates; not required, but a good idea for styling */
    -webkit-transform-origin: 50% 50%;
    -moz-transform-origin: 50% 50%;
    -ms-transform-origin: 50% 50%;
    -o-transform-origin: 50% 50%;
    transform-origin: 50% 50%;
    max-height: 300px;
}
.rotate div:after {
    content: "";
    display: block;
    margin: -1.5em 0 100%;
}

.table-fixed-header {
    height: 100%;
    position: relative;
}
.table-body {
    height: 500px;
    overflow-y: scroll;
}
.table-header {
    overflow-y: scroll; 
}

.table-header td{
    border-bottom: dotted 1px gray;
}
.checkPointNumber, .paramName {
    word-wrap: normal !important;
    width: 50px;
}
.result-point {
    border-radius: 5px 5px 5px 5px;
    width: 49px;
    float: left;
    margin-right: 3px;
    padding: 2px;
    text-align: center;
    font-size: smaller;
}
.highlighted {
    /*border-left: solid 2px gray;
    padding-left: 2px;*/
    border-right: solid 2px gray;
    padding-right: 2px;
}
</style>
<script type="text/javascript">
$(document).ready(function() {
    $('td.cp-group').hover(function() {
        var t = parseInt($(this).index()) + 1;
        $('td:nth-child(' + t + ')').addClass('highlighted');
    },
    function() {
        var t = parseInt($(this).index()) + 1;
        $('td:nth-child(' + t + ')').removeClass('highlighted');
    });
});
</script>
<c:if test='${!empty testRunData}'>
	<div class="headingDiv">
		<h3>
			${setRunManager.setName} -
			<fmt:formatDate value="${setRunManager.startDate}"
				pattern="yyyy/MM/dd HH:mm" />
		</h3>
	</div>
</c:if>
<div class="table-fixed-header">
<div class="table-header">
<table class="main">
	<tr>
		<td>Test Name</td>
		<td class="rotate paramName"><div>Parameter Name</div></td>
		<td class="rotate checkPointNumber"><div>CheckPoint Number</div></td>
        <td class="separate">&nbsp;</td>
        <c:forEach var="cpGroup" items="${allCpGroups }">
            <td class="cp-group rotate"><div>${cpGroup}</div></td>
        </c:forEach>
	</tr>
</table>
</div>
<div class="table-body">
<table class="main">
    <tbody>
	<c:forEach var="test" items="${testRunData}" varStatus="row">
		<tr class="table-row">
            <c:set var="failedCp" value="0" />
            <c:forEach var="cp" items="${test.key.checkPoints}">
                <c:if test="${cp.state != 'PASSED'}">
                    <c:set var="failedCp" value="${failedCp + 1}" />
                 </c:if>
            </c:forEach>
			<td class="testName" data-test-name="${test.key.testName }">
            <c:choose>
                <c:when test="${test.key.state == 'STARTED'}">
                    <span class='result-point started'>In progress</span>
                </c:when>
                <c:when test="${test.key.state == 'PASSED'}">
                    <span class='result-point passed'>Passed</span>
                </c:when>
                <c:when test="${test.key.state == 'FAILED'}">
                    <span class='result-point failed'>Failed</span>
                </c:when>
                <c:when test="${test.key.state == 'NOT_AVAILABLE'}">
                    <span class='result-point notavailable'>NA</span>
                </c:when>
                <c:when test="${test.key.state == 'ABORTED'}">
                    <span class='result-point aborted'>Aborted</span>
                </c:when>
                <c:otherwise>
                    <span class="result-point"></span>
                </c:otherwise>
            </c:choose>
            <a href="${test.key.linkToResult}"
				target="_blank">${test.key.displayTestName}</a></td>
			<td class="paramName" data-param-name="${test.key.paramName }">${test.key.displayParamName}</td>
			<td class="checkPointNumber">${test.key.checkPoints.size()}/${failedCp}</td>
            <td class="separate">&nbsp;</td>
            <c:forEach var="cpGroup" items="${allCpGroups}">
                <c:set var="cpGroupResult" value="${test.value[cpGroup]}"></c:set>
                <c:choose>
                    <c:when test="${cpGroupResult == 'PASSED' }">
                        <td class="cp-group passed">&nbsp;</td>
                    </c:when>
                    <c:when test="${cpGroupResult == 'NOT_AVAILABLE' }">
                        <td class="cp-group notavailable">&nbsp;</td>
                    </c:when>
                    <c:when test="${cpGroupResult == nul }">
                        <td class="cp-group">&nbsp;</td>
                    </c:when>
                    <c:otherwise>
                        <td class="cp-group failed">&nbsp;</td>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
		</tr>
		<tr>
			<td colspan="10" style="padding: 0; display: none;"
				class="checkpoints_for_row_${row.index}"></td>
		</tr>
	</c:forEach>
    </tbody>
    <tfoot></tfoot>
</table>
</div>
</div>