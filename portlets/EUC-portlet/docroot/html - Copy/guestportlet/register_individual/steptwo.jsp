<%@page import="com.ecims.portlet.guest.GuestPortletState"%>
<%@page import="com.ecims.portlet.guest.GuestPortletState.*"%>
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
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Arrays" %>
<%@page import="java.util.List" %>
<%@page import="com.ecims.commins.Util"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="net.tanesha.recaptcha.ReCaptcha" %>
<%@ page import="net.tanesha.recaptcha.ReCaptchaFactory" %>




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
<script type="text/javascript" src="<%=(resourceBaseURL + "/js/anytimec.js")%>"></script>
<link rel="stylesheet" href='<%=resourceBaseURL + "/css/anytimec.css"%>' type="text/css" />
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" />
<style>
#recaptcha_area input { height: auto; }
#recaptcha_area a { font-weight: normal; }
</style>
<%

GuestPortletState portletState = GuestPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(GuestPortletState.class);
List<IdentificationType> identificationType = IdentificationType.values();
List<KinRelationshipType> relationship = KinRelationshipType.values();

%>
<portlet:actionURL var="proceedToStepThree" name="processAction">
	<portlet:param name="action"
		value="<%=APPLICANT_ACTIONS.CREATE_A_PORTAL_USER_STEP_TWO.name()%>" />
</portlet:actionURL>


<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>


	  
	  
