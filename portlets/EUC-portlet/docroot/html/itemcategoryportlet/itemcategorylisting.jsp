<%@page import="smartpay.entity.enumerations.ApplicantType"%>
<%@page import="com.ecims.portlet.admin.itemcategoryportlet.ItemCategoryPortletState"%>
<%@page import="com.ecims.portlet.admin.itemcategoryportlet.ItemCategoryPortletState.*"%>
<%@page import="java.util.Collection"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@ page import="com.liferay.portal.kernel.util.Validator"%>
<%@ page import="javax.portlet.PortletPreferences"%>
<%@ page import="javax.portlet.PortletURL"%>
<%@ page import="javax.portlet.ActionRequest"%>
<%@ page import="javax.portlet.RenderResponse"%>
<%@ page import="java.lang.NumberFormatException"%>
<%@ page import="com.liferay.portal.util.PortalUtil"%>
<%@ page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.util.Arrays"%>
<%@ page import="java.util.TimeZone"%>
<%@ page import="java.util.GregorianCalendar"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.audittrail.AuditTrail"%>
<%@page import="smartpay.entity.Company"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.RoleType"%>
<%@page import="smartpay.entity.Applicant"%>
<%@page import="smartpay.entity.ApplicationItem"%>
<%@page import="smartpay.entity.Application"%>
<%@page import="smartpay.entity.ApplicationFlag"%>
<%@page import="smartpay.entity.ItemCategory"%>
<%@page import="smartpay.entity.ItemCategoryApplicantType"%>
<%@page import="smartpay.entity.ApplicationAttachment"%>
<%@page import="smartpay.entity.Role"%>
<%@page import="smartpay.entity.Certificate"%>
<%@page import="smartpay.entity.ApplicationWorkflow"%>
<%@page import="smartpay.entity.enumerations.UserStatus"%>
<%@page import="smartpay.entity.enumerations.RoleConstants"%>
<%@page import="smartpay.entity.enumerations.ApplicantStatus"%>
<%@page import="smartpay.entity.enumerations.ApplicationStatus"%>
<%@page import="smartpay.entity.enumerations.CertificateStatus"%>
<%@page import="smartpay.entity.enumerations.PermissionType"%>
<%@page import="com.ecims.commins.ECIMSConstants"%>
<%@page import="java.text.DateFormat"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />
<%
	String resourceBaseURL = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ "/resources";
	String faceboxCssUrl = resourceBaseURL + "/css/facebox.css";
	String pagingUrl = resourceBaseURL + "/css/paging.css";

	String jqueryDataTableCssUrl = resourceBaseURL + "/css/jquery.dataTables.css";
	String jqueryDataTableThemeCssUrl = resourceBaseURL + "/css/jquery.dataTables_themeroller.css";
	String jqueryDataTableUrl = resourceBaseURL + "/js/jquery.dataTables.min.js";

		
	String jqueryUICssUrl = resourceBaseURL + "/css/jquery-ui.min.css";
	
	String jqueryJsUrl = resourceBaseURL + "/js/jquery-1.10.2.min.js";
	String jqueryUIJsUrl = resourceBaseURL + "/js/jquery-ui.min.js";
%>
<script type="text/javascript"
	src="<%=(resourceBaseURL + "/js/jquery.validate.js")%>"></script>
<script type="text/javascript"
	src="<%=(resourceBaseURL + "/js/facebox.js")%>"></script>
<script type="text/javascript"
	src="<%=(resourceBaseURL + "/js/paging.js")%>"></script>
<link rel="stylesheet" href='<%=faceboxCssUrl%>' type="text/css" />
<link rel="stylesheet" href='<%=pagingUrl%>' type="text/css" />
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" /><%


ItemCategoryPortletState portletState = ItemCategoryPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ItemCategoryPortletState.class);
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
%>

<jsp:include page="/html/itemcategoryportlet/tabs.jsp" flush="" />


