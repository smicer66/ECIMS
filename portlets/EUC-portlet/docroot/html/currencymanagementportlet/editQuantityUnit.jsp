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
<%@page import="com.ecims.portlet.currencymanagement.StateManagementPortletUtil"%>
<%@page import="com.ecims.portlet.currencymanagement.QuantityUnitManagementPortletUtil"%>
<%@page import="com.ecims.portlet.currencymanagement.ItemCategoryManagementPortletUtil"%>
<%@page import="java.util.Collection" %>
<%@page import="smartpay.entity.QuantityUnit" %>
<%@page import="smartpay.entity.ItemCategorySub" %>
<%@page import="smartpay.entity.ItemCategory" %>

<%@page import="smartpay.entity.enumerations.ApplicantType" %>
<%@page import="java.util.Iterator" %>
<%@page import="java.util.List" %>

<portlet:defineObjects />

<%
String Id = (String)renderRequest.getParameter("id");
PortletPreferences prefs = renderRequest.getPreferences();
QuantityUnitManagementPortletUtil util = new QuantityUnitManagementPortletUtil();

String greeting = (String)prefs.getValue(
	    "greeting", "Hello! Welcome to our portal.");
%>

<% QuantityUnit qunit = util.getQuantityUnitById(Id); %>
<% String name = (qunit != null) ? qunit.getName() : ""; %>
<% String unit = (qunit != null) ? qunit.getUnit() : ""; %>
<% boolean status =qunit.getStatus(); %>
<% //String statusValue = (status.equals("1")) ? "Enabled" : "Disabled"; %>

<portlet:actionURL var="updateAgencyURL" name="editQuantityUnitAction">
    <portlet:param name="mvcPath" value="/html/currencymanagementportlet/editQuantityUnit.jsp" />
</portlet:actionURL>

<div style="padding-left:10px; padding-right:10px; width:900px">
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold"></span></div>
	  <div class="panel-body">
	    	
	    <form  id="startRegFormId" action="<%=updateAgencyURL%>" method="post" enctype="application/x-www-form-urlencoded">
	     
		  <div style="padding-bottom:5px">
		  	<fieldset>
		  		<legend>Update QuantityUnit</legend>
		            <div>
				Name <input class=" form-control" type="text"  name="name" id="name" value="<%= name %>" />
				</div>
				<br/>
				  <div>
			Unit <input class=" form-control" type="text"  name="unit" id="unit" value="<%= unit %>" />
				</div>
				<br/>
						        <div>
		Status
			<select  name="status" id="status">
		<%
		String enabledSelected = "";
		String disabledSelected = "";
		
		if(status)
		{
			enabledSelected = "selected='selected'";
		}
		
		if(!status)
		{
			disabledSelected = "selected='selected'";
		}
		%>
		<option <%= enabledSelected %> value="True">Enabled</option>
		<option <%= disabledSelected %> value="False">Disabled</option>
		
			</select>
					</div>
				<input type="hidden" name="unitKey" value="<%= Id %>" />
			
				  	</fieldset>
	      </div>
		  
	      <div>
	      	<button type="submit" class="btn btn-success" style="float:right">Save</button>
	      </div>
	    </form>
	  </div>
	</div>
</div>

<portlet:renderURL var="viewCurrencyURL">
    <portlet:param name="mvcPath" value="/html/currencymanagementportlet/view.jsp" />
</portlet:renderURL>
<% String defaultTab = "QuantityUnit Management"; %>
<p><a href="<%= viewCurrencyURL %>?tabs1=<%= defaultTab %>">&larr; Back</a></p>