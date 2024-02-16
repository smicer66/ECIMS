<%@page import="com.ecims.portlet.contactus.ContactUsPortletState"%>
<%@page import="com.ecims.portlet.contactus.ContactUsPortletState.*"%>
<%@page import="com.ecims.commins.Util"%>
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
<%@page import="smartpay.entity.Company"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.entity.Settings"%>
<%@ page import="net.tanesha.recaptcha.ReCaptcha" %>
<%@ page import="net.tanesha.recaptcha.ReCaptchaImpl" %>
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

ContactUsPortletState portletState = ContactUsPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ContactUsPortletState.class);
ReCaptcha c = null;
if(renderRequest.isSecure())
{
	c = ReCaptchaFactory.newSecureReCaptcha("6LfMX_4SAAAAALzkpcm66ZZ4Fsd1us8ROKxp6lLC", "6LfMX_4SAAAAAP71kP42bqQV9AInMII_2B3xbDy-", false);
	((ReCaptchaImpl)c).setRecaptchaServer("https://www.google.com/recaptcha/api");
}else
{
	c = ReCaptchaFactory.newReCaptcha("6LfMX_4SAAAAALzkpcm66ZZ4Fsd1us8ROKxp6lLC", "6LfMX_4SAAAAAP71kP42bqQV9AInMII_2B3xbDy-", false);
	((ReCaptchaImpl)c).setRecaptchaServer("http://www.google.com/recaptcha/api");
}


%>
<portlet:actionURL var="proceedToStepThree" name="processAction">
	<portlet:param name="action"
		value="<%=CONTACTUS_ACTIONS.CREATE_NEW_CONTACTUS.name()%>" />
</portlet:actionURL>


<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>


	  
	  
<section class="container">
<div style="padding-left:10px; padding-right:10px;" class="col-sm-9 panel panel-default" role="news">
	<h4>Contact Us</h4>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Contact Us if you need assistance on any issues</span></div>
	  <div class="panel-body">
	    	
	    <form  id="startRegFormId" action="<%=proceedToStepThree%>" data-parsley-validate method="post" enctype="application/x-www-form-urlencoded">
	     <fieldset>
			  <legend>Contact Us
			  </legend>
			  <div style="padding-bottom:5px"> <strong>Full Name:<span style="color:red">*</span></strong>
				<div>
				  <input class="clear form-control formwidth50" type="text" data-parsley-required="true" value="<%=portletState.getFullName()==null ? "" : portletState.getFullName() %>" name="fullName" id="fullName" placeholder="Provide Your Full Name" />
				</div>
			  </div>
			  <div style="padding-bottom:5px"><strong>Email Address:<span style="color:red">*</span></strong>
				<div>
				  <input class="clear form-control formwidth50" type="text" data-parsley-required="true" value="<%=portletState.getEmailAddress()==null ? "" : portletState.getEmailAddress() %>" name="emailAddress" id="emailAddress" placeholder="Provide Your Email Address" />
				</div>
			  </div>
			  <div style="padding-bottom:5px"><strong>Mobile Number(s):<span style="color:red">*</span></strong>
				<div>
				  <input class="clear form-control formwidth50" type="text" data-parsley-required="true" value="<%=portletState.getMobileNumber()==null ? "" : portletState.getMobileNumber() %>" name="mobileNumber" id="mobileNumber" placeholder="Provide Your Mobile Number" />
				</div>
			  </div>
			  <div style="padding-bottom:5px"><strong>Subject:<span style="color:red">*</span></strong>
				<div>
				  <input class="clear form-control formwidth50" type="text" data-parsley-required="true" value="<%=portletState.getSubject()==null ? "" : portletState.getSubject() %>" name="subject" id="subject" placeholder="Subject:" />
				</div>
			  </div>
			  <div style="padding-bottom:5px"><strong>Type Your Contents:<span style="color:red">*</span></strong>
				<div>
				  <textarea class="clear form-control formwidth50" type="text" data-parsley-required="true" value="<%=portletState.getContents()==null ? "" : portletState.getContents() %>" name="contents" id="contents" placeholder="Provide details here"><%=portletState.getContents()==null ? "" : portletState.getContents() %></textarea>
				</div>
			  </div>
		  </fieldset>
		  
		  
		  <div style="padding-bottom:5px">
			<div>
				<strong>Type Out the Captcha Code You Can See On Your Page:</strong> 
				<div>
					<script type="text/javascript" src="http://api.recaptcha.net/challenge?k=6LfMX_4SAAAAALzkpcm66ZZ4Fsd1us8ROKxp6lLC">
					</script>
					<noscript>
					<iframe src="http://api.recaptcha.net/noscript?k=6LfMX_4SAAAAALzkpcm66ZZ4Fsd1us8ROKxp6lLC"
					height="300" width="500" frameborder="0">
					</iframe><br>
					</noscript>
				</div>
			</div>
		  </div>
		  
		  
		  
	      <div>
	      	<input type="hidden" value="" name="act" id="act" />
	      	<button type="submit" class="btn btn-success" style="float:right" onclick="javascript:submitForm('proceed')">Send</button>
	      	<div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
	    All fields with red asterisk (*) imply they must be provided</div>
	        <!-- <input type="submit" name="createportaluserStepOne" value="Proceed to Next" id="createportaluserStepOne" class="floatLeft" style="background-color:#00CC00" />-->
	      </div>
		  
		  
		  
	    </form>
	  </div>
	</div>
