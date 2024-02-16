<%@page import="smartpay.entity.enumerations.PermissionType"%>
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
<%@ page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.Date"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@page import="org.apache.log4j.Logger"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />

<%

ApplicationManagementPortletState portletState = ApplicationManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ApplicationManagementPortletState.class);

%>

		<portlet:actionURL var="forwardedapplications" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.LIST_FORWARDED_APPLICATIONS_NSA.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="pendingapplications" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="approvedapplications" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.LIST_APPROVED_APPLICATIONS_NSA.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="rejectedapplications" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.LIST_REJECTED_APPLICATIONS_NSA.name()%>" />
</portlet:actionURL>
		<portlet:actionURL var="endorsedapplications" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.LIST_ENDORSED_APPLICATIONS_NSA.name()%>" />
</portlet:actionURL>
		<portlet:actionURL var="disendorsedapplications" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_NSA.name()%>" />
</portlet:actionURL>

<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<%
String allNsaClass="label-default";
String pendingNsaClass="label-default";
String approvedNsaClass="label-default";
String rejectedNsaClass="label-default";
String endorsedNsaClass="label-default";
String disendorsedNsaClass="label-default";

if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA))
{
	pendingNsaClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_APPROVED_APPLICATIONS_NSA))
{
	approvedNsaClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_REJECTED_APPLICATIONS_NSA))
{
	rejectedNsaClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_FORWARDED_APPLICATIONS_NSA))
{
	allNsaClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_NSA))
{
	endorsedNsaClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_NSA))
{
	disendorsedNsaClass="label-primary";
}else
{
	pendingNsaClass="label-primary";
}

%>



<%
ArrayList<PermissionType> pt = new ArrayList<PermissionType>();
pt.add(PermissionType.PERMISSION_REJECT_APPLICATION);
pt.add(PermissionType.PERMISSION_APPROVE_APPLICATION);
if(portletState.getPermissionList()!=null && portletState.getPermissionList().size()==2 && 
	portletState.getPermissionList().containsAll(pt))
{
	%>
	<div style="padding-top: 20px">

		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=pendingapplications%>">
			<div class="label <%=pendingNsaClass%>" style="padding:10px; ">
			Applications Pending Approval</div></a>
		</div>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=approvedapplications%>">
			<div class="label <%=approvedNsaClass%>" style="padding:10px;">
			Approved EUC Applications</div></a>
		</div>
	
	</div>
	<%
}else
{
	%>
	<div style="padding-top: 20px">

		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=pendingapplications%>">
			<div class="label <%=pendingNsaClass%>" style="padding:10px; ">
			Pending EUC Applications</div></a>
		</div>
		<% %>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=forwardedapplications%>">
			<div class="label <%=allNsaClass%>" style="padding:10px;">
			Forwarded EUC Applications</div></a>
		</div>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=endorsedapplications%>">
			<div class="label <%=endorsedNsaClass%>" style="padding:10px;">
			Endorsed EUC Applications</div></a>
		</div>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=disendorsedapplications%>">
			<div class="label <%=disendorsedNsaClass%>" style="padding:10px;">
			Disendorsed EUC Applications</div></a>
		</div>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=rejectedapplications%>">
			<div class="label <%=rejectedNsaClass%>" style="padding:10px;">
			Rejected EUC Applications</div></a>
		</div>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=approvedapplications%>">
			<div class="label <%=approvedNsaClass%>" style="padding:10px;">
			Approved EUC Applications</div></a>
		</div>
	
	</div>
	<%
}
%>


<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>