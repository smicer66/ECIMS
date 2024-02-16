<%@page import="java.util.ArrayList"%>
<%@page import="smartpay.entity.enumerations.PermissionType"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.List"%>
<%@page import="smartpay.entity.Agency"%>
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
<%@page import="smartpay.entity.Role"%>
<%@page import="smartpay.entity.ApplicationWorkflow"%>
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
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" />
<link href="<%=jqueryDataTableCssUrl%>" rel="stylesheet" type="text/css" /><%

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
<div class="panel panel-info" style="padding-bottom:40px;">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>List of Applications</strong></div>
  	<div class="panel-body">&nbsp;
  	</div>
		<form  id="userListFormId" action="<%=listing%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
			
		<legend>Application Listing</legend>
		  <%
		  if(portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()>0)
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
			  			if(portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION) && 
			  					portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()>0 && portletState.getCurrentTab()!=null && 
			  					portletState.getCurrentTab().equals(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA))
			  		   {
			  		%>
					 <th>Select All<br><input type="checkbox" name="clickSelectAll" id="clickSelectAll" onclick="javascript:handleSelectAll();" /></th>
					<%
			  			}
			  		}
					%>
				  
				  <th>Date of Action(Y-M-D)</th>
				  <th>Application Number</th>
				  <th>Applicant Number</th>
				  <th>Item Category</th>
				  <th>Status</th>
				  <th>Action</th>
				  <%
				  if(portletState.getPermissionList()!=null && !portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION))
		  			{
				  %>
				  <th>&nbsp;</th>
				  <%
		  			}
				  %>
				</thead>
				<%
			  ArrayList h1 = new ArrayList();
			  for(Iterator<ApplicationWorkflow> iter1 = portletState.getApplicationWorkFlowListing().iterator(); iter1.hasNext();)
			  {
				  
				  ApplicationWorkflow appWf= iter1.next();
				  Application app = appWf.getApplication();
				  PortalUser pu = app.getApplicant()!=null ? app.getApplicant().getPortalUser() : app.getPortalUser();
				  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
				  boolean check = false;
				  if(portletState.getApplicationinAppFlags()!=null && portletState.getApplicationinAppFlags().contains(app))
					  check = true;
				  
				  String dateApplied = "";
				  if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
				  {
					  dateApplied = sdf.format(new Date(app.getDateCreated().getTime())) + "";
				  }else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
				  {
					  dateApplied = sdf.format(new Date(appWf.getDateCreated().getTime())) + "";
				  }else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
				  {
					  dateApplied = sdf.format(new Date(appWf.getDateCreated().getTime())) + "";
				  }
				  
				  Collection<ApplicationItem> appItemList = portletState.getApplicationManagementPortletUtil().getApplicationItemsByApplication(app);
				  if(appItemList!=null && appItemList.size()>0)
				  {
					  Iterator<ApplicationItem> iter = appItemList.iterator();
					  ApplicationItem appItem = iter.next();
					  String status = "";
					  String roleSource = ((RoleType)portletState.getApplicationManagementPortletUtil().getEntityObjectById(RoleType.class, appWf.getSourceId())).getName().getValue();
					  String roleRec = ((RoleType)portletState.getApplicationManagementPortletUtil().getEntityObjectById(RoleType.class, appWf.getReceipientRole())).getName().getValue();
					  Agency agencyActor = null;
					  if(appWf.getSourceAgencyId()!=null)
					  {
					  	agencyActor = ((Agency)portletState.getApplicationManagementPortletUtil().getEntityObjectById(Agency.class, appWf.getSourceAgencyId()));
					  }
					  //Collection<String> endAgency = ((Collection<String>)portletState.getApplicationManagementPortletUtil().getAllAppWorkflowByApplicationAndStatus(app.getId(), ApplicationStatus.APPLICATION_STATUS_ENDORSED.getValue()));
					  //Collection<String> dendAgency = ((Collection<String>)portletState.getApplicationManagementPortletUtil().getAllAppWorkflowByApplicationAndStatus(app.getId(), ApplicationStatus.APPLICATION_STATUS_DISENDORSED.getValue()));
					  
					  if(h1!=null && h1.contains(appWf.getApplication().getId()))
					  {
						  status = null;
					  }
					  else
					  {
						  h1.add(appWf.getApplication().getId());
						  Collection<String> endAgency = ((Collection<String>)portletState.getApplicationManagementPortletUtil().getAgencyReceipientByApplicationAndStatusAndWorkedOn2(app.getId(), 
								  ApplicationStatus.APPLICATION_STATUS_ENDORSED.getValue(), false));
						  Collection<String> dendAgency = ((Collection<String>)portletState.getApplicationManagementPortletUtil().getAgencyReceipientByApplicationAndStatusAndWorkedOn2(app.getId(), 
								  ApplicationStatus.APPLICATION_STATUS_DISENDORSED.getValue(), false));
						  Collection<String> fwdAgency = ((Collection<String>)portletState.getApplicationManagementPortletUtil().getAgencyReceipientByApplicationAndStatusAndWorkedOn(app.getId(), 
								  ApplicationStatus.APPLICATION_STATUS_FORWARDED.getValue(),false, 
								  (portletState.getPortalUser().getAgency()!=null ? portletState.getPortalUser().getAgency().getId() : null), appWf));
					  
					  
						  String[] endorsementList = null;
						  String[] disendorsementList = null;
						  String[] fwdAgencyList = null;
						  if(endAgency!=null && endAgency.size()>0)
						  {
							  endorsementList = endAgency.toArray(new String[endAgency.size()]);
						  }
						  
						  if(dendAgency!=null && dendAgency.size()>0)
						  {
							  disendorsementList = dendAgency.toArray(new String[dendAgency.size()]);
						  }
						  
						  if(fwdAgency!=null && fwdAgency.size()>0)
						  {
							  fwdAgencyList = fwdAgency.toArray(new String[fwdAgency.size()]);
						  }
							 
						  if(portletState.getCurrentTab()==null)
						  {
							  	if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
						  		{
									status = "Pending";  
						  		}
							  	else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
						  		{
									status = "Pending";  
						  		}
							  		
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_ALL_APPLICATIONS_EU))
						  {
							  if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
								  status = "Approved Awaiting EUC Issuance";
							  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
								  status = "<span style='color:#ffffff; background-color:red'>Request Disapproved</span>";
							  else
								  status = "Pending";
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_APPROVED_APPLICATIONS_EU))
						  {
							  if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
								  status = "Approved Awaiting EUC Issuance";
							  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
								  status = "<span style='color:#ffffff; background-color:red'>Request Disapproved</span>";
							  else
								  status = "Pending";
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_APPROVED_AWAIT_ISSUANCE_APPLICATIONS_EU))
						  {
							  if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
								  status = "Approved Awaiting EUC Issuance";
							  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
								  status = "<span style='color:#ffffff; background-color:red'>Request Disapproved</span>";
							  else
								  status = "Pending";
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_PENDING_APPLICATIONS_EU))
						  {
							  if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
								  status = "Approved Awaiting EUC Issuance";
							  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
								  status = "<span style='color:#ffffff; background-color:red'>Request Disapproved</span>";
							  else
								  status = "Pending";
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_REJECTED_APPLICATIONS_EU))
						  {
							  if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
								  status = "Approved Awaiting EUC Issuance";
							  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
								  status = "<span style='color:#ffffff; background-color:red'>Request Disapproved</span>";
							  else
								  status = "Pending";
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_APPROVED_APPLICATIONS_NSA))
						  {
							  if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
									  status = (agencyActor!=null ? ("Approved by " + agencyActor.getAgencyName()) : roleSource);
							  if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED))
									  status = "Certificate Printed";
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA))
						  {
							  if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CREATED))
							  {
								  status = "Pending";
							  }else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_FORWARDED))
							  {
								  status = "Requiring Approval";
							  } 
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_REJECTED_APPLICATIONS_NSA))
						  {
							  status = (agencyActor!=null ? ("Rejected by " + agencyActor.getAgencyName()) : roleSource);
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_NSA))
						  {
							  //status = "Disendorsed by " + "(" + (appWf.getAgency()==null ? " " : appWf.getAgency().getAgencyName()) + ")";
							  status = "Disendorsed by " + "(" + dendAgency + ")";
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_FORWARDED_APPLICATIONS_NSA))
						  {
							  //RoleType rel = (RoleType)portletState.getApplicationManagementPortletUtil().getEntityObjectById(RoleType.class, appWf.getReceipientRole());
							  status = "Forwarded to " + "(" + (fwdAgencyList!=null && fwdAgencyList.length>0 ? Arrays.toString(fwdAgencyList) : " ") + ")";
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_DEVALIDATED_APPLICATIONS_AG))
						  {
							  status = "Attachments Devalidated by " + roleSource;
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_AG))
						  {
							  //status = "Disendorsed by " + (agencyActor!=null ? agencyActor.getAgencyName() : roleSource);
							  status = (disendorsementList!=null ? ("Disendorsed by " + Arrays.toString(disendorsementList)) : null);
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_AG))
						  {
							  status = (endorsementList!=null ? ("Endorsed by " + Arrays.toString(endorsementList)) : null);
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_FLAGGED_APPLICATIONS_AG))
						  {
							  if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
								  status = "Approved Awaiting EUC Issuance";
							  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
								  status = "Request Disapproved";
							  else
								  status = "Pending";
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_PENDING_APPLICATIONS_AG))
						  {
							  status = "Requires Endorsement or Disendorsement";
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_NSA))
						  {
							  status = endorsementList!=null ? ("Endorsed by " +  Arrays.toString(endorsementList)) : null;
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_NSA))
						  {
							  status = (disendorsementList!=null && disendorsementList.length>0) ? ("Disendorsed by " + Arrays.toString(disendorsementList)) : null;
						  }
				  	
					  
					  
						  if(status!=null)
						  {
							  %>
							  <tr>
							  	<%
							  	if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
						  		{
						  		%>
								
								<%
						  		}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
						  		{
						  			if(portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION) && 
						  					portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()>0 && portletState.getCurrentTab()!=null && 
						  					portletState.getCurrentTab().equals(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA))
						  		    {
						  		%>
										<td><input type="checkbox" name="selectAllCheckbox" value="<%=appWf.getId()%>"></td>
								<%
						  			}
						  		}
								%>
								<td><%=dateApplied%></td>
								<td><%=app.getApplicationNumber()==null ? "N/A" : app.getApplicationNumber()%></td>
								<td><%=app.getApplicant()!=null ? (app.getApplicant().getApplicantNumber()==null ? "N/A" : app.getApplicant().getApplicantNumber()) : "<span title='This is an application created by an agency' style='font-weight:bold'>" + app.getPortalUser().getAgency().getAgencyName() + "<span>"%></td>
								<td><%=appItem.getItemCategorySub()==null ? "N/A" : appItem.getItemCategorySub().getHsCode() + " - " + appItem.getItemCategorySub().getItemCategory().getItemCategoryName()%></td>
								<td><%=status%></td>
								<td>
								
								
								<%
								if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
						  		{
						  		%>
									<button type="button" class="btn btn-success" onclick="javascript:handleButtonAction('viewapplication', '<%=appWf.getId()%>')">View Application Details</button>
								<%
						  		}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
						  		{
						  			if(portletState.getPermissionList()!=null && !portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION))
						  			{
					  			%>
					  				<button type="button" class="btn btn-success" onclick="javascript:handleButtonAction('viewapplication', '<%=appWf.getId()%>')">View Application Details</button>
								<%
						  			}
						  			else
						  			{
								%>
										<div class="btn-group">
											<button type="button" class="btn btn-success">Click Here for Actions</button>
										  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
												<span class="caret"></span>
												<span class="sr-only">Toggle Dropdown</span>
											</button>
										  	<ul class="dropdown-menu" role="menu">
										  		<%
												if(portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION))
												{
													
												%>
										  		<li><a href="javascript: handleButtonAction('viewapplication', '<%=appWf.getId()%>')">View Application</a></li>
												<%
												}else
												{
													
													%>
											  		<!-- <li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=appWf.getId()%>')">Blacklist Applicant</a></li>-->
													<%	
												}
												if(portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION))
												{
												%>
												<!-- <li><a href="javascript: handleButtonAction('approveone', '<%=app.getId()%>')">Approve Application</a></li>-->
												<%
												}
												if(portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_REJECT_APPLICATION))
												{
												%>
												<!-- <li><a href="javascript: handleButtonAction('rejectone', '<%=appWf.getId()%>')">Reject Application</a></li>-->
												<%
												}
												%>
											</ul>
										</div>
								<%
						  			}
						  		}
								%>
											  
								</td>
								<%
								if(portletState.getPermissionList()!=null && !portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION))
					  			{
								%>
								<td>&nbsp;
									<%
									if(check)
									{
										%>
										<img src="/resources/images/flag.png" style="height:18px" title="Application has been flagged previously" >
										<%
									}
									%>
								</td>
								<%
					  			}
								%>
							  </tr>
							  
					  <%
						  }
					  }
				  }
			  }
				%>
			   </table>
			   <%
			   if(portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION) && 
	  					portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()>0 && portletState.getCurrentTab()!=null && 
	  					portletState.getCurrentTab().equals(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA))
	  		   {
			   %>
			   <div>
				   <!-- <button type="submit" name="btnSaveApplication" id="SaveApplication" class="btn btn-danger btn-cons" style="float:right" onClick="javascript:submitForm('batchprocessdisaprprove')">
	               Disapprove Selected Applications</button>-->
	               <button type="submit" name="btnSaveApplication" id="SaveApplication" class="btn btn-success btn-cons" style="float:right" onClick="javascript:submitForm('batchprocessapprove')">
	               Approve Selected Applications</button>
	           </div>
               <%
	  		   }
               %>
			  <%
		  }
		  
		  
		  %>
		  
		<input type="hidden" name="selectedApplications" id="selectedApplications" value="" />
		<input type="hidden" name="selectedUserAction" id="selectedUserAction" value="" />	
	</form>
</div>
</div>
<input type="button" onclick="printDiv('print-content')" value="Print Page"/>

<script type="text/javascript" charset="utf-8" src="<%=jqueryJsUrl%>"></script>
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




function submitForm(action){
	
	if(action=='batchprocessdisaprprove' || action=='batchprocessaprprove')
	{
		str = "";
		if(action=='batchprocessdisaprprove')
		{
			str = "Are you sure you want to disapprove the selected application(s)?";
		}else
		{
			str = "Are you sure you want to approve the selected application(s)?";
		}
		if(confirm(str))
		{
			document.getElementById('selectedUserAction').value = action;
			document.getElementById('userListFormId').submit();
		}
	}else
	{
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

