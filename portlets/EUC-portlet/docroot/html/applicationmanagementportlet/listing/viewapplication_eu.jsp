
<%@page import="smartpay.entity.enumerations.ApplicantType"%>
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
Application app = portletState.getApplication();
Logger log = Logger.getLogger(ApplicationManagementPortletState.class);
//String folder = ECIMSConstants.NEW_APPLICATION_DIRECTORY;
Collection<ApplicationItem> appItemList = portletState.getApplicationManagementPortletUtil().getApplicationItemsByApplication(app);
ApplicationItem appitem = null;
if(appItemList.size()>0){
Iterator<ApplicationItem> iter1 = appItemList.iterator(); 
	appitem = iter1.next();
}
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
 }


if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
{
%>
<jsp:include page="/html/applicationmanagementportlet/tabs_eu.jsp" flush="" />
<%
}
%>

<portlet:actionURL var="proceed" name="processAction">
	<portlet:param name="action"
		value="<%=APPLIST_ACTIONS.ACT_ON_APPLICATION.name()%>" />
</portlet:actionURL>


<div style="padding-left:10px; padding-right:10px; width:900px" id="print-content">
	<h2>Application Details</h2>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">View Application Details</span></div>
	  <div class="panel-body">
				<form  id="appListFormId" action="<%=proceed%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
			
										<%
										if(app.getApplicant().getPortalUser().getCompany()==null)
										{
										%>
			                            <div class="panel panel-primary">
			                                <div class="panel-heading">
			                                    APPLICANT DETAILS                  
			                                </div>
			                                <div class="panel-body" id="collapseOne">
			                                    <div class="row" style="padding-left:10px;">
			                                    	<div class="col-lg-9">
														<div class="form-group">
			                                                <br>
			                                                <strong>Application Number</strong>
			                                                <div><%=app.getApplicationNumber()==null ? "" : app.getApplicationNumber()%></div>
			                                                
			                                            </div>
														<div class="form-group">
			                                                <br>
			                                                <strong>Applicant-Number</strong>
			                                                <div><%=app.getApplicant().getApplicantNumber()==null ? "" : app.getApplicant().getApplicantNumber()%></div>
			                                                
			                                            </div>
			                                            <div class="form-group">
			                                                <br>
			                                                <strong>Applicant Name</strong>
			                                                  <div><%=app.getApplicant().getPortalUser().getFirstName() + " " + app.getApplicant().getPortalUser().getSurname()%></div>
			                                                
			                                            </div>
														<div class="form-group">
			                                                <br>
			                                                <strong>Applicant Type</strong>
			                                                  <div><%=app.getApplicant().getApplicantType().getValue()%></div>
			                                                
			                                            </div>
			                                            <% 
                                            if(app.getApplicant().getApplicantType().getValue().equals(ApplicantType.APPLICANT_TYPE_INDIVIDUAL.getValue()))
                                            {
                                            %>
			                                            <div class="form-group">
			                                                <strong>Applicant Address</strong>
			                                                 <div><%=app.getApplicant().getPortalUser().getAddressLine1()!=null ? (app.getApplicant().getPortalUser().getAddressLine1() + "<br>" + (app.getApplicant().getPortalUser().getAddressLine2()!=null ? app.getApplicant().getPortalUser().getAddressLine2() : "")) : "N/A"%></div>
			                                         	<%
                                            }
			                                         	%>
			                                            </div>
			                                            <div class="form-group">
			                                                <strong>Applicant Email Address</strong>
			                                                 <div><%=app.getApplicant().getPortalUser().getEmailAddress() %></div>
			                                                
			                                            </div>
			                                            
			                                            <div class="form-group">
			                                                <strong>Applicant Phone Number</strong>
			                                                 <div><%=app.getApplicant().getPortalUser().getPhoneNumber() %></div>
			                                                
			                                            </div>
			                                            <%
			                                            if(portletState.getPortalUser()!=null && !portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_END_USER))
			                                            {
			                                            %>
			                                            <div class="form-group">
			                                                <button type="button" class="btn btn-primary" onclick="javascript:handleButtonAction('viewapplicant', '<%=app.getId()%>', '')">More Applicant Details</button>
			                                            </div>
			                                            <%
			                                            }
			                                            %>
			                                        </div>
			                                        
			                                        <div class="col-lg-2">
			                                            <img style="width:100px;" src="/resources/images/uploadedfiles/<%= (app.getApplicant().getPortalUser().getPassportPhoto()==null ? "" : app.getApplicant().getPortalUser().getPassportPhoto())%>">
			                                            <div style="font-size:11px"><strong>Applicants passport photo</strong></div>
			                                        </div>
			                                        
			                                            
			                                    </div>
			                                    
			                                    <!-- .panel-body -->
			                                </div>
			
			                            </div>
			                            <%
										}else
										{
										%>
										<div class="panel-heading">
	                                        APPLICANT DETAILS                  
	                                    </div>
	                                    <div class="panel-body" id="collapseOne">
	                                        <div class="row">
	
	                                            <div class="col-lg-5">
	    										  	<div class="form-group">
	                                                    <br>
	                                                    <strong>Applicant-Number</strong>                                            
	                                                    <div><%=app.getApplicant().getApplicantNumber()%></div>                                            
	                                                </div>
	                                                <div class="form-group">
	                                                    <br>
	                                                    <strong>Company Name</strong>
	                                                      <div><%=app.getApplicant().getPortalUser().getCompany().getName()%></div>
	                                                    
	                                                </div>
	    											<div class="form-group">
	                                                    <br>
	                                                    <strong>Company Address</strong>
	                                                      <div><%=app.getApplicant().getPortalUser().getCompany().getAddress()%></div>
	                                                    
	                                                </div>
	                                                <div class="form-group">
	                                                    <strong>Company Phone Number</strong>
	                                                     <div><%=app.getApplicant().getPortalUser().getCompany().getPhoneNumber() %></div>
	                                                    
	                                                </div>
	                                                <div class="form-group">
	                                                    <strong>Company Email Address</strong>
	                                                     <div><%=app.getApplicant().getPortalUser().getCompany().getEmailAddress() %></div>
	                                                    
	                                                </div>
	                                                
	                                                <div class="form-group">
	                                                    <strong>Company Registration Number</strong>
	                                                     <div><%=app.getApplicant().getPortalUser().getCompany().getRegNo() %></div>
	                                                    
	                                                </div>
	                                                <div class="form-group">
	                                                    <strong>Company Website</strong>
	                                                     <div><%=app.getApplicant().getPortalUser().getCompany().getWebsite() %></div>
	                                                    
	                                                </div>
	                                            </div>
	                                            
                                                <div class="col-lg-5">
                                               		<img style="width:100px;" src="<%="/resources/images/uploadedfiles/" + (app.getApplicant().getPortalUser().getCompany().getLogo()==null ? "" : app.getApplicant().getPortalUser().getCompany().getLogo())%>"><br>
                                               		<div style="font-size:10px">Applicants' Company Logo</div>
                                           		</div>
	                                                
	                                        </div>
	                                        
	                                        <!-- .panel-body -->
	                                    </div>
										<%
										}
			                            %>
			                            
			                            
			                            
			                            <div class="panel panel-primary">
			                                <div class="panel-heading">
			                                    APPLICATION DETAILS                    
			                                </div>
			                                <div class="panel-body">
			                                	<div class="row">
			                                        <div class="col-lg-5">
			                                            <div class="form-group">
			                                                <div style="font-weight:bold;">Application Status:<br>
											<span style=" color:red"><%=status!=null ? status : ""%></span></div> 
			                                            </div>
			                                        </div>
			
			                                        
			                                    </div>
			                                    
			                                    <div class="row">
			                                        <div class="col-lg-5">
			                                            <div class="form-group">
			                                                <strong>Category Items fall under:<br></strong>
											<%=(appitem!=null && appitem.getItemCategorySub()!=null) ? appitem.getItemCategorySub().getItemCategory().getItemCategoryName() : ""%> 
			                                            </div>
			                                            <div class="form-group">
			                                                <strong>Country of Origin/Manufacture:<br></strong>
			                                                <%="Nigeria"%>
			                                            </div>
			                                            <div class="form-group">
			                                                <strong>Purpose Of Usage:<br></strong>
			                                                <%=app.getPurposeOfUsage()==null ? "N/A" : app.getPurposeOfUsage()%>
			                                            </div>
			                                            <div class="form-group">
			                                                 <strong>Port of Landing:<br></strong>
			                                                 <%=portletState.getPortCodeEntity()==null ? "N/A" : portletState.getPortCodeEntity().getPortCode()%>
			                                            </div>
			                                        </div>
			                                    </div>
			                                    
			                                    
			                                    <%
										if(app.getImportName()!=null)
										{
										%>
			                            <div class="panel panel-default">
			                                <div class="panel-heading">
			                                    <div class="panel-title">
			                                    	<label><strong>Details on Organization you are importing for:</strong></label>
			                                    </div>
			                                </div>
			                                <div class="panel-body">
			                                    <div class="row" style="padding-left:10px;">
			                                        <div class="col-lg-5" id="divImportingfor" style="display: block;">
			
			                                            <div class="form-group">
			                                                <strong>Name:<br></strong>
			                            					<%=app.getImportName()!=null ? app.getImportName() : ""%>
			                                            </div>
			                                            <div class="form-group">
			                                                <strong>Address:<br></strong>
			                                                <%=app.getImportAddress()!=null ? app.getImportAddress() : ""%>
			                                            </div>
			                                            <div class="form-group">
			                                                <strong>Attached Contract Award Letter:</strong><br>
			                                                <%
			                                                if(app.getImportLetter()!=null)
			                                                {
			                                                %>
			                                                	<a target="_blank" href="/resources/images/uploadedfiles/<%=app.getImportLetter() %>">Download/View Document</a>
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
			                                    <div class="panel-title">
			                                    	<label><strong>Items Imported:</strong></label>
			                                    </div>
			                                </div>
			                                <div class="panel-body">
			                                    <div class="row">
			                                        <div class="row" style="padding: 10px">
			                                            <div class="col-lg-12">
			                                            	<table width="100%" class="table table-hover" id="btable">
			                                            		<thead>
			                                            			<th>S/No</th>
			                                            			<th>HS Code</th>
			                                            			<th>Item Description</th>
			                                            			<th>Quantity/Weight</th>
			                                            			<th>Unit Cost</td>
			                                            		</thead>
			                                            <%
			                                            
			                                            Double totalAmount = 0.00;
			                                            int sn= 1;
			                                            String currency = "";
			                                            
			                                            if(appItemList!=null && appItemList.size()>0)
			                                            {
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
			                                            }
			                                            %>
			                                            <tr>
			                                        			<td colspan="4" style="font-weight:bold">Total Cost</td>
			                                        			<td><%=currency + "" + new Util().roundUpAmount(totalAmount) %></td>
			                                        		</tr>
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
			                                    <div class="row" style="padding-left:10px">
			                                        <div class="col-lg-5">
			                                            <div class="form-group file">
			                                                Attachment Type:
			                                                        <table id="ContentPlaceHolder1_dlAttachType" cellspacing="0" style="border-collapse:collapse;">
				<tbody><tr>
																		<td class="table table-bordered no-more-tables">
																	<%
																Collection<ApplicationAttachment> attachListing = portletState.getApplicationManagementPortletUtil().getApplicationAttachmentListByApplication(app);
																	
																
																if(attachListing!=null && attachListing.size()>0)
																{
																	for(Iterator<ApplicationAttachment> iter= attachListing.iterator(); iter.hasNext();)
																	{
																		ApplicationAttachment applicationAttachment = iter.next();
																		%>
			                                                                <div style="padding: 10px">
			                                                                    <span id="ContentPlaceHolder1_dlAttachType_lblAttachType_0"><%=applicationAttachment.getApplicationAttachmentType().getName() %></span>
			                                                                    <a href="/resources/images/uploadedfiles/<%=applicationAttachment.getAttachmentFile() %>">Download/View Document</a>
			                                                                    <%
			                                                                    if(!app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED) && 
			                															!app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ENDORSED) && 
			                															!app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISENDORSED) && 
			                															!app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
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
			
										
			
			
										
			<!-- 	                            <div class="panel panel-default">
				                                <div class="panel-heading">
				                                	<%
													if(app.getImportDuty()!=null)
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
														if(app.getImportDuty()!=null)
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
			                                                if(app.getProofAttachment()!=null)
			                                                {
			                                                %>
			                                                	<img src="\resources\images\uploadedfiles\<%=app.getProofAttachment() %>%>" height="50px;">
			                                                	
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
				                                                    <%=app.getPortCode()!=null ? app.getPortCode().getState().getName() : "N/A"%>
				                                                </div>
				                                            </div>
				                                            <div class="col-lg-3">
				                                                <div class="form-group">
				                                                    Port of Landing Port:<br>
				                                                    <%=app.getPortCode()!=null ? app.getPortCode().getPortCode() : "N/A"%>
				                                                </div>
				                                            </div>
				                                        </div>
				                                    </div>
				                                </div>
				                            </div>
			-->
			                        
				                        
				                <button type="button" class="btn btn-danger" onclick="javascript:handleButtonAction('gobacktoviewlisting', '<%=app.getId()%>')">Go Back</button>        
				                        <%
								if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
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
												if(app.getApplicant().getExceptionList().equals(Boolean.FALSE))
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
												if(app.getApplicant().getExceptionList().equals(Boolean.FALSE))
												{
												%>
												<!-- <li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>')">BlackList Applicant</a></li>-->
												<%
												}else
												{
												%>
												<!-- <li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>')">Remove Applicant from BlackList</a></li>-->
												<%
												}
												%>
										  	</ul>
										</div>
									<%
						  			}else if(app.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
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
												<li><a href="javascript: handleButtonAction('printcertificate', '<%=app.getId()%>')">View Applicant Details</a></li>
												
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
												if(app.getApplicant().getExceptionList().equals(Boolean.FALSE))
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
												if(app.getApplicant().getExceptionList().equals(Boolean.FALSE))
												{
												%>
												<!-- <li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>')">BlackList Applicant</a></li>-->
												<%
												}else
												{
												%>
												<!-- <li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>')">Remove Applicant from BlackList</a></li>-->
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
												if(app.getApplicant().getExceptionList().equals(Boolean.FALSE))
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
												if(app.getApplicant().getExceptionList().equals(Boolean.FALSE))
												{
												%>
												<!-- <li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>')">BlackList Applicant</a></li>-->
												<%
												}else
												{
												%>
												<!-- <li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>')">Remove Applicant from BlackList</a></li>-->
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
												if(app.getApplicant().getExceptionList().equals(Boolean.FALSE))
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
												if(app.getApplicant().getExceptionList().equals(Boolean.FALSE))
												{
												%>
												<!-- <li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>')">BlackList Applicant</a></li>-->
												<%
												}else
												{
												%>
												<!--<li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>')">Remove Applicant from BlackList</a></li>-->
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
												if(app.getApplicant().getExceptionList().equals(Boolean.FALSE))
												{
												%>
												<li><a href="javascript: handleButtonAction('addtoexception', '<%=app.getId()%>')">Add Applicant to Exception List</a></li>
												<%
												}
												if(app.getApplicant().getExceptionList().equals(Boolean.FALSE))
												{
												%>
												<!-- <li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>')">BlackList Applicant</a></li>-->
												<%
												}else
												{
												%>
												<!--<li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>')">Remove Applicant from BlackList</a></li>-->
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
												if(app.getApplicant().getExceptionList().equals(Boolean.FALSE))
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
												if(app.getApplicant().getExceptionList().equals(Boolean.FALSE))
												{
												%>
												<!-- <li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>')">BlackList Applicant</a></li>-->
												<%
												}else
												{
												%>
												<!--<li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>')">Remove Applicant from BlackList</a></li>-->
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
												if(app.getApplicant().getExceptionList().equals(Boolean.FALSE))
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
												if(app.getApplicant().getExceptionList().equals(Boolean.FALSE))
												{
												%>
												<!-- <li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>')">BlackList Applicant</a></li>-->
												<%
												}else
												{
												%>
												<!-- <li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>')">Remove Applicant from BlackList</a></li>-->
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
												if(app.getApplicant().getExceptionList().equals(Boolean.FALSE))
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
												if(app.getApplicant().getExceptionList().equals(Boolean.FALSE))
												{
												%>
												<!-- <li><a href="javascript: handleButtonAction('blacklistapplicant', '<%=app.getId()%>')">BlackList Applicant</a></li>-->
												<%
												}else
												{
												%>
												<!-- <li><a href="javascript: handleButtonAction('unblacklistapplicant', '<%=app.getId()%>')">Remove Applicant from BlackList</a></li>-->
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
	</div>
</div>
<input type="button" onclick="printDiv('print-content')" value="Print Page"/>






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