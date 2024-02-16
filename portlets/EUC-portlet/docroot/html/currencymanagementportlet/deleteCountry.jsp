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
	import="com.ecims.portlet.currencymanagement.CountryManagementPortletUtil"%>
<%@page import="java.util.Collection"%>
<%@page import="smartpay.entity.Currency"%>
<%@ page import="java.util.Iterator"%>

<portlet:defineObjects />

<%
CountryManagementPortletUtil util = new CountryManagementPortletUtil();
String countryKey = renderRequest.getParameter("countryKey");

try {
	util.deleteCountryById(countryKey);
	// process adding
	SessionMessages.add(renderRequest, "successDelete");
} catch (Exception e) {
	SessionErrors.add(renderRequest, "errorUpdate");
}

%>
<% String defaultTab = "Country Management"; %>
<portlet:renderURL var="goHomeUrl">
	<portlet:param name="mvcPath"
		value="/html/currencymanagementportlet/view.jsp" />
</portlet:renderURL>
<form id="toHome" action=<%= goHomeUrl %> method="post">
<input type="hidden" name="test" value="go" />
<input type="hidden" name="tabs1" value="<%= defaultTab %>" />
</form>
<script type="text/javascript">
   //alert("here2");
	   var form = document.getElementById("toHome");

	    form.submit();
	  
  // }
</script>
