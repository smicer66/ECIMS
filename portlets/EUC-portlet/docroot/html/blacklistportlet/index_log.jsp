<%@page import="smartpay.entity.BlackListLog"%>
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
		value="<%=BLACKLIST_ACTIONS.HANDLE_ACTIONS_ON_BLACKLISTED_APPLICANTS.name()%>" />
</portlet:actionURL>

<div style="padding:10px" id="print-content">
<div class="panel panel-info">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>History of Agency's OR Individuals Blacklisting</strong></div>
  	<div class="panel-body">
  		<p>Click on an entry to view full details on the blacklist.</p>
		
  	</div>
		<form  id="userListFormId" action="<%=listing%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
			
		<legend>Blacklist Listing</legend>
		  <%
		  if(portletState.getBlackListLogListing()!=null && portletState.getBlackListLogListing().size()>0)
		  {
			  %>
			  
			  <table width="100%" class="table table-hover" id="btable">
				<thead>
				  <th>Date Blacklisted</th>
				  <th>Action Performed</th>
				  <th>Applicant Type</th>
				  <th>Applicant Name</th>
				  <th>Applicant Number</th>
				  <th>Action Performed By</th>
				  <th>Action</th>
				</thead>
				<%
			  for(Iterator<BlackListLog> iter1 = portletState.getBlackListLogListing().iterator(); iter1.hasNext();)
			  {
				  
				  BlackListLog blackList= iter1.next();
				  Applicant applicant = blackList.getApplicant();
				  Agency agency = blackList.getAgency();
				  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
				  String dateApplied = sdf.format(new Date(blackList.getDatePerformed().getTime()));
				  
				  
				  %>
				  <tr>
					<td><%=dateApplied%></td>
					<td><%=blackList.getActionPerformed()%></td>
					<td><%=(applicant!=null) ? "Applicant" : "Agency" %></td>
					<td><%=(agency!=null) ? agency.getAgencyName() : applicant.getPortalUser().getFirstName() + " " + applicant.getPortalUser().getSurname() %></td>
					<td><%=(agency!=null) ? "N/A" : applicant.getApplicantNumber() %></td>
					<td><%=blackList.getPortalUser().getFirstName() + " " + blackList.getPortalUser().getSurname() %></td>
					<td>
					
					
					<%
					if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
			  		{
			  		%>
							<div class="btn-group">
								<button type="button" class="btn btn-success">Action</button>
							  	<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								</button>
							  	<ul class="dropdown-menu" role="menu">
									<li><a href="javascript: handleButtonAction('viewBlackListHistoryDetails', '<%=blackList.getId()%>')">View Blacklist Details</a></li>
									
							  	</ul>
							</div>
					<%
			  		}
					%>
					
								  
					</td>
				  	
				  </tr>
				  <%
			  }
				%>
			   </table>
			  <%
		  }
		  %>
		<input type="hidden" name="selectedApplications" id="selectedApplications" value="" />
		<input type="hidden" name="selectedUserAction" id="selectedUserAction" value="" />	
	</form>
</div>
</div>
<input type="button" onclick="printDiv1('print-content')" value="Print Page"/>

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
	
	if(action=='viewBlackListHistoryDetails')
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

function printDiv1(divId) {
	var mywindow = window.open('', divId, 'height=400,width=600');
	var data = document.getElementById(divId).innerHTML;
	mywindow.document.write('<html><head><title>ECIMS</title>');
	//mywindow.document.write('<link href="http://localhost/html/css/main.css?browserId=other&amp;themeId=DarkRider_WAR_DarkRidertheme&amp;languageId=en_US&amp;b=6100&amp;t=1323698628000" rel="stylesheet" type="text/css"><link href="http://localhost/EUC-portlet/css/main.css?browserId=other&amp;themeId=DarkRider_WAR_DarkRidertheme&amp;languageId=en_US&amp;b=6100&amp;t=1416975581850" rel="stylesheet" type="text/css"><link class="lfr-css-file" href="http://localhost/DarkRider-theme/css/main.css?browserId=other&amp;themeId=DarkRider_WAR_DarkRidertheme&amp;languageId=en_US&amp;b=6100&amp;t=1416667852292" rel="stylesheet" type="text/css"><link href="http://localhost/resources/css/kelvin/framework.css" rel="stylesheet"><link href="http://localhost/resources/css/kelvin/fonts.css" rel="stylesheet"><link href="http://localhost/resources/css/kelvin/default.css" rel="stylesheet"><link href="http://localhost/resources/css/kelvin/style.css" rel="stylesheet"><link href="http://localhost/resources/css/kelvin/fotorama.css" rel="stylesheet">');
	mywindow.document.write('</head><body >');
	mywindow.document.write(data);
	mywindow.document.write('</body></html>');
	mywindow.document.close(); // necessary for IE >= 10
	mywindow.focus(); // necessary for IE >= 10
	
	mywindow.print();
	mywindow.close();
	
	return true;
}
</script>

