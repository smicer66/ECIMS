<%@page import="smartpay.entity.BlackList"%>
<%@page import="smartpay.entity.Agency"%>
<%@page import="com.ecims.portlet.blacklist.BlacklistPortletState"%>
<%@page import="com.ecims.portlet.blacklist.BlacklistPortletState.*"%>
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
<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.entity.Company"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.RoleType"%>
<%@page import="smartpay.entity.Applicant"%>
<%@page import="smartpay.entity.ApplicationItem"%>
<%@page import="smartpay.entity.Application"%>
<%@page import="smartpay.entity.enumerations.UserStatus"%>
<%@page import="smartpay.entity.enumerations.RoleConstants"%>
<%@page import="smartpay.entity.enumerations.ApplicantStatus"%>
<%@page import="smartpay.entity.enumerations.ApplicationStatus"%>
<%@page import="java.text.DateFormat"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />
<%
	String resourceBaseURL = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ "/resources";
	String faceboxCssUrl = resourceBaseURL + "/css/facebox.css";
	String pagingUrl = resourceBaseURL + "/css/paging.css";

	String jqueryDataTableCssUrl = resourceBaseURL + "/css/jquery.dataTables.css";
	String jqueryDataTableThemeCssUrl = resourceBaseURL + "/css/jquery.dataTables_themeroller.css";
	String jqueryDataTableUrl = resourceBaseURL + "/js/jquery.dataTables.min.js";

		
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
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" /><%

BlacklistPortletState portletState = BlacklistPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(BlacklistPortletState.class);
Applicant applicant = portletState.getApplicantToBlackList();

%>
<%

if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
{
%>
<jsp:include page="/html/blacklistportlet/tabs.jsp" flush="" />
<%
}
%>

<portlet:actionURL var="listing" name="processAction">
	<portlet:param name="action"
		value="<%=BLACKLIST_ACTIONS.BLACKLIST_AN_APPLICANT_STEP_TWO.name()%>" />
</portlet:actionURL>

<div style="padding:10px" id="print-content">
<div class="panel panel-info">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>BlackList Agency or Applicant</strong></div>
  	<div class="panel-body">
  		<p><b style="color: red">Step Two: Provide Reasons for Blacklisting</b></p>
  		
  	</div>
		<form  id="userListFormId" action="<%=listing%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
			<p><b>Applicant Details</b></p>
			  <!-- brnr/yzOiNfKomD330VFLc5B9qE= -->
			  <table width="100%" class="table table-hover" id="btable">
			  	<tr>
			  		<td><b>Applicant Type</b></td>
			  		<td><%=applicant.getPortalUser().getAgency()==null ? applicant.getApplicantType().getValue() : ("Agency [" + applicant.getPortalUser().getAgency().getAgencyType().getValue() + "]") %></td>
			  	</tr>
			  	<tr>
			  		<td><b>Applicant Name or Agency Name</b></td>
			  		<td><%=applicant.getPortalUser().getAgency()!=null ? applicant.getPortalUser().getAgency().getAgencyName() : (applicant.getPortalUser().getFirstName() + " " + applicant.getPortalUser().getSurname()) %></td>
			  	</tr>
			  	<tr>
			  		<td><b>Applicant Number</b></td>
			  		<td><%=applicant.getApplicantNumber() %></td>
			  	</tr>
			  	<tr>
			  		<td><b>Account Creation Date</b></td>
			  		<td><%=applicant.getPortalUser().getDateCreated()%></td>
			  	</tr>
			  	<tr>
			  		<td><b>Applicant Email Address</b></td>
			  		<td><%=applicant.getPortalUser().getEmailAddress()%></td>
			  	</tr>
			  	<tr>
			  		<td colspan="2">
			  		<p><b>Provide Reasons for BlackListing Agency in the text box below</b></p>
			  		<textarea style="width: 100%; height: 120px;" name="reason" placeholder="Provide Reason For Unblacklisting This Agency or Applicant"></textarea>
			  		</td>
			  	</tr>
			   </table>
		<input type="submit" value="BlackList This Applicant/Agency" class="btn btn-success btn-cons" style="float:right" />
		<input type="hidden" name="selectedApplications" id="selectedApplications" value="" />
		<input type="hidden" name="selectedUserAction" id="selectedUserAction" value="" />	
	</form>
</div>
</div>


<script type="text/javascript" charset="utf-8" src="<%=jqueryDataTableUrl%>"></script>

<script type="text/javascript">
$(document).ready(function() {
    $('#btable').dataTable();
} );




//gyus=")%>'+aert +'&iosdp='+usId" +		//gyus = portalUser, iosdp = certificate id
//handleButtonAction2(action, aert, usId)		//aert = portalUser, usId = certificate id
function handleButtonAction2(action, usId, aert)
{
	if(action=='downloadApplicantTicket')
	{
		document.location.href='<%=response.encodeURL(request.getContextPath()+"/ActiveServlet?action=downloadApplicantTicket&gyus=")%>'+aert +'&iosdp='+usId;
	}
}

function handleButtonAction3(action, usId, aert)
{
	if (action=='downloadAgencyTicket')
	{
		document.location.href='<%=response.encodeURL(request.getContextPath()+"/ActiveServlet?action=downloadAgencyTicket&gyus=")%>'+aert +'&iosdp='+usId;
	}
}

function handleButtonAction(action, usId){
	
	if(action=='approve' || action=='reject')
	{
		if(confirm("Are you sure you want to approve the selected application(s)?"))
		{
			document.getElementById('selectedApplications').value = usId;
			document.getElementById('selectedUserAction').value = action;
			document.getElementById('userListFormId').submit();
		}
	}else
	{
		document.getElementById('selectedApplications').value = usId;
		document.getElementById('selectedUserAction').value = action;
		document.getElementById('userListFormId').submit();
	}
}


function handleSelectAll()
{
	if(document.getElementById('clickSelectAll').checked==true)
	{
		var cbs = document.getElementsByName('selectAllCheckbox');
		for(var i=0; i<cbs.length; i++)
		{
			cbs[i].checked = true;
		}
	}	  
	else
	{
		var cbs = document.getElementsByName('selectAllCheckbox');
		for(var i=0; i<cbs.length; i++)
		{
			cbs[i].checked = false;
		}
	}
}



function isCheckBoxesChecked()
{
	var cbs = document.getElementsByName('selectAllCheckbox');
	var c = 0;
	for(var i=0; i < cbs.length; i++) {
		if(document.getElementsByName('selectAllCheckbox')[i].checked)
		{
			c++;
		}
	}
	
	if(c==0)
		return false;
	else
		return true;
}



function uncheckAll()
{
	var cbs = document.getElementsByName('selectAllCheckbox');
	var c = 0;
	for(var i=0; i < cbs.length; i++) {
		document.getElementsByName('selectAllCheckbox')[i].checked=false;
	}
}
</script>

