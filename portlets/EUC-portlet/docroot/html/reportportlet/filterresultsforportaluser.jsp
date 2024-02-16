<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.enumerations.UserStatus"%>
<%@page import="smartpay.entity.RoleType"%>
<%@page import="smartpay.entity.Agency"%>
<%@page import="smartpay.entity.State"%>
<%@page import="smartpay.entity.enumerations.ApplicantType"%>
<%@page import="com.ecims.portlet.reportportlet.ReportPortletState"%>
<%@page import="com.ecims.portlet.reportportlet.ReportPortletState.*"%>
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
<%@ page import="java.util.List"%>
<%@ page import="java.util.TimeZone"%>
<%@ page import="java.util.GregorianCalendar"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="java.text.DateFormat"%>
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
<%

ReportPortletState portletState = ReportPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ReportPortletState.class);
List<ApplicantType> paymentTypes = ApplicantType.values();
List<UserStatus> userStatusList = UserStatus.values();
List<RoleTypeConstants> roleTypeList = RoleTypeConstants.values();
%>


<portlet:actionURL var="proceedToStepOne" name="processAction">
	<portlet:param name="action"
		value="<%=REPORTING_ACTIONS.CREATE_AN_APPLICANT_REPORT_STEP_TWO.name()%>" />
</portlet:actionURL>


