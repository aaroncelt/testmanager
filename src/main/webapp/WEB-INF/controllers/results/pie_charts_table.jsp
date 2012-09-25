<table>
	<tr>
		<td>
			<table>
				<tr>
					<td>
						<table>
							<tr>
								<th>Check Points</th>
								<th>Passed</th>
								<th>Failed</th>
							</tr>
							<tr>
								<td>${passedCP+failedCP}</td>
								<td class='passed'>${passedCP} (<fmt:formatNumber
										value="${passedCP / (passedCP + failedCP) * 100}"
										pattern="0.00" />%)
								</td>
								<td class='failed'>${failedCP} (<fmt:formatNumber
										value="${failedCP / (passedCP + failedCP) * 100}"
										pattern="0.00" />%)
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<div id="chart-cp" class="chart" />
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<table>
				<tr>
					<td>
						<table>
							<tr>
								<th>Finished Tests</th>
								<th>Passed</th>
								<th>Failed</th>
								<th>N/A</th>
							</tr>
							<tr>
								<td>${setRunManager.finishedTestNumber}</td>
								<td class='passed'>${setRunManager.resultStatPassed} (<fmt:formatNumber
										value="${setRunManager.resultStatPassed / setRunManager.finishedTestNumber * 100}"
										pattern="0.00" />%)
								</td>
								<td class='failed'>${setRunManager.resultStatFailed} (<fmt:formatNumber
										value="${setRunManager.resultStatFailed / setRunManager.finishedTestNumber * 100}"
										pattern="0.00" />%)
								</td>
								<td class='notavailable'>${setRunManager.resultStatNA} (<fmt:formatNumber
										value="${setRunManager.resultStatNA / setRunManager.finishedTestNumber * 100}"
										pattern="0.00" />%)
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<div id="chart-main" class="chart" />
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
