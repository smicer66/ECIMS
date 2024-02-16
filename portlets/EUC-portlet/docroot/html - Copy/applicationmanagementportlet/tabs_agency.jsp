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
<%@ page import="java.text.SimpleDateFormat"%>
<%@page import="org.apache.log4j.Logger"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />

<%

ApplicationManagementPortletState portletState = ApplicationManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ApplicationManagementPortletState.class);

%>

		<portlet:actionURL var="pendingapplications" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.LIST_PENDING_APPLICATIONS_AG.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="endorsedapplications" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.LIST_ENDORSED_APPLICATIONS_AG.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="disendorsedapplications" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_AG.name()%>" />
</portlet:actionURL>

		<portlet:actionURL var="devalidatedapplications" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.LIST_DEVALIDATED_APPLICATIONS_AG.name()%>" />
		</portlet:actionURL>

		<portlet:actionURL var="disputedapplications" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.LIST_DISPUTED_APPLICATIONS_AG.name()%>" />
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
String devalidClass="label-default";
String disputedNsaClass="label-default";

if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_PENDING_APPLICATIONS_AG))
{
	allNsaClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_AG))
{
	pendingNsaClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_AG))
{
	approvedNsaClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_DEVALIDATED_APPLICATIONS_AG))
{
	devalidClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_DISPUTED_APPLICATIONS_AG))
{
	disputedNsaClass="label-primary";
}else
{
	allNsaClass="label-primary";
}

%>

<div style="padding-top: 20px">

	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=pendingapplications%>">
		<div class="label <%=allNsaClass%>" style="padding:10px;">
		Pending Applications</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=endorsedapplications%>">
		<div class="label <%=pendingNsaClass%>" style="padding:10px; ">
		Endorsed Applications</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=disendorsedapplications%>">
		<div class="label <%=approvedNsaClass%>" style="padding:10px;">
		Disendorsed Applications</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=devalidatedapplications%>">
		<div class="label <%=rejectedNsaClass%>" style="padding:10px;">
		Devalidated Applications</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=disputedapplications%>">
		<div class="label <%=disputedNsaClass%>" style="padding:10px;">
		Disputed Applications</div></a>
	</div>

</div>

<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>