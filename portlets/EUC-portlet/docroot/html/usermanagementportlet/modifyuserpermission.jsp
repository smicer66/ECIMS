<%@page import="com.ecims.portlet.usermanagement.UserManagementPortletState"%>
<%@page import="com.ecims.portlet.usermanagement.UserManagementPortletState.*"%>
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
<%@page import="smartpay.entity.enumerations.RoleConstants"%>
<%@page import="smartpay.entity.enumerations.PermissionType"%>
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
<%

UserManagementPortletState portletState = UserManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(UserManagementPortletState.class);
Collection<Agency> agencyList = portletState.getAgencyList();
Collection<PermissionType> permissionList = portletState.getSelectedPortalUserPermissions();
Collection<PermissionType> pList = PermissionType.values();
String checked = "";

%>


<%
if(portletState.getPortalUser().getRoleType().getName().getValue().equals(RoleTypeConstants.ROLE_END_USER.getValue()))
{
%>
<jsp:include page="/html/usermanagementportlet/tabs_eu.jsp" flush="" />
<%
}else if(portletState.getPortalUser().getRoleType().getName().getValue().equals(RoleTypeConstants.ROLE_NSA_ADMIN.getValue()))
{
%>
<jsp:include page="/html/usermanagementportlet/tabs_nsa_admin.jsp" flush="" />
<%
}else if(portletState.getPortalUser().getRoleType().getName().getValue().equals(RoleTypeConstants.ROLE_NSA_USER.getValue()))
{
%>
<jsp:include page="/html/usermanagementportlet/tabs_nsa_user.jsp" flush="" />
<%
}else 
{
	if(portletState.getPortalUser().getRoleType().getRole().getName().getValue().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP.getValue()))
	{
%>
<jsp:include page="/html/usermanagementportlet/tabs_agency.jsp" flush="" />
<%
	}
}
%>
<portlet:actionURL var="proceedToStepThree" name="processAction">
	<portlet:param name="action"
		value="<%=EUC_DESK_ACTIONS.UPDATE_PORTAL_USER_PERMISSION.name()%>" />
</portlet:actionURL>

<jsp:include page="/html/usermanagementportlet/tabs_nsa_admin.jsp" flush="" />



<div style="padding-left:10px; padding-right:10px; width:900px">
	<h2>User Permission</h2>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Modify User Permissions</span></div>
	  <div class="panel-body">
	    	
	    <form  id="startRegFormId" action="<%=proceedToStepThree%>" method="post" enctype="application/x-www-form-urlencoded">
	     
		  <div style="padding-bottom:5px"> <strong>Update User Permissions:</strong>
		  <%
		  if(pList!=null && pList.size()>0)
		  {
			  for(Iterator<PermissionType> it = pList.iterator(); it.hasNext();)
			  {
				  PermissionType pt = it.next();
				  if(pt.getValue().equals(PermissionType.PERMISSION_ADMIN_MANAGEMENT.getValue()) || 
						  pt.getValue().equals(PermissionType.PERMISSION_MANAGE_PORTAL_USER.getValue()))
				  {
				  }else
				  {
					  checked = "";
					  if(permissionList!=null && permissionList.contains(pt))
					  {
						  checked = "checked='checked'";
					  }
				 %>
					<div>
			          <input type="checkbox" <%=checked %> name="permission" value="<%=pt.getValue()%>">&nbsp;&nbsp;&nbsp;
			          <%= pt.getValue()%>
			        </div>
				 <% 
				  }
			  }
		  }
		  %>
	        
	      </div>
<div>
	      	<input type="hidden" value="" name="act" id="act" />
	      	<input type="hidden" value="<%=portletState.getSelectedPortalUser()!=null ? portletState.getSelectedPortalUser().getId() : "" %>" name="actId" id="actId" />
	      	<button type="submit" class="btn btn-success" style="float:left" onclick="javascript:submitForm('cancelupdatepermission')">Cancel</button>
	      	<button type="submit" class="btn btn-success" style="float:right" onclick="javascript:submitForm('savepermission')">Save</button>
	      	<div style="clear:both; padding-top:10px; font-size:11px; color:red; font-weight:bold">
	    All fields with red asterisk (*) imply they must be provided</div>
	        <!-- <input type="submit" name="createportaluserStepOne" value="Proceed to Next" id="createportaluserStepOne" class="floatLeft" style="background-color:#00CC00" />-->
	      </div>
	    </form>
	  </div>
	</div>
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
	document.getElementById('act').value=clicked;
	document.getElementById('startRegFormId').submit;
	
}
</script>