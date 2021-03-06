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
@import url("<c:url value='/styles/common/links.css'/>");

span.warning-message{
	color: #a94442;
	background-color: #f2dede;
	border-color: #ebccd1;
	padding: 5px;
	border: 1px solid transparent;
	border-radius: 4px;
}
</style>
<script>

$(document).ready(function() {
	$("input[name=pattern]").keyup(function() {
		if (isValidateRegex()){
			$("#add-button").removeAttr("disabled");
			$("span.warning-message").hide();
		} else {
			$("#add-button").attr("disabled","disabled");
			$("span.warning-message").show();
		}
	})
});

function isValidateRegex(){
	var pattern = $("input[name=pattern]").val();
	try{
		new RegExp(pattern);
		return true;
	} catch (e) {
		return false;
	}
}
</script>

<h1 class="pageName">
	<fmt:message key="admin.index.heading" />
</h1>

<div class="set-results-box">
	<span>Error Comment patterns</span>
	<div id="error-pattern-table">
		<form action="addErrorCommentPattern" method="get">
			<table class="main">
				<thead>
					<tr>
						<th><label for="pattern">Error pattern</label></th>
						<th><label for="type">Type</label></th>
						<th><label for="comment">Comment</label></th>
						<th>&nbsp;</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="entry" items="${errorComments}">
						<tr>
							<td>${entry.key}</td>
							<td>${entry.value.type }</td>
							<td>${entry.value.comment }</td>
							<td><a class="deleteLink"
								href="deleteErrorCommentPattern?pattern=${entry.key}"
								title="Click here to delete pattern"></span></td>
						</tr>
					</c:forEach>
					<tr>
						<td><input type="text" name="pattern"></td>
						<td><select name="type">
								<c:forEach var="type" items="${errorTypes}">
									<option value="${type}">${type}</option>
								</c:forEach>
						</select></td>
						<td><input type="text" name="comment"></td>
						<td><button id="add-button">Add</button> <span class="warning-message" style="display: none;">Wrong pattern syntax!</span></td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
	<div></div>
</div>
