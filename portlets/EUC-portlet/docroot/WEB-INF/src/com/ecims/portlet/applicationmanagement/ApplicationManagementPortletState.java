package com.ecims.portlet.applicationmanagement;

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
import com.ecims.portlet.applicationmanagement.ApplicationManagementPortletState.VIEW_TABS;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.ecims.commins.ComminsApplicationState;
import com.sf.primepay.smartpay13.ServiceLocator;
import com.sun.mail.iap.Literal;

public class ApplicationManagementPortletState {

	private static Logger log = Logger.getLogger(ApplicationManagementPortletState.class);
	private static ApplicationManagementPortletUtil applicationManagementUtil;
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
	private Collection<Application> applicationListing;
	
	private Settings sendingEmail;
	private Settings sendingEmailPassword;
	private Settings sendingEmailPort;
	private Settings sendingEmailUsername;
	
	private Settings ApplicationName;
	private Settings SystemUrl;
	
	private Settings mobileApplicationName;
	private Settings proxyHost;
	private Settings proxyPort;
	private Collection<EndorsedApplicationDesk> endorsedApplicationDeskList;
	private Collection<Agency> agenciesAlreadyHandled;

	
	
	/*****************non-request objects*****************/
	private Collection<ApplicationWorkflow> applicationWorkFlowsListingForComments;
	private Certificate certificateGenerated;
	private Collection<EndorsementDesk> endorsementDeskList;
	private ItemCategory itemCategoryEntity;
	private Country countryEntity;
	private String purposeOfUsage;
	private Collection<ApplicationItem> applicationItemList;
	private Collection<ApplicationAttachment> attachmentListing;
	private String chkImportingFor;
	private String importName;
	private String txtImportAddress;
	private String fileUploadAwardLetter;
	private String chkBoxImportDuty;
	private String txtProofTitle;
	private String fileUploadProofAttachment;
	private State portofLandingStateEntity;
	private PortCode portCodeEntity;
	private String applciationNumber;
	private ItemCategorySub itemCategorySubEntity;
	private QuantityUnit quanityUnitEntity;
	private WeightUnit weightUnitEntity;
	private Collection<String> attachmentTypeListByHSCode;
	private Collection<PortCode> portCodeList;
	private Collection<ItemCategorySub> itemCategorySubList;
	private Collection<QuantityUnit> quantityUnitList;
	private Collection<WeightUnit> weightUnitList;
	private Collection<ItemCategory> ItemCategoryList;
	private Collection<ItemCategory> applyItemCategoryList;
	private Collection<Currency> currencyList;
	private Applicant applicant;
	private PortalUser agencyApplicant;
	private Application application;
	private ApplicationWorkflow applicationWorkflow;
	private Collection<ApplicationWorkflow> applicationWorkFlowListing;
	private Collection<ApplicationFlag> applicationFlags;
	private Collection<Application> applicationinAppFlags;
	private Collection<ApplicationWorkflow> alreadyForwardedTo;
	private Currency currencyEntity;
	private Collection<ApplicationAttachmentAgency> validatedAttachments;
	
	private String rejectComment;
	private boolean exemptionYes;
	
	
	private String description;
	private String quantity;
	private String quantityUnit;
	private String weight;
	private String weightUnit;
	private String amount;
	private String currency;
	
	private Collection<Agency> agencyList;
	private Agency agencyEntity;
	private String commentsForward;
	private Collection<PortalUser> agencyPortalUserList;
	private boolean isAttachsValid;
	
	private VIEW_TABS currentTab;
	private Collection<PermissionType> permissionList;
	private ArrayList<ApplicationWorkflow> awfList;
	private String commentsonsa;
	private static ApplicationManagementPortletState portletState = null;
	private BlackList hotChocoloateAllowDip;

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
	
