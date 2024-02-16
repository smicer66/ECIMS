package com.ecims.portlet.contactus;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.apache.log4j.Logger;

import smartpay.entity.Applicant;
import smartpay.entity.Country;
import smartpay.entity.Permission;
import smartpay.entity.PortalUser;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.State;
import smartpay.entity.enumerations.ApplicantStatus;
import smartpay.entity.enumerations.PermissionType;
import smartpay.service.SwpService;

import com.ecims.commins.Util;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.sf.primepay.smartpay13.ServiceLocator;

public class ContactUsPortletState {

	private static Logger log = Logger.getLogger(ContactUsPortletState.class);
	private static ContactUsPortletUtil contactUsPortletUtil;
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
	
	/***step 1*****/
	private String accountType;
	
	/****step 2****/	
	private String fullName;
	private String emailAddress;
	private String contents;
	private String subject;
	private String mobileNumber;
	private static ContactUsPortletState portletState = null;
	static ServiceLocator serviceLocator = ServiceLocator.getInstance();
	static SwpService swpService = serviceLocator.getSwpService();
	private VIEW_TABS currentTab;
	
	private PortalUser activationPortalUser;
	
	private Collection<Applicant> applicantListing;
	private Collection<PermissionType> permissionList;
	private Applicant selectedApplicant;
	/****enum section****/
    
    
	public static enum EUC_DESK_ACTIONS{
		CREATE_A_PORTAL_USER_STEP_ZERO, CREATE_A_PORTAL_USER_STEP_ONE, CREATE_A_PORTAL_USER_STEP_TWO, CREATE_A_PORTAL_USER_STEP_THREE, 
		LIST_PORTAL_USERS, VIEW_A_PORTAL_USER_ACTION, APPROVE_PORTAL_USER_SIGNUP, REJECT_PORTAL_USER_SIGNUP, 
		LIST_PORTAL_USERS_ACTIONS, CREATE_A_PORTAL_USER_STEP_TWO_INSIDE, UPDATE_PORTAL_USER_PERMISSION
	}
	
	
	public static enum CONTACTUS_ACTIONS{
		CREATE_NEW_CONTACTUS
	}
	
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	public static enum VIEW_TABS{
		VIEW_ALL_APPLICANT, VIEW_NEW_APPLICANT_REQUESTS, 
		VIEW_APPROVED_APPLICANT_LISTINGS, VIEW_REJECTED_PORTAL_USER_REQUESTS
    }
	
	/*****APPLICATION CORE METHODS! DO NOT TAMPER WITH THIS SECTION OF CODE - START HERE*****/
	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			ContactUsPortletState portletState) {

		log.info("Eeeror Msg = " + errorMessage);
		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			ContactUsPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			ContactUsPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			ContactUsPortletState.log.debug("Error including error message", e);
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
	
	
	
	public static ContactUsPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		
		Logger.getLogger(ContactUsPortletState.class).info("------getInstance");
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (ContactUsPortletState) session.getAttribute(ContactUsPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new ContactUsPortletState();
					ContactUsPortletUtil util = new ContactUsPortletUtil();
					portletState.setContactUsPortletUtil(util);
					session.setAttribute(ContactUsPortletState.class.getName(), portletState);
					defaultInit(request, portletState);
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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	/****core section starts here****/
	public void reinitializeForCreateCorporateIndividual(
			ContactUsPortletState portletState) {
		// TODO Auto-generated method stub
	}
	
	
	
	
	private void setContactUsPortletUtil(ContactUsPortletUtil util) {
		// TODO Auto-generated method stub
		this.contactUsPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, ContactUsPortletState portletState) {
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
				ContactUsPortletUtil util = ContactUsPortletUtil.getInstance();
				portletState.setContactUsPortletUtil(util);
				
				ArrayList<RoleType> pur = null;
				
				if (request.getRemoteUser() != null
						&& !request.getRemoteUser().equals("")) {
					portletState.getContactUsPortletUtil().
							getRoleTypeByPortalUser(portletState.getPortalUser());	
				}		

				portletState.setPortalUserRoleType(pur);

				portletState.setFullName(null);
				portletState.setEmailAddress(null);
				portletState.setMobileNumber(null);
				portletState.setContents(null);
				portletState.setSubject(null);
				
				if(portletState.getPortalUser()!=null)
				{
					portletState.setFullName(portletState.getPortalUser().getFirstName() + " " + portletState.getPortalUser().getSurname());
					portletState.setEmailAddress(portletState.getPortalUser().getEmailAddress());
					portletState.setMobileNumber(portletState.getPortalUser().getPhoneNumber());
				}
				
				portletState.reinitializeForCreateCorporateIndividual(portletState);
				
				portletState.setSendingEmail(portletState.getContactUsPortletUtil().getSettingsByName("SENDING EMAIL ADDRESS"));
				portletState.setSendingEmailPassword(portletState.getContactUsPortletUtil().getSettingsByName("SENDING EMAIL PASSWORD"));
				portletState.setSendingEmailPort(portletState.getContactUsPortletUtil().getSettingsByName("SENDING EMAIL PORT"));
				portletState.setSendingEmailUsername(portletState.getContactUsPortletUtil().getSettingsByName("SENDING EMAIL USERNAME"));
				portletState.setMobileApplicationName(portletState.getContactUsPortletUtil().getSettingsByName("MOBILE APPLICATION NAME"));
				portletState.setProxyHost(portletState.getContactUsPortletUtil().getSettingsByName("PROXY HOST"));
				portletState.setProxyPort(portletState.getContactUsPortletUtil().getSettingsByName("PROXY PORT"));
				portletState.setApplicationName(portletState.getContactUsPortletUtil().getSettingsByName("APPLICATION NAME"));
				portletState.setSystemUrl(portletState.getContactUsPortletUtil().getSettingsByName("SYSTEM URL"));
				
				//Collection<PermissionType> peL = portletState.getContactUsPortletUtil().getPermissionsByPortalUser(portletState.getPortalUser(), swpService);
				//portletState.setPermissionListing(peL);
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

	
	
	public String getAccountType() {
		return accountType;
	}



	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}



	private void setPortalUser(PortalUser portalUser) {
		// TODO Auto-generated method stub
		this.portalUser = portalUser;
	}
	
	public PortalUser getPortalUser() {
		// TODO Auto-generated method stub
		return this.portalUser;
	}


	public ContactUsPortletUtil getContactUsPortletUtil() {
		// TODO Auto-generated method stub
		return this.contactUsPortletUtil;
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



	public String getSelectedPortalUserId() {
		return selectedPortalUserId;
	}

	public void setSelectedPortalUserId(String selectedPortalUserId) {
		this.selectedPortalUserId = selectedPortalUserId;
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


	public PortalUser getActivationPortalUser() {
		return activationPortalUser;
	}

	public void setActivationPortalUser(PortalUser activationPortalUser) {
		this.activationPortalUser = activationPortalUser;
	}

	public VIEW_TABS getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(VIEW_TABS currentTab) {
		this.currentTab = currentTab;
	}

	public Collection<Applicant> getApplicantListing() {
		return applicantListing;
	}

	public void setApplicantListing(Collection<Applicant> applicantListing) {
		this.applicantListing = applicantListing;
	}

	public Collection<PermissionType> getPermissionList() {
		return permissionList;
	}

	public void setPermissionListing(Collection<PermissionType> permissionList) {
		this.permissionList = permissionList;
	}

	public Applicant getSelectedApplicant() {
		return selectedApplicant;
	}

	public void setSelectedApplicant(Applicant selectedApplicant) {
		this.selectedApplicant = selectedApplicant;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	
}
