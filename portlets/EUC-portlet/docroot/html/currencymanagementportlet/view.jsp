<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %> 
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="javax.portlet.ResourceURL" %>
<%@ page import="javax.portlet.WindowState" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.util.TextFormatter" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>

<portlet:defineObjects />
<%
// Tab value to preselect on page load
// Default parameter name used by the taglib is "tabs1"
String currentTab = ParamUtil.getString(request, "tabs1", "Currency Management");
String currentTabPage = currentTab.replaceAll("\\s","");
%>
 
<liferay-portlet:renderURL var="changeTabURL" />

<liferay-ui:tabs
    names="Currency Management,Agency Management,Country Management,State Management,Item Category Management,Item SubCategory Management,QuantityUnit Management,Workflow Management"
    url="<%= changeTabURL  %>"
/>

<liferay-util:include
    page='<%= "/html/currencymanagementportlet/tabs/" + TextFormatter.format(currentTabPage, TextFormatter.N) + ".jsp" %>'
    servletContext="<%= application %>"
/>

