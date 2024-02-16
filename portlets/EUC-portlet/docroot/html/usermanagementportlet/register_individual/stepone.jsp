<%@page import="net.tanesha.recaptcha.ReCaptchaFactory"%>
<%@page import="net.tanesha.recaptcha.ReCaptcha"%>
<%@page import="com.ecims.portlet.usermanagement.UserManagementPortletState"%>
<%@page import="com.ecims.portlet.usermanagement.UserManagementPortletState.*"%>
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
<%@page import="smartpay.entity.enumerations.KinRelationshipType"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.RoleType"%>
<%@page import="smartpay.entity.Agency"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Arrays" %>
<%@page import="java.util.List" %>
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
<link rel="stylesheet" href="<%=faceboxCssUrl%>" type="text/css" />
<link rel="stylesheet" href="<%=pagingUrl%>" type="text/css" />
<script type="text/javascript" src="<%=(resourceBaseURL + "/js/anytimec.js")%>"></script>
<link rel="stylesheet" href='<%=resourceBaseURL + "/css/anytimec.css"%>' type="text/css" />
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" />

<!-- <script src="<%= resourceBaseURL %>/js/parsley.min.js"></script>
<script src="<%= resourceBaseURL %>/js/parsley.extend.js"></script>
<script src="<%= resourceBaseURL %> %>/js/app.plugin.js"></script>

<script src="<%= resourceBaseURL %>/js/jquery/parsley.js"></script>-->
<script src="<%= resourceBaseURL %>/js/jquery/parsley.min.js"></script>


<style type="text/css">
.parsley-required, .parsley-type, .parsley-equalto{
	color:red;
	
}
</style>
<%

UserManagementPortletState portletState = UserManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(UserManagementPortletState.class);
Collection<Agency> agencyList = portletState.getAgencyList();
List<IdentificationType> identificationType = IdentificationType.values();
ReCaptcha c = ReCaptchaFactory.newReCaptcha("6LfMX_4SAAAAALzkpcm66ZZ4Fsd1us8ROKxp6lLC", "6LfMX_4SAAAAAP71kP42bqQV9AInMII_2B3xbDy-", false);

%>

<portlet:actionURL var="proceedToStepThree" name="processAction">
	<portlet:param name="action"
		value="<%=EUC_DESK_ACTIONS.CREATE_A_PORTAL_USER_STEP_ONE.name()%>" />
</portlet:actionURL>

<%
if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_SYSTEM_ADMIN))
{
%>
<liferay-ui:success key="successMessage"
message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>
<%	
}
else
{
%>
<jsp:include page="/html/usermanagementportlet/tabs_nsa_admin.jsp" flush="" />
<%	
}
%>





