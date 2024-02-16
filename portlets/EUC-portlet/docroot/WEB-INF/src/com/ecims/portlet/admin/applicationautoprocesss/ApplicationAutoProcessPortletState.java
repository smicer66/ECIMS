package com.ecims.portlet.admin.applicationautoprocesss;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.apache.log4j.Logger;

import smartpay.entity.Agency;
import smartpay.entity.Country;
import smartpay.entity.EndorsementDesk;
import smartpay.entity.ItemCategory;
import smartpay.entity.PortalUser;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.State;
import smartpay.entity.WokFlowSetting;
import smartpay.service.SwpService;

import com.ecims.commins.Util;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.sf.primepay.smartpay13.ServiceLocator;

public class ApplicationAutoProcessPortletState {

	private static Logger log = Logger.getLogger(ApplicationAutoProcessPortletState.class);
	private static ApplicationAutoProcessPortletUtil applicationAutoProcessPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	

	private String successMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	private VIEW_TABS currentTab;
	
	private Collection<Agency> allAgencyListing;
	private Agency selectedAgency;
	private Collection<WokFlowSetting> workFlowSettingList;
	private Collection<ItemCategory> itemCategoryListing;
	private ItemCategory itemCategoryEntity;
	
	
	
	
	/****enum section****/
    
    
    
	public static enum ACTIONS{
		MANAGE_AUTO_PROCESS
	}
	
	public static enum VIEW_TABS{
		VIEW_NEW_AGENCY, VIEW_NEW_DESK, VIEW_ALL_AGENCY, VIEW_ALL_DESK
		
	}
	
	/*****APPLICATION CORE METHODS! DO NOT TAMPER WITH THIS SECTION OF CODE - START HERE*****/
	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			ApplicationAutoProcessPortletState portletState) {

		log.info("Eeeror Msg = " + errorMessage);
		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			ApplicationAutoProcessPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			ApplicationAutoProcessPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			ApplicationAutoProcessPortletState.log.debug("Error including error message", e);
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
	
	
	
	public static ApplicationAutoProcessPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		ApplicationAutoProcessPortletState portletState = null;
		Logger.getLogger(ApplicationAutoProcessPortletState.class).info("------getInstance");

		ServiceLocator serviceLocator = ServiceLocator.getInstance();
		SwpService swpService = serviceLocator.getSwpService();
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (ApplicationAutoProcessPortletState) session.getAttribute(ApplicationAutoProcessPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new ApplicationAutoProcessPortletState();
					ApplicationAutoProcessPortletUtil util = new ApplicationAutoProcessPortletUtil();
					portletState.setApplicationAutoProcessPortletUtil(util);
					session.setAttribute(ApplicationAutoProcessPortletState.class.getName(), portletState);
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
			ApplicationAutoProcessPortletState portletState, SwpService swpService) {
		// TODO Auto-generated method stub
		Collection<Agency> agencyList = portletState.getApplicationAutoProcessPortletUtil().getAllAgency();
		portletState.setAllAgencyListing(agencyList);
	}

	/****core section starts here****/
	
	private void setApplicationAutoProcessPortletUtil(ApplicationAutoProcessPortletUtil util) {
		// TODO Auto-generated method stub
		this.applicationAutoProcessPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, ApplicationAutoProcessPortletState portletState) {
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
				ApplicationAutoProcessPortletUtil util = ApplicationAutoProcessPortletUtil.getInstance();
				portletState.setApplicationAutoProcessPortletUtil(util);
				
				ArrayList<RoleType> pur = null;
				
				if (request.getRemoteUser() != null
						&& !request.getRemoteUser().equals("")) {
					portletState.getApplicationAutoProcessPortletUtil().
							getRoleTypeByPortalUser(portletState.getPortalUser());	
				}		

				portletState.setPortalUserRoleType(pur);
				
				
//			}else if (request.getRemoteUser() != null
//					&& !request.getRemoteUser().equals("")) {
//				log.info(">>>Remote user durin default init2: " + portletState.getCurrentRemoteUserId());
//				log.info(">>>Do Not give Access ");
//			}
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


	public ApplicationAutoProcessPortletUtil getApplicationAutoProcessPortletUtil() {
		// TODO Auto-generated method stub
		return this.applicationAutoProcessPortletUtil;
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
	public Collection<Agency> getAllAgencyListing() {
		return allAgencyListing;
	}

	public void setAllAgencyListing(Collection<Agency> allAgencyListing) {
		this.allAgencyListing = allAgencyListing;
	}

	public Agency getSelectedAgency() {
		return selectedAgency;
	}

	public void setSelectedAgency(Agency selectedAgency) {
		this.selectedAgency = selectedAgency;
	}

	public Collection<WokFlowSetting> getWorkFlowSettingList() {
		return workFlowSettingList;
	}

	public void setWorkFlowSettingList(Collection<WokFlowSetting> workFlowSettingList) {
		this.workFlowSettingList = workFlowSettingList;
	}

	public Collection<ItemCategory> getItemCategoryListing() {
		return itemCategoryListing;
	}

	public void setItemCategoryListing(Collection<ItemCategory> itemCategoryListing) {
		this.itemCategoryListing = itemCategoryListing;
	}

	public ItemCategory getItemCategoryEntity() {
		return itemCategoryEntity;
	}

	public void setItemCategoryEntity(ItemCategory itemCategoryEntity) {
		this.itemCategoryEntity = itemCategoryEntity;
	}
	


	
	
}
