<%@page import="com.ecims.portlet.admin.applicationautoprocesss.ApplicationAutoProcessPortletState"%>
<%@page	import="com.ecims.portlet.admin.applicationautoprocesss.ApplicationAutoProcessPortletState.*"%>
<%@page	import="com.ecims.portlet.admin.applicationautoprocesss.ApplicationAutoProcessPortletUtil"%>
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
<%@page import="smartpay.entity.ItemCategory"%>
<%@page import="smartpay.entity.WokFlowSetting"%>
<%@page import="smartpay.entity.enumerations.AgencyType"%>
<%@page import="smartpay.entity.enumerations.KinRelationshipType"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.RoleType"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Arrays" %>
<%@page import="java.util.List" %>
<%@page import="com.ecims.commins.Util"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="net.tanesha.recaptcha.ReCaptcha" %>
<%@ page import="net.tanesha.recaptcha.ReCaptchaFactory" %>




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
<script type="text/javascript" src="<%=(resourceBaseURL + "/js/anytimec.js")%>"></script>
<link rel="stylesheet" href='<%=resourceBaseURL + "/css/anytimec.css"%>' type="text/css" />
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=(resourceBaseURL + "/js/bootstap.min.js")%>"></script>
<script src="<%=resourceBaseURL %>/js/jquery-1.11.1.min.js"></script>
<script src="<%= resourceBaseURL %>/js/app.v1.js"></script>
<!-- parsley -->
<script src="<%= resourceBaseURL %>/js/parsley.min.js"></script>
<script src="<%= resourceBaseURL %>/js/parsley.extend.js"></script>
<script src="<%= resourceBaseURL %>/js/app.plugin.js"></script>
<style>
#recaptcha_area input { height: auto; }
#recaptcha_area a { font-weight: normal; }
.formwidth50{
	width:300px;
}
.required{
	color:red;
	
}
</style>
<%

ApplicationAutoProcessPortletState portletState = ApplicationAutoProcessPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ApplicationAutoProcessPortletState.class);
String selected = "";
%>


<portlet:actionURL var="listing" name="processAction">
	<portlet:param name="action"
		value="<%=ACTIONS.MANAGE_AUTO_PROCESS.name()%>" />
</portlet:actionURL>

<div style="padding:10px">
<div class="panel panel-info">
  	<!-- Default panel contents -->
  	<div class="panel-heading"><strong>Step 1 of 2: Application WorkFlow Process</strong></div>
  	<div class="panel-body">&nbsp;
  	</div>
		<form  id="userListFormId" action="<%=listing%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
			
		<legend>Select An Item Category To Create A Work Flow Process For</legend>
		 
	  	<select name="itemcategory" id="itemcategory"  class="clear form-control formwidth50">
	  		<option value="">Select One</option>
	  		<%
	  		for(Iterator<ItemCategory> it = portletState.getItemCategoryListing().iterator(); it.hasNext();)
	  		{
	  			ItemCategory itemCat = it.next();
	  			if(portletState.getItemCategoryEntity().getId().equals(itemCat.getId()))
	  			{
	  				selected = "selected='selected'";
	  			}
	  			%>
	  			<option value="<%=itemCat.getId()%>" <%=selected %>><%=itemCat.getItemCategoryName() %></option>
	  			<%
	  		}
	  		%>
	  	</select>
		<input type="hidden" name="selectedApplications" id="selectedApplications" value="" />
		<input type="hidden" name="selectedUserAction" id="selectedUserAction" value="" />	
	      	<button type="submit" class="btn btn-success" style="float:right" onclick="javascript:handleButtonAction('gotostepthreeautoprocess', '', '')">Next</button>
	</form>
</div>
</div>


<script type="text/javascript" charset="utf-8" src="<%=jqueryDataTableUrl%>"></script>

<script type="text/javascript">
$(document).ready(function() {
    $('#btable').dataTable();
} );


function handleButtonAction(action, usId, aert){
	
	if(action=='recallcertificate' || action=='canceldisputecertificate')
	{
		if(confirm("Are you sure you want to carry out this action on the certificate?"))
		{
			document.getElementById('selectedApplications').value = usId;
			document.getElementById('selectedUserAction').value = action;
			document.getElementById('userListFormId').submit();
		}
	}if(action=='downloadcertificate')
	{
		document.location.href='<%=response.encodeURL(request.getContextPath()+"/ActiveServlet?action=downloadCertificate&gyus=")%>'+aert +'&iosdp='+usId;
	}else
	{
		document.getElementById('selectedApplications').value = usId;
		document.getElementById('selectedUserAction').value = action;
		document.getElementById('userListFormId').submit();
	}
}


function handleSelectAll()
{
	if(document.getElementById('clickSelectAll').checked==true)
	{
		var cbs = document.getElementsByName('selectAllCheckbox');
		for(var i=0; i<cbs.length; i++)
		{
			cbs[i].checked = true;
		}
	}	  
	else
	{
		var cbs = document.getElementsByName('selectAllCheckbox');
		for(var i=0; i<cbs.length; i++)
		{
			cbs[i].checked = false;
		}
	}
}



function isCheckBoxesChecked()
{
	var cbs = document.getElementsByName('selectAllCheckbox');
	var c = 0;
	for(var i=0; i < cbs.length; i++) {
		if(document.getElementsByName('selectAllCheckbox')[i].checked)
		{
			c++;
		}
	}
	
	if(c==0)
		return false;
	else
		return true;
}



function uncheckAll()
{
	var cbs = document.getElementsByName('selectAllCheckbox');
	var c = 0;
	for(var i=0; i < cbs.length; i++) {
		document.getElementsByName('selectAllCheckbox')[i].checked=false;
	}
}
</script>