<div style="padding-left:10px; padding-right:10px; width:900px">
	<h4>Create An Individual ECIMS Profile Account</h4>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Step 2 of 3: Bio-Data (Individual Account)</span></div>
	  <div class="panel-body">
	    	
	    <form  id="startRegFormId" action="<%=proceedToStepThree%>" method="post" enctype="multipart/form-data">
	     <fieldset>
			  <legend>Your Bio-Data
			  </legend>
			  <div style="padding-bottom:5px"> <strong>Surname:<span style="color:red">*</span></strong>
				<div>
				  <input class="clear form-control" type="text" value="<%=portletState.getLastName()==null ? "" : portletState.getFirstName() %>" name="lastname" id="lastname" placeholder="Provide Your Surname" />
				</div>
			  </div>
			  <div style="padding-bottom:5px"><strong>First Name:<span style="color:red">*</span></strong>
				<div>
				  <input class="clear form-control" type="text" value="<%=portletState.getFirstName()==null ? "" : portletState.getFirstName() %>" name="firstname" id="firstname" placeholder="Provide Your First Name" />
				</div>
			  </div>
			  <div style="padding-bottom:5px"><strong>Other Name:</strong>
				<div>
				  <input class="clear form-control" type="text" value="<%=portletState.getOtherName()==null ? "" : portletState.getOtherName() %>" name="othername" id="othername" placeholder="Provide Your Other Name" />
				</div>
			  </div>
			  <div style="padding-bottom:5px"><strong>Email Address:<span style="color:red">*</span></strong>
				<div>
				  <input class="clear form-control" type="text" value="<%=portletState.getEmail()==null ? "" : portletState.getEmail() %>" name="contactEmailAddress" id="contactEmailAddress" placeholder="Provide Your Email Address" />
				</div>
			  </div>
			  
			  <div style="padding-bottom:5px"><strong>Date of Birth:<span style="color:red">*</span></strong>
				<div>
				  <input class="clear form-control" type="text" value="<%=portletState.getDob()==null ? "" : portletState.getDob() %>" name="dob" id="dob" placeholder="YYYY-MM-DD" />
				</div>
			  </div>
			  <div style="padding-bottom:5px; clear:both"><div><strong>Mobile Number:<span style="color:red">*</span></strong></div>
				<div>
				  
				  
				  <div class="input-group">
					<span class="input-group-addon">+234</span>
					<input onkeypress="return onlyNumKey(event)" class="clear form-control" type="text" value="<%=portletState.getMobile()==null ? "" : portletState.getMobile() %>" name="mobile" id="mobile" placeholder="e.g. 9XXXXXXXXX" />
				  </div>
				</div>
			  </div>
			  <div style="padding-bottom:5px; clear:both"><div><strong>Nationality:<span style="color:red">*</span></strong></div>
				<div>
					<div style="padding-right:10px">
						<select class="clear form-control" name="nationality">
							<%
							Collection<Country> countryList = portletState.getAllCountry();
							
							for(Iterator<Country> iter = countryList.iterator(); iter.hasNext();)
							{

								String countryName = iter.next().getName();
								if(countryName.equalsIgnoreCase("Nigeria"))
								{
									String selected = "";
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
			  <div style="padding-bottom:5px; clear:both"><div><strong>State:<span style="color:red">*</span></strong></div>
				<div>
					<div style="padding-right:10px">
						<select class="clear form-control" name="state">
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
			  <div style="padding-bottom:5px"><strong>Gender:<span style="color:red">*</span></strong>
				<div class="form-group">
					<div class="col-sm-9">
					<%
					if(portletState.getGender()==null)
					{
					%>
					  <div class="col-sm-1"><input type="radio" name="gender" value="1"></div> <div class="col-sm-3">Male</div>
					  <div class="col-sm-1"><input type="radio" name="gender" value="0"></div> <div class="col-sm-3">Female</div>
					<%
					}else if(portletState.getGender().equals("1"))
					{
					%>
					  <div class="col-sm-1"><input type="radio" checked="checked" name="gender" value="1"></div> <div class="col-sm-3">Male</div>
					  <div class="col-sm-1"><input type="radio" name="gender" value="0"></div> <div class="col-sm-3">Female</div>
					<%
					}else if(portletState.getGender().equals("0"))
					{
					%>
					  <div class="col-sm-1"><input type="radio" name="gender" value="1"></div> <div class="col-sm-3">Male</div>
					  <div class="col-sm-1"><input type="radio" checked="checked" name="gender" value="0"></div> <div class="col-sm-3">Female</div>
					<%
					}
					%>
					</div>
				  </div>
				
			  </div>
			  
			  <div style="padding-bottom:5px">
				<div>
					<strong>Your Passport Photo:</strong> <input type="file" name="passportPhoto">
				</div>
			  </div>
			  <div style="padding-bottom:5px"><strong>Marital Status:</strong>
				<div>
				<select class="clear form-control" name="maritalStatus" id="maritalStatus">
					<option value="-1">-Select One-</option>
					<%
					String selected="";
					if(portletState.getMaritalStatus()!=null && portletState.getMaritalStatus().equals("Single"))	
					{
						selected = "selected='selected'";
					}
					%>
					<option <%=selected%> value="Single">Single</option>
					<%
					selected="";
					if(portletState.getMaritalStatus()!=null && portletState.getMaritalStatus().equals("Married"))	
					{
						selected = "selected='selected'";
					}
					%>
					<option <%=selected%> value="Married">Married</option>
					<%
					selected="";
					if(portletState.getMaritalStatus()!=null && portletState.getMaritalStatus().equals("Divorced"))	
					{
						selected = "selected='selected'";
					}
					%>
					<option <%=selected%> value="Divorced">Divorced</option>
				</select>
				  
				</div>
			  </div>
		  </fieldset>
		  
		  <fieldset>
			  <legend>Residential Address</legend>
			  <div style="padding-bottom:5px"><strong>First Line of Address:</strong>
				<div>
				  <input class="clear form-control" type="text" value="<%=portletState.getAddressLine1()==null ? "" : portletState.getAddressLine1() %>" name="contactAddressLine1" id="contactAddressLine1" placeholder="Provide First Line of Address" />
				</div>
			  </div>
			  <div style="padding-bottom:5px"><strong>Second Line of Address:</strong>
				<div>
				  <input class="clear form-control" type="text" value="<%=portletState.getAddressLine2()==null ? "" : portletState.getAddressLine2() %>" name="contactAddressLine2" id="contactAddressLine2" placeholder="Provide Second Line of Address" />
				</div>
			  </div>
			  <div style="padding-bottom:5px"><strong>Phone Number of Residence:</strong>
				<div>
				  <input class="clear form-control" onkeypress="return onlyNumKey(event)" type="text" value="<%=portletState.getResidencePhoneNumber()==null ? "" : portletState.getResidencePhoneNumber() %>" name="residencePhoneNumber" id="residencePhoneNumber" placeholder="Provide Phone Number of Residence" />
				</div>
			  </div>
			  <div style="padding-bottom:5px"><strong>City:</strong>
				<div>
				  <input class="clear form-control" type="text" value="<%=portletState.getResidenceCity()==null ? "" : portletState.getResidenceCity() %>" name="residenceCity" id="residenceCity" placeholder="Provide City of Residence" />
				</div>
			  </div>
			  <div style="padding-bottom:5px"><strong>State</strong>
				<div>
			  		<select class="clear form-control" name="residenceState">
						<%
						stateList = portletState.getAllState();
						for(Iterator<State> iter = stateList.iterator(); iter.hasNext();)
						{
							String stateName = iter.next().getName();
							selected = "";
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
		  </fieldset>
		  
		  <fieldset>
			  <legend>Means of Identification
			  </legend>
				  <div style="padding-bottom:5px">
					<div>
					  	<select class="clear form-control" name="identificationMeans" id="identificationMeans" onchange="javascript:handleIdentificationOption()">
							<option value="-1">-Select A Means of Identification-</option>
							<%
							String display1 = "none";
							String display2 = "none";
							String display3 = "none";
							
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
								}
								if(portletState.getIdentificationType()!=null && portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_DRIVERS_LICENSE.getValue()))
								{
									display1 = "none";
									display2 = "block";
									display3 = "none";
								}
								if(portletState.getIdentificationType()!=null && portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_INTL_PASSPORT.getValue()))
								{
									display1 = "none";
									display2 = "none";
									display3 = "block";
								}
							%>
								<option <%=selected%> value="<%=identificationTypeStr %>"><%=identificationTypeStr %></option>
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
							  <input class="clear form-control" type="text" value="<%=portletState.getIdentificationNumber()==null ? "" : portletState.getIdentificationNumber() %>" name="nationalIdNumber" id="nationalIdNumber" placeholder="Provide Your National ID Number" />
							</div>
						</div>
					  </div>
					  
					  <div style="padding-bottom:5px">
						<div>
							<strong>Issue Date:</strong> 
							<div>
							  <input class="clear form-control" type="text" value="<%=portletState.getIssueDate()==null ? "" : portletState.getIssueDate() %>" name="issueDate" id="issueDate" placeholder="YYYY-MM-DD" />
							</div>
						</div>
					  </div>
					  
					  <div style="padding-bottom:5px">
						<div>
							<strong>Expiry Date:</strong> 
							<div>
							  <input class="clear form-control" type="text" value="<%=portletState.getIdentificationExpiryDate()==null ? "" : portletState.getIdentificationExpiryDate() %>" name="identificationExpiryDate" id="identificationExpiryDate" placeholder="YYYY-MM-DD" />
							</div>
						</div>
					  </div>
					  
					  <div style="padding-bottom:5px">
						<div>
							<strong>Place Of Issue:</strong> 
							<div>
							  <input class="clear form-control" type="text" value="<%=portletState.getPlaceOfIssue()==null ? "" : portletState.getPlaceOfIssue() %>" name="placeOfIssue" id="placeOfIssue" placeholder="Provide place of Issuance" />
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
							  <input class="clear form-control" type="text" value="<%=portletState.getIdentificationNumber()==null ? "" : portletState.getIdentificationNumber() %>" name="driversIdNumber" id="driversIdNumber" placeholder="Provide Your Drivers License Number" />
							</div>
						</div>
					  </div>
					  
					  
					  <div style="padding-bottom:5px">
						<div>
							<strong>Expiry Date:</strong> 
							<div>
							  <input class="clear form-control" type="text" value="<%=portletState.getIdentificationExpiryDate()==null ? "" : portletState.getIdentificationExpiryDate() %>" name="driversExpiryDate" id="driversExpiryDate" placeholder="YYYY-MM-DD" />
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
							  <input class="clear form-control" type="text" value="<%=portletState.getIdentificationNumber()==null ? "" : portletState.getIdentificationNumber() %>" name="intlpassportIdNumber" id="intlpassportIdNumber" placeholder="Provide Your National ID Number" />
							</div>
						</div>
					  </div>
					  
					  
					  <div style="padding-bottom:5px">
						<div>
							<strong>Expiry Date:</strong> 
							<div>
							  <input class="clear form-control" type="text" value="<%=portletState.getIdentificationExpiryDate()==null ? "" : portletState.getIdentificationExpiryDate() %>" name="intlpassportExpiryDate" id="intlpassportExpiryDate" placeholder="YYYY-MM-DD" readonly="readonly" />
							</div>
						</div>
					  </div>
				   </div>
		  <fieldset>
		  
		  
		  
		  
		  <fieldset>
			  <legend>Next Of Kin
			  </legend>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Full Name:</strong> 
						<div>
						  <input class="clear form-control" type="text" value="<%=portletState.getNextOfKinName()==null ? "" : portletState.getNextOfKinName() %>" name="nextOfKinFullName" id="nextOfKinFullName" placeholder="Provide Next of Kins Full Name" />
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Address of Residence:</strong> 
				
						<div>
						  <input class="clear form-control" type="text" value="<%=portletState.getNextOfKinAddress()==null ? "" : portletState.getNextOfKinAddress() %>" name="nextOfKinAddress" id="nextOfKinAddress" placeholder="Provide Next of Kin's Address of Residence" />
						</div>
					</div>
				  </div>
				  
				  <div style="padding-bottom:5px">
					<div>
						<strong>How Is Your Next of Kin Related to You?</strong> 
					  	<select class="clear form-control" name="nextOfKinRelationship">
							<option value="-1">-Specify A Relationship-</option>
							<%
							
							for(Iterator<KinRelationshipType> iter = relationship.iterator(); iter.hasNext();)
							{
								String relationshipStr = iter.next().getValue();
								selected="";
								if(portletState.getNextOfKinRelationship()!=null && portletState.getNextOfKinRelationship().equalsIgnoreCase(relationshipStr))
								{
									selected = "selected='selected'";
								}
							%>
								<option <%=selected%> value="<%=relationshipStr %>"><%=relationshipStr %></option>
							<%
							}
							
							%>
						</select>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Next of Kin Phone Number:</strong> 
						<div>
						  <input class="clear form-control" type="text" value="<%=portletState.getNextOfKinPhoneNumber()==null ? "" : portletState.getNextOfKinPhoneNumber() %>" name="nextOfKinPhoneNumber" id="nextOfKinPhoneNumber" placeholder="Provide Next of Kin Phone Number" />
						</div>
					</div>
				  </div>
		  <fieldset>
		  
		  
		  <div style="padding-bottom:5px">
			<div>
				<strong>Just To Confirm You Are Human:</strong> 
				<div>
				  <script type="text/javascript" src="http://api.recaptcha.net/challenge?k=6LfYDv0SAAAAADV3cL5FocZv5w8NhRw5f4V3E0Et">
