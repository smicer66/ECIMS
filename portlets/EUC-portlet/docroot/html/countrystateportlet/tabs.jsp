<%@page import="com.ecims.portlet.admin.countrystate.CountryStatePortletState"%>
<%@page	import="com.ecims.portlet.admin.countrystate.CountryStatePortletState.*"%>
<%@page	import="com.ecims.portlet.admin.countrystate.CountryStatePortletUtil"%>
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

CountryStatePortletState portletState = CountryStatePortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(CountryStatePortletState.class);

%>
<portlet:actionURL var="newcountry" name="processAction">
	<portlet:param name="action"
		value="<%=VIEW_TABS.VIEW_NEW_COUNTRY.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="newstate" name="processAction">
	<portlet:param name="action"
		value="<%=VIEW_TABS.VIEW_NEW_STATE.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="newportcode" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.VIEW_NEW_PORT_CODE.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="countrylist" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.VIEW_COUNTRY_LIST.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="statelist" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.VIEW_STATE_LIST.name()%>" />
		</portlet:actionURL>
		<portlet:actionURL var="portcodelist" name="processAction">
			<portlet:param name="action"
				value="<%=VIEW_TABS.VIEW_PORT_CODE_LIST.name()%>" />
		</portlet:actionURL>

<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>



<%
String newcountryclass="label-default";
String newstateclass="label-default";
String newportcodeclass="label-default";
String countrylistclass="label-default";
String statelistclass="label-default";
String portcodelistclass="label-default";


if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_NEW_COUNTRY))
{
	newcountryclass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_NEW_STATE))
{
	newstateclass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_NEW_PORT_CODE))
{
	newportcodeclass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_COUNTRY_LIST))
{
	countrylistclass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_STATE_LIST))
{
	statelistclass="label-primary";
}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_PORT_CODE_LIST))
{
	portcodelistclass="label-primary";
}else
{
	newcountryclass="label-primary";
}

%>

<div style="padding-top: 20px">


	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=newcountry%>">
		<div class="label <%=newcountryclass%>" style="padding:10px;">
		New Country</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=newstate%>">
		<div class="label <%=newstateclass%>" style="padding:10px;">
		New State</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=newportcode%>">
		<div class="label <%=newportcodeclass%>" style="padding:10px;">
		New Port Code</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=countrylist%>">
		<div class="label <%=countrylistclass%>" style="padding:10px; ">
		List of Countries</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=statelist%>">
		<div class="label <%=statelistclass%>" style="padding:10px; ">
		List of States</div></a>
	</div>
	<div style="padding:2px; float:left; font-weight:bold">
		<a href="<%=portcodelist%>">
		<div class="label <%=portcodelistclass%>" style="padding:10px; ">
		List of Port Codes</div></a>
	</div>
	
</div>

<div style="clear:both; font-size:20px; height:20px">&nbsp;</div>