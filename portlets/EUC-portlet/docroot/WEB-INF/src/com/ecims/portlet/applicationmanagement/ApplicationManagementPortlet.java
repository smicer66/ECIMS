package com.ecims.portlet.applicationmanagement;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import javax.naming.NamingException;
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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import smartpay.audittrail.AuditTrail;
import smartpay.entity.Agency;
import smartpay.entity.Applicant;
import smartpay.entity.Application;
import smartpay.entity.ApplicationAttachment;
import smartpay.entity.ApplicationAttachmentAgency;
import smartpay.entity.ApplicationAttachmentType;
import smartpay.entity.ApplicationFlag;
import smartpay.entity.ApplicationItem;
import smartpay.entity.ApplicationWorkflow;
import smartpay.entity.Certificate;
import smartpay.entity.Company;
import smartpay.entity.Country;
import smartpay.entity.Currency;
import smartpay.entity.EndorsedApplicationDesk;
import smartpay.entity.EndorsementDesk;
import smartpay.entity.ItemCategory;
import smartpay.entity.ItemCategorySub;
import smartpay.entity.Permission;
import smartpay.entity.PortCode;
import smartpay.entity.PortalUser;
import smartpay.entity.QuantityUnit;
import smartpay.entity.RoleType;
import smartpay.entity.State;
import smartpay.entity.WeightUnit;
import smartpay.entity.WokFlowSetting;
import smartpay.entity.enumerations.AgencyType;
import smartpay.entity.enumerations.ApplicantStatus;
import smartpay.entity.enumerations.ApplicantType;
import smartpay.entity.enumerations.ApplicationStatus;
import smartpay.entity.enumerations.CertificateStatus;
import smartpay.entity.enumerations.PermissionType;
import smartpay.entity.enumerations.RoleConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.entity.enumerations.UserStatus;
import smartpay.service.SwpService;

import com.ecims.commins.AuthUser;
import com.ecims.commins.ECIMSConstants;
import com.ecims.commins.ImageUpload;
import com.ecims.commins.Mailer;
import com.ecims.commins.SendSms;
import com.ecims.commins.Util;
import com.ecims.portlet.applicationmanagement.ApplicationManagementPortlet;
import com.ecims.portlet.applicationmanagement.ApplicationManagementPortletState;
import com.ecims.portlet.applicationmanagement.ApplicationManagementPortletState.APPLIST_ACTIONS;
import com.ecims.portlet.applicationmanagement.ApplicationManagementPortletState.EU_ACTIONS;
import com.ecims.portlet.applicationmanagement.ApplicationManagementPortletState.AGENCY_ACTIONS;
import com.ecims.portlet.applicationmanagement.ApplicationManagementPortletState.NSA_ACTIONS;
import com.ecims.portlet.applicationmanagement.ApplicationManagementPortletUtil;
import com.ecims.portlet.applicationmanagement.ApplicationManagementPortletState.VIEW_TABS;
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
import com.lowagie.text.pdf.PdfGState;
import com.rsa.authagent.authapi.AuthSession;
import com.sf.primepay.smartpay13.HibernateUtils;
import com.sf.primepay.smartpay13.ServiceLocator;
import com.trice.fk.jyhon.*;
/**
 * Portlet implementation class ApplicationManagementPortlet
 */
public class ApplicationManagementPortlet extends MVCPortlet {
	private Logger log = Logger.getLogger(ApplicationManagementPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	private String dpe = "03";
	public com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	ApplicationManagementPortletUtil util = ApplicationManagementPortletUtil.getInstance();
	ServiceContext serviceContext = new ServiceContext();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	private String mp = "03";
	private int yp = 2016;
	
	
	@Override
	public void init(PortletConfig config) throws PortletException {
		// TODO Auto-generated method stub
		//log.info("Administrative portlet init called...");		
		pContext = config.getPortletContext();
		super.init(config);
		pConfig = config;
	    this.swpService = this.serviceLocator.getSwpService();
	}
	
	@Override
	public void render(RenderRequest renderRequest,
			RenderResponse renderResponse) throws PortletException, IOException {
		//log.info("Administrative render called...");	
		PortletSession ps = renderRequest.getPortletSession();
		ApplicationManagementPortletState portletState = 
				ApplicationManagementPortletState.getInstance(renderRequest, renderResponse);

		//log.info(">>>next page = " + renderRequest.getParameter("jspPage"));
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
	
	public boolean managePortletState(Long userId)
	{
		CallMandate callMandate = new CallMandate();
		return callMandate.crossCheckUserSession(userId);
		
	}
	
	
	
	@Override
	public void processAction(ActionRequest aReq,
			ActionResponse aRes) throws IOException, PortletException {
		SessionMessages.add(aReq, pConfig.getPortletName()
				+ SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
		//log.info("inside process Action");
		
		
		
		
		ApplicationManagementPortletState portletState = ApplicationManagementPortletState.getInstance(aReq, aRes);
		
		
		String action = aReq.getParameter("action");
		//log.info("action == " + action);
		if (action == null) {
			//log.info("----------------action value is null----------------");
			return;
		}
		else
		{
			//log.info("----------------action value is " + action +"----------------");
		}
        if (portletState == null) {
			//log.info("----------------portletState is null----------------");
			return;
		}
        
        /*************POST ACTIONS*********************/
        if(action.equalsIgnoreCase(EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_ONE.name()))
        {
        	//log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	if(managePortletState(portletState.getPortalUser().getId())==false)
        	{
        		createApplicationEUStepOne(aReq, aRes, swpService, portletState);
        	}
        }
        if(action.equalsIgnoreCase(EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_TWO.name()))
        {
        	//log.info("CREATE_A_PORTAL_USER_STEP_TWO");
        	//Select Account type
        	String act = aReq.getParameter("act");
        	//log.info("act==" + act);
        	
        	if(managePortletState(portletState.getPortalUser().getId())==false)
        	{
        		
	        	if(act!=null && act.equalsIgnoreCase("next"))
		        	createApplicationEUStepTwo(aReq, aRes, swpService, portletState);
	        	else if(act!=null && act.equalsIgnoreCase("Add"))
	        		createApplicationEUStepTwo(aReq, aRes, swpService, portletState);
	        	else if(act!=null && act.equalsIgnoreCase("Clear"))
	        		clearApplicationItems(aReq, aRes, swpService, portletState);
	    		else if(act!=null && act.equalsIgnoreCase("backfromsteptwomakeapplication"))
	        		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepone.jsp");
        	}
        		
        }
        if(action.equalsIgnoreCase(EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_THREE.name()))
        {
        	//log.info("CREATE_A_PORTAL_USER_STEP_THREE");
        	
        	if(managePortletState(portletState.getPortalUser().getId())==false)
        	{
	        	//Select Account type
	        	UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(aReq);
	        	String act = uploadRequest.getParameter("act");
	        	//log.info("act===" + act);
	        	if(act!=null && act.equalsIgnoreCase("next"))
	        		createApplicationEUStepThree(aReq, aRes, swpService, portletState, uploadRequest);
	        	else if(act!=null && act.equalsIgnoreCase("backfromstepthreemakeapplication"))
	        		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/steptwo.jsp");
        	}
        		
        }
        if(action.equalsIgnoreCase(EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_FOUR.name()))
        {
        	//log.info("CREATE_A_PORTAL_USER_STEP_TWO");
        	//Select Account type
        	createApplicationEUStepFour(aReq, aRes, swpService, portletState);
        		
        }
        
        
        if(action.equalsIgnoreCase(AGENCY_ACTIONS.SIGN_ENDORSE_APPLICATION.name()))
        {
        	//log.info("NSA_ACTION");
        	handleEndorseApplicationStepTwo(aReq, aRes, swpService, portletState);
			 
        }
        

        if(action.equalsIgnoreCase(APPLIST_ACTIONS.HANDLE_ACTIONS_ON_LIST_APPLICATIONS.name()))
        {
        	//log.info("HANDLE ACTIONS ON APPLICATiON LISTING");
        	handleActionsOnListApplications(aReq, aRes, swpService, portletState);
        		
        }
        
        
        if(action.equalsIgnoreCase(APPLIST_ACTIONS.ACT_ON_APPLICATION.name()))
        {
        	//log.info("HANDLE ACTIONS ON ONE APPLICATiON");
        	handleActionsOnOneApplication(aReq, aRes, swpService, portletState);
        	
        }
        
        if(action.equalsIgnoreCase(APPLIST_ACTIONS.ACT_ON_UPLOAD_CERTIFICATE.name()))
        {
        	//log.info("HANDLE ACTIONS ON ONE APPLICATiON");
        	UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(aReq);
        	String act = uploadRequest.getParameter("act");
        	String actId = uploadRequest.getParameter("actId");
        	if(act!=null && act.equals("issuecertificateStepThree"))			//step 3 for certificate gen proceess //issueCertificateStepOne(aReq, aRes, swpService2, portletState);
				confirmPrint(uploadRequest, aRes, aReq, portletState, swpService, actId);
			else if(act!=null && act.equals("cancelissuestepthree"))			//step 3 for certificate gen proceess //issueCertificateStepOne(aReq, aRes, swpService2, portletState);
			{
				Certificate cert = portletState.getCertificateGenerated();
				//cert.setCertificateNo(null);
				cert.setSignature(null);
				cert.setCertificatePrinted(null);
				swpService.updateRecord(cert);
				
				new Util().pushAuditTrail(swpService, cert.getId().toString(), 
						ECIMSConstants.REJECT_CERTIFICATE, 
						portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/issuecertificate_steptwo.jsp");
			}
        	
        }
        
        

        if(action.equalsIgnoreCase(NSA_ACTIONS.SIGN_APPROVE_APPLICATION.name()))
        {
        	//log.info("HANDLE ACTIONS ON APPLICATiON LISTING");
        	approveApplicationWithToken(aReq, aRes, swpService, portletState);
        		
        }

        if(action.equalsIgnoreCase(VIEW_TABS.CREATE_AN_APPLICATION_EU.name()))
        {
        	portletState.reinitializeForNewApplication(portletState);
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
        		portletState.setAgencyApplicant(null);
        	}
        	else
        	{
        		portletState.setApplicant(null);
        		portletState.setAgencyApplicant(portletState.getPortalUser());
        	}
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepone.jsp");
        	portletState.setCurrentTab(VIEW_TABS.CREATE_AN_APPLICATION_EU);
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_ALL_APPLICATIONS_EU.name()))
        {
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
        		portletState.setAgencyApplicant(null);
        		if(portletState.getApplicant()!=null)
        			portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByApplicant(portletState.getApplicant()));
        		else
        			portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByAgencyApplicant(portletState.getAgencyApplicant()));
        	}
        	else
        	{
        		portletState.setApplicant(null);
        		portletState.setAgencyApplicant(portletState.getPortalUser());
        		portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByAgencyApplicant(portletState.getAgencyApplicant()));
        	}
        	
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_ALL_APPLICATIONS_EU);
        	if(portletState.getApplicationListing()==null || (portletState.getApplicationListing()!=null && portletState.getApplicationListing().size()==0))
        	{
        		portletState.addError(aReq, "You have not created any EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_FORWARDED_APPLICATIONS_NSA.name()))
        {
        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        			getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(portletState.getPortalUser().getRoleType().getId(), 
							ApplicationStatus.APPLICATION_STATUS_FORWARDED, false);
//        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
//        			getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_FORWARDED, false, 
//							portletState.getPortalUser().getAgency());
        	portletState.setApplicationWorkFlowListing(appList);
        	//portletState.setApplicationFlags(portletState.getApplicationManagementPortletUtil().getApplicationFlags());
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_FORWARDED_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no EUC request applications forwarded yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_APPROVED_APPLICATIONS_EU.name()))
        {
//        	portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
        	
        	
        	
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
        		portletState.setAgencyApplicant(null);
        		portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByApplicantAndStatus(
        				portletState.getApplicant(), 
        				new ApplicationStatus[]{ApplicationStatus.APPLICATION_STATUS_APPROVED, ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED}));
        	}
        	else
        	{
        		portletState.setApplicant(null);
        		portletState.setAgencyApplicant(portletState.getPortalUser());
        		portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByAgencyApplicantAndStatus(portletState.getAgencyApplicant(), ApplicationStatus.APPLICATION_STATUS_APPROVED));
        	}
        	
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_APPLICATIONS_EU);
        	if(portletState.getApplicationListing()==null || (portletState.getApplicationListing()!=null && portletState.getApplicationListing().size()==0))
        	{
        		portletState.addError(aReq, "You do not have any approved EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_APPROVED_APPLICATIONS_NSA.name()))
        {
        	//portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_APPROVED));
        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        			getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(portletState.getPortalUser().getRoleType().getId(), 
							ApplicationStatus.APPLICATION_STATUS_APPROVED, false);
        	portletState.setApplicationWorkFlowListing(appList);
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no approved EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_PENDING_APPLICATIONS_EU.name()))
        {
        	String[] a = {ApplicationStatus.APPLICATION_STATUS_APPROVED.getValue(), 
        				ApplicationStatus.APPLICATION_STATUS_ATTACHMENT_DEVALIDATED.getValue(), 
        				ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED.getValue(), 
        				ApplicationStatus.APPLICATION_STATUS_REJECTED.getValue()};
        	
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().
            			getApplicationsByApplicantAndExceptStatus
            			(portletState.getApplicant(), a));
            	portletState.setAgencyApplicant(null);
        	}
        	else
        	{
        		portletState.setAgencyApplicant(portletState.getPortalUser());
        		portletState.setApplicant(null);
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().
            			getApplicationsByAgencyApplicantAndExceptStatus
            			(portletState.getAgencyApplicant(), a));
        	}
        	
        	
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_EU);
        	
        	if(portletState.getApplicationListing()==null || (portletState.getApplicationListing()!=null && portletState.getApplicationListing().size()==0))
        	{
        		portletState.addError(aReq, "You have not created any EUC request applications yet.", portletState);
        	}else
        	{
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA.name()))
        {
        	//portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_CREATED));
//        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
//					getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
//							portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_CREATED, false, portletState.getPortalUser().getAgency());
        	//log.info("----------------------------" + "44:");
        	Collection<ApplicationWorkflow> appList = null;
        	if(portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_FORWARD_APPLICATION) && 
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
				//log.info("----------------------------" + "45:");
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
        	portletState.setApplicationWorkFlowListing(appList);
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no pending EUC request applications at the moment.", portletState);
        	}
        	
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_REJECTED_APPLICATIONS_EU.name()))
        {
        	
        	
        	
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByApplicantAndStatus(portletState.getApplicant(), ApplicationStatus.APPLICATION_STATUS_REJECTED));
            	portletState.setAgencyApplicant(null);
        	}
        	else
        	{
        		portletState.setApplicant(null);
        		portletState.setAgencyApplicant(portletState.getPortalUser());
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByAgencyApplicantAndStatus(portletState.getAgencyApplicant(), ApplicationStatus.APPLICATION_STATUS_REJECTED));
        	}
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_REJECTED_APPLICATIONS_EU);
        	if(portletState.getApplicationListing()==null || (portletState.getApplicationListing()!=null && portletState.getApplicationListing().size()==0))
        	{
        		portletState.addError(aReq, "You do not have any rejected EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_REJECTED_APPLICATIONS_NSA.name()))
        {
        	//portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED));
        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        			getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(portletState.getPortalUser().getRoleType().getId(), 
							ApplicationStatus.APPLICATION_STATUS_REJECTED, false);
        	portletState.setApplicationWorkFlowListing(appList);

        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_REJECTED_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no rejected EUC request applications yet.", portletState);
        	}
        }
//        if(action.equalsIgnoreCase(VIEW_TABS.LIST_DISPUTED_APPLICATIONS_NSA.name()))
//        {
//        	//portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED));
//        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
//        			getApplicationWorkFlowByReceipientRoleIdAndStatus(portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_DISPUTED);
//        	portletState.setApplicationWorkFlowListing(appList);
//        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
//        	portletState.setCurrentTab(VIEW_TABS.LIST_DISPUTED_APPLICATIONS_NSA);
//        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
//        	{
//        		portletState.addError(aReq, "There are no EUC request currently being disputed applications yet.", portletState);
//        	}
//        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_NSA.name()))
        {
//        	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED));
        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        			getApplicationWorkFlowByReceipientRoleIdAndStatus(portletState.getPortalUser().getRoleType().getId(), 
        					ApplicationStatus.APPLICATION_STATUS_ENDORSED, portletState.getPortalUser().getAgency());
//        			.
//					getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
//							portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_ENDORSED, 
//							false, portletState.getPortalUser().getAgency());
        	portletState.setApplicationWorkFlowListing(appList);
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no endorsed EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_NSA.name()))
        {
        	//portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED));
        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        			getApplicationWorkflowByStatus(ApplicationStatus.APPLICATION_STATUS_DISENDORSED);
//        			getApplicationWorkFlowByReceipientRoleIdAndStatus(portletState.getPortalUser().getRoleType().getId(), 
//        					ApplicationStatus.APPLICATION_STATUS_DISENDORSED, portletState.getPortalUser().getAgency());
//        			getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
//							portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_DISENDORSED, 
//							false, portletState.getPortalUser().getAgency());
        	portletState.setApplicationWorkFlowListing(appList);
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no disendorsed EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_APPROVED_AWAIT_ISSUANCE_APPLICATIONS_EU.name()))
        {
        	String status[] = {ApplicationStatus.APPLICATION_STATUS_REJECTED.getValue(), ApplicationStatus.APPLICATION_STATUS_APPROVED.getValue(), ApplicationStatus.APPLICATION_STATUS_CREATED.getValue(), };
        	
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByApplicantAndExceptStatus(portletState.getApplicant(), status));
            	
            	portletState.setAgencyApplicant(null);
        	}
        	else
        	{
        		portletState.setApplicant(null);
        		portletState.setAgencyApplicant(portletState.getPortalUser());
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByAgencyApplicantAndExceptStatus(portletState.getAgencyApplicant(), status));
            	
        	}
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_AWAIT_ISSUANCE_APPLICATIONS_EU);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "You do not have any EUC requests awaiting issuance at the moment.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_APPROVED_AWAIT_ISSUANCE_APPLICATIONS_NSA.name()))
        {
        	String status[] = {ApplicationStatus.APPLICATION_STATUS_REJECTED.getValue(), ApplicationStatus.APPLICATION_STATUS_APPROVED.getValue(), ApplicationStatus.APPLICATION_STATUS_CREATED.getValue(), };
        	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByExceptStatus(status));
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_AWAIT_ISSUANCE_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no EUC requests awaiting issuance at the moment.", portletState);
        	}
        }
        
        
        
