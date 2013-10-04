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
table.main tr td:nth-child(2) {
    word-wrap: normal;
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
.collapse{
    width: 20px;
}
body{
    margin: 0px;
}
.modal-div{
    display:none;
    position: fixed;
    width: 100%;
    height: 100%;
    background-color: rgba(14, 13, 13, 0.78);
    z-index: 1000;
}

.modal-div>div{
    width: 80%;
    margin: auto;
}
.modal-div .header{
    background-color: lightgray;
    border-radius: 5px 5px 0px 0px; 
    margin-top: 30px;
}
.modal-div .body{
    background-color: green;
    max-height: 90%;
    max-width: 100%;
    margin-top: 3px;
    overflow: auto;
}

.modal-div .header h3{
    padding-top: 5px;
    padding-bottom: 3px;
    text-align: center;
    margin: 0px;
}
</style>
<script type="text/javascript">
var setId = '${setId}';
function createModalDiv(){
	var backgroundDiv = $('<div class="modal-div">');
	backgroundDiv.append($('<div class="header"><h3>Checkpoint results</h3></div'));
	backgroundDiv.append($('<div class="body">'));
	$('body').prepend(backgroundDiv);
    $('.modal-div').click(function() {
		$(this).hide();
	});
    $('.modal-div .header,.modal-div .body').click(function(e){
    	   e.stopPropagation();
    	});
    $('body').keydown(function(e) {
        // ESCAPE key pressed
        if (e.keyCode == 27) {
        	$('.modal-div').hide();
        }
    });
}
$(document).ready(function() {
	createModalDiv();
	$('img').each(function() {
        var testName = $(this).attr('testname');
        var paramName = $(this).attr('paramname');
        $(this).click(function() {
            $('#progressbar').show();
            $.get("checkpoints", {
                setId : setId,
                testName : testName,
                paramName : paramName
            }, function(data) {
                $('.modal-div .body').html(data);
                $('#progressbar').hide();
                $('.modal-div').show();
            });
        });

    });
	
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
            <td class="collapse">
                <c:if test="${!empty test.key.checkPoints}">
                    <img src="<c:url value='/images/plus.png'/>" class="link" testname="<c:out value='${test.key.testName}'/>" paramname='${test.key.paramName}' index='${row.index}' />
                </c:if></td>
			<td class="testName" data-test-name="${test.key.testName }">
            <c:set var="stateClass" value="" />
            <c:choose>
                <c:when test="${test.key.state == 'STARTED'}">
                    <c:set var="stateClass" value="started" />
                </c:when>
                <c:when test="${test.key.state == 'PASSED'}">
                    <c:set var="stateClass" value="passed" />
                </c:when>
                <c:when test="${test.key.state == 'FAILED'}">
                    <c:set var="stateClass" value="failed" />
                </c:when>
                <c:when test="${test.key.state == 'NOT_AVAILABLE'}">
                    <c:set var="stateClass" value="notavailable" />
                </c:when>
                <c:when test="${test.key.state == 'ABORTED'}">
                    <c:set var="stateClass" value="aborted" />
                </c:when>
            </c:choose>
            <c:choose>
                <c:when test="${test.key.state == 'PASSED'}">
                    <span class='result-point ${stateClass}'>Passed</span>
                </c:when>
                <c:when test="${test.key.errorComment == nul}">
                    <span class='result-point ${stateClass}'>Unknown</span>
                </c:when>
                <c:otherwise>
                    <span class='result-point ${stateClass}' title='${test.key.errorComment}'>${test.key.errorType }</span>
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