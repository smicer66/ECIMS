<%@page import="smartpay.entity.Dispute"%>
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
<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.entity.Company"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.RoleType"%>
<%@page import="smartpay.entity.Applicant"%>
<%@page import="smartpay.entity.ApplicationItem"%>
<%@page import="smartpay.entity.Application"%>
<%@page import="smartpay.entity.Role"%>
<%@page import="smartpay.entity.Certificate"%>
<%@page import="smartpay.entity.ApplicationWorkflow"%>
<%@page import="smartpay.entity.enumerations.UserStatus"%>
<%@page import="smartpay.entity.enumerations.RoleConstants"%>
<%@page import="smartpay.entity.enumerations.ApplicantStatus"%>
<%@page import="smartpay.entity.enumerations.ApplicationStatus"%>
<%@page import="smartpay.entity.enumerations.CertificateStatus"%>
<%@page import="smartpay.entity.enumerations.PermissionType"%>
<%@page import="java.text.DateFormat"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />
<%
	String resourceBaseURL = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ "/resources";
	String faceboxCssUrl = resourceBaseURL + "/css/facebox.css";
	String pagingUrl = resourceBaseURL + "/css/paging.css";

	String jqueryDataTableCssUrl = resourceBaseURL + "/css/jquery.dataTables.css";
	String jqueryDataTableThemeCssUrl = resourceBaseURL + "/css/jquery.dataTables_themeroller.css";
	String jqueryDataTableUrl = resourceBaseURL + "/js/jquery.dataTables.min.js";

		
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
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" /><%

CertificatePortletState portletState = CertificatePortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(CertificatePortletState.class);
SimpleDateFormat sdf = new SimpleDateFormat();
%>

<%

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
}else if (portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
{
	%>
	<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>
	<%
}
%>

<portlet:actionURL var="listing" name="processAction">
	<portlet:param name="action"
		value="<%=CERTLIST_ACTIONS.HANDLE_ACTIONS_ON_LIST_CERTIFICATES.name()%>" />
</portlet:actionURL>



<div class="list-group-item">
	<form  id="startRegFormId" action="<%=listing%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
                            


                                <div class="panel panel-info">
								  	<!-- Default panel contents -->
								  	<div class="panel-heading"><strong>Certificate Details</strong></div>
								  	<div class="panel-body">&nbsp;
	                                	<div class="row">
	                                        <div class="col-lg-5">
	                                            <div class="form-group">
	                                                <strong>Certificate Number:</strong><br>
									<%=portletState.getCertificateSelected()!=null ? portletState.getCertificateSelected().getCertificateNo() : "N/A"%> 
	                                            </div>
	                                        </div>
	
	                                        
	                                    </div>
	                                    <!-- .panel-body -->
	                                </div>
	                            </div>
                                <div class="panel-body">
                                    <div class="row">
                                        <div class="col-lg-5">
                                                <div class="form-group">
                                                    Add Comments:
                            						  <textarea name="comments" id="comments" class="form-control" placeholder="Provide Comments if necessary"><%=portletState.getCommentsDispute()==null ? "" : portletState.getCommentsDispute()%></textarea>
                                                </div>
                                        </div>

                                        
                                    </div>
                                    <!-- .panel-body -->
                                </div>
                                
							<div class="panel panel-default">
								
								<input type="hidden" name="selectedApplications" id="selectedApplications" value="<%=portletState.getCertificateSelected()==null ? "" : portletState.getCertificateSelected().getId() %>" />
								<input type="hidden" name="selectedUserAction" id="selectedUserAction" value="" />	
		
                                <input type="submit" name="btnSaveApplication" value="Cancel" id="SaveApplication" class="btn btn-danger btn-cons" style="float:left" onClick="javascript:submitForm('canceldispute')">
                                <input type="submit" name="btnSaveApplication" value="Next" id="SaveApplication" class="btn btn-success btn-cons" style="float:right" onClick="javascript:submitForm('disputeproceed')">
                            </div>
	</form>
</div>






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
	document.getElementById('selectedUserAction').value=clicked;
	document.getElementById('startRegFormId').submit;
	
}
</script>