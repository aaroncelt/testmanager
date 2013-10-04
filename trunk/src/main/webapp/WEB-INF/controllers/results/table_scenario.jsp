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
@import url("<c:url value='/styles/common/resultState.css'/>");

.separate {
    padding-right: 10px;
    border-right: solid 2px black !important;
    width: 5px
}

.scenario-name{
    padding: 1px 3px 1px 3px;
    border-radius: 2px;
}
table.main {
        table-layout: fixed;
        width: 80%;
        margin: 0 auto;
}
table.main td{
    vertical-align: bottom;
}
table.main tr:hover{
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
.paramName {
    word-wrap: normal !important;
    width: 50px;
    border-left: double;
}
.highlighted {
    /*border-left: solid 2px gray;
    padding-left: 2px;*/
    border-right: solid 2px gray;
    padding-right: 2px;
}
table.main thead td{
    border:  0px;
}
body{
    margin: 0px;
}

.hidden-phases .phases{
    display: none;
}

.visible-phases .phases{
    display: table-cell;
}
</style>
<script type="text/javascript">
var setId = '${setId}';

function showAllRows(){
    $('table.main tbody tr').show();
}

function filter(term){
    $('table.main tbody tr').each(function(){
    	var terms = term.toLowerCase().split(",");
    	var currentItem = $(this).find("td:first").text().toLowerCase();
    	var matched = true;
    	
    	for (var i=0; i<terms.length;i++){
    		if(currentItem.indexOf(terms[i])===-1){
    			matched = false;
    		}
    	}
    	if (matched){
    	   $(this).show();
    	} else {
    	   $(this).hide();
    	};
    });
}

function showHidePhases(){
	if ($('.hidden-phases').length > 0){
		$('.hidden-phases').removeClass('hidden-phases').addClass('visible-phases');
	} else {
		$('.visible-phases').removeClass('visible-phases').addClass('hidden-phases');
	}
}

$(document).ready(function() {
    $('td.cp-group').hover(function() {
        var t = parseInt($(this).index()) + 1;
        $('td:nth-child(' + t + ')').addClass('highlighted');
    },
    function() {
        var t = parseInt($(this).index()) + 1;
        $('td:nth-child(' + t + ')').removeClass('highlighted');
    });
    
    $('#search-input').keyup(function(event) {
        if (event.which == 13){
            filter($(this).val());
        }
    });
    
    $("div.labels").show();
});

document.title="${setRunManager.setName}";
</script>
<div class="headingDiv">
	<h3>
		${setRunManager.setName} -
		<fmt:formatDate value="${setRunManager.startDate}"
			pattern="yyyy/MM/dd HH:mm" />
	</h3>
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