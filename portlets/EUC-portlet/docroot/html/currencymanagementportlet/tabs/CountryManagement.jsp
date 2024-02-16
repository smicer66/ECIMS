<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@page import="javax.portlet.PortletPreferences"%>
<%@page import="com.ecims.portlet.currencymanagement.CountryManagementPortletUtil"%>

<%@page import="java.util.Collection"%>
<%@page import="smartpay.entity.Country"%>
<%@page import="java.util.Iterator"%>
<portlet:defineObjects />
<% //String successMessage = renderRequest.getParameter("successMessage"); %>
<liferay-ui:success key="success" message="Country created successfully!" />
<liferay-ui:error key="error" message="Sorry, an error prevented saving
the country" />
<liferay-ui:success key="successUpdate" message="Country updated successfully!" />
<liferay-ui:success key="successDelete" message="Country removed successfully!" />

<liferay-ui:error key="errorUpdate" message="Sorry, an error prevented updating
the country" />

<% CountryManagementPortletUtil util = new CountryManagementPortletUtil(); %>
<portlet:renderURL var="editCurrencyURL">
	<portlet:param name="mvcPath"
		value="/html/currencymanagementportlet/editCountry.jsp" />
</portlet:renderURL>

<portlet:renderURL var="addCurrencyURL">
	<portlet:param name="mvcPath"
		value="/html/currencymanagementportlet/addCountry.jsp" />
</portlet:renderURL>

<portlet:renderURL var="deleteAgencyURL">
    <portlet:param name="mvcPath" value="/html/currencymanagementportlet/deleteCountry.jsp" />
</portlet:renderURL>


<a href="<%= addCurrencyURL %>">Add country</a>
<table id="countryTable" class="table" cellpadding="5"  style="width:100%; border : 1px solid #ccc;">
<thead>
	<tr class="portlet-section-header results-header">
		<th>Name</th>
		<th>Code</th>
		<th></th>
		<th></th>
	</tr>
	</thead>
	<tbody>
	<%
		Collection<Country> agencyList = util.getAllCountries();
		for (Iterator<Country> iter = agencyList.iterator(); iter
				.hasNext();) {
			Country country = iter.next();
			if(country != null) {
	%>
	<tr >
		<td><%= country.getName() %></td>
		<td><%= country.getCode() %></td>
		<td><a href="<%= editCurrencyURL %>?id=<%= country.getId() %>">Edit</a> </td>
		<td><a onclick="deleteCurrency(); return false;"  href="<%= deleteAgencyURL %>?countryKey=<%= country.getId() %>">Delete</a></td>
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
    if(confirm("do you want to delete this Country ?")){
     evt.preventDefault();
    return false;
      
    }else{
   return true;
    }
   }
   $(document).ready(function() { $('#countryTable').dataTable( { "order": [[ 3, "desc" ]] } ); } );
</script>