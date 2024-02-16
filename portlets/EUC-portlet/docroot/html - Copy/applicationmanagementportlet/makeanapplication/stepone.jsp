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
String folder = File.separator + "resources" + File.separator + "images"; 

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
		value="<%=EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_ONE.name()%>" />
</portlet:actionURL>
<div class="list-group-item">
	<form  id="startRegFormId" action="<%=proceed%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
                            

                            <div class="row">
                                Step 1 of 4: Create an EUC Request
                            </div>

                            <div class="panel panel-default">
                                <div class="panel-heading">
                                    APPLICANT DETAILS                  
                                </div>
                                <div class="panel-body" id="collapseOne">
                                    <div class="row">
                                        <div class="col-lg-2">
                                            <img style="width:100px;" src="<%=folder + "/uploadedfiles/" + (portletState.getApplicant().getPortalUser().getPassportPhoto()==null ? "" : portletState.getApplicant().getPortalUser().getPassportPhoto())%>">
                                        </div>

                                        <div class="col-lg-5">
										  <div class="form-group">
                                                <br>
                                                <strong>Applicant-Number</strong>                                            <span id="ApplicantNo" class="form-control"><%=portletState.getApplicant().getApplicantNumber()%></span>                                            </div>
                                            <div class="form-group">
                                                <br>
                                                <strong>Applicant Name</strong>
                                                  <span id="Name" class="form-control"><%=portletState.getApplicant().getPortalUser().getFirstName() + " " + portletState.getApplicant().getPortalUser().getSurname()%></span>
                                                
                                            </div>
											<div class="form-group">
                                                <br>
                                                <strong>Applicant Type</strong>
                                                  <span id="Name" class="form-control"><%=portletState.getApplicant().getApplicantType().getValue()%></span>
                                                
                                            </div>
                                            <div class="form-group">
                                                <strong>Applicant Address</strong>
                                                 <span id="Address" class="form-control"><%=portletState.getApplicant().getPortalUser().getAddressLine1() + "<br>" + portletState.getApplicant().getPortalUser().getAddressLine2()%></span>
                                                
                                            </div>
                                            <div class="form-group">
                                                <strong>Applicant Email Address</strong>
                                                 <span id="Email" class="form-control"><%=portletState.getApplicant().getPortalUser().getEmailAddress() %></span>
                                                
                                            </div>
                                        </div>
                                        <div class="col-lg-5">
                                            <div class="form-group">
                                                <strong>Applicant Phone Number</strong>
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
                                                Is this EUC application an Exemption Application?
                                <select name="exemption" id="exemption" class="form-control">
									<option  value="0">No</option>
									<option  value="1">Yes</option>
								</select>
                                                
                                            </div>
                                            <div class="form-group">
                                                Select the category your item falls under:
                                <select name="ItemCatagory" id="ItemCatagory" class="form-control">
									<option  value="-1">Select Item Category</option>
								<%
								for(Iterator<ItemCategory> iter = portletState.getItemCategoryList().iterator(); iter.hasNext();)
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
                                                Country of Origin/Manufacture:
                                                <select name="CountryofManufacture" id="DDLCountryofManufacture" class="form-control" placeholder="Address">
													<option value="-1">Select Country</option>
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
                                                <div class="form-group">
                                                    Purpose Of Usage:
                            						<textarea name="txtPurposeofUsage" id="PurposeofUsage" class="form-control" placeholder="Purpose Of Usage"><%=portletState.getPurposeOfUsage()==null ? "" : portletState.getPurposeOfUsage()%></textarea>
                                                </div>
												<div class="form-group">
													Currency of Purchase:
													<select name="Currency" id="Currency" class="form-control" style="width:65px;">
													<%
													if(portletState.getCurrencyList()!=null && portletState.getCurrencyList().size()>0)
													{
														for(Iterator<Currency> iter = portletState.getCurrencyList().iterator(); iter.hasNext();)
														{
															Currency currency = iter.next();
															String selected = "";
															if(portletState.getCurrencyEntity()!=null && portletState.getCurrencyEntity().getId()==(currency.getId()))
															{
																selected = "selected='selected'";
															}
														%>
															<option <%=selected%> value="<%=currency.getId()%>"><%=currency.getHtmlEntity()%></option>
														<%
														}
													}
											%>
													</select>
												</div>
                                            </div>
                                        </div>

                                        
                                    </div>
                                    <!-- .panel-body -->
                                </div>
                            </div>
							<div class="panel panel-default">
                                <input type="submit" name="btnSaveApplication" value="Next" id="SaveApplication" class="btn btn-success btn-cons" style="float:right">
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