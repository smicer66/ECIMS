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
		value="<%=EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_TWO.name()%>" />
</portlet:actionURL>



<div class="list-group-item">
	<form  id="startRegFormId" action="<%=proceed%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
                            

                            <div class="row">
                                Step 2 of 4
                            </div>

                            <div class="panel panel-default">
                                <div class="panel-heading">
                                    APPLICATION DETAILS                    
                                </div>
                                <div class="panel-body">
                                    
                                    <div class="row">
                                        <div class="col-lg-14">
                                            <br>
                                            Items Description : 
                                        </div>
                                        <div class="row" style="padding: 16px">
                                            <div class="col-lg-6">
                                                HS Code
                                <select name="HSCode" id="HSCode" class="form-control">
									<option value="-1">Select HS Code</option>
									<%
									if(portletState.getItemCategorySubList()!=null && portletState.getItemCategorySubList().size()>0)
									{
										for(Iterator<ItemCategorySub> iter = portletState.getItemCategorySubList().iterator(); iter.hasNext();)
										{
											ItemCategorySub itemCategorySub = iter.next();
											String selected = "";
											if(portletState.getItemCategorySubEntity()!=null && portletState.getItemCategorySubEntity().getId()==(itemCategorySub.getId()))
											{
												selected = "selected='selected'";
											}
										%>
											<option <%=selected%> value="<%=itemCategorySub.getId()%>"><%=itemCategorySub.getName()%></option>
										<%
										}
									}
									%>
								</select>
                                                
                                            </div>

                                            <div class="col-lg-6">
                                                Item Description
                                                <input name="txtDescription" value="<%=portletState.getDescription()==null ? "" : portletState.getDescription()%>" type="text" id="Description" class="form-control" placeholder="Description">
                                                
                                            </div>
                                        </div>
										<%
										if(portletState.getItemCategoryEntity()!=null && 
										portletState.getItemCategoryEntity().getShowQuantityYes()!=null && 
										portletState.getItemCategoryEntity().getShowQuantityYes().equals(Boolean.TRUE))
										{
										%>
											<div class="col-lg-2">
												<input name="txtQuantity" value="<%=portletState.getQuantity()==null ? "" : portletState.getQuantity()%>" type="text" id="Quantity" title="Quantity" class="form-control" placeholder="Quantity">
											</div>
											<div class="col-lg-2">
												<select name="txtQuantityUnit" id="txtQuantityUnit" class="form-control" style="width:65px;">
												<%
										if(portletState.getQuantityUnitList()!=null && portletState.getQuantityUnitList().size()>0)
										{
											for(Iterator<QuantityUnit> iter = portletState.getQuantityUnitList().iterator(); iter.hasNext();)
											{
												QuantityUnit quantityUnit = iter.next();
												String selected = "";
												if(portletState.getQuanityUnitEntity()!=null && portletState.getQuanityUnitEntity().getId()==(quantityUnit.getId()))
												{
													selected = "selected='selected'";
												}
											%>
												<option <%=selected%> value="<%=quantityUnit.getId()%>"><%=quantityUnit.getUnit()%></option>
											<%
											}
										}
										%>
												</select>
											</div>
                                        <%
										}
										if(portletState.getItemCategoryEntity()!=null && 
										portletState.getItemCategoryEntity().getShowQuantityYes()!=null && 
										portletState.getItemCategoryEntity().getShowQuantityYes().equals(Boolean.FALSE))
										{
										%>
											<div class="col-lg-2">
												<input name="txtWeight" value="<%=portletState.getWeight()==null ? "" : portletState.getWeight()%>" type="text" id="Weight" title="Wieght" class="form-control" placeholder="Weight">
											</div>
											<div class="col-lg-2">
                                            <select name="txtWeightUnit" id="txtWeightUnit" class="form-control" style="width:65px;">
											<%
											if(portletState.getWeightUnitList()!=null && portletState.getWeightUnitList().size()>0)
											{
												for(Iterator<WeightUnit> iter = portletState.getWeightUnitList().iterator(); iter.hasNext();)
												{
													WeightUnit weightUnit = iter.next();
													String selected = "";
													if(portletState.getWeightUnitEntity()!=null && portletState.getWeightUnitEntity().getId()==(weightUnit.getId()))
													{
														selected = "selected='selected'";
													}
												%>
													<option <%=selected%> value="<%=weightUnit.getId()%>"><%=weightUnit.getUnit()%></option>
												<%
												}
											}
									%>
											</select>
                                        </div>
										<%
										}
										%>
                                        <div class="col-lg-2">
                                            <input name="txtAmount" value="<%=portletState.getAmount()==null ? "" : portletState.getAmount()%>" type="text" id="Amount" title="Amount" class="form-control" placeholder="Amount">
                                        </div>
                                        <div class="col-lg-1">
                                            <select name="Currency" id="Currency" class="form-control" style="width:65px;">
											<%
											if(portletState.getCurrencyEntity()!=null)
											{
												
												%>
													<option value="<%=portletState.getCurrencyEntity().getId()%>"><%=portletState.getCurrencyEntity().getHtmlEntity()%></option>
												<%
											}
									%>
											</select>
                                        </div>
                                        <div class="col-lg-1">
                                            <input type="submit" name="btnADD" value="ADD" id="ADD" class="btn btn-primary btn-cons" onclick="javascript:submitForm('Add')" >
                                        </div>
                                    </div>
									
									
									<%
									if(portletState.getApplicationItemList()!=null && portletState.getApplicationItemList().size()>0)
									{
									%>
									<div class="row">
                                        <div class="col-lg-14">
                                            <br>
                                            Items Description : 
                                        </div>
                                        <div class="row" style="padding: 16px">
                                            <div class="col-lg-6">
                                            	<table width="100%">
                                            	
                                            		<th>
	                                            		<tr>
	                                            			<td>HS Code</td>
	                                            			<td>Item Description</td>
	                                            			<td>Quantity</td>
	                                            			<td>Weight</td>
	                                            			<td>Amount</td>
	                                            			<td>Currency</td>
	                                            		</tr>
                                            		</th>
                                            		<tbody>
                                            <%
                                            
                                            for(Iterator<ApplicationItem> iter = portletState.getApplicationItemList().iterator(); iter.hasNext();)
                                            {
                                            	ApplicationItem applicationItem = iter.next();
                                            	//
                                            	
                                            %>
                                            	<tr>
                                           			<td><%=applicationItem.getItemCategorySub()==null ? "N/A" : applicationItem.getItemCategorySub().getHsCode() %></td>
                                           			<td><%=applicationItem.getDescription()==null ? "N/A" : applicationItem.getDescription()%></td>
                                           			<td><%=applicationItem.getQuantity()==null ? "N/A" : applicationItem.getQuantity()%></td>
                                           			<td><%=applicationItem.getWeight()==null ? "N/A" : applicationItem.getWeight()%></td>
                                           			<td><%=applicationItem.getAmount()==null ? "N/A" : applicationItem.getAmount()%></td>
                                           			<td><%=applicationItem.getCurrency()!=null ? applicationItem.getCurrency().getHtmlEntity() : "" %></td>
                                           		</tr>
                                            	
                                            <%
                                            	amount += applicationItem.getAmount()==null ? 0.00 : applicationItem.getAmount();
                                            }
                                            %>
                                            		</tbody>
                                            	</table>                                                
                                            </div>
                                        </div>
                                    </div>
                                    <%
									}
                                    %>
                                    
                                    
                                    <div class="row">
                                        <div class="form-group">
                                            <div>

</div>
                                        </div>
                                        <div class="col-lg-2">
                                            <div class="form-group">
                                                Amount
                                <input name="txtItemValue" value="<%=amount%>" type="text" readonly="readonly" id="ItemValue" class="form-control" placeholder="Value">
                                            </div>
                                        </div>
                                        <div class="col-lg-10">
                                            <div class="form-group">
                                                Amount In Word
                                <input name="txtItemValueInword" id="txtItemValueInword" value="<%=new Util().convertNumberToWords(amount) %>" type="text" readonly="readonly" id="ItemValueInword" class="form-control" placeholder="Value">
                                            </div>
                                        </div>
                                    </div>
                                    <!-- .panel-body -->
                                </div>
                            </div>
							<div class="panel panel-default">
								<input type="hidden" name="act" id="act" value="">
                                <input type="submit" name="btnSaveApplication" value="Go Back" id="SaveApplication" class="btn btn-danger btn-cons" style="float:left" onclick="javascript:submitForm('back')">
                                <input type="submit" name="btnSaveApplication" value="Next" id="SaveApplication" class="btn btn-success btn-cons" style="float:right" onclick="javascript:submitForm('next')">
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