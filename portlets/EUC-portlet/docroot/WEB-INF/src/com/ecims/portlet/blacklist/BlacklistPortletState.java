package com.ecims.portlet.blacklist;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import smartpay.entity.Agency;
import smartpay.entity.Applicant;
import smartpay.entity.Application;
import smartpay.entity.ApplicationAttachment;
import smartpay.entity.ApplicationAttachmentAgency;
import smartpay.entity.ApplicationAttachmentType;
import smartpay.entity.ApplicationFlag;
import smartpay.entity.ApplicationItem;
import smartpay.entity.ApplicationWorkflow;
import smartpay.entity.BlackList;
import smartpay.entity.BlackListLog;
import smartpay.entity.Certificate;
import smartpay.entity.Country;
import smartpay.entity.Currency;
import smartpay.entity.EndorsedApplicationDesk;
import smartpay.entity.EndorsementDesk;
import smartpay.entity.ItemCategory;
import smartpay.entity.ItemCategorySub;
import smartpay.entity.PortCode;
import smartpay.entity.PortalUser;
import smartpay.entity.QuantityUnit;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.State;
import smartpay.entity.WeightUnit;
import smartpay.entity.enumerations.ApplicationStatus;
import smartpay.entity.enumerations.PermissionType;
import smartpay.entity.enumerations.RoleConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.service.SwpService;

import com.ecims.commins.Util;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.ecims.commins.ComminsApplicationState;
import com.sf.primepay.smartpay13.ServiceLocator;
import com.sun.mail.iap.Literal;

public class BlacklistPortletState {

	private static Logger log = Logger.getLogger(BlacklistPortletState.class);
	private static BlacklistPortletUtil blacklistPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	

	private String successMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	
	private Collection<Country> allCountry;
	private Collection<State> allState;
	private Application selectedApplication;
	private Collection<BlackList> blackListListing;
	private Collection<BlackListLog> blackListLogListing;
	
	private Settings sendingEmail;
	private Settings sendingEmailPassword;
	private Settings sendingEmailPort;
	private Settings sendingEmailUsername;
	
	private Settings ApplicationName;
	private Settings SystemUrl;
	
	private Settings mobileApplicationName;
	private Settings proxyHost;
	private Settings proxyPort;
	private BlackList currentBlackList;
	private BlackListLog currentBlackListLog;
	private Applicant applicantToBlackList;
	
	/*****************non-request objects*****************/
	
	
	private VIEW_TABS currentTab;
	private Collection<PermissionType> permissionList;
	private static BlacklistPortletState portletState = null;

	static ServiceLocator serviceLocator = ServiceLocator.getInstance();
	static SwpService swpService = serviceLocator.getSwpService();
	
	

	/****enum section****/
    
    
    
	public static enum EU_ACTIONS{
		CREATE_AN_APPLICATION_EU_STEP_ONE, CREATE_AN_APPLICATION_EU_STEP_TWO, CREATE_AN_APPLICATION_EU_STEP_THREE, CREATE_AN_APPLICATION_EU_STEP_FOUR
		
	}
	
	public static enum AGENCY_ACTIONS{
		SIGN_ENDORSE_APPLICATION
	}
	
	public static enum NSA_ACTIONS{
		SIGN_APPROVE_APPLICATION
	}
	
	public static enum BLACKLIST_ACTIONS{
		HANDLE_ACTIONS_ON_BLACKLISTED_APPLICANTS, 
		HANDLE_ACTIONS_ON_BLACKLISTED_LOG_APPLICANTS, 
		BLACKLIST_AN_APPLICANT_STEP_ONE, BLACKLIST_AN_APPLICANT_STEP_TWO, 
		REMOVE_FROM_BLACKLIST
	}
	
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	public static enum VIEW_TABS{
		LIST_BLACKLISTED_APPLICANTS, 
		BLACKLIST_AN_APPLICANT, LIST_BLACKLIST_HISTORY
    }
    
