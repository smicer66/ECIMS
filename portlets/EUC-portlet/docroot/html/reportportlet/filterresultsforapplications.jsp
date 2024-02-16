<%@page import="smartpay.entity.PortCode"%>
<%@page import="smartpay.entity.ItemCategory"%>
<%@page import="smartpay.entity.Country"%>
<%@page import="smartpay.entity.enumerations.ApplicationStatus"%>
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
<%@page import="smartpay.entity.Company"%>
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
List<ApplicationStatus> applicationStatus = ApplicationStatus.values();


%>


<portlet:actionURL var="proceedToStepOne" name="processAction">
	<portlet:param name="action"
		value="<%=REPORTING_ACTIONS.CREATE_AN_APPLICATION_REPORT_STEP_TWO.name()%>" />
</portlet:actionURL>


<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<div style="padding:10px; width:900px"> 	
    <div class="panel  panel-primary">
		<div class="panel-heading" style="padding-bottom:25px">
			<div style="color:white; font-weight: bold; float:left">Report on End-User Applications!</div>
			<div style="color:white; font-weight: bold; float:right">Report Format: 
							<select name="reportFormat" id="reportFormat" class="form-control">
								<!-- <option value="PDF">PDF</option>-->
								<option value="Spreadsheet">Spreadsheet</option>
							</select>
			</div>
		</div>
		<div class="panel-body">
		    <form  id="panelcreatorform" action="<%=proceedToStepOne%>" method="post" enctype="application/x-www-form-urlencoded">
			    <fieldset>
			      <div style="width:500px">
				  	<div style="padding-bottom:10px"><strong>Generate reports on EUC Applications based on this filter properties </strong></div>
			      	<div>
			          	<div style="font-weight:bold">Date Period Application was created is between:</div>
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
			          	<div style="font-weight:bold">Application Number :</div>
					  <div style="padding-bottom:10px"><input otype="text"  class="form-control" name="applicationNumber" id="applicationNumber" value="<%=portletState.getApplicationNumber()==null ? "" : portletState.getApplicationNumber()%>" />
					  </div>
			        </div>
					<div>
			          	<div style="font-weight:bold">Applicant Number:</div>
						<div style="padding-bottom:10px"><input type="text"  class="form-control" name="applicantNumber" id="applicantNumber" value="<%=portletState.getApplicantNumber()==null ? "" : portletState.getApplicantNumber()%>" />
						</div>
			        </div>
					<div>
			          	<div style="font-weight:bold">Handled By Exception:</div>
							<div style="padding-bottom:10px">
								<select name="exceptionType" id="exceptionType" class="form-control">
									<option value="">-Select An Exception Type-</option>
									<%
									String selected="";
									String selected1="";
									if(portletState.getExceptionType()!=null && portletState.getExceptionType().equals("Yes"))
									{
										selected="selected='selected'";
									}else if(portletState.getExceptionType()!=null && portletState.getExceptionType().equals("No"))
									{
										selected1="selected='selected'";
									}
									%>
									<option <%=selected %> value="Yes">Yes</option>
									<option <%=selected1 %> value="No">No</option>
							  </select>
							<div>
						</div>
			        </div>
					<div>
			          	<div style="font-weight:bold">Status of Application:</div>
							<div style="padding-bottom:10px">
								<select name="status" id="status" class="form-control">
									<option value="">-Select An Application Status-</option>
									<%
									selected="";
									for(Iterator<ApplicationStatus> it = applicationStatus.iterator(); it.hasNext();)
									{
										ApplicationStatus itName = it.next();
										selected= "";
										if(portletState.getApplicationStatus()!=null && portletState.getApplicationStatus().equals(itName))
										{
											selected = "selected='selected'";
										}
									%>
										<option <%=selected%> value="<%=itName.getValue()%>"><%=itName.getValue()%></option>
									<%
									}
									%>
							  </select>
							<div>
						</div>
			        </div>
			        <div>
			          	<div style="font-weight:bold">Total Cost of Importation Items is between:</div>
						<div style="padding-bottom:10px">
							<div style="width:200px; float:left">
								<input  type="text" onkeypress="return onlyDoubleKey(event, 'amountLowerLimit')"  class="form-control" name="amountLowerLimit" id="amountLowerLimit" value="<%=portletState.getAmountLowerLimit()==null ? "" : portletState.getAmountLowerLimit()%>" placeholder="" />
							</div>
							<div style="width:50px; float:left; padding-left:5px; padding-right:5px; padding-top:5px;"> and </div>
							<div style="width:200px; float:left">
								<input onkeypress="return onlyDoubleKey(event, 'amountUpperLimit')"  type="text"  class="form-control" name="amountUpperLimit" id="amountUpperLimit" value="<%=portletState.getAmountUpperLimit()==null ? "" : portletState.getAmountUpperLimit()%>" placeholder="" />
							</div>
						</div>
			        </div>
					<div style="clear:both">
			          	<div style="font-weight:bold">Importing Country :</div>
						<div style="padding-bottom:10px">
							<select name="country" id="country" class="form-control">
								<option value="">-Select An Importation Source Country-</option>
								<%
								for(Iterator<Country> it = portletState.getCountryList().iterator(); it.hasNext();)
								{
									Country c = it.next();
									selected= "";
									if(portletState.getCountrySelected()!=null && portletState.getCountrySelected().equals(Long.toString(c.getId())))
									{
										selected = "selected='selected'";
									}
							%>
								<option <%=selected%> value="<%=c.getName()%>"><%=c.getName()%></option>
								<%
								}
								%>
						  </select>
						</div>
			        </div>
					<div>
			          	<div style="font-weight:bold">Item Category :</div>
						<div style="padding-bottom:10px">
							<select name="itemCategory" id="itemCategory" class="form-control">
								<option value="">-Select A Item Category Of Entry-</option>
								<%
								for(Iterator<ItemCategory> it = portletState.getItemCategoryList().iterator(); it.hasNext();)
								{
									ItemCategory itemCategory = it.next();
									selected= "";
									if(portletState.getItemCategory()!=null && portletState.getItemCategory().equals(Long.toString(itemCategory.getId())))
									{
										selected = "selected='selected'";
									}
							%>
								<option <%=selected%> value="<%=itemCategory.getItemCategoryName()%>"><%=itemCategory.getItemCategoryName()%></option>
								<%
								}
								%>
						  </select>
						</div>
			        </div>
			        <div>
			          	<div style="font-weight:bold">Port of Entry:</div>
						<div style="padding-bottom:10px">
							<select name="portOfEntry" id="portOfEntry" class="form-control">
								<option value="">-Select A Port Of Entry-</option>
								<%
								for(Iterator<PortCode> it = portletState.getPortList().iterator(); it.hasNext();)
								{
									PortCode portCode = it.next();
									selected= "";
									if(portletState.getPortOfEntry()!=null && portletState.getPortOfEntry().equals(portCode.getPortCode()))
									{
										selected = "selected='selected'";
									}
							%>
								<option <%=selected%> value="<%=portCode.getPortCode()%>"><%=portCode.getPortCode()%></option>
								<%
								}
								%>
						  </select>
						</div>
			        </div>
					<div>
			          	<div style="font-weight:bold; display:none"><input onclick="javascript:handleDefaultColumnShow()" type="checkbox" <%=(portletState.getDefaultColumnShow()!=null && portletState.getDefaultColumnShow().equals("1")) ? "checked='checked'" : (portletState.getDefaultColumnShow()==null ? "checked='checked'" : "") %> name="defaultColumnShow" id="defaultColumnShow" value="1" />&nbsp;&nbsp;&nbsp;&nbsp;Display default columns in generated report! <br><a href="javascript:displayCustomColumns('defaultColumnShow')">Customize Columns to display</a></div>
			        </div>
					<div style="display:none; padding:10px;" id="customizeColumn">
						<div style="padding:10px; width:500px"> 	
						    <div class="panel  panel-primary">
								<div class="panel-heading"><span style="color:white; font-weight: bold">Select Columns to Display:</span></div>
								<div class="panel-body">
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										String checked="";
										if(portletState.getShowApplicationNumber()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowApplicationNumber()!=null && portletState.getShowApplicationNumber().equals("APPLICATIONNUMBER")) ? "checked='checked'" : "" %> name="showApplicationNumber" id="showApplicationNumber" value="APPLICATIONNUMBER" />
										Application Number
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowDateCreated()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowDateCreated()!=null && portletState.getShowDateCreated().equals("DATECREATED")) ? "checked='checked'" : "" %> name="showDateCreated" id="showDateCreated" value="DATECREATED" />
										Date Created </div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowStatus()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowStatus()!=null && portletState.getShowStatus().equals("STATUS")) ? "checked='checked'" : "" %> name="showStatus" id="showStatus" value="STATUS" />
										Application Status</div>
								  <div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowException()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowException()!=null && portletState.getShowException().equals("TREATEDBYEXCEPTION")) ? "checked='checked'" : "" %> name="showException" id="showException" value="TREATEDBYEXCEPTION" />
										Treated By Exception </div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowItemCategory()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowItemCategory()!=null && portletState.getShowItemCategory().equals("ITEMCATEGORY")) ? "checked='checked'" : "" %> name="showItemCategory" id="showItemCategory" value="ITEMCATEGORY" />
										Item Category </div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowApplicantName()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowApplicantName()!=null && portletState.getShowApplicantName().equals("APPLICANTNAME")) ? "checked='checked'" : "" %> name="showApplicantName" id="showApplicantName" value="APPLICANTNAME" />
										Applicant Name 
									</div>
									<div style="padding-bottom:10px; padding-right:10px">
										<%
										checked="";
										if(portletState.getShowImportationPort()!=null)
										{
											checked = "checked='checked'";
										}
										%>
										<input <%=checked %> type="checkbox" <%=(portletState.getShowImportationPort()!=null && portletState.getShowImportationPort().equals("IMPORTATIONPORT")) ? "checked='checked'" : "" %> name="showImportationPort" id="showImportationPort" value="IMPORTATIONPORT" />
										Importation Port 
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