<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<div style="padding:10px; width:900px"> 	
    <div class="panel  panel-primary">
		<div class="panel-heading" style="padding-bottom:25px">
			<div style="color:white; font-weight: bold; float:left">Report on Portal Users!</div>
			<div style="color:white; font-weight: bold; float:right">Report Format: 
							<select name="reportFormat" id="reportFormat" class="form-control">
								<!-- <option value="">-Select A Report Format-</option>
								<option value="PDF">PDF</option>-->
								<option value="Spreadsheet">Spreadsheet</option>
							</select>
			</div>
		</div>
		<div class="panel-body">
		    <form  id="panelcreatorform" action="<%=proceedToStepOne%>" method="post" enctype="application/x-www-form-urlencoded">
			    <fieldset>
			      <div style="width:500px">
				  	<div style="padding-bottom:10px"><h2><strong>Filter properties </strong></h2></div>
			      	<div>
			          	<div style="font-weight:bold">Date User Account Was Created is between:</div>
						<div style="padding-bottom:10px">
							<div style="width:200px; float:left">
								<input  type="text"  class="form-control" name="startDate" id="startDate" value="<%=portletState.getStartDate()==null ? "" : portletState.getStartDate()%>" placeholder="yyyy/mm/dd" readonly="readonly" />
							</div>
							<div style="width:50px; float:left; padding-left:5px; padding-right:5px; padding-top:5px;"> and </div>
							<div style="width:200px; float:left">
								<input readonly="readonly"  type="text"  class="form-control" name="endDate" id="endDate" value="<%=portletState.getEndDate()==null ? "" : portletState.getEndDate()%>" placeholder="yyyy/mm/dd" />
							</div>
						</div>
			        </div>
			        <div style="clear:both">
			          	<div style="font-weight:bold">Date of Birth of Users is between:</div>
						<div style="padding-bottom:10px">
							<div style="width:200px; float:left">
								<input  type="text"  class="form-control" name="dobStartDate" id="dobStartDate" value="<%=portletState.getDobStartDate()==null ? "" : portletState.getDobStartDate()%>" placeholder="yyyy/mm/dd" readonly="readonly" />
							</div>
							<div style="width:50px; float:left; padding-left:5px; padding-right:5px; padding-top:5px;"> and </div>
							<div style="width:200px; float:left">
								<input readonly="readonly"  type="text"  class="form-control" name="dobEndDate" id="dobEndDate" value="<%=portletState.getDobEndDate()==null ? "" : portletState.getDobEndDate()%>" placeholder="yyyy/mm/dd" />
							</div>
						</div>
			        </div>
			        <div style="clear:both">
			          	<div style="font-weight:bold">Gender:</div>
							<div style="padding-bottom:10px">
								<select name="gender" id="gender" class="form-control">
									<option value="">-Select A Gender-</option>
									<%
									String selected="";
									String selected1 = "";
									if(portletState.getGender()!=null && portletState.getGender().equalsIgnoreCase("Male"))
										selected1 = "selected='selected'";
									if(portletState.getGender()!=null && portletState.getGender().equalsIgnoreCase("Female"))
										selected = "selected='selected'";
									%>
									<option <%=selected %> value="Female">Female</option>
									<option <%=selected1 %> value="Male">Male</option>
								</select>
							<div>
						</div>
			        </div>
			        
			        <div style="clear:both">
			          	<div style="font-weight:bold">Marital Status:</div>
							<div style="padding-bottom:10px">
								<select name="maritalStatus" id="maritalStatus" class="form-control">
									<option value="">-Select A Marital Status-</option>
									<%
									selected="";
									if(portletState.getMaritalStatus()!=null && portletState.getMaritalStatus().equals("Single"))	
									{
										selected = "selected='selected'";
									}
									%>
									<option <%=selected%> value="Single">Single</option>
									<%
									selected="";
									if(portletState.getMaritalStatus()!=null && portletState.getMaritalStatus().equals("Married"))	
									{
										selected = "selected='selected'";
									}
									%>
									<option <%=selected%> value="Married">Married</option>
									<%
									selected="";
									if(portletState.getMaritalStatus()!=null && portletState.getMaritalStatus().equals("Divorced"))	
									{
										selected = "selected='selected'";
									}
									%>
									<option <%=selected%> value="Divorced">Divorced</option>
								</select>
							<div>
						</div>
			        </div>
			        
			        <div style="clear:both">
			          	<div style="font-weight:bold">State Of Residence:</div>
							<div style="padding-bottom:10px">
								<select name="selectedState" id="selectedState" class="form-control">
									<option value="">-Select A State-</option>
									<%
									
									for(Iterator<State> it = portletState.getStateList().iterator(); it.hasNext();)
									{
										State itName = it.next();
										selected="";
										if(portletState.getSelectedState()!=null && portletState.getSelectedState().equals(itName.getName()))
										{
											selected = "selected='selected'";
										}
									%>
										<option <%=selected %> value="<%=itName.getName()%>"><%=itName.getName()%></option>
									<%
									}
									%>
								</select>
							<div>
						</div>
			        </div>
			        
			        <div style="clear:both">
			          	<div style="font-weight:bold">Users Belongs to This Agency:</div>
							<div style="padding-bottom:10px">
								<select name="agencySelected" id="agencySelected" class="form-control">
									<option value="">-Select An Agency-</option>
									<%
									
									for(Iterator<Agency> it = portletState.getAgencyList().iterator(); it.hasNext();)
									{
										Agency itName = it.next();
										selected="";
										if(portletState.getAgencySelected()!=null && portletState.getAgencySelected().equals(itName.getAgencyName()))
										{
											selected = "selected='selected'";
										}
									%>
										<option <%=selected %> value="<%=itName.getAgencyName()%>"><%=itName.getAgencyName()%> - <%=itName.getAgencyType().getValue() %></option>
									<%
									}
									%>
								</select>
							<div>
						</div>
			        </div>
			        
			        
			        <div style="clear:both">
			          	<div style="font-weight:bold">Type of User:</div>
							<div style="padding-bottom:10px">
								<select name="selectedRoleType" id="selectedRoleType" class="form-control">
									<option value="">-Select A Type of User-</option>
									<%
									
									for(Iterator<RoleTypeConstants> it = roleTypeList.iterator(); it.hasNext();)
									{
										RoleTypeConstants itName = it.next();
										selected="";
										if(portletState.getSelectedRoleType()!=null && portletState.getSelectedRoleType().equals(itName.getValue()))
										{
											selected = "selected='selected'";
										}
									%>
										<option <%=selected %> value="<%=itName.getValue()%>"><%=itName.getValue()%></option>
									<%
									}
									%>
								</select>
							<div>
						</div>
			        </div>
			        
			        <div style="clear:both">
			          	<div style="font-weight:bold">Status of User Account:</div>
							<div style="padding-bottom:10px">
								<select name="userStatus" id="userStatus" class="form-control">
									<option value="">-Select A Status Type-</option>
									<%
									
									for(Iterator<UserStatus> it = userStatusList.iterator(); it.hasNext();)
									{
										UserStatus itName = it.next();
										selected="";
										if(portletState.getUserStatus()!=null && portletState.getUserStatus().equals(itName.getValue()))
										{
											selected = "selected='selected'";
										}
									%>
										<option <%=selected %> value="<%=itName.getValue()%>"><%=itName.getValue()%></option>
									<%
									}
									%>
								</select>
							<div>
						</div>
			        </div>
					<div style="clear:both; display:none">
			          	<div style="font-weight:bold"><input onclick="javascript:handleDefaultColumnShow()" type="checkbox" <%=(portletState.getDefaultColumnShow()!=null && portletState.getDefaultColumnShow().equals("1")) ? "checked='checked'" : "" %> name="defaultColumnShow" id="defaultColumnShow" value="1" />&nbsp;&nbsp;&nbsp;&nbsp;Display default columns in generated report! <br><a href="javascript:displayCustomColumns('defaultColumnShow')">Customize Columns to display</a></div>
			        </div>
					<div style="display:none; padding:10px;" id="customizeColumn">
						<div style="padding:10px; width:500px"> 	
						    <div class="panel  panel-primary">
								<div class="panel-heading"><span style="color:white; font-weight: bold">Select Columns to Display:</span></div>
								<div class="panel-body">
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										String checked="";
										if(portletState.getShowFullName()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowFullName()!=null && portletState.getShowFullName().equals("FULLNAME")) ? "checked='checked'" : "" %> name="showFullName" id="showFullName" value="FULLNAME" />User's Full Name
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowAddress()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowAddress()!=null && portletState.getShowAddress().equals("ADDRESS")) ? "checked='checked'" : "" %> name="showAddress" id="showAddress" value="ADDRESS" />Address Of User
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowDOB()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowDOB()!=null && portletState.getShowDOB().equals("DOB")) ? "checked='checked'" : "" %> name="showDOB" id="showDOB" value="DOB" />Date Of Birth
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowEmailAddress()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowEmailAddress()!=null && portletState.getShowEmailAddress().equals("EMAILADDRESS")) ? "checked='checked'" : "" %> name="showEmailAddress" id="showEmailAddress" value="EMAILADDRESS" />User Email Address
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowMobileNumber()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowMobileNumber()!=null && portletState.getShowMobileNumber().equals("MOBILENO")) ? "checked='checked'" : "" %> name="showMobileNumber" id="showMobileNumber" value="MOBILENO" />User Mobile No.
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowMaritalStatus()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowMaritalStatus()!=null && portletState.getShowMaritalStatus().equals("MARITALSTATUS")) ? "checked='checked'" : "" %> name="showMaritalStatus" id="showMaritalStatus" value="MARITALSTATUS" />Marital Status
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowStateOfOrigin()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowStateOfOrigin()!=null && portletState.getShowStateOfOrigin().equals("STATEOFORIGIN")) ? "checked='checked'" : "" %> name="showStateOfOrigin" id="showStateOfOrigin" value="STATEOFORIGIN" />State Of Origin
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowUserAgency()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowUserAgency()!=null && portletState.getShowUserAgency().equals("AGENCYUSER")) ? "checked='checked'" : "" %> name="showUserAgency" id="showUserAgency" value="AGENCYUSER" />Users Agency
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowUserRole()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowUserRole()!=null && portletState.getShowUserRole().equals("1")) ? "checked='checked'" : (portletState.getShowUserRole()==null ? "checked='checked'" : "") %> name="showUserRole" id="showUserRole" value="USERROLE" />Type of User
									</div>
								</div>
							</div>
						</div>
			        </div>
					
					<div style="padding-top:10px; display:none">
			          	<div style="font-weight:bold">Generate Report and Send Report to this email Address:</div>
						<div style="padding-bottom:10px"><input  type="text"  class="form-control" name="reportEmailSend" id="reportEmailSend" value="<%=portletState.getReportEmailSend()==null ? "" : portletState.getReportEmailSend()%>" placeholder="" /></div>
			        </div>
			      </div>
			      </div>
				  <div style="padding-top:10px; padding-bottom:10px;">
			        <input type="hidden" name="act" id="act" value="">
			        <button name="filterreport" id="filterreport" class="btn btn-danger" style="float:left" onClick="javascript:submitform('back')">Back</button>
					<button name="filterreport" id="filterreport" class="btn btn-success" style="float:right" onClick="javascript:submitform('generate')">Generate Report</button>
			      </div>
			    </fieldset>
		    </form>
		</div>
	</div>
