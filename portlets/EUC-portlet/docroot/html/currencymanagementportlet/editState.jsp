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
<%@page
	import="com.ecims.portlet.currencymanagement.StateManagementPortletUtil"%>
<%@page
	import="com.ecims.portlet.currencymanagement.CountryManagementPortletUtil"%>
<%@page import="java.util.Collection"%>
<%@page import="smartpay.entity.Country"%>
<%@page import="smartpay.entity.State"%>
<%@page import="smartpay.entity.enumerations.AgencyType"%>
<%@ page import="java.util.Iterator"%>

<portlet:defineObjects />

<%
String Id = (String)renderRequest.getParameter("id");
PortletPreferences prefs = renderRequest.getPreferences();
CountryManagementPortletUtil countryUtil = new CountryManagementPortletUtil();
StateManagementPortletUtil stateUtil = new StateManagementPortletUtil();

String greeting = (String)prefs.getValue(
	    "greeting", "Hello! Welcome to our portal.");
%>

<% State state = stateUtil.getStateById(Id); %>
<% Country country = null ; %>
<% if(state != null){ %>
<%  country = state.getCountry(); %>
<%  } %>
<% String countryId = (country != null) ? String.valueOf(country.getId()): ""; %>
<% String name = (state != null) ? state.getName() : ""; %>

<portlet:actionURL var="updateAgencyURL" name="editStateAction">
    <portlet:param name="mvcPath" value="/html/currencymanagementportlet/editState.jsp" />
</portlet:actionURL>



<div style="padding-left:10px; padding-right:10px; width:900px">
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold"></span></div>
	  <div class="panel-body">
	    	
	    <form  id="startRegFormId" action="<%=updateAgencyURL%>" method="post" enctype="application/x-www-form-urlencoded">
	     
		  <div style="padding-bottom:5px">
		  	<fieldset>
		  		<legend>Update State</legend>
		            <div>
			State	Name <input class=" form-control" type="text"  name="name" id="name" value="<%= name %>" />
				</div>
				<br/>
		        <div>
						Country	
		<select  name="countryId" id="countryId">
			<%
		Collection<Country> countryList = countryUtil.getAllCountries();
		for (Iterator<Country> iter = countryList.iterator(); iter
				.hasNext();) {
			Country  countryObj = iter.next();
			if(country != null) {
	%>
			<option value="<%= countryObj.getId() %>"><%= countryObj.getName() %></option>
			<% } %>
			<% } %>
			</select>
	</div>
				<input type="hidden" name="stateKey" value="<%= Id %>" />
			
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
<% String defaultTab = "State Management"; %>
<p><a href="<%= viewCurrencyURL %>?tabs1=<%= defaultTab %>">&larr; Back</a></p>