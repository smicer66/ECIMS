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
<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.entity.Company"%>
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

UserManagementPortletState portletState = UserManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(UserManagementPortletState.class);
List<String> identificationType = IdentificationType.values();
List<String> relationship = KinRelationshipType.values();
String folder = File.separator + "resources" + File.separator + "images";
PortalUser pu = portletState.getUserManagementPortletUtil().getPortalUserByApplicant(portletState.getSelectedApplicant());
Company company = portletState.getUserManagementPortletUtil().getCompanyByPortalUser(pu);
Country country =  portletState.getUserManagementPortletUtil().getCountryByState(pu.getState());
%>

<jsp:include page="/html/usermanagementportlet/tabs.jsp" flush="" />


<portlet:actionURL var="proceedToStepFour" name="processAction">
	<portlet:param name="action"
		value="<%=EUC_DESK_ACTIONS.APPROVE_PORTAL_USER_SIGNUP.name()%>" />
</portlet:actionURL>


<div style="padding-left:10px; padding-right:10px; width:900px">
	<h2>Applicant Details</h2>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">View Applicant Details</span></div>
	  <div class="panel-body">
	    	
	    <form  id="startRegFormId" action="<%=proceedToStepFour%>" method="post" enctype="application/x-www-form-urlencoded">
	     
		  <div style="padding-bottom:5px"> <strong>Surname:</strong>
	        <div>
	          <%=portletState.getSelectedApplicant().getPortalUser().getSurname()==null ? "N/A" : portletState.getSelectedApplicant().getPortalUser().getSurname() %>
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>First Name:</strong>
	        <div>
	          <%=portletState.getSelectedApplicant().getPortalUser().getFirstName()==null ? "N/A" : portletState.getSelectedApplicant().getPortalUser().getFirstName() %>
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>Other Name:</strong>
	        <div>
	          <%=portletState.getSelectedApplicant().getPortalUser().getOtherName()==null ? "N/A" : portletState.getSelectedApplicant().getPortalUser().getOtherName() %>
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>Email Address:</strong>
	        <div>
				<%=portletState.getSelectedApplicant().getPortalUser().getEmailAddress()==null ? "" : portletState.getSelectedApplicant().getPortalUser().getEmailAddress() %>
	        </div>
	      </div>
		  
		  <div style="padding-bottom:5px">
			<div>
				<strong>Your Passport Photo:</strong> <img src="<%=folder + File.separator + "uploadedfiles" + File.separator + portletState.getSelectedApplicant().getPortalUser().getPassportPhoto()%>" height="50px;">
			</div>
		  </div>
	      
	      <div style="padding-bottom:5px"><strong>Date of Birth:</strong>
	        <div>
				<%=portletState.getSelectedApplicant().getPortalUser().getDateOfBirth()==null ? "N/A" : portletState.getSelectedApplicant().getPortalUser().getDateOfBirth() %>
	        </div>
	      </div>
		  
		  <div style="padding-bottom:5px; clear:both"><div><strong>Nationality:</strong></div>
	        <div>
				<div style="padding-right:10px">
						<%= country.getName()!=null ? country.getName() : "N/A"%>
				</div>
	        </div>
	      </div>
		  
		  <div style="padding-bottom:5px; clear:both"><div><strong>State:</strong></div>
	        <div>
				<div style="padding-right:10px">
					<%=portletState.getSelectedApplicant().getPortalUser().getState().getName()!=null ? portletState.getSelectedApplicant().getPortalUser().getState() : "N/A" %>
				</div>
	        </div>
	      </div>
		  
		  <div style="padding-bottom:5px"><strong>Gender:</strong>
	        <div>
			<%= portletState.getSelectedApplicant().getPortalUser().getGender()==null ? "N/A" : portletState.getSelectedApplicant().getPortalUser().getGender().equals("1") ? "Male" : "Female" %>
	        </div>
	      </div>
	      
	      <div style="padding-bottom:5px"><strong>Designation</strong>
	        <div>
				<%=portletState.getSelectedApplicant().getPortalUser().getDesignation()==null ? "N/A" : portletState.getSelectedApplicant().getPortalUser().getDesignation() %>
	        </div>
	      </div>
		  
		  <div style="padding-bottom:5px; clear:both"><div><strong>Mobile Number:</strong></div>
	        <div>
				<div style="float:left; padding-right:10px"><%=portletState.getSelectedApplicant().getPortalUser().getPhoneNumber() %>
			  	</div>
	        </div>
	      </div>
		  
		  
		  
		  <fieldset>
			  <legend>Company Details
			  </legend>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Company Name:</strong> 
						<div>
						  <%=portletState.getSelectedApplicant().getPortalUser().getCompany().getName()==null ? "N/A" : portletState.getSelectedApplicant().getPortalUser().getCompany().getName() %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Address of Company:</strong> 
				
						<div>
						  <%=portletState.getSelectedApplicant().getPortalUser().getCompany().getAddress()==null ? "N/A" : portletState.getSelectedApplicant().getPortalUser().getCompany().getAddress() %>
						</div>
					</div>
				  </div>
				  
				  <div style="padding-bottom:5px"><strong>State where Company Resides:</strong>
					<div><%=portletState.getSelectedApplicant().getPortalUser().getState()!=null ? portletState.getSelectedApplicant().getPortalUser().getState() : "N/A" %>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Company Contact Phone Number:</strong> 
						<div>
						  <%=portletState.getSelectedApplicant().getPortalUser().getCompany().getPhoneNumber()==null ? "N/A" : portletState.getSelectedApplicant().getPortalUser().getCompany().getPhoneNumber() %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Company Contact Email Address:</strong> 
						<div>
						  <%=portletState.getSelectedApplicant().getPortalUser().getCompany().getEmailAddress()==null ? "N/A" : portletState.getSelectedApplicant().getPortalUser().getEmailAddress() %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Company Website URL:</strong> 
						<div>
						  <%=portletState.getSelectedApplicant().getPortalUser().getCompany().getWebsite()==null ? "N/A" : portletState.getSelectedApplicant().getPortalUser().getCompany().getWebsite() %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Company's CAC Number:</strong> 
						<div>
						  <%=portletState.getSelectedApplicant().getPortalUser().getCompany().getRegNo()==null ? "N/A" : portletState.getSelectedApplicant().getPortalUser().getCompany().getRegNo() %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Date Of Incorporation: </strong> 
						<div>
						  <%=portletState.getSelectedApplicant().getPortalUser().getCompany().getCertificateDate()==null ? "N/A" : portletState.getSelectedApplicant().getPortalUser().getCompany().getCertificateDate() %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Your Company Logo:</strong> <img src="<%=folder + File.separator + "uploadedfiles" + File.separator + portletState.getSelectedApplicant().getPortalUser().getCompany().getLogo()%>" height="50px;">
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Your Company's Certificate of Incorporation: <img src="<%=folder + File.separator + "uploadedfiles" + File.separator + portletState.getSelectedApplicant().getPortalUser().getCompany().getCertificate()%>" height="50px;">
					</div>
				  </div>
		  <fieldset>
		  
		  
		  
		  
	      <div>
	      	<input type="hidden" value="" name="act" id="act" />
	      	<button type="submit" class="btn btn-danger" style="float:left" onclick="javascript:submitForm('back')">Go Back</button>
	      	<button type="submit" class="btn btn-danger" style="float:right" onclick="javascript:submitForm('reject')">Reject SignUp Request</button>
	      	<button type="submit" class="btn btn-success" style="float:right" onclick="javascript:submitForm('approve')" >Approve SignUp Request</button>
	      	
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