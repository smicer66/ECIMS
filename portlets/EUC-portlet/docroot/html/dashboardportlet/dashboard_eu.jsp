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
portletState.setCurrentTab(null);
%>

		
		
<section class="container" style="margin-top: 10px;">
<h4>Welcome, <%=portletState.getPortalUser().getFirstName() %> <%=portletState.getPortalUser().getSurname() %>!</h4>
<!-- <div style="border-top:solid 1px #cccccc;">
	<div id="signup-requests" style="width:300px; float:left; padding:10px;">
		&nbsp;
	</div>
	
	
	<div id="application-requests" style="width:300px; float:left; padding:10px;">
		&nbsp;
	</div>
	
	<div id="certificate-data" style="width:300px; float:left; padding:10px;">
		&nbsp;
	</div>


</div>-->

<div style="clear:both;  border-top: 1px #cccccc; padding-top:10px;">
<h4>My Tools</h4>
	<div class="col-xs-6 col-md-3  col-sm-4 text-center icons">
	  <a href="/web/end-user/euc-application-management" class="img-thumbnail">
	    <img src="<%=resourceBaseURL %>/images/applications.png" style="height: 50px;margin-top:50px;">
	    <p class="title">My EUC Applications</p>
	  </a>
	</div>
	
	<div class="col-xs-6 col-md-3  col-sm-4 text-center icons">
	  <a href="/web/end-user/certificate-management" class="img-thumbnail">
	    <img src="<%=resourceBaseURL %>/images/certificate.png" style="height: 50px;margin-top:50px;">
	    <p class="title">My End-User Certificates</p>
	  </a>
	</div>
	
	<!-- <div class="col-xs-6 col-md-3  col-sm-4 text-center icons">
	  <a href="/web/end-user/track-application" class="img-thumbnail">
	    <img src="<%=resourceBaseURL %>/images/tracker.png" style="height: 50px;margin-top:50px;">
	    <p class="title">Track Application</p>
	  </a>
	</div>-->
	
	<div class="col-xs-6 col-md-3  col-sm-4 text-center icons">
	  <a href="/web/end-user/manage-my-account" class="img-thumbnail">
	    <img src="<%=resourceBaseURL %>/images/usermanage.png" style="height: 50px;margin-top:50px;">
	    <p class="title">Manage My Account</p>
	  </a>
	</div>
</div>
</section>

<script type="text/javascript">
function onlyDoubleKey(e, elementId)
{
	var src = (e.srcElement || e.target);
	var unicode=e.charCode? e.charCode : e.keyCode;
	
	var check = false;
	var lenAfter = document.getElementById(elementId).value.length - document.getElementById(elementId).value.indexOf(".");
	if(document.getElementById(elementId).value.length>0 && 
			document.getElementById(elementId).value.indexOf(".")==-1)
	{
		check =true;
	}
	
	if(((unicode>47) && (unicode<58)) || (check==true && unicode==46))
		{}
	else
		{return false}
	 
	
}


function onlyNumKey(e)
{
	var src = (e.srcElement || e.target);
	var unicode=e.charCode? e.charCode : e.keyCode
	
	
	
	if ((unicode>47) && (unicode<58)) 
		{}
	else
		{return false}
	 
	
}

function submitForm(clicked)
{
	document.getElementById('act').value=clicked;
	document.getElementById('startRegFormId').submit;
	
}






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
                "showPercentValues": "1",
                "showPercentInTooltip": "0",
                "plotTooltext": "EUC Application Request Category : $label<br>Total Count : $datavalue",
                "theme": "fint"
            },
            "data": [{
                "label": "Pending Application Requests",
                "value": "<%=portletState.getPendingApplicationRequestCount()%>"
            }, {
                "label": "Approved Applications",
                "value": "<%=portletState.getDisendorsedApplicationCount()%>"
            }, {
                "label": "Rejected Applications",
                "value": "<%=portletState.getRejectedApplicationCount()%>"
            }]
        }
    });
    demographicsChart.render();
});




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
                "showPercentValues": "1",
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
</script>