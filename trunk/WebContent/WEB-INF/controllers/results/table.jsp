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
.testName {
    max-width: 450px;
    text-align: left;
}
.suggestion {
    color: gray;
}
.started { background-color: white; }
.aborted { background-color: grey; }
.passed { background-color: #64E986; }
.failed { background-color: #F75D59; }
.notavailable { background-color: #77BFC7; }
.saveLink {
    cursor: pointer;
    color: #FD9B11;
}
.link {
    cursor: pointer;
}
.saveLinkHidden {
    color: black;
    display: none;
}
.commentless {
    background-color: #E8E8FF;
}
.pageName {
    padding-left: 50px;
}
.headingDiv {
    padding-left: 100px;
    padding-bottom: 10px;
}
.infobox {
    text-align: left;
    vertical-align: top;
    padding: 30px;
}
</style>

<script>
var setId = "${setId}";
var datas = [${chartPieData.dataString}];

$(document).ready(function() {
    createPieChart('chart-main', '', datas);
});

function saveAll() {
    $('#saveAllLink').hide();
    $('#saveAllLinkHidden').show();
    $.get(
        "save_all",
        { setId: setId },
        function(data) {
            $('#saveAllLinkHidden').hide();
            $('#saveAllLink').show();
            window.location.reload();
        }
    );
}

function saveSelected(testName, paramName, index) {
    $('#saveLink-' + index).hide();
    $('#saveLinkHidden-' + index).show();
    $.get(
        "save",
        { setId: setId, testName: testName, paramName: paramName, type: $('#errorType-' + index).val(), comment: $('#comment-' + index).val() },
        function(data) {
            $('#saveLinkHidden-' + index).hide();
            $('#saveLink-' + index).show();
            window.location.reload();
        }
    );
}

function toggleCp(index) {
    $('.cp-' + index).toggle();
}
</script>

<h1 class="pageName"><fmt:message key="results.table.heading" /></h1>
<c:if test='${!empty testRunData}'>
<div class="headingDiv">
    <h3>${setRunManager.setName} - <fmt:formatDate value="${setRunManager.startDate}" pattern="yyyy/MM/dd HH:mm"/>
        - <a href="generate_xls?setId=${setId}">Generate XLS</a></h3>
</div>
</c:if>

<table width="95%" border="0" cellspacing="0" cellpadding="5" align="center">
    <tr>
        <td class="infobox" colspan="2">
            <b>Set Info</b><br/>
            <table>
                <tr>
                    <th>Finished Tests</th>
                    <th>Passed</th>
                    <th>Failed</th>
                    <th>N/A</th>
                </tr>
                <tr>
                    <td>${setRunManager.finishedTestNumber}</td>
                    <td class='passed'>${setRunManager.resultStatPassed} (${fn:substringBefore(setRunManager.resultStatPassed / setRunManager.finishedTestNumber * 100, '.')}%)</td>
                    <td class='failed'>${setRunManager.resultStatFailed} (${fn:substringBefore(setRunManager.resultStatFailed / setRunManager.finishedTestNumber * 100, '.')}%)</td>
                    <td class='notavailable'>${setRunManager.resultStatNA} (${fn:substringBefore(setRunManager.resultStatNA / setRunManager.finishedTestNumber * 100, '.')}%)</td>
                </tr>
            </table>
            Sum execution time: ${setRunManager.displayExecutionTime}<br/>
            <br/>
            <b>Environment:</b><br/>
            <c:forEach var="env" items="${testRunData[0].environment}">
                ${env.key}=${env.value}
            </c:forEach>
        </td>
        <td colspan="7">
            <div id="chart-main" style="width: 500px; height: 250px; margin: 0 auto"></div>
        </td>
    </tr>

    <tr>
        <th></th>
        <th class="testName">Test Name</th>
        <th>Parameter Name</th>
        <th>Run Time</th>
        <th>State</th>
        <th>Error Message</th>
        <th>Error Type</th>
        <th>Comment</th>
        <th>
            <span id="saveAllLink" class="saveLink" onclick="saveAll();">SAVE SUGGESTIONS</span>
            <span id="saveAllLinkHidden" class="saveLinkHidden">SAVING...</span>
        </th>
    </tr>
    <c:forEach var="test" items="${testRunData}" varStatus="row">
    <tr <c:if test="${test.state != 'PASSED' && test.state != 'STARTED' && test.errorComment == null}">class="commentless"</c:if>>
        <td><c:if test="${!empty test.checkPoints}"><img src="<c:url value="/images/plus.png"/>" class="link" onclick="toggleCp('${row.index}');"/></c:if></td>
        <td class="testName"><a href="${test.linkToResult}" target="_blank">${test.displayTestName}</a></td>
        <td>${test.displayParamName}</td>
        <td>${test.displayExecutionTime}</td>
        <c:choose>
            <c:when test="${test.state == 'STARTED'}"><td class='started'>${test.state}</td></c:when>
            <c:when test="${test.state == 'PASSED'}"><td class='passed'>${test.state}</td></c:when>
            <c:when test="${test.state == 'FAILED'}"><td class='failed'>${test.state}</td></c:when>
            <c:when test="${test.state == 'NOT_AVAILABLE'}"><td class='notavailable'>${test.state}</td></c:when>
            <c:when test="${test.state == 'ABORTED'}"><td class='aborted'>${test.state}</td></c:when>
            <c:otherwise><td>${test.state}</td></c:otherwise>
        </c:choose>
        <td>${test.displayErrorMessage}</td>
        <td>
            <c:if test="${test.state != 'PASSED' && test.state != 'STARTED'}">
                <select id="errorType-${row.index}">
                    <c:set var="done" value="false"/>
                    <c:forEach var="type" items="${errorTypes}">
                        <c:choose>
                            <c:when test="${type == test.errorType && done == false}">
                                <c:set var="done" value="true"/>
                                <option selected="selected" value="${type}">${type}</option>
                            </c:when>
                            <c:when test="${type == test.errorTypeSuggestion && done == false}">
                                <c:set var="done" value="true"/>
                                <option selected="selected" class="suggestion" value="${type}">${type}</option>
                            </c:when>
                            <c:otherwise>
                                <option value="${type}">${type}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </c:if>
        </td>
        <td>
            <c:if test="${test.state != 'PASSED' && test.state != 'STARTED'}">
                <c:choose>
                    <c:when test="${test.errorComment == null}">
                        <textarea id="comment-${row.index}" class="suggestion" cols="25" rows="2">${test.errorCommentSuggestion}</textarea>
                    </c:when>
                    <c:otherwise>
                        <textarea id="comment-${row.index}" cols="25" rows="2">${test.errorComment}</textarea>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </td>
        <td>
            <c:if test="${test.state != 'PASSED' && test.state != 'STARTED'}">
                <span id="saveLink-${row.index}" class="saveLink" onclick="saveSelected('${test.testName}', '${test.paramName}', '${row.index}');">SAVE</span>
                <span id="saveLinkHidden-${row.index}" class="saveLinkHidden">SAVING...</span>
            </c:if>
        </td>
    </tr>
    <c:forEach var="cp" items="${test.checkPoints}">
    <tr class="cp-${row.index}" style="display: none">
        <td colspan="6">${cp.message}</td>
        <td>${cp.mainType}</td>
        <td>${cp.subType}</td>
        <c:choose>
            <c:when test="${cp.state == 'PASSED'}"><td class='passed'>${cp.state}</td></c:when>
            <c:when test="${cp.state == 'FAILED'}"><td class='failed'>${cp.state}</td></c:when>
            <c:otherwise><td>${cp.state}</td></c:otherwise>
        </c:choose>
    </tr>
    </c:forEach>
    </c:forEach>
</table>