</script>
<noscript>
<iframe src="http://api.recaptcha.net/noscript?k=6LfYDv0SAAAAADV3cL5FocZv5w8NhRw5f4V3E0Et"
height="300" width="500" frameborder="0">
</iframe><br>
</noscript>
				</div>
			</div>
		  </div>
		  
		  
		  
	      <div>
	      	<input type="hidden" value="" name="act" id="act" />
	      	<button type="submit" class="btn btn-success" style="float:left" onclick="javascript:submitForm('back')">Go Back</button>
	      	<button type="submit" class="btn btn-success" style="float:right" onclick="javascript:submitForm('proceed')">Proceed to Next</button>
	      	<div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
	    All fields with red asterisk (*) imply they must be provided</div>
	        <!-- <input type="submit" name="createportaluserStepOne" value="Proceed to Next" id="createportaluserStepOne" class="floatLeft" style="background-color:#00CC00" />-->
	      </div>
		  
		  
		  
	    </form>
	  </div>
	</div>
</div>



<script type="text/javascript" charset="utf-8" src="<%=jqueryJsUrl%>"></script>
<script type="text/javascript" charset="utf-8" src="<%=jqueryUIJsUrl%>"></script>

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


function handleIdentificationOption()
{
	selected = document.getElementById('identificationMeans').value;
	if(selected=="-1")
	{
		document.getElementById('intlpassportoption').style.display="none";
		document.getElementById('driverslicenseoption').style.display="none";
		document.getElementById('nationalidcardoption').style.display="none";
		
	}
	if(selected=="<%=IdentificationType.IDENTIFICATION_TYPE_NATIONAL_ID.getValue()%>")
	{
		document.getElementById('intlpassportoption').style.display="none";
		document.getElementById('driverslicenseoption').style.display="none";
		document.getElementById('nationalidcardoption').style.display="block";
	}
	if(selected=="<%=IdentificationType.IDENTIFICATION_TYPE_INTL_PASSPORT.getValue()%>")
	{
		document.getElementById('intlpassportoption').style.display="block";
		document.getElementById('driverslicenseoption').style.display="none";
		document.getElementById('nationalidcardoption').style.display="none";
	}
	if(selected=="<%=IdentificationType.IDENTIFICATION_TYPE_DRIVERS_LICENSE.getValue()%>")
	{
		document.getElementById('intlpassportoption').style.display="none";
		document.getElementById('driverslicenseoption').style.display="block";
		document.getElementById('nationalidcardoption').style.display="none";
	}
}



$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#intlpassportExpiryDate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "-20:+40",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yyyy/mm/dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false
	});
	
	
	
});


$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#driversExpiryDate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "-20:+40",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yyyy/mm/dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false
	});
	
	
	
});


$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#identificationExpiryDate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "-20:+40",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yyyy/mm/dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false
	});
	
	
	
});



$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#dob').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "-80:0",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yyyy/mm/dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false
	});
	
	
	
});

</script>