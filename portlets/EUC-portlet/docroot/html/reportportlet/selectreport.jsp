<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.entity.Certificate"%>
<%@page import="smartpay.entity.ItemCategory"%>
<%@page import="smartpay.entity.Applicant"%>
<%@page import="smartpay.entity.Application"%>
<%@page import="com.ecims.portlet.reportportlet.ReportPortletState"%>
<%@page import="com.ecims.portlet.reportportlet.ReportPortletState.*"%>
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
<%@page import="java.text.DateFormat"%>
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
<%

ReportPortletState portletState = ReportPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ReportPortletState.class);


%>


<portlet:actionURL var="proceedToStepOne" name="processAction">
	<portlet:param name="action"
		value="<%=REPORTING_ACTIONS.CREATE_A_REPORT_STEP_ONE.name()%>" />
</portlet:actionURL>


<div style="padding:10px; width:900px"> 	
    <div class="panel  panel-primary">
		<div class="panel-heading"><span style="color:white; font-weight: bold">Select Report Type:</span></div>
		<div class="panel-body">
		    <form  id="panelcreatorform" action="<%=proceedToStepOne%>" method="post" enctype="application/x-www-form-urlencoded">
			    <fieldset>
			      <div> <strong>Report Type:</strong>
			      	<div>
			          	<select name="reportSelected" id="reportSelected" class="form-control">
						  	<option value="-1">-Select A Report Type-</option>
							<!-- <option value="<%=Applicant.class.getSimpleName()%>">Reports on Applicants</option>-->
							<option value="<%=Application.class.getSimpleName()%>">Reports on Applications</option>
							<!-- <option value="<%=ItemCategory.class.getSimpleName()%>">Reports on Item Categories</option>-->
							<option value="<%=Certificate.class.getSimpleName()%>">Reports on Certificates</option>
							<option value="<%=PortalUser.class.getSimpleName()%>">Reports on Systems' Users</option>
					  	</select>
			        </div>
			      </div>
				  <div style="padding-top: 10px;">
			        <button name="createPanelName" id="createPanelName" class="btn btn-success">Next</button>
			      </div>
			    </fieldset>
		    </form>
		</div>
	</div>
</div>