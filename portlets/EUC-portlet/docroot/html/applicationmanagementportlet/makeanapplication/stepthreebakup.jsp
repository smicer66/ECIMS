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
                                        <div class="col-lg-5">
                                            <div class="form-group file">
                                                <strong>Please attach the follow documents:</strong>
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
                                    <div style="padding: 10px; width:100%; float:left">
                                        <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_0"><%=atlItStr.toUpperCase() %></div>
                                        <input type="file" name="FileAttachUpload<%=atlItStr.replace(" ", "").toLowerCase() %>" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_0">
                                    </div>
                                	<%
							}
							
							//if(portletState.getAttachmentTypeListByHSCode()!=null && portletState.getAttachmentTypeListByHSCode().contains(AttachmentTypeConstant.NPF_FIREWORKS_PERMIT))
							//{
								if(c%2==0)
								{
								%>
									<div style="clear:both;">&nbsp;</div>
								<%
								}
								c++;
								%>
                                                        <div style="padding: 10px; width:100%; float:left">
                                                            <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_0">NPF FIREWORKS PERMIT</div>
                                                            <input type="file" name="FileAttachUploadNpfFireworksPermit" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_0">
                                                        </div>
                                                    <%
							//}
                                                    
							//if(portletState.getAttachmentTypeListByHSCode()!=null && portletState.getAttachmentTypeListByHSCode().contains(AttachmentTypeConstant.YEARS_OF_OPERATION))
							//{
								if(c%2==0)
								{
								%>
									<div style="clear:both;">&nbsp;</div>
								<%
								}
								c++;
								%>
                                                        <div style="padding: 10px; float:left; width:100%; ">
                                                            <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_4">YEARS OF OPERATION/EXPERIENCE</div>
                                                            <input type="file" name="FileAttachUploadYearsOfOperation" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_4">
                                                        </div> 
							
							<%
							//}
							//if(portletState.getAttachmentTypeListByHSCode()!=null && portletState.getAttachmentTypeListByHSCode().contains(AttachmentTypeConstant.MANUFACTURERS_SAFETY))
							//{
								if(c%2==0)
								{
								%>
									<div style="clear:both;">&nbsp;</div>
								<%
								}
								c++;
								%>
                                                        <div style="padding: 10px; width:100%; float: left">
                                                            <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_7">MANUFACTURERS SAFETY DATASHEET</div>
                                                            <input type="file" name="FileAttachUploadManufacturersSafety" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_7">
                                                        </div>
							
							<%
							//}
							//if(portletState.getAttachmentTypeListByHSCode()!=null && portletState.getAttachmentTypeListByHSCode().contains(AttachmentTypeConstant.NAFDAC_ATTACHMENT))
							//{
								if(c%2==0)
								{
								%>
									<div style="clear:both;">&nbsp;</div>
								<%
								}
								c++;
								%>
                                                        <div style="padding: 10px; float:left; width:100%; ">
                                                            <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_1">NAFDAC CHEMICAL IMPORTS PERMIT</div>
                                                            <input type="file" name="FileAttachUploadNafdac" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_1">
                                                        </div>
                                                        
							<%
							//}
							//if(portletState.getAttachmentTypeListByHSCode()!=null && portletState.getAttachmentTypeListByHSCode().contains(AttachmentTypeConstant.STORAGE_FACILITY))
							//{
								if(c%2==0)
								{
								%>
									<div style="clear:both;">&nbsp;</div>
								<%
								}
								c++;
								%>
                                                        <div style="padding: 10px; width:100%; float: left">
                                                            <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_5">STORAGE FACILITY DETAILS</div>
                                                            <input type="file" name="FileAttachUploadStorageFacility" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_5">
                                                        </div>
							
							<%
							//}
							//if(portletState.getAttachmentTypeListByHSCode()!=null && portletState.getAttachmentTypeListByHSCode().contains(AttachmentTypeConstant.PREVIOUS_STORAGE_ATTACHMENT))
							//{
								if(c%2==0)
								{
								%>
									<div style="clear:both;">&nbsp;</div>
								<%
								}
								c++;
								%>
                                                        <div style="padding: 10px; float:left; width:100%; ">
                                                            <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_8">PREVIOUS STORAGE STOCK DETAILS</div>
                                                            <input type="file" name="FileAttachUploadPreviousStorage" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_8">
                                                        </div>
							
							<%
							//}
							//if(portletState.getAttachmentTypeListByHSCode()!=null && portletState.getAttachmentTypeListByHSCode().contains(AttachmentTypeConstant.REGISTRATION_WITH_FFD))
							//{
								if(c%2==0)
								{
								%>
									<div style="clear:both;">&nbsp;</div>
								<%
								}
								c++;
								%>
                                                        <div style="padding: 10px; width:100%; float: left">
                                                            <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_2">REGISTRATION WITH FFD</div>
                                                            <input type="file" name="FileAttachUploadRegistrationWithFfd" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_2">
                                                        </div>
							
							<%
							//}
							//if(portletState.getAttachmentTypeListByHSCode()!=null && portletState.getAttachmentTypeListByHSCode().contains(AttachmentTypeConstant.SPECIFICATION_OF_GOODS))
							//{
								if(c%2==0)
								{
								%>
									<div style="clear:both;">&nbsp;</div>
								<%
								}
								c++;
								%>
                                                        <div style="padding: 10px; float:left; width:100%; ">
                                                            <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_6">DETAILS SPECIFICATION OF GOODS</div>
                                                            <input type="file" name="FileAttachUploadSpecOfGoods" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_6">
                                                        </div>
							
							<%
							//}
							//if(portletState.getAttachmentTypeListByHSCode()!=null && portletState.getAttachmentTypeListByHSCode().contains(AttachmentTypeConstant.TRANSIT_DETAILS))
							//{
								if(c%2==0)
								{
								%>
									<div style="clear:both;">&nbsp;</div>
								<%
								}
								c++;
								%>
                                                        <div style="padding: 10px; width:100%; float: left">
                                                            <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_9">TRANSIT DETAILS DESTINATION AND TRANSIT VEHILCE DETAILS</div>
                                                            <input type="file" name="FileAttachUploadTransitDetails" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_9">
                                                        </div>
							
							<%
							//}
							//if(portletState.getAttachmentTypeListByHSCode()!=null && portletState.getAttachmentTypeListByHSCode().contains(AttachmentTypeConstant.IDENTIFICATION_CARDS))
							//{
								if(c%2==0)
								{
								%>
									<div style="clear:both;">&nbsp;</div>
								<%
								}
								c++;
								%>
                                                        <div style="padding: 10px; float:left; width:100%; ">
                                                            <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_3">IDENTIFICATION CARDS</div>
                                                            <input type="file" name="FileAttachUploadIdentificationCards" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_3">
                                                        </div>
                                                    <%
							//}
                                                    %>
                                                        	</td>
	</tr>
</tbody></table>

                                            </div>

                                            



                                            
                                        </div>
                                        
                                        
                                    </div>

                                </div>
                                
                            </div>


                            <div class="panel panel-default">
                                <div class="panel-heading">
                                	<strong><input onchange="javascript:showHide('divImportingfor', 'chkImportingFor')" id="chkImportingFor" <%=portletState.getChkImportingFor()!=null && portletState.getChkImportingFor().equals("1") ? "checked='checked'" : ""%> type="checkbox" name="chkImportingFor" value="1"><label for="chkImportingFor">&nbsp;&nbsp;&nbsp;Fill this section if making this application on behalf of an organization:</strong></label>
                                </div>
                                <div class="panel-body">
                                    <div class="row">
                                    <%
                                    String display = "none";
                                    if(portletState.getPortalUser().getAgency()!=null)
                                    {
                                    	display = "block";
                                    }
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