package com.ecims.portlet.reportportlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.apache.log4j.Logger;

import smartpay.entity.Agency;
import smartpay.entity.Application;
import smartpay.entity.Certificate;
import smartpay.entity.Company;
import smartpay.entity.Country;
import smartpay.entity.ItemCategory;
import smartpay.entity.PortCode;
import smartpay.entity.PortalUser;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.State;
import smartpay.service.SwpService;

import com.ecims.commins.Util;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.sf.primepay.smartpay13.ServiceLocator;

public class ReportPortletState {

	private static Logger log = Logger.getLogger(ReportPortletState.class);
	private static ReportPortletUtil reportPortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private String remoteIPAddress;
	private ArrayList<RoleType> portalUserRoleType;
	private String successMessage;
	private String errorMessage;
	private String filName;
	private ArrayList<String> errorList = new ArrayList<String>();  
	private String selectedReportType;
	
	
	/*************SETTINGS************************/
	private String startDate;
	private String endDate;
	private String applicationNumber;
	private String applicantNumber;
	private String exceptionType;
	private String applicationStatus;
	private String countrySelected;
	private String itemCategory;
	private String portOfEntry;
	private Collection<PortCode> portList;
	private Collection<Country> countryList;
	private Collection<ItemCategory> itemCategoryList;
	private String showApplicationNumber;
	private String showDateCreated;
	private String showStatus;
	private String showException;
	private String showItemCategory;
	private String showApplicantName;
	private String showImportationPort;
	private String amountLowerLimit;
	private String amountUpperLimit;
	private String defaultColumnShow;
	private String reportEmailSend;
	private Collection<Application> appList;
	
	private String certificateStatus;
	private String showValidityPeriod;
	private String showApplicantNumber;
	private String showCertificateStatus;
	private String showImportationCosts;
	private Collection<Certificate> certList;
	
	private String dobStartDate; 
	private String dobEndDate;
	private String gender;
	private Collection<State> stateList;
	private String maritalStatus;
	private String selectedState;
	private String agencySelected;
	private String selectedRoleType;
	private String userStatus;
	private String showFullName;
	private String showAddress;
	private String showDOB;
	private String showEmailAddress;
	private String showMobileNumber;
	private String showMaritalStatus;
	private String showStateOfOrigin;
	private String showUserAgency;
	private String showUserRole;
	private String showApplicantType;
	private Collection<Agency> agencyList;
	private Collection<PortalUser> portalUserList;
	



	public String getReportEmailSend() {
		return reportEmailSend;
	}


	public void setReportEmailSend(String reportEmailSend) {
		this.reportEmailSend = reportEmailSend;
	}



	public String getAmountLowerLimit() {
		return amountLowerLimit;
	}


	public void setAmountLowerLimit(String amountLowerLimit) {
		this.amountLowerLimit = amountLowerLimit;
	}

    
    
    
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	public static enum REPORTING_ACTIONS{
		CREATE_A_REPORT_STEP_ONE,
		CREATE_A_PAYMENT_REPORT_STEP_TWO, 
		CREATE_AN_ASSESSMENT_REPORT_STEP_TWO, 
		LOGIN_STEP_TWO, 
		CREATE_A_REPORT_STEP_TWO, CREATE_AN_APPLICATION_REPORT_STEP_TWO, CREATE_A_CERTIFICATE_REPORT, 
		CREATE_AN_APPLICANT_REPORT_STEP_TWO
	}
	