</div>

<script type="text/javascript" charset="utf-8" src="<%=jqueryJsUrl%>"></script>
<script type="text/javascript" charset="utf-8" src="<%=jqueryUIJsUrl%>"></script>
<script type="text/javascript">

<%
int year1 = Calendar.getInstance().get(Calendar.YEAR);
boolean proceed;
int startYear = 1990;
int earliestYearDiff = year1 - startYear;
%>

$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#endDate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "-<%=earliestYearDiff%>:+0",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yy/mm/dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false,
		onSelect: function(date){
			var date1 = $('#agmDate__NSMC').datepicker('getDate');
			//alert(date1);
			var maxdate = new Date(Date.parse(date1));
			//alert(maxdate.toDateString());
			var mindate = new Date(Date.parse(date1));
		}
	});
	
	
	
});


$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#startDate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "-<%=earliestYearDiff%>:+0",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yy/mm/dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false,
		onSelect: function(date){
			var date1 = $('#agmDate__NSMC').datepicker('getDate');
			//alert(date1);
			var maxdate = new Date(Date.parse(date1));
			//alert(maxdate.toDateString());
			var mindate = new Date(Date.parse(date1));
		}
	});
	
	
	
});


$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#dobEndDate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "-<%=earliestYearDiff%>:+0",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yy/mm/dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false,
		onSelect: function(date){
			var date1 = $('#agmDate__NSMC').datepicker('getDate');
			//alert(date1);
			var maxdate = new Date(Date.parse(date1));
			//alert(maxdate.toDateString());
			var mindate = new Date(Date.parse(date1));
		}
	});
	
	
	
});


