<div id="cp-filter">
    <fieldset class="group"> 
        <legend>Exclude labels</legend>
        <ul class="checkbox">
            <c:forEach var="cpFilterLabel" items="${availabelCpFilters}">
                <c:choose>
                    <c:when test="${fn:contains(filterCpLabels, cpFilterLabel)}">
                        <li><input name="excludeCpLabels" type="checkbox" value="${cpFilterLabel }" id="${cpFilterLabel }" checked="checked"><label for="${cpFilterLabel }">${cpFilterLabel }</label></li>
                    </c:when>
                     <c:otherwise>
                        <li><input name="excludeCpLabels" type="checkbox" value="${cpFilterLabel }" id="${cpFilterLabel }"><label for="${cpFilterLabel }">${cpFilterLabel }</label></li>
                     </c:otherwise>
                 </c:choose>
            </c:forEach>
            <li>
            <hr>
                <form action="table">
                    <c:choose>
                        <c:when test="${filterType == 'INCLUDE' }">
                            <input name="filterType" type="radio" value="exclude">Exclude
                            <input name="filterType" type="radio" value="include" checked="checked">Include
                        </c:when>
                        <c:otherwise>
                            <input name="filterType" type="radio" value="exclude" checked="checked">Exclude
                            <input name="filterType" type="radio" value="include">Include
                        </c:otherwise>
                    </c:choose>
                    <input type="hidden" name="setId" value="${setId}">
                    <input type="hidden" name="filterCpLabels" value="${filterCpLabels }" id="filterCpLabels">
                    <input type="submit" id="cp-filter-button" >
                </form>
            </li>
        </ul> 
    </fieldset> 
</div>
