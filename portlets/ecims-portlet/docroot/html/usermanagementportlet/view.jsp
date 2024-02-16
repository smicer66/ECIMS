<%@page import="com.ecims.portlet.usermanagement.UserManagementPortletState"%>
<%@page import="com.ecims.portlet.usermanagement.UserManagementPortletState.*"%>
<%@page import="com.ecims.commins.Util"%>
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
<%@page import="smartpay.entity.Company"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.entity.Settings"%>

<%@page import="java.text.DateFormat"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />
<%

UserManagementPortletState portletState = UserManagementPortletState.getInstance(renderRequest, renderResponse);

%>

<%
	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_ADMIN))
	{
	%>
	<jsp:include page="/html/usermanagementportlet/register_individual/stepone.jsp" flush="" />
	<%
	}else if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_SYSTEM_ADMIN))
	{
	%>
	<jsp:include page="/html/usermanagementportlet/register_individual/stepone.jsp" flush="" />
	<%
	}else
	{
	%>
	<div class="panel panel-danger">You do not have access to carry out any actions as you do not have valid access. Contact Appropriate Administrators for rights</div>
	<%
	}
%>