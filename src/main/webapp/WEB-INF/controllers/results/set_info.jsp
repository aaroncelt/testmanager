<table id="envInfo">
	<tr class="headerRow">
		<td colspan="3">Set Info</td>
	</tr>
	<tr>
		<td>Execution time</td>
		<td>:</td>
		<td>${setRunManager.displayExecutionTime}</td>
	</tr>
	<tr />
	<tr />
	<tr class="headerRow">
		<td colspan="3" style="text-align: center">Environment</td>
	</tr>
	<c:forEach var="env" items="${testRunData[0].environment}">
		<tr>
			<td>${env.key}</td>
			<td>:</td>
			<td>${env.value}</td>
		</tr>
	</c:forEach>
</table>