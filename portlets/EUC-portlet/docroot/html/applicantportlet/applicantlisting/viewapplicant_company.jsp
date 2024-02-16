<%@page import="com.ecims.portlet.applicant.ApplicantPortletState"%>
<%@page import="com.ecims.portlet.applicant.ApplicantPortletState.*"%>
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
<%@page import="smartpay.entity.enumerations.PermissionType"%>
<%@page import="smartpay.entity.enumerations.ApplicantStatus"%>
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

ApplicantPortletState portletState = ApplicantPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ApplicantPortletState.class);
List<String> identificationType = IdentificationType.values();
List<String> relationship = KinRelationshipType.values();
String folder = "/" + "resources" + "/" + "images";
PortalUser pu = portletState.getApplicantPortletUtil().getPortalUserByApplicant(portletState.getSelectedApplicant());
Company company = portletState.getApplicantPortletUtil().getCompanyByPortalUser(pu);
Country country =  portletState.getApplicantPortletUtil().getCountryByState(pu.getState());
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
%>

<jsp:include page="/html/applicantportlet/tabs.jsp" flush="" />


<portlet:actionURL var="proceedToStepFour" name="processAction">
	<portlet:param name="action"
		value="<%=EUC_DESK_ACTIONS.APPROVE_PORTAL_USER_SIGNUP.name()%>" />
</portlet:actionURL>


