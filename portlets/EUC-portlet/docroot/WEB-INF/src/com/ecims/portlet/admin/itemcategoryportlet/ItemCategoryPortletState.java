package com.ecims.portlet.admin.itemcategoryportlet;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;

import smartpay.entity.Agency;
import smartpay.entity.ApplicationAttachmentType;
import smartpay.entity.Country;
import smartpay.entity.Currency;
import smartpay.entity.EndorsementDesk;
import smartpay.entity.ItemCategory;
import smartpay.entity.ItemCategoryApplicantType;
import smartpay.entity.ItemCategorySub;
import smartpay.entity.PortalUser;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.State;
import smartpay.entity.enumerations.ApplicantType;
import smartpay.service.SwpService;

import com.ecims.commins.Util;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.sf.primepay.smartpay13.ServiceLocator;

public class ItemCategoryPortletState {

	private static Logger log = Logger.getLogger(ItemCategoryPortletState.class);
	private static ItemCategoryPortletUtil itemCategoryPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	
	private Collection<ItemCategory> itemCategoryListing;
	private Collection<ItemCategorySub> itemCategorySubListing;
	private Collection<ApplicationAttachmentType> applicationAttachmentTypeListing;
	private Collection<Agency> agencyListing;
	

	private String successMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	private VIEW_TABS currentTab;
	
	private Collection<String> applicationAttachmentList;
	private String itemCategoryName;
	private Collection<ApplicantType> applicantTypeList;
	
	private Collection<ApplicantType> applicanttype;
	private Collection<String> attachmenttype;
	private String categoryname;
	private ItemCategory itemCategoryEntity;
	private Collection<ItemCategoryApplicantType> itemCategoryApplicantList;
	private Collection<Currency> currencyList;
	private HashMap<String, List<String>> selectedAgency;
	private Collection<String> compulsory;
	
	
	private String itemSubCategoryName;
	private String itemSubCategoryHSCode;
	private ItemCategorySub itemCategorySubEntity;
	private Collection<ItemCategory> allIC;
	
	
	/****enum section****/
    
    
    
	public static enum ITEM_CATEGORY_ACTIONS{
		CREATE_NEW_ITEM_CATEGORY, CREATE_NEW_ITEM_SUB_CATEGORY, 
		EDIT_NEW_ITEM_CATEGORY, EDIT_NEW_ITEM_SUB_CATEGORY,
		HANDLE_ITEM_CATEGORY_ACTIONS, HANDLE_ITEM_SUB_CATEGORY_ACTIONS
	}
	
	public static enum VIEW_TABS{
		VIEW_NEW_ITEM_CATEGORY, VIEW_NEW_ITEM_SUB_CATEGORY, VIEW_ALL_ITEM_CATEGORY, VIEW_ALL_ITEM_SUB_CATEGORY
	}
	
	/*****APPLICATION CORE METHODS! DO NOT TAMPER WITH THIS SECTION OF CODE - START HERE*****/
	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			ItemCategoryPortletState portletState) {

		log.info("Eeeror Msg = " + errorMessage);
		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			ItemCategoryPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			ItemCategoryPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			ItemCategoryPortletState.log.debug("Error including error message", e);
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
	
	
	
	public static ItemCategoryPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		ItemCategoryPortletState portletState = null;
		Logger.getLogger(ItemCategoryPortletState.class).info("------getInstance");

		ServiceLocator serviceLocator = ServiceLocator.getInstance();
		SwpService swpService = serviceLocator.getSwpService();
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (ItemCategoryPortletState) session.getAttribute(ItemCategoryPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new ItemCategoryPortletState();
					ItemCategoryPortletUtil util = new ItemCategoryPortletUtil();
					portletState.setItemCategoryPortletUtil(util);
					session.setAttribute(ItemCategoryPortletState.class.getName(), portletState);
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
			ItemCategoryPortletState portletState, SwpService swpService) {
		// TODO Auto-generated method stub
		Collection<ItemCategory> itemCList = portletState.getItemCategoryPortletUtil().getAllItemCategory();
		portletState.setItemCategoryListing(itemCList);
		portletState.setAgencyListing((Collection<Agency>)portletState.getItemCategoryPortletUtil().getAllEntity(Agency.class));
	}

	/****core section starts here****/
	
	private void setItemCategoryPortletUtil(ItemCategoryPortletUtil util) {
		// TODO Auto-generated method stub
		this.itemCategoryPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, ItemCategoryPortletState portletState) {
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
				ItemCategoryPortletUtil util = ItemCategoryPortletUtil.getInstance();
				portletState.setItemCategoryPortletUtil(util);
				
				ArrayList<RoleType> pur = null;
				
				if (request.getRemoteUser() != null
						&& !request.getRemoteUser().equals("")) {
					portletState.getItemCategoryPortletUtil().
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


	public ItemCategoryPortletUtil getItemCategoryPortletUtil() {
		// TODO Auto-generated method stub
		return this.itemCategoryPortletUtil;
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

	public Collection<ItemCategory> getItemCategoryListing() {
		return itemCategoryListing;
	}

	public void setItemCategoryListing(Collection<ItemCategory> itemCategoryListing) {
		this.itemCategoryListing = itemCategoryListing;
	}

	public Collection<ItemCategorySub> getItemCategorySubListing() {
		return itemCategorySubListing;
	}

	public void setItemCategorySubListing(Collection<ItemCategorySub> itemCategorySubListing) {
		this.itemCategorySubListing = itemCategorySubListing;
	}

	public Collection<ApplicationAttachmentType> getApplicationAttachmentTypeListing() {
		return applicationAttachmentTypeListing;
	}

	public void setApplicationAttachmentTypeListing(
			Collection<ApplicationAttachmentType> applicationAttachmentTypeListing) {
		this.applicationAttachmentTypeListing = applicationAttachmentTypeListing;
	}

	public Collection<String> getApplicationAttachmentList() {
		return applicationAttachmentList;
	}

	public void setApplicationAttachmentList(
			Collection<String> applicationAttachmentList) {
		this.applicationAttachmentList = applicationAttachmentList;
	}

	public Collection<ApplicantType> getApplicantTypeList() {
		return applicantTypeList;
	}

	public void setApplicantTypeList(Collection<ApplicantType> applicantTypeList) {
		this.applicantTypeList = applicantTypeList;
	}
	public String getItemCategoryName() {
		return itemCategoryName;
	}

	public void setItemCategoryName(String itemCategoryName) {
		this.itemCategoryName = itemCategoryName;
	}

	public Collection<ApplicantType> getApplicanttype() {
		return applicanttype;
	}

	public void setApplicanttype(Collection<ApplicantType> applicanttype) {
		this.applicanttype = applicanttype;
	}

	public Collection<String> getAttachmenttype() {
		return attachmenttype;
	}

	public void setAttachmenttype(Collection<String> attachmenttype) {
		this.attachmenttype = attachmenttype;
	}

	public String getCategoryname() {
		return categoryname;
	}

	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
	}

	public ItemCategory getItemCategoryEntity() {
		return itemCategoryEntity;
	}

	public void setItemCategoryEntity(ItemCategory itemCategoryEntity) {
		this.itemCategoryEntity = itemCategoryEntity;
	}

	public Collection<ItemCategoryApplicantType> getItemCategoryApplicantList() {
		return itemCategoryApplicantList;
	}

	public void setItemCategoryApplicantList(
			Collection<ItemCategoryApplicantType> itemCategoryApplicantList) {
		this.itemCategoryApplicantList = itemCategoryApplicantList;
	}

	public void setCurrencyListing(Collection<Currency> currencyList) {
		// TODO Auto-generated method stub
		this.currencyList = currencyList;
	}
	
	public Collection<Currency> getCurrencyListing() {
		// TODO Auto-generated method stub
		return this.currencyList;
	}


	public Collection<Agency> getAgencyListing() {
		return agencyListing;
	}

	public void setAgencyListing(Collection<Agency> agencyListing) {
		this.agencyListing = agencyListing;
	}

	public HashMap<String, List<String>> getSelectedAgency() {
		return selectedAgency;
	}

	public void setSelectedAgency(HashMap<String, List<String>> selectedAgency) {
		this.selectedAgency = selectedAgency;
	}

	public Collection<String> getCompulsory() {
		return compulsory;
	}

	public void setCompulsory(Collection<String> compulsory) {
		this.compulsory = compulsory;
	}

	public String getItemSubCategoryName() {
		return itemSubCategoryName;
	}

	public void setItemSubCategoryName(String itemSubCategoryName) {
		this.itemSubCategoryName = itemSubCategoryName;
	}

	public String getItemSubCategoryHSCode() {
		return itemSubCategoryHSCode;
	}

	public void setItemSubCategoryHSCode(String itemSubCategoryHSCode) {
		this.itemSubCategoryHSCode = itemSubCategoryHSCode;
	}

	public void setItemCategorySubEntity(ItemCategorySub ics) {
		// TODO Auto-generated method stub
		this.itemCategorySubEntity = ics;
	}
	
	public ItemCategorySub getItemCategorySubEntity() {
		// TODO Auto-generated method stub
		return this.itemCategorySubEntity;
	}

	public void setApplyItemCategoryList(Collection<ItemCategory> allIC) {
		// TODO Auto-generated method stub
		this.allIC = allIC;
	}
	
	public Collection<ItemCategory> getApplyItemCategoryList() {
		// TODO Auto-generated method stub
		return this.allIC;
	}

	
	
}
