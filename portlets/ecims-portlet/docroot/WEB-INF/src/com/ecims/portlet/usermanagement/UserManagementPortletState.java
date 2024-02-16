package com.ecims.portlet.usermanagement;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.apache.log4j.Logger;

import smartpay.entity.Country;
import smartpay.entity.PortalUser;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.State;
import smartpay.service.SwpService;

import com.ecims.commins.Util;
import com.ecims.portlet.usermanagement.UserManagementPortletState.VIEW_TABS;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.sf.primepay.smartpay13.ServiceLocator;

public class UserManagementPortletState {

	private static Logger log = Logger.getLogger(UserManagementPortletState.class);
	private static UserManagementPortletUtil userManagementPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	

	private String successMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	
	private Collection<Country> allCountry;
	private Collection<State> allState;
	private Collection<PortalUser> portalUserListing;
	private String selectedPortalUserId;
	
	private Settings sendingEmail;
	private Settings sendingEmailPassword;
	private Settings sendingEmailPort;
	private Settings sendingEmailUsername;
	
	private Settings ApplicationName;
	private Settings SystemUrl;
	
	private Settings mobileApplicationName;
	private Settings proxyHost;
	private Settings proxyPort;
	
	/*****************NAVIGATION*****************/
	
	private String usertype;
	private String lastname;
	private String firstname;
	private String othername;
	private String contactEmailAddress;
	private String dob;
	private String countryCode;
	private String mobile;
	private String nationality;
	private String state;
	private VIEW_TABS currentTab;
	
	private Collection<PortalUser> newRequestListing;
	private Collection<PortalUser> allPortalUserListing;
	
	/****enum section****/
    
    
    
	public static enum EUC_DESK_ACTIONS{
		CREATE_A_PORTAL_USER_STEP_ONE, CREATE_A_PORTAL_USER_STEP_TWO, CREATE_A_PORTAL_USER_STEP_THREE, 
		LIST_PORTAL_USERS, VIEW_A_PORTAL_USER_ACTION, APPROVE_PORTAL_USER_SIGNUP, REJECT_PORTAL_USER_SIGNUP, 
		LIST_PORTAL_USERS_ACTIONS
	}
	
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	public static enum VIEW_TABS{
    	CREATE_A_PORTAL_USERS, VIEW_UNAPPROVED_APPLICANTS, NEW_PORTAL_USER_REQUESTS, VIEW_PORTAL_USER_LISTINGS
    }
    