	public static enum APPLIST_ACTIONS{
		HANDLE_ACTIONS_ON_LIST_APPLICATIONS, ACT_ON_APPLICATION, ACT_ON_UPLOAD_CERTIFICATE
	}
	
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	public static enum VIEW_TABS{
		CREATE_AN_APPLICATION_EU, 
		LIST_PENDING_APPLICATIONS_EU, LIST_APPROVED_APPLICATIONS_EU, LIST_REJECTED_APPLICATIONS_EU, LIST_ALL_APPLICATIONS_EU, LIST_APPROVED_AWAIT_ISSUANCE_APPLICATIONS_EU,  
		
		LIST_PENDING_APPLICATIONS_NSA, LIST_APPROVED_APPLICATIONS_NSA, LIST_REJECTED_APPLICATIONS_NSA, 
		LIST_FORWARDED_APPLICATIONS_NSA, LIST_APPROVED_AWAIT_ISSUANCE_APPLICATIONS_NSA, 
		LIST_ENDORSED_APPLICATIONS_NSA, LIST_DISPUTED_APPLICATIONS_NSA, LIST_DISENDORSED_APPLICATIONS_NSA,  
		
		LIST_PENDING_APPLICATIONS_AG, LIST_ENDORSED_APPLICATIONS_AG, LIST_DISENDORSED_APPLICATIONS_AG, LIST_FLAGGED_APPLICATIONS_AG, LIST_DEVALIDATED_APPLICATIONS_AG, LIST_DISPUTED_APPLICATIONS_AG, 
		LIST_MY_AGENCY_APPLICATIONS_AG, CREATE_AN_APPLICATION_AG
    }
    
