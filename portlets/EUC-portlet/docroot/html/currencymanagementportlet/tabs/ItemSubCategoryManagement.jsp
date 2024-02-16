<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@page import="javax.portlet.PortletPreferences"%>
<%@page import="com.ecims.portlet.currencymanagement.CountryManagementPortletUtil"%>
<%@page import="com.ecims.portlet.currencymanagement.ItemCategoryManagementPortletUtil"%>
<%@page import="com.ecims.portlet.currencymanagement.ItemCategorySubManagementPortletUtil"%>

<%@page import="java.util.Collection"%>
<%@page import="smartpay.entity.Country"%>
<%@page import="smartpay.entity.ItemCategory"%>
<%@page import="smartpay.entity.ItemCategorySub"%>
<%@page import="smartpay.entity.enumerations.ApplicantType"%>

<%@page import="java.util.Iterator"%>
<portlet:defineObjects />
<% //String successMessage = renderRequest.getParameter("successMessage"); %>
<liferay-ui:success key="success" message="Item SubCategory created successfully!" />
<liferay-ui:error key="error" message="Sorry, an error prevented saving
the Item SubCategory" />
<liferay-ui:success key="successUpdate" message="Item SubCategory updated successfully!" />
<liferay-ui:success key="successDelete" message="Item SubCategory removed successfully!" />

<liferay-ui:error key="errorUpdate" message="Sorry, an error prevented updating
the Item SubCategory" />

<% ItemCategorySubManagementPortletUtil util = new ItemCategorySubManagementPortletUtil(); %>
<portlet:renderURL var="editCurrencyURL">
	<portlet:param name="mvcPath"
		value="/html/currencymanagementportlet/editItemCategorySub.jsp" />
</portlet:renderURL>

<portlet:renderURL var="addCurrencyURL">
	<portlet:param name="mvcPath"
		value="/html/currencymanagementportlet/addItemCategorySub.jsp" />
</portlet:renderURL>

<portlet:renderURL var="deleteAgencyURL">
    <portlet:param name="mvcPath" value="/html/currencymanagementportlet/deleteItemCategorySub.jsp" />
</portlet:renderURL>


<a href="<%= addCurrencyURL %>">Add item subcategory</a>
<table cellpadding="5" class="table" id="itemSubCategoryTable" style="width:100%; border : 1px solid #ccc;">
<thead>
	<tr class="portlet-section-header results-header">
		<th>Name</th>
		<th>HsCode</th>
		<th>category</th>
		<th></th>
		<th></th>
	</tr>
	</thead>
	<tbody>
	<%
		Collection<ItemCategorySub> catList = util.getAllItemSubCategories();
	
	ItemCategory itemCategory;
	
		for (Iterator<ItemCategorySub> iter = catList.iterator(); iter
.hasNext();) {
			ItemCategorySub itemCategorySub = iter.next();
			if(itemCategorySub != null) {
	%>
	<tr >
		<td><%= itemCategorySub.getName() %></td>
		<td><%= itemCategorySub.getHsCode() %></td>
		<td>
		<%  %>
		<% itemCategory = itemCategorySub.getItemCategory(); %>
		<%= itemCategory.getItemCategoryName() %>
		</td>
		<td><a href="<%= editCurrencyURL %>?id=<%= itemCategorySub.getId() %>">Edit</a> </td>
		<td><a onclick="deleteCurrency(); return false;"  href="<%= deleteAgencyURL %>?subcategoryKey=<%= itemCategorySub.getId() %>">Delete</a></td>
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
    if(confirm("do you want to delete this Item SubCategory ?")){
     evt.preventDefault();
    return false;
      
    }else{
   return true;
    }
   }
   $(document).ready(function() { $('#itemSubCategoryTable').dataTable( { "order": [[ 3, "desc" ]] } ); } );
</script>