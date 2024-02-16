package com.ecims.portlet.applicant;

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
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.sf.primepay.smartpay13.ServiceLocator;

public class ApplicantPortletState {

	private static Logger log = Logger.getLogger(ApplicantPortletState.class);
	private static ApplicantPortletUtil applicantPortletUtil;
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
	private String firstName;
	private String lastName;
	private String otherName;
	private String email;
	private String mobile;
	private String countryCode;
	private String gender;
	private String dob;
	private String maritalStatus;
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String nationality;
	private String identificationType;
	private String identificationNumber;
	private String identificationFileName;
	private String identificationExpiryDate;
	private String nextOfKinName;
	private String nextOfKinAddress;
	private String nextOfKinRelationship;		//o:Initiator 1:Approver
	private String nextOfKinPhoneNumber;		//o:Initiator 1:Approver
	private String passportPhoto;
	private String residencePhoneNumber;
	private String residenceState;
	private String residenceCity;
	private String issueDate;
	private String placeOfIssue;
	private String designation;
	
	
	
	
	
	private String companyName;
	private String companyAddress;
	private String companyState;
	private String companyPhoneNumber;
	private String companyEmailAddress;
	private String websiteUrl;
	private String registrationNumber;
	private String dateOfIncorporation;
	private String companyLogo;
	private String cacCertificate;
	
	
	
	private String activationEmail;
	private PortalUser activationPortalUser;
	
	/****enum section****/
    
    
    
