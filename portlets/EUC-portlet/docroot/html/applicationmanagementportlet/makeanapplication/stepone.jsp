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
<%@page import="smartpay.entity.Currency"%>
<%@page import="smartpay.entity.ItemCategory"%>
<%@page import="smartpay.entity.enumerations.KinRelationshipType"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.enumerations.RoleConstants"%>
<%@page import="smartpay.entity.RoleType"%>
<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.entity.PortCode"%>
<%@page import="smartpay.entity.Applicant"%>
<%@page import="smartpay.entity.Application"%>
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

<style type="text/css">
.formwidth50{
	width:300px;
}
.required{
	color:red;
	
}
</style>


<%

ApplicationManagementPortletState portletState = ApplicationManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ApplicationManagementPortletState.class);
String folder = File.separator + "resources" + File.separator + "images"; 
Applicant applicant = portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser());
portletState.setCurrentTab(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP) ? VIEW_TABS.CREATE_AN_APPLICATION_EU : VIEW_TABS.CREATE_AN_APPLICATION_AG);

if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
	portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
	portletState.setAgencyApplicant(portletState.getPortalUser());

if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER))
	portletState.setApplicant(applicant);


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
		value="<%=EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_ONE.name()%>" />
</portlet:actionURL>


<%
if((applicant!=null && applicant.getBlackList()!=null && applicant.getBlackList().equals(Boolean.TRUE)) || 
		(portletState.getPortalUser().getAgency()!=null && 
		portletState.getPortalUser().getAgency().getBlacklist()!=null && 
		portletState.getPortalUser().getAgency().getBlacklist().equals(Boolean.TRUE)))
{
	if(portletState.getPortalUser().getAgency()!=null)
	{
%>
	<div style="padding:20px;">You can not apply for an EUC certificate as your agency has been blacklisted. Kindly visit our office to resolve this issue</div>
<%
	}else
	{
%>
	<div style="padding:20px;">You can not apply for an EUC certificate as you have been blacklisted. Kindly visit our office to resolve this issue</div>
<%
	}
}else
{
%>
<section class="container">
<div style="padding-left:10px; padding-right:10px;" class="col-sm-9 panel panel-default" role="news">
	<h4>Create An EUC Application Request</h4>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Step 1 of 4: Item Category & Currency</span></div>
	  <div class="panel-body">
	<form  id="startRegFormId" action="<%=proceed%>" data-validate="parsley" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
                            

                            <div class="panel panel-primary">
                            
                            	<%
                            	if(portletState.getPortalUser().getAgency()==null)
                            	{
                            		if(portletState.getPortalUser().getCompany()!=null)
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
	                                        </div>
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
                                                <strong>Agency Name</strong>
                                                  <div><%=portletState.getAgencyApplicant().getAgency().getAgencyName()%></div>
                                            </div>
											<div class="form-group">
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
                                    <div class="row">
                                        <div class="col-lg-5">
                                        
                                            <div class="form-group">
                                                Select the category your item falls under:<span style="color:red">*</span>
                                <select data-required="true" name="ItemCatagory" id="ItemCatagory" class="form-control formwidth50">
									<option  value="">Select Item Category</option>
								<%
								for(Iterator<ItemCategory> iter = portletState.getApplyItemCategoryList().iterator(); iter.hasNext();)
								{
									ItemCategory itemCategory = iter.next();
									String selected = "";
									if(portletState.getItemCategoryEntity()!=null && portletState.getItemCategoryEntity().getId().equals(itemCategory.getId()))
									{
										selected = "selected='selected'";
									}
								%>
									<option <%=selected%> value="<%=itemCategory.getId()%>"><%=itemCategory.getItemCategoryName()%></option>
								<%
								}
								%>
								</select>
                                                
                                            </div>
                                            <div class="form-group">
                                                Country of Origin/Manufacture:<span style="color:red">*</span>
                                                <select data-required="true" name="CountryofManufacture" id="DDLCountryofManufacture" class="form-control formwidth50" placeholder="Address">
													<option value="">Select Country</option>
													<%
													for(Iterator<Country> iter = portletState.getAllCountry().iterator(); iter.hasNext();)
													{
														Country country = iter.next();
														String selected = "";
														if(portletState.getCountryEntity()!=null && portletState.getCountryEntity().getId().equals(country.getId()))
														{
															selected = "selected='selected'";
														}
													%>
														<option <%=selected%> value="<%=country.getId()%>"><%=country.getName()%></option>
													<%
													}
													%>

												</select>
											</div>

                                             <div class="form-group">
                                                 Purpose Of Usage:<span style="color:red">*</span>
                         						<textarea data-required="true" name="txtPurposeofUsage" id="PurposeofUsage" class="form-control formwidth50" placeholder="Purpose Of Usage"><%=portletState.getPurposeOfUsage()==null ? "" : portletState.getPurposeOfUsage()%></textarea>
                                             </div>
												<div class="form-group">
													Currency of Purchase:<span style="color:red">*</span>
													<select data-required="true" name="Currency" id="Currency" class="form-control formwidth50" style="">
													<option value="">Select Currency</option>
													<%
													if(portletState.getCurrencyList()!=null && portletState.getCurrencyList().size()>0)
													{
														for(Iterator<Currency> iter = portletState.getCurrencyList().iterator(); iter.hasNext();)
														{
															Currency currency = iter.next();
															String selected = "";
															if(portletState.getCurrencyEntity()!=null && portletState.getCurrencyEntity().getId().equals(currency.getId()))
															{
																selected = "selected='selected'";
															}
														%>
															<option <%=selected%> value="<%=currency.getId()%>"><%=currency.getName() + " - " + currency.getHtmlEntity()%></option>
														<%
														}
													}
											%>
													</select>
												</div>
												
                                                <div class="form-group">
                                                	Expected Port of Landing: (Optional)
                                                    <select name="PortofLandingPort" id="DDLPortofLandingPort" class="form-control formwidth50">
                                                    
	<option value="">-Select A Port of Landing-</option>
	<%
	if(portletState.getPortCodeList()!=null && portletState.getPortCodeList().size()>0)
	{
		for(Iterator<PortCode> iter = portletState.getPortCodeList().iterator(); iter.hasNext();)
		{
			PortCode portCode = iter.next();
			String selected = "";
			if(portletState.getPortCodeEntity()!=null && portletState.getPortCodeEntity().getId().equals(portCode.getId()))
			{
				selected = "selected='selected'";
			}
		%>
			<option <%=selected%> value="<%=portCode.getId()%>"><%=portCode.getState().getName() + " - Port Code (" + portCode.getPortCode() + ")"%></option>
		<%
		}
	}
	%>

</select>
                                        
                                                </div>
                                            
                                            
                                                
												
                                            </div>
                                        </div>

                                        
                                    <!-- .panel-body -->
                                </div>
                            </div>
							<div class="panel panel-default">
                                <button type="submit" onclick="javascript:submitForm('makeappstepone')" name="btnSaveApplication" id="SaveApplication" class="btn btn-success btn-cons" style="float:right">Next</button>
                            </div>
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
			</li>
			<li>
				<div><strong>Document Validity</strong></div>
				<div>Ensure your licenses are valid at least 3 months before date of expiration
				</div>
			
			</li>
		</ul>
	</div>
</aside>
</section>

<%
}
%>



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