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
<%

GuestPortletState portletState = GuestPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(GuestPortletState.class);
List<String> identificationType = IdentificationType.values();
List<String> relationship = KinRelationshipType.values();


%>

<portlet:actionURL var="proceedToStepThree" name="processAction">
	<portlet:param name="action"
		value="<%=APPLICANT_ACTIONS.CREATE_A_CORPORATE_PORTAL_USER_STEP_TWO.name()%>" />
</portlet:actionURL>


<div style="padding-left:10px; padding-right:10px; width:900px">
	<h4>Create A Corporation ECIMS Profile Account</h4>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Step 2 of 3: Bio-Data (Corporation Account)</span></div>
	  <div class="panel-body">
	    	
	    <form  id="startRegFormId" action="<%=proceedToStepThree%>" method="post" enctype="multipart/form-data">
	     
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
		  
		  <div style="padding-bottom:5px">
			<div>
				<strong>Your Passport Photo:</strong> <input type="file" name="passportPhoto">
			</div>
		  </div>
	      
	      <div style="padding-bottom:5px"><strong>Date of Birth:<span style="color:red">*</span></strong>
	        <div>
	          <input class="clear form-control" type="text" value="<%=portletState.getDob()==null ? "" : portletState.getDob() %>" name="dob" id="dob" placeholder="YYYY-MM-DD" />
	        </div>
	      </div>
		  
		  <div style="padding-bottom:5px; clear:both"><div><strong>Nationality:<span style="color:red">*</span></strong></div>
	        <div>
				<div style="padding-right:10px">
					<select class="form-control" name="nationality">
						<%
						Collection<Country> countryList = portletState.getAllCountry();
						
						for(Iterator<Country> iter = countryList.iterator(); iter.hasNext();)
						{
							String countryName = iter.next().getName();
							String selected = "";
							if(portletState.getNationality()!=null && portletState.getNationality().equals(countryName))
							{
								selected = "selected='selected'";
							}
						%>
						<option <%=selected%> value="<%=countryName%>"><%=countryName%></option>
						<%
						}
						%>
					</select>
				</div>
	        </div>
	      </div>
		  
		  <div style="padding-bottom:5px; clear:both"><div><strong>State:<span style="color:red">*</span></strong></div>
	        <div>
				<div style="padding-right:10px">
					<select class="form-control" name="state">
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
	        <div>
			<%
			if(portletState.getGender()==null)
			{
			%>
				<input class="clear form-control" type="radio" value="1" name="gender" id="gender" />Male
			  	<input class="clear form-control" type="radio" value="0" name="gender" id="gender" />Female
			<%
			}else if(portletState.getGender().equals("1"))
			{
			%>
				<input class="clear form-control" checked="checked" type="radio" value="1" name="gender" id="gender" />Male
			  	<input class="clear form-control" type="radio" value="0" name="gender" id="gender" />Female
			<%
			}else if(portletState.getGender().equals("0"))
			{
			%>
				<input class="clear form-control" type="radio" value="1" name="gender" id="gender" />Male
			  	<input class="clear form-control" checked="checked" type="radio" value="0" name="gender" id="gender" />Female
			<%
			}
			%>
			  
	        </div>
	      </div>
	      
	      <div style="padding-bottom:5px"><strong>Designation<span style="color:red">*</span></strong>
	        <div>
	          <input class="clear form-control" type="text" value="<%=portletState.getDesignation()==null ? "" : portletState.getDesignation() %>" name="designation" id="designation" placeholder="Your Designation in the Company" />
	        </div>
	      </div>
		  
		  <div style="padding-bottom:5px; clear:both"><div><strong>Mobile Number:<span style="color:red">*</span></strong></div>
	        <div>
				<div style="float:left; padding-right:10px">
					<select class="form-control" name="countryCode">
						<%
						countryList = portletState.getAllCountry();
						
						
						for(Iterator<Country> iter = countryList.iterator(); iter.hasNext();)
						{
							Country country = iter.next();
							String selected = "";
							if(portletState.getCountryCode()!=null && portletState.getCountryCode().equals(country.getCode()))
							{
								selected = "selected='selected'";
							}
							%>
							<option <%=selected%> value="<%=country.getCode()%>">+<%=country.getCode()%></option>
							<%
						}
						%>
					</select>
				</div>
				<div style="float:left">
	          <input onkeypress="return onlyNumKey(event)" class="form-control" type="text" value="<%=portletState.getMobile()==null ? "" : portletState.getMobile() %>" name="mobile" id="mobile" placeholder="e.g. 9XXXXXXXXX" />
			  	</div>
	        </div>
	      </div>
		  
		  
		  
		  <fieldset>
			  <legend>Company Details
			  </legend>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Company Name:<span style="color:red">*</span></strong> 
						<div>
						  <input class="clear form-control" type="text" value="<%=portletState.getCompanyName()==null ? "" : portletState.getCompanyName() %>" name="companyName" id="companyName" placeholder="Provide Your Company Name" />
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Address of Company:<span style="color:red">*</span></strong> 
				
						<div>
						  <input class="clear form-control" type="text" value="<%=portletState.getCompanyAddress()==null ? "" : portletState.getCompanyAddress() %>" name="companyAddress" id="companyAddress" placeholder="Provide Address of Company" />
						</div>
					</div>
				  </div>
				  
				  <div style="padding-bottom:5px">
					<div><span style="color:red">*</span>
					  	<select class="form-control" name="companyState">
							<option value="-1">-What State Is Your Company Located-</option>
							<%
							stateList = portletState.getAllState();
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
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Company Contact Phone Number:<span style="color:red">*</span></strong> 
						<div>
						  <input class="clear form-control" type="text" value="<%=portletState.getCompanyPhoneNumber()==null ? "" : portletState.getCompanyPhoneNumber() %>" name="companyPhoneNumber" id="companyPhoneNumber" placeholder="Provide Company's Phone Number" />
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Company Contact Email Address:</strong> 
						<div>
						  <input class="clear form-control" type="text" value="<%=portletState.getCompanyEmailAddress()==null ? "" : portletState.getCompanyEmailAddress() %>" name="companyEmailAddress" id="companyEmailAddress" placeholder="Provide Email Address of the Company" />
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Company Website URL:</strong> 
						<div>
						  <input class="clear form-control" type="text" value="<%=portletState.getWebsiteUrl()==null ? "" : portletState.getWebsiteUrl() %>" name="websiteUrl" id="websiteUrl" placeholder="Provide Company Website URL" />
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Company's CAC Number:</strong> 
						<div>
						  <input class="clear form-control" type="text" value="<%=portletState.getRegistrationNumber()==null ? "" : portletState.getRegistrationNumber() %>" name="cacNo" id="cacNo" placeholder="Provide Company's Registration Number" />
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Date Of Incorporation:<span style="color:red">*</span></strong> 
						<div>
						  <input class="clear form-control" type="text" value="<%=portletState.getDateOfIncorporation()==null ? "" : portletState.getDateOfIncorporation() %>" name="dateOfIncorporation" id="dateOfIncorporation" placeholder="YYYY-MM-DD" />
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Your Company Logo:</strong> <input type="file" name="companyLogo">
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Your Company's Certificate of Incorporation:<span style="color:red">*</span></strong> <input type="file" name="cacCertificate">
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
	      	<button type="submit" class="btn btn-success" style="float:left" onclick="javascript:submitForm('cancel')">Go Back</button>
	      	<button type="submit" class="btn btn-success" style="float:right" onclick="javascript:submitForm('proceed')">Proceed to Next</button>
	      	<div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
	    All fields with red asterisk (*) imply they must be provided</div>
	        <!-- <input type="submit" name="createportaluserStepOne" value="Proceed to Next" id="createportaluserStepOne" class="floatLeft" style="background-color:#00CC00" />-->
	      </div>
	    </form>
	  </div>
	</div>
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