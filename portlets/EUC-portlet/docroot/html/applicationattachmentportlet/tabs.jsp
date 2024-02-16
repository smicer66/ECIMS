<%@page import="com.ecims.portlet.admin.applicationattachment.ApplicationAttachmentPortletState"%>
<%@page	import="com.ecims.portlet.admin.applicationattachment.ApplicationAttachmentPortletState.*"%>
<%@page	import="com.ecims.portlet.admin.applicationattachment.ApplicationAttachmentPortletUtil"%>
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
<%@page import="smartpay.entity.enumerations.IdentificationType"%>
<%@page import="smartpay.entity.Country"%>
<%@page import="smartpay.entity.State"%>
<%@page import="smartpay.entity.enumerations.AgencyType"%>
<%@page import="smartpay.entity.enumerations.KinRelationshipType"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.RoleType"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Arrays" %>
<%@page import="java.util.List" %>
<%@page import="com.ecims.commins.Util"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="net.tanesha.recaptcha.ReCaptcha" %>
<%@ page import="net.tanesha.recaptcha.ReCaptchaFactory" %>




<portlet:defineObjects />

<%

ApplicationAttachmentPortletState portletState = ApplicationAttachmentPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ApplicationAttachmentPortletState.class);

%>
<portlet:actionURL var="newagency" name="processAction">
	<portlet:param name="action"
		value="<%=VIEW_TABS.CREATE_NEW_ATTACHMENT.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="newdesk" name="processAction">
	<portlet:param name="action"
		value="<%=VIEW_TABS.VIEW_ATTACHMENT_TYPES.name()%>" />
		</portlet:actionURL>



<%
String newattachclass="label-default";
String allattachclass="label-default";

if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.CREATE_NEW_ATTACHMENT))
{
	newattachclass="label-primary";
}if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ATTACHMENT_TYPES))
{
	allattachclass="label-primary";
}else
{
	newattachclass="label-primary";
}
%>

<div style="padding-top: 20px">


	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=newagency%>">
		<div class="label <%=newattachclass%>" style="padding:10px;">
		New Attachment Type</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=newdesk%>">
		<div class="label <%=allattachclass%>" style="padding:10px;">
		All Attachment Types</div></a>
	</div>
	
</div>

<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>