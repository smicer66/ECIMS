<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@page import="javax.portlet.PortletPreferences"%>
<%@page import="com.ecims.portlet.currencymanagement.CountryManagementPortletUtil"%>
<%@page import="com.ecims.portlet.currencymanagement.ItemCategoryManagementPortletUtil"%>

<%@page import="java.util.Collection"%>
<%@page import="smartpay.entity.Country"%>
<%@page import="smartpay.entity.ItemCategory"%>
<%@page import="smartpay.entity.enumerations.ApplicantType"%>

<%@page import="java.util.Iterator"%>
<portlet:defineObjects />
<% //String successMessage = renderRequest.getParameter("successMessage"); %>
<liferay-ui:success key="success" message="Item Category created successfully!" />
<liferay-ui:error key="error" message="Sorry, an error prevented saving
the Item Category" />
<liferay-ui:success key="successUpdate" message="Item Category updated successfully!" />
<liferay-ui:success key="successDelete" message="Item Category removed successfully!" />

<liferay-ui:error key="errorUpdate" message="Sorry, an error prevented updating
the Item Category" />

<% ItemCategoryManagementPortletUtil util = new ItemCategoryManagementPortletUtil(); %>
<portlet:renderURL var="editCurrencyURL">
	<portlet:param name="mvcPath"
		value="/html/currencymanagementportlet/editItemCategory.jsp" />
</portlet:renderURL>

<portlet:renderURL var="addCurrencyURL">
	<portlet:param name="mvcPath"
		value="/html/currencymanagementportlet/addItemCategory.jsp" />
</portlet:renderURL>

<portlet:renderURL var="deleteAgencyURL">
    <portlet:param name="mvcPath" value="/html/currencymanagementportlet/deleteItemCategory.jsp" />
</portlet:renderURL>


<a href="<%= addCurrencyURL %>">Add item category</a>
<table cellpadding="5" class="table" id="itemCategoryTable"  style="width:100%; border : 1px solid #ccc;">
<thead>
	<tr class="portlet-section-header results-header">
		<th>Name</th>
		<th>Applicant type</th>
		<th></th>
		<th></th>
	</tr>
	</thead>
	<tbody>
	<%
		Collection<ItemCategory> catList = util.getAllItemCategories();
	
	ApplicantType appType;
	
		for (Iterator<ItemCategory> iter = catList.iterator(); iter
.hasNext();) {
			ItemCategory itemCategory = iter.next();
			if(itemCategory != null) {
	%>
	<tr >
		<td><%= itemCategory.getItemCategoryName() %></td>
		<td>
		<% appType = itemCategory.getItemFor(); %>
		<%= appType.getValue() %>
		</td>
		<td><a href="<%= editCurrencyURL %>?id=<%= itemCategory.getId() %>">Edit</a> </td>
		<td><a onclick="deleteCurrency(); return false;"  href="<%= deleteAgencyURL %>?categoryKey=<%= itemCategory.getId() %>">Delete</a></td>
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
    if(confirm("do you want to delete this Item Category ?")){
     evt.preventDefault();
    return false;
      
    }else{
   return true;
    }
   }
   
   $(document).ready(function() { $('#itemCategoryTable').dataTable( { "order": [[ 3, "desc" ]] } ); } );
</script>