package com.ecims.portlet.certificatemanagement;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import smartpay.audittrail.AuditTrail;
import smartpay.entity.Agency;
import smartpay.entity.Applicant;
import smartpay.entity.Application;
import smartpay.entity.ApplicationAttachment;
import smartpay.entity.ApplicationAttachmentType;
import smartpay.entity.ApplicationFlag;
import smartpay.entity.ApplicationItem;
import smartpay.entity.ApplicationWorkflow;
import smartpay.entity.Certificate;
import smartpay.entity.Company;
import smartpay.entity.Country;
import smartpay.entity.Currency;
import smartpay.entity.Dispute;
import smartpay.entity.Enquiry;
import smartpay.entity.ItemCategory;
import smartpay.entity.ItemCategorySub;
import smartpay.entity.PortCode;
import smartpay.entity.PortalUser;
import smartpay.entity.QuantityUnit;
import smartpay.entity.RoleType;
import smartpay.entity.State;
import smartpay.entity.WeightUnit;
import smartpay.entity.enumerations.AgencyType;
import smartpay.entity.enumerations.ApplicantStatus;
import smartpay.entity.enumerations.ApplicantType;
import smartpay.entity.enumerations.ApplicationStatus;
import smartpay.entity.enumerations.CertificateStatus;
import smartpay.entity.enumerations.DisputeType;
import smartpay.entity.enumerations.PermissionType;
import smartpay.entity.enumerations.RoleConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.entity.enumerations.UserStatus;
import smartpay.service.SwpService;

import com.ecims.commins.ECIMSConstants;
import com.ecims.commins.ImageUpload;
import com.ecims.commins.Mailer;
import com.ecims.commins.SendSms;
import com.ecims.commins.Util;
import com.ecims.portlet.applicationmanagement.ApplicationManagementPortletState;
import com.ecims.portlet.certificatemanagement.CertificatePortlet;
import com.ecims.portlet.certificatemanagement.CertificatePortletState;
import com.ecims.portlet.certificatemanagement.CertificatePortletState.CERTLIST_ACTIONS;
import com.ecims.portlet.certificatemanagement.CertificatePortletState.EU_ACTIONS;
import com.ecims.portlet.certificatemanagement.CertificatePortletUtil;
import com.ecims.portlet.certificatemanagement.CertificatePortletState.VIEW_TABS;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.liferay.portal.DuplicateUserEmailAddressException;
import com.liferay.portal.DuplicateUserScreenNameException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class CertificatePortlet
 */