<div style="padding-left:10px; padding-right:10px; width:900px">
	<h2>Create A User Profile</h2>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Step 2 of 3: Portal User's Bio-Data</span></div>
	  <div class="panel-body">
	    	
	    <form  id="startRegFormId" action="<%=proceedToStepThree%>" data-parsley-validate method="post" method="post" enctype="multipart/form-data">
	     
	     <%
	     if(portletState.getUsertype()!=null && (portletState.getUsertype().equals(RoleTypeConstants.ROLE_INFORMATION_USER.getValue()) || 
	    		 portletState.getUsertype().equals(RoleTypeConstants.ROLE_REGULATOR_USER.getValue()) || 
	    		 portletState.getUsertype().equals(RoleTypeConstants.ROLE_ACCREDITOR_USER.getValue()) ||
	    		 portletState.getUsertype().equals(RoleTypeConstants.ROLE_NSA_USER.getValue()) ))
	    	 
	     {
	     %>
		  	<div style="padding-bottom:5px">
					<div>
						<strong>Agency User belongs to:<span style="color:red">*</span></strong>
					  	<select class="form-control formwidth50" data-parsley-required="true" name="agency">
							<option value="">-Select An Agency This User Belongs to-</option>
							<%
						if(agencyList!=null && agencyList.size()>0)
						{
							
							for(Iterator<Agency> iter = agencyList.iterator(); iter.hasNext();)
							{
								Agency agency = iter.next();
								
								String selected="";
								if(portletState.getAgencyEntity()!=null && portletState.getAgencyEntity().getAgencyName().equalsIgnoreCase(agency.getAgencyName()))
								{
									selected = "selected='selected'";
								}
								%>
									<option <%=selected%> value="<%=agency.getId() %>"><%=agency.getAgencyName() %></option>
								<%
								
							}
						}
							
							%>
						</select>
					</div>
		  	</div>
		  <% 
	     }
		  %>
		 
		 
		 <%
		 if(portletState.getUsertype().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER.getValue()))
		 {
		 %>
		  <div style="padding-bottom:5px"> <strong>Company/Agency Name:<span style="color:red">*</span></strong>
	        <div>
	          <input class="clear form-control formwidth50" type="text" data-parsley-required="true" value="<%=portletState.getCompanyName()==null ? "" : portletState.getCompanyName() %>" name="companyName" id="companyname" placeholder="Provide Your Agency or Company Name" />
	        </div>
	      </div>
	      <%
		 }
	      %>
	      <div style="padding-bottom:5px"> <strong>Surname:<span style="color:red">*</span></strong>
	        <div>
	          <input class="clear form-control formwidth50" type="text" data-parsley-required="true" value="<%=portletState.getLastname()==null ? "" : portletState.getLastname() %>" name="lastname" id="lastname" placeholder="Provide Your Surname" />
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>First Name:<span style="color:red">*</span></strong>
	        <div>
	          <input class="clear form-control formwidth50" type="text" data-parsley-required="true" value="<%=portletState.getFirstname()==null ? "" : portletState.getFirstname() %>" name="firstname" id="firstname" placeholder="Provide Your First Name" />
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>Other Name:</strong>
	        <div>
	          <input class="clear form-control formwidth50" type="text" value="<%=portletState.getOthername()==null ? "" : portletState.getOthername() %>" name="othername" id="othername" placeholder="Provide Your Other Name" />
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>Email Address:<span style="color:red">*</span></strong>
	        <div>
	          <input class="clear form-control formwidth50" type="text" data-parsley-required="true" value="<%=portletState.getContactEmailAddress()==null ? "" : portletState.getContactEmailAddress() %>" name="contactEmailAddress" id="contactEmailAddress" placeholder="Provide Your Email Address" />
	        </div>
	      </div>
	      
	      <div style="padding-bottom:5px"><strong>Date of Birth:<span style="color:red">*</span></strong>
	        <div>
	          <input class="clear form-control formwidth50" readonly="readonly" type="text" data-parsley-required="true" value="<%=portletState.getDob()==null ? "" : portletState.getDob() %>" name="dob" id="dob" placeholder="YYYY-MM-DD" />
	        </div>
	      </div>
		  <div style="padding-bottom:5px; clear:both"><div><strong>Mobile Number:<span style="color:red">*</span></strong></div>
	        <div>
				<div style="float:left; padding-right:10px">
					<div class="input-group formwidth50">
					<span class="input-group-addon">+234</span>
					<%
					String mob = "";
					if(portletState.getMobile()!=null && portletState.getMobile().contains("+234"))
					{
						mob = portletState.getMobile().substring(4, portletState.getMobile().length());
					}else if(portletState.getMobile()!=null && portletState.getMobile().contains("234"))
					{
						mob = portletState.getMobile().substring(3, portletState.getMobile().length());
					}else if(portletState.getMobile()!=null)
					{
						mob = portletState.getMobile();
					}
					%>
	          <input onkeypress="return onlyNumKey(event)" data-parsley-required="true" class="form-control formwidth50" style="" type="text" value="<%=mob %>" name="mobile" id="mobile" placeholder="e.g. 9XXXXXXXXX" />
	          		</div>
			  	</div>
	        </div>
	      </div>
		  <div style="padding-bottom:5px; clear:both"><div><strong>Country of Origin:<span style="color:red">*</span></strong></div>
	        <div>
				<div style="padding-right:10px">
					<select class="form-control formwidth50" data-parsley-required="true" name="nationality">
						<option value="">-Select One-</option>
						<%
						Collection<Country> countryList = portletState.getAllCountry();
						
						for(Iterator<Country> iter = countryList.iterator(); iter.hasNext();)
						{
							String countryName = iter.next().getName();
							String selected = "";
							if(countryName.equalsIgnoreCase("Nigeria"))
							{
								if(portletState.getNationality()!=null && portletState.getNationality().equals(countryName))
								{
									selected = "selected='selected'";
								}
						%>
						<option <%=selected%> value="<%=countryName%>"><%=countryName%></option>
						<%
							}
						}
						%>
					</select>
				</div>
	        </div>
	      </div>
		  <div style="padding-bottom:5px; clear:both"><div><strong>State of Origin:<span style="color:red">*</span></strong></div>
	        <div>
				<div style="padding-right:10px">
					<select class="form-control formwidth50" data-parsley-required="true" name="state">
						<option value="">-Select One-</option>
						<%
						Collection<State> stateList = portletState.getAllState();
						for(Iterator<State> iter = stateList.iterator(); iter.hasNext();)
						{
							String stateName = iter.next().getName();
							String selected = "";
							if(portletState.getState()!=null && portletState.getState().equals(stateName))
							{
								selected = "selected='selected'";
							}
						%>
						<option <%=selected%> value="<%=stateName%>"><%=stateName%></option>
						<%
						}
						%>
					</select>
				</div>
	        </div>
	      </div>
	      
		  <div style="padding-bottom:5px; clear:both">
			<div>
				<strong>Your Passport Photo:</strong> <input type="file" data-parsley-required="true" name="passportPhoto">
			</div>
		  </div>
	      
	      <fieldset>
			  <legend>Means of Identification
			  </legend>
				  <div style="padding-bottom:5px">
					<div>
					  	<select data-parsley-required="true" class="clear form-control formwidth50" name="identificationMeans" id="identificationMeans" onchange="javascript:handleIdentificationOption()">
							<option value="">-Select A Means of Identification-</option>
							<%
							String display1 = "none";
							String display2 = "none";
							String display3 = "none";
							String display4 = "none";
							String selected = "";
							
							for(Iterator<IdentificationType> iter = identificationType.iterator(); iter.hasNext();)
							{
								String identificationTypeStr = iter.next().getValue();
								selected = "";
								if(portletState.getIdentificationType()!=null && portletState.getIdentificationType().equalsIgnoreCase(identificationTypeStr))
								{
									selected = "selected='selected'";
								}
								
								
								if(portletState.getIdentificationType()!=null && portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_NATIONAL_ID.getValue()))
								{
									display1 = "block";
									display2 = "none";
									display3 = "none";
									display4 = "none";
								}
								if(portletState.getIdentificationType()!=null && portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_DRIVERS_LICENSE.getValue()))
								{
									display1 = "none";
									display2 = "block";
									display3 = "none";
									display4 = "none";
								}
								if(portletState.getIdentificationType()!=null && portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_INTL_PASSPORT.getValue()))
								{
									display1 = "none";
									display2 = "none";
									display3 = "block";
									display4 = "none";
								}
								if(portletState.getIdentificationType()!=null && portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_PVC.getValue()))
								{
									display1 = "none";
									display2 = "none";
									display3 = "none";
									display4 = "block";
								}
							%>
								<option <%=selected%> value="<%=identificationTypeStr %>"><%=identificationTypeStr %></option>
							<%
							}
							if(portletState.getUsertype().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER.getValue()))
							{
							%>
							<option value="<%=IdentificationType.IDENTIFICATION_TYPE_DRIVERS_LICENSE.getValue()%>">FOREIGNERS'/DIPLOMATIC ID CARD</option>
							<%
							}
							
							%>
						</select>
					</div>
				  </div>
				  <div id="nationalidcardoption" style="display:<%=display1 %>">
					  <div style="padding-bottom:5px">
						<div>
							<strong>Your National ID Photo:</strong> <input type="file" name="nationalIdPhoto">
						</div>
					  </div>
					  <div style="padding-bottom:5px">
						<div>
							<strong>Your National ID Number:</strong> 
							<div>
							  <input class="clear form-control formwidth50" type="text" value="<%=portletState.getNationalIdNumber()==null ? "" : portletState.getNationalIdNumber() %>" name="nationalIdNumber" id="nationalIdNumber" placeholder="Provide Your National ID Number" />
							</div>
						</div>
					  </div>
					  
					  <div style="padding-bottom:5px">
						<div>
							<strong>Issue Date:</strong> 
							<div>
							  <input class="clear form-control formwidth50" type="text" value="<%=portletState.getNatlissueDate()==null ? "" : portletState.getNatlissueDate() %>" name="natlissueDate" id="natlissueDate" readonly="readonly" placeholder="YYYY-MM-DD" />
							</div>
						</div>
					  </div>
				   </div>
				   
				   
				   <div id="driverslicenseoption" style="display:<%=display2 %>">
					  <div style="padding-bottom:5px">
						<div>
							<strong>Your Drivers License ID Photo:</strong> <input type="file" name="driversPhoto">
						</div>
					  </div>
					  <div style="padding-bottom:5px">
						<div>
							<strong>Your Drivers License ID Number:</strong> 
							<div>
							  <input class="clear form-control formwidth50" type="text" value="<%=portletState.getDriversIdNumber()==null ? "" : portletState.getDriversIdNumber() %>" name="driversIdNumber" id="driversIdNumber" placeholder="Provide Your Drivers License Number" />
							</div>
						</div>
					  </div>
					  
					  
					  
					  <div style="padding-bottom:5px">
						<div>
							<strong>Place Of Issue:</strong> 
							<div>
							  <input class="clear form-control formwidth50" type="text" value="<%=portletState.getDriversplaceOfIssue()==null ? "" : portletState.getDriversplaceOfIssue() %>" name="driversplaceOfIssue" id="driversplaceOfIssue" placeholder="Provide place of Issuance" />
							</div>
						</div>
					  </div>
					  
					  <div style="padding-bottom:5px; display:block">
						<div>
							<strong>Issuance Date:</strong> 
							<div>
							  <input class="clear form-control formwidth50" readonly="readonly" type="text" value="<%=portletState.getDriversissuancedate()==null ? "" : portletState.getDriversissuancedate() %>" name="driversissuancedate" id="driversissuancedate" placeholder="YYYY-MM-DD" />
							</div>
						</div>
					  </div>
					  
					  <div style="padding-bottom:5px">
						<div>
							<strong>Expiry Date:</strong> 
							<div>
							  <input class="clear form-control formwidth50" readonly="readonly" type="text" value="<%=portletState.getDriversExpiryDate()==null ? "" : portletState.getDriversExpiryDate() %>" name="driversExpiryDate" id="driversExpiryDate" placeholder="YYYY-MM-DD" />
							</div>
						</div>
					  </div>
					  
				   </div>
				   
				   
				   <div id="pvcoption" style="display:<%=display4 %>">
					  <div style="padding-bottom:5px">
						<div>
							<strong>Your Permanent Voters Card:</strong> <input type="file" name="pvcPhoto">
						</div>
					  </div>
					  <div style="padding-bottom:5px">
						<div>
							<strong>Your Permanent Voters Card Number:</strong> 
							<div>
							  <input class="clear form-control formwidth50" type="text" value="<%=portletState.getPvcNumber()==null ? "" : portletState.getPvcNumber() %>" name="pvcNumber" id="pvcNumber" placeholder="Provide Your Permanent Voters Card Number" />
							</div>
						</div>
					  </div>
					  
					  <div style="padding-bottom:5px">
						<div>
							<strong>Issue Date:</strong> 
							<div>
							  <input class="clear form-control formwidth50" type="text" value="<%=portletState.getPvcIssueDate()==null ? "" : portletState.getPvcIssueDate() %>" name="pvcissueDate" id="pvcissueDate" readonly="readonly" placeholder="YYYY-MM-DD" />
							</div>
						</div>
					  </div>
				   </div>
				   
				   
				   <div id="intlpassportoption" style="display:<%=display3 %>">
					  <div style="padding-bottom:5px">
						<div>
							<strong>Your International Passport Photo:</strong> <input type="file" name="intlPassportPhoto">
						</div>
					  </div>
					  <div style="padding-bottom:5px">
						<div>
							<strong>Intl Passport Number:</strong> 
							<div>
							  <input class="clear form-control formwidth50" type="text" value="<%=portletState.getIntlpassportIdNumber()==null ? "" : portletState.getIntlpassportIdNumber() %>" name="intlpassportIdNumber" id="intlpassportIdNumber" placeholder="Provide Your National ID Number" />
							</div>
						</div>
					  </div>
					  
					  
					  <div style="padding-bottom:5px">
						<div>
							<strong>Place Of Issue:</strong> 
							<div>
							  <input class="clear form-control formwidth50" type="text" value="<%=portletState.getIntlplaceOfIssue()==null ? "" : portletState.getIntlpassportIdNumber() %>" name="intlplaceOfIssue" id="intlplaceOfIssue" placeholder="Provide place of Issuance" />
							</div>
						</div>
					  </div>
					  
					  <div style="padding-bottom:5px; display:none">
						<div>
							<strong>Issuance Date:</strong> 
							<div>
							  <input class="clear form-control formwidth50" readonly="readonly" type="text" value="<%=portletState.getIntlissuancedate()==null ? "" : portletState.getIntlissuancedate() %>" name="intlissuancedate" id="intlissuancedate" placeholder="YYYY-MM-DD" />
							</div>
						</div>
					  </div>
					  
					  <div style="padding-bottom:5px;">
						<div>
							<strong>Expiry Date:</strong> 
							<div>
							  <input class="clear form-control formwidth50" readonly="readonly" type="text" value="<%=portletState.getIntlpassportExpiryDate()==null ? "" : portletState.getIntlpassportExpiryDate() %>" name="intlpassportExpiryDate" id="intlpassportExpiryDate" placeholder="YYYY-MM-DD" />
							</div>
						</div>
					  </div>
				   </div>
		  <fieldset>


		  <div style="padding-bottom:5px">
			<div>
			  	<strong>Your Tax Identification Number:</strong> 
				<div>
				  <input class="clear form-control formwidth50" type="text" value="<%=portletState.getTaxIdNumber()==null ? "" : portletState.getTaxIdNumber() %>" name="tin" id="tin" placeholder="Provide Your Tax ID Number" />
				</div>
			</div>
		  </div>
