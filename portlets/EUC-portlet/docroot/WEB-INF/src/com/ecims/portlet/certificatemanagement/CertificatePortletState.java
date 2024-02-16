package com.ecims.portlet.certificatemanagement;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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
import smartpay.entity.ApplicationAttachmentType;
import smartpay.entity.ApplicationFlag;
import smartpay.entity.ApplicationItem;
import smartpay.entity.ApplicationWorkflow;
import smartpay.entity.Certificate;
import smartpay.entity.Country;
import smartpay.entity.Currency;
import smartpay.entity.Dispute;
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
import smartpay.entity.enumerations.CertificateStatus;
import smartpay.entity.enumerations.PermissionType;
import smartpay.entity.enumerations.RoleConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.service.SwpService;

import com.ecims.commins.ComminsApplicationState;
import com.ecims.commins.Util;
import com.ecims.portlet.certificatemanagement.CertificatePortletState.VIEW_TABS;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.sf.primepay.smartpay13.ServiceLocator;
import com.sun.mail.iap.Literal;

public class CertificatePortletState {

	private static Logger log = Logger.getLogger(CertificatePortletState.class);
	private static CertificatePortletUtil certificatePortletUtil;
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	

	private String successMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	private String commentsDispute;
	
	
	private Certificate selectedCertificate;
	private Collection<Certificate> certificateListing;
	
	private Settings sendingEmail;
	private Settings sendingEmailPassword;
	private Settings sendingEmailPort;
	private Settings sendingEmailUsername;
	
	private Settings ApplicationName;
	private Settings SystemUrl;
	
	private Settings mobileApplicationName;
	private Settings proxyHost;
	private Settings proxyPort;
	private static CertificatePortletState portletState = null;
	
	/*****************non-request objects*****************/
	
	
	private VIEW_TABS currentTab;
	private Certificate certificateSelected;
	private Collection<PermissionType> permissionList;
	private HashMap<String, Collection<Dispute>> oldDisputes;
	private HashMap<String,Collection<Dispute>> newDisputes;
	private ApplicationWorkflow aw;
	
	
	

	public Certificate getSelectedCertificate() {
		return selectedCertificate;
	}

	public void setSelectedCertificate(Certificate selectedCertificate) {
		this.selectedCertificate = selectedCertificate;
	}

	public Collection<Certificate> getCertificateListing() {
		return certificateListing;
	}

	public void setCertificateListing(Collection<Certificate> certificateListing) {
		this.certificateListing = certificateListing;
	}

	/****enum section****/
    
    
    
	public static enum EU_ACTIONS{
		VIEW_ALL_CERTIFICATES_EU, ALL_CERT, DISPUTED_CERT, UTILIZED_CERT, EXPIRED_CERT;
		
	}
	
	public static enum NSA_ACTIONS{
		VIEW_ALL_CERTIFICATES_EU, ALL_CERT, DISPUTED_CERT, UTILIZED_CERT, EXPIRED_CERT;
		
	}
	
	public static enum CERTLIST_ACTIONS{
		HANDLE_ACTIONS_ON_LIST_CERTIFICATES, ACT_ON_CERTIFICATE, UPLOAD_A_CERT
	}
	
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	public static enum VIEW_TABS{
		VIEW_ALL_CERTIFICATES_EU, VIEW_ALL_CERTIFICATES_AGENCY, VIEW_MY_CERTIFICATES_AGENCY, 
		VIEW_RECALLED_CERTIFICATES, VIEW_ALL_CERTIFICATES_NSA, VIEW_ALL_CERTIFICATES_INFO, VIEW_DISPUTED_CERTIFICATES, VIEW_UTILIZED_CERTIFICATES, VIEW_EXPIRED_CERTIFICATES
    }
	
	public static enum APPLIST_ACTIONS{
		ACT_ON_CERTIFICATE
	}
    
	/*****APPLICATION CORE METHODS! DO NOT TAMPER WITH THIS SECTION OF CODE - START HERE*****/
	public String getSuccessMessage()
	{
		return this.successMessage;
	}
	