	/*****APPLICATION CORE METHODS! DO NOT TAMPER WITH THIS SECTION OF CODE - START HERE*****/
	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			ApplicationManagementPortletState portletState) {

		log.info("Eeeror Msg = " + errorMessage);
		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			ApplicationManagementPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			ApplicationManagementPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			ApplicationManagementPortletState.log.debug("Error including error message", e);
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
	
	
	
	public static ApplicationManagementPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		//ApplicationManagementPortletState portletState = null;
		Logger.getLogger(ApplicationManagementPortletState.class).info("------getInstance");
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				
				portletState = (ApplicationManagementPortletState) session.getAttribute(ApplicationManagementPortletState.class.getName(), PortletSession.PORTLET_SCOPE);

				
				if (portletState == null) 
				{
					portletState = new ApplicationManagementPortletState();
					ApplicationManagementPortletUtil util = new ApplicationManagementPortletUtil();
					portletState.setApplicationManagementPortletUtil(util);
					session.setAttribute(ApplicationManagementPortletState.class.getName(), portletState);
					defaultInit(request, portletState);
					//populateAttachmentType(swpService);
					populateDefaultLists(swpService, portletState, request);
	            }
				
				
			}
			
			//initSettings(portletState, swpService);
			// init settings
			return portletState;
		} catch (Exception e) {
			return null;
		}


	}
	
	private static void populateDefaultLists(SwpService swpService,
			ApplicationManagementPortletState portletState, PortletRequest req) {
		// TODO Auto-generated method stub
		log.info("User Role Type ==" + portletState.getPortalUser().getRoleType().getName());
		log.info("RoleTypeConstants.ROLE_REGULATOR_USER ==" + RoleTypeConstants.ROLE_REGULATOR_USER);
		
		portletState.setQuantityUnitList((Collection<QuantityUnit>)portletState.getApplicationManagementPortletUtil().getAllEntityObjects(QuantityUnit.class));
		log.info("Quanity List size ==" +portletState.getQuantityUnitList().size());
		portletState.setWeightUnitList((Collection<WeightUnit>)portletState.getApplicationManagementPortletUtil().getAllEntityObjects(WeightUnit.class));
		log.info("Weight List size ==" +portletState.getWeightUnitList().size());
		portletState.setPortCodeList((Collection<PortCode>)portletState.getApplicationManagementPortletUtil().getAllEntityObjects(PortCode.class));
		log.info("PortCode List size ==" +portletState.getPortCodeList().size());
		portletState.setAllCountry((Collection<Country>)portletState.getApplicationManagementPortletUtil().getAllEntityObjects(Country.class));
		log.info("Country List size ==" +portletState.getAllCountry().size());
		portletState.setItemCategoryList((Collection<ItemCategory>)portletState.getApplicationManagementPortletUtil().getAllEntityObjects(ItemCategory.class));
		if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_END_USER) || 
				portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_REGULATOR_USER) || 
				portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_ACCREDITOR_USER) || 
				portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER) ||
				portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))
		{
			if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_END_USER))
			{
				Applicant app = portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser());
				portletState.setApplyItemCategoryList((Collection<ItemCategory>)portletState.getApplicationManagementPortletUtil().getAllItemCategoryByApplicantType(app));
			}else 
			{
				portletState.setApplyItemCategoryList((Collection<ItemCategory>)portletState.getApplicationManagementPortletUtil().getAllEntityObjects(ItemCategory.class));
			}
		}else
		{
			portletState.setApplyItemCategoryList(null);
		}
		log.info("Category List size ==" +portletState.getItemCategoryList().size());
		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
		portletState.setCurrencyList((Collection<Currency>)portletState.getApplicationManagementPortletUtil().getAllEntityObjects(Currency.class));
		log.info("Currency List size ==" +portletState.getCurrencyList().size());
		portletState.setPermissionList((Collection<PermissionType>)portletState.getPermissionsByPortalUser(portletState.getPortalUser(), swpService));
		log.info("getPermissionList size ==" +portletState.getPermissionList().size());
		
		for(Iterator<PermissionType> i = portletState.getPermissionList().iterator(); i.hasNext();)
		{
			log.info("--->Permission Type=" + i.next().getValue());
		}
		if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER))
		{
			Collection<ApplicationWorkflow> appList = null;
			if(portletState.getPermissionList().contains(PermissionType.PERMISSION_FORWARD_APPLICATION) && 
					!portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION))
			{
				appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
					getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
							portletState.getPortalUser().getRoleType().getId(), 
							ApplicationStatus.APPLICATION_STATUS_CREATED, false, 
							portletState.getPortalUser().getAgency());
				portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA);
			}
			else if(!portletState.getPermissionList().contains(PermissionType.PERMISSION_FORWARD_APPLICATION) && 
					portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION))
			{
				appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
					getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
							portletState.getPortalUser().getRoleType().getId(), 
							ApplicationStatus.APPLICATION_STATUS_FORWARDED, false, 
							portletState.getPortalUser().getAgency());
				portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA);
			}
			else if(portletState.getPermissionList().contains(PermissionType.PERMISSION_FORWARD_APPLICATION) && 
					portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION))
			{
				String[] l1 = {ApplicationStatus.APPLICATION_STATUS_FORWARDED.getValue(), ApplicationStatus.APPLICATION_STATUS_FORWARDED.getValue()};
				appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
						getApplicationWorkFlowByReceipientRoleIdAndStatusesAndNotWorkedOn(
							portletState.getPortalUser().getRoleType().getId(), 
							l1, false, 
							portletState.getPortalUser().getAgency());
				portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA);
			}
			//Collection<Application> appList = (Collection<Application>)portletState.getApplicationManagementPortletUtil().getAllEntityObjects(Application.class);
			if(appList!=null)
				log.info("appList size = " + appList.size());
			
			if(appList.size()>0)
			{
				portletState.setApplicationWorkFlowListing(appList);
			}else
			{
				//portletState.addError((ActionRequest)req, "There are no pending EUC application requests currently on the system", portletState);
			}
		}else if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_REGULATOR_USER))
		{
//			Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
//					getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
//							portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_FORWARDED, false, portletState.getPortalUser().getAgency());
//			//Collection<Application> appList = (Collection<Application>)portletState.getApplicationManagementPortletUtil().getAllEntityObjects(Application.class);
//			if(appList!=null)
//				log.info("appList size = " + appList.size());
//			
//			if(appList.size()>0)
//			{
//				portletState.setApplicationWorkFlowListing(appList);
//			}else
//			{
//				//portletState.addSuccess((ActionRequest)req, "There are no pending EUC application requests currently awaiting your action on the system", portletState);
//			}
		}
		else if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_ACCREDITOR_USER))
		{
//			Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
//					getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
//							portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_FORWARDED, false, portletState.getPortalUser().getAgency());
//			//Collection<Application> appList = (Collection<Application>)portletState.getApplicationManagementPortletUtil().getAllEntityObjects(Application.class);
//			if(appList!=null)
//				log.info("appList size = " + appList.size());
//			
//			if(appList.size()>0)
//			{
//				portletState.setApplicationWorkFlowListing(appList);
//			}else
//			{
//				//portletState.addSuccess((ActionRequest)req, "There are no pending EUC application requests currently awaiting your action on the system", portletState);
//			}
		}
		else if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_REGULATOR_USER))
		{
			Applicant applicant = portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser());
			Collection<Application> appList = portletState.getApplicationManagementPortletUtil().getApplicationsByApplicant(applicant);
			portletState.setApplicationListing(appList);
		}
