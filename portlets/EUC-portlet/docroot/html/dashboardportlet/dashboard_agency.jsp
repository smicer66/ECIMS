<%@page import="com.ecims.portlet.dashboard.DashboardPortletState"%>
<%@page import="com.ecims.portlet.dashboard.DashboardPortletState.*"%>
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
<%@page import="smartpay.entity.Agency"%>
<%@page import="smartpay.entity.enumerations.KinRelationshipType"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.RoleType"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Arrays" %>
<%@page import="java.util.List" %>
<%@page import="com.ecims.commins.Util"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />
<%
	String resourceBaseURL = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ "/resources";
	String faceboxCssUrl = resourceBaseURL + "/css/facebox.css";
	String pagingUrl = resourceBaseURL + "/css/paging.css";


		
	String jqueryUICssUrl = resourceBaseURL + "/css/jquery-ui.min.css";
	
	String jqueryJsUrl = resourceBaseURL + "/js/jquery-1.10.2.min.js";
	String jqueryUIJsUrl = resourceBaseURL + "/js/jquery-ui.min.js";
%>
<script type="text/javascript"
	src="<%=(resourceBaseURL + "/js/jquery.validate.js")%>"></script>
<script type="text/javascript"
	src="<%=(resourceBaseURL + "/js/facebox.js")%>"></script>
<script type="text/javascript"
	src="<%=(resourceBaseURL + "/js/paging.js")%>"></script>
<link rel="stylesheet" href='<%=faceboxCssUrl%>' type="text/css" />
<link rel="stylesheet" href='<%=pagingUrl%>' type="text/css" />
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" />

<meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width,initial-scale=1">

    <link href="//www.google-analytics.com" rel="dns-prefetch">
    <link href="//ajax.googleapis.com" rel="dns-prefetch">
    <link href="<%=(resourceBaseURL + "/css/kelvin/framework.css")%>" rel="stylesheet">
    <link href="<%=(resourceBaseURL + "/css/kelvin/default.css")%>" rel="stylesheet">
    <link href="<%=(resourceBaseURL + "/css/kelvin/style.css")%>" rel="stylesheet">
    <link href="<%=(resourceBaseURL + "/css/kelvin/fonts.css")%>" rel="stylesheet">
    
    <!-- Fotorama -->
    <link href="<%=(resourceBaseURL + "/css/kelvin/fotorama.css")%>" rel="stylesheet"> 

  <script src="<%=(resourceBaseURL + "/js/modernizr/modernizr.js")%>"></script>
  
  <script src="<%=(resourceBaseURL + "/js/fusioncharts/js/fusioncharts.js")%>"></script>
  <script src="<%=(resourceBaseURL + "/js/fusioncharts/js/fusioncharts.charts.js")%>"></script>
  <script src="<%=(resourceBaseURL + "/js/fusioncharts/js/themes/fusioncharts.theme.fint.js")%>"></script>

<%

DashboardPortletState portletState = DashboardPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(DashboardPortletState.class);
String user="home";
if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_ACCREDITOR_USER))
{
	user = "accreditor";
}else if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))
{
	user = "information-user";
}else if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_REGULATOR_USER))
{
	user = "regulator-user";
}else if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER))
{
	user = "exclusive-users";
}
portletState.setCurrentTab(null);
%>

<portlet:actionURL var="pendingapplication" name="processAction">
	<portlet:param name="action"
		value="<%=VIEW_TABS.PENDING_APPLICATION_AGENCY.name()%>" />
	<portlet:param name="usrty"
		value="<%=user%>" />
</portlet:actionURL>








<section class="container" style="margin-top: 10px;">
<h4>Welcome, 
<%
if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER))
{
%>
<%=portletState.getPortalUser().getFirstName() %> <%=portletState.getPortalUser().getSurname() %> (<%=portletState.getPortalUser().getCompany().getName() %>)!</h4>
<%
}else
{
%>
<%=portletState.getPortalUser().getFirstName() %> <%=portletState.getPortalUser().getSurname() %> (<%=portletState.getPortalUser().getAgency().getAgencyName() %>)!</h4>
<%
}
%>


<%
if(!portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER))
{
%>
<div style="clear:both; float:left; padding:5px;">
<a href="<%=pendingapplication%>">Pending applications(<%=portletState.getNotWorkOnApplication()%>)</a>
</div>
<div style="clear:both">&nbsp;</div>
<%
}
%>


