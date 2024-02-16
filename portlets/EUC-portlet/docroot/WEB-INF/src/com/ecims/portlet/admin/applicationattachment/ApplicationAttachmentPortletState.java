package com.ecims.portlet.admin.applicationattachment;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.apache.log4j.Logger;

import smartpay.entity.Agency;
import smartpay.entity.ApplicationAttachmentType;
import smartpay.entity.Country;
import smartpay.entity.EndorsementDesk;
import smartpay.entity.PortalUser;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.State;
import smartpay.service.SwpService;

import com.ecims.commins.Util;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.sf.primepay.smartpay13.ServiceLocator;

public class ApplicationAttachmentPortletState {

	private static Logger log = Logger.getLogger(ApplicationAttachmentPortletState.class);
	private static ApplicationAttachmentPortletUtil applicationAttachmentPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	

	private String successMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	private VIEW_TABS currentTab;
	private String attachmentType;
	
	
	private Collection<ApplicationAttachmentType> attachmentTypesListing;
	private ApplicationAttachmentType selectedAttachmentType;
	
	
	
	
	/****enum section****/
    
    
    
	public static enum HANDLE_ACTIONS{
		HANDLE_ACTION_ON_LIST_OF_ATTACHMENT_TYPE, NEW_ATTACHMENT_TYPE
	}
	
	public static enum VIEW_TABS{
		VIEW_ATTACHMENT_TYPES, CREATE_NEW_ATTACHMENT
		
	}
	
	/*****APPLICATION CORE METHODS! DO NOT TAMPER WITH THIS SECTION OF CODE - START HERE*****/
	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			ApplicationAttachmentPortletState portletState) {

		log.info("Eeeror Msg = " + errorMessage);
		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			ApplicationAttachmentPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			ApplicationAttachmentPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			ApplicationAttachmentPortletState.log.debug("Error including error message", e);
		}
	}
	
	public void setSuccessMessage(String successMessage)
	{
		this.successMessage=successMessage;
	}
	
	public String getRemoteIPAddress() {
		return remoteIPAddress;
	}

	public void setRemoteIPAddress(String remoteIPAddress) {
		this.remoteIPAddress = remoteIPAddress;
	}

	
	public String getErrorMessage()
	{
		return this.errorMessage;
	}
	
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	private void setCurrentRemoteUserId(String remoteUser) {
		// TODO Auto-generated method stub
		this.remoteUser = remoteUser;
	}
	
	private String getCurrentRemoteUserId() {
		// TODO Auto-generated method stub
		return this.remoteUser;
	}
	
	
	
	public static ApplicationAttachmentPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		ApplicationAttachmentPortletState portletState = null;
		Logger.getLogger(ApplicationAttachmentPortletState.class).info("------getInstance");

		ServiceLocator serviceLocator = ServiceLocator.getInstance();
		SwpService swpService = serviceLocator.getSwpService();
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (ApplicationAttachmentPortletState) session.getAttribute(ApplicationAttachmentPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new ApplicationAttachmentPortletState();
					ApplicationAttachmentPortletUtil util = new ApplicationAttachmentPortletUtil();
					portletState.setApplicationAttachmentManagementPortletUtil(util);
					session.setAttribute(ApplicationAttachmentPortletState.class.getName(), portletState);
					defaultInit(request, portletState);
					populateDefault(request, portletState, swpService);
	            }
				
			}
			
			//initSettings(portletState, swpService);
			// init settings
			return portletState;
		} catch (Exception e) {
			return null;
		}


	}
	
	/*****APPLICATION CORE METHODS! DO NOT TAMPER WITH THIS SECTION OF CODE - END HERE*****/
	
	
	private static void populateDefault(PortletRequest request,
			ApplicationAttachmentPortletState portletState, SwpService swpService) {
		// TODO Auto-generated method stub
		Collection<ApplicationAttachmentType> attachTypeList = portletState.getApplicationAttachmentManagementPortletUtil().getAllAttachmentTypes();
		portletState.setAttachmentTypesListing(attachTypeList);
	}

	/****core section starts here****/
	
	private void setApplicationAttachmentManagementPortletUtil(ApplicationAttachmentPortletUtil util) {
		// TODO Auto-generated method stub
		this.applicationAttachmentPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, ApplicationAttachmentPortletState portletState) {
		// TODO Auto-generated method stub
		com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
		log.info("------set default init");
		try
		{
		
//			if (request.getRemoteUser() == null) {
				portletState.setCurrentRemoteUserId(request.getRemoteUser());
				log.info(">>>Remote user durin default init: " + portletState.getCurrentRemoteUserId());
				log.info("request.getRemoteUser() =" + request.getRemoteUser());
				log.info("request.getRemoteUser() =" + swpCustomService);
				if (request.getRemoteUser() != null
						&& !request.getRemoteUser().equals("")) {
				Long orbitaId = Long.parseLong(request.getRemoteUser());
				portletState.setPortalUser((PortalUser) swpCustomService
						.getPortalUserByOrbitaId(Long.toString(orbitaId)));
				}
				portletState.setRemoteIPAddress(PortalUtil.getHttpServletRequest(request).getRemoteAddr());
				ApplicationAttachmentPortletUtil util = ApplicationAttachmentPortletUtil.getInstance();
				portletState.setApplicationAttachmentManagementPortletUtil(util);
				
				ArrayList<RoleType> pur = null;
				
				if (request.getRemoteUser() != null
						&& !request.getRemoteUser().equals("")) {
					portletState.getApplicationAttachmentManagementPortletUtil().
							getRoleTypeByPortalUser(portletState.getPortalUser());	
				}		

				portletState.setPortalUserRoleType(pur);
				
		}
		catch(Exception ex)
		{
			log.error("", ex);
		} finally {
			
			
		}
	}

	

	private void setPortalUser(PortalUser portalUser) {
		// TODO Auto-generated method stub
		this.portalUser = portalUser;
	}
	
	public PortalUser getPortalUser() {
		// TODO Auto-generated method stub
		return this.portalUser;
	}


	public ApplicationAttachmentPortletUtil getApplicationAttachmentManagementPortletUtil() {
		// TODO Auto-generated method stub
		return this.applicationAttachmentPortletUtil;
	}


	public ArrayList<RoleType> getPortalUserRoleType() {
		return portalUserRoleType;
	}


	public void setPortalUserRoleType(ArrayList<RoleType> portalUserRoleType) {
		this.portalUserRoleType = portalUserRoleType;
	}
	
	
	public ArrayList<String> getErrorList() {
		return errorList;
	}


	public void setErrorList(ArrayList<String> errorList) {
		this.errorList = errorList;
	}

	public VIEW_TABS getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(VIEW_TABS currentTab) {
		this.currentTab = currentTab;
	}


	public Collection<ApplicationAttachmentType> getAttachmentTypesListing() {
		return attachmentTypesListing;
	}

	public void setAttachmentTypesListing(Collection<ApplicationAttachmentType> attachmentTypesListing) {
		this.attachmentTypesListing = attachmentTypesListing;
	}

	public String getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(String attachmentType) {
		this.attachmentType = attachmentType;
	}

	public void setSelectedAttachmentType(ApplicationAttachmentType at) {
		// TODO Auto-generated method stub
		this.selectedAttachmentType = at;
	}
	
	public ApplicationAttachmentType getSelectedAttachmentType() {
		// TODO Auto-generated method stub
		return this.selectedAttachmentType;
	}
	


	
	
}
