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

<portlet:actionURL var="myagencycertificates" name="processAction">
	<portlet:param name="action" value="<%=VIEW_TABS.VIEW_MY_CERTIFICATES_AGENCY.name()%>" />
</portlet:actionURL>
<portlet:actionURL var="allcertificatesagency" name="processAction">
	<portlet:param name="action" value="<%=VIEW_TABS.VIEW_ALL_CERTIFICATES_AGENCY.name()%>" />
</portlet:actionURL>

<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<%
String allcertClass="label-default";
String mycertClass="label-default";


if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ALL_CERTIFICATES_AGENCY))
{
	allcertClass="label-primary";
}if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_MY_CERTIFICATES_AGENCY))
{
	mycertClass="label-primary";
}else
{
	if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))
	{
		allcertClass="label-primary";
	}
	else if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_ACCREDITOR_USER))
	{
		mycertClass="label-primary";
	}else if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_REGULATOR_USER))
	{
		mycertClass="label-primary";
	}
}

%>

<div style="padding-top: 20px">
<%
if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))
{
%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=allcertificatesagency%>">
		<div class="label <%=allcertClass%>" style="padding:10px;">
		All Certificates</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=myagencycertificates%>">
		<div class="label <%=mycertClass%>" style="padding:10px;">
		My Agency Certificates</div></a>
	</div>
<%
}else if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_ACCREDITOR_USER))
{
%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=myagencycertificates%>">
		<div class="label <%=mycertClass%>" style="padding:10px;">
		My Agency Certificates</div></a>
	</div>
<%
}else if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_REGULATOR_USER))
{
%>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=myagencycertificates%>">
		<div class="label <%=mycertClass%>" style="padding:10px;">
		My Agency Certificates</div></a>
	</div>
<%
}
%>
	
</div>

<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>