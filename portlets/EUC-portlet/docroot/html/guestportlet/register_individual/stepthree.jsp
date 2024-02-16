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
<script src="<%=resourceBaseURL %>/js/jquery-1.11.1.min.js"></script>
<script src="<%= resourceBaseURL %>/js/app.v1.js"></script>
<!-- parsley -->
<script src="<%= resourceBaseURL %>/js/parsley.min.js"></script>
<script src="<%= resourceBaseURL %>/js/parsley.extend.js"></script>
<script src="<%= resourceBaseURL %> %>/js/app.plugin.js"></script>

<style>
.formwidth50{
	width:300px;
}
.required{
	color:red;
}
</style>
<%

GuestPortletState portletState = GuestPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(GuestPortletState.class);
List<String> identificationType = IdentificationType.values();
List<String> relationship = KinRelationshipType.values();
String folder = "/resources/images"; 


%>

<portlet:actionURL var="proceedToStepFour" name="processAction">
	<portlet:param name="action"
		value="<%=APPLICANT_ACTIONS.CREATE_A_PORTAL_USER_STEP_THREE.name()%>" />
</portlet:actionURL>


<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>
		
		

<div style="padding-left:10px; padding-right:10px; width:900px" id="print-content">
	<h4>Create An Individual ECIMS Profile Account</h4>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Step 3 of 3: Preview Bio-Data (Individual Account)</span></div>
	  <div class="panel-body">
	    	
	    <h4>Please review your form before submission</h4>
	    <form  id="startRegFormId" action="<%=proceedToStepFour%>" method="post" enctype="application/x-www-form-urlencoded">
	     	<fieldset>
			  <legend>Bio-Data
			  </legend>
			  <div style="padding-bottom:5px"> <strong>Surname:<span style="color:red"></span></strong>
		        <div>
		          <%=portletState.getLastName()==null ? "" : portletState.getLastName() %>
		        </div>
		      </div>
		      <div style="padding-bottom:5px"><strong>First Name:<span style="color:red"></span></strong>
		        <div>
		          <%=portletState.getFirstName()==null ? "" : portletState.getFirstName() %>
		        </div>
		      </div>
		      <div style="padding-bottom:5px"><strong>Other Name:</strong>
		        <div>
		          <%=portletState.getOtherName()==null ? "" : portletState.getOtherName() %>
		        </div>
		      </div>
		      <div style="padding-bottom:5px"><strong>Email Address:<span style="color:red"></span></strong>
		        <div>
		          <%=portletState.getEmail()==null ? "" : portletState.getEmail() %>
		        </div>
		      </div>
			  <div style="padding-bottom:5px; clear:both"><div><strong>Mobile Number:<span style="color:red"></span></strong></div>
		        <div>
					<div style="float:left; padding-right:10px">
						<%=portletState.getMobile()==null ? "N/A" : portletState.getCountryCode() + portletState.getMobile()%>
				  	</div>
		        </div>
		      </div>
			  <div style="padding-bottom:5px; clear:both"><div><strong>Country You Are Residing In:<span style="color:red"></span></strong></div>
		        <div>
					<div style="padding-right:10px">
						<%=portletState.getNationality()==null ? "N/A" : portletState.getNationality() %>
					</div>
		        </div>
		      </div>
			  <div style="padding-bottom:5px; clear:both"><div><strong>State:<span style="color:red"></span></strong></div>
		        <div>
					<div style="padding-right:10px">
							<%=portletState.getState()==null ? "N/A" : portletState.getState() %>
					</div>
		        </div>
		      </div>
			  <div style="padding-bottom:5px; clear:both"><div><strong>City:<span style="color:red"></span></strong></div>
		        <div>
					<div style="padding-right:10px">
							<%=portletState.getResidenceCity()==null ? "N/A" : portletState.getResidenceCity() %>
					</div>
		        </div>
		      </div>
			  <div style="padding-bottom:5px"><strong>Gender:</strong>
		        <div>
				<%=portletState.getGender()==null ? "N/A" : portletState.getGender().equals("1") ? "Male" : "Female" %>
				  
		        </div>
		      </div>
			  <div style="padding-bottom:5px"><strong>Passport Photo:</strong>
		        <div>
				<img src="<%=folder + "/uploadedfiles/" + portletState.getPassportPhoto()%>" height="50px;">
				<div><a target="blank" href="<%=folder + "/uploadedfiles/" + (portletState.getPassportPhoto()==null ? "" : portletState.getPassportPhoto())%>">View In Large Size</a></div>
		        </div>
		      </div>
			  
			  <div style="padding-bottom:5px"><strong>Marital Status:</strong>
		        <div>
				<%= portletState.getMaritalStatus()==null ? "N/A" : portletState.getMaritalStatus()%>
				  
		        </div>
		      </div>
			  <div style="padding-bottom:5px"><strong>Address:</strong>
		        <div>
		            <%=portletState.getAddressLine1()==null ? "N/A" : portletState.getAddressLine1() %>
		        	<%=portletState.getAddressLine1()!=null ? (portletState.getAddressLine2()==null ? "" : ("<br>" + portletState.getAddressLine2())) : "" %>
		        </div>
		      </div>
		      
			  <div style="padding-bottom:5px; clear:both">
				  	<strong>Tax Identification Number:</strong>
				<div>
					<%=portletState.getTaxIdNumber()==null ? "N/A" : portletState.getTaxIdNumber() %>
				</div>
			  </div>
		  </fieldset>
		  
		  <fieldset>
			  <legend>Proof of Identification
			  </legend>
				  <div style="padding-bottom:5px"><strong>Means of Identification:</strong>
					<div>
						<strong>Selected Means of Identification:</strong><br/>
					  	<%=portletState.getIdentificationType()==null ? "N/A" : portletState.getIdentificationType()%>
						</select>
					</div>
				  </div>
				  <%
				  if(portletState.getIdentificationType()!=null && portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_NATIONAL_ID.getValue()))
				  {
				  %>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Your National ID Photo:</strong><br/>
					  	<img src="<%=folder + "/uploadedfiles/" + (portletState.getIdentificationFileName()==null ? "" : portletState.getIdentificationFileName())%>" height="50px;">
					  	<div><a target="blank" href="<%=folder + "/uploadedfiles/" + (portletState.getIdentificationFileName()==null ? "" : portletState.getIdentificationFileName())%>">View In Large Size</a></div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Your National ID Number:</strong> 
						<div>
						  <%=portletState.getNationalIdNumber()==null ? "" : portletState.getNationalIdNumber() %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Issue Date:</strong>
					  	<%=portletState.getNatlissueDate()!=null ? portletState.getNatlissueDate() : "N/A"%>
					</div>
				  </div>
				  <%
				  }else if(portletState.getIdentificationType()!=null && portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_DRIVERS_LICENSE.getValue()))
				  {
				  %>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Your Drivers License Photo:</strong>
					  	<img src="<%=folder + "/uploadedfiles/" + (portletState.getIdentificationFileName()==null ? "" : portletState.getIdentificationFileName())%>" height="50px;">
					  	<div><a target="blank" href="<%=folder + "/uploadedfiles/" + (portletState.getIdentificationFileName()==null ? "" : portletState.getIdentificationFileName())%>">View In Large Size</a></div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Your Drivers License Number:</strong> 
						<div>
						  <%=portletState.getDriversIdNumber()==null ? "" : portletState.getDriversIdNumber() %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Expiry Date:</strong> 
						<div>
						  <%=portletState.getDriversExpiryDate()!=null ? portletState.getDriversExpiryDate() : "N/A"%>
						</div>
					</div>
				  </div>
				  
				  
				  
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Place of Issue:</strong> 
						<div>
						  <%=portletState.getDriversplaceOfIssue()==null ? "" : portletState.getDriversplaceOfIssue() %>
						</div>
					</div>
				  </div>
				  
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Issue Date:</strong> 
						<div>
						  <%=portletState.getDriversissuancedate()!=null ? portletState.getDriversissuancedate() : "N/A"%>
						</div>
					</div>
				  </div>
				  <%
				  }else if(portletState.getIdentificationType()!=null && portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_INTL_PASSPORT.getValue()))
				  {
				  %>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Your International Passport Photo:</strong>
					  	<img src="<%=folder + "/uploadedfiles/" + (portletState.getIdentificationFileName()==null ? "" : portletState.getIdentificationFileName())%>" height="50px;">
					  	<div><a target="blank" href="<%=folder + "/uploadedfiles/" + (portletState.getIdentificationFileName()==null ? "" : portletState.getIdentificationFileName())%>">View In Large Size</a></div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Your International Passport Number:</strong> 
						<div>
						  <%=portletState.getIntlpassportIdNumber()==null ? "" : portletState.getIntlpassportIdNumber() %>
						</div>
					</div>
				  </div>
				  
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Expiry Date:</strong> 
						<div>
						  <%=portletState.getIntlpassportExpiryDate()!=null ? portletState.getIntlpassportExpiryDate() : "N/A"%>
						</div>
					</div>
				  </div>
				  
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Place of Issue:</strong> 
						<div>
						  <%=portletState.getIntlplaceOfIssue()==null ? "" : portletState.getIntlplaceOfIssue() %>
						</div>
					</div>
				  </div>
				  
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Issue Date:</strong> 
						<div>
						  <%=portletState.getIntlissuancedate()!=null ? portletState.getIntlissuancedate() : "N/A"%>
						</div>
					</div>
				  </div>
				  <%
				  }else if(portletState.getIdentificationType()!=null && portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_PVC.getValue()))
				  {
				  %>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Your Permanent Voters Card Photo:</strong><br/>
					  	<a target="blank" href="<%=folder + "/uploadedfiles/" + (portletState.getIdentificationFileName()==null ? "" : portletState.getIdentificationFileName())%>"><img src="<%=folder + "/uploadedfiles/" + (portletState.getIdentificationFileName()==null ? "" : portletState.getIdentificationFileName())%>" height="50px;"></a>
					  	<div><a target="blank" href="<%=folder + "/uploadedfiles/" + (portletState.getIdentificationFileName()==null ? "" : portletState.getIdentificationFileName())%>">View In Large Size</a></div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Your Permanent Voters Card Number:</strong> 
						<div>
						  <%=portletState.getPvcNumber()==null ? "" : portletState.getPvcNumber() %>
						</div>
					</div>
				  </div>
				  
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Issue Date:</strong> 
						<div>
						  <%=portletState.getPvcIssueDate()!=null ? portletState.getPvcIssueDate() : "N/A"%>
						</div>
					</div>
				  </div>
				  <%
				  }
				  %>
		  <fieldset>
		  
		  
		  
		  
		  <fieldset>
			  <legend>Next Of Kin
			  </legend>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Full Name:</strong> 
						<div>
						  <%=portletState.getNextOfKinName()==null ? "" : portletState.getNextOfKinName() %>
						</div>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Address of Residence:</strong> 
						<div>
						  <%=portletState.getNextOfKinAddress()==null ? "N/A" : portletState.getNextOfKinAddress() %>
						</div>
					</div>
				  </div>
				  
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Relationship:</strong> 
						<%=portletState.getNextOfKinRelationship()==null ? "N/A" : portletState.getNextOfKinRelationship() %>
					</div>
				  </div>
				  <div style="padding-bottom:5px">
					<div>
					  	<strong>Next of Kin Phone Number:</strong> 
						<div>
						  <%=portletState.getNextOfKinPhoneNumber()==null ? "N/A" : portletState.getNextOfKinPhoneNumber() %>
						</div>
					</div>
				  </div>
		  <fieldset>
		  
		  
	      <div>
	      	<input type="hidden" value="" name="act" id="act" />
	      	<button type="submit" class="btn btn-danger" style="float:left" onclick="javascript:submitForm('gobacktosteponeguestregister')">Go Back</button>
	      	<button type="submit" class="btn btn-success" style="float:right" onclick="javascript:submitForm('proceed')" >Create My Account</button>
	      	
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