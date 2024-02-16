package com.ecims.portlet.audittrail;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

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

import org.apache.log4j.Logger;

import smartpay.audittrail.AuditTrail;
import smartpay.entity.Applicant;
import smartpay.entity.Application;
import smartpay.entity.ApplicationAttachment;
import smartpay.entity.ApplicationFlag;
import smartpay.entity.ApplicationWorkflow;
import smartpay.entity.Certificate;
import smartpay.entity.PortalUser;
import smartpay.service.SwpService;

import com.ecims.commins.ECIMSConstants;
import com.ecims.commins.Util;
import com.ecims.commins.WriteExcel;
import com.ecims.portlet.audittrail.AuditTrailPortlet;
import com.ecims.portlet.audittrail.AuditTrailPortletState;
import com.ecims.portlet.audittrail.AuditTrailPortletState.AUDITTRAIL_ACTIONS;
import com.ecims.portlet.audittrail.AuditTrailPortletUtil;
import com.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalService;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class AuditTrailPortlet
 */
public class AuditTrailPortlet extends MVCPortlet {
	private Logger log = Logger.getLogger(AuditTrailPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	AuditTrailPortletUtil util = AuditTrailPortletUtil.getInstance();
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
		AuditTrailPortletState portletState = 
				AuditTrailPortletState.getInstance(renderRequest, renderResponse);

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
		
		AuditTrailPortletState portletState = AuditTrailPortletState.getInstance(aReq, aRes);
		
		
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
        if(action.equalsIgnoreCase(AUDITTRAIL_ACTIONS.GENERATE.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	generateExcelDoc(portletState, portletState.getAuditTrailListing(), aReq, aRes);
        		
        }
	}

	private void generateExcelDoc(AuditTrailPortletState portletState, Collection<AuditTrail> auditTrailListing, 
			ActionRequest aReq, ActionResponse aRes) {
		// TODO Auto-generated method stub
		
		ArrayList<String> labelRow = new ArrayList<String>();
		
		
		/***Conetent****/

		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		labelRow.add("ACTIVITY DATE");
		labelRow.add("ACTORS NAME");
		labelRow.add("DESCRIPTION OF ACTION");
		labelRow.add("ACTORS IP ADDRESS");
		ArrayList<ArrayList<String>> parentRow= new ArrayList<ArrayList<String>>();
		
		for(Iterator<AuditTrail> it = auditTrailListing.iterator(); it.hasNext();)
		{
			AuditTrail adLst = it.next();
			ArrayList<String> auditRow = new ArrayList<String>();
			Long usrId = adLst.getUserId()!=null ? (Long.valueOf(adLst.getUserId())) : 0L;
			
			String dt = adLst.getDate()!=null ? sdf.format(new Date(adLst.getDate().getTime())) : "N/A";
			String uId = adLst.getUserId()!=null ? adLst.getUserId() : "N/A";
			String act1 = adLst.getAction()!=null ? (adLst.getAction()) : "N/A";
			String ip = adLst.getIpAddress()!=null ? adLst.getIpAddress() : "N/A";
			
			if(usrId!=0L)
			{
				//log.info("usrId!=0L");
				
				if(adLst.getAction()!=null && adLst.getAction().equalsIgnoreCase("APPLICANT DISAPPROVED"))
				{
					auditRow.add(dt);
					try{
						Long userId = Long.valueOf(uId);
						User us = UserLocalServiceUtil.getUserById(userId);
						auditRow.add(us.getFirstName() + " " + us.getLastName());
					}catch(Exception e)
					{
						e.printStackTrace();
						auditRow.add(adLst.getUserId()!=null ? adLst.getUserId() : "N/A");
					}
					try{
						Long userId = Long.valueOf(uId);
						PortalUser pu = (PortalUser)portletState.getAuditTrailPortletUtil().
								getEntityObjectById(Applicant.class, Long.valueOf(adLst.getActivity().trim()));
						auditRow.add("Applicant " + pu.getFirstName() + " " + pu.getSurname() + " SignUp Disapproved");
					}catch(Exception e)
					{
						e.printStackTrace();
						auditRow.add("N/A");
					}
					
					String activity = null;
					auditRow.add(adLst.getIpAddress()!=null ? adLst.getIpAddress() : "N/A");
				}else
				{
					auditRow.add(dt);
					try{
						Long userId = Long.valueOf(uId);
						User us = UserLocalServiceUtil.getUserById(userId);
						auditRow.add(us.getFirstName() + " " + us.getLastName());
					}catch(Exception e)
					{
						e.printStackTrace();
						auditRow.add(adLst.getUserId()!=null ? adLst.getUserId() : "N/A");
					}
					
					String activity = null;
					activity = whatActivityIsThis(adLst, portletState);
					auditRow.add(activity == null ? "N/A" : activity);
					auditRow.add(adLst.getIpAddress()!=null ? adLst.getIpAddress() : "N/A");
				}
				
			}else
			{
				//log.info("usrId==0L");
				auditRow.add(dt);
				try{
					Long userId = Long.valueOf(uId);
					User us = UserLocalServiceUtil.getUserById(userId);
					auditRow.add(us.getFirstName() + " " + us.getLastName());
				}catch(Exception e)
				{
					e.printStackTrace();
					auditRow.add(adLst.getUserId()!=null ? adLst.getUserId() : "N/A");
				}
				
				String activity = null;
				activity = whatActivityIsThis(adLst, portletState);
				auditRow.add(activity == null ? "N/A" : activity);
				auditRow.add(adLst.getIpAddress()!=null ? adLst.getIpAddress() : "N/A");
			}
			parentRow.add(auditRow);
			
			//log.info("app id = " + adLst.getId());
		}
		
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat sdfA = new SimpleDateFormat("dd_MM_yyyy_HH_mm");
		Timestamp tstamp = new Timestamp((new Date()).getTime());
//        String userDirFile = System.getProperty("user.dir") + File.separator + "Payments" + File.separator + 
//        		"plr_for_" + portletState.getPortalUser().getCompany().
//        		getCompanyRCNumber().toLowerCase().replace(" ", "_") + "_on_" + sdf.format(new Date(tstamp.getTime()));
//		String userDirFile = aReq.getScheme() + "://"
//    			+ aReq.getServerName() + ":" + aReq.getServerPort()
//    			+ File.separator + "resources" + File.separator + "Payments" + File.separator + 
//        		"applications_reports_for_on_" + sdf.format(new Date(tstamp.getTime()));
		
		String userDir = System.getProperty("user.dir");
		userDir = "C:/jcodes/dev/appservers/ecims/webapps/resources";
		String sep = File.separator;
		String filname = "audit_trail_generated_on_" + sdfA.format(new Date(tstamp.getTime())).replace(" ", "") + ".xls";
		String userDirFile1 = userDir +sep+"ReportEngine"+sep+"Reports" + File.separator + "Payments" + File.separator + filname;
        		
				
//				"/resources" + File.separator + "Payments" + File.separator + 
//        		"plr_for_" + portletState.getPortalUser().getCompany().
//        		getCompanyRCNumber().toLowerCase().replace(" ", "_") + "_on_" + sdf.format(new Date(tstamp.getTime()));
        log.info("File: " + userDirFile1);
		WriteExcel writeExcel = new WriteExcel(userDirFile1, 
				"Audit Trail Listing - Generated " + sdf1.format(new Date(tstamp.getTime())), 
				labelRow, parentRow );
		new Util().pushAuditTrail(swpService, filname, ECIMSConstants.GENERATE_AUDIT_XLS, 
				portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
		try {
			writeExcel.write();
			portletState.addSuccess(aReq, "Audit Trail Report Generated successfully. To download your audit trail, right-click on the link below and " +
					"click on SAVE LINK AS option.<br>Download Audit Trail Report: " +
					"<a target='blank' href='/resources/ReportEngine/Reports/Payments/" +filname+"'>Audit Trail Listings</a>", portletState);
			aRes.setRenderParameter("jspPage", "/html/audittrailportlet/audittraillisting.jsp");
			//portletState.setFilName(filname);
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			portletState.addError(aReq, "We experienced errors generating your report. Please try again.", portletState);
			aRes.setRenderParameter("jspPage", "/html/audittrailportlet/audittraillisting.jsp");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			portletState.addError(aReq, "We experienced errors generating your report. Please try again.", portletState);
			aRes.setRenderParameter("jspPage", "/html/audittrailportlet/audittraillisting.jsp");
		}
	}

	private String whatActivityIsThis(AuditTrail ad, AuditTrailPortletState portletState) {
		// TODO Auto-generated method stub
		String description = null;
		PortalUser pu = portletState.getAuditTrailPortletUtil().getPortalUserByUserId(ad.getUserId());
		String portalUserName  = "";
		if(pu!=null)
		{
			portalUserName = pu.getFirstName() + " " + pu.getSurname();
		}
		  
		  
		  if(ad.getActivity().equals(ECIMSConstants.APPLICANT_ACTIVATE))
		  {
			  description = "Activation of ECIMS Account By Applicant - " + portalUserName;
		  }else if(ad.getActivity().equals(ECIMSConstants.APPLICANT_SET_PASSWORD))
		  {
			  description = "Password Set By Portal User - " + portalUserName;
		  }else if(ad.getActivity().equals(ECIMSConstants.APPLICATION_FLAGGING))
		  {
			  try{
			  	ApplicationFlag apf = (ApplicationFlag)portletState.getAuditTrailPortletUtil().getEntityObjectById(ApplicationFlag.class, Long.valueOf(ad.getAction()));
			  
			  	description = "Application Flagging | Application Number: " + apf.getApplication().getApplicationNumber() + " | Applicant: " + portalUserName;
			  }catch(Exception e)
			  {
				  e.printStackTrace();
			  }
		  }else if(ad.getActivity().equals(ECIMSConstants.APPROVE_APPLICANT_SIGNUP))
		  {
			  description = "New Applicant SignUp Approval - " + portalUserName;
		  }else if(ad.getActivity().equals(ECIMSConstants.APPROVE_APPLICATION))
		  {
			  description = "New Application Request Approval - " + portalUserName;
		  }else if(ad.getActivity().equals(ECIMSConstants.ASSIGN_CERTIFICATE_NUMBER))
		  {
			  try{
				  	Certificate cert = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
				  
				  	description = "Assign Certificate Number | Certificate Number: " + cert.getApplicationNumber() + " | Carried out by " + portalUserName;
				  }catch(Exception e)
				  {
					  e.printStackTrace();
				  }
			  
		  }else if(ad.getActivity().equals(ECIMSConstants.AUTO_PROCESS_APPLICATION))
		  {
			  try{
				  	Certificate cert = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
				  
				  	description = "Auto Process Application | Certificate Number: " + cert.getApplicationNumber() + " | Carried out by " + portalUserName;
				  }catch(Exception e)
				  {
					  e.printStackTrace();
				  }
		  }else if(ad.getActivity().equals(ECIMSConstants.BLACKLIST_APPLICANT))
		  {
			  try{
			  PortalUser pu1 = portletState.getAuditTrailPortletUtil().getPortalUserByUserId(ad.getAction());
			  description = "BlackList Applicant - " + pu1.getFirstName() + " " + pu1.getSurname() + " | Carried out By " + portalUserName;
			  }catch(Exception e)
			  {
				  
			  }
		  }else if(ad.getActivity().equals(ECIMSConstants.CAPTURE_FINGER_PRINT))
		  {
			  try{
				  PortalUser pu1 = portletState.getAuditTrailPortletUtil().getPortalUserByUserId(ad.getAction());
				  description = "Capture Fingerprint | Carried out By " + pu1.getFirstName() + " " + pu1.getSurname() + "" + portalUserName;
				  }catch(Exception e)
				  {
					  
				  }
		  }else if(ad.getActivity().equals(ECIMSConstants.DEVALIDATE_APPLICATION_ATTACHMENT))
		  {
			  try{
				  ApplicationAttachment aa = (ApplicationAttachment)portletState.getAuditTrailPortletUtil().getEntityObjectById(ApplicationAttachment.class, Long.valueOf(ad.getAction()));
				  description = "Application Attachment Devalidation. Application Number: " + aa.getApplication().getApplicationNumber() + " | Carried out By " + portalUserName;
				  }catch(Exception e)
				  {
					  
				  }
			  
		  }else if(ad.getActivity().equals(ECIMSConstants.DISAPPROVE_APPLICATION))
		  {
			  try{
				  Application app = (Application)portletState.getAuditTrailPortletUtil().getEntityObjectById(Application.class, Long.valueOf(ad.getAction()));
				  description = "Application Disapproval. Application Number: " + app.getApplicationNumber() + " | Carried out by " + portalUserName;
				  }catch(Exception e)
				  {
					  
				  }
		  }else if(ad.getActivity().equals(ECIMSConstants.DISENDORSE_APPLICATION_REQUEST))
		  {
			  try{
				  ApplicationWorkflow app = (ApplicationWorkflow)portletState.getAuditTrailPortletUtil().getEntityObjectById(ApplicationWorkflow.class, Long.valueOf(ad.getAction()));
				  description = "Application Disendorsed. Application Number: " + app.getApplication().getApplicationNumber() + " | Carried out by " + portalUserName;
				  }catch(Exception e)
				  {
					  
				  }
		  }else if(ad.getActivity().equals(ECIMSConstants.ENDORSE_APPLICATION_REQUEST))
		  {
			  try{
				  ApplicationWorkflow app = (ApplicationWorkflow)portletState.getAuditTrailPortletUtil().getEntityObjectById(ApplicationWorkflow.class, Long.valueOf(ad.getAction()));
				  description = "Application Endorsement. Application Number: " + app.getApplication().getApplicationNumber() + " | Carried out by " + portalUserName;
				  }catch(Exception e)
				  {
					  
				  }
		  }else if(ad.getActivity().equals(ECIMSConstants.FORWARD_APPLICATION_REQUEST))
		  {
			  try{
				  ApplicationWorkflow app = (ApplicationWorkflow)portletState.getAuditTrailPortletUtil().getEntityObjectById(ApplicationWorkflow.class, Long.valueOf(ad.getAction()));
				  description = "Application Endorsement. Application Number: " + app.getApplication().getApplicationNumber() + " | Carried out by " + portalUserName;
				  }catch(Exception e)
				  {
					  
				  }
		  }else if(ad.getActivity().equals(ECIMSConstants.ISSUE_CERTIFICATE))
		  {
			  try{
				  Certificate cert = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
				  description = "Certificate Issuance Endorsement. Certificate Number: " + cert.getCertificateNo() + " | Carried out by " + portalUserName;
				  }catch(Exception e)
				  {
					  
				  }
		  }else if(ad.getActivity().equals(ECIMSConstants.NEW_APPLICATION_REQUEST))
		  {
			  try{
				  ApplicationWorkflow app = (ApplicationWorkflow)portletState.getAuditTrailPortletUtil().getEntityObjectById(ApplicationWorkflow.class, Long.valueOf(ad.getAction()));
				  description = "New Application Request. Application Number: " + app.getApplication().getApplicationNumber() + " | Carried out by " + portalUserName;
				  }catch(Exception e)
				  {
					  
				  }
		  }else if(ad.getActivity().equals(ECIMSConstants.RECALL_CERTIFICATE))
		  {
			  try{
				  Certificate cert = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
				  description = "Certificate Recall. Certificate Number: " + cert.getCertificateNo() + " | Carried out by " + portalUserName;
				  }catch(Exception e)
				  {
					  
				  }
		  }else if(ad.getActivity().equals(ECIMSConstants.USER_SIGNUP))
		  {
			  try{
				  PortalUser p1 = (PortalUser)portletState.getAuditTrailPortletUtil().getEntityObjectById(PortalUser.class, Long.valueOf(ad.getAction()));
				  description = "Portal User SignUp for Portal User: " + p1.getFirstName() + " " + p1.getSurname();
				  }catch(Exception e)
				  {
					  
				  }
		  }else if(ad.getActivity().equals(ECIMSConstants.VALIDATE_APPLICATION_ATTACHMENT))
		  {
			  try{
				  ApplicationAttachment aa = (ApplicationAttachment)portletState.getAuditTrailPortletUtil().getEntityObjectById(ApplicationAttachment.class, Long.valueOf(ad.getAction()));
				  description = "Application Attachment Validation. Application Number: " + aa.getApplication().getApplicationNumber() + " | Carried out By " + portalUserName;
				  }catch(Exception e)
				  {
					  
				  }
		  }else if(ad.getActivity().equals(ECIMSConstants.APPROVE_APPLICANT_SIGNUP))
		  {
			  try{
				  Applicant aa = (Applicant)portletState.getAuditTrailPortletUtil().getEntityObjectById(Applicant.class, Long.valueOf(ad.getAction()));
				  description = "Approve Applicant Sign Up. Applicant Number: " + aa.getApplicantNumber() + " | Carried out By " + portalUserName;
				  }catch(Exception e)
				  {
					  
				  }
		  }
		  else if(ad.getActivity().equals(ECIMSConstants.COLLECT_CERTIFICATE))
		  {
			  try{
				  Certificate aa = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
				  description = "Certificate Collection. Certificate Number: " + aa.getCertificateNo() + " | Carried out By " + portalUserName;
				  }catch(Exception e)
				  {
					  
				  }
		  }
		  else if(ad.getActivity().equals(ECIMSConstants.APPLICANT_UPDATE_PASSWORD))
		  {
			  try{
				  PortalUser aa = (PortalUser)portletState.getAuditTrailPortletUtil().getEntityObjectById(PortalUser.class, Long.valueOf(ad.getAction()));
				  description = "Password Update. Portal User: " + aa.getEmailAddress() + " | Carried out By " + portalUserName;
				  }catch(Exception e)
				  {
					  
				  }
		  }
		  else if(ad.getActivity().equals(ECIMSConstants.REJECT_CERTIFICATE))
		  {
			  try{
				  Certificate aa = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
				  description = "Certificate Printing Error. Certificate Number: " + aa.getCertificateNo() + " | Carried out By " + portalUserName;
				  }catch(Exception e)
				  {
					  
				  }
		  }
		  else if(ad.getActivity().equals(ECIMSConstants.DISPUTE_CERTIFICATE))
		  {
			  try{
				  Certificate aa = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
				  description = "Certificate Dispute. Certificate Number: " + aa.getCertificateNo() + " | Carried out By " + portalUserName;
				  }catch(Exception e)
				  {
					  
				  }
		  }
		  else if(ad.getActivity().equals(ECIMSConstants.UPLOAD_CERTIFICATE))
		  {
			  try{
				  Certificate aa = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
				  description = "Certificate Upload. Certificate Number: " + aa.getCertificateNo() + " | Carried out By " + portalUserName;
				  }catch(Exception e)
				  {
					  
				  }
		  }
		  else if(ad.getActivity().equals(ECIMSConstants.CANCEL_DISPUTE_CERTIFICATE))
		  {
			  try{
				  Certificate aa = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
				  description = "Certificate Dispute Cancelation. Certificate Number: " + aa.getCertificateNo() + " | Carried out By " + portalUserName;
				  }catch(Exception e)
				  {
					  
				  }
		  }
		  else if(ad.getActivity().equals(ECIMSConstants.GENERATE_REPORTS_XLS))
		  {
			  try{
				  //Certificate aa = (Certificate)portletState.getAuditTrailPortletUtil().getEntityObjectById(Certificate.class, Long.valueOf(ad.getAction()));
				  description = "Generate Reports. Report File Name: " + ad.getAction() + " | Carried out By " + portalUserName;
				  }catch(Exception e)
				  {
					  
				  }
		  }
		  
		  return description;
		  
	}

}