//        if(action.equalsIgnoreCase(VIEW_TABS.LIST_DISPUTED_APPLICATIONS_AG.name()))
//        {
//        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
//        	{
//        		ArrayList<Application> al = null;
//        		Collection<ApplicationWorkflow> apwList = 
//        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
//        				getApplicationWorkFlowBySourceRoleIdAndStatus(portletState.getPortalUser().getRoleType().getId(), ApplicationStatus.APPLICATION_STATUS_DISPUTED);
//        		if(apwList!=null && apwList.size()>0)
//        		{
//        			al = new ArrayList<Application>();
//	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
//	        		{
//	        			ApplicationWorkflow aw = it.next();
//	        			al.add(aw.getApplication());
//	        		}
//	        		portletState.setApplicationWorkFlowListing(apwList);
//	        		portletState.setApplicationListing(al);
//	        		portletState.setCurrentTab(VIEW_TABS.LIST_DISPUTED_APPLICATIONS_AG);
//        		}else
//        		{
//        			portletState.addError(aReq, "There are no applications disputed by your agency", portletState);
//        		}
//        	}
//        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_PENDING_APPLICATIONS_AG.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
        	{
        		ArrayList<Application> al = null;
        		Collection<ApplicationWorkflow> apwList = 
        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        				getApplicationWorkFlowByReceipientRoleIdAndNotWorkedOn(portletState.getPortalUser().getRoleType().getId(), false, portletState.getPortalUser().getAgency());
        		if(apwList!=null && apwList.size()>0)
        		{
        			al = new ArrayList<Application>();
	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
	        		{
	        			ApplicationWorkflow aw = it.next();
	        			al.add(aw.getApplication());
	        		}
	        		portletState.setApplicationWorkFlowListing(apwList);
	        		portletState.setApplicationListing(al);
        		}else
        		{
        			portletState.setApplicationWorkFlowListing(null);
            		portletState.setApplicationListing(null);
        			portletState.addError(aReq, "There are no pending applications available for you to work on", portletState);
        		}
        	}
        	
    		portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_AG);
    		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.CREATE_AN_APPLICATION_AG.name()))
        {
        	portletState.setCurrentTab(VIEW_TABS.CREATE_AN_APPLICATION_AG);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepone.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_MY_AGENCY_APPLICATIONS_AG.name()))
        {
			Collection<ApplicationWorkflow> apwList = null;
			if(!portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER))
			{
				apwList = 
    				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
    				getApplicationWorkFlowByAgency(
    						portletState.getPortalUser().getAgency().getId());
			}else
			{
				portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
				apwList = 
    				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
    				getApplicationWorkFlowByApplicant(
    						portletState.getApplicant().getId());
			}
        	
        	
    		if(apwList!=null && apwList.size()>0)
    		{
    			//log.info("apwListsize ===" + apwList.size());
    			ArrayList<Application> al = new ArrayList<Application>();
        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
        		{
        			ApplicationWorkflow aw = it.next();
        			al.add(aw.getApplication());
        		}
        		portletState.setApplicationWorkFlowListing(apwList);
        		portletState.setApplicationListing(al);
    		}else
    		{
    			portletState.setApplicationWorkFlowListing(null);
        		portletState.setApplicationListing(null);
        		if(!portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER))
    			{
        			portletState.addError(aReq, "There are no applications created by your agency on this system", portletState);
    			}else
    			{
    				portletState.addError(aReq, "You do not have any applications created on this system", portletState);
    			}
    		}
        	portletState.setCurrentTab(VIEW_TABS.LIST_MY_AGENCY_APPLICATIONS_AG);
    		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_AG.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
        	{
        		ArrayList<Application> al = null;
        		Collection<ApplicationWorkflow> apwList = 
        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        				getApplicationWorkFlowBySourceAgencyIdAndStatus(
        						portletState.getPortalUser().getAgency().getId(), 
        						ApplicationStatus.APPLICATION_STATUS_ENDORSED);
        		if(apwList!=null && apwList.size()>0)
        		{
        			al = new ArrayList<Application>();
	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
	        		{
	        			ApplicationWorkflow aw = it.next();
	        			al.add(aw.getApplication());
	        		}
	        		portletState.setApplicationWorkFlowListing(apwList);
	        		portletState.setApplicationListing(al);
        		}else
        		{
        			portletState.setApplicationWorkFlowListing(null);
            		portletState.setApplicationListing(null);
        			portletState.addError(aReq, "There are no applications endorsed by your agency", portletState);
        		}
        	}

    		portletState.setCurrentTab(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_AG);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_AG.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
        	{
        		ArrayList<Application> al = null;
        		Collection<ApplicationWorkflow> apwList = 
        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        				getApplicationWorkFlowBySourceAgencyIdAndStatus(
        						portletState.getPortalUser().getAgency().getId(), ApplicationStatus.APPLICATION_STATUS_DISENDORSED);
        		if(apwList!=null && apwList.size()>0)
        		{
        			al = new ArrayList<Application>();
	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
	        		{
	        			ApplicationWorkflow aw = it.next();
	        			al.add(aw.getApplication());
	        		}
	        		portletState.setApplicationWorkFlowListing(apwList);
	        		portletState.setApplicationListing(al);
        		}else
        		{
        			portletState.setApplicationWorkFlowListing(null);
            		portletState.setApplicationListing(null);
        			portletState.addError(aReq, "There are no applications disendorsed by your agency", portletState);
        		}
        	}

    		portletState.setCurrentTab(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_AG);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_FLAGGED_APPLICATIONS_AG.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
        	{
        		ArrayList<Application> al = null;
        		Collection<ApplicationWorkflow> apwList = 
        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        				getApplicationWorkFlowBySourceAgencyIdAndStatus(portletState.getPortalUser().getAgency().getId(), ApplicationStatus.APPLICATION_STATUS_FLAGGED);
        		if(apwList!=null && apwList.size()>0)
        		{
        			al = new ArrayList<Application>();
	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
	        		{
	        			ApplicationWorkflow aw = it.next();
	        			al.add(aw.getApplication());
	        		}
	        		portletState.setApplicationWorkFlowListing(apwList);
	        		portletState.setApplicationListing(al);
        		}else
        		{
        			portletState.setApplicationWorkFlowListing(null);
            		portletState.setApplicationListing(null);
        			portletState.addError(aReq, "There are no applications flagged by your agency", portletState);
        		}
        	}
    		portletState.setCurrentTab(VIEW_TABS.LIST_FLAGGED_APPLICATIONS_AG);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_DEVALIDATED_APPLICATIONS_AG.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
        	{
        		ArrayList<Application> al = null;
        		Collection<ApplicationWorkflow> apwList = 
        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        				getApplicationWorkFlowBySourceAgencyIdAndStatus(portletState.getPortalUser().getAgency().getId(), ApplicationStatus.APPLICATION_STATUS_ATTACHMENT_DEVALIDATED);
        		if(apwList!=null && apwList.size()>0)
        		{
        			al = new ArrayList<Application>();
	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
	        		{
	        			ApplicationWorkflow aw = it.next();
	        			al.add(aw.getApplication());
	        		}
	        		portletState.setApplicationWorkFlowListing(apwList);
	        		portletState.setApplicationListing(al);
        		}else
        		{
        			portletState.setApplicationWorkFlowListing(null);
            		portletState.setApplicationListing(null);
        			portletState.addError(aReq, "There are no applications devalidated by your agency", portletState);
        		}
        	}
    		portletState.setCurrentTab(VIEW_TABS.LIST_DEVALIDATED_APPLICATIONS_AG);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        
        
	}

	private void approveApplicationWithToken(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String token = aReq.getParameter("token");
		String tokenConfirm = aReq.getParameter("tokenConfirm");
    	int i=0;
		if(token!=null && tokenConfirm!=null && token.trim().length()>0 && token.equals(tokenConfirm))
		{
			Collection<ApplicationWorkflow> awflist = portletState.getApplicationWorkFlowListingForApproval();
			//log.info("a");
			if(awflist!=null && awflist.size()>0)
			{
				//log.info("b");
				for(Iterator<ApplicationWorkflow> afit = awflist.iterator(); afit.hasNext();)
				{
					//log.info("a1");
					ApplicationWorkflow awf = afit.next();
					//log.info(awf.getId());
					Application app = awf.getApplication();
					//log.info(app.getId());
					if(app.getApprovalToken()!=null && app.getApprovalToken().equals(tokenConfirm))
					{
						//log.info("a2");
						awf.setWorkedOn(Boolean.TRUE);
						swpService.updateRecord(awf);
						app.setStatus(ApplicationStatus.APPLICATION_STATUS_APPROVED);
						app.setTransactionId(RandomStringUtils.random(10, false, true));
						String certificateCollectionCode = RandomStringUtils.random(10, true, true);
						app.setCertificateCollectionCode(certificateCollectionCode);
						swpService.updateRecord(app);
						
						
						ApplicationWorkflow apwNew = new ApplicationWorkflow();
						apwNew.setApplication(app);
						apwNew.setDateCreated(new Timestamp((new Date()).getTime()));
						apwNew.setReceipientRole(portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER).getId());
						apwNew.setSourceId(portletState.getPortalUser().getRoleType().getId());
						apwNew.setStatus(ApplicationStatus.APPLICATION_STATUS_APPROVED);
						apwNew.setWorkedOn(Boolean.FALSE);
						apwNew.setSourceAgencyId(portletState.getPortalUser().getAgency().getId());
						Agency ag = portletState.getApplicationManagementPortletUtil().getAgencyByName(AgencyType.ONSA_GROUP);
						apwNew.setAgency(ag);
						
						swpService2.createNewRecord(apwNew);
						
						new Util().pushAuditTrail(swpService2, apwNew.getId().toString(), ECIMSConstants.APPROVE_APPLICATION, 
								portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
						
						
				    	i++;
				    	notifyEndUserApplicationApproved(apwNew, portletState, certificateCollectionCode);
					}
				}
				
				
			}
			
			if(i>0)
			{
				Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
		    			getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(portletState.getPortalUser().getRoleType().getId(), 
								ApplicationStatus.APPLICATION_STATUS_APPROVED, false);
		    	portletState.setApplicationWorkFlowListing(appList);
		    	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_APPLICATIONS_NSA);
		    	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
				portletState.addSuccess(aReq, "Selected Applications have been successfully approved!", portletState);
				
			}else
			{
				portletState.addSuccess(aReq, "Selected Applications were not approved successfully. Please try again!", portletState);
			}
			
			
			
		}else
		{
			portletState.addError(aReq, "Invalid tokens provided. Ensure you provide the approval token for this EUC application request", portletState);
			aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/dotoken.jsp");
		}
    	
	}
	
	
	
	private void setTokenPin(ActionRequest aReq, 
			ActionResponse aRes, SwpService swpService2, 
			ApplicationManagementPortletState portletState)
	{
		String tokenPin = aReq.getParameter("tokenPin");
		String tokenPinConfirm = aReq.getParameter("tokenPinConfirm");
		if(tokenPin!=null && tokenPinConfirm!=null && tokenPin.trim().length()>0 && tokenPin.equals(tokenPinConfirm))
		{
			String config_path = "C://jcodes//dev//appservers//ecims//webapps//EUC-portlet//rsa_api.properties";
			AuthUser test;
			try {
				test = new AuthUser(config_path, tokenPin, "rsachief");
				int result = test.setPin(tokenPin);
				//log.info("Result - " + result);
				String authStatus=null;
				if(result==AuthSession.PIN_ACCEPTED)
				{
					portletState.addSuccess(aReq, "Pin accepted! Please provide the passcode displaying on your token", portletState);
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/dotoken.jsp");
					
				}else if(result==AuthSession.PIN_REJECTED)
				{
					portletState.addSuccess(aReq, "Invalid pin provided! Please provide a valid memorable pin", portletState);
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/dotoken_pin.jsp");
					
				}
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	
	
	
	private void setNextToken(ActionRequest aReq, 
			ActionResponse aRes, SwpService swpService2, 
			ApplicationManagementPortletState portletState)
	{
		String nextToken = aReq.getParameter("nextToken");
		if(nextToken!=null && nextToken.trim().length()>0)
		{
			String config_path = "C://jcodes//dev//appservers//ecims//webapps//EUC-portlet//rsa_api.properties";
			AuthUser test;
			try {
				test = new AuthUser(config_path, nextToken, "rsachief");
				int result = test.setNextCode(nextToken);
				//log.info("Result - " + result);
				int i=0;
				if(result==AuthSession.PIN_ACCEPTED)
				{
					Collection<ApplicationWorkflow> awflist = portletState.getApplicationWorkFlowListingForApproval();
					//log.info("a");
					processTheApprovalNow(aReq, aRes, i, awflist, portletState, swpService2);
					portletState.addSuccess(aReq, "Token Code accepted!", portletState);
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/dotoken.jsp");
					
				}else if(result==AuthSession.PIN_REJECTED)
				{
					portletState.addSuccess(aReq, "Invalid pin provided! Please provide a valid memorable pin", portletState);
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/dotoken_pin.jsp");
					
				}
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	private void approveApplicationWithTokenHardware(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String token = aReq.getParameter("token");
		String tokenConfirm = aReq.getParameter("tokenConfirm");
    	int i=0;
		if(token!=null && tokenConfirm!=null && token.trim().length()>0 && token.equals(tokenConfirm))
		{
			String url="http://localhost:8081/AuthServer/ActiveServlet?action=approveApplicationWithTokenHardware&token=" + token + "&tokenConfirm=" + tokenConfirm;
			//PostMethod post = new PostMethod(url);
			GetMethod post = new GetMethod(url);
			
			NameValuePair nvp1 = new NameValuePair("token", token);
			NameValuePair nvp2 = new NameValuePair("tokenConfirm", tokenConfirm);
			NameValuePair nvp3 = new NameValuePair("action", "approveApplicationWithTokenHardware");
			//post.addParameters(new NameValuePair[]{nvp1, nvp2, nvp3});
			post.setQueryString(new NameValuePair[]{nvp1, nvp2, nvp3});
			HttpClient httpclient = new HttpClient();
			try {
				int result = httpclient.executeMethod(post);
				//log.info("result =" + result);
				InputStream is = post.getResponseBodyAsStream();
				StringWriter sw = new StringWriter();
				IOUtils.copy(is, sw);
				String theString = sw.toString();
				//log.info("String =" + theString);
				if(theString!=null && theString.length()>0)
				{
					try {
						JSONObject js = new JSONObject(theString.trim());
						
						if(js.has("key") && !js.isNull("key"))
						{
							if(((Integer)js.get("key"))==5)
							{
								//Next Code
								aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/dotoken_nextcode.jsp");
							}
							else if((((Integer)js.get("key")))==6)
							{
								//Provide New Pin
								aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/dotoken.jsp");
								portletState.addError(aReq, "Authentication failed. Contact system admin to reset your token device!", portletState);
							}
							else if((((Integer)js.get("key")))==7)
							{
								//bad Authentication
								aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/dotoken.jsp");
								portletState.addError(aReq, "Hardware Token Passcode provided is invalid. Please provide the passcode you see on your hardware token!", portletState);
							}
							else if(((Integer)js.get("key"))==8)
							{
								//Success
								Collection<ApplicationWorkflow> awflist = portletState.getApplicationWorkFlowListingForApproval();
								//log.info("a");
								//if(awflist==null)
								if(awflist!=null && awflist.size()>0)
								{
									//log.info("b");
									for(Iterator<ApplicationWorkflow> afit = awflist.iterator(); afit.hasNext();)
									{
										//log.info("a1");
										ApplicationWorkflow awf = afit.next();
										//log.info(awf.getId());
										Application app = awf.getApplication();
										//log.info(app.getId());
										//if(app.getApprovalToken()!=null && app.getApprovalToken().equals(tokenConfirm))
										//{
											//log.info("a2");
											awf.setWorkedOn(Boolean.TRUE);
											swpService.updateRecord(awf);
											app.setStatus(ApplicationStatus.APPLICATION_STATUS_APPROVED);
											app.setTransactionId(RandomStringUtils.random(10, false, true));
											String certificateCollectionCode = RandomStringUtils.random(10, true, true);
											app.setCertificateCollectionCode(certificateCollectionCode);
											swpService.updateRecord(app);
											
											
											ApplicationWorkflow apwNew = new ApplicationWorkflow();
											apwNew.setApplication(app);
											apwNew.setDateCreated(new Timestamp((new Date()).getTime()));
											apwNew.setReceipientRole(portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER).getId());
											apwNew.setSourceId(portletState.getPortalUser().getRoleType().getId());
											apwNew.setStatus(ApplicationStatus.APPLICATION_STATUS_APPROVED);
											apwNew.setWorkedOn(Boolean.FALSE);
											apwNew.setSourceAgencyId(portletState.getPortalUser().getAgency().getId());
											Agency ag = portletState.getApplicationManagementPortletUtil().getAgencyByName(AgencyType.ONSA_GROUP);
											apwNew.setAgency(ag);
											
											swpService2.createNewRecord(apwNew);
											
											new Util().pushAuditTrail(swpService2, apwNew.getId().toString(), ECIMSConstants.APPROVE_APPLICATION, 
													portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
											
											
									    	i++;
									    	notifyEndUserApplicationApproved(apwNew, portletState, certificateCollectionCode);
										//}
									}
									
									
								}
								
								if(i>0)
								{
									Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
							    			getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(portletState.getPortalUser().getRoleType().getId(), 
													ApplicationStatus.APPLICATION_STATUS_APPROVED, false);
							    	portletState.setApplicationWorkFlowListing(appList);
							    	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_APPLICATIONS_NSA);
							    	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
									portletState.addSuccess(aReq, "Selected Applications have been successfully approved!", portletState);
									
								}else
								{
									portletState.addSuccess(aReq, "Selected Applications were not approved successfully. Please try again!", portletState);
								}
							}
						}else
						{
							aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/dotoken.jsp");
							portletState.addError(aReq, "Errors Experienced carrying out process. Please try again!", portletState);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/dotoken.jsp");
						portletState.addError(aReq, "Errors Experienced carrying out process. Please try again!", portletState);
					}
					post.releaseConnection();
				}
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/dotoken.jsp");
				portletState.addError(aReq, "Errors Experienced carrying out process. Please try again!", portletState);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/dotoken.jsp");
				portletState.addError(aReq, "Errors Experienced carrying out process. Please try again!", portletState);
			}
			
			
			
			
			
		}else
		{
			portletState.addError(aReq, "Invalid tokens provided. Ensure you provide the approval token for this EUC application request", portletState);
			aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/dotoken.jsp");
		}
    	
	}
	


	private void processTheApprovalNow(ActionRequest aReq, ActionResponse aRes,
			int i, Collection<ApplicationWorkflow> awflist, 
			ApplicationManagementPortletState portletState, SwpService swpService2) {
		// TODO Auto-generated method stub
		//log.info("b");
		for(Iterator<ApplicationWorkflow> afit = awflist.iterator(); afit.hasNext();)
		{
			//log.info("a1");
			ApplicationWorkflow awf = afit.next();
			//log.info(awf.getId());
			Application app = awf.getApplication();
			//log.info(app.getId());

				//log.info("a2");
				awf.setWorkedOn(Boolean.TRUE);
				swpService.updateRecord(awf);
				app.setStatus(ApplicationStatus.APPLICATION_STATUS_APPROVED);
				app.setTransactionId(RandomStringUtils.random(10, false, true));
				String certificateCollectionCode = RandomStringUtils.random(10, true, true);
				app.setCertificateCollectionCode(certificateCollectionCode);
				swpService.updateRecord(app);
				
				
				ApplicationWorkflow apwNew = new ApplicationWorkflow();
				apwNew.setApplication(app);
				apwNew.setDateCreated(new Timestamp((new Date()).getTime()));
				apwNew.setReceipientRole(portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER).getId());
				apwNew.setSourceId(portletState.getPortalUser().getRoleType().getId());
				apwNew.setStatus(ApplicationStatus.APPLICATION_STATUS_APPROVED);
				apwNew.setWorkedOn(Boolean.FALSE);
				apwNew.setSourceAgencyId(portletState.getPortalUser().getAgency().getId());
				Agency ag = portletState.getApplicationManagementPortletUtil().getAgencyByName(AgencyType.ONSA_GROUP);
				apwNew.setAgency(ag);
				
				swpService2.createNewRecord(apwNew);
				
				new Util().pushAuditTrail(swpService2, apwNew.getId().toString(), ECIMSConstants.APPROVE_APPLICATION, 
						portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				
				
		    	i++;
		    	notifyEndUserApplicationApproved(apwNew, portletState, certificateCollectionCode);
			
		}
		
		if(i>0)
		{
			Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
	    			getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(portletState.getPortalUser().getRoleType().getId(), 
							ApplicationStatus.APPLICATION_STATUS_APPROVED, false);
	    	portletState.setApplicationWorkFlowListing(appList);
	    	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_APPLICATIONS_NSA);
	    	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
			portletState.addSuccess(aReq, "Selected Applications have been successfully approved!", portletState);
			
		}else
		{
			portletState.addError(aReq, "Selected Applications were not approved successfully. Please try again!", portletState);
		}
	}

	private void handleActionsOnOneApplication(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		
		
		if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
		{
			if(aReq.getParameter("act")=="blacklistapplicant")
			{
				if(aReq.getParameter("reason")!=null && aReq.getParameter("reason").length()>0)
					handleForNSAUserGroup(aReq, aRes, swpService2, portletState);
				else
				{
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
					portletState.addSuccess(aReq, "To blacklist an applicant, ensure you provide reasons for blacklisting that applicant!", portletState);
				}
			}else
			{
				handleForNSAUserGroup(aReq, aRes, swpService2, portletState);
			}
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
			SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String act = aReq.getParameter("act");
		if(act!=null && act.equalsIgnoreCase("gobacktoviewlisting"))
		{
			aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
			portletState.setCurrentTab(VIEW_TABS.LIST_ALL_APPLICATIONS_EU);
		}
	}

	private void handleForAdminUserGroup(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		
	}

	private void handleForAgencyUserGroup(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String act = aReq.getParameter("act");
		//log.info("act=" + act);
		String actId = aReq.getParameter("actId");
		//log.info("actId=" + actId);
		try
		{
			if(act!=null && act.equals("validateattachment"))
			{
				Long attachId = Long.valueOf(actId);
				handleValidateAttachment(aReq, aRes, swpService2, portletState, attachId);
			}else if(act!=null && act.equals("validateattachments"))
			{
				Long attachId = Long.valueOf(actId);
				handleValidateAttachments(aReq, aRes, swpService2, portletState, attachId);
			}else
			{
				ApplicationWorkflow appW = (ApplicationWorkflow)portletState.getApplicationManagementPortletUtil().
					getEntityObjectById(ApplicationWorkflow.class, Long.valueOf(actId));
			
			
				if(appW!=null)
				{
					if(act!=null && act.equals("viewapplication"))
					{
						portletState.setApplicationWorkflow(appW);
						portletState.setApplication(appW.getApplication());
						handleViewApplication(aReq, aRes, swpService2, portletState);
					}
					else if(act!=null && act.equals("viewapplicant"))
					{
						portletState.setApplicationWorkflow(appW);
						portletState.setApplication(appW.getApplication());
						handleViewApplicationApplicant(aReq, aRes, swpService2, portletState);
						
					}
					else if(act!=null && act.equals("backtoapplicationview"))
					{
						aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/viewapplication.jsp");
					}
					else
					{
						boolean xy = false;
						if((appW.getApplication().getPortalUser()!=null && 
								appW.getApplication().getPortalUser().getAgency()!=null && 
								(appW.getApplication().getPortalUser().getAgency().getBlacklist()==null || 
								(appW.getApplication().getPortalUser().getAgency().getBlacklist()!=null && 
								appW.getApplication().getPortalUser().getAgency().getBlacklist().equals(Boolean.FALSE)))) 
								|| (appW.getApplication().getApplicant()!=null && appW.getApplication().getApplicant()!=null && 
								(appW.getApplication().getApplicant().getBlackList()==null || 
								(appW.getApplication().getApplicant().getBlackList()!=null && 
								appW.getApplication().getApplicant().getBlackList().equals(Boolean.FALSE)))))
						{
							portletState.setApplicationWorkflow(appW);
							portletState.setApplication(appW.getApplication());
							xy = true;
						}


						if(xy==true)
						{
							if(act!=null && act.equals("disendorseapplication"))
								handleDisendorseApplication(aReq, aRes, swpService2, portletState);
							if(act!=null && act.equals("disendorseNow"))
								handleDisendorseNow(aReq, aRes, swpService2, portletState);
							if(act!=null && act.equals("canceldisendorsenow"))
								aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/viewapplication.jsp");
							else if(act!=null && act.equals("flagapplication"))
								aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/addcomment.jsp");
							else if(act!=null && act.equals("endorseapplication"))
								handleEndorseApplicationStepOne(aReq, aRes, swpService2, portletState);
							else if(act!=null && act.equals("raiseflag"))
								handleFlagApplicationApplicant(aReq, aRes, swpService2, portletState);
							else if(act!=null && act.equalsIgnoreCase("gobacktoviewlisting"))
							{
								if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
									aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
							}
						}else
						{
							portletState.addError(aReq, "Your action cannot be carried out on the application as this applicant has been blacklisted before", portletState);
						}
					}
					
					
				}else
				{
					portletState.addError(aReq, "Invalid EUC Request application selected", portletState);
				}
			}
		}catch(NumberFormatException e)
		{
			e.printStackTrace();
			if(act!=null && act.equals("validateattachment"))
			{
				portletState.addError(aReq, "Attachment could not be validated. There seems to be some problem. Please try again.", portletState);
			}else
			{
				portletState.addError(aReq, "Invalid EUC Request application selected", portletState);
			}
		}
	}

//	private void handleDisputeApplicationApplicant(ActionRequest aReq,
//			ActionResponse aRes, SwpService swpService2,
//			ApplicationManagementPortletState portletState) {
//		// TODO Auto-generated method stub
//		String comment = aReq.getParameter("disputeComment");
//		ApplicationWorkflow apw = portletState.getApplicationWorkflow();
//		Application app = apw.getApplication();
//		app.setStatus(ApplicationStatus.APPLICATION_STATUS_DISPUTED);
//		swpService2.updateRecord(app);
//		
//		apw.setWorkedOn(Boolean.TRUE);
//		portletState.setApplicationWorkflow(apw);
//		swpService2.updateRecord(apw);
//		
//		ApplicationWorkflow apwNew = new ApplicationWorkflow();
//		apwNew.setApplication(app);
//		apwNew.setComment(comment);
//		apwNew.setDateCreated(new Timestamp((new Date()).getTime()));
//		apwNew.setReceipientRole(portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER).getId());
//		apwNew.setSourceId(portletState.getPortalUser().getRoleType().getId());
//		apwNew.setStatus(ApplicationStatus.APPLICATION_STATUS_DISPUTED);
//		apwNew.setWorkedOn(Boolean.FALSE);
//		swpService2.createNewRecord(apwNew);
//		
//		ApplicationWorkflow awf = (ApplicationWorkflow)portletState.getApplicationManagementPortletUtil().
//				getApplicationWorkFlowBySourceRoleIdAndStatusAndApplication(apw.getSourceId(), 
//				ApplicationStatus.APPLICATION_STATUS_DISPUTED, apw.getApplication());
//		portletState.setCurrentTab(VIEW_TABS.LIST_DISPUTED_APPLICATIONS_AG);
//		portletState.setApplicationWorkflow(awf);
//		portletState.addSuccess(aReq, "Application has been disputed successfully.", portletState);
//		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
//	}

	private void handleDisendorseNow(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String cmnt = aReq.getParameter("cmnt");
		if(cmnt!=null && cmnt.trim().length()>0)
		{
			ApplicationWorkflow apw = portletState.getApplicationWorkflow();
			Application app = apw.getApplication();
			app.setStatus(ApplicationStatus.APPLICATION_STATUS_DISENDORSED);
			swpService2.updateRecord(app);
			
			apw.setWorkedOn(Boolean.TRUE);
			portletState.setApplicationWorkflow(apw);
			swpService2.updateRecord(apw);
			
			ApplicationWorkflow apwNew = new ApplicationWorkflow();
			apwNew.setApplication(app);
			try {
				apwNew.setComment(java.net.URLDecoder.decode(cmnt, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				apwNew.setComment(null);
			}
			apwNew.setDateCreated(new Timestamp((new Date()).getTime()));
			apwNew.setReceipientRole(portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER).getId());
			apwNew.setSourceId(portletState.getPortalUser().getRoleType().getId());
			apwNew.setStatus(ApplicationStatus.APPLICATION_STATUS_DISENDORSED);
			apwNew.setWorkedOn(Boolean.FALSE);
			apwNew.setSourceAgencyId(portletState.getPortalUser().getAgency().getId());
			Agency ag = portletState.getApplicationManagementPortletUtil().getAgencyByName(AgencyType.ONSA_GROUP);
			apwNew.setAgency(ag);
			swpService2.createNewRecord(apwNew);
			
			new Util().pushAuditTrail(swpService2, apwNew.getId().toString(), ECIMSConstants.DISENDORSE_APPLICATION_REQUEST, 
					portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
			
			ApplicationWorkflow awf = (ApplicationWorkflow)portletState.getApplicationManagementPortletUtil().
					getApplicationWorkFlowBySourceAgencyIdAndStatusAndApplication(portletState.getPortalUser().getAgency().getId(), 
					ApplicationStatus.APPLICATION_STATUS_DISENDORSED, apw.getApplication());
			portletState.setCurrentTab(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_AG);
			portletState.setApplicationWorkflow(awf);
			
			portletState.addSuccess(aReq, "Application has been disendorsed successfully.", portletState);
			aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
			
			notifyNSAOnEndorse(portletState, apwNew, false);
		}else
		{
			portletState.addError(aReq, "Application has not been disendorsed successfully. Please provide comments before disendorsing", portletState);
			aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/adddiscomment.jsp");
		}
	}

	private void handleValidateAttachment(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState, Long attachId) {
		// TODO Auto-generated method stub
		ApplicationAttachment aa = (ApplicationAttachment)portletState.getApplicationManagementPortletUtil().
				getEntityObjectById(ApplicationAttachment.class, attachId);
		//aa.setIsValid(Boolean.TRUE);
		
		
		ApplicationAttachmentAgency aag = new ApplicationAttachmentAgency();
		aag.setApplicationAttachment(aa);
		aag.setAgency(portletState.getPortalUser().getAgency());
		
		swpService2.createNewRecord(aag);
		
		new Util().pushAuditTrail(swpService2, aa.getId().toString(), ECIMSConstants.VALIDATE_APPLICATION_ATTACHMENT, 
				portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/viewapplication.jsp");
		portletState.setAttachsValid(true);
		portletState.addSuccess(aReq, "Attachment has been validated successfully", portletState);
	}
	
	
	private void handleValidateAttachments(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState, Long attachId) {
		// TODO Auto-generated method stub
		
		
		String[] validateVer2 = aReq.getParameterValues("validateVer2");
		if(validateVer2!=null && validateVer2.length>0)
		{
			for(int c=0; c<validateVer2.length; c++)
			{
				attachId = Long.valueOf(validateVer2[c]);
				ApplicationAttachment aa = (ApplicationAttachment)portletState.getApplicationManagementPortletUtil().
						getEntityObjectById(ApplicationAttachment.class, attachId);
				//aa.setIsValid(Boolean.TRUE);
				
				
				ApplicationAttachmentAgency aag = new ApplicationAttachmentAgency();
				aag.setApplicationAttachment(aa);
				aag.setAgency(portletState.getPortalUser().getAgency());
				
				swpService2.createNewRecord(aag);
				
				new Util().pushAuditTrail(swpService2, aa.getId().toString(), ECIMSConstants.VALIDATE_APPLICATION_ATTACHMENT, 
						portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/viewapplication.jsp");
				portletState.setAttachsValid(true);
			}
			portletState.addSuccess(aReq, "Attachment has been validated successfully", portletState);
		}else
		{
			portletState.addError(aReq, "No Attachment has been validated. Select attachments before validating them", portletState);
			aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/viewapplication.jsp");
		}
			
	}
	
	private void handleDeValidateAttachment(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState, Long attachId) {
		// TODO Auto-generated method stub
		ApplicationAttachment aa = (ApplicationAttachment)portletState.getApplicationManagementPortletUtil().
				getEntityObjectById(ApplicationAttachment.class, attachId);
		aa.setIsValid(Boolean.FALSE);
		swpService2.updateRecord(aa);
		new Util().pushAuditTrail(swpService2, aa.getId().toString(), ECIMSConstants.DEVALIDATE_APPLICATION_ATTACHMENT, 
				portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
		portletState.setAttachsValid(false);
		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/viewapplication.jsp");
		
		portletState.addSuccess(aReq, "Attachment has been not validated successfully", portletState);
	}

//	private void handleDisputeApplication(ActionRequest aReq,
//			ActionResponse aRes, SwpService swpService2,
//			ApplicationManagementPortletState portletState) {
//		// TODO Auto-generated method stub
//		String comment = aReq.getParameter("disputeComment");
//		ApplicationWorkflow apw = portletState.getApplicationWorkflow();
//		Application app = apw.getApplication();
//		app.setStatus(ApplicationStatus.APPLICATION_STATUS_DISPUTED);
//		swpService2.updateRecord(app);
//		
//		apw.setWorkedOn(Boolean.TRUE);
//		portletState.setApplicationWorkflow(apw);
//		swpService2.updateRecord(apw);
//		
//		ApplicationWorkflow apwNew = new ApplicationWorkflow();
//		apwNew.setApplication(app);
//		apwNew.setComment(comment);
//		apwNew.setDateCreated(new Timestamp((new Date()).getTime()));
//		apwNew.setReceipientRole(portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER).getId());
//		apwNew.setSourceId(portletState.getPortalUser().getRoleType().getId());
//		apwNew.setStatus(ApplicationStatus.APPLICATION_STATUS_DISPUTED);
//		apwNew.setWorkedOn(Boolean.FALSE);
//		swpService2.createNewRecord(apwNew);
//		
//		portletState.addError(aReq, "Your dispute on application has been saved.", portletState);
//		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
//		
//	}

	private void handleEndorseApplicationStepOne(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Agency agency = portletState.getPortalUser().getAgency();
		Collection<EndorsementDesk> edList = portletState.getApplicationManagementPortletUtil().getEndorsementDeskByAgencyAndActive(agency);
		portletState.setEndorsementDeskList(edList);
		if(edList!=null && edList.size()>0)
		{
			aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/endorsedesk.jsp");
		}else
		{
			portletState.addError(aReq, "This agency does not have a desk. Request the system " +
					"administrator to create desks for this agency", portletState);
		}
	}
	
	
	
	private void handleApproveOneApplicationStepOne(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Collection<EndorsedApplicationDesk> eadL = portletState.getApplicationManagementPortletUtil().
				getEndorsedAppDeskByApplication(portletState.getApplicationWorkflow().getApplication());
		portletState.setEndorsedApplicationDeskList(eadL);
		Application app = portletState.getApplicationWorkflow().getApplication();
		app.setApprovalToken(RandomStringUtils.random(6, false, true));
		swpService2.updateRecord(app);
		String[] c = {app.getApplicationNumber()};
		String t = app.getApprovalToken();
		
		ArrayList<ApplicationWorkflow> coll = new ArrayList<ApplicationWorkflow>();
		coll.add(portletState.getApplicationWorkflow());
		portletState.setApplicationWorkFlowListingForApproval(coll);
		
		notifyApplicationApprovalToken(aReq, aRes, portletState, c, t);
	}

	

	private void handleEndorseApplicationStepTwo(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState)
	{
		String comment = aReq.getParameter("cmnt");
		String flagNow = aReq.getParameter("flagNow");
		String flagComment = aReq.getParameter("flagcmnt");
		
		
		

		ApplicationWorkflow apw = portletState.getApplicationWorkflow();
		Application app = apw.getApplication();
		
		
		String[] endorseDeskChkbox = aReq.getParameterValues("endorseDeskChkbox");
		
		if(comment!=null && comment.trim().length()>0)
		{
			if(endorseDeskChkbox!=null && endorseDeskChkbox.length>0)
			{
				for(int c=0; c<endorseDeskChkbox.length; c++)
				{
					try{
						Long eId = Long.valueOf(endorseDeskChkbox[c]);
						EndorsementDesk ed = (EndorsementDesk)portletState.getApplicationManagementPortletUtil().getEntityObjectById(EndorsementDesk.class, eId);
						EndorsedApplicationDesk ead = new EndorsedApplicationDesk();
						ead.setEndorsementDesk(ed);
						ead.setApplication(app);
						ead.setEndorsementDate(new Timestamp((new Date()).getTime()));
						swpService.createNewRecord(ead);
						
						
					}catch(NumberFormatException e)
					{
						
					}
				}
				
				
				if(flagNow!=null && flagNow.equals("1"))
				{
					ApplicationFlag apFlag = new ApplicationFlag();
					apFlag.setApplication(portletState.getApplicationWorkflow().getApplication());
					apFlag.setComment(flagComment==null ? "" : flagComment);
					apFlag.setDateCreated(new Timestamp((new Date()).getTime()));
					apFlag.setPortalUser(portletState.getPortalUser());
					swpService2.createNewRecord(apFlag);
					new Util().pushAuditTrail(swpService2, apFlag.getId().toString(), ECIMSConstants.APPLICATION_FLAGGING, 
							portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
					portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
					
				}
				
				
				
				
				
				if(app.getExceptionType()!=null && app.getExceptionType().equals(Boolean.FALSE))	//FALSE = NOT FAST LANE
				{
					
					ApplicationWorkflow fwaTemp = (ApplicationWorkflow)portletState.getApplicationManagementPortletUtil().getEntityObjectById(ApplicationWorkflow.class, apw.getId());
					
					if(fwaTemp.getWorkedOn()!=null && fwaTemp.getWorkedOn().equals(Boolean.TRUE))
					{
						ApplicationWorkflow awf = (ApplicationWorkflow)portletState.getApplicationManagementPortletUtil().
								getApplicationWorkFlowBySourceAgencyIdAndStatusAndApplication(portletState.getPortalUser().getAgency().getId(), 
								ApplicationStatus.APPLICATION_STATUS_ENDORSED, apw.getApplication());
						portletState.setCurrentTab(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_AG);
						portletState.setApplicationWorkflow(awf);
						portletState.addSuccess(aReq, "Application has already been endorsed successfully by another person in your agency.", portletState);
						
						aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
					}else
					{
					
						apw.setWorkedOn(Boolean.TRUE);
						portletState.setApplicationWorkflow(apw);
						swpService2.updateRecord(apw);
						
						ApplicationWorkflow apwNew = new ApplicationWorkflow();
						apwNew.setApplication(app);
						apwNew.setDateCreated(new Timestamp((new Date()).getTime()));
						apwNew.setReceipientRole(portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER).getId());
						apwNew.setSourceId(portletState.getPortalUser().getRoleType().getId());
						apwNew.setStatus(ApplicationStatus.APPLICATION_STATUS_ENDORSED);
						apwNew.setComment(comment);
						apwNew.setWorkedOn(Boolean.FALSE);
						apwNew.setSourceAgencyId(portletState.getPortalUser().getAgency().getId());
		
						Agency ag = portletState.getApplicationManagementPortletUtil().getAgencyByName(AgencyType.ONSA_GROUP);
						apwNew.setAgency(ag);
						swpService2.createNewRecord(apwNew);
						
						
						app.setStatus(ApplicationStatus.APPLICATION_STATUS_ENDORSED);
						swpService2.updateRecord(app);
						
						
						new Util().pushAuditTrail(swpService2, apwNew.getId().toString(), ECIMSConstants.ENDORSE_APPLICATION_REQUEST, 
								portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
						
					
						
						ApplicationWorkflow awf = (ApplicationWorkflow)portletState.getApplicationManagementPortletUtil().
								getApplicationWorkFlowBySourceAgencyIdAndStatusAndApplication(portletState.getPortalUser().getAgency().getId(), 
								ApplicationStatus.APPLICATION_STATUS_ENDORSED, apw.getApplication());
						portletState.setCurrentTab(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_AG);
						portletState.setApplicationWorkflow(awf);
						portletState.addSuccess(aReq, "Application has been endorsed successfully.", portletState);
						
						//notifyNextInLineToWorkOnApplicationWorkflowItem(portletState, apwNew);
						notifyNSAOnEndorse(portletState, apwNew, true);
						aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
					}
				}
				else if(app.getExceptionType()!=null && app.getExceptionType().equals(Boolean.TRUE))
				{
					app.setStatus(ApplicationStatus.APPLICATION_STATUS_ENDORSED);
					swpService2.updateRecord(app);
					
					apw.setWorkedOn(Boolean.TRUE);
					portletState.setApplicationWorkflow(apw);
					swpService2.updateRecord(apw);
					
					ApplicationWorkflow apwNew = new ApplicationWorkflow();
					apwNew.setApplication(app);
					apwNew.setDateCreated(new Timestamp((new Date()).getTime()));
					apwNew.setReceipientRole(portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER).getId());
					apwNew.setSourceId(portletState.getPortalUser().getRoleType().getId());
					apwNew.setStatus(ApplicationStatus.APPLICATION_STATUS_ENDORSED);
					apwNew.setWorkedOn(Boolean.FALSE);
					apwNew.setComment(comment);
					Agency ag = portletState.getApplicationManagementPortletUtil().getAgencyByName(AgencyType.ONSA_GROUP);
					apwNew.setAgency(ag);
					apwNew.setSourceAgencyId(apw.getAgency()!=null ? apw.getAgency().getId() : null);
					swpService2.createNewRecord(apwNew);
					new Util().pushAuditTrail(swpService2, apwNew.getId().toString(), ECIMSConstants.ENDORSE_APPLICATION_REQUEST, 
							portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
					
				
					
					ApplicationWorkflow awf = (ApplicationWorkflow)portletState.getApplicationManagementPortletUtil().
							getApplicationWorkFlowBySourceAgencyIdAndStatusAndApplication(portletState.getPortalUser().getAgency().getId(), 
							ApplicationStatus.APPLICATION_STATUS_ENDORSED, apw.getApplication());
					portletState.setCurrentTab(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_AG);
					portletState.setApplicationWorkflow(awf);
					portletState.addSuccess(aReq, "Application has been endorsed successfully.", portletState);
					
					notifyNextInLineToWorkOnApplicationWorkflowItem(portletState, apwNew);
					if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
						aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
				}
				
				
			}
		}else
		{

			portletState.addError(aReq, "Application has not been endorsed successfully. Please provide valid comments before you can endorse this application", portletState);
			aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
		}
		
	}
	
	
	private void handleFlagApplicationApplicant(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String comment = aReq.getParameter("comments");
		
		ApplicationFlag apFlag = new ApplicationFlag();
		apFlag.setApplication(portletState.getApplicationWorkflow().getApplication());
		apFlag.setComment(comment);
		apFlag.setDateCreated(new Timestamp((new Date()).getTime()));
		apFlag.setPortalUser(portletState.getPortalUser());
		swpService2.createNewRecord(apFlag);
		new Util().pushAuditTrail(swpService2, apFlag.getId().toString(), ECIMSConstants.APPLICATION_FLAGGING, 
				portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
		
		portletState.addSuccess(aReq, "Application has been flagged successfully.", portletState);
		portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
		//aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/viewapplication.jsp");
	}

	private void handleDisendorseApplication(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String cmnt =aReq.getParameter("cmnt");
		ApplicationWorkflow apwTemp = (ApplicationWorkflow)portletState.getApplicationManagementPortletUtil().
				getEntityObjectById(ApplicationWorkflow.class, portletState.getApplicationWorkflow().getId());
		portletState.setApplicationWorkflow(apwTemp);
		
//		ApplicationWorkflow awf = (ApplicationWorkflow)portletState.getApplicationManagementPortletUtil().
//				getApplicationWorkFlowBySourceAgencyIdAndStatusAndApplication(portletState.getPortalUser().getAgency().getId(), 
//				ApplicationStatus.APPLICATION_STATUS_DISENDORSED, apwTemp.getApplication());
		
		
		
		if(apwTemp!=null && apwTemp.getWorkedOn()!=null && apwTemp.getWorkedOn().equals(Boolean.TRUE))
		{

//			ApplicationWorkflow awf = (ApplicationWorkflow)portletState.getApplicationManagementPortletUtil().
//					getApplicationWorkFlowBySourceAgencyIdAndStatusAndApplication(portletState.getPortalUser().getAgency().getId(), 
//					ApplicationStatus.APPLICATION_STATUS_DISENDORSED, apwTemp.getApplication());
			portletState.setCurrentTab(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_AG);
//			portletState.setApplicationWorkflow(awf);
			
			portletState.addError(aReq, "Application has already been disendorsed by another user in your agency before.", portletState);
			aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
			
		}else
		{
//			if(cmnt!=null && cmnt.trim().length()>0)
//			{
//				ApplicationWorkflow apw = portletState.getApplicationWorkflow();
//				Application app = apw.getApplication();
//				app.setStatus(ApplicationStatus.APPLICATION_STATUS_DISENDORSED);
//				swpService2.updateRecord(app);
//				
//				apw.setWorkedOn(Boolean.TRUE);
//				portletState.setApplicationWorkflow(apw);
//				swpService2.updateRecord(apw);
//				
//				ApplicationWorkflow apwNew = new ApplicationWorkflow();
//				apwNew.setApplication(app);
//				try {
//					apwNew.setComment(java.net.URLDecoder.decode(cmnt, "UTF-8"));
//				} catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					apwNew.setComment(null);
//				}
//				apwNew.setDateCreated(new Timestamp((new Date()).getTime()));
//				apwNew.setReceipientRole(portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER).getId());
//				apwNew.setSourceId(portletState.getPortalUser().getRoleType().getId());
//				apwNew.setStatus(ApplicationStatus.APPLICATION_STATUS_DISENDORSED);
//				apwNew.setWorkedOn(Boolean.FALSE);
//				apwNew.setSourceAgencyId(portletState.getPortalUser().getAgency().getId());
//				Agency ag = portletState.getApplicationManagementPortletUtil().getAgencyByName(AgencyType.ONSA_GROUP);
//				apwNew.setAgency(ag);
//				swpService2.createNewRecord(apwNew);
//				
//				new Util().pushAuditTrail(swpService2, apwNew.getId().toString(), ECIMSConstants.DISENDORSE_APPLICATION_REQUEST, 
//						portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
//				
//				ApplicationWorkflow awf = (ApplicationWorkflow)portletState.getApplicationManagementPortletUtil().
//						getApplicationWorkFlowBySourceAgencyIdAndStatusAndApplication(portletState.getPortalUser().getAgency().getId(), 
//						ApplicationStatus.APPLICATION_STATUS_DISENDORSED, apw.getApplication());
//				portletState.setCurrentTab(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_AG);
//				portletState.setApplicationWorkflow(awf);
//				
//				portletState.addSuccess(aReq, "Application has been disendorsed successfully.", portletState);
//				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
//				
//				notifyNSAOnEndorse(portletState, apwNew, false);
//			}else
//			{
//				portletState.addError(aReq, "Application has not been disendorsed successfully. Please provide comments before disendorsing", portletState);
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/adddiscomment.jsp");
//			}
		}
	}

	private void handleForNSAUserGroup(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		
		String act = aReq.getParameter("act");
		//log.info("act=" + act);
		String actId = aReq.getParameter("actId");
		//log.info("actId=" + actId);
		try
		{
			
			if(act!=null && act.equals("cancelTokenApproval"))
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
			else if(act!=null && act.equals("approveWithToken"))
			{
				String tokenType = aReq.getParameter("tokenType").trim();
				if(tokenType.equals("-1"))
				{
					
				}else if(tokenType.equalsIgnoreCase("Software Token"))
				{
					approveApplicationWithToken(aReq, aRes, swpService2, portletState);
				}else if(tokenType.equalsIgnoreCase("Hardware Token"))
				{
					approveApplicationWithTokenHardware(aReq, aRes, swpService2, portletState);
				}
				
			}
			else if(act!=null && act.equals("approveWithNextToken"))
			{
				//setNextToken(aReq, aRes, swpService2, portletState);
				approveApplicationWithTokenHardware(aReq, aRes, swpService2, portletState);
			}
			else if(act!=null && act.equals("setTokenPin"))
				setTokenPin(aReq, aRes, swpService2, portletState);
			else
			{
				if(actId!=null)
				{
					//log.info("We are in 1");
					ApplicationWorkflow appW = (ApplicationWorkflow)portletState.getApplicationManagementPortletUtil().
							getEntityObjectById(ApplicationWorkflow.class, Long.valueOf(actId));
					
					if(appW!=null)
					{
						//log.info("We are in 2");
						if(act!=null && act.equals("viewapplication"))
						{
							portletState.setApplicationWorkflow(appW);
							portletState.setApplication(appW.getApplication());
							handleViewApplication(aReq, aRes, swpService2, portletState);
						}
						else if(act!=null && act.equals("viewapplicant"))
						{
							portletState.setApplicationWorkflow(appW);
							portletState.setApplication(appW.getApplication());
							handleViewApplicationApplicant(aReq, aRes, swpService2, portletState);
						}
						else if(act!=null && act.equals("backtoapplicationlisting"))
						{
							aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
						}
						else if(act!=null && act.equals("backtoapplicationview"))
						{
							aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/viewapplication.jsp");
						}
						
						else if(act!=null && act.equals("skipbiometric"))
						{
							aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/issuecertificate_stepone.jsp");
						}
						else
						{
							//log.info("We are in 3");
//							//log.info("We are in NULL for blacklist");
//							//log.info("appW.getApplication().getPortalUser() = " + appW.getApplication().getApplicant().getId());
//							//log.info("appW.getApplication().getPortalUser() = " + 
//									(appW.getApplication().getApplicant().getBlackList()==null ? "NULL" : appW.getApplication().getApplicant().getBlackList()));
							
//							if(appW.getApplication().getPortalUser()!=null)
//								//log.info(81);
//							if(appW.getApplication().getPortalUser().getAgency()!=null)
//								//log.info(82);
//							if(appW.getApplication().getPortalUser().getAgency().getBlacklist()==null)
//								//log.info(83);
//							if(appW.getApplication().getPortalUser().getAgency().getBlacklist()!=null)
//								//log.info(84);
//							if(appW.getApplication().getPortalUser().getAgency().getBlacklist().equals(Boolean.FALSE))
//								//log.info(85);
//							if(appW.getApplication().getApplicant()!=null)
//								//log.info(86);
//							if(appW.getApplication().getApplicant().getBlackList()==null)
//								//log.info(87);
//							if(appW.getApplication().getApplicant().getBlackList()!=null)
//								//log.info(88);
//							if(appW.getApplication().getApplicant().getBlackList().equals(Boolean.FALSE))
//								//log.info(89);
							
							
//							if((appW.getApplication().getPortalUser()!=null && 
//									appW.getApplication().getPortalUser().getAgency()!=null && 
//									(appW.getApplication().getPortalUser().getAgency().getBlacklist()==null || 
//									(appW.getApplication().getPortalUser().getAgency().getBlacklist()!=null && 
//									appW.getApplication().getPortalUser().getAgency().getBlacklist().equals(Boolean.FALSE)))) || 
//									(appW.getApplication().getApplicant()!=null && 
//									(appW.getApplication().getApplicant().getBlackList()==null || 
//									(appW.getApplication().getApplicant().getBlackList()!=null && 
//									appW.getApplication().getApplicant().getBlackList().equals(Boolean.FALSE)))))
//							{
//								//log.info("We are in 31");
//							}
							
								
								
							if((appW.getApplication().getPortalUser()!=null && 
									appW.getApplication().getPortalUser().getAgency()!=null && 
									(appW.getApplication().getPortalUser().getAgency().getBlacklist()==null || 
									(appW.getApplication().getPortalUser().getAgency().getBlacklist()!=null && 
									appW.getApplication().getPortalUser().getAgency().getBlacklist().equals(Boolean.FALSE)))) || 
									(appW.getApplication().getApplicant()!=null && 
									(appW.getApplication().getApplicant().getBlackList()==null || 
									(appW.getApplication().getApplicant().getBlackList()!=null && 
									appW.getApplication().getApplicant().getBlackList().equals(Boolean.FALSE)))))
							{
								//log.info("We are in 4");
								portletState.setApplicationWorkflow(appW);
								portletState.setApplication(appW.getApplication());
								if(act!=null && act.equals("forward"))
								{
									if(appW.getApplication().getExceptionType()!=null &&
											portletState.getApplication().getExceptionType().equals(Boolean.TRUE))
									{
										portletState.setAgencyList((Collection<Agency>)portletState.getApplicationManagementPortletUtil().getAllEntityObjects(Agency.class));
										processNormalWay(aReq, aRes, swpService2, portletState);
									}else if(appW.getApplication().getExceptionType()!=null &&
											portletState.getApplication().getExceptionType().equals(Boolean.FALSE))
									{
										
										processAutomateWay(aReq, aRes, swpService2, portletState);
									}else if(appW.getApplication().getExceptionType()==null)
									{
										portletState.setAgencyList((Collection<Agency>)portletState.getApplicationManagementPortletUtil().getAllEntityObjects(Agency.class));
										aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/forwardstepzero.jsp");
									}
								}
		
								else if(act!=null && act.equals("cancelfwdstepzero"))
								{
									aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
								}
								else if(act!=null && act.equals("processapplication"))
								{
									String type = aReq.getParameter("exceptionType");	
									if(type!=null && type.equals("1"))					//manual
									{
										
										processNormalWay(aReq, aRes, swpService2, portletState);
									}
									else if(type!=null && type.equals("0"))					//automate
									{
										Application appl = portletState.getApplicationWorkflow().getApplication();
										ItemCategory ic = appl.getItemCategory();
										processApplicationAutomate(ic, 
												appl.getCurrentWorkflowPosition()==null ? 0 : appl.getCurrentWorkflowPosition(), 
												appW, portletState, aReq, aRes, swpService2);
									
									}
										
								}
								else if(act!=null && act.equals("cancelfwd"))
									handleCancelForwardApplication(aReq, aRes, swpService2, portletState);
								else if(act!=null && act.equals("rejectapplication"))
									aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/reject.jsp");
								else if(act!=null && act.equals("rejectthisapplication"))
									handleRejectApplication(aReq, aRes, swpService2, portletState);
								else if(act!=null && act.equals("addtoexception"))
									handleAddToException(aReq, aRes, swpService2, portletState);
								else if(act!=null && act.equals("removefromexception"))
									handleRemoveFromException(aReq, aRes, swpService2, portletState);
								else if(act!=null && act.equals("blacklistapplicant"))
									handleBlackListApplication(aReq, aRes, swpService2, portletState);
								else if(act!=null && act.equals("unblacklistapplicant"))
									handleUnBlackListApplication(aReq, aRes, swpService2, portletState);
								else if(act!=null && act.equals("approveone"))
									handleApproveOneApplicationStepOne(aReq, aRes, swpService2, portletState);
								//handleApproveOneApplication
								
								else if(act!=null && act.equals("approve"))
									handleApproveApplication(aReq, aRes, swpService2, portletState);
								else if(act!=null && act.equals("batchprocessapprove"))
									handleApproveApplication(aReq, aRes, swpService2, portletState);
								else if(act!=null && act.equals("batchprocessdisaprprove"))
									handleDisApproveApplication(aReq, aRes, swpService2, portletState);
								else if(act!=null && act.equals("rejectone"))
									handleRejectOneApplication(aReq, aRes, swpService2, portletState);
								else if(act!=null && act.equals("reject"))
									handleRejectApplication(aReq, aRes, swpService2, portletState);
								else if(act!=null && act.equals("issuecertificate"))			//step 3 for certificate gen proceess
									issueCertificate(aReq, aRes, swpService2, portletState);
								else if(act!=null && act.equals("entercertificate"))			//step 1 for certificate gen proceess
									aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/issuecertificate_steptwo.jsp");
								else if(act!=null && act.equals("scanbarcode"))			//step 1 for certificate gen proceess
									portletState.addError(aReq, "This feature is currently not installed on the system. Consult your system administrators if you wish to use this feature", portletState);
								else if(act!=null && act.equals("issuecertificateStepTwo"))			//step 2 for certificate gen proceess //issueCertificateStepOne(aReq, aRes, swpService2, portletState);
									issueCertificateNow(aReq, aRes, portletState, swpService2);
//								else if(act!=null && act.equals("issuecertificateStepThree"))			//step 3 for certificate gen proceess //issueCertificateStepOne(aReq, aRes, swpService2, portletState);
//									confirmPrint(aReq, aRes, portletState, swpService2);
//								else if(act!=null && act.equals("cancelissuestepthree"))			//step 3 for certificate gen proceess //issueCertificateStepOne(aReq, aRes, swpService2, portletState);
//								{
//									Certificate cert = portletState.getCertificateGenerated();
//									cert.setCertificateNo(null);
//									cert.setSignature(null);
//									cert.setCertificatePrinted(null);
//									swpService2.updateRecord(cert);
//									
//									new Util().pushAuditTrail(swpService2, cert.getId().toString(), 
//											ECIMSConstants.REJECT_CERTIFICATE, 
//											portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
//									aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/issuecertificate_steptwo.jsp");
//								}
								else if(act!=null && act.equals("cancelissuecertsteptwo"))			//step 3 for certificate gen proceess //issueCertificateStepOne(aReq, aRes, swpService2, portletState);
									aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/issuecertificate_stepone.jsp");
								else if(act!=null && act.equals("addbiometric"))				//step 1 for certificate gen proceess
									handleAddBiometric(aReq, aRes, portletState, swpService2);
								else if(act!=null && act.equals("capturebiometric"))				//step 2 for certificate gen proceess
									captureBiometric(aReq, aRes, portletState, swpService2);
								//handle flow for forward from step one to step x
								else if(act!=null && act.equalsIgnoreCase("fwdtoagencystepone"))
								{
									if(portletState.getPermissionList().contains(PermissionType.PERMISSION_FORWARD_APPLICATION))
									{
										forwardApplicationToAgencyStepOne(aReq, aRes, swpService2, portletState);
									}else
									{
										portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
									}
								}
								else if(act!=null && act.equalsIgnoreCase("cancelfwdstepone"))
									aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
								else if(act!=null && act.equalsIgnoreCase("fwdtoagencysteptwo"))
								{
									if(portletState.getPermissionList().contains(PermissionType.PERMISSION_FORWARD_APPLICATION))
									{
										forwardApplicationToAgencyStepTwo(aReq, aRes, swpService2, portletState);
									}else
									{
										portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
									}
								}
								else if(act!=null && act.equalsIgnoreCase("gobackfwdsteptwo"))
								{
									Collection<Agency> alreadyHandled = portletState.getApplicationManagementPortletUtil().getAgencyAlreadyEndorsed(portletState.getApplication());
									Collection<ApplicationWorkflow> alreadyForwardedTo = portletState.getApplicationManagementPortletUtil().getApplicationWorkFlowsByApplicationAndStatus(portletState.getApplication(), ApplicationStatus.APPLICATION_STATUS_FORWARDED);
									portletState.setAlreadyForwardedTo(alreadyForwardedTo);
									portletState.setAgenciesAlreadyHandled(alreadyHandled);
									aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/forwardstepone.jsp");
								}
								
							}else
							{
								if((appW.getApplication().getPortalUser()!=null && appW.getApplication().getPortalUser().getAgency()!=null && 
									appW.getApplication().getPortalUser().getAgency().getBlacklist()!=null && 
									appW.getApplication().getPortalUser().getAgency().getBlacklist().equals(Boolean.TRUE)) || 
									(appW.getApplication().getApplicant()!=null && 
									appW.getApplication().getApplicant().getBlackList()!=null && 
									appW.getApplication().getApplicant().getBlackList().equals(Boolean.TRUE)))
								{
									if(appW.getApplication().getPortalUser()!=null)
									{
										if(act!=null && act.equals("unblacklistapplicant"))
											handleUnBlackListApplication(aReq, aRes, swpService2, portletState);
										else if(act!=null && act.equals("blacklistapplicant"))
											handleBlackListApplication(aReq, aRes, swpService2, portletState);
										else
											portletState.addError(aReq, "You cannot carry out this action as this user is currently blacklisted. To carry out this action, unblacklist the applicant first", portletState);
										
									}else
									{
										if(appW.getApplication().getApplicant()!=null)
										{
											if(act!=null && act.equals("unblacklistapplicant"))
												handleUnBlackListApplication(aReq, aRes, swpService2, portletState);
											else if(act!=null && act.equals("blacklistapplicant"))
												handleBlackListApplication(aReq, aRes, swpService2, portletState);
											else
												portletState.addError(aReq, "You cannot carry out this action as this user is currently blacklisted. To carry out this action, unblacklist the applicant first", portletState);
											
										}else
										{
											portletState.addError(aReq, "Your action can not be carried out on the application because the applicant is currently on a blacklist", portletState);
										}
									}
								}
								else if((appW.getApplication().getPortalUser()!=null && appW.getApplication().getPortalUser().getAgency()!=null && 
										appW.getApplication().getPortalUser().getAgency().getBlacklist()==null) || 
										(appW.getApplication().getApplicant()!=null && appW.getApplication().getApplicant().getBlackList()==null))
								{
									//log.info("We are in 1757");
									if(act!=null && act.equals("blacklistapplicant"))
										handleBlackListApplication(aReq, aRes, swpService2, portletState);
								}
							}
						}
						
						
						
						
					}else
					{
						portletState.addError(aReq, "Invalid EUC Request application selected", portletState);
					}
				}
			}
		}catch(NumberFormatException e)
		{
			e.printStackTrace();
		}
		
		
	}

	private void processAutomateWay(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		//aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/forwardstepone.jsp");
		//process this applcation. Send to receipient based on current position of application
		ApplicationWorkflow appW = portletState.getApplicationWorkflow();
		int currentPos = 0;
		ItemCategory ic = appW.getApplication().getItemCategory();
		if(appW.getApplication().getCurrentWorkflowPosition()!=null)
		{
			currentPos = appW.getApplication().getCurrentWorkflowPosition();
		}
		processApplicationAutomate(ic, currentPos, 
			appW, portletState, aReq, aRes, swpService2);
		
	}

	private void processNormalWay(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Collection<Agency> alreadyHandled = portletState.getApplicationManagementPortletUtil().getAgencyAlreadyEndorsed(portletState.getApplication());
		Collection<ApplicationWorkflow> alreadyForwardedTo = portletState.getApplicationManagementPortletUtil().getApplicationWorkFlowsByApplicationAndStatus(portletState.getApplication(), ApplicationStatus.APPLICATION_STATUS_FORWARDED);
		portletState.setAlreadyForwardedTo(alreadyForwardedTo);
		portletState.setAgenciesAlreadyHandled(alreadyHandled);
		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/forwardstepone.jsp");
	}

	private void processApplicationAutomate(ItemCategory ic, int i,
			ApplicationWorkflow appW,
			ApplicationManagementPortletState portletState, ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2) {
		// TODO Auto-generated method stub
		WokFlowSetting wfs = (WokFlowSetting)portletState.getApplicationManagementPortletUtil().getWorkflowSettingByItemCategoryOrderByPosition(ic, i);
		Agency agencyToForward = wfs.getAgency();
		if(agencyToForward!=null)
		{
			ApplicationWorkflow appwf = new ApplicationWorkflow();
			appwf.setAgency(agencyToForward);
			appwf.setApplication(appW.getApplication());
			Long recRole = null;
			Long srcRole = portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER).getId();
			if(agencyToForward.getAgencyType().getValue().equals(AgencyType.REGULATOR_GROUP.getValue()))
			{
				recRole = portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_REGULATOR_USER).getId();
			}
			if(agencyToForward.getAgencyType().getValue().equals(AgencyType.ACCREDITOR_GROUP.getValue()))
			{
				recRole = portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_ACCREDITOR_USER).getId();
			}
			if(agencyToForward.getAgencyType().getValue().equals(AgencyType.INFORMATION_GROUP.getValue()))
			{
				recRole = portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_INFORMATION_USER).getId();
			}
			if(agencyToForward.getAgencyType().getValue().equals(AgencyType.ONSA_GROUP.getValue()))
			{
				recRole = portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER).getId();
			}
			appwf.setReceipientRole(recRole);
			appwf.setSourceId(srcRole);
			appwf.setSourceAgencyId(portletState.getPortalUser().getAgency().getId());
			appwf.setStatus(ApplicationStatus.APPLICATION_STATUS_FORWARDED);
			appwf.setWorkedOn(Boolean.FALSE);
			appwf.setDateCreated(new Timestamp((new Date()).getTime()));
			appwf.setSourceAgencyId(portletState.getPortalUser().getAgency().getId());
			swpService2.createNewRecord(appwf);
			Application app = appwf.getApplication();
			app.setExceptionType(Boolean.FALSE);
			app.setStatus(ApplicationStatus.APPLICATION_STATUS_FORWARDED);
			app.setCurrentWorkflowPosition(wfs.getPositionId());
			swpService2.updateRecord(app);
			
			appW.setWorkedOn(Boolean.TRUE);
			swpService2.updateRecord(appW);
			new Util().pushAuditTrail(swpService2, appwf.getId().toString(), 
					ECIMSConstants.FORWARD_APPLICATION_REQUEST, 
					portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
			portletState.addSuccess(aReq, "Application has been forwarded successfully to " + agencyToForward.getAgencyName() + " (" +agencyToForward.getAgencyType().getValue()+ ")", portletState);
			
			Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
					getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
							portletState.getPortalUser().getRoleType().getId(), 
							ApplicationStatus.APPLICATION_STATUS_CREATED, false, portletState.getPortalUser().getAgency());
        	portletState.setApplicationWorkFlowListing(appList);
        	
        	appW.setWorkedOn(Boolean.TRUE);
			swpService2.updateRecord(appW);
			notifyNextInLineToWorkOnApplicationWorkflowItem(portletState, appwf);
			Collection<ApplicationWorkflow> appList1 = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
					getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
							portletState.getPortalUser().getRoleType().getId(), 
							ApplicationStatus.APPLICATION_STATUS_CREATED, false, portletState.getPortalUser().getAgency());
        	portletState.setApplicationWorkFlowListing(appList1);
			//portletState.setApplicationListing((Collection<Application>)portletState.getApplicationManagementPortletUtil().getAllEntityObjects(Application.class));
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
		}
//		WokFlowSetting wfs = (WokFlowSetting)portletState.getApplicationManagementPortletUtil().getWorkflowSettingByItemCategoryOrderByPosition(ic, i);
//		Agency agencyToForward = wfs.getAgency();
//		int currentPos = wfs.getPositionId();
//		if(agencyToForward!=null)
//		{
//			ApplicationWorkflow appwf = new ApplicationWorkflow();
//			appwf.setAgency(agencyToForward);
//			appwf.setApplication(appW.getApplication());
//			Long recRole = null;
//			Long srcRole = null;
//			srcRole = portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER).getId();
//			Application app = appW.getApplication();
//			
//			if(agencyToForward.getAgencyType().getValue().equals(AgencyType.REGULATOR_GROUP.getValue()))
//			{
//				recRole = portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_REGULATOR_USER).getId();
//			}
//			if(agencyToForward.getAgencyType().getValue().equals(AgencyType.ACCREDITOR_GROUP.getValue()))
//			{
//				recRole = portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_ACCREDITOR_USER).getId();
//			}
//			if(agencyToForward.getAgencyType().getValue().equals(AgencyType.INFORMATION_GROUP.getValue()))
//			{
//				recRole = portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_INFORMATION_USER).getId();
//			}
//			if(agencyToForward.getAgencyType().getValue().equals(AgencyType.ONSA_GROUP.getValue()))
//			{
//				recRole = portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER).getId();
////				app.setApprovalToken(RandomStringUtils.random(6, false, true));
//			}
//			
//				
//			appwf.setReceipientRole(recRole);
//			appwf.setSourceId(srcRole);
//			appwf.setSourceAgencyId(portletState.getPortalUser().getAgency().getId());
//			appwf.setStatus(ApplicationStatus.APPLICATION_STATUS_FORWARDED);
//			appwf.setWorkedOn(Boolean.FALSE);
//			appwf.setDateCreated(new Timestamp((new Date()).getTime()));
//			appwf.setSourceAgencyId(portletState.getPortalUser().getAgency().getId());
//			swpService2.createNewRecord(appwf);
//			
//			app.setExceptionType(Boolean.FALSE);
//			app.setStatus(ApplicationStatus.APPLICATION_STATUS_FORWARDED);
//			app.setCurrentWorkflowPosition(currentPos);
//			swpService2.updateRecord(app);
//			new Util().pushAuditTrail(swpService2, appwf.getId().toString(), 
//					ECIMSConstants.FORWARD_APPLICATION_REQUEST, 
//					portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
//			portletState.addSuccess(aReq, "Application has been forwarded successfully to " + agencyToForward.getAgencyName() + " (" +agencyToForward.getAgencyType().getValue()+ ")", portletState);
//			
//			Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
//					getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
//							portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_CREATED, false, portletState.getPortalUser().getAgency());
//        	portletState.setApplicationWorkFlowListing(appList);
//        	
//        	appW.setWorkedOn(Boolean.TRUE);
//			swpService2.updateRecord(appW);
//			notifyNextInLineToWorkOnApplicationWorkflowItem(portletState, appwf);
//        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
//		}else
//		{
//			portletState.addError(aReq, "There are no other receipients configured to receive forwarded applications on the system.", portletState);
//        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
//		}
	}

	private void handleCancelForwardApplication(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		ApplicationWorkflow awf = portletState.getApplicationWorkflow();
		ApplicationWorkflow awf1 = (ApplicationWorkflow)portletState.getApplicationManagementPortletUtil().
				getEntityObjectById(ApplicationWorkflow.class, awf.getId());
		ApplicationWorkflow awf2 = (ApplicationWorkflow)portletState.getApplicationManagementPortletUtil().
				getPreviousApplicationWorkflowOfApplication(awf1.getApplication());
		if(awf1!=null && awf1.getWorkedOn()!=null && awf1.getWorkedOn().equals(Boolean.FALSE))
		{
			awf1.setStatus(ApplicationStatus.APPLICATION_STATUS_DISPUTED);
			awf1.setWorkedOn(Boolean.TRUE);
			swpService2.updateRecord(awf1);
			awf2.setWorkedOn(Boolean.FALSE);
			swpService2.updateRecord(awf2);
			Application app = awf2.getApplication();
			app.setStatus(awf2.getStatus());
			swpService2.updateRecord(app);
			portletState.addSuccess(aReq, "EUC Application forwarded has been canceled. The receipient can no longer work on the EUC application", portletState);
		}else
		{
			portletState.addSuccess(aReq, "EUC Application forwarded can not be canceled. The receipient has already worked on the application", portletState);
		}
		
	}

	private void handleApproveApplicationWithToken(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		
	}

	private void confirmPrint(UploadPortletRequest uploadRequest, ActionResponse aRes, ActionRequest aReq,
			ApplicationManagementPortletState portletState,
			SwpService swpService2, String awfId) {
		// TODO Auto-generated method stub
		Long awfd = Long.valueOf(awfId);
		ApplicationWorkflow aw = (ApplicationWorkflow)(portletState.getApplicationManagementPortletUtil().
				getEntityObjectById(ApplicationWorkflow.class, awfd));
		Certificate cert1 = portletState.getApplicationManagementPortletUtil().
				getCertificateByApplication(aw.getApplication());
		portletState.setCertificateGenerated(cert1);
		
		if(portletState.getPermissionList().contains(PermissionType.PERMISSION_ISSUE_CERTIFICATE))
		{
			
			System.out.println("Size for certificatemagen: "+uploadRequest.getSize("certificatemagen"));
			System.out.println("Size for certificatemagen: "+uploadRequest.getSize("certificatemagen"));
			if (uploadRequest.getSize("certificatemagen")==0) {
				portletState.addError(aReq, "Ensure you select your the scanned copy of your certificate", portletState);
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/issuecertificate_stepthree.jsp");
			}else
			{
				String folder = ECIMSConstants.NEW_APPLICANT_DIRECTORY;
				ImageUpload certificatemagenImageFile = uploadImage(uploadRequest, "certificatemagen", folder);
				if(certificatemagenImageFile!=null && (certificatemagenImageFile.isUploadValid()))
				{
					Certificate cert = portletState.getCertificateGenerated();
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
					Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
			    			getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(portletState.getPortalUser().getRoleType().getId(), 
									ApplicationStatus.APPLICATION_STATUS_APPROVED, false);
			    	portletState.setApplicationWorkFlowListing(appList);
			    	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_APPLICATIONS_NSA);
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
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
	
	
	private ImageUpload uploadImage(UploadPortletRequest uploadRequest, String parameterName, String folderDestination)
	{
		ImageUpload imageUpload =null;
		try
		{
			String sourceFileName = uploadRequest.getFileName(parameterName);
			String[] strArr = sourceFileName.split("\\.");
			File file = uploadRequest.getFile(parameterName);
			if(file.length()/1048576 < 3)
			{
				//log.info("Nome file:" + uploadRequest.getFileName(parameterName));
				File newFile = null;
				String newFileName = RandomStringUtils.random(7, true, true) + "." + (strArr.length>0 ? strArr[strArr.length-1] : "png");
				newFile = new File(folderDestination + newFileName);
				//log.info("New file name: " + newFile.getName());
				//log.info("New file path: " + newFile.getPath());
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
	

	private void issueCertificateStepOne(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String token = aReq.getParameter("token");
		String transactionId = aReq.getParameter("transactionId");
		
		if(token!=null && transactionId!=null)
		{
			Application app = portletState.getApplication();
			if(app.getApprovalToken()!=null && app.getTransactionId()!=null)
			{
				if(app.getApprovalToken().equals(token) && app.getTransactionId().equals(transactionId))
				{
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/issuecertificate_steptwo.jsp");
				}else
				{
					portletState.addError(aReq, "Invalid token and transaction Id combination provided. Provide valid token and transaction Id to proceed", portletState);
				}
			}else
			{
				portletState.addError(aReq, "Invalid EUC application selected. Provide select a valid EUC application to issue its certificate", portletState);
			}
		}else
		{
			portletState.addError(aReq, "Invalid token and transaction Id combination provided. Provide valid token and transaction Id to proceed", portletState);
		}
	}

	private void handleAddBiometric(ActionRequest aReq, ActionResponse aRes,
			ApplicationManagementPortletState portletState,
			SwpService swpService2) {
		// TODO Auto-generated method stub
		if(portletState.getPermissionList().contains(PermissionType.PERMISSION_ISSUE_CERTIFICATE))
		{
			try{
				String selectedApplications=aReq.getParameter("selectedApplications");
				Long id = Long.valueOf(selectedApplications);
				ApplicationWorkflow apw = (ApplicationWorkflow)portletState.
						getApplicationManagementPortletUtil().getEntityObjectById(ApplicationWorkflow.class, id);
				portletState.setApplicationWorkflow(apw);
				PortalUser pu = null;
				if(portletState.getApplicationWorkflow().getApplication().getApplicant()!=null)
				{
					pu = portletState.getApplicationWorkflow().getApplication().getApplicant().getPortalUser();
				}
				else
				{
					pu = portletState.getApplicationWorkflow().getApplication().getPortalUser();
				}
				if(pu!=null && pu.getFingerPrint1()==null && pu.getFingerPrint2()==null)
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/addbiometric.jsp");
				else
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/issuecertificate.jsp");
			
			}catch(NumberFormatException e)
			{
				e.printStackTrace();
				portletState.addError(aReq, "Invalid Application selected. Please select a valid Application to issue a certificate", portletState);
			}
		}else
		{
			portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
		}
		
	}

	private void captureBiometric(ActionRequest aReq,
			ActionResponse aRes, ApplicationManagementPortletState portletState,
			SwpService swpService2) {
		// TODO Auto-generated method stub
		if(portletState.getPermissionList().contains(PermissionType.PERMISSION_ISSUE_CERTIFICATE))
		{
			String fingerPrint1 = RandomStringUtils.random(100, true, true);
			String fingerPrint2 = RandomStringUtils.random(100, true, true);
			if(fingerPrint1!=null && fingerPrint2!=null)
			{
				PortalUser pu = null;
				if(portletState.getApplicationWorkflow().getApplication().getApplicant()!=null)
					pu = portletState.getApplicationWorkflow().getApplication().getApplicant().getPortalUser();
				else
					pu = portletState.getApplicationWorkflow().getApplication().getPortalUser();
				
				pu.setFingerPrint1(fingerPrint1.getBytes());
				pu.setFingerPrint2(fingerPrint2.getBytes());
				swpService.updateRecord(pu);
				new Util().pushAuditTrail(swpService2, pu.getId().toString(), ECIMSConstants.CAPTURE_FINGER_PRINT, 
						portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/issuecertificate.jsp");
			}else
			{
				portletState.addError(aReq, "Biometric data capture failed. Please start again", portletState);
			}
		}else
		{
			portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
		}
	}
	
	
	
	
	private void issueCertificate(ActionRequest aReq, ActionResponse aRes, 
			SwpService swpService2, ApplicationManagementPortletState portletState)
	{
		if(portletState.getPermissionList().contains(PermissionType.PERMISSION_ISSUE_CERTIFICATE))
		{
			ApplicationWorkflow apw = portletState.getApplicationWorkflow();
			Application app = apw.getApplication();
			Certificate cert = portletState.getApplicationManagementPortletUtil().getCertificateByApplication(app);
			portletState.setCertificateGenerated(null);
			
			//log.info("testing 1");
			if(cert!=null  && cert.getCertificatePrinted()!=null && cert.getCertificatePrinted().equals(Boolean.TRUE))
			{
				//log.info("testing 2");
				portletState.addError(aReq, "A certificate for this application has already been issued. Check the list of certificates to find this certificate", portletState);
			}else
			{
				//log.info("testing 3");
				if(cert!=null)
				{
					//log.info("testing 4");
					portletState.setCertificateGenerated(cert);
					portletState.setApplication(app);
					portletState.setApplicationWorkflow(apw);
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/issuecertificate_stepthree.jsp");
				}else
				{
					//Check if user has bio
					//log.info("testing 5");
					SessionFactory sf;
					try {
						sf = HibernateUtils.getSessionFactory();
						Session sess = sf.getCurrentSession();
						
						
						if(app.getApplicant()!=null)
						{
							if(new Util().validateBiometric(app.getApplicant().getPortalUser(), app, apw, sess))
							{
								//issueCertificateNow(aReq, aRes, portletState, swpService2);ACT_ON_APPLICATION
								aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/issuecertificate_stepone.jsp");
							}else
							{
								portletState.addError(aReq, "The applicant of the application - " + app.getApplicationNumber() + " does not have a biometric data captured on the system. <br>" +
										"Please make use of the ECIMS biometric application on your system to capture the user's biometric data before proceeding.", portletState);
								portletState.setApplication(app);
								portletState.setApplicationWorkflow(apw);
								aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/skipbiometric.jsp");
							}
						}else
						{
							if(new Util().validateBiometric(app.getPortalUser(), app, apw, sess))
							{
								//issueCertificateNow(aReq, aRes, portletState, swpService2);ACT_ON_APPLICATION
								aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/issuecertificate_stepone.jsp");
							}else
							{
								portletState.addError(aReq, "The applicant of the application - " + app.getApplicationNumber() + " does not have a biometric data captured on the system. <br>" +
										"Please make use of the ECIMS biometric application on your system to capture the user's biometric data before proceeding.", portletState);
								portletState.setApplication(app);
								portletState.setApplicationWorkflow(apw);
								aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/skipbiometric.jsp");
							}
						}
					} catch (NamingException e) {
						// TODO Auto-generated catch block
						portletState.addError(aReq, "Sorry, we are currently experiencing issues. Please check back later", portletState);
					}
				}
			}
			
			
		}else
		{
			portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
		}
	}

	private void issueCertificateNow(ActionRequest aReq, ActionResponse aRes,
			ApplicationManagementPortletState portletState,
			SwpService swpService2) {
		// TODO Auto-generated method stub
		if(portletState.getPermissionList().contains(PermissionType.PERMISSION_ISSUE_CERTIFICATE))
		{
			Application app = portletState.getApplication();
			Collection<ApplicationItem> appItemList = portletState.getApplicationManagementPortletUtil().getApplicationItemsByApplication(app);
			Double inValue = 0.00;
			if(appItemList!=null)
			{
				
				for(Iterator<ApplicationItem> it = appItemList.iterator(); it.hasNext();)
				{
					inValue += it.next().getAmount();
				}
			}
			String certNo = aReq.getParameter("certificateNumber");
			if(certNo!=null && certNo.trim().length()>0)
			{
				Certificate c = portletState.getApplicationManagementPortletUtil().getCertificateByCertificateNumber(certNo);
				if(c==null)
				{
					Certificate cert = null;
					if(portletState.getCertificateGenerated()==null)
						cert = new Certificate();
					else
						cert = portletState.getCertificateGenerated();
					
					//log.info("We are on line 1");
					cert.setApplication(app);
					//log.info("We are on line 2");
					cert.setApplicationNumber(app.getApplicationNumber());
					//log.info("We are on line 3");
					cert.setAprOrTo(RandomStringUtils.random(6, true, false));
					//log.info("We are on line 4");
					if(app.getApplicant()!=null)
					{
						//log.info("We are on line 5");
						String address = "";
						if(app.getApplicant().getPortalUser().getCompany()!=null)
						{
							//log.info("We are on line 51");
							address=app.getApplicant().getPortalUser().getCompany().getAddress() == null ? "" : 
								app.getApplicant().getPortalUser().getCompany().getAddress();
						}else
						{
							//log.info("We are on line 52");
							address = app.getApplicant().getPortalUser().getAddressLine1() == null ? "" : 
								app.getApplicant().getPortalUser().getAddressLine1();
							address = address + (app.getApplicant().getPortalUser().getAddressLine2() == null ? "" : 
								("." + app.getApplicant().getPortalUser().getAddressLine2()));
						}
						//log.info("We are on line 6");
						cert.setAprOrToAddress(address);
						//log.info("We are on line 7");
						cert.setVendorAddress(address);
						//log.info("We are on line 8");
					}else
					{
//						String address = app.getPortalUser().getAddressLine1() == null ? "" : 
//							app.getPortalUser().getAddressLine1() + " " + 
//							(app.getPortalUser().getAddressLine2()==null ? "" : app.getPortalUser().getAddressLine2());
						String address = app.getImportAddress() == null ? "" : app.getImportAddress();
						//log.info("We are on line 9");
						cert.setAprOrToAddress(address);
						//log.info("We are on line 10");
						cert.setVendorAddress(address);
						//log.info("We are on line 11");
					}
					cert.setByOrganization(app.getImportName());
					//log.info("We are on line 12");
					cert.setCertificateNo(certNo);
					//log.info("We are on line 13");
					cert.setDateCreated(new Timestamp((new Date()).getTime()));
					//log.info("We are on line 14");
					cert.setDate(null);
					//log.info("We are on line 15");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					//log.info("We are on line 16");
					Calendar cal = Calendar.getInstance();
					//log.info("We are on line 17");
					cal.add(Calendar.YEAR, 1);
					//log.info("We are on line 18");
					Date exp = cal.getTime();
					//log.info("We are on line 19");
					cert.setExpireDate(exp);
					//log.info("We are on line 20");
					cert.setGoodsDescription("AS PER ATTACHED");
					//log.info("We are on line 21");
					cert.setInValue(inValue);
					//log.info("We are on line 22");
					cert.setInWords("");
					//log.info("We are on line 23");
					cert.setIssuanceDate(new Date());
					//log.info("We are on line 24");
					cert.setOfficialSeal(null);
					//log.info("We are on line 25");
					cert.setPlace(app.getPortCode()==null ? null : app.getPortCode().getState().getName());
					//log.info("We are on line 26");
					cert.setPurposeOfPurchase(app.getPurposeOfUsage());
					//log.info("We are on line 27");
					cert.setStatus(CertificateStatus.CERTIFICATE_STATUS_ISSUED);
					//log.info("We are on line 28");


					if(app.getApplicant()!=null)
					{
						//log.info("We are on line 29");
						String vendorName = app.getApplicant().getApplicantType().
								equals(ApplicantType.APPLICANT_TYPE_CORPORATE) ? app.getApplicant().getPortalUser().getCompany().getName() : 
									(app.getApplicant().getPortalUser().getFirstName() + " " + app.getApplicant().getPortalUser().getSurname());
						//log.info("We are on line 30");
						cert.setVendorName(vendorName);
						//log.info("We are on line 31");
					}else
					{
						//log.info("We are on line 32");
						//String vendorName = app.getPortalUser().getFirstName() + " " + app.getPortalUser().getSurname();
						String vendorName = app.getPortalUser().getAgency()==null ? "" : app.getPortalUser().getAgency().getAgencyName();
						//log.info("We are on line 33");
						cert.setVendorName(vendorName);
						cert.setVendorName(app.getImportName());
						
						//log.info("We are on line 34");
					}
					//log.info("We are on line 35");
					if(portletState.getCertificateGenerated()==null)
					{
						//log.info("We are on line 3512");
						cert = (Certificate)swpService2.createNewRecord(cert);
					}
					else
					{
						//log.info("We are on line 3523");
						swpService2.updateRecord(cert);
					}
					
					
					//log.info("We are on line 36");
					portletState.setCertificateGenerated(cert);
					//log.info("We are on line 37");
					new Util().pushAuditTrail(swpService2, cert.getId().toString(), ECIMSConstants.ISSUE_CERTIFICATE, 
							portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
					Application app1 = cert.getApplication();
					app1.setStatus(ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED);
					swpService2.updateRecord(app1);
					
					notifyEndUserCertificateIssuance(cert, portletState);
					
					if(appItemList!=null && appItemList.size()==1)
					{
						if(app1.getApplicant()!=null)
						{
							portletState.addSuccess(aReq, "Certificate issued successfully. Follow the following steps to ensure you end this process correctly." +
								"<ol><li>Download and Print this certificate by clicking on this link - " +
								"<a href=\"javascript: handleButtonAction('downloadcertificate', '" + 
								app1.getApplicant().getPortalUser().getId() + "', '" + (cert!=null ? cert.getId() : "") + "')\">Download Certificate[PDF]</a>." +
								"This may take some time depending on the speed of your internet.</li> " +
								"<li>After printing, If printing was successful Click on CHOOSE FILE.</li>" +
								"<li>Select scanned copy of certificate (preferrably PDF).</li>" +
								"<li>Click on YES UPLOAD SCANNED CERTIFICATE button.</li>" +
								"<li>If printing failed, click on NO REPRINT CERTIFICATE.</li></ol>", portletState);
						}
						else if(app1.getPortalUser()!=null)
						{
							portletState.addSuccess(aReq, "Certificate issued successfully. Follow the following steps to ensure you end this process correctly." +
									"<ol><li>Download and Print this certificate by clicking on this link - " +
									"<a href=\"javascript: handleButtonAction('downloadcertificate', '" + 
									app1.getPortalUser().getId() + "', '" + (cert!=null ? cert.getId() : "") + "')\">Download Certificate[PDF]</a>." +
									"This may take some time depending on the speed of your internet.</li> " +
									"<li>After printing, If printing was successful Click on CHOOSE FILE.</li>" +
									"<li>Select scanned copy of certificate (preferrably PDF).</li>" +
									"<li>Click on YES UPLOAD SCANNED CERTIFICATE button.</li>" +
									"<li>If printing failed, click on NO REPRINT CERTIFICATE.</li></ol>", portletState);
						}
					}else
					{
						if(app1.getApplicant()!=null)
						{
							portletState.addSuccess(aReq, "Certificate issued successfully. Follow the following steps to ensure you end this process correctly." +
								"<ol><li>Download and Print this certificate by clicking on this link - " +
								"<a href=\"javascript: handleButtonAction('downloadcertificate', '" + 
								app1.getApplicant().getPortalUser().getId() + "', '" + (cert!=null ? cert.getId() : "") + "')\">Download Certificate[PDF]</a>." +
										"This may take some time depending on the speed of your internet.</li> " +
										"<li>After printing, If printing was successful Click on CHOOSE FILE.</li>" +
										"<li>Select scanned copy of certificate (preferrably PDF).</li>" +
										"<li>Click on YES UPLOAD SCANNED CERTIFICATE button.</li>" +
										"<li>If printing failed, click on NO REPRINT CERTIFICATE.</li></ol>", portletState);
						}
						else if(app1.getPortalUser()!=null)
						{
							portletState.addSuccess(aReq, "Certificate issued successfully. Follow the following steps to ensure you end this process correctly." +
									"<ol><li>Download and Print this certificate by clicking on this link - " +
									"<a href=\"javascript: handleButtonAction('downloadcertificate', '" + 
									app1.getPortalUser().getId() + "', '" + (cert!=null ? cert.getId() : "") + "')\">Download Certificate[PDF]</a>." +
									"This may take some time depending on the speed of your internet.</li> " +
									"<li>After printing, If printing was successful Click on CHOOSE FILE.</li>" +
									"<li>Select scanned copy of certificate (preferrably PDF).</li>" +
									"<li>Click on YES UPLOAD SCANNED CERTIFICATE button.</li>" +
									"<li>If printing failed, click on NO REPRINT CERTIFICATE.</li></ol>", portletState);
						}
					}
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/issuecertificate_stepthree.jsp");
				}else
				{
					portletState.addError(aReq, "A certificate on the system already has this certificate number. Please provide another certificate number.", portletState);
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/issuecertificate_steptwo.jsp");
				}
			}else
			{
				portletState.addError(aReq, "Ensure you provide a valid certificate number before you can proceed.", portletState);
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/issuecertificate_steptwo.jsp");
			}
		}else
		{
			portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
			aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/issuecertificate_steptwo.jsp");
		}
	}

	private void notifyEndUserCertificateIssuance(Certificate cert,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		if(cert.getApplication().getApplicant()!=null)
		{
			emailer.emailEUCIssuanceNotification
				(cert.getApplication().getApplicant().getPortalUser().getEmailAddress(), 
					cert.getApplication().getApplicant().getPortalUser().getFirstName(), 
					cert.getApplication().getApplicant().getPortalUser().getSurname(), 
					"ECIMS End-User Certicate - " + cert.getCertificateNo() + " - Issuance! ", 
				portletState.getApplicationName().getValue(), 
				portletState.getSystemUrl().getValue(), cert.getCertificateNo());
		}else if(cert.getApplication().getPortalUser()!=null)
		{
			emailer.emailEUCIssuanceNotification
			(cert.getApplication().getPortalUser().getEmailAddress(), 
				cert.getApplication().getPortalUser().getFirstName(), 
				cert.getApplication().getPortalUser().getSurname(), 
				"ECIMS End-User Certicate - " + cert.getCertificateNo() + " - Issuance! ", 
			portletState.getApplicationName().getValue(), 
			portletState.getSystemUrl().getValue(), cert.getCertificateNo());
		}
		
		String message = "Your End-User Certificate - " + cert.getCertificateNo() + " - " +
				"has been issued. Thank you for your patience during this process";
		try{
//				message = "Approval request awaiting your action. " +
//						"Visit " + portletState.getSystemUrl().getValue() + " to view requests awaiting your " +
//						"approval/disapproval action";
			if(cert.getApplication().getApplicant()!=null)
			{
				new SendSms(cert.getApplication().getApplicant().getPortalUser().getPhoneNumber(), message, 
						portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
			}else if(cert.getApplication().getPortalUser()!=null)
			{
				new SendSms(cert.getApplication().getPortalUser().getPhoneNumber(), message, 
						portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
			}
				
		}catch(Exception e){
			log.error("error sending sms ",e);
		}
	}

	private void handleApproveApplication(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		ArrayList<ApplicationWorkflow> awfList = null;
		if(portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION))
		{
			String[] appList = aReq.getParameterValues("selectAllCheckbox");
			Application app = null;
			if(appList!=null && appList.length>0)
			{
				awfList = new ArrayList<ApplicationWorkflow>();
				String rnd = RandomStringUtils.random(6, true, true);
				String[] plio = new String[appList.length];
				
				for(int c=0; c<appList.length; c++)
				{
					ApplicationWorkflow apwf = (ApplicationWorkflow)portletState.
							getApplicationManagementPortletUtil().getEntityObjectById(ApplicationWorkflow.class, 
							Long.valueOf(appList[c]));
					awfList.add(apwf);
					Application app1 = apwf.getApplication();
					app1.setApprovalToken(rnd);
					swpService.updateRecord(app1);
					plio[c] = apwf.getApplication().getApplicationNumber();
				}
				
				notifyApplicationApprovalToken(aReq, aRes, portletState, plio, rnd);
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/dotoken.jsp");
			}else
			{
				portletState.addError(aReq, "Approval failed as no applications were selected. Select at least one application before carrying out this action", portletState);
			}
		}else
		{
			portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
		}
		portletState.setApplicationWorkFlowListingForApproval(awfList);
		
	}
	
	
	
	private void handleDisApproveApplication(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		ArrayList<ApplicationWorkflow> awfList = null;
		int j = 0;
		if(portletState.getPermissionList().contains(PermissionType.PERMISSION_REJECT_APPLICATION))
		{
			String[] appList = aReq.getParameterValues("selectAllCheckbox");
			Application app = null;
			if(appList!=null && appList.length>0)
			{
				for(int i=0; i<appList.length; i++)
				{
					ApplicationWorkflow appwf = portletState.getApplicationWorkflow();
					appwf.setWorkedOn(Boolean.TRUE);
					swpService2.updateRecord(appwf);
					
					app = appwf.getApplication();
					app.setStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED);
					swpService2.updateRecord(app);
					
					ApplicationWorkflow apwNew = new ApplicationWorkflow();
					apwNew.setApplication(portletState.getApplication());
					apwNew.setDateCreated(new Timestamp((new Date()).getTime()));
					apwNew.setReceipientRole(portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER).getId());
					apwNew.setSourceId(portletState.getPortalUser().getRoleType().getId());
					apwNew.setStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED);
					apwNew.setWorkedOn(Boolean.FALSE);
					Agency ag = portletState.getApplicationManagementPortletUtil().getAgencyByName(AgencyType.ONSA_GROUP);
					apwNew.setAgency(ag);
					apwNew.setSourceAgencyId(portletState.getPortalUser().getAgency().getId());
					swpService2.createNewRecord(apwNew);
					notifyEndUserApplicationRejected(apwNew, portletState);
					portletState.addSuccess(aReq, "EUC request disapproved successfully", portletState);
					new Util().pushAuditTrail(swpService2, apwNew.getId().toString(), ECIMSConstants.DISAPPROVE_APPLICATION, 
							portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
					j = 1;
					
				}	
				
				if(j==0)
				{
					portletState.addError(aReq, "EUC application approval request disapproved successfully", portletState);
				}
				Collection<ApplicationWorkflow> appWList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
	        			getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(portletState.getPortalUser().getRoleType().getId(), 
								ApplicationStatus.APPLICATION_STATUS_APPROVED, false);
	        	portletState.setApplicationWorkFlowListing(appWList);
	        	ArrayList<ApplicationWorkflow> ar = new ArrayList();
	        	for(Iterator<ApplicationWorkflow> it1 = appWList.iterator(); it1.hasNext();)
	        	{
	        		ApplicationWorkflow at = it1.next();
	        		ar.add(at);
	        	}
	        	portletState.setApplicationWorkFlowListingForApproval(ar);
	        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
	        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
	        	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_APPLICATIONS_NSA);
			}else
			{
				portletState.addError(aReq, "Approval failed as no applications were selected. Select at least one application before carrying out this action", portletState);
			}
		}else
		{
			portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
		}
		
		
	}
	
	
	
	private void handleApproveOneApplication(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		ApplicationWorkflow apwf = portletState.getApplicationWorkflow();
		apwf.setWorkedOn(Boolean.TRUE);
		swpService2.updateRecord(apwf);
						
		ApplicationWorkflow apwNew = new ApplicationWorkflow();
		apwNew.setApplication(portletState.getApplication());
		apwNew.setDateCreated(new Timestamp((new Date()).getTime()));
		apwNew.setReceipientRole(portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER).getId());
		apwNew.setSourceId(portletState.getPortalUser().getRoleType().getId());
		apwNew.setStatus(ApplicationStatus.APPLICATION_STATUS_APPROVED);
		apwNew.setWorkedOn(Boolean.FALSE);
		Agency ag = portletState.getApplicationManagementPortletUtil().getAgencyByName(AgencyType.ONSA_GROUP);
		apwNew.setAgency(ag);
		apwNew.setSourceAgencyId(portletState.getPortalUser().getAgency().getId());
		swpService2.createNewRecord(apwNew);
		new Util().pushAuditTrail(swpService2, apwNew.getId().toString(), ECIMSConstants.APPROVE_APPLICATION, 
				portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
		
		
		Application app = apwNew.getApplication();
		app.setStatus(ApplicationStatus.APPLICATION_STATUS_APPROVED);
		app.setTransactionId(RandomStringUtils.random(10, false, true));
		String certificateCollectionCode = RandomStringUtils.random(10, true, true);
		app.setCertificateCollectionCode(certificateCollectionCode);
		swpService2.updateRecord(app);
		
		ApplicationWorkflow awf = (ApplicationWorkflow)portletState.getApplicationManagementPortletUtil().
				getApplicationWorkFlowBySourceRoleIdAndStatusAndApplication(apwf.getSourceId(),  
				ApplicationStatus.APPLICATION_STATUS_APPROVED, apwf.getApplication());
		portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_APPLICATIONS_NSA);
		portletState.setApplicationWorkflow(awf);
		portletState.addSuccess(aReq, "Application approved successfully", portletState);
		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");

		
	}

	private void handleBlackListApplication(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Application application = portletState.getApplicationWorkflow().getApplication();
		if(application.getApplicant()!=null)
		{
			Applicant applicant = application.getApplicant();
			applicant.setBlackList(Boolean.TRUE);
			if(applicant.getExceptionList()==null)
			{
				//applicant.setExceptionList(Boolean.FALSE);
			}
			swpService2.updateRecord(applicant);
			
			new Util().pushAuditTrail(swpService2, "Applicant No:" + applicant.getApplicantNumber().toString() + ". Reason for blacklist: " + aReq.getParameter("reason"), ECIMSConstants.BLACKLIST_APPLICANT, 
					portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
			
			Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
					portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
							Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
					portletState.getSendingEmailUsername().getValue());
			
			
			emailer.emailApplicantBlacklisted
				(applicant.getPortalUser().getEmailAddress(), 
						applicant.getPortalUser().getFirstName(), 
						applicant.getPortalUser().getSurname(), 
					"ECIMS - You " + applicant.getApplicantNumber() + " have been blacklisted!", 
				portletState.getApplicationName().getValue(), 
				portletState.getSystemUrl().getValue(), aReq.getParameter("reason"));
			
			
			portletState.addSuccess(aReq, "Applicant has been added to black list successfully", portletState);
		}else 
		{
			if(application.getPortalUser()!=null)
			{
				Agency ag = application.getPortalUser().getAgency();
				ag.setBlacklist(Boolean.TRUE);
				swpService2.updateRecord(ag);
				new Util().pushAuditTrail(swpService2, "Agency: " + ag.getAgencyName() + " ["+ag.getAgencyType().getValue() +"]".toString() + ". Reason for blacklist: " + aReq.getParameter("reason"), ECIMSConstants.BLACKLIST_AGENCY, 
						portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
			}
			portletState.addSuccess(aReq, "Applicants' agency has been added to black list successfully", portletState);
		}
		
		
	}
	
	private void handleUnBlackListApplication(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Application application = portletState.getApplicationWorkflow().getApplication();
		Applicant applicant = application.getApplicant();
		
		if(applicant!=null)
		{
			applicant.setBlackList(Boolean.FALSE);
			if(applicant.getExceptionList()==null)
			{
				//applicant.setExceptionList(Boolean.FALSE);
			}
			swpService2.updateRecord(applicant);
			
			new Util().pushAuditTrail(swpService2, "Applicant Number: " + applicant.getApplicantNumber() + ". Reason for Unblacklist: " + aReq.getParameter("reason"), ECIMSConstants.UNBLACKLIST_APPLICANT, 
					portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
			
			portletState.addSuccess(aReq, "Applicant has been removed from black list successfully", portletState);
		}else 
		{
			Agency ag = application.getPortalUser().getAgency();
			if(ag!=null)
			{
				ag.setBlacklist(Boolean.FALSE);
				swpService2.updateRecord(ag);
				new Util().pushAuditTrail(swpService2, "Agency: " + ag.getAgencyName() + " ["+ag.getAgencyType().getValue()+"]. Reason for Unblacklist: " + aReq.getParameter("reason"), ECIMSConstants.UNBLACKLIST_AGENCY, 
						portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				portletState.addSuccess(aReq, "Agency has been removed from black list successfully", portletState);
			}
		}
		
	}

	private void handleAddToException(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Application application = portletState.getApplicationWorkflow().getApplication();
		application.setExceptionType(Boolean.TRUE);
		swpService2.updateRecord(application);
		portletState.addSuccess(aReq, "Application has been added to exception list successfully", portletState);
	}
	
	private void handleRemoveFromException(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Application application = portletState.getApplicationWorkflow().getApplication();
		application.setExceptionType(Boolean.TRUE);
		swpService2.updateRecord(application);
		portletState.addSuccess(aReq, "Application has been removed from the exception list successfully", portletState);
	}

	private void handleRejectOneApplication(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		if(portletState.getPermissionList().contains(PermissionType.PERMISSION_REJECT_APPLICATION))
		{
			ApplicationWorkflow appwf = portletState.getApplicationWorkflow();
			appwf.setWorkedOn(Boolean.TRUE);
			swpService2.updateRecord(appwf);
			
			Application app = appwf.getApplication();
			app.setStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED);
			swpService2.updateRecord(app);
			
			ApplicationWorkflow apwNew = new ApplicationWorkflow();
			apwNew.setApplication(portletState.getApplication());
			apwNew.setDateCreated(new Timestamp((new Date()).getTime()));
			apwNew.setReceipientRole(portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER).getId());
			apwNew.setSourceId(portletState.getPortalUser().getRoleType().getId());
			apwNew.setStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED);
			apwNew.setWorkedOn(Boolean.TRUE);		//No One Can Work on This application again
			Agency ag = portletState.getApplicationManagementPortletUtil().getAgencyByName(AgencyType.ONSA_GROUP);
			apwNew.setAgency(ag);
			apwNew.setSourceAgencyId(portletState.getPortalUser().getAgency().getId());
			swpService2.createNewRecord(apwNew);
			new Util().pushAuditTrail(swpService2, apwNew.getId().toString(), ECIMSConstants.DISAPPROVE_APPLICATION, 
					portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
		}
		else
		{
			portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
		}
		
	}
	
	private void handleRejectApplication(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		if(portletState.getPermissionList().contains(PermissionType.PERMISSION_REJECT_APPLICATION))
		{
			String comment = aReq.getParameter("rejectcomment");
			//log.info("Comment=" + comment);
			if(comment!=null && comment.trim().length()>0)
			{
				ApplicationWorkflow appwf = portletState.getApplicationWorkflow();
				appwf.setWorkedOn(Boolean.TRUE);
				swpService2.updateRecord(appwf);
				
				Application app = appwf.getApplication();
				app.setStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED);
				swpService2.updateRecord(app);
				
				ApplicationWorkflow apwNew = new ApplicationWorkflow();
				apwNew.setApplication(portletState.getApplication());
				apwNew.setDateCreated(new Timestamp((new Date()).getTime()));
				apwNew.setReceipientRole(portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER).getId());
				apwNew.setSourceId(portletState.getPortalUser().getRoleType().getId());
				apwNew.setStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED);
				apwNew.setWorkedOn(Boolean.FALSE);
				apwNew.setComment(comment);
				Agency ag = portletState.getApplicationManagementPortletUtil().getAgencyByName(AgencyType.ONSA_GROUP);
				apwNew.setAgency(ag);
				apwNew.setSourceAgencyId(portletState.getPortalUser().getAgency().getId());
				swpService2.createNewRecord(apwNew);
				notifyEndUserApplicationRejected(apwNew, portletState);
				portletState.addSuccess(aReq, "EUC request disapproved successfully", portletState);
				new Util().pushAuditTrail(swpService2, apwNew.getId().toString(), ECIMSConstants.DISAPPROVE_APPLICATION, 
						portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				
				Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
						getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
								portletState.getPortalUser().getRoleType().getId(), 
								ApplicationStatus.APPLICATION_STATUS_FORWARDED, false, 
								portletState.getPortalUser().getAgency());
				portletState.setApplicationWorkFlowListing(appList);
				
			}else
			{
				portletState.addError(aReq, "Please provide a note which will be sent to the EUC applicant giving reasons for disapproval", portletState);
			}
		}
		else
		{
			portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
		}
		
	}

	private void notifyEndUserApplicationRejected(ApplicationWorkflow apwNew,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		if(apwNew.getApplication().getApplicant()!=null)
		{
			emailer.emailAppRequestStatusDisapproved
				(apwNew.getApplication().getApplicant().getPortalUser().getEmailAddress(), 
					apwNew.getApplication().getApplicant().getPortalUser().getFirstName(), 
					apwNew.getApplication().getApplicant().getPortalUser().getSurname(), 
					"ECIMS Application Request - " + apwNew.getApplication().getApplicationNumber() + " - Disapproved", 
				portletState.getApplicationName().getValue(), 
				portletState.getSystemUrl().getValue(), apwNew);
		}else if(apwNew.getApplication().getPortalUser()!=null)
		{
			emailer.emailAppRequestStatusDisapproved
			(apwNew.getApplication().getPortalUser().getEmailAddress(), 
				apwNew.getApplication().getPortalUser().getFirstName(), 
				apwNew.getApplication().getPortalUser().getSurname(), 
				"ECIMS Application Request - " + apwNew.getApplication().getApplicationNumber() + " - Disapproved", 
			portletState.getApplicationName().getValue(), 
			portletState.getSystemUrl().getValue(), apwNew);
		}
		
		String message = "Your ECIMS Application Request - " + apwNew.getApplication().getApplicationNumber() + " - has been disapproved. For further details and reasons, please check your email";
		try{
//				message = "Approval request awaiting your action. " +
//						"Visit " + portletState.getSystemUrl().getValue() + " to view requests awaiting your " +
//						"approval/disapproval action";
			if(apwNew.getApplication().getApplicant()!=null)
			{
				new SendSms(apwNew.getApplication().getApplicant().getPortalUser().getPhoneNumber(), message, 
						portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
			}else if(apwNew.getApplication().getPortalUser()!=null)
			{
				new SendSms(apwNew.getApplication().getPortalUser().getPhoneNumber(), message, 
						portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
			}
				
		}catch(Exception e){
			log.error("error sending sms ",e);
		}
	}
	
	
	private void notifyEndUserApplicationApproved(ApplicationWorkflow apwNew,
			ApplicationManagementPortletState portletState, String collectionCode) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		if(apwNew.getApplication().getApplicant()!=null)
		{
			emailer.emailAppRequestStatusApproved
			(apwNew.getApplication().getApplicant().getPortalUser().getEmailAddress(), 
					apwNew.getApplication().getApplicant().getPortalUser().getFirstName(), 
					apwNew.getApplication().getApplicant().getPortalUser().getSurname(), 
					"ECIMS Application Request - " + apwNew.getApplication().getApplicationNumber() + " - Approved. ", 
				portletState.getApplicationName().getValue(), 
				portletState.getSystemUrl().getValue(), apwNew, 
				collectionCode);
		
			String message = "Your ECIMS Application Request - " + apwNew.getApplication().getApplicationNumber() + " - " +
					"has been approved. \nYour Collection Code: " + collectionCode;
			try{
	//				message = "Approval request awaiting your action. " +
	//						"Visit " + portletState.getSystemUrl().getValue() + " to view requests awaiting your " +
	//						"approval/disapproval action";
					new SendSms(apwNew.getApplication().getApplicant().getPortalUser().getPhoneNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
					
			}catch(Exception e){
				log.error("error sending sms ",e);
			}
		}else if(apwNew.getApplication().getPortalUser()!=null)
		{
			emailer.emailAppRequestStatusApproved
			(apwNew.getApplication().getPortalUser().getEmailAddress(), 
					apwNew.getApplication().getPortalUser().getFirstName(), 
					apwNew.getApplication().getPortalUser().getSurname(), 
					"ECIMS Application Request - " + apwNew.getApplication().getApplicationNumber() + " - Approved. ", 
				portletState.getApplicationName().getValue(), 
				portletState.getSystemUrl().getValue(), apwNew, 
				collectionCode);
		
			String message = "Your ECIMS Application Request - " + apwNew.getApplication().getApplicationNumber() + " - " +
					"has been approved. \nYour Collection Code: " + collectionCode;
			try{
	//				message = "Approval request awaiting your action. " +
	//						"Visit " + portletState.getSystemUrl().getValue() + " to view requests awaiting your " +
	//						"approval/disapproval action";
					new SendSms(apwNew.getApplication().getPortalUser().getPhoneNumber(), message, 
							portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
					
			}catch(Exception e){
				log.error("error sending sms ",e);
			}
		}
		
	}
	
	
	
	
	private void handleNewForward(ActionRequest aReq, ActionResponse aRes, 
			SwpService swpService2,
			ApplicationManagementPortletState portletState)
	{
		
	}
	
	
	
	
	private ApplicationWorkflow workOnAnAnApplicationFxn(ApplicationWorkflow awf, ApplicationStatus status, SwpService swpService,
			ApplicationManagementPortletState portletState, PortalUser pu, RoleType recRole, Agency recAg, String comment)
	{
		Application application = awf.getApplication();
		ApplicationWorkflow applWF = new ApplicationWorkflow();
		applWF.setAgency(recAg);
		applWF.setApplication(application);
		applWF.setCertificate(null);
		applWF.setComment(comment);
		applWF.setDateCreated(new Timestamp((new Date()).getTime()));
		applWF.setReceipientRole(recRole.getId());
		applWF.setSourceAgencyId(pu.getAgency().getId());
		applWF.setSourceId(pu.getRoleType().getId());
		applWF.setStatus(status);
		applWF.setWorkedOn(Boolean.FALSE);
		applWF.setSourceAgencyId(portletState.getPortalUser().getAgency().getId());
		swpService.updateRecord(applWF);
		return applWF;
	}
	

	private void handleForwardApplication(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		if(portletState.getPermissionList().contains(PermissionType.PERMISSION_FORWARD_APPLICATION))
		{
			portletState.setAgencyList((Collection<Agency>)portletState.getApplicationManagementPortletUtil().getAllEntityObjects(Agency.class));
//			{
//	        	forwardApplicationToAgency(aReq, aRes, swpService2, portletState);
//			}
//			else if(act!=null && act.equalsIgnoreCase("fwdtoagencystepone"))
//			{
//	        	forwardApplicationToAgencyStepOne(aReq, aRes, swpService2, portletState);
//			}else if(act!=null && act.equalsIgnoreCase("cancelfwdstepone"))
//			{
//	        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
//			}else if(act!=null && act.equalsIgnoreCase("fwdtoagencysteptwo"))
//			{
//	        	forwardApplicationToAgencyStepTwo(aReq, aRes, swpService2, portletState);
//			}else if(act!=null && act.equalsIgnoreCase("gobackfwdsteptwo"))
//			{
//	        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/forwardstepone.jsp");
//			}
			if(portletState.getApplication()!=null && portletState.getApplication().getExceptionType()!=null 
					&& portletState.getApplication().getExceptionType().equals(Boolean.TRUE))
			{
				//log.info("Forward to step one");
				Collection<Agency> alreadyHandled = portletState.getApplicationManagementPortletUtil().getAgencyAlreadyEndorsed(portletState.getApplication());
				Collection<ApplicationWorkflow> alreadyForwardedTo = portletState.getApplicationManagementPortletUtil().getApplicationWorkFlowsByApplicationAndStatus(portletState.getApplication(), ApplicationStatus.APPLICATION_STATUS_FORWARDED);
				portletState.setAlreadyForwardedTo(alreadyForwardedTo);
				portletState.setAgenciesAlreadyHandled(alreadyHandled);
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/forwardstepone.jsp");
			}else if(portletState.getApplication()!=null && portletState.getApplication().getExceptionType()!=null 
					&& portletState.getApplication().getExceptionType().equals(Boolean.FALSE))
			{
				//log.info("Exception type is false");
				ApplicationWorkflow awf = portletState.getApplicationManagementPortletUtil()
						.getApplicationWorkFlowByReceipientIdAndStatusAndWorkedOn(portletState.getPortalUser().getRoleType().getId(), 
								ApplicationStatus.APPLICATION_STATUS_CREATED, portletState.getApplication(), Boolean.FALSE);
				if(awf!=null)
				{
					
					Collection<ApplicationItem> ai = portletState.getApplicationManagementPortletUtil().
							getApplicationItemsByApplication(portletState.getApplication());
					if(ai!=null && ai.size()>0)
					{
						Agency ag = awf.getAgency();
						ApplicationItem appItem = ai.iterator().next();
						WokFlowSetting p = portletState.getApplicationManagementPortletUtil().
								getWorkFlowSettingByAgencyApplicationCategory(ag, appItem.getItemCategorySub().getItemCategory());
						processApplicationAutomate(appItem.getItemCategorySub().getItemCategory(), 0, 
								awf, portletState, aReq, aRes, swpService2);
					}
				}else
				{
					awf = portletState.getApplicationManagementPortletUtil()
							.getApplicationWorkFlowByReceipientIdAndNotStatusAndWorkedOn(portletState.getPortalUser().getRoleType().getId(), 
									ApplicationStatus.APPLICATION_STATUS_CREATED, portletState.getApplication(), Boolean.FALSE);
					if(awf!=null)
					{
						ApplicationWorkflow a_w = portletState.getApplicationManagementPortletUtil().
								getPreviousApplicationWorkflowOfApplicationByAW(awf);
						if(a_w!=null)
						{
							Agency ag = a_w.getAgency();
							Collection<ApplicationItem> ai = portletState.getApplicationManagementPortletUtil().
									getApplicationItemsByApplication(portletState.getApplication());
							ApplicationItem appItem = ai.iterator().next();
							WokFlowSetting p = portletState.getApplicationManagementPortletUtil().
									getWorkFlowSettingByAgencyApplicationCategory(ag, appItem.getItemCategorySub().getItemCategory());
							processApplicationAutomate(appItem.getItemCategorySub().getItemCategory(), p.getPositionId(), 
									awf, portletState, aReq, aRes, swpService2);
						}else
						{
							portletState.addError(aReq, "There seems to be a broken link in the process flow. Please inform system admin of this " +
									"issue or make use of the Application-Exception Processing", portletState);
						}
						
					}
				}
				
			}else if(portletState.getApplication()!=null && portletState.getApplication().getExceptionType()==null)
			{
				//log.info("Exception type is null");
				Collection<ApplicationItem> ai = portletState.getApplicationManagementPortletUtil().
						getApplicationItemsByApplication(portletState.getApplication());
				if(ai!=null && ai.size()>0)
				{
					ItemCategory ic = ai.iterator().next().getItemCategorySub().getItemCategory();
					Collection<WokFlowSetting> wfs = portletState.getApplicationManagementPortletUtil().
							getWorkFlowSettingByCategory(ic);
					if(wfs!=null && wfs.size()>0)
						aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/forwardstepzero.jsp");
					else
					{
						Collection<Agency> alreadyHandled = portletState.getApplicationManagementPortletUtil().getAgencyAlreadyEndorsed(portletState.getApplication());
						Collection<ApplicationWorkflow> alreadyForwardedTo = portletState.getApplicationManagementPortletUtil().getApplicationWorkFlowsByApplicationAndStatus(portletState.getApplication(), ApplicationStatus.APPLICATION_STATUS_FORWARDED);
						portletState.setAlreadyForwardedTo(alreadyForwardedTo);
						portletState.setAgenciesAlreadyHandled(alreadyHandled);
						aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/forwardstepone.jsp");
					}
				}
			}
			else
			{
				//log.info("I duno");
			}
		}else
		{
			portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
		}
	}

	private void handleViewApplicationApplicant(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		
		Applicant applicant = portletState.getApplication().getApplicant();
		if(applicant!=null)
		{
			portletState.setApplicant(applicant);
			portletState.setAgencyApplicant(null);
		
			if(applicant.getApplicantType().getValue().equals(ApplicantType.APPLICANT_TYPE_CORPORATE.getValue()))
			{
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/applicant_company/viewapplicant.jsp");
			}
			else if(applicant.getApplicantType().getValue().equals(ApplicantType.APPLICANT_TYPE_INDIVIDUAL.getValue()))
			{
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/applicant_individual/viewapplicant.jsp");
			}
		}else
		{
			portletState.setAgencyApplicant(portletState.getApplication().getPortalUser());
			portletState.setApplicant(null);
			aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/applicant_agency/viewagency.jsp");
		}
	}

	private void handleViewApplication(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Collection<ApplicationAttachment> aaList=portletState.getApplicationManagementPortletUtil().getApplicationAttachmentByApplication(portletState.getApplicationWorkflow().getApplication());
		int c=0;
		for(Iterator<ApplicationAttachment> it = aaList.iterator(); it.hasNext();)
		{
			if(it.next().getIsValid().equals(Boolean.TRUE))
			{
				c++;
			}
		}
		if(c==aaList.size())
		{
			//log.info("Set attach = true");
			portletState.setAttachsValid(true);
		}
		else
		{
			//log.info("Set attach = false");
			portletState.setAttachsValid(false);
		}
		Collection<ApplicationFlag> appFlag = portletState.getApplicationManagementPortletUtil().getApplicationFlagByApplication(portletState.getApplicationWorkflow().getApplication());
		portletState.setApplicationFlags(appFlag);
		Collection<ApplicationWorkflow> apwl1 = portletState.getApplicationManagementPortletUtil().getAppWorkFlowsByAppForComments(portletState.getApplicationWorkflow().getApplication());
		portletState.setApplicationWorkFlowsListingForComments(apwl1);
		Collection<EndorsedApplicationDesk> eadL = portletState.getApplicationManagementPortletUtil().
				getEndorsedAppDeskByApplication(portletState.getApplicationWorkflow().getApplication());
		portletState.setEndorsedApplicationDeskList(eadL);
		Collection<ApplicationAttachmentAgency> aaaCol = portletState.getApplicationManagementPortletUtil().getApplicationAttachmentAgencyByApplication(portletState.getApplicationWorkflow().getApplication());
		portletState.setValidatedAttachments(aaaCol);
		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/viewapplication.jsp");
	}

	private void forwardApplicationToAgency(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Collection<Agency> alreadyHandled = portletState.getApplicationManagementPortletUtil().getAgencyAlreadyEndorsed(portletState.getApplication());
		Collection<ApplicationWorkflow> alreadyForwardedTo = portletState.getApplicationManagementPortletUtil().getApplicationWorkFlowsByApplicationAndStatus(portletState.getApplication(), ApplicationStatus.APPLICATION_STATUS_FORWARDED);
		portletState.setAlreadyForwardedTo(alreadyForwardedTo);
		portletState.setAgenciesAlreadyHandled(alreadyHandled);
		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/forwardstepone.jsp");
	}
	
	
	private void forwardApplicationToAgencyStepTwo(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String agencyUser = aReq.getParameter("agencyUser");
		
		if(agencyUser!=null && !agencyUser.equals("-1"))
		{
			try
			{
				ApplicationWorkflow apf = portletState.getApplicationWorkflow();
				apf.setWorkedOn(Boolean.TRUE);
				swpService2.updateRecord(apf);
				
				Agency ag = (Agency)portletState.getApplicationManagementPortletUtil().getEntityObjectById(Agency.class, Long.valueOf(agencyUser));
				Long rlId = null;
				if(ag.getAgencyType()!=null && ag.getAgencyType().equals(AgencyType.ACCREDITOR_GROUP))
				{
					RoleType rld = (RoleType)(portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_ACCREDITOR_USER));
					rlId = rld.getId();
				}
				if(ag.getAgencyType()!=null && ag.getAgencyType().equals(AgencyType.ONSA_GROUP))
				{
					RoleType rld = (RoleType)(portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER));
					rlId = rld.getId();
				}
				if(ag.getAgencyType()!=null && ag.getAgencyType().equals(AgencyType.INFORMATION_GROUP))
				{
					RoleType rld = (RoleType)(portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_INFORMATION_USER));
					rlId = rld.getId();
				}
					
				if(ag.getAgencyType()!=null && ag.getAgencyType().equals(AgencyType.REGULATOR_GROUP))
				{
					RoleType rld = (RoleType)(portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_REGULATOR_USER));
					rlId = rld.getId();
				}
					
				ApplicationWorkflow aw = new ApplicationWorkflow();
				aw.setApplication(portletState.getApplication());
				aw.setComment(portletState.getCommentsForward());
				aw.setDateCreated(new Timestamp((new Date()).getTime()));
				aw.setReceipientRole(rlId);
				aw.setSourceId(portletState.getPortalUser().getRoleType().getId());
				aw.setStatus(ApplicationStatus.APPLICATION_STATUS_FORWARDED);
				aw.setWorkedOn(Boolean.FALSE);
				aw.setAgency(ag);
				aw.setSourceAgencyId(portletState.getPortalUser().getAgency().getId());
				aw = (ApplicationWorkflow)swpService2.createNewRecord(aw);
				new Util().pushAuditTrail(swpService2, aw.getId().toString(), ECIMSConstants.FORWARD_APPLICATION_REQUEST, 
						portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				
				Application ap = portletState.getApplication();
				ap.setStatus(ApplicationStatus.APPLICATION_STATUS_FORWARDED);
				ap.setExceptionType(Boolean.TRUE);
				swpService.updateRecord(ap);
				portletState.setCommentsForward(null);
				ApplicationWorkflow aw1 = portletState.getApplicationManagementPortletUtil().getLastApplicationWorkFlowByApplicationIdAndReceipientRole(ap,
						portletState.getPortalUser().getRoleType(), portletState.getPortalUser().getAgency());
				if(aw1!=null)
				{
					aw1.setWorkedOn(Boolean.TRUE);
					swpService.updateRecord(ap);
				}
				
				
				notifyNextInLineToWorkOnApplicationWorkflowItem(portletState, aw);
				
				portletState.addSuccess(aReq, "Application (" + ap.getApplicationNumber() +") has been " +
						"forwarded to your specified Agency - " + ag.getAgencyName() + 
						" (" + ag.getAgencyType().getValue() + ")", portletState);
				Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
						getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
								portletState.getPortalUser().getRoleType().getId(), 
								ApplicationStatus.APPLICATION_STATUS_CREATED, false, portletState.getPortalUser().getAgency());
	        	portletState.setApplicationWorkFlowListing(appList);
				//portletState.setApplicationListing((Collection<Application>)portletState.getApplicationManagementPortletUtil().getAllEntityObjects(Application.class));
	        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
	        	portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA);
	        	
			}catch(NumberFormatException e)
			{
				portletState.addError(aReq, "Invalid user selected. Please select a valid user before proceeding to the next step", portletState);
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/forwardsteptwo.jsp");
			}
		}else
		{
			portletState.addError(aReq, "Select an agency user before proceeding to the next step", portletState);
			aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/forwardsteptwo.jsp");
		}
	}
	
	
	private void sendTokenMessage(PortalUser pu, String message, ApplicationManagementPortletState portletState)
	{
		new SendSms(pu.getPhoneNumber(), message, 
				portletState.getMobileApplicationName().getValue(), 
				portletState.getProxyHost().getValue(), 
				portletState.getProxyPort().getValue());
	}
	
	
	
	private void notifyNSAOnEndorse(
			ApplicationManagementPortletState portletState,
			ApplicationWorkflow aw, boolean e) {
		//log.info("Notify NSA on endorsement");
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		Collection<PortalUser> pl = portletState.getApplicationManagementPortletUtil().
				getPortalUserByPermission(PermissionType.PERMISSION_FORWARD_APPLICATION);
		
		if(pl!=null && pl.size()>0)
		{
			for(Iterator<PortalUser> puIt = pl.iterator(); puIt.hasNext();)
			{
				PortalUser pu = puIt.next();
				emailer.emailPortalUserOnEndorse
					(pu.getEmailAddress(), 
					portletState.getSystemUrl().getValue(), 
					portletState.getPortalUser().getFirstName(), 
					portletState.getPortalUser().getSurname(),
					e==true ? "ECIMS application request - "+aw.getApplication().getApplicationNumber()+" endorsement" : 
						"ECIMS application request - "+aw.getApplication().getApplicationNumber()+" disendorsement", 
					portletState.getApplicationName().getValue(), 
					aw, e);
			}
			
			
			String message = e==true ? "ECIMS application request - "+aw.getApplication().getApplicationNumber()+" - " +
					"has been endorsed. Log in to view details" : "ECIMS application request - "+aw.getApplication().getApplicationNumber()+" - " +
							"has been dis. Log in to view details";
			if(pl!=null && pl.size()>0)
			{
				for(Iterator<PortalUser> puIt = pl.iterator(); puIt.hasNext();)
				{
					PortalUser pu = puIt.next();
					
					new SendSms(pu.getPhoneNumber(), message, 
							portletState.getMobileApplicationName().getValue(), 
							portletState.getProxyHost().getValue(), 
							portletState.getProxyPort().getValue());
				}
			}
		}
	}
	
	private void notifyNextInLineToWorkOnApplicationWorkflowItem(
			ApplicationManagementPortletState portletState,
			ApplicationWorkflow aw) {
		// TODO Auto-generated method stub
		
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		if(aw.getAgency().getAgencyType().getValue().equals(AgencyType.ONSA_GROUP.getValue()))
		{
			//log.info("aw.id=" + aw.getId() + " & aw.getAgency = " + aw.getAgency().getId());
			Collection<PortalUser> puL = portletState.getApplicationManagementPortletUtil().getApplicationApproversNew();
			if(puL!=null && puL.size()>0)
			{
				for(Iterator<PortalUser> puIt = puL.iterator(); puIt.hasNext();)
				{
					PortalUser pu = puIt.next();
					//log.info("pu.id=" + pu.getId());
					//log.info("pu.getEmailAddress()=" + pu.getEmailAddress());
					emailer.emailNextInLineOnApplicationWorkflowItem
						(pu.getEmailAddress(), 
						portletState.getSystemUrl().getValue(), 
						portletState.getPortalUser().getFirstName(), 
						portletState.getPortalUser().getSurname(),
						"EUC Application Awaits Your Approval", 
						portletState.getApplicationName().getValue(), 
						aw);
				}
			}
			
			String message = "EUC Application Awaits Your Approval";
			if(puL!=null && puL.size()>0)
			{
				for(Iterator<PortalUser> puIt = puL.iterator(); puIt.hasNext();)
				{
					PortalUser pu = puIt.next();
					
					new SendSms(pu.getPhoneNumber(), message, 
							portletState.getMobileApplicationName().getValue(), 
							portletState.getProxyHost().getValue(), 
							portletState.getProxyPort().getValue());
				}
			}
			
			
		}else if(aw.getAgency().getAgencyType().getValue().equals(AgencyType.REGULATOR_GROUP.getValue()) || 
				aw.getAgency().getAgencyType().getValue().equals(AgencyType.ACCREDITOR_GROUP.getValue()) || 
				aw.getAgency().getAgencyType().getValue().equals(AgencyType.INFORMATION_GROUP.getValue()))
		{
			//log.info("aw.id=" + aw.getId() + " & aw.getAgency = " + aw.getAgency().getId());
			Collection<PortalUser> puL = portletState.getApplicationManagementPortletUtil().getPortalUserByAgency(aw.getAgency());
			if(puL!=null && puL.size()>0)
			{
				for(Iterator<PortalUser> puIt = puL.iterator(); puIt.hasNext();)
				{
					PortalUser pu = puIt.next();
					//log.info("pu.id=" + pu.getId());
					//log.info("pu.getEmailAddress()=" + pu.getEmailAddress());
					
					emailer.emailNextInLineOnApplicationWorkflowItem
						(pu.getEmailAddress(), 
						portletState.getSystemUrl().getValue(), 
						portletState.getPortalUser().getFirstName(), 
						portletState.getPortalUser().getSurname(),
						"New Application Request Awaits Your Endorsement or DisEndorsement", 
						portletState.getApplicationName().getValue(), 
						aw);
				}
			}
			
			String message = "An ECIMS application request has been forwarded to you for your action";
			if(puL!=null && puL.size()>0)
			{
				for(Iterator<PortalUser> puIt = puL.iterator(); puIt.hasNext();)
				{
					PortalUser pu = puIt.next();
					
					new SendSms(pu.getPhoneNumber(), message, 
							portletState.getMobileApplicationName().getValue(), 
							portletState.getProxyHost().getValue(), 
							portletState.getProxyPort().getValue());
				}
			}
		} 
		
		
	}

	private void forwardApplicationToAgencyStepOne(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String comments = aReq.getParameter("comments");
		String commentsonsa = aReq.getParameter("commentsonsa");
		String[] agency = aReq.getParameterValues("agency");
		String agencyonsa = aReq.getParameter("agencyonsa");
		
		if(agency!=null && agency.length>0)
		{
			String[] agencyL = new String[agency.length];
			List<ApplicationWorkflow> al = new ArrayList<ApplicationWorkflow>();
			for(int c=0; c<agency.length; c++)
			{
				
				try
				{
					Agency agencyEntity = (Agency)portletState.getApplicationManagementPortletUtil().getEntityObjectById(Agency.class, Long.valueOf(agency[c]));
					agencyL[c] = agencyEntity.getAgencyName();
					portletState.setAgencyEntity(agencyEntity);
					portletState.setCommentsForward(comments);
					
					
					ApplicationWorkflow apf = portletState.getApplicationWorkflow();
					apf.setWorkedOn(Boolean.TRUE);
					swpService2.updateRecord(apf);
					
					RoleType recRoleType  = null;
					if(agencyEntity!=null && agencyEntity.getAgencyType().getValue().equalsIgnoreCase(AgencyType.ACCREDITOR_GROUP.getValue()))
						recRoleType = portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_ACCREDITOR_USER);
					if(agencyEntity!=null && agencyEntity.getAgencyType().getValue().equalsIgnoreCase(AgencyType.INFORMATION_GROUP.getValue()))
						recRoleType = portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_INFORMATION_USER);
					if(agencyEntity!=null && agencyEntity.getAgencyType().getValue().equalsIgnoreCase(AgencyType.REGULATOR_GROUP.getValue()))
						recRoleType = portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_REGULATOR_USER);
					if(agencyEntity!=null && agencyEntity.getAgencyType().getValue().equalsIgnoreCase(AgencyType.ONSA_GROUP.getValue()))
						recRoleType = portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER);
					
					
					ApplicationWorkflow aw = new ApplicationWorkflow();
					aw.setApplication(portletState.getApplication());
					aw.setComment(portletState.getCommentsForward());
					aw.setDateCreated(new Timestamp((new Date()).getTime()));
					aw.setReceipientRole(recRoleType.getId());
					aw.setSourceId(portletState.getPortalUser().getRoleType().getId());
					aw.setStatus(ApplicationStatus.APPLICATION_STATUS_FORWARDED);
					
					aw.setWorkedOn(Boolean.FALSE);
					aw.setAgency(agencyEntity);
					aw.setSourceAgencyId(portletState.getPortalUser().getAgency().getId());
					aw = (ApplicationWorkflow)swpService2.createNewRecord(aw);
					al.add(aw);
					new Util().pushAuditTrail(swpService2, aw.getId().toString(), ECIMSConstants.FORWARD_APPLICATION_REQUEST, 
							portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
					
					Application ap = portletState.getApplication();
//						if(agencyEntity!=null && agencyEntity.getAgencyType().getValue().equalsIgnoreCase(AgencyType.ONSA_GROUP.getValue()))
//							ap.setApprovalToken(RandomStringUtils.random(6, false, true));
					ap.setStatus(ApplicationStatus.APPLICATION_STATUS_FORWARDED);
					ap.setExceptionType(Boolean.TRUE);
					swpService.updateRecord(ap);
					
					ApplicationWorkflow aw1 = portletState.getApplicationManagementPortletUtil().getLastApplicationWorkFlowByApplicationIdAndReceipientRole(ap,
							portletState.getPortalUser().getRoleType(), portletState.getPortalUser().getAgency());
					if(aw1!=null)
					{
						aw1.setWorkedOn(Boolean.TRUE);
						swpService.updateRecord(ap);
					}
				}catch(NumberFormatException e)
				{
					portletState.addError(aReq, "Invalid agency selected. Please select a valid agency before proceeding to the next step", portletState);
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/forwardstepone.jsp");
				}
			}
			
			portletState.addSuccess(aReq, "Application (" + portletState.getApplication().getApplicationNumber() +") has been " +
					"forwarded to the following Agencies - " + Arrays.toString(agencyL), portletState);
			Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
					getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
							portletState.getPortalUser().getRoleType().getId(), 
							ApplicationStatus.APPLICATION_STATUS_CREATED, false, portletState.getPortalUser().getAgency());
        	portletState.setApplicationWorkFlowListing(appList);
			//portletState.setApplicationListing((Collection<Application>)portletState.getApplicationManagementPortletUtil().getAllEntityObjects(Application.class));
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA);
        	
        	if(al!=null && al.size()>0)
        	{
        		for(Iterator<ApplicationWorkflow> it = al.iterator(); it.hasNext();)
        		{
        			ApplicationWorkflow aw = it.next();
        			notifyNextInLineToWorkOnApplicationWorkflowItem(portletState, aw);
        		}
        	}
			
			
			
		}else if(agencyonsa!=null && !agencyonsa.equals("-1"))
		{
			ApplicationWorkflow apf = portletState.getApplicationWorkflow();
			Application app = apf.getApplication();
			try
			{
				
				Collection<ApplicationWorkflow> apwflw = portletState.getApplicationManagementPortletUtil().
						getApplicationWorkFlowByApplication(app);
				boolean proceedToFwdToNSA = true;
				
				if(apwflw!=null && apwflw.size()>0)
				{
					for(Iterator<ApplicationWorkflow> itwf = apwflw.iterator(); itwf.hasNext();)
					{
						ApplicationWorkflow appwl = itwf.next();
						
						if(appwl.getStatus().getValue().equals(ApplicationStatus.APPLICATION_STATUS_FORWARDED.getValue()) && 
								((appwl.getWorkedOn()==null) || (appwl.getWorkedOn().equals(Boolean.FALSE))))
						{
							proceedToFwdToNSA = false;
						}
					}
				}
				
				if(proceedToFwdToNSA==false)
				{
					portletState.addError(aReq, "You can not forward this application to NSA for approval until the application has been endorsed or disendorsed by agencies it has been forwarded to.", portletState);
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/forwardstepone.jsp");
				}else
				{
				
					Agency agencyEntity = (Agency)portletState.getApplicationManagementPortletUtil().getEntityObjectById(Agency.class, Long.valueOf(agencyonsa));
					portletState.setAgencyEntity(agencyEntity);
					portletState.setCommentsONSAForward(commentsonsa);
					
						
					
					apf.setWorkedOn(Boolean.TRUE);
					swpService2.updateRecord(apf);
					
					
					RoleType recRoleType  = null;
					recRoleType = portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER);
					ApplicationWorkflow aw = new ApplicationWorkflow();
					aw.setApplication(portletState.getApplication());
					aw.setComment(portletState.getCommentsONSAForward());
					aw.setDateCreated(new Timestamp((new Date()).getTime()));
					aw.setReceipientRole(recRoleType.getId());
					aw.setSourceId(portletState.getPortalUser().getRoleType().getId());
					aw.setStatus(ApplicationStatus.APPLICATION_STATUS_FORWARDED);
					
					aw.setWorkedOn(Boolean.FALSE);
					aw.setAgency(agencyEntity);
					aw.setSourceAgencyId(portletState.getPortalUser().getAgency().getId());
					aw = (ApplicationWorkflow)swpService2.createNewRecord(aw);
					new Util().pushAuditTrail(swpService2, aw.getId().toString(), ECIMSConstants.FORWARD_APPLICATION_REQUEST, 
							portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
					
					Application ap = portletState.getApplication();
					//if(agencyEntity!=null && agencyEntity.getAgencyType().getValue().equalsIgnoreCase(AgencyType.ONSA_GROUP.getValue()))
						//ap.setApprovalToken(RandomStringUtils.random(6, false, true));
					ap.setStatus(ApplicationStatus.APPLICATION_STATUS_FORWARDED);
					ap.setExceptionType(Boolean.TRUE);
					swpService.updateRecord(ap);
					
					ApplicationWorkflow aw1 = portletState.getApplicationManagementPortletUtil().getLastApplicationWorkFlowByApplicationIdAndReceipientRole(ap,
							portletState.getPortalUser().getRoleType(), portletState.getPortalUser().getAgency());
					if(aw1!=null)
					{
						aw1.setWorkedOn(Boolean.TRUE);
						swpService.updateRecord(ap);
					}
						
					portletState.addSuccess(aReq, "Application (" + ap.getApplicationNumber() +") has been " +
							"forwarded to - " + agencyEntity.getAgencyName() + 
							" (" + agencyEntity.getAgencyType().getValue() + ") - for approval", portletState);
					Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
							getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
									portletState.getPortalUser().getRoleType().getId(), 
									ApplicationStatus.APPLICATION_STATUS_FORWARDED, false, agencyEntity);
		        	portletState.setApplicationWorkFlowListing(appList);
					//portletState.setApplicationListing((Collection<Application>)portletState.getApplicationManagementPortletUtil().getAllEntityObjects(Application.class));
		        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
		        	portletState.setCurrentTab(VIEW_TABS.LIST_FORWARDED_APPLICATIONS_NSA);
		        	portletState.setCommentsForward(null);
		        	notifyNextInLineToWorkOnApplicationWorkflowItem(portletState, aw);
				}
	        	
	        	
			}catch(NumberFormatException e)
			{
				portletState.addError(aReq, "Invalid receipient selected. Please select a valid agency or ONSA as a receipient before proceeding to the next step", portletState);
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/forwardstepone.jsp");
			}
		}
		else
		{
			portletState.addError(aReq, "Select at least one agency or ONSA before proceeding to the next step", portletState);
			aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/forwardstepone.jsp");
		}
	}

	private void notifyApplicationApprovalToken(ActionRequest aReq, ActionResponse aRes,  
			ApplicationManagementPortletState portletState, String[] ap, String token) {
		// TODO Auto-generated method stub
//		Collection<PortalUser> puL = portletState.getApplicationManagementPortletUtil().getApplicationApprovers();
//		if(puL!=null && puL.size()>0)
//		{
//			for(Iterator<PortalUser> puIt = puL.iterator(); puIt.hasNext();)
//			{
//				String message = "Application Approval Request\nApplication Number: " +ap.getApplicationNumber()+"\nToken:"+ap.getApprovalToken();
//				sendTokenMessage(puIt.next(), message, portletState);
//			}
//		}
		
		if(portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION))
		{
			Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
					portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
							Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
					portletState.getSendingEmailUsername().getValue());
			
			ArrayList<ApplicationWorkflow> coll = portletState.getApplicationWorkFlowListingForApproval();
			String applicationNoString = "";
			for(Iterator<ApplicationWorkflow> it = coll.iterator(); it.hasNext();)
			{
				applicationNoString = it.next().getApplication().getApplicationNumber();
			}
			
				emailer.emailAppApprovalToken
				(portletState.getPortalUser().getEmailAddress(), 
						portletState.getPortalUser().getFirstName(), 
						portletState.getPortalUser().getSurname(), 
						"ECIMS Approval Token For Application No - " + applicationNoString + " ", 
					portletState.getApplicationName().getValue(), 
					portletState.getSystemUrl().getValue(), 
					"Application Approval Request (Software/SMS token)\nApplication Number(s): " +Arrays.toString(ap)+"\nToken:"+token,
					token, "[" + Arrays.toString(ap) + "]");
			
			String message = "Application Approval Request (Software/SMS token)\nApplication Number(s): " +Arrays.toString(ap)+"\nToken:"+token;
			sendTokenMessage(portletState.getPortalUser(), message, portletState);
			aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/dotoken.jsp");
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
			portletState.addError(aReq, "You do not have the appropriate permissions to approve this application. Request for appropriate permissions from appropriate personnel", portletState);
		}
			
	}

	private void handleActionsOnListApplications(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String selectedApplications=aReq.getParameter("selectedApplications");
		//log.info("selectedApplications=" + selectedApplications);
		String selectedUserAction=aReq.getParameter("selectedUserAction");
		//log.info("selectedUserAction=" + selectedUserAction);
		//log.info("selectedUserAction=" + selectedUserAction);
		//log.info("portletState.getPortalUser().getRoleType().getRole()=");
		
		if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
		{
			//log.info("user group === end user group");
			
			if(selectedUserAction.equalsIgnoreCase("gobacktoviewlisting"))
			{
				//view application
				if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP))
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
				if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
				if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
				
			}
			else if(selectedUserAction.equalsIgnoreCase("view"))
			{
				//view application
				try{
					Long id = Long.valueOf(selectedApplications);
					ApplicationWorkflow app = (ApplicationWorkflow)portletState.getApplicationManagementUtil().getEntityObjectById(ApplicationWorkflow.class, id);
					PortalUser pu = app.getApplication().getApplicant()!=null ? app.getApplication().getApplicant().getPortalUser() : app.getApplication().getPortalUser();
					if(app!=null && pu.getId().equals(portletState.getPortalUser().getId()))
					{
						portletState.setApplicationWorkflow(app);
						portletState.setApplication(app.getApplication());
						Collection<ApplicationWorkflow> apwl1 = portletState.getApplicationManagementPortletUtil().getAppWorkFlowsByAppForComments(app.getApplication());
						portletState.setApplicationWorkFlowsListingForComments(apwl1);
						Collection<EndorsedApplicationDesk> eadL = portletState.getApplicationManagementPortletUtil().
								getEndorsedAppDeskByApplication(app.getApplication());
						portletState.setEndorsedApplicationDeskList(eadL);
					}
					else
					{
						portletState.addError(aReq, "You have not selected a valid application. Ensure you select a valid application request by clicking the View button assigned to it", portletState);
					}
				}catch(NumberFormatException e)
				{
					e.printStackTrace();
					portletState.addError(aReq, "You have not selected a valid application. Ensure you select a valid application request by clicking the View button assigned to it", portletState);
				}

				
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/viewapplication.jsp");
				
			}else if(selectedUserAction.equalsIgnoreCase("viewApp"))
			{
				//view application
				try{
					Long id = Long.valueOf(selectedApplications);
					Application app = (Application)portletState.getApplicationManagementUtil().getEntityObjectById(Application.class, id);
					PortalUser pu = app.getApplicant()!=null ? app.getApplicant().getPortalUser() : app.getPortalUser();
					if(app!=null && pu.getId().equals(portletState.getPortalUser().getId()))
					{
						portletState.setApplication(app);
					}
					else
					{
						portletState.addError(aReq, "You have not selected a valid application. Ensure you select a valid application request by clicking the View button assigned to it", portletState);
					}
				}catch(NumberFormatException e)
				{
					e.printStackTrace();
					portletState.addError(aReq, "You have not selected a valid application. Ensure you select a valid application request by clicking the View button assigned to it", portletState);
				}
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/viewapplication_eu.jsp");
				
			}
			else if(selectedUserAction.equalsIgnoreCase("resendCode"))
			{
				//view application
				try{
					selectedApplications=aReq.getParameter("selectedApplications");
					Long id = Long.valueOf(selectedApplications);
					Application ap = (Application)portletState.getApplicationManagementUtil().getEntityObjectById(Application.class, id);
					ApplicationWorkflow apwNew = (ApplicationWorkflow)portletState.getApplicationManagementUtil().
							getApplicationWorkFlowByApplicationAndStatus(ap, ApplicationStatus.APPLICATION_STATUS_APPROVED);
					
					notifyEndUserApplicationApproved(apwNew, portletState, ap.getCertificateCollectionCode());
				}catch(Exception e)
				{
					e.printStackTrace();
					portletState.addError(aReq, "We experienced errors resending your collection code. Please try again. If this problem persists contact our administrator", portletState);
				}
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
				
			}else if(selectedUserAction.equalsIgnoreCase("viewapplication"))
			{
				//view application
				try{
					Long id = Long.valueOf(selectedApplications);
					Collection<ApplicationAttachment> aaList=portletState.getApplicationManagementPortletUtil().getApplicationAttachmentByApplication(portletState.getApplicationWorkflow().getApplication());
					int c=0;
					for(Iterator<ApplicationAttachment> it = aaList.iterator(); it.hasNext();)
					{
						if(it.next().getIsValid().equals(Boolean.TRUE))
						{
							c++;
						}
					}
					if(c==aaList.size())
						portletState.setAttachsValid(true);
					else
						portletState.setAttachsValid(false);
					
					ApplicationWorkflow app = (ApplicationWorkflow)portletState.getApplicationManagementUtil().getEntityObjectById(ApplicationWorkflow.class, id);
					Collection<ApplicationFlag> appFlag = portletState.getApplicationManagementPortletUtil().getApplicationFlagByApplication(app.getApplication());
					portletState.setApplicationFlags(appFlag);
					PortalUser pu = app.getApplication().getApplicant()!=null ? app.getApplication().getApplicant().getPortalUser() : app.getApplication().getPortalUser();
					
					if(app!=null && pu.getId().equals(portletState.getPortalUser().getId()))
					{
						portletState.setApplicationWorkflow(app);
						portletState.setApplication(app.getApplication());

						Collection<ApplicationWorkflow> apwl1 = portletState.getApplicationManagementPortletUtil().getAppWorkFlowsByAppForComments(app.getApplication());
						portletState.setApplicationWorkFlowsListingForComments(apwl1);
						Collection<EndorsedApplicationDesk> eadL = portletState.getApplicationManagementPortletUtil().
								getEndorsedAppDeskByApplication(app.getApplication());
						portletState.setEndorsedApplicationDeskList(eadL);
						aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/viewapplication.jsp");
					}
					else
					{
						portletState.addError(aReq, "You have not selected a valid application. Ensure you select a valid application request by clicking the View button assigned to it", portletState);
					}
					
				}catch(NumberFormatException e)
				{
					e.printStackTrace();
					portletState.addError(aReq, "You have not selected a valid application. Ensure you select a valid application request by clicking the View button assigned to it", portletState);
				}
				
			}
		}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
		{
			//log.info("user group === nsa user group");
			if(selectedUserAction.equalsIgnoreCase("view"))
			{
				//view application
				try{
					Long id = Long.valueOf(selectedApplications);
					//log.info("id == " + id);
					ApplicationWorkflow app = (ApplicationWorkflow)portletState.getApplicationManagementUtil().getEntityObjectById(ApplicationWorkflow.class, id);
					if(app!=null)
					{
						//log.info("app != null");
						portletState.setApplicationWorkflow(app);
						portletState.setApplication(app.getApplication());

						Collection<ApplicationWorkflow> apwl1 = portletState.getApplicationManagementPortletUtil().getAppWorkFlowsByAppForComments(app.getApplication());
						portletState.setApplicationWorkFlowsListingForComments(apwl1);
						Collection<EndorsedApplicationDesk> eadL = portletState.getApplicationManagementPortletUtil().
								getEndorsedAppDeskByApplication(app.getApplication());
						portletState.setEndorsedApplicationDeskList(eadL);
						aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/viewapplication.jsp");
					}
					else
					{
						//log.info("app == null");
						portletState.addError(aReq, "You have not selected a valid application. Ensure you select a valid application request by clicking the View button assigned to it", portletState);
					}
				}catch(NumberFormatException e)
				{
					e.printStackTrace();
					portletState.addError(aReq, "You have not selected a valid application. Ensure you select a valid application request by clicking the View button assigned to it", portletState);
				}
				
			}else if(selectedUserAction.equalsIgnoreCase("viewapplication"))
			{
				//view application
				try{
					Long id = Long.valueOf(selectedApplications);
					ApplicationWorkflow app = (ApplicationWorkflow)portletState.getApplicationManagementUtil().getEntityObjectById(ApplicationWorkflow.class, id);
					
					if(app!=null)
					{
						//log.info("app != null");
						portletState.setApplicationWorkflow(app);
						portletState.setApplication(app.getApplication());
						Collection<ApplicationFlag> appFlag = portletState.getApplicationManagementPortletUtil().getApplicationFlagByApplication(app.getApplication());
						portletState.setApplicationFlags(appFlag);

						Collection<ApplicationWorkflow> apwl1 = portletState.getApplicationManagementPortletUtil().getAppWorkFlowsByAppForComments(app.getApplication());
						portletState.setApplicationWorkFlowsListingForComments(apwl1);
						Collection<EndorsedApplicationDesk> eadL = portletState.getApplicationManagementPortletUtil().
								getEndorsedAppDeskByApplication(app.getApplication());
						portletState.setEndorsedApplicationDeskList(eadL);
						aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/viewapplication.jsp");
					}
					else
					{
						//log.info("app == null");
						portletState.addError(aReq, "You have not selected a valid application. Ensure you select a valid application request by clicking the View button assigned to it", portletState);
					}
				}catch(NumberFormatException e)
				{
					e.printStackTrace();
					portletState.addError(aReq, "You have not selected a valid application. Ensure you select a valid application request by clicking the View button assigned to it", portletState);
				}
				
			}else if(selectedUserAction.equalsIgnoreCase("addbiometric"))				//step 1 for certificate gen proceess
			{
				if(portletState.getPermissionList().contains(PermissionType.PERMISSION_ISSUE_CERTIFICATE))
				{
					handleAddBiometric(aReq, aRes, portletState, swpService2);
				}else
				{
					portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
				}
				
			}
			else if(selectedUserAction.equalsIgnoreCase("capturebiometric"))				//step 2 for certificate gen proceess
			{
				if(portletState.getPermissionList().contains(PermissionType.PERMISSION_ISSUE_CERTIFICATE))
				{
					captureBiometric(aReq, aRes, portletState, swpService2);
				}else
				{
					portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
				}
			}
			else if(selectedUserAction.equalsIgnoreCase("approveone"))
			{
				Long id = Long.valueOf(selectedApplications);
				ApplicationWorkflow app = (ApplicationWorkflow)portletState.getApplicationManagementUtil().getEntityObjectById(ApplicationWorkflow.class, id);
				portletState.setApplicationWorkflow(app);
				handleApproveOneApplicationStepOne(aReq, aRes, swpService2, portletState);
			}
			else if(selectedUserAction.equalsIgnoreCase("batchprocessapprove"))
				handleApproveApplication(aReq, aRes, swpService2, portletState);
			else if(selectedUserAction!=null && selectedUserAction.equals("batchprocessdisaprprove"))
				handleDisApproveApplication(aReq, aRes, swpService2, portletState);
		}else if(portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
		{
			//log.info("user group === nsa user group");
			if(selectedUserAction.equalsIgnoreCase("view"))
			{
				//view application
				try{
					Long id = Long.valueOf(selectedApplications);
					//log.info("id == " + id);
					ApplicationWorkflow app = (ApplicationWorkflow)portletState.getApplicationManagementUtil().getEntityObjectById(ApplicationWorkflow.class, id);
					if(app!=null)
					{
						//log.info("app != null");
						portletState.setApplicationWorkflow(app);
						portletState.setApplication(app.getApplication());
						Collection<ApplicationAttachment> aaList=portletState.getApplicationManagementPortletUtil().getApplicationAttachmentByApplication(portletState.getApplicationWorkflow().getApplication());
						int c=0;
						for(Iterator<ApplicationAttachment> it = aaList.iterator(); it.hasNext();)
						{
							if(it.next().getIsValid().equals(Boolean.TRUE))
							{
								c++;
							}
						}
						if(c==aaList.size())
						{
							//log.info("Set attach = true");
							portletState.setAttachsValid(true);
						}
						else
						{
							//log.info("Set attach = false");
							portletState.setAttachsValid(false);
						}

						Collection<ApplicationFlag> appFlag = portletState.getApplicationManagementPortletUtil().getApplicationFlagByApplication(app.getApplication());
						portletState.setApplicationFlags(appFlag);

						Collection<ApplicationWorkflow> apwl1 = portletState.getApplicationManagementPortletUtil().getAppWorkFlowsByAppForComments(app.getApplication());
						portletState.setApplicationWorkFlowsListingForComments(apwl1);
						Collection<EndorsedApplicationDesk> eadL = portletState.getApplicationManagementPortletUtil().
								getEndorsedAppDeskByApplication(app.getApplication());
						portletState.setEndorsedApplicationDeskList(eadL);

						Collection<ApplicationAttachmentAgency> aaaCol = portletState.getApplicationManagementPortletUtil().
								getApplicationAttachmentAgencyByApplication(app.getApplication());
						portletState.setValidatedAttachments(aaaCol);
						aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/viewapplication.jsp");
					}
					else
					{
						//log.info("app == null");
						portletState.addError(aReq, "You have not selected a valid application. Ensure you select a valid application request by clicking the View button assigned to it", portletState);
					}
				}catch(NumberFormatException e)
				{
					e.printStackTrace();
					portletState.addError(aReq, "You have not selected a valid application. Ensure you select a valid application request by clicking the View button assigned to it", portletState);
				}
				
			}else if(selectedUserAction.equalsIgnoreCase("viewapplication"))
			{
				//view application
				try{
					Long id = Long.valueOf(selectedApplications);
					ApplicationWorkflow app = (ApplicationWorkflow)portletState.getApplicationManagementUtil().getEntityObjectById(ApplicationWorkflow.class, id);
					if(app!=null)
					{
						//log.info("app != null");
						portletState.setApplicationWorkflow(app);
						portletState.setApplication(app.getApplication());
						Collection<ApplicationAttachment> aaList=portletState.getApplicationManagementPortletUtil().getApplicationAttachmentByApplication(portletState.getApplicationWorkflow().getApplication());
						int c=0;
						for(Iterator<ApplicationAttachment> it = aaList.iterator(); it.hasNext();)
						{
							if(it.next().getIsValid().equals(Boolean.TRUE))
							{
								c++;
							}
						}
						if(c==aaList.size())
						{
							//log.info("Set attach = true");
							portletState.setAttachsValid(true);
						}
						else
						{
							//log.info("Set attach = false");
							portletState.setAttachsValid(false);
						}
						Collection<ApplicationFlag> appFlag = portletState.getApplicationManagementPortletUtil().getApplicationFlagByApplication(app.getApplication());
						portletState.setApplicationFlags(appFlag);

						Collection<ApplicationWorkflow> apwl1 = portletState.getApplicationManagementPortletUtil().getAppWorkFlowsByAppForComments(app.getApplication());
						portletState.setApplicationWorkFlowsListingForComments(apwl1);
						Collection<EndorsedApplicationDesk> eadL = portletState.getApplicationManagementPortletUtil().
								getEndorsedAppDeskByApplication(app.getApplication());
						portletState.setEndorsedApplicationDeskList(eadL);
						Collection<ApplicationAttachmentAgency> aaaCol = portletState.getApplicationManagementPortletUtil().
								getApplicationAttachmentAgencyByApplication(app.getApplication());
						portletState.setValidatedAttachments(aaaCol);
						aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/viewapplication.jsp");
					}
					else
					{
						//log.info("app == null");
						portletState.addError(aReq, "You have not selected a valid application. Ensure you select a valid application request by clicking the View button assigned to it", portletState);
					}
				}catch(NumberFormatException e)
				{
					e.printStackTrace();
					portletState.addError(aReq, "You have not selected a valid application. Ensure you select a valid application request by clicking the View button assigned to it", portletState);
				}
				
			}
		}
	}

	private void createApplicationEUStepFour(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		createNewApplication(portletState, swpService2, aReq, aRes);
	}

	private void createNewApplication(
			ApplicationManagementPortletState portletState,
			SwpService swpService2, ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		String act = aReq.getParameter("act");
		if(act!=null && act.equalsIgnoreCase("next"))
		{
			Application application = new Application();
			String applciationNumber = "APPLN" + RandomStringUtils.random(6, false, true);
			portletState.setApplciationNumber(applciationNumber);
			application.setApplicationNumber(applciationNumber);
			Applicant applicant = portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser());
			if(applicant!=null)
			{
				application.setApplicant(applicant);
			}else
			{
				application.setPortalUser(portletState.getPortalUser());
			}
			application.setDateCreated(new Timestamp((new Date()).getTime()));
			application.setImportAddress(portletState.getTxtImportAddress());
			application.setImportDuty(portletState.getChkBoxImportDuty()!=null && portletState.getChkBoxImportDuty().equals("1") ? true : false);
			application.setImportLetter(portletState.getFileUploadAwardLetter());
			application.setImportName(portletState.getImportName());
			application.setProofAttachment(portletState.getFileUploadProofAttachment());
			application.setProofTitle(null);
			application.setPurposeOfUsage(portletState.getPurposeOfUsage());
			//application.setState(portletState.getPortofLandingStateEntity());
			application.setCertificateCollectionCode(null);
			application.setStatus(ApplicationStatus.APPLICATION_STATUS_CREATED);
			application.setPortCode(portletState.getPortCodeEntity());
			application.setCountry(portletState.getCountryEntity());
			application.setExceptionType(null);
			application.setCurrency(portletState.getCurrencyEntity());
			application.setCurrentWorkflowPosition(null);
			application.setItemCategory(portletState.getItemCategoryEntity());
			application = (Application) swpService2.createNewRecord(application);
			//log.info("Application created = " + application.getId());
			ApplicationWorkflow aw = new ApplicationWorkflow();
			aw.setApplication(application);
			aw.setComment(null);
			aw.setDateCreated(new Timestamp((new Date()).getTime()));
			aw.setReceipientRole(portletState.getApplicationManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_NSA_USER).getId());
			aw.setSourceId(portletState.getPortalUser().getRoleType().getId());
			aw.setStatus(ApplicationStatus.APPLICATION_STATUS_CREATED);
			aw.setWorkedOn(Boolean.FALSE);
			//aw.setSourceAgencyId();
			Agency ag = portletState.getApplicationManagementPortletUtil().getAgencyByName(AgencyType.ONSA_GROUP);
			aw.setAgency(ag);
			aw = (ApplicationWorkflow)swpService2.createNewRecord(aw);
			//log.info("ApplicationWorkflow Created = " + aw.getId());
			
			
			new Util().pushAuditTrail(swpService2, aw.getId().toString(), ECIMSConstants.NEW_APPLICATION_REQUEST, 
					portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());


			//log.info("ApplicationItems  = " + portletState.getApplicationItemList().size());
			for(Iterator<ApplicationItem> iter = portletState.getApplicationItemList().iterator(); iter.hasNext();)
			{
				ApplicationItem applicationItem = iter.next();
				applicationItem.setApplication(application);
//				applicationItem.setItemCategorySub(portletState.getItemCategorySubEntity());
				swpService2.createNewRecord(applicationItem);
				
			}
			
			if(portletState.getAttachmentListing()!=null && portletState.getAttachmentListing().size()>0)
			{
				for(Iterator<ApplicationAttachment> iter = portletState.getAttachmentListing().iterator(); iter.hasNext();)
				{
					ApplicationAttachment applicationAttachment = iter.next();
					//log.info("applicationAttachment file name==" + applicationAttachment.getAttachmentFile());
					applicationAttachment.setApplication(application);
					swpService2.createNewRecord(applicationAttachment);
					
				}
			}
			
			
			notifyForMakeNewApplication(portletState, application);
			portletState.addSuccess(aReq, "EUC Application Request successfully created", portletState);
			portletState.reinitializeForNewApplication(portletState);
			aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepfive.jsp");
		}
		else if(act!=null && act.equalsIgnoreCase("back"))
		{
			aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepthree.jsp");
		}
		
		
		
	}

	private void notifyForMakeNewApplication(
			ApplicationManagementPortletState portletState, Application application) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		emailer.emailNewApplicationNotification
				(portletState.getPortalUser().getEmailAddress(), portletState.getSystemUrl().getValue(), 
				portletState.getPortalUser().getFirstName(), portletState.getPortalUser().getSurname(), 
				"New EUC Application Request!", 
				portletState.getApplicationName().getValue(), portletState.getApplciationNumber());
		
		Collection<PortalUser> puL = portletState.getApplicationManagementPortletUtil().getApplicationApprovers();
		
		if(puL!=null && puL.size()>0)
		{
			for(Iterator<PortalUser> puIt = puL.iterator(); puIt.hasNext();)
			{
				PortalUser pu = puIt.next();
				emailer.emailNSANewApplicationRequest
					(pu.getEmailAddress(), 
					portletState.getSystemUrl().getValue(), 
					portletState.getPortalUser().getFirstName(), 
					portletState.getPortalUser().getSurname(),
					"New ECIMS Application Request - "+application.getApplicationNumber()+" - Awaiting Your Action", 
					portletState.getApplicationName().getValue(), 
					application);
			}
		}
		
		
		
		String message = "New EUC application request created! \nApplication Number: " + portletState.getApplciationNumber();
		try{
				
				new SendSms(portletState.getPortalUser().getPhoneNumber(), message, 
						portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
				message = "New EUC application request awaiting your action! \nApplication Number: " + portletState.getApplciationNumber();
				if(puL!=null && puL.size()>0)
				{
					for(Iterator<PortalUser> puIt = puL.iterator(); puIt.hasNext();)
					{
						PortalUser pu = puIt.next();
						new SendSms(pu.getPhoneNumber(), message, 
								portletState.getMobileApplicationName().getValue(), 
								portletState.getProxyHost().getValue(), 
								portletState.getProxyPort().getValue());
					}
					
				}
		}catch(Exception e){
			log.error("error sending sms ",e);
		}
	}

	private void createApplicationEUStepThree(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState, UploadPortletRequest uploadRequest) {
		// TODO Auto-generated method stub
		String folder = ECIMSConstants.NEW_APPLICATION_DIRECTORY;
		//log.info("Folder===" + folder);
		String realPath = getPortletContext().getRealPath("/");
		//log.info("folder=" + folder + " && realPath=" + realPath);
		
		//UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(aReq);
//		String FileAttachUploadNpfFireworksPermit = uploadRequest.getParameter("FileAttachUploadNpfFireworksPermit");
//		String FileAttachUploadYearsOfOperation = uploadRequest.getParameter("FileAttachUploadYearsOfOperation");
//		String FileAttachUploadManufacturersSafety = uploadRequest.getParameter("FileAttachUploadManufacturersSafety");
//		String FileAttachUploadNafdac = uploadRequest.getParameter("FileAttachUploadNafdac");
//		String FileAttachUploadStorageFacility = uploadRequest.getParameter("FileAttachUploadStorageFacility");
//		String FileAttachUploadPreviousStorage = uploadRequest.getParameter("FileAttachUploadPreviousStorage");
//		String FileAttachUploadRegistrationWithFfd = uploadRequest.getParameter("FileAttachUploadRegistrationWithFfd");
//		String FileAttachUploadSpecOfGoods = uploadRequest.getParameter("FileAttachUploadSpecOfGoods");
//		String FileAttachUploadTransitDetails = uploadRequest.getParameter("FileAttachUploadTransitDetails");
//		String FileAttachUploadIdentificationCards = uploadRequest.getParameter("FileAttachUploadIdentificationCards");
		String chkImportingFor = uploadRequest.getParameter("chkImportingFor");
		String txtImportName = uploadRequest.getParameter("txtImportName");
		String txtImportAddress = uploadRequest.getParameter("txtImportAddress");
		String FileUploadAwardLetter = uploadRequest.getParameter("FileUploadAwardLetter");
		String chkBoxImportDuty = uploadRequest.getParameter("chkBoxImportDuty");
		//String txtProofTitle = uploadRequest.getParameter("txtProofTitle");
		//String FileUploadProofAttachment = uploadRequest.getParameter("FileUploadProofAttachment");
		//String PortofLandingState = uploadRequest.getParameter("PortofLandingPort");
		String act = uploadRequest.getParameter("act");
		ArrayList<String> shameList = new ArrayList<String>();
		//log.info("act =" + act);
		if(act.equalsIgnoreCase("next"))
		{
			if(validateMakeApplicationStepThree(chkImportingFor, txtImportName, 
					txtImportAddress, chkBoxImportDuty, aReq, portletState))
			{
				try
				{

					
					boolean proceed = true;
					if(portletState.getPortalUser().getAgency()!=null)
					{
						chkImportingFor = "1";
					}
					if(chkImportingFor!=null && chkImportingFor.equals("1"))
					{
						portletState.setChkImportingFor(chkImportingFor);
						portletState.setImportName(txtImportName);
						portletState.setTxtImportAddress(txtImportAddress);
						ImageUpload imupload = handleUploadsForAttachments("FileUploadAwardLetter", 
								"/html/applicationmanagementportlet/makeanapplication/stepthree.jsp", 
								"/html/applicationmanagementportlet/makeanapplication/stepfour.jsp", aReq, aRes, portletState, true, "Contract Award Letter", uploadRequest, folder);
						if(imupload!=null && imupload.getNewFileName()!=null && imupload.getNewFileName().length()>0)
						{
							portletState.setFileUploadAwardLetter(imupload.getNewFileName());
							proceed = true;
						}else
						{
							proceed = false;
						}
//						else
//						{
//							//proceed23 = false;
//							portletState.addError(aReq, "Ensure you provide the contract award letter", portletState);
//						}
					}
					if(proceed==false)
					{
						portletState.addError(aReq, "If You are Importing for another organization or individual, you must provide the contract award letter attachment", portletState);
						aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepthree.jsp");
					}else
					{
						
	//					if(chkBoxImportDuty!=null && chkBoxImportDuty.equals("1"))
	//					{
							portletState.setChkBoxImportDuty(chkBoxImportDuty);
	//						portletState.setTxtProofTitle(txtProofTitle);
	//						ImageUpload imupload = handleUploadsForAttachments("FileUploadProofAttachment", 
	//								"/html/applicationmanagementportlet/makeanapplication/stepthree.jsp", 
	//								"/html/applicationmanagementportlet/makeanapplication/stepfour.jsp", aReq, aRes, portletState, true, "Import Duty Proof", uploadRequest, folder);
	//						if(imupload!=null)
	//						{
	//							portletState.setFileUploadProofAttachment(imupload.getNewFileName());
	//							
	//						}
	//					}
							
						
						
	
						boolean compulsory =false;
						ImageUpload imupload = null;
						Collection<ApplicationAttachment> appAttachList = new ArrayList<ApplicationAttachment>();
						Collection<ApplicationAttachmentType> aatList = (Collection<ApplicationAttachmentType>)portletState.getApplicationManagementPortletUtil().
								getAllEntityObjects(ApplicationAttachmentType.class);
						int x1=0;
						for(Iterator<ApplicationAttachmentType> ata = aatList.iterator(); ata.hasNext();)
						{
							imupload = null;
							ApplicationAttachmentType aat1 = ata.next();
							imupload = handleUploadsForAttachments("FileAttachUpload" + aat1.getName().replace(" ", "").toLowerCase(),  
									"/html/applicationmanagementportlet/makeanapplication/stepthree.jsp", 
									"/html/applicationmanagementportlet/makeanapplication/stepfour.jsp", aReq, aRes, portletState, compulsory, aat1.getName(), uploadRequest, folder);
							if(imupload!=null)
							{
								
									ApplicationAttachment appAttach = new ApplicationAttachment();
									appAttach.setApplicationAttachmentType((ApplicationAttachmentType)portletState.getApplicationManagementUtil().getApplicationAttachmentByName(aat1.getName()));
									appAttach.setAttachmentFile(imupload.getNewFileName());
									appAttach.setIsValid(Boolean.FALSE);
									appAttachList.add(appAttach);
									x1++;
							}else
							{
								if(compulsory)
									shameList.add(aat1.getName());
							}
						}
						
						
						
						
						//FileAttachUploadNpfFireworksPermit
						
//						ApplicationAttachmentType aat = null;
//						
//						compulsory =new Util().isAttachmentCompulsoryForHSCode("NPF FireWorks Permit", portletState.getItemCategoryMapListing(), portletState.getItemCategoryEntity());
//						imupload = handleUploadsForAttachments("FileAttachUploadNpfFireworksPermit", 
//								"/html/applicationmanagementportlet/makeanapplication/stepthree.jsp", 
//								"/html/applicationmanagementportlet/makeanapplication/stepfour.jsp", aReq, aRes, portletState, compulsory, "NPF FireWorks Permit", uploadRequest, folder);
//						
//						if(imupload!=null)
//						{
//							
//								ApplicationAttachment appAttach = new ApplicationAttachment();
//								appAttach.setApplicationAttachmentType((ApplicationAttachmentType)portletState.getApplicationManagementUtil().getApplicationAttachmentByName(AttachmentTypeConstant.NPF_FIREWORKS_PERMIT.getValue()));
//								appAttach.setAttachmentFile(imupload.getNewFileName());
//								appAttach.setIsValid(Boolean.FALSE);
//								appAttachList.add(appAttach);
//						}else
//						{
//							if(compulsory)
//								shameList.add("NPF FireWorks Permit");
//						}
						
						
						
//						compulsory =new Util().isAttachmentCompulsoryForHSCode("NPF FireWorks Permit", portletState.getItemCategoryMapListing(), portletState.getItemCategoryEntity());
//						imupload = handleUploadsForAttachments("FileAttachUploadNpfFireworksPermit", 
//								"/html/applicationmanagementportlet/makeanapplication/stepthree.jsp", 
//								"/html/applicationmanagementportlet/makeanapplication/stepfour.jsp", aReq, aRes, portletState, compulsory, "NPF FireWorks Permit", uploadRequest, folder);
//						
//						if(imupload!=null)
//						{
//							
//								ApplicationAttachment appAttach = new ApplicationAttachment();
//								appAttach.setApplicationAttachmentType((ApplicationAttachmentType)portletState.getApplicationManagementUtil().getApplicationAttachmentByName(AttachmentTypeConstant.NPF_FIREWORKS_PERMIT.getValue()));
//								appAttach.setAttachmentFile(imupload.getNewFileName());
//								appAttach.setIsValid(Boolean.FALSE);
//								appAttachList.add(appAttach);
//						}else
//						{
//							if(compulsory)
//								shameList.add("NPF FireWorks Permit");
//						}
//						
//						//FileAttachUploadYearsOfOperation
//						//Years Of Operation Attachment
//						compulsory =new Util().isAttachmentCompulsoryForHSCode("Years Of Operation Attachment", portletState.getItemCategoryMapListing(), portletState.getItemCategoryEntity());
//						imupload = handleUploadsForAttachments("FileAttachUploadYearsOfOperation", 
//								"/html/applicationmanagementportlet/makeanapplication/stepthree.jsp", 
//								"/html/applicationmanagementportlet/makeanapplication/stepfour.jsp", aReq, aRes, portletState, compulsory, "Years of Operation", uploadRequest, folder);
//						if(imupload!=null)
//						{
//							ApplicationAttachment appAttach = new ApplicationAttachment();
//							appAttach.setApplicationAttachmentType((ApplicationAttachmentType)portletState.getApplicationManagementUtil().getApplicationAttachmentByName(AttachmentTypeConstant.YEARS_OF_OPERATION.getValue()));
//							appAttach.setAttachmentFile(imupload.getNewFileName());
//							appAttach.setIsValid(Boolean.FALSE);
//							appAttachList.add(appAttach);
//						}
//						else
//						{
//							if(compulsory)
//								shameList.add("Years Of Operation Attachment");
//						}
//						
//						//FileAttachUploadManufacturersSafety
//						compulsory =new Util().isAttachmentCompulsoryForHSCode(AttachmentTypeConstant.MANUFACTURERS_SAFETY.getValue(), portletState.getItemCategoryMapListing(), portletState.getItemCategoryEntity());
//						imupload = handleUploadsForAttachments("FileAttachUploadManufacturersSafety", 
//								"/html/applicationmanagementportlet/makeanapplication/stepthree.jsp", 
//								"/html/applicationmanagementportlet/makeanapplication/stepfour.jsp", aReq, aRes, portletState, compulsory, "Manufacturers Safety", uploadRequest, folder);
//						if(imupload!=null)
//						{
//							ApplicationAttachment appAttach = new ApplicationAttachment();
//							appAttach.setApplicationAttachmentType((ApplicationAttachmentType)portletState.getApplicationManagementUtil().getApplicationAttachmentByName(AttachmentTypeConstant.MANUFACTURERS_SAFETY.getValue()));
//							appAttach.setAttachmentFile(imupload.getNewFileName());
//							appAttach.setIsValid(Boolean.FALSE);
//							appAttachList.add(appAttach);
//						}
//						else
//						{
//							if(compulsory)
//								shameList.add(AttachmentTypeConstant.MANUFACTURERS_SAFETY.getValue());
//						}
//						
//						
//						//FileAttachUploadNafdac
//						compulsory =new Util().isAttachmentCompulsoryForHSCode(AttachmentTypeConstant.NAFDAC_ATTACHMENT.getValue(), portletState.getItemCategoryMapListing(), portletState.getItemCategoryEntity());
//						imupload = handleUploadsForAttachments("FileAttachUploadNafdac", 
//								"/html/applicationmanagementportlet/makeanapplication/stepthree.jsp", 
//								"/html/applicationmanagementportlet/makeanapplication/stepfour.jsp", aReq, aRes, portletState, compulsory, "NAFDAC Permit", uploadRequest, folder);
//						if(imupload!=null)
//						{
//							ApplicationAttachment appAttach = new ApplicationAttachment();
//							appAttach.setApplicationAttachmentType((ApplicationAttachmentType)portletState.getApplicationManagementUtil().getApplicationAttachmentByName(AttachmentTypeConstant.NAFDAC_ATTACHMENT.getValue()));
//							appAttach.setAttachmentFile(imupload.getNewFileName());
//							appAttach.setIsValid(Boolean.FALSE);
//							appAttachList.add(appAttach);
//						}
//						else
//						{
//							if(compulsory)
//								shameList.add(AttachmentTypeConstant.NAFDAC_ATTACHMENT.getValue());
//						}
//						
//						
//						//FileAttachUploadStorageFacility
//						compulsory =new Util().isAttachmentCompulsoryForHSCode(AttachmentTypeConstant.STORAGE_FACILITY.getValue(), portletState.getItemCategoryMapListing(), portletState.getItemCategoryEntity());
//						imupload = handleUploadsForAttachments("FileAttachUploadStorageFacility", 
//								"/html/applicationmanagementportlet/makeanapplication/stepthree.jsp", 
//								"/html/applicationmanagementportlet/makeanapplication/stepfour.jsp", aReq, aRes, portletState, compulsory, "Storage Facility", uploadRequest, folder);
//						if(imupload!=null)
//						{
//							ApplicationAttachment appAttach = new ApplicationAttachment();
//							appAttach.setApplicationAttachmentType((ApplicationAttachmentType)portletState.getApplicationManagementUtil().getApplicationAttachmentByName(AttachmentTypeConstant.STORAGE_FACILITY.getValue()));
//							appAttach.setAttachmentFile(imupload.getNewFileName());
//							appAttach.setIsValid(Boolean.FALSE);
//							appAttachList.add(appAttach);
//						}
//						else
//						{
//							if(compulsory)
//								shameList.add(AttachmentTypeConstant.STORAGE_FACILITY.getValue());
//						}
//						
//						
//						//FileAttachUploadPreviousStorage
//						compulsory =new Util().isAttachmentCompulsoryForHSCode(AttachmentTypeConstant.PREVIOUS_STORAGE_ATTACHMENT.getValue(), portletState.getItemCategoryMapListing(), portletState.getItemCategoryEntity());
//						imupload = handleUploadsForAttachments("FileAttachUploadPreviousStorage", 
//								"/html/applicationmanag)ementportlet/makeanapplication/stepthree.jsp", 
//								"/html/applicationmanagementportlet/makeanapplication/stepfour.jsp", aReq, aRes, portletState, compulsory, "Previous Storage", uploadRequest, folder);
//						if(imupload!=null)
//						{
//							ApplicationAttachment appAttach = new ApplicationAttachment();
//							appAttach.setApplicationAttachmentType((ApplicationAttachmentType)portletState.getApplicationManagementUtil().getApplicationAttachmentByName(AttachmentTypeConstant.PREVIOUS_STORAGE_ATTACHMENT.getValue()));
//							appAttach.setAttachmentFile(imupload.getNewFileName());
//							appAttach.setIsValid(Boolean.FALSE);
//							appAttachList.add(appAttach);
//						}
//						else
//						{
//							if(compulsory)
//								shameList.add(AttachmentTypeConstant.PREVIOUS_STORAGE_ATTACHMENT.getValue());
//						}
//						
//						
//						//FileAttachUploadRegistrationWithFfd
//						compulsory =new Util().isAttachmentCompulsoryForHSCode(AttachmentTypeConstant.REGISTRATION_WITH_FFD.getValue(), portletState.getItemCategoryMapListing(), portletState.getItemCategoryEntity());
//						imupload = handleUploadsForAttachments("FileAttachUploadRegistrationWithFfd", 
//								"/html/applicationmanagementportlet/makeanapplication/stepthree.jsp", 
//								"/html/applicationmanagementportlet/makeanapplication/stepfour.jsp", aReq, aRes, portletState, compulsory, "Registration with FFD", uploadRequest, folder);
//						if(imupload!=null)
//						{
//							ApplicationAttachment appAttach = new ApplicationAttachment();
//							appAttach.setApplicationAttachmentType((ApplicationAttachmentType)portletState.getApplicationManagementUtil().getApplicationAttachmentByName(AttachmentTypeConstant.REGISTRATION_WITH_FFD.getValue()));
//							appAttach.setAttachmentFile(imupload.getNewFileName());
//							appAttach.setIsValid(Boolean.FALSE);
//							appAttachList.add(appAttach);
//						}
//						else
//						{
//							if(compulsory)
//								shameList.add(AttachmentTypeConstant.REGISTRATION_WITH_FFD.getValue());
//						}
//						
//						
//						//FileAttachUploadSpecOfGoods
//						compulsory =new Util().isAttachmentCompulsoryForHSCode(AttachmentTypeConstant.SPECIFICATION_OF_GOODS.getValue(), portletState.getItemCategoryMapListing(), portletState.getItemCategoryEntity());
//						imupload = handleUploadsForAttachments("FileAttachUploadSpecOfGoods", 
//								"/html/applicationmanagementportlet/makeanapplication/stepthree.jsp", 
//								"/html/applicationmanagementportlet/makeanapplication/stepfour.jsp", aReq, aRes, portletState, compulsory, "Specification of Goods", uploadRequest, folder);
//						if(imupload!=null)
//						{
//							ApplicationAttachment appAttach = new ApplicationAttachment();
//							appAttach.setApplicationAttachmentType((ApplicationAttachmentType)portletState.getApplicationManagementUtil().getApplicationAttachmentByName(AttachmentTypeConstant.SPECIFICATION_OF_GOODS.getValue()));
//							appAttach.setAttachmentFile(imupload.getNewFileName());
//							appAttach.setIsValid(Boolean.FALSE);
//							appAttachList.add(appAttach);
//						}
//						else
//						{
//							if(compulsory)
//								shameList.add(AttachmentTypeConstant.SPECIFICATION_OF_GOODS.getValue());
//						}
//						
//						
//						//FileAttachUploadTransitDetails
//						compulsory =new Util().isAttachmentCompulsoryForHSCode(AttachmentTypeConstant.TRANSIT_DETAILS.getValue(), portletState.getItemCategoryMapListing(), portletState.getItemCategoryEntity());
//						imupload = handleUploadsForAttachments("FileAttachUploadTransitDetails", 
//								"/html/applicationmanagementportlet/makeanapplication/stepthree.jsp", 
//								"/html/applicationmanagementportlet/makeanapplication/stepfour.jsp", aReq, aRes, portletState, compulsory, "Transit Details", uploadRequest, folder);
//						if(imupload!=null)
//						{
//							ApplicationAttachment appAttach = new ApplicationAttachment();
//							appAttach.setApplicationAttachmentType((ApplicationAttachmentType)portletState.getApplicationManagementUtil().getApplicationAttachmentByName(AttachmentTypeConstant.TRANSIT_DETAILS.getValue()));
//							appAttach.setAttachmentFile(imupload.getNewFileName());
//							appAttach.setIsValid(Boolean.FALSE);
//							appAttachList.add(appAttach);
//						}
//						else
//						{
//							if(compulsory)
//								shameList.add(AttachmentTypeConstant.TRANSIT_DETAILS.getValue());
//						}
//						
//						
//						//FileAttachUploadIdentificationCards
//						compulsory =new Util().isAttachmentCompulsoryForHSCode(AttachmentTypeConstant.IDENTIFICATION_CARDS.getValue(), portletState.getItemCategoryMapListing(), portletState.getItemCategoryEntity());
//						imupload = handleUploadsForAttachments("FileAttachUploadIdentificationCards", 
//								"/html/applicationmanagementportlet/makeanapplication/stepthree.jsp", 
//								"/html/applicationmanagementportlet/makeanapplication/stepfour.jsp", aReq, aRes, portletState, compulsory, "Identification Cards", uploadRequest, folder);
//						if(imupload!=null)
//						{
//							ApplicationAttachment appAttach = new ApplicationAttachment();
//							appAttach.setApplicationAttachmentType((ApplicationAttachmentType)portletState.getApplicationManagementUtil().getApplicationAttachmentByName(AttachmentTypeConstant.IDENTIFICATION_CARDS.getValue()));
//							appAttach.setAttachmentFile(imupload.getNewFileName());
//							appAttach.setIsValid(Boolean.FALSE);
//							appAttachList.add(appAttach);
//						}
//						else
//						{
//							if(compulsory)
//								shameList.add(AttachmentTypeConstant.IDENTIFICATION_CARDS.getValue());
//						}
						
						if(x1>0 && aatList.size()>0)
						{
							if(shameList!=null && shameList.size()==0)
							{
								portletState.setAttachmentListing(appAttachList);					
								aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepfour.jsp");
							}else
							{
								String[] a = shameList.toArray(new String[shameList.size()]);
								portletState.addError(aReq, "The following attachment types are compulsory and " +
										"must be provided before you can proceed: " + Arrays.toString(a), portletState);
								aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepthree.jsp");
							}
						}else
						{
							if(x1==0 && aatList.size()>0)
							{
								portletState.setAttachmentListing(null);					
								aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepthree.jsp");
								portletState.addError(aReq, "You must upload at least one document before you " +
										"can proceed.", portletState);
							}
						}
						
					}
					
				}catch(NumberFormatException e)
				{
					portletState.addError(aReq, "Specify the Port of Landing", portletState);
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepthree.jsp");
				}
			}else
			{
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepthree.jsp");
			}
		}else 
		{
			
		}
	}

	private boolean validateMakeApplicationStepThree(String chkImportingFor,
			String txtImportName, String txtImportAddress,
			String chkBoxImportDuty, ActionRequest aReq, ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		if(chkImportingFor!=null && chkImportingFor.equals("1"))
		{
			if(txtImportName!=null && txtImportName.length()>0)
			{
				if(txtImportAddress!=null && txtImportAddress.length()>0)
				{
					return true;
				}else
				{
					portletState.addError(aReq, "Provide the address of the organization you are importing these items for", portletState);
					return false;
				}
			}else
			{
				portletState.addError(aReq, "Provide the name of the organization you are importing these items for", portletState);
				return false;
			}
		}else
		{
			return true;
		}
	}

	private ImageUpload handleUploadsForAttachments(String reqParaName, String backUrl,
			String fwdUrl, ActionRequest aReq, ActionResponse aRes,
			ApplicationManagementPortletState portletState, boolean compulsory, String reqFullName, UploadPortletRequest uploadRequest, String folder) {
		// TODO Auto-generated method stub
		System.out.println("Size for " + reqParaName + ": "+uploadRequest.getSize(reqParaName));
		if(compulsory)
		{
			if (uploadRequest.getSize(reqParaName)==0) {
				portletState.addError(aReq, "You have not attached your "+ reqFullName + ". Ensure you attach your " + reqFullName + " before proceeding", portletState);
				aRes.setRenderParameter("jspPage", backUrl);
				return null;
			}else
			{
				ImageUpload imupload = new Util().uploadImage(uploadRequest, reqParaName, folder);
				return imupload;
			}
		}else
		{
			if (uploadRequest.getSize(reqParaName)==0) {
				return null;
			}else
			{
				ImageUpload imupload = new Util().uploadImage(uploadRequest, reqParaName, folder);
				return imupload;
			}
		}
	}

	
	
	private void addApplicationItems(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		//log.info("1");
		String txtDescription = aReq.getParameter("txtDescription");
		String txtQuantity = aReq.getParameter("txtQuantity");
		String txtQuantityUnit = aReq.getParameter("txtQuantityUnit");
		String txtAmount = aReq.getParameter("txtAmount");
		//String Currency = aReq.getParameter("Currency");
		String txtItemValue = aReq.getParameter("txtItemValue");
		String txtItemValueInword = aReq.getParameter("txtItemValueInword");
		String HSCode = aReq.getParameter("HSCode");
		
		//log.info("HSCode ==" + HSCode);
		
		Collection<ApplicationItem> appItemList = portletState.getApplicationItemList();
		//log.info("2");
		if(validateMakeApplicationAddItems(HSCode, txtDescription, txtQuantity, 
				txtAmount, txtItemValue, txtItemValueInword, txtQuantityUnit, portletState, aReq))
		{
			if(appItemList==null)
				appItemList = new ArrayList<ApplicationItem>();
			
			//log.info("3");
			
			//log.info("4");
			ApplicationItem appItem = new ApplicationItem();
			appItem.setAmount(Double.valueOf(txtAmount));
			appItem.setAmountInWords(txtItemValueInword);
			appItem.setCurrency(portletState.getCurrencyEntity());
			appItem.setDateCreated(new Timestamp((new Date()).getTime()));
			appItem.setDescription(txtDescription);
			appItem.setItemCategorySub((ItemCategorySub)portletState.getApplicationManagementPortletUtil().getEntityObjectById(ItemCategorySub.class, Long.valueOf(HSCode)));
			boolean proceed = true;
			if(txtQuantity!=null)
			{
				try
				{
					appItem.setQuantity(Integer.valueOf(txtQuantity));
					appItem.setQuantityUnit((QuantityUnit)portletState.getApplicationManagementPortletUtil().getEntityObjectById(QuantityUnit.class, Long.valueOf(txtQuantityUnit)));
					appItem.setWeight(null);
				}catch(NumberFormatException e)
				{
					proceed = false;
					e.printStackTrace();
					portletState.addError(aReq, "Invalid weight/quantity or unit selected. Provide valid data before proceeding", portletState);
				}
				
				
				
				//appItem.setWeightUnit((WeightUnit)portletState.getApplicationManagementPortletUtil().getEntityObjectById(WeightUnit.class, Long.valueOf(txtWeightUnit)));
				
			}
			
				
			if(proceed==true)
			{
				//log.info("5");
				appItemList.add(appItem);
				portletState.setApplicationItemList(appItemList);
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/steptwo.jsp");
			}else
			{
				portletState.setApplicationItemList(null);
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/steptwo.jsp");
			}
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/steptwo.jsp");
		}
	}

	private boolean validateMakeApplicationAddItems(String HSCode, String txtDescription,
			String txtQuantity, String txtAmount,
			String txtItemValue, String txtItemValueInword,
			String txtQuantityUnit, ApplicationManagementPortletState portletState, ActionRequest aReq) {
		// TODO Auto-generated method stub
		ArrayList<String> err = new ArrayList<String>();
		if(HSCode!=null && !HSCode.equals("-1"))
		{
			
		}
		else
		{
			err.add("Select an Item Type before proceeding");
		}
		if(txtDescription!=null && txtDescription.length()!=0)
		{
			
		}
		else
		{
			err.add("Specify the currency of purchase for your items");
		}
		if(txtAmount!=null && txtAmount.length()!=0)
		{
			try{
				Double d= Double.valueOf(txtAmount);
			}catch(NumberFormatException e)
			{
				err.add("Invalid cost of item provided. Do not include comma or spaces in the cost of item field. Cost of item should be numeric e.g. 2000, 200.34");
			}
		}
		else
		{
			err.add("Specify the cost of the item");
		}
		if(txtQuantity!=null && txtQuantity.length()>0)
		{
			
		}
		else
		{
			err.add("Provide the quantity/weight of the item you are specifying");
		}

		String errMsg = "";
		for(Iterator<String> it = err.iterator(); it.hasNext();)
		{
			errMsg += it.next() + "<br>";
		}
		
		if(errMsg.length()>0)
		{
			portletState.addError(aReq, errMsg, portletState);
			return false;
		}else
		{
			return true;
		}
	}

	private void createApplicationEUStepTwo(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		
		
		String act = aReq.getParameter("act");
		
		
		if(act!=null && act.equals("Add"))
		{
			addApplicationItems(aReq, aRes, swpService2, portletState);
		}
		if(act!=null && act.equals("Clear"))
		{
			clearApplicationItems(aReq, aRes, swpService2, portletState);
		}else if(act!=null && act.equals("next"))
		{
			if(validateMakeApplicationStepTwo(aReq, portletState.getApplicationItemList(), portletState))
			{
				try
				{
					
					if(portletState.getApplicationItemList()!=null && portletState.getApplicationItemList().size()>0)
					{
						portletState.setDescription(null);
						portletState.setQuantity(null);
						portletState.setQuantityUnit(null);
						portletState.setWeight(null);
						portletState.setWeightUnit(null);
						portletState.setAmount(null);
						portletState.setCurrency(null);

						portletState.setAttachmentTypeListByHSCode(portletState.getApplicationManagementUtil().getAttachmentTypeByHSCode());						
						aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepthree.jsp");
					}else
					{
						portletState.addError(aReq, "You have not added your items. Please add at least one item before clicking the Next button", portletState);
						aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/steptwo.jsp");
					}
				}catch(NumberFormatException e)
				{
					portletState.addError(aReq, "Invalid Item category selected. Select a valid item category before proceeding", portletState);
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/steptwo.jsp");
				}
			}else
			{
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/steptwo.jsp");
			}
		}else if(act!=null && act.equals("back"))
		{
			aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepone.jsp");
		} 
		
		
	}

	private void clearApplicationItems(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setApplicationItemList(null);
		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/steptwo.jsp");
	}

	private boolean validateMakeApplicationStepTwo(ActionRequest aReq, 
			Collection itemList, ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String txtDescription = aReq.getParameter("txtDescription");
		String txtQuantity = aReq.getParameter("txtQuantity");
		String txtQuantityUnit = aReq.getParameter("txtQuantityUnit");
		String txtWeight = aReq.getParameter("txtWeight");
		String txtWeightUnit = aReq.getParameter("txtWeightUnit");
		String txtAmount = aReq.getParameter("txtAmount");
		String currency = aReq.getParameter("Currency");
		String txtItemValue = aReq.getParameter("txtItemValue");
		String txtItemValueInword = aReq.getParameter("txtItemValueInword");
		
		portletState.setDescription(txtDescription);
		portletState.setQuantity(txtQuantity);
		portletState.setQuantityUnit(txtQuantityUnit);
		portletState.setWeight(txtWeight);
		portletState.setWeightUnit(txtWeightUnit);
		portletState.setAmount(txtAmount);
		if(portletState.getCurrencyEntity()==null)
			portletState.setCurrency(currency);
		return true;
	}

	private void createApplicationEUStepOne(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicationManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String itemCatagory = aReq.getParameter("ItemCatagory");
		String CountryofManufacture = aReq.getParameter("CountryofManufacture");
		String txtPurposeofUsage = aReq.getParameter("txtPurposeofUsage");
		String currency = aReq.getParameter("Currency");
		String PortofLandingPort = aReq.getParameter("PortofLandingPort");
		
		if(validateMakeApplicationStepOne(PortofLandingPort, itemCatagory, CountryofManufacture, txtPurposeofUsage, portletState, aReq, currency))
		{
			try
			{

				if(PortofLandingPort!=null && PortofLandingPort.trim().length()>0)
				{
					portletState.setPortCodeEntity((PortCode)portletState.getApplicationManagementPortletUtil().
							getEntityObjectById(PortCode.class, Long.valueOf(PortofLandingPort)));
				}
				Currency c = ((Currency)portletState.getApplicationManagementPortletUtil().getEntityObjectById(Currency.class, Long.valueOf(currency)));
				portletState.setItemCategoryEntity((ItemCategory)portletState.getApplicationManagementPortletUtil().getEntityObjectById(ItemCategory.class, Long.valueOf(itemCatagory)));
				portletState.setItemCategorySubList(portletState.getApplicationManagementPortletUtil().getItemCategorySubByItemCategory(portletState.getItemCategoryEntity()));
				if(portletState.getApplicationItemList()!=null && portletState.getApplicationItemList().size()>0)
				{
					
				}else
				{
					portletState.setCurrencyEntity(c);
				}
				portletState.setExemptionYes(false);
				
				try
				{
					portletState.setCountryEntity((Country)portletState.getApplicationManagementPortletUtil().getEntityObjectById(Country.class, Long.valueOf(CountryofManufacture)));
					portletState.setPurposeOfUsage(txtPurposeofUsage);
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/steptwo.jsp");
				}catch(NumberFormatException e)
				{
					//log.info("Test 0");
					portletState.addError(aReq, "Invalid country selected. Select a valid country before proceeding", portletState);
					aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepone.jsp");
				}
			}catch(NumberFormatException e)
			{
				//log.info("Test 1");
				portletState.addError(aReq, "Invalid Item category selected. Select a valid item category before proceeding", portletState);
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepone.jsp");
			}
				
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepone.jsp");
		}
		
	}

	private boolean validateMakeApplicationStepOne(String port, String itemCatagory,
			String countryofManufacture, String txtPurposeofUsage, 
			ApplicationManagementPortletState portletState, ActionRequest aReq, String currency) {
		// TODO Auto-generated method stub
		ArrayList<String> err = new ArrayList<String>();
		if(itemCatagory!=null && !itemCatagory.equals("-1"))
		{
			
		}
		else
		{
			err.add("You did not select an item category. Select one");
		}
//		
//		if(port!=null && !port.equals("-1"))
//		{
//			
//		}
//		else
//		{
//			err.add("You did not select a landing port. Select one");
//		}
		if(countryofManufacture!=null && !countryofManufacture.equals("-1"))
		{
			
		}
		else
		{
			err.add("Specify the country of manufacture for your item(s)");
		}
		if(currency!=null && !currency.equals("-1"))
		{
			
		}
		else
		{
			err.add("Specify the currency of purchase for your items");
		}
		if(txtPurposeofUsage!=null && !txtPurposeofUsage.equals("-1"))
		{
		}
		else
		{
			err.add("Provide the purpose of usage for the item(s) you are specifying");
		}
		
		String errMsg = "";
		for(Iterator<String> it = err.iterator(); it.hasNext();)
		{
			errMsg += it.next() + "<br>";
		}
		
		if(errMsg.length()>0)
		{
			portletState.addError(aReq, errMsg, portletState);
			return false;
		}else
		{
			return true;
		}
	}
	
	
	public void test(String action, ActionRequest aReq, ActionResponse aRes, ApplicationManagementPortletState portletState)
	{
		if(action.equalsIgnoreCase(EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_ONE.name()))
        {
        	//log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	if(this.managePortletState(portletState.getPortalUser().getId()))
        		createApplicationEUStepOne(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_TWO.name()))
        {
        	//log.info("CREATE_A_PORTAL_USER_STEP_TWO");
        	//Select Account type
        	String act = aReq.getParameter("act");
        	//log.info("act==" + act);
        	if(act!=null && act.equalsIgnoreCase("next"))
	        	createApplicationEUStepTwo(aReq, aRes, swpService, portletState);
        	else if(act!=null && act.equalsIgnoreCase("Add"))
        		createApplicationEUStepTwo(aReq, aRes, swpService, portletState);
        	else if(act!=null && act.equalsIgnoreCase("Clear"))
        		clearApplicationItems(aReq, aRes, swpService, portletState);
    		else if(act!=null && act.equalsIgnoreCase("backfromsteptwomakeapplication"))
        		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepone.jsp");
        		
        }
        if(action.equalsIgnoreCase(EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_THREE.name()))
        {
        	//log.info("CREATE_A_PORTAL_USER_STEP_THREE");
        	//Select Account type
        	UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(aReq);
        	String act = uploadRequest.getParameter("act");
        	//log.info("act===" + act);
        	if(act!=null && act.equalsIgnoreCase("next"))
        		createApplicationEUStepThree(aReq, aRes, swpService, portletState, uploadRequest);
        	else if(act!=null && act.equalsIgnoreCase("backfromstepthreemakeapplication"))
        		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/steptwo.jsp");
        		
        }
        if(action.equalsIgnoreCase(EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_FOUR.name()))
        {
        	//log.info("CREATE_A_PORTAL_USER_STEP_TWO");
        	//Select Account type
        	createApplicationEUStepFour(aReq, aRes, swpService, portletState);
        		
        }
        
        
        if(action.equalsIgnoreCase(AGENCY_ACTIONS.SIGN_ENDORSE_APPLICATION.name()))
        {
        	//log.info("NSA_ACTION");
        	handleEndorseApplicationStepTwo(aReq, aRes, swpService, portletState);
			 
        }
        

        if(action.equalsIgnoreCase(APPLIST_ACTIONS.HANDLE_ACTIONS_ON_LIST_APPLICATIONS.name()))
        {
        	//log.info("HANDLE ACTIONS ON APPLICATiON LISTING");
        	handleActionsOnListApplications(aReq, aRes, swpService, portletState);
        		
        }
        
        
        if(action.equalsIgnoreCase(APPLIST_ACTIONS.ACT_ON_APPLICATION.name()))
        {
        	//log.info("HANDLE ACTIONS ON ONE APPLICATiON");
        	handleActionsOnOneApplication(aReq, aRes, swpService, portletState);
        	
        }
        
        if(action.equalsIgnoreCase(APPLIST_ACTIONS.ACT_ON_UPLOAD_CERTIFICATE.name()))
        {
        	//log.info("HANDLE ACTIONS ON ONE APPLICATiON");
        	UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(aReq);
        	String act = uploadRequest.getParameter("act");
        	String actId = uploadRequest.getParameter("actId");
        	if(act!=null && act.equals("issuecertificateStepThree"))			//step 3 for certificate gen proceess //issueCertificateStepOne(aReq, aRes, swpService2, portletState);
				confirmPrint(uploadRequest, aRes, aReq, portletState, swpService, actId);
			else if(act!=null && act.equals("cancelissuestepthree"))			//step 3 for certificate gen proceess //issueCertificateStepOne(aReq, aRes, swpService2, portletState);
			{
				Certificate cert = portletState.getCertificateGenerated();
				//cert.setCertificateNo(null);
				cert.setSignature(null);
				cert.setCertificatePrinted(null);
				swpService.updateRecord(cert);
				
				new Util().pushAuditTrail(swpService, cert.getId().toString(), 
						ECIMSConstants.REJECT_CERTIFICATE, 
						portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/issuecertificate_steptwo.jsp");
			}
        	
        }
        
        

        if(action.equalsIgnoreCase(NSA_ACTIONS.SIGN_APPROVE_APPLICATION.name()))
        {
        	//log.info("HANDLE ACTIONS ON APPLICATiON LISTING");
        	approveApplicationWithToken(aReq, aRes, swpService, portletState);
        		
        }

        if(action.equalsIgnoreCase(VIEW_TABS.CREATE_AN_APPLICATION_EU.name()))
        {
        	portletState.reinitializeForNewApplication(portletState);
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
        		portletState.setAgencyApplicant(null);
        	}
        	else
        	{
        		portletState.setApplicant(null);
        		portletState.setAgencyApplicant(portletState.getPortalUser());
        	}
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepone.jsp");
        	portletState.setCurrentTab(VIEW_TABS.CREATE_AN_APPLICATION_EU);
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_ALL_APPLICATIONS_EU.name()))
        {
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
        		portletState.setAgencyApplicant(null);
        		if(portletState.getApplicant()!=null)
        			portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByApplicant(portletState.getApplicant()));
        		else
        			portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByAgencyApplicant(portletState.getAgencyApplicant()));
        	}
        	else
        	{
        		portletState.setApplicant(null);
        		portletState.setAgencyApplicant(portletState.getPortalUser());
        		portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByAgencyApplicant(portletState.getAgencyApplicant()));
        	}
        	
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_ALL_APPLICATIONS_EU);
        	if(portletState.getApplicationListing()==null || (portletState.getApplicationListing()!=null && portletState.getApplicationListing().size()==0))
        	{
        		portletState.addError(aReq, "You have not created any EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_FORWARDED_APPLICATIONS_NSA.name()))
        {
        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        			getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(portletState.getPortalUser().getRoleType().getId(), 
							ApplicationStatus.APPLICATION_STATUS_FORWARDED, false);
//        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
//        			getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_FORWARDED, false, 
//							portletState.getPortalUser().getAgency());
        	portletState.setApplicationWorkFlowListing(appList);
        	//portletState.setApplicationFlags(portletState.getApplicationManagementPortletUtil().getApplicationFlags());
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_FORWARDED_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no EUC request applications forwarded yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_APPROVED_APPLICATIONS_EU.name()))
        {
//        	portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
        	
        	
        	
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
        		portletState.setAgencyApplicant(null);
        		portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByApplicantAndStatus(
        				portletState.getApplicant(), 
        				new ApplicationStatus[]{ApplicationStatus.APPLICATION_STATUS_APPROVED, ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED}));
        	}
        	else
        	{
        		portletState.setApplicant(null);
        		portletState.setAgencyApplicant(portletState.getPortalUser());
        		portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByAgencyApplicantAndStatus(portletState.getAgencyApplicant(), ApplicationStatus.APPLICATION_STATUS_APPROVED));
        	}
        	
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_APPLICATIONS_EU);
        	if(portletState.getApplicationListing()==null || (portletState.getApplicationListing()!=null && portletState.getApplicationListing().size()==0))
        	{
        		portletState.addError(aReq, "You do not have any approved EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_APPROVED_APPLICATIONS_NSA.name()))
        {
        	//portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_APPROVED));
        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        			getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(portletState.getPortalUser().getRoleType().getId(), 
							ApplicationStatus.APPLICATION_STATUS_APPROVED, false);
        	portletState.setApplicationWorkFlowListing(appList);
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no approved EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_PENDING_APPLICATIONS_EU.name()))
        {
        	String[] a = {ApplicationStatus.APPLICATION_STATUS_APPROVED.getValue(), 
        				ApplicationStatus.APPLICATION_STATUS_ATTACHMENT_DEVALIDATED.getValue(), 
        				ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED.getValue(), 
        				ApplicationStatus.APPLICATION_STATUS_REJECTED.getValue()};
        	
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().
            			getApplicationsByApplicantAndExceptStatus
            			(portletState.getApplicant(), a));
            	portletState.setAgencyApplicant(null);
        	}
        	else
        	{
        		portletState.setAgencyApplicant(portletState.getPortalUser());
        		portletState.setApplicant(null);
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().
            			getApplicationsByAgencyApplicantAndExceptStatus
            			(portletState.getAgencyApplicant(), a));
        	}
        	
        	
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_EU);
        	
        	if(portletState.getApplicationListing()==null || (portletState.getApplicationListing()!=null && portletState.getApplicationListing().size()==0))
        	{
        		portletState.addError(aReq, "You have not created any EUC request applications yet.", portletState);
        	}else
        	{
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA.name()))
        {
        	//portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_CREATED));
//        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
//					getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
//							portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_CREATED, false, portletState.getPortalUser().getAgency());
        	//log.info("----------------------------" + "44:");
        	Collection<ApplicationWorkflow> appList = null;
        	if(portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_FORWARD_APPLICATION) && 
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
				//log.info("----------------------------" + "45:");
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
        	portletState.setApplicationWorkFlowListing(appList);
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no pending EUC request applications at the moment.", portletState);
        	}
        	
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_REJECTED_APPLICATIONS_EU.name()))
        {
        	
        	
        	
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByApplicantAndStatus(portletState.getApplicant(), ApplicationStatus.APPLICATION_STATUS_REJECTED));
            	portletState.setAgencyApplicant(null);
        	}
        	else
        	{
        		portletState.setApplicant(null);
        		portletState.setAgencyApplicant(portletState.getPortalUser());
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByAgencyApplicantAndStatus(portletState.getAgencyApplicant(), ApplicationStatus.APPLICATION_STATUS_REJECTED));
        	}
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_REJECTED_APPLICATIONS_EU);
        	if(portletState.getApplicationListing()==null || (portletState.getApplicationListing()!=null && portletState.getApplicationListing().size()==0))
        	{
        		portletState.addError(aReq, "You do not have any rejected EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_REJECTED_APPLICATIONS_NSA.name()))
        {
        	//portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED));
        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        			getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(portletState.getPortalUser().getRoleType().getId(), 
							ApplicationStatus.APPLICATION_STATUS_REJECTED, false);
        	portletState.setApplicationWorkFlowListing(appList);

        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_REJECTED_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no rejected EUC request applications yet.", portletState);
        	}
        }
//        if(action.equalsIgnoreCase(VIEW_TABS.LIST_DISPUTED_APPLICATIONS_NSA.name()))
//        {
//        	//portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED));
//        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
//        			getApplicationWorkFlowByReceipientRoleIdAndStatus(portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_DISPUTED);
//        	portletState.setApplicationWorkFlowListing(appList);
//        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
//        	portletState.setCurrentTab(VIEW_TABS.LIST_DISPUTED_APPLICATIONS_NSA);
//        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
//        	{
//        		portletState.addError(aReq, "There are no EUC request currently being disputed applications yet.", portletState);
//        	}
//        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_NSA.name()))
        {
//        	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED));
        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        			getApplicationWorkFlowByReceipientRoleIdAndStatus(portletState.getPortalUser().getRoleType().getId(), 
        					ApplicationStatus.APPLICATION_STATUS_ENDORSED, portletState.getPortalUser().getAgency());
//        			.
//					getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
//							portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_ENDORSED, 
//							false, portletState.getPortalUser().getAgency());
        	portletState.setApplicationWorkFlowListing(appList);
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no endorsed EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_NSA.name()))
        {
        	//portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED));
        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        			getApplicationWorkflowByStatus(ApplicationStatus.APPLICATION_STATUS_DISENDORSED);
//        			getApplicationWorkFlowByReceipientRoleIdAndStatus(portletState.getPortalUser().getRoleType().getId(), 
//        					ApplicationStatus.APPLICATION_STATUS_DISENDORSED, portletState.getPortalUser().getAgency());
//        			getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
//							portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_DISENDORSED, 
//							false, portletState.getPortalUser().getAgency());
        	portletState.setApplicationWorkFlowListing(appList);
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no disendorsed EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_APPROVED_AWAIT_ISSUANCE_APPLICATIONS_EU.name()))
        {
        	String status[] = {ApplicationStatus.APPLICATION_STATUS_REJECTED.getValue(), ApplicationStatus.APPLICATION_STATUS_APPROVED.getValue(), ApplicationStatus.APPLICATION_STATUS_CREATED.getValue(), };
        	
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByApplicantAndExceptStatus(portletState.getApplicant(), status));
            	
            	portletState.setAgencyApplicant(null);
        	}
        	else
        	{
        		portletState.setApplicant(null);
        		portletState.setAgencyApplicant(portletState.getPortalUser());
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByAgencyApplicantAndExceptStatus(portletState.getAgencyApplicant(), status));
            	
        	}
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_AWAIT_ISSUANCE_APPLICATIONS_EU);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "You do not have any EUC requests awaiting issuance at the moment.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_APPROVED_AWAIT_ISSUANCE_APPLICATIONS_NSA.name()))
        {
        	String status[] = {ApplicationStatus.APPLICATION_STATUS_REJECTED.getValue(), ApplicationStatus.APPLICATION_STATUS_APPROVED.getValue(), ApplicationStatus.APPLICATION_STATUS_CREATED.getValue(), };
        	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByExceptStatus(status));
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_AWAIT_ISSUANCE_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no EUC requests awaiting issuance at the moment.", portletState);
        	}
        }
        
        
        
