<%@page import="com.ecims.portlet.applicationmanagement.ApplicationManagementPortletState"%>
<%@page import="com.ecims.portlet.applicationmanagement.ApplicationManagementPortletState.*"%>
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
<%@ page import="java.util.TimeZone"%>
<%@ page import="java.util.GregorianCalendar"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.entity.Company"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.RoleType"%>
<%@page import="smartpay.entity.Applicant"%>
<%@page import="smartpay.entity.ApplicationItem"%>
<%@page import="smartpay.entity.Application"%>
<%@page import="smartpay.entity.enumerations.UserStatus"%>
<%@page import="smartpay.entity.enumerations.RoleConstants"%>
<%@page import="smartpay.entity.enumerations.ApplicantStatus"%>
<%@page import="smartpay.entity.enumerations.ApplicationStatus"%>
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

ApplicationManagementPortletState portletState = ApplicationManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ApplicationManagementPortletState.class);

%>
<%

if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
{
%>
<jsp:include page="/html/applicationmanagementportlet/tabs_eu.jsp" flush="" />
<%
}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
{
%>
<jsp:include page="/html/applicationmanagementportlet/tabs_nsa.jsp" flush="" />
<%
}
%>

<portlet:actionURL var="listing" name="processAction">
	<portlet:param name="action"
		value="<%=APPLIST_ACTIONS.HANDLE_ACTIONS_ON_LIST_APPLICATIONS.name()%>" />
</portlet:actionURL>

<div style="padding:10px">
<div class="panel panel-info">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>List of Applications</strong></div>
  	<div class="panel-body">
  		<%
  		if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
  		{
  		%>
		<p>Click on an applicant to view the portal users' details.</p>
		<%
  		}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
  		{
  		%>
		<p>To approve multiple applications select their checkboxes before clicking the APPROVE button</p>
		<%
  		}
		%>
  	</div>
		<form  id="userListFormId" action="<%=listing%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
			
		<legend>Application Listing</legend>
		  <%
		  if(portletState.getApplicationListing()!=null && portletState.getApplicationListing().size()>0)
		  {
			  %>
			  
			  <table width="100%" class="table table-hover" id="btable">
				<thead>
					<%
					if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
			  		{
			  		%>
					
					<%
			  		}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
			  		{
			  		%>
					<th>Select All<br><input type="checkbox" name="clickSelectAll" id="clickSelectAll" onclick="javascript:handleSelectAll();" /></th>
					<%
			  		}
					%>
				  
				  <th>Date</th>
				  <th>Application Number</th>
				  <th>Item Category</th>
				  <th>Applicant Number</th>
				  <th>Status</th>
				  <th>Action</th>
				</thead>
				<%
			  for(Iterator<Application> iter1 = portletState.getApplicationListing().iterator(); iter1.hasNext();)
			  {
				  
				  Application app= iter1.next();
				  PortalUser pu = app.getApplicant().getPortalUser();
				  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
				  String dateApplied = sdf.format(new Date(app.getDateCreated().getTime()));
				  Collection<ApplicationItem> appItemList = portletState.getApplicationManagementPortletUtil().getApplicationItemsByApplication(app);
				  Iterator<ApplicationItem> iter = appItemList.iterator();
				  ApplicationItem appItem = iter.next();
				  String status = "";
				  
				  if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
			  	  {
					  if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
						  status = "Approved Awaiting EUC Issuance";
					  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
						  status = "Request Disapproved";
					  else
						  status = "Pending";
			  	  }else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
			  	  {
					  if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
						  status = "Approved Awaiting EUC Issuance";
					  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
						  status = "Request Disapproved";
					  else
						  status = "Pending";
			  	  }
				  
				  %>
				  <tr>
				  	<%
				  	if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
			  		{
			  		%>
					
					<%
			  		}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
			  		{
			  		%>
					<td><input type="checkbox" name="selectAllCheckbox" value="<%=app.getId()%>"></td>
					<%
			  		}
					%>
				  	
					<td><%=dateApplied%></td>
					<td><%=app.getApplicationNumber()==null ? "N/A" : app.getApplicationNumber()%></td>
					<td><%=appItem.getItemCategorySub()==null ? "N/A" : appItem.getItemCategorySub().getHsCode() + " - " + appItem.getItemCategorySub().getItemCategory().getItemCategoryName()%></td>
					<td><%=app.getApplicant()!=null ? (app.getApplicant().getApplicantNumber()==null ? "N/A" : app.getApplicant().getApplicantNumber()) : "N/A"%></td>
					<td><%=status%></td>
					<td>
					
					
					<%
					if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
			  		{
			  		%>
						<button type="button" class="btn btn-success" onclick="javascript:handleButtonAction('view', '<%=app.getId()%>')">View Application Details</button>
					<%
			  		}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
			  		{
			  		%>
						<div class="btn-group">
							<button type="button" class="btn btn-success" onclick="javascript:handleButtonAction('view', '<%=app.getId()%>')">View Application Details</button>
						  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
								<span class="caret"></span>
								<span class="sr-only">Toggle Dropdown</span>
							</button>
						  	<ul class="dropdown-menu" role="menu">
								<li><a href="javascript: handleButtonAction('view', '<%=app.getId()%>')">View Application Details</a></li>
								<li><a href="javascript: handleButtonAction('approve', '<%=app.getId()%>')">Approve Application</a></li>
								<li><a href="javascript: handleButtonAction('reject', '<%=app.getId()%>')">Reject Application</a></li>
						  	</ul>
						</div>
					<%
			  		}
					%>
					
								  
					</td>
				  </tr>
				  <%
			  }
				%>
			   </table>
			  <%
		  }
		  %>
		<input type="hidden" name="selectedApplications" id="selectedApplications" value="" />
		<input type="hidden" name="selectedUserAction" id="selectedUserAction" value="" />	
	</form>
</div>
</div>


<script type="text/javascript" charset="utf-8" src="<%=jqueryDataTableUrl%>"></script>

<script type="text/javascript">
$(document).ready(function() {
    $('#btable').dataTable();
} );


function handleButtonAction(action, usId){
	
	if(action=='approve' || action=='reject')
	{
		if(confirm("Are you sure you want to approve the selected application(s)?"))
		{
			document.getElementById('selectedApplications').value = usId;
			document.getElementById('selectedUserAction').value = action;
			document.getElementById('userListFormId').submit();
		}
	}else
	{
		document.getElementById('selectedApplications').value = usId;
		document.getElementById('selectedUserAction').value = action;
		document.getElementById('userListFormId').submit();
	}
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

