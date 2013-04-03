<div id="bulk-save-popdiv">
    <div class="header">
        <span>Bulk save...</span><span class="close-button"></span>
    </div>
    <div class="body">
        <label for="bulk-error-type">Error type:</label> <select
            id="bulk-error-type">
            <c:forEach var="type" items="${errorTypes }">
                <option value="${type}">${type}</option>
            </c:forEach>
        </select>
        <div id="bulk-comment" contenteditable="true"></div>
        <div>
            <span id="bulk-save-button"class="button">Save</span>
        </div>
    </div>
</div>