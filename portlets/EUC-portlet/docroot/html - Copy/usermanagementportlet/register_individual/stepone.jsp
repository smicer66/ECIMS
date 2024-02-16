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
<%@page import="smartpay.entity.enumerations.KinRelationshipType"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.RoleType"%>
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

UserManagementPortletState portletState = UserManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(UserManagementPortletState.class);
Collection<Agency> agencyList = portletState.getAgencyList();

%>

<portlet:actionURL var="proceedToStepThree" name="processAction">
	<portlet:param name="action"
		value="<%=EUC_DESK_ACTIONS.CREATE_A_PORTAL_USER_STEP_ONE.name()%>" />
</portlet:actionURL>

<jsp:include page="/html/usermanagementportlet/tabs.jsp" flush="" />


<liferay-ui:success key="successMessage"
		message="<%=portletState.getSuccessMessage()%>"></liferay-ui:success>
<liferay-ui:error key="errorMessage"
		message="<%=portletState.getErrorMessage()%>"></liferay-ui:error>




<div style="padding-left:10px; padding-right:10px; width:900px">
	<h2>Create A User Profile</h2>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Step 2 of 3: Portal User's Bio-Data</span></div>
	  <div class="panel-body">
	    	
	    <form  id="startRegFormId" action="<%=proceedToStepThree%>" method="post" enctype="application/x-www-form-urlencoded">
	     
	     <%
	     if(portletState.getUsertype()!=null && (portletState.getUsertype().equals(RoleTypeConstants.ROLE_INFORMATION_USER.getValue()) || 
	    		 portletState.getUsertype().equals(RoleTypeConstants.ROLE_REGULATOR_USER.getValue()) || 
	    		 portletState.getUsertype().equals(RoleTypeConstants.ROLE_ACCREDITOR_USER.getValue()) ))
	     {
	     %>
		  	<div style="padding-bottom:5px">
					<div>
					  	<select class="form-control" name="agency">
							<option value="-1">-Select An Agency This User Belongs to-</option>
							<%
						if(agencyList!=null && agencyList.size()>0)
						{
							
							for(Iterator<Agency> iter = agencyList.iterator(); iter.hasNext();)
							{
								Agency agency = iter.next();
								
								String selected="";
								if(portletState.getAgencyEntity()!=null && portletState.getAgencyEntity().getAgencyName().equalsIgnoreCase(agency.getAgencyName()))
								{
									selected = "selected='selected'";
								}
								%>
									<option <%=selected%> value="<%=agency.getId() %>"><%=agency.getAgencyName() %></option>
								<%
								
							}
						}
							
							%>
						</select>
					</div>
		  	</div>
		  <%
	     }
		  %>
		  
		  <div style="padding-bottom:5px"> <strong>Surname:<span style="color:red">*</span></strong>
	        <div>
	          <input class="clear form-control" type="text" value="<%=portletState.getLastname()==null ? "" : portletState.getLastname() %>" name="lastname" id="lastname" placeholder="Provide Your Surname" />
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>First Name:<span style="color:red">*</span></strong>
	        <div>
	          <input class="clear form-control" type="text" value="<%=portletState.getFirstname()==null ? "" : portletState.getFirstname() %>" name="firstname" id="firstname" placeholder="Provide Your First Name" />
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>Other Name:</strong>
	        <div>
	          <input class="clear form-control" type="text" value="<%=portletState.getOthername()==null ? "" : portletState.getOthername() %>" name="othername" id="othername" placeholder="Provide Your Other Name" />
	        </div>
	      </div>
	      <div style="padding-bottom:5px"><strong>Email Address:<span style="color:red">*</span></strong>
	        <div>
	          <input class="clear form-control" type="text" value="<%=portletState.getContactEmailAddress()==null ? "" : portletState.getContactEmailAddress() %>" name="contactEmailAddress" id="contactEmailAddress" placeholder="Provide Your Email Address" />
	        </div>
	      </div>
	      
	      <div style="padding-bottom:5px"><strong>Date of Birth:<span style="color:red">*</span></strong>
	        <div>
	          <input class="clear form-control" type="text" value="<%=portletState.getDob()==null ? "" : portletState.getDob() %>" name="dob" id="dob" placeholder="YYYY-MM-DD" />
	        </div>
	      </div>
		  <div style="padding-bottom:5px; clear:both"><div><strong>Mobile Number:<span style="color:red">*</span></strong></div>
	        <div>
				<div style="float:left; padding-right:10px">
					<select class="form-control" name="countryCode">
						<%
						Collection<Country> countryList = portletState.getAllCountry();
						
						
						for(Iterator<Country> iter = countryList.iterator(); iter.hasNext();)
						{
							Country country = iter.next();
							String selected = "";
							if(portletState.getCountryCode()!=null && portletState.getCountryCode().equals(country.getCode()))
							{
								selected = "selected='selected'";
							}
							%>
							<option <%=selected%> value="<%=country.getCode()%>">+<%=country.getCode()%></option>
							<%
						}
						%>
					</select>
				</div>
				<div style="float:left">
	          <input onkeypress="return onlyNumKey(event)" class="form-control" type="text" value="<%=portletState.getMobile()==null ? "" : portletState.getMobile() %>" name="mobile" id="mobile" placeholder="e.g. 9XXXXXXXXX" />
			  	</div>
	        </div>
	      </div>
		  <div style="padding-bottom:5px; clear:both"><div><strong>Nationality:<span style="color:red">*</span></strong></div>
	        <div>
				<div style="padding-right:10px">
					<select class="form-control" name="nationality">
						<%
						portletState.getAllCountry();
						
						for(Iterator<Country> iter = countryList.iterator(); iter.hasNext();)
						{
							String countryName = iter.next().getName();
							String selected = "";
							if(portletState.getNationality()!=null && portletState.getNationality().equals(countryName))
							{
								selected = "selected='selected'";
							}
						%>
						<option <%=selected%> value="<%=countryName%>"><%=countryName%></option>
						<%
						}
						%>
					</select>
				</div>
	        </div>
	      </div>
		  <div style="padding-bottom:5px; clear:both"><div><strong>State:<span style="color:red">*</span></strong></div>
	        <div>
				<div style="padding-right:10px">
					<select class="form-control" name="state">
						<%
						Collection<State> stateList = portletState.getAllState();
						for(Iterator<State> iter = stateList.iterator(); iter.hasNext();)
						{
							String stateName = iter.next().getName();
							String selected = "";
							if(portletState.getState()!=null && portletState.getState().equals(stateName))
							{
								selected = "selected='selected'";
							}
						%>
						<option <%=selected%> value="<%=stateName%>"><%=stateName%></option>
						<%
						}
						%>
					</select>
				</div>
	        </div>
	      </div>
<div>
	      	<input type="hidden" value="" name="act" id="act" />
	      	<button type="submit" class="btn btn-success" style="float:left" onclick="javascript:submitForm('back')">Go Back</button>
	      	<button type="submit" class="btn btn-success" style="float:right" onclick="javascript:submitForm('proceed')">Proceed to Next</button>
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