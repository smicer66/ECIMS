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
<%@page import="smartpay.entity.enumerations.KinRelationshipType"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.enumerations.RoleConstants"%>
<%@page import="com.ecims.commins.ECIMSConstants"%>
<%@page import="smartpay.entity.RoleType"%>
<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.entity.ApplicationAttachment"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Arrays" %>
<%@page import="java.util.List" %>
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
<script src="<%=resourceBaseURL %>/js/jquery-1.11.1.min.js"></script>
<script src="<%= resourceBaseURL %>/js/app.v1.js"></script>
<!-- parsley -->
<script src="<%= resourceBaseURL %>/js/parsley.min.js"></script>
<script src="<%= resourceBaseURL %>/js/parsley.extend.js"></script>
<script src="<%= resourceBaseURL %>/js/app.plugin.js"></script>


<%

ApplicationManagementPortletState portletState = ApplicationManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ApplicationManagementPortletState.class);
//String folder = "/resources/images";
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

<portlet:actionURL var="proceed" name="processAction">
	<portlet:param name="action"
		value="<%=EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_FOUR.name()%>" />
</portlet:actionURL>



<section class="container" id="print-content">
<div style="padding-left:10px; padding-right:10px;" class="col-sm-9 panel panel-default" role="news">
	<h4>Create An EUC Application Request</h4>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Step 4 of 4: Preview Application Details Before Submitting</span></div>
	  <div class="panel-body">
	<form  id="startRegFormId" action="<%=proceed%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">

	    <h4>Please preview your information before submission</h4>
                            <div class="panel panel-primary">
                            
                            	<%
                            	if(portletState.getPortalUser().getAgency()==null)
                            	{
                            		if(portletState.getPortalUser().getCompany()==null)
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
                                                <div><%=portletState.getApplicant().getApplicantNumber()%></div>                                            
                                            </div>
                                            <div class="form-group">
                                                <br>
                                                <strong>Applicant Name</strong>
                                                  <div><%=portletState.getApplicant().getPortalUser().getFirstName() + " " + portletState.getApplicant().getPortalUser().getSurname()%></div>
                                                
                                            </div>
											<div class="form-group">
                                                <br>
                                                <strong>Applicant Type</strong>
                                                  <div><%=portletState.getApplicant().getApplicantType().getValue()%></div>
                                                
                                            </div>
                                            
                                            <%
                                            if(portletState.getApplicant().getApplicantType().getValue().equals(ApplicantType.APPLICANT_TYPE_INDIVIDUAL.getValue()))
                                            {
                                            %>
                                            <div class="form-group">
                                                <strong>Applicant Address</strong>
                                                 <div><%=(portletState.getApplicant().getPortalUser().getAddressLine1()==null ? "N/A" : portletState.getApplicant().getPortalUser().getAddressLine1())%>
                                                 <%=(portletState.getApplicant().getPortalUser().getAddressLine2()==null ? "" : ("<br>" + portletState.getApplicant().getPortalUser().getAddressLine2()))%></div>
                                                
                                            </div>
                                            <%
                                            }
                                            %>
                                            <div class="form-group">
                                                <strong>Applicant Email Address</strong>
                                                 <div><%=portletState.getApplicant().getPortalUser().getEmailAddress() %></div>
                                                
                                            </div>
                                            <div class="form-group">
                                                <strong>Applicant Phone Number</strong>
                                                 <div><%=portletState.getApplicant().getPortalUser().getPhoneNumber() %></div>
                                                
                                            </div>
                                        </div>
                                        <div class="col-lg-5">
                                            
                                            <img style="width:100px;" src="<%="/resources/images/uploadedfiles/" + (portletState.getApplicant().getPortalUser().getPassportPhoto()==null ? "" : portletState.getApplicant().getPortalUser().getPassportPhoto())%>"><br>
                                            <a target="_blank" href="<%="/resources/images/uploadedfiles/" + (portletState.getApplicant().getPortalUser().getPassportPhoto()==null ? "" : portletState.getApplicant().getPortalUser().getPassportPhoto())%>">
                                            Click to View Larger Image</a>
                                            
                                            <%
                                            if(portletState.getPortalUser()!=null && portletState.getPortalUser().getId().equals(portletState.getApplicant().getPortalUser().getId()))
                                            {
                                            %>
                                            	<div style="font-size:10px">Your Passport</div>
                                            <%
                                            }else if(portletState.getPortalUser().getId().equals(portletState.getApplicant().getPortalUser().getId()))
                                            {
                                            %>
                                            	<div style="font-size:10px">Applicants' Passport</div>
                                            <%
                                            }
                                            %>
                                        </div>
                                    </div>
                                    
                                    <!-- .panel-body -->
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
		                                                <strong>Company Name</strong>                                            
		                                                <div><%=portletState.getApplicant().getPortalUser().getCompany().getName()%></div>                                            
		                                            </div>
													<%
													if(!portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER))
													{
													%>
		                                            <div class="form-group">
		                                                <br>
		                                                <strong>Company Certificate Number</strong>
		                                                  <div><%=portletState.getApplicant().getPortalUser().getCompany().getRegNo()%></div>
		                                                
		                                            </div>
													<div class="form-group">
		                                                <br>
		                                                <strong>Company Contact Number</strong>
		                                                  <div><%=portletState.getApplicant().getPortalUser().getCompany().getPhoneNumber()%></div>
		                                                
		                                            </div>
		                                            
		                                            
													<div class="form-group">
		                                                <br>
		                                                <strong>Company Contact Email Address</strong>
		                                                  <div><%=portletState.getApplicant().getPortalUser().getCompany().getEmailAddress()%></div>
		                                                
		                                            </div>
		                                            
		                                            
													<div class="form-group">
		                                                <br>
		                                                <strong>Company Address</strong>
		                                                  <div><%=portletState.getApplicant().getPortalUser().getCompany().getAddress()%></div>
		                                                
		                                            </div>
			                                        <div class="col-lg-5">
			                                            <img style="width:100px;" src="<%="/resources/images/uploadedfiles/" + (portletState.getApplicant().getPortalUser().getCompany().getLogo()==null ? "" : portletState.getApplicant().getPortalUser().getCompany().getLogo())%>"><br>
			                                            <div style="font-size:10px">Company Logo</div>
			                                            <a target="_blank" href="<%="/resources/images/uploadedfiles/" + (portletState.getApplicant().getPortalUser().getCompany().getLogo()==null ? "" : portletState.getApplicant().getPortalUser().getCompany().getLogo())%>">
			                                            View Company Logo in large size</a>
			                                        </div>
													<%
													}
													%>
			                                	</div>
		                                    </div>
	                                        
	                                        <!-- .panel-body -->
	                                    </div>
                            			<%
                            		}
                            	}else
                            	{
								%>
								<div class="panel-heading">
                                    AGENCY DETAILS                  
                                </div>
                                <div class="panel-body" id="collapseOne">
                                    <div class="row">

                                        <div class="col-lg-5">
                                            <div class="form-group">
                                                <br>
                                                <strong>Agency Name</strong>
                                                  <div><%=portletState.getAgencyApplicant().getAgency().getAgencyName()%></div>
                                                
                                            </div>
											<div class="form-group">
                                                <br>
                                                <strong>Agency Type</strong>
                                                  <div><%=portletState.getAgencyApplicant().getAgency().getAgencyType().getValue()%></div>
                                                
                                            </div>
                                            
                                           
                                            <div class="form-group">
                                                <strong>Applicant Name:</strong>
                                                 <div><%=portletState.getAgencyApplicant().getFirstName() + " " + portletState.getAgencyApplicant().getSurname()%></div>
                                                
                                            </div>
                                            <div class="form-group">
                                                <strong>Applicant Phone Number:</strong>
                                                 <div><%=portletState.getAgencyApplicant().getPhoneNumber()%></div>
                                                
                                            </div>
                                            <div class="form-group">
                                                <strong>Applicant Email Address:</strong>
                                                 <div><%=portletState.getAgencyApplicant().getEmailAddress()%></div>
                                                
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <!-- .panel-body -->
                                </div>
								
								<%
                            	}
								%>
                            </div>

                            <div class="panel panel-primary">
                                <div class="panel-heading">
                                    APPLICATION DETAILS                    
                                </div>
                                <div class="panel-body">
                                            <div class="form-group">
                                                <strong>Item falls under this Category:<br></strong>
								<%=portletState.getItemCategoryEntity()!=null ? portletState.getItemCategoryEntity().getItemCategoryName() : ""%> 
                                            </div>
                                            <div class="form-group">
                                                <strong>Country of Origin/Manufacture:<br></strong>
                                                <%=portletState.getCountryEntity()!=null ? portletState.getCountryEntity().getName() : ""%>
                                            </div>
                                             <div class="form-group">
                                                 <strong>Purpose Of Usage:<br></strong>
                                                 <%=portletState.getPurposeOfUsage()==null ? "N/A" : portletState.getPurposeOfUsage()%>
                                             </div>
                                             <div class="form-group">
                                                 <strong>Port of Landing:<br></strong>
                                                 <%=portletState.getPortCodeEntity()==null ? "N/A" : portletState.getPortCodeEntity().getPortCode()%>
                                             </div>
                                  </div>
                            </div>
                                        
                                    
                                    
                             
                            <div class="panel panel-primary">
                                <div class="panel-heading">
                                    LIST OF ITEMS:                  
                                </div>
                                <div class="panel-body">
                                    <div class="row">
                                        <div class="col-lg-14">
                                            <br>
                                             
                                        </div>
                                        <div class="row" style="padding-left: 16px">
                                            	<table width="100%" class="table table-hover" id="btable">
                                            		<thead>
                                            			<th>S/No</th>
                                            			<th>HS Code</th>
                                            			<th>Item Description</th>
                                            			<th>Quantity</th>
                                            			<th>Amount</th>
                                            		</thead>
                                            		<tbody>
                                            <%
                                            int sn=1;
                                            Double amount = 0.00;
                                            String cur="";
                                            for(Iterator<ApplicationItem> iter = portletState.getApplicationItemList().iterator(); iter.hasNext();)
                                            {
                                            	ApplicationItem applicationItem = iter.next();
                                            	//
                                            %>
                                            	<tr>
                                            		<td><%=sn++ %></td>
                                           			<td><%=applicationItem.getItemCategorySub()==null ? "N/A" : applicationItem.getItemCategorySub().getHsCode() %></td>
                                           			<td><%=applicationItem.getDescription()==null ? "N/A" : applicationItem.getDescription()%></td>
                                           			<td><%=applicationItem.getQuantity()==null ? "N/A" : applicationItem.getQuantity()%></td>
                                           			<td><%=applicationItem.getCurrency()!=null ? applicationItem.getCurrency().getHtmlEntity() : "" %><%=applicationItem.getAmount()==null ? "N/A" : applicationItem.getAmount()%></td>
                                           		</tr>
                                            <%
                                            	cur= applicationItem.getCurrency().getHtmlEntity();
                                            	amount += applicationItem.getAmount()==null ? 0.00 : applicationItem.getAmount();
                                            }
                                            %>
	                                            <tr>
	                                        		<td colspan="4" style="font-weight:bold">Total Cost</td>
	                                       			<td><strong><%=cur + "" + new Util().roundUpAmount(amount) %></strong></td>
	                                        	</tr>
	                                        	</tbody>
                                            	</table>    
                                        </div>
                                    </div>
                                    <!-- .panel-body -->
                                </div>
                            </div>

                            <div class="panel panel-primary">
                                <div class="panel-heading">
                                    SUPPORTING DOCUMENTS                    
                                </div>
                                <div class="panel-body">
                                    <div class="row">
                                        <div class="col-lg-5">
                                            <div class="form-group file">
                                                <strong>Attachment Type:</strong>
                                                        <table id="ContentPlaceHolder1_dlAttachType" cellspacing="0" style="border-collapse:collapse;">
	<tbody><tr>
															<td class="table table-bordered no-more-tables">
														<%
													if(portletState.getAttachmentListing()!=null && portletState.getAttachmentListing().size()>0)
													{
														for(Iterator<ApplicationAttachment> iter= portletState.getAttachmentListing().iterator(); iter.hasNext();)
														{
															ApplicationAttachment applicationAttachment = iter.next();
															%>
                                                                <div style="padding: 10px">
                                                                    <span id="ContentPlaceHolder1_dlAttachType_lblAttachType_0"><%=applicationAttachment.getApplicationAttachmentType().getName() %></span>
                                                                    <div><a target="_blank" href="<%="/resources/images/uploadedfiles/" + (applicationAttachment.getAttachmentFile()==null ? "" : applicationAttachment.getAttachmentFile())%>">Download or View Document</a></div>
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
							if(portletState.getChkImportingFor()!=null && portletState.getChkImportingFor().equals("1") && 
							portletState.getImportName()!=null && portletState.getImportName().length()>0 && 
							portletState.getTxtImportAddress()!=null && portletState.getTxtImportAddress().length()>0)
							{
							%>
								<div class="panel panel-primary">
	                                <div class="panel-heading">
	                                	<strong>Section for Importing for an organization:</strong>
	                                </div>
	                                <div class="panel-body">
	                                    <div class="row">
	                                        <div class="col-lg-5" id="divImportingfor" style="display: block;">
	
	                                            <div class="form-group">
	                                                <strong>Name:</strong>
	                            <%=portletState.getImportName()!=null ? portletState.getImportName() : ""%>
	                                            </div>
	                                            <div class="form-group">
	                                                <strong>Address:</strong>
	                            <%=portletState.getTxtImportAddress()!=null ? portletState.getTxtImportAddress() : ""%>
	                                            </div>
	                                            <div class="form-group">
	                                                <strong>Attach Contract Award Letter:</strong>
	                    							<div><a target="_blank" href="<%="/resources/images/uploadedfiles/" + (portletState.getFileUploadAwardLetter()==null ? "" : portletState.getFileUploadAwardLetter())%>">Download or View Document</a></div>
	                                            </div>
	
	                                        </div>
	                                    </div>
	                                </div>
	                            </div>
	                         <%
							}
	                         %>


							
	                            
                            	<input type="hidden" value="" name="act" id="act">
                                <button type="submit" id="SaveApplication" class="btn btn-danger" onclick="javascript:submitForm('back')">
                                Go Back</button>
                                <input type="button" onclick="printDiv('print-content')" class="btn btn-inverse" style="float:left" value="Print Page"/>
                                <button type="submit" id="SaveApplication" class="btn btn-success" style="float:right" onclick="javascript:submitForm('next')">
                                Finish</button>
                                
	</form>
	
</div>
	  		
	</div>
</div>
<aside class="col-sm-3" role="resources">
	<div><strong style="color:#FF0000">Help Tips</strong></div>
	<div style="padding:top:10px">
		<ul>
			<li>
				<div><strong>Necessary Documents:</strong></div>
				<div>Ensure you have the following items on hand before you start creating an EUC application:<br>
					<ol>
						<li>A scanned copy of your passport photo</li>
						<li>A scanned copy of any of these: Your National ID Card, Drivers License, International Passport</li>
						<li>Details of your CAC Certificate if you are signing up as a corporation</li>
					</ol>
				</div>
				</div>
			
			</li>
		</ul>
	</div>
</aside>
</section>






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
	document.getElementById('startRegFormId').submit;
	
}
</script>