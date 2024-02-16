<%@page import="smartpay.entity.Certificate"%>
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

<div style="padding:10px" id="print-content">
<div class="panel panel-info">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>List of Applications</strong></div>
  	<div class="panel-body">
  		<%
  		if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
  		{
  		%>
		<p>Click on an application to view the applications' details.</p>
		<%
  		}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
  		{
  		%>
		<p>Click on an application to view the applications' details.</p>
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
				  
				  <th>Date Created</th>
				  <th>Application Number</th>
				  <th>Item Category</th>
				  <th>Applicant Number</th>
				  <th>Status</th>
				  <th>Action</th>
				  <th>&nbsp;</th>
				</thead>
				<%
			  for(Iterator<Application> iter1 = portletState.getApplicationListing().iterator(); iter1.hasNext();)
			  {
				  
				  Application app= iter1.next();
				  PortalUser pu = app.getApplicant().getPortalUser();
				  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
				  String dateApplied = sdf.format(new Date(app.getDateCreated().getTime()));
				  Collection<ApplicationItem> appItemList = portletState.getApplicationManagementPortletUtil().getApplicationItemsByApplication(app);
				  boolean check = false;
				  if(portletState.getApplicationinAppFlags()!=null && portletState.getApplicationinAppFlags().contains(app))
					  check = true;
				  
				  Certificate cert = portletState.getApplicationManagementUtil().getCertificateByApplication(app);
				  
				  
				  ApplicationItem appItem = null;
				  if(appItemList!=null && appItemList.size()>0)
				  {
					  Iterator<ApplicationItem> iter = appItemList.iterator();
					  appItem = iter.next();
				  }
				  String status = "";
				  
				  if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
			  	  {
					  if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
						  status = "Approved Awaiting EUC Issuance";
					  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
						  status = "Request Disapproved";
					  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED))
						  status = "Certificate Issued";
					  else
						  status = "Pending";
			  	  }else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
			  	  {
					  if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
						  status = "Approved Awaiting EUC Issuance";
					  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
						  status = "Request Disapproved";
					  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED))
						  status = "Certificate Issued";
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
					<td><%=(appItem!=null && appItem.getItemCategorySub()!=null) ? (appItem.getItemCategorySub().getHsCode() + " - " + appItem.getItemCategorySub().getItemCategory().getItemCategoryName()) : "N/A"%></td>
					<td><%=app.getApplicant()!=null ? (app.getApplicant().getApplicantNumber()==null ? "N/A" : app.getApplicant().getApplicantNumber()) : app.getPortalUser().getAgency().getAgencyName()%></td>
					<td><%=status%></td>
					<td>
					
					
					<%
					if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
			  		{
			  		%>
						
						
						
					<%
						if(app.getStatus()!=null && (app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED) || 
								 app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED)))
						{
							%>
							<div class="btn-group">
								<button type="button" class="btn btn-success" onclick="javascript:handleButtonAction('view', '<%=app.getId()%>')">View Application Details</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									<li><a href="javascript: handleButtonAction('viewApp', '<%=app.getId()%>')">View Application Details</a></li>
									<%
									//if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED))
									//{
									%>
									<li><a href="javascript: handleButtonAction('resendCode', '<%=app.getId()%>')">Resend Collection Code</a></li>
									<%
									if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED) 
											|| app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
									{
										if(app.getPortalUser()!=null)
										{
											%>
											<li><a target='_blank' href="javascript: handleButtonAction3('downloadAgencyTicket', '<%=app.getPortalUser().getId()%>', '<%=app.getId()%>')">Download Certificate Ticket</a></li>
											<%
										}
										
										if(app.getApplicant()!=null)
										{
											%>
											<li><a target='_blank' href="javascript: handleButtonAction2('downloadApplicantTicket', '<%=app.getApplicant().getPortalUser().getId()%>', '<%=app.getId()%>')">Download Certificate Ticket</a></li>
											<%
										}
									
									}
									//}
									%>
							  	</ul>
							</div>
							<%
						}else
						{
							%>
							<button type="button" class="btn btn-success" onclick="javascript:handleButtonAction('viewApp', '<%=app.getId()%>')">View Application Details</button>
							<%
						}
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
				  	<td>&nbsp;
						<%
						if(check)
						{
							%>
							<img src="/resources/images/flag.png" style="height:18px" title="Application has been flagged previously" >
							<%
						}
						%></td>
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
<input type="button" onclick="printDiv1('print-content')" value="Print Page"/>

<script type="text/javascript" charset="utf-8" src="<%=jqueryDataTableUrl%>"></script>

<script type="text/javascript">
$(document).ready(function() {
    $('#btable').dataTable();
} );




//gyus=")%>'+aert +'&iosdp='+usId" +		//gyus = portalUser, iosdp = certificate id
//handleButtonAction2(action, aert, usId)		//aert = portalUser, usId = certificate id
function handleButtonAction2(action, usId, aert)
{
	if(action=='downloadApplicantTicket')
	{
		document.location.href='<%=response.encodeURL(request.getContextPath()+"/ActiveServlet?action=downloadApplicantTicket&gyus=")%>'+aert +'&iosdp='+usId;
	}
}

function handleButtonAction3(action, usId, aert)
{
	if (action=='downloadAgencyTicket')
	{
		document.location.href='<%=response.encodeURL(request.getContextPath()+"/ActiveServlet?action=downloadAgencyTicket&gyus=")%>'+aert +'&iosdp='+usId;
	}
}

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

function printDiv1(divId) {
	var mywindow = window.open('', divId, 'height=400,width=600');
	var data = document.getElementById(divId).innerHTML;
	mywindow.document.write('<html><head><title>ECIMS</title>');
	//mywindow.document.write('<link href="http://localhost/html/css/main.css?browserId=other&amp;themeId=DarkRider_WAR_DarkRidertheme&amp;languageId=en_US&amp;b=6100&amp;t=1323698628000" rel="stylesheet" type="text/css"><link href="http://localhost/EUC-portlet/css/main.css?browserId=other&amp;themeId=DarkRider_WAR_DarkRidertheme&amp;languageId=en_US&amp;b=6100&amp;t=1416975581850" rel="stylesheet" type="text/css"><link class="lfr-css-file" href="http://localhost/DarkRider-theme/css/main.css?browserId=other&amp;themeId=DarkRider_WAR_DarkRidertheme&amp;languageId=en_US&amp;b=6100&amp;t=1416667852292" rel="stylesheet" type="text/css"><link href="http://localhost/resources/css/kelvin/framework.css" rel="stylesheet"><link href="http://localhost/resources/css/kelvin/fonts.css" rel="stylesheet"><link href="http://localhost/resources/css/kelvin/default.css" rel="stylesheet"><link href="http://localhost/resources/css/kelvin/style.css" rel="stylesheet"><link href="http://localhost/resources/css/kelvin/fotorama.css" rel="stylesheet">');
	mywindow.document.write('</head><body >');
	mywindow.document.write(data);
	mywindow.document.write('</body></html>');
	mywindow.document.close(); // necessary for IE >= 10
	mywindow.focus(); // necessary for IE >= 10
	
	mywindow.print();
	mywindow.close();
	
	return true;
}
</script>

