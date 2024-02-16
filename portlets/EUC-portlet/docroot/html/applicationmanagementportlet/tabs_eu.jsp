<%@page import="com.ecims.portlet.applicationmanagement.ApplicationManagementPortletState"%>
<%@page import="com.ecims.portlet.applicationmanagement.ApplicationManagementPortletState.*"%>
<%@page	import="com.ecims.portlet.applicationmanagement.ApplicationManagementPortletUtil"%>
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
<%@page import="smartpay.entity.enumerations.ApplicantStatus"%>

<%@page import="smartpay.entity.Applicant"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@page import="org.apache.log4j.Logger"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />

<%

ApplicationManagementPortletState portletState = ApplicationManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ApplicationManagementPortletState.class);
Applicant applicant = portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser());
%>
<portlet:actionURL var="createapplication" name="processAction">
	<portlet:param name="action"
		value="<%=VIEW_TABS.CREATE_AN_APPLICATION_EU.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="allapplications" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.LIST_ALL_APPLICATIONS_EU.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="pendingapplications" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.LIST_PENDING_APPLICATIONS_EU.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="approvedapplications" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.LIST_APPROVED_APPLICATIONS_EU.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="rejectedapplications" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.LIST_REJECTED_APPLICATIONS_EU.name()%>" />
</portlet:actionURL>

<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<%
String createportaluserClass="label-default";
String allEucClass="label-default";
String pendingEucClass="label-default";
String approvedEucClass="label-default";
String rejectedEucClass="label-default";


if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.CREATE_AN_APPLICATION_EU))
{
	createportaluserClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_ALL_APPLICATIONS_EU))
{
	allEucClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_PENDING_APPLICATIONS_EU))
{
	pendingEucClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_APPROVED_APPLICATIONS_EU))
{
	approvedEucClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_REJECTED_APPLICATIONS_EU))
{
	rejectedEucClass="label-primary";
}else
{
	createportaluserClass="label-primary";
}

%>

<div style="padding-top: 20px">
<%
if(applicant!=null && applicant.getStatus().equals(ApplicantStatus.APPLICANT_STATUS_APPROVED))
{
	if(applicant.getBlackList()!=null && applicant.getBlackList().equals(Boolean.TRUE))
	{
%>

<%		
	}else
	{
%>
<div style="padding:2px; float:left; font-weight:bold">
	<a href="<%=createapplication%>">
	<div class="label <%=createportaluserClass%>" style="padding:10px;">
	New EUC Application</div></a>
</div>
<%		
	}

}
%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=allapplications%>">
		<div class="label <%=allEucClass%>" style="padding:10px;">
		All EUC Applications</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=pendingapplications%>">
		<div class="label <%=pendingEucClass%>" style="padding:10px; ">
		Pending EUC Applications</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=rejectedapplications%>">
		<div class="label <%=rejectedEucClass%>" style="padding:10px;">
		Rejected EUC Applications</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=approvedapplications%>">
		<div class="label <%=approvedEucClass%>" style="padding:10px;">
		Approved EUC Applications</div></a>
	</div>

</div>

<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>