$(function() {
	$.datepicker.setDefaults($.extend($.datepicker.regional['']));
	$('#dobStartDate').datepicker({
		changeMonth : true,
		changeYear : true,
		yearRange: "-<%=earliestYearDiff%>:+0",
		maxDate: -0,
		showButtonPanel : true,
		dateFormat: 'yy/mm/dd',
		showOn : "button",
		buttonImage : "<%=resourceBaseURL + "/images/calendar.gif"%>",
		buttonImageOnly : false,
		onSelect: function(date){
			var date1 = $('#agmDate__NSMC').datepicker('getDate');
			//alert(date1);
			var maxdate = new Date(Date.parse(date1));
			//alert(maxdate.toDateString());
			var mindate = new Date(Date.parse(date1));
		}
	});
	
	
	
});



function downloadReport()
{
	document.location.href='<%=response.encodeURL(request.getContextPath()+"/ActiveServlet?action=downloadReceipt&reportId=" + portletState.getFilName())%>';
}


function displayCustomColumns(id)
{
	document.getElementById('defaultColumnShow').checked = false;
	document.getElementById('customizeColumn').style.display = 'block';
}


function handleDefaultColumnShow()
{
	if(document.getElementById('defaultColumnShow').checked)
	{
		document.getElementById('customizeColumn').style.display = 'none';
	}else
	{
		document.getElementById('customizeColumn').style.display = 'block';
	}
}



function onlyDoubleKey(e, elementId)
{
	var src = (e.srcElement || e.target);
	var unicode=e.charCode? e.charCode : e.keyCode;
	
	var check = false;
	var lenAfter = document.getElementById(elementId).value.length - document.getElementById(elementId).value.indexOf(".");
	if(document.getElementById(elementId).value.length>0 && 
			document.getElementById(elementId).value.indexOf(".")==-1 && 
			lenAfter<3)
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


function submitform(clicked)
{
	document.getElementById('act').value=clicked;
	document.getElementById('panelcreatorform').submit;
	
}

</script>