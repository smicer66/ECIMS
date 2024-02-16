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
<%@page import="com.ecims.portlet.currencymanagement.ItemCategorySubManagementPortletUtil"%>

<%@page import="java.util.Collection" %>
<%@page import="smartpay.entity.Country" %>
<%@page import="smartpay.entity.State" %>

<%@page import="smartpay.entity.ItemCategory" %>
<%@page import="smartpay.entity.enumerations.ApplicantType" %>
<%@page import="java.util.Iterator" %>
<%@page import="java.util.List" %>

<portlet:defineObjects />

<portlet:actionURL var="newCountryURL" name="addItemCategorySubAction">
    <portlet:param name="mvcPath" value="/html/currencymanagementportlet/addItemCategorySub.jsp" />
</portlet:actionURL>

<div style="padding-left:10px; padding-right:10px; width:900px">
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold"></span></div>
	  <div class="panel-body">
	    	
	    <form  id="startRegFormId" action="<%=newCountryURL%>" method="post" enctype="application/x-www-form-urlencoded">
	     
		  <div style="padding-bottom:5px">
		  	<fieldset>
		  		<legend>New Item SubCategory</legend>
		         <div>
		Name <input class=" form-control" type="text"  name="name" id="name" />
				</div>
				<br/>
				 <div>
		HsCode <input class=" form-control" type="text"  name="hsCode" id="hsCode" />
				</div>
				<br/>
		        <div>
		Item Category
			<% List<ApplicantType> applicantType = ApplicantType.values();
 %>
 <% ItemCategoryManagementPortletUtil util = new ItemCategoryManagementPortletUtil(); %>
		<select  name="itemCategory" id="itemCategory">
			<%
		Collection<ItemCategory> catList = util.getAllItemCategories();
		for (Iterator<ItemCategory> iter = catList.iterator(); iter
				.hasNext();) {
			ItemCategory itemCategory = iter.next();
			if(itemCategory != null) {
	%>
			<option value="<%= itemCategory.getId() %>"><%= itemCategory.getItemCategoryName() %></option>
			<% } %>
			<% } %>
			</select>
					</div>
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
<% String defaultTab = "Item SubCategory Management"; %>
<p><a href="<%= viewCurrencyURL %>?tabs1=<%= defaultTab %>">&larr; Back</a></p>