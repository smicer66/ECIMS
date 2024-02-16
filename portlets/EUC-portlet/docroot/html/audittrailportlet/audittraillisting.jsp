<%@page import="com.ecims.portlet.audittrail.AuditTrailPortletState"%>
<%@page import="com.ecims.portlet.audittrail.AuditTrailPortletState.*"%>
<%@page import="java.util.Collection"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@ page import="com.liferay.portal.kernel.util.Validator"%>
<%@ page import="javax.portlet.PortletPreferences"%>
<%@ page import="javax.portlet.PortletURL"%>
<%@ page import="javax.portlet.ActionRequest"%>
<%@ page import="javax.portlet.RenderResponse"%>
<%@ page import="java.lang.Exception"%>
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
<%@page import="smartpay.audittrail.AuditTrail"%>
<%@page import="smartpay.entity.Company"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.RoleType"%>
<%@page import="smartpay.entity.Applicant"%>
<%@page import="smartpay.entity.ApplicationItem"%>
<%@page import="smartpay.entity.Application"%>
<%@page import="smartpay.entity.ApplicationFlag"%>
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


AuditTrailPortletState portletState = AuditTrailPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(AuditTrailPortletState.class);
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
%>

<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>

<portlet:actionURL var="proceed" name="processAction">
	<portlet:param name="action"
		value="<%=AuditTrailPortletState.AUDITTRAIL_ACTIONS.GENERATE.name()%>" />
</portlet:actionURL>


