<%@page import="smartpay.entity.enumerations.UserStatus"%>
<%@page import="smartpay.entity.ApplicationWorkflow"%>
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
	<portlet:param name="action"
		value="<%=APPLIST_ACTIONS.ACT_ON_APPLICATION.name()%>" />
</portlet:actionURL>
<div style="padding-left:10px; padding-right:10px; width:900px">
	<h4>Application Management</h4>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Step 1 of 2: Forward Application To An Agency</span></div>
	  <div class="panel-body">
	<form  id="startRegFormId" action="<%=proceed%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
                            


                            <div class="panel panel-default">
                                <div class="panel-body">
                                    <div class="row">
                                        <div class="col-lg-5">
                                            <div class="form-group">
                                                Forward This Application to Agency(s):
                                                <div>To select more than one agency, hold Shit or Ctrl keys while clicking on agencies</div>
                                                  <select onselect="javascript:handleforward()" name="agency" id="agency" multiple="multiple" size="4" class="form-control">
								<%
								PortalUser p = portletState.getApplicationWorkflow().getApplication().getApplicant().getPortalUser()==null ? 
										portletState.getApplicationWorkflow().getApplication().getPortalUser() : 
											portletState.getApplicationWorkflow().getApplication().getApplicant().getPortalUser();
								if(p.getStatus().equals(UserStatus.USER_STATUS_ACTIVE))
								{
									for(Iterator<Agency> iter = portletState.getAgencyList().iterator(); iter.hasNext();)
									{
										
										Agency agency = iter.next();
										if(!agency.getAgencyType().equals(AgencyType.INFORMATION_GROUP) && 
												!agency.getAgencyType().equals(AgencyType.EXCLUSIVE_GROUP))
										{
											if(!agency.getAgencyType().getValue().equals(AgencyType.ONSA_GROUP.getValue()))
											{
												String selected = "";
												if(portletState.getAgencyEntity()!=null && portletState.getAgencyEntity().getId()==(agency.getId()))
												{
													selected = "selected='selected'";
												}
												
												String agType= agency.getAgencyType().getValue().equalsIgnoreCase("ACCREDITOR") ? "VETTING" : agency.getAgencyType().getValue();
												if(portletState.getAgenciesAlreadyHandled()!=null)
												{
													if(portletState.getAgenciesAlreadyHandled().contains(agency))
													{
														
													}else
													{
													%>
														<option <%=selected%> value="<%=agency.getId()%>"><%=agency.getAgencyName()%> - <%=agType%></option>
													<%
													}
													
												}else if(portletState.getAgenciesAlreadyHandled()==null)
												{
												%>
													<option <%=selected%> value="<%=agency.getId()%>"><%=agency.getAgencyName()%> - <%=agType%></option>
												<%			
												}
											}
									
										}
									}
								}
								%>
								</select>
                                                
                                                
                                            </div>
                                                <div class="form-group">
                                                    Add Comments:
                                                    <%
                                                    if(p.getStatus().equals(UserStatus.USER_STATUS_ACTIVE))
                    								{
                                                    %>
                            						  <textarea name="comments" id="comments" class="form-control" placeholder="Provide Comments if necessary"><%=portletState.getCommentsForward()==null ? "" : portletState.getCommentsForward()%></textarea>
                            						<%
                    								}else
                    								{
                   									%>
                              						  <textarea readonly="readonly" name="comments" id="comments" class="form-control" placeholder="Provide Comments if necessary"><%=portletState.getCommentsForward()==null ? "" : portletState.getCommentsForward()%></textarea>
                              						<%	
                    								}
                            						%>
                                                </div>
                                        </div>
                                        
                                        
                                        
                                        <%
                                        boolean x=false;
                                        int c = 0;
                                        if(portletState.getAlreadyForwardedTo()==null)
                                        {
                                        	x=true;
                                        }else
                                        {
                                        	if(portletState.getAlreadyForwardedTo()!=null && portletState.getAlreadyForwardedTo().size()>0)
                                        	{
                                        		for(Iterator<ApplicationWorkflow> it_1 = portletState.getAlreadyForwardedTo().iterator(); it_1.hasNext();)
                                        		{
                                        			ApplicationWorkflow awpf = it_1.next();
                                        			if(awpf.getWorkedOn()!=null && awpf.getWorkedOn().equals(Boolean.TRUE))
                                        				c++;
                                        		}
                                        		
                                        		if(c>0 && c==portletState.getAlreadyForwardedTo().size())
                                        		{
                                        			x=true;
                                        		}
                                        	}else
                                        	{
                                        		x=true;
                                        	}
                                        }
                                        
                                        if(x==true)
                                        {
                                        %>
                                        
                                        <div style="font-size:18px; padding-top:15px; padding-bottom:15px;clear:both">OR</div>
                                        
                                        <div class="col-lg-5">
                                            <div class="form-group">
                                                Forward This Application to ONSA:
                                                <select name="agencyonsa" id="agencyonsa" class="form-control" onselect="javascript:handleforward()">
                                                	<option value="-1">Select One-</option>
												<%
												for(Iterator<Agency> iter = portletState.getAgencyList().iterator(); iter.hasNext();)
												{
													
													Agency agency = iter.next();
													if(agency.getAgencyType().getValue().equals(AgencyType.ONSA_GROUP.getValue()))
													{
														String selected = "";
														if(portletState.getAgencyEntity()!=null && portletState.getAgencyEntity().getId()==(agency.getId()))
														{
															selected = "selected='selected'";
														}
														%>
														<option <%=selected%> value="<%=agency.getId()%>"><%=agency.getAgencyName()%> - <%=agency.getAgencyType().getValue()%></option>
														<%
															
													}
												}
												%>
												</select>
                                            </div>
                                                <div class="form-group">
                                                    Add Comments:
                            						  <textarea name="commentsonsa" id="commentsonsa" class="form-control" placeholder="Provide Comments if necessary"><%=portletState.getCommentsONSAForward()==null ? "" : portletState.getCommentsONSAForward()%></textarea>
                                                </div>
                                        </div>
                                        <%
                                        }
                                        %>

                                        
                                    </div>
                                    <!-- .panel-body -->
                                </div>
                            </div>
							<div class="panel panel-default">
								
                                <input type="hidden" name="act" value="" id="act">
                                <input type="hidden" name="actId" value="<%=portletState.getApplicationWorkflow()==null ? "" : portletState.getApplicationWorkflow().getId() %>" id="actId">
                                <button type="submit" name="btnSaveApplication" id="SaveApplication" class="btn btn-danger btn-cons" style="float:left" onClick="javascript:submitForm('cancelfwdstepone')">
                                Cancel</button>
                                <button type="submit" name="btnSaveApplication" id="SaveApplication" class="btn btn-success btn-cons" style="float:right" onClick="javascript:submitForm('fwdtoagencystepone')">
                                Next</button>
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