<%@page import="smartpay.entity.enumerations.UserStatus"%>
<%@page import="smartpay.entity.ApplicationAttachmentAgency"%>
<%@page import="smartpay.entity.enumerations.CertificateStatus"%>
<%@page import="smartpay.entity.Agency"%>
<%@page import="smartpay.entity.enumerations.ApplicantType"%>
<%@page import="smartpay.entity.EndorsedApplicationDesk"%>
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
<%@page import="smartpay.entity.enumerations.IdentificationType"%>
<%@page import="smartpay.entity.Country"%>
<%@page import="smartpay.entity.State"%>
<%@page import="smartpay.entity.ApplicationItem"%>
<%@page import="smartpay.entity.Certificate"%>
<%@page import="smartpay.entity.ApplicationWorkflow"%>
<%@page import="smartpay.entity.enumerations.KinRelationshipType"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.enumerations.RoleConstants"%>
<%@page import="smartpay.entity.enumerations.ApplicationStatus"%>
<%@page import="smartpay.entity.enumerations.AgencyType"%>
<%@page import="smartpay.entity.enumerations.PermissionType"%>
<%@page import="com.ecims.commins.ECIMSConstants"%>
<%@page import="smartpay.entity.RoleType"%>
<%@page import="smartpay.entity.Application"%>
<%@page import="smartpay.entity.ApplicationFlag"%>
<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.entity.ApplicationAttachment"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Arrays" %>
<%@page import="java.util.List" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="java.io.File" %>
<%@page import="com.ecims.commins.Util"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />
<%
	String resourceBaseURL = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ "/resources";
	String faceboxCssUrl = resourceBaseURL + "/css/facebox.css";
	String pagingUrl = resourceBaseURL + "/css/paging.css";


		
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
<style>
	.plus{
		padding-left:5px;
		padding-right:5px;
		padding-top:5px;
		padding-bottom:5px;
		background-color: #33CC33;
		color: #ffffff;
	}
</style>
<%

ApplicationManagementPortletState portletState = ApplicationManagementPortletState.getInstance(renderRequest, renderResponse);
ApplicationWorkflow app = portletState.getApplicationWorkflow();
Logger log = Logger.getLogger(ApplicationManagementPortletState.class);
//String folder = ECIMSConstants.NEW_APPLICATION_DIRECTORY;
Collection<ApplicationItem> appItemList = portletState.getApplicationManagementPortletUtil().getApplicationItemsByApplication(app.getApplication());
Iterator<ApplicationItem> iter1 = appItemList.iterator(); 
ApplicationItem appitem = iter1.next();
String status = "";
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm");
Certificate cert = (Certificate)portletState.getApplicationManagementPortletUtil().getCertificateByApplication(app.getApplication());

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
	 String roleSource = ((RoleType)portletState.getApplicationManagementPortletUtil().getEntityObjectById(RoleType.class, app.getSourceId())).getName().getValue();
	 String roleRec = ((RoleType)portletState.getApplicationManagementPortletUtil().getEntityObjectById(RoleType.class, app.getReceipientRole())).getName().getValue();
	 
	 Agency agencyActor = null;
	  if(app.getSourceAgencyId()!=null)
	  {
	  	agencyActor = ((Agency)portletState.getApplicationManagementPortletUtil().getEntityObjectById(Agency.class, app.getSourceAgencyId()));
	  }
	  Collection<String> endAgency = ((Collection<String>)portletState.getApplicationManagementPortletUtil().getAllAppWorkflowByApplicationWFAndStatus(app.getId(), ApplicationStatus.APPLICATION_STATUS_ENDORSED.getValue()));
	  Collection<String> dendAgency = ((Collection<String>)portletState.getApplicationManagementPortletUtil().getAllAppWorkflowByApplicationWFAndStatus(app.getId(), ApplicationStatus.APPLICATION_STATUS_DISENDORSED.getValue()));
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
	  
  if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
	  status = "Approved awaiting EUC Issuance";
  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
	  status = "Request disapproved";
  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_FORWARDED))
	  status = "Forwarded to " + roleRec;
  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISPUTED))
	  status = "Disputed By " + roleSource;
  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISENDORSED))
  {
	  //status = "Disendorsed By " + roleSource;
	  status = "Disendorsed By " + Arrays.toString(disendorsementList);
  }
  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ENDORSED))
  {
	  //status = "Endorsed By " + roleSource;
	  status = "Endorsed By " + Arrays.toString(endorsementList);
  }
  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CREATED))
	  status = "Pending";
  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ATTACHMENT_DEVALIDATED))
	  status = "Attachment Devalidated by " + roleSource;
  else
	  status = "Pending";
 }else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
 {
	 if(app.getApplication().getApplicant()!=null)
	 {
		  if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISENDORSED))
			  status = "Disendorsed";
		  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISPUTED))
			  status = "Disputed";
		  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ENDORSED))
			  status = "Endorsed";
		  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ATTACHMENT_DEVALIDATED))
			  status = "Devalidated";
		  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_FORWARDED))
			  status = "Forwarded From ONSA";
		  else 
			  status = "N/A";
	 }else if(app.getApplication().getPortalUser()!=null)
	 {
		 
		 if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
		  {
			  status = "Approved by ONSA Staff";
		  }
		  if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED))
		  {
			  status = "Certificate Issued";
		  }
		  if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CREATED))
		  {
			  status = "Pending ONSA Action";
		  }
		  if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISENDORSED))
		  {
			  Collection<String> dendAgency = ((Collection<String>)portletState.getApplicationManagementPortletUtil().getAllAppWorkflowByApplicationAndStatus(app.getApplication().getId(), ApplicationStatus.APPLICATION_STATUS_DISENDORSED.getValue()));
			  String[] disendorsementList = null;
			  if(dendAgency!=null && dendAgency.size()>0)
			  {
				  disendorsementList = dendAgency.toArray(new String[dendAgency.size()]);
			  }
			  status = "Disendorsed by " + Arrays.toString(disendorsementList);
		  }
		  if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISPUTED))
		  {
			  status = "Disputed";
		  }
		  if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ENDORSED))
		  {
			  Collection<String> endAgency = ((Collection<String>)portletState.getApplicationManagementPortletUtil().getAllAppWorkflowByApplicationAndStatus(app.getApplication().getId(), ApplicationStatus.APPLICATION_STATUS_ENDORSED.getValue()));
			  String[] endorsementList = null;
			  if(endAgency!=null && endAgency.size()>0)
			  {
				  endorsementList = endAgency.toArray(new String[endAgency.size()]);
			  }
			  status = "Endorsed by " + Arrays.toString(endorsementList);
		  }
		  if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_FORWARDED))
		  {
			  status = "Forwarded From ONSA Staff";
		  }
		  if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
		  {
			  status = "Rejected By ONSA Staff";
		  }
	 }
  }


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
}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
{
%>
<jsp:include page="/html/applicationmanagementportlet/tabs_agency.jsp" flush="" />
<%
}
%>

<portlet:actionURL var="proceed" name="processAction">
	<portlet:param name="action"
		value="<%=APPLIST_ACTIONS.ACT_ON_APPLICATION.name()%>" />
</portlet:actionURL>