//		Collection<PermissionType> pm = loadPermissions(portletState, portletState.getPortalUser(), swpService);
//		portletState.setPermissionList(pm);
	}

	/*****APPLICATION CORE METHODS! DO NOT TAMPER WITH THIS SECTION OF CODE - END HERE*****/
	
	
	public static Collection<PermissionType> loadPermissions(ApplicationManagementPortletState applicationState, PortalUser pu, SwpService swpService2) {
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
	public void reinitializeForNewApplication(
			ApplicationManagementPortletState portletState) {
		portletState.setApplciationNumber(null);
		portletState.setApplicant(null);
		portletState.setTxtImportAddress(null);
		portletState.setChkBoxImportDuty(null);
		portletState.setFileUploadAwardLetter(null);
		portletState.setImportName(null);
		portletState.setFileUploadProofAttachment(null);
		portletState.setTxtProofTitle(null);
		portletState.setPurposeOfUsage(null);
		portletState.setPortofLandingStateEntity(null);
		portletState.setApplicationItemList(null);
		portletState.setAttachmentListing(null);
		portletState.setDescription(null);
		portletState.setQuantity(null);
		portletState.setQuantityUnit(null);
		portletState.setWeight(null);
		portletState.setWeightUnit(null);
		portletState.setAmount(null);
		portletState.setCurrency(null);
		portletState.setItemCategoryEntity(null);
		portletState.setCountryEntity(null);
		portletState.setCurrencyEntity(null);
		portletState.setPortCodeEntity(null);
		
	}
	
	
	
	
	private void setApplicationManagementPortletUtil(ApplicationManagementPortletUtil util) {
		// TODO Auto-generated method stub
		this.applicationManagementUtil = util;
	}


	private static void defaultInit(PortletRequest request, ApplicationManagementPortletState portletState) {
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
				ApplicationManagementPortletUtil util = ApplicationManagementPortletUtil.getInstance();
				portletState.setApplicationManagementPortletUtil(util);
				
				ArrayList<RoleType> pur = null;
				
				if (request.getRemoteUser() != null
						&& !request.getRemoteUser().equals("")) {
					portletState.getApplicationManagementPortletUtil().
							getRoleTypeByPortalUser(portletState.getPortalUser());	
				}		

				portletState.setPortalUserRoleType(pur);
				
				portletState.reinitializeForNewApplication(portletState);
				portletState.setAllCountry(portletState.getApplicationManagementPortletUtil().getAllCountries());
				portletState.setAllState(portletState.getApplicationManagementPortletUtil().getAllStates());
				
				if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
				{
					portletState.setAgencyList((Collection<Agency>)portletState.getApplicationManagementPortletUtil().getAllEntityObjects(Agency.class));
				}
				
				if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
				{
//					portletState.setApplicationWorkFlowListing(portletState.getApplicationManagementPortletUtil().
//							getApplicationWorkFlowByReceipientRoleIdAndNotWorkedOn(portletState.getPortalUser().getRoleType().getId(), false, portletState.getPortalUser().getAgency()));
					portletState.setAgencyApplicant(portletState.getPortalUser());
				}
				
				if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
				{
					portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
				}
				
				
				
				
				
				portletState.setSendingEmail(portletState.getApplicationManagementPortletUtil().getSettingsByName("SENDING EMAIL ADDRESS"));
				portletState.setSendingEmailPassword(portletState.getApplicationManagementPortletUtil().getSettingsByName("SENDING EMAIL PASSWORD"));
				portletState.setSendingEmailPort(portletState.getApplicationManagementPortletUtil().getSettingsByName("SENDING EMAIL PORT"));
				portletState.setSendingEmailUsername(portletState.getApplicationManagementPortletUtil().getSettingsByName("SENDING EMAIL USERNAME"));
				portletState.setMobileApplicationName(portletState.getApplicationManagementPortletUtil().getSettingsByName("MOBILE APPLICATION NAME"));
				portletState.setProxyHost(portletState.getApplicationManagementPortletUtil().getSettingsByName("PROXY HOST"));
				portletState.setProxyPort(portletState.getApplicationManagementPortletUtil().getSettingsByName("PROXY PORT"));
				portletState.setApplicationName(portletState.getApplicationManagementPortletUtil().getSettingsByName("APPLICATION NAME"));
				portletState.setSystemUrl(portletState.getApplicationManagementPortletUtil().getSettingsByName("SYSTEM URL"));
				
//			}else if (request.getRemoteUser() != null
//					&& !request.getRemoteUser().equals("")) {
//				log.info(">>>Remote user durin default init2: " + portletState.getCurrentRemoteUserId());
//				log.info(">>>Do Not give Access ");
				if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
				{
					portletState.setHotChocoloateAllowDip((BlackList)(portletState.getApplicationManagementPortletUtil().getHotChocoloteAgency(portletState.getPortalUser().getAgency().getId())));
				}
				
				if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
				{
					portletState.setHotChocoloateAllowDip((BlackList)(portletState.getApplicationManagementPortletUtil().getHotChocolotePortalUser(portletState.getApplicant().getId())));
				}
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


	public ApplicationManagementPortletUtil getApplicationManagementPortletUtil() {
		// TODO Auto-generated method stub
		return this.applicationManagementUtil;
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


	public static ApplicationManagementPortletUtil getApplicationManagementUtil() {
		return applicationManagementUtil;
	}

	public static void setApplicationManagementUtil(
			ApplicationManagementPortletUtil applicationManagementUtil) {
		ApplicationManagementPortletState.applicationManagementUtil = applicationManagementUtil;
	}

	public ItemCategory getItemCategoryEntity() {
		return itemCategoryEntity;
	}

	public void setItemCategoryEntity(ItemCategory itemCategoryEntity) {
		this.itemCategoryEntity = itemCategoryEntity;
	}

	public Country getCountryEntity() {
		return countryEntity;
	}

	public void setCountryEntity(Country countryEntity) {
		this.countryEntity = countryEntity;
	}

	public String getPurposeOfUsage() {
		return purposeOfUsage;
	}

	public void setPurposeOfUsage(String purposeOfUsage) {
		this.purposeOfUsage = purposeOfUsage;
	}

	public Collection<ApplicationItem> getApplicationItemList() {
		return applicationItemList;
	}

	public void setApplicationItemList(
			Collection<ApplicationItem> applicationItemList) {
		this.applicationItemList = applicationItemList;
	}

	public Collection<ApplicationAttachment> getAttachmentListing() {
		return attachmentListing;
	}

	public void setAttachmentListing(
			Collection<ApplicationAttachment> attachmentListing) {
		this.attachmentListing = attachmentListing;
	}

	public String getChkImportingFor() {
		return chkImportingFor;
	}

	public void setChkImportingFor(String chkImportingFor) {
		this.chkImportingFor = chkImportingFor;
	}

	public String getImportName() {
		return importName;
	}

	public void setImportName(String importName) {
		this.importName = importName;
	}

	public String getTxtImportAddress() {
		return txtImportAddress;
	}

	public void setTxtImportAddress(String txtImportAddress) {
		this.txtImportAddress = txtImportAddress;
	}

	public String getFileUploadAwardLetter() {
		return fileUploadAwardLetter;
	}

	public void setFileUploadAwardLetter(String fileUploadAwardLetter) {
		this.fileUploadAwardLetter = fileUploadAwardLetter;
	}

	public String getChkBoxImportDuty() {
		return chkBoxImportDuty;
	}

	public void setChkBoxImportDuty(String chkBoxImportDuty) {
		this.chkBoxImportDuty = chkBoxImportDuty;
	}

	public String getTxtProofTitle() {
		return txtProofTitle;
	}

	public void setTxtProofTitle(String txtProofTitle) {
		this.txtProofTitle = txtProofTitle;
	}

	public String getFileUploadProofAttachment() {
		return fileUploadProofAttachment;
	}

	public void setFileUploadProofAttachment(String fileUploadProofAttachment) {
		this.fileUploadProofAttachment = fileUploadProofAttachment;
	}

	public State getPortofLandingStateEntity() {
		return portofLandingStateEntity;
	}

	public void setPortofLandingStateEntity(State portofLandingStateEntity) {
		this.portofLandingStateEntity = portofLandingStateEntity;
	}


	
	public VIEW_TABS getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(VIEW_TABS currentTab) {
		this.currentTab = currentTab;
	}


	public PortCode getPortCodeEntity() {
		return portCodeEntity;
	}

	public void setPortCodeEntity(PortCode portCodeEntity) {
		this.portCodeEntity = portCodeEntity;
	}

	public String getApplciationNumber() {
		return applciationNumber;
	}

	public void setApplciationNumber(String applciationNumber) {
		this.applciationNumber = applciationNumber;
	}

	public ItemCategorySub getItemCategorySubEntity() {
		return itemCategorySubEntity;
	}

	public void setItemCategorySubEntity(ItemCategorySub itemCategorySubEntity) {
		this.itemCategorySubEntity = itemCategorySubEntity;
	}

	public QuantityUnit getQuanityUnitEntity() {
		return quanityUnitEntity;
	}

	public void setQuanityUnitEntity(QuantityUnit quanityUnitEntity) {
		this.quanityUnitEntity = quanityUnitEntity;
	}

	public WeightUnit getWeightUnitEntity() {
		return weightUnitEntity;
	}

	public void setWeightUnitEntity(WeightUnit weightUnitEntity) {
		this.weightUnitEntity = weightUnitEntity;
	}

	public Collection<String> getAttachmentTypeListByHSCode() {
		return attachmentTypeListByHSCode;
	}

	public void setAttachmentTypeListByHSCode(
			Collection<String> attachmentTypeListByHSCode) {
		this.attachmentTypeListByHSCode = attachmentTypeListByHSCode;
	}

	public Collection<PortCode> getPortCodeList() {
		return portCodeList;
	}

	public void setPortCodeList(Collection<PortCode> portCodeList) {
		this.portCodeList = portCodeList;
	}

	public Collection<ItemCategorySub> getItemCategorySubList() {
		return itemCategorySubList;
	}

	public void setItemCategorySubList(Collection<ItemCategorySub> itemCategorySubList) {
		this.itemCategorySubList = itemCategorySubList;
	}

	public Collection<QuantityUnit> getQuantityUnitList() {
		return quantityUnitList;
	}

	public void setQuantityUnitList(Collection<QuantityUnit> quantityUnitList) {
		this.quantityUnitList = quantityUnitList;
	}

	public Collection<WeightUnit> getWeightUnitList() {
		return weightUnitList;
	}

	public void setWeightUnitList(Collection<WeightUnit> weightUnitList) {
		this.weightUnitList = weightUnitList;
	}

	public Collection<ItemCategory> getItemCategoryList() {
		return ItemCategoryList;
	}

	public void setItemCategoryList(Collection<ItemCategory> itemCategoryList) {
		ItemCategoryList = itemCategoryList;
	}

	public Applicant getApplicant() {
		return applicant;
	}

	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getQuantityUnit() {
		return quantityUnit;
	}

	public void setQuantityUnit(String quantityUnit) {
		this.quantityUnit = quantityUnit;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getWeightUnit() {
		return weightUnit;
	}

	public void setWeightUnit(String weightUnit) {
		this.weightUnit = weightUnit;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Collection<Currency> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(Collection<Currency> currencyList) {
		this.currencyList = currencyList;
	}

	public Application getSelectedApplication() {
		return selectedApplication;
	}

	public void setSelectedApplication(Application selectedApplication) {
		this.selectedApplication = selectedApplication;
	}

	public Collection<Application> getApplicationListing() {
		return applicationListing;
	}

	public void setApplicationListing(Collection<Application> applicationListing) {
		this.applicationListing = applicationListing;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public Collection<Agency> getAgencyList() {
		return agencyList;
	}

	public void setAgencyList(Collection<Agency> agencyList) {
		this.agencyList = agencyList;
	}

	public Agency getAgencyEntity() {
		return agencyEntity;
	}

	public void setAgencyEntity(Agency agencyEntity) {
		this.agencyEntity = agencyEntity;
	}

	public String getCommentsForward() {
		return commentsForward;
	}

	public void setCommentsForward(String commentsForward) {
		this.commentsForward = commentsForward;
	}

	public Collection<PortalUser> getAgencyPortalUserList() {
		return agencyPortalUserList;
	}

	public void setAgencyPortalUserList(Collection<PortalUser> agencyPortalUserList) {
		this.agencyPortalUserList = agencyPortalUserList;
	}

	public Collection<ApplicationWorkflow> getApplicationWorkFlowListing() {
		return applicationWorkFlowListing;
	}

	public void setApplicationWorkFlowListing(
			Collection<ApplicationWorkflow> applicationWorkFlowListing) {
		this.applicationWorkFlowListing = applicationWorkFlowListing;
	}

	public ApplicationWorkflow getApplicationWorkflow() {
		return applicationWorkflow;
	}

	public void setApplicationWorkflow(ApplicationWorkflow applicationWorkflow) {
		this.applicationWorkflow = applicationWorkflow;
	}

	public String getRejectComment() {
		return rejectComment;
	}

	public void setRejectComment(String rejectComment) {
		this.rejectComment = rejectComment;
	}

	public boolean isAttachsValid() {
		return isAttachsValid;
	}

	public void setAttachsValid(boolean isAttachsValid) {
		this.isAttachsValid = isAttachsValid;
	}

	public Collection<ApplicationFlag> getApplicationFlags() {
		return applicationFlags;
	}

	public void setApplicationFlags(Collection<ApplicationFlag> applicationFlags) {
		this.applicationFlags = applicationFlags;
	}

	public Currency getCurrencyEntity() {
		return currencyEntity;
	}

	public void setCurrencyEntity(Currency currencyEntity) {
		this.currencyEntity = currencyEntity;
	}

	public boolean isExemptionYes() {
		return exemptionYes;
	}

	public void setExemptionYes(boolean exemptionYes) {
		this.exemptionYes = exemptionYes;
	}

	public Collection<EndorsementDesk> getEndorsementDeskList() {
		return endorsementDeskList;
	}

	public void setEndorsementDeskList(Collection<EndorsementDesk> endorsementDeskList) {
		this.endorsementDeskList = endorsementDeskList;
	}

	public Certificate getCertificateGenerated() {
		return certificateGenerated;
	}

	public void setCertificateGenerated(Certificate certificateGenerated) {
		this.certificateGenerated = certificateGenerated;
	}

	public Collection<ApplicationWorkflow> getApplicationWorkFlowsListingForComments() {
		return applicationWorkFlowsListingForComments;
	}

	public void setApplicationWorkFlowsListingForComments(
			Collection<ApplicationWorkflow> applicationWorkFlowsListingForComments) {
		this.applicationWorkFlowsListingForComments = applicationWorkFlowsListingForComments;
	}

	public Collection<Application> getApplicationinAppFlags() {
		return applicationinAppFlags;
	}

	public void setApplicationinAppFlags(Collection<Application> applicationinAppFlags) {
		this.applicationinAppFlags = applicationinAppFlags;
	}

	public Collection<PermissionType> getPermissionList() {
		return permissionList;
	}

	public void setPermissionList(Collection<PermissionType> permissionList) {
		this.permissionList = permissionList;
	}

	public Collection<EndorsedApplicationDesk> getEndorsedApplicationDeskList() {
		return endorsedApplicationDeskList;
	}

	public void setEndorsedApplicationDeskList(
			Collection<EndorsedApplicationDesk> endorsedApplicationDeskList) {
		this.endorsedApplicationDeskList = endorsedApplicationDeskList;
	}

	public Collection<ItemCategory> getApplyItemCategoryList() {
		return applyItemCategoryList;
	}

	public void setApplyItemCategoryList(Collection<ItemCategory> applyItemCategoryList) {
		this.applyItemCategoryList = applyItemCategoryList;
	}

	public void setApplicationWorkFlowListingForApproval(
			ArrayList<ApplicationWorkflow> awfList) {
		// TODO Auto-generated method stub
		this.awfList = awfList;
	}
	
	
	public ArrayList<ApplicationWorkflow> getApplicationWorkFlowListingForApproval() {
		// TODO Auto-generated method stub
		return this.awfList;
	}

	public Collection<Agency> getAgenciesAlreadyHandled() {
		return agenciesAlreadyHandled;
	}

	public void setAgenciesAlreadyHandled(Collection<Agency> agenciesAlreadyHandled) {
		this.agenciesAlreadyHandled = agenciesAlreadyHandled;
	}

	public PortalUser getAgencyApplicant() {
		return agencyApplicant;
	}

	public void setAgencyApplicant(PortalUser agencyApplicant) {
		this.agencyApplicant = agencyApplicant;
	}

	public void setCommentsONSAForward(String commentsonsa) {
		// TODO Auto-generated method stub
		this.commentsonsa = commentsonsa;
	}
	

	public String getCommentsONSAForward() {
		// TODO Auto-generated method stub
		return this.commentsonsa;
	}

	public Collection<ApplicationWorkflow> getAlreadyForwardedTo() {
		return alreadyForwardedTo;
	}

	public void setAlreadyForwardedTo(Collection<ApplicationWorkflow> alreadyForwardedTo) {
		this.alreadyForwardedTo = alreadyForwardedTo;
	}

	public Collection<ApplicationAttachmentAgency> getValidatedAttachments() {
		return validatedAttachments;
	}

	public void setValidatedAttachments(Collection<ApplicationAttachmentAgency> validatedAttachments) {
		this.validatedAttachments = validatedAttachments;
	}

	public BlackList getHotChocoloateAllowDip() {
		return hotChocoloateAllowDip;
	}

	public void setHotChocoloateAllowDip(BlackList hotChocoloateAllowDip) {
		this.hotChocoloateAllowDip = hotChocoloateAllowDip;
	}


	

	
	
}
