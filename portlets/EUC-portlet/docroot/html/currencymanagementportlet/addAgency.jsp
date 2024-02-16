<%--
/**
* Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
*
* This library is free software; you can redistribute it and/or modify it under
* the terms of the GNU Lesser General Public License as published by the Free
* Software Foundation; either version 2.1 of the License, or (at your option)
* any later version.
*
* This library is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
* details.
*/
--%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>

<%@ page import="javax.portlet.PortletPreferences" %>

<portlet:defineObjects />


<portlet:actionURL var="newCurrencyURL" name="addAgencyAction">
    <portlet:param name="mvcPath" value="/html/currencymanagementportlet/addAgency.jsp" />
</portlet:actionURL>

<div style="padding-left:10px; padding-right:10px; width:900px">
	<div class="panel  panel-primary">
	  <div class="panel-heading"><span style="color:white; font-weight: bold"></span></div>
	  <div class="panel-body">
	    	
	    <form  id="startRegFormId" action="<%=newCurrencyURL%>" method="post" enctype="application/x-www-form-urlencoded">
	     
		  <div style="padding-bottom:5px">
		  	<fieldset>
		  		<legend>New Agency</legend>
		         <div>
			Agency	Name <input class=" form-control" type="text"  name="agencyName" id="agencyName" />
				</div>
				<br/>
		        <div>
			Agency Phone	<input class="form-control" type="text"  name="agencyPhone" id="agencyPhone" />
				</div>
				<br/>
				  <div>
			Agency Email	<input class="form-control" type="text"  name="agencyEmail" id="agencyEmail" />
				</div>
				<br/>
				  <div>
			Agency Type	<select  name="agencyType" id="agencyType">
			<option value="ACCREDITOR">Accreditors</option>
			<option value="REGULATOR">Regulators</option>
			<option value="INFORMATION">Information</option>
			</select>
				</div>
				
				<br/>
				 <div>
			Contact Name	<input class="form-control" type="text"  name="contactName" id="contactName" />
				</div>
		  	</fieldset>
	      </div>
		  
	      <div>
	      	<button type="submit" class="btn btn-success" style="float:right">Save</button>
	      </div>
	    </form>
	  </div>
	</div>
</div>


<portlet:renderURL var="viewCurrencyURL">
    <portlet:param name="mvcPath" value="/html/currencymanagementportlet/view.jsp" />
</portlet:renderURL>
<% String defaultTab = "Agency Management"; %>
<p><a href="<%= viewCurrencyURL %>?tabs1=<%= defaultTab %>">&larr; Back</a></p>