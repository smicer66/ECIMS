<%@page import="com.ecims.portlet.certificatemanagement.CertificatePortletState"%>
<%@page import="com.ecims.portlet.certificatemanagement.CertificatePortletState.*"%>
<%@page import="java.util.Collection"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@ page import="com.liferay.portal.kernel.util.Validator"%>
<%@ page import="javax.portlet.PortletPreferences"%>
<%@ page import="javax.portlet.PortletURL"%>
<%@ page import="javax.portlet.ActionRequest"%>
<%@ page import="javax.portlet.RenderResponse"%>
<%@ page import="org.apache.commons.lang.RandomStringUtils"%>
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
<%@page import="smartpay.entity.ItemCategory"%>
<%@page import="smartpay.entity.enumerations.KinRelationshipType"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.enumerations.RoleConstants"%>
<%@page import="smartpay.entity.enumerations.AgencyType"%>
<%@page import="smartpay.entity.RoleType"%>
<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.entity.Agency"%>
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
<%

CertificatePortletState portletState = CertificatePortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(CertificatePortletState.class);

if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
{
%>
<jsp:include page="/html/certificateportlet/tabs_eu.jsp" flush="" />
<%
}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
{
%>
<jsp:include page="/html/certificateportlet/tabs_nsa.jsp" flush="" />
<%
}
%>
<portlet:actionURL var="proceed" name="processAction">
	<portlet:param name="action"
		value="<%=APPLIST_ACTIONS.ACT_ON_CERTIFICATE.name()%>" />
</portlet:actionURL>


<div style="padding-left:10px; padding-right:10px; width:900px">
	<h4>Collect A Certificate</h4>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Collect A Certificate</span></div>
	  <div class="panel-body">
	<form  id="startRegFormId" action="<%=proceed%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
                            
	                                	<div class="row">
	                                        <div class="col-lg-5">
	                                            <div class="form-group">
	                                                <strong>Certificate Number:</strong><br>
									<%=portletState.getCertificateSelected()!=null ? portletState.getCertificateSelected().getCertificateNo() : ""%> 
	                                            </div>
	                                        </div>
	
	                                        
	                                    </div>
	                                    <div class="row">
	                                        <div class="col-lg-5">
	                                            <div class="form-group">
	                                                Certificate Collection Code:<br>
	                                                <input type="text" placeholder="Enter Certificate Collection Code Here" name="certificateCode" id="certificateCode" value=""/>
	                                            </div>
	                                        </div>
	
	                                        
	                                    </div>
	                                    
                                        	<button type="submit" name="btnSaveApplication" id="SaveApplication" class="btn btn-danger btn-cons" style="float:left" onClick="javascript:submitForm('cancelcollect')">
                                        	Cancel</button>
                                			<button type="submit" name="btnSaveApplication"  id="SaveApplication" class="btn btn-success btn-cons" style="float:right" onClick="javascript:submitForm('collectcertificate')">
                                			Next</button>
                                        
							
								
                                <input type="hidden" name="act" value="" id="act">
                                <input type="hidden" name="actId" value="<%=portletState.getCertificateSelected()==null ? "" : portletState.getCertificateSelected().getId() %>" id="actId">
                          
	</form>
</div>






<script type="text/javascript">
$(document).ready(function() {
    $('#btable').dataTable();
} );



function downloadSlip(gyus, iosdp, a){

	document.location.href='<%=response.encodeURL(request.getContextPath()+"/ActiveServlet?action=downloadCertificate&gyus=")%>'+gyus +'&iosdp='+iosdp;
	
}

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