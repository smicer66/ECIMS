<%--
/**
* Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
*
* This library is free software; you can redistribute it and/or modify it under
* the terms of the GNU Lesser General Public License as published by the Free
* Software Foundation; either version 2.1 of the License, or (at your option)
* any later version.
*
* This library is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
* details.
*/
--%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>

<%@ page import="javax.portlet.PortletPreferences" %>
<%@ page import="com.liferay.portal.kernel.servlet.SessionMessages" %>
<%@ page import="com.liferay.portal.kernel.servlet.SessionErrors" %>
<%@page
	import="com.ecims.portlet.currencymanagement.CurrencyManagementPortletUtil"%>
<%@page import="java.util.Collection"%>
<%@page import="smartpay.entity.Currency"%>
<%@ page import="java.util.Iterator"%>

<portlet:defineObjects />

<%
CurrencyManagementPortletUtil util = new CurrencyManagementPortletUtil();
String currencyKey = renderRequest.getParameter("currencyKey");

try {
	util.deleteCurrencyById(currencyKey);
	// process adding
	SessionMessages.add(renderRequest, "successUpdate");
} catch (Exception e) {
	SessionErrors.add(renderRequest, "errorUpdate");
}
%>

<portlet:renderURL var="goHomeUrl">
	<portlet:param name="mvcPath"
		value="/html/currencymanagementportlet/view.jsp" />
</portlet:renderURL>
<form id="toHome" action=<%= goHomeUrl %> method="post">
<input type="hidden" name="test" value="go" />
</form>
<script type="text/javascript">
   //alert("here2");
	   var form = document.getElementById("toHome");

	    form.submit();
	  
  // }
</script>
