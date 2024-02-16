<%@page import="smartpay.entity.Dispute"%>
<%@page import="com.ecims.portlet.certificatemanagement.CertificatePortletState"%>
<%@page import="com.ecims.portlet.certificatemanagement.CertificatePortletState.*"%>
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
<%@page import="smartpay.entity.Certificate"%>
<%@page import="smartpay.entity.ApplicationWorkflow"%>
<%@page import="smartpay.entity.enumerations.UserStatus"%>
<%@page import="smartpay.entity.enumerations.RoleConstants"%>
<%@page import="smartpay.entity.enumerations.ApplicantStatus"%>
<%@page import="smartpay.entity.enumerations.ApplicationStatus"%>
<%@page import="smartpay.entity.enumerations.CertificateStatus"%>
<%@page import="smartpay.entity.enumerations.PermissionType"%>
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

CertificatePortletState portletState = CertificatePortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(CertificatePortletState.class);
SimpleDateFormat sdf = new SimpleDateFormat();

int j = 0;
%>

<%

if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
{
%>
<jsp:include page="/html/certificateportlet/tabs_eu.jsp" flush="" />
<%
}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
{
%>
<jsp:include page="/html/certificateportlet/tabs_nsa.jsp" flush="" />
<%
}else if (portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
{
	%>
	<jsp:include page="/html/certificateportlet/tabs_agency.jsp" flush="" />
	<%
}
	%>
	<%

%>

<portlet:actionURL var="listing" name="processAction">
	<portlet:param name="action"
		value="<%=CERTLIST_ACTIONS.HANDLE_ACTIONS_ON_LIST_CERTIFICATES.name()%>" />
</portlet:actionURL>

<div style="padding:10px" id="print-content">
<div class="panel panel-info">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>List of Certificates</strong></div>
  	<div class="panel-body">&nbsp;
  	</div>
		<form  id="userListFormId" action="<%=listing%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
			
		<legend>Certificate Listing</legend>
		  <%
		  
		  if(portletState.getCertificateListing()!=null && portletState.getCertificateListing().size()>0)
		  {
			  %>
			  
			  <table width="100%" class="table table-hover" id="btable">
				<thead>
				<%
				if(portletState.getCurrentTab()==null)
				{
				%>
					<th>Expiry Date</th>
					  <th>Issuance Date</th>
					  <th>Certificate Number</th>
					  <th>Application Number</th>
					  <th>Certificate Status</th>
					  <th>Action</th>
				<%
				}
				else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_RECALLED_CERTIFICATES))
				{
				%>
					<th>Expiry Date</th>
					  <th>Issuance Date</th>
					  <th>Certificate Number</th>
					  <th>Application Number</th>
					  <th>Certificate Status</th>
					  <th>Action</th>
				<%
				}
				else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_DISPUTED_CERTIFICATES))
				{
				%><th>Expiry Date</th>
				  <th>Issuance Date</th>
				  <th>Certificate Number</th>
				  <th>Date of Dispute</th>
				  <th>Owner of Dispute</th>
				<%
				}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_UTILIZED_CERTIFICATES))
				{
				%><th>Expiry Date</th>
				  <th>Issuance Date</th>
				  <th>Certificate Number</th>
				  <th>Certificate Status</th>
				  <th>Date of Last Use</th>
				  <th>Utilization Details</th>
				  <th>Action</th>
				<%
				}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_EXPIRED_CERTIFICATES))
				{
				%>  
				  <th>Expiry Date</th>
				  <th>Issuance Date</th>
				  <th>Certificate Number</th>
				  <th>Utilization Status</th>
				  <th>Date of Issuance</th>
				  <th>Utilization Details</th>
				  <th>Action</th>
				<%
				}else if(portletState.getCurrentTab()!=null && (portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ALL_CERTIFICATES_NSA) || 
						portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ALL_CERTIFICATES_EU) || 
						portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ALL_CERTIFICATES_INFO)))
				{
				%>
				  <th>Expiry Date</th>
				  <th>Issuance Date</th>
				  <th>Certificate Number</th>
				  <th>Application Number</th>
				  <th>Certificate Status</th>
				  <th>Action</th>
				<%
				}else
				{
				%>
					  <th>Expiry Date</th>
					  <th>Issuance Date</th>
					  <th>Certificate Number</th>
					  <th>Application Number</th>
					  <th>Certificate Status</th>
					  <th>Action</th>
				<%
				}
				%>
				</thead>
				<%
			  for(Iterator<Certificate> iter1 = portletState.getCertificateListing().iterator(); iter1.hasNext();)
			  {
				 	
				  Certificate appWf= iter1.next();
				  
				  String status = "";
				  Date expireDate = appWf.getExpireDate();
				  Date issueDate = appWf.getIssuanceDate();
				  String certNo = appWf.getCertificateNo();
				  String appno = appWf.getApplicationNumber();
				  PortalUser pu = portletState.getPortalUser();
				  
				  Calendar cal = Calendar.getInstance();
					 
					 
				  if(appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_APPROVED))
				  {
					  status = "Approved Awaiting Collection";
					  if(cal.after(expireDate))
						  status = "Certificate Expired";
				  }
				  else if(appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_DISPUTED))
				  {
					  status = "Disputed";
					  if(cal.after(expireDate))
						  status = "Certificate Expired";
				  }
				  else if(appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_ISSUED))
				  {
					  status = "Certificate Printed";
					  if(cal.after(expireDate))
						  status = "Certificate Expired";
				  }
				  else if(appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_RECALLED))
				  {
					  status = "Certificate Recalled";
					  if(cal.after(expireDate))
						  status = "Certificate Expired";
				  }
				  else if(appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_REJECTED))
				  {
					  status = "Certificate Rejected";
					  if(cal.after(expireDate))
						  status = "Certificate Expired";
				  }
				  else if(appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_COLLECTED))
				  {
					  if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
					  {
					  	status = "Certificate Collected";
					  }else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
					  {
						status = "Certificate Collected";
					  }else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP) && 
							  portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))
					  {
					  	status = "Certificate Valid";
					  } 
					  

					  if(cal.after(expireDate))
						  status = "Certificate Expired";
				  }
				  
				  %>
				  <tr>
				  	<%
				if(portletState.getCurrentTab()==null)
				{
				%>
					  <td><%=expireDate %></td>
					  <td><%=issueDate %></td>
				  	  <td><%=certNo %></td>
					  <td><%=appno %></td>
					  <td><%=status %></td>
					  <td>
					  		<div class="btn-group">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
							  		<%
							  		if(pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER) && 
							  				!appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_REJECTED) && 
							  						!appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_RECALLED))
								  	{
							  		%>
							  			<li><a href="javascript: handleButtonAction('recallcertificate', '<%=appWf.getId()%>', '')">Recall Certificate</a></li>
							  		<%
								  	}
							  		%>
							  		<%
							  		if(pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER)
							  			|| (pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP)
							  			&& pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER)) || 
							  			pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
							  		{
							  				if(!pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_END_USER))
							  				{
											%>
											<li><a href="javascript: handleButtonAction('downloadcertificate', '<%=appWf.getApplication().getApplicant()!=null ? appWf.getApplication().getApplicant().getPortalUser().getId() : appWf.getApplication().getPortalUser().getId() %>', '<%=appWf.getId()%>')">Download PDF of Certificate</a></li>
									<%
							  				}
							  			
										if(appWf.getCertificatePrinted()!=null && appWf.getCertificatePrinted().equals(Boolean.TRUE) && (appWf.getSignature()!=null && appWf.getSignature().length()>0))
										{
											%>
											<li><a target="blank" href="/resources/images/uploadedfiles/<%=(appWf.getSignature()!=null && appWf.getSignature().length()>0) ? appWf.getSignature() : "" %>">Download Uploaded Certificate</a></li>
											<%
										}
							  		}
							  		if(pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP) 
							  			|| pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER)
							  			|| (pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP)
							  			&& pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER)))
							  		{
							  		%>
									<!-- <li><a href="javascript: handleButtonAction('viewcertificate', '<%=appWf.getId()%>', '')">View Certificate Details</a></li>-->
									<%
							  		}
							  		if((!appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_DISPUTED) &&   pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP)
								  			&& pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER)))
							  		{
									%>
									<li><a href="javascript: handleButtonAction('disputecertficate', '<%=appWf.getId()%>', '')">Dispute Certificate</a></li>
									<%
							  		}
							  		if(appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_DISPUTED) && pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER))
							  		{
							  			if(portletState!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_ISSUE_CERTIFICATE))
							  			{
							  				if(pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER))
							  				{
									%>
									
									<%
							  				}
									%>
									<li><a href="javascript: handleButtonAction('canceldisputecertificate', '<%=appWf.getId()%>', '')">Cancel Dispute On Certificate</a></li>
									<% 
							  			}
							  		}
							  		if(appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_ISSUED) && pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER))
							  		{
							  			if(portletState!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_ISSUE_CERTIFICATE))
							  			{
									%>
									<li><a href="javascript: handleButtonAction('collectcertificate', '<%=appWf.getId()%>', '')">Collect Certificate</a></li>
										<%
											if(pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER))
							  				{
											%>
											<!-- <li><a href="javascript: handleButtonAction('recallcertificate', '<%=appWf.getId()%>', '')">Recall Certificate</a></li>-->
											<%
							  				}
										%>
									
									<% 
							  			}
							  		}
							  		if(appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_ISSUED) && (appWf.getCertificatePrinted()==null || (appWf.getCertificatePrinted()!=null && appWf.getCertificatePrinted().equals(Boolean.FALSE))) && pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER))
							  		{
							  			if(portletState!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_ISSUE_CERTIFICATE))
							  			{
									%>
									<li><a href="javascript: handleButtonAction('uploadcertificate', '<%=appWf.getId()%>', '')">Upload Certificate</a></li>
										<%
											if(pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER))
							  				{
											%>
											<!-- <li><a href="javascript: handleButtonAction('recallcertificate', '<%=appWf.getId()%>', '')">Recall Certificate</a></li>-->
											<%
							  				}
										%>
									
									<% 
							  			}
							  		}
									%>
									
									
									
								</ul>
							</div>
					  </td>
				<%
				}
				int newDisputeCount=0;
				int oldDisputeCount=0;
				if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_DISPUTED_CERTIFICATES))
				{
					j++;
					String oldDisputes = "";
					String newDisputes = "";
					if(portletState.getOldDisputes()!=null && portletState.getOldDisputes().size()>0 && portletState.getOldDisputes().containsKey(appWf.getCertificateNo()))
					{
						Collection<Dispute> oldDList = portletState.getOldDisputes().get(appWf.getCertificateNo());
						if(oldDList!=null && oldDList.size()>0)
						{
							for(Iterator<Dispute> dIt=oldDList.iterator(); dIt.hasNext();)
							{
								Dispute disp = dIt.next();
								oldDisputes = oldDisputes + "<div style='padding-top:5px; clear:both'>Dispute date: " +sdf.format(disp.getDateCreated()) + "</div>" + 
										"<div>Dispute details: " +(disp.getDetails()) + "</div>" +
								"<div>Dispute By: " +(disp.getPortalUser().getFirstName() + " " + disp.getPortalUser().getSurname()) + "</div>";
								oldDisputeCount++;
							}
						}
					}
					
					if(portletState.getNewDisputes()!=null && portletState.getNewDisputes().size()>0 && portletState.getNewDisputes().containsKey(appWf.getCertificateNo()))
					{
						Collection<Dispute> newDList = portletState.getNewDisputes().get(appWf.getCertificateNo());
						if(newDList!=null && newDList.size()>0)
						{
							for(Iterator<Dispute> dIt=newDList.iterator(); dIt.hasNext();)
							{
								Dispute disp = dIt.next();
								newDisputes = newDisputes + "<div style='padding-top:5px; clear:both'>Dispute date: " +sdf.format(disp.getDateCreated()) + "</div>" + 
										"<div>Dispute details: " +(disp.getDetails()) + "</div>" +
										"<div>Dispute By: " +(disp.getPortalUser().getFirstName() + " " + disp.getPortalUser().getSurname()) + "</div>";
								if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER))
								{		
									newDisputes = newDisputes + "<div><button type=\"submit\" onclick=\"javascript:handleButtonAction('canceldisputecertificate', " + disp.getCertificate().getId() + ", '')\" name=\"btnSaveApplication\" id=\"SaveApplication\" class=\"btn btn-success btn-cons\" style=\"float:right\">Close Dispute</button>" +
										"<div style='padding:3px;'>&nbsp;</div><button type=\"submit\" onclick=\"javascript:handleButtonAction('acceptdisputecertficate', " + disp.getCertificate().getId() + ", '')\" name=\"btnSaveApplication\" id=\"SaveApplication\" class=\"btn btn-danger btn-cons\" style=\"float:right\">Recall Certificate</button></div>";
									newDisputeCount++;
										
								}
							}
						}
					}
					
					if(oldDisputes.trim().length()>0)
					{
						oldDisputes = "<div style='position: relative; display:none' id='prev" + j + "'>"+oldDisputes+"</div>";
					}else
					{
						oldDisputes = "<div style='position: relative; display:none' id='prev" + j + "'>No Old Disputes</div>";
					}
					
					if(newDisputes.trim().length()>0)
					{
						newDisputes = "<div style='position: relative; display:none' id='new" + j + "'>"+newDisputes+"</div>";
					}else
					{
						newDisputes = "<div style='position: relative; display:none' id='new" + j + "'>No New Disputes</div>";
					}
				%>
				  <td><%=expireDate %></td>
				  <td><%=issueDate %></td>
				  <td><%=certNo %></td>
				  <td><a href="javascript:showDisputes('prev<%=j %>', 'new<%=j %>')">Previous Disputes(<%=oldDisputeCount %>)</a> | 
				  <a href="javascript:showDisputes('new<%=j %>', 'prev<%=j++ %>')">New Disputes(<%=newDisputeCount %>)</a>
				  	<%=oldDisputes %>
				  	<%=newDisputes %>  
				  </td>
				  <td>
				  			<div class="btn-group">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
							  		<%
							  		if(pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER) && 
							  				!appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_REJECTED) && 
							  						!appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_RECALLED))
								  	{
							  		%>
							  			<li><a href="javascript: handleButtonAction('recallcertificate', '<%=appWf.getId()%>', '')">Recall Certificate</a></li>
							  		<%
								  	}
							  		%>
							  		
							  		<%
							  		if(appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_ISSUED) && 
							  			(pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP) 
							  			|| pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER)
							  			|| (pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP)
							  			&& pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))))
							  		{
							  			if(!pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
							  			{
							  				if(pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))
							  				{
							  				%>
											<li><a target="blank" href="/resources/images/uploadedfiles/<%=(appWf.getSignature()!=null && appWf.getSignature().length()>0) ? appWf.getSignature() : "" %>">Download Uploaded Certificate</a></li>
											<%
							  				}
							  				else
							  				{
							  		%>
									<li><a href="javascript: handleButtonAction('downloadcertificate', '<%=appWf.getApplication().getApplicant()!=null ? 
											appWf.getApplication().getApplicant().getPortalUser().getId() : appWf.getApplication().getPortalUser().getId()%>', '<%=appWf.getId()%>')">Download PDF of Certificate</a></li>
											
									<%
							  				}
							  			}
										if(appWf.getCertificatePrinted()!=null && appWf.getCertificatePrinted().equals(Boolean.TRUE) && (appWf.getSignature()!=null && appWf.getSignature().length()>0))
										{
											%>
											<li><a target="blank" href="/resources/images/uploadedfiles/<%=(appWf.getSignature()!=null && appWf.getSignature().length()>0) ? appWf.getSignature() : "" %>">Download Uploaded Certificate</a></li>
											<%
										}
							  		}
							  		if(pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP) 
							  			|| pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER)
							  			|| (pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP)
							  			&& pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER)))
							  		{
							  		%>
									<!-- <li><a href="javascript: handleButtonAction('viewcertificate', '<%=appWf.getId()%>', '')">View Certificate</a></li>-->
									<li><a target="blank" href="/resources/images/uploadedfiles/<%=(appWf.getSignature()!=null && appWf.getSignature().length()>0) ? appWf.getSignature() : "" %>">Download Uploaded Certificate</a></li>
									<%
							  		}
							  		if(appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_DISPUTED) && pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER))
							  		{
							  			if(portletState!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_ISSUE_CERTIFICATE))
							  			{
									%>
									<!-- <li><a href="javascript: handleButtonAction('recallcertificate', '<%=appWf.getId()%>', '')">Recall Certificate</a></li>-->
									<li><a href="javascript: handleButtonAction('canceldisputecertificate', '<%=appWf.getId()%>', '')">Cancel Dispute On Certificate</a></li>
									<% 
							  			}
							  		}
									%>
									
									
									
								</ul>
							</div>
				  </td>
				<%
				}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_UTILIZED_CERTIFICATES))
				{
				%>
				  <td><%=expireDate %></td>
				  <td><%=issueDate %></td>
				  <td><%=certNo %></td>
				  <td><%=status %></td>
				  <td>Date of Last Use</td>
				  <td>Utilization Details Appears here</td>
				  <td>
				  <div class="btn-group">
					<button type="button" class="btn btn-success">Click Here for Actions</button>
				  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
						<span class="caret"></span>
						<span class="sr-only">Toggle Dropdown</span>
					</button>
				</div>
				  </td>
				<%
				}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_EXPIRED_CERTIFICATES))
				{
				%>  
				  <td><%=expireDate %></td>
				  <td><%=issueDate %></td>
				  <td><%=certNo %></td>
				  <td>Utilization Status</td>
				  <td><%=issueDate %></td>
				  <td>Utilization Details</td>
				  <td>
				  			&nbsp;
				  </td>
				<%
				}
				else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_RECALLED_CERTIFICATES))
				{
				%>
					  <td><%=expireDate %></td>
					  <td><%=issueDate %></td>
					  <td><%=certNo %></td>
					  <td><%=appno %></td>
					  <td><%=status %></td>
					  <td>
				  			<div class="btn-group">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
							  		
									<%
							  		if((pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP) 
							  			|| pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER)
							  			|| (pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP)
							  			&& pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))))
							  		{
							  			if(!pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
							  			{
							  				if(pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))
							  				{
							  				%>
											<li><a target="blank" href="/resources/images/uploadedfiles/<%=(appWf.getSignature()!=null && appWf.getSignature().length()>0) ? appWf.getSignature() : "" %>">Download Uploaded Certificate</a></li>
											<%
							  				}
							  				else
							  				{
							  		%>
									<li><a href="javascript: handleButtonAction('downloadcertificate', '<%=appWf.getApplication().getApplicant()!=null ? 
											appWf.getApplication().getApplicant().getPortalUser().getId() : appWf.getApplication().getPortalUser().getId()%>', '<%=appWf.getId()%>')">Download PDF of Certificate</a></li>
									<%
							  				}
							  			}
										if(!pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER) && 
												appWf.getCertificatePrinted()!=null && appWf.getCertificatePrinted().equals(Boolean.TRUE) && (appWf.getSignature()!=null && appWf.getSignature().length()>0))
										{
											%>
											<li><a target="blank" href="/resources/images/uploadedfiles/<%=(appWf.getSignature()!=null && appWf.getSignature().length()>0) ? appWf.getSignature() : "" %>">Download Uploaded Certificate</a></li>
											<%
										}
							  		}
							  		if(pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP) 
							  			|| pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER)
							  			|| (pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP)
							  			&& pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER)))
							  		{
							  			if(pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))
							  			{
							  		%>
							  			<li><a target="blank" href="/resources/images/uploadedfiles/<%=(appWf.getSignature()!=null && appWf.getSignature().length()>0) ? appWf.getSignature() : "" %>">Download Uploaded Certificate</a></li>
							  		<!-- <li><a href="javascript: handleButtonAction('viewcertificate', '<%=appWf.getId()%>', '')">View Certificate</a></li>-->
									<%
							  			}
							  		}
							  		%>
								</ul>
							</div>
				  	</td>
				<%
				}
				else if(portletState.getCurrentTab()!=null && (portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ALL_CERTIFICATES_NSA) || 
						portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ALL_CERTIFICATES_EU) || 
						portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ALL_CERTIFICATES_INFO) || 
						portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ALL_CERTIFICATES_AGENCY)))
				{
				%>
				  <td><%=expireDate %></td>
				  <td><%=issueDate %></td>
				  <td><%=certNo %></td>
				  <td><%=appno %></td>
				  <td><%=status %></td>
				  <td>
				  			<div class="btn-group">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
							  		<%
							  		if(pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER) && 
							  				!appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_REJECTED) && 
							  						!appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_RECALLED))
								  	{
							  		%>
							  			<li><a href="javascript: handleButtonAction('recallcertificate', '<%=appWf.getId()%>', '')">Recall Certificate</a></li>
							  		<%
								  	}
							  		%>
									<%
							  		if(appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_ISSUED) && (
							  				pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP) 
							  			|| pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER)
							  			|| (pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP)
							  			&& pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))))
							  		{
							  			if(!pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
							  			{
							  				if(pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))
							  				{
							  				%>
											<li><a target="blank" href="/resources/images/uploadedfiles/<%=(appWf.getSignature()!=null && appWf.getSignature().length()>0) ? appWf.getSignature() : "" %>">Download Uploaded Certificate</a></li>
											<%
							  				}
							  				else
							  				{
							  		%>
									<li><a href="javascript: handleButtonAction('downloadcertificate', '<%=appWf.getApplication().getApplicant()!=null ? 
											appWf.getApplication().getApplicant().getPortalUser().getId() : appWf.getApplication().getPortalUser().getId()%>', '<%=appWf.getId()%>')">Download PDF of Certificate</a></li>
									<%
							  				}
							  			}
										if(!pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER) && 
												appWf.getCertificatePrinted()!=null && appWf.getCertificatePrinted().equals(Boolean.TRUE) && (appWf.getSignature()!=null && appWf.getSignature().length()>0))
										{
											%>
											<li><a target="blank" href="/resources/images/uploadedfiles/<%=(appWf.getSignature()!=null && appWf.getSignature().length()>0) ? appWf.getSignature() : "" %>">Download Uploaded Certificate</a></li>
											<%
										}
							  		}
							  		if(pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP) 
							  			|| pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER)
							  			|| (pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP)
							  			&& pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER)))
							  		{
							  			if(pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))
							  			{
							  		%>
							  			<li><a target="blank" href="/resources/images/uploadedfiles/<%=(appWf.getSignature()!=null && appWf.getSignature().length()>0) ? appWf.getSignature() : "" %>">Download Uploaded Certificate</a></li>
							  		<!-- <li><a href="javascript: handleButtonAction('viewcertificate', '<%=appWf.getId()%>', '')">View Certificate</a></li>-->
									<%
							  			}
							  		}
							  		if((!appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_DISPUTED) &&   pu.getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP)
								  			&& pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER)))
							  		{
									%>
									<li><a href="javascript: handleButtonAction('disputecertficate', '<%=appWf.getId()%>', '')">Dispute Certificate</a></li>
									<%
							  		}
							  		if(appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_DISPUTED) && pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER))
							  		{
							  			if(portletState!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_ISSUE_CERTIFICATE))
							  			{
									%>
										<%
											if(pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER))
							  				{
											%>
											<!-- <li><a href="javascript: handleButtonAction('recallcertificate', '<%=appWf.getId()%>', '')">Recall Certificate</a></li>-->
											<%
							  				}
										%>
									<li><a href="javascript: handleButtonAction('canceldisputecertificate', '<%=appWf.getId()%>', '')">Cancel Dispute On Certificate</a></li>
									<% 
							  			}
							  		}
							  		if(appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_ISSUED) && pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER))
							  		{
							  			if(portletState!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_ISSUE_CERTIFICATE))
							  			{
									%>
									<li><a href="javascript: handleButtonAction('collectcertificate', '<%=appWf.getId()%>', '')">Collect Certificate</a></li>
									<% 
							  			}
							  		}if(appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_ISSUED) && (appWf.getCertificatePrinted()==null || (appWf.getCertificatePrinted()!=null && appWf.getCertificatePrinted().equals(Boolean.FALSE))) && pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER))
							  		{
							  			if(portletState!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_ISSUE_CERTIFICATE))
							  			{
									%>
									<li><a href="javascript: handleButtonAction('uploadcertificate', '<%=appWf.getId()%>', '')">Upload Certificate</a></li>
									<% 
							  			}
							  		}
							  		
							  		if(appWf.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_COLLECTED)
							  				&& pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER))
							  		{
							  			if(portletState!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_ISSUE_CERTIFICATE))
							  			{
											%>
											<li><a target="blank" href="/resources/images/uploadedfiles/<%=(appWf.getSignature()!=null && appWf.getSignature().length()>0) ? appWf.getSignature() : "" %>">Download Uploaded Certificate</a></li>
											<% 
							  			}
							  		}
									%>
								</ul>
							</div>
				  </td>
				<%
				}
				%>
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
<input type="button" onclick="printDiv('print-content')" value="Print Page"/>

<script type="text/javascript" charset="utf-8" src="<%=jqueryDataTableUrl%>"></script>

<script type="text/javascript">
$(document).ready(function() {
    $('#btable').dataTable();
} );


function showDisputes(id1, id2)
{
	document.getElementById(id1).style.display = 'block';
	document.getElementById(id2).style.display = 'none';
}

function handleButtonAction(action, usId, aert){
	
	if(action=='recallcertificate' || action=='canceldisputecertificate')
	{
		if(confirm("Are you sure you want to carry out this action on the certificate?"))
		{
			document.getElementById('selectedApplications').value = usId;
			document.getElementById('selectedUserAction').value = action;
			document.getElementById('userListFormId').submit();
		}
	}else if(action=='downloadcertificate')
	{
		document.location.href='<%=response.encodeURL(request.getContextPath()+"/ActiveServlet?action=downloadCertificate&gyus=")%>'+aert +'&iosdp='+usId;
	}
	else
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