<div style="padding:10px" id="print-content">
<div class="panel panel-info" style="padding:10px">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>Audit Trails</strong></div>
  	<div class="panel-body">&nbsp;
  	</div>
		<legend>Audit Trail Listing</legend>
		  <%
		  if(portletState.getAuditTrailListing()!=null && portletState.getAuditTrailListing().size()>0)
		  {
			  %>
			  <form  id="audittraillist" action="<%=proceed%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
			  
				<div class="btn-group" style="float:right">
					<button type="button" class="btn btn-success">Export Audit Trail To...</button>
				  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
						<span class="caret"></span>
						<span class="sr-only">Toggle Dropdown</span>
					</button>
				  	<ul class="dropdown-menu" role="menu">
				  		<li><a href="javascript: handleButtonAction('exportexcel', '', '')">Excel Document</a></li>
				  	</ul>
				</div>
				
				
                  <input type="hidden" name="actId" id="actId" value="">
                  <input type="hidden" name="act" id="act" value="">
                  
                  
				</form>
				
			  <table width="100%" class="table table-hover" id="btable">
				<thead>
				
					  <th>Activity Date</th>
					  <th>Action Carried Out</th>
					  <th>By Portal User</th>
					  <th>Action Description</th>
					  <th>User IP Address</th>
				
				</thead>
				<%
			  for(Iterator<AuditTrail> iter1 = portletState.getAuditTrailListing().iterator(); iter1.hasNext();)
			  {
				  AuditTrail ad = iter1.next();
				  String action = ad.getActivity();
				  PortalUser pu = portletState.getAuditTrailPortletUtil().getPortalUserByUserId(ad.getUserId());
				  String portalUserName  = "";
				  if(pu!=null)
					  portalUserName = pu.getFirstName() + " " + pu.getSurname();
				  String description = "";
				  if(ad.getActivity().equals(ECIMSConstants.APPLICANT_ACTIVATE))
				  {
					  description = "Activation of ECIMS Account By Applicant - " + portalUserName;
				  }else if(ad.getActivity().equals(ECIMSConstants.APPLICANT_SET_PASSWORD))
				  {
					  description = "Password Set By Portal User - " + portalUserName;
				  }else if(ad.getActivity().equals(ECIMSConstants.APPLICATION_FLAGGING))
				  {
					  try{
					  	ApplicationFlag apf = (ApplicationFlag)portletState.getAuditTrailPortletUtil().getEntityObjectById(ApplicationFlag.class, Long.valueOf(ad.getAction()));
					  
					  	description = "Application Flagging | Application Number: " + apf.getApplication().getApplicationNumber() + " | Applicant: " + portalUserName;
					  }catch(Exception e)
					  {
						  e.printStackTrace();
					  }
				  }else if(ad.getActivity().equals(ECIMSConstants.APPROVE_APPLICANT_SIGNUP))
				  {
					  description = "New Applicant SignUp Approval - " + portalUserName;
				  }else if(ad.getActivity().equals(ECIMSConstants.APPROVE_APPLICATION))
				  {
					  description = "New Application Request Approval - " + portalUserName;
				  }else if(ad.getActivity().equals(ECIMSConstants.ASSIGN_CERTIFICATE_NUMBER))
				  {
					  try{
						  	Certificate cert = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
						  
						  	description = "Assign Certificate Number | Certificate Number: " + cert.getApplicationNumber() + " | Carried out by " + portalUserName;
						  }catch(Exception e)
						  {
							  e.printStackTrace();
						  }
					  
				  }else if(ad.getActivity().equals(ECIMSConstants.AUTO_PROCESS_APPLICATION))
				  {
					  try{
						  	Certificate cert = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
						  
						  	description = "Auto Process Application | Certificate Number: " + cert.getApplicationNumber() + " | Carried out by " + portalUserName;
						  }catch(Exception e)
						  {
							  e.printStackTrace();
						  }
				  }else if(ad.getActivity().equals(ECIMSConstants.BLACKLIST_APPLICANT))
				  {
					  try{
					  PortalUser pu1 = portletState.getAuditTrailPortletUtil().getPortalUserByUserId(ad.getAction());
					  description = "BlackList Applicant - " + pu1.getFirstName() + " " + pu1.getSurname() + " | Carried out By " + portalUserName;
					  }catch(Exception e)
					  {
						  
					  }
				  }else if(ad.getActivity().equals(ECIMSConstants.CAPTURE_FINGER_PRINT))
				  {
					  try{
						  PortalUser pu1 = portletState.getAuditTrailPortletUtil().getPortalUserByUserId(ad.getAction());
						  description = "Capture Fingerprint | Carried out By " + pu1.getFirstName() + " " + pu1.getSurname() + "" + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }else if(ad.getActivity().equals(ECIMSConstants.DEVALIDATE_APPLICATION_ATTACHMENT))
				  {
					  try{
						  ApplicationAttachment aa = (ApplicationAttachment)portletState.getAuditTrailPortletUtil().getEntityObjectById(ApplicationAttachment.class, Long.valueOf(ad.getAction()));
						  description = "Application Attachment Devalidation. Application Number: " + aa.getApplication().getApplicationNumber() + " | Carried out By " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
					  
				  }else if(ad.getActivity().equals(ECIMSConstants.DISAPPROVE_APPLICATION))
				  {
					  try{
						  Application app = (Application)portletState.getAuditTrailPortletUtil().getEntityObjectById(Application.class, Long.valueOf(ad.getAction()));
						  description = "Application Disapproval. Application Number: " + app.getApplicationNumber() + " | Carried out by " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }else if(ad.getActivity().equals(ECIMSConstants.DISENDORSE_APPLICATION_REQUEST))
				  {
					  try{
						  ApplicationWorkflow app = (ApplicationWorkflow)portletState.getAuditTrailPortletUtil().getEntityObjectById(ApplicationWorkflow.class, Long.valueOf(ad.getAction()));
						  description = "Application Disendorsed. Application Number: " + app.getApplication().getApplicationNumber() + " | Carried out by " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }else if(ad.getActivity().equals(ECIMSConstants.ENDORSE_APPLICATION_REQUEST))
				  {
					  try{
						  ApplicationWorkflow app = (ApplicationWorkflow)portletState.getAuditTrailPortletUtil().getEntityObjectById(ApplicationWorkflow.class, Long.valueOf(ad.getAction()));
						  description = "Application Endorsement. Application Number: " + app.getApplication().getApplicationNumber() + " | Carried out by " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }else if(ad.getActivity().equals(ECIMSConstants.FORWARD_APPLICATION_REQUEST))
				  {
					  try{
						  ApplicationWorkflow app = (ApplicationWorkflow)portletState.getAuditTrailPortletUtil().getEntityObjectById(ApplicationWorkflow.class, Long.valueOf(ad.getAction()));
						  description = "Application Endorsement. Application Number: " + app.getApplication().getApplicationNumber() + " | Carried out by " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }else if(ad.getActivity().equals(ECIMSConstants.ISSUE_CERTIFICATE))
				  {
					  try{
						  Certificate cert = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
						  description = "Certificate Issuance Endorsement. Certificate Number: " + cert.getCertificateNo() + " | Carried out by " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }else if(ad.getActivity().equals(ECIMSConstants.NEW_APPLICATION_REQUEST))
				  {
					  try{
						  ApplicationWorkflow app = (ApplicationWorkflow)portletState.getAuditTrailPortletUtil().getEntityObjectById(ApplicationWorkflow.class, Long.valueOf(ad.getAction()));
						  description = "New Application Request. Application Number: " + app.getApplication().getApplicationNumber() + " | Carried out by " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }else if(ad.getActivity().equals(ECIMSConstants.RECALL_CERTIFICATE))
				  {
					  try{
						  Certificate cert = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
						  description = "Certificate Recall. Certificate Number: " + cert.getCertificateNo() + " | Carried out by " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }else if(ad.getActivity().equals(ECIMSConstants.USER_SIGNUP))
				  {
					  try{
						  PortalUser p1 = (PortalUser)portletState.getAuditTrailPortletUtil().getEntityObjectById(PortalUser.class, Long.valueOf(ad.getAction()));
						  description = "Portal User SignUp for Portal User: " + p1.getFirstName() + " " + p1.getSurname();
						  }catch(Exception e)
						  {
							  
						  }
				  }else if(ad.getActivity().equals(ECIMSConstants.VALIDATE_APPLICATION_ATTACHMENT))
				  {
					  try{
						  ApplicationAttachment aa = (ApplicationAttachment)portletState.getAuditTrailPortletUtil().getEntityObjectById(ApplicationAttachment.class, Long.valueOf(ad.getAction()));
						  description = "Application Attachment Validation. Application Number: " + aa.getApplication().getApplicationNumber() + " | Carried out By " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }else if(ad.getActivity().equals(ECIMSConstants.APPROVE_APPLICANT_SIGNUP))
				  {
					  try{
						  Applicant aa = (Applicant)portletState.getAuditTrailPortletUtil().getEntityObjectById(Applicant.class, Long.valueOf(ad.getAction()));
						  description = "Approve Applicant Sign Up. Applicant Number: " + aa.getApplicantNumber() + " | Carried out By " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }
				  else if(ad.getActivity().equals(ECIMSConstants.COLLECT_CERTIFICATE))
				  {
					  try{
						  Certificate aa = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
						  description = "Certificate Collection. Certificate Number: " + aa.getCertificateNo() + " | Carried out By " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }
				  else if(ad.getActivity().equals(ECIMSConstants.APPLICANT_UPDATE_PASSWORD))
				  {
					  try{
						  PortalUser aa = (PortalUser)portletState.getAuditTrailPortletUtil().getEntityObjectById(PortalUser.class, Long.valueOf(ad.getAction()));
						  description = "Password Update. Portal User: " + aa.getEmailAddress() + " | Carried out By " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }
				  else if(ad.getActivity().equals(ECIMSConstants.REJECT_CERTIFICATE))
				  {
					  try{
						  Certificate aa = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
						  description = "Certificate Printing Error. Certificate Number: " + aa.getCertificateNo() + " | Carried out By " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }
				  else if(ad.getActivity().equals(ECIMSConstants.DISPUTE_CERTIFICATE))
				  {
					  try{
						  Certificate aa = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
						  description = "Certificate Dispute. Certificate Number: " + aa.getCertificateNo() + " | Carried out By " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }
				  else if(ad.getActivity().equals(ECIMSConstants.UPLOAD_CERTIFICATE))
				  {
					  try{
						  Certificate aa = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
						  description = "Certificate Upload. Certificate Number: " + aa.getCertificateNo() + " | Carried out By " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }
				  else if(ad.getActivity().equals(ECIMSConstants.CANCEL_DISPUTE_CERTIFICATE))
				  {
					  try{
						  Certificate aa = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
						  description = "Certificate Dispute Cancelation. Certificate Number: " + aa.getCertificateNo() + " | Carried out By " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }
				  else if(ad.getActivity().equals(ECIMSConstants.GENERATE_REPORTS_XLS))
				  {
					  try{
						  //Certificate aa = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
						  description = "Generate Reports. Report File Name: " + ad.getAction() + " | Carried out By " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }
				  else if(ad.getActivity().equals(ECIMSConstants.BLACKLIST_AGENCY))
				  {
					  try{
						  String[] action3 = ad.getAction().split("|||");
						  //Certificate aa = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
						  description = "Blacklist of Agency " + action3[0] + " | Reason: " + action3[1] + " | Carried out By " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }
				  else if(ad.getActivity().equals(ECIMSConstants.BLACKLIST_APPLICANT))
				  {
					  try{
						  String[] action3 = ad.getAction().split("|||");
						  
						  //Certificate aa = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
						  description = "Blacklist of Applicant No " + action3[0] + " | Reason: " + action3[1] + " | Carried out By " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }
				  else if(ad.getActivity().equals(ECIMSConstants.UNBLACKLIST_AGENCY))
				  {
					  try{
						  String[] action3 = ad.getAction().split("|||");
						  //Certificate aa = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
						  description = "Un-blacklist of Agency " + action3[0] + " | Carried out By " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }
				  else if(ad.getActivity().equals(ECIMSConstants.UNBLACKLIST_APPLICANT))
				  {
					  try{
						  String[] action3 = ad.getAction().split("|||");
						  //Certificate aa = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
						  description = "Un-blacklist of Applicant No " + action3[0] + " | Carried out By " + portalUserName;
						  }catch(Exception e)
						  {
							  
						  }
				  }
				  
				  
				  
				  %>
				  <tr>
				  	
					  <td><%=sdf.format(new Date(ad.getDate().getTime())) %> Hrs</td>
					  <td><%=action %></td>
				  	  <td><%=portalUserName %></td>
				  	  <%
				  	  if(ad.getActivity().equals(ECIMSConstants.BLACKLIST_APPLICANT))
				  	  {
				  		%>
				  	  <td><%=ad.getAction() %></td>
				  	  <%  
				  	  }
				  	  else if(ad.getActivity().equals(ECIMSConstants.BLACKLIST_APPLICANT))
				  	  {
				  		%>
				  	  <td><%=ad.getAction() %></td>
				  	  <%  
				  	  }
				  	  else if(ad.getActivity().equals(ECIMSConstants.BLACKLIST_APPLICANT))
				  	  {
				  		%>
				  	  <td><%=ad.getAction() %></td>
				  	  <%  
				  	  }
				  	  else if(ad.getActivity().equals(ECIMSConstants.BLACKLIST_APPLICANT))
				  	  {
				  		%>
				  	  <td><%=ad.getAction() %></td>
				  	  <%  
				  	  }
				  	  else
				  	  {
				  	  %>
				  	  <td><%=description %></td>
				  	  <%
				  	  }
				  	  %>
					   
					  <td><%=ad.getIpAddress() %></td>
				  </tr>
				  <%
			  }
				%>
			   </table>
			  <%
		  }
		  %>
</div>
</div>
<input type="button" onclick="printDiv('print-content')" value="Print Page"/>


<script type="text/javascript" charset="utf-8" src="<%=jqueryDataTableUrl%>"></script>

<script type="text/javascript">
$(document).ready(function() {
    $('#btable').dataTable();
} );


function handleButtonAction(action, usId, aert){
	if(action=='exportpdf')
	{
		document.location.href='<%=response.encodeURL(request.getContextPath()+"/ActiveServlet?action=generateAudit&gyus=")%>'+aert +'&iosdp='+usId +'&act='+action;
	}
	if(action=='exportexcel')
	{
		document.getElementById('act').value = action;
		document.getElementById('actId').value = usId;
		document.getElementById('audittraillist').submit();
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