	/****core section starts here****/
	public static ReportPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		ReportPortletState portletState = null;
		Logger.getLogger(ReportPortletState.class).info("------getInstance");
		try {
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (ReportPortletState) session.getAttribute(ReportPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new ReportPortletState();
					ReportPortletUtil util = new ReportPortletUtil();
					portletState.setReportPortletUtil(util);
					session.setAttribute(ReportPortletState.class.getName(), portletState);
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
	
	
	public void setReportPortletUtil(ReportPortletUtil util) {
		// TODO Auto-generated method stub
		this.reportPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, ReportPortletState portletState) {
		// TODO Auto-generated method stub
		com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
		log.info("------set default init");
		try
		{
		
			if (request.getRemoteUser() != null
					&& !request.getRemoteUser().equals("")) {
				portletState.setCurrentRemoteUserId(request.getRemoteUser());
				log.info(">>>Remote user durin default init: " + portletState.getCurrentRemoteUserId());
				log.info("request.getRemoteUser() =" + request.getRemoteUser());
				log.info("request.getRemoteUser() =" + swpCustomService);
				Long orbitaId = Long.parseLong(request.getRemoteUser());
				portletState.setPortalUser((PortalUser) swpCustomService
						.getPortalUserByOrbitaId(Long.toString(orbitaId)));
				portletState.setRemoteIPAddress(PortalUtil.getHttpServletRequest(request).getRemoteAddr());
				reinitiliazeForApplication(portletState);
				
//				ReportPortletUtil util = ReportPortletUtil.getInstance();
//				portletState.setReportPortletUtil(util);
//				
//				ArrayList<RoleType> pur = portletState.getReportPortletUtil().
//						getRoleTypeByPortalUser(portletState.getPortalUser());	
//				portletState.setPortalUserRoleType(pur);
				//loadWorkFlowsForCompany(portletState);
			}else{
				log.info(">>>Remote user durin default init2: " + portletState.getCurrentRemoteUserId());
			}
		}
		catch(Exception ex)
		{
			log.error("", ex);
		} finally {
			
			
		}
	}
	
	
	




	public static void reinitiliazeForApplication(ReportPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setStartDate(null);
		portletState.setEndDate(null);
		portletState.setApplicationNumber(null);
		portletState.setApplicantNumber(null);
		portletState.setAmountLowerLimit(null);
		portletState.setCertificateStatus(null);
		portletState.setAmountUpperLimit(null);
		portletState.setShowApplicantNumber(null);
		portletState.setShowApplicationNumber(null);
		portletState.setShowValidityPeriod(null);
		portletState.setShowCertificateStatus(null);
		portletState.setShowImportationCosts(null);
		portletState.setReportEmailSend(null);
		portletState.setDobStartDate(null);
		portletState.setDobEndDate(null);
    	portletState.setGender(null);
		portletState.setMaritalStatus(null);
		portletState.setSelectedState(null);
		portletState.setAgencySelected(null);
		portletState.setSelectedRoleType(null);
		portletState.setUserStatus(null);
		portletState.setShowFullName(null);
		portletState.setShowAddress(null);
		portletState.setShowDOB(null);
		portletState.setShowEmailAddress(null);
		portletState.setShowMobileNumber(null);
		portletState.setShowMaritalStatus(null);
		portletState.setShowStateOfOrigin(null);
		portletState.setShowUserAgency(null);
		portletState.setShowUserRole(null);
		portletState.setShowApplicantType(null);
		portletState.setStartDate(null);
		portletState.setEndDate(null);
		portletState.setApplicationNumber(null);
		portletState.setApplicantNumber(null);
		portletState.setExceptionType(null);
		portletState.setApplicationStatus(null);
		portletState.setCountrySelected(null);
		portletState.setItemCategory(null);
		portletState.setShowApplicationNumber(null);
		portletState.setShowDateCreated(null);
		portletState.setDefaultColumnShow(null);
		portletState.setShowApplicantName(null);
		portletState.setShowApplicationNumber(null);
		portletState.setShowDateCreated(null);
		portletState.setShowException(null);
		portletState.setShowStatus(null);
		portletState.setShowImportationPort(null);
		portletState.setShowItemCategory(null);
		portletState.setReportEmailSend(null);
	}


	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			ReportPortletState portletState) {

		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			ReportPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			ReportPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			ReportPortletState.log.debug("Error including error message", e);
		}
	}
	
	public void setSuccessMessage(String successMessage)
	{
		this.successMessage=successMessage;
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


	private void setPortalUser(PortalUser portalUser) {
		// TODO Auto-generated method stub
		this.portalUser = portalUser;
	}
	
	public PortalUser getPortalUser() {
		// TODO Auto-generated method stub
		return this.portalUser;
	}


	public ReportPortletUtil getReportPortletUtil() {
		// TODO Auto-generated method stub
		return this.reportPortletUtil;
	}


	public ArrayList<RoleType> getPortalUserRoleType() {
		return portalUserRoleType;
	}


	public void setPortalUserRoleType(ArrayList<RoleType> portalUserRoleType) {
		this.portalUserRoleType = portalUserRoleType;
	}
	
	

	/****company creation section starts here****/
	

	public ArrayList<String> getErrorList() {
		return errorList;
	}


	public void setErrorList(ArrayList<String> errorList) {
		this.errorList = errorList;
	}
	
	

	public String getRemoteIPAddress() {
		return remoteIPAddress;
	}

	public void setRemoteIPAddress(String remoteIPAddress) {
		this.remoteIPAddress = remoteIPAddress;
	}

	
	

	public void reinitializeForTaxBreakDown(
			ReportPortletState portletState) {
		// TODO Auto-generated method stub
		
	}




	public String getStartDate() {
		return startDate;
	}


	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}


