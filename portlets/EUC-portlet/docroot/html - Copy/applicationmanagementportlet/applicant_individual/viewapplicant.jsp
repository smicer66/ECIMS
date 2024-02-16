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
<%@page import="smartpay.entity.enumerations.KinRelationshipType"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.enumerations.RoleConstants"%>
<%@page import="smartpay.entity.RoleType"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Arrays" %>
<%@page import="java.util.List" %>
<%@page import="com.ecims.commins.Util"%>
<%@page import="java.util.Properties" %>
<%@page import="java.io.File" %>
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
List<String> identificationType = IdentificationType.values();
List<String> relationship = KinRelationshipType.values();
String folder = File.separator + "resources" + File.separator + "images"; 
%>

<%

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



<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>
		
		

<div style="padding-left:10px; padding-right:10px; width:900px">
	<h2>Create A User Profile</h2>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Applicant Details</span></div>
	  <div class="panel-body">
	    	
	     <div style="padding-bottom:5px"> <strong>Application Number:<span style="color:red"></span></strong>
	        <div>
	          <%=portletState.getApplication()==null ? "" : portletState.getApplication().getApplicationNumber() %>
	        </div>
	      </div>
		  <div style="padding-bottom:5px"> <strong>Surname:<span style="color:red"></span></strong>
	        <div>
	          <%=portletState.getApplicant().getPortalUser().getSurname()==null ? "" : portletState.getApplicant().getPortalUser().getSurname() %>
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>First Name:<span style="color:red"></span></strong>
	        <div>
	          <%=portletState.getApplicant().getPortalUser().getFirstName()==null ? "" : portletState.getApplicant().getPortalUser().getFirstName() %>
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>Other Name:</strong>
	        <div>
	          <%=portletState.getApplicant().getPortalUser().getOtherName()==null ? "" : portletState.getApplicant().getPortalUser().getOtherName() %>
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>Email Address:<span style="color:red"></span></strong>
	        <div>
	          <%=portletState.getApplicant().getPortalUser().getEmailAddress()==null ? "" : portletState.getApplicant().getPortalUser().getEmailAddress() %>
	        </div>
	      </div>
		  <div style="padding-bottom:5px; clear:both"><div><strong>Mobile Number:<span style="color:red"></span></strong></div>
	        <div>
				<div style="float:left; padding-right:10px">
					<%=portletState.getApplicant().getPortalUser().getPhoneNumber()==null ? "N/A" : "+" + portletState.getApplicant().getPortalUser().getPhoneNumber()%>
			  	</div>
	        </div>
	      </div>
		  <div style="padding-bottom:5px; clear:both"><div><strong>Nationality:<span style="color:red"></span></strong></div>
	        <div>
				<div style="padding-right:10px">
					<%=portletState.getApplicant().getPortalUser().getState()==null ? "N/A" : portletState.getApplicant().getPortalUser().getState().getCountry().getName() %>
				</div>
	        </div>
	      </div>
		  <div style="padding-bottom:5px; clear:both"><div><strong>State:<span style="color:red"></span></strong></div>
	        <div>
				<div style="padding-right:10px">
						<%=portletState.getApplicant().getPortalUser().getState()==null ? "N/A" : portletState.getApplicant().getPortalUser().getState() %>
				</div>
	        </div>
	      </div>
		  <div style="padding-bottom:5px"><strong>Gender:</strong>
	        <div>
			<%=portletState.getApplicant().getPortalUser().getGender()==null ? "N/A" : portletState.getApplicant().getPortalUser().getGender().equals("1") ? "Male" : "Female" %>
			  
	        </div>
	      </div>
		  <div style="padding-bottom:5px"><strong>Passport Photo:</strong>
	        <div>
			<img src="<%=folder + File.separator + "uploadedfiles" + File.separator + portletState.getApplicant().getPortalUser().getPassportPhoto()%>" height="50px;">
			  
	        </div>
	      </div>
		  
		  <div style="padding-bottom:5px"><strong>Marital Status:</strong>
	        <div>
			<%= portletState.getApplicant().getPortalUser().getMaritalStatus()==null ? "N/A" : portletState.getApplicant().getPortalUser().getMaritalStatus()%>
			  
	        </div>
	      </div>
		  <div style="padding-bottom:5px"><strong>Address:</strong>
	        <div>
	            <%=portletState.getApplicant().getPortalUser().getAddressLine1()==null ? "N/A" : portletState.getApplicant().getPortalUser().getAddressLine1() %>
	        	<%=portletState.getApplicant().getPortalUser().getAddressLine1()!=null ? (portletState.getApplicant().getPortalUser().getAddressLine2()==null ? "" : ("<br>" + portletState.getApplicant().getPortalUser().getAddressLine2())) : "" %>
	        </div>
	      </div>
		  
		  <fieldset>
			  <legend>Means of Identification
			  </legend>
				  <div style="padding-bottom:5px">
					<div>
					  	<%=portletState.getApplicant().getIdentificationType()==null ? "N/A" : portletState.getApplicant().getIdentificationType().getValue()%>
						</select>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Your National ID Photo:</strong>
					  	<img src="<%=folder + File.separator + "uploadedfiles" + File.separator + portletState.getApplicant().getIdentificationPhoto()%>" height="50px;">
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Your National ID Number:</strong> 
						<div>
						  <%=portletState.getApplicant().getIdentificationNumber()==null ? "" : portletState.getApplicant().getIdentificationNumber() %>
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
						  <%=portletState.getApplicant().getNextOfKinName()==null ? "" : portletState.getApplicant().getNextOfKinName() %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Address of Residence:</strong> 
						<div>
						  <%=portletState.getApplicant().getNextOfKinAddress()==null ? "N/A" : portletState.getApplicant().getNextOfKinAddress() %>
						</div>
					</div>
				  </div>
				  
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Relationship:</strong> 
						<%=portletState.getApplicant().getNextOfKinRelationship()==null ? "N/A" : portletState.getApplicant().getNextOfKinRelationship().getValue() %>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Next of Kin Phone Number:</strong> 
						<div>
						  <%=portletState.getApplicant().getNextOfKinPhoneNumber()==null ? "N/A" : portletState.getApplicant().getNextOfKinPhoneNumber() %>
						</div>
					</div>
				  </div>
		  <fieldset>
		  
		  
	      <div>
	      	<input type="hidden" value="" name="act" id="act" />
	      	<button type="submit" class="btn btn-success" style="float:left" onclick="javascript:submitForm('back')">Go Back</button>
	      	<button type="submit" class="btn btn-success" style="float:right" onclick="javascript:submitForm('proceed')" >Create My Account</button>
	      	<div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
	    All fields with red asterisk () imply they must be provided</div>
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