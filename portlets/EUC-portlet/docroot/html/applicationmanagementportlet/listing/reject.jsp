<%@page import="com.ecims.portlet.applicationmanagement.ApplicationManagementPortletState"%>
<%@page import="com.ecims.portlet.applicationmanagement.ApplicationManagementPortletState.*"%>
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
<%@page import="smartpay.entity.enumerations.RoleConstants"%>
<%@page import="smartpay.entity.RoleType"%>
<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.entity.ItemCategorySub"%>
<%@page import="smartpay.entity.QuantityUnit"%>
<%@page import="smartpay.entity.WeightUnit"%>
<%@page import="smartpay.entity.Currency"%>
<%@page import="smartpay.entity.ApplicationItem"%>
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

ApplicationManagementPortletState portletState = ApplicationManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ApplicationManagementPortletState.class);
Double amount = 0.00;
if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
{
%>
<jsp:include page="/html/applicationmanagementportlet/tabs_eu.jsp" flush="" />
<%
}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
{
%>
<jsp:include page="/html/applicationmanagementportlet/tabs_nsa.jsp" flush="" />
<%
}
%>

<portlet:actionURL var="proceed" name="processAction">
	<portlet:param name="action"
		value="<%=APPLIST_ACTIONS.ACT_ON_APPLICATION.name()%>" />
</portlet:actionURL>



<div class="list-group-item">
	<form  id="startRegFormId" action="<%=proceed%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
                            

                            <div class="row">
                               REJECT APPLICATION
                            </div>

                            <div class="panel panel-default">
                                <div class="panel-heading">
                                    You want to reject this application                  
                                </div>
                                <div class="panel-body">
                                    
                                    <div class="row">
                                        <div class="row" style="padding: 16px">
                                            <div class="col-lg-6">
                                                Application Number:
                                                <%=portletState.getApplicationWorkflow()==null ? "N/A" : portletState.getApplicationWorkflow().getApplication().getApplicationNumber() %>
                                            </div>

                                            <div class="col-lg-6">
                                                Item Category:
                                                <%=portletState.getApplicationWorkflow()==null ? "" : portletState.getApplicationWorkflow().getApplication().getItemCategory().getItemCategoryName()%>
                                                
                                            </div>
                                        </div>
                                        <div class="col-lg-2">
                                            <textarea name="rejectcomment" style="width:300px; font-size:11px;" id="rejectcomment" placeholder="Provide information for rejecting this application"><%=portletState.getRejectComment()==null ? "" : portletState.getRejectComment()%></textarea>
                                        </div>
                                    </div>
									
									
                                    <!-- .panel-body -->
                                </div>
                            </div>
							<div class="panel panel-default">
								<input type="hidden" name="act" id="act" value="">
								<input type="hidden" name="actId" id="actId" value="<%=portletState.getApplicationWorkflow()==null ? "" : portletState.getApplicationWorkflow().getId()%>">
                                <input type="submit" name="btnSaveApplication" value="Cancel" id="SaveApplication" class="btn btn-danger btn-cons" style="float:left" onclick="javascript:submitForm('back')">
                                <input type="submit" name="btnSaveApplication" value="Proceed With Rejection" id="SaveApplication" class="btn btn-success btn-cons" style="float:right" onclick="javascript:submitForm('rejectthisapplication')">
                            </div>
	</form>
</div>






<script type="text/javascript">


var th = ['','Thousand','Million', 'Billion','Trillion'];
var dg = ['Zero','One','Two','Three','Four', 'Five','Six','Seven','Eight','Nine']; 
var tn = ['Ten','Eleven','Twelve','Thirteen', 'Fourteen','Fifteen','Sixteen', 'Seventeen','Eighteen','Nineteen']; 
var tw = ['Twenty','Thirty','Forty','Fifty', 'Sixty','Seventy','Eighty','Ninety']; 

$( document ).ready(function() {
    document.getElementBuId("txtItemValueInword").value = toWords(<%=amount%>)
});


function toWords(s)
{
	s = s.toString(); 
	var gh = s.indexOf(".");
	if(gh != -1)
	{
		s = s.substr(0, (gh+3));	
	}
	s = s.replace(/[\, ]/g,''); 
	if (s != parseFloat(s)) 
		return 'not a number'; 
		
	var andTest = false;
	
	var x = s.indexOf('.'); 
	if (x == -1) 
	{
		x = s.length; 
		if(s.substr(0, x).length>2)
		{
			andTest = true;
		}
	}else
	{
		andTest = true;
	}
		
	if (x > 15) 
		return 'too big'; 
		
	var n = s.split(''); 
	var str = ''; 
	var sk = 0; 
	for (var i=0; i < x; i++) 
	{
		if ((x-i)%3==2) 
		{
			if (n[i] == '1') 
			{
				str += tn[Number(n[i+1])] + ' '; 
				i++; sk=1;
			} 
			else if (n[i]!=0) 
			{
				str += tw[n[i]-2] + ' ';
				sk=1;
			}
		} 
		else if (n[i]!=0) 
		{
			str += dg[n[i]] +' '; 
			if ((x-i)%3==0) 
				str += 'Hundred ';
			
			sk=1;
		} 
		
		if ((x-i)%3==1) 
		{
			if (sk) 
				str += th[(x-i-1)/3] + ' ';
			
			sk=0;
		}
		
		if(andTest==true && i==0)
		{
			str += ' ';
		}
	} 
	if(str.length>0)
	{
		str += "Naira";
	}
	
	if (x != s.length) 
	{
		var y = s.length; 
		str += ' and '; 
		if ((y-x -1)==2) 
		{
			var u1 = s.substr((x+1), y)
			var u2 = u1.split('')
			if(u2.length>0)
			{
				str += tw[u2[0] - 2] + ' ' + dg[u2[1]];
			}else if(u2.length==1)
			{
				str += dg[u2[0]];
			}
		}
		//for (var i=x+1; i<y; i++) 
			//str += dg[n[i]] +' ';
		str += " Kobo";
	} 
	return str.replace(/\s+/g,' ');
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