	public String getEndDate() {
		return endDate;
	}


	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}




	public String getDefaultColumnShow() {
		return defaultColumnShow;
	}


	public void setDefaultColumnShow(String defaultColumnShow) {
		this.defaultColumnShow = defaultColumnShow;
	}


	public String getFilName() {
		return filName;
	}


	public void setFilName(String filName) {
		this.filName = filName;
	}


	public String getApplicationNumber() {
		return applicationNumber;
	}


	public void setApplicationNumber(String applicationNumber) {
		this.applicationNumber = applicationNumber;
	}


	public String getApplicantNumber() {
		return applicantNumber;
	}


	public void setApplicantNumber(String applicantNumber) {
		this.applicantNumber = applicantNumber;
	}


	public String getExceptionType() {
		return exceptionType;
	}


	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}


	public String getApplicationStatus() {
		return applicationStatus;
	}


	public void setApplicationStatus(String applicationStatus) {
		this.applicationStatus = applicationStatus;
	}


	public String getCountrySelected() {
		return countrySelected;
	}


	public void setCountrySelected(String countrySelected) {
		this.countrySelected = countrySelected;
	}


	public String getItemCategory() {
		return itemCategory;
	}


	public void setItemCategory(String itemCategory) {
		this.itemCategory = itemCategory;
	}


	public String getShowApplicationNumber() {
		return showApplicationNumber;
	}


	public void setShowApplicationNumber(String showApplicationNumber) {
		this.showApplicationNumber = showApplicationNumber;
	}


	public String getShowDateCreated() {
		return showDateCreated;
	}


	public void setShowDateCreated(String showDateCreated) {
		this.showDateCreated = showDateCreated;
	}


	public String getPortOfEntry() {
		return portOfEntry;
	}


	public void setPortOfEntry(String portOfEntry) {
		this.portOfEntry = portOfEntry;
	}
	
	
	public Collection<PortCode> getPortList() {
		return portList;
	}


	public void setPortList(Collection<PortCode> portList) {
		this.portList = portList;
	}


	public Collection<Country> getCountryList() {
		return countryList;
	}


	public void setCountryList(Collection<Country> countryList) {
		this.countryList = countryList;
	}


	public Collection<ItemCategory> getItemCategoryList() {
		return itemCategoryList;
	}


	public void setItemCategoryList(Collection<ItemCategory> itemCategoryList) {
		this.itemCategoryList = itemCategoryList;
	}


	public String getAmountUpperLimit() {
		return amountUpperLimit;
	}


	public void setAmountUpperLimit(String amountUpperLimit) {
		this.amountUpperLimit = amountUpperLimit;
	}


	public void setApplicationList(Collection<Application> appList) {
		// TODO Auto-generated method stub
		this.appList = appList;
	}
	
	public Collection<Application> getApplicationList() {
		// TODO Auto-generated method stub
		return this.appList;
	}


	public String getShowStatus() {
		return showStatus;
	}


	public void setShowStatus(String showStatus) {
		this.showStatus = showStatus;
	}


	public String getShowException() {
		return showException;
	}


	public void setShowException(String showException) {
		this.showException = showException;
	}


	public String getShowItemCategory() {
		return showItemCategory;
	}


	public void setShowItemCategory(String showItemCategory) {
		this.showItemCategory = showItemCategory;
	}


	public String getShowApplicantName() {
		return showApplicantName;
	}


	public void setShowApplicantName(String showApplicantName) {
		this.showApplicantName = showApplicantName;
	}


	public String getShowImportationPort() {
		return showImportationPort;
	}


	public void setShowImportationPort(String showImportationPort) {
		this.showImportationPort = showImportationPort;
	}


	public String getSelectedReportType() {
		return selectedReportType;
	}


	public void setSelectedReportType(String selectedReportType) {
		this.selectedReportType = selectedReportType;
	}


	public String getCertificateStatus() {
		return certificateStatus;
	}


	public void setCertificateStatus(String certificateStatus) {
		this.certificateStatus = certificateStatus;
	}


	public String getShowValidityPeriod() {
		return showValidityPeriod;
	}


	public void setShowValidityPeriod(String showValidityPeriod) {
		this.showValidityPeriod = showValidityPeriod;
	}


	public String getShowApplicantNumber() {
		return showApplicantNumber;
	}


	public void setShowApplicantNumber(String showApplicantNumber) {
		this.showApplicantNumber = showApplicantNumber;
	}


	public String getShowCertificateStatus() {
		return showCertificateStatus;
	}


	public void setShowCertificateStatus(String showCertificateStatus) {
		this.showCertificateStatus = showCertificateStatus;
	}


	public String getShowImportationCosts() {
		return showImportationCosts;
	}


	public void setShowImportationCosts(String showImportationCosts) {
		this.showImportationCosts = showImportationCosts;
	}


	public void setCertificateList(Collection<Certificate> certList) {
		// TODO Auto-generated method stub
		this.certList = certList;
	}
	
	public Collection<Certificate> getCertificateList() {
		// TODO Auto-generated method stub
		return this.certList;
	}


	public String getDobStartDate() {
		return dobStartDate;
	}


	public void setDobStartDate(String dobStartDate) {
		this.dobStartDate = dobStartDate;
	}


	public String getDobEndDate() {
		return dobEndDate;
	}


	public void setDobEndDate(String dobEndDate) {
		this.dobEndDate = dobEndDate;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}



	public String getSelectedState() {
		return selectedState;
	}


	public void setSelectedState(String selectedState) {
		this.selectedState = selectedState;
	}


	public String getSelectedRoleType() {
		return selectedRoleType;
	}


	public void setSelectedRoleType(String selectedRoleType) {
		this.selectedRoleType = selectedRoleType;
	}


	public String getUserStatus() {
		return userStatus;
	}


	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}


	public String getShowFullName() {
		return showFullName;
	}


	public void setShowFullName(String showFullName) {
		this.showFullName = showFullName;
	}


	public String getShowAddress() {
		return showAddress;
	}


	public void setShowAddress(String showAddress) {
		this.showAddress = showAddress;
	}


	public String getShowDOB() {
		return showDOB;
	}


	public void setShowDOB(String showDOB) {
		this.showDOB = showDOB;
	}


	public String getShowEmailAddress() {
		return showEmailAddress;
	}


	public void setShowEmailAddress(String showEmailAddress) {
		this.showEmailAddress = showEmailAddress;
	}


	public String getShowMobileNumber() {
		return showMobileNumber;
	}


	public void setShowMobileNumber(String showMobileNumber) {
		this.showMobileNumber = showMobileNumber;
	}


	public String getShowMaritalStatus() {
		return showMaritalStatus;
	}


	public void setShowMaritalStatus(String showMaritalStatus) {
		this.showMaritalStatus = showMaritalStatus;
	}


	public String getShowStateOfOrigin() {
		return showStateOfOrigin;
	}


	public void setShowStateOfOrigin(String showStateOfOrigin) {
		this.showStateOfOrigin = showStateOfOrigin;
	}


	public String getShowUserAgency() {
		return showUserAgency;
	}


	public void setShowUserAgency(String showUserAgency) {
		this.showUserAgency = showUserAgency;
	}


	public String getShowUserRole() {
		return showUserRole;
	}


	public void setShowUserRole(String showUserRole) {
		this.showUserRole = showUserRole;
	}


	public String getShowApplicantType() {
		return showApplicantType;
	}


	public void setShowApplicantType(String showApplicantType) {
		this.showApplicantType = showApplicantType;
	}

	public String getAgencySelected() {
		return agencySelected;
	}


	public void setAgencySelected(String agencySelected) {
		this.agencySelected = agencySelected;
	}


	public Collection<State> getStateList() {
		return stateList;
	}


	public void setStateList(Collection<State> stateList) {
		this.stateList = stateList;
	}


	public String getMaritalStatus() {
		return maritalStatus;
	}


	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}


	public Collection<Agency> getAgencyList() {
		return agencyList;
	}


	public void setAgencyList(Collection<Agency> agencyList) {
		this.agencyList = agencyList;
	}


	public Collection<PortalUser> getPortalUserList() {
		return portalUserList;
	}


	public void setPortalUserList(Collection<PortalUser> portalUserList) {
		this.portalUserList = portalUserList;
	}

	
	
}