<div style="padding-left:10px; padding-right:10px; width:900px" id="print-content">
	<h4>Application Details</h4>
	<form  id="appListFormId" action="<%=proceed%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">

                            <div class="panel panel-primary">
                                <div class="panel-heading">
                                    APPLICANT DETAILS                  
                                </div>
                                <div class="panel-body" id="collapseOne">
                                    <div class="row" style="padding-left:10px;">
                                    	<div class="col-lg-9">
                                    	<% 
                                    	if(app.getApplication().getApplicant()!=null)
                                    	{
                                    		if(app.getApplication().getApplicant().getPortalUser().getCompany()==null)
                                    		{
                                    	%>
                                    	
                                    	
											<div class="form-group">
                                                <br>
                                                <strong>Application Number</strong>
                                                <div><%=app.getApplication().getApplicationNumber()==null ? "" : app.getApplication().getApplicationNumber()%></div>
                                                
                                            </div>
											<div class="form-group">
                                                <br>
                                                <strong>Applicant-Number</strong>
                                                <div><%=app.getApplication().getApplicant().getApplicantNumber()==null ? "" : app.getApplication().getApplicant().getApplicantNumber()%></div>
                                                
                                            </div>
                                            <div class="form-group">
                                                <br>
                                                <strong>Applicant Name</strong>
                                                  <div><%=app.getApplication().getApplicant().getPortalUser().getFirstName() + " " + app.getApplication().getApplicant().getPortalUser().getSurname()%></div>
                                                
                                            </div>
											<div class="form-group">
                                                <br>
                                                <strong>Applicant Type</strong>
                                                  <div><%=app.getApplication().getApplicant().getApplicantType().getValue()%></div>
                                                
                                            </div>
                                            <% 
                                            if(app.getApplication().getApplicant().getApplicantType().getValue().equals(ApplicantType.APPLICANT_TYPE_INDIVIDUAL.getValue()))
                                            {
                                            %>
                                            <div class="form-group">
                                                <strong>Applicant Address</strong>
                                                 <div><%=app.getApplication().getApplicant().getPortalUser().getAddressLine1()!=null ? (app.getApplication().getApplicant().getPortalUser().getAddressLine1() + "<br>" + (app.getApplication().getApplicant().getPortalUser().getAddressLine2()!=null ? app.getApplication().getApplicant().getPortalUser().getAddressLine2() : "")) : "N/A"%></div>
                                                
                                            </div>
                                            <%
                                            }
                                            %>
                                            <div class="form-group">
                                                <strong>Applicant Email Address</strong>
                                                 <div><%=app.getApplication().getApplicant().getPortalUser().getEmailAddress() %></div>
                                                
                                            </div>
                                            
                                            <div class="form-group">
                                                <strong>Applicant Phone Number</strong>
                                                 <div><%=app.getApplication().getApplicant().getPortalUser().getPhoneNumber() %></div>
                                                
                                            </div>
                                            <%
                                            if(portletState.getPortalUser()!=null && !portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_END_USER))
                                            {
                                            %>
                                            <div class="form-group">
                                                <button type="button" class="btn btn-inverse" onclick="javascript:handleButtonAction('viewapplicant', '<%=app.getId()%>', '')">View Applicants Complete Details</button>
                                            </div>
                                            <%
                                            }
                                            %>
                                        <%
                                    		}else
                                    		{
                                    			%>
			                                    <div class="panel-body" id="collapseOne">
			                                        <div class="row">
			
			                                            <div class="col-lg-5">
			    										  	<div class="form-group">
			                                                    <br>
			                                                    <strong>Applicant-Number</strong>                                            
			                                                    <div><%=app.getApplication().getApplicant().getApplicantNumber()%></div>                                            
			                                                </div>
			                                                <%
			                                                if(!portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER))
			                                            	{
			                                                %>
			                                                <div class="form-group">
			                                                    <br>
			                                                    <strong>Company Name</strong>
			                                                      <div><%=app.getApplication().getApplicant().getPortalUser().getCompany().getName()%></div>
			                                                    
			                                                </div>
			                                                
			                                                <%
			                                            	}else
			                                            	{
			                                            	%>
			                                            	<div class="form-group">
			                                                    <br>
			                                                    <strong>Company/Agency Name</strong>
			                                                      <div><%=app.getApplication().getApplicant().getPortalUser().getCompany().getName()%></div>
			                                                    
			                                                </div>
			                                            	<%
			                                            	}
			                                                if(!portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER))
			                                            	{
			                                                %>
			    											<div class="form-group">
			                                                    <br>
			                                                    <strong>Company Address</strong>
			                                                      <div><%=app.getApplication().getApplicant().getPortalUser().getCompany().getAddress()%></div>
			                                                    
			                                                </div>
			                                                <div class="form-group">
			                                                    <strong>Company Phone Number</strong>
			                                                     <div><%=app.getApplication().getApplicant().getPortalUser().getCompany().getPhoneNumber() %></div>
			                                                    
			                                                </div>
			                                                <div class="form-group">
			                                                    <strong>Company Email Address</strong>
			                                                     <div><%=app.getApplication().getApplicant().getPortalUser().getCompany().getEmailAddress() %></div>
			                                                    
			                                                </div>
			                                                
			                                                <div class="form-group">
			                                                    <strong>Company Registration Number</strong>
			                                                     <div><%=app.getApplication().getApplicant().getPortalUser().getCompany().getRegNo() %></div>
			                                                    
			                                                </div>
			                                                <div class="form-group">
			                                                    <strong>Company Website</strong>
			                                                     <div><%=app.getApplication().getApplicant().getPortalUser().getCompany().getWebsite() == null ? "N/A" : app.getApplication().getApplicant().getPortalUser().getCompany().getWebsite() %></div>
			                                                    
			                                                </div>
			                                                <%
			                                            	}
			                                                %>
			                                            </div>
			                                            
		                                                <div class="col-lg-5">
	                                                		<img style="width:100px;" src="<%="/resources/images/uploadedfiles/" + (app.getApplication().getApplicant().getPortalUser().getCompany().getLogo()==null ? "" : app.getApplication().getApplicant().getPortalUser().getCompany().getLogo())%>"><br>
	                                                		<div style="font-size:11px">Applicants' Company Logo</div>
	                                            		</div>
	                                            		
	                                            		<%
			                                            if(portletState.getPortalUser()!=null && !portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_END_USER))
			                                            {
			                                            %>
			                                            <div class="form-group">
			                                                <button type="button" class="btn btn-inverse" onclick="javascript:handleButtonAction('viewapplicant', '<%=app.getId()%>', '')">View Applicants Complete Details</button>
			                                            </div>
			                                            <%
			                                            }
			                                            %>
			                                                
			                                        </div>
			                                        
			                                        <!-- .panel-body -->
			                                    </div>
                                    			<%
                                    		}
                                        }
                                    	else if(app.getApplication().getPortalUser()!=null)
                                    	{
                                    	%>
                                    	
                                    	
											<div class="form-group">
                                                <br>
                                                <strong>Application Number</strong>
                                                <div><%=app.getApplication().getApplicationNumber()==null ? "" : app.getApplication().getApplicationNumber()%></div>
                                                
                                            </div>
											<div class="form-group">
                                                <br>
                                                <strong>Agency Name</strong>
                                                <div><%=app.getApplication().getPortalUser()==null ? "" : app.getApplication().getPortalUser().getAgency().getAgencyName()%></div>
                                                
                                            </div>
                                            <div class="form-group">
                                                <br>
                                                <strong>Applicant Name</strong>
                                                  <div><%=app.getApplication().getPortalUser().getFirstName() + " " + app.getApplication().getPortalUser().getSurname()%></div>
                                                
                                            </div>
                                            <div class="form-group">
                                                <strong>Applicant Email Address</strong>
                                                 <div><%=app.getApplication().getPortalUser().getEmailAddress() %></div>
                                                
                                            </div>
                                            
                                            <div class="form-group">
                                                <strong>Applicant Phone Number</strong>
                                                 <div><%=app.getApplication().getPortalUser().getPhoneNumber() %></div>
                                                
                                            </div>
                                           	
                                           	<%
                                            if(portletState.getPortalUser()!=null && !portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_END_USER))
                                            {
                                            %>
                                            <div class="form-group">
                                                <button type="button" class="btn btn-inverse" onclick="javascript:handleButtonAction('viewapplicant', '<%=app.getId()%>', '')">View Applicants Complete Details</button>
                                            </div>
                                            <%
                                            }
                                            %>
                                        <%
                                        }
                                        %>
                                        
                                        </div>
                                        
                                        
                                            
                                    </div>
                                    
                                    <!-- .panel-body -->
                                </div>

                            </div>

                            <div class="panel panel-primary">
                                <div class="panel-heading">
                                    APPLICATION DETAILS                    
                                </div>
                                <div class="panel-body">
                                	<div class="row" style="padding-left:10px;">
                                        <div class="col-lg-5">
                                            <div class="form-group">
                                                <div style="font-weight:bold;"><strong>Application Status:</strong><br>
								<span style="color:red"><%=status!=null ? status : ""%></span></div>
                                            </div>
                                        </div>

                                        
                                    </div>
                                    
                                    <div class="row" style="padding-left:10px;">
                                        <div class="col-lg-5">
                                            <div class="form-group">
                                                <strong>Category Items fall under:</strong><br>
								<%=appitem.getItemCategorySub()!=null ? appitem.getItemCategorySub().getItemCategory().getItemCategoryName() : ""%> 
                                            </div>
                                            <div class="form-group">
                                                <strong>Country of Origin/Manufacture:</strong><br>
                                                <%=app.getApplication().getCountry()!=null ? app.getApplication().getCountry().getName() : "N/A"%>
                                            </div>
                                            <div class="form-group">
                                                 <strong>Purpose Of Usage:</strong><br>
                                                 <%=app.getApplication().getPurposeOfUsage()==null ? "N/A" : app.getApplication().getPurposeOfUsage()%>
                                            </div>
                                            <div class="form-group">
                                               <strong>Port of Landing:</strong><br>
                                               <%=app.getApplication().getPortCode()==null ? "N/A" : app.getApplication().getPortCode().getPortCode()%>
                                           	</div>
                                            
                                             
                                        </div>
                                     </div>

                                </div>        
                             </div>
                             <%
							if(app.getApplication().getImportName()!=null)
							{
							%>
                            <div class="panel panel-primary">
                                <div class="panel-heading">
                                    <div class="panel-title">
                                    	<strong>SECTION FOR THE ORGANIZATION YOU ARE IMPORTING FOR</strong>
                                    </div>
                                </div>
                                <div class="panel-body">
                                    <div class="row" style="padding-left:10px;">
                                        <div class="col-lg-12" id="divImportingfor" style="display: block;">

                                            <div class="form-group">
                                                <strong>Name:</strong><br>
                            					<%=app.getApplication().getImportName()!=null ? app.getApplication().getImportName() : ""%>
                                            </div>
                                            <div class="form-group">
                                                <strong>Address:</strong><br>
                                                <%=app.getApplication().getImportAddress()!=null ? app.getApplication().getImportAddress() : ""%>
                                            </div>
                                            <div class="form-group">
                                                <strong>Attached Contract Award Letter:</strong>
                                                <%
                                                if(portletState.getApplication().getImportLetter()!=null)
                                                {
                                                %>
                                                	<a target="_blank" href="/resources/images/uploadedfiles/<%=app.getApplication().getImportLetter() %>">Download/View Document</a>
                                                <%
                                                }
                                                %>
                                            </div>

                                        </div>
                                    </div>
                                </div>
                            </div>
                            <%
							}
                            %>
                            
                            <div class="panel panel-primary">
                                <div class="panel-heading">
                                    <div class="panel-title">
                                    	Items Imported:
                                    </div>
                                </div>
                                <div class="panel-body">
                                    <div class="row">
                                        <div class="row" style="padding: 10px">
                                            	<table width="100%" class="table table-hover" id="btable">
                                            		<thead>
                                            			<th>S/No</th>
                                            			<th>HS Code</th>
                                            			<th>Item Description</th>
                                            			<th>Quantity/Weight</th>
                                            			<th>Unit Cost</th>
                                            		</thead>
                                            		<tbody>
                                            <%
                                            int sn =1;
                                            Double totalAmount = 0.00;
                                            String currency = "";
                                            for(Iterator<ApplicationItem> iter = appItemList.iterator(); iter.hasNext();)
                                            {
                                            	ApplicationItem applicationItem = iter.next();
                                            	//
                                            	currency = applicationItem.getCurrency().getHtmlEntity();
                                            %>
                                            	<tr>
                                            		<td><%=sn++ %></td>
                                           			<td><%=applicationItem.getItemCategorySub()!=null ? (applicationItem.getItemCategorySub().getHsCode() + " - " + applicationItem.getItemCategorySub().getName()) : "N/A"%></td>
                                           			<td><%=applicationItem.getDescription()==null ? "N/A" : applicationItem.getDescription()%></td>
                                           			<td><%=applicationItem.getQuantity()==null ? "N/A" : applicationItem.getQuantity()%></td>
                                           			<td><%=applicationItem.getCurrency()!=null ? applicationItem.getCurrency().getHtmlEntity() : "" %><%=applicationItem.getAmount()==null ? "N/A" : new Util().roundUpAmount(applicationItem.getAmount())%></td>
                                           			
                                           		</tr>
                                            	
                                            <%
                                            	totalAmount += (applicationItem.getAmount()==null ? 0 : applicationItem.getAmount());
                                            }
                                            %>
                                            </tbody>
                                            <tr>
                                        			<td colspan="4" style="font-weight:bold">Total Cost</td>
                                        			<td><%=currency + "" + new Util().roundUpAmount(totalAmount) %></td>
                                        		</tr>
                                            	</table>
                                            </div>

                                        </div>
                                    </div>
                                </div>
                            </div>

							<div style="padding-left:10px; padding-right:10px; width:900px">
	                            <div class="panel panel-primary">
	                                <div class="panel-heading">
	                                    SUPPORTING DOCUMENTS                    
	                                </div>
	                                <div class="panel-body">
	                                    <div class="row" style="padding-left:10px;">
	                                            <div class="form-group file">
	                                                <strong>Attachment Type:</strong>
	                                                        <table cellspacing="0" style="border-collapse:collapse;">
		<tbody><tr>
																<td class="table table-bordered no-more-tables">
															<%
														Collection<ApplicationAttachment> attachListing = portletState.getApplicationManagementPortletUtil().getApplicationAttachmentListByApplication(app.getApplication());
															
														
														if(attachListing!=null && attachListing.size()>0)
														{
															for(Iterator<ApplicationAttachment> iter= attachListing.iterator(); iter.hasNext();)
															{
																ApplicationAttachment applicationAttachment = iter.next();
																%>
	                                                                <div style="padding: 10px">
	                                                                    <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_0"><%=applicationAttachment.getApplicationAttachmentType().getName() %></div>
	                                                                    <%
	                                                                    String validatedStr=null;
	                                                                    if(portletState.getValidatedAttachments()!=null && portletState.getValidatedAttachments().size()>0)
	                                                                    {
	                                                                    	validatedStr="";
		                                                                    for(Iterator<ApplicationAttachmentAgency> itr = portletState.getValidatedAttachments().iterator(); itr.hasNext();)
		                                                                    {
		                                                                    	ApplicationAttachmentAgency fwa = itr.next();
		                                                                    	if(fwa.getApplicationAttachment().getId().equals(applicationAttachment.getId()))
		                                                                    	{
		                                                                    		validatedStr = validatedStr + fwa.getAgency().getAgencyName() + ", ";
		                                                                    	}
		                                                                    }
	                                                                    }
	                                                                    
	                                                                    
	                                                                    
	                                                                    %>
	                                                                    <div style="font-style: italic;"><%=(validatedStr!=null? ("Endorsed by " + (validatedStr.length()>2 ? validatedStr.substring(0, validatedStr.length()-2) : "")) : "") %></div>
	                                                                    <a target="_blank" href="/resources/images/uploadedfiles/<%=applicationAttachment.getAttachmentFile() %>">Download/View Document</a>
	                                                                    <%
	                                                                    
	                                                                    
	                                                                    if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
	                                                                    {
	                                                                    	if(portletState.getApplicationManagementPortletUtil().canUserValidateThisApplication(app, portletState.getPortalUser(), applicationAttachment))
	                                                                    	{
	                                                                    		%>
			                                                                    <input type="checkbox" name="validateVer2" value="<%=applicationAttachment.getId()%>">&nbsp;&nbsp;Validate Attachment</br>
			                                                                    <%
	                                                                    	}
	                                                                    }
	                                                                    
	                                                                    
	                                                                    %>
	                                                                    
	                                                                </div>
	                                                            
																<%
															}
															
															
															if(portletState.getPortalUser().getRoleType().getName().getValue().equals(RoleTypeConstants.ROLE_REGULATOR_USER.getValue()) && 
																	attachListing!=null && portletState.getValidatedAttachments()!=null && attachListing.size()!=portletState.getValidatedAttachments().size())
															{
															%>
																<button type="button" class="btn btn-success" onclick="javascript: handleButtonAction('validateattachments', '371', '')">Validate Selected Attachments</button>
															<%
															}
														}
														
															%></td>
		</tr>
	</tbody></table>
	
	                                            </div>
	
	                                            
	                                    </div>
	
	                                </div>
	                                
	                            </div>
							</div>
								


							
	                           
							<div style="padding-left:10px; padding-right:10px; width:900px">
								<div class="panel panel-primary">
			                            <div class="panel-heading">
			                            	
			                                <div class="panel-title">
			                                	<label>Comments & Flags</label>
			                                </div>
			                                
			                            </div>
			                            <div class="panel-body">
			                                <div class="row" style="padding-left:10px;">
			                                	<h5>Flags Raised (<%=portletState.getApplicationFlags()==null ? 0 : portletState.getApplicationFlags().size() %>)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:toggleViewAW('flagraid', 'plus1')" title="Click to view FLags Raised"><span id="plus1" class="plus">Click to View/Hide</span></a></h5>
			                                    <div class="row">
			                                        <div id="flagraid" style="display: none;">
			                                        	<%
			                                        if(portletState.getApplicationFlags()!=null)
			                                        {
			                                        	String sclaa="left";
			                                        	int c =0;
			                                        	for(Iterator<ApplicationFlag> iter = portletState.getApplicationFlags().iterator(); iter.hasNext();)
			                                        	{
			                                        		ApplicationFlag af = iter.next();
			                                        		if(c++%2==0)
			                                        			sclaa="left";
			                                        		else
			                                        			sclaa="right";
			                                        	%>
			                                            <div style="border-top: 1px #ccc solid;">
				                                            <div class="col-lg-10">
				                                                <strong><u>Date:</u></strong><br>
				                                                <%=af.getDateCreated()!=null ? sdf.format(af.getDateCreated()) : "N/A"%><br>
				                                                <strong><u>Comment:</u></strong><br>
				                                                <%=af.getComment()!=null ? af.getComment() : "N/A"%><br>
				                                                <strong><u>Flagged By:</u></strong><br>
				                                                <%=af.getPortalUser()!=null ? af.getPortalUser().getFirstName() + " " + af.getPortalUser().getSurname() + " " +(af.getPortalUser().getAgency()==null ? "" : "(" +af.getPortalUser().getAgency().getAgencyName()+")")+"" : "N/A"%>
				                                            </div>
				                                        </div>
			                                            <%
			                                        	}
			                                        }else
			                                        {
			                                        	%>
			                                        	<div class="col-lg-12">
			                                                None Available.
			                                            </div>
			                                        	<%
			                                        }
			                                            %>
			                                        </div>
			                                    </div>
			                                    
			                                    
			                                    <div class="row" style="clear:both; padding-left:20px;">
			                                    	<h5>Comments Made on Applications <%=portletState.getApplicationWorkFlowsListingForComments()!=null ? ("(" + portletState.getApplicationWorkFlowsListingForComments().size() + ")") : "(0)" %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:toggleViewAW('cmtApp', 'plus2')" title="Click to view comments"><span id="plus2" class="plus">Click to View/Hide</span></a></h5>
			                                        <div id="cmtApp" style="display: none;">
			                                        	<%
			                                        if(portletState.getApplicationWorkFlowsListingForComments()!=null)
			                                        {
			                                        	int c=0;
			                                        	String sclaa="left";
			                                        	
			                                        	
			                                        	for(Iterator<ApplicationWorkflow> iter = portletState.getApplicationWorkFlowsListingForComments().iterator(); iter.hasNext();)
			                                        	{
			                                        		ApplicationWorkflow af = iter.next();
				                                        	Long sId = af.getSourceId();
			                                        		RoleType srtp = (RoleType)portletState.getApplicationManagementPortletUtil().getEntityObjectById(RoleType.class, sId);
			                                        		if(c++%2==0)
			                                        			sclaa="left";
			                                        		else
			                                        			sclaa="right";
			                                        		
			                                        		
			                                        	%>
			                                            <div style="width: 90%; padding-top: 10px;">
			                                        	
				                                            <div class="col-lg-12">
				                                                <strong><u>Date:</u></strong><br>
				                                                <%=af.getDateCreated()!=null ? sdf.format(af.getDateCreated()) : "N/A"%>
				                                            </div>
				                                            <div class="col-lg-12">
				                                               <strong><u>From:</u></strong><br>
				                                                <%=srtp!=null ? srtp.getName() : "N/A"%>
				                                            </div>
				
				                                            <div class="col-lg-12" style="border-bottom: 1px #ccc solid;">
				                                                <strong><u>Comment:</u></strong><br>
				                                                <%=af.getComment()!=null ? af.getComment() : "N/A"%>
				                                            </div>
				                                        </div>
			                                            <%
			                                        	}
			                                        }else
			                                        {
			                                        	%>
			                                        	<div class="col-lg-12">
			                                                None Available.
			                                            </div>
			                                        	<%
			                                        }
			                                            %>
			                                        </div>
			                                    </div>
			                                    
			                                    
			                                    
			                                    <%
												if(portletState.getEndorsedApplicationDeskList()!=null && portletState.getEndorsedApplicationDeskList().size()>0)
												{
												%>
												<div class="row" style="clear:both; padding-left:10px;">
					                            	<h5>Endorsements Made on Application <%=portletState.getEndorsedApplicationDeskList()!=null ? ("(" + portletState.getEndorsedApplicationDeskList().size() + ")") : "(0)" %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:toggleViewAW('endApps', 'plus3')" title="Click to view endorsements"><span id="plus3" class="plus">Click to View/Hide</span></a></h5>
					                                <div id="endApps" style="display: none;">
														<div class="row" style="padding-left:10px;padding-right:10px;">
											                <h4>This Application has been endorsed by</h4><br>
											                <table width="100%" class="table table-hover" id="btable">
											                			<thead>
											                    			<th>S/No</th>
											                    			<th>Endorsement Date</th>
											                    			<th>Endorsement Desk</th>
											                    			<th>Agency Name</th>
											                    			<th>Agency Type</th>
											                    		</thead>
											                    		<tbody>
											                    <%
											                    sn=1;
											                    
											                    for(Iterator<EndorsedApplicationDesk> iter = portletState.getEndorsedApplicationDeskList().iterator(); iter.hasNext();)
											                    {
											                    	EndorsedApplicationDesk ead = iter.next();
											                    	
											                    	
											                    %>
											                    	<tr>
											                    		<td><%=sn++ %></td>
											                    		<td><%=ead.getEndorsementDate()==null ? "N/A" : sdf.format(new Date(ead.getEndorsementDate().getTime())) %></td>
											                   			<td><%=ead.getEndorsementDesk().getEndorseDeskName()==null ? "N/A" : ead.getEndorsementDesk().getEndorseDeskName() %></td>
											                   			<td><%=ead.getEndorsementDesk().getAgency()==null ? "N/A" : ead.getEndorsementDesk().getAgency().getAgencyName()%></td>
											                   			<td><%=ead.getEndorsementDesk().getAgency()==null ? "N/A" : ead.getEndorsementDesk().getAgency().getAgencyType().getValue()%></td>
											                   		</tr>
											                    	
											                    <%
											                    }
											                    %>
											                                   </tbody>
											                           	</table>  
											                               
														</div>
													</div>
												</div>
												<%
												}
												%>
			                                </div>
			                            </div>
			                        </div>
			                </div>        
					
							
					
					<div style="padding-left:10px; padding-right:10px; width:900px">  
					<%
					if(app.getApplication().getApplicant()!=null && app.getApplication().getApplicant().getBlackList()!=null && app.getApplication().getApplicant().getBlackList().equals(Boolean.TRUE))
					{
					}else if(app.getApplication().getApplicant()!=null)
					{
					%>
						<!-- <textarea style="float:right; width: 80%" name="reason" placeholder="To blacklist this applicant, provide reasons in this box before blacklisting applicant"></textarea>-->
					<%
					}
					%>
						<div style="clear:both">&nbsp;</div>    
	                	<button type="button" class="btn btn-danger" style="float:left" onclick="javascript:handleButtonAction('gobacktoviewlisting', '<%=app.getId()%>', '')">Go Back</button>        
	                        <%
					if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
			  		{
			  		%>
						<div class="btn-group" style="float:right">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>', '')">View Applicant Details</a></li>
									<li><a target='_blank' href="javascript: handleButtonAction('downloadcertificateTicket', '<%=app.getApplication().getApplicant().getPortalUser().getId()%>', '<%=cert!=null ? cert.getId() : ""%>')">Download Certificate Ticket</a></li>
							  	</ul>
							</div>
						
					<%
			  		}
					else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
			  		{
			  			if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CREATED))
			  			{
			  				%>
							<div class="btn-group" style="float:right">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>', '')">View Applicant Details</a></li>
									<%
									if(portletState!=null && portletState.getPermissionList().size()>0 && portletState.getPermissionList().contains(PermissionType.PERMISSION_FORWARD_APPLICATION))
									{
										if(app.getApplication().getApplicant().getPortalUser().getStatus().equals(UserStatus.USER_STATUS_ACTIVE))
										{
									%>
									<li><a href="javascript: handleButtonAction('forward', '<%=app.getId()%>', '')">Forward Application</a></li>
									<%
										}
									}
									if(portletState!=null && portletState.getPermissionList().size()>0 && portletState.getPermissionList().contains(PermissionType.PERMISSION_REJECT_APPLICATION))
									{
									%>
									<li><a href="javascript: handleButtonAction('rejectapplication', '<%=app.getId()%>', '')">Reject Application</a></li>
									<%
									}
									%>
									<%
									if(app.getApplication().getExceptionType()!=null && app.getApplication().getExceptionType().equals(Boolean.TRUE))
									{
										if(portletState!=null && portletState.getPermissionList().size()>0 && portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION))
										{
											if(app.getApplication().getApplicant().getPortalUser().getStatus().equals(UserStatus.USER_STATUS_ACTIVE))
											{
										%>
										<li><a href="javascript: handleButtonAction('approve', '<%=app.getId()%>', '')">Approve Application</a></li>
										<%
											}
										}
									}
									if(portletState!=null && portletState.getPermissionList().size()>0 && (portletState.getPermissionList().contains(PermissionType.PERMISSION_MANAGE_APPLICANT) || portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION)))
									{
										if(app.getApplication().getApplicant()!=null)
										{
											if(app.getApplication().getApplicant()!=null && 
													(app.getApplication().getApplicant().getBlackList()==null || (app.getApplication().getApplicant().getBlackList()!=null && 
													app.getApplication().getApplicant().getBlackList().equals(Boolean.FALSE))))
											{	
											%>
											<!-- <li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>', '')">BlackList Applicant</a></li>-->
											<%
											}else 
											{
												if(app.getApplication().getApplicant()!=null && app.getApplication().getApplicant().getBlackList()!=null && app.getApplication().getApplicant().getBlackList().equals(Boolean.TRUE))
												{
													%>
													<!-- <li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>', '')">Remove Applicant from BlackList</a></li>-->
													<%
												}
												if(app.getApplication().getPortalUser()!=null && app.getApplication().getPortalUser().getAgency().getBlacklist()!=null && app.getApplication().getPortalUser().getAgency().getBlacklist().equals(Boolean.TRUE))
												{
													%>
													<!-- <li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>', '')">Remove Applicant from BlackList</a></li>-->
													<%
												}
											}
										}
										else 
										{
											if(app.getApplication().getPortalUser()!=null && 
													app.getApplication().getPortalUser().getAgency()!=null)
											{
												if(app.getApplication().getPortalUser()!=null && 
														app.getApplication().getPortalUser().getAgency().getBlacklist()!=null && 
														app.getApplication().getPortalUser().getAgency().getBlacklist().equals(Boolean.TRUE))
												{	
												%>
												<!-- <li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>', '')">Remove Agency from BlackList</a></li>-->
												<%
												}else if(app.getApplication().getPortalUser()!=null && 
														(app.getApplication().getPortalUser().getAgency().getBlacklist()==null || 
														(app.getApplication().getPortalUser().getAgency().getBlacklist()!=null && 
														app.getApplication().getPortalUser().getAgency().getBlacklist().equals(Boolean.FALSE))))
												{
												%>
													<!-- <li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>', '')">BlackList Agency</a></li>-->
												<%
												}
											}
										}
			  						}
									%>
							  	</ul>
							</div>
						<%
			  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ATTACHMENT_DEVALIDATED))
			  			{
			  				%>
							<div class="btn-group" style="float:right">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>', '')">View Applicant Details</a></li>
								<%
								if(app.getApplication().getApplicant().getPortalUser().getStatus().equals(UserStatus.USER_STATUS_ACTIVE))
								{
								%>
									<li><a href="javascript: handleButtonAction('forward', '<%=app.getId()%>', '')">Forward Application</a></li>
									<!--<li><a href="javascript: handleButtonAction('forwardwithexception', '<%=app.getId()%>', '')">Forward Application With Exception</a></li>-->
									<li><a href="javascript: handleButtonAction('rejectapplication', '<%=app.getId()%>', '')">Reject Application</a></li>
									<%
									if(app.getApplication().getExceptionType()!=null && app.getApplication().getExceptionType().equals(Boolean.TRUE))
									{
										if(portletState!=null && portletState.getPermissionList().size()>0 && portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION))
										{
										%>
										<li><a href="javascript: handleButtonAction('approve', '<%=app.getId()%>', '')">Approve Application</a></li>
										<%
										}
									}
									if(portletState!=null && portletState.getPermissionList().size()>0 && (portletState.getPermissionList().contains(PermissionType.PERMISSION_MANAGE_APPLICANT) || portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION)))
									{
										if(app.getApplication().getApplicant()!=null && app.getApplication().getApplicant().getBlackList()!=null && app.getApplication().getApplicant().getBlackList().equals(Boolean.FALSE))
										{
									%>
									<!-- <li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>', '')">BlackList Applicant</a></li>-->
									<%
										}else
										{
									%>
									<!--<li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>', '')">Remove Applicant from BlackList</a></li>-->
									<%
										}
									}
								}
									%>
							  	</ul>
							</div>
							<%	
			  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED))
			  			{
			  				%>
							<div class="btn-group" style="float:right">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>', '')">View Applicant Details</a></li>
										<%
								if(app.getApplication().getApplicant().getPortalUser().getStatus().equals(UserStatus.USER_STATUS_ACTIVE))
								{
									if(portletState!=null && portletState.getPermissionList().size()>0 && (portletState.getPermissionList().contains(PermissionType.PERMISSION_MANAGE_APPLICANT) || portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION)))
									{
										if(app.getApplication().getApplicant()!=null && app.getApplication().getApplicant().getBlackList()!=null && app.getApplication().getApplicant().getBlackList().equals(Boolean.FALSE))
										{
										%>
										<!-- <li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>', '')">BlackList Applicant</a></li>-->
										<%
										}else
										{
										%>
										<!--<li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>', '')">Remove Applicant from BlackList</a></li>-->
										<%
										}
									}
										%>
									<li><a target='_blank' href="javascript: handleButtonAction('downloadcertificate', '<%=app.getApplication().getApplicant().getPortalUser().getId()%>', '<%=cert!=null ? cert.getId() : ""%>')">Download Certificate</a></li>
								<%
								}
								%>
							  	</ul>
							</div>
							<%	
			  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISENDORSED))
			  			{
			  				%>
							<div class="btn-group" style="float:right">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>', '')">View Applicant Details</a></li>
									<%
								if(app.getApplication().getApplicant().getPortalUser().getStatus().equals(UserStatus.USER_STATUS_ACTIVE))
								{
									if(portletState!=null && portletState.getPermissionList().size()>0 && portletState.getPermissionList().contains(PermissionType.PERMISSION_FORWARD_APPLICATION))
									{
									%>
									<li><a href="javascript: handleButtonAction('forward', '<%=app.getId()%>', '')">Forward Application</a></li>
									<!--<li><a href="javascript: handleButtonAction('forwardwithexception', '<%=app.getId()%>', '')">Forward Application With Exception</a></li>-->
									<%
									}
									if(portletState!=null && portletState.getPermissionList().size()>0 && portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION))
									{
									%>
									<li><a href="javascript: handleButtonAction('approve', '<%=app.getId()%>', '')">Approve Application</a></li>
									<%
									}
									if(portletState!=null && portletState.getPermissionList().size()>0 && portletState.getPermissionList().contains(PermissionType.PERMISSION_REJECT_APPLICATION))
									{
									%>
									<li><a href="javascript: handleButtonAction('rejectapplication', '<%=app.getId()%>', '')">Reject Application</a></li>
									<%
									}
									if(portletState!=null && portletState.getPermissionList().size()>0 && (portletState.getPermissionList().contains(PermissionType.PERMISSION_MANAGE_APPLICANT) || portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION)))
									{
										if(app.getApplication().getApplicant()!=null && app.getApplication().getApplicant().getBlackList()!=null && app.getApplication().getApplicant().getBlackList().equals(Boolean.FALSE))
										{
										%>
										<!-- <li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>', '')">BlackList Applicant</a></li>-->
										<%
										}else
										{
										%>
										<!-- <li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>', '')">Remove Applicant from BlackList</a></li>-->
										<%
										}
									}
								}
									%>
							  	</ul>
							</div>
						<%
			  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISPUTED))
			  			{
			  				%>
							<div class="btn-group" style="float:right">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>', '')">View Applicant Details</a></li>
									<%
								if(app.getApplication().getApplicant().getPortalUser().getStatus().equals(UserStatus.USER_STATUS_ACTIVE))
								{
									if(portletState!=null && portletState.getPermissionList().size()>0 && portletState.getPermissionList().contains(PermissionType.PERMISSION_FORWARD_APPLICATION))
									{
									%>
									<li><a href="javascript: handleButtonAction('forward', '<%=app.getId()%>', '')">Forward Application</a></li>
									<!--<li><a href="javascript: handleButtonAction('forwardwithexception', '<%=app.getId()%>', '')">Forward Application With Exception</a></li>-->
									<%
									}
									if(portletState!=null && portletState.getPermissionList().size()>0 && portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION))
									{
									%>
									<li><a href="javascript: handleButtonAction('approve', '<%=app.getId()%>', '')">Approve Application</a></li>
									<%
									}
									if(portletState!=null && portletState.getPermissionList().size()>0 && portletState.getPermissionList().contains(PermissionType.PERMISSION_REJECT_APPLICATION))
									{
									%>
									<li><a href="javascript: handleButtonAction('rejectapplication', '<%=app.getId()%>', '')">Reject Application</a></li>
									<%
									}
									if(portletState!=null && portletState.getPermissionList().size()>0 && (portletState.getPermissionList().contains(PermissionType.PERMISSION_MANAGE_APPLICANT) || portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION)))
									{
										if(app.getApplication().getApplicant()!=null && app.getApplication().getApplicant().getBlackList()!=null && app.getApplication().getApplicant().getBlackList().equals(Boolean.FALSE))
										{
										%>
										<!-- <li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>', '')">BlackList Applicant</a></li>-->
										<%
										}else
										{
										%>
										<!-- <li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>', '')">Remove Applicant from BlackList</a></li>-->
										<%
										}
									}
								}
									%>
							  	</ul>
							</div>
						<%
			  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ENDORSED))
			  			{
			  				%>
							<div class="btn-group" style="float:right">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>', '')">View Applicant Details</a></li>
									<%
								if(app.getApplication().getApplicant().getPortalUser().getStatus().equals(UserStatus.USER_STATUS_ACTIVE))
								{
									if(portletState!=null && portletState.getPermissionList().size()>0 && portletState.getPermissionList().contains(PermissionType.PERMISSION_FORWARD_APPLICATION))
									{
									%>
									<li><a href="javascript: handleButtonAction('forward', '<%=app.getId()%>', '')">Forward Application</a></li>
									<!--<li><a href="javascript: handleButtonAction('forwardwithexception', '<%=app.getId()%>', '')">Forward Application With Exception</a></li>-->
									<%
									}
									if(portletState!=null && portletState.getPermissionList().size()>0 && portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION))
									{
									%>
									<!-- <li><a href="javascript: handleButtonAction('approveone', '<%=app.getId()%>', '')">Approve Application</a></li>-->
									<%
									}
									if(portletState!=null && portletState.getPermissionList().size()>0 && portletState.getPermissionList().contains(PermissionType.PERMISSION_REJECT_APPLICATION))
									{
									%>
									<li><a href="javascript: handleButtonAction('rejectone', '<%=app.getId()%>', '')">Reject Application</a></li>
									<%
									}
									if(portletState!=null && portletState.getPermissionList().size()>0 && (portletState.getPermissionList().contains(PermissionType.PERMISSION_MANAGE_APPLICANT) || portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION)))
									{
										if(app.getApplication().getApplicant()!=null && app.getApplication().getApplicant().getBlackList()!=null && app.getApplication().getApplicant().getBlackList().equals(Boolean.FALSE))
										{
										%>
										<!-- <li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>', '')">BlackList Applicant</a></li>-->
										<%
										}else
										{
										%>
										<!-- <li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>', '')">Remove Applicant from BlackList</a></li>-->
										<%
										}
									}
								}
									%>
							  	</ul>
							</div>
						<%
			  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_FORWARDED))
			  			{
			  				%>
							<div class="btn-group" style="float:right">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>', '')">View Applicant Details</a></li>
									<%
								if(app.getApplication().getApplicant().getPortalUser().getStatus().equals(UserStatus.USER_STATUS_ACTIVE))
								{
									if(portletState!=null && portletState.getPermissionList().size()>0 && portletState.getPermissionList().contains(PermissionType.PERMISSION_FORWARD_APPLICATION))
									{
									%>
									<li><a href="javascript: handleButtonAction('forward', '<%=app.getId()%>', '')">Forward Application</a></li>
									<!--<li><a href="javascript: handleButtonAction('forwardwithexception', '<%=app.getId()%>', '')">Forward Application With Exception</a></li>-->
									<%
									}
									if(portletState!=null && portletState.getPermissionList().size()>0 && portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION))
									{
										if(app.getReceipientRole().equals(portletState.getPortalUser().getRoleType().getId()) && 
												app.getAgency().getId().equals(portletState.getPortalUser().getAgency().getId()))
										{
									%>
									<li><a href="javascript: handleButtonAction('approveone', '<%=app.getId()%>', '')">Approve Application</a></li>
									<%
										}
									}
									if(portletState!=null && portletState.getPermissionList().size()>0 && portletState.getPermissionList().contains(PermissionType.PERMISSION_REJECT_APPLICATION))
									{
										if(app.getReceipientRole().equals(portletState.getPortalUser().getRoleType().getId()) && 
												app.getAgency().getId().equals(portletState.getPortalUser().getAgency().getId()))
										{
									%><li><a href="javascript: handleButtonAction('rejectapplication', '<%=app.getId()%>', '')">Reject Application</a></li><%
										}
									}
									if(portletState!=null && portletState.getPermissionList().size()>0 && (portletState.getPermissionList().contains(PermissionType.PERMISSION_MANAGE_APPLICANT) || portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION)))
									{
										if(app.getApplication().getApplicant()!=null && app.getApplication().getApplicant().getBlackList()!=null && app.getApplication().getApplicant().getBlackList().equals(Boolean.FALSE))
										{
									%>
									<!-- <li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>', '')">BlackList Applicant</a></li>-->
									<%
										}else
										{
									%>
									<!-- <li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>', '')">Remove Applicant from BlackList</a></li>-->
									<%
										}
									}
								}
									%>
									<!-- <li><a href="javascript: handleButtonAction('cancelfwd', '<%=app.getId()%>', '')">Cancel Forwarding</a></li>-->
							  	</ul>
							</div>
						<%
			  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
			  			{
			  				%>
							<div class="btn-group" style="float:right">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>', '')">View Applicant Details</a></li>
									<%
								if(app.getApplication().getApplicant().getPortalUser().getStatus().equals(UserStatus.USER_STATUS_ACTIVE))
								{
									if(app.getApplication().getApplicant()!=null && app.getApplication().getApplicant().getBlackList()!=null && app.getApplication().getApplicant().getBlackList().equals(Boolean.FALSE))
									{
									%>
									<!-- <li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>', '')">BlackList Applicant</a></li>-->
									<%
									}else
									{
									%>
									<!-- <li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>', '')">Remove Applicant from BlackList</a></li>-->
									<%
									}
								}
									%>
							  	</ul>
							</div>
						<%
			  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
			  			{
			  				%>
							<div class="btn-group" style="float:right">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>', '')">View Applicant Details</a></li>
									<%
									if(portletState.getPermissionList().contains(PermissionType.PERMISSION_ISSUE_CERTIFICATE) && 
											(cert==null || (cert!=null && !cert.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_COLLECTED) && 
											(cert.getCertificatePrinted()== null || 
											(cert.getCertificatePrinted()!=null && cert.getCertificatePrinted().equals(Boolean.FALSE))))))
									{
										if(app.getApplication().getApplicant().getPortalUser().getStatus().equals(UserStatus.USER_STATUS_ACTIVE))
										{
									%>
										<li><a href="javascript: handleButtonAction('issuecertificate', '<%=app.getId()%>', '')">Issue Certificate</a></li>
									<%
										}
									}
									%>
									
							  	</ul>
							</div>
						<%
			  			}
			  		}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
			  		{
			  			if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))
			  			{
			  				
			  			}
			  			else
			  			{
			  				if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ATTACHMENT_DEVALIDATED))
				  			{
				  				%>
								<div class="btn-group" style="float:right">
									<button type="button" class="btn btn-success">Click Here for Actions</button>
								  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
										<span class="caret"></span>
										<span class="sr-only">Toggle Dropdown</span>
									</button>
								  	<ul class="dropdown-menu" role="menu">
								  		
										<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>', '')">View Applicant Details</a></li>
										<%
									if(app.getApplication().getApplicant().getPortalUser().getStatus().equals(UserStatus.USER_STATUS_ACTIVE))
									{
										if(portletState.getPortalUser().getAgency()!=null && !portletState.getPortalUser().getAgency().getAgencyType().equals(AgencyType.INFORMATION_GROUP))
										{
										%>
										<li><a href="javascript: handleButtonAction('disendorseapplication', '<%=app.getId()%>', '')">Disendorse Application</a></li>
										<!-- <li><a href="javascript: handleButtonAction('flagapplication', '<%=app.getId()%>', '')">Flag Application</a></li>-->
										<%
										}
									}
										%>
								  	</ul>
								</div>
								<%	
				  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISENDORSED))
				  			{
				  				%>
								<div class="btn-group" style="float:right">
									<button type="button" class="btn btn-success">Click Here for Actions</button>
								  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
										<span class="caret"></span>
										<span class="sr-only">Toggle Dropdown</span>
									</button>
								  	<ul class="dropdown-menu" role="menu">
										
										<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>', '')">View Applicant Details</a></li>
										<%
									if(app.getApplication().getApplicant().getPortalUser().getStatus().equals(UserStatus.USER_STATUS_ACTIVE))
									{
										if(portletState.getPortalUser().getAgency()!=null && !portletState.getPortalUser().getAgency().getAgencyType().equals(AgencyType.INFORMATION_GROUP))
										{
										%>
										<!-- <li><a href="javascript: handleButtonAction('flagapplication', '<%=app.getId()%>', '')">Flag Application</a></li>-->
										<%
										}
									}
										%>
								  	</ul>
								</div>
							<%
				  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISPUTED))
				  			{
				  				%>
								<div class="btn-group" style="float:right">
									<button type="button" class="btn btn-success">Click Here for Actions</button>
								  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
										<span class="caret"></span>
										<span class="sr-only">Toggle Dropdown</span>
									</button>
								  	<ul class="dropdown-menu" role="menu">
										
										<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>', '')">View Applicant Details</a></li>
										<%
									if(app.getApplication().getApplicant().getPortalUser().getStatus().equals(UserStatus.USER_STATUS_ACTIVE))
									{
										if(portletState.getPortalUser().getAgency()!=null && !portletState.getPortalUser().getAgency().getAgencyType().equals(AgencyType.INFORMATION_GROUP))
										{
										%>
										<!-- <li><a href="javascript: handleButtonAction('flagapplication', '<%=app.getId()%>', '')">Flag Application</a></li>-->
										<li><a href="javascript: handleButtonAction('disendorseapplication', '<%=app.getId()%>', '')">Disendorse Application</a></li>
										<%
										}
									}
										%>
								  	</ul>
								</div>
							<%
				  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ENDORSED))
				  			{
				  				%>
								<div class="btn-group" style="float:right">
									<button type="button" class="btn btn-success">Click Here for Actions</button>
								  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
										<span class="caret"></span>
										<span class="sr-only">Toggle Dropdown</span>
									</button>
								  	<ul class="dropdown-menu" role="menu">
										<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>', '')">View Applicant Details</a></li>
										<%
									if(app.getApplication().getApplicant().getPortalUser().getStatus().equals(UserStatus.USER_STATUS_ACTIVE))
									{
										if(portletState.getPortalUser().getAgency()!=null && !portletState.getPortalUser().getAgency().getAgencyType().equals(AgencyType.INFORMATION_GROUP))
										{
										%>
										<!-- <li><a href="javascript: handleButtonAction('flagapplication', '<%=app.getId()%>')">Flag Application</a></li>
										<li><a href="javascript: handleButtonAction('disputeAplication', '<%=app.getId()%>')">Dispute Application</a></li>-->
										<%
										}
									}
										%>
								  	</ul>
								</div>
							<%
				  			}
				  			else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
				  			{
				  			%>
				  				<div class="btn-group" style="float:right">
									<button type="button" class="btn btn-success">Click Here for Actions</button>
								  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
										<span class="caret"></span>
										<span class="sr-only">Toggle Dropdown</span>
									</button>
								  	<ul class="dropdown-menu" role="menu">
										<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>', '')">View Applicant Details</a></li>
										<%
									if(app.getApplication().getApplicant().getPortalUser().getStatus().equals(UserStatus.USER_STATUS_ACTIVE))
									{
										if(app.getApplication().getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED) && 
												(app.getApplication().getPortalUser().getId().equals(portletState.getPortalUser().getId())))
										{
										%>
											<li><a target='_blank' href="javascript: handleButtonAction('downloadAgencyTicket', '<%=app.getApplication().getPortalUser().getId()%>', '<%=app.getApplication()!=null ? app.getApplication().getId() : ""%>')">Download Certificate Ticket</a></li>
										<%
										}else if(app.getApplication().getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED) && 
												(app.getApplication().getApplicant().getPortalUser().getId().equals(portletState.getPortalUser().getId())))
										{
										%>
											<li><a target='_blank' href="javascript: handleButtonAction('downloadApplicantTicket', '<%=app.getApplication().getApplicant().getPortalUser().getId()%>', '<%=app.getApplication()!=null ? app.getApplication().getId() : ""%>')">Download Certificate Ticket</a></li>
										<%
										}
									}
										%>
								  	</ul>
								</div>
								<%			
				  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_FORWARDED))
				  			{
				  				
				  				%>
								<div class="btn-group" style="float:right">
									<button type="button" class="btn btn-success">Click Here for Actions</button>
								  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
										<span class="caret"></span>
										<span class="sr-only">Toggle Dropdown</span>
									</button>
								  	<ul class="dropdown-menu" role="menu">
								  		<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>', '')">View Applicant Details</a></li>
								  		
										<%
										if(app.getApplication().getApplicant().getPortalUser().getStatus().equals(UserStatus.USER_STATUS_ACTIVE))
										{
											if(portletState.getPortalUser().getAgency()!=null && !portletState.getPortalUser().getAgency().getAgencyType().equals(AgencyType.INFORMATION_GROUP))
											{
												
												if(portletState.getApplicationManagementPortletUtil().canUserEndorseThisApplication(app, portletState))
												{
											%>
											<li>
											<a href="javascript:handleButtonAction('endorseapplication', '<%=app.getId()%>')">Endorse Application</a></li>
												<%
												}
												%>
											<li>
											<a href="javascript: handleButtonAction('disendorseapplication', '<%=app.getId()%>', '')">Disendorse Application</a></li>
											<!-- <li><a href="javascript: handleButtonAction('disputeAplication', '<%=app.getId()%>', '')">Dispute Application</a></li>-->
											<!-- <li><a href="javascript: handleButtonAction('flagapplication', '<%=app.getId()%>', '')">Flag Application</a></li>-->
											
											<%
											}
										}
										%>
								  	</ul>
								</div>
							<%
				  			}
			  			}
			  			
			  		}
					%>
				</div>	
					

                    
				  <input type="hidden" name="cmnt" id="cmnt" value="">	
                  <input type="hidden" name="actId" id="actId" value="">
                  <input type="hidden" name="act" id="act" value="">
                            
	</form>
