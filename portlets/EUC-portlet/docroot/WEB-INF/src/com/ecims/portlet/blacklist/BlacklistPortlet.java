package com.ecims.portlet.blacklist;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
import org.json.JSONException;
import org.json.JSONObject;

import smartpay.audittrail.AuditTrail;
import smartpay.entity.Agency;
import smartpay.entity.Applicant;
import smartpay.entity.BlackList;
import smartpay.entity.BlackListLog;
import smartpay.entity.PortalUser;
import smartpay.entity.enumerations.ApplicantType;
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
import com.ecims.portlet.blacklist.BlacklistPortletState;
import com.ecims.portlet.blacklist.BlacklistPortletState.BLACKLIST_ACTIONS;
import com.ecims.portlet.blacklist.BlacklistPortletState.VIEW_TABS;
import com.ecims.portlet.blacklist.BlacklistPortletUtil;
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
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.lowagie.text.pdf.PdfGState;
import com.rsa.authagent.authapi.AuthSession;
import com.sf.primepay.smartpay13.HibernateUtils;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class BlacklistPortlet
 */
public class BlacklistPortlet extends MVCPortlet {
	private Logger log = Logger.getLogger(BlacklistPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	BlacklistPortletUtil util = BlacklistPortletUtil.getInstance();
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
		BlacklistPortletState portletState = 
				BlacklistPortletState.getInstance(renderRequest, renderResponse);

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
		
		BlacklistPortletState portletState = BlacklistPortletState.getInstance(aReq, aRes);
		
		
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
        
        if(action.equalsIgnoreCase(VIEW_TABS.BLACKLIST_AN_APPLICANT.name()))
        {
        	log.info("VIEW_BLACKLIST_AN_APPLICANT");
        	//Select Account type
        	portletState.setCurrentTab(VIEW_TABS.BLACKLIST_AN_APPLICANT);
        	aRes.setRenderParameter("jspPage", "/html/blacklistportlet/blacklistanapplication_stepone.jsp");
        }
        else if(action.equalsIgnoreCase(VIEW_TABS.LIST_BLACKLIST_HISTORY.name()))
        {
        	log.info("VIEW_BLACKLISTED_APPLICANTS_LOG");
        	//Select Account type
        	portletState.setCurrentTab(VIEW_TABS.LIST_BLACKLIST_HISTORY);
        	Collection<BlackListLog> blacklistLogListing = portletState.getBlacklistPortletUtil().getBlackListLog();
        	portletState.setBlackListLogListing(blacklistLogListing);
        	aRes.setRenderParameter("jspPage", "/html/blacklistportlet/index_log.jsp");
        		
        }
        else if(action.equalsIgnoreCase(VIEW_TABS.LIST_BLACKLISTED_APPLICANTS.name()))
        {
        	log.info("VIEW_BLACKLISTED_APPLICANTS");
        	//Select Account type
        	portletState.setCurrentTab(VIEW_TABS.LIST_BLACKLISTED_APPLICANTS);
        	Collection<BlackList> blacklistListing = portletState.getBlacklistPortletUtil().getBlackList();
        	portletState.setBlackListListing(blacklistListing);
        	aRes.setRenderParameter("jspPage", "/html/blacklistportlet/index.jsp");
        		
        }else if(action.equalsIgnoreCase(BLACKLIST_ACTIONS.BLACKLIST_AN_APPLICANT_STEP_ONE.name()))
        {
        	String applicantNumber = aReq.getParameter("applicantNumber");
        	Applicant applicant = portletState.getBlacklistPortletUtil().getApplicantByApplicantNumber(applicantNumber);
        	if(applicant==null)
        	{
        		
        	}else
        	{
        		portletState.setApplicantToBlackList(applicant);
            	aRes.setRenderParameter("jspPage", "/html/blacklistportlet/reasons.jsp");
        	}
        }
        else if(action.equalsIgnoreCase(BLACKLIST_ACTIONS.BLACKLIST_AN_APPLICANT_STEP_TWO.name()))
        {
        	String reasons = aReq.getParameter("reason");
        	Applicant applicant = portletState.getApplicantToBlackList();
        	Agency agency = applicant.getPortalUser().getAgency();
        	
        	if(applicant!=null && reasons.length()>0)
        	{
        		BlackList blacklist = new BlackList();
        		blacklist.setActionPerformed("BLACKLIST");
        		blacklist.setAgency(agency);
        		blacklist.setApplicant(applicant);
        		blacklist.setBlackListedByPortalUserId(portletState.getPortalUser().getId());
        		blacklist.setDatePerformed(new Timestamp(new Date().getTime()));
        		blacklist.setPortalUser(portletState.getPortalUser());
        		blacklist.setReasons(reasons);
        		swpService.createNewRecord(blacklist);
        		
        		BlackListLog blacklistLog = new BlackListLog();
        		blacklistLog.setActionPerformed("BLACKLIST");
        		blacklistLog.setAgency(agency);
        		blacklistLog.setApplicant(applicant);
        		blacklistLog.setBlackListedByPortalUserId(portletState.getPortalUser().getId());
        		blacklistLog.setDatePerformed(new Timestamp(new Date().getTime()));
        		blacklistLog.setPortalUser(portletState.getPortalUser());
        		blacklistLog.setReasons(reasons);
        		swpService.createNewRecord(blacklistLog);
        		
        		PortalUser pu1 = applicant.getPortalUser();
        		try {
					User user = UserLocalServiceUtil.getUser(Long.valueOf(pu1.getUserId()));
					UserLocalServiceUtil.updateStatus(user.getUserId(), 1);
					UserLocalServiceUtil.updateLockout(user, true);
					pu1.setStatus(UserStatus.USER_STATUS_SUSPENDED);
					swpService.updateRecord(pu1);
					
					
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (PortalException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SystemException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        		applicant.setBlackList(Boolean.TRUE);
        		if(agency!=null)
        		{
        			agency.setBlacklist(Boolean.TRUE);
        		}
        		portletState.setCurrentTab(VIEW_TABS.LIST_BLACKLISTED_APPLICANTS);
        		Collection<BlackList> blacklistListing = portletState.getBlacklistPortletUtil().getBlackList();
        		portletState.setBlackListListing(blacklistListing);
        		portletState.addSuccess(aReq, "Agency/Applicant has been blacklisted successfully", portletState);
        		
        		String details = "";
        		if(agency!=null)
        		{
        			details = "Agency Name:" + agency.getAgencyName().toString() + ". Reason for blacklist: " + aReq.getParameter("reason");        			
        		}
        		else
        		{
        			details = "Applicant No:" + applicant.getApplicantNumber().toString() + ". Reason for blacklist: " + aReq.getParameter("reason");
        		}
        		
        		
        		new Util().pushAuditTrail(swpService, details, ECIMSConstants.BLACKLIST_APPLICANT, 
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
    			
    			
            	aRes.setRenderParameter("jspPage", "/html/blacklistportlet/index.jsp");
        	}else
        	{
            	aRes.setRenderParameter("jspPage", "/html/blacklistportlet/blacklistanapplication_stepone.jsp");
        	}
        }
        else if(action.equalsIgnoreCase(BLACKLIST_ACTIONS.HANDLE_ACTIONS_ON_BLACKLISTED_APPLICANTS.name()))
        {
        	String selectedUserAction = aReq.getParameter("selectedUserAction");
        	String id = aReq.getParameter("selectedApplications");
        	log.info("selectedUserAction = " + selectedUserAction);
        	log.info("id = " + id);
        	if(selectedUserAction.equalsIgnoreCase("removeFromBlackList"))
        	{
        		try
        		{
	        		Long in = Long.parseLong(id);
	        		log.info("in = " + in);
	        		BlackList blacklist = portletState.getBlacklistPortletUtil().
	        				getBlackListById(in);

	        		log.info("blacklist = " + blacklist.getReasons());
	        		if(blacklist!=null)
	        		{
	        			log.info("in = " + in);
	        			portletState.setCurrentBlackList(blacklist);
	        			aRes.setRenderParameter("jspPage", "/html/blacklistportlet/reasons_for_unblacklist.jsp");
	        		}
	        		else
	        		{
	        			portletState.addError(aReq, "Agency/Applicant Could not be found. Please Try again", portletState);
	        			aRes.setRenderParameter("jspPage", "/html/blacklistportlet/index.jsp");
	        		}
        		}catch(NumberFormatException e)
        		{
        			e.printStackTrace();
        		}
        	}else if(selectedUserAction.equalsIgnoreCase("viewBlackListHistory"))
        	{
        		portletState.setCurrentTab(VIEW_TABS.LIST_BLACKLIST_HISTORY);
            	
            	
            	try
        		{
	        		Long in = Long.parseLong(id);
	        		log.info("in = " + in);
	        		Collection<BlackListLog> blacklistLogListing = portletState.getBlacklistPortletUtil().getBlackListLogByApplicantId(in);
	            	portletState.setBlackListListing(null);
	            	portletState.setBlackListLogListing(blacklistLogListing);
	            	aRes.setRenderParameter("jspPage", "/html/blacklistportlet/index_log.jsp");
	            	
        		}catch(NumberFormatException e)
        		{
        			e.printStackTrace();
        		}
        	}
        	else if(selectedUserAction.equalsIgnoreCase("viewBlackListHistoryDetails"))
        	{
        		try
        		{
	        		Long in = Long.parseLong(id);
	        		log.info("in = " + in);
	        		BlackListLog blacklistLog = portletState.getBlacklistPortletUtil().
	        				getBlackListLogByBlacklistLogId(in);

	        		log.info("blacklistLog = " + blacklistLog.getReasons());
	        		if(blacklistLog!=null)
	        		{
	        			log.info("in = " + in);
	        			portletState.setCurrentBlackListLog(blacklistLog);
	        			aRes.setRenderParameter("jspPage", "/html/blacklistportlet/reasons_for_unblacklist_history.jsp");
	        		}
	        		else
	        		{
	        			portletState.addError(aReq, "Agency/Applicant Could not be found. Please Try again", portletState);
	        			aRes.setRenderParameter("jspPage", "/html/blacklistportlet/index.jsp");
	        		}
        		}catch(NumberFormatException e)
        		{
        			e.printStackTrace();
        		}
        	}
        }
        else if(action.equalsIgnoreCase(BLACKLIST_ACTIONS.REMOVE_FROM_BLACKLIST.name()))
        {
        	String reason = aReq.getParameter("reason");
        	log.info("reason ==" + reason);
        	if(portletState.getCurrentBlackList()!=null && reason.length()>0)
        	{
        		log.info("reason1 ==" + reason);
        		BlackList blacklist = portletState.getCurrentBlackList();
        		Applicant applicant = blacklist.getApplicant();
        		Agency  agency = blacklist.getAgency();
    			applicant.setBlackList(Boolean.FALSE);
        		
        		
        		if(agency!=null)
        		{
        			agency.setBlacklist(Boolean.FALSE);
        		}
        		swpService.deleteRecord(blacklist);
        		
        		BlackListLog blacklistLog = new BlackListLog();
        		blacklistLog.setActionPerformed("REMOVE FROM BLACKLIST");
        		blacklistLog.setAgency(agency);
        		blacklistLog.setApplicant(applicant);
        		blacklistLog.setBlackListedByPortalUserId(portletState.getPortalUser().getId());
        		blacklistLog.setDatePerformed(new Timestamp(new Date().getTime()));
        		blacklistLog.setPortalUser(portletState.getPortalUser());
        		blacklistLog.setReasons(reason);
        		swpService.createNewRecord(blacklistLog);
        		
        		try {
        			PortalUser pu1 = applicant.getPortalUser();
					User user = UserLocalServiceUtil.getUser(Long.valueOf(applicant.getPortalUser().getUserId()));
					UserLocalServiceUtil.updateStatus(user.getUserId(), 0);
					UserLocalServiceUtil.updateLockout(user, false);
					pu1.setStatus(UserStatus.USER_STATUS_ACTIVE);
					swpService.updateRecord(pu1);
					
					
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (PortalException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SystemException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		new Util().pushAuditTrail(swpService, "Applicant Number: " + applicant.getApplicantNumber() + ". Reason for Unblacklist: " + aReq.getParameter("reason"), ECIMSConstants.UNBLACKLIST_APPLICANT, 
    					portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
    			
        	}else
        	{
        		
        	}
        }
	}

	

}