<div>
 
	      	<input type="hidden" value="" name="act" id="act" />
	      	<button type="submit" class="btn btn-danger" style="float:left" onclick="javascript:disableFormParsley(); submitForm('back')">Go Back</button>
	      	<button type="submit" class="btn btn-success" style="float:right" onclick="javascript:submitForm('proceed')">Proceed to Next</button>
	      	<div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
	    All fields with red asterisk (*) imply they must be provided</div>
	        <!-- <input type="submit" name="createportaluserStepOne" value="Proceed to Next" id="createportaluserStepOne" class="floatLeft" style="background-color:#00CC00" />-->
	      </div>
	    </form>
	  </div>
	</div>
</div>




<%
Calendar cal = Calendar.getInstance();
int yearNow = cal.get(Calendar.YEAR);
int year80Back = yearNow - 80;
int year40Further = yearNow + 40;
int year18Back = yearNow - 18;

%>
<script type="text/javascript" charset="utf-8" src="<%=jqueryJsUrl%>"></script>
<script type="text/javascript" charset="utf-8" src="<%=jqueryUIJsUrl%>"></script>
<script type="text/javascript">


function handleIdentificationOption()
{
	selected = document.getElementById('identificationMeans').value;
	if(selected=="-1")
	{
		document.getElementById('intlpassportoption').style.display="none";
		document.getElementById('driverslicenseoption').style.display="none";
		document.getElementById('nationalidcardoption').style.display="none";
		document.getElementById('pvcoption').style.display="none";
		
	}
	if(selected=="<%=IdentificationType.IDENTIFICATION_TYPE_NATIONAL_ID.getValue()%>")
	{
		document.getElementById('intlpassportoption').style.display="none";
		document.getElementById('driverslicenseoption').style.display="none";
		document.getElementById('nationalidcardoption').style.display="block";
		document.getElementById('pvcoption').style.display="none";
	}
	if(selected=="<%=IdentificationType.IDENTIFICATION_TYPE_INTL_PASSPORT.getValue()%>")
	{
		document.getElementById('intlpassportoption').style.display="block";
		document.getElementById('driverslicenseoption').style.display="none";
		document.getElementById('nationalidcardoption').style.display="none";
		document.getElementById('pvcoption').style.display="none";
	}
	if(selected=="<%=IdentificationType.IDENTIFICATION_TYPE_DRIVERS_LICENSE.getValue()%>")
	{
		document.getElementById('intlpassportoption').style.display="none";
		document.getElementById('driverslicenseoption').style.display="block";
		document.getElementById('nationalidcardoption').style.display="none";
		document.getElementById('pvcoption').style.display="none";
	}
	if(selected=="<%=IdentificationType.IDENTIFICATION_TYPE_PVC.getValue()%>")
	{
		document.getElementById('intlpassportoption').style.display="none";
		document.getElementById('driverslicenseoption').style.display="none";
		document.getElementById('nationalidcardoption').style.display="none";
		document.getElementById('pvcoption').style.display="block";
	}
}


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




$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#dob').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "<%=year80Back%>:<%=year18Back%>",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yy-mm-dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false
	});
	
	
	
});

function submitForm(clicked)
{
	document.getElementById('act').value=clicked;
	document.getElementById('startRegFormId').submit;
	
}


$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#driversExpiryDate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "<%=yearNow%>:<%=year40Further%>",
		showButtonPanel : true,
		dateFormat: 'yy-mm-dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false
	});
	
	
	
});


$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#intlpassportExpiryDate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "<%=yearNow%>:<%=year40Further%>",
		showButtonPanel : true,
		dateFormat: 'yy-mm-dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false
	});
	
	
	
});


$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#driversissuancedate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "<%=year80Back%>:<%=yearNow%>",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yy-mm-dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false
	});
	
	
	
});


$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#pvcissueDate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "<%=year80Back%>:<%=yearNow%>",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yy-mm-dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false
	});
	
	
	
});



$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#natlissueDate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "<%=year80Back%>:<%=yearNow%>",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yy-mm-dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false
	});
	
	
	
});



$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#intlissuancedate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "<%=year80Back%>:<%=yearNow%>",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yy-mm-dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false
	});
	
	
	
});
</script>