<portlet:actionURL var="proceedToStepThree" name="processAction">
	<portlet:param name="action"
		value="<%=ITEM_CATEGORY_ACTIONS.HANDLE_ITEM_CATEGORY_ACTIONS.name()%>" />
</portlet:actionURL>

<div style="padding:10px" id="print-content">
<div class="panel panel-info">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>Category of Importation Items</strong></div>
  	<div class="panel-body">&nbsp;
  	</div>
  	<form  id="startRegFormId" action="<%=proceedToStepThree%>" method="post" enctype="application/x-www-form-urlencoded" >
		<legend>Item Category Listing</legend>
		  <%
		  if(portletState.getItemCategoryListing()!=null && portletState.getItemCategoryListing().size()>0)
		  {
			  %>
			  
			  <table width="100%" class="table table-hover" id="btable">
				<thead>
				
					  <th>Item Category Name</th>
					  <th>Item For</th>
					  <th>Action</th>
				</thead>
				<%
			  for(Iterator<ItemCategory> iter1 = portletState.getItemCategoryListing().iterator(); iter1.hasNext();)
			  {
				  ItemCategory ad = iter1.next();
				  Collection<ApplicantType> icat = (Collection<ApplicantType>)portletState.getItemCategoryPortletUtil().
						  getItemCategoryApplicantTypeByItemCategory(ad); 
				  String itemFor = "";
				  for(Iterator<ApplicantType> it= icat.iterator(); it.hasNext();)
				  {
					  itemFor +=it.next().getValue() + ", ";
				  }
				  %>
				  <tr>
					  <td><%=ad.getItemCategoryName() %></td>
					  <td><%=itemFor.substring(0, (itemFor.length() - 2)) %></td>
					  <td>
					  		<div class="btn-group">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									<li><a href="javascript: handleButtonAction('edititemcategory', '<%=ad.getId()%>')">Edit Item Category Details</a></li>
									<li><a href="javascript: handleButtonAction('addhscode', '<%=ad.getId()%>')">Add HS Codes to Item Category</a></li>
								</ul>
							</div>
					  </td>
				  </tr>
				  <%
			  }
				%>
			   </table>
			  <%
		  }
		  %>
		  <input name="act" id="act" value="" type="hidden">
		  <input name="actId" id="actId" value="" type="hidden"> 
	</form>
</div>
</div>
<input type="button" onclick="printDiv('print-content')" value="Print Page"/>

<script type="text/javascript" charset="utf-8" src="<%=jqueryDataTableUrl%>"></script>

<script type="text/javascript">
$(document).ready(function() {
    $('#btable').dataTable();
} );


function handleButtonAction(action, usId){
	
	document.getElementById('act').value=action;
	document.getElementById('actId').value=usId;
	document.getElementById('startRegFormId').submit();
}


function handleSelectAll()
{
	if(document.getElementById('clickSelectAll').checked==true)
	{
		var cbs = document.getElementsByName('selectAllCheckbox');
		for(var i=0; i<cbs.length; i++)
		{
			cbs[i].checked = true;
		}
	}	  
	else
	{
		var cbs = document.getElementsByName('selectAllCheckbox');
		for(var i=0; i<cbs.length; i++)
		{
			cbs[i].checked = false;
		}
	}
}



function isCheckBoxesChecked()
{
	var cbs = document.getElementsByName('selectAllCheckbox');
	var c = 0;
	for(var i=0; i < cbs.length; i++) {
		if(document.getElementsByName('selectAllCheckbox')[i].checked)
		{
			c++;
		}
	}
	
	if(c==0)
		return false;
	else
		return true;
}



function uncheckAll()
{
	var cbs = document.getElementsByName('selectAllCheckbox');
	var c = 0;
	for(var i=0; i < cbs.length; i++) {
		document.getElementsByName('selectAllCheckbox')[i].checked=false;
	}
}
</script>

