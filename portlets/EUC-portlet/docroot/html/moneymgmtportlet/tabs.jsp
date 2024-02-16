<%@page import="com.ecims.portlet.moneymgmt.MoneyMgmtPortletState"%>
<%@page	import="com.ecims.portlet.moneymgmt.MoneyMgmtPortletState.*"%>
<%@page	import="com.ecims.portlet.moneymgmt.MoneyMgmtPortletUtil"%>
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

MoneyMgmtPortletState portletState = MoneyMgmtPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(MoneyMgmtPortletState.class);

%>
<portlet:actionURL var="newagency" name="processAction">
	<portlet:param name="action"
		value="<%=VIEW_TABS.VIEW_CREATE_NEW_CURRENCY.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="newdesk" name="processAction">
	<portlet:param name="action"
		value="<%=VIEW_TABS.VIEW_ALL_CURRENCY.name()%>" />
		</portlet:actionURL>
		


<%
String newagencyclass="label-default";
String newdeskclass="label-default";
String allagencyclass="label-default";
String alldeskclass="label-default";


if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_CREATE_NEW_CURRENCY))
{
	newagencyclass="label-primary";
}if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ALL_CURRENCY))
{
	newdeskclass="label-primary";
}else
{
	newagencyclass="label-primary";
}

%>

<div style="padding-top: 20px">


	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=newagency%>">
		<div class="label <%=newagencyclass%>" style="padding:10px;">
		Create A Currency</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=newdesk%>">
		<div class="label <%=newdeskclass%>" style="padding:10px;">
		Manage Currencies</div></a>
	</div>
	
</div>

<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>