<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@page import="javax.portlet.PortletPreferences"%>
<%@page import="com.ecims.portlet.currencymanagement.AgencyManagementPortletUtil"%>

<%@page import="java.util.Collection"%>
<%@page import="smartpay.entity.Agency"%>
<%@page import="java.util.Iterator"%>
<portlet:defineObjects />
<% //String successMessage = renderRequest.getParameter("successMessage"); %>
<liferay-ui:success key="success" message="Agency created successfully!" />
<liferay-ui:error key="error" message="Sorry, an error prevented saving
the agency" />
<liferay-ui:success key="successUpdate" message="Agency updated successfully!" />
<liferay-ui:success key="successDelete" message="Agency removed successfully!" />

<liferay-ui:error key="errorUpdate" message="Sorry, an error prevented updating
the agency" />

<% AgencyManagementPortletUtil util = new AgencyManagementPortletUtil(); %>
<portlet:renderURL var="editCurrencyURL">
	<portlet:param name="mvcPath"
		value="/html/currencymanagementportlet/editAgency.jsp" />
</portlet:renderURL>

<portlet:renderURL var="addCurrencyURL">
	<portlet:param name="mvcPath"
		value="/html/currencymanagementportlet/addAgency.jsp" />
</portlet:renderURL>

<portlet:renderURL var="deleteAgencyURL">
    <portlet:param name="mvcPath" value="/html/currencymanagementportlet/deleteAgency.jsp" />
</portlet:renderURL>


<a href="<%= addCurrencyURL %>">Add agency</a>
<table cellpadding="5" id="agencyTable"  class="table" style="width:100%; border : 1px solid #ccc;">
<thead>
	<tr class="portlet-section-header results-header">
		<th>Agency Name</th>
		<th>Contact Name</th>
		<th>Phone no.</th>
		<th>Email</th>
		<th>Agency Type</th>
		<th></th>
		<th></th>
	</tr>
	</thead>
	<tbody>
	<%
		Collection<Agency> agencyList = util.getAllAgencies();
		for (Iterator<Agency> iter = agencyList.iterator(); iter
				.hasNext();) {
			Agency agency = iter.next();
			if(agency != null) {
	%>
	<tr >
		<td><%= agency.getAgencyName() %></td>
		<td><%= agency.getContactName() %></td>
		<td><%= agency.getAgencyPhone() %></td>
		<td><%= agency.getAgencyEmail() %></td>
		<td><%= agency.getAgencyType() %></td>
		<td><a href="<%= editCurrencyURL %>?id=<%= agency.getId() %>">Edit</a> </td>
		<td><a onclick="deleteCurrency(); return false;"  href="<%= deleteAgencyURL %>?agencyKey=<%= agency.getId() %>">Delete</a></td>
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
    if(confirm("do you want to delete this Agency ?")){
     evt.preventDefault();
    return false;
      
    }else{
   return true;
    }
   }
   $(document).ready(function() { $('#agencyTable').dataTable( { "order": [[ 3, "desc" ]] } ); } );
</script>