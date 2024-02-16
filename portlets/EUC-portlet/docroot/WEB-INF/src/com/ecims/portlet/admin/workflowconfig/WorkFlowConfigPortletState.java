package com.ecims.portlet.admin.workflowconfig;

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
import smartpay.entity.PortCode;
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

public class WorkFlowConfigPortletState {

	private static Logger log = Logger.getLogger(WorkFlowConfigPortletState.class);
	private static WorkFlowConfigPortletUtil workFlowConfigPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	

	private String successMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	private VIEW_TABS currentTab;
	
	private String positionId;
	

	private ItemCategory itemCategoryEntity;
	private Agency agencyEntity;
	private String[] positionIds;
	private String itemCategorys;
	private String[] agencys;
	
	private Collection<Agency> agencyList;
	private Collection<ItemCategory> itemCategoryList;
	
	
	
	
	/****enum section****/
    
    
    
	public static enum WORKFLOWCONFIG_ACTIONS{
		UPDATE_WORK_FLOW_CONFIG
	}
	
	public static enum VIEW_TABS{
		VIEW_PORTCODES, VIEW_NEW_PORTCODE
		
	}
	
	/*****APPLICATION CORE METHODS! DO NOT TAMPER WITH THIS SECTION OF CODE - START HERE*****/
	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			WorkFlowConfigPortletState portletState) {

		log.info("Eeeror Msg = " + errorMessage);
		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			WorkFlowConfigPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			WorkFlowConfigPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			WorkFlowConfigPortletState.log.debug("Error including error message", e);
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
	
	
	
	public static WorkFlowConfigPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		WorkFlowConfigPortletState portletState = null;
		Logger.getLogger(WorkFlowConfigPortletState.class).info("------getInstance");

		ServiceLocator serviceLocator = ServiceLocator.getInstance();
		SwpService swpService = serviceLocator.getSwpService();
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (WorkFlowConfigPortletState) session.getAttribute(WorkFlowConfigPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new WorkFlowConfigPortletState();
					WorkFlowConfigPortletUtil util = new WorkFlowConfigPortletUtil();
					portletState.setWorkFlowConfigPortletUtil(util);
					session.setAttribute(WorkFlowConfigPortletState.class.getName(), portletState);
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
			WorkFlowConfigPortletState portletState, SwpService swpService) {
		// TODO Auto-generated method stub
		portletState.setAgencyList(portletState.getWorkFlowConfigPortletUtil().getAllAgency());
		portletState.setItemCategoryList((Collection<ItemCategory>)portletState.getWorkFlowConfigPortletUtil().getAllEntity(ItemCategory.class));
	}

	/****core section starts here****/
	
	private void setWorkFlowConfigPortletUtil(WorkFlowConfigPortletUtil util) {
		// TODO Auto-generated method stub
		this.workFlowConfigPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, WorkFlowConfigPortletState portletState) {
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
				WorkFlowConfigPortletUtil util = WorkFlowConfigPortletUtil.getInstance();
				portletState.setWorkFlowConfigPortletUtil(util);
				
				ArrayList<RoleType> pur = null;
				
				if (request.getRemoteUser() != null
						&& !request.getRemoteUser().equals("")) {
					portletState.getWorkFlowConfigPortletUtil().
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


	public WorkFlowConfigPortletUtil getWorkFlowConfigPortletUtil() {
		// TODO Auto-generated method stub
		return this.workFlowConfigPortletUtil;
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


	
	public String getPositionId() {
		return positionId;
	}

	public void setPositionId(String positionId) {
		this.positionId = positionId;
	}

	public ItemCategory getItemCategoryEntity() {
		return itemCategoryEntity;
	}

	public void setItemCategoryEntity(ItemCategory itemCategoryEntity) {
		this.itemCategoryEntity = itemCategoryEntity;
	}

	public Agency getAgencyEntity() {
		return agencyEntity;
	}

	public void setAgencyEntity(Agency agencyEntity) {
		this.agencyEntity = agencyEntity;
	}

	public Collection<Agency> getAgencyList() {
		return agencyList;
	}

	public void setAgencyList(Collection<Agency> agencyList) {
		this.agencyList = agencyList;
	}

	public Collection<ItemCategory> getItemCategoryList() {
		return itemCategoryList;
	}

	public void setItemCategoryList(Collection<ItemCategory> itemCategoryList) {
		this.itemCategoryList = itemCategoryList;
	}

	public String[] getPositionIds() {
		return positionIds;
	}

	public void setPositionIds(String[] positionIds) {
		this.positionIds = positionIds;
	}

	public String getItemCategorys() {
		return itemCategorys;
	}

	public void setItemCategorys(String itemCategorys) {
		this.itemCategorys = itemCategorys;
	}

	public String[] getAgencys() {
		return agencys;
	}

	public void setAgencys(String[] agencys) {
		this.agencys = agencys;
	}


	
	
}
