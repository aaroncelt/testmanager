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

<div class="left"></div>
<div class="mid">
    <table>
      <tr>
        <th></th>
        <th><a href="<c:url value="/app/index" />">Index</a></th>
<!--        <th><a href="<c:url value="/app/tests/list" />">Test List</a></th>-->
        <th><a href="<c:url value="/app/results/index" />">Results</a></th>
        <th><a href="<c:url value="/app/admin/index" />">Configuration</a></th>
      </tr>
    </table>
</div>
<div class="right"></div>
