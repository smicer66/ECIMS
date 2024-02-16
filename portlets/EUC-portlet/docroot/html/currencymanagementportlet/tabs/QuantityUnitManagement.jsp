<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@page import="javax.portlet.PortletPreferences"%>
<%@page import="com.ecims.portlet.currencymanagement.CountryManagementPortletUtil"%>
<%@page import="com.ecims.portlet.currencymanagement.QuantityUnitManagementPortletUtil"%>
<%@page import="com.ecims.portlet.currencymanagement.ItemCategorySubManagementPortletUtil"%>

<%@page import="java.util.Collection"%>
<%@page import="smartpay.entity.Country"%>
<%@page import="smartpay.entity.QuantityUnit"%>
<%@page import="smartpay.entity.ItemCategorySub"%>
<%@page import="smartpay.entity.enumerations.ApplicantType"%>

<%@page import="java.util.Iterator"%>
<portlet:defineObjects />
<% //String successMessage = renderRequest.getParameter("successMessage"); %>
<liferay-ui:success key="success" message="Quantity unit created successfully!" />
<liferay-ui:error key="error" message="Sorry, an error prevented saving
the Quantity unit" />
<liferay-ui:success key="successUpdate" message="Quantity unit updated successfully!" />
<liferay-ui:success key="successDelete" message="Quantity unit removed successfully!" />

<liferay-ui:error key="errorUpdate" message="Sorry, an error prevented updating
the Quantity unit" />

<% QuantityUnitManagementPortletUtil util = new QuantityUnitManagementPortletUtil(); %>
<portlet:renderURL var="editCurrencyURL">
	<portlet:param name="mvcPath"
		value="/html/currencymanagementportlet/editQuantityUnit.jsp" />
</portlet:renderURL>

<portlet:renderURL var="addCurrencyURL">
	<portlet:param name="mvcPath"
		value="/html/currencymanagementportlet/addQuantityUnit.jsp" />
</portlet:renderURL>

<portlet:renderURL var="deleteAgencyURL">
    <portlet:param name="mvcPath" value="/html/currencymanagementportlet/deleteQuantityUnit.jsp" />
</portlet:renderURL>


<a href="<%= addCurrencyURL %>">Add quantity unit</a>
<table cellpadding="5" class="table" id="quantityUnitTable" style="width:100%; border : 1px solid #ccc;">
<thead>
	<tr class="portlet-section-header results-header">
		<th>Name</th>
		<th>Unit</th>
		<th>Status</th>
		<th></th>
		<th></th>
	</tr>
	</thead>
	<tbody>
	<%
		Collection<QuantityUnit> catList = util.getAllQuantityUnits();
	
	
		for (Iterator<QuantityUnit> iter = catList.iterator(); iter.hasNext();) {
			QuantityUnit qunit = iter.next();
			if(qunit != null) {
	%>
	<tr >
		<td><%= qunit.getName() %></td>
		<td><%= qunit.getUnit() %></td>
		<td>
		<% String status = String.valueOf(qunit.getStatus()); %>
		<% String statusValue = (qunit.getStatus()) ? "Enabled" : "Disabled"; %>
		<% // status %>
		<%= statusValue %>
		</td>
		<td><a href="<%= editCurrencyURL %>?id=<%= qunit.getId() %>">Edit</a> </td>
		<td><a onclick="deleteCurrency(); return false;"  href="<%= deleteAgencyURL %>?unitKey=<%= qunit.getId() %>">Delete</a></td>
	</tr>
	<% 
			}
		}
	%>
	</tbody>
</table>


<script type="text/javascript">
   function deleteCurrency(evt){
   //alert("here2");
    if(confirm("do you want to delete this Unit ?")){
     evt.preventDefault();
    return false;
      
    }else{
   return true;
    }
   }
   $(document).ready(function() { $('#quantityUnitTable').dataTable( { "order": [[ 3, "desc" ]] } ); } );
</script>