//        if(action.equalsIgnoreCase(VIEW_TABS.LIST_DISPUTED_APPLICATIONS_AG.name()))
//        {
//        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
//        	{
//        		ArrayList<Application> al = null;
//        		Collection<ApplicationWorkflow> apwList = 
//        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
//        				getApplicationWorkFlowBySourceRoleIdAndStatus(portletState.getPortalUser().getRoleType().getId(), ApplicationStatus.APPLICATION_STATUS_DISPUTED);
//        		if(apwList!=null && apwList.size()>0)
//        		{
//        			al = new ArrayList<Application>();
//	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
//	        		{
//	        			ApplicationWorkflow aw = it.next();
//	        			al.add(aw.getApplication());
//	        		}
//	        		portletState.setApplicationWorkFlowListing(apwList);
//	        		portletState.setApplicationListing(al);
//	        		portletState.setCurrentTab(VIEW_TABS.LIST_DISPUTED_APPLICATIONS_AG);
//        		}else
//        		{
//        			portletState.addError(aReq, "There are no applications disputed by your agency", portletState);
//        		}
//        	}
//        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_PENDING_APPLICATIONS_AG.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
        	{
        		ArrayList<Application> al = null;
        		Collection<ApplicationWorkflow> apwList = 
        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        				getApplicationWorkFlowByReceipientRoleIdAndNotWorkedOn(portletState.getPortalUser().getRoleType().getId(), false, portletState.getPortalUser().getAgency());
        		if(apwList!=null && apwList.size()>0)
        		{
        			al = new ArrayList<Application>();
	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
	        		{
	        			ApplicationWorkflow aw = it.next();
	        			al.add(aw.getApplication());
	        		}
	        		portletState.setApplicationWorkFlowListing(apwList);
	        		portletState.setApplicationListing(al);
        		}else
        		{
        			portletState.setApplicationWorkFlowListing(null);
            		portletState.setApplicationListing(null);
        			portletState.addError(aReq, "There are no pending applications available for you to work on", portletState);
        		}
        	}
        	
    		portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_AG);
    		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.CREATE_AN_APPLICATION_AG.name()))
        {
        	portletState.setCurrentTab(VIEW_TABS.CREATE_AN_APPLICATION_AG);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepone.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_MY_AGENCY_APPLICATIONS_AG.name()))
        {
			Collection<ApplicationWorkflow> apwList = null;
			if(!portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER))
			{
				apwList = 
    				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
    				getApplicationWorkFlowByAgency(
    						portletState.getPortalUser().getAgency().getId());
			}else
			{
				apwList = 
    				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
    				getApplicationWorkFlowByApplicant(
    						portletState.getApplicant().getId());
			}
        	
        	
    		if(apwList!=null && apwList.size()>0)
    		{
    			//log.info("apwListsize ===" + apwList.size());
    			ArrayList<Application> al = new ArrayList<Application>();
        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
        		{
        			ApplicationWorkflow aw = it.next();
        			al.add(aw.getApplication());
        		}
        		portletState.setApplicationWorkFlowListing(apwList);
        		portletState.setApplicationListing(al);
    		}else
    		{
    			portletState.setApplicationWorkFlowListing(null);
        		portletState.setApplicationListing(null);
        		if(!portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER))
    			{
        			portletState.addError(aReq, "There are no applications created by your agency on this system", portletState);
    			}else
    			{
    				portletState.addError(aReq, "You do not have any applications created on this system", portletState);
    			}
    		}
        	portletState.setCurrentTab(VIEW_TABS.LIST_MY_AGENCY_APPLICATIONS_AG);
    		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_AG.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
        	{
        		ArrayList<Application> al = null;
        		Collection<ApplicationWorkflow> apwList = 
        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        				getApplicationWorkFlowBySourceAgencyIdAndStatus(
        						portletState.getPortalUser().getAgency().getId(), 
        						ApplicationStatus.APPLICATION_STATUS_ENDORSED);
        		if(apwList!=null && apwList.size()>0)
        		{
        			al = new ArrayList<Application>();
	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
	        		{
	        			ApplicationWorkflow aw = it.next();
	        			al.add(aw.getApplication());
	        		}
	        		portletState.setApplicationWorkFlowListing(apwList);
	        		portletState.setApplicationListing(al);
        		}else
        		{
        			portletState.setApplicationWorkFlowListing(null);
            		portletState.setApplicationListing(null);
        			portletState.addError(aReq, "There are no applications endorsed by your agency", portletState);
        		}
        	}

    		portletState.setCurrentTab(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_AG);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_AG.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
        	{
        		ArrayList<Application> al = null;
        		Collection<ApplicationWorkflow> apwList = 
        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        				getApplicationWorkFlowBySourceAgencyIdAndStatus(
        						portletState.getPortalUser().getAgency().getId(), ApplicationStatus.APPLICATION_STATUS_DISENDORSED);
        		if(apwList!=null && apwList.size()>0)
        		{
        			al = new ArrayList<Application>();
	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
	        		{
	        			ApplicationWorkflow aw = it.next();
	        			al.add(aw.getApplication());
	        		}
	        		portletState.setApplicationWorkFlowListing(apwList);
	        		portletState.setApplicationListing(al);
        		}else
        		{
        			portletState.setApplicationWorkFlowListing(null);
            		portletState.setApplicationListing(null);
        			portletState.addError(aReq, "There are no applications disendorsed by your agency", portletState);
        		}
        	}

    		portletState.setCurrentTab(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_AG);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_FLAGGED_APPLICATIONS_AG.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
        	{
        		ArrayList<Application> al = null;
        		Collection<ApplicationWorkflow> apwList = 
        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        				getApplicationWorkFlowBySourceAgencyIdAndStatus(portletState.getPortalUser().getAgency().getId(), ApplicationStatus.APPLICATION_STATUS_FLAGGED);
        		if(apwList!=null && apwList.size()>0)
        		{
        			al = new ArrayList<Application>();
	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
	        		{
	        			ApplicationWorkflow aw = it.next();
	        			al.add(aw.getApplication());
	        		}
	        		portletState.setApplicationWorkFlowListing(apwList);
	        		portletState.setApplicationListing(al);
        		}else
        		{
        			portletState.setApplicationWorkFlowListing(null);
            		portletState.setApplicationListing(null);
        			portletState.addError(aReq, "There are no applications flagged by your agency", portletState);
        		}
        	}
    		portletState.setCurrentTab(VIEW_TABS.LIST_FLAGGED_APPLICATIONS_AG);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_DEVALIDATED_APPLICATIONS_AG.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
        	{
        		ArrayList<Application> al = null;
        		Collection<ApplicationWorkflow> apwList = 
        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        				getApplicationWorkFlowBySourceAgencyIdAndStatus(portletState.getPortalUser().getAgency().getId(), ApplicationStatus.APPLICATION_STATUS_ATTACHMENT_DEVALIDATED);
        		if(apwList!=null && apwList.size()>0)
        		{
        			al = new ArrayList<Application>();
	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
	        		{
	        			ApplicationWorkflow aw = it.next();
	        			al.add(aw.getApplication());
	        		}
	        		portletState.setApplicationWorkFlowListing(apwList);
	        		portletState.setApplicationListing(al);
        		}else
        		{
        			portletState.setApplicationWorkFlowListing(null);
            		portletState.setApplicationListing(null);
        			portletState.addError(aReq, "There are no applications devalidated by your agency", portletState);
        		}
        	}
    		portletState.setCurrentTab(VIEW_TABS.LIST_DEVALIDATED_APPLICATIONS_AG);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        
        if(action.equalsIgnoreCase(EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_ONE.name()))
        {
        	//log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	if(this.managePortletState(portletState.getPortalUser().getId()))
        		createApplicationEUStepOne(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_TWO.name()))
        {
        	//log.info("CREATE_A_PORTAL_USER_STEP_TWO");
        	//Select Account type
        	String act = aReq.getParameter("act");
        	//log.info("act==" + act);
        	if(act!=null && act.equalsIgnoreCase("next"))
	        	createApplicationEUStepTwo(aReq, aRes, swpService, portletState);
        	else if(act!=null && act.equalsIgnoreCase("Add"))
        		createApplicationEUStepTwo(aReq, aRes, swpService, portletState);
        	else if(act!=null && act.equalsIgnoreCase("Clear"))
        		clearApplicationItems(aReq, aRes, swpService, portletState);
    		else if(act!=null && act.equalsIgnoreCase("backfromsteptwomakeapplication"))
        		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepone.jsp");
        		
        }
        if(action.equalsIgnoreCase(EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_THREE.name()))
        {
        	//log.info("CREATE_A_PORTAL_USER_STEP_THREE");
        	//Select Account type
        	UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(aReq);
        	String act = uploadRequest.getParameter("act");
        	//log.info("act===" + act);
        	if(act!=null && act.equalsIgnoreCase("next"))
        		createApplicationEUStepThree(aReq, aRes, swpService, portletState, uploadRequest);
        	else if(act!=null && act.equalsIgnoreCase("backfromstepthreemakeapplication"))
        		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/steptwo.jsp");
        		
        }
        if(action.equalsIgnoreCase(EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_FOUR.name()))
        {
        	//log.info("CREATE_A_PORTAL_USER_STEP_TWO");
        	//Select Account type
        	createApplicationEUStepFour(aReq, aRes, swpService, portletState);
        		
        }
        
        
        if(action.equalsIgnoreCase(AGENCY_ACTIONS.SIGN_ENDORSE_APPLICATION.name()))
        {
        	//log.info("NSA_ACTION");
        	handleEndorseApplicationStepTwo(aReq, aRes, swpService, portletState);
			 
        }
        

        if(action.equalsIgnoreCase(APPLIST_ACTIONS.HANDLE_ACTIONS_ON_LIST_APPLICATIONS.name()))
        {
        	//log.info("HANDLE ACTIONS ON APPLICATiON LISTING");
        	handleActionsOnListApplications(aReq, aRes, swpService, portletState);
        		
        }
        
        
        if(action.equalsIgnoreCase(APPLIST_ACTIONS.ACT_ON_APPLICATION.name()))
        {
        	//log.info("HANDLE ACTIONS ON ONE APPLICATiON");
        	handleActionsOnOneApplication(aReq, aRes, swpService, portletState);
        	
        }
        
        if(action.equalsIgnoreCase(APPLIST_ACTIONS.ACT_ON_UPLOAD_CERTIFICATE.name()))
        {
        	//log.info("HANDLE ACTIONS ON ONE APPLICATiON");
        	UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(aReq);
        	String act = uploadRequest.getParameter("act");
        	String actId = uploadRequest.getParameter("actId");
        	if(act!=null && act.equals("issuecertificateStepThree"))			//step 3 for certificate gen proceess //issueCertificateStepOne(aReq, aRes, swpService2, portletState);
				confirmPrint(uploadRequest, aRes, aReq, portletState, swpService, actId);
			else if(act!=null && act.equals("cancelissuestepthree"))			//step 3 for certificate gen proceess //issueCertificateStepOne(aReq, aRes, swpService2, portletState);
			{
				Certificate cert = portletState.getCertificateGenerated();
				//cert.setCertificateNo(null);
				cert.setSignature(null);
				cert.setCertificatePrinted(null);
				swpService.updateRecord(cert);
				
				new Util().pushAuditTrail(swpService, cert.getId().toString(), 
						ECIMSConstants.REJECT_CERTIFICATE, 
						portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/issuecertificate_steptwo.jsp");
			}
        	
        }
        
        

        if(action.equalsIgnoreCase(NSA_ACTIONS.SIGN_APPROVE_APPLICATION.name()))
        {
        	//log.info("HANDLE ACTIONS ON APPLICATiON LISTING");
        	approveApplicationWithToken(aReq, aRes, swpService, portletState);
        		
        }

        if(action.equalsIgnoreCase(VIEW_TABS.CREATE_AN_APPLICATION_EU.name()))
        {
        	portletState.reinitializeForNewApplication(portletState);
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
        		portletState.setAgencyApplicant(null);
        	}
        	else
        	{
        		portletState.setApplicant(null);
        		portletState.setAgencyApplicant(portletState.getPortalUser());
        	}
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepone.jsp");
        	portletState.setCurrentTab(VIEW_TABS.CREATE_AN_APPLICATION_EU);
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_ALL_APPLICATIONS_EU.name()))
        {
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
        		portletState.setAgencyApplicant(null);
        		if(portletState.getApplicant()!=null)
        			portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByApplicant(portletState.getApplicant()));
        		else
        			portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByAgencyApplicant(portletState.getAgencyApplicant()));
        	}
        	else
        	{
        		portletState.setApplicant(null);
        		portletState.setAgencyApplicant(portletState.getPortalUser());
        		portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByAgencyApplicant(portletState.getAgencyApplicant()));
        	}
        	
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_ALL_APPLICATIONS_EU);
        	if(portletState.getApplicationListing()==null || (portletState.getApplicationListing()!=null && portletState.getApplicationListing().size()==0))
        	{
        		portletState.addError(aReq, "You have not created any EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_FORWARDED_APPLICATIONS_NSA.name()))
        {
        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        			getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(portletState.getPortalUser().getRoleType().getId(), 
							ApplicationStatus.APPLICATION_STATUS_FORWARDED, false);
//        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
//        			getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_FORWARDED, false, 
//							portletState.getPortalUser().getAgency());
        	portletState.setApplicationWorkFlowListing(appList);
        	//portletState.setApplicationFlags(portletState.getApplicationManagementPortletUtil().getApplicationFlags());
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_FORWARDED_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no EUC request applications forwarded yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_APPROVED_APPLICATIONS_EU.name()))
        {
//        	portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
        	
        	
        	
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
        		portletState.setAgencyApplicant(null);
        		portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByApplicantAndStatus(
        				portletState.getApplicant(), 
        				new ApplicationStatus[]{ApplicationStatus.APPLICATION_STATUS_APPROVED, ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED}));
        	}
        	else
        	{
        		portletState.setApplicant(null);
        		portletState.setAgencyApplicant(portletState.getPortalUser());
        		portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByAgencyApplicantAndStatus(portletState.getAgencyApplicant(), ApplicationStatus.APPLICATION_STATUS_APPROVED));
        	}
        	
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_APPLICATIONS_EU);
        	if(portletState.getApplicationListing()==null || (portletState.getApplicationListing()!=null && portletState.getApplicationListing().size()==0))
        	{
        		portletState.addError(aReq, "You do not have any approved EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_APPROVED_APPLICATIONS_NSA.name()))
        {
        	//portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_APPROVED));
        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        			getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(portletState.getPortalUser().getRoleType().getId(), 
							ApplicationStatus.APPLICATION_STATUS_APPROVED, false);
        	portletState.setApplicationWorkFlowListing(appList);
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no approved EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_PENDING_APPLICATIONS_EU.name()))
        {
        	String[] a = {ApplicationStatus.APPLICATION_STATUS_APPROVED.getValue(), 
        				ApplicationStatus.APPLICATION_STATUS_ATTACHMENT_DEVALIDATED.getValue(), 
        				ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED.getValue(), 
        				ApplicationStatus.APPLICATION_STATUS_REJECTED.getValue()};
        	
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().
            			getApplicationsByApplicantAndExceptStatus
            			(portletState.getApplicant(), a));
            	portletState.setAgencyApplicant(null);
        	}
        	else
        	{
        		portletState.setAgencyApplicant(portletState.getPortalUser());
        		portletState.setApplicant(null);
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().
            			getApplicationsByAgencyApplicantAndExceptStatus
            			(portletState.getAgencyApplicant(), a));
        	}
        	
        	
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_EU);
        	
        	if(portletState.getApplicationListing()==null || (portletState.getApplicationListing()!=null && portletState.getApplicationListing().size()==0))
        	{
        		portletState.addError(aReq, "You have not created any EUC request applications yet.", portletState);
        	}else
        	{
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA.name()))
        {
        	//portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_CREATED));
//        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
//					getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
//							portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_CREATED, false, portletState.getPortalUser().getAgency());
        	//log.info("----------------------------" + "44:");
        	Collection<ApplicationWorkflow> appList = null;
        	if(portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_FORWARD_APPLICATION) && 
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
				//log.info("----------------------------" + "45:");
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
        	portletState.setApplicationWorkFlowListing(appList);
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no pending EUC request applications at the moment.", portletState);
        	}
        	
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_REJECTED_APPLICATIONS_EU.name()))
        {
        	
        	
        	
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByApplicantAndStatus(portletState.getApplicant(), ApplicationStatus.APPLICATION_STATUS_REJECTED));
            	portletState.setAgencyApplicant(null);
        	}
        	else
        	{
        		portletState.setApplicant(null);
        		portletState.setAgencyApplicant(portletState.getPortalUser());
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByAgencyApplicantAndStatus(portletState.getAgencyApplicant(), ApplicationStatus.APPLICATION_STATUS_REJECTED));
        	}
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_REJECTED_APPLICATIONS_EU);
        	if(portletState.getApplicationListing()==null || (portletState.getApplicationListing()!=null && portletState.getApplicationListing().size()==0))
        	{
        		portletState.addError(aReq, "You do not have any rejected EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_REJECTED_APPLICATIONS_NSA.name()))
        {
        	//portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED));
        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        			getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(portletState.getPortalUser().getRoleType().getId(), 
							ApplicationStatus.APPLICATION_STATUS_REJECTED, false);
        	portletState.setApplicationWorkFlowListing(appList);

        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_REJECTED_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no rejected EUC request applications yet.", portletState);
        	}
        }
//        if(action.equalsIgnoreCase(VIEW_TABS.LIST_DISPUTED_APPLICATIONS_NSA.name()))
//        {
//        	//portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED));
//        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
//        			getApplicationWorkFlowByReceipientRoleIdAndStatus(portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_DISPUTED);
//        	portletState.setApplicationWorkFlowListing(appList);
//        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
//        	portletState.setCurrentTab(VIEW_TABS.LIST_DISPUTED_APPLICATIONS_NSA);
//        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
//        	{
//        		portletState.addError(aReq, "There are no EUC request currently being disputed applications yet.", portletState);
//        	}
//        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_NSA.name()))
        {
//        	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED));
        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        			getApplicationWorkFlowByReceipientRoleIdAndStatus(portletState.getPortalUser().getRoleType().getId(), 
        					ApplicationStatus.APPLICATION_STATUS_ENDORSED, portletState.getPortalUser().getAgency());
//        			.
//					getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
//							portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_ENDORSED, 
//							false, portletState.getPortalUser().getAgency());
        	portletState.setApplicationWorkFlowListing(appList);
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no endorsed EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_NSA.name()))
        {
        	//portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED));
        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        			getApplicationWorkflowByStatus(ApplicationStatus.APPLICATION_STATUS_DISENDORSED);
//        			getApplicationWorkFlowByReceipientRoleIdAndStatus(portletState.getPortalUser().getRoleType().getId(), 
//        					ApplicationStatus.APPLICATION_STATUS_DISENDORSED, portletState.getPortalUser().getAgency());
//        			getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
//							portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_DISENDORSED, 
//							false, portletState.getPortalUser().getAgency());
        	portletState.setApplicationWorkFlowListing(appList);
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no disendorsed EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_APPROVED_AWAIT_ISSUANCE_APPLICATIONS_EU.name()))
        {
        	String status[] = {ApplicationStatus.APPLICATION_STATUS_REJECTED.getValue(), ApplicationStatus.APPLICATION_STATUS_APPROVED.getValue(), ApplicationStatus.APPLICATION_STATUS_CREATED.getValue(), };
        	
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByApplicantAndExceptStatus(portletState.getApplicant(), status));
            	
            	portletState.setAgencyApplicant(null);
        	}
        	else
        	{
        		portletState.setApplicant(null);
        		portletState.setAgencyApplicant(portletState.getPortalUser());
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByAgencyApplicantAndExceptStatus(portletState.getAgencyApplicant(), status));
            	
        	}
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_AWAIT_ISSUANCE_APPLICATIONS_EU);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "You do not have any EUC requests awaiting issuance at the moment.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_APPROVED_AWAIT_ISSUANCE_APPLICATIONS_NSA.name()))
        {
        	String status[] = {ApplicationStatus.APPLICATION_STATUS_REJECTED.getValue(), ApplicationStatus.APPLICATION_STATUS_APPROVED.getValue(), ApplicationStatus.APPLICATION_STATUS_CREATED.getValue(), };
        	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByExceptStatus(status));
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_AWAIT_ISSUANCE_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no EUC requests awaiting issuance at the moment.", portletState);
        	}
        }
        
        
        
