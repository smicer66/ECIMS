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
<%@page import="smartpay.entity.enumerations.ApplicantType"%>
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
	
	String bootstrapCss = resourceBaseURL + "/css/bootstrap.css";
	String actnowCss = resourceBaseURL + "/css/actnow.css";
	String bootstrapJs = resourceBaseURL + "/js/bootstrap.min.js";
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

GuestPortletState portletState = GuestPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(GuestPortletState.class);
List<String> identificationType = IdentificationType.values();
List<String> relationship = KinRelationshipType.values();


%>

<portlet:actionURL var="proceedToStepTwo" name="processAction">
	<portlet:param name="action"
		value="<%=APPLICANT_ACTIONS.CREATE_A_PORTAL_USER_STEP_ONE.name()%>" />
</portlet:actionURL>

<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>




<section class="container"> 
<div style="padding-left:10px; padding-right:10px;" class="col-sm-9 panel panel-default" role="news">
	<h4>Create A User Profile</h4>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Step 1 of 3: Select Type of ECIMS Account</span></div>
	  <div class="panel-body">
	    	
	    <form  id="startRegFormId" action="<%=proceedToStepTwo%>" method="post" enctype="application/x-www-form-urlencoded">
	     	<fieldset>
		  		<legend>Please Select An ECIMS Account Type</legend>
		        <div>
				<%
				if(portletState.getAccountType()==null)
				{
				%>
				<div class="form-group">
					<div class="col-sm-9">
					  <div class="col-sm-1"><input type="radio" name="accountType" value="1"></div> <div class="col-sm-3">Individual</div>
					  <div class="col-sm-1"><input type="radio" name="accountType" value="0"></div> <div class="col-sm-3">Corporation</div>
					</div>
				  </div>
				<%
				}else if(portletState.getAccountType().equals("1"))
				{
				%>
					<div class="form-group">
					<div class="col-sm-9">
					  <div class="col-sm-1"><input type="radio" checked="checked" name="accountType" value="1"></div> <div class="col-sm-3">Individual</div>
					  <div class="col-sm-1"><input type="radio" name="accountType" value="0"></div> <div class="col-sm-3">Corporation</div>
					</div>
				  </div>
				
				<%
				}else if(portletState.getAccountType().equals("0"))
				{
				%>
					<div class="form-group">
					<div class="col-sm-9">
					  <div class="col-sm-1"><input type="radio" name="accountType" value="1"></div> <div class="col-sm-3">Individual</div>
					  <div class="col-sm-1"><input type="radio" name="accountType" checked="checked" value="0"></div> <div class="col-sm-3">Corporation</div>
					</div>
				  </div>
				<%
				}
				%>
				  
		        </div>
		  	</fieldset>
	     
			<div class="form-group">
				<button type="submit" class="btn btn-danger" onclick="javascript:submitForm('backtohome')">Cancel</button>
				<div class="btn-group pull-right">
			  
				<button type="submit" class="btn btn-success" onclick="javascript:submitForm('proceedToStepTwo')">Proceed to Next Page</button>
				</div>
			</div>
			<input type="hidden" name="act" id="act" value="" />
	    </form>
	  </div>
	  		
	</div>
</div>
<aside class="col-sm-3" role="resources">
	<div><strong style="color:#FF0000">Help Tips</strong></div>
	<div style="padding:top:10px">
		<ul>
			<li>
				<div><strong>Individual Accounts:</strong></div>
				<div>These Are accounts that are specifically for individuals wishing to import security items into the country</div>
				<div>&nbsp;</div>
				<div><strong>Corporation Accounts:</strong></div>
				<div>These Are accounts that are specifically for corporations wishing to import security items into the country</div>
				<div>&nbsp;</div>
				<div><strong>Necessary Documents:</strong></div>
				<div>Ensure you have the following items on hand as they will be necessary during this sign up process:<br>
					<ol>
						<li>A scanned copy of your passport photo</li>
						<li>A scanned copy of any of these: Your National ID Card, Drivers License, International Passport</li>
						<li>Details of your CAC Certificate if you are signing up as a corporation</li>
					</ol>
				</div>
				</div>
			
			</li>
		</ul>
	</div>
</aside>
</section>




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