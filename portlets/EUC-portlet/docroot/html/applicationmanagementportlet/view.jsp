<%@page import="smartpay.entity.enumerations.PermissionType"%>
<%@page import="smartpay.entity.Application"%>
<%@page import="java.util.ArrayList"%>
<%@page import="smartpay.entity.ApplicationWorkflow"%>
<%@page import="smartpay.entity.enumerations.ApplicationStatus"%>
<%@page import="com.ecims.portlet.applicationmanagement.ApplicationManagementPortletState"%>
<%@page import="com.ecims.portlet.applicationmanagement.ApplicationManagementPortletState.*"%>
<%@page import="com.ecims.commins.Util"%>
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
<%@page import="smartpay.entity.Company"%>
<%@page import="smartpay.entity.enumerations.RoleTypeConstants"%>
<%@page import="smartpay.entity.enumerations.RoleConstants"%>
<%@page import="smartpay.entity.PortalUser"%>
<%@page import="smartpay.entity.Settings"%>

<%@page import="java.text.DateFormat"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>




<portlet:defineObjects />
<%

ApplicationManagementPortletState portletState = ApplicationManagementPortletState.getInstance(renderRequest, renderResponse);
HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest));
String pend = httpReq.getParameter("pendnsa");
String pend1 = httpReq.getParameter("pendnsa1");
String iss = httpReq.getParameter("issnsa");
String end = httpReq.getParameter("endnsa");
String pendag = httpReq.getParameter("pendag");
String apvnsa = httpReq.getParameter("apvnsa");
String appns = httpReq.getParameter("appns");

	if(pend!=null)
	{
		portletState.setApplicationListing(portletState.getApplicationManagementPortletUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_CREATED));
		portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA);
		Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
    			getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
    					portletState.getPortalUser().getRoleType().getId(), 
    					ApplicationStatus.APPLICATION_STATUS_CREATED, false, 
    					portletState.getPortalUser().getAgency());
		portletState.setApplicationWorkFlowListing(appList);
		%>
		<jsp:include page="/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp" flush="" />
		<%
	}else if(apvnsa!=null)
	{
		portletState.setApplicationListing(portletState.getApplicationManagementPortletUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_APPROVED));
		portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA);
		Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
    			getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(portletState.getPortalUser().getRoleType().getId(), 
						ApplicationStatus.APPLICATION_STATUS_APPROVED, false);
		portletState.setApplicationWorkFlowListing(appList);
		%>
		<jsp:include page="/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp" flush="" />
		<%
	}else if(appns!=null)
	{
		Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
				getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
						portletState.getPortalUser().getRoleType().getId(), 
						ApplicationStatus.APPLICATION_STATUS_FORWARDED, false, 
						portletState.getPortalUser().getAgency());
		portletState.setApplicationWorkFlowListing(appList);
		
		ArrayList<Application> lst = new ArrayList<Application>();
		if(appList!=null && appList.size()>0)
		{
			for(Iterator<ApplicationWorkflow> it = appList.iterator(); it.hasNext();)
			{
				ApplicationWorkflow afw = it.next();
				lst.add(afw.getApplication());
			}
		}
		if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER) && portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION))
		{
			portletState.setApplicationListing(lst);
			portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA);
			%>
			<jsp:include page="/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp" flush="" />
		<%
		}
	}
	else if(pend1!=null)
	{
		//portletState.setApplicationListing(portletState.getApplicationManagementPortletUtil().getApplicationsByStatus(ApplicationStatus.A));
		Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
				getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
						portletState.getPortalUser().getRoleType().getId(), 
						ApplicationStatus.APPLICATION_STATUS_FORWARDED, false, 
						portletState.getPortalUser().getAgency());
		portletState.setApplicationWorkFlowListing(appList);

		ArrayList<Application> lst = new ArrayList<Application>();
		if(appList!=null && appList.size()>0)
		{
			for(Iterator<ApplicationWorkflow> it = appList.iterator(); it.hasNext();)
			{
				ApplicationWorkflow afw = it.next();
				lst.add(afw.getApplication());
			}
		}
		if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER) && portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION))
		{
			portletState.setApplicationListing(lst);
			portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA);
			%>
			<jsp:include page="/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp" flush="" />
		<%
		}
	}else if(iss!=null)
	{
		Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
    			getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(portletState.getPortalUser().getRoleType().getId(), 
						ApplicationStatus.APPLICATION_STATUS_APPROVED, false);
    	portletState.setApplicationWorkFlowListing(appList);
		portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_APPLICATIONS_NSA);
		%>
		<jsp:include page="/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp" flush="" />
		<%
	}else if(end!=null)
	{
		ArrayList<Application> al = null;
		Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
				getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
						portletState.getPortalUser().getRoleType().getId(), 
						ApplicationStatus.APPLICATION_STATUS_ENDORSED, 
						false, portletState.getPortalUser().getAgency());
    	portletState.setApplicationWorkFlowListing(appList);
		portletState.setCurrentTab(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_NSA);
		%>
		<jsp:include page="/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp" flush="" />
		<%
	}else if(pendag!=null)
	{
		portletState.setApplicationListing(portletState.getApplicationManagementPortletUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_CREATED));
		Collection<ApplicationWorkflow> apwList = 
				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
				getApplicationWorkFlowByReceipientRoleIdAndNotWorkedOn(portletState.getPortalUser().getRoleType().getId(), false, portletState.getPortalUser().getAgency());
		
		portletState.setApplicationWorkFlowListing(apwList);
    	portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_AG);
		%>
		<jsp:include page="/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp" flush="" />
		<%
	}else
	{
		if(portletState==null)
		{
			
		}
		else if(portletState.getHotChocoloateAllowDip()!=null)
		{
			%>
			<div class="panel panel-danger">You cant create new applications because you are currently blacklisted. To request to be removed from our blacklist, use the ECIMS Help button on the left of your screen to send us a message</div>
			<%
		}
		else if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
		{
		%>
		<jsp:include page="/html/applicationmanagementportlet/makeanapplication/stepone.jsp" flush="" />
		<%
		}else if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
		{
		%>
		<jsp:include page="/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp" flush="" />
		<%
		}else if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
		{
		%>
		<jsp:include page="/html/applicationmanagementportlet/makeanapplication/stepone.jsp" flush="" />
		<%
		}else
		{
		%>
		<div class="panel panel-danger">You do not have access to carry out any actions as you do not have valid access. Contact Appropriate Administrators for rights</div>
		<%
		}
	}
%>