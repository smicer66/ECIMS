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
<%@page import="smartpay.entity.PortalUser"%>
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

GuestPortletState portletState = GuestPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(GuestPortletState.class);
List<String> identificationType = IdentificationType.values();
List<String> relationship = KinRelationshipType.values();
HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest));
String kickstart = httpReq.getParameter("kickstart");
PortalUser kickstartEmail = null;
kickstartEmail = portletState.getGuestPortletUtil().getPortalUserByActivationCode(kickstart);

%>

<portlet:actionURL var="proceedToStepThree" name="processAction">
	<portlet:param name="action"
		value="<%=APPLICANT_ACTIONS.ACTIVATE_PORTAL_USER_ACCOUNT.name()%>" />
</portlet:actionURL>

<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>


<div style="padding-left:10px; padding-right:10px; width:900px">
	<h3>Activate Your ECIMS Account</h3>
	<div class="panel  panel-primary">
	<div class="panel-heading"><span style="color:white; font-weight: bold">Step 1 of 2: Activate Your Account</span></div>
	  <div class="panel-body">
	  	<center>
	    <div style="width:300px; text-align: center">	
		    <form  id="startRegFormId" data-parsley-validate action="<%=proceedToStepThree%>" method="post" enctype="application/x-www-form-urlencoded">
		      <center>
			  <div align="center" style="padding-bottom:5px"> <strong>My Email:</strong>
		        <div>
		          <%=kickstartEmail==null ? "" : kickstartEmail.getEmailAddress() %>
		        </div>
		      </div>
			      <div align="center" style="padding-bottom:5px"><strong>Activation Code:<span style="color:red">*</span></strong>
			        <div>
			          <input class="clear form-control" type="text" value="" name="activationCode" id="activationCode" placeholder="Type your Activation Code" />
			        </div>
			      </div>
			      <div align="center" style="padding-bottom:5px"><strong>Retype Activation Code:<span style="color:red">*</span></strong>
		          <div>
			          <input class="clear form-control" type="text" value="" name="activationCode2" id="activationCode2" placeholder="Type your Activation Code again" />
			        </div>
			      </div>
		
			      <div align="center">
		      	<input type="hidden" value="" name="act" id="act" />
		      	<input type="hidden" type="text" value="<%=kickstartEmail==null ? "" : kickstartEmail.getEmailAddress() %>" name="email" id="email" />
		      	<input type="hidden" value="<%=kickstart==null ? "" : kickstart %>" name="kickstart" id="kickstart" />
		      	<button type="submit" class="btn btn-success" onclick="javascript:submitForm('proceed')">Activate Account</button>
		      	<div>&nbsp;</div>
		      	<button type="submit" class="btn btn-success" onclick="javascript:submitForm('resendactivationcode')">Resend Activation Code</button>
		      	
		        <!-- <input type="submit" name="createportaluserStepOne" value="Proceed to Next" id="createportaluserStepOne" class="floatLeft" style="background-color:#00CC00" />-->
		      </div>
		      </center>
		    </form>
		</div>
		</center>
	  </div>
	</div>
</div>




<script type="text/javascript" charset="utf-8" src="<%=jqueryJsUrl%>"></script>
<script type="text/javascript" charset="utf-8" src="<%=jqueryUIJsUrl%>"></script>
<script type="text/javascript">

$(document).ready(function(){
	  $('#activationCode2').bind("paste",function(e) {
	      e.preventDefault();
	  });
	});
	
	
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