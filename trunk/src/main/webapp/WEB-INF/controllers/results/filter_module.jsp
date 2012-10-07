<div class="filterModule">
	<div>
		<p id="filterType">
			<select>
				<option value="testName">Test Name</option>
				<option value="checkPointNumber">CheckPoint Number</option>
				<option value="resultState">State</option>
				<option value="errorMessage">Error Message</option>
				<option value="errorType">Error Type</option>
				<option value="comment">Comment</option>
				<option value="isAnalyzed">Is Analyzed?</option>
			</select>
		</p>
		<p id="filterValue">
			<input id="text"> <input id="checkBox" type="checkbox" hidden="">
			<select id="errorTypes" hidden="">
				<c:forEach var="type" items="${errorTypes}">
					<option value="${type}">${type}</option>
				</c:forEach>
			</select> 
			<select id="resultStates" hidden="">
				<option value="PASSED">PASSED</option>
				<option value="FAILED">FAILED</option>
				<option value="NOT_AVAILABLE">NOT_AVAILABLE</option>
			</select>
		</p>
	</div>
	<a id="filterButton" class="filterLink">Filter</a> <a id="filterClear"
		class="filterLink">Clear</a>
</div>