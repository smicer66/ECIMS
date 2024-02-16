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
<%@page import="smartpay.entity.RoleType"%>
<%@page import="java.text.SimpleDateFormat"%>
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
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

%>

<portlet:actionURL var="proceedToStepThree" name="processAction">
	<portlet:param name="action"
		value="<%=EUC_DESK_ACTIONS.LIST_PORTAL_USERS_ACTIONS.name()%>" />
</portlet:actionURL>

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

String usr="";
String usr1="";

if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_END_USER))
{
	usr = "My Profile";
	usr1 = "Edit My Account";
}
else
{
	if(portletState.getSelectedPortalUser()!=null && portletState.getPortalUser().getId().equals(portletState.getSelectedPortalUser().getId()))
	{
		usr = "My Profile";
		usr1 = "Edit My Account";
	}else
	{
		usr = "User Profile";
		usr1 = "Edit This Account";
	}	
}

%>


<div style="padding-left:10px; padding-right:10px; width:900px">
	<h2><%=usr %></h2>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold"><%=usr %></span></div>
	  <div class="panel-body">
	    	
	    <form  id="startRegFormId" action="<%=proceedToStepThree%>" method="post" enctype="application/x-www-form-urlencoded">
	     
		  	<div style="padding-bottom:5px"> <strong>Type Of User:</strong>
					<div>
					  	<%=portletState.getSelectedPortalUser()!=null ? portletState.getSelectedPortalUser().getRoleType().getName().getValue() : "N/A"%>
					</div>
		  	</div>
		  
		  <%
		  if(portletState.getSelectedPortalUser()!=null && portletState.getSelectedPortalUser().getAgency()!=null)
		  {
		  	%>
		    <div style="padding-bottom:5px"> <strong>This User Belongs to This Group Of Users:</strong>
		        <div>
		          	<%=portletState.getSelectedPortalUser()!=null ? portletState.getSelectedPortalUser().getAgency().getAgencyName() : "N/A"%>
					</select>
		        </div>
		      </div>
		  	<%  
		  }
		  %>
		  <div style="padding-bottom:5px"> <strong>Surname:</strong>
	        <div>
	          <%=portletState.getSelectedPortalUser()==null ? "N/A" : portletState.getSelectedPortalUser().getSurname() %>
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>First Name:</strong>
	        <div>
	          <%=portletState.getSelectedPortalUser()==null ? "N/A" : portletState.getSelectedPortalUser().getFirstName() %>
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>Other Name:</strong>
	        <div>
	          <%=portletState.getSelectedPortalUser()==null ? "N/A" : portletState.getSelectedPortalUser().getOtherName() %>
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>Email Address:</strong>
	        <div>
	          <%=portletState.getSelectedPortalUser()==null ? "N/A" : portletState.getSelectedPortalUser().getEmailAddress() %>
	        </div>
	      </div>
	      
	      <div style="padding-bottom:5px"><strong>Date of Birth:</strong>
	        <div>
	          <%=portletState.getSelectedPortalUser()==null ? "" : sdf.format(portletState.getSelectedPortalUser().getDateOfBirth()) %>
	        </div>
	      </div>
		  <div style="padding-bottom:5px; clear:both"><div><strong>Mobile Number:</strong></div>
	        <div>
				<div style="float:left; padding-right:10px">
					<%=portletState.getSelectedPortalUser()!=null ? portletState.getSelectedPortalUser().getPhoneNumber() : ""%>
			  	</div>
	        </div>
	      </div>
		  <div style="padding-bottom:5px; clear:both"><div><strong>Nationality:</strong></div>
	        <div>
				<div style="padding-right:10px">
					<%=portletState.getSelectedPortalUser()!=null ? portletState.getSelectedPortalUser().getState().getCountry().getName() : "N/A"%>
				</div>
	        </div>
	      </div>
		  <div style="padding-bottom:5px; clear:both"><div><strong>State:</strong></div>
	        <div>
				<div style="padding-right:10px">
					<%=portletState.getSelectedPortalUser().getState()!=null ? portletState.getSelectedPortalUser().getState().getName() : "N/A"%>
				</div>
	        </div>
	      </div>
	      
		  <div style="padding-bottom:5px">
			  	<strong>Tax Identification Number:</strong>
			<div>
				<%=portletState.getSelectedPortalUser().getTaxIdNo()==null ? "N/A" : portletState.getSelectedPortalUser().getTaxIdNo() %>
			</div>
		  </div>
<div>
	      	<input type="hidden" value="" name="selectedPortalUserAction" id="selectedPortalUserAction" />
	      	<input type="hidden" value="" name="selectedPortalUser" id="selectedPortalUser" />
	      	<%
	      	if(portletState.getPortalUser().getId().equals(portletState.getSelectedPortalUser().getId()) && 
	      			!portletState.getSelectedPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_END_USER))
	      	{
	      	%>
	      	<button type="submit" class="btn btn-success" style="float:right" 
	      	onclick="javascript:handleButtonAction('edituser', '<%=portletState.getSelectedPortalUser().getId()%>')"><%=usr1 %></button>
	      	<%
	      	}else
	      	{
	      		if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_ADMIN) && 
		      			!portletState.getSelectedPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_END_USER))
	      		{
	      			%>
	    	      	<button type="submit" class="btn btn-success" style="float:right" 
	    	      	onclick="javascript:handleButtonAction('edituser', '<%=portletState.getSelectedPortalUser().getId()%>')"><%=usr1 %></button>
	    	      	<%
	      		}
	      	}
	      	%>
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

function handleButtonAction(action, usId){

	if(action=='delete')
	{
		if(confirm("Are you sure you want delete ed this user? You will not be able to undo this action after deleting the user!"))
		{
			document.getElementById('selectedPortalUser').value = usId;
			document.getElementById('selectedPortalUserAction').value = action;
			document.getElementById('startRegFormId').submit();
		}
	}else
	{
	document.getElementById('selectedPortalUser').value = usId;
	document.getElementById('selectedPortalUserAction').value = action;
	document.getElementById('startRegFormId').submit();
	}
}
</script>