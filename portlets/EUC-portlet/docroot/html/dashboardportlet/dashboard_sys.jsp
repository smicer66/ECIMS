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
 <%

DashboardPortletState portletState = DashboardPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(DashboardPortletState.class);
portletState.setCurrentTab(null);

%>
		
<section class="container" style="margin-top: 60px;">
<h4>Dashboard</h4>

<div class="col-xs-6 col-md-3  col-sm-4 text-center icons">
  <a href="/web/system-admin/user-management" class="img-thumbnail">
    <img src="<%=resourceBaseURL %>/images/audittrail.png" style="height: 100px;">
    <p class="title">User Management</p>
  </a>
</div>


<div class="col-xs-6 col-md-3  col-sm-4 text-center icons">
  <a href="/web/system-admin/agency-management" class="img-thumbnail">
    <img src="<%=resourceBaseURL %>/images/audittrail.png" style="height: 100px;">
    <p class="title">Agency Management</p>
  </a>
</div>

<div class="col-xs-6 col-md-3  col-sm-4 text-center icons">
  <a href="/web/system-admin/item-category-management" class="img-thumbnail">
    <img src="<%=resourceBaseURL %>/images/audittrail.png" style="height: 100px;">
    <p class="title">HS Codes Management</p>
  </a>
</div>

<div class="col-xs-6 col-md-3  col-sm-4 text-center icons">
  <a href="/web/system-admin/currency-management" class="img-thumbnail">
    <img src="<%=resourceBaseURL %>/images/audittrail.png" style="height: 100px;">
    <p class="title">Currency Management</p>
  </a>
</div>

<div class="col-xs-6 col-md-3  col-sm-4 text-center icons">
  <a href="/web/system-admin/ports-management" class="img-thumbnail">
    <img src="<%=resourceBaseURL %>/images/audittrail.png" style="height: 100px;">
    <p class="title">Ports Management</p>
  </a>
</div>

<div class="col-xs-6 col-md-3  col-sm-4 text-center icons">
  <a href="/web/system-admin/work-flow-configuration" class="img-thumbnail">
    <img src="<%=resourceBaseURL %>/images/audittrail.png" style="height: 100px;">
    <p class="title">Work Flow Config</p>
  </a>
</div>

<div class="col-xs-6 col-md-3  col-sm-4 text-center icons">
  <a href="/web/system-admin/applicationattachment" class="img-thumbnail">
    <img src="<%=resourceBaseURL %>/images/audittrail.png" style="height: 100px;">
    <p class="title">Application Attachment Types</p>
  </a>
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
</script>