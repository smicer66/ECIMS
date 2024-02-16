package com.ecims.portlet.dashboard;

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
import smartpay.entity.ApplicationAttachmentType;
import smartpay.entity.ApplicationFlag;
import smartpay.entity.ApplicationItem;
import smartpay.entity.ApplicationWorkflow;
import smartpay.entity.Certificate;
import smartpay.entity.Country;
import smartpay.entity.Currency;
import smartpay.entity.ItemCategory;
import smartpay.entity.ItemCategorySub;
import smartpay.entity.Permission;
import smartpay.entity.PortCode;
import smartpay.entity.PortalUser;
import smartpay.entity.QuantityUnit;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.State;
import smartpay.entity.WeightUnit;
import smartpay.entity.enumerations.AgencyType;
import smartpay.entity.enumerations.ApplicantStatus;
import smartpay.entity.enumerations.ApplicationStatus;
import smartpay.entity.enumerations.CertificateStatus;
import smartpay.entity.enumerations.PermissionType;
import smartpay.entity.enumerations.RoleConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.service.SwpService;

import com.ecims.commins.ComminsApplicationState;
import com.ecims.commins.Util;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.util.PortalUtil;
import com.sf.primepay.smartpay13.ServiceLocator;

public class DashboardPortletState {

	private static Logger log = Logger.getLogger(DashboardPortletState.class);
	private PortalUser portalUser;
	private String remoteUser;
	private ArrayList<RoleType> portalUserRoleType;
	

	private String successMessage;
	private String remoteIPAddress;
	private String errorMessage;
	private ArrayList<String> errorList = new ArrayList<String>();
	
	
	
	private Settings sendingEmail;
	private Settings sendingEmailPassword;
	private Settings sendingEmailPort;
	private Settings sendingEmailUsername;
	
	private Settings ApplicationName;
	private Settings SystemUrl;
	
	private Settings mobileApplicationName;
	private Settings proxyHost;
	private Settings proxyPort;
	private Collection<PermissionType> permissionList;
	
	
	private int newSignUpRequestCount;
	private int approvedSignUpRequestCount;
	private int rejectedSignUpRequestCount;
	
	private int newApplicationRequestCount;
	private int forwardedApplicationCount;
	private int endorsedApplicationCount;
	private int approvedApplicationCount;
	private int disendorsedApplicationCount;
	private int rejectedApplicationCount;
	
	private int recalledCertificateCount;
	private int issuedCertificateCount;
	
	private int pendingApplicationRequestCount;
	
	private int workOnApplication;
	private int notWorkOnApplication;
	
	/*****************non-request objects*****************/
	
	
	private VIEW_TABS currentTab;
	private Certificate certificateSelected;
	
	

	/****enum section****/
    
    
    
	public static enum EU_ACTIONS{
		VIEW_ALL_CERTIFICATES_EU, ALL_CERT, DISPUTED_CERT, UTILIZED_CERT, EXPIRED_CERT;
		
	}
	
	public static enum NSA_ACTIONS{
		VIEW_ALL_CERTIFICATES_EU, ALL_CERT, DISPUTED_CERT, UTILIZED_CERT, EXPIRED_CERT;
		
	}
	
	public static enum CERTLIST_ACTIONS{
		HANDLE_ACTIONS_ON_LIST_CERTIFICATES, ACT_ON_CERTIFICATE
	}
	
	public static enum NAVIGATE{
		NAVIGATE_ACTIONS
	}
	
	public static enum VIEW_TABS{
		VIEW_ALL_CERTIFICATES_EU, 
		VIEW_ALL_CERTIFICATES_NSA, VIEW_DISPUTED_CERTIFICATES, VIEW_UTILIZED_CERTIFICATES, VIEW_EXPIRED_CERTIFICATES, 
		PENDING_APPLICATION, PENDING_APPLICANT, PENDING_ISSUANCE, RECENT_ENDORSEMENT, 
		PENDING_APPLICATION_AGENCY, 
		PENDING_APPLICATION_APPROVAL_NSA, APPROVED_APPLICATIONS_NSA
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
			DashboardPortletState portletState) {

		log.info("Eeeror Msg = " + errorMessage);
		portletState.setErrorMessage(errorMessage);

		try {
			SessionErrors.add(aReq, "errorMessage");
		} catch (Exception e) {
			DashboardPortletState.log.debug("Error including error message", e);
		}
	}
	
	
	public static void addSuccess(ActionRequest aReq, String successMessage,
			DashboardPortletState portletState) {

		portletState.setSuccessMessage(successMessage);

		try {
			com.liferay.portal.kernel.servlet.SessionMessages.add(aReq, "successMessage");
		} catch (Exception e) {
			DashboardPortletState.log.debug("Error including error message", e);
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
	
	
	
	public static DashboardPortletState getInstance (PortletRequest request, PortletResponse response ) {
		
		DashboardPortletState portletState = null;
		Logger.getLogger(DashboardPortletState.class).info("------getInstance");
		try {
			
			if(new Util().checkmate()==false)
			{
				PortletSession session = request.getPortletSession();
				portletState = (DashboardPortletState) session.getAttribute(DashboardPortletState.class.getName(), PortletSession.PORTLET_SCOPE);
				
				ServiceLocator serviceLocator = ServiceLocator.getInstance();
				SwpService swpService = serviceLocator.getSwpService();
				if (portletState == null) {
					portletState = new DashboardPortletState();
					session.setAttribute(DashboardPortletState.class.getName(), portletState);
					defaultInit(request, portletState, swpService);
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
			DashboardPortletState portletState, PortletRequest req) {
		// TODO Auto-generated method stub
		Collection<PermissionType> permission = null;
		try {
			String hql = "select p.permissionType from Permission p where " +
					"p.portalUser.id = " + portletState.getPortalUser().getId();
			log.info("Get hqlType = " + hql);
			permission = (Collection<PermissionType>) swpService.getAllRecordsByHQL(hql);
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		portletState.setPermissionList(permission);
		
		
		int nsurc =0;
		int asurc = 0;
		int rsurc = 0;
		
		int narc=0;
		int fac=0;
		int eac=0;
		int dac=0;
		int rac=0;
		int aac=0;
		
		int icc=0;
		int rcc=0;
		
		int parc=0;
		
		int woapp=0;
		int nwoapp=0;
		log.info("portletState.getPortalUser().getRoleType().getRole() =" + portletState.getPortalUser().getRoleType().getRole().getName().getValue());
		if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
		{
			String hql = "select p from Applicant p";
			log.info("Get hqlType = " + hql);
			Collection<Applicant> apl = (Collection<Applicant>) swpService.getAllRecordsByHQL(hql);
			
			hql = "select p from Application p";
			log.info("Get hqlType = " + hql);
			Collection<Application> app = (Collection<Application>) swpService.getAllRecordsByHQL(hql);
			

			hql = "select p from ApplicationWorkflow p";
			log.info("Get hqlType = " + hql);
			Collection<ApplicationWorkflow> appWorkflow = (Collection<ApplicationWorkflow>) swpService.getAllRecordsByHQL(hql);
			
			
			
			hql = "select p from Certificate p";
			log.info("Get hqlType = " + hql);
			Collection<Certificate> cer = (Collection<Certificate>) swpService.getAllRecordsByHQL(hql);
			
			
			
			for(Iterator<Applicant> aplIt = apl.iterator(); aplIt.hasNext();)
			{
				Applicant applicant = aplIt.next();
				if(applicant.getStatus().equals(ApplicantStatus.APPLICANT_STATUS_UNAPPROVED))
					nsurc++;
				else if(applicant.getStatus().equals(ApplicantStatus.APPLICANT_STATUS_APPROVED))
					asurc++;
				else if(applicant.getStatus().equals(ApplicantStatus.APPLICANT_STATUS_REJECTED))
					rsurc++;
				
			}
			
			for(Iterator<Application> aplIt = app.iterator(); aplIt.hasNext();)
			{
				Application application = aplIt.next();
				if(application.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CREATED))
					narc++;
				else if(application.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_FORWARDED))
					fac++;
				else if(application.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ENDORSED))
					eac++;
				else if(application.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISENDORSED))
					dac++;
				else if(application.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
					rac++;
				else if(application.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
					aac++;
			}
			
			narc=0;
			fac=0;
			eac=0;
			dac=0;
			rac=0;
			aac=0;
			
			
			for(Iterator<ApplicationWorkflow> appWorkflowIt = appWorkflow.iterator(); appWorkflowIt.hasNext();)
			{
				ApplicationWorkflow applicationWf = appWorkflowIt.next();
				if(applicationWf.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CREATED) && 
						applicationWf.getWorkedOn()!=null && applicationWf.getWorkedOn().equals(Boolean.FALSE))
				{
					narc++;
				}
				else if(applicationWf.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_FORWARDED) && 
						applicationWf.getWorkedOn()!=null && applicationWf.getWorkedOn().equals(Boolean.FALSE))
				{
					if(portletState.getPermissionList()!=null 
					&& (portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION) || 
					portletState.getPermissionList().contains(PermissionType.PERMISSION_REJECT_APPLICATION)))
					{
						if(applicationWf.getAgency().getAgencyType().getValue().equals(AgencyType.ONSA_GROUP.getValue()))
							fac++;
					}
					else
					{
						fac++;
					}
				}
				else if(applicationWf.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ENDORSED) && 
						applicationWf.getWorkedOn()!=null && applicationWf.getWorkedOn().equals(Boolean.FALSE))
				{
					eac++;
				}
				else if(applicationWf.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISENDORSED) && 
						applicationWf.getWorkedOn()!=null && applicationWf.getWorkedOn().equals(Boolean.FALSE))
				{
					dac++;
				}
				else if(applicationWf.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
				{
					rac++;
				}
				else if(applicationWf.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
				{
					aac++;
				}
			}
			
			log.info("narc==" + narc);
			log.info("fac==" + fac);
			log.info("eac==" + eac);
			log.info("dac==" + dac);
			log.info("rac==" + rac);
			log.info("aac==" + aac);
			
			for(Iterator<Certificate> aplIt = cer.iterator(); aplIt.hasNext();)
			{
				Certificate certif = aplIt.next();
				if(certif.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_APPROVED) || 
						certif.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_COLLECTED) || 
						certif.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_ISSUED))
					rcc++;
				else if(certif.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_RECALLED))
					icc++;
			}
			
			portletState.setNewSignUpRequestCount(nsurc);
			log.info("rsurc==" + asurc);
			portletState.setApprovedSignUpRequestCount(asurc);
			log.info("rsurc==" + asurc);
			portletState.setRejectedSignUpRequestCount(rsurc);
			log.info("rsurc==" + rsurc);
			
			portletState.setNewApplicationRequestCount(narc);
			portletState.setForwardedApplicationCount(fac);
			if(portletState.getPermissionList()!=null && (portletState.getPermissionList().contains
					(PermissionType.PERMISSION_APPROVE_APPLICATION) || portletState.getPermissionList().contains(PermissionType.PERMISSION_REJECT_APPLICATION)))
			{
				log.info("permission approve application 1");
				
				
				
				
				if(portletState.getPermissionList()!=null 
						&& (portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION) || 
						portletState.getPermissionList().contains(PermissionType.PERMISSION_REJECT_APPLICATION)))
				{
					Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.
							getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(swpService, 
									portletState.getPortalUser().getRoleType().getId(), 
									ApplicationStatus.APPLICATION_STATUS_FORWARDED, false, 
									portletState.getPortalUser().getAgency());
					portletState.setForwardedApplicationCount(appList!=null ? appList.size() : 0);
				}else
				{
					Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(
							portletState.getPortalUser().getRoleType().getId(), 
									ApplicationStatus.APPLICATION_STATUS_FORWARDED, false, swpService);
					portletState.setForwardedApplicationCount(appList!=null ? appList.size() : 0);
					
				}
			}
			portletState.setEndorsedApplicationCount(eac);
			portletState.setDisendorsedApplicationCount(dac);
			portletState.setApprovedApplicationCount(aac);
			portletState.setRejectedApplicationCount(rac);
			
			portletState.setIssuedCertificateCount(rcc);
			portletState.setRecalledCertificateCount(icc);
		}
		else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
		{
			
			String hql = "select p from Application p WHERE p.applicant.portalUser.id = " + portletState.getPortalUser().getId();
			log.info("Get hqlType = " + hql);
			Collection<Application> app = (Collection<Application>) swpService.getAllRecordsByHQL(hql);
			
			hql = "select p from Certificate p WHERE p.application.applicant.portalUser.id = " + portletState.getPortalUser().getId();
			log.info("Get hqlType = " + hql);
			Collection<Certificate> cer = (Collection<Certificate>) swpService.getAllRecordsByHQL(hql);
			
			
			
			for(Iterator<Application> aplIt = app.iterator(); aplIt.hasNext();)
			{
				Application application = aplIt.next();
				if(application.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_CREATED))
				{
					narc++;
				}
				if(application.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_FORWARDED) 
						|| application.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_ENDORSED) 
						|| application.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_DISENDORSED))
				{
					parc++;
				}
				else if(application.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_REJECTED))
					rac++;
				else if(application.getStatus().equals(ApplicationStatus.APPLICATION_STATUS_APPROVED))
					aac++;
			}
			
			for(Iterator<Certificate> aplIt = cer.iterator(); aplIt.hasNext();)
			{
				Certificate certif = aplIt.next();
				if(certif.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_APPROVED) || 
						certif.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_COLLECTED) || 
						certif.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_ISSUED))
					rcc++;
				else if(certif.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_RECALLED))
					icc++;
			}
			
			portletState.setNewSignUpRequestCount(nsurc);
			portletState.setApprovedSignUpRequestCount(asurc);
			portletState.setRejectedSignUpRequestCount(rsurc);
			
			portletState.setNewApplicationRequestCount(narc);
			portletState.setPendingApplicationRequestCount(parc);
			portletState.setEndorsedApplicationCount(eac);
			portletState.setDisendorsedApplicationCount(dac);
			portletState.setApprovedApplicationCount(aac);
			portletState.setRejectedApplicationCount(rac);
			
			portletState.setIssuedCertificateCount(icc);
			portletState.setRecalledCertificateCount(rcc);
		}
		
		else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
		{
			
			String hql = "select p from ApplicationWorkflow p WHERE " +
					"p.agency.id = " + portletState.getPortalUser().getAgency().getId();
			log.info("Get hqlType = " + hql);
			Collection<ApplicationWorkflow> app = (Collection<ApplicationWorkflow>) swpService.getAllRecordsByHQL(hql);
			

			hql = "select p from Certificate p WHERE " +
					"p.application.applicant.portalUser.id = " + portletState.getPortalUser().getId();
			log.info("Get hqlType = " + hql);
			Collection<Certificate> cer = (Collection<Certificate>) swpService.getAllRecordsByHQL(hql);
			
			
			for(Iterator<ApplicationWorkflow> aplIt = app.iterator(); aplIt.hasNext();)
			{
				ApplicationWorkflow application = aplIt.next();
				if(application.getWorkedOn().equals(Boolean.TRUE))
				{
					woapp++;
				}else
				{
					nwoapp++;
				}
			}
			
			for(Iterator<Certificate> aplIt = cer.iterator(); aplIt.hasNext();)
			{
				Certificate certif = aplIt.next();
				if(certif.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_APPROVED) || 
						certif.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_COLLECTED) || 
						certif.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_ISSUED))
					rcc++;
				else if(certif.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_RECALLED))
					icc++;
			}
			
			
			portletState.setNotWorkOnApplication(nwoapp);
			portletState.setWorkOnApplication(woapp);
			
			portletState.setIssuedCertificateCount(icc);
			portletState.setRecalledCertificateCount(rcc);
		}
		
		
	}

	/*****APPLICATION CORE METHODS! DO NOT TAMPER WITH THIS SECTION OF CODE - END HERE*****/
	
	
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

	
	


	private static void defaultInit(PortletRequest request, DashboardPortletState portletState, SwpService swpService) {
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
				
				ArrayList<RoleType> pur = null;
				
				if (request.getRemoteUser() != null
						&& !request.getRemoteUser().equals("")) {
					PortalUser portalUser = null;
					Collection<RoleType> roles = null;
					try {
						String hql = "select pu.roleType from PortalUser pu where " +
								"pu.id = " + portletState.getPortalUser().getId();
						log.info("Get hqlType = " + hql);
						roles = (Collection<RoleType>) swpService.getAllRecordsByHQL(hql);
					} catch (HibernateException e) {
						log.error("",e);
					} catch (Exception e) {
						log.error("",e);
					} finally {
						
					}
					if(roles!=null && roles.size()>0)
					{
						pur = new ArrayList<RoleType>();
						for(Iterator<RoleType> roleIter = roles.iterator(); roleIter.hasNext();)
						{
							pur.add(roleIter.next());
						}
					}else
					{
						
					}
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


	public Collection<ApplicationWorkflow> getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(
			Long id, ApplicationStatus status, boolean workedOn, SwpService swpService) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = "";
				if(workedOn==false)
				{
					hql = "select rt from ApplicationWorkflow rt where " +
						"rt.sourceId = " + id + " AND " +
						"lower(rt.status) = lower('" + status.getValue() + "') AND rt.workedOn = 'false'";
				}
				else
				{
					hql = "select rt from ApplicationWorkflow rt where " +
						"rt.sourceId = " + id + " AND " +
						"lower(rt.status) = lower('" + status.getValue() + "') AND rt.workedOn = 'true'";
				}
				log.info("Get hql = " + hql);
				rt = (Collection<ApplicationWorkflow>) swpService.getAllRecordsByHQL(hql);
				log.info("Rt size = " + rt.size());
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
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
	
	public Collection<ApplicationWorkflow> getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
			SwpService swpService,
			Long id, ApplicationStatus status, boolean b, Agency agency) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.receipientRole = " +
						"" + id + " AND rt.agency.id = " +agency.getId()+" AND rt.workedOn = " + (b==true ? "'true'" : "'false'" + " AND " +
						"lower(rt.status) = lower('" + status.getValue() + "')");
				log.info("Get hql = " + hql);
				rt = (Collection<ApplicationWorkflow>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
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


	public Collection<PermissionType> getPermissionList() {
		return permissionList;
	}

	public void setPermissionList(Collection<PermissionType> permissionList) {
		this.permissionList = permissionList;
	}

	public int getNewSignUpRequestCount() {
		return newSignUpRequestCount;
	}

	public void setNewSignUpRequestCount(int newSignUpRequestCount) {
		this.newSignUpRequestCount = newSignUpRequestCount;
	}

	public int getApprovedSignUpRequestCount() {
		return approvedSignUpRequestCount;
	}

	public void setApprovedSignUpRequestCount(int approvedSignUpRequestCount) {
		this.approvedSignUpRequestCount = approvedSignUpRequestCount;
	}

	public int getRejectedSignUpRequestCount() {
		return rejectedSignUpRequestCount;
	}

	public void setRejectedSignUpRequestCount(int rejectedSignUpRequestCount) {
		this.rejectedSignUpRequestCount = rejectedSignUpRequestCount;
	}

	public int getNewApplicationRequestCount() {
		return newApplicationRequestCount;
	}

	public void setNewApplicationRequestCount(int newApplicationRequestCount) {
		this.newApplicationRequestCount = newApplicationRequestCount;
	}

	public int getForwardedApplicationCount() {
		return forwardedApplicationCount;
	}

	public void setForwardedApplicationCount(int forwardedApplicationCount) {
		this.forwardedApplicationCount = forwardedApplicationCount;
	}

	public int getEndorsedApplicationCount() {
		return endorsedApplicationCount;
	}

	public void setEndorsedApplicationCount(int endorsedApplicationCount) {
		this.endorsedApplicationCount = endorsedApplicationCount;
	}

	public int getApprovedApplicationCount() {
		return approvedApplicationCount;
	}

	public void setApprovedApplicationCount(int approvedApplicationCount) {
		this.approvedApplicationCount = approvedApplicationCount;
	}

	public int getDisendorsedApplicationCount() {
		return disendorsedApplicationCount;
	}

	public void setDisendorsedApplicationCount(int disendorsedApplicationCount) {
		this.disendorsedApplicationCount = disendorsedApplicationCount;
	}

	public int getRejectedApplicationCount() {
		return rejectedApplicationCount;
	}

	public void setRejectedApplicationCount(int rejectedApplicationCount) {
		this.rejectedApplicationCount = rejectedApplicationCount;
	}

	public int getRecalledCertificateCount() {
		return recalledCertificateCount;
	}

	public void setRecalledCertificateCount(int recalledCertificateCount) {
		this.recalledCertificateCount = recalledCertificateCount;
	}

	public int getIssuedCertificateCount() {
		return issuedCertificateCount;
	}

	public void setIssuedCertificateCount(int issuedCertificateCount) {
		this.issuedCertificateCount = issuedCertificateCount;
	}

	public int getPendingApplicationRequestCount() {
		return pendingApplicationRequestCount;
	}

	public void setPendingApplicationRequestCount(int pendingApplicationRequestCount) {
		this.pendingApplicationRequestCount = pendingApplicationRequestCount;
	}

	public int getWorkOnApplication() {
		return workOnApplication;
	}

	public void setWorkOnApplication(int workOnApplication) {
		this.workOnApplication = workOnApplication;
	}

	public int getNotWorkOnApplication() {
		return notWorkOnApplication;
	}

	public void setNotWorkOnApplication(int notWorkOnApplication) {
		this.notWorkOnApplication = notWorkOnApplication;
	}

	
	
}
