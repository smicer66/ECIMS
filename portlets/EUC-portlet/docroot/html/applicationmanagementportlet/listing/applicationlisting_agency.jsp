<%@page import="java.util.Arrays"%>
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
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" /><%

ApplicationManagementPortletState portletState = ApplicationManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ApplicationManagementPortletState.class);

%>
<jsp:include page="/html/applicationmanagementportlet/tabs_agency.jsp" flush="" />


<portlet:actionURL var="listing" name="processAction">
	<portlet:param name="action"
		value="<%=APPLIST_ACTIONS.HANDLE_ACTIONS_ON_LIST_APPLICATIONS.name()%>" />
</portlet:actionURL>

<div style="padding:10px" id="print-content">
<div class="panel panel-info">
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
				  
				  <th>
				  <%
				  if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
				  {%>Date Created<%}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
				  {%>Date Received<%}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
				  {%>Date Received<%}
				  %>
				  </th>
				  <th>Application Number</th>
				  <th>Applicant Number</th>
				  <th>Item Category</th>
				  <th>Status</th>
				  <th>Action</th>
				  <th>&nbsp;</th>
				</thead>
					<%
					if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP) || 
							portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
			  		{
			  		%>
						<%
					  for(Iterator<ApplicationWorkflow> iter1 = portletState.getApplicationWorkFlowListing().iterator(); iter1.hasNext();)
					  {
						  
						  ApplicationWorkflow appWf= iter1.next();
						  Application app = appWf.getApplication();
						  PortalUser pu = app.getApplicant()!=null ? app.getApplicant().getPortalUser() : app.getPortalUser();
						  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MMM-dd HH:mm");
						  boolean check = false;
						  if(portletState.getApplicationinAppFlags()!=null && portletState.getApplicationinAppFlags().contains(app))
							  check = true;
						  
						  String dateApplied = "";
						  if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
						  {
							  dateApplied = sdf.format(new Date(app.getDateCreated().getTime())) + " Hrs";
						  }else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
						  {
							  dateApplied = sdf.format(new Date(appWf.getDateCreated().getTime())) + " Hrs";
						  }else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
						  {
							  dateApplied = sdf.format(new Date(appWf.getDateCreated().getTime())) + " Hrs";
						  }
						  
						  Collection<ApplicationItem> appItemList = portletState.getApplicationManagementPortletUtil().getApplicationItemsByApplication(app);
						  Iterator<ApplicationItem> iter = appItemList.iterator();
						  ApplicationItem appItem = iter.next();
						  String status = "";
						  String roleSource = ((RoleType)portletState.getApplicationManagementPortletUtil().getEntityObjectById(RoleType.class, appWf.getSourceId())).getName().getValue();
						  String roleRec = ((RoleType)portletState.getApplicationManagementPortletUtil().getEntityObjectById(RoleType.class, appWf.getReceipientRole())).getName().getValue();
						  //Agency agencyActor = null;
						  //agencyActor = ((Agency)portletState.getApplicationManagementPortletUtil().getEntityObjectById(Agency.class, appWf.getSourceAgencyId()));
						  Collection<String> endAgency = ((Collection<String>)portletState.getApplicationManagementPortletUtil().getAllAppWorkflowByApplicationAndStatus(app.getId(), ApplicationStatus.APPLICATION_STATUS_ENDORSED.getValue()));
						  Collection<String> dendAgency = ((Collection<String>)portletState.getApplicationManagementPortletUtil().getAllAppWorkflowByApplicationAndStatus(app.getId(), ApplicationStatus.APPLICATION_STATUS_DISENDORSED.getValue()));
						  String[] endorsementList = null;
						  String[] disendorsementList = null;
						  if(endAgency!=null && endAgency.size()>0)
						  {
							  endorsementList = endAgency.toArray(new String[endAgency.size()]);
						  }
						  
						  if(dendAgency!=null && dendAgency.size()>0)
						  {
							  disendorsementList = dendAgency.toArray(new String[dendAgency.size()]);
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
							  	else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
						  		{
									status = "Forwarded From ONSA Staff";  
						  		}
							  		
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_DEVALIDATED_APPLICATIONS_AG))
						  {
							  status = "Attachments Devalidated by " + roleSource;
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_AG))
						  {
							  status = "Disendorsed by " +  Arrays.toString(disendorsementList);
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_AG))
						  {
							  status = "Endorsed by " +  Arrays.toString(endorsementList);
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_FLAGGED_APPLICATIONS_AG))
						  {
							  status = "Flagged";
						  }else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_PENDING_APPLICATIONS_AG))
						  {
							  status = "Forwarded From ONSA Staff";
						  }
						  else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_DISPUTED_APPLICATIONS_AG))
						  {
							  status = "Disputed";
						  }
						  else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_MY_AGENCY_APPLICATIONS_AG))
						  {
							  if(appWf.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
							  {
								  status = "Approved by ONSA Staff";
							  }
							  if(appWf.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED))
							  {
								  status = "Certificate Issued";
							  }
							  if(appWf.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CREATED))
							  {
								  status = "Pending ONSA Action";
							  }
							  if(appWf.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISENDORSED))
							  {
								  status = "Disendorsed by " + Arrays.toString(disendorsementList);
							  }
							  if(appWf.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISPUTED))
							  {
								  status = "Disputed";
							  }
							  if(appWf.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ENDORSED))
							  {
								  status = "Endorsed by " + Arrays.toString(endorsementList);
							  }
							  if(appWf.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_FORWARDED))
							  {
								  status = "Forwarded From ONSA Staff";
							  }
							  if(appWf.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
							  {
								  status = "Rejected By ONSA Staff";
							  }
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
							<td><%=app.getApplicant()!=null ? 
									(app.getApplicant().getApplicantNumber()==null ? 
											"N/A" : 
												app.getApplicant().getApplicantNumber()) : 
													app.getPortalUser().getAgency().getAgencyName()%></td>
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
				  			%>
				  				<button type="button" class="btn btn-success" onclick="javascript:handleButtonAction('viewapplication', '<%=appWf.getId()%>')">View Application Details</button>
							<%
					  		}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
					  		{
				  			%>
				  				<button type="button" class="btn btn-success" onclick="javascript:handleButtonAction('viewapplication', '<%=appWf.getId()%>')">View Application Details</button>
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
					<%
			  		}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
			  		{
			  		%>
					<th>Select All<br><input type="checkbox" name="clickSelectAll" id="clickSelectAll" onclick="javascript:handleSelectAll();" /></th>
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
<input type="button" onclick="printDiv('print-content')" value="Print Page"/>


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

