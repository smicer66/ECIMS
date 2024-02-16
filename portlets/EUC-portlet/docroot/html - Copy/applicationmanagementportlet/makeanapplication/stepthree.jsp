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
<%@page import="smartpay.entity.enumerations.AttachmentTypeConstant"%>
<%@page import="smartpay.entity.Country"%>
<%@page import="smartpay.entity.State"%>
<%@page import="smartpay.entity.enumerations.KinRelationshipType"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.enumerations.RoleConstants"%>
<%@page import="smartpay.entity.RoleType"%>
<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.entity.PortCode"%>

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



<div class="list-group-item">
	<form  id="startRegFormId" action="<%=proceed%>" method="post" enctype="multipart/form-data" style="padding:10px">

                            

                            <div class="row">
                                Step 3 of 4: Supporting Documents & Importation details
                            </div>
                            
                            <div class="panel panel-default">
                                <div class="panel-heading">
                                    SUPPORTING DOCUMENTS                    
                                </div>
                                <div class="panel-body">
                                    <div class="row">
                                        <div class="col-lg-5">
                                            <div class="form-group file">
                                                Attachment Type:
                                                        <table id="ContentPlaceHolder1_dlAttachType" cellspacing="0" style="border-collapse:collapse;">
	<tbody><tr>
															<td class="table table-bordered no-more-tables">
															<%
															int c=0;
															
															if(portletState.getAttachmentTypeListByHSCode()!=null && portletState.getAttachmentTypeListByHSCode().contains(AttachmentTypeConstant.NPF_FIREWORKS_PERMIT))
															{
																if(c%2==0)
																{
																%>
																	<div style="clear:both;">&nbsp;</div>
																<%
																}
																c++;
																%>
                                                                <div style="padding: 10px; width:50%; float:left">
                                                                    <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_0">NPF FIREWORKS PERMIT</div>
                                                                    <input type="file" name="FileAttachUploadNpfFireworksPermit" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_0">
                                                                </div>
                                                            <%
															}
                                                            
															if(portletState.getAttachmentTypeListByHSCode()!=null && portletState.getAttachmentTypeListByHSCode().contains(AttachmentTypeConstant.YEARS_OF_OPERATION))
															{
																if(c%2==0)
																{
																%>
																	<div style="clear:both;">&nbsp;</div>
																<%
																}
																c++;
																%>
                                                                <div style="padding: 10px; float:left; width:50%; ">
                                                                    <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_4">YEARS OF OPERATION/EXPERIENCE</div>
                                                                    <input type="file" name="FileAttachUploadYearsOfOperation" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_4">
                                                                </div> 
															
															<%
															}
															if(portletState.getAttachmentTypeListByHSCode()!=null && portletState.getAttachmentTypeListByHSCode().contains(AttachmentTypeConstant.MANUFACTURERS_SAFETY))
															{
																if(c%2==0)
																{
																%>
																	<div style="clear:both;">&nbsp;</div>
																<%
																}
																c++;
																%>
                                                                <div style="padding: 10px; width:50%; float: left">
                                                                    <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_7">MANUFACTURERS SAFETY DATASHEET</div>
                                                                    <input type="file" name="FileAttachUploadManufacturersSafety" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_7">
                                                                </div>
															
															<%
															}
															if(portletState.getAttachmentTypeListByHSCode()!=null && portletState.getAttachmentTypeListByHSCode().contains(AttachmentTypeConstant.NAFDAC_ATTACHMENT))
															{
																if(c%2==0)
																{
																%>
																	<div style="clear:both;">&nbsp;</div>
																<%
																}
																c++;
																%>
                                                                <div style="padding: 10px; float:left; width:50%; ">
                                                                    <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_1">NAFDAC CHEMICAL IMPORTS PERMIT</div>
                                                                    <input type="file" name="FileAttachUploadNafdac" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_1">
                                                                </div>
                                                                
															<%
															}
															if(portletState.getAttachmentTypeListByHSCode()!=null && portletState.getAttachmentTypeListByHSCode().contains(AttachmentTypeConstant.STORAGE_FACILITY))
															{
																if(c%2==0)
																{
																%>
																	<div style="clear:both;">&nbsp;</div>
																<%
																}
																c++;
																%>
                                                                <div style="padding: 10px; width:50%; float: left">
                                                                    <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_5">STORAGE FACILITY DETAILS</div>
                                                                    <input type="file" name="FileAttachUploadStorageFacility" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_5">
                                                                </div>
															
															<%
															}
															if(portletState.getAttachmentTypeListByHSCode()!=null && portletState.getAttachmentTypeListByHSCode().contains(AttachmentTypeConstant.PREVIOUS_STORAGE_ATTACHMENT))
															{
																if(c%2==0)
																{
																%>
																	<div style="clear:both;">&nbsp;</div>
																<%
																}
																c++;
																%>
                                                                <div style="padding: 10px; float:left; width:50%; ">
                                                                    <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_8">PREVIOUS STORAGE STOCK DETAILS</div>
                                                                    <input type="file" name="FileAttachUploadPreviousStorage" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_8">
                                                                </div>
															
															<%
															}
															if(portletState.getAttachmentTypeListByHSCode()!=null && portletState.getAttachmentTypeListByHSCode().contains(AttachmentTypeConstant.REGISTRATION_WITH_FFD))
															{
																if(c%2==0)
																{
																%>
																	<div style="clear:both;">&nbsp;</div>
																<%
																}
																c++;
																%>
                                                                <div style="padding: 10px; width:50%; float: left">
                                                                    <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_2">REGISTRATION WITH FFD</div>
                                                                    <input type="file" name="FileAttachUploadRegistrationWithFfd" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_2">
                                                                </div>
															
															<%
															}
															if(portletState.getAttachmentTypeListByHSCode()!=null && portletState.getAttachmentTypeListByHSCode().contains(AttachmentTypeConstant.SPECIFICATION_OF_GOODS))
															{
																if(c%2==0)
																{
																%>
																	<div style="clear:both;">&nbsp;</div>
																<%
																}
																c++;
																%>
                                                                <div style="padding: 10px; float:left; width:50%; ">
                                                                    <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_6">DETAILS SPECIFICATION OF GOODS</div>
                                                                    <input type="file" name="FileAttachUploadSpecOfGoods" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_6">
                                                                </div>
															
															<%
															}
															if(portletState.getAttachmentTypeListByHSCode()!=null && portletState.getAttachmentTypeListByHSCode().contains(AttachmentTypeConstant.TRANSIT_DETAILS))
															{
																if(c%2==0)
																{
																%>
																	<div style="clear:both;">&nbsp;</div>
																<%
																}
																c++;
																%>
                                                                <div style="padding: 10px; width:50%; float: left">
                                                                    <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_9">TRANSIT DETAILS DESTINATION AND TRANSIT VEHILCE DETAILS</div>
                                                                    <input type="file" name="FileAttachUploadTransitDetails" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_9">
                                                                </div>
															
															<%
															}
															if(portletState.getAttachmentTypeListByHSCode()!=null && portletState.getAttachmentTypeListByHSCode().contains(AttachmentTypeConstant.IDENTIFICATION_CARDS))
															{
																if(c%2==0)
																{
																%>
																	<div style="clear:both;">&nbsp;</div>
																<%
																}
																c++;
																%>
                                                                <div style="padding: 10px; float:left; width:50%; ">
                                                                    <div id="ContentPlaceHolder1_dlAttachType_lblAttachType_3">IDENTIFICATION CARDS</div>
                                                                    <input type="file" name="FileAttachUploadIdentificationCards" id="ContentPlaceHolder1_dlAttachType_FileAttachUpload_3">
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


                            <div class="panel panel-default">
                                <div class="panel-heading">
                                    <div class="panel-title">
                                        <div class="checkbox checkbox check-success">
                                            <input onchange="javascript:showHide('divImportingfor', 'chkImportingFor')" id="chkImportingFor" <%=portletState.getChkImportingFor()!=null && portletState.getChkImportingFor()=="1" ? "checked='checked'" : ""%> type="checkbox" name="chkImportingFor" value="1"><label for="chkImportingFor">Importing for:</label>
                                        </div>
                                    </div>
                                </div>
                                <div class="panel-body">
                                    <div class="row">
                                        <div class="col-lg-5" id="divImportingfor" style="display: none;">

                                            <div class="form-group">
                                                Name:
                            <input name="txtImportName" value="<%=portletState.getImportName()!=null ? portletState.getImportName() : ""%>" type="text" id="ImportName" class="form-control" placeholder="Name">
                                            </div>
                                            <div class="form-group">
                                                Address:
                            <textarea name="txtImportAddress" type="text" id="ImportAddress" class="form-control" placeholder="Address"><%=portletState.getTxtImportAddress()!=null ? portletState.getTxtImportAddress() : ""%></textarea>
                                            </div>
                                            <div class="form-group">
                                                Attach Contract Award Letter:
                    <input type="file" name="FileUploadAwardLetter" id="FileUploadAwardLetter" class="file" data-show-caption="true" data-show-upload="false">
                                            </div>

                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="panel panel-default">
                                <div class="panel-heading">
                                    <div class="panel-title">
                                        <div class="checkbox checkbox check-success">
                                            <input value="1" onchange="javascript:showHide('divProof', 'chkBoxImportDuty')" id="chkBoxImportDuty" <%=portletState.getChkBoxImportDuty()!=null && portletState.getChkBoxImportDuty()=="1" ? "checked='checked'" : ""%> type="checkbox" name="chkBoxImportDuty"><label for="chkBoxImportDuty">Import Duty Exemption applies to this Application</label>
                                        </div>
                                    </div>
                                </div>
                                <div class="panel-body">
                                    <div class="row">
                                        
                                        <div class="row">
                                            <div id="divProof" style="display: none;">
                                                <div class="col-lg-5">
                                                    <br>
                                                    <input name="txtProofTitle" value="<%=portletState.getTxtProofTitle()!=null ? portletState.getTxtProofTitle() : ""%>" type="text" id="ProofTitle" class="form-control" placeholder="Proof Title">
                                                </div>

                                                <div class="col-lg-12">
                                                    <br>
                                                    Attach Proof   
                                <input type="file" name="FileUploadProofAttachment" id="FileUploadProofAttachment" class="file" data-show-caption="true" data-show-upload="false">
                                                    <br>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="row">
                                            <div class="col-lg-2">
                                                Expected Port of Landing: 
                                            </div>
                                            
                                            <div class="col-lg-3">
                                                <div class="form-group">
                                                    <select name="PortofLandingPort" id="DDLPortofLandingPort" class="form-control">
	<option value="">Select Port of Landing Port</option>
	<%
	if(portletState.getPortCodeList()!=null && portletState.getPortCodeList().size()>0)
	{
		for(Iterator<PortCode> iter = portletState.getPortCodeList().iterator(); iter.hasNext();)
		{
			PortCode portCode = iter.next();
			String selected = "";
			if(portletState.getPortCodeEntity()!=null && portletState.getPortCodeEntity().getId()==(portCode.getId()))
			{
				selected = "selected='selected'";
			}
		%>
			<option <%=selected%> value="<%=portCode.getId()%>"><%=portCode.getState().getName() + " - Port Code (" + portCode.getPortCode() + ")"%></option>
		<%
		}
	}
	%>
	%>

</select>
                                        
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="panel panel-default">
                            	<input type="hidden" value="" name="act" id="act">
                                <input type="submit" value="Go Back" id="SaveApplication" class="btn btn-danger btn-cons" style="float:left" onclick="javascript:submitForm('back')">
                                <input type="submit" value="Next" id="SaveApplication" class="btn btn-success btn-cons" style="float:right" onclick="javascript:submitForm('next')">
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