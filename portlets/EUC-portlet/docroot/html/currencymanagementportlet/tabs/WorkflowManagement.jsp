<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@page import="javax.portlet.PortletPreferences"%>
<%@page
	import="com.ecims.portlet.currencymanagement.CountryManagementPortletUtil"%>
<%@page
	import="com.ecims.portlet.currencymanagement.ItemCategoryManagementPortletUtil"%>
<%@page
	import="com.ecims.portlet.currencymanagement.AgencyManagementPortletUtil"%>

<%@page
	import="com.ecims.portlet.currencymanagement.WorkflowPortletUtil"%>
<%@page import="java.util.Collection"%>
<%@page import="smartpay.entity.Country"%>
<%@page import="smartpay.entity.ItemCategory"%>
<%@page import="smartpay.entity.enumerations.ApplicantType"%>
<%@page import="smartpay.entity.WokFlowSetting"%>

<%@page import="java.util.Iterator"%>
<portlet:defineObjects />
<%
	//String successMessage = renderRequest.getParameter("successMessage");
%>
<liferay-ui:success key="success"
	message="Work flow item created successfully!" />
<liferay-ui:error key="error"
	message="Sorry, an error prevented saving
the Work flow item" />
<liferay-ui:success key="successUpdate"
	message="Item Category updated successfully!" />
<liferay-ui:success key="successDelete"
	message="Item Category removed successfully!" />

<liferay-ui:error key="errorUpdate"
	message="Sorry, an error prevented updating
the Item Category" />
<%
	AgencyManagementPortletUtil agencyUtil = new AgencyManagementPortletUtil();
%>

<%
	ItemCategoryManagementPortletUtil util = new ItemCategoryManagementPortletUtil();
%>
<portlet:renderURL var="editCurrencyURL">
	<portlet:param name="mvcPath"
		value="/html/currencymanagementportlet/editItemCategory.jsp" />
</portlet:renderURL>

<portlet:renderURL var="addWorkflowURL">
	<portlet:param name="mvcPath"
		value="/html/currencymanagementportlet/addWorkflow.jsp" />
</portlet:renderURL>

<portlet:renderURL var="deleteAgencyURL">
	<portlet:param name="mvcPath"
		value="/html/currencymanagementportlet/deleteItemCategory.jsp" />
</portlet:renderURL>


<portlet:actionURL var="searchWorkflowURL" name="searchWorkflowAction">
	<portlet:param name="mvcPath"
		value="/html/currencymanagementportlet/view.jsp" />
</portlet:actionURL>

<%
	WorkflowPortletUtil  wutil = new WorkflowPortletUtil();
String itemcategoryId = renderRequest.getParameter("itemcategoryId");
if(itemcategoryId == null){
	itemcategoryId = "0";
}

Collection<ItemCategory> catList = util.getAllItemCategories();

ApplicantType appType;
%>
<form method="post" action="<%=searchWorkflowURL%>">
	Select Item category <select name="itemcategory">
	<option value="">Select a category </option>
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
		<option <%=selected %> value="<%=itemCategory.getId()%>"><%=itemCategory.getItemCategoryName()%>
		</option>
		<%
			}
			}
		%>
	</select>
	<input name="tabs1" type="hidden" value="Workflow Management" />
	<button type="submit" class="btn btn-success" style="">Search</button>
</form>

<a href="<%=addWorkflowURL%>">Set workflow for an item</a>
<table cellpadding="5" class="table" id="itemCategoryTable"
	style="width: 100%; border: 1px solid #ccc;">
	<thead>
		<tr class="portlet-section-header results-header">
			<th>Agency</th>
			<th>Position</th>
				</tr>
	</thead>
	<tbody>
		<%
			//WokFlowSetting workflows = wutil.getWorkflowByItemCategory(itemcategoryId);
				Collection<WokFlowSetting> catList2 = wutil.getWorkflowByItemCategory(itemcategoryId);
			ApplicantType appType2;
			
				for (Iterator<WokFlowSetting> iter = catList2.iterator(); iter
		.hasNext();) {
			WokFlowSetting workflow = iter.next();
			if(workflow != null) {
		%>
		<tr>
			<td><%=workflow.getAgency().getAgencyName()%></td>
			<td><%=workflow.getPositionId()%></td>
		</tr>
		<%
			}
				}
		%>
	</tbody>
</table>


<script type="text/javascript">
	function deleteCurrency(evt) {
		//alert("here2");
		if (confirm("do you want to delete this Item Category ?")) {
			evt.preventDefault();
			return false;

		} else {
			return true;
		}
	}

	$(document).ready(function() {
		//alert("here");
		$('#itemCategoryTable').dataTable({
			"order" : [ [ 3, "desc" ] ]
		});
	});
</script>