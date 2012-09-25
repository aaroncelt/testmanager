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

<!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>
            <tiles:getAsString name="title" />
        </title>

        <style type="text/css" media="screen">
            @import url("<c:url value="/styles/standard2.css" />");
        </style>

        <tiles:useAttribute id="jslist" name="javascripts" classname="java.util.List" ignore="true" />

        <c:forEach items="${jslist}" var="js">
            <script type="text/javascript" src="<c:url value="${js}" />" ></script>
        </c:forEach>

    </head>
    <body>
    	<div id="progressbar" class="progressbar"><img src="<c:url value='/images/loader.gif'/>"></img></div>
        <div id="page" class="center" >
            <div id="header">
                <tiles:insertAttribute name="header" />
            </div><!-- end header -->
            <div id="body">
                <div id="nav">
                    <tiles:insertAttribute name="navigation" />
                </div><!-- end navigation -->
                <div id="content">
                    <div id="main">
                        <tiles:insertAttribute name="body" />
                    </div>
                </div><!-- end content -->
                <div id="footer">
                    <tiles:insertAttribute name="footer" />
                </div><!-- end footer -->
            </div><!-- end body -->
        </div><!-- end page -->
        <div id="extra1">&nbsp;</div>
        <div id="extra2">&nbsp;</div>
    </body>
</html>