</div>

<aside class="col-sm-3" role="resources">
	<div><strong style="color:#FF0000">Help Tips</strong></div>
	<div style="padding:top:10px">
		<ul>
			<li>
				<div><strong>Red Asterisk(<span style="color:red">*</span>):</strong></div>
				<div>You must provide data in the fields having red asterisks</div>
				<div>&nbsp;</div>
				<div><strong>Full Name:</strong></div>
				<div>Your Full Name</div>
				<div>&nbsp;</div>
				<div><strong>Email Address:</strong></div>
				<div>Your personal email address on which you can be communicated with</div>
				<div>&nbsp;</div>
				<div><strong>Mobile Number:</strong></div>
				<div>Your valid mobile number on which you can be communicated with/div>
				<div>&nbsp;</div>
				<div><strong>Subject:</strong></div>
				<div>The subject of your contact us details</div>
			
			</li>
		</ul>
	</div>
</aside>
</section>


<%
Calendar cal = Calendar.getInstance();
int yearNow = cal.get(Calendar.YEAR);
int year80Back = yearNow - 80;
int year40Further = yearNow + 40;
int year18Back = yearNow - 18;

%>
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

function disableFormParsley()
{
	$('#startRegFormId').parsley().destroy();
	
}
function submitForm(clicked)
{
	
	document.getElementById('act').value=clicked;
	document.getElementById('startRegFormId').submit;
	
}






$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#driversExpiryDate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "<%=yearNow%>:<%=year40Further%>",
		showButtonPanel : true,
		dateFormat: 'yy-mm-dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false
	});
	
	
	
});


$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#intlpassportExpiryDate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "<%=yearNow%>:<%=year40Further%>",
		showButtonPanel : true,
		minDate: 0,
		dateFormat: 'yy-mm-dd',
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
		yearRange: "<%=year80Back%>:<%=year18Back%>",
		showButtonPanel : true,
		dateFormat: 'yy-mm-dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false
	});
	
	
	
});


$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#driversissuancedate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "<%=year80Back%>:<%=yearNow%>",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yy-mm-dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false
	});
	
	
	
});


$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#pvcissueDate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "<%=year80Back%>:<%=yearNow%>",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yy-mm-dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false
	});
	
	
	
});



$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#natlissueDate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "<%=year80Back%>:<%=yearNow%>",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yy-mm-dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false
	});
	
	
	
});



$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#intlissuancedate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "<%=year80Back%>:<%=yearNow%>",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yy-mm-dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false
	});
	
	
	
});
	
$(document).ready(function(){
	  $('#confirmContactEmailAddress').bind("paste",function(e) {
	      e.preventDefault();
	  });
	});
	
	
$(document).ready(function(){
	  $('#confirmContactPhoneNumber').bind("paste",function(e) {
	      e.preventDefault();
	  });
	});

</script>