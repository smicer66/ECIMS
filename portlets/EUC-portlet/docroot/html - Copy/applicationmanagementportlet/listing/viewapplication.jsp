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
<%@page import="smartpay.entity.ApplicationWorkflow"%>
<%@page import="smartpay.entity.enumerations.KinRelationshipType"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.enumerations.RoleConstants"%>
<%@page import="smartpay.entity.enumerations.ApplicationStatus"%>
<%@page import="smartpay.entity.enumerations.AgencyType"%>
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
<%

ApplicationManagementPortletState portletState = ApplicationManagementPortletState.getInstance(renderRequest, renderResponse);
ApplicationWorkflow app = portletState.getApplicationWorkflow();
Logger log = Logger.getLogger(ApplicationManagementPortletState.class);
String folder = ECIMSConstants.NEW_APPLICATION_DIRECTORY;
Collection<ApplicationItem> appItemList = portletState.getApplicationManagementPortletUtil().getApplicationItemsByApplication(app.getApplication());
Iterator<ApplicationItem> iter1 = appItemList.iterator(); 
ApplicationItem appitem = iter1.next();
String status = "";
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm");

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
	 
	 
  if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
	  status = "Approved awaiting EUC Issuance";
  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
	  status = "Request disapproved";
  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_FORWARDED))
	  status = "Forwarded to " + roleRec;
  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISPUTED))
	  status = "Disputed By " + roleSource;
  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISENDORSED))
	  status = "Disendorsed By " + roleSource;
  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ENDORSED))
	  status = "Endorsed By " + roleSource;
  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CREATED))
	  status = "Pending";
  else if(app.getStatus()!=null && app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ATTACHMENT_DEVALIDATED))
	  status = "Attachment devalidated by " + roleSource;
  else
	  status = "Pending";
 }else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
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
		  status = "Forwarded From NSA";
	  else 
		  status = "N/A";
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



