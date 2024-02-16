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
<script type="text/javascript" charset="utf-8" src="<%=jqueryJsUrl%>"></script>
<script type="text/javascript"
	src="<%=(resourceBaseURL + "/js/jquery.validate.js")%>"></script>
<script type="text/javascript"
	src="<%=(resourceBaseURL + "/js/facebox.js")%>"></script>
<script type="text/javascript"
	src="<%=(resourceBaseURL + "/js/paging.js")%>"></script>
	
	<%
	String jqueryDataTableCssUrl = resourceBaseURL + "/css/jquery.dataTables.css";
	String jqueryDataTableThemeCssUrl = resourceBaseURL + "/css/jquery.dataTables_themeroller.css";
	String jqueryDataTableUrl = resourceBaseURL + "/js/jquery.dataTables.min.js";
	%>
	
<link rel="stylesheet" href='<%=faceboxCssUrl%>' type="text/css" />
<link rel="stylesheet" href='<%=pagingUrl%>' type="text/css" />
<link href="<%=jqueryUICssUrl%>" rel="stylesheet" type="text/css" />
<link href="<%=jqueryDataTableCssUrl%>" rel="stylesheet" type="text/css" />
<script src="<%=resourceBaseURL %>/js/jquery-1.11.1.min.js"></script>
<script src="<%= resourceBaseURL %>/js/app.v1.js"></script>
<!-- parsley -->
<script src="<%= resourceBaseURL %>/js/parsley.min.js"></script>
<script src="<%= resourceBaseURL %>/js/parsley.extend.js"></script>
<script src="<%= resourceBaseURL %> %>/js/app.plugin.js"></script>

<script src="<%= resourceBaseURL %>/js/jquery/parsley.min.js"></script>
<style type="text/css">
.formwidth50{
	width:300px;
}
.required{
	color:red;
	
}
</style>



<style type="text/css">
.parsley-required, .parsley-type, .parsley-equalto{
	color:red;
	
}
</style>


<%

ApplicationManagementPortletState portletState = ApplicationManagementPortletState.getInstance(renderRequest, renderResponse);
Logger log = Logger.getLogger(ApplicationManagementPortletState.class);
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
}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
{
%>
<jsp:include page="/html/applicationmanagementportlet/tabs_agency.jsp" flush="" />
<%
}
%>

<portlet:actionURL var="proceed" name="processAction">
	<portlet:param name="action" value="<%=EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_THREE.name()%>" />
</portlet:actionURL>


<%
String req1 = portletState.getPortalUser().getAgency()!=null ? "data-parsley-validate" : "";
%>

<section class="container">
<div style="padding-left:10px; padding-right:10px;" class="col-sm-9 panel panel-default" role="news">
	<h4>Create An EUC Application Request</h4>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Step 3 of 4: Supporting Documents & Importation details</span></div>
	  <div class="panel-body">
	<form  id="startRegFormId"<%=req1 %> action="<%=proceed%>" method="post" enctype="multipart/form-data" style="padding:10px">

                            
                            <div class="panel panel-default">
                                <div class="panel-heading">
                                    <strong>SUPPORTING DOCUMENTS</strong>
                                </div>
                                <div class="panel-body">
                                    <div class="row">
                                        <div class="col-lg-12">
                                            <div class="form-group file">
                                                <strong>Please attach the documents applicable to your application:</strong>
                                                <table id="" cellspacing="0" style="width:100%">
<tbody><tr>
														<td class="table table-bordered no-more-tables">
							<%
							int c=0;
														
							for(Iterator<String> atlIt = portletState.getAttachmentTypeListByHSCode().iterator(); atlIt.hasNext();)
							{
									String atlItStr = atlIt.next();
									if(c%2==0)
									{
									%>
										<div style="clear:both;">&nbsp;</div>
									<%
									}
									c++;
									%>
                                    <div style="padding: 10px; width:50%; float:left">
                                        <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_0"><%=atlItStr.toUpperCase() %></div>
                                        <input type="file" name="FileAttachUpload<%=atlItStr.replace(" ", "").toLowerCase() %>" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_0">
                                    </div>
                                	<%
							}
							
							%>
                                                        	</td>
	</tr>
