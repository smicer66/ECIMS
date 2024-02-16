package com.ecims.portlet.reportportlet;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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

import jxl.write.WriteException;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;

import smartpay.audittrail.AuditTrail;
import smartpay.entity.Agency;
import smartpay.entity.Applicant;
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
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.service.SwpService;


import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.ecims.commins.ECIMSConstants;
import com.ecims.commins.Util;
import com.ecims.commins.WriteExcel;
import com.ecims.portlet.reportportlet.ReportPortletState.REPORTING_ACTIONS;
import com.sf.primepay.smartpay13.ServiceLocator;
import com.liferay.portal.DuplicateUserScreenNameException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * Portlet implementation class ReportPortlet
 */
public class ReportPortlet extends MVCPortlet {
 
	private Logger log = Logger.getLogger(ReportPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	//ReportPortletUtil util = ReportPortletUtil.getInstance();
	ServiceContext serviceContext = new ServiceContext();
	
	
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
		ReportPortletState portletState = 
				ReportPortletState.getInstance(renderRequest, renderResponse);

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
		
		ReportPortletState portletState = ReportPortletState.getInstance(aReq, aRes);

		SimpleDateFormat sdfA = new SimpleDateFormat("yyyyMMddHHmm");
		
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
        /*************GENERAL NAVIGATION***************/
        
        
        /*************POST ACTIONS*********************/
        portletState.setDefaultColumnShow("1");
        if(action.equalsIgnoreCase(REPORTING_ACTIONS.CREATE_A_REPORT_STEP_ONE.name()))
        {
        	portletState.setStartDate(null);
        	portletState.setEndDate(null);
        	selectReportType(aReq, aRes, portletState);
        	
        }
        if(action.equalsIgnoreCase(REPORTING_ACTIONS.CREATE_A_CERTIFICATE_REPORT.name()))
        {
        	String act = aReq.getParameter("act");
        	log.info("Act ===" + act);
        	if(act!=null && act.equalsIgnoreCase("back"))
        	{
        		portletState.reinitiliazeForApplication(portletState);
        		aRes.setRenderParameter("jspPage", "/html/reportportlet/selectreport.jsp");
        	}else if(act!=null && act.equalsIgnoreCase("generate"))
        	{
        		log.info("start date = " + aReq.getParameter("startDate"));
            	log.info("end date = " + aReq.getParameter("endDate"));
            	portletState.setStartDate(aReq.getParameter("startDate"));
        		portletState.setEndDate(aReq.getParameter("endDate"));
        		portletState.setApplicationNumber(aReq.getParameter("applicationNumber"));
        		portletState.setApplicantNumber(aReq.getParameter("applicantNumber"));
        		portletState.setAmountLowerLimit(aReq.getParameter("amountLowerLimit"));
        		portletState.setCertificateStatus(aReq.getParameter("certificateStatus"));
        		portletState.setAmountUpperLimit(aReq.getParameter("amountUpperLimit"));
        		portletState.setShowApplicantNumber(aReq.getParameter("showApplicantNumber"));
        		portletState.setShowApplicationNumber(aReq.getParameter("showApplicationNumber"));
        		portletState.setShowValidityPeriod(aReq.getParameter("showValidityPeriod"));
        		portletState.setShowCertificateStatus(aReq.getParameter("showCertificateStatus"));
        		portletState.setShowImportationCosts(aReq.getParameter("showImportationCosts"));
        		portletState.setReportEmailSend(aReq.getParameter("reportEmailSend"));
        		
        		String hql="";
            	String errorMessage=null;
            	DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            	
            	try{
    	        	if(portletState.getStartDate()!=null && portletState.getStartDate().length()>0 && 
    	        			portletState.getEndDate()!=null && portletState.getEndDate().length()>0)
    	        	{
    	        		
    	        		Date dd1 = df.parse(portletState.getStartDate());
    	        		Date dd2 = df.parse(portletState.getEndDate());
    	        		
    	        		Timestamp t1 = new Timestamp(dd1.getTime());
    	        		Date dd11 = new Date(t1.getTime());
    	        		
    	        		Timestamp t2 = new Timestamp(dd2.getTime());
    	        		Date dd12 = new Date(t2.getTime());
    	        		
    	        		log.info(df.format(dd11));
    	        		log.info(df.format(dd11));
    	        		if(t1.getTime()<t2.getTime())
    	        		{
    	        			hql += " (ph.expireDate >= '" + df.format(dd11) + "' AND  ph.expireDate <= '" + df.format(dd12) + "') ";
    	        		}else if(t2.getTime()<t1.getTime())
    	        		{
    	        			hql += " (ph.expireDate >= '" + df.format(dd12) + "' AND  ph.expireDate <= '" + df.format(dd11) + "') ";
    	        		}else {
    	        			hql += " (ph.expireDate >= '" + df.format(dd11) + "' AND  ph.expireDate <= '" + df.format(dd12) + "') ";
    	        		}
    	        		log.info(hql);
    	        	}else if(portletState.getStartDate()!=null && portletState.getStartDate().length()>0)
    	        	{
    	        		Date dd1 = df.parse(portletState.getStartDate());
    	        		Timestamp t1 = new Timestamp(dd1.getTime());
    	        		Date dd11 = new Date(t1.getTime());
    	        		hql +=  " ph.expireDate = ('" + df.format(dd11) + "') ";
    	        		log.info(hql);
    	        	}else if(portletState.getEndDate()!=null && portletState.getEndDate().length()>0)
    	        	{
    	        		Date dd1 = df.parse(portletState.getEndDate());
    	        		Timestamp t1 = new Timestamp(dd1.getTime());
    	        		Date dd11 = new Date(t1.getTime());
    	        		hql +=  " ph.expireDate = ('" + df.format(dd11) + "') ";
    	        		log.info(hql);
    	        	}
            	}catch(ParseException e)
            	{
            		errorMessage = "Invalid date provided for one of the dates";
            		log.info(errorMessage);
            	}
            	
//            	if(portletState.getAmountLowerLimit()!=null && portletState.getAmountLowerLimit().length()>0 && 
//            			portletState.getAmountUpperLimit()!=null && portletState.getAmountUpperLimit().length()>0)
//            	{
//            		hql += hql.length()>0 ? " AND (ph.inValue >= " + Double.valueOf(portletState.getAmountLowerLimit()) + " AND ph.inValue <= " + Double.valueOf(portletState.getAmountUpperLimit()) + ") " : 
//            			" (ph.inValue >= " + Double.valueOf(portletState.getAmountLowerLimit()) + " AND ph.inValue <=  " + Double.valueOf(portletState.getAmountUpperLimit()) + ") " ;
//            	}else if(portletState.getAmountLowerLimit()!=null && portletState.getAmountLowerLimit().length()>0)
//            	{
//            		hql += hql.length()>0 ? " AND ph.inValue = " + Double.valueOf(portletState.getAmountLowerLimit()) :  " ph.inValue = " + Double.valueOf(portletState.getAmountLowerLimit());
//            	}else if(portletState.getAmountUpperLimit()!=null && portletState.getAmountUpperLimit().length()>0)
//            	{
//            		hql += hql.length()>0 ? " AND ph.inValue = " + Double.valueOf(portletState.getAmountUpperLimit()) :  " ph.inValue = " + Double.valueOf(portletState.getAmountUpperLimit());
//            	}
//            	
//            	
            	if(portletState.getApplicantNumber()!=null && portletState.getApplicantNumber().length()>0)
            	{
            		hql +=  hql.length()>0 ? " AND ph.application.applicant.applicantNumber = '" + portletState.getApplicantNumber()+ "' " :  "ph.application.applicant.applicantNumber = '" + portletState.getApplicantNumber()+ "' ";
            	}
            	
            	if(portletState.getApplicationNumber()!=null && portletState.getApplicationNumber().length()>0)
            	{
            		hql +=  hql.length()>0 ? " AND ph.application.applicationNumber = '" + portletState.getApplicationNumber() + "' " :  "ph.application.applicationNumber = '" + portletState.getApplicationNumber()+ "' ";
            	}
            	
            	if(portletState.getCertificateStatus()!=null && portletState.getCertificateStatus().length()>0)
            	{
            		hql +=  hql.length()>0 ? " AND lower(ph.status) = lower('" + portletState.getCertificateStatus()+ "') " :  "lower(ph.status) = lower('" + portletState.getCertificateStatus()+ "') ";
            	}

            	
//            	if(portletState.getCountrySelected()!=null && portletState.getCountrySelected().length()>0)
//            	{
//            		hql +=  hql.length()>0 ? " AND lower(ph.country.name) = lower('" + portletState.getCountrySelected()+ "') " :  "lower(ph.country.name) = lower('" + portletState.getCountrySelected()+ "') ";
//            	}
            	        	
            	
            	
            	
            	if(hql!=null && hql.length()>0)
            	{
            		hql= "select ph from Certificate ph WHERE "+ hql + " ORDER by ph.issuanceDate DESC";
            	}
            	log.info("HQL for report ==" + hql);
            	Collection<Certificate> appList = (Collection<Certificate>)portletState.getReportPortletUtil().runReportHQL(hql);
            	if(appList!=null && appList.size()>0)
            	{
            		log.info("appList size===" + appList.size());
            		portletState.setCertificateList(appList);
            		ArrayList<ArrayList<String>> payHistParent= new ArrayList<ArrayList<String>>();
            		
            		/*****LABEL*****/
            		ArrayList<String> labelRow = new ArrayList<String>();
            		
            		
            		/***Conetent****/

            		
            		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            		if(portletState.getDefaultColumnShow()!=null && portletState.getDefaultColumnShow().equals("1"))
        			{
            				labelRow.add("DATE CREATED (YYYY-MM-DD)");
        					labelRow.add("CERTIFICATE NO.");
        					labelRow.add("VALID UNTIL (YYYY-MM-DD)");
                    		labelRow.add("APPLICATION NO.");
                    		labelRow.add("APPLICANT");
                    		labelRow.add("CERTIFICATE STATUS");
                    		labelRow.add("IMPORTATION COSTS");
        			}else
        			{
                    		labelRow.add("DATE CREATED (YYYY-MM-DD)");
                    		labelRow.add("CERTIFICATE NO.");
        				if(portletState.getShowValidityPeriod()!=null && portletState.getShowValidityPeriod().equalsIgnoreCase("VALIDITYPERIOD"))
                        	labelRow.add("VALID UNTIL (YYYY-MM-DD)");
        				if(portletState.getShowApplicationNumber()!=null && portletState.getShowApplicationNumber().equalsIgnoreCase("APPLICATIONNUMBER"))
                        	labelRow.add("APPLICATION NO.");
                    	if(portletState.getShowApplicantNumber()!=null && portletState.getShowApplicantNumber().equalsIgnoreCase("APPLICANTNUMBER"))
                    		labelRow.add("APPLICANT");
                    	if(portletState.getShowStatus()!=null && portletState.getShowStatus().equalsIgnoreCase("CERTIFICATESTATUS"))
                    		labelRow.add("CERTIFICATE STATUS");
                    	if(portletState.getShowException()!=null && portletState.getShowException().equalsIgnoreCase("IMPORTATIONCOSTS"))
                    		labelRow.add("IMPORTATION COSTS");
        			}
            		for(Iterator<Certificate> it = appList.iterator(); it.hasNext();)
            		{
            			Certificate app = it.next();
            			ArrayList<String> payHistoryRow = new ArrayList<String>();
            			if(portletState.getDefaultColumnShow()!=null && portletState.getDefaultColumnShow().equals("1"))
            			{
            				payHistoryRow.add(app.getDateCreated()!=null ? sdf.format(new Date(app.getDateCreated().getTime())) : "N/A");
            				payHistoryRow.add(app.getCertificateNo()!=null ? app.getCertificateNo() : "N/A");
            				payHistoryRow.add(app.getExpireDate()!=null ? sdf.format(app.getExpireDate()) : "N/A");
            				payHistoryRow.add(app.getApplication()!=null ? app.getApplication().getApplicationNumber() : "N/A");
            				payHistoryRow.add(app.getApplication()!=null ? (app.getApplication().getApplicant().getApplicantNumber() + " - " + app.getApplication().getApplicant().getPortalUser().getFirstName() + " " + app.getApplication().getApplicant().getPortalUser().getSurname() + " ") : "N/A");
            				payHistoryRow.add(app.getStatus()!=null ? app.getStatus().getValue() : "N/A");
            				payHistoryRow.add(app.getInValue()!=null ? app.getApplication().getCurrency().getHtmlEntity() + "" + app.getInValue() : "N/A");
            				
            				
            			}else
            			{

            				labelRow.add("DATE CREATED");
                    		labelRow.add("CERTIFICATE NO.");
    	    				if(portletState.getShowValidityPeriod()!=null && portletState.getShowValidityPeriod().equalsIgnoreCase("VALIDITYPERIOD"))
    	    					payHistoryRow.add(app.getExpireDate()!=null ? sdf.format(app.getExpireDate()) : "N/A");
    	    				if(portletState.getShowApplicationNumber()!=null && portletState.getShowApplicationNumber().equalsIgnoreCase("APPLICATIONNUMBER"))
    	    					payHistoryRow.add(app.getApplication()!=null ? app.getApplication().getApplicationNumber() : "N/A");
    	                	if(portletState.getShowApplicantNumber()!=null && portletState.getShowApplicantNumber().equalsIgnoreCase("APPLICANTNUMBER"))
    	                		payHistoryRow.add(app.getApplication()!=null ? (app.getApplication().getApplicant().getApplicantNumber() + " - " + app.getApplication().getApplicant().getPortalUser().getFirstName() + " " + app.getApplication().getApplicant().getPortalUser().getSurname() + " ") : "N/A");
    	                	if(portletState.getShowStatus()!=null && portletState.getShowStatus().equalsIgnoreCase("CERTIFICATESTATUS"))
    	                		payHistoryRow.add(app.getStatus()!=null ? app.getStatus().getValue() : "N/A");
    	                	if(portletState.getShowException()!=null && portletState.getShowException().equalsIgnoreCase("IMPORTATIONCOSTS"))
    	                		payHistoryRow.add(app.getInValue()!=null ? app.getApplication().getCurrency().getHtmlEntity() + "" + app.getInValue() : "N/A");
            			}
            			payHistParent.add(payHistoryRow);
            			
            			
            			log.info("app id = " + app.getId());
            		}
            		
            		
            		log.info("-----------------user dir is " + System.getProperty("user.dir"));
            		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            		Timestamp tstamp = new Timestamp((new Date()).getTime());
//                    String userDirFile = System.getProperty("user.dir") + File.separator + "Payments" + File.separator + 
//                    		"plr_for_" + portletState.getPortalUser().getCompany().
//                    		getCompanyRCNumber().toLowerCase().replace(" ", "_") + "_on_" + sdf.format(new Date(tstamp.getTime()));
//            		String userDirFile = aReq.getScheme() + "://"
//                			+ aReq.getServerName() + ":" + aReq.getServerPort()
//                			+ File.separator + "resources" + File.separator + "Payments" + File.separator + 
//                    		"applications_reports_for_on_" + sdf.format(new Date(tstamp.getTime()));
            		
            		String userDir = System.getProperty("user.dir");
            		userDir = "C:/jcodes/dev/appservers/ecims/webapps/resources";
            		String sep = File.separator;
            		String filname = "certificate_reports_on_" + sdfA.format(new Date(tstamp.getTime())).replace(" ", "") + ".xls";
            		String userDirFile1 = userDir +sep+"ReportEngine"+sep+"Reports" + File.separator + "Payments" + File.separator + filname;
                    		
            				
//            				"/resources" + File.separator + "Payments" + File.separator + 
//                    		"plr_for_" + portletState.getPortalUser().getCompany().
//                    		getCompanyRCNumber().toLowerCase().replace(" ", "_") + "_on_" + sdf.format(new Date(tstamp.getTime()));
                    log.info("File: " + userDirFile1);
            		WriteExcel writeExcel = new WriteExcel(userDirFile1, 
            				"End-User Certificate Listing Report - Generated " + sdf1.format(new Date(tstamp.getTime())), 
            				labelRow, payHistParent );
            		new Util().pushAuditTrail(swpService, filname, ECIMSConstants.GENERATE_REPORTS_XLS, 
    						portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
            		try {
    					writeExcel.write();
    					portletState.addSuccess(aReq, "Report Generated successfully. To download your report, right-click on the link below and " +
    							"click on SAVE LINK AS option.<br>Download Report: " +
    							"<a target='blank' href='/resources/ReportEngine/Reports/Payments/" +filname+"'>End-User Certificate Listings</a>", portletState);
    					aRes.setRenderParameter("jspPage", "/html/reportportlet/filterresultsforcertificate.jsp");
    					portletState.setFilName(filname);
    				} catch (WriteException e) {
    					// TODO Auto-generated catch block
    					portletState.addError(aReq, "We experienced errors generating your report. Please try again.", portletState);
    					aRes.setRenderParameter("jspPage", "/html/reportportlet/filterresultsforcertificate.jsp");
    				}
            	}
            	else
            	{
            		portletState.addError(aReq, "There are currently no certificates matching the criteria you provided. Please refine your filter criteria and run the report again.", portletState);
    				aRes.setRenderParameter("jspPage", "/html/reportportlet/filterresultsforcertificate.jsp");
            	}
        	} 
        	
        	
        	
        }
        if(action.equalsIgnoreCase(REPORTING_ACTIONS.CREATE_AN_APPLICANT_REPORT_STEP_TWO.name()))
        {
        	String act = aReq.getParameter("act");
        	log.info("Act ===" + act);
        	if(act!=null && act.equalsIgnoreCase("back"))
        	{
        		portletState.reinitiliazeForApplication(portletState);
        		aRes.setRenderParameter("jspPage", "/html/reportportlet/selectreport.jsp");
        	}else if(act!=null && act.equalsIgnoreCase("generate"))
        	{
	        	log.info("start date = " + aReq.getParameter("dobStartDate"));
	        	log.info("end date = " + aReq.getParameter("dobEndDate"));
	        	portletState.setDobStartDate(aReq.getParameter("dobStartDate"));
	    		portletState.setDobEndDate(aReq.getParameter("dobEndDate"));
	    		log.info("Acct created on date = " + aReq.getParameter("startDate"));
	        	log.info("Acct created on end date = " + aReq.getParameter("endDate"));
	        	portletState.setStartDate(aReq.getParameter("startDate"));
	    		portletState.setEndDate(aReq.getParameter("endDate"));
	        	portletState.setGender(aReq.getParameter("gender"));
	    		portletState.setMaritalStatus(aReq.getParameter("maritalStatus"));
	    		portletState.setSelectedState(aReq.getParameter("selectedState"));
	    		portletState.setAgencySelected(aReq.getParameter("agencySelected"));
	    		portletState.setSelectedRoleType(aReq.getParameter("selectedRoleType"));
	    		portletState.setUserStatus(aReq.getParameter("userStatus"));
	    		portletState.setShowFullName(aReq.getParameter("showFullName"));
	    		portletState.setShowAddress(aReq.getParameter("showAddress"));
	    		portletState.setShowDOB(aReq.getParameter("showDOB"));
	    		portletState.setShowEmailAddress(aReq.getParameter("showEmailAddress"));
	    		portletState.setShowMobileNumber(aReq.getParameter("showMobileNumber"));
	    		portletState.setShowMaritalStatus(aReq.getParameter("showMaritalStatus"));
	    		portletState.setShowStateOfOrigin(aReq.getParameter("showStateOfOrigin"));
	    		portletState.setShowUserAgency(aReq.getParameter("showUserAgency"));
	    		portletState.setShowUserRole(aReq.getParameter("showUserRole"));
	    		portletState.setShowApplicantType(aReq.getParameter("showApplicantType"));
	    		
	    		
	    		String hql="";
	        	String errorMessage=null;
	        	DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
	        	
	        	try{
		        	if(portletState.getDobStartDate()!=null && portletState.getDobStartDate().length()>0 && 
		        			portletState.getDobEndDate()!=null && portletState.getDobEndDate().length()>0)
		        	{
		        		
		        		Date dd1 = df.parse(portletState.getDobStartDate());
		        		Date dd2 = df.parse(portletState.getDobEndDate());
		        	
		        		Timestamp t1 = new Timestamp(dd1.getTime());
		        		Date dd11 = new Date(t1.getTime());
		        		
		        		Timestamp t2 = new Timestamp(dd2.getTime());
		        		Date dd12 = new Date(t2.getTime());
		        		
		        		log.info(df.format(dd11));
		        		log.info(df.format(dd11));
		        		if(t1.getTime()<t2.getTime())
		        		{
		        			hql += " (ph.dateOfBirth >= '" + df.format(dd11) + "' AND  ph.dateOfBirth <= '" + df.format(dd12) + "') ";
		        		}else if(t2.getTime()<t1.getTime())
		        		{
		        			hql += " (ph.dateOfBirth >= '" + df.format(dd12) + "' AND  ph.dateOfBirth <= '" + df.format(dd11) + "') ";
		        		}else {
		        			hql += " (ph.dateOfBirth >= '" + df.format(dd11) + "' AND  ph.dateOfBirth <= '" + df.format(dd12) + "') ";
		        		}
		        		log.info(hql);
		        	}else if(portletState.getDobStartDate()!=null && portletState.getDobStartDate().length()>0)
		        	{
		        		Date dd1 = df.parse(portletState.getDobStartDate());
		        		Timestamp t1 = new Timestamp(dd1.getTime());
		        		Date dd11 = new Date(t1.getTime());
		        		hql +=  " ph.dateOfBirth = ('" + df.format(dd11) + "') ";
		        		log.info(hql);
		        	}else if(portletState.getDobEndDate()!=null && portletState.getDobEndDate().length()>0)
		        	{
		        		Date dd1 = df.parse(portletState.getDobEndDate());
		        		Timestamp t1 = new Timestamp(dd1.getTime());
		        		Date dd11 = new Date(t1.getTime());
		        		hql +=  " ph.dateOfBirth = ('" + df.format(dd11) + "') ";
		        		log.info(hql);
		        	}
		        	 
		        	
		        	
		        	
		        	
		        	
		        	
		        	if(portletState.getStartDate()!=null && portletState.getStartDate().length()>0 && 
		        			portletState.getEndDate()!=null && portletState.getEndDate().length()>0)
		        	{
		        		
		        		Date dd2 = df.parse(portletState.getStartDate());
		        		Date dd3 = df.parse(portletState.getEndDate());
		        	
		        		Timestamp t1 = new Timestamp(dd3.getTime());
		        		Date dd11 = new Date(t1.getTime());
		        		
		        		Timestamp t2 = new Timestamp(dd2.getTime());
		        		Date dd12 = new Date(t2.getTime());
		        		
		        		log.info(df.format(dd11));
		        		log.info(df.format(dd11));
		        		
		        		if(t1.getTime()<t2.getTime())
		        		{
		        			hql += hql.length()>0 ? " AND (ph.dateCreated >= '" + df.format(dd11) + "' AND  " +
		        					"ph.dateCreated <= '" + df.format(dd12) + "') " : " (ph.dateCreated >= '" + df.format(dd11) + "' AND  " +
		        					"ph.dateCreated <= '" + df.format(dd12) + "') ";
		        		}else if(t2.getTime()<t1.getTime())
		        		{
		        			hql += hql.length()>0 ? " AND (ph.dateCreated >= '" + df.format(dd12) + "' AND  " +
		        					"ph.dateCreated <= '" + df.format(dd11) + "') " : " (ph.dateCreated >= '" + df.format(dd12) + "' AND  " +
		        					"ph.dateCreated <= '" + df.format(dd11) + "')";
		        		}else {
		        			hql += hql.length()>0 ? " AND (ph.dateCreated >= '" + df.format(dd11) + "' AND  " +
		        					"ph.dateCreated <= '" + df.format(dd12) + "') " : " (ph.dateCreated >= '" + df.format(dd11) + "' AND  " +
		        					"ph.dateCreated <= '" + df.format(dd12) + "')";
		        		}
		        		log.info(hql);
		        	}else if(portletState.getStartDate()!=null && portletState.getStartDate().length()>0)
		        	{
		        		Date dd1 = df.parse(portletState.getStartDate());
		        		Timestamp t1 = new Timestamp(dd1.getTime());
		        		Date dd11 = new Date(t1.getTime());
		        		hql +=   hql.length()>0 ? " AND ph.dateCreated = ('" + df.format(dd11) + "') " : 
		        			" ph.dateCreated = ('" + df.format(dd11) + "') ";
		        		log.info(hql);
		        	}else if(portletState.getEndDate()!=null && portletState.getEndDate().length()>0)
		        	{
		        		Date dd1 = df.parse(portletState.getEndDate());
		        		Timestamp t1 = new Timestamp(dd1.getTime());
		        		Date dd11 = new Date(t1.getTime());
		        		hql +=   hql.length()>0 ? " AND ph.dateCreated = ('" + df.format(dd11) + "') " : 
		        			" ph.dateCreated = ('" + df.format(dd11) + "') ";
		        		log.info(hql);
		        	}
		        	
		        	
		        	
	        	}catch(ParseException e)
	        	{
	        		errorMessage = "Invalid date provided for one of the dates";
	        		log.info(errorMessage);
	        	}
	        	
	        	
	        	if(portletState.getGender()!=null && portletState.getGender().length()>0)
	        	{
	        		hql +=  hql.length()>0 ? " AND ph.gender = '" + portletState.getGender()+ "' " :  "ph.gender = '" + portletState.getGender()+ "' ";
	        	}
	        	
	        	if(portletState.getMaritalStatus()!=null && portletState.getMaritalStatus().length()>0)
	        	{
	        		hql +=  hql.length()>0 ? " AND ph.maritalStatus = '" + portletState.getMaritalStatus() + "' " :  "ph.maritalStatus = '" + portletState.getMaritalStatus()+ "' ";
	        	}
	        	
	        	if(portletState.getSelectedState()!=null && portletState.getSelectedState().length()>0)
	        	{
	        		hql +=  hql.length()>0 ? " AND lower(ph.state.name) = lower('" + portletState.getSelectedState()+ "') " :  "lower(ph.state.name) = lower('" + portletState.getSelectedState()+ "') ";
	        	}
	        	
	        	if(portletState.getAgencySelected()!=null && portletState.getAgencySelected().length()>0)
	        	{
	        		hql +=  hql.length()>0 ? " AND lower(ph.agency.agencyName) = lower('" + portletState.getAgencySelected() + "') " :  "lower(ph.agency.agencyName) = lower('" + portletState.getAgencySelected() + "') ";
	        	}
	        	
	        	if(portletState.getSelectedRoleType()!=null && portletState.getSelectedRoleType().length()>0)
	        	{
	        		hql +=  hql.length()>0 ? " AND lower(ph.roleType.name) = lower('" + portletState.getSelectedRoleType()+ "') " :  "lower(ph.roleType.name) = lower('" + portletState.getSelectedRoleType()+ "') ";
	        	}
	        	
	        	if(portletState.getUserStatus()!=null && portletState.getUserStatus().length()>0)
	        	{
	        		hql +=  hql.length()>0 ? " AND ph.status = '" + portletState.getUserStatus() + "' " :  "ph.status = '" + portletState.getUserStatus() + "' ";
	        	}
	        	
	        	if(portletState.getApplicantNumber()!=null && portletState.getApplicantNumber().length()>0)
	        	{
	        		hql +=  hql.length()>0 ? " AND ph.id in (SELECT app.portalUser.id FROM Applicant app where app.applicantNumber = '"+portletState.getApplicantNumber()+"') " :  "ph.id in (SELECT app.portalUser.id FROM Applicant app where app.applicantNumber = '"+portletState.getApplicantNumber()+"') ";
	        	}
	
	        	        	
	        	
	        	
	        	
	        	if(hql!=null && hql.length()>0)
	        	{
	        		hql= "select ph from PortalUser ph WHERE "+ hql + " ORDER by ph.dateCreated DESC";
	        	}
	        	log.info("HQL for report ==" + hql);
	        	Collection<PortalUser> appList = (Collection<PortalUser>)portletState.getReportPortletUtil().runReportHQL(hql);
	        	
	        	if(appList!=null && appList.size()>0)
	        	{
	        		log.info("appList size===" + appList.size());
	        		portletState.setPortalUserList(appList);
	        		ArrayList<ArrayList<String>> payHistParent= new ArrayList<ArrayList<String>>();
	        		
	        		/*****LABEL*****/
	        		ArrayList<String> labelRow = new ArrayList<String>();
	        		
	        		
	        		/***Conetent****/
	
	        		
	        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	        		if(portletState.getDefaultColumnShow()!=null && portletState.getDefaultColumnShow().equals("1"))
	    			{
	        				labelRow.add("DATE CREATED");
	    					labelRow.add("FULL NAME");
	    					labelRow.add("ADDRESS LINE 1");
	                		labelRow.add("ADDRESS LINE 2");
	                		labelRow.add("DATE OF BIRTH");
	                		labelRow.add("GENDER");
	                		labelRow.add("EMAIL ADDRESS");
	                		labelRow.add("PHONE NUMBER");
	                		labelRow.add("MARITAL STATUS");
	    					labelRow.add("STATUS");
	    					labelRow.add("COMPANY NAME");
	                		labelRow.add("TYPE OF USER");
	                		labelRow.add("USERS AGENCY");
	                		labelRow.add("STATE OF ORIGIN");
	    			}else
	    			{
	    				labelRow.add("DATE CREATED");
	                	if(portletState.getShowDateCreated()!=null && portletState.getShowDateCreated().equalsIgnoreCase("FULLNAME"))
	    					labelRow.add("FULL NAME");
	    				if(portletState.getShowApplicationNumber()!=null && portletState.getShowApplicationNumber().equalsIgnoreCase("ADDRESS"))
	    				{
	    					labelRow.add("ADDRESS LINE 1");
	    					labelRow.add("ADDRESS LINE 2");
	    				}
	                	if(portletState.getShowStatus()!=null && portletState.getShowStatus().equalsIgnoreCase("DOB"))
	                		labelRow.add("DATE OF BIRTH");
	                	if(portletState.getShowApplicantName()!=null && portletState.getShowApplicantName().equalsIgnoreCase("EMAILADDRESS"))
	                		labelRow.add("EMAIL ADDRESS");
	                	if(portletState.getShowException()!=null && portletState.getShowException().equalsIgnoreCase("MOBILENO"))
	                		labelRow.add("PHONE NUMBER");
	                	if(portletState.getShowImportationPort()!=null && portletState.getShowImportationPort().equalsIgnoreCase("MARITALSTATUS"))
	                		labelRow.add("MARITAL STATUS");
	                	if(portletState.getShowItemCategory()!=null && portletState.getShowItemCategory().equalsIgnoreCase("STATEOFORIGIN"))
	                		labelRow.add("STATE OF ORIGIN");
		            	if(portletState.getShowException()!=null && portletState.getShowException().equalsIgnoreCase("AGENCYUSER"))
		            		labelRow.add("USERS AGENCY");
		            	if(portletState.getShowImportationPort()!=null && portletState.getShowImportationPort().equalsIgnoreCase("USERROLE"))
		            		labelRow.add("TYPE OF USER");
	    			}
	        		for(Iterator<PortalUser> it = appList.iterator(); it.hasNext();)
	        		{
	        			PortalUser app = it.next();
	        			ArrayList<String> payHistoryRow = new ArrayList<String>();
	        			if(portletState.getDefaultColumnShow()!=null && portletState.getDefaultColumnShow().equals("1"))
	        			{
	        				payHistoryRow.add(app.getDateCreated()!=null ? sdf.format(new Date(app.getDateCreated().getTime())) : "N/A");
	        				payHistoryRow.add(app.getFirstName()!=null ? app.getFirstName() + " " + app.getSurname() : "N/A");
	        				payHistoryRow.add(app.getAddressLine1()!=null ? (app.getAddressLine1()) : "N/A");
	        				payHistoryRow.add(app.getAddressLine2()!=null ? (app.getAddressLine2()) : "N/A");
	        				payHistoryRow.add(sdf.format(app.getDateOfBirth())!=null ? sdf.format(app.getDateOfBirth()) : "N/A");
	        				payHistoryRow.add(app.getGender()!=null ? app.getGender() : "N/A");
	        				payHistoryRow.add(app.getEmailAddress()!=null ? app.getEmailAddress() : "N/A");
	        				payHistoryRow.add(app.getPhoneNumber()!=null ? app.getPhoneNumber() : "N/A");
	        				payHistoryRow.add(app.getMaritalStatus()!=null ? app.getMaritalStatus() : "N/A");
	        				payHistoryRow.add(app.getStatus()!=null ? app.getStatus().getValue() : "N/A");
	        				payHistoryRow.add(app.getCompany()!=null ? app.getCompany().getName() : "N/A");
	        				payHistoryRow.add(app.getRoleType()!=null ? app.getRoleType().getName().getValue() : "N/A");
	        				payHistoryRow.add(app.getAgency()!=null ? app.getAgency().getAgencyName() : "N/A");
	        				payHistoryRow.add(app.getState()!=null ? app.getState().getName() : "N/A");
	        				
	        			}else
	        			{
	                    	payHistoryRow.add(app.getDateCreated()!=null ? sdf.format(new Date(app.getDateCreated().getTime())) : "N/A");
	                    	if(portletState.getShowDateCreated()!=null && portletState.getShowDateCreated().equalsIgnoreCase("FULLNAME"))
	                    		payHistoryRow.add(app.getFirstName()!=null ? app.getFirstName() + " " + app.getSurname() : "N/A");
	        				if(portletState.getShowApplicationNumber()!=null && portletState.getShowApplicationNumber().equalsIgnoreCase("ADDRESS"))
	        				{
	        					payHistoryRow.add(app.getAddressLine1()!=null ? (app.getAddressLine1()) : "N/A");
	            				payHistoryRow.add(app.getAddressLine2()!=null ? (app.getAddressLine2()) : "N/A");
	        				}
	                    	if(portletState.getShowStatus()!=null && portletState.getShowStatus().equalsIgnoreCase("DOB"))
	                    		payHistoryRow.add(sdf.format(app.getDateOfBirth())!=null ? sdf.format(app.getDateOfBirth()) : "N/A");
	                    	if(portletState.getShowApplicantName()!=null && portletState.getShowApplicantName().equalsIgnoreCase("EMAILADDRESS"))
	                    		payHistoryRow.add(app.getEmailAddress()!=null ? app.getEmailAddress() : "N/A");
	                    	if(portletState.getShowException()!=null && portletState.getShowException().equalsIgnoreCase("MOBILENO"))
	                    		payHistoryRow.add(app.getPhoneNumber()!=null ? app.getPhoneNumber() : "N/A");
	                    	if(portletState.getShowImportationPort()!=null && portletState.getShowImportationPort().equalsIgnoreCase("MARITALSTATUS"))
	                    		payHistoryRow.add(app.getMaritalStatus()!=null ? app.getMaritalStatus() : "N/A");
	                    	if(portletState.getShowItemCategory()!=null && portletState.getShowItemCategory().equalsIgnoreCase("STATEOFORIGIN"))
	                    		payHistoryRow.add(app.getState()!=null ? app.getState().getName() : "N/A");
	    	            	if(portletState.getShowException()!=null && portletState.getShowException().equalsIgnoreCase("AGENCYUSER"))
	    	            		payHistoryRow.add(app.getAgency()!=null ? app.getAgency().getAgencyName() : "N/A");
	    	            	if(portletState.getShowImportationPort()!=null && portletState.getShowImportationPort().equalsIgnoreCase("USERROLE"))
	    	            		payHistoryRow.add(app.getRoleType()!=null ? app.getRoleType().getName().getValue() : "N/A");
	    	            	
	        			}
	        			payHistParent.add(payHistoryRow);
	        			
	        			
	        			log.info("app id = " + app.getId());
	        		}
	        		
	        		
	        		log.info("-----------------user dir is " + System.getProperty("user.dir"));
	        		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	        		Timestamp tstamp = new Timestamp((new Date()).getTime());
	//                String userDirFile = System.getProperty("user.dir") + File.separator + "Payments" + File.separator + 
	//                		"plr_for_" + portletState.getPortalUser().getCompany().
	//                		getCompanyRCNumber().toLowerCase().replace(" ", "_") + "_on_" + sdf.format(new Date(tstamp.getTime()));
	//        		String userDirFile = aReq.getScheme() + "://"
	//            			+ aReq.getServerName() + ":" + aReq.getServerPort()
	//            			+ File.separator + "resources" + File.separator + "Payments" + File.separator + 
	//                		"applications_reports_for_on_" + sdf.format(new Date(tstamp.getTime()));
	        		
	        		String userDir = System.getProperty("user.dir");
	        		userDir = "C:/jcodes/dev/appservers/ecims/webapps/resources";
	        		String sep = File.separator;
	        		String filname = "portal_users_reports_for_on_" + sdfA.format(new Date(tstamp.getTime())).replace(" ", "") + ".xls";
	        		String userDirFile1 = userDir +sep+"ReportEngine"+sep+"Reports" + File.separator + "Payments" + File.separator + filname;
	                		
	        				
	//        				"/resources" + File.separator + "Payments" + File.separator + 
	//                		"plr_for_" + portletState.getPortalUser().getCompany().
	//                		getCompanyRCNumber().toLowerCase().replace(" ", "_") + "_on_" + sdf.format(new Date(tstamp.getTime()));
	                log.info("File: " + userDirFile1);
	        		WriteExcel writeExcel = new WriteExcel(userDirFile1, 
	        				"ECIMS Portal User Listing Report - Generated " + sdf1.format(new Date(tstamp.getTime())), 
	        				labelRow, payHistParent );
	        		new Util().pushAuditTrail(swpService, filname, ECIMSConstants.GENERATE_REPORTS_XLS, 
							portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
	        		try {
						writeExcel.write();
						portletState.addSuccess(aReq, "Report Generated successfully. To download your report, right-click on the link below and " +
								"click on SAVE LINK AS option.<br>Download Report: " +
								"<a target='blank' href='/resources/ReportEngine/Reports/Payments/" +filname+"'>ECIMS Portal User Listings</a>", portletState);
						aRes.setRenderParameter("jspPage", "/html/reportportlet/filterresultsforportaluser.jsp");
						portletState.setFilName(filname);
					} catch (WriteException e) {
						// TODO Auto-generated catch block
						portletState.addError(aReq, "We experienced errors generating your report. Please try again.", portletState);
						aRes.setRenderParameter("jspPage", "/html/reportportlet/filterresultsforportaluser.jsp");
					}
	        	}
	        	else
	        	{
	        		portletState.addError(aReq, "There are currently no portal users matching the criteria you provided. Please refine your filter criteria and run the report again.", portletState);
					aRes.setRenderParameter("jspPage", "/html/reportportlet/filterresultsforportaluser.jsp");
	        	}
        	}
        }
        if(action.equalsIgnoreCase(REPORTING_ACTIONS.CREATE_AN_APPLICATION_REPORT_STEP_TWO.name()))
        {
        	String act = aReq.getParameter("act");
        	log.info("Act ===" + act);
        	if(act!=null && act.equalsIgnoreCase("back"))
        	{
        		portletState.reinitiliazeForApplication(portletState);
        		aRes.setRenderParameter("jspPage", "/html/reportportlet/selectreport.jsp");
        	}else if(act!=null && act.equalsIgnoreCase("generate"))
        	{
	        	log.info("start date = " + aReq.getParameter("startDate"));
	        	log.info("end date = " + aReq.getParameter("endDate"));
	        	portletState.setStartDate(aReq.getParameter("startDate"));
	    		portletState.setEndDate(aReq.getParameter("endDate"));
	    		portletState.setApplicationNumber(aReq.getParameter("applicationNumber"));
	    		portletState.setApplicantNumber(aReq.getParameter("applicantNumber"));
	    		portletState.setExceptionType(aReq.getParameter("exceptionType"));
	    		portletState.setApplicationStatus(aReq.getParameter("status"));
	    		portletState.setCountrySelected(aReq.getParameter("country"));
	    		portletState.setItemCategory(aReq.getParameter("itemCategory"));
	    		portletState.setPortOfEntry(aReq.getParameter("portOfEntry"));
	    		portletState.setAmountLowerLimit(aReq.getParameter("amountLowerLimit"));
	    		portletState.setAmountUpperLimit(aReq.getParameter("amountUpperLimit"));
	    		portletState.setExceptionType(aReq.getParameter("exceptionType"));
	    		portletState.setShowApplicationNumber(aReq.getParameter("showApplicationNumber"));
	    		portletState.setShowDateCreated(aReq.getParameter("showDateCreated"));
	    		portletState.setDefaultColumnShow(aReq.getParameter("defaultColumnShow"));
	    		portletState.setShowApplicantName(aReq.getParameter("showApplicantName"));
	    		portletState.setShowApplicationNumber(aReq.getParameter("showApplicationNumber"));
	    		portletState.setShowDateCreated(aReq.getParameter("showDateCreated"));
	    		portletState.setShowException(aReq.getParameter("showException"));
	    		portletState.setShowStatus(aReq.getParameter("showStatus"));
	    		portletState.setShowImportationPort(aReq.getParameter("showImportationPort"));
	    		portletState.setShowItemCategory(aReq.getParameter("showItemCategory"));
	    		portletState.setReportEmailSend(aReq.getParameter("reportEmailSend"));
	    		
	        	
	        	
	        	String hql="";
	        	String errorMessage=null;
	        	DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
	        	
	        	try{
		        	if(portletState.getStartDate()!=null && portletState.getStartDate().length()>0 && 
		        			portletState.getEndDate()!=null && portletState.getEndDate().length()>0)
		        	{
		        		
		        		Date dd1 = df.parse(portletState.getStartDate());
		        		Date dd2 = df.parse(portletState.getEndDate());
		        		
		        		Timestamp t1 = new Timestamp(dd1.getTime());
		        		Date dd11 = new Date(t1.getTime());
		        		
		        		Timestamp t2 = new Timestamp(dd2.getTime());
		        		Date dd12 = new Date(t2.getTime());
		        		
		        		log.info(df.format(dd11));
		        		log.info(df.format(dd11));
		        		if(t1.getTime()<t2.getTime())
		        		{
		        			hql += " (ph.dateCreated >= '" + df.format(dd11) + "' AND  ph.dateCreated <= '" + df.format(dd12) + "') ";
		        		}else if(t2.getTime()<t1.getTime())
		        		{
		        			hql += " (ph.dateCreated >= '" + df.format(dd12) + "' AND  ph.dateCreated <= '" + df.format(dd11) + "') ";
		        		}else {
		        			hql += " (ph.dateCreated >= '" + df.format(dd11) + "' AND  ph.dateCreated <= '" + df.format(dd12) + "') ";
		        		}
		        		log.info(hql);
		        	}else if(portletState.getStartDate()!=null && portletState.getStartDate().length()>0)
		        	{
		        		Date dd1 = df.parse(portletState.getStartDate());
		        		Timestamp t1 = new Timestamp(dd1.getTime());
		        		Date dd11 = new Date(t1.getTime());
		        		hql +=  " ph.dateCreated = ('" + df.format(dd11) + "') ";
		        		log.info(hql);
		        	}else if(portletState.getEndDate()!=null && portletState.getEndDate().length()>0)
		        	{
		        		Date dd1 = df.parse(portletState.getEndDate());
		        		Timestamp t1 = new Timestamp(dd1.getTime());
		        		Date dd11 = new Date(t1.getTime());
		        		hql +=  " ph.dateCreated = ('" + df.format(dd11) + "') ";
		        		log.info(hql);
		        	}
	        	}catch(ParseException e)
	        	{
	        		errorMessage = "Invalid date provided for one of the dates";
	        		log.info(errorMessage);
	        	}
	        	
	        	if(portletState.getApplicantNumber()!=null && portletState.getApplicantNumber().length()>0)
	        	{
	        		hql +=  hql.length()>0 ? " AND ph.applicant.applicantNumber = '" + portletState.getApplicantNumber()+ "' " :  "ph.applicant.applicantNumber = '" + portletState.getApplicantNumber()+ "' ";
	        	}
	        	
	        	if(portletState.getApplicationNumber()!=null && portletState.getApplicationNumber().length()>0)
	        	{
	        		hql +=  hql.length()>0 ? " AND ph.applicationNumber = '" + portletState.getApplicationNumber() + "' " :  "ph.applicationNumber = '" + portletState.getApplicationNumber()+ "' ";
	        	}
	        	
	        	if(portletState.getApplicationStatus()!=null && portletState.getApplicationStatus().length()>0)
	        	{
	        		hql +=  hql.length()>0 ? " AND lower(ph.status) = lower('" + portletState.getApplicationStatus()+ "') " :  "lower(ph.status) = lower('" + portletState.getApplicationStatus()+ "') ";
	        	}
	        	
	        	if(portletState.getCountrySelected()!=null && portletState.getCountrySelected().length()>0)
	        	{
	        		hql +=  hql.length()>0 ? " AND lower(ph.country.name) = lower('" + portletState.getCountrySelected() + "') " :  "lower(ph.country.name) = lower('" + portletState.getCountrySelected() + "') ";
	        	}
	        	
	        	if(portletState.getItemCategory()!=null && portletState.getItemCategory().length()>0)
	        	{
	        		hql +=  hql.length()>0 ? " AND lower(ph.itemCategory.itemCategoryName) = lower('" + portletState.getItemCategory()+ "') " :  "lower(ph.itemCategory.itemCategoryName) = lower('" + portletState.getItemCategory()+ "') ";
	        	}
	        	
	        	if(portletState.getExceptionType()!=null && portletState.getExceptionType().length()>0)
	        	{
	        		hql +=  hql.length()>0 ? " AND ph.exceptionType = '" + (portletState.getExceptionType().equals("Yes") ? "true" : "false") + "' " :  "ph.exceptionType = '" + (portletState.getExceptionType().equals("Yes") ? "true" : "false") + "' ";
	        	}
	
	        	if(portletState.getPortOfEntry()!=null && !portletState.getPortOfEntry().equals("-1") && portletState.getPortOfEntry().length()>0)
	        	{
	        		hql +=  hql.length()>0 ? " AND (ph.portCode.portCode = '" + portletState.getPortOfEntry()+ "') " :  "(ph.portCode.portCode = '" + portletState.getPortOfEntry()+ "') ";
	        	}
	        	        	
	        	
	        	
	        	
	        	if(hql!=null && hql.length()>0)
	        	{
	        		hql= "select ph from Application ph WHERE "+ hql + " ORDER by ph.dateCreated DESC";
	        	}
	        	log.info("HQL for report ==" + hql);
	        	Collection<Application> appList = (Collection<Application>)portletState.getReportPortletUtil().runReportHQL(hql);
	        	if(appList!=null && appList.size()>0)
	        	{
	        		log.info("appList size===" + appList.size());
	        		portletState.setApplicationList(appList);
	        		ArrayList<ArrayList<String>> payHistParent= new ArrayList<ArrayList<String>>();
	        		
	        		/*****LABEL*****/
	        		ArrayList<String> labelRow = new ArrayList<String>();
	        		
	        		
	        		/***Conetent****/
	
	        		
	        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	        		if(portletState.getDefaultColumnShow()!=null && portletState.getDefaultColumnShow().equals("1"))
	    			{
	        				labelRow.add("DATE CREATED");
	    					labelRow.add("APPLICATION NO.");
	    					labelRow.add("APPLICANT NAME");
	                		labelRow.add("TREATED BY EXCEPTION RULE");
	                		labelRow.add("COUNTRY IMPORTED FROM");
	                		labelRow.add("APPLICATION STATUS");
	                		labelRow.add("IMPORTATION ITEM CATEGORY");
	                		labelRow.add("IMPORTATION PORT");
	    			}else
	    			{
	                	if(portletState.getShowDateCreated()!=null && portletState.getShowDateCreated().equalsIgnoreCase("DATECREATED"))
	                		labelRow.add("DATE CREATED");
	    				if(portletState.getShowApplicationNumber()!=null && portletState.getShowApplicationNumber().equalsIgnoreCase("APPLICATIONNUMBER"))
	                    	labelRow.add("APPLICATION NO.");
	                	if(portletState.getShowStatus()!=null && portletState.getShowStatus().equalsIgnoreCase("STATUS"))
	                		labelRow.add("APPLICATION STATUS");
	                	if(portletState.getShowApplicantName()!=null && portletState.getShowApplicantName().equalsIgnoreCase("APPLICANTNAME"))
	                		labelRow.add("APPLICANT NAME");
	                	if(portletState.getShowException()!=null && portletState.getShowException().equalsIgnoreCase("TREATEDBYEXCEPTION"))
	                		labelRow.add("TREATED BY EXCEPTION RULE");
	                	if(portletState.getShowImportationPort()!=null && portletState.getShowImportationPort().equalsIgnoreCase("IMPORTATIONPORT"))
	                		labelRow.add("IMPORTATION PORT");
	                	if(portletState.getShowItemCategory()!=null && portletState.getShowItemCategory().equalsIgnoreCase("ITEMCATEGORY"))
	                		labelRow.add("IMPORTATION ITEM CATEGORY");
	    			}
	        		for(Iterator<Application> it = appList.iterator(); it.hasNext();)
	        		{
	        			Application app = it.next();
	        			ArrayList<String> payHistoryRow = new ArrayList<String>();
	        			if(portletState.getDefaultColumnShow()!=null && portletState.getDefaultColumnShow().equals("1"))
	        			{
	        				payHistoryRow.add(app.getDateCreated()!=null ? sdf.format(new Date(app.getDateCreated().getTime())) : "N/A");
	        				payHistoryRow.add(app.getApplicationNumber()!=null ? app.getApplicationNumber() : "N/A");
	        				payHistoryRow.add(app.getApplicant()!=null ? (app.getApplicant().getApplicantNumber() + " - " + app.getApplicant().getPortalUser().getFirstName() + " " + app.getApplicant().getPortalUser().getSurname() + " ") : "N/A");
	        				payHistoryRow.add(app.getExceptionType()!=null && app.getExceptionType().equals(Boolean.FALSE) ? "False" : (app.getExceptionType()!=null && app.getExceptionType().equals(Boolean.TRUE) ? "True" : "Not Processed Yet"));
	        				payHistoryRow.add(app.getCountry()!=null ? app.getCountry().getName() : "N/A");
	        				payHistoryRow.add(app.getStatus()!=null ? app.getStatus().getValue() : "N/A");
	        				payHistoryRow.add(app.getItemCategory()!=null ? app.getItemCategory().getItemCategoryName() : "N/A");
	        				payHistoryRow.add(app.getPortCode()!=null ? app.getPortCode().getPortCode() : "N/A");
	        				
	        				
	        			}else
	        			{
	
	                    	if(portletState.getShowDateCreated()!=null && portletState.getShowDateCreated().equalsIgnoreCase("DATECREATED"))
	                    		payHistoryRow.add(app.getDateCreated()!=null ? sdf.format(new Date(app.getDateCreated().getTime())) : "N/A");
	        				if(portletState.getShowApplicationNumber()!=null && portletState.getShowApplicationNumber().equalsIgnoreCase("APPLICATIONNUMBER"))
	        					payHistoryRow.add(app.getApplicationNumber()!=null ? app.getApplicationNumber() : "N/A");
	                    	if(portletState.getShowStatus()!=null && portletState.getShowStatus().equalsIgnoreCase("STATUS"))
	            				payHistoryRow.add(app.getStatus()!=null ? app.getStatus().getValue() : "N/A");
	                    	if(portletState.getShowApplicantName()!=null && portletState.getShowApplicantName().equalsIgnoreCase("APPLICANTNAME"))
	            				payHistoryRow.add(app.getApplicant()!=null ? (app.getApplicant().getApplicantNumber() + " - " + app.getApplicant().getPortalUser().getFirstName() + " " + app.getApplicant().getPortalUser().getSurname() + " ") : "N/A");
	                    	if(portletState.getShowException()!=null && portletState.getShowException().equalsIgnoreCase("TREATEDBYEXCEPTION"))
	                    		payHistoryRow.add(app.getExceptionType()!=null && app.getExceptionType().equals(Boolean.FALSE) ? "False" : (app.getExceptionType()!=null && app.getExceptionType().equals(Boolean.TRUE) ? "True" : "Not Processed Yet"));
	                    	if(portletState.getShowImportationPort()!=null && portletState.getShowImportationPort().equalsIgnoreCase("IMPORTATIONPORT"))
	            				payHistoryRow.add(app.getPortCode()!=null ? app.getPortCode().getPortCode() : "N/A");
	                    	if(portletState.getShowItemCategory()!=null && portletState.getShowItemCategory().equalsIgnoreCase("ITEMCATEGORY"))
	            				payHistoryRow.add(app.getItemCategory()!=null ? app.getItemCategory().getItemCategoryName() : "N/A");
	        			}
	        			payHistParent.add(payHistoryRow);
	        			
	        			
	        			log.info("app id = " + app.getId());
	        		}
	        		
	        		
	        		log.info("-----------------user dir is " + System.getProperty("user.dir"));
	        		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	        		Timestamp tstamp = new Timestamp((new Date()).getTime());
	//                String userDirFile = System.getProperty("user.dir") + File.separator + "Payments" + File.separator + 
	//                		"plr_for_" + portletState.getPortalUser().getCompany().
	//                		getCompanyRCNumber().toLowerCase().replace(" ", "_") + "_on_" + sdf.format(new Date(tstamp.getTime()));
	//        		String userDirFile = aReq.getScheme() + "://"
	//            			+ aReq.getServerName() + ":" + aReq.getServerPort()
	//            			+ File.separator + "resources" + File.separator + "Payments" + File.separator + 
	//                		"applications_reports_for_on_" + sdf.format(new Date(tstamp.getTime()));
	        		
	        		String userDir = System.getProperty("user.dir");
	        		String sep = File.separator;
	        		userDir = "C:/jcodes/dev/appservers/ecims/webapps/resources";
	        		String filname = "applications_reports_for_on_" + sdfA.format(new Date(tstamp.getTime())).replace(" ", "") + ".xls";
	        		String userDirFile1 = userDir +sep+"ReportEngine"+sep+"Reports" + File.separator + "Payments" + File.separator + filname;
	                		
	        				
	//        				"/resources" + File.separator + "Payments" + File.separator + 
	//                		"plr_for_" + portletState.getPortalUser().getCompany().
	//                		getCompanyRCNumber().toLowerCase().replace(" ", "_") + "_on_" + sdf.format(new Date(tstamp.getTime()));
	                log.info("File: " + userDirFile1);
	        		WriteExcel writeExcel = new WriteExcel(userDirFile1, 
	        				"EUC Applications Listing Report - Generated " + sdf1.format(new Date(tstamp.getTime())), 
	        				labelRow, payHistParent );
	        		new Util().pushAuditTrail(swpService, filname, ECIMSConstants.GENERATE_REPORTS_XLS, 
							portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
	        		try {
						writeExcel.write();
						portletState.addSuccess(aReq, "Report Generated successfully. To download your report, right-click on the link below and " +
								"click on SAVE LINK AS option.<br>Download Report: " +
								"<a target='blank' href='/resources/ReportEngine/Reports/Payments/" +filname+"'>EUC Application Listings</a>", portletState);
						aRes.setRenderParameter("jspPage", "/html/reportportlet/filterresultsforapplications.jsp");
						portletState.setFilName(filname);
					} catch (WriteException e) {
						// TODO Auto-generated catch block
						portletState.addError(aReq, "We experienced errors generating your report. Please try again.", portletState);
						aRes.setRenderParameter("jspPage", "/html/reportportlet/filterresultsforapplications.jsp");
					}
	        	}
	        	else
	        	{
	        		portletState.addError(aReq, "There are currently no applications matching the criteria you provided. Please refine your filter criteria and run the report again.", portletState);
					aRes.setRenderParameter("jspPage", "/html/reportportlet/filterresultsforapplications.jsp");
	        	}
        	}
        	
        	
        }
		
	}


	private void selectReportType(ActionRequest aReq, ActionResponse aRes,
			ReportPortletState portletState) {
		// TODO Auto-generated method stub
		String reportType = aReq.getParameter("reportSelected");
		if(reportType!=null && !reportType.equals("-1"))
		{
			portletState.setSelectedReportType(reportType);
			if(reportType.equalsIgnoreCase(Application.class.getSimpleName()))
			{
				portletState.setPortList((Collection<PortCode>)portletState.getReportPortletUtil().getAllEntityObjects(PortCode.class));
				portletState.setCountryList((Collection<Country>)portletState.getReportPortletUtil().getAllEntityObjects(Country.class));
				portletState.setItemCategoryList((Collection<ItemCategory>)portletState.getReportPortletUtil().getAllEntityObjects(ItemCategory.class));
				aRes.setRenderParameter("jspPage", "/html/reportportlet/filterresultsforapplications.jsp");
			}
			else if(reportType.equalsIgnoreCase(PortalUser.class.getSimpleName()))
			{
				portletState.setPortList((Collection<PortCode>)portletState.getReportPortletUtil().getAllEntityObjects(PortCode.class));
				portletState.setStateList((Collection<State>)portletState.getReportPortletUtil().getAllEntityObjects(State.class));
				portletState.setAgencyList((Collection<Agency>)portletState.getReportPortletUtil().getAllEntityObjects(Agency.class));
				aRes.setRenderParameter("jspPage", "/html/reportportlet/filterresultsforportaluser.jsp");
			}else if(reportType.equalsIgnoreCase(Certificate.class.getSimpleName()))
			{
				aRes.setRenderParameter("jspPage", "/html/reportportlet/filterresultsforcertificate.jsp");
			}
		}else
		{
			
		}
	}


}