<div class="list-group-item">
	<form  id="appListFormId" action="<%=proceed%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">

                            <div class="row">
                                View Application Details
                            </div>

                            <div class="panel panel-default">
                                <div class="panel-heading">
                                    APPLICANT DETAILS                  
                                </div>
                                <div class="panel-body" id="collapseOne">
                                    <div class="row">
                                        <div class="col-lg-2">
                                            <img id="ContentPlaceHolder1_AppcntImage" class="img-responsive" src="../Upload/Applicant/APPCANT000001/7bd50440-4852-474e-955a-c263c620524f.png">
                                        </div>
                                        <div class="col-lg-5">
											<div class="form-group">
                                                <br>
                                                Applicant-Number
                                                <span id="ApplicantNo" class="form-control"><%=app.getApplication().getApplicant().getApplicantNumber()==null ? "" : app.getApplication().getApplicant().getApplicantNumber()%></span>
                                                
                                            </div>
                                            <div class="form-group">
                                                <br>
                                                Applicant Name
                                                  <span id="Name" class="form-control"><%=app.getApplication().getApplicant().getPortalUser().getFirstName() + " " + app.getApplication().getApplicant().getPortalUser().getSurname()%></span>
                                                
                                            </div>
											<div class="form-group">
                                                <br>
                                                Applicant Type
                                                  <span id="Name" class="form-control"><%=app.getApplication().getApplicant().getApplicantType().getValue()%></span>
                                                
                                            </div>
                                            <div class="form-group">
                                                Applicant Address
                                                 <span id="Address" class="form-control"><%=app.getApplication().getApplicant().getPortalUser().getAddressLine1()!=null ? (app.getApplication().getApplicant().getPortalUser().getAddressLine1() + "<br>" + (app.getApplication().getApplicant().getPortalUser().getAddressLine2()!=null ? app.getApplication().getApplicant().getPortalUser().getAddressLine2() : "")) : "N/A"%></span>
                                                
                                            </div>
                                            <div class="form-group">
                                                Applicant Email Address
                                                 <span id="Email" class="form-control"><%=app.getApplication().getApplicant().getPortalUser().getEmailAddress() %></span>
                                                
                                            </div>
                                        </div>
                                        <div class="col-lg-5">
                                            <div class="form-group">
                                                Applicant Phone Number
                                                 <span id="PhNumber" class="form-control"><%=app.getApplication().getApplicant().getPortalUser().getPhoneNumber() %></span>
                                                
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <!-- .panel-body -->
                                </div>

                            </div>

                            <div class="panel panel-default">
                                <div class="panel-heading">
                                    APPLICATION DETAILS                    
                                </div>
                                <div class="panel-body">
                                	<div class="row">
                                        <div class="col-lg-5">
                                            <div class="form-group">
                                                Application Status:<br>
								<%=status!=null ? status : ""%> 
                                            </div>
                                        </div>

                                        
                                    </div>
                                    
                                    <div class="row">
                                        <div class="col-lg-5">
                                            <div class="form-group">
                                                Category Items fall under:<br>
								<%=appitem.getItemCategorySub()!=null ? appitem.getItemCategorySub().getItemCategory().getItemCategoryName() : ""%> 
                                            </div>
                                            <div class="form-group">
                                                Country of Origin/Manufacture:<br>
                                                <%="Nigeria"%>
                                                <div class="form-group">
                                                    Purpose Of Usage:<br>
                                                    <%=app.getApplication().getPurposeOfUsage()==null ? "" : app.getApplication().getPurposeOfUsage()%>
                                                </div>
                                            </div>
                                        </div>

                                        
                                    </div>
                                    <div class="row">
                                        <div class="col-lg-14">
                                            <br>
                                            Items Description : 
                                        </div>
                                        <div class="row" style="padding: 16px">
                                            <div class="col-lg-6">
                                            	<table width="100%">
                                            		<th>
                                            			<td>HS Code</td>
                                            			<td>Item Description</td>
                                            			<td>Quantity</td>
                                            			<td>Weight</td>
                                            			<td>Amount</td>
                                            			<td>Currency</td>
                                            		</th>
                                            <%
                                            
                                            
                                            for(Iterator<ApplicationItem> iter = appItemList.iterator(); iter.hasNext();)
                                            {
                                            	ApplicationItem applicationItem = iter.next();
                                            	//
                                            %>
                                            	<tr>
                                           			<td><%=applicationItem.getItemCategorySub()!=null ? applicationItem.getItemCategorySub().getName() : "N/A"%></td>
                                           			<td><%=applicationItem.getDescription()==null ? "N/A" : applicationItem.getDescription()%></td>
                                           			<td><%=applicationItem.getQuantity()==null ? "N/A" : applicationItem.getQuantity()%></td>
                                           			<td><%=applicationItem.getWeight()==null ? "N/A" : applicationItem.getWeight()%></td>
                                           			<td><%=applicationItem.getAmount()==null ? "N/A" : applicationItem.getAmount()%></td>
                                           			<td><%=applicationItem.getCurrency()!=null ? applicationItem.getCurrency().getHtmlEntity() : "" %></td>
                                           		</tr>
                                            	
                                            <%
                                            }
                                            %>
                                            	</table>                                                
                                            </div>
                                        </div>
                                    </div>
                                    <!-- .panel-body -->
                                </div>
                            </div>

                            <div class="panel panel-default">
                                <div class="panel-heading">
                                    SUPPORTING DOCUMENTS                    
                                </div>
                                <div class="panel-body">
                                    <div class="row">
                                        <div class="col-lg-5">
                                            <div class="form-group file">
                                                Attachment Type:
                                                        <table id="ContentPlaceHolder1_dlAttachType" cellspacing="0" style="border-collapse:collapse;">
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
                                                                    <span id="ContentPlaceHolder1_dlAttachType_lblAttachType_0"><%=applicationAttachment.getApplicationAttachmentType().getName() %></span>
                                                                    <img src="\resources\images\uploadedfiles\<%=applicationAttachment.getAttachmentFile()%>" height="50px;">
                                                                    <%
                                                                    if(!app.getApplication().getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED) && 
                															!app.getApplication().getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ENDORSED) && 
                															!app.getApplication().getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISENDORSED) && 
                															!app.getApplication().getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
                													{
                                                                    	if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
                                                                    	{
		                                                                    if(applicationAttachment.getIsValid())
		                                                                    {
		                                                                    %>
		                                                                    <button type="button" class="btn btn-danger" onclick="javascript: handleButtonAction('devalidateattachment', '<%=applicationAttachment.getId()%>')">Devalidate Attachment</button>
		                                                                    <%
		                                                                    }else
		                                                                    {
		                                                                    %>
		                                                                    <button type="button" class="btn btn-success" onclick="javascript: handleButtonAction('validateattachment', '<%=applicationAttachment.getId()%>')">Validate Attachment</button>
		                                                                    <%
		                                                                    }
                                                                    	}
                													}
                                                                    %>
                                                                </div>
                                                            
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

							<%
							if(app.getApplication().getImportName()!=null)
							{
							%>
                            <div class="panel panel-default">
                                <div class="panel-heading">
                                    <div class="panel-title">
                                    	<label>Importing for:</label>
                                    </div>
                                </div>
                                <div class="panel-body">
                                    <div class="row">
                                        <div class="col-lg-5" id="divImportingfor" style="display: none;">

                                            <div class="form-group">
                                                Name:<br>
                            					<%=app.getApplication().getImportName()!=null ? app.getApplication().getImportName() : ""%>
                                            </div>
                                            <div class="form-group">
                                                Address:<br>
                                                <%=app.getApplication().getImportAddress()!=null ? app.getApplication().getImportAddress() : ""%>
                                            </div>
                                            <div class="form-group">
                                                Attach Contract Award Letter:
                                                <%
                                                if(app.getApplication().getImportLetter()!=null)
                                                {
                                                %>
                                                	<img src="\resources\images\uploadedfiles\<%=app.getApplication().getImportLetter() %>%>" height="50px;">
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


							
	                            <div class="panel panel-default">
	                                <div class="panel-heading">
	                                	<%
										if(app.getApplication().getImportDuty()!=null)
										{
										%>
	                                    <div class="panel-title">
	                                    	<label>Import Duty Exemption applies to this Application</label>
	                                    </div>
	                                    <%
										}
				                        %>
	                                </div>
	                                <div class="panel-body">
	                                    <div class="row">
		                                    <%
											if(app.getApplication().getImportDuty()!=null)
											{
											%>
	                                        <div class="row">
	                                            <div id="divProof" style="display: none;">
	                                                <div class="col-lg-5">
	                                                    Proof of Title<br>
	                                                    <%=portletState.getTxtProofTitle()!=null ? portletState.getTxtProofTitle() : ""%>
	                                                </div>
	
	                                                <div class="col-lg-12">
	                                                    <br>
	                                                    Attach Proof   
	                                <input type="file" name="FileUploadProofAttachment" id="FileUploadProofAttachment" class="file" data-show-caption="true" data-show-upload="false">
	                                			<%
                                                if(app.getApplication().getProofAttachment()!=null)
                                                {
                                                %>
                                                	<img src="\resources\images\uploadedfiles\<%=app.getApplication().getProofAttachment() %>%>" height="50px;">
                                                	
                                                <%
                                                }
                                                %>
	                                                    <br>
	                                                </div>
	                                            </div>
	                                        </div>
	                                        <%
											}
	                                        %>
	
	                                        <div class="row">
	                                            <div class="col-lg-2">
	                                                Expected Port of Landing:<br> 
	                                            </div>
	                                            <div class="col-lg-3">
	                                                <div class="form-group">
	                                                    <%=app.getApplication().getPortCode()!=null ? app.getApplication().getPortCode().getState().getName() : "N/A"%>
	                                                </div>
	                                            </div>
	                                            <div class="col-lg-3">
	                                                <div class="form-group">
	                                                    Port of Landing Port:<br>
	                                                    <%=app.getApplication().getPortCode()!=null ? app.getApplication().getPortCode().getPortCode() : "N/A"%>
	                                                </div>
	                                            </div>
	                                        </div>
	                                    </div>
	                                </div>
	                            </div>

					<div class="panel panel-default">
                            <div class="panel-heading">
                            	
                                <div class="panel-title">
                                	<label>Comments & Flags<%=portletState.getApplicationFlags()==null ? "" : portletState.getApplicationFlags().size() %></label>
                                </div>
                                
                            </div>
                            <div class="panel-body">
                                <div class="row">
                                
                                    <div class="row">
                                        <div id="divProof" style="display: block;">
                                        	<%
                                        if(portletState.getApplicationFlags()!=null)
                                        {
                                        	for(Iterator<ApplicationFlag> iter = portletState.getApplicationFlags().iterator(); iter.hasNext();)
                                        	{
                                        		ApplicationFlag af = iter.next();
                                        	%>
                                            <div class="col-lg-5">
                                                Date: <br>
                                                <%=af.getDateCreated()!=null ? sdf.format(af.getDateCreated()) : "N/A"%>
                                            </div>

                                            <div class="col-lg-12">
                                                Comment:<br>
                                                <%=af.getComment()!=null ? af.getComment() : "N/A"%>
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
                                    
                                </div>
                            </div>
                        </div>
                        
	                        
	                <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('gobacktoviewlisting', '<%=app.getId()%>')">Go Back</button>        
	                        <%
					if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
			  		{
			  		%>
						<button type="button" class="btn btn-success" onclick="javascript:handleButtonAction('view', '<%=app.getId()%>')">View Application Details</button>
					<%
			  		}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
			  		{
			  			if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CREATED))
			  			{
			  				%>
							<div class="btn-group">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>')">View Applicant Details</a></li>
									<li><a href="javascript: handleButtonAction('forward', '<%=app.getId()%>')">Forward Application To Agency</a></li>
									<li><a href="javascript: handleButtonAction('rejectapplication', '<%=app.getId()%>')">Reject Application</a></li>
									<%
									if(app.getApplication().getApplicant().getExceptionList().equals(Boolean.FALSE))
									{
									%>
									<li><a href="javascript: handleButtonAction('addtoexception', '<%=app.getId()%>')">Add Applicant to Exception List</a></li>
									<%
									}else 
									{
									%>
									<li><a href="javascript: handleButtonAction('removefromexception', '<%=app.getId()%>')">Remove Applicant from Exception List</a></li>
									<%	
									}
									if(app.getApplication().getApplicant().getExceptionList().equals(Boolean.FALSE))
									{
									%>
									<li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>')">BlackList Applicant</a></li>
									<%
									}else
									{
									%>
									<li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>')">Remove Applicant from BlackList</a></li>
									<%
									}
									%>
							  	</ul>
							</div>
						<%
			  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ATTACHMENT_DEVALIDATED))
			  			{
			  				%>
							<div class="btn-group">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>')">View Applicant Details</a></li>
									<li><a href="javascript: handleButtonAction('forward', '<%=app.getId()%>')">Forward Application To Agency</a></li>
									<li><a href="javascript: handleButtonAction('rejectapplication', '<%=app.getId()%>')">Reject Application</a></li>
									<%
									if(app.getApplication().getApplicant().getExceptionList().equals(Boolean.FALSE))
									{
									%>
									<li><a href="javascript: handleButtonAction('addtoexception', '<%=app.getId()%>')">Add Applicant to Exception List</a></li>
									<%
									}else 
									{
									%>
									<li><a href="javascript: handleButtonAction('removefromexception', '<%=app.getId()%>')">Remove Applicant from Exception List</a></li>
									<%	
									}
									if(app.getApplication().getApplicant().getExceptionList().equals(Boolean.FALSE))
									{
									%>
									<li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>')">BlackList Applicant</a></li>
									<%
									}else
									{
									%>
									<li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>')">Remove Applicant from BlackList</a></li>
									<%
									}
									%>
							  	</ul>
							</div>
							<%	
			  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED))
			  			{
			  				%>
							<div class="btn-group">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>')">View Applicant Details</a></li>
									<%
									if(app.getApplication().getApplicant().getExceptionList().equals(Boolean.FALSE))
									{
									%>
									<li><a href="javascript: handleButtonAction('addtoexception', '<%=app.getId()%>')">Add Applicant to Exception List</a></li>
									<%
									}else 
									{
									%>
									<li><a href="javascript: handleButtonAction('removefromexception', '<%=app.getId()%>')">Remove Applicant from Exception List</a></li>
									<%	
									}
									if(app.getApplication().getApplicant().getExceptionList().equals(Boolean.FALSE))
									{
									%>
									<li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>')">BlackList Applicant</a></li>
									<%
									}else
									{
									%>
									<li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>')">Remove Applicant from BlackList</a></li>
									<%
									}
									%>
							  	</ul>
							</div>
							<%	
			  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISENDORSED))
			  			{
			  				%>
							<div class="btn-group">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>')">View Applicant Details</a></li>
									<li><a href="javascript: handleButtonAction('forward', '<%=app.getId()%>')">Forward Application</a></li>
									<li><a href="javascript: handleButtonAction('approve', '<%=app.getId()%>')">Approve Application</a></li>
									<li><a href="javascript: handleButtonAction('reject', '<%=app.getId()%>')">Reject Application</a></li>
									<%
									if(app.getApplication().getApplicant().getExceptionList().equals(Boolean.FALSE))
									{
									%>
									<li><a href="javascript: handleButtonAction('addtoexception', '<%=app.getId()%>')">Add Applicant to Exception List</a></li>
									<%
									}else 
									{
									%>
									<li><a href="javascript: handleButtonAction('removefromexception', '<%=app.getId()%>')">Remove Applicant from Exception List</a></li>
									<%	
									}
									if(app.getApplication().getApplicant().getExceptionList().equals(Boolean.FALSE))
									{
									%>
									<li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>')">BlackList Applicant</a></li>
									<%
									}else
									{
									%>
									<li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>')">Remove Applicant from BlackList</a></li>
									<%
									}
									%>
							  	</ul>
							</div>
						<%
			  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISPUTED))
			  			{
			  				%>
							<div class="btn-group">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>')">View Applicant Details</a></li>
									<li><a href="javascript: handleButtonAction('forward', '<%=app.getId()%>')">Forward Application</a></li>
									<li><a href="javascript: handleButtonAction('approve', '<%=app.getId()%>')">Approve Application</a></li>
									<li><a href="javascript: handleButtonAction('reject', '<%=app.getId()%>')">Reject Application</a></li>
									<%
									if(app.getApplication().getApplicant().getExceptionList().equals(Boolean.FALSE))
									{
									%>
									<li><a href="javascript: handleButtonAction('addtoexception', '<%=app.getId()%>')">Add Applicant to Exception List</a></li>
									<%
									}
									if(app.getApplication().getApplicant().getExceptionList().equals(Boolean.FALSE))
									{
									%>
									<li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>')">BlackList Applicant</a></li>
									<%
									}else
									{
									%>
									<li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>')">Remove Applicant from BlackList</a></li>
									<%
									}
									%>
							  	</ul>
							</div>
						<%
			  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ENDORSED))
			  			{
			  				%>
							<div class="btn-group">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>')">View Applicant Details</a></li>
									<li><a href="javascript: handleButtonAction('forward', '<%=app.getId()%>')">Forward Application</a></li>
									<li><a href="javascript: handleButtonAction('approveone', '<%=app.getId()%>')">Approve Application</a></li>
									<li><a href="javascript: handleButtonAction('rejectone', '<%=app.getId()%>')">Reject Application</a></li>
									<%
									if(app.getApplication().getApplicant().getExceptionList().equals(Boolean.FALSE))
									{
									%>
									<li><a href="javascript: handleButtonAction('addtoexception', '<%=app.getId()%>')">Add Applicant to Exception List</a></li>
									<%
									}else 
									{
									%>
									<li><a href="javascript: handleButtonAction('removefromexception', '<%=app.getId()%>')">Remove Applicant from Exception List</a></li>
									<%	
									}
									if(app.getApplication().getApplicant().getExceptionList().equals(Boolean.FALSE))
									{
									%>
									<li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>')">BlackList Applicant</a></li>
									<%
									}else
									{
									%>
									<li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>')">Remove Applicant from BlackList</a></li>
									<%
									}
									%>
							  	</ul>
							</div>
						<%
			  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_FORWARDED))
			  			{
			  				%>
							<div class="btn-group">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>')">View Applicant Details</a></li>
									<li><a href="javascript: handleButtonAction('approve', '<%=app.getId()%>')">Approve Application</a></li>
									<li><a href="javascript: handleButtonAction('reject', '<%=app.getId()%>')">Reject Application</a></li>
									<%
									if(app.getApplication().getApplicant().getExceptionList().equals(Boolean.FALSE))
									{
									%>
									<li><a href="javascript: handleButtonAction('addtoexception', '<%=app.getId()%>')">Add Applicant to Exception List</a></li>
									<%
									}else 
									{
									%>
									<li><a href="javascript: handleButtonAction('removefromexception', '<%=app.getId()%>')">Remove Applicant from Exception List</a></li>
									<%	
									}
									if(app.getApplication().getApplicant().getExceptionList().equals(Boolean.FALSE))
									{
									%>
									<li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>')">BlackList Applicant</a></li>
									<%
									}else
									{
									%>
									<li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>')">Remove Applicant from BlackList</a></li>
									<%
									}
									%>
							  	</ul>
							</div>
						<%
			  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
			  			{
			  				%>
							<div class="btn-group">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>')">View Applicant Details</a></li>
									<%
									if(app.getApplication().getApplicant().getExceptionList().equals(Boolean.FALSE))
									{
									%>
									<li><a href="javascript: handleButtonAction('addtoexception', '<%=app.getId()%>')">Add Applicant to Exception List</a></li>
									<%
									}else 
									{
									%>
									<li><a href="javascript: handleButtonAction('removefromexception', '<%=app.getId()%>')">Remove Applicant from Exception List</a></li>
									<%	
									}
									if(app.getApplication().getApplicant().getExceptionList().equals(Boolean.FALSE))
									{
									%>
									<li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>')">BlackList Applicant</a></li>
									<%
									}else
									{
									%>
									<li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>')">Remove Applicant from BlackList</a></li>
									<%
									}
									%>
							  	</ul>
							</div>
						<%
			  			}
			  		}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
			  		{
			  			if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ATTACHMENT_DEVALIDATED))
			  			{
			  				%>
							<div class="btn-group">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
							  		
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>')">View Applicant Details</a></li>
									<%
									if(portletState.getPortalUser().getAgency()!=null && !portletState.getPortalUser().getAgency().getAgencyType().equals(AgencyType.INFORMATION_GROUP))
									{
									%>
									<li><a href="javascript: handleButtonAction('disendorseapplication', '<%=app.getId()%>')">Disendorse Application</a></li>
									<li><a href="javascript: handleButtonAction('flagapplication', '<%=app.getId()%>')">Flag Application</a></li>
									<%
									}
									%>
							  	</ul>
							</div>
							<%	
			  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISENDORSED))
			  			{
			  				%>
							<div class="btn-group">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>')">View Applicant Details</a></li>
									<%
									if(portletState.getPortalUser().getAgency()!=null && !portletState.getPortalUser().getAgency().getAgencyType().equals(AgencyType.INFORMATION_GROUP))
									{
									%>
									<li><a href="javascript: handleButtonAction('flagapplication', '<%=app.getId()%>')">Flag Application</a></li>
									<%
									}
									%>
							  	</ul>
							</div>
						<%
			  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISPUTED))
			  			{
			  				%>
							<div class="btn-group">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>')">View Applicant Details</a></li>
									<%
									if(portletState.getPortalUser().getAgency()!=null && !portletState.getPortalUser().getAgency().getAgencyType().equals(AgencyType.INFORMATION_GROUP))
									{
									%>
									<li><a href="javascript: handleButtonAction('flagapplication', '<%=app.getId()%>')">Flag Application</a></li>
									<li><a href="javascript: handleButtonAction('disendorseapplication', '<%=app.getId()%>')">Disendorse Application</a></li>
									<%
									}
									%>
							  	</ul>
							</div>
						<%
			  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ENDORSED))
			  			{
			  				%>
							<div class="btn-group">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>')">View Applicant Details</a></li>
									<%
									if(portletState.getPortalUser().getAgency()!=null && !portletState.getPortalUser().getAgency().getAgencyType().equals(AgencyType.INFORMATION_GROUP))
									{
									%>
									<!-- <li><a href="javascript: handleButtonAction('flagapplication', '<%=app.getId()%>')">Flag Application</a></li>
									<li><a href="javascript: handleButtonAction('disputeAplication', '<%=app.getId()%>')">Dispute Application</a></li>-->
									<%
									}
									%>
							  	</ul>
							</div>
						<%
			  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_FORWARDED))
			  			{
			  				%>
							<div class="btn-group">
								<button type="button" class="btn btn-success">Click Here for Actions</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
							  		<li><a href="javascript: handleButtonAction('viewapplicant', '<%=app.getId()%>')">View Applicant Details</a></li>
							  		
									<%
									
										if(portletState.getPortalUser().getAgency()!=null && !portletState.getPortalUser().getAgency().getAgencyType().equals(AgencyType.INFORMATION_GROUP))
										{
											if(portletState.isAttachsValid()==true)
											{
										%>
										<li><a href="javascript:
										 handleButtonAction('endorseapplication', '<%=app.getId()%>')">Endorse Application</a></li>
											<%
											}
											%>
										<li><a href="javascript: handleButtonAction('disendorseapplication', '<%=app.getId()%>')">Disendorse Application</a></li>
										<li><a href="javascript: handleButtonAction('disputeAplication', '<%=app.getId()%>')">Dispute Application</a></li>
										<li><a href="javascript: handleButtonAction('flagapplication', '<%=app.getId()%>')">Flag Application</a></li>
										<%
										}
									%>
							  	</ul>
							</div>
						<%
			  			}
			  		}
					%>
					
					

                    
					
                  <input type="hidden" name="actId" id="actId" value="">
                  <input type="hidden" name="act" id="act" value="">
                            
	</form>
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


function handleButtonAction(action, usId){
	
	if(action=='rejectbynsauser')
	{
		if(confirm("Are you sure you want to reject this EUC application request? You will not be able to undo this action after rejecting this request!"))
		{
			document.getElementById('actId').value = usId;
			document.getElementById('act').value = action;
			document.getElementById('appListFormId').submit();
		}
	}else
	{
		document.getElementById('actId').value = usId;
		document.getElementById('act').value = action;
		document.getElementById('appListFormId').submit();
	}
}
</script>