<%@page import="com.ecims.portlet.dashboard.DashboardPortletState"%>
<%@page import="com.ecims.portlet.dashboard.DashboardPortletState.*"%>
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
<%@page import="smartpay.entity.enumerations.RoleConstants"%>
<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.entity.Settings"%>

<%@page import="java.text.DateFormat"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />
 <%

DashboardPortletState portletState = DashboardPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(DashboardPortletState.class);






	if(portletState!=null && portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
	{
	%>
	<jsp:include page="/html/dashboardportlet/dashboard_eu.jsp" flush="" />
	<%
	}else if(portletState!=null && portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
	{
	%>
	<jsp:include page="/html/dashboardportlet/dashboard_nsa.jsp" flush="" />
	<%
	}else if(portletState!=null && portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
	{
	%>
	<jsp:include page="/html/dashboardportlet/dashboard_agency.jsp" flush="" />
	<%
	}else if(portletState!=null && portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_ADMIN_GROUP))
	{
	%>
	<jsp:include page="/html/dashboardportlet/dashboard_sys.jsp" flush="" />
	<%
	}else
	{
	%>
	<div class="panel panel-danger">You do not have access to carry out any actions as you do not have valid access. Contact Appropriate Administrators for rights</div>
	<%
	}
%>