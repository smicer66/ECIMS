<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@page import="javax.portlet.PortletPreferences"%>
<%@page import="com.ecims.portlet.currencymanagement.CurrencyManagementPortletUtil"%>
<%@page import="java.util.Collection"%>
<%@page import="smartpay.entity.Currency"%>
<%@page import="java.util.Iterator"%>
<portlet:defineObjects />

<liferay-ui:success key="success" message="Currency saved successfully!" />
<liferay-ui:success key="successDelete" message="Currency removed successfully!" />

<liferay-ui:error key="error" message="Sorry, an error prevented saving
the currency" />
<liferay-ui:success key="successUpdate" message="Currency updated successfully!" />
<liferay-ui:error key="errorUpdate" message="Sorry, an error prevented updating
the currency" />

<%
	CurrencyManagementPortletUtil util = new CurrencyManagementPortletUtil();
	PortletPreferences prefs = renderRequest.getPreferences();
	String greeting = (String) prefs.getValue("greeting",
			"Hello! Welcome to our portal.");
%>

<%
String currencyClass="label-default";
String stateClass="label-default";
String newportalusersClass="label-default";
String approvedportalusersClass="label-default";
String rejectedportalrequestsClass="label-default";

String currentTab = renderRequest.getParameter("currentTab");
 if(currentTab !=null && currentTab.equals("state"))
{
	stateClass="label-primary";
}
 else
{
	currencyClass="label-primary";
}
%>

<portlet:renderURL var="editCurrencyURL">
	<portlet:param name="mvcPath"
		value="/html/currencymanagementportlet/edit.jsp" />
</portlet:renderURL>

<portlet:renderURL var="addCurrencyURL">
	<portlet:param name="mvcPath"
		value="/html/currencymanagementportlet/add.jsp" />
</portlet:renderURL>

<portlet:renderURL var="deleteCurrencyURL">
    <portlet:param name="mvcPath" value="/html/currencymanagementportlet/delete.jsp" />
</portlet:renderURL>

<a href="<%= addCurrencyURL %>">Add currency</a>
<table cellpadding="5" class="table" id="currencyTable"  style="width:500px; border : 1px solid #ccc;">
<thead>
	<tr class="portlet-section-header results-header">
		<th>Name</th>
		<th>Html Entity</th>
		<th></th>
		<th></th>
	</tr>
	</thead>
	<tbody>
	<%
		Collection<Currency> currencyList = util.getAllCurrencies();
		for (Iterator<Currency> iter = currencyList.iterator(); iter
				.hasNext();) {
			Currency currency = iter.next();
	%>
	<tr >
		<td><%=currency.getName()%></td>
		<td><%=currency.getHtmlEntity()%></td>
		<td><a href="<%= editCurrencyURL %>?id=<%= currency.getId() %>">Edit</a> </td>
		<td><a onclick="deleteCurrency(); return false;"  href="<%= deleteCurrencyURL %>?currencyKey=<%= currency.getId() %>">Delete</a></td>
	</tr>
	<% 
		}
	%>
	</tbody>
</table>


<script type="text/javascript">
   function deleteCurrency(evt){
   //alert("here2");
    if(confirm("do you want to delete this currency ?")){
     evt.preventDefault();
    return false;
      
    }else{
   return true;
    }
   }
   $(document).ready(function() { $('#agencyTable').dataTable( { "order": [[ 3, "desc" ]] } ); } );
</script>