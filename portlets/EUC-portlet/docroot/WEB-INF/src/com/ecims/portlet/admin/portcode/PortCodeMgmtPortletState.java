package com.ecims.portlet.admin.portcode;

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

public class PortCodeMgmtPortletState {

	private static Logger log = Logger.getLogger(PortCodeMgmtPortletState.class);
	private static PortCodeMgmtPortletUtil portCodeMgmtPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	

	private String successMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	private VIEW_TABS currentTab;
	
	private String portCodeName;
	private String portCodeState;
	private Collection<PortCode> portCodeListing;
	private Collection<State> stateList;
	private PortCode portCodeEntity;
	
	
	
	
	/****enum section****/
    
    
    
	public static enum PORTCODE_ACTIONS{
		CREATE_NEW_PORTCODE, EDIT_A_PORTCODE, MAP_PORTCODE_TO_STATE, VIEW_PORTCODES, HANDLE_PORT_CODE_ACTIONS
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
			PortCodeMgmtPortletState portletState) {

		log.info("Eeeror Msg = " + errorMessage);
		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			PortCodeMgmtPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			PortCodeMgmtPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			PortCodeMgmtPortletState.log.debug("Error including error message", e);
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
	
	
	
	public static PortCodeMgmtPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		PortCodeMgmtPortletState portletState = null;
		Logger.getLogger(PortCodeMgmtPortletState.class).info("------getInstance");

		ServiceLocator serviceLocator = ServiceLocator.getInstance();
		SwpService swpService = serviceLocator.getSwpService();
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (PortCodeMgmtPortletState) session.getAttribute(PortCodeMgmtPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new PortCodeMgmtPortletState();
					PortCodeMgmtPortletUtil util = new PortCodeMgmtPortletUtil();
					portletState.setPortCodeMgmtPortletUtil(util);
					session.setAttribute(PortCodeMgmtPortletState.class.getName(), portletState);
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
			PortCodeMgmtPortletState portletState, SwpService swpService) {
		// TODO Auto-generated method stub
		portletState.setStateList(portletState.getPortCodeMgmtPortletUtil().getAllStates());
	}

	/****core section starts here****/
	
	private void setPortCodeMgmtPortletUtil(PortCodeMgmtPortletUtil util) {
		// TODO Auto-generated method stub
		this.portCodeMgmtPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, PortCodeMgmtPortletState portletState) {
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
				PortCodeMgmtPortletUtil util = PortCodeMgmtPortletUtil.getInstance();
				portletState.setPortCodeMgmtPortletUtil(util);
				
				ArrayList<RoleType> pur = null;
				
				if (request.getRemoteUser() != null
						&& !request.getRemoteUser().equals("")) {
					portletState.getPortCodeMgmtPortletUtil().
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


	public PortCodeMgmtPortletUtil getPortCodeMgmtPortletUtil() {
		// TODO Auto-generated method stub
		return this.portCodeMgmtPortletUtil;
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


	public String getPortCodeName() {
		return portCodeName;
	}

	public void setPortCodeName(String portCodeName) {
		this.portCodeName = portCodeName;
	}

	public String getPortCodeState() {
		return portCodeState;
	}

	public void setPortCodeState(String portCodeState) {
		this.portCodeState = portCodeState;
	}

	public Collection<PortCode> getPortCodeListing() {
		return portCodeListing;
	}

	public void setPortCodeListing(Collection<PortCode> portCodeListing) {
		this.portCodeListing = portCodeListing;
	}

	public PortCode getPortCodeEntity() {
		return portCodeEntity;
	}

	public void setPortCodeEntity(PortCode portCodeEntity) {
		this.portCodeEntity = portCodeEntity;
	}

	public Collection<State> getStateList() {
		return stateList;
	}

	public void setStateList(Collection<State> stateList) {
		this.stateList = stateList;
	}
	


	
	
}
