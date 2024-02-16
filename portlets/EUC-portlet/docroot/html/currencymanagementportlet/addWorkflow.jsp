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

<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>

<%@ page import="javax.portlet.PortletPreferences" %>
<%@page import="com.ecims.portlet.currencymanagement.StateManagementPortletUtil"%>
<%@page import="com.ecims.portlet.currencymanagement.ItemCategoryManagementPortletUtil"%>
<%@page
	import="com.ecims.portlet.currencymanagement.WorkflowPortletUtil"%>
	<%@page import="smartpay.entity.Agency"%>
<%@page import="com.ecims.portlet.currencymanagement.AgencyManagementPortletUtil"%>
	
<%@page import="java.util.Collection"%>
<%@page import="smartpay.entity.Country"%>
<%@page import="smartpay.entity.ItemCategory"%>
<%@page import="smartpay.entity.enumerations.ApplicantType"%>
<%@page import="smartpay.entity.WokFlowSetting"%>

<%@page import="java.util.Iterator"%>
<portlet:defineObjects />


<portlet:actionURL var="newCountryURL" name="addWorkflowAction">
    <portlet:param name="mvcPath" value="/html/currencymanagementportlet/addWorkflow.jsp" />
</portlet:actionURL>

<portlet:actionURL var="getworkflowURL" name="getWorkflowAction">
    <portlet:param name="mvcPath" value="/html/currencymanagementportlet/addWorkflow.jsp" />
</portlet:actionURL>

<liferay-ui:error key="error" message="The positions contains  wrong entry(s)." />
<liferay-ui:error key="errorUpdate"
	message="The positions contains  wrong entry(s)." />
<% AgencyManagementPortletUtil agencyUtil = new AgencyManagementPortletUtil(); %>

	<%
	//first count all agencies
	int agencyCount = 0;
		Collection<Agency> agencyList = agencyUtil.getAllAgencies();
		for (Iterator<Agency> iter = agencyList.iterator(); iter
				.hasNext();) {
			Agency agency = iter.next();
			if(agency != null) {
				agencyCount+= 1;
			}
		}
	%>

<div style="padding-left:10px; padding-right:10px; width:900px">
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold"></span></div>
	  <div class="panel-body">
	    	
	    <form  id="startRegFormId" action="<%=newCountryURL%>" method="post" enctype="application/x-www-form-urlencoded">
	     
		  <div style="padding-bottom:5px">
		  	<fieldset>
		  		<legend>New Item Workflow</legend>
		  		<div>
		  		Select Item category <select name="itemcategory" id="itemcategory" >
		  		<option value="">Please select an item</option>
		<%
	ItemCategoryManagementPortletUtil util = new ItemCategoryManagementPortletUtil();
%>
		<%
	WorkflowPortletUtil  wutil = new WorkflowPortletUtil();
String itemcategoryId = renderRequest.getParameter("itemcategoryId");
if(itemcategoryId == null){
	itemcategoryId = "0";
}

Collection<ItemCategory> catList = util.getAllItemCategories();

ApplicantType appType;
%>
		
		<%
			for (Iterator<ItemCategory> iter = catList.iterator(); iter
		.hasNext();) {
			ItemCategory itemCategory = iter.next();
			if(itemCategory != null) {
				String selected = "";
				if(itemCategory.getId() == Long.parseLong(itemcategoryId))
				{
					selected = "selected='selected'";
				}
		%>
		<option <%= selected %> value="<%=itemCategory.getId()%>"><%=itemCategory.getItemCategoryName()%>
		</option>
		<%
			}
			}
		%>
	</select>
		  		</div> <br/>
		  		<table class="table">
		  		<thead>
		  		<tr>
		  		
		  		<th>Agency</th>
		  		<th>Position</th>
		  		</tr>
		  		</thead>
		  		<tbody>
		  		
		  		<%
		  		int agencyCntrl = 0;
		  		int position;
		  		for (Iterator<Agency> iter = agencyList.iterator(); iter
						.hasNext();) {
					Agency agency = iter.next();
					if(agency != null) {
						agencyCntrl += 1;
						%>
						<tr>
						<td> <%= agency.getAgencyName() %> <input type="hidden" name="agencyid<%= agencyCntrl %>" value="<%= agency.getId() %>" /></td>
						<td> 
						    <select name="positionid<%= agencyCntrl %>">
						    <option value="">Select</option>
						      <% for(int i = 0; i < agencyCount; i++){ %>
						      <% position = (i+1); %>
						      <%
						      String selected = "";
						      String positionValue = renderRequest.getParameter("positionid"+agencyCntrl);
								if(positionValue == null){
									positionValue = "0";
								}
						      if(position == Integer.parseInt(positionValue))
								{
									selected = "selected='selected'";
								}
						      %>
						      <option <%= selected %> value="<%= position %>" > <%= position %></option>
						      <% } %>
						    </select>
						 </td>
						 </tr>
						<%
					}
				}
		  		%>
		  		
		  		</tbody>
		  		</table>
		  		<input type="hidden" name="total" value="<%= agencyCntrl %>" />
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
<% String defaultTab = "Workflow Management"; %>
<p><a href="<%= viewCurrencyURL %>?tabs1=<%= defaultTab %>">&larr; Back</a></p>

<form id="getworkflow" action=<%= getworkflowURL %> method="post">
<input type="hidden" name="getitemcategory" id="getitemcategory" value="" />
</form>
<script type="text/javascript">
$(document).ready(function() {
	$("#itemcategory").change(function(){
		var itemcat = $(this).val();
		if(itemcat != ""){
			//submit form 
			$("#getitemcategory").val(itemcat);
			$("#getworkflow").submit();
		}
	});
	
});
	function getWorkflow(evt) {
		//alert("here2");
	
		if (confirm("do you want to delete this Item Category ?")) {
			evt.preventDefault();
			return false;

		} else {
			return true;
		}
	}
	</script>