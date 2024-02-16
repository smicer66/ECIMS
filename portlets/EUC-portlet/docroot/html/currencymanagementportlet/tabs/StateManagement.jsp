<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@page import="javax.portlet.PortletPreferences"%>
<%@page import="com.ecims.portlet.currencymanagement.StateManagementPortletUtil"%>
<%@page import="com.ecims.portlet.currencymanagement.CountryManagementPortletUtil"%>

<%@page import="java.util.Collection"%>
<%@page import="smartpay.entity.Country"%>
<%@page import="smartpay.entity.State"%>
<%@page import="java.util.Iterator"%>
<portlet:defineObjects />
<% //String successMessage = renderRequest.getParameter("successMessage"); %>
<liferay-ui:success key="success" message="State created successfully!" />
<liferay-ui:error key="error" message="Sorry, an error prevented saving
the State" />
<liferay-ui:success key="successUpdate" message="State updated successfully!" />
<liferay-ui:success key="successDelete" message="State removed successfully!" />

<liferay-ui:error key="errorUpdate" message="Sorry, an error prevented updating
the state" />

<% CountryManagementPortletUtil countryUtil = new CountryManagementPortletUtil(); %>
<% StateManagementPortletUtil stateUtil = new StateManagementPortletUtil(); %>

<portlet:renderURL var="editCurrencyURL">
	<portlet:param name="mvcPath"
		value="/html/currencymanagementportlet/editState.jsp" />
</portlet:renderURL>

<portlet:renderURL var="addCurrencyURL">
	<portlet:param name="mvcPath"
		value="/html/currencymanagementportlet/addState.jsp" />
</portlet:renderURL>

<portlet:renderURL var="deleteAgencyURL">
    <portlet:param name="mvcPath" value="/html/currencymanagementportlet/deleteState.jsp" />
</portlet:renderURL>


<a href="<%= addCurrencyURL %>">Add state</a>
<table cellpadding="5" class="table" id="stateTable"  style="width:100%; border : 1px solid #ccc;">
<thead>
	<tr class="portlet-section-header results-header">
		<th>Name</th>
		<th>Country</th>
		<th></th>
		<th></th>
	</tr>
	</thead>
	<tbody>
	<%
		Collection<State> stateList = stateUtil.getAllStates();
	Country country = null;
	String cName;
		for (Iterator<State> iter = stateList.iterator(); iter
				.hasNext();) {
			State state = iter.next();
			if(state != null) {
	%>
	<tr >
		<td><%= state.getName() %></td>
		
		<td>
		<% country = state.getCountry(); %>
		<%try { %>
		<%  cName = (country != null) ? country.getName() : " Country is null"; %>
		<%= cName %>
		<% } catch(Exception ex){ %>
		<%=ex.getMessage() %>
		<% } %>
		</td>
		<td><a href="<%= editCurrencyURL %>?id=<%= state.getId() %>">Edit</a> </td>
		<td><a onclick="deleteCurrency(); return false;"  href="<%= deleteAgencyURL %>?stateKey=<%= state.getId() %>">Delete</a></td>
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
    if(confirm("do you want to delete this State ?")){
     evt.preventDefault();
    return false;      
    }else{
   return true;
    }
   }
   $(document).ready(function() { $('#stateTable').dataTable( { "order": [[ 3, "desc" ]] } ); } );
</script>