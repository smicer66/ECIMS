<%@page import="com.ecims.portlet.applicant.ApplicantPortletState"%>
<%@page	import="com.ecims.portlet.applicant.ApplicantPortletState.*"%>
<%@page	import="com.ecims.portlet.applicant.ApplicantPortletState"%>
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

ApplicantPortletState portletState = ApplicantPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ApplicantPortletState.class);

%>
<portlet:actionURL var="allportalrequests" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.VIEW_ALL_APPLICANT.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="newportalusers" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.VIEW_NEW_APPLICANT_REQUESTS.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="approvedportalusers" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.VIEW_APPROVED_APPLICANT_LISTINGS.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="rejectedportalrequests" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.VIEW_REJECTED_PORTAL_USER_REQUESTS.name()%>" />
</portlet:actionURL>

<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<%
String allportalrequestsClass="label-default";
String newportalusersClass="label-default";
String approvedportalusersClass="label-default";
String rejectedportalrequestsClass="label-default";

if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ALL_APPLICANT))
{
	allportalrequestsClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_NEW_APPLICANT_REQUESTS))
{
	newportalusersClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_APPROVED_APPLICANT_LISTINGS))
{
	approvedportalusersClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_REJECTED_PORTAL_USER_REQUESTS))
{
	rejectedportalrequestsClass="label-primary";
}else
{
	allportalrequestsClass="label-primary";
}

%>

<div style="padding-top: 20px">
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=allportalrequests%>">
		<div class="label <%=allportalrequestsClass%>" style="padding:10px; ">
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
		<a href="<%=rejectedportalrequests%>">
		<div class="label <%=rejectedportalrequestsClass%>" style="padding:10px;">
		Rejected Sign Up Requests</div></a>
	</div>

</div>

<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>