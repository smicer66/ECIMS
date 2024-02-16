<%@page import="com.ecims.portlet.usermanagement.UserManagementPortletState"%>
<%@page import="com.ecims.portlet.usermanagement.UserManagementPortletState.*"%>
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
<%@page import="smartpay.entity.enumerations.UserStatus"%>
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
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" />
<link href="<%=jqueryDataTableCssUrl%>" rel="stylesheet" type="text/css" />
<link href="<%=jqueryDataTableThemeCssUrl%>" rel="stylesheet" type="text/css" />

<%


UserManagementPortletState portletState = UserManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(UserManagementPortletState.class);

%>

<%
if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_ADMIN))
{
%>
<jsp:include page="/html/usermanagementportlet/tabs_nsa_admin.jsp" flush="" />
<%
}else if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER))
{
%>
<jsp:include page="/html/usermanagementportlet/tabs_nsa_user.jsp" flush="" />
<%
}else if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_END_USER))
{
%>
<jsp:include page="/html/usermanagementportlet/tabs_eu.jsp" flush="" />
<%
}else if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_ACCREDITOR_USER) || 
		portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER) ||
		portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_REGULATOR_USER))
{
%>
<jsp:include page="/html/usermanagementportlet/tabs_agency.jsp" flush="" />
<%
}
%>


<portlet:actionURL var="modifyPortalUser" name="processAction">
	<portlet:param name="action"
		value="<%=EUC_DESK_ACTIONS.LIST_PORTAL_USERS_ACTIONS.name()%>" />
</portlet:actionURL>

<div style="padding:10px" id="print-content">
<div class="panel panel-info">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>List of Portal Users</strong></div>
  	<div class="panel-body">
		<p>Click on a portal user to view the portal users' details. Click on an EDIT button to update a portal users details</p>
  	</div>
		<form  id="userListFormId" action="<%=modifyPortalUser%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
			
		<legend>Portal Users Listing</legend>
		  <%
		  if(portletState.getAllPortalUserListing()!=null && portletState.getAllPortalUserListing().size()>0)
		  {
			  %>
			  
			  <table width="100%" class="table table-hover" id="btable">
				<thead>
				  <th>Full Name</th>
				  <th>User Type</th>
				  <th>Email Address</th>
				  <th>Mobile Number</th>
				  <th>Agency</th>
				  <th>Status</th>
				  <th>Action</th>
				</thead>
				<%
			  for(Iterator<PortalUser> iter = portletState.getAllPortalUserListing().iterator(); iter.hasNext();)
			  {
				  
				  PortalUser pu = iter.next();
				  %>
				  <tr>
					<td><%=pu.getFirstName()==null ? "N/A" : pu.getFirstName() + " " + pu.getSurname()  %></td>
				  	<td><%=pu!=null ? pu.getRoleType().getName().getValue() : "" %></td>
					<td><%=pu.getEmailAddress()==null ? "N/A" : pu.getEmailAddress()%></td>
					<td><%=pu.getPhoneNumber()==null ? "N/A" : pu.getPhoneNumber()%></td>
					<td><%=pu.getAgency()!=null ? pu.getAgency().getAgencyName() : "N/A"%></td>
					<td><%=pu.getStatus().getValue()%></td>
					<td>
							<%
							if(!pu.getStatus().equals(UserStatus.USER_STATUS_DELETED))
							{
								log.info("This is for unapproved users");
								%>
								<div class="btn-group">
								  <button type="button" class="btn btn-success" onclick="javascript:handleButtonAction('view', '<%=pu.getId()%>')">View User Details</button>
								  <button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only">Toggle Dropdown</span>
								  </button>
								  <ul class="dropdown-menu" role="menu">
									<li><a href="javascript: handleButtonAction('view', '<%=pu.getId()%>')">View User Details</a></li>
									<%
									if(pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_ADMIN) || 
											pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_SYSTEM_ADMIN) || 
											pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER))
									{
									%>
										<li><a href="javascript: handleButtonAction('edituser', '<%=pu.getId()%>')">Edit User</a></li>
										<%
										if(pu.getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER))
										{
										%>
										<li><a href="javascript: handleButtonAction('modifyUserPermissions', '<%=pu.getId()%>')">Modify User Permissions</a></li>
										<%
										}
									}
										%>
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
		<input type="hidden" name="selectedPortalUser" id="selectedPortalUser" value="" />
		<input type="hidden" name="selectedPortalUserAction" id="selectedPortalUserAction" value="" />	
	</form>
</div>
</div>
<input type="button" onclick="printDiv('print-content')" value="Print Page"/>

<script type="text/javascript" charset="utf-8" src="<%=jqueryDataTableUrl%>"></script>

<script type="text/javascript">
$(document).ready(function() {
    $('#btable').dataTable();
} );


function handleButtonAction(action, usId){
	
	if(action=='delete')
	{
		if(confirm("Are you sure you want to delete this user? You will not be able to undo this action after deleting the user!"))
		{
			document.getElementById('selectedPortalUser').value = usId;
			document.getElementById('selectedPortalUserAction').value = action;
			document.getElementById('userListFormId').submit();
		}
	}else
	{
		document.getElementById('selectedPortalUser').value = usId;
		document.getElementById('selectedPortalUserAction').value = action;
		document.getElementById('userListFormId').submit();
	}
}
</script>