//        if(action.equalsIgnoreCase(VIEW_TABS.LIST_DISPUTED_APPLICATIONS_AG.name()))
//        {
//        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
//        	{
//        		ArrayList<Application> al = null;
//        		Collection<ApplicationWorkflow> apwList = 
//        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
//        				getApplicationWorkFlowBySourceRoleIdAndStatus(portletState.getPortalUser().getRoleType().getId(), ApplicationStatus.APPLICATION_STATUS_DISPUTED);
//        		if(apwList!=null && apwList.size()>0)
//        		{
//        			al = new ArrayList<Application>();
//	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
//	        		{
//	        			ApplicationWorkflow aw = it.next();
//	        			al.add(aw.getApplication());
//	        		}
//	        		portletState.setApplicationWorkFlowListing(apwList);
//	        		portletState.setApplicationListing(al);
//	        		portletState.setCurrentTab(VIEW_TABS.LIST_DISPUTED_APPLICATIONS_AG);
//        		}else
//        		{
//        			portletState.addError(aReq, "There are no applications disputed by your agency", portletState);
//        		}
//        	}
//        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_PENDING_APPLICATIONS_AG.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
        	{
        		ArrayList<Application> al = null;
        		Collection<ApplicationWorkflow> apwList = 
        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        				getApplicationWorkFlowByReceipientRoleIdAndNotWorkedOn(portletState.getPortalUser().getRoleType().getId(), false, portletState.getPortalUser().getAgency());
        		if(apwList!=null && apwList.size()>0)
        		{
        			al = new ArrayList<Application>();
	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
	        		{
	        			ApplicationWorkflow aw = it.next();
	        			al.add(aw.getApplication());
	        		}
	        		portletState.setApplicationWorkFlowListing(apwList);
	        		portletState.setApplicationListing(al);
        		}else
        		{
        			portletState.setApplicationWorkFlowListing(null);
            		portletState.setApplicationListing(null);
        			portletState.addError(aReq, "There are no pending applications available for you to work on", portletState);
        		}
        	}
        	
    		portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_AG);
    		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.CREATE_AN_APPLICATION_AG.name()))
        {
        	portletState.setCurrentTab(VIEW_TABS.CREATE_AN_APPLICATION_AG);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepone.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_MY_AGENCY_APPLICATIONS_AG.name()))
        {
			Collection<ApplicationWorkflow> apwList = null;
			if(!portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER))
			{
				apwList = 
    				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
    				getApplicationWorkFlowByAgency(
    						portletState.getPortalUser().getAgency().getId());
			}else
			{
				apwList = 
    				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
    				getApplicationWorkFlowByApplicant(
    						portletState.getApplicant().getId());
			}
        	
        	
    		if(apwList!=null && apwList.size()>0)
    		{
    			//log.info("apwListsize ===" + apwList.size());
    			ArrayList<Application> al = new ArrayList<Application>();
        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
        		{
        			ApplicationWorkflow aw = it.next();
        			al.add(aw.getApplication());
        		}
        		portletState.setApplicationWorkFlowListing(apwList);
        		portletState.setApplicationListing(al);
    		}else
    		{
    			portletState.setApplicationWorkFlowListing(null);
        		portletState.setApplicationListing(null);
        		if(!portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER))
    			{
        			portletState.addError(aReq, "There are no applications created by your agency on this system", portletState);
    			}else
    			{
    				portletState.addError(aReq, "You do not have any applications created on this system", portletState);
    			}
    		}
        	portletState.setCurrentTab(VIEW_TABS.LIST_MY_AGENCY_APPLICATIONS_AG);
    		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_AG.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
        	{
        		ArrayList<Application> al = null;
        		Collection<ApplicationWorkflow> apwList = 
        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        				getApplicationWorkFlowBySourceAgencyIdAndStatus(
        						portletState.getPortalUser().getAgency().getId(), 
        						ApplicationStatus.APPLICATION_STATUS_ENDORSED);
        		if(apwList!=null && apwList.size()>0)
        		{
        			al = new ArrayList<Application>();
	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
	        		{
	        			ApplicationWorkflow aw = it.next();
	        			al.add(aw.getApplication());
	        		}
	        		portletState.setApplicationWorkFlowListing(apwList);
	        		portletState.setApplicationListing(al);
        		}else
        		{
        			portletState.setApplicationWorkFlowListing(null);
            		portletState.setApplicationListing(null);
        			portletState.addError(aReq, "There are no applications endorsed by your agency", portletState);
        		}
        	}

    		portletState.setCurrentTab(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_AG);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_AG.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
        	{
        		ArrayList<Application> al = null;
        		Collection<ApplicationWorkflow> apwList = 
        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        				getApplicationWorkFlowBySourceAgencyIdAndStatus(
        						portletState.getPortalUser().getAgency().getId(), ApplicationStatus.APPLICATION_STATUS_DISENDORSED);
        		if(apwList!=null && apwList.size()>0)
        		{
        			al = new ArrayList<Application>();
	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
	        		{
	        			ApplicationWorkflow aw = it.next();
	        			al.add(aw.getApplication());
	        		}
	        		portletState.setApplicationWorkFlowListing(apwList);
	        		portletState.setApplicationListing(al);
        		}else
        		{
        			portletState.setApplicationWorkFlowListing(null);
            		portletState.setApplicationListing(null);
        			portletState.addError(aReq, "There are no applications disendorsed by your agency", portletState);
        		}
        	}

    		portletState.setCurrentTab(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_AG);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_FLAGGED_APPLICATIONS_AG.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
        	{
        		ArrayList<Application> al = null;
        		Collection<ApplicationWorkflow> apwList = 
        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        				getApplicationWorkFlowBySourceAgencyIdAndStatus(portletState.getPortalUser().getAgency().getId(), ApplicationStatus.APPLICATION_STATUS_FLAGGED);
        		if(apwList!=null && apwList.size()>0)
        		{
        			al = new ArrayList<Application>();
	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
	        		{
	        			ApplicationWorkflow aw = it.next();
	        			al.add(aw.getApplication());
	        		}
	        		portletState.setApplicationWorkFlowListing(apwList);
	        		portletState.setApplicationListing(al);
        		}else
        		{
        			portletState.setApplicationWorkFlowListing(null);
            		portletState.setApplicationListing(null);
        			portletState.addError(aReq, "There are no applications flagged by your agency", portletState);
        		}
        	}
    		portletState.setCurrentTab(VIEW_TABS.LIST_FLAGGED_APPLICATIONS_AG);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_DEVALIDATED_APPLICATIONS_AG.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
        	{
        		ArrayList<Application> al = null;
        		Collection<ApplicationWorkflow> apwList = 
        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        				getApplicationWorkFlowBySourceAgencyIdAndStatus(portletState.getPortalUser().getAgency().getId(), ApplicationStatus.APPLICATION_STATUS_ATTACHMENT_DEVALIDATED);
        		if(apwList!=null && apwList.size()>0)
        		{
        			al = new ArrayList<Application>();
	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
	        		{
	        			ApplicationWorkflow aw = it.next();
	        			al.add(aw.getApplication());
	        		}
	        		portletState.setApplicationWorkFlowListing(apwList);
	        		portletState.setApplicationListing(al);
        		}else
        		{
        			portletState.setApplicationWorkFlowListing(null);
            		portletState.setApplicationListing(null);
        			portletState.addError(aReq, "There are no applications devalidated by your agency", portletState);
        		}
        	}
    		portletState.setCurrentTab(VIEW_TABS.LIST_DEVALIDATED_APPLICATIONS_AG);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        
        if(action.equalsIgnoreCase(EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_ONE.name()))
        {
        	//log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	if(this.managePortletState(portletState.getPortalUser().getId()))
        		createApplicationEUStepOne(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_TWO.name()))
        {
        	//log.info("CREATE_A_PORTAL_USER_STEP_TWO");
        	//Select Account type
        	String act = aReq.getParameter("act");
        	//log.info("act==" + act);
        	if(act!=null && act.equalsIgnoreCase("next"))
	        	createApplicationEUStepTwo(aReq, aRes, swpService, portletState);
        	else if(act!=null && act.equalsIgnoreCase("Add"))
        		createApplicationEUStepTwo(aReq, aRes, swpService, portletState);
        	else if(act!=null && act.equalsIgnoreCase("Clear"))
        		clearApplicationItems(aReq, aRes, swpService, portletState);
    		else if(act!=null && act.equalsIgnoreCase("backfromsteptwomakeapplication"))
        		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepone.jsp");
        		
        }
        if(action.equalsIgnoreCase(EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_THREE.name()))
        {
        	//log.info("CREATE_A_PORTAL_USER_STEP_THREE");
        	//Select Account type
        	UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(aReq);
        	String act = uploadRequest.getParameter("act");
        	//log.info("act===" + act);
        	if(act!=null && act.equalsIgnoreCase("next"))
        		createApplicationEUStepThree(aReq, aRes, swpService, portletState, uploadRequest);
        	else if(act!=null && act.equalsIgnoreCase("backfromstepthreemakeapplication"))
        		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/steptwo.jsp");
        		
        }
        if(action.equalsIgnoreCase(EU_ACTIONS.CREATE_AN_APPLICATION_EU_STEP_FOUR.name()))
        {
        	//log.info("CREATE_A_PORTAL_USER_STEP_TWO");
        	//Select Account type
        	createApplicationEUStepFour(aReq, aRes, swpService, portletState);
        		
        }
        
        
        if(action.equalsIgnoreCase(AGENCY_ACTIONS.SIGN_ENDORSE_APPLICATION.name()))
        {
        	//log.info("NSA_ACTION");
        	handleEndorseApplicationStepTwo(aReq, aRes, swpService, portletState);
			 
        }
        

        if(action.equalsIgnoreCase(APPLIST_ACTIONS.HANDLE_ACTIONS_ON_LIST_APPLICATIONS.name()))
        {
        	//log.info("HANDLE ACTIONS ON APPLICATiON LISTING");
        	handleActionsOnListApplications(aReq, aRes, swpService, portletState);
        		
        }
        
        
        if(action.equalsIgnoreCase(APPLIST_ACTIONS.ACT_ON_APPLICATION.name()))
        {
        	//log.info("HANDLE ACTIONS ON ONE APPLICATiON");
        	handleActionsOnOneApplication(aReq, aRes, swpService, portletState);
        	
        }
        
        if(action.equalsIgnoreCase(APPLIST_ACTIONS.ACT_ON_UPLOAD_CERTIFICATE.name()))
        {
        	//log.info("HANDLE ACTIONS ON ONE APPLICATiON");
        	UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(aReq);
        	String act = uploadRequest.getParameter("act");
        	String actId = uploadRequest.getParameter("actId");
        	if(act!=null && act.equals("issuecertificateStepThree"))			//step 3 for certificate gen proceess //issueCertificateStepOne(aReq, aRes, swpService2, portletState);
				confirmPrint(uploadRequest, aRes, aReq, portletState, swpService, actId);
			else if(act!=null && act.equals("cancelissuestepthree"))			//step 3 for certificate gen proceess //issueCertificateStepOne(aReq, aRes, swpService2, portletState);
			{
				Certificate cert = portletState.getCertificateGenerated();
				//cert.setCertificateNo(null);
				cert.setSignature(null);
				cert.setCertificatePrinted(null);
				swpService.updateRecord(cert);
				
				new Util().pushAuditTrail(swpService, cert.getId().toString(), 
						ECIMSConstants.REJECT_CERTIFICATE, 
						portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
				aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/generatecertificate/issuecertificate_steptwo.jsp");
			}
        	
        }
        
        

        if(action.equalsIgnoreCase(NSA_ACTIONS.SIGN_APPROVE_APPLICATION.name()))
        {
        	//log.info("HANDLE ACTIONS ON APPLICATiON LISTING");
        	approveApplicationWithToken(aReq, aRes, swpService, portletState);
        		
        }

        if(action.equalsIgnoreCase(VIEW_TABS.CREATE_AN_APPLICATION_EU.name()))
        {
        	portletState.reinitializeForNewApplication(portletState);
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
        		portletState.setAgencyApplicant(null);
        	}
        	else
        	{
        		portletState.setApplicant(null);
        		portletState.setAgencyApplicant(portletState.getPortalUser());
        	}
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepone.jsp");
        	portletState.setCurrentTab(VIEW_TABS.CREATE_AN_APPLICATION_EU);
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_ALL_APPLICATIONS_EU.name()))
        {
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
        		portletState.setAgencyApplicant(null);
        		if(portletState.getApplicant()!=null)
        			portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByApplicant(portletState.getApplicant()));
        		else
        			portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByAgencyApplicant(portletState.getAgencyApplicant()));
        	}
        	else
        	{
        		portletState.setApplicant(null);
        		portletState.setAgencyApplicant(portletState.getPortalUser());
        		portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByAgencyApplicant(portletState.getAgencyApplicant()));
        	}
        	
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_ALL_APPLICATIONS_EU);
        	if(portletState.getApplicationListing()==null || (portletState.getApplicationListing()!=null && portletState.getApplicationListing().size()==0))
        	{
        		portletState.addError(aReq, "You have not created any EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_FORWARDED_APPLICATIONS_NSA.name()))
        {
        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        			getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(portletState.getPortalUser().getRoleType().getId(), 
							ApplicationStatus.APPLICATION_STATUS_FORWARDED, false);
//        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
//        			getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_FORWARDED, false, 
//							portletState.getPortalUser().getAgency());
        	portletState.setApplicationWorkFlowListing(appList);
        	//portletState.setApplicationFlags(portletState.getApplicationManagementPortletUtil().getApplicationFlags());
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_FORWARDED_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no EUC request applications forwarded yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_APPROVED_APPLICATIONS_EU.name()))
        {
//        	portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
        	
        	
        	
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
        		portletState.setAgencyApplicant(null);
        		portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByApplicantAndStatus(
        				portletState.getApplicant(), 
        				new ApplicationStatus[]{ApplicationStatus.APPLICATION_STATUS_APPROVED, ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED}));
        	}
        	else
        	{
        		portletState.setApplicant(null);
        		portletState.setAgencyApplicant(portletState.getPortalUser());
        		portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByAgencyApplicantAndStatus(portletState.getAgencyApplicant(), ApplicationStatus.APPLICATION_STATUS_APPROVED));
        	}
        	
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_APPLICATIONS_EU);
        	if(portletState.getApplicationListing()==null || (portletState.getApplicationListing()!=null && portletState.getApplicationListing().size()==0))
        	{
        		portletState.addError(aReq, "You do not have any approved EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_APPROVED_APPLICATIONS_NSA.name()))
        {
        	//portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_APPROVED));
        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        			getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(portletState.getPortalUser().getRoleType().getId(), 
							ApplicationStatus.APPLICATION_STATUS_APPROVED, false);
        	portletState.setApplicationWorkFlowListing(appList);
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no approved EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_PENDING_APPLICATIONS_EU.name()))
        {
        	String[] a = {ApplicationStatus.APPLICATION_STATUS_APPROVED.getValue(), 
        				ApplicationStatus.APPLICATION_STATUS_ATTACHMENT_DEVALIDATED.getValue(), 
        				ApplicationStatus.APPLICATION_STATUS_CERTIFICATE_ISSUED.getValue(), 
        				ApplicationStatus.APPLICATION_STATUS_REJECTED.getValue()};
        	
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().
            			getApplicationsByApplicantAndExceptStatus
            			(portletState.getApplicant(), a));
            	portletState.setAgencyApplicant(null);
        	}
        	else
        	{
        		portletState.setAgencyApplicant(portletState.getPortalUser());
        		portletState.setApplicant(null);
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().
            			getApplicationsByAgencyApplicantAndExceptStatus
            			(portletState.getAgencyApplicant(), a));
        	}
        	
        	
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_EU);
        	
        	if(portletState.getApplicationListing()==null || (portletState.getApplicationListing()!=null && portletState.getApplicationListing().size()==0))
        	{
        		portletState.addError(aReq, "You have not created any EUC request applications yet.", portletState);
        	}else
        	{
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA.name()))
        {
        	//portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_CREATED));
//        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
//					getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
//							portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_CREATED, false, portletState.getPortalUser().getAgency());
        	//log.info("----------------------------" + "44:");
        	Collection<ApplicationWorkflow> appList = null;
        	if(portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_FORWARD_APPLICATION) && 
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
				//log.info("----------------------------" + "45:");
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
        	portletState.setApplicationWorkFlowListing(appList);
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no pending EUC request applications at the moment.", portletState);
        	}
        	
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_REJECTED_APPLICATIONS_EU.name()))
        {
        	
        	
        	
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByApplicantAndStatus(portletState.getApplicant(), ApplicationStatus.APPLICATION_STATUS_REJECTED));
            	portletState.setAgencyApplicant(null);
        	}
        	else
        	{
        		portletState.setApplicant(null);
        		portletState.setAgencyApplicant(portletState.getPortalUser());
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByAgencyApplicantAndStatus(portletState.getAgencyApplicant(), ApplicationStatus.APPLICATION_STATUS_REJECTED));
        	}
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_REJECTED_APPLICATIONS_EU);
        	if(portletState.getApplicationListing()==null || (portletState.getApplicationListing()!=null && portletState.getApplicationListing().size()==0))
        	{
        		portletState.addError(aReq, "You do not have any rejected EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_REJECTED_APPLICATIONS_NSA.name()))
        {
        	//portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED));
        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        			getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(portletState.getPortalUser().getRoleType().getId(), 
							ApplicationStatus.APPLICATION_STATUS_REJECTED, false);
        	portletState.setApplicationWorkFlowListing(appList);

        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_REJECTED_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no rejected EUC request applications yet.", portletState);
        	}
        }
//        if(action.equalsIgnoreCase(VIEW_TABS.LIST_DISPUTED_APPLICATIONS_NSA.name()))
//        {
//        	//portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED));
//        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
//        			getApplicationWorkFlowByReceipientRoleIdAndStatus(portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_DISPUTED);
//        	portletState.setApplicationWorkFlowListing(appList);
//        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
//        	portletState.setCurrentTab(VIEW_TABS.LIST_DISPUTED_APPLICATIONS_NSA);
//        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
//        	{
//        		portletState.addError(aReq, "There are no EUC request currently being disputed applications yet.", portletState);
//        	}
//        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_NSA.name()))
        {
//        	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED));
        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        			getApplicationWorkFlowByReceipientRoleIdAndStatus(portletState.getPortalUser().getRoleType().getId(), 
        					ApplicationStatus.APPLICATION_STATUS_ENDORSED, portletState.getPortalUser().getAgency());
//        			.
//					getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
//							portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_ENDORSED, 
//							false, portletState.getPortalUser().getAgency());
        	portletState.setApplicationWorkFlowListing(appList);
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no endorsed EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_NSA.name()))
        {
        	//portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByStatus(ApplicationStatus.APPLICATION_STATUS_REJECTED));
        	Collection<ApplicationWorkflow> appList = (Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        			getApplicationWorkflowByStatus(ApplicationStatus.APPLICATION_STATUS_DISENDORSED);
//        			getApplicationWorkFlowByReceipientRoleIdAndStatus(portletState.getPortalUser().getRoleType().getId(), 
//        					ApplicationStatus.APPLICATION_STATUS_DISENDORSED, portletState.getPortalUser().getAgency());
//        			getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
//							portletState.getPortalUser().getRoleType().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_DISENDORSED, 
//							false, portletState.getPortalUser().getAgency());
        	portletState.setApplicationWorkFlowListing(appList);
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no disendorsed EUC request applications yet.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_APPROVED_AWAIT_ISSUANCE_APPLICATIONS_EU.name()))
        {
        	String status[] = {ApplicationStatus.APPLICATION_STATUS_REJECTED.getValue(), ApplicationStatus.APPLICATION_STATUS_APPROVED.getValue(), ApplicationStatus.APPLICATION_STATUS_CREATED.getValue(), };
        	
        	if(portletState.getPortalUser().getAgency()==null)
        	{
        		portletState.setApplicant(portletState.getApplicationManagementPortletUtil().getApplicantOfPortalUser(portletState.getPortalUser()));
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByApplicantAndExceptStatus(portletState.getApplicant(), status));
            	
            	portletState.setAgencyApplicant(null);
        	}
        	else
        	{
        		portletState.setApplicant(null);
        		portletState.setAgencyApplicant(portletState.getPortalUser());
            	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByAgencyApplicantAndExceptStatus(portletState.getAgencyApplicant(), status));
            	
        	}
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_AWAIT_ISSUANCE_APPLICATIONS_EU);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "You do not have any EUC requests awaiting issuance at the moment.", portletState);
        	}
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_APPROVED_AWAIT_ISSUANCE_APPLICATIONS_NSA.name()))
        {
        	String status[] = {ApplicationStatus.APPLICATION_STATUS_REJECTED.getValue(), ApplicationStatus.APPLICATION_STATUS_APPROVED.getValue(), ApplicationStatus.APPLICATION_STATUS_CREATED.getValue(), };
        	portletState.setApplicationListing(portletState.getApplicationManagementUtil().getApplicationsByExceptStatus(status));
        	portletState.setApplicationinAppFlags(portletState.getApplicationManagementPortletUtil().getApplicationinAppFlags());
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_nsa.jsp");
        	portletState.setCurrentTab(VIEW_TABS.LIST_APPROVED_AWAIT_ISSUANCE_APPLICATIONS_NSA);
        	if(portletState.getApplicationWorkFlowListing()==null || (portletState.getApplicationWorkFlowListing()!=null && portletState.getApplicationWorkFlowListing().size()==0))
        	{
        		portletState.addError(aReq, "There are no EUC requests awaiting issuance at the moment.", portletState);
        	}
        }
        
        
        
