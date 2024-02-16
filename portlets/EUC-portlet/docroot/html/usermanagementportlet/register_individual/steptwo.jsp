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
<%@page import="smartpay.entity.Agency"%>
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
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" />
<%

UserManagementPortletState portletState = UserManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(UserManagementPortletState.class);
Collection<Agency> agencyList = portletState.getAgencyList();
List<IdentificationType> identificationType = IdentificationType.values();
List<KinRelationshipType> relationship = KinRelationshipType.values();
String folder = "/resources/images";

%>

<portlet:actionURL var="proceedToStepThree" name="processAction">
	<portlet:param name="action"
		value="<%=EUC_DESK_ACTIONS.CREATE_A_PORTAL_USER_STEP_TWO.name()%>" />
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



<div style="padding-left:10px; padding-right:10px; width:900px" id="print-content">
	<h2>Create A User Profile</h2>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Step 3 of 3: Preview User Details</span></div>
	  <div class="panel-body">
	    <h4>Please preview your information before submission</h4>
	    <form  id="startRegFormId" action="<%=proceedToStepThree%>" method="post" enctype="application/x-www-form-urlencoded">
	     
		  	<div style="padding-bottom:5px"> <strong>Type Of User:</strong>
					<div>
					  	<%=portletState.getUsertype()!=null ? portletState.getUsertype() : "N/A"%>
					</div>
		  	</div>
		  
		  <%
		  if(portletState.getAgencyEntity()!=null)
		  {
		  	%>
		    <div style="padding-bottom:5px"> <strong>This User Belongs to This Group Of Users:</strong>
		        <div>
		          	<%=portletState.getAgencyEntity()!=null ? portletState.getAgencyEntity().getAgencyName() : "N/A"%>
					</select>
		        </div>
		      </div>
		  	<%  
		  }
		  %>
		  <div style="padding-bottom:5px"> <strong>Surname:</strong>
	        <div>
	          <%=portletState.getLastname()==null ? "N/A" : portletState.getLastname() %>
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>First Name:</strong>
	        <div>
	          <%=portletState.getFirstname()==null ? "N/A" : portletState.getFirstname() %>
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>Other Name:</strong>
	        <div>
	          <%=portletState.getOthername()==null ? "N/A" : portletState.getOthername() %>
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>Email Address:</strong>
	        <div>
	          <%=portletState.getContactEmailAddress()==null ? "N/A" : portletState.getContactEmailAddress() %>
	        </div>
	      </div>
	      
	      <div style="padding-bottom:5px"><strong>Date of Birth:</strong>
	        <div>
	          <%=portletState.getDob()==null ? "" : portletState.getDob() %>
	        </div>
	      </div>
		  <div style="padding-bottom:5px; clear:both"><div><strong>Mobile Number:</strong></div>
	        <div>
				<div style="float:left; padding-right:10px">
					<%= portletState.getMobile()%>
			  	</div>
	        </div>
	      </div>
		  <div style="padding-bottom:5px; clear:both"><div><strong>Nationality:</strong></div>
	        <div>
				<div style="padding-right:10px">
					<%=portletState.getNationality()!=null ? portletState.getNationality() : "N/A"%>
				</div>
	        </div>
	      </div>
		  <div style="padding-bottom:5px; clear:both"><div><strong>State:</strong></div>
	        <div>
				<div style="padding-right:10px">
					<%=portletState.getState()!=null ? portletState.getState() : "N/A"%>
				</div>
	        </div>
	      </div>
	      
		  <div style="padding-bottom:5px; clear:both">
			<div>
			  	<strong>Your Tax Identification Number:</strong> 
				<div>
				  <%=portletState.getTaxIdNumber()==null ? "" : portletState.getTaxIdNumber() %>
				</div>
			</div>
		  </div>
		  
		  
		  
		  <div style="padding-bottom:5px"><strong>Passport Photo:</strong>
	        <div>
			<img src="<%=folder + "/uploadedfiles/" + portletState.getPassportPhoto()%>" height="50px;">
			<div><a target="blank" href="<%=folder + "/uploadedfiles/" + (portletState.getPassportPhoto()==null ? "" : portletState.getPassportPhoto())%>">View In Large Size</a></div>
	        </div>
	      </div>
		  
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
<div>
	      	<input type="hidden" value="" name="act" id="act" />
	      	<button type="submit" class="btn btn-danger" style="float:left" onclick="javascript:submitForm('back')">Go Back</button>
	      	<button type="submit" class="btn btn-success" style="float:right" onclick="javascript:submitForm('proceed')">Create This Account</button>
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



</script>