	public static enum APPLICANT_ACTIONS{
		CREATE_A_PORTAL_USER_STEP_ONE, CREATE_A_PORTAL_USER_STEP_TWO, CREATE_A_PORTAL_USER_STEP_THREE, CREATE_A_PORTAL_USER_STEP_FOUR,
		LIST_PORTAL_USERS, UPDATE_A_PORTAL_USER_STEP_ONE, UPDATE_A_PORTAL_USER_STEP_THREE, 
		VIEW_A_PORTAL_USER_ACTION, LOGIN_STEP_TWO, 
		CREATE_A_CORPORATE_PORTAL_USER_STEP_ONE, CREATE_A_CORPORATE_PORTAL_USER_STEP_TWO, CREATE_A_CORPORATE_PORTAL_USER_STEP_THREE, 
		CREATE_A_CORPORATE_PORTAL_USER_STEP_FOUR, ACTIVATE_PORTAL_USER_ACCOUNT, CREATE_NEW_PASSWORD_PORTAL_USER_ACCOUNT
	}
	
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	
	/*****APPLICATION CORE METHODS! DO NOT TAMPER WITH THIS SECTION OF CODE - START HERE*****/
	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			ApplicantPortletState portletState) {

		log.info("Eeeror Msg = " + errorMessage);
		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			ApplicantPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			ApplicantPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			ApplicantPortletState.log.debug("Error including error message", e);
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
	
	
	
	public static ApplicantPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		ApplicantPortletState portletState = null;
		Logger.getLogger(ApplicantPortletState.class).info("------getInstance");
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (ApplicantPortletState) session.getAttribute(ApplicantPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				if (portletState == null) {
					portletState = new ApplicantPortletState();
					ApplicantPortletUtil util = new ApplicantPortletUtil();
					portletState.setApplicantPortletUtil(util);
					session.setAttribute(ApplicantPortletState.class.getName(), portletState);
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
			ApplicantPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setAccountType(null);
		portletState.setAddressLine1(null);
		portletState.setAddressLine2(null);
		portletState.setCity(null);
		portletState.setEmail(null);
		portletState.setFirstName(null);
		portletState.setLastName(null);
		portletState.setOtherName(null);
		portletState.setGender(null);
		portletState.setMaritalStatus(null);
		portletState.setIdentificationFileName(null);
		portletState.setIdentificationNumber(null);
		portletState.setIdentificationType(null);
		portletState.setMobile(null);
		portletState.setNextOfKinAddress(null);
		portletState.setNextOfKinName(null);
		portletState.setNextOfKinPhoneNumber(null);
		portletState.setNextOfKinRelationship(null);
		portletState.setState(null);
		portletState.setPassportPhoto(null);
		portletState.setIdentificationExpiryDate(null);
		portletState.setDateOfIncorporation(null);
		portletState.setDob(null);
		portletState.setCacCertificate(null);
		portletState.setCompanyAddress(null);
		portletState.setCompanyLogo(null);
		portletState.setCompanyName(null);
		portletState.setCompanyPhoneNumber(null);
		portletState.setCompanyState(null);
		portletState.setCountryCode(null);
		portletState.setCompanyEmailAddress(null);
		portletState.setIdentificationExpiryDate(null);
		portletState.setPlaceOfIssue(null);
		portletState.setRegistrationNumber(null);
		portletState.setWebsiteUrl(null);
		
	}
	
	
	
	
	private void setApplicantPortletUtil(ApplicantPortletUtil util) {
		// TODO Auto-generated method stub
		this.applicantPortletUtil = util;
	}


	private static void defaultInit(PortletRequest request, ApplicantPortletState portletState) {
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
				ApplicantPortletUtil util = ApplicantPortletUtil.getInstance();
				portletState.setApplicantPortletUtil(util);
				
				ArrayList<RoleType> pur = null;
				
				if (request.getRemoteUser() != null
						&& !request.getRemoteUser().equals("")) {
					portletState.getApplicantPortletUtil().
							getRoleTypeByPortalUser(portletState.getPortalUser());	
				}		

				portletState.setPortalUserRoleType(pur);
				
				portletState.reinitializeForCreateCorporateIndividual(portletState);
				portletState.setAllCountry(portletState.getApplicantPortletUtil().getAllCountries());
				portletState.setAllState(portletState.getApplicantPortletUtil().getAllStates());
				
				
				
				
				portletState.setSendingEmail(portletState.getApplicantPortletUtil().getSettingsByName("SENDING EMAIL ADDRESS"));
				portletState.setSendingEmailPassword(portletState.getApplicantPortletUtil().getSettingsByName("SENDING EMAIL PASSWORD"));
				portletState.setSendingEmailPort(portletState.getApplicantPortletUtil().getSettingsByName("SENDING EMAIL PORT"));
				portletState.setSendingEmailUsername(portletState.getApplicantPortletUtil().getSettingsByName("SENDING EMAIL USERNAME"));
				portletState.setMobileApplicationName(portletState.getApplicantPortletUtil().getSettingsByName("MOBILE APPLICATION NAME"));
				portletState.setProxyHost(portletState.getApplicantPortletUtil().getSettingsByName("PROXY HOST"));
				portletState.setProxyPort(portletState.getApplicantPortletUtil().getSettingsByName("PROXY PORT"));
				portletState.setApplicationName(portletState.getApplicantPortletUtil().getSettingsByName("APPLICATION NAME"));
				portletState.setSystemUrl(portletState.getApplicantPortletUtil().getSettingsByName("SYSTEM URL"));
				
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


	public ApplicantPortletUtil getApplicantPortletUtil() {
		// TODO Auto-generated method stub
		return this.applicantPortletUtil;
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



	public String getFirstName() {
		return firstName;
	}



	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}



	public String getLastName() {
		return lastName;
	}



	public void setLastName(String lastName) {
		this.lastName = lastName;
	}



	public String getOtherName() {
		return otherName;
	}



	public void setOtherName(String otherName) {
		this.otherName = otherName;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public String getMobile() {
		return mobile;
	}



	public void setMobile(String mobile) {
		this.mobile = mobile;
	}



	public String getGender() {
		return gender;
	}



	public void setGender(String gender) {
		this.gender = gender;
	}



	public String getMaritalStatus() {
		return maritalStatus;
	}



	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}



	public String getAddressLine1() {
		return addressLine1;
	}



	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}



	public String getAddressLine2() {
		return addressLine2;
	}



	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}



	public String getCity() {
		return city;
	}



	public void setCity(String city) {
		this.city = city;
	}



	public String getState() {
		return state;
	}



	public void setState(String state) {
		this.state = state;
	}



	public String getIdentificationType() {
		return identificationType;
	}



	public void setIdentificationType(String identificationType) {
		this.identificationType = identificationType;
	}



	public String getIdentificationNumber() {
		return identificationNumber;
	}



	public void setIdentificationNumber(String identificationNumber) {
		this.identificationNumber = identificationNumber;
	}



	public String getIdentificationFileName() {
		return identificationFileName;
	}



	public void setIdentificationFileName(String identificationFileName) {
		this.identificationFileName = identificationFileName;
	}



	public String getNextOfKinName() {
		return nextOfKinName;
	}



	public void setNextOfKinName(String nextOfKinName) {
		this.nextOfKinName = nextOfKinName;
	}



	public String getNextOfKinAddress() {
		return nextOfKinAddress;
	}



	public void setNextOfKinAddress(String nextOfKinAddress) {
		this.nextOfKinAddress = nextOfKinAddress;
	}



	public String getNextOfKinRelationship() {
		return nextOfKinRelationship;
	}



	public void setNextOfKinRelationship(String nextOfKinRelationship) {
		this.nextOfKinRelationship = nextOfKinRelationship;
	}



	public String getNextOfKinPhoneNumber() {
		return nextOfKinPhoneNumber;
	}



	public void setNextOfKinPhoneNumber(String nextOfKinPhoneNumber) {
		this.nextOfKinPhoneNumber = nextOfKinPhoneNumber;
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

	public String getPassportPhoto() {
		return passportPhoto;
	}

	public void setPassportPhoto(String passportPhoto) {
		this.passportPhoto = passportPhoto;
	}

	public String getIdentificationExpiryDate() {
		return identificationExpiryDate;
	}

	public void setIdentificationExpiryDate(String identificationExpiryDate) {
		this.identificationExpiryDate = identificationExpiryDate;
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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getCompanyState() {
		return companyState;
	}

	public void setCompanyState(String companyState) {
		this.companyState = companyState;
	}

	public String getCompanyPhoneNumber() {
		return companyPhoneNumber;
	}

	public void setCompanyPhoneNumber(String companyPhoneNumber) {
		this.companyPhoneNumber = companyPhoneNumber;
	}

	public String getCompanyEmailAddress() {
		return companyEmailAddress;
	}

	public void setCompanyEmailAddress(String companyEmailAddress) {
		this.companyEmailAddress = companyEmailAddress;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public String getDateOfIncorporation() {
		return dateOfIncorporation;
	}

	public void setDateOfIncorporation(String dateOfIncorporation) {
		this.dateOfIncorporation = dateOfIncorporation;
	}

	public String getCompanyLogo() {
		return companyLogo;
	}

	public void setCompanyLogo(String companyLogo) {
		this.companyLogo = companyLogo;
	}

	public String getCacCertificate() {
		return cacCertificate;
	}

	public void setCacCertificate(String cacCertificate) {
		this.cacCertificate = cacCertificate;
	}

	public String getResidencePhoneNumber() {
		return residencePhoneNumber;
	}

	public void setResidencePhoneNumber(String residencePhoneNumber) {
		this.residencePhoneNumber = residencePhoneNumber;
	}

	public String getResidenceState() {
		return residenceState;
	}

	public void setResidenceState(String residenceState) {
		this.residenceState = residenceState;
	}

	public String getResidenceCity() {
		return residenceCity;
	}

	public void setResidenceCity(String residenceCity) {
		this.residenceCity = residenceCity;
	}

	public String getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}

	public String getPlaceOfIssue() {
		return placeOfIssue;
	}

	public void setPlaceOfIssue(String placeOfIssue) {
		this.placeOfIssue = placeOfIssue;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getActivationEmail() {
		return activationEmail;
	}

	public void setActivationEmail(String activationEmail) {
		this.activationEmail = activationEmail;
	}

	public PortalUser getActivationPortalUser() {
		return activationPortalUser;
	}

	public void setActivationPortalUser(PortalUser activationPortalUser) {
		this.activationPortalUser = activationPortalUser;
	}

	
	
}
