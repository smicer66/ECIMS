<%@page import="smartpay.entity.ApplicationAttachmentType"%>
<%@page import="smartpay.entity.ApplicationAttachmentTypeFactory"%>
<%@page import="java.util.Collections"%>
<%@page import="smartpay.entity.enumerations.ApplicantType"%>
<%@page import="com.ecims.portlet.admin.itemcategoryportlet.ItemCategoryPortletState"%>
<%@page	import="com.ecims.portlet.admin.itemcategoryportlet.ItemCategoryPortletState.*"%>
<%@page	import="com.ecims.portlet.admin.itemcategoryportlet.ItemCategoryPortletUtil"%>
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
<%@page import="smartpay.entity.enumerations.ApplicantType"%>
<%@page import="smartpay.entity.enumerations.KinRelationshipType"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.RoleType"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Arrays" %>
<%@page import="java.util.List" %>
<%@page import="com.ecims.commins.Util"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="net.tanesha.recaptcha.ReCaptcha" %>
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
<link rel="stylesheet" href='<%=faceboxCssUrl%>' type="text/css" />
<link rel="stylesheet" href='<%=pagingUrl%>' type="text/css" />
<script type="text/javascript" src="<%=(resourceBaseURL + "/js/anytimec.js")%>"></script>
<link rel="stylesheet" href='<%=resourceBaseURL + "/css/anytimec.css"%>' type="text/css" />
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=(resourceBaseURL + "/js/bootstap.min.js")%>"></script>
<script src="<%=resourceBaseURL %>/js/jquery-1.11.1.min.js"></script>
<script src="<%= resourceBaseURL %>/js/app.v1.js"></script>
<!-- parsley -->
<script src="<%= resourceBaseURL %>/js/parsley.min.js"></script>
<script src="<%= resourceBaseURL %>/js/parsley.extend.js"></script>
<script src="<%= resourceBaseURL %>/js/app.plugin.js"></script>
<style>
#recaptcha_area input { height: auto; }
#recaptcha_area a { font-weight: normal; }
.formwidth50{
	width:300px;
}
.required{
	color:red;
	
}
</style>
<%

ItemCategoryPortletState portletState = ItemCategoryPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ItemCategoryPortletState.class);
List<ApplicantType> applicantTypeList = ApplicantType.values();
Collection<ApplicationAttachmentType> applicationAttachmentTypeList = (Collection<ApplicationAttachmentType>)portletState.getItemCategoryPortletUtil().getAllEntity(ApplicationAttachmentType.class);
String selected = "";
%>

<jsp:include page="/html/itemcategoryportlet/tabs.jsp" flush="" />


<portlet:actionURL var="proceedToStepThree" name="processAction">
	<portlet:param name="action"
		value="<%=ITEM_CATEGORY_ACTIONS.EDIT_NEW_ITEM_CATEGORY.name()%>" />
</portlet:actionURL>


	  
<section class="container">
<div style="padding-left:10px; padding-right:10px;" class="col-sm-9 panel panel-default" role="news">
	<h4>Create An New Item Category</h4>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Set Compulsory Attachments</span></div>
	  <div class="panel-body">
	    	
	    <form  id="startRegFormId" action="<%=proceedToStepThree%>" data-validate="parsley" method="post" enctype="application/x-www-form-urlencoded">
	     <fieldset>
			  <legend>Step 2 of 2: Item Category Details
			  </legend>
			  
			  
			  <div style="padding-bottom:5px"> <strong>Name of Item Category:<span style="color:red">*</span></strong>
				<div>
				  <%=portletState.getCategoryname()==null ? "" : portletState.getCategoryname() %>
				</div>
			  </div>
			  <div style="padding-bottom:5px"><strong>Applicant Type(s) That Can Access This Item Category:<span style="color:red">*</span></strong>
				<div>
				  		<%
				  		if(portletState.getApplicantTypeList()!=null && portletState.getApplicantTypeList().size()>0)
				  		{
				  			String[] a = new String[portletState.getApplicantTypeList().size()];
				  			int c=0;
				  			for(Iterator<ApplicantType> it = portletState.getApplicantTypeList().iterator(); it.hasNext();)
				  			{
				  				ApplicantType appType = it.next();
				  				a[c++] = appType.getValue();
				  				
				  			}
				  		%>
				  		
				  		<%=Arrays.toString(a) %>
				  		<%
				  		}
				  		%>
				</div>
			  </div>
			  
			  <div style="padding-bottom:5px"> <strong>Name of Item Category:<span style="color:red">*</span></strong>
				<div>
				  <table width="100%" class="table table-hover" id="btable">
					<thead>
					
						  <th>Item Category Name</th>
						  <th>Application Attachment Type</th>
						  <th>Compulsory</th>
					</thead>
				</div>
			  </div>

			  
			  
		  </fieldset>
		  
			  
		  
		  
		  
	      <div>
	      	<input type="hidden" value="" name="act" id="act" />
	      	<button type="submit" class="btn btn-success" style="float:right" onclick="javascript:submitForm('createnewagencysteptwo')">Create New Agency</button>
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
				<div><strong>Nationality:</strong></div>
				<div>Only Nigerians can signup or apply for an End-User Certificate</div>
				<div>&nbsp;</div>
				<div><strong>Mobile Numbers:</strong></div>
				<div>Mobile numbers must be entered in the form of: 8123456789</div>
				<div>&nbsp;</div>
				<div><strong>Email Address & Phone Numbers:</strong></div>
				<div>Provide your valid email address & phone numbers as these will be the means through which you will receive notifications on the status 
				of your EUC applications</div>
				<div>&nbsp;</div>
				<div><strong>Photos & Images:</strong></div>
				<div>Your image files you upload should not be more than 3MB</div>
			
			</li>
		</ul>
	</div>
</aside>
</section>



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
		
	}
	if(selected=="<%=IdentificationType.IDENTIFICATION_TYPE_NATIONAL_ID.getValue()%>")
	{
		document.getElementById('intlpassportoption').style.display="none";
		document.getElementById('driverslicenseoption').style.display="none";
		document.getElementById('nationalidcardoption').style.display="block";
	}
	if(selected=="<%=IdentificationType.IDENTIFICATION_TYPE_INTL_PASSPORT.getValue()%>")
	{
		document.getElementById('intlpassportoption').style.display="block";
		document.getElementById('driverslicenseoption').style.display="none";
		document.getElementById('nationalidcardoption').style.display="none";
	}
	if(selected=="<%=IdentificationType.IDENTIFICATION_TYPE_DRIVERS_LICENSE.getValue()%>")
	{
		document.getElementById('intlpassportoption').style.display="none";
		document.getElementById('driverslicenseoption').style.display="block";
		document.getElementById('nationalidcardoption').style.display="none";
	}
}



$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#intlpassportExpiryDate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "-20:+40",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yyyy/mm/dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false
	});
	
	
	
});


$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#driversExpiryDate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "-20:+40",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yyyy/mm/dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false
	});
	
	
	
});


$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#identificationExpiryDate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "-20:+40",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yyyy/mm/dd',
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
		yearRange: "-80:0",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yyyy/mm/dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false
	});
	
	
	
});

</script>