<div style="border-top:solid 1px #cccccc;">
	
<%
if(portletState.getNotWorkOnApplication()>0 || portletState.getWorkOnApplication()>0)
{
%>
	<div id="application-requests" style="width:300px; float:left; padding:10px;">
		&nsp;
	</div>
<%
}
if(!portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_REGULATOR_USER))
{
	if(portletState.getIssuedCertificateCount()>0 || portletState.getRecalledCertificateCount()>0)
	{
%>
	<div id="certificate-data" style="width:300px; float:left; padding:10px;">
		&nbsp;
	</div>
<%
	}
}
%>

</div>


	



<div style="clear:both;  border-top: 1px #cccccc; padding-top:10px;">
<h4>My Tools</h4>

	<%
	if(!portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))
	{
	%>
	<div class="col-xs-6 col-md-3  col-sm-4 text-center icons">
	  <a href="/web/<%=user %>/application-management" class="img-thumbnail">
	    <img src="<%=resourceBaseURL %>/images/applications.png" style="height: 50px;margin-top:50px;">
	    <p class="title">EUC Applications</p>
	  </a>
	</div>
	<%
	}
	%>
	<%
	//if(!portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_REGULATOR_USER) && 
		//	!portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_ACCREDITOR_USER))
	//{
	%>
	
<%
if(!portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER))
{
%>
	<div class="col-xs-6 col-md-3  col-sm-4 text-center icons">
	  <a href="/web/<%=user %>/certificate-management" class="img-thumbnail">
	    <img src="<%=resourceBaseURL %>/images/certificate.png" style="height: 50px;margin-top:50px;">
	    <p class="title">End-User Certificates</p>
	  </a>
	</div>
<%
}
%>
	<%
	//}
	if(!portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER))
	{
	%>
	
	<div class="col-xs-6 col-md-3  col-sm-4 text-center icons">
	  <a href="/web/<%=user %>/manage-my-account" class="img-thumbnail">
	    <img src="<%=resourceBaseURL %>/images/usermanage.png" style="height: 50px;margin-top:50px;">
	    <p class="title">Manage My Account</p>
	  </a>
	</div>
	<%
	}
	%>
	
</div>


</section>

<script type="text/javascript">


FusionCharts.ready(function () {
    var demographicsChart = new FusionCharts({
        type: 'pie3d',
        renderAt: 'application-requests',
        width: '300',
        height: '350',
        dataFormat: 'json',
        dataSource: {
            "chart": {
                "caption": "EUC Application Requests",
                "subCaption": "On The System",
                "startingAngle": "120",
                "showLabels": "0",
                "showLegend": "1",
                "enableMultiSlicing": "0",
                "slicingDistance": "15",
                //To show the values in percentage
                "showPercentValues": "0",
                "showPercentInTooltip": "0",
                "plotTooltext": "EUC Application Request Category : $label<br>Total Count : $datavalue",
                "theme": "fint"
            },
            "data": [{
                "label": "Application Requests Not Worked On",
                "value": "<%=portletState.getNotWorkOnApplication()%>"
            }, {
                "label": "Application Requests Worked On",
                "value": "<%=portletState.getWorkOnApplication()%>"
            }]
        }
    });
    demographicsChart.render();
});


<%
if(!portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_REGULATOR_USER))
{
%>
	FusionCharts.ready(function () {
	    var demographicsChart = new FusionCharts({
	        type: 'pie3d',
	        renderAt: 'certificate-data',
	        width: '300',
	        height: '350',
	        dataFormat: 'json',
	        dataSource: {
	            "chart": {
	                "caption": "Certificates",
	                "subCaption": "On The System",
	                "startingAngle": "120",
	                "showLabels": "0",
	                "showLegend": "1",
	                "enableMultiSlicing": "0",
	                "slicingDistance": "15",
	                //To show the values in percentage
	                "showPercentValues": "0",
	                "showPercentInTooltip": "0",
	                "plotTooltext": "Certificate Category : $label<br>Total Count : $datavalue",
	                "theme": "fint"
	            },
	            "data": [{
	                "label": "Issued Certificates",
	                "value": "<%=portletState.getIssuedCertificateCount()%>"
	            }, {
	                "label": "Recalled Certificates",
	                "value": "<%=portletState.getRecalledCertificateCount()%>"
	            }]
	        }
	    });
	    demographicsChart.render();
	});
<%
}
%>
</script>