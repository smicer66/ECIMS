<%@page import="smartpay.entity.Agency"%>
<%@page import="smartpay.entity.ItemCategory"%>
<%@page import="com.ecims.portlet.admin.workflowconfig.WorkFlowConfigPortletState"%>
<%@page	import="com.ecims.portlet.admin.workflowconfig.WorkFlowConfigPortletState.*"%>
<%@page	import="com.ecims.portlet.admin.workflowconfig.WorkFlowConfigPortletUtil"%>
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
<%@page import="smartpay.entity.enumerations.AgencyType"%>
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

WorkFlowConfigPortletState portletState = WorkFlowConfigPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(WorkFlowConfigPortletState.class);
List<AgencyType> agencyTypeList = AgencyType.values();

%>
<portlet:actionURL var="proceedToStepThree" name="processAction">
	<portlet:param name="action"
		value="<%=WORKFLOWCONFIG_ACTIONS.UPDATE_WORK_FLOW_CONFIG.name()%>" />
</portlet:actionURL>


<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>


	  
	  
<section class="container">
<div style="padding-left:10px; padding-right:10px;" class="col-sm-9 panel panel-default" role="news">
	<h4>Update Automatic Work Flow Configuration</h4>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Work Flow Configuration Details</span></div>
	  <div class="panel-body">
	    	
	    <form  id="startRegFormId" action="<%=proceedToStepThree%>" data-validate="parsley" method="post" enctype="application/x-www-form-urlencoded">
	     <fieldset>
			  <legend>Automatic Work Flow Configuration Details
			  </legend>
			  
			  
			   <div style="padding-bottom:5px"><strong>Item Category:</strong>
				<div>
				<select class="clear form-control formwidth50" name="itemCategory" data-required="true" id="itemCategory">
					<option value="-1">-Select One-</option>
					<%
					for(Iterator<ItemCategory> it = portletState.getItemCategoryList().iterator(); it.hasNext();)
					{
						ItemCategory at = it.next();
						%>
						<option value="<%=at.getId()%>"><%=at.getItemCategoryName()%></option>
						<%
					}
					%>
				</select>
				  
				</div>
			  </div>
			  
			  
			  
			  <div style="padding-bottom:5px"><strong>Set Up Automatic Work Flow Process:</strong>
				  <table width="100%" class="table table-hover" id="btable">
					<thead>
					
					  <th>Agency</th>
					  <th>Position In WorkFlow</th>
					
					</thead>
					<%
				  for(Iterator<Agency> iter1 = portletState.getAgencyList().iterator(); iter1.hasNext();)
				  {
					 	
					  Agency ed= iter1.next();
						 
					  %>
					  <tr>
					  	
						  <td>
							<div>
								<select class="clear form-control formwidth50" name="agency" id="agency">
									<option value="">-Select Agency-</option>
									<%
									for(Iterator<Agency> it = portletState.getAgencyList().iterator(); it.hasNext();)
									{
										Agency at = it.next();
										%>
										<option value="<%=at.getId()%>"><%=at.getAgencyName()%></option>
										<%
									}
									%>
								</select>
							</div>
						</td>
					  	  <td>
							<div>
					  	  		<select class="clear form-control formwidth50" name="positionId" id="positionId">
									<option value="">-Select Position-</option>
									<%
									for(int i=1; i<(portletState.getAgencyList().size() + 1); i++)
									{
										
										%>
										<option value="<%=i%>"><%=i%></option>
										<%
									}
									%>
								</select>
							</div>
						  </td>
					
					  </tr>
					  <%
				  }
					%>
				   </table>
			  </div> 
			   
			  
			  
		  </fieldset>
		  
			  
		  
		  
		  
	      <div>
	      	<input type="hidden" value="" name="act" id="act" />
	      	<button type="submit" class="btn btn-success" style="float:right" onclick="javascript:submitForm('saveworkflowprocess')">Save Work Flow Process</button>
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