</div>
<div style="clear:both">
<input type="button" onclick="printDiv('print-content')" value="Print Page"/>
</div>





<script type="text/javascript">
function onlyDoubleKey(e, elementId)
{
	var src = (e.srcElement || e.target);
	var unicode=e.charCode? e.charCode : e.keyCode;
	
	var check = false;
	var lenAfter = document.getElementById(elementId).value.length - document.getElementById(elementId).value.indexOf(".");
	if(document.getElementById(elementId).value.length>0 && 
			document.getElementById(elementId).value.indexOf(".")==-1)
	{
		check =true;
	}
	
	if(((unicode>47) && (unicode<58)) || (check==true && unicode==46))
		{}
	else
		{return false}
	 
	
}


function onlyNumKey(e)
{
	var src = (e.srcElement || e.target);
	var unicode=e.charCode? e.charCode : e.keyCode
	
	
	
	if ((unicode>47) && (unicode<58)) 
		{}
	else
		{return false}
	 
	
}

function submitForm(clicked)
{
	document.getElementById('act').value=clicked;
	document.getElementById('appListFormId').submit;
	
}


function handleButtonAction(action, usId, aert){
	
	if(action=='rejectbynsauser')
	{
		if(confirm("Are you sure you want to reject this EUC application request? You will not be able to undo this action after rejecting this request!"))
		{
			document.getElementById('actId').value = usId;
			document.getElementById('act').value = action;
			document.getElementById('appListFormId').submit();
		}
	}else if(action=='downloadcertificate')
	{
		document.location.href='<%=response.encodeURL(request.getContextPath()+"/ActiveServlet?action=downloadCertificate&gyus=")%>'+aert +'&iosdp='+usId;
	}else if(action=='downloadAgencyTicket')
	{
		
		document.location.href='<%=response.encodeURL(request.getContextPath()+"/ActiveServlet?action=downloadAgencyTicket&gyus=")%>'+aert +'&iosdp='+usId;
	}
	else if(action=='downloadApplicantTicket')
	{
		
		document.location.href='<%=response.encodeURL(request.getContextPath()+"/ActiveServlet?action=downloadApplicantTicket&gyus=")%>'+aert +'&iosdp='+usId;
	}
	
	else if(action=='endorseapplication')
	{
		var l1 = 'comme' + usId;
		var l1txt = document.getElementById(l1);
		document.getElementById('cmnt').value = encodeURIComponent(l1txt);
		document.getElementById('actId').value = usId;
		document.getElementById('act').value = action;
		document.getElementById('appListFormId').submit();
	}
	else if(action=='disendorseapplication')
	{ 
		var l1 = 'commd' + usId;
		var l1txt = document.getElementById(l1);
		document.getElementById('cmnt').value = encodeURIComponent(l1txt);
		document.getElementById('actId').value = usId;
		document.getElementById('act').value = action;
		document.getElementById('appListFormId').submit();
	}
	
	else
	{
		document.getElementById('actId').value = usId;
		document.getElementById('act').value = action;
		document.getElementById('appListFormId').submit();
	}
}


function toggleViewAW(divId, plus)
{
	if(document.getElementById(divId).style.display=="block")
	{
		document.getElementById(divId).style.display="none";
		document.getElementById(plus).innerHTML = "Click to View/Hide";
	}else {
		document.getElementById(divId).style.display="block";
		document.getElementById(plus).innerHTML = "Click to View/Hide";
	}
}



</script>