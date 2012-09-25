<table id="setInfo">
	<tr>
		<th colspan="3" style="text-align: center">Set Info</th>
	</tr>
	<tr>
		<td>Execution time</td>
		<td>:</td>
		<td>${setRunManager.displayExecutionTime}</td>
	</tr>
	<tr />
	<tr />
	<tr>
		<th colspan="3" style="text-align: center">Environment</th>
	</tr>
	<c:forEach var="env" items="${testRunData[0].environment}">
		<tr>
			<td>${env.key}</td>
			<td>:</td>
			<td>${env.value}</td>
		</tr>
	</c:forEach>
</table>