<div style="padding-left:10px; padding-right:10px; width:900px" id="print-content">
	<h2>Applicant Profile</h2>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">View Applicant Details</span></div>
	  <div class="panel-body">
	    	
	    <form  id="startRegFormId" action="<%=proceedToStepFour%>" method="post" enctype="application/x-www-form-urlencoded">
	     
	     	<fieldset>
			  <legend>Contact Person's Bio Data
			  </legend>
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
					<strong>Contact Persons Passport Photo:</strong> <br>
					<a style="border:0px;" target="blank" href="<%=folder + "/uploadedfiles/" + (portletState.getSelectedApplicant().getPortalUser().getPassportPhoto()==null ? "" : portletState.getSelectedApplicant().getPortalUser().getPassportPhoto())%>">
					<img style="border:0px;" src="<%=folder + "/" + "uploadedfiles" + "/" + portletState.getSelectedApplicant().getPortalUser().getPassportPhoto()%>" height="50px;"></a>
					<div><a target="blank" href="<%=folder + "/uploadedfiles/" + (portletState.getSelectedApplicant().getPortalUser().getPassportPhoto()==null ? "" : portletState.getSelectedApplicant().getPortalUser().getPassportPhoto())%>">View In Large Size</a></div>
				</div>
			  </div>
		      
		      <div style="padding-bottom:5px"><strong>Date of Birth:</strong>
		        <div>
					<%=portletState.getSelectedApplicant().getPortalUser().getDateOfBirth()==null ? "N/A" : 
						sdf.format(portletState.getSelectedApplicant().getPortalUser().getDateOfBirth()) %>
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
						<%=portletState.getSelectedApplicant().getPortalUser().getState().getName()!=null ? portletState.getSelectedApplicant().getPortalUser().getState().getName() : "N/A" %>
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
		      
		      
			  <div style="padding-bottom:5px">
				  	<strong>Tax Identification Number:</strong>
				<div>
					<%=portletState.getSelectedApplicant().getPortalUser().getTaxIdNo()==null ? "N/A" : portletState.getSelectedApplicant().getPortalUser().getTaxIdNo() %>
				</div>
			  </div>
		  </fieldset>
		  
		  <fieldset>
			  <legend>Means of Identification
			  </legend>
				  <div style="padding-bottom:5px">
					<div>
						<strong>Selected Means of Identification:</strong><br>
					  	<%=portletState.getSelectedApplicant().getPortalUser().getIdentificationHistory().getIdentificationType().getValue()==null ? "N/A" : portletState.getSelectedApplicant().getPortalUser().getIdentificationHistory().getIdentificationType().getValue()%>
						</select>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Your Means of Identification Photo:</strong><br>
					  	<a target="blank" href="<%=folder + "/uploadedfiles/" + (portletState.getSelectedApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto()==null ? "" : portletState.getSelectedApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto())%>" style="border:0px;"><img style="border:0px;" src="<%=folder + "/" + "uploadedfiles" + "/" + portletState.getSelectedApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto()%>" height="50px;"></a>
					  	<div><a target="blank" href="<%=folder + "/uploadedfiles/" + (portletState.getSelectedApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto()==null ? "" : portletState.getSelectedApplicant().getPortalUser().getIdentificationHistory().getIdentificationPhoto())%>">View In Large Size</a></div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Your Means of Identification Number:</strong> 
						<div>
						  <%=portletState.getSelectedApplicant().getPortalUser().getIdentificationHistory().getIdentificationNumber()==null ? "" : portletState.getSelectedApplicant().getPortalUser().getIdentificationHistory().getIdentificationNumber() %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Issue Date:</strong> 
						<div>
						  <%=portletState.getSelectedApplicant().getPortalUser().getIdentificationHistory().getIssueDate()==null ? "N/A" : portletState.getSelectedApplicant().getPortalUser().getIdentificationHistory().getIssueDate() %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Expiry Date:</strong> 
						<div>
						  <%=portletState.getSelectedApplicant().getPortalUser().getIdentificationHistory().getIdentificationExpiryDate()==null ? "N/A" : portletState.getSelectedApplicant().getPortalUser().getIdentificationHistory().getIdentificationExpiryDate() %>
						</div>
					</div>
				  </div>
		  <fieldset>
		  
		  
		  <fieldset>
			  <legend>Company Details
			  </legend>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Company Name:</strong> 
						<div>
						  <%=(portletState.getSelectedApplicant().getPortalUser().getCompany()!=null && portletState.getSelectedApplicant().getPortalUser().getCompany().getName()!=null) ? portletState.getSelectedApplicant().getPortalUser().getCompany().getName() : "N/A" %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Address of Company:</strong> 
				
						<div>
						  <%=(portletState.getSelectedApplicant().getPortalUser().getCompany()!=null && portletState.getSelectedApplicant().getPortalUser().getCompany().getAddress()!=null) ? portletState.getSelectedApplicant().getPortalUser().getCompany().getAddress() : "N/A" %>
						</div>
					</div>
				  </div>
				  
				  <div style="padding-bottom:5px"><strong>State where Company Resides:</strong>
					<div><%=portletState.getSelectedApplicant().getPortalUser().getState()!=null ? portletState.getSelectedApplicant().getPortalUser().getState().getName() : "N/A" %>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Company Contact Phone Number:</strong> 
						<div>
						  <%=(portletState.getSelectedApplicant().getPortalUser().getCompany()!=null && portletState.getSelectedApplicant().getPortalUser().getCompany().getPhoneNumber()!=null) ? portletState.getSelectedApplicant().getPortalUser().getCompany().getPhoneNumber() : "N/A"  %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Company Contact Email Address:</strong> 
						<div>
						  <%=(portletState.getSelectedApplicant().getPortalUser().getCompany()!=null && portletState.getSelectedApplicant().getPortalUser().getCompany().getEmailAddress()!=null) ? portletState.getSelectedApplicant().getPortalUser().getEmailAddress() : "N/A" %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Company Website URL:</strong> 
						<div>
						  <%=(portletState.getSelectedApplicant().getPortalUser().getCompany()!=null && portletState.getSelectedApplicant().getPortalUser().getCompany().getWebsite()==null) ? portletState.getSelectedApplicant().getPortalUser().getCompany().getWebsite() : "N/A" %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Company's CAC Number:</strong> 
						<div>
						  <%=(portletState.getSelectedApplicant().getPortalUser().getCompany()!=null && portletState.getSelectedApplicant().getPortalUser().getCompany().getRegNo()!=null) ? portletState.getSelectedApplicant().getPortalUser().getCompany().getRegNo() : "N/A" %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Date Of Incorporation: </strong> 
						<div>
						  <%=(portletState.getSelectedApplicant().getPortalUser().getCompany()!=null && portletState.getSelectedApplicant().getPortalUser().getCompany().getCertificateDate()!=null) ? 
								  sdf.format(portletState.getSelectedApplicant().getPortalUser().getCompany().getCertificateDate()) : "N/A" %>
						</div>
					</div>
				  </div>
				  <%
				  if(portletState.getSelectedApplicant().getPortalUser().getCompany()!=null && portletState.getSelectedApplicant().getPortalUser().getCompany().getLogo()!=null)
				  {
				  %>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Your Company Logo:</strong> <br>
					  	<img src="<%=folder + "/" + "uploadedfiles" + "/" + ((portletState.getSelectedApplicant().getPortalUser().getCompany()!=null && portletState.getSelectedApplicant().getPortalUser().getCompany().getLogo()!=null) ? portletState.getSelectedApplicant().getPortalUser().getCompany().getLogo() : "") %>" height="50px;">
					  	<div><a target="blank" href="<%=folder + "/uploadedfiles/" + ((portletState.getSelectedApplicant().getPortalUser().getCompany()!=null && portletState.getSelectedApplicant().getPortalUser().getCompany().getLogo()!=null) ? portletState.getSelectedApplicant().getPortalUser().getCompany().getLogo() : "")%>">View In Large Size</a></div>
					</div>
				  </div>
				  <%
				  }
				  if(portletState.getSelectedApplicant().getPortalUser().getCompany()!=null && portletState.getSelectedApplicant().getPortalUser().getCompany().getCertificate()!=null)
				  {
				  %>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Your Company's Certificate of Incorporation: <br>
					  	<img src="<%=folder + "/" + "uploadedfiles" + "/" + ((portletState.getSelectedApplicant().getPortalUser().getCompany()!=null && portletState.getSelectedApplicant().getPortalUser().getCompany().getCertificate()!=null) ? portletState.getSelectedApplicant().getPortalUser().getCompany().getCertificate() : "") %>" height="50px;">
					  	<div><a target="blank" href="<%=folder + "/uploadedfiles/" + ((portletState.getSelectedApplicant().getPortalUser().getCompany()!=null && portletState.getSelectedApplicant().getPortalUser().getCompany().getCertificate()!=null) ? portletState.getSelectedApplicant().getPortalUser().getCompany().getCertificate() : "")%>">View In Large Size</a></div>
					</div>
				  </div>
				  <%
				  }
				  %>
		  <fieldset>
		  
		  
		  
		  
	      <div>
	      	<input type="hidden" value="" name="act" id="act" />
	      	<input type="hidden" value="" name="actId" id="actId" />
	      	<%
	      	if(portletState.getSelectedApplicant().getStatus().equals(ApplicantStatus.APPLICANT_STATUS_UNAPPROVED))
	      	{
		      	if(portletState!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_MANAGE_APPLICANT))
				{
	      	%>
	      	<div style="padding-bottom:5px; float: right; font-size:11px; width:100%;">
				<div>
				  	<strong>Reason For Rejecting SignUp Request:</strong><br>
				  	<textarea name="rejectreason" id="rejectreason" placeholder="If you are rejecting this signup request, provide reasons in this box"></textarea>
				</div>
			</div>
			<div style="clear:both"></div>
			<%
				}
			}
		      	
	      	%>
	      	
	      	
	      	<button type="submit" class="btn btn-danger" style="float:left" onclick="javascript:submitForm('back')">Go Back</button>
	      	<%
	      	if(portletState.getSelectedApplicant().getStatus().equals(ApplicantStatus.APPLICANT_STATUS_UNAPPROVED))
	      	{
		      	if(portletState!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_MANAGE_APPLICANT))
				{
	      	%>
	      	
	      	<input type="submit" class="btn btn-danger" style="float:right" onclick="javascript:submitForm('reject', '<%=portletState.getSelectedApplicant().getId()%>');" value="Reject SignUp Request">
	      	<input type="submit" class="btn btn-success" style="float:right" onclick="javascript:submitForm('approve', '<%=portletState.getSelectedApplicant().getId()%>');" value="Approve SignUp Request">
	      	
	      	<%
				}
			}
		      	
	      	%>
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

function submitForm(clicked, id)
{
	
	document.getElementById('act').value=clicked;
	document.getElementById('actId').value=id;
	document.getElementById('startRegFormId').submit;
	
}
</script>