	/*****APPLICATION CORE METHODS! DO NOT TAMPER WITH THIS SECTION OF CODE - START HERE*****/
	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			UserManagementPortletState portletState) {

		log.info("Eeeror Msg = " + errorMessage);
		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			UserManagementPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			UserManagementPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			UserManagementPortletState.log.debug("Error including error message", e);
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
	
	
	
	public static UserManagementPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		UserManagementPortletState portletState = null;
		Logger.getLogger(UserManagementPortletState.class).info("------getInstance");
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (UserManagementPortletState) session.getAttribute(UserManagementPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new UserManagementPortletState();
					UserManagementPortletUtil util = new UserManagementPortletUtil();
					portletState.setUserManagementPortletUtil(util);
					session.setAttribute(UserManagementPortletState.class.getName(), portletState);
					defaultInit(request, portletState);
	            }
				
				ServiceLocator serviceLocator = ServiceLocator.getInstance();
				SwpService swpService = serviceLocator.getSwpService();
			}
			
			//initSettings(portletState, swpService);
			// init settings
			return portletState;
		} catch (Exception e) {
			return null;
		}


	}
	
	/*****APPLICATION CORE METHODS! DO NOT TAMPER WITH THIS SECTION OF CODE - END HERE*****/
	
	
	/****core section starts here****/
	public void reinitializeForCreateCorporateIndividual(
			UserManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setUsertype(null);
		portletState.setLastname(null);
		portletState.setFirstname(null);
		portletState.setOthername(null);
		portletState.setContactEmailAddress(null);
		portletState.setDob(null);
		portletState.setCountryCode(null);
		portletState.setMobile(null);
		portletState.setNationality(null);
		portletState.setState(null);
		
	}
	
	
	
	
	private void setUserManagementPortletUtil(UserManagementPortletUtil util) {
		// TODO Auto-generated method stub
		this.userManagementPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, UserManagementPortletState portletState) {
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
				UserManagementPortletUtil util = UserManagementPortletUtil.getInstance();
				portletState.setUserManagementPortletUtil(util);
				
				ArrayList<RoleType> pur = null;
				
				if (request.getRemoteUser() != null
						&& !request.getRemoteUser().equals("")) {
					portletState.getUserManagementPortletUtil().
							getRoleTypeByPortalUser(portletState.getPortalUser());	
				}		

				portletState.setPortalUserRoleType(pur);
				
				portletState.reinitializeForCreateCorporateIndividual(portletState);
				portletState.setAllCountry(portletState.getUserManagementPortletUtil().getAllCountries());
				portletState.setAllState(portletState.getUserManagementPortletUtil().getAllStates());
				
				
				
				
				portletState.setSendingEmail(portletState.getUserManagementPortletUtil().getSettingsByName("SENDING EMAIL ADDRESS"));
				portletState.setSendingEmailPassword(portletState.getUserManagementPortletUtil().getSettingsByName("SENDING EMAIL PASSWORD"));
				portletState.setSendingEmailPort(portletState.getUserManagementPortletUtil().getSettingsByName("SENDING EMAIL PORT"));
				portletState.setSendingEmailUsername(portletState.getUserManagementPortletUtil().getSettingsByName("SENDING EMAIL USERNAME"));
				portletState.setMobileApplicationName(portletState.getUserManagementPortletUtil().getSettingsByName("MOBILE APPLICATION NAME"));
				portletState.setProxyHost(portletState.getUserManagementPortletUtil().getSettingsByName("PROXY HOST"));
				portletState.setProxyPort(portletState.getUserManagementPortletUtil().getSettingsByName("PROXY PORT"));
				portletState.setApplicationName(portletState.getUserManagementPortletUtil().getSettingsByName("APPLICATION NAME"));
				portletState.setSystemUrl(portletState.getUserManagementPortletUtil().getSettingsByName("SYSTEM URL"));
				
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


	public UserManagementPortletUtil getUserManagementPortletUtil() {
		// TODO Auto-generated method stub
		return this.userManagementPortletUtil;
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
	

	public Collection<PortalUser> getPortalUserListing() {
		return portalUserListing;
	}



	public void setPortalUserListing(Collection<PortalUser> portalUserListing) {
		this.portalUserListing = portalUserListing;
	}




	public String getMobile() {
		return mobile;
	}



	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


	public String getState() {
		return state;
	}



	public void setState(String state) {
		this.state = state;
	}



	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public Collection<Country> getAllCountry() {
		return allCountry;
	}

	public void setAllCountry(Collection<Country> allCountry) {
		this.allCountry = allCountry;
	}

	public Collection<State> getAllState() {
		return allState;
	}

	public void setAllState(Collection<State> allState) {
		this.allState = allState;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	public String getSelectedPortalUserId() {
		return selectedPortalUserId;
	}

	public void setSelectedPortalUserId(String selectedPortalUserId) {
		this.selectedPortalUserId = selectedPortalUserId;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}
	
	

	public Settings getSendingEmail() {
		return sendingEmail;
	}

	public void setSendingEmail(Settings sendingEmail) {
		this.sendingEmail = sendingEmail;
	}

	public Settings getSendingEmailPassword() {
		return sendingEmailPassword;
	}

	public void setSendingEmailPassword(Settings sendingEmailPassword) {
		this.sendingEmailPassword = sendingEmailPassword;
	}

	public Settings getSendingEmailPort() {
		return sendingEmailPort;
	}

	public void setSendingEmailPort(Settings sendingEmailPort) {
		this.sendingEmailPort = sendingEmailPort;
	}

	public Settings getSendingEmailUsername() {
		return sendingEmailUsername;
	}

	public void setSendingEmailUsername(Settings sendingEmailUsername) {
		this.sendingEmailUsername = sendingEmailUsername;
	}
	
	
	public Settings getApplicationName() {
		return ApplicationName;
	}

	public void setApplicationName(Settings applicationName) {
		ApplicationName = applicationName;
	}

	public Settings getSystemUrl() {
		return SystemUrl;
	}

	public void setSystemUrl(Settings systemUrl) {
		SystemUrl = systemUrl;
	}

	
	public Settings getMobileApplicationName() {
		return mobileApplicationName;
	}

	public void setMobileApplicationName(Settings mobileApplicationName) {
		this.mobileApplicationName = mobileApplicationName;
	}

	public Settings getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(Settings proxyHost) {
		this.proxyHost = proxyHost;
	}

	public Settings getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(Settings proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getOthername() {
		return othername;
	}

	public void setOthername(String othername) {
		this.othername = othername;
	}

	public String getContactEmailAddress() {
		return contactEmailAddress;
	}

	public void setContactEmailAddress(String contactEmailAddress) {
		this.contactEmailAddress = contactEmailAddress;
	}

	public void setCurrentTab(VIEW_TABS viewTab) {
		// TODO Auto-generated method stub
		this.currentTab = viewTab;
	}
	
	public VIEW_TABS getCurrentTab() {
		// TODO Auto-generated method stub
		return this.currentTab;
	}

	public Collection<PortalUser> getNewRequestListing() {
		return newRequestListing;
	}

	public void setNewRequestListing(Collection<PortalUser> newRequestListing) {
		this.newRequestListing = newRequestListing;
	}

	public Collection<PortalUser> getAllPortalUserListing() {
		return allPortalUserListing;
	}

	public void setAllPortalUserListing(Collection<PortalUser> allPortalUserListing) {
		this.allPortalUserListing = allPortalUserListing;
	}

	
	
}
