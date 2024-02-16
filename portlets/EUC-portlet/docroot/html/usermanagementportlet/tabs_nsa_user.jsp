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

		<portlet:actionURL var="changepassword" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.CHANGE_PASSWORD.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="myprofile" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.VIEW_MY_PROFILE.name()%>" />
		</portlet:actionURL>

<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<%
String changePasswordClass="label-default";
String myprofileClass="label-default";


if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.CHANGE_PASSWORD))
{
	changePasswordClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_MY_PROFILE))
{
	myprofileClass="label-primary";
}else
{
	myprofileClass="label-primary";
}

%>

<div style="padding-top: 20px">

	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=myprofile%>">
		<div class="label <%=myprofileClass%>" style="padding:10px; ">
		My Profile</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=changepassword%>">
		<div class="label <%=changePasswordClass%>" style="padding:10px; ">
		Change My Password</div></a>
	</div>
	
</div>

<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>