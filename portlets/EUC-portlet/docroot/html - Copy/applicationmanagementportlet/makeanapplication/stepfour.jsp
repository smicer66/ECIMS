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
<%

ApplicationManagementPortletState portletState = ApplicationManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ApplicationManagementPortletState.class);
String folder = ECIMSConstants.NEW_APPLICATION_DIRECTORY;
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



<div class="list-group-item">
	<form  id="startRegFormId" action="<%=proceed%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">

                            <div class="row">
                                Step 4 of 4: Preview Application Details
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
                                                <span id="ApplicantNo" class="form-control"><%=portletState.getApplicant().getApplicantNumber()==null ? "" : portletState.getApplicant().getApplicantNumber()%></span>
                                                
                                            </div>
                                            <div class="form-group">
                                                <br>
                                                Applicant Name
                                                  <span id="Name" class="form-control"><%=portletState.getApplicant().getPortalUser().getFirstName() + " " + portletState.getApplicant().getPortalUser().getSurname()%></span>
                                                
                                            </div>
											<div class="form-group">
                                                <br>
                                                Applicant Type
                                                  <span id="Name" class="form-control"><%=portletState.getApplicant().getApplicantType().getValue()%></span>
                                                
                                            </div>
                                            <div class="form-group">
                                                Applicant Address
                                                 <span id="Address" class="form-control"><%=portletState.getApplicant().getPortalUser().getAddressLine1()!=null ? (portletState.getApplicant().getPortalUser().getAddressLine1() + "<br>" + portletState.getApplicant().getPortalUser().getAddressLine2()) : "N/A"%></span>
                                                
                                            </div>
                                            <div class="form-group">
                                                Applicant Email Address
                                                 <span id="Email" class="form-control"><%=portletState.getApplicant().getPortalUser().getEmailAddress() %></span>
                                                
                                            </div>
                                        </div>
                                        <div class="col-lg-5">
                                            <div class="form-group">
                                                Applicant Phone Number
                                                 <span id="PhNumber" class="form-control"><%=portletState.getApplicant().getPortalUser().getPhoneNumber() %></span>
                                                
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
                                                Select the category your item falls under:<br>
								<%=portletState.getItemCategoryEntity()!=null ? portletState.getItemCategoryEntity().getItemCategoryName() : ""%> 
                                            </div>
                                            <div class="form-group">
                                                Country of Origin/Manufacture:<br>
                                                <%=portletState.getCountryEntity()!=null ? portletState.getCountryEntity().getName() : ""%>
                                                <div class="form-group">
                                                    Purpose Of Usage:<br>
                                                    <%=portletState.getPurposeOfUsage()==null ? "" : portletState.getPurposeOfUsage()%>
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
                                            for(Iterator<ApplicationItem> iter = portletState.getApplicationItemList().iterator(); iter.hasNext();)
                                            {
                                            	ApplicationItem applicationItem = iter.next();
                                            	//
                                            %>
                                            	<tr>
                                           			<td><%=applicationItem.getItemCategorySub()==null ? "N/A" : applicationItem.getItemCategorySub().getHsCode() %></td>
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
													if(portletState.getAttachmentListing()!=null && portletState.getAttachmentListing().size()>0)
													{
														for(Iterator<ApplicationAttachment> iter= portletState.getAttachmentListing().iterator(); iter.hasNext();)
														{
															ApplicationAttachment applicationAttachment = iter.next();
															%>
                                                                <div style="padding: 10px">
                                                                    <span id="ContentPlaceHolder1_dlAttachType_lblAttachType_0"><%=applicationAttachment.getApplicationAttachmentType().getName() %></span>
                                                                    <img src="\resources\images\uploadedfiles\<%=applicationAttachment.getAttachmentFile()%>" height="50px;">
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
							if(portletState.getChkImportingFor()!=null)
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
                                        <div class="col-lg-5" id="divImportingfor" style="display: block;">

                                            <div class="form-group">
                                                Name:<br>
                            					<%=portletState.getImportName()!=null ? portletState.getImportName() : ""%>
                                            </div>
                                            <div class="form-group">
                                                Address:<br>
                                                <%=portletState.getTxtImportAddress()!=null ? portletState.getTxtImportAddress() : ""%>
                                            </div>
                                            <div class="form-group">
                                                Attach Contract Award Letter:
                                                <%
                                                if(portletState.getFileUploadAwardLetter()!=null)
                                                {
                                                %>
                                                	<img src="\resources\images\uploadedfiles\<%=portletState.getFileUploadAwardLetter() %>%>" height="50px;">
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
										if(portletState.getChkBoxImportDuty()!=null)
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
											if(portletState.getChkBoxImportDuty()!=null)
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
                                                if(portletState.getFileUploadProofAttachment()!=null)
                                                {
                                                %>
                                                	<img src="\resources\images\uploadedfiles\<%=portletState.getFileUploadProofAttachment() %>" height="50px;">
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
	                                                    <%=portletState.getPortCodeEntity()!=null ? portletState.getPortCodeEntity().getState().getName() : "N/A"%>
	                                                </div>
	                                            </div>
	                                            <div class="col-lg-3">
	                                                <div class="form-group">
	                                                    Port of Landing Port:<br>
	                                                    <%=portletState.getPortCodeEntity()!=null ? portletState.getPortCodeEntity().getPortCode() : "N/A"%>
	                                                </div>
	                                            </div>
	                                        </div>
	                                    </div>
	                                </div>
	                            </div>
	                        
                            <div class="panel panel-default">
                            	<input type="hidden" value="" name="act" id="act">
                                <input type="submit" value="Go Back" id="SaveApplication" class="btn btn-danger btn-cons" style="float:left" onclick="javascript:submitForm('back')">
                                <input type="submit" value="Finish" id="SaveApplication" class="btn btn-success btn-cons" style="float:right" onclick="javascript:submitForm('next')">
                            </div>
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
	document.getElementById('startRegFormId').submit;
	
}
</script>