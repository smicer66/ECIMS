<%@page import="smartpay.entity.PortCode"%>
<%@page import="smartpay.entity.enumerations.CertificateStatus"%>
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
<link rel="stylesheet" href='<%=faceboxCssUrl%>' type="text/css" />
<link rel="stylesheet" href='<%=pagingUrl%>' type="text/css" />
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" />
<%

ReportPortletState portletState = ReportPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ReportPortletState.class);
List<CertificateStatus> certificateStatus = CertificateStatus.values();
Collection<PortCode> portList = portletState.getPortList();

%>


<portlet:actionURL var="proceedToStepOne" name="processAction">
	<portlet:param name="action"
		value="<%=REPORTING_ACTIONS.CREATE_A_CERTIFICATE_REPORT.name()%>" />
</portlet:actionURL>

<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>
		
		
<div style="padding:10px; width:900px"> 	
    <div class="panel  panel-primary">
		<div class="panel-heading" style="padding-bottom:25px">
			<div style="color:white; font-weight: bold; float:left">Report on End-User Certificates!</div>
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
			      <div>
				  	<div style="padding-bottom:10px"><strong>Generate reports on End-User Certificates based on this filter properties </strong></div>
			      	<div>
			          	<div>Validity Period is between:</div>
						<div style="padding-bottom:10px"><input type="text" name="startDate" id="startDate" value="<%=portletState.getStartDate()==null ? "" : portletState.getStartDate()%>" placeholder="yyyy/mm/dd" readonly="readonly" /> and <input readonly="readonly" type="text" name="endDate" id="endDate" value="<%=portletState.getEndDate()==null ? "" : portletState.getEndDate()%>" placeholder="yyyy/mm/dd" /></div>
			        </div>
					<div>
			          	<div>Application Number:</div>
						<div style="padding-bottom:10px"><input type="text" name="applicationNumber" id="applicationNumber" value="<%=portletState.getApplicationNumber()==null ? "" : portletState.getApplicationNumber() %>" /></div>
			        </div>
					<div>
			          	<div>Applicant Number:</div>
						<div style="padding-bottom:10px"><input type="text" name="applicantNumber" id="applicantNumber" value="<%=portletState.getApplicantNumber()==null ? "": portletState.getApplicantNumber() %>" /></div>
			        </div>
					<div>
			          	<div>Certificate Status:</div>
							<div style="padding-bottom:10px">
								<select name="certificateStatus" id="certificateStatus" class="form-control">
									<option value="">-Select An Certificate Status-</option>
									<%
									String selected="";
									for(Iterator<CertificateStatus> it = certificateStatus.iterator(); it.hasNext();)
									{
										String itName = it.next().getValue();
										selected="";
										if(portletState.getCertificateStatus()!=null && portletState.getCertificateStatus().equals(itName))
										{
											selected = "selected='selected'";
										}
									%>
										<option <%=selected%> value="<%=itName%>"><%=itName%></option>
									<%
									}
									%>
							  </select>
							<div>
						</div>
			        </div>
					<div>
			          	<div style="font-weight:bold; display:none"><input onclick="javascript:handleDefaultColumnShow()" type="checkbox" <%=(portletState.getDefaultColumnShow()!=null && portletState.getDefaultColumnShow().equals("1")) ? "checked='checked'" : (portletState.getDefaultColumnShow()==null ? "checked='checked'" : "") %> name="defaultColumnShow" id="defaultColumnShow" value="1" />&nbsp;&nbsp;&nbsp;&nbsp;Display default columns in generated report! <br><a href="javascript:displayCustomColumns('defaultColumnShow')">Customize Columns to display</a></div>
			        </div>
					<div style="display:none" id="customizeColumn">
			          	<div>Select Columns to display:</div>
			          	<%=(portletState.getDefaultColumnShow()!=null && portletState.getDefaultColumnShow().equals("1")) ? "checked='checked'" : "" %> 
						<div style="padding-bottom:10px">
						<%
						String checked="";
						if(portletState.getShowValidityPeriod()!=null)
						{
							checked = "checked='checked'";
						}
						%>
							<input type="checkbox" name="showValidityPeriod" id="showValidityPeriod" value="VALIDITYPERIOD" />Validity Period
						</div>
						<div style="padding-bottom:10px">
						<%
						checked="";
						if(portletState.getShowApplicationNumber()!=null)
						{
							checked = "checked='checked'";
						}
						%>
							<input type="checkbox" name="showApplicationNumber" id="showApplicationNumber" value="APPLICATIONNUMBER" />Application Number
						</div>
						<div style="padding-bottom:10px">
						<%
						checked="";
						if(portletState.getShowApplicantNumber()!=null)
						{
							checked = "checked='checked'";
						}
						%>
							<input type="checkbox" name="showApplicantNumber" id="showApplicantNumber" value="APPLICANTNUMBER" />Applicant Number
						</div>
						<div style="padding-bottom:10px">
						<%
						checked="";
						if(portletState.getShowCertificateStatus()!=null)
						{
							checked = "checked='checked'";
						}
						%>
							<input type="checkbox" name="showCertificateStatus" id="showCertificateStatus" value="CERTIFICATESTATUS" />Certificate Status
						</div>
						<div style="padding-bottom:10px">
						<%
						checked="";
						if(portletState.getShowImportationCosts()!=null)
						{
							checked = "checked='checked'";
						}
						%>
							<input type="checkbox" name="showImportationCosts" id="showImportationCosts" value="IMPORTATIONCOSTS" />Importation Costs
						</div>
			        </div>
					
					<div style="padding-top:10px; display:none">
			          	<div>Generate Report and Send Report to this email Address:</div>
						<div style="padding-bottom:10px"><input type="text" name="reportEmailSend" id="reportEmailSend" value="<%=portletState.getReportEmailSend()==null ? "" : portletState.getReportEmailSend()%>" placeholder="" /></div>
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
		yearRange: "-<%=earliestYearDiff%>:+10",
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


function submitform(clicked)
{
	document.getElementById('act').value=clicked;
	document.getElementById('panelcreatorform').submit;
	
}
</script>