//        if(action.equalsIgnoreCase(VIEW_TABS.LIST_DISPUTED_APPLICATIONS_AG.name()))
//        {
//        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
//        	{
//        		ArrayList<Application> al = null;
//        		Collection<ApplicationWorkflow> apwList = 
//        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
//        				getApplicationWorkFlowBySourceRoleIdAndStatus(portletState.getPortalUser().getRoleType().getId(), ApplicationStatus.APPLICATION_STATUS_DISPUTED);
//        		if(apwList!=null && apwList.size()>0)
//        		{
//        			al = new ArrayList<Application>();
//	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
//	        		{
//	        			ApplicationWorkflow aw = it.next();
//	        			al.add(aw.getApplication());
//	        		}
//	        		portletState.setApplicationWorkFlowListing(apwList);
//	        		portletState.setApplicationListing(al);
//	        		portletState.setCurrentTab(VIEW_TABS.LIST_DISPUTED_APPLICATIONS_AG);
//        		}else
//        		{
//        			portletState.addError(aReq, "There are no applications disputed by your agency", portletState);
//        		}
//        	}
//        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_PENDING_APPLICATIONS_AG.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
        	{
        		ArrayList<Application> al = null;
        		Collection<ApplicationWorkflow> apwList = 
        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        				getApplicationWorkFlowByReceipientRoleIdAndNotWorkedOn(portletState.getPortalUser().getRoleType().getId(), false, portletState.getPortalUser().getAgency());
        		if(apwList!=null && apwList.size()>0)
        		{
        			al = new ArrayList<Application>();
	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
	        		{
	        			ApplicationWorkflow aw = it.next();
	        			al.add(aw.getApplication());
	        		}
	        		portletState.setApplicationWorkFlowListing(apwList);
	        		portletState.setApplicationListing(al);
        		}else
        		{
        			portletState.setApplicationWorkFlowListing(null);
            		portletState.setApplicationListing(null);
        			portletState.addError(aReq, "There are no pending applications available for you to work on", portletState);
        		}
        	}
        	
    		portletState.setCurrentTab(VIEW_TABS.LIST_PENDING_APPLICATIONS_AG);
    		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.CREATE_AN_APPLICATION_AG.name()))
        {
        	portletState.setCurrentTab(VIEW_TABS.CREATE_AN_APPLICATION_AG);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/makeanapplication/stepone.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_MY_AGENCY_APPLICATIONS_AG.name()))
        {
			Collection<ApplicationWorkflow> apwList = null;
			if(!portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER))
			{
				apwList = 
    				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
    				getApplicationWorkFlowByAgency(
    						portletState.getPortalUser().getAgency().getId());
			}else
			{
				apwList = 
    				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
    				getApplicationWorkFlowByApplicant(
    						portletState.getApplicant().getId());
			}
        	
        	
    		if(apwList!=null && apwList.size()>0)
    		{
    			//log.info("apwListsize ===" + apwList.size());
    			ArrayList<Application> al = new ArrayList<Application>();
        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
        		{
        			ApplicationWorkflow aw = it.next();
        			al.add(aw.getApplication());
        		}
        		portletState.setApplicationWorkFlowListing(apwList);
        		portletState.setApplicationListing(al);
    		}else
    		{
    			portletState.setApplicationWorkFlowListing(null);
        		portletState.setApplicationListing(null);
        		if(!portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER))
    			{
        			portletState.addError(aReq, "There are no applications created by your agency on this system", portletState);
    			}else
    			{
    				portletState.addError(aReq, "You do not have any applications created on this system", portletState);
    			}
    		}
        	portletState.setCurrentTab(VIEW_TABS.LIST_MY_AGENCY_APPLICATIONS_AG);
    		aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_AG.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
        	{
        		ArrayList<Application> al = null;
        		Collection<ApplicationWorkflow> apwList = 
        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        				getApplicationWorkFlowBySourceAgencyIdAndStatus(
        						portletState.getPortalUser().getAgency().getId(), 
        						ApplicationStatus.APPLICATION_STATUS_ENDORSED);
        		if(apwList!=null && apwList.size()>0)
        		{
        			al = new ArrayList<Application>();
	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
	        		{
	        			ApplicationWorkflow aw = it.next();
	        			al.add(aw.getApplication());
	        		}
	        		portletState.setApplicationWorkFlowListing(apwList);
	        		portletState.setApplicationListing(al);
        		}else
        		{
        			portletState.setApplicationWorkFlowListing(null);
            		portletState.setApplicationListing(null);
        			portletState.addError(aReq, "There are no applications endorsed by your agency", portletState);
        		}
        	}

    		portletState.setCurrentTab(VIEW_TABS.LIST_ENDORSED_APPLICATIONS_AG);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_AG.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
        	{
        		ArrayList<Application> al = null;
        		Collection<ApplicationWorkflow> apwList = 
        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        				getApplicationWorkFlowBySourceAgencyIdAndStatus(
        						portletState.getPortalUser().getAgency().getId(), ApplicationStatus.APPLICATION_STATUS_DISENDORSED);
        		if(apwList!=null && apwList.size()>0)
        		{
        			al = new ArrayList<Application>();
	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
	        		{
	        			ApplicationWorkflow aw = it.next();
	        			al.add(aw.getApplication());
	        		}
	        		portletState.setApplicationWorkFlowListing(apwList);
	        		portletState.setApplicationListing(al);
        		}else
        		{
        			portletState.setApplicationWorkFlowListing(null);
            		portletState.setApplicationListing(null);
        			portletState.addError(aReq, "There are no applications disendorsed by your agency", portletState);
        		}
        	}

    		portletState.setCurrentTab(VIEW_TABS.LIST_DISENDORSED_APPLICATIONS_AG);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_FLAGGED_APPLICATIONS_AG.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
        	{
        		ArrayList<Application> al = null;
        		Collection<ApplicationWorkflow> apwList = 
        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        				getApplicationWorkFlowBySourceAgencyIdAndStatus(portletState.getPortalUser().getAgency().getId(), ApplicationStatus.APPLICATION_STATUS_FLAGGED);
        		if(apwList!=null && apwList.size()>0)
        		{
        			al = new ArrayList<Application>();
	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
	        		{
	        			ApplicationWorkflow aw = it.next();
	        			al.add(aw.getApplication());
	        		}
	        		portletState.setApplicationWorkFlowListing(apwList);
	        		portletState.setApplicationListing(al);
        		}else
        		{
        			portletState.setApplicationWorkFlowListing(null);
            		portletState.setApplicationListing(null);
        			portletState.addError(aReq, "There are no applications flagged by your agency", portletState);
        		}
        	}
    		portletState.setCurrentTab(VIEW_TABS.LIST_FLAGGED_APPLICATIONS_AG);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.LIST_DEVALIDATED_APPLICATIONS_AG.name()))
        {
        	if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getRole().getName().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP))
        	{
        		ArrayList<Application> al = null;
        		Collection<ApplicationWorkflow> apwList = 
        				(Collection<ApplicationWorkflow>)portletState.getApplicationManagementPortletUtil().
        				getApplicationWorkFlowBySourceAgencyIdAndStatus(portletState.getPortalUser().getAgency().getId(), ApplicationStatus.APPLICATION_STATUS_ATTACHMENT_DEVALIDATED);
        		if(apwList!=null && apwList.size()>0)
        		{
        			al = new ArrayList<Application>();
	        		for(Iterator<ApplicationWorkflow> it = apwList.iterator(); it.hasNext();)
	        		{
	        			ApplicationWorkflow aw = it.next();
	        			al.add(aw.getApplication());
	        		}
	        		portletState.setApplicationWorkFlowListing(apwList);
	        		portletState.setApplicationListing(al);
        		}else
        		{
        			portletState.setApplicationWorkFlowListing(null);
            		portletState.setApplicationListing(null);
        			portletState.addError(aReq, "There are no applications devalidated by your agency", portletState);
        		}
        	}
    		portletState.setCurrentTab(VIEW_TABS.LIST_DEVALIDATED_APPLICATIONS_AG);
        	aRes.setRenderParameter("jspPage", "/html/applicationmanagementportlet/listing/applicationlisting_agency.jsp");
        }
        
	}

}
