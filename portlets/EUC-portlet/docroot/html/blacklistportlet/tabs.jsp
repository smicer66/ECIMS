<%@page import="smartpay.entity.enumerations.PermissionType"%>
<%@page import="com.ecims.portlet.blacklist.BlacklistPortletState"%>
<%@page import="com.ecims.portlet.blacklist.BlacklistPortletState.*"%>
<%@page	import="com.ecims.portlet.blacklist.BlacklistPortletUtil"%>
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

BlacklistPortletState portletState = BlacklistPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(BlacklistPortletState.class);

%>

		<portlet:actionURL var="blacklistedApplicants" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.LIST_BLACKLISTED_APPLICANTS.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="blacklistAnApplicant" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.BLACKLIST_AN_APPLICANT.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="blacklistHistory" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.LIST_BLACKLIST_HISTORY.name()%>" />
		</portlet:actionURL>

<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<%
String blacklistedApplicantsClass="label-default";
String blacklistAnApplicantClass="label-default";
String blacklistHistoryClass="label-default";

if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_BLACKLISTED_APPLICANTS))
{
	blacklistedApplicantsClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.BLACKLIST_AN_APPLICANT))
{
	blacklistAnApplicantClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.LIST_BLACKLIST_HISTORY))
{
	blacklistHistoryClass="label-primary";
}else
{
	blacklistedApplicantsClass="label-primary";
}

%>



<%

	%>
	<div style="padding-top: 20px">

		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=blacklistedApplicants%>">
			<div class="label <%=blacklistedApplicantsClass%>" style="padding:10px;">
			Blacklisted Applicants</div></a>
		</div>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=blacklistAnApplicant%>">
			<div class="label <%=blacklistAnApplicantClass%>" style="padding:10px;">
			Blacklist An Applicant</div></a>
		</div>
		<div style="padding:2px; float:left; font-weight:bold">
			<a href="<%=blacklistHistory%>">
			<div class="label <%=blacklistHistoryClass%>" style="padding:10px;">
			Blacklist History</div></a>
		</div>
	
	</div>


<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>