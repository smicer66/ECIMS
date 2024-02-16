package com.ecims.portlet.admin.countrystate;

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

public class CountryStatePortletState {

	private static Logger log = Logger.getLogger(CountryStatePortletState.class);
	private static CountryStatePortletUtil countrystatePortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	

	private String successMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	private VIEW_TABS currentTab;
	
	
	private Collection<Country> countryList;
	
	private String stateName;
	private Collection<State> stateList;
	private Collection<PortCode> portCodeList;
	private Country selectedCountry;
	private State selectedState;
	private PortCode selectedPortCode;
	private Boolean editMode;
	private String portCodeName;
	

	private String countryName;
	
	private String phoneCode;

	private String portstate;
	private String portCode;

	private String statecountry;
	private String statename;
	
	
	
	/****enum section****/
    
    
    
	public static enum ACTIONS{
		CREATE_NEW_COUNTRY, CREATE_NEW_STATE, CREATE_NEW_PORT_CODE, 
		MANAGE_COUNTRY, MANAGE_STATE, MANAGE_PORT_CODE
	}
	
	public static enum VIEW_TABS{
		VIEW_NEW_COUNTRY, VIEW_NEW_STATE, VIEW_NEW_PORT_CODE, VIEW_COUNTRY_LIST, VIEW_STATE_LIST, VIEW_PORT_CODE_LIST		
	}
	
	/*****APPLICATION CORE METHODS! DO NOT TAMPER WITH THIS SECTION OF CODE - START HERE*****/
	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			CountryStatePortletState portletState) {

		log.info("Eeeror Msg = " + errorMessage);
		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			CountryStatePortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			CountryStatePortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			CountryStatePortletState.log.debug("Error including error message", e);
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
	
	
	
	public static CountryStatePortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		CountryStatePortletState portletState = null;
		Logger.getLogger(CountryStatePortletState.class).info("------getInstance");

		ServiceLocator serviceLocator = ServiceLocator.getInstance();
		SwpService swpService = serviceLocator.getSwpService();
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (CountryStatePortletState) session.getAttribute(CountryStatePortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new CountryStatePortletState();
					CountryStatePortletUtil util = new CountryStatePortletUtil();
					portletState.setCountryStatePortletUtil(util);
					session.setAttribute(CountryStatePortletState.class.getName(), portletState);
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
			CountryStatePortletState portletState, SwpService swpService) {
		// TODO Auto-generated method stub
		Collection<Country> countryList = portletState.getCountryStatePortletUtil().getAllCountries();
		portletState.setCountryList(countryList);
		Collection<State> stateList = portletState.getCountryStatePortletUtil().getAllStates();
		portletState.setStateList(stateList);
	}

	/****core section starts here****/
	
	private void setCountryStatePortletUtil(CountryStatePortletUtil util) {
		// TODO Auto-generated method stub
		this.countrystatePortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, CountryStatePortletState portletState) {
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
				CountryStatePortletUtil util = CountryStatePortletUtil.getInstance();
				portletState.setCountryStatePortletUtil(util);
				
				ArrayList<RoleType> pur = null;
				
				if (request.getRemoteUser() != null
						&& !request.getRemoteUser().equals("")) {
					portletState.getCountryStatePortletUtil().
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


	public CountryStatePortletUtil getCountryStatePortletUtil() {
		// TODO Auto-generated method stub
		return this.countrystatePortletUtil;
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
	
	public Collection<Country> getCountryList() {
		return countryList;
	}

	public void setCountryList(Collection<Country> countryList) {
		this.countryList = countryList;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public Collection<State> getStateList() {
		return stateList;
	}

	public void setStateList(Collection<State> stateList) {
		this.stateList = stateList;
	}

	public String getPortCodeName() {
		return portCodeName;
	}

	public void setPortCodeName(String portCodeName) {
		this.portCodeName = portCodeName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getPhoneCode() {
		return phoneCode;
	}

	public void setPhoneCode(String phoneCode) {
		this.phoneCode = phoneCode;
	}

	public String getPortstate() {
		return portstate;
	}

	public void setPortstate(String portstate) {
		this.portstate = portstate;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getStatecountry() {
		return statecountry;
	}

	public void setStatecountry(String statecountry) {
		this.statecountry = statecountry;
	}

	public String getStatename() {
		return statename;
	}

	public void setStatename(String statename) {
		this.statename = statename;
	}

	public Collection<PortCode> getPortCodeList() {
		return portCodeList;
	}

	public void setPortCodeList(Collection<PortCode> portCodeList) {
		this.portCodeList = portCodeList;
	}

	public Boolean getEditMode() {
		return editMode;
	}

	public void setEditMode(Boolean editMode) {
		this.editMode = editMode;
	}

	public Country getSelectedCountry() {
		return selectedCountry;
	}

	public void setSelectedCountry(Country selectedCountry) {
		this.selectedCountry = selectedCountry;
	}

	public State getSelectedState() {
		return selectedState;
	}

	public void setSelectedState(State selectedState) {
		this.selectedState = selectedState;
	}

	public PortCode getSelectedPortCode() {
		return selectedPortCode;
	}

	public void setSelectedPortCode(PortCode selectedPortCode) {
		this.selectedPortCode = selectedPortCode;
	}


	
	
}
