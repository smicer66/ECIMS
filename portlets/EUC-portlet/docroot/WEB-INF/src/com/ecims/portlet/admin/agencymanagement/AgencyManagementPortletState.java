package com.ecims.portlet.admin.agencymanagement;

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

public class AgencyManagementPortletState {

	private static Logger log = Logger.getLogger(AgencyManagementPortletState.class);
	private static AgencyManagementPortletUtil agencyManagementPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	

	private String successMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	private VIEW_TABS currentTab;
	
	private String agencyName;
	private String contactName;
	private String agencyPhone;
	private String agencyEmail;
	private String agencyType;
	private String selectedAgencyId;
	private Agency agencyEntity;
	private Collection<Agency> agencyListing;
	
	
	private Collection<EndorsementDesk> deskListing;
	private String deskName;
	private String selectedEndorsementDeskId;
	private EndorsementDesk endorsementDeskEntity;
	
	
	
	/****enum section****/
    
    
    
	public static enum AGENCY_ACTIONS{
		CREATE_NEW_AGENCY, EDIT_AN_AGENCY, DELETE_AN_AGENCY, VIEW_AGENCIES,
		CREATE_NEW_AGENCY_DESK, EDIT_AGENCY_DESK, DELETE_AGENCY_DESK, 
		HANDLE_ACTION_ON_LIST_OF_AGENCY, HANDLE_ACTION_ON_LIST_OF_DESK
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
			AgencyManagementPortletState portletState) {

		log.info("Eeeror Msg = " + errorMessage);
		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			AgencyManagementPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			AgencyManagementPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			AgencyManagementPortletState.log.debug("Error including error message", e);
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
	
	
	
	public static AgencyManagementPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		AgencyManagementPortletState portletState = null;
		Logger.getLogger(AgencyManagementPortletState.class).info("------getInstance");

		ServiceLocator serviceLocator = ServiceLocator.getInstance();
		SwpService swpService = serviceLocator.getSwpService();
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (AgencyManagementPortletState) session.getAttribute(AgencyManagementPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new AgencyManagementPortletState();
					AgencyManagementPortletUtil util = new AgencyManagementPortletUtil();
					portletState.setAgencyManagementPortletUtil(util);
					session.setAttribute(AgencyManagementPortletState.class.getName(), portletState);
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
			AgencyManagementPortletState portletState, SwpService swpService) {
		// TODO Auto-generated method stub
		Collection<Agency> agencyList = portletState.getAgencyManagementPortletUtil().getAllAgency();
		portletState.setAgencyListing(agencyList);
	}

	/****core section starts here****/
	
	private void setAgencyManagementPortletUtil(AgencyManagementPortletUtil util) {
		// TODO Auto-generated method stub
		this.agencyManagementPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, AgencyManagementPortletState portletState) {
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
				AgencyManagementPortletUtil util = AgencyManagementPortletUtil.getInstance();
				portletState.setAgencyManagementPortletUtil(util);
				
				ArrayList<RoleType> pur = null;
				
				if (request.getRemoteUser() != null
						&& !request.getRemoteUser().equals("")) {
					portletState.getAgencyManagementPortletUtil().
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


	public AgencyManagementPortletUtil getAgencyManagementPortletUtil() {
		// TODO Auto-generated method stub
		return this.agencyManagementPortletUtil;
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

	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getAgencyPhone() {
		return agencyPhone;
	}

	public void setAgencyPhone(String agencyPhone) {
		this.agencyPhone = agencyPhone;
	}

	public String getAgencyEmail() {
		return agencyEmail;
	}

	public void setAgencyEmail(String agencyEmail) {
		this.agencyEmail = agencyEmail;
	}

	public String getAgencyType() {
		return agencyType;
	}

	public void setAgencyType(String agencyType) {
		this.agencyType = agencyType;
	}

	public String getSelectedAgencyId() {
		return selectedAgencyId;
	}

	public void setSelectedAgencyId(String selectedAgencyId) {
		this.selectedAgencyId = selectedAgencyId;
	}

	public Agency getAgencyEntity() {
		return agencyEntity;
	}

	public void setAgencyEntity(Agency agencyEntity) {
		this.agencyEntity = agencyEntity;
	}

	public Collection<Agency> getAgencyListing() {
		return agencyListing;
	}

	public void setAgencyListing(Collection<Agency> agencyListing) {
		this.agencyListing = agencyListing;
	}

	public VIEW_TABS getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(VIEW_TABS currentTab) {
		this.currentTab = currentTab;
	}

	public Collection<EndorsementDesk> getDeskListing() {
		return deskListing;
	}

	public void setDeskListing(Collection<EndorsementDesk> deskListing) {
		this.deskListing = deskListing;
	}

	public String getDeskName() {
		return deskName;
	}

	public void setDeskName(String deskName) {
		this.deskName = deskName;
	}

	public String getSelectedEndorsementDeskId() {
		return selectedEndorsementDeskId;
	}

	public void setSelectedEndorsementDeskId(String selectedEndorsementDeskId) {
		this.selectedEndorsementDeskId = selectedEndorsementDeskId;
	}

	public EndorsementDesk getEndorsementDeskEntity() {
		return endorsementDeskEntity;
	}

	public void setEndorsementDeskEntity(EndorsementDesk endorsementDeskEntity) {
		this.endorsementDeskEntity = endorsementDeskEntity;
	}
	


	
	
}
