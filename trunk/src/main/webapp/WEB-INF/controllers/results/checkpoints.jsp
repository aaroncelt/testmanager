<table style="width: 100%;display: none" class="cp-${row.index}">
	<c:forEach var="cp" items="${test.checkPoints}">
		<tr>
			<td>${cp.message}</td>
			<td>${cp.mainType}</td>
			<td>${cp.subType}</td>
			<c:choose>
				<c:when test="${cp.state == 'PASSED'}">
					<td class='passed'>${cp.state}</td>
				</c:when>
				<c:when test="${cp.state == 'FAILED'}">
					<td class='failed'>${cp.state}</td>
				</c:when>
				<c:otherwise>
					<td>${cp.state}</td>
				</c:otherwise>
			</c:choose>
		</tr>
	</c:forEach>
</table>