	/*****APPLICATION CORE METHODS! DO NOT TAMPER WITH THIS SECTION OF CODE - START HERE*****/
	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			BlacklistPortletState portletState) {

		log.info("Eeeror Msg = " + errorMessage);
		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			BlacklistPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			BlacklistPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			BlacklistPortletState.log.debug("Error including error message", e);
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
	
	
	
	public static BlacklistPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		//BlacklistPortletPortletState portletState = null;
		Logger.getLogger(BlacklistPortletState.class).info("------getInstance");
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				
				portletState = (BlacklistPortletState) session.getAttribute(BlacklistPortletState.class.getName(), PortletSession.PORTLET_SCOPE);

				
				if (portletState == null) {
					portletState = new BlacklistPortletState();
					BlacklistPortletUtil util = new BlacklistPortletUtil();
					portletState.setBlacklistPortletPortletUtil(util);
					session.setAttribute(BlacklistPortletState.class.getName(), portletState);
					defaultInit(request, portletState);
					//populateAttachmentType(swpService);
					
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
	
	
	public static Collection<PermissionType> loadPermissions(BlacklistPortletState applicationState, PortalUser pu, SwpService swpService2) {
		// TODO Auto-generated method stub
		Collection<PermissionType> pm = applicationState.getPermissionsByPortalUser(pu, swpService2);
		applicationState.setPermissionList(pm);
		return pm;
	}
	
	
	private Collection<PermissionType> getPermissionsByPortalUser(PortalUser pu2, SwpService swpService2) {
		// TODO Auto-generated method stub
		Collection<PermissionType> permList = null;
		try {
			
			String hql = "select rt.permissionType from Permission rt where (" +
					"rt.portalUser.id = " + pu2.getId() + ")";
			log.info("Get hql = " + hql);
			permList = (Collection<PermissionType>) swpService.getAllRecordsByHQL(hql);
		
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return permList;
	}
	
	private static void populateAttachmentType(SwpService swpService) {
		// TODO Auto-generated method stub
		Collection<ApplicationAttachmentType> li = (Collection<ApplicationAttachmentType>) swpService.getAllRecords(ApplicationAttachmentType.class);
		
		for(Iterator<ApplicationAttachmentType> it = li.iterator(); it.hasNext();)
		{
			String lit = it.next().getName();
			ApplicationAttachmentType aat = new ApplicationAttachmentType();
//			aat.setExpiryDateApplicable(false);
//			aat.setName(AttachmentTypeConstant.fromString(lit));
//			swpService.createNewRecord(aat);
		}
	}

	/****core section starts here****/
	
	
	private void setBlacklistPortletPortletUtil(BlacklistPortletUtil util) {
		// TODO Auto-generated method stub
		this.blacklistPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, BlacklistPortletState portletState) {
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
				BlacklistPortletUtil util = BlacklistPortletUtil.getInstance();
				portletState.setBlacklistPortletPortletUtil(util);
				
				ArrayList<RoleType> pur = null;
				
				if (request.getRemoteUser() != null
						&& !request.getRemoteUser().equals("")) {
					portletState.getBlacklistPortletUtil().
							getRoleTypeByPortalUser(portletState.getPortalUser());	
				}		

				portletState.setPortalUserRoleType(pur);
				portletState.setAllCountry(portletState.getBlacklistPortletUtil().getAllCountries());
				portletState.setAllState(portletState.getBlacklistPortletUtil().getAllStates());
				
				
				
				portletState.setSendingEmail(portletState.getBlacklistPortletUtil().getSettingsByName("SENDING EMAIL ADDRESS"));
				portletState.setSendingEmailPassword(portletState.getBlacklistPortletUtil().getSettingsByName("SENDING EMAIL PASSWORD"));
				portletState.setSendingEmailPort(portletState.getBlacklistPortletUtil().getSettingsByName("SENDING EMAIL PORT"));
				portletState.setSendingEmailUsername(portletState.getBlacklistPortletUtil().getSettingsByName("SENDING EMAIL USERNAME"));
				portletState.setMobileApplicationName(portletState.getBlacklistPortletUtil().getSettingsByName("MOBILE APPLICATION NAME"));
				portletState.setProxyHost(portletState.getBlacklistPortletUtil().getSettingsByName("PROXY HOST"));
				portletState.setProxyPort(portletState.getBlacklistPortletUtil().getSettingsByName("PROXY PORT"));
				portletState.setApplicationName(portletState.getBlacklistPortletUtil().getSettingsByName("APPLICATION NAME"));
				portletState.setSystemUrl(portletState.getBlacklistPortletUtil().getSettingsByName("SYSTEM URL"));
				
				portletState.setBlackListListing(portletState.getBlacklistPortletUtil().getBlackList());
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


	public static BlacklistPortletUtil getBlacklistPortletUtil() {
		return blacklistPortletUtil;
	}

	public static void setBlacklistPortletUtil(
			BlacklistPortletUtil blacklistPortletUtil) {
		BlacklistPortletState.blacklistPortletUtil = blacklistPortletUtil;
	}



	
	public VIEW_TABS getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(VIEW_TABS currentTab) {
		this.currentTab = currentTab;
	}


	public Application getSelectedApplication() {
		return selectedApplication;
	}

	public void setSelectedApplication(Application selectedApplication) {
		this.selectedApplication = selectedApplication;
	}

	public Collection<PermissionType> getPermissionList() {
		return permissionList;
	}

	public void setPermissionList(Collection<PermissionType> permissionList) {
		this.permissionList = permissionList;
	}
	
	
	public Collection<BlackList> getBlackListListing()
	{
		return this.blackListListing;
	}
	
	public void setBlackListListing(Collection<BlackList> blackListListing)
	{
		this.blackListListing = blackListListing;
	}

	public BlackList getCurrentBlackList() {
		return currentBlackList;
	}

	public void setCurrentBlackList(BlackList currentBlackList) {
		this.currentBlackList = currentBlackList;
	}

	public Collection<BlackListLog> getBlackListLogListing() {
		return blackListLogListing;
	}

	public void setBlackListLogListing(Collection<BlackListLog> blackListLogListing) {
		this.blackListLogListing = blackListLogListing;
	}

	public Applicant getApplicantToBlackList() {
		return applicantToBlackList;
	}

	public void setApplicantToBlackList(Applicant applicantToBlackList) {
		this.applicantToBlackList = applicantToBlackList;
	}

	public BlackListLog getCurrentBlackListLog() {
		return currentBlackListLog;
	}

	public void setCurrentBlackListLog(BlackListLog currentBlackListLog) {
		this.currentBlackListLog = currentBlackListLog;
	}

	

	

	
	
}
