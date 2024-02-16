<%@page import="com.ecims.portlet.usermanagement.UserManagementPortletUtil"%>
<%@page	import="com.ecims.portlet.usermanagement.UserManagementPortletState.*"%>
<%@page	import="com.ecims.portlet.usermanagement.UserManagementPortletState"%>
<%@page import="java.util.Collection"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@ page import="com.liferay.portal.kernel.util.Validator"%>
<%@ page import="javax.portlet.PortletPreferences"%>
<%@ page import="javax.portlet.PortletURL"%>
<%@ page import="javax.portlet.ActionRequest"%>
<%@ page import="javax.portlet.RenderResponse"%>
<%@ page import="com.liferay.portal.util.PortalUtil"%>
<%@ page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.Date"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@page import="org.apache.log4j.Logger"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />

<%

UserManagementPortletState portletState = UserManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(UserManagementPortletState.class);

%>
<portlet:actionURL var="createportaluser" name="processAction">
	<portlet:param name="action"
		value="<%=VIEW_TABS.CREATE_A_PORTAL_USERS.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="allportalrequests" name="processAction">
	<portlet:param name="action"
		value="<%=VIEW_TABS.VIEW_PORTAL_USER_LISTINGS.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="newportalusers" name="processAction">
	<portlet:param name="action"
		value="<%=VIEW_TABS.NEW_PORTAL_USER_REQUESTS.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="rejectedportalrequests" name="processAction">
	<portlet:param name="action"
		value="<%=VIEW_TABS.VIEW_PORTAL_USER_LISTINGS.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="rejectedportalrequests" name="processAction">
	<portlet:param name="action"
		value="<%=VIEW_TABS.NEW_PORTAL_USER_REQUESTS.name()%>" />
</portlet:actionURL>
<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<%
String createportaluserClass="label-default";
String allportalrequestsClass="label-default";
String newportalusersClass="label-default";
String approvedportalusersClass="label-default";
String rejectedportalrequestsClass="label-default";


if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.CREATE_A_PORTAL_USERS))
{
	createportaluserClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_PORTAL_USER_LISTINGS))
{
	allportalrequestsClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.NEW_PORTAL_USER_REQUESTS))
{
	newportalusersClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_PORTAL_USER_LISTINGS))
{
	approvedportalusersClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.NEW_PORTAL_USER_REQUESTS))
{
	rejectedportalrequestsClass="label-primary";
}else
{
	createportaluserClass="label-primary";
}

%>

<div style="padding-top: 20px">

	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=createportaluser%>">
		<div class="label <%=createportaluserClass%>" style="padding:10px;">
		Create A Portal User</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=allportalrequests%>">
		<div class="label <%=newportalusersClass%>" style="padding:10px; ">
		All Sign Up Requests</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=newportalusers%>">
		<div class="label <%=newportalusersClass%>" style="padding:10px;">
		View New Signup Requests</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=approvedportalusers%>">
		<div class="label <%=approvedportalusersClass%>" style="padding:10px;">
		Approved Sign Up Requests</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=rejectedportalrequests%>">new
		<div class="label <%=rejectedportalrequestsClass%>" style="padding:10px;">
		Rejected Sign Up Requests</div></a>
	</div>

</div>

<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>