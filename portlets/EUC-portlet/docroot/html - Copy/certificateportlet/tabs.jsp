<%@page import="com.ecims.portlet.certificatemanagement.CertificatePortletState.*"%>
<%@page	import="com.ecims.portlet.certificatemanagement.CertificatePortletState"%>
<%@page	import="com.ecims.portlet.certificatemanagement.CertificatePortletUtil"%>
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
<%@page import="smartpay.entity.enumerations.RoleConstants"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@page import="org.apache.log4j.Logger"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />

<%

CertificatePortletState portletState = CertificatePortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(CertificatePortletState.class);

%>
<portlet:actionURL var="allcertificates" name="processAction">
	<portlet:param name="action"
		value="<%=VIEW_TABS.VIEW_ALL_CERTIFICATES_EU.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="allcertificatesnsa" name="processAction">
	<portlet:param name="action"
		value="<%=VIEW_TABS.VIEW_ALL_CERTIFICATES_NSA.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="disputedcertificates" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.VIEW_DISPUTED_CERTIFICATES.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="utilizedcertificates" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.VIEW_UTILIZED_CERTIFICATES.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="expiredcertificates" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.VIEW_EXPIRED_CERTIFICATES.name()%>" />
		</portlet:actionURL>

<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<%
String allcertClass="label-default";
String disputedcertClass="label-default";
String utilizedcertClass="label-default";
String expiredcertClass="label-default";


if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ALL_CERTIFICATES_EU))
{
	allcertClass="label-primary";
}if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ALL_CERTIFICATES_NSA))
{
	allcertClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_DISPUTED_CERTIFICATES))
{
	disputedcertClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_UTILIZED_CERTIFICATES))
{
	utilizedcertClass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_EXPIRED_CERTIFICATES))
{
	expiredcertClass="label-primary";
}else
{
	allcertClass="label-primary";
}

%>

<div style="padding-top: 20px">
<%
if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
{
%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=allcertificatesnsa%>">
		<div class="label <%=allcertClass%>" style="padding:10px;">
		All Certificates</div></a>
	</div>
<%
}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
{
%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=allcertificates%>">
		<div class="label <%=allcertClass%>" style="padding:10px;">
		All Certificates</div></a>
	</div>
<%
}
%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=allcertificates%>">
		<div class="label <%=disputedcertClass%>" style="padding:10px;">
		Disputed Certificates</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=utilizedcertificates%>">
		<div class="label <%=utilizedcertClass%>" style="padding:10px;">
		Utilized Certificates</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=expiredcertificates%>">
		<div class="label <%=expiredcertClass%>" style="padding:10px; ">
		Expired Certificates</div></a>
	</div>
	
</div>

<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>