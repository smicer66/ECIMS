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
//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//String folder = "/resources/images";

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

<portlet:actionURL var="proceed" name="processAction">
	<portlet:param name="action"
		value="<%=APPLIST_ACTIONS.ACT_ON_APPLICATION.name()%>" />
</portlet:actionURL>

<div style="padding-left:10px; padding-right:10px; width:900px" id="print-content">
	<h2>Applicant Profile</h2>
	<form  id="appListFormId" action="<%=proceed%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Step 3 of 3: Preview Bio-Data (Corporation Account)</span></div>
	  <div class="panel-body">
	    	
	      <div style="padding-bottom:5px"> <strong>Application Number:<span style="color:red"></span></strong>
	        <div>
	          <%=portletState.getApplication()==null ? "" : portletState.getApplication().getApplicationNumber() %>
	        </div>
	      </div>
		  <div style="padding-bottom:5px"> <strong>Surname:</strong>
	        <div>
	          <%=portletState.getApplicant().getPortalUser().getSurname()==null ? "N/A" : portletState.getApplicant().getPortalUser().getSurname() %>
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>First Name:</strong>
	        <div>
	          <%=portletState.getApplicant().getPortalUser().getFirstName()==null ? "N/A" : portletState.getApplicant().getPortalUser().getFirstName() %>
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>Other Name:</strong>
	        <div>
	          <%=portletState.getApplicant().getPortalUser().getOtherName()==null ? "N/A" : portletState.getApplicant().getPortalUser().getOtherName() %>
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>Email Address:</strong>
	        <div>
				<%=portletState.getApplicant().getPortalUser().getEmailAddress()==null ? "" : portletState.getApplicant().getPortalUser().getEmailAddress() %>
	        </div>
	      </div>
		  
		  <div style="padding-bottom:5px">
			<div>
				<strong>Passport Photo:</strong> <img src="<%="/resources/images/uploadedfiles/" + portletState.getApplicant().getPortalUser().getPassportPhoto()%>" height="50px;">
			</div>
		  </div>
	      
	      <div style="padding-bottom:5px"><strong>Date of Birth:</strong>
	        <div>
				<%=portletState.getApplicant().getPortalUser().getDateOfBirth()==null ? "N/A" : portletState.getApplicant().getPortalUser().getDateOfBirth() %>
	        </div>
	      </div>
		  
		  <div style="padding-bottom:5px; clear:both"><div><strong>Nationality:</strong></div>
	        <div>
				<div style="padding-right:10px">
						<%= portletState.getApplicant().getPortalUser().getState()!=null ? portletState.getApplicant().getPortalUser().getState().getCountry().getName() : "N/A"%>
				</div>
	        </div>
	      </div>
		  
		  <div style="padding-bottom:5px; clear:both"><div><strong>State:</strong></div>
	        <div>
				<div style="padding-right:10px">
					<%=portletState.getApplicant().getPortalUser().getState()!=null ? portletState.getApplicant().getPortalUser().getState().getName() : "N/A" %>
				</div>
	        </div>
	      </div>
		  
		  <div style="padding-bottom:5px"><strong>Gender:</strong>
	        <div>
			<%= portletState.getApplicant().getPortalUser().getGender()==null ? "N/A" : portletState.getApplicant().getPortalUser().getGender().equals("1") ? "Male" : "Female" %>
	        </div>
	      </div>
	      
	      <div style="padding-bottom:5px"><strong>Designation</strong>
	        <div>
				<%=portletState.getApplicant().getPortalUser().getDesignation()==null ? "N/A" : portletState.getApplicant().getPortalUser().getDesignation() %>
	        </div>
	      </div>
	      
	      
		  <div style="padding-bottom:5px">
			  	<strong>Tax Identification Number:</strong>
			<div>
				<%=portletState.getApplicant().getPortalUser().getTaxIdNo()==null ? "N/A" : portletState.getApplicant().getPortalUser().getTaxIdNo() %>
			</div>
		  </div>
		  
		  <div style="padding-bottom:5px; clear:both"><div><strong>Mobile Number:</strong></div>
	        <div>
				<div style="float:left; padding-right:10px"><%=portletState.getApplicant().getPortalUser().getPhoneNumber() %>
			  	</div>
	        </div>
	      </div>
		  <div style="padding-bottom:5px"><strong>Passport Photo:</strong>
	        <div>
			<img src="<%="/resources/images/uploadedfiles/" + portletState.getApplicant().getPortalUser().getPassportPhoto()%>" height="50px;">
			<div><a target="blank" href="<%="/resources/images/uploadedfiles/" + (portletState.getApplicant().getPortalUser().getPassportPhoto()==null ? "" : portletState.getApplicant().getPortalUser().getPassportPhoto())%>">View In Large Size</a></div>  
	        </div>
	      </div>
		  
		  
		  <fieldset>
			  <legend>Proof of Identification
			  </legend>
				  <div style="padding-bottom:5px"><strong>Means of Identification:</strong>
					<div>
					  	<%=(portletState.getApplicant().getPortalUser().getIdentificationHistory()!=null && portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationType()!=null) ? portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationType().getValue() : "N/A"%>
						</select>
					</div>
				  </div>
				  <%
				  if(portletState.getApplicant().getPortalUser().getIdentificationHistory()==null)
				  {
					  
				  }else
				  {
					  if(portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationType()!=null && portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationType().getValue().equals(IdentificationType.IDENTIFICATION_TYPE_NATIONAL_ID.getValue()))
					  {
					  %>
					  <div style="padding-bottom:5px">
						<div>
						  	<strong>National ID Photo:</strong><br/>
						  	<img src="<%="/resources/images/uploadedfiles/" + (portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto()==null ? "" : portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto())%>" height="50px;">
						  	<div><a target="blank" href="<%="/resources/images/uploadedfiles/" + (portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto()==null ? "" : portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto())%>">View In Large Size</a></div>
						</div>
					  </div>
					  <div style="padding-bottom:5px">
						<div>
						  	<strong>National ID Number:</strong> 
							<div>
							  <%=portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationNumber()==null ? "" : portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationNumber() %>
							</div>
						</div>
					  </div>
					  <div style="padding-bottom:5px">
						<div>
						  	<strong>Issue Date:</strong>
						  	<%=portletState.getApplicant().getPortalUser().getIdentificationHistory().getIssueDate()!=null ? portletState.getApplicant().getPortalUser().getIdentificationHistory().getIssueDate() : "N/A"%>
						</div>
					  </div>
					  <%
					  }else if(portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationType()!=null && portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationType().getValue().equals(IdentificationType.IDENTIFICATION_TYPE_DRIVERS_LICENSE.getValue()))
					  {
					  %>
					  <div style="padding-bottom:5px">
						<div>
						  	<strong>Drivers License Photo:</strong><br/>
						  	<img src="<%="/resources/images/uploadedfiles/" + (portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto()==null ? "" : portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto())%>" height="50px;">
						  	<div><a target="blank" href="<%="/resources/images/uploadedfiles/" + (portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto()==null ? "" : portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto())%>">View In Large Size</a></div>
						</div>
					  </div>
					  <div style="padding-bottom:5px">
						<div>
						  	<strong>Drivers License Number:</strong> 
							<div>
							  <%=portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationNumber()==null ? "" : portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationNumber() %>
							</div>
						</div>
					  </div>
					  <div style="padding-bottom:5px">
						<div>
						  	<strong>Expiry Date:</strong> 
							<div>
							  <%=portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationExpiryDate()!=null ? portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationExpiryDate() : "N/A"%>
							</div>
						</div>
					  </div>
					  
					  
					  
					  <div style="padding-bottom:5px">
						<div>
						  	<strong>Place of Issue:</strong> 
							<div>
							  <%=portletState.getApplicant().getPortalUser().getIdentificationHistory().getPlaceOfIssue()==null ? "" : portletState.getApplicant().getPortalUser().getIdentificationHistory().getPlaceOfIssue() %>
							</div>
						</div>
					  </div>
					  
					  <div style="padding-bottom:5px">
						<div>
						  	<strong>Issue Date:</strong> 
							<div>
							  <%=portletState.getApplicant().getPortalUser().getIdentificationHistory().getIssueDate()!=null ? portletState.getApplicant().getPortalUser().getIdentificationHistory().getIssueDate() : "N/A"%>
							</div>
						</div>
					  </div>
					  <%
					  }else if(portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationType()!=null && portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationType().getValue().equals(IdentificationType.IDENTIFICATION_TYPE_INTL_PASSPORT.getValue()))
					  {
					  %>
					  <div style="padding-bottom:5px">
						<div>
						  	<strong>International Passport Photo:</strong><br/>
						  	<img src="<%="/resources/images/uploadedfiles/" + (portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto()==null ? "" : portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto())%>" height="50px;">
						  	<div><a target="blank" href="<%="/resources/images/uploadedfiles/" + (portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto()==null ? "" : portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto())%>">View In Large Size</a></div>
						</div>
					  </div>
					  <div style="padding-bottom:5px">
						<div>
						  	<strong>International Passport Number:</strong> 
							<div>
							  <%=portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationNumber()==null ? "" : portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationNumber() %>
							</div>
						</div>
					  </div>
					  
					  <div style="padding-bottom:5px">
						<div>
						  	<strong>Expiry Date:</strong> 
							<div>
							  <%=portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationExpiryDate()!=null ? portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationExpiryDate() : "N/A"%>
							</div>
						</div>
					  </div>
					  
					  <div style="padding-bottom:5px">
						<div>
						  	<strong>Place of Issue:</strong> 
							<div>
							  <%=portletState.getApplicant().getPortalUser().getIdentificationHistory().getPlaceOfIssue()==null ? "" : portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationNumber() %>
							</div>
						</div>
					  </div>
					  
					  <div style="padding-bottom:5px">
						<div>
						  	<strong>Issue Date:</strong> 
							<div>
							  <%=portletState.getApplicant().getPortalUser().getIdentificationHistory().getIssueDate()!=null ? portletState.getApplicant().getPortalUser().getIdentificationHistory().getIssueDate() : "N/A"%>
							</div>
						</div>
					  </div>
					  <%
					  }
					  else if(portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationType()!=null && portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationType().getValue().equals(IdentificationType.IDENTIFICATION_TYPE_PVC.getValue()))
					  {
					  %>
					  <div style="padding-bottom:5px">
						<div>
						  	<strong>Your Permanent Voters Card Photo:</strong><br/>
						  	<a target="blank" href="<%="/resources/images/uploadedfiles/" + (portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto()==null ? "" : portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto())%>"><img src="<%="/resources/images/uploadedfiles/" + (portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto()==null ? "" : portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto())%>" height="50px;"></a>
						  	<div><a target="blank" href="<%="/resources/images/uploadedfiles/" + (portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto()==null ? "" : portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto())%>">View In Large Size</a></div>
						</div>
					  </div>
					  <div style="padding-bottom:5px">
						<div>
						  	<strong>Your Permanent Voters Card Number:</strong> 
							<div>
							  <%=portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationNumber()==null ? "" : portletState.getApplicant().getPortalUser().getIdentificationHistory().getIdentificationNumber() %>
							</div>
						</div>
					  </div>
					  
					  <div style="padding-bottom:5px">
						<div>
						  	<strong>Issue Date:</strong> 
							<div>
							  <%=portletState.getApplicant().getPortalUser().getIdentificationHistory().getIssueDate()!=null ? portletState.getApplicant().getPortalUser().getIdentificationHistory().getIssueDate() : "N/A"%>
							</div>
						</div>
					  </div>
					  <%
					  }
				  }
				  
				  %>
		  <fieldset>
		  
		  <fieldset>
			  <legend>Company Details
			  </legend>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Company Name:</strong> 
						<div>
						  <%=portletState.getApplicant().getPortalUser().getCompany()==null ? "N/A" : portletState.getApplicant().getPortalUser().getCompany().getName() %>
						</div>
					</div>
				  </div>
			  	<%
				if(!portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER))
				{
				%>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Address of Company:</strong> 
				
						<div>
						  <%=portletState.getApplicant().getPortalUser().getCompany()==null ? "N/A" : portletState.getApplicant().getPortalUser().getCompany().getAddress() %>
						</div>
					</div>
				  </div>
				  
				  <div style="padding-bottom:5px">
					<div><%=portletState.getApplicant().getPortalUser().getState()!=null ? portletState.getApplicant().getPortalUser().getState().getName() : "N/A" %>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Company Contact Phone Number:</strong> 
						<div>
						  <%=portletState.getApplicant().getPortalUser().getCompany()==null ? "N/A" : portletState.getApplicant().getPortalUser().getCompany().getPhoneNumber() %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Company Contact Email Address:</strong> 
						<div>
						  <%=portletState.getApplicant().getPortalUser().getCompany()==null ? "N/A" : portletState.getApplicant().getPortalUser().getCompany().getEmailAddress() %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Company Website URL:</strong> 
						<div>
						  <%=portletState.getApplicant().getPortalUser().getCompany().getWebsite()==null ? "N/A" : portletState.getApplicant().getPortalUser().getCompany().getWebsite() %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Company's CAC Number:</strong> 
						<div>
						  <%=portletState.getApplicant().getPortalUser().getCompany().getRegNo()==null ? "N/A" : portletState.getApplicant().getPortalUser().getCompany().getRegNo() %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Date Of Incorporation: </strong> 
						<div>
						  <%=portletState.getApplicant().getPortalUser().getCompany()==null ? "N/A" : portletState.getApplicant().getPortalUser().getCompany().getCertificateDate() %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Your Company Logo:</strong> <img src="<%="/resources/images/uploadedfiles/" + (portletState.getApplicant().getPortalUser().getCompany().getLogo()==null ? "" : portletState.getApplicant().getPortalUser().getCompany().getLogo())%>" height="50px;">
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Your Company's Certificate of Incorporation: <br>
					  	<img src="<%="/resources/images/uploadedfiles/" + (portletState.getApplicant().getPortalUser().getCompany().getCertificate()==null ? "" : portletState.getApplicant().getPortalUser().getCompany().getCertificate())%>" height="50px;">
					  	<div><a target="blank" href="<%="/resources/images/uploadedfiles/" + (portletState.getApplicant().getPortalUser().getCompany().getCertificate()==null ? "" : portletState.getApplicant().getPortalUser().getCompany().getCertificate())%>">Download or View Document</a></div>
					</div>
				  </div>
				  <%
				}
				  %>
		  <fieldset>
		  
		  
		  <div>
	      	<input type="hidden" value="" name="act" id="act" />
	      	<input type="hidden" value="<%=portletState.getApplicationWorkflow()==null? "" : portletState.getApplicationWorkflow().getId() %>" name="actId" id="actId" />
	      	
	      	<button type="submit" class="btn btn-success" style="float:left" onclick="javascript:submitForm('backtoapplicationview')">Go Back</button>	      	
	        <!-- <input type="submit" name="createportaluserStepOne" value="Proceed to Next" id="createportaluserStepOne" class="floatLeft" style="background-color:#00CC00" />-->
	      </div>
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
	document.getElementById('startRegFormId').submit;
	
}
</script>