	public static void addError(ActionRequest aReq, String errorMessage,
			CertificatePortletState portletState) {

		log.info("Eeeror Msg = " + errorMessage);
		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			CertificatePortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			CertificatePortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			CertificatePortletState.log.debug("Error including error message", e);
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
	
	
	
	public static CertificatePortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		
		Logger.getLogger(CertificatePortletState.class).info("------getInstance");
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (CertificatePortletState) session.getAttribute(CertificatePortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				ServiceLocator serviceLocator = ServiceLocator.getInstance();
				SwpService swpService = serviceLocator.getSwpService();
				if (portletState == null) {
					portletState = new CertificatePortletState();
					CertificatePortletUtil util = new CertificatePortletUtil();
					portletState.setCertificatePortletUtil(util);
					session.setAttribute(CertificatePortletState.class.getName(), portletState);
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
			CertificatePortletState portletState, PortletRequest req) {
		// TODO Auto-generated method stub
		if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
		{
			portletState.setCertificateListing((Collection<Certificate>)portletState.getCertificatePortletUtil().getAllEntityObjects(Certificate.class));
			portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_CERTIFICATES_NSA);
		}
		if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
		{
			portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_CERTIFICATES_EU);
			portletState.setCertificateListing((Collection<Certificate>)portletState.getCertificatePortletUtil().getCertificatesByPortalUser(portletState.getPortalUser()));
		}
		if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP) && 
				portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))
		{
			if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))
			{
				portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_CERTIFICATES_NSA);
				Collection<Certificate> alist = (Collection<Certificate>)portletState.getCertificatePortletUtil().getAllEntityObjects(Certificate.class);
				portletState.setCertificateListing(null);
				if(alist!=null && alist.size()>0)
				{
					ArrayList<Certificate> alistNew = new ArrayList<Certificate>();
					for(Iterator<Certificate> it = alist.iterator(); it.hasNext();)
					{
						Certificate c2 = it.next();
						if(c2.getStatus()!=null && c2.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_COLLECTED))
							alistNew.add(c2);
					}
					portletState.setCertificateListing(alistNew);
				}
			}else
			{
				portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_CERTIFICATES_AGENCY);
				Collection<Certificate> alist = (Collection<Certificate>)portletState.getCertificatePortletUtil().getCertificatesByAgency(
						portletState.getPortalUser());
				portletState.setCertificateListing(alist);
			}
					
			
		}
		if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP) && 
				(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_ACCREDITOR_USER) || 
				portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_REGULATOR_USER)))
		{
			
				portletState.setCurrentTab(VIEW_TABS.VIEW_MY_CERTIFICATES_AGENCY);
				Collection<Certificate> alist = (Collection<Certificate>)portletState.getCertificatePortletUtil().getAllEntityObjects(Certificate.class);
				portletState.setCertificateListing(null);
				if(alist!=null && alist.size()>0)
				{
					ArrayList<Certificate> alistNew = new ArrayList<Certificate>();
					for(Iterator<Certificate> it = alist.iterator(); it.hasNext();)
					{
						Certificate c2 = it.next();
						if(c2.getApplication().getPortalUser()!=null && 
								c2.getApplication().getPortalUser().getAgency().getId().equals(portletState.getPortalUser().getAgency().getId()))
							alistNew.add(c2);
					}
					portletState.setCertificateListing(alistNew);
					
				}
				
			
		}
		
		if(portletState.getCertificateListing()==null || (portletState.getCertificateListing()!=null && portletState.getCertificateListing().size()==0))
		{
			portletState.addError((ActionRequest)req, "There are no certificates available", portletState);
		}
		
		
		portletState.setPermissionList((Collection<PermissionType>)portletState.getPermissionsByPortalUser(portletState.getPortalUser(), swpService));
		log.info("getPermissionList size ==" +portletState.getPermissionList().size());
	}

	public void setPermissionList(
			Collection<PermissionType> permissionsByPortalUser) {
		// TODO Auto-generated method stub
		this.permissionList = permissionsByPortalUser;
	}
	
	public Collection<PermissionType> getPermissionList() {
		// TODO Auto-generated method stub
		return this.permissionList;
	}

	/*****APPLICATION CORE METHODS! DO NOT TAMPER WITH THIS SECTION OF CODE - END HERE*****/
	
	private Collection<PermissionType> getPermissionsByPortalUser(PortalUser pu2, SwpService swpService2) {
		// TODO Auto-generated method stub
		Collection<PermissionType> permList = null;
		try {
			
			String hql = "select rt.permissionType from Permission rt where (" +
					"rt.portalUser.id = " + pu2.getId() + ")";
			log.info("Get hql = " + hql);
			permList = (Collection<PermissionType>) swpService2.getAllRecordsByHQL(hql);
		
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
//		List<String> li = AttachmentTypeConstant.values();
//		for(Iterator<String> it = li.iterator(); it.hasNext();)
//		{
//			String lit = it.next();
//			ApplicationAttachmentType aat = new ApplicationAttachmentType();
//			aat.setExpiryDateApplicable(false);
//			aat.setName(AttachmentTypeConstant.fromString(lit));
//			swpService.createNewRecord(aat);
//		}
	}

	
	


	private static void defaultInit(PortletRequest request, CertificatePortletState portletState) {
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
				CertificatePortletUtil util = CertificatePortletUtil.getInstance();
				portletState.setCertificatePortletUtil(util);
				
				ArrayList<RoleType> pur = null;
				
				if (request.getRemoteUser() != null
						&& !request.getRemoteUser().equals("")) {
					portletState.getCertificatePortletUtil().getRoleTypeByPortalUser(portletState.getPortalUser());	
				}		

				portletState.setPortalUserRoleType(pur);
				
				
				portletState.setSendingEmail(portletState.getCertificatePortletUtil().getSettingsByName("SENDING EMAIL ADDRESS"));
				portletState.setSendingEmailPassword(portletState.getCertificatePortletUtil().getSettingsByName("SENDING EMAIL PASSWORD"));
				portletState.setSendingEmailPort(portletState.getCertificatePortletUtil().getSettingsByName("SENDING EMAIL PORT"));
				portletState.setSendingEmailUsername(portletState.getCertificatePortletUtil().getSettingsByName("SENDING EMAIL USERNAME"));
				portletState.setMobileApplicationName(portletState.getCertificatePortletUtil().getSettingsByName("MOBILE APPLICATION NAME"));
				portletState.setProxyHost(portletState.getCertificatePortletUtil().getSettingsByName("PROXY HOST"));
				portletState.setProxyPort(portletState.getCertificatePortletUtil().getSettingsByName("PROXY PORT"));
				portletState.setApplicationName(portletState.getCertificatePortletUtil().getSettingsByName("APPLICATION NAME"));
				portletState.setSystemUrl(portletState.getCertificatePortletUtil().getSettingsByName("SYSTEM URL"));
				
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


	public static CertificatePortletUtil getCertificatePortletUtil() {
		return certificatePortletUtil;
	}

	public static void setCertificatePortletUtil(
			CertificatePortletUtil certificatePortletUtil) {
		CertificatePortletState.certificatePortletUtil = certificatePortletUtil;
	}

	
	public VIEW_TABS getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(VIEW_TABS currentTab) {
		this.currentTab = currentTab;
	}

	public Certificate getCertificateSelected() {
		return certificateSelected;
	}

	public void setCertificateSelected(Certificate certificateSelected) {
		this.certificateSelected = certificateSelected;
	}

	public void setApplicationWorkflow(ApplicationWorkflow aw) {
		// TODO Auto-generated method stub
		this.aw = aw;
	}
	public ApplicationWorkflow getApplicationWorkflow() {
		// TODO Auto-generated method stub
		return this.aw;
	}

	public HashMap<String,Collection<Dispute>> getOldDisputes() {
		return oldDisputes;
	}

	public void setOldDisputes(HashMap<String, Collection<Dispute>> olddisputeList) {
		this.oldDisputes = olddisputeList;
	}

	public HashMap<String,Collection<Dispute>> getNewDisputes() {
		return newDisputes;
	}

	public void setNewDisputes(HashMap<String,Collection<Dispute>> newDisputes) {
		this.newDisputes = newDisputes;
	}

	public String getCommentsDispute() {
		return commentsDispute;
	}

	public void setCommentsDispute(String commentsDispute) {
		this.commentsDispute = commentsDispute;
	}

	
	
}
