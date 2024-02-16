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
<style>
.formwidth50
{
	width:250px;
}
#passwordStrength
{
	height:10px;
	display:block;
	float:left;
}

.strength0
{
	width:250px;
	background:#cccccc;
}

.strength1
{
	width:50px;
	background:#ff0000;
}

.strength2
{
	width:100px;	
	background:#ff5f5f;
}

.strength3
{
	width:150px;
	background:#56e500;
}

.strength4
{
	background:#4dcd00;
	width:200px;
}

.strength5
{
	background:#399800;
	width:250px;
}

</style>
<%

ApplicantPortletState portletState = ApplicantPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ApplicantPortletState.class);
List<String> identificationType = IdentificationType.values();
List<String> relationship = KinRelationshipType.values();


%>

<portlet:actionURL var="proceedToStepThree" name="processAction">
	<portlet:param name="action"
		value="<%=APPLICANT_ACTIONS.CREATE_NEW_PASSWORD_PORTAL_USER_ACCOUNT.name()%>" />
</portlet:actionURL>

<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>


<div style="padding-left:10px; padding-right:10px; width:900px">
	<h2>Activate Your ECIMS Account</h2>
	<div class="panel  panel-primary">
	  <div class="panel-body">
	    	
	    <form  id="startRegFormId" action="<%=proceedToStepThree%>" method="post" enctype="application/x-www-form-urlencoded">
	     
		  <div align="center" style="padding-bottom:5px"> <strong>Password:</strong>
	        <div>
	          <input class="clear form-control" onKeyUp="passwordStrength(this.value)" type="password" value="<%=portletState.getActivationEmail()==null ? "" : portletState.getActivationEmail() %>" name="password" id="password" placeholder="Provide your password" />
	        </div>
	      </div>
	      <div align="center" style="padding-bottom:5px"><strong>Confirm your Password:</strong>
	        <div>
	          <input class="clear form-control" type="password" value="" name="passwordconfirm" id="passwordconfirm" placeholder="Retype your password" />
	        </div>
	      </div>
	      
	      <div align="center" style="padding-bottom:5px"><strong>Allow Me See My Passwords:</strong>
	      	  <input class="clear" type="checkbox" value="" onclick="mouseoverPass();" name="seepassword" id="seepassword" />
	      </div>
	      
	      
	      <p>
			<label for="passwordStrength">Password strength</label>
			<div id="passwordDescription">Password not entered</div>
			<div id="passwordStrength" class="strength0"></div>
		  </p>
		  
		  
		  
		  
	      <div align="center">
	      	<input type="hidden" value="" name="act" id="act" />
	      	<button type="submit" class="btn btn-success" onclick="javascript:submitForm('proceed')">Save</button>
	      	
	        <!-- <input type="submit" name="createportaluserStepOne" value="Proceed to Next" id="createportaluserStepOne" class="floatLeft" style="background-color:#00CC00" />-->
	      </div>
	    </form>
	  </div>
	</div>
</div>





<script type="text/javascript">

function mouseoverPass(obj) {
	if(document.getElementById('seepassword').checked)
	{
	 	var obj = document.getElementById('passwordconfirm');
	 	var obj1 = document.getElementById('password');
	  	obj.type = "text";
	  	obj1.type = "text";
	}else
	{
		var obj = document.getElementById('passwordconfirm');
	  	var obj1 = document.getElementById('password');
	  	obj.type = "password";
	  	obj1.type = "password";
	}
}
function mouseoutPass(obj) {
  	var obj = document.getElementById('passwordconfirm');
  	var obj1 = document.getElementById('password');
  	obj.type = "password";
  	obj1.type = "password";
}


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


function passwordStrength(password)
{
	var desc = new Array();
	desc[0] = "Very Weak";
	desc[1] = "Weak";
	desc[2] = "Better";
	desc[3] = "Medium";
	desc[4] = "Strong";
	desc[5] = "Strongest";

	var score   = 0;

	//if password bigger than 6 give 1 point
	if (password.length > 6) score++;

	//if password has both lower and uppercase characters give 1 point	
	if ( ( password.match(/[a-z]/) ) && ( password.match(/[A-Z]/) ) ) score++;

	//if password has at least one number give 1 point
	if (password.match(/\d+/)) score++;

	//if password has at least one special caracther give 1 point
	if ( password.match(/.[!,@,#,$,%,^,&,*,?,_,~,-,(,)]/) )	score++;

	//if password bigger than 12 give another 1 point
	if (password.length > 12) score++;

	 document.getElementById("passwordDescription").innerHTML = desc[score];
	 document.getElementById("passwordStrength").className = "strength" + score;
	 
	 if(score>3)
	 {
		 return true;
	 }else
	 {
		 return false;
	 }
}
</script>