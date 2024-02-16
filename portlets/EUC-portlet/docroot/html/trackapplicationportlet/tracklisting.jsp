<%@page import="com.ecims.portlet.trackapplication.TrackApplicationPortletState"%>
<%@page import="com.ecims.portlet.trackapplication.TrackApplicationPortletState.*"%>
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


TrackApplicationPortletState portletState = TrackApplicationPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(TrackApplicationPortletState.class);
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
%>

<portlet:actionURL var="proceedToStepThree" name="processAction">
	<portlet:param name="action"
		value="<%=ACTIONS.SEARCH_FOR_APPLICATION.name()%>" />
</portlet:actionURL>

<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>
		
		
<div style="padding:10px" id="print-content">
<div class="panel panel-info">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>Track An EUC Application</strong></div>
  	<div class="panel-body">&nbsp;
	<form  id="startRegFormId" action="<%=proceedToStepThree%>" data-validate="parsley" method="post" enctype="application/x-www-form-urlencoded">
	     <fieldset>
			  <legend>Application Number
			  </legend>
			<div>
			<input type="text" placeholder="Type an application number to track it" style="width:700px" name="applicationNumber" id="applicationNumber" value="<%=portletState.getApplicationNumber()==null ? "" : portletState.getApplicationNumber()%>" data-required="true">
			<button type="submit" class="btn btn-success" onclick="javascript:submitForm('searchapplication')">Search</button>
			</div>
		</fieldset>
		
		<legend>EUC Application Workflow Listing</legend>
		<div>
			
		</div>
		  <%
		  if(portletState.getAppWorkflowListing()!=null && portletState.getAppWorkflowListing().size()>0)
		  {
			  %>
			  
			  <table width="100%" class="table table-hover" id="btable">
				<thead>
				
					  <th>Activity Date</th>
					  <th>Application Number</th>
					  <th>Action Source</th>
					  <th>Action Receipient</th>
					  <th>Handled</th>
					  <th>Current Status</th>
				
				</thead>
				<%
			  for(Iterator<ApplicationWorkflow> iter1 = portletState.getAppWorkflowListing().iterator(); iter1.hasNext();)
			  {
				  ApplicationWorkflow ad = iter1.next();
				  String src = "";
				  String rec = "";
				  String status = "";
				  
					  
				  if(portletState.getPortalUser().getRoleType().getId().equals(ad.getSourceId()))
					  src = portletState.getPortalUser().getRoleType().getName().getValue();
				  else
					  src = ((RoleType)portletState.getTrackApplicationPortletUtil().getEntityObjectById(RoleType.class, ad.getSourceId())).getName().getValue();
				  
				  if(portletState.getPortalUser().getRoleType().getId().equals(ad.getReceipientRole()))
					  rec = portletState.getPortalUser().getRoleType().getName().getValue() + " - " + ad.getAgency().getAgencyName();
				  else
					  rec = ((RoleType)portletState.getTrackApplicationPortletUtil().getEntityObjectById(RoleType.class, ad.getReceipientRole())).getName().getValue() + " - " + ad.getAgency().getAgencyName();
					  
				  
				  
				  if(ad.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
					  status = "Approved by " + src;
				  else if(ad.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ATTACHMENT_DEVALIDATED))
					  status = "Attachments Devalidated by " + src;
				  else if(ad.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED))
					  status = "Certificate Issued by " + src;
				  else if(ad.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CREATED))
					  status = "Request created by " + src;
				  else if(ad.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISENDORSED))
					  status = "Request disendorsed by " + src;
				  else if(ad.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISPUTED))
					  status = "Request disputed by " + src;
				  else if(ad.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ENDORSED))
					  status = "Request endorsed by " + src;
				  else if(ad.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_FLAGGED))
					  status = "Request flagged by " + src;
				  else if(ad.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_FORWARDED))
					  status = "Request forwarded by " + src + " to " + rec;
				  else if(ad.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
					  status = "Request disapproved by " + src;
				  
				  
				  %>
				  <tr>
				  	
					  <td><%=sdf.format(ad.getDateCreated()) %> Hrs</td>
					  <td><%=ad.getApplication()!=null ? ad.getApplication().getApplicationNumber() : "" %></td>
				  	  <td><%=src %></td>
					  <td><%=rec %></td>
					  <td><%=(ad.getWorkedOn()!=null && ad.getWorkedOn().equals(Boolean.TRUE)) ? "Yes" : "No"%></td>
					  <td><%=status %></td>
				  </tr>
				  <%
			  }
				%>
			   </table>
			  <%
		  }
		  %>
		</div>
	</form>
		
	</div>
		
</div>
<input type="button" onclick="printDiv('print-content')" value="Print Page"/>

<script type="text/javascript" charset="utf-8" src="<%=jqueryDataTableUrl%>"></script>

<script type="text/javascript">
$(document).ready(function() {
    $('#btable').dataTable();
} );


function handleButtonAction(action, usId, aert){
	
	
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


function submitForm(clicked)
{
	document.getElementById('act').value=clicked;
	document.getElementById('startRegFormId').submit;
	
}
</script>

