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
<%@page import="com.ecims.portlet.currencymanagement.ItemCategoryManagementPortletUtil"%>

<%@page import="java.util.Collection" %>
<%@page import="smartpay.entity.Country" %>
<%@page import="smartpay.entity.ItemCategory" %>
<%@page import="smartpay.entity.enumerations.ApplicantType" %>
<%@page import="java.util.Iterator" %>
<%@page import="java.util.List" %>

<portlet:defineObjects />

<%
String Id = (String)renderRequest.getParameter("id");
PortletPreferences prefs = renderRequest.getPreferences();
ItemCategoryManagementPortletUtil util = new ItemCategoryManagementPortletUtil();

String greeting = (String)prefs.getValue(
	    "greeting", "Hello! Welcome to our portal.");
%>

<% ItemCategory itemCategory = util.getItemCategoryById(Id); %>
<% String name = (itemCategory != null) ? itemCategory.getItemCategoryName() : ""; %>
<% String appType = (itemCategory != null) ? itemCategory.getItemFor().getValue() : ""; %>

<portlet:actionURL var="updateAgencyURL" name="editItemCategoryAction">
    <portlet:param name="mvcPath" value="/html/currencymanagementportlet/editItemCategory.jsp" />
</portlet:actionURL>

<div style="padding-left:10px; padding-right:10px; width:900px">
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold"></span></div>
	  <div class="panel-body">
	    	
	    <form  id="startRegFormId" action="<%=updateAgencyURL%>" method="post" enctype="application/x-www-form-urlencoded">
	     
		  <div style="padding-bottom:5px">
		  	<fieldset>
		  		<legend>Update ItemCategory</legend>
		            <div>
			Category <input class=" form-control" type="text"  name="name" id="name" value="<%= name %>" />
				</div>
				<br/>
		        <div>
						Applicant Type
						<% List<ApplicantType> applicantType = ApplicantType.values();
 %>
		<select  name="applicantType" id="applicantType">
	<option value="-1">-Select An Applicant type-</option>
							<%
							for(Iterator<ApplicantType> iter = applicantType.iterator(); iter.hasNext();)
							{
								String applicantTypeStr = iter.next().getValue();
								String selected = "";
								if(appType.equals(applicantTypeStr))
								{
									selected = "selected='selected'";
								}
							
							%>
							
								<option <%= selected %> value="<%=applicantTypeStr %>"><%=applicantTypeStr %></option>
							<%
							}
							
							%>
						</select>
	</div>
				<input type="hidden" name="categoryKey" value="<%= Id %>" />
			
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
<% String defaultTab = "Item Category Management"; %>
<p><a href="<%= viewCurrencyURL %>?tabs1=<%= defaultTab %>">&larr; Back</a></p>