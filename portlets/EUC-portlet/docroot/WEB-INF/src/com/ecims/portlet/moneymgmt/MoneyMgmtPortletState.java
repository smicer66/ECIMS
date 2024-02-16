package com.ecims.portlet.moneymgmt;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.apache.log4j.Logger;

import smartpay.entity.Agency;
import smartpay.entity.Country;
import smartpay.entity.EndorsementDesk;
import smartpay.entity.PortalUser;
import smartpay.entity.Currency;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.State;
import smartpay.service.SwpService;

import com.ecims.commins.Util;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.sf.primepay.smartpay13.ServiceLocator;

public class MoneyMgmtPortletState {

	private static Logger log = Logger.getLogger(MoneyMgmtPortletState.class);
	private static MoneyMgmtPortletUtil moneyMgmtPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	

	private String successMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	private VIEW_TABS currentTab;
	
	private String currencyName;
	private String htmlName;
	private String selectedCurrencyId;
	private Currency agencyCurrency;
	private Currency currencyEntity;
	private Collection<Currency> currencyListing;

	
	
	
	
	
	/****enum section****/
    
    
    
	public static enum CURRENCY_ACTIONS{
		CREATE_NEW_CURRENCY, EDIT_A_CURRENCY, DELETE_A_CURRENCY, HANDLE_ACTION_ON_CURRENCY
		
	}
	
	public static enum VIEW_TABS{
		VIEW_CREATE_NEW_CURRENCY, VIEW_EDIT_A_CURRENCY, VIEW_ALL_CURRENCY
		
	}
	
	/*****APPLICATION CORE METHODS! DO NOT TAMPER WITH THIS SECTION OF CODE - START HERE*****/
	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			MoneyMgmtPortletState portletState) {
		log.info("Eeeror Msg = " + errorMessage);
		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			MoneyMgmtPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			MoneyMgmtPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			MoneyMgmtPortletState.log.debug("Error including error message", e);
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
	
	
	
	public static MoneyMgmtPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		MoneyMgmtPortletState portletState = null;
		Logger.getLogger(MoneyMgmtPortletState.class).info("------getInstance");

		ServiceLocator serviceLocator = ServiceLocator.getInstance();
		SwpService swpService = serviceLocator.getSwpService();
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (MoneyMgmtPortletState) session.getAttribute(MoneyMgmtPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new MoneyMgmtPortletState();
					MoneyMgmtPortletUtil util = new MoneyMgmtPortletUtil();
					portletState.setMoneyMgmtPortletUtil(util);
					session.setAttribute(MoneyMgmtPortletState.class.getName(), portletState);
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
			MoneyMgmtPortletState portletState, SwpService swpService) {
		// TODO Auto-generated method stub
	}

	/****core section starts here****/
	
	private void setMoneyMgmtPortletUtil(MoneyMgmtPortletUtil util) {
		// TODO Auto-generated method stub
		this.moneyMgmtPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, MoneyMgmtPortletState portletState) {
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
				MoneyMgmtPortletUtil util = MoneyMgmtPortletUtil.getInstance();
				portletState.setMoneyMgmtPortletUtil(util);
				
				ArrayList<RoleType> pur = null;
				
				if (request.getRemoteUser() != null
						&& !request.getRemoteUser().equals("")) {
					portletState.getMoneyMgmtPortletUtil().
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


	public MoneyMgmtPortletUtil getMoneyMgmtPortletUtil() {
		// TODO Auto-generated method stub
		return this.moneyMgmtPortletUtil;
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

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getSelectedCurrencyId() {
		return selectedCurrencyId;
	}

	public void setSelectedCurrencyId(String selectedCurrencyId) {
		this.selectedCurrencyId = selectedCurrencyId;
	}

	public String getHtmlName() {
		return htmlName;
	}

	public void setHtmlName(String htmlName) {
		this.htmlName = htmlName;
	}

	public Collection<Currency> getCurrencyListing() {
		return currencyListing;
	}

	public void setCurrencyListing(Collection<Currency> currencyListing) {
		this.currencyListing = currencyListing;
	}

	public Currency getAgencyCurrency() {
		return agencyCurrency;
	}

	public void setAgencyCurrency(Currency agencyCurrency) {
		this.agencyCurrency = agencyCurrency;
	}

	public smartpay.entity.Currency getCurrencyEntity() {
		return currencyEntity;
	}

	public void setCurrencyEntity(smartpay.entity.Currency ed) {
		this.currencyEntity = ed;
	}

	
	
}