</tbody></table>

                                            </div>

                                            



                                            
                                        </div>
                                        
                                        
                                    </div>

                                </div>
                                
                            </div>


							<%
							if(portletState.getPortalUser().getAgency()!=null)
							{
							%>
							<div class="panel panel-default">
                                <div class="panel-heading">
                                	<strong>Fill this section if making this application on behalf of an organization:</strong>
                                </div>
                                <div class="panel-body">
                                    <div class="row">
                                    
                                        <div class="col-lg-5" id="divImportingfor" style="display: block;">

                                            <div class="form-group">
                                                <strong>Name of Organisation:</strong>
                            <input name="txtImportName" value="<%=portletState.getImportName()!=null ? portletState.getImportName() : ""%>" type="text" id="ImportName" class="form-control" placeholder="Name of Organization">
                                            </div>
                                            <div class="form-group">
                                                <strong>Address of Organisation:</strong>
                            <textarea name="txtImportAddress" type="text" id="ImportAddress" class="form-control" placeholder="Address of Organisation"><%=portletState.getTxtImportAddress()!=null ? portletState.getTxtImportAddress() : ""%></textarea>
                                            </div>
                                            <div class="form-group">
                                                <strong>Attach Contract Award Letter:</strong>
                    <input type="file" name="FileUploadAwardLetter" id="FileUploadAwardLetter" class="file" data-show-caption="true" data-show-upload="false">
                                            </div>

                                        </div>
                                    </div>
                                </div>
                            </div>
							<%
							}else
							{
							%>
	                            <div class="panel panel-default">
	                                <div class="panel-heading">
	                                	<strong><input onchange="javascript:showHide('divImportingfor', 'chkImportingFor')"  id="chkImportingFor" 
	                                	<%=portletState.getPortalUser().getAgency()==null ? (portletState.getChkImportingFor()!=null && portletState.getChkImportingFor().equals("1") ? "checked='checked'" : "") : "checked='checked'" %> type="checkbox" name="chkImportingFor" value="1"><label for="chkImportingFor">&nbsp;&nbsp;&nbsp;Fill this section if making this application on behalf of an organization:</strong></label>
	                                </div>
	                                <div class="panel-body">
	                                    <div class="row">
		                                    <%
		                                    String display = "none";
		                                    if(portletState.getChkImportingFor()!=null && portletState.getChkImportingFor().equals("1"))
		                                    {
		                                    	display="block";
		                                    }
		                                    String req = "";
		                                    req = portletState.getPortalUser().getAgency()!=null ? " data-parsley-required=\"true\"" : "";
		                                    %>
	                                        <div class="col-lg-5" id="divImportingfor" style="display: <%=display%>;">
	
	                                            <div class="form-group">
	                                                <strong>Name of Organization:</strong>
	                            <input name="txtImportName" value="<%=portletState.getImportName()!=null ? portletState.getImportName() : ""%>" <%=req %> type="text" id="ImportName" class="form-control" placeholder="Name of Organization">
	                                            </div>
	                                            <div class="form-group">
	                                                <strong>Address of Organisation:</strong>
	                            <textarea name="txtImportAddress" type="text" id="ImportAddress" class="form-control" <%=req %> placeholder="Address of Organisation"><%=portletState.getTxtImportAddress()!=null ? portletState.getTxtImportAddress() : ""%></textarea>
	                                            </div>
	                                            <div class="form-group">
	                                                <strong>Attach Contract Award Letter:</strong>
	                    <input type="file" name="FileUploadAwardLetter" id="FileUploadAwardLetter" class="file" data-show-caption="true" data-show-upload="false">
	                                            </div>
	
	                                        </div>
	                                    </div>
	                                </div>
	                            </div>
	                        <%
	                        }
	                        %>

                           	<input type="hidden" value="" name="act" id="act">
                            <button type="submit" id="SaveApplication" class="btn btn-danger btn-cons" style="float:left" onclick="javascript:submitForm('backfromstepthreemakeapplication')">
                            Go Back</button>
                            <button type="submit" id="SaveApplication" class="btn btn-success btn-cons" style="float:right" onclick="javascript:submitForm('next')">
                            Next</button>
	</form></div>
	  		
	</div>
</div>
<aside class="col-sm-3" role="resources">
	<div><strong style="color:#FF0000">Help Tips</strong></div>
	<div style="padding:top:10px">
		<ul>
			<li>
				<div><strong>Supporting Documents:</strong></div>
				<div>These documents are required by the various agencies responsible for the importation items you are 
				are importing into the country:<br>
					<ol>
						<li>Items in red MUST be provided before you can proceed to the next step</li>
						<li>If you are importing on behalf of an organization, click on the checkbox in the 
						"Select this box if you are Importing on behalf of an Organisation" section then proceed to provide 
						the required information</li>
					</ol>
				</div>
				</div>
			
			</li>
		</ul>
	</div>
</aside>
</section>




<script type="text/javascript" charset="utf-8" src="<%=jqueryDataTableUrl%>"></script>

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


function showHide(divId, divClicked)
{
	if(document.getElementById(divClicked).checked)
	{
		document.getElementById(divId).style.display="block";
	}else
	{
		document.getElementById(divId).style.display="none";
		
	}
}
</script>