public class CertificatePortlet extends MVCPortlet {
	private Logger log = Logger.getLogger(CertificatePortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	CertificatePortletUtil util = CertificatePortletUtil.getInstance();
	ServiceContext serviceContext = new ServiceContext();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	
	@Override
	public void init(PortletConfig config) throws PortletException {
		// TODO Auto-generated method stub
		log.info("Administrative portlet init called...");		
		pContext = config.getPortletContext();
		super.init(config);
		pConfig = config;
	    this.swpService = this.serviceLocator.getSwpService();
	}
	
	@Override
	public void render(RenderRequest renderRequest,
			RenderResponse renderResponse) throws PortletException, IOException {
		log.info("Administrative render called...");	
		PortletSession ps = renderRequest.getPortletSession();
		CertificatePortletState portletState = 
				CertificatePortletState.getInstance(renderRequest, renderResponse);

		log.info(">>>next page = " + renderRequest.getParameter("jspPage"));
		super.render(renderRequest, renderResponse);
	}

	@Override
	public void serveResource(ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) throws IOException,
			PortletException {

		String resourceID = resourceRequest.getResourceID();
		if (resourceID == null || resourceID.equals(""))
			return;
	}
	
	
	@Override
	public void processAction(ActionRequest aReq,
			ActionResponse aRes) throws IOException, PortletException {
		SessionMessages.add(aReq, pConfig.getPortletName()
				+ SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
		log.info("inside process Action");
		
		CertificatePortletState portletState = CertificatePortletState.getInstance(aReq, aRes);
		
		
		String action = aReq.getParameter("action");
		log.info("action == " + action);
		if (action == null) {
			log.info("----------------action value is null----------------");
			return;
		}
		else
		{
			log.info("----------------action value is " + action +"----------------");
		}
        if (portletState == null) {
			log.info("----------------portletState is null----------------");
			return;
		}
        
        /*************POST ACTIONS*********************/

        if(action.equalsIgnoreCase(CERTLIST_ACTIONS.HANDLE_ACTIONS_ON_LIST_CERTIFICATES.name()))
        {
        	log.info("HANDLE ACTIONS ON CERTIFICATE LISTING");
        	handleActionsOnListCertificates(aReq, aRes, swpService, portletState);
        		
        }
        
        if(action.equalsIgnoreCase(CERTLIST_ACTIONS.UPLOAD_A_CERT.name()))
        {
        	log.info("HANDLE ACTIONS ON CERTIFICATE LISTING");
        	handleupload(aReq, aRes, swpService, portletState);
        		
        }
        
        
        if(action.equalsIgnoreCase(CERTLIST_ACTIONS.ACT_ON_CERTIFICATE.name()))
        {
        	log.info("HANDLE ACTIONS ON ONE CERTIFICATE");
        	handleActionsOnOneCertificate(aReq, aRes, swpService, portletState);
        	
        }
        


        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_ALL_CERTIFICATES_EU.name()))
        {
        	portletState.setCertificateListing((Collection<Certificate>)portletState.getCertificatePortletUtil().
        			getCertificatesByPortalUser(portletState.getPortalUser()));
        	aRes.setRenderParameter("jspPage", "/html/certificateportlet/certificatelisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_CERTIFICATES_EU);
        	if(portletState.getCertificateListing()==null || (portletState.getCertificateListing()!=null && portletState.getCertificateListing().size()==0))
        	{
        		portletState.addError(aReq, "You have not created any End-User Certificates generated on the system yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_MY_CERTIFICATES_AGENCY.name()))
        {
        	portletState.setCertificateListing((Collection<Certificate>)portletState.getCertificatePortletUtil().
        			getCertificatesByAgency(portletState.getPortalUser()));
//        			getCertificatesByPortalUser(portletState.getPortalUser()));
        	aRes.setRenderParameter("jspPage", "/html/certificateportlet/certificatelisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_MY_CERTIFICATES_AGENCY);
        	if(portletState.getCertificateListing()==null || (portletState.getCertificateListing()!=null && portletState.getCertificateListing().size()==0))
        	{
        		portletState.addError(aReq, "Your agency does not have any Certificates generated on the system yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_ALL_CERTIFICATES_AGENCY.name()))
        {
        	//portletState.setCertificateListing();
        	log.info("All certificate count=  " + portletState.getCertificateListing().size());
//        			getCertificatesByPortalUser(portletState.getPortalUser()));
        	
        	
        	Collection<Certificate> alist = (Collection<Certificate>)portletState.getCertificatePortletUtil().
        			getAllEntityObjects(Certificate.class);
        	if(alist!=null && alist.size()>0)
			{
				ArrayList<Certificate> alistNew = new ArrayList<Certificate>();
				for(Iterator<Certificate> it = alist.iterator(); it.hasNext();)
				{
					Certificate c2 = it.next();
					if(c2.getStatus()!=null && 
							(c2.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_COLLECTED)))
						alistNew.add(c2);
				}
				portletState.setCertificateListing(alistNew);
			}
        	aRes.setRenderParameter("jspPage", "/html/certificateportlet/certificatelisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_CERTIFICATES_AGENCY);
        	if(portletState.getCertificateListing()==null || (portletState.getCertificateListing()!=null && portletState.getCertificateListing().size()==0))
        	{
        		portletState.addError(aReq, "There are currently no Certificates generated on the system yet.", portletState);
        	}
        }
        
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_ALL_CERTIFICATES_NSA.name()))
        {
        	portletState.setCertificateListing((Collection<Certificate>)portletState.getCertificatePortletUtil().getAllEntityObjects(Certificate.class));
        	aRes.setRenderParameter("jspPage", "/html/certificateportlet/certificatelisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_CERTIFICATES_NSA);
        	if(portletState.getCertificateListing()==null || (portletState.getCertificateListing()!=null && portletState.getCertificateListing().size()==0))
        	{
        		portletState.addError(aReq, "You have not created any End-User Certificates generated on the system yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_DISPUTED_CERTIFICATES.name()))
        {
        	Collection<Certificate> certList = null;
        	if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_END_USER))
        	{
        		Collection<Certificate> certList1 = (Collection<Certificate>)portletState.getCertificatePortletUtil().
            			getCertificatesByPortalUser(portletState.getPortalUser());
        		ArrayList<Certificate> al = new ArrayList<Certificate>();
        		if(certList!=null && certList.size()>0)
        		{
        			for(Iterator<Certificate> itC = certList.iterator(); itC.hasNext();)
        			{
        				Certificate cer = itC.next();
        				if(cer.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_DISPUTED))
        				{
        					al.add(cer);
        				}
        			}
        		}
        		portletState.setCertificateListing(al);
        		aRes.setRenderParameter("jspPage", "/html/certificateportlet/certificatelisting.jsp");
            	portletState.setCurrentTab(VIEW_TABS.VIEW_DISPUTED_CERTIFICATES);
            	if(portletState.getCertificateListing()==null || (portletState.getCertificateListing()!=null && portletState.getCertificateListing().size()==0))
            	{
            		portletState.addError(aReq, "You do not have any disputed EUC request applications on the system yet.", portletState);
            	}
            			
        	}else
        	{
        		certList = (Collection<Certificate>)portletState.getCertificatePortletUtil().
        			getCertificateByStatus(CertificateStatus.CERTIFICATE_STATUS_DISPUTED);
        		portletState.setCertificateListing(certList);
        		HashMap<String, Collection<Dispute>> olddisputeList = portletState.getCertificatePortletUtil().getDisputeList(Boolean.TRUE, DisputeType.DISPUTE_TYPE_CERTIFICATE);
        		HashMap<String, Collection<Dispute>> newdisputeList = portletState.getCertificatePortletUtil().getDisputeList(Boolean.FALSE, DisputeType.DISPUTE_TYPE_CERTIFICATE);
        		
        		aRes.setRenderParameter("jspPage", "/html/certificateportlet/certificatelisting.jsp");
            	portletState.setCurrentTab(VIEW_TABS.VIEW_DISPUTED_CERTIFICATES);
            	portletState.setOldDisputes(olddisputeList);
            	portletState.setNewDisputes(newdisputeList);
            	if(portletState.getCertificateListing()==null || (portletState.getCertificateListing()!=null && portletState.getCertificateListing().size()==0))
            	{
            		portletState.addError(aReq, "There are no disputed EUC request applications on the system yet.", portletState);
            	}
        	}
        	
        }
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_EXPIRED_CERTIFICATES.name()))
        {
        	Collection<Certificate> certList = null;
        	if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_END_USER))
        	{
        		Collection<Certificate> certList1 = (Collection<Certificate>)portletState.getCertificatePortletUtil().
            			getCertificatesByPortalUser(portletState.getPortalUser());
        		ArrayList<Certificate> al = new ArrayList<Certificate>();
        		if(certList!=null && certList.size()>0)
        		{
        			for(Iterator<Certificate> itC = certList.iterator(); itC.hasNext();)
        			{
        				Certificate cer = itC.next();
        				Calendar cal = Calendar.getInstance();
        				
        				if(cer.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_COLLECTED) && cal.after(cer.getExpireDate()))
        				{
        					al.add(cer);
        				}
        			}
        		}
        		portletState.setCertificateListing(al);
        		aRes.setRenderParameter("jspPage", "/html/certificateportlet/certificatelisting.jsp");
            	portletState.setCurrentTab(VIEW_TABS.VIEW_EXPIRED_CERTIFICATES);
            	if(portletState.getCertificateListing()==null || (portletState.getCertificateListing()!=null && portletState.getCertificateListing().size()==0))
            	{
            		portletState.addError(aReq, "You do not have any expired EUC request applications on the system yet.", portletState);
            	}
            			
        	}else
        	{
	        	certList = (Collection<Certificate>)portletState.getCertificatePortletUtil().getAllEntityObjects(Certificate.class);
	        			//getCertificateByStatus(CertificateStatus.C);
	        	portletState.setCertificateListing(certList);
        		ArrayList<Certificate> al = new ArrayList<Certificate>();
        		if(certList!=null && certList.size()>0)
        		{
        			for(Iterator<Certificate> itC = certList.iterator(); itC.hasNext();)
        			{
        				Certificate cer = itC.next();
        				Calendar cal = Calendar.getInstance();
        				
        				if(!cer.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_REJECTED) && cal.after(cer.getExpireDate()))
        				{
        					al.add(cer);
        				}
        			}
        		}
        		portletState.setCertificateListing(al);
	        	aRes.setRenderParameter("jspPage", "/html/certificateportlet/certificatelisting.jsp");
	        	portletState.setCurrentTab(VIEW_TABS.VIEW_EXPIRED_CERTIFICATES);
	        	if(portletState.getCertificateListing()==null || (portletState.getCertificateListing()!=null && portletState.getCertificateListing().size()==0))
	        	{
	        		portletState.addError(aReq, "There are no expired End-User certificates on the system at the moment.", portletState);
	        	}
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_RECALLED_CERTIFICATES.name()))
        {
	        portletState.setCertificateListing(portletState.getCertificatePortletUtil().getCertificateByStatus(CertificateStatus.CERTIFICATE_STATUS_RECALLED));
			if(portletState.getCertificateListing()==null || (portletState.getCertificateListing()!=null && 
					portletState.getCertificateListing().size()==0))
			{
				portletState.addError(aReq, "There are no recalled certificates on the system", portletState);
			}
			log.info("Viewing recalled certificates");
			portletState.setCurrentTab(VIEW_TABS.VIEW_RECALLED_CERTIFICATES);
        }
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_UTILIZED_CERTIFICATES.name()))
        {
        	//NOW REJECTED
        	//portletState.setApplicationListing(portletState.getCertificateUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_APPROVED));
        	Collection<Certificate> certList = null;
        	if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_END_USER))
        	{
        		Collection<Certificate> certList1 = (Collection<Certificate>)portletState.getCertificatePortletUtil().
            			getCertificatesByPortalUser(portletState.getPortalUser());
        		ArrayList<Certificate> al = new ArrayList<Certificate>();
        		if(certList!=null && certList.size()>0)
        		{
        			for(Iterator<Certificate> itC = certList.iterator(); itC.hasNext();)
        			{
        				Certificate cer = itC.next();
        				Calendar cal = Calendar.getInstance();
        				
        				if(cer.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_REJECTED) && cal.after(cer.getExpireDate()))
        				{
        					al.add(cer);
        				}
        			}
        		}
        		portletState.setCertificateListing(al);
        		aRes.setRenderParameter("jspPage", "/html/certificateportlet/certificatelisting.jsp");
            	portletState.setCurrentTab(VIEW_TABS.VIEW_UTILIZED_CERTIFICATES);
            	if(portletState.getCertificateListing()==null || (portletState.getCertificateListing()!=null && portletState.getCertificateListing().size()==0))
            	{
            		portletState.addError(aReq, "You do not have any rejected EUC request applications on the system yet.", portletState);
            	}
            			
        	}else
        	{
	        	certList = (Collection<Certificate>)portletState.getCertificatePortletUtil().
	        			getCertificateByStatus(CertificateStatus.CERTIFICATE_STATUS_REJECTED);
	        	portletState.setCertificateListing(certList);
	        	aRes.setRenderParameter("jspPage", "/html/certificateportlet/certificatelisting.jsp");
	        	portletState.setCurrentTab(VIEW_TABS.VIEW_UTILIZED_CERTIFICATES);
	        	if(portletState.getCertificateListing()==null || (portletState.getCertificateListing()!=null && portletState.getCertificateListing().size()==0))
	        	{
	        		portletState.addError(aReq, "There are no rejected End-User Certificates on the system at the moment.", portletState);
	        	}
        	}
        }
        
	}
	
	
	
	private void handleupload(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			CertificatePortletState portletState)
	{
		UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(aReq);
		String selectedApplications = uploadRequest.getParameter("selectedApplications");
		log.info("selectedApplications" + selectedApplications);
		String selectedUserAction = uploadRequest.getParameter("selectedUserAction");
		log.info("selectedUserAction" + selectedUserAction);
		if (selectedUserAction!=null && selectedUserAction.equalsIgnoreCase("issuecertificateStepThree"))
		{
			confirmPrint(aReq, aRes, portletState, swpService2, uploadRequest);
		}
		else if(selectedUserAction!=null && selectedUserAction.equalsIgnoreCase("cancelissuestepthree"))			//step 3 for certificate gen proceess //issueCertificateStepOne(aReq, aRes, swpService2, portletState);
		{
			Certificate cert = portletState.getCertificateSelected();
			cert.setCertificateNo(null);
			cert.setStatus(CertificateStatus.CERTIFICATE_STATUS_REJECTED);
			swpService2.updateRecord(cert);
			Collection<Certificate> li = (Collection<Certificate>)portletState.getCertificatePortletUtil().getAllEntityObjects(Certificate.class);
			portletState.setCertificateListing(li);
			new Util().pushAuditTrail(swpService2, cert.getId().toString(), 
					ECIMSConstants.REJECT_CERTIFICATE, 
					portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
			aRes.setRenderParameter("jspPage", "/html/certificateportlet/certificatelisting.jsp");
		}
	}

	private void handleActionsOnListCertificates(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			CertificatePortletState portletState) {
		// TODO Auto-generated method stub
		
		
		Long appCertId = null;
		
		
			String selectedApplications = aReq.getParameter("selectedApplications");
			log.info("selectedApplications" + selectedApplications);
			String selectedUserAction = aReq.getParameter("selectedUserAction");
			log.info("selectedUserAction" + selectedUserAction);
			try
			{
				appCertId = Long.valueOf(selectedApplications);
				portletState.setCertificateSelected((Certificate)portletState.getCertificatePortletUtil().getEntityObjectById(Certificate.class, appCertId));
				if(selectedUserAction!=null && selectedUserAction.equalsIgnoreCase("viewcertificate"))
				{
					aRes.setRenderParameter("jspPage", "/html/certificateportlet/viewcertificate.jsp");
				}
				else if(selectedUserAction!=null && selectedUserAction.equalsIgnoreCase("disputeproceed"))
				{
					String comment = aReq.getParameter("comments");
					
					if(portletState!=null && portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))
		  			{
						Certificate cert = portletState.getCertificateSelected();
						if(comment!=null)
						{
//							Enquiry en = new Enquiry();
//							Date d = new Date();
//							en.setDateCreated(new Timestamp(d.getTime()));
//							en.setEnquireDetail(comment);
//							en.setPortalUser(portletState.getPortalUser());
//							en.setSubject("Dispute Certificate");
//							en.setEnquiryId(cert.getId());
//							swpService.createNewRecord(en);
							Dispute d = new Dispute();
							d.setActedOn(Boolean.FALSE);
							d.setActedOnByPortalUserId(null);
							d.setApplication(null);
							d.setCertificate(cert);
							d.setDateActedOn(null);
							d.setDateCreated(new Timestamp((new Date()).getTime()));
							d.setDetails(comment);
							d.setDisputeType(DisputeType.DISPUTE_TYPE_CERTIFICATE);
							d.setPortalUser(portletState.getPortalUser());
							swpService.createNewRecord(d);
						}
						
						cert.setStatus(CertificateStatus.CERTIFICATE_STATUS_DISPUTED);
						swpService2.updateRecord(cert);
						portletState.addSuccess(aReq, "Certificate Disputed successfully.", portletState);
						new Util().pushAuditTrail(swpService2, cert.getId().toString(), ECIMSConstants.DISPUTE_CERTIFICATE, 
								portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
						aRes.setRenderParameter("jspPage", "/html/certificateportlet/certificatelisting.jsp");
						
						portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_CERTIFICATES_NSA);
						notifyCertificateAction(cert,
								portletState, "disputed.");
						portletState.setCertificateSelected(null);
						
						if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))
						{
							portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_CERTIFICATES_AGENCY);
							Collection<Certificate> alist = (Collection<Certificate>)portletState.getCertificatePortletUtil().getAllEntityObjects(Certificate.class);
							portletState.setCertificateListing(null);
							if(alist!=null && alist.size()>0)
							{
								ArrayList<Certificate> alistNew = new ArrayList<Certificate>();
								for(Iterator<Certificate> it = alist.iterator(); it.hasNext();)
								{
									Certificate c2 = it.next();
									if(c2.getStatus()!=null && 
											(c2.getStatus().equals(CertificateStatus.CERTIFICATE_STATUS_COLLECTED)))
										alistNew.add(c2);
								}
								portletState.setCertificateListing(alistNew);
							}
						}else
						{
							portletState.setCertificateListing((Collection<Certificate>)portletState.getCertificatePortletUtil().getAllEntityObjects(Certificate.class));
						}
		  			}else
		  			{
		  				portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
		  				aRes.setRenderParameter("jspPage", "/html/certificateportlet/addcomment.jsp");
		  			}
					
				}
				else if(selectedUserAction!=null && selectedUserAction.equalsIgnoreCase("recallcertificate"))
				{
					if(portletState!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_ISSUE_CERTIFICATE))
		  			{
						Certificate cert = portletState.getCertificateSelected();
						cert.setStatus(CertificateStatus.CERTIFICATE_STATUS_RECALLED);
						swpService2.updateRecord(cert);
						new Util().pushAuditTrail(swpService2, cert.getId().toString(), ECIMSConstants.RECALL_CERTIFICATE, 
								portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
						portletState.addSuccess(aReq, "Certificate Recalled successfully.", portletState);
						notifyCertificateRecall(cert, portletState, cert.getApplication().getApplicant().getPortalUser(), "Recalled");
						if(portletState.getCurrentTab()==null)
						{
							if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_END_USER))
								portletState.setCertificateListing((Collection<Certificate>)portletState.getCertificatePortletUtil().getCertificatesByPortalUser(portletState.getPortalUser()));
							else if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_USER))
								portletState.setCertificateListing((Collection<Certificate>)portletState.getCertificatePortletUtil().getAllEntityObjects(Certificate.class));
							
							if(portletState.getCertificateListing()==null || (portletState.getCertificateListing()!=null && 
									portletState.getCertificateListing().size()==0))
							{
								portletState.addError(aReq, "There are no certificates on the system", portletState);
							}
						}
						else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ALL_CERTIFICATES_EU))
						{
							portletState.setCertificateListing((Collection<Certificate>)portletState.getCertificatePortletUtil().getCertificatesByPortalUser(portletState.getPortalUser()));
							if(portletState.getCertificateListing()==null || (portletState.getCertificateListing()!=null && 
									portletState.getCertificateListing().size()==0))
							{
								portletState.addError(aReq, "There are no certificates on the system", portletState);
							}
						}
						else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ALL_CERTIFICATES_NSA))
						{
							portletState.setCertificateListing((Collection<Certificate>)portletState.getCertificatePortletUtil().getAllEntityObjects(Certificate.class));
							if(portletState.getCertificateListing()==null || (portletState.getCertificateListing()!=null && 
									portletState.getCertificateListing().size()==0))
							{
								portletState.addError(aReq, "There are no certificates on the system", portletState);
							}
						}
						else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_DISPUTED_CERTIFICATES))
						{
							portletState.setCertificateListing(portletState.getCertificatePortletUtil().getCertificateByStatus(CertificateStatus.CERTIFICATE_STATUS_DISPUTED));
							if(portletState.getCertificateListing()==null || (portletState.getCertificateListing()!=null && 
									portletState.getCertificateListing().size()==0))
							{
								portletState.addError(aReq, "There are no disputed certificates on the system", portletState);
							}
						}
						else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_RECALLED_CERTIFICATES))
						{
							portletState.setCertificateListing(portletState.getCertificatePortletUtil().getCertificateByStatus(CertificateStatus.CERTIFICATE_STATUS_RECALLED));
							if(portletState.getCertificateListing()==null || (portletState.getCertificateListing()!=null && 
									portletState.getCertificateListing().size()==0))
							{
								portletState.addError(aReq, "There are no recalled certificates on the system", portletState);
							}
							log.info("Viewing recalled certificates");
								
						}
						else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_EXPIRED_CERTIFICATES))
						{
							Collection<Certificate> li = (Collection<Certificate>)portletState.getCertificatePortletUtil().getAllEntityObjects(Certificate.class);
							ArrayList<Certificate> al = new ArrayList<Certificate>();
							for(Iterator<Certificate> crIt = li.iterator(); crIt.hasNext();)
							{
								Certificate certl = crIt.next();
								Calendar cal = Calendar.getInstance();
								Date issDate = cert.getIssuanceDate();
								cal.add(Calendar.YEAR, -1);
								//issDate= 2010-10-10 2011-10-10
								if(cal.getTime().after(issDate))
									al.add(certl);
							}
							if(al.size()>0)
								portletState.setCertificateListing(al);
							else
								portletState.addError(aReq, "There are no expired certificates on the system", portletState);
						}
						
						aRes.setRenderParameter("jspPage", "/html/certificateportlet/certificatelisting.jsp");
		  			}
					else
					{
						portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
					}
				}else if(selectedUserAction!=null && selectedUserAction.equalsIgnoreCase("canceldisputecertificate"))
				{
					if(portletState!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_ISSUE_CERTIFICATE))
		  			{
						Certificate cert = portletState.getCertificateSelected();
						cert.setStatus(CertificateStatus.CERTIFICATE_STATUS_COLLECTED);
						swpService2.updateRecord(cert);
						new Util().pushAuditTrail(swpService2, portletState.getCertificateSelected().getId().toString(), ECIMSConstants.CANCEL_DISPUTE_CERTIFICATE, 
								portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
						portletState.addSuccess(aReq, "Certificate Dispute Canceled successfully.", portletState);
						portletState.setCertificateListing((Collection<Certificate>)portletState.getCertificatePortletUtil().getAllEntityObjects(Certificate.class));
						portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_CERTIFICATES_NSA);
						aRes.setRenderParameter("jspPage", "/html/certificateportlet/certificatelisting.jsp");
		  			}else
		  			{
		  				portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
		  			}
				}
				else if(selectedUserAction!=null && selectedUserAction.equalsIgnoreCase("disputecertficate"))
				{
					if(portletState!=null && portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))
		  			{
//						Certificate cert = portletState.getCertificateSelected();
//						cert.setStatus(CertificateStatus.CERTIFICATE_STATUS_DISPUTED);
//						swpService2.updateRecord(cert);
//						portletState.addSuccess(aReq, "Certificate Disputed successfully.", portletState);
//						new Util().pushAuditTrail(swpService2, cert.getId().toString(), ECIMSConstants.DISPUTE_CERTIFICATE, 
//								portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
						aRes.setRenderParameter("jspPage", "/html/certificateportlet/addcomment.jsp");
//						portletState.setCertificateListing((Collection<Certificate>)portletState.getCertificatePortletUtil().getAllEntityObjects(Certificate.class));
//						portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_CERTIFICATES_NSA);
//						notifyCertificateAction(cert,
//								portletState, "disputed.");
		  			}else
		  			{
		  				portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
		  			}
				}
				else if(selectedUserAction!=null && selectedUserAction.equalsIgnoreCase("acceptdisputecertficate"))
				{
					if(portletState!=null && portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))
		  			{
						Certificate cert = portletState.getCertificateSelected();
						cert.setStatus(CertificateStatus.CERTIFICATE_STATUS_REJECTED);
						swpService2.updateRecord(cert);
						portletState.addSuccess(aReq, "Certificate has been rejected due to the dispute(s) raised.", portletState);
						portletState.setCertificateListing((Collection<Certificate>)portletState.getCertificatePortletUtil().getAllEntityObjects(Certificate.class));
						new Util().pushAuditTrail(swpService2, cert.getId().toString(), ECIMSConstants.ACCEPT_DISPUTE_CERTIFICATE, 
								portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
						aRes.setRenderParameter("jspPage", "/html/certificateportlet/certificatelisting.jsp");
						portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_CERTIFICATES_NSA);
						notifyCertificateRecall(cert, portletState, cert.getApplication().getApplicant().getPortalUser(), "Rejected due to disputes raised on it.");
		  			}else
		  			{
		  				portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
		  			}
				}
				else if(selectedUserAction!=null && selectedUserAction.equalsIgnoreCase("collectcertificate"))
				{
					
					aRes.setRenderParameter("jspPage", "/html/certificateportlet/collectcertificate.jsp");
				}else if(selectedUserAction!=null && selectedUserAction.equalsIgnoreCase("uploadcertificate"))
				{
					
					ApplicationWorkflow aw = portletState.getCertificatePortletUtil().
							getApplicationWorkFlowByApplicationAndStatus(
									portletState.getCertificateSelected().getApplication(), ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED);
					portletState.setApplicationWorkflow(aw);
					new Util().pushAuditTrail(swpService2, portletState.getCertificateSelected().getId().toString(), ECIMSConstants.UPLOAD_CERTIFICATE, 
							portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
					aRes.setRenderParameter("jspPage", "/html/certificateportlet/uploadcertificate.jsp");
					
				}else if(selectedUserAction!=null && selectedUserAction.equalsIgnoreCase("cancelcollect"))
				{
					aRes.setRenderParameter("jspPage", "/html/certificateportlet/certificatelisting.jsp");
				}
			}catch(NumberFormatException e)
			{
				e.printStackTrace();
				portletState.addError(aReq, "Invalid certificate selected. Ensure you select on a valid certificate to carry out this action", portletState);
			}
		
	}
	
	
	private void notifyCertificateAction(Certificate cert,
			CertificatePortletState portletState, String as) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		Collection<PortalUser> pl = portletState.getCertificatePortletUtil().
				getPortalUserByPermission(PermissionType.PERMISSION_FORWARD_APPLICATION);
		String message = "An End-User Certificate - " + cert.getCertificateNo() + " - " +
				"has been " + as;
		
		if(pl!=null && pl.size()>0)
		{
			for(Iterator<PortalUser> puIt = pl.iterator(); puIt.hasNext();)
			{
				PortalUser pu = puIt.next();
				try{
//					message = "Approval request awaiting your action. " +
//							"Visit " + portletState.getSystemUrl().getValue() + " to view requests awaiting your " +
//							"approval/disapproval action";
					new SendSms(pu.getPhoneNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
					log.info("Sending SMS to " + pu.getPhoneNumber());
				}catch(Exception e){
					log.error("error sending sms ",e);
				}
			}
		}
			
			
		
	}
	
	
	private void notifyCertificateRecall(Certificate cert,
			CertificatePortletState portletState, PortalUser pu, String actions) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		String message = "Your End-User Certificate - " + cert.getCertificateNo() + " - " +
				"has been " + actions;
		
		if(pu!=null)
		{
			
				try{
//					message = "Approval request awaiting your action. " +
//							"Visit " + portletState.getSystemUrl().getValue() + " to view requests awaiting your " +
//							"approval/disapproval action";
					new SendSms(pu.getPhoneNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
					
				}catch(Exception e){
					log.error("error sending sms ",e);
				}
			
		}
			
			
		
	}
	
	
	private ImageUpload uploadImage(UploadPortletRequest uploadRequest, String parameterName, String folderDestination)
	{
		ImageUpload imageUpload =null;
		try
		{
			String sourceFileName = uploadRequest.getFileName(parameterName);
			String[] strArr = sourceFileName.split(".");
			File file = uploadRequest.getFile(parameterName);
			if(file.length()/1048576 < 3)
			{
				log.info("Nome file:" + uploadRequest.getFileName(parameterName));
				File newFile = null;
				String newFileName = RandomStringUtils.random(7, true, true) + "." + (strArr.length>0 ? strArr[strArr.length-1] : "png");
				newFile = new File(folderDestination + newFileName);
				log.info("New file name: " + newFile.getName());
				log.info("New file path: " + newFile.getPath());
				InputStream in;
				
				in = new BufferedInputStream(uploadRequest.getFileAsStream(parameterName));
				FileInputStream fis = new FileInputStream(file);
				FileOutputStream fos = new FileOutputStream(newFile);
				byte[] bytes_ = FileUtil.getBytes(in);
				int i = fis.read(bytes_);
				while (i != -1) {
					fos.write(bytes_, 0, i);
					i = fis.read(bytes_);
				}
				fis.close();
				fos.close();
				
				Float size = (float) newFile.length();
				System.out.println("file size bytes:" + size);
				System.out.println("file size Mb:" + size / 1048576);
				imageUpload = new ImageUpload();
				imageUpload.setNewFileName(newFileName);
				imageUpload.setUploadValid(true);
			}
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		return imageUpload;
	}
	
	
	private void confirmPrint(ActionRequest aReq, ActionResponse aRes,
			CertificatePortletState portletState,
			SwpService swpService2, UploadPortletRequest uploadRequest) {
		// TODO Auto-generated method stub
		
		
		if(portletState.getPermissionList().contains(PermissionType.PERMISSION_ISSUE_CERTIFICATE))
		{
			
			System.out.println("Size for certificatemagen: "+uploadRequest.getSize("certificatemagen"));
			System.out.println("Size for certificatemagen: "+uploadRequest.getSize("certificatemagen"));
			if (uploadRequest.getSize("certificatemagen")==0) {
				portletState.addError(aReq, "Ensure you select your the scanned copy of your certificate", portletState);
				aRes.setRenderParameter("jspPage", "/html/certificateportlet/uploadcertificate.jsp");
			}else
			{
				String folder = ECIMSConstants.NEW_APPLICANT_DIRECTORY;
				ImageUpload certificatemagenImageFile = uploadImage(uploadRequest, "certificatemagen", folder);
				if(certificatemagenImageFile!=null && (certificatemagenImageFile.isUploadValid()))
				{
					Certificate cert = portletState.getCertificateSelected();
					cert.setSignature(certificatemagenImageFile.getNewFileName());
					cert.setCertificatePrinted(Boolean.TRUE);
					swpService2.updateRecord(cert);
					Application ac = cert.getApplication();
//					String certificateCollectionCode = RandomStringUtils.random(10, true, true);
//					ac.setCertificateCollectionCode(certificateCollectionCode);
//					swpService2.updateRecord(cert);
						
					portletState.addSuccess(aReq, "Certificate Issuance successful! Request the applicant to provide the collection code sent to their email address and phone number. <br>" +
							"<br>You must request the End-User to provide you with this collection code before giving the printed certificate to the End-User. " +
							"To carry this action out, click on the certificate icon in your dashboard page then follow the instructions on the page.", portletState);
					portletState.setCertificateListing((Collection<Certificate>)portletState.getCertificatePortletUtil().getAllEntityObjects(Certificate.class));
					aRes.setRenderParameter("jspPage", "/html/certificateportlet/certificatelisting.jsp");
				}else
				{
					portletState.addError(aReq, "Certificate upload was not successful. Please try again", portletState);
				}
			}
			
		}else
		{
			portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
		}
	
	}
	
	private void handleCollectCertificate(ActionRequest aReq, ActionResponse aRes, SwpService swpService2, CertificatePortletState portletState)
	{
		if(portletState!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_ISSUE_CERTIFICATE))
			{
				String certificateCode = aReq.getParameter("certificateCode");
				if(portletState.getCertificateSelected()!=null && 
						portletState.getCertificateSelected().getApplication().getCertificateCollectionCode()!=null && 
						portletState.getCertificateSelected().getApplication().getCertificateCollectionCode().equals(certificateCode))
				{
					Certificate cert = portletState.getCertificateSelected();
					cert.setStatus(CertificateStatus.CERTIFICATE_STATUS_COLLECTED);
					swpService2.updateRecord(cert);
					new Util().pushAuditTrail(swpService2, cert.getId().toString(), ECIMSConstants.COLLECT_CERTIFICATE, 
							portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
					portletState.addSuccess(aReq, "Certificate Issuance & Collection Process Complete!", portletState);
					portletState.setCertificateListing((Collection<Certificate>)portletState.getCertificatePortletUtil().getAllEntityObjects(Certificate.class));
					portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_CERTIFICATES_NSA);
					aRes.setRenderParameter("jspPage", "/html/certificateportlet/certificatelisting.jsp");
					String rec = (cert.getApplication().getApplicant()!=null ? 
							cert.getApplication().getApplicant().getPortalUser().getFirstName() + " " + cert.getApplication().getApplicant().getPortalUser().getSurname() : 
								(cert.getApplication().getPortalUser()!=null ?  
										cert.getApplication().getPortalUser().getFirstName() + " " + cert.getApplication().getPortalUser().getSurname() : ""));
					String msg = "New certificate has been issued to " + rec + ". \nCertificate Number: " + cert.getCertificateNo();
							
					notifyCertificateIssuance(portletState, msg);
					
				}else
				{
					Certificate cert = portletState.getCertificateSelected();
					cert.setStatus(CertificateStatus.CERTIFICATE_STATUS_ISSUED);
					swpService2.updateRecord(cert);
					portletState.addSuccess(aReq, "Certificate Issuance & Collection Process could not be completed. Ensure you provide the correct collection code from the Certificates End-User.", portletState);
					aRes.setRenderParameter("jspPage", "/html/certificateportlet/certificatelisting.jsp");
				}
			}else
			{
				portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
			}
	}

	private void notifyCertificateIssuance(CertificatePortletState portletState, String msg) {
		// TODO Auto-generated method stub
		Collection<PortalUser> agencyList = CertificatePortletState.getCertificatePortletUtil().getAllPortalUserByAgencyType(AgencyType.INFORMATION_GROUP.getValue());
		if(agencyList!=null && agencyList.size()>0)
		{
			for(Iterator<PortalUser>it = agencyList.iterator(); it.hasNext();)
			{
				PortalUser pu = it.next();
				try{
					new SendSms(pu.getPhoneNumber(), msg, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
					
				}catch(Exception e){
					log.error("error sending sms ",e);
				}
			}
		}
	}

	private void handleActionsOnOneCertificate(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			CertificatePortletState portletState) {
		// TODO Auto-generated method stub
		if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
		{
			handleForNSAUserGroup(aReq, aRes, swpService2, portletState);
		}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
		{
			handleForAgencyUserGroup(aReq, aRes, swpService2, portletState);
		}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_ADMIN_GROUP))
		{
			handleForAdminUserGroup(aReq, aRes, swpService2, portletState);
		}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
		{
			handleForEndUserGroup(aReq, aRes, swpService2, portletState);
		}    	
	}

	
	private void handleForEndUserGroup(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, CertificatePortletState portletState) {
		// TODO Auto-generated method stub
		
	}

	private void handleForAdminUserGroup(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			CertificatePortletState portletState) {
		// TODO Auto-generated method stub
		
	}

	private void handleForAgencyUserGroup(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			CertificatePortletState portletState) {
		// TODO Auto-generated method stub
		
	}

	private void handleForNSAUserGroup(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2,
			CertificatePortletState portletState) {
		// TODO Auto-generated method stub
		
		String act = aReq.getParameter("act");
		log.info("act=" + act);
		String actId = aReq.getParameter("actId");
		log.info("actId=" + actId);
		try
		{
			Certificate appW = (Certificate)portletState.getCertificatePortletUtil().
					getEntityObjectById(Certificate.class, Long.valueOf(actId));
			
			
			if(appW!=null)
			{
				
				if(act!=null && act.equals("cancelcollect"))
				{
//					portletState.setApplicationWorkflow(appW);
//					portletState.setApplication(appW.getApplication());
//					handleViewApplication(aReq, aRes, swpService2, portletState);
					aRes.setRenderParameter("jspPage", "/html/certificateportlet/certificatelisting.jsp");
				}
				else if(act!=null && act.equals("collectcertificate"))
				{
//					portletState.setApplicationWorkflow(appW);
//					portletState.setApplication(appW.getApplication());
//					handleViewApplicationApplicant(aReq, aRes, swpService2, portletState);
					handleCollectCertificate(aReq, aRes, swpService2, portletState);
				}
				else
				{
					if(appW.getApplication().getApplicant().getBlackList()!=null && 
							appW.getApplication().getApplicant().getBlackList().equals(Boolean.FALSE))
					{
//						portletState.setApplicationWorkflow(appW);
//						portletState.setApplication(appW.getApplication());
//						if(act!=null && act.equals("forward"))
//							handleForwardApplication(aReq, aRes, swpService2, portletState);
//						else if(act!=null && act.equals("rejectapplication"))
//							aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/reject.jsp");
//						else if(act!=null && act.equals("rejectthisapplication"))
//							handleRejectApplication(aReq, aRes, swpService2, portletState);
//						else if(act!=null && act.equals("addtoexception"))
//							handleAddToException(aReq, aRes, swpService2, portletState);
//						else if(act!=null && act.equals("removefromexception"))
//							handleRemoveFromException(aReq, aRes, swpService2, portletState);
//						else if(act!=null && act.equals("blacklistapplicant"))
//							handleBlackListApplication(aReq, aRes, swpService2, portletState);
//						else if(act!=null && act.equals("unblacklistapplicant"))
//							handleUnBlackListApplication(aReq, aRes, swpService2, portletState);
//						else if(act!=null && act.equals("approveone"))
//							handleApproveOneApplication(aReq, aRes, swpService2, portletState);
//						else if(act!=null && act.equals("approve"))
//							handleApproveApplication(aReq, aRes, swpService2, portletState);
//						else if(act!=null && act.equals("rejectone"))
//							handleRejectOneApplication(aReq, aRes, swpService2, portletState);
//						else if(act!=null && act.equals("reject"))
//							handleRejectApplication(aReq, aRes, swpService2, portletState);
//						else if(act!=null && act.equals("issuecertificate"))			//step 3 for certificate gen proceess
//							issueCertificate(aReq, aRes, swpService2, portletState);
//						else if(act!=null && act.equals("addbiometric"))				//step 1 for certificate gen proceess
//							handleAddBiometric(aReq, aRes, portletState, swpService2);
//						else if(act!=null && act.equals("capturebiometric"))				//step 2 for certificate gen proceess
//							captureBiometric(aReq, aRes, portletState, swpService2);
//						//handle flow for forward from step one to step x
//						else if(act!=null && act.equalsIgnoreCase("fwdtoagencystepone"))
//							forwardApplicationToAgencyStepOne(aReq, aRes, swpService2, portletState);
//						else if(act!=null && act.equalsIgnoreCase("cancelfwdstepone"))
//							aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
//						else if(act!=null && act.equalsIgnoreCase("fwdtoagencysteptwo"))
//							forwardApplicationToAgencyStepTwo(aReq, aRes, swpService2, portletState);
//						else if(act!=null && act.equalsIgnoreCase("gobackfwdsteptwo"))
//							aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/forwardstepone.jsp");
//						
					}else
					{
						portletState.addError(aReq, "Your action can not be carried out on the application because the applicant is currently on a blacklist", portletState);
					}
				}
				
				
				
				
			}else
			{
				portletState.addError(aReq, "Invalid EUC Request application selected", portletState);
			}
		}catch(NumberFormatException e)
		{
			e.printStackTrace();
		}
		
		
	}

}
