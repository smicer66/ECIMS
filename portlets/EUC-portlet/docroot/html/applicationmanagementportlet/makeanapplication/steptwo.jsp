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
<script src="<%= resourceBaseURL %>/js/app.plugin.js"></script>
<style type="text/css">
.formwidth50{
	width:300px;
}
.required{
	color:red;
	
}
.h5
{
	font-size:26px;
}
</style>

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

String currn = "";
if(portletState.getApplicationItemList()!=null && portletState.getApplicationItemList().size()>0)
{
	for(Iterator<ApplicationItem> iter = portletState.getApplicationItemList().iterator(); iter.hasNext();)
	{
		ApplicationItem applicationItem = iter.next();
		amount += applicationItem.getAmount()==null ? 0.00 : applicationItem.getAmount();
		currn = applicationItem.getCurrency().getName();
	}
}

Boolean req = Boolean.FALSE;
if(portletState.getApplicationItemList()!=null && portletState.getApplicationItemList().size()>0)
{
	req = Boolean.TRUE;
}
%>

<portlet:actionURL var="proceed" name="processAction">
	<portlet:param name="action"
		value="<%=EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_TWO.name()%>" />
</portlet:actionURL>



<section class="container">
<div style="padding-left:10px; padding-right:10px;" class="col-sm-9 panel panel-default" role="news">
	<h4>Create An EUC Application Request</h4>
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold">Step 2 of 4: List Your Items</span></div>
	  <div class="panel-body">
	<form  id="startRegFormId" data-validate="parsley" action="<%=proceed%>" method="post" enctype="application/x-www-form-urlencoded" style="padding:10px">
                            
                            <div class="panel">
                                <div class="panel-body">
                                    
                                    <div class="row">
                                        <div class="row" style="padding-left: 10px">
                                            <br>
                                            <h5>Items Description :</h5> 
                                        </div>
                                        <div class="row" style="padding: 10px">
                                            <div class="col-lg-12">
                                                <strong>HS Code<span style="color:red">*</span></strong>
                                <select style="font-size:11px; width:100%" <%if(req.equals(Boolean.FALSE)){ %> data-required="true" <%} %> name="HSCode" id="HSCode" class="form-control">
									<option value="-1" style="font-size:11px;">Select HS Code</option>
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
											<option style="font-size:11px;" <%=selected%> value="<%=itemCategorySub.getId()%>"><%=itemCategorySub.getHsCode() + " - " + itemCategorySub.getName()%></option>
										<%
										}
									}
									%>
								</select>
                                                
                                            </div>

                                            <div class="col-lg-12">
                                                <strong>Item Description<span style="color:red">*</span></strong>
                                                <textarea <%if(req.equals(Boolean.FALSE)){ %> data-required="true" <%} %> name="txtDescription" style="width:100%; height:100px" id="Description" class="form-control" placeholder="Description"><%=portletState.getDescription()==null ? "" : portletState.getDescription()%></textarea>
                                                
                                            </div>
                                        </div>
										
											<div class="col-lg-4">
                                                <strong>Quantity/Weight<span style="color:red">*</span></strong>
												<input <%if(req.equals(Boolean.FALSE)){ %> data-required="true" <%} %> name="txtQuantity" value="<%=portletState.getQuantity()==null ? "" : portletState.getQuantity()%>" type="text" id="Quantity" title="Quantity" class="form-control" placeholder="Quantity/Weight">
											</div>
											<div class="col-lg-4">
                                                <strong>Quantity/Weight Unit<span style="color:red">*</span></strong>
												<select <%if(req.equals(Boolean.FALSE)){ %> data-required="true" <%} %> name="txtQuantityUnit" id="txtQuantityUnit" class="form-control" style="width:90px;">
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
                                        
                                        <div class="col-lg-4">
                                        	<strong>Unit Cost (<%=portletState.getCurrencyEntity().getHtmlEntity() %>)<span style="color:red">*</span></strong>
											<input <%if(req.equals(Boolean.FALSE)){ %> data-required="true" <%} %> name="txtAmount" value="<%=portletState.getAmount()==null ? "" : portletState.getAmount()%>" onkeypress="return onlyDoubleKey(event, 'Amount')" type="text" id="Amount" title="Amount" class="form-control" placeholder="Unit Cost">
                                        </div>
                                        
                                        <div class="col-lg-1" style="clear:both; padding-bottom:5px;">
                                            <button type="submit" name="btnADD" id="ADD" class="btn btn-primary btn-cons" onclick="javascript:submitForm('Add')" >Add Item</button>
                                        </div>
                                        
                                    </div>
									
									
									<%
                                    String cur = "";
									if(portletState.getApplicationItemList()!=null && portletState.getApplicationItemList().size()>0)
									{
									%>
									<div class="row">
                                        <div class="col-lg-14">
                                            <br>
                                            <strong>Items Description:</strong> 
                                        </div>
                                        <div class="row" style="padding: 16px">
                                            <div class="col-lg-12">
                                            	<table width="100%" class="table table-hover" id="btable">
                                            	
                                            		<thead>
	                                            		<tr>
	                                            			<th>S/N</th>
	                                            			<th>HS Code</th>
	                                            			<th>Item Description</th>
	                                            			<th>Quantity/Weight</th>
	                                            			<th>Unit Cost</th>
	                                            		</tr>
                                            		</thead>
                                            		<tbody>
                                            <%
                                            int c= 1;
                                            for(Iterator<ApplicationItem> iter = portletState.getApplicationItemList().iterator(); iter.hasNext();)
                                            {
                                            	ApplicationItem applicationItem = iter.next();
                                            	cur = applicationItem.getCurrency().getHtmlEntity();
                                            	//
                                            	
                                            %>
                                            	<tr>
                                            		<td><%=c++ %></td>
                                           			<td><%=applicationItem.getItemCategorySub()==null ? "N/A" : applicationItem.getItemCategorySub().getHsCode() %></td>
                                           			<td><%=applicationItem.getDescription()==null ? "N/A" : applicationItem.getDescription()%></td>
                                           			<td><%=applicationItem.getQuantity()==null ? "N/A" : (applicationItem.getQuantity() + applicationItem.getQuantityUnit().getUnit())%></td>
                                           			<td><%=applicationItem.getCurrency()!=null ? applicationItem.getCurrency().getHtmlEntity() : "" %><%=applicationItem.getAmount()==null ? "N/A" : new Util().roundUpAmount(applicationItem.getAmount())%></td>
                                           		</tr>
                                            	
                                            <%
                                            	//amount += applicationItem.getAmount()==null ? 0.00 : applicationItem.getAmount();
                                            }
                                            %>
                                            <tr>
                                        			<td colspan="4" style="font-weight:bold"><strong>Total Cost :</strong></td>
                                       			<td><%=cur%><%=new Util().roundUpAmount(amount) %></td>
                                        		</tr>
                                            		</tbody>
                                            	</table>   
                                         <%
                                        if(portletState.getApplicationItemList()!=null && portletState.getApplicationItemList().size()>0)
    									{
                                        %>
                                        <div class="col-lg-1" style="clear:both; padding-bottom:5px;">
                                            <button type="submit" name="btnADD" id="CLEAR" class="btn btn-danger btn-cons" onclick="javascript:submitForm('Clear')" >Delete All Added Items</button>
                                        </div>
                                        <%
    									}
                                        %>                                             
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
                                        <div class="col-lg-3">
                                            <div class="form-group">
                                                <strong>Total Cost :</strong>
                                				<div id="ItemValue"><%=portletState.getCurrencyEntity().getHtmlEntity() %><%=new Util().roundUpAmount(amount)%></div>
                                            </div>
                                        </div>
                                        <%
                                        if(portletState.getApplicationItemList()!=null && portletState.getApplicationItemList().size()>0)
    									{
                                        %>
                                        <div class="col-lg-9">
                                            <div class="form-group">
                                                <strong>Total Cost In Word:</strong>
                                <div id="txtItemValueInword"></div>
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
								<input type="hidden" name="act" id="act" value="">
                                <button type="submit" name="btnSaveApplication" id="SaveApplication" class="btn btn-danger btn-cons" style="float:left" onclick="javascript:submitForm('backfromsteptwomakeapplication')">
                                Go Back</button>
                                <%
                                if(portletState.getApplicationItemList()!=null && portletState.getApplicationItemList().size()>0)
								{
                                %>
                                <button type="submit" name="btnSaveApplication" id="SaveApplication" class="btn btn-success btn-cons" style="float:right" onclick="javascript:submitForm('next')">
                                Next</button>
                                <%
								}
                                %>
                            </div>
	</form>
</div>
	  		
	</div>
</div>
<aside class="col-sm-3" role="resources">
	<div><strong style="color:#FF0000">Help Tips</strong></div>
	<div style="padding:top:10px">
		<ul>
			<li>
				<div><strong>Importation Items:</strong></div>
				<div>You must add at least one item before you can proceed to the next step:<br>
					<ol>
						<li>To delete items added previously, click the Delete All Added Items button</li>
						<li>To Go Back, click the back button.</li>
						<li>Currency of transaction can not be changed except you have not added any importation items to this application request</li>
					</ol>
				</div>
				<div><strong>Further Tips:</strong></div>
				<div>
					<ol>
						<li>Item Description: Provide a brief description of the item you are adding</li>
						<li>To Go Back, click the back button.</li>
						<li>Currency of transaction can not be changed except you have not added any importation items to this application request</li>
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
		str += "<%=currn%>";
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
		str += " Cents";
	} 
	//alert(str.replace(/\s+/g,' '));
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
	if(clicked=='backfromsteptwomakeapplication')
	{
		$( '#HSCode' ).parsley( 'removeConstraint', 'required' );
		$( '#Quantity' ).parsley( 'removeConstraint', 'required' );
		$( '#Description' ).parsley( 'removeConstraint', 'required' );
		$( '#txtQuantityUnit' ).parsley( 'removeConstraint', 'required' );
		$( '#Amount' ).parsley( 'removeConstraint', 'required' );
		document.getElementById('act').value=clicked;
		document.getElementById('startRegFormId').submit;
	}else
	{
		document.getElementById('act').value=clicked;
		document.getElementById('startRegFormId').submit;
	}
	
	
}

if (window.jQuery) {  
    // jQuery is loaded 
    var xop = toWords(<%=amount%>);
    //alert(xop);
    document.getElementById('txtItemValueInword').innerHTML = xop;
} else {
    // jQuery is not loaded
	//alert("No");
}


$(document).ready(function() {
    $('#btable').dataTable();
} );
</script>