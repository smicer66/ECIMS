package com.ecims.portlet.applicant;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
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
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;

import smartpay.audittrail.AuditTrail;
import smartpay.entity.Applicant;
import smartpay.entity.Company;
import smartpay.entity.IdentificationHistory;
import smartpay.entity.PortalUser;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.State;
import smartpay.entity.enumerations.ApplicantStatus;
import smartpay.entity.enumerations.ApplicantType;
import smartpay.entity.enumerations.IdentificationType;
import smartpay.entity.enumerations.KinRelationshipType;
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
import com.ecims.portlet.applicant.ApplicantPortletState.APPLICANT_ACTIONS;
import com.ecims.portlet.applicant.ApplicantPortletState.EUC_DESK_ACTIONS;
import com.ecims.portlet.applicant.ApplicantPortletState.VIEW_TABS;
import com.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.DuplicateUserEmailAddressException;
import com.liferay.portal.DuplicateUserScreenNameException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class ApplicantPortlet
 */
public class ApplicantPortlet extends MVCPortlet {
	private Logger log = Logger.getLogger(ApplicantPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	ApplicantPortletUtil util = ApplicantPortletUtil.getInstance();
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
		ApplicantPortletState portletState = 
				ApplicantPortletState.getInstance(renderRequest, renderResponse);

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
		
		ApplicantPortletState portletState = ApplicantPortletState.getInstance(aReq, aRes);
		
		
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
        if(action.equalsIgnoreCase(APPLICANT_ACTIONS.CREATE_A_PORTAL_USER_STEP_ONE.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	createApplicantStepOne(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(APPLICANT_ACTIONS.CHANGE_MY_PASSWORD.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	changePassword(aReq, aRes, swpService, portletState);
        }
        if(action.equalsIgnoreCase(APPLICANT_ACTIONS.CREATE_A_PORTAL_USER_STEP_TWO.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_TWO");
        	//Provide User details
        	createApplicantStepTwo(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(APPLICANT_ACTIONS.CREATE_A_PORTAL_USER_STEP_THREE.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_THREE");
        	//Preview Data
        	createApplicantStepThree(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(APPLICANT_ACTIONS.CREATE_A_PORTAL_USER_STEP_FOUR.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_FOUR");
        	//Populate And Create user
        	createApplicantStepFour(aReq, aRes, swpService, portletState);
        		
        }
        
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_ALL_APPLICANT.name()))
        {
        	//set corpoate indivudla listings
        	portletState.setApplicantListing(portletState.getApplicantPortletUtil().getAllApplicant());
        	aRes.setRenderParameter("jspPage", "/html/applicantportlet/applicantlisting/applicantlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_APPLICANT);
        }
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_APPROVED_APPLICANT_LISTINGS.name()))
        {
        	//set corpoate indivudla listings
        	portletState.setApplicantListing(portletState.getApplicantPortletUtil().getApplicantByStatus(ApplicantStatus.APPLICANT_STATUS_APPROVED));
        	aRes.setRenderParameter("jspPage", "/html/applicantportlet/applicantlisting/applicantlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_APPROVED_APPLICANT_LISTINGS);
        }
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_REJECTED_PORTAL_USER_REQUESTS.name()))
        {
        	//set corpoate indivudla listings
        	portletState.setApplicantListing(portletState.getApplicantPortletUtil().getApplicantByStatus(ApplicantStatus.APPLICANT_STATUS_REJECTED));
        	aRes.setRenderParameter("jspPage", "/html/applicantportlet/applicantlisting/applicantlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_REJECTED_PORTAL_USER_REQUESTS);
        }
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_NEW_APPLICANT_REQUESTS.name()))
        {
        	//set corpoate indivudla listings
        	portletState.setApplicantListing(portletState.getApplicantPortletUtil().getApplicantByStatus(ApplicantStatus.APPLICANT_STATUS_UNAPPROVED));
        	aRes.setRenderParameter("jspPage", "/html/applicantportlet/applicantlisting/applicantlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_NEW_APPLICANT_REQUESTS);
        }
        
        
        
        if(action.equalsIgnoreCase(APPLICANT_ACTIONS.CREATE_A_CORPORATE_PORTAL_USER_STEP_TWO.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_TWO");
        	//Provide User details
        	createCorporateApplicantStepTwo(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(APPLICANT_ACTIONS.CREATE_A_CORPORATE_PORTAL_USER_STEP_THREE.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_THREE");
        	//Preview Data
        	createCorporateApplicantStepThree(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(APPLICANT_ACTIONS.CREATE_A_CORPORATE_PORTAL_USER_STEP_FOUR.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_FOUR");
        	//Populate And Create user
        	createCorporateApplicantStepFour(aReq, aRes, swpService, portletState);
        		
        }
        
        if(action.equalsIgnoreCase(APPLICANT_ACTIONS.ACTIVATE_PORTAL_USER_ACCOUNT.name()))
        {
        	log.info("ACTIVATE_PORTAL_USER_ACCOUNT");
        	//Populate And Create user
        	activatePortalUser(aReq, aRes, swpService, portletState);
        		
        }
        
        
        if(action.equalsIgnoreCase(APPLICANT_ACTIONS.CREATE_NEW_PASSWORD_PORTAL_USER_ACCOUNT.name()))
        {
        	log.info("ACTIVATE_PORTAL_USER_ACCOUNT");
        	//Populate And Create user
        	createNewPassword(aReq, aRes, swpService, portletState);
        		
        }
        
        
        if(action.equalsIgnoreCase(EUC_DESK_ACTIONS.LIST_PORTAL_USERS_ACTIONS.name()))
        {
        	//set corpoate indivudla listings
        		// TODO Auto-generated method stub
    		String selectedApplicant= aReq.getParameter("selectedPortalUser");
    		String selectedApplicantAction= aReq.getParameter("selectedPortalUserAction");
    		if(selectedApplicantAction.equalsIgnoreCase("view"))
    		{
            	viewApplicant(aReq, aRes, portletState, selectedApplicant);
    		}else if(selectedApplicantAction.equalsIgnoreCase("delete"))
    		{
    			if(portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_MANAGE_APPLICANT))
    			{
    				deleteApplicant(aReq, aRes, portletState, selectedApplicant);
    			}
    			else
    			{
    				portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
    			}
    		}else if(selectedApplicantAction.equalsIgnoreCase("approve"))
    		{
    			if(portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_MANAGE_APPLICANT))
    			{
    				approveApplicant(aReq, aRes, portletState, selectedApplicant);
	    		}
				else
				{
					portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
				}
    		}else if(selectedApplicantAction.equalsIgnoreCase("disapprove"))
    		{
    			if(portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_MANAGE_APPLICANT))
    			{
    				disapproveApplicant(aReq, aRes, portletState, selectedApplicant);
    			}
				else
				{
					portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
				}
    		}
        	
        }
        
        
        if(action.equalsIgnoreCase(EUC_DESK_ACTIONS.APPROVE_PORTAL_USER_SIGNUP.name()))
        {
        	//set corpoate indivudla listings
        		// TODO Auto-generated method stub
        	if(portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_MANAGE_APPLICANT))
			{
	    		//String selectedApplicant= Long.toString(portletState.getSelectedApplicant().getId());
	    		String selectedApplicantAction= aReq.getParameter("act");
	    		String selectedApplicant= aReq.getParameter("actId");
	    		log.info("acct Id ==" + selectedApplicant);
	    		log.info("selectedApplicantAction ==" + selectedApplicantAction);
	    		if(selectedApplicantAction.equalsIgnoreCase("back"))
	    		{
	    			portletState.setSelectedApplicant(null);
	            	aRes.setRenderParameter("jspPage", "/html/applicantportlet/applicantlisting/applicantlisting.jsp");
	            	
	    		}else if(selectedApplicantAction.equalsIgnoreCase("approve"))
	    		{
	    			approveApplicant(aReq, aRes, portletState, selectedApplicant);
	    		}else if(selectedApplicantAction.equalsIgnoreCase("reject"))
	    		{
	    			if(aReq.getParameter("rejectreason")!=null && aReq.getParameter("rejectreason").length()>0)
	    				disapproveApplicant(aReq, aRes, portletState, selectedApplicant);
	    			else
	    			{
	    				portletState.addError(aReq, "To disapprove a signup request, ensure you provide reasons for this in the reasons box", portletState);
	    				aRes.setRenderParameter("jspPage", "/html/applicantportlet/applicantlisting/applicantlisting.jsp");
	    			}
	    		}
			}else
			{
				portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
			}
        	
        }
        
        
        
	}
	
	
	
	
	

	private void changePassword(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, ApplicantPortletState portletState) {
		// TODO Auto-generated method stub
		String oldpassword = aReq.getParameter("oldpassword");
		String newpassword = aReq.getParameter("newpassword");
		String rnewpassword = aReq.getParameter("rnewpassword");
		PortalUser pu = portletState.getPortalUser();
		boolean isUserOldPasswordCorrect = new Util().isUserEmailPasswordValid(pu, 
				pu.getEmailAddress(), 
				oldpassword, swpService2);
		if(isUserOldPasswordCorrect)
		{
			try
			{
				User us = null;
				us = UserLocalServiceUtil.updatePassword(Long.valueOf(pu.getUserId()), 
					newpassword, rnewpassword, false);
				if(us==null)
					portletState.addError(aReq, "Password change failed. Please try again", portletState);
				else
				{
					portletState.addSuccess(aReq, "Password change was successful. Thanks", portletState);
					aRes.setRenderParameter("jspPage", "/html/applicantportlet/activation/setpasswordstatus.jsp");
				}
			}catch(NumberFormatException e)
			{
				e.printStackTrace();
				portletState.addError(aReq, "Password change failed. Please try again", portletState);
			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				portletState.addError(aReq, "Password change failed. Please try again", portletState);
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				portletState.addError(aReq, "Password change failed. Please try again", portletState);
			}
			
		}else
		{
			portletState.addError(aReq, "Password change failed. Please try again", portletState);
		}
		
	}

	private void createNewPassword(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, ApplicantPortletState portletState) {
		// TODO Auto-generated method stub
		String password =  aReq.getParameter("password");
		String confirmPassword =  aReq.getParameter("confirmPassword");
		boolean proceed = false;
		if(password!=null && confirmPassword!=null && password.equals(confirmPassword))
		{
			User user = null;
			try {
				user = UserLocalServiceUtil.updatePassword(Long.valueOf(portletState.getActivationPortalUser().getUserId()), 
						password, confirmPassword, false);
				if(user!=null)
				{
					PortalUser pu1 = portletState.getActivationPortalUser();
					pu1.setPasscode(null);
					pu1.setActivationCode(null);
					swpService.updateRecord(pu1);
					
					new Util().pushAuditTrail(swpService2, pu1.getId().toString(), ECIMSConstants.APPLICANT_SET_PASSWORD, 
							portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
					portletState.addSuccess(aReq, "Your ECIMS account has  been activated successfully.", portletState);
					aRes.setRenderParameter("jspPage", "/html/applicantportlet/activation/setpasswordstatus.jsp");
					proceed = true;
				}else
				{
					portletState.addError(aReq, "ECIMS account activation failed. Please try again.", portletState);
					aRes.setRenderParameter("jspPage", "/html/applicantportlet/activation/setpassword.jsp");
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				portletState.addError(aReq, "ECIMS account activation failed. Invalid account found. Please contact " +
						"administrator of this site.", portletState);
				aRes.setRenderParameter("jspPage", "/html/applicantportlet/activation/setpassword.jsp");
				
			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				portletState.addError(aReq, "ECIMS account activation failed. Please try again.", portletState);
				aRes.setRenderParameter("jspPage", "/html/applicantportlet/activation/setpassword.jsp");
			} catch (SystemException e) {
				// TODO Auto-generated ca
				portletState.addError(aReq, "ECIMS account activation failed. Please try again.", portletState);
				aRes.setRenderParameter("jspPage", "/html/applicantportlet/activation/setpassword.jsp");
			}
			
			if(proceed==false)
			{
				try {
					UserLocalServiceUtil.updateStatus(Long.valueOf(portletState.getActivationPortalUser().getUserId()), 1);
					User lpUser = null;
					Long uId = null;
					uId = Long.valueOf(portletState.getActivationPortalUser().getUserId());
					try {
						lpUser = UserLocalServiceUtil.getUserById(uId);
					} catch (PortalException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SystemException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					UserLocalServiceUtil.updateLockout(lpUser, true);
					
					//104
					PortalUser pu1 = portletState.getActivationPortalUser();
					pu1.setStatus(UserStatus.USER_STATUS_INACTIVE);
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
				
			}
		}else
		{
			portletState.addError(aReq, "Your passwords do not match. Ensure you provide the same password in both fields.", portletState);
			aRes.setRenderParameter("jspPage", "/html/applicantportlet/activation/setpassword.jsp");
		}
		
	}

	private void activatePortalUser(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, ApplicantPortletState portletState) {
		// TODO Auto-generated method stub
		String emailAddress = aReq.getParameter("activationEmailAddress");
		String activationCode = aReq.getParameter("activationCode");
		String activationCode1 = aReq.getParameter("activationCode2");
		
		
		if(activationCode.equals(activationCode1))
		{
			if(emailAddress!=null && emailAddress.trim().length()>0)
			{
				PortalUser pu = portletState.getApplicantPortletUtil().getPortalUserByEmailAddress(emailAddress);
				//Test 102
				if(pu.getStatus().equals(UserStatus.USER_STATUS_ACTIVE) && pu.getPasscode().equals(activationCode))
				{
					User lpUser = null;
					Long uId = null;
					uId = Long.valueOf(pu.getUserId());
					try {
						lpUser = UserLocalServiceUtil.getUserById(uId);
					} catch (PortalException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SystemException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if(lpUser!=null)
					{
						
						
						
						
						//UserLocalServiceUtil.updateLockout(lpUser, false);
						try {
							UserLocalServiceUtil.updateStatus(uId, 0);
							UserLocalServiceUtil.updateLockout(lpUser, false);
							
							pu.setStatus(UserStatus.USER_STATUS_ACTIVE);
							pu.setPasscode(null);
							pu.setActivationCode(null);
							swpService.updateRecord(pu);
							
							new Util().pushAuditTrail(swpService2, pu.getId().toString(), ECIMSConstants.APPLICANT_ACTIVATE, 
									portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
							aRes.setRenderParameter("jspPage", "/html/applicantportlet/activation/setpassword.jsp");
						} catch (PortalException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							portletState.addError(aReq, "We experienced technical issues activating your account. Please try again later.", portletState);
							aRes.setRenderParameter("jspPage", "/html/applicantportlet/activation/activateaccount.jsp");
						} catch (SystemException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							portletState.addError(aReq, "We experienced technical issues activating your account. Please try again later.", portletState);
							aRes.setRenderParameter("jspPage", "/html/applicantportlet/activation/activateaccount.jsp");
						}
					}
					else
					{
						portletState.addError(aReq, "You are carrying out an invalid action. Please close this window if you have not received an " +
								"activation code to make use of on this page", portletState);
						aRes.setRenderParameter("jspPage", "/html/applicantportlet/activation/activateaccount.jsp");
					}
				}
				else
				{
					portletState.addError(aReq, "You are carrying out an invalid action. Please close this window if you have not received an " +
							"activation code to make use of on this page", portletState);
					aRes.setRenderParameter("jspPage", "/html/applicantportlet/activation/activateaccount.jsp");
				}
			}else
			{	
				portletState.addError(aReq, "Email address provided is invalid. Ensure you clicked on your activation link in your activation email", portletState);
				aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_company/stepthree.jsp");
			}
		}else
		{
			portletState.addError(aReq, "Activation Codes typed do not match. Ensure they match before proceeding.", portletState);
			aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_company/stepthree.jsp");
		}
	}

	
	private void viewApplicant(ActionRequest aReq, ActionResponse aRes,
			ApplicantPortletState portletState, String selectedApplicant) {
		// TODO Auto-generated method stub
		Long selectedApplicantLong = Long.valueOf(selectedApplicant);
		if(selectedApplicantLong!=null)
		{
			Applicant applicant = (Applicant)portletState.getApplicantPortletUtil().getEntityObjectById(Applicant.class, selectedApplicantLong);
			if(applicant!=null)
			{
				portletState.setSelectedApplicant(applicant);
				log.info("APPLICANT TYPE ===" + applicant.getApplicantType().getValue());
				if(applicant.getApplicantType().equals(ApplicantType.APPLICANT_TYPE_INDIVIDUAL))
					aRes.setRenderParameter("jspPage", "/html/applicantportlet/applicantlisting/viewapplicant_individual.jsp");
				else if(applicant.getApplicantType().equals(ApplicantType.APPLICANT_TYPE_CORPORATE))
					aRes.setRenderParameter("jspPage", "/html/applicantportlet/applicantlisting/viewapplicant_company.jsp");
				
			}else
			{
				portletState.addError(aReq, "Invalid applicant selected. Please select a valid applicant", portletState);
				aRes.setRenderParameter("jspPage", "/html/applicantportlet/applicantlisting/applicantlisting.jsp");
			}
		}else
		{
			portletState.addError(aReq, "Invalid applicant selected. Please select a valid applicant", portletState);
			aRes.setRenderParameter("jspPage", "/html/applicantportlet/applicantlisting/applicantlisting.jsp");
		}
	}

	private void deleteApplicant(ActionRequest aReq, ActionResponse aRes,
			ApplicantPortletState portletState, String selectedApplicant) {
		// TODO Auto-generated method stub
		Long selectedApplicantLong = Long.valueOf(selectedApplicant);
		if(selectedApplicantLong!=null)
		{
			Applicant applicant = (Applicant)portletState.getApplicantPortletUtil().getEntityObjectById(Applicant.class, selectedApplicantLong);
			if(applicant!=null)
			{
				PortalUser pu = applicant.getPortalUser();
				//Test 107
				applicant.setStatus(ApplicantStatus.APPLICANT_STATUS_DELETED);
				swpService.updateRecord(applicant);
				try {
					UserLocalServiceUtil.updateStatus(
							Long.valueOf(pu.getUserId()), 1);
					pu.setStatus(UserStatus.USER_STATUS_DELETED);
					swpService.updateRecord(pu);
					
					new Util().auditTrail("APPLICANT DELETE", applicant.getId().toString(), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId(), swpService);
					portletState.addSuccess(aReq, "SignUp request successfully deleted!", portletState);
					if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ALL_APPLICANT))
					{
						portletState.setApplicantListing(portletState.getApplicantPortletUtil().getAllApplicant());
						portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_APPLICANT);
					}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_APPROVED_APPLICANT_LISTINGS))
					{
						portletState.setApplicantListing(portletState.getApplicantPortletUtil().getApplicantByStatus(ApplicantStatus.APPLICANT_STATUS_APPROVED));
					}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_NEW_APPLICANT_REQUESTS))
					{
						portletState.setApplicantListing(portletState.getApplicantPortletUtil().getApplicantByStatus(ApplicantStatus.APPLICANT_STATUS_UNAPPROVED));
					}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_REJECTED_PORTAL_USER_REQUESTS))
					{
						portletState.setApplicantListing(portletState.getApplicantPortletUtil().getApplicantByStatus(ApplicantStatus.APPLICANT_STATUS_REJECTED));
					}else
					{
						portletState.setApplicantListing(portletState.getApplicantPortletUtil().getAllApplicant());
						portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_APPLICANT);
					}
					
					aRes.setRenderParameter("jspPage", "/html/applicantportlet/applicantlisting/applicantlisting.jsp");
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
				
				
			}else
			{
				portletState.addError(aReq, "Invalid applicant selected. Please select a valid applicant", portletState);
				aRes.setRenderParameter("jspPage", "/html/applicantportlet/applicantlisting/applicantlisting.jsp");
			}
		}else
		{
			portletState.addError(aReq, "Invalid applicant selected. Please select a valid applicant", portletState);
			aRes.setRenderParameter("jspPage", "/html/applicantportlet/applicantlisting/applicantlisting.jsp");
		}
	}

	private void disapproveApplicant(ActionRequest aReq, ActionResponse aRes,
			ApplicantPortletState portletState, String selectedApplicant) {
		// TODO Auto-generated method stub
		Long selectedApplicantLong = Long.valueOf(selectedApplicant);
		if(selectedApplicantLong!=null)
		{
			Applicant applicant = (Applicant)portletState.getApplicantPortletUtil().getEntityObjectById(Applicant.class, selectedApplicantLong);
			if(applicant!=null)
			{
//				applicant.setStatus(ApplicantStatus.APPLICANT_STATUS_REJECTED);
//				swpService.updateRecord(applicant);
//				
//				PortalUser pu = applicant.getPortalUser();
//				try {
//					UserLocalServiceUtil.updateStatus(
//							Long.valueOf(pu.getUserId()), 1);
//					pu.setStatus(UserStatus.USER_STATUS_INACTIVE);
//					swpService.updateRecord(pu);
//					activatePortalUserStatus(applicant.getPortalUser(), portletState, false);
//					new Util().auditTrail("APPLICANT DISAPPROVED", applicant.getId().toString(), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId(), swpService);
//					portletState.addSuccess(aReq, "SignUp request successfully disapproved!", portletState);
//					
//					if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ALL_APPLICANT))
//					{
//						portletState.setApplicantListing(portletState.getApplicantPortletUtil().getAllApplicant());
//					}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_APPROVED_APPLICANT_LISTINGS))
//					{
//						portletState.setApplicantListing(portletState.getApplicantPortletUtil().getApplicantByStatus(ApplicantStatus.APPLICANT_STATUS_APPROVED));
//					}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_NEW_APPLICANT_REQUESTS))
//					{
//						portletState.setApplicantListing(portletState.getApplicantPortletUtil().getApplicantByStatus(ApplicantStatus.APPLICANT_STATUS_UNAPPROVED));
//					}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_REJECTED_PORTAL_USER_REQUESTS))
//					{
//						portletState.setApplicantListing(portletState.getApplicantPortletUtil().getApplicantByStatus(ApplicantStatus.APPLICANT_STATUS_REJECTED));
//					}else
//					{
//						portletState.setApplicantListing(portletState.getApplicantPortletUtil().getAllApplicant());
//						portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_APPLICANT);
//					}
//				} catch (NumberFormatException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					portletState.addError(aReq, "Invalid applicant selected. Please start this process from the beginning!", portletState);
//				} catch (PortalException e) {
//					// TODO Auto-generated catch block
//					portletState.addError(aReq, "Invalid applicant selected. Please start this process from the beginning!", portletState);
//					e.printStackTrace();
//				} catch (SystemException e) {
//					// TODO Auto-generated catch block
//					portletState.addError(aReq, "System Error. Please start this process from the beginning!", portletState);
//					e.printStackTrace();
//				}
				
				applicant.setStatus(ApplicantStatus.APPLICANT_STATUS_REJECTED);
				//swpService.updateRecord(applicant);
				PortalUser pu = applicant.getPortalUser();
				//sswpService.deleteRecord(applicant);
				
				
				try {
					User lpUser = UserLocalServiceUtil.getUserById(Long.valueOf(pu.getUserId()));
					UserLocalServiceUtil.deleteUser(lpUser);
//					UserLocalServiceUtil.updateStatus(
//							Long.valueOf(pu.getUserId()), 1);
//					pu.setStatus(UserStatus.USER_STATUS_INACTIVE);
//					swpService.updateRecord(pu);
//					activatePortalUserStatus(applicant.getPortalUser(), portletState, false);
					
					
					Mailer mailer = new Mailer(portletState.getSendingEmail().getValue(), 
						portletState.getSendingEmailPassword().getValue(), 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()), 
						portletState.getSendingEmailUsername().getValue());
					mailer.emailSignUpDisapprovalRequest(pu, portletState.getSystemUrl().getValue(), 
							portletState.getApplicationName().getValue(), aReq.getParameter("rejectreason"));
					
					String smsMessage = "End-User Certificate signup request approved! \nPasscode: " + pu.getPasscode() + "\nCheck your email for an activation link to activate your ECIMS account" ;
					SendSms sendSms = new SendSms(pu.getPhoneNumber(), 
							smsMessage, 
							portletState.getMobileApplicationName().getValue(), 
							portletState.getProxyHost().getValue(), 
							portletState.getProxyPort().getValue());
					swpService.deleteRecord(applicant);
					swpService.deleteRecord(pu);
						
						
					new Util().auditTrail("APPLICANT DISAPPROVED", applicant.getPortalUser().getEmailAddress(), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId(), swpService);
					portletState.addSuccess(aReq, "SignUp request successfully disapproved!", portletState);
					
					if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ALL_APPLICANT))
					{
						portletState.setApplicantListing(portletState.getApplicantPortletUtil().getAllApplicant());
					}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_APPROVED_APPLICANT_LISTINGS))
					{
						portletState.setApplicantListing(portletState.getApplicantPortletUtil().getApplicantByStatus(ApplicantStatus.APPLICANT_STATUS_APPROVED));
					}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_NEW_APPLICANT_REQUESTS))
					{
						portletState.setApplicantListing(portletState.getApplicantPortletUtil().getApplicantByStatus(ApplicantStatus.APPLICANT_STATUS_UNAPPROVED));
					}else if(portletState.getCurrentTab()!=null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_REJECTED_PORTAL_USER_REQUESTS))
					{
						portletState.setApplicantListing(portletState.getApplicantPortletUtil().getApplicantByStatus(ApplicantStatus.APPLICANT_STATUS_REJECTED));
					}else
					{
						portletState.setApplicantListing(portletState.getApplicantPortletUtil().getAllApplicant());
						portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_APPLICANT);
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					portletState.addError(aReq, "Invalid applicant selected. Please start this process from the beginning!", portletState);
				} catch (PortalException e) {
					// TODO Auto-generated catch block
					portletState.addError(aReq, "Invalid applicant selected. Please start this process from the beginning!", portletState);
					e.printStackTrace();
				} catch (SystemException e) {
					// TODO Auto-generated catch block
					portletState.addError(aReq, "System Error. Please start this process from the beginning!", portletState);
					e.printStackTrace();
				}catch (Exception e) {
					// TODO Auto-generated catch block
					portletState.addError(aReq, "System Error. Please start this process from the beginning!", portletState);
					e.printStackTrace();
				}
				
				
				portletState.setSelectedApplicant(null);
				aRes.setRenderParameter("jspPage", "/html/applicantportlet/applicantlisting/applicantlisting.jsp");
				
			}else
			{
				portletState.addError(aReq, "Invalid applicant selected. Please select a valid applicant", portletState);
				aRes.setRenderParameter("jspPage", "/html/applicantportlet/applicantlisting/applicantlisting.jsp");
			}
		}else
		{
			portletState.addError(aReq, "Invalid applicant selected. Please select a valid applicant", portletState);
			aRes.setRenderParameter("jspPage", "/html/applicantportlet/applicantlisting/applicantlisting.jsp");
		}
	}

	private void approveApplicant(ActionRequest aReq, ActionResponse aRes,
			ApplicantPortletState portletState, String selectedApplicant) {
		// TODO Auto-generated method stub
//		try
//		{
			Long selectedApplicantLong = Long.valueOf(selectedApplicant);
			if(selectedApplicantLong!=null)
			{
				log.info("selectedApplicantLong = " + selectedApplicantLong);
				Applicant applicant = (Applicant)portletState.getApplicantPortletUtil().getEntityObjectById(Applicant.class, selectedApplicantLong);
				if(applicant!=null)
				{
					log.info("applicant id = " + applicant.getId());
					applicant.setStatus(ApplicantStatus.APPLICANT_STATUS_APPROVED);
					swpService.updateRecord(applicant);
					PortalUser pu = applicant.getPortalUser();
					pu.setDateCreated(new Timestamp((new Date()).getTime()));
					//new Util().auditTrail("APPLICANT APPROVAL", applicant.getId().toString(), portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId(), swpService);

					new Util().pushAuditTrail(swpService, pu.getId().toString(), ECIMSConstants.APPROVE_APPLICANT_SIGNUP, 
							portletState.getRemoteIPAddress(), pu.getUserId());
					portletState.addSuccess(aReq, "SignUp request successfully approved!", portletState);
					activatePortalUserStatus(applicant.getPortalUser(), portletState, true, "");
					if(portletState.getCurrentTab()!= null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_ALL_APPLICANT))
					{
						log.info("11111111111");
						portletState.setApplicantListing(portletState.getApplicantPortletUtil().getAllApplicant());
					}else if(portletState.getCurrentTab()!= null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_APPROVED_APPLICANT_LISTINGS))
					{
						log.info("555555555555");
						portletState.setApplicantListing(portletState.getApplicantPortletUtil().getApplicantByStatus(ApplicantStatus.APPLICANT_STATUS_APPROVED));
					}else if(portletState.getCurrentTab()!= null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_NEW_APPLICANT_REQUESTS))
					{
						log.info("4444444444");
						portletState.setApplicantListing(portletState.getApplicantPortletUtil().getApplicantByStatus(ApplicantStatus.APPLICANT_STATUS_UNAPPROVED));
					}else if(portletState.getCurrentTab()!= null && portletState.getCurrentTab().equals(VIEW_TABS.VIEW_REJECTED_PORTAL_USER_REQUESTS))
					{
						log.info("33333333333");
						portletState.setApplicantListing(portletState.getApplicantPortletUtil().getApplicantByStatus(ApplicantStatus.APPLICANT_STATUS_REJECTED));
					}else
					{
						log.info("222222222222");
						portletState.setApplicantListing(portletState.getApplicantPortletUtil().getAllApplicant());
					}
					portletState.setSelectedApplicant(null);
					
					aRes.setRenderParameter("jspPage", "/html/applicantportlet/applicantlisting/applicantlisting.jsp");
					
				}else
				{
					portletState.addError(aReq, "1) Invalid applicant selected. Please select a valid applicant", portletState);
					aRes.setRenderParameter("jspPage", "/html/applicantportlet/applicantlisting/applicantlisting.jsp");
				}
			}else
			{
				portletState.addError(aReq, "1. Invalid applicant selected. Please select a valid applicant", portletState);
				aRes.setRenderParameter("jspPage", "/html/applicantportlet/applicantlisting/applicantlisting.jsp");
			}
//		}catch(Exception e)
//		{
//			e.printStackTrace();
//			portletState.addError(aReq, "Invalid applicant selected. Please select a valid applicant", portletState);
//			aRes.setRenderParameter("jspPage", "/html/applicantportlet/applicantlisting/applicantlisting.jsp");
//		}
	}

	
	
	private void activatePortalUserStatus(PortalUser pu, ApplicantPortletState portletState, boolean active, String reason) {
		// TODO Auto-generated method stub
		//Test 108
		//emailPortaluser with activation code
		pu.setDateCreated(new Timestamp((new Date()).getTime()));
		swpService.updateRecord(pu);
		try{
			if(active==true)
			{
				Mailer mailer = new Mailer(portletState.getSendingEmail().getValue(), 
					portletState.getSendingEmailPassword().getValue(), 
					Integer.valueOf(portletState.getSendingEmailPort().getValue()), 
					portletState.getSendingEmailUsername().getValue());
				mailer.emailSignUpApprovalRequest(pu, portletState.getSystemUrl().getValue(), 
						portletState.getApplicationName().getValue());
				
				String smsMessage = "End-User Certificate signup request approved! \nPasscode: " + pu.getPasscode() + "\nCheck your email for an activation link to activate your ECIMS account" ;
				SendSms sendSms = new SendSms(pu.getPhoneNumber(), 
						smsMessage, 
						portletState.getMobileApplicationName().getValue(), 
						portletState.getProxyHost().getValue(), 
						portletState.getProxyPort().getValue());
			}else
			{
				Mailer mailer = new Mailer(portletState.getSendingEmail().getValue(), 
						portletState.getSendingEmailPassword().getValue(), 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()), 
						portletState.getSendingEmailUsername().getValue());
				mailer.emailSignUpDisapprovalRequest(pu, portletState.getSystemUrl().getValue(), 
						portletState.getApplicationName().getValue(), reason);
				
				String smsMessage = "End-User Certificate signup request disapproved! For further details contact us " +
						"using our websites contact page";
				SendSms sendSms = new SendSms(pu.getPhoneNumber(), 
						smsMessage, 
						portletState.getMobileApplicationName().getValue(), 
						portletState.getProxyHost().getValue(), 
						portletState.getProxyPort().getValue());
			}
			
			
		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
		}
	}
	
	
	private void createApplicantStepFour(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicantPortletState portletState) {
		// TODO Auto-generated method stub
		aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_individual/steptwo.jsp");
	}
	
	private void createCorporateApplicantStepFour(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicantPortletState portletState) {
		// TODO Auto-generated method stub
		aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_company/steptwo.jsp");
	}

	private void createApplicantStepThree(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicantPortletState portletState) {
		// TODO Auto-generated method stub
		String act = aReq.getParameter("act");
		log.info("act = " + act);
		if(act.equalsIgnoreCase("Proceed"))
		{
			log.info("Proceed = ");
			PortalUser pu = null;
			RoleType roletype = portletState.getApplicantPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_END_USER);
			State state = portletState.getApplicantPortletUtil().getStateByName(portletState.getState());
			
			String placeOfIssue=null;
			String identificationNumber=null;
			String identificationFileName=null;
			String identificationExpiryDate=null;
			String issueDate=null;
			String identificationType=null;
			
			if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_NATIONAL_ID.getValue()))
			{
				issueDate = portletState.getIdentificationType();
				identificationFileName = portletState.getIdentificationFileName();
				identificationNumber = portletState.getNationalIdNumber();
				issueDate = portletState.getNatlissueDate();
			}else if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_DRIVERS_LICENSE.getValue()))
			{
				identificationExpiryDate = portletState.getDriversExpiryDate();
				identificationType = portletState.getIdentificationType();
				identificationFileName = portletState.getIdentificationFileName();
				identificationNumber = portletState.getDriversIdNumber();
				placeOfIssue = portletState.getDriversplaceOfIssue();
				issueDate = portletState.getDriversissuancedate();
			}else if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_INTL_PASSPORT.getValue()))
			{
				identificationExpiryDate = portletState.getIntlpassportExpiryDate();
				identificationType = portletState.getIdentificationType();
				identificationFileName = portletState.getIdentificationFileName();
				identificationNumber = portletState.getIntlpassportIdNumber();
				placeOfIssue = portletState.getIntlplaceOfIssue();
				issueDate = portletState.getIntlissuancedate();
			}else if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_PVC.getValue()))
			{
				identificationType = portletState.getIdentificationType();
				identificationFileName = portletState.getIdentificationFileName();
				identificationNumber = portletState.getPvcNumber();
				issueDate = portletState.getPvcIssueDate();
			}
			pu = createPortalUser(
					portletState.getLastName(), portletState.getFirstName(), 
					portletState.getOtherName(), portletState.getEmail(), 
					portletState.getCountryCode(), portletState.getMobile(), portletState.getNationality(), 
					state, portletState.getGender(), 
					portletState.getMaritalStatus(), portletState.getAddressLine1(), 
					portletState.getAddressLine2(), portletState.getDob(), roletype,
					portletState.getResidenceCity(), portletState.getResidenceState(), portletState.getResidencePhoneNumber(),
					portletState.getDesignation(), portletState.getPassportPhoto(), null,
					portletState, aReq, aRes, 
					placeOfIssue, identificationNumber, identificationFileName, identificationExpiryDate, issueDate, identificationType);
			
			
			if(pu!=null)
			{
				log.info("pu != null");
				createApplicantAccount(portletState.getPassportPhoto(), 
						portletState.getNextOfKinName(), portletState.getNextOfKinAddress(), 
						portletState.getNextOfKinRelationship(), portletState.getNextOfKinPhoneNumber(), 
						portletState.getAccountType(), pu, 
						aReq, aRes, portletState);
				
				
			}else
			{
				aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_individual/stepthree.jsp");
			}
		}else if(act.equalsIgnoreCase("Back"))
		{
			aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_individual/steptwo.jsp");
		}
	}

	
	private void createCorporateApplicantStepThree(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicantPortletState portletState) {
		// TODO Auto-generated method stub
		String act = aReq.getParameter("act");
		log.info("act = " + act);
		if(act.equalsIgnoreCase("Proceed"))
		{
			log.info("Proceed = ");
			
			Company company = null;
			company = createNewCompany(portletState.getCompanyName(), portletState.getCompanyAddress(), 
					portletState.getCompanyEmailAddress(), portletState.getCompanyLogo(), 
					portletState.getCompanyPhoneNumber(), portletState.getCompanyState(), 
					portletState.getCacCertificate(), portletState.getDateOfIncorporation(), 
					portletState.getRegistrationNumber());
			
			if(company!=null)
			{
			
				PortalUser pu = null;
				RoleType roletype = portletState.getApplicantPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_END_USER);
				State state = portletState.getApplicantPortletUtil().getStateByName(portletState.getState());
				
				String placeOfIssue=null;
				String identificationNumber=null;
				String identificationFileName=null;
				String identificationExpiryDate=null;
				String issueDate=null;
				String identificationType=null;
				
				if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_NATIONAL_ID.getValue()))
				{
					issueDate = portletState.getIdentificationType();
					identificationFileName = portletState.getIdentificationFileName();
					identificationNumber = portletState.getNationalIdNumber();
					issueDate = portletState.getNatlissueDate();
				}else if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_DRIVERS_LICENSE.getValue()))
				{
					identificationExpiryDate = portletState.getDriversExpiryDate();
					identificationType = portletState.getIdentificationType();
					identificationFileName = portletState.getIdentificationFileName();
					identificationNumber = portletState.getDriversIdNumber();
					placeOfIssue = portletState.getDriversplaceOfIssue();
					issueDate = portletState.getDriversissuancedate();
				}else if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_INTL_PASSPORT.getValue()))
				{
					identificationExpiryDate = portletState.getIntlpassportExpiryDate();
					identificationType = portletState.getIdentificationType();
					identificationFileName = portletState.getIdentificationFileName();
					identificationNumber = portletState.getIntlpassportIdNumber();
					placeOfIssue = portletState.getIntlplaceOfIssue();
					issueDate = portletState.getIntlissuancedate();
				}else if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_PVC.getValue()))
				{
					identificationType = portletState.getIdentificationType();
					identificationFileName = portletState.getIdentificationFileName();
					identificationNumber = portletState.getPvcNumber();
					issueDate = portletState.getPvcIssueDate();
				}
				pu = createPortalUser(portletState.getLastName(), portletState.getFirstName(), 
						portletState.getOtherName(), portletState.getEmail(), 
						portletState.getCountryCode(), portletState.getMobile(), portletState.getNationality(), 
						state, portletState.getGender(), 
						portletState.getMaritalStatus(), portletState.getAddressLine1(), 
						portletState.getAddressLine2(), portletState.getDob(), roletype,
						portletState.getResidenceCity(), portletState.getResidenceState(), portletState.getResidencePhoneNumber(),
						portletState.getDesignation(), portletState.getPassportPhoto(), company, 
						portletState, aReq, aRes, 
						placeOfIssue, identificationNumber, identificationFileName, identificationExpiryDate, issueDate, identificationType);
				
				
				if(pu!=null)
				{
					log.info("pu != null");
					createApplicantAccount(portletState.getPassportPhoto(), 
							portletState.getNextOfKinName(), portletState.getNextOfKinAddress(), 
							portletState.getNextOfKinRelationship(), portletState.getNextOfKinPhoneNumber(), 
							portletState.getAccountType(), pu, 
							aReq, aRes, portletState);
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_company/stepthree.jsp");
				}
			}else
			{
				aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_company/stepthree.jsp");
			}
		}else if(act.equalsIgnoreCase("Back"))
		{
			aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_company/steptwo.jsp");
		}
	}

	
	private Company createNewCompany(String companyName, String companyAddress,
			String companyEmailAddress, String companyLogo,
			String companyPhoneNumber, String companyState, 
			String cacCertificate, String dateOfIncorporation, 
			String registrationNumber) {
		// TODO Auto-generated method stub
		Company company = new Company();
		company.setAddress(companyAddress);
		company.setName(companyName);
		company.setEmailAddress(companyEmailAddress);
		company.setLogo(companyLogo);
		company.setPhoneNumber(companyPhoneNumber);
		company.setCertificate(cacCertificate);
		try
		{
			company.setCertificateDate(sdf.parse(dateOfIncorporation));
		}catch(ParseException e)
		{
			e.printStackTrace();
		}
		company.setRegNo(registrationNumber);
		company = (Company)swpService.createNewRecord(company);
		
		return company;
	}

	private void createApplicantAccount(String passportPhoto,
			String nextOfKinName, String nextOfKinAddress,
			String nextOfKinRelationship, String nextOfKinPhoneNumber,
			String accountType, PortalUser pu, 
			ActionRequest aReq, ActionResponse aRes, 
			ApplicantPortletState portletState) {
		// TODO Auto-generated method stub
		String successMessage = "Application request successfully created. We will review your request. On approval, you should" +
				" get an SMS and email...";
		String failMessage = "Application request was not successfully created.";
		Applicant applicant = new Applicant();
		applicant.setApplicantNumber("APN" + RandomStringUtils.random(8, false, true));
		applicant.setApplicantType(accountType.equals("1") ? ApplicantType.APPLICANT_TYPE_INDIVIDUAL : ApplicantType.APPLICANT_TYPE_CORPORATE);
		
		applicant.setNextOfKinAddress(nextOfKinAddress);
		applicant.setNextOfKinName(nextOfKinName);
		applicant.setNextOfKinPhoneNumber(nextOfKinPhoneNumber);
			nextOfKinRelationship = "UNCLE";
		applicant.setNextOfKinRelationship(KinRelationshipType.fromString(nextOfKinRelationship));
		applicant.setPortalUser(pu);
		applicant.setStatus(ApplicantStatus.APPLICANT_STATUS_UNAPPROVED);
		
		
		
		applicant = (Applicant)swpService.createNewRecord(applicant);
		if(applicant!=null)
		{
			new Util().pushAuditTrail(swpService, applicant.getId().toString(), ECIMSConstants.APPLICANT_SIGNUP, 
					portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
			
			portletState.reinitializeForCreateCorporateIndividual(portletState);
			
			aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_company/stepfour.jsp");
			portletState.addSuccess(aReq, successMessage, portletState);
		}
		else
		{
			aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_company/stepthree.jsp");
			portletState.addError(aReq, successMessage, portletState);
		}
	}

	private PortalUser createPortalUser(String lastName, String firstName,
			String otherName, String email, String countryCode, String mobile,
			String nationality, State state, String gender,
			String maritalStatus, String addressLine1, String addressLine2, String dob, RoleType roleType, 
			String residenceCity, String residenceState, String residencePhoneNumber,
			String designation, String passportPhoto, Company company, 
			ApplicantPortletState portletState, ActionRequest aReq, ActionResponse aRes, 
			String placeOfIssue, String identificationNumber, String identificationFileName, String identificationExpiryDate, String issueDate, String identificationType) {
		
		
		
		
		// TODO Auto-generated method stub
		log.info("createPortalUser  inside");
		PortalUser pu = null;
		try
		{
			log.info("1");
			pu = new PortalUser();
			pu.setAddressLine1(addressLine1);
			pu.setAddressLine2(addressLine2);
			pu.setCompany(null);
			Date dateOfBirth = sdf.parse(dob);
			pu.setDateOfBirth(dateOfBirth);
			pu.setEmailAddress(email);
			pu.setFirstName(firstName);
			pu.setSurname(lastName);
			pu.setOtherName(otherName);
			pu.setPhoneNumber(countryCode + mobile);
			pu.setState(state);
			pu.setAgency(null);
			pu.setDateCreated(new Timestamp((new Date()).getTime()));
			pu.setGender(gender);
			pu.setMaritalStatus(maritalStatus);
			pu.setPasscode(RandomStringUtils.random(6, true, true));
			pu.setActivationCode(UUID.randomUUID().toString().replace("-",  ""));
			pu.setPasscodeGenerateTime(new Timestamp((new Date()).getTime()));
			pu.setStatus(UserStatus.USER_STATUS_INACTIVE);
			pu.setRoleType(roleType);
			pu.setCity(residenceCity == null ? "" : residenceCity);
			if(residenceState!=null)
				pu.setResidenceState(portletState.getApplicantPortletUtil().getStateByName(residenceState).getId());
			pu.setResidencePhoneNumber(residencePhoneNumber);
			pu.setCompany(company);
			log.info("1");
			long communities[] = new long[1];
			
			communities[0] = new Util().getPortalUserCommunityByRoleType(roleType);
			log.info("communities[0] = " + communities[0]);
			
			AuditTrail auditTrail = new AuditTrail();
			auditTrail.setAction("Create Portal User");
			auditTrail.setDate(new Timestamp((new Date()).getTime()));
			auditTrail.setIpAddress(portletState.getRemoteIPAddress());
			auditTrail.setUserId(portletState.getPortalUser()!=null ? (portletState.getPortalUser().getUserId()) : "");
			log.info("auditTrail = Create Portal User");
			pu = handleCreateUserOrbitaAccount(pu, 
					firstName, 
					otherName,
					lastName,
					email,
					communities,
					auditTrail, 
					serviceContext, 
					swpService,
					portletState.getPortalUser()!=null ? (portletState.getPortalUser().getUserId()) : "",
					true,
					true, 
					true,
					true,
					"ECIMS",
					portletState,
					aReq, 
					aRes
					);
			if(pu!=null)
			{
				IdentificationHistory idHistory = new IdentificationHistory();
				idHistory.setPlaceOfIssue(placeOfIssue);
				idHistory.setIdentificationNumber(identificationNumber);
				idHistory.setIdentificationPhoto(identificationFileName);
				idHistory.setIdentificationExpiryDate((identificationExpiryDate));
				idHistory.setIssueDate(issueDate);
				log.info("Identification Type = " + identificationType);
				idHistory.setIdentificationType(IdentificationType.fromString(identificationType));
				idHistory = (IdentificationHistory)swpService.createNewRecord(idHistory);
				
				if(idHistory!=null)
				{
					pu.setIdentificationHistory(idHistory);
					swpService.updateRecord(pu);
				}
			}
		}catch(ParseException e)
		{
			e.printStackTrace();
		}
		
		
		return pu;
	}
	
	
	
	public static PortalUser handleCreateUserOrbitaAccount(PortalUser user, String firstname, String middlename, String surname, String email, 
			long[] communities, AuditTrail auditTrail, ServiceContext serviceContext, SwpService sService, String loggedInUserId,
			boolean passwordReset, boolean active, boolean sendEmail, boolean sendSms, String systemUrl, 
			ApplicantPortletState portletState, ActionRequest aReq, ActionResponse aRes) {			
		
		Logger log = Logger.getLogger(ApplicantPortlet.class);
		log.info("handleCreateUserOrbitaAccount = " + portletState.getSendingEmail().getValue());
		log.info("getSendingEmailPassword = " + portletState.getSendingEmailPassword().getValue());
		log.info("getSendingEmailPort = " + portletState.getSendingEmailPort().getValue());
		log.info("getSendingEmailUsername = " + portletState.getSendingEmailUsername().getValue());

		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		Logger log1 = Logger.getLogger(ApplicantPortlet.class);
		PortalUser createdUser = user;
		
		Long creatorUserId = null;
		try{
			creatorUserId = Long.valueOf(ECIMSConstants.SUPERADMIN_ID);
			System.out.println("communities "+communities.length);
			boolean alreadyInOrbita = Boolean.FALSE;

			long companyId = ECIMSConstants.COMPANY_ID;
			String jobTitle = "";
			long organizationId = 0;
			long locationId = 0;
			long[] orgAndLocation = new long[2];
			orgAndLocation[0] = organizationId;
			orgAndLocation[1] = locationId;

			int prefixId = 1;
			int suffixId = 1;

			boolean male = true;
			boolean emailSend = true;

			String[] birthdate = portletState.getDob().split("-");
			int birthdayMonth = Integer.parseInt(birthdate[1]);
			int birthdayDay = Integer.parseInt(birthdate[2]);
			int birthdayYear = Integer.parseInt(birthdate[0]);

			long facebookId = 0;
			String openId = "";

			User aUser = null;
			long[] groupIds = communities;
			if (createdUser.getId() == null) {
				System.out.println("In cService method, starting user creation");

				User newlyCreatedUser = null;

				boolean autoPassword = false;
				String password1 = RandomStringUtils.random(8, true, true);
				String password2 = password1;			
				String emailSuffix = ECIMSConstants.DOMAIN_EMAIL_SUFFIX;			

				boolean autoScreenName = false;
				String formattedUsername = "";
				try {
					formattedUsername = getUniqueScreenName(createdUser.getSurname(), createdUser.getFirstName(), emailSuffix);
				} catch (SystemException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (PortalException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				formattedUsername = formattedUsername.replaceAll("/", ".").replaceAll("-", ".").replaceAll("_", ".");
				String screenName = formattedUsername;
				String emailAddress = email;
				String firstName = firstname;
				String lastName = surname;
				String middleName = middlename;

				System.out.println("Successfully set all parameters");

				if (emailSuffix != null || !emailSuffix.equalsIgnoreCase("")) {
					//emailAddress = createdUser.getEmailAddress();
				}

				// if(emailAddress != null && !emailAddress.equalsIgnoreCase("")){
				// only make AuthorizedUser an orbita user account if he has an
				// email
				String greeting = "Welcome, " + firstName + "!";
				try {
					
									
					
					if (aUser == null){
						try {
							log1.debug("getting auser");
							aUser = UserLocalServiceUtil.getUserByScreenName(companyId, screenName);
							log1.debug("auser gotten" + aUser);
						} catch (NoSuchUserException e) {
							//log1.error("NoSuchUserException");
						}
					}				
					// try creating an orbita acct..

					
					
					long[] organizationIds = new long[1];
					long[] roleIds = new long[2];
					long[] userGroupIds = new long[1];

					
					organizationIds[0] = 10134;
					//roleIds[0] = ECIMSConstants.ORBITA_ADMIN_ROLE_ID; //Administrator role
					roleIds[0] = ECIMSConstants.ORBITA_USER_ROLE_ID;//User role
					userGroupIds[0] = ECIMSConstants.ORBITA_USER_GROUP_ID;

					boolean addGroupStatus = Boolean.FALSE;				

					if (aUser != null || alreadyInOrbita) {
						newlyCreatedUser = aUser; log1.debug("User already exists.");									
					} else {
						try {
							
							newlyCreatedUser = UserLocalServiceUtil.addUser(
									creatorUserId, companyId, autoPassword,
									password1, password2, autoScreenName,
									screenName, emailAddress, facebookId, openId,
									Locale.US, firstName, middleName, lastName,
									prefixId, suffixId, male, birthdayMonth,
									birthdayDay, birthdayYear, jobTitle, groupIds,
									organizationIds, roleIds, userGroupIds,
									emailSend, serviceContext);

							System.out.println("Password1 = " + password1);
							System.out.println("Password2 = " + password2);
							System.out.println("Creation succcessful..now adding to community!!! "+newlyCreatedUser.getUserId());

							UserLocalServiceUtil.updatePasswordReset(
									newlyCreatedUser.getUserId(), passwordReset);

							//Update user's status
							if(active){
								UserLocalServiceUtil.updateStatus(
										newlyCreatedUser.getUserId(), 0);
							}else{
								UserLocalServiceUtil.updateStatus(
										newlyCreatedUser.getUserId(), 1);
							}
							for(int i = 0; i < communities.length; i++){
							addGroupStatus = addUserToCommmunity(newlyCreatedUser.getUserId(), communities[i] );	
							if (addGroupStatus == false) {
								System.out.println("addGroupStatus is false");
								try {
									UserLocalServiceUtil.deleteUser(newlyCreatedUser
											.getUserId());
								} catch (Exception e) {
									log1.error("", e);
									portletState.addError(aReq, "Your Online Account could not be created" +
											" on " + systemUrl + ". Please try again", portletState);
								}
							}
							}
							
							try {
								
								

								createdUser = (PortalUser)sService.createNewRecord(createdUser);
								new Util().pushAuditTrail(sService, createdUser.getId().toString(), ECIMSConstants.USER_SIGNUP, 
										portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
								
								
								if(sendEmail){
									//sendMail(firstname, surname, email);
									
									
									emailer.emailNewAccountRequest
									(emailAddress, 
											"", password1, portletState.getSystemUrl().getValue(), 
											createdUser.getFirstName(), createdUser.getSurname(), createdUser.getRoleType().getName().getValue(), 
											"New " + portletState.getApplicationName().getValue() + " Bank Staff Account Created for you", 
											portletState.getApplicationName().getValue());
									
								}
								if(sendSms){
									String message = "Hello, Your Online Account has been " +
											"successfully created on " + systemUrl + ". Your login email is " +  
											emailAddress + " and password: " + password1;
									try{
											message = "Approval request awaiting your action. " +
													"Visit " + portletState.getSystemUrl().getValue() + " to view requests awaiting your " +
													"approval/disapproval action";
											new SendSms(createdUser.getPhoneNumber(), message, 
													portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
									}catch(Exception e){
										log1.error("error sending sms ",e);
									}
								}
								portletState.addSuccess(aReq, "The online " + portletState.getApplicationName().getValue() + " web account for <em>" + emailAddress + "</em> has been " +
											"successfully created on " + systemUrl + ". <br>Your login details are: <br><strong>Email Address:</strong> " +  
											emailAddress + "<br><strong>Password:</strong> " + password1, portletState);
								System.out.println("Added succcessful");

								

								System.out.println("Setting Orbita StaffId");
								createdUser.setUserId(Long.toString(newlyCreatedUser.getUserId()));
								
							} catch (Exception e) { 
								log1.error("", e);
								portletState.addError(aReq, "The online " + portletState.getApplicationName().getValue() + " web Account could not be created" +
									" on " + systemUrl + ". Please try again", portletState); 
								
							}

						} catch (DuplicateUserScreenNameException t) {
							log1.error("DuplicateScreenNameException");
							portletState.addError(aReq, "Your Online Account could not be created" +
									" on " + systemUrl + ". Please try again", portletState);
							return null ;
						} catch(DuplicateUserEmailAddressException e)
						{
							portletState.addError(aReq, "Your Online Account could not be created" +
									" on " + systemUrl + ". Please provide another email address as this email address has been taken.", portletState);
						}
						catch (Exception e) {
							log1.error("", e);
							portletState.addError(aReq, "Your Online Account could not be created" +
									" on " + systemUrl + ". Please try again", portletState);
							return null ;
						}

						
					}

				} catch (Exception e) { 
					portletState.addError(aReq, "Your Online Account could not be created" +
							" on " + systemUrl + ". Please try again", portletState); 
					return null ;
				}
				
			}
		}catch(NumberFormatException e)
		{
			return null ;
		}
		

		return createdUser;		
	}
	
	
	
	
	public static boolean addUserToCommmunity(long userId, long communityId) {
		boolean status = false;
		try {
			Logger logger = Logger.getLogger(ApplicantPortlet.class);
			long[] group = new long[1];
			group[0] = userId; // possible null if user is not

			logger.info("userId is " + userId + " community id is " + communityId);

			try { UserLocalServiceUtil.getUserById(userId);
			}catch (NoSuchUserException e){ logger.error("NoSuchUserException"); return status; }			

			if (!UserLocalServiceUtil.hasGroupUser(communityId, userId)) {
				UserLocalServiceUtil.addGroupUsers(communityId, group);
			}			
			status = UserLocalServiceUtil.hasGroupUser(communityId, userId);

		} catch (Exception e) { throw new HibernateException(e); }
		return status;
	}
	
	public static String getUniqueScreenName(String surname, String firstname, String emailSuffix) throws SystemException, PortalException {
		for (int i = 1; i < surname.length(); i++) {
			try {
				User user = UserLocalServiceUtil.getUserByScreenName(ECIMSConstants.COMPANY_ID, surname.substring(0, i) + firstname);
			} catch (NoSuchUserException e) {
				try {
					User user = UserLocalServiceUtil.getUserByEmailAddress(ECIMSConstants.COMPANY_ID, surname.substring(0, i) + firstname
							+ emailSuffix);
				} catch (NoSuchUserException e1) {
					return surname.substring(0, i) + firstname;
				}
			}
		}
		for (int i = 1;; i++) {
			try {
				User user = UserLocalServiceUtil.getUserByScreenName(ECIMSConstants.COMPANY_ID, surname + firstname + i);
			} catch (NoSuchUserException e) {
				try {
					User user = UserLocalServiceUtil.getUserByEmailAddress(ECIMSConstants.COMPANY_ID, surname + firstname + i
							+ emailSuffix);
				} catch (NoSuchUserException e1) {
					return surname + firstname + i;
				}
			}
		}
	}
	

	private void createApplicantStepTwo(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicantPortletState portletState) {
		// TODO Auto-generated method stub

		UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(aReq);
		String act = uploadRequest.getParameter("act");
		log.info("act =" + act);
		if(act.equalsIgnoreCase("proceed"))
		{
			String folder = "C:" + File.separator + "jcodes" + File.separator + "dev" + 
					File.separator + "appservers" + File.separator + "ecims" + File.separator + "webapps"
					+ File.separator + "resources" + File.separator + "images" + File.separator;
			log.info("Folder===" + folder);
			String realPath = getPortletContext().getRealPath("/");
			log.info("folder=" + folder + " && realPath=" + realPath);
			System.out.println("Size for passportPhoto: "+uploadRequest.getSize("passportPhoto"));
			System.out.println("Size for nationalIdPhoto: "+uploadRequest.getSize("nationalIdPhoto"));
			if (uploadRequest.getSize("passportPhoto")==0 && uploadRequest.getSize("nationalIdPhoto")==0) {
				portletState.addError(aReq, "Ensure you select your passport photo and photo showing your means of identification", portletState);
				aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_individual/steptwo.jsp");
			}else
			{
				String lastname = uploadRequest.getParameter("lastname");
				log.info("lastname==" + lastname);
				String firstname = uploadRequest.getParameter("firstname");
				log.info("firstname==" + firstname);
				String othername = uploadRequest.getParameter("othername");
				log.info("othername==" + othername);
				String contactEmailAddress = uploadRequest.getParameter("contactEmailAddress");
				String countryCode = uploadRequest.getParameter("countryCode");
				String mobile = uploadRequest.getParameter("mobile");
				String nationality = uploadRequest.getParameter("nationality");
				String state = uploadRequest.getParameter("state");
				String dob = uploadRequest.getParameter("dob");
				String gender = uploadRequest.getParameter("gender");
				String maritalStatus = uploadRequest.getParameter("maritalStatus");
				String contactAddressLine1 = uploadRequest.getParameter("contactAddressLine1");
				String contactAddressLine2 = uploadRequest.getParameter("contactAddressLine2");
				String identificationMeans = uploadRequest.getParameter("identificationMeans");
				String identificationExpiryDate = uploadRequest.getParameter("identificationExpiryDate");
//				String sourceFileName = uploadRequest.getFileName("passportPhoto");
//				File file = uploadRequest.getFile("passportPhoto");
//				log.info("Nome file:" + uploadRequest.getFileName("passportPhoto"));
//				File newFile = null;
//				String newFileName = new SimpleDateFormat("yyyyMMddHHmmssS").format(new Date());
//				newFile = new File(folder + newFileName);
//				log.info("New file name: " + newFile.getName());
//				log.info("New file path: " + newFile.getPath());
//				InputStream in;
//				try {
//					in = new BufferedInputStream(uploadRequest.getFileAsStream("passportPhoto"));
//					FileInputStream fis = new FileInputStream(file);
//					FileOutputStream fos = new FileOutputStream(newFile);
//					byte[] bytes_ = FileUtil.getBytes(in);
//					int i = fis.read(bytes_);
//					while (i != -1) {
//						fos.write(bytes_, 0, i);
//						i = fis.read(bytes_);
//					}
//					fis.close();
//					fos.close();
//					
//					Float size = (float) newFile.length();
//					System.out.println("file size bytes:" + size);
//					System.out.println("file size Mb:" + size / 1048576);
				ImageUpload imupload = uploadImage(uploadRequest, "passportPhoto", folder + "" + File.separator + "uploadedfiles" + File.separator);
				ImageUpload imupload2 = uploadImage(uploadRequest, "nationalIdPhoto", folder + "" + File.separator + "uploadedfiles" + File.separator);
				if(imupload==null && imupload2==null)
				{
					aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_individual/steptwo.jsp");
					portletState.addError(aReq, "Uploading passport failed. Ensure you selected a valid file", portletState);
				}else if(imupload!=null && (imupload.isUploadValid()) && imupload2!=null && (imupload2.isUploadValid()))
				{
					String passportPhoto = imupload.getNewFileName();
					String nationalIdPhoto = imupload2.getNewFileName();
					String nationalIdNumber = uploadRequest.getParameter("nationalIdNumber");
					String nextOfKinFullName = uploadRequest.getParameter("nextOfKinFullName");
					String nextOfKinAddress = uploadRequest.getParameter("nextOfKinAddress");
					String nextOfKinRelationship = uploadRequest.getParameter("nextOfKinRelationship");
					String nextOfKinPhoneNumber = uploadRequest.getParameter("nextOfKinPhoneNumber");
					

					String residencePhoneNumber = uploadRequest.getParameter("residencePhoneNumber");
					String residenceState = uploadRequest.getParameter("residenceState");
					String residenceCity = uploadRequest.getParameter("residenceCity");
					String issueDate = uploadRequest.getParameter("issueDate");
					String placeOfIssue = uploadRequest.getParameter("placeOfIssue");
					
					
					
					log.info("Proceed to next clicked");
					portletState.setLastName(lastname);
					portletState.setFirstName(firstname);
					portletState.setOtherName(othername);
					portletState.setEmail(contactEmailAddress);
					portletState.setCountryCode(countryCode);
					portletState.setMobile(mobile);
					portletState.setNationality(nationality);
					portletState.setState(state);
					portletState.setGender(gender);
					portletState.setMaritalStatus(maritalStatus);
					portletState.setAddressLine1(contactAddressLine1);
					portletState.setAddressLine2(contactAddressLine2);
					portletState.setIdentificationType(identificationMeans);
					portletState.setIdentificationFileName(nationalIdPhoto);
					portletState.setNextOfKinName(nextOfKinFullName);
					portletState.setNextOfKinAddress(nextOfKinAddress);
					portletState.setNextOfKinRelationship(nextOfKinRelationship);
					portletState.setNextOfKinPhoneNumber(nextOfKinPhoneNumber);
					portletState.setPassportPhoto(passportPhoto);
					portletState.setDob(dob);
					portletState.setPlaceOfIssue(placeOfIssue);
					portletState.setIssueDate(issueDate);
					portletState.setResidenceCity(residenceCity);
					portletState.setResidenceState(residenceState);
					portletState.setResidencePhoneNumber(residencePhoneNumber);
					
					
					if(validateApplicantProfile(aReq, false, lastname, firstname, othername, contactEmailAddress, 
							countryCode, mobile, nationality, state, gender, maritalStatus, contactAddressLine1, 
							contactAddressLine2, passportPhoto, identificationMeans, nationalIdPhoto, 
							nationalIdNumber, identificationExpiryDate, nextOfKinFullName, nextOfKinAddress, 
							nextOfKinRelationship, nextOfKinPhoneNumber, passportPhoto, dob, placeOfIssue, 
							issueDate, residenceCity, residenceState, residencePhoneNumber, portletState)==true)
					{
						log.info("Validate True");
						
						
						
						aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_individual/stepthree.jsp");
					}else
					{
						log.info("Validate False");
						aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_individual/steptwo.jsp");
					}
				}
				else
				{
					aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_individual/steptwo.jsp");
					portletState.addError(aReq, "Uploading passport failed. Ensure you selected a valid file", portletState);
				}
				
				
			}
		}else if(act.equalsIgnoreCase("back"))
		{
			log.info("Go Back clicked");
			aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_individual/stepone.jsp");
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
			log.info("Nome file:" + uploadRequest.getFileName(parameterName));
			File newFile = null;
			String newFileName = new SimpleDateFormat("yyyyMMddHHmmssS").format(new Date()) + "." + (strArr.length>0 ? strArr[strArr.length-1] : "png");
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
			String passportPhoto = newFileName;
			imageUpload = new ImageUpload();
			imageUpload.setNewFileName(newFileName);
			imageUpload.setUploadValid(true);
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		return imageUpload;
	}

	
	private void createCorporateApplicantStepTwo(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicantPortletState portletState) {
		// TODO Auto-generated method stub
		
		UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(aReq);
		String act = uploadRequest.getParameter("act");
		log.info("act =" + act);
		if(act.equalsIgnoreCase("proceed"))
		{
			String folder = "C:" + File.separator + "jcodes" + File.separator + "dev" + 
					File.separator + "appservers" + File.separator + "ecims" + File.separator + "webapps"
					+ File.separator + "resources" + File.separator + "images" + File.separator;
			//prop.getProperty("liferay.home");
			log.info("Folder===" + folder);
			String realPath = getPortletContext().getRealPath("/");
			log.info("folder=" + folder + " && realPath=" + realPath);
			System.out.println("Size for passportPhoto: "+uploadRequest.getSize("passportPhoto"));
			System.out.println("Size for cacCertificate: "+uploadRequest.getSize("cacCertificate"));
			if (uploadRequest.getSize("passportPhoto")==0 && uploadRequest.getSize("cacCertificate")==0) {
				portletState.addError(aReq, "Ensure you select your passport photo and CAC Certificate", portletState);
				aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_company/steptwo.jsp");
			}else
			{
				
				String lastname = uploadRequest.getParameter("lastname");
				log.info("lastname==" + lastname);
				String firstname = uploadRequest.getParameter("firstname");
				log.info("firstname==" + firstname);
				String othername = uploadRequest.getParameter("othername");
				log.info("othername==" + othername);
				String contactEmailAddress = uploadRequest.getParameter("contactEmailAddress");
				//String passportPhoto = uploadRequest.getParameter("passportPhoto");
				String dob = uploadRequest.getParameter("dob");
				String nationality = uploadRequest.getParameter("nationality");
				String state = uploadRequest.getParameter("state");
				String gender = uploadRequest.getParameter("gender");
				String designation = uploadRequest.getParameter("designation");
				String countryCode = uploadRequest.getParameter("countryCode");
				String mobile = uploadRequest.getParameter("mobile");
				String companyName = uploadRequest.getParameter("companyName");
				String companyAddress =  uploadRequest.getParameter("companyAddress");
				String companyState =  uploadRequest.getParameter("companyState");
				String companyPhoneNumber =  uploadRequest.getParameter("companyPhoneNumber");
				String companyEmailAddress =  uploadRequest.getParameter("companyEmailAddress");
				String websiteUrl =  uploadRequest.getParameter("websiteUrl");
				String cacNo = uploadRequest.getParameter("cacNo");
				String dateOfIncorporation = uploadRequest.getParameter("dateOfIncorporation");
//				String companyLogo = uploadRequest.getParameter("companyLogo");
//				String cacCertificate = uploadRequest.getParameter("cacCertificate");
				
				portletState.setLastName(lastname);
				portletState.setFirstName(firstname);
				portletState.setOtherName(othername);
				portletState.setEmail(contactEmailAddress);
				portletState.setDob(dob);
				portletState.setNationality(nationality);
				portletState.setState(state);
				portletState.setGender(gender);
				portletState.setDesignation(designation);
				portletState.setCountryCode(countryCode);
				portletState.setMobile(mobile);
				portletState.setCompanyName(companyName);
				portletState.setCompanyAddress(companyAddress);
				portletState.setCompanyState(companyState);
				portletState.setCompanyPhoneNumber(companyPhoneNumber);
				portletState.setCompanyEmailAddress(companyEmailAddress);
				portletState.setWebsiteUrl(websiteUrl);
				portletState.setRegistrationNumber(cacNo);
				portletState.setDateOfIncorporation(dateOfIncorporation);
				
				if(validateCorporateApplicantProfile(aReq, false, lastname, firstname, contactEmailAddress, 
						dob, nationality, state, gender, designation, 
						countryCode, mobile, companyName, companyAddress, companyState, companyPhoneNumber, 
						cacNo, dateOfIncorporation, portletState)==true)
				{
					ImageUpload imupload = uploadImage(uploadRequest, "passportPhoto", folder + "" + File.separator + "uploadedfiles" + File.separator);
					ImageUpload imupload2 = uploadImage(uploadRequest, "cacCertificate", folder + "" + File.separator + "uploadedfiles" + File.separator);
					ImageUpload imupload3 = uploadImage(uploadRequest, "companyLogo", folder + "" + File.separator + "uploadedfiles" + File.separator);
					if(imupload==null && imupload2==null)
					{
						aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_company/steptwo.jsp");
						portletState.addError(aReq, "Uploading passport failed. Ensure you selected a valid file", portletState);
					}else if(imupload!=null && (imupload.isUploadValid()) && imupload2!=null && (imupload2.isUploadValid()))
					{
						portletState.setCompanyLogo(imupload.getNewFileName());
						portletState.setCacCertificate(imupload2.getNewFileName());
						portletState.setPassportPhoto(imupload3==null ? null : imupload3.getNewFileName());
						
							log.info("Validate True");
							
							
							
							aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_company/stepthree.jsp");
						
					}
				}else
				{
					log.info("Validate False");
					aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_company/steptwo.jsp");
				}	
			}
		}else if(act.equalsIgnoreCase("back"))
		{
			log.info("Go Back clicked");
			aRes.setRenderParameter("jspPage", "/html/applicantportlet/stepone.jsp");
		}
		
		log.info("act =" + act);
	}
	
	
	
	private boolean validateApplicantProfile(ActionRequest aReq, boolean editTrue, String lastname,
			String firstname, String othername, String contactEmailAddress,
			String countryCode, String mobile, String nationality,
			String state, String gender, String maritalStatus,
			String contactAddressLine1, String contactAddressLine2,
			String passportPhoto, String identificationMeans,
			String nationalIdPhoto, String nationalIdNumber,
			String identificationExpiryDate, String nextOfKinFullName,
			String nextOfKinAddress, String nextOfKinRelationship,
			String nextOfKinPhoneNumber, String passportPhoto2, String dob, 
			String placeOfIssue, 
			String issueDate, String residenceCity,  String residenceState, 
			String residencePhoneNumber, 
			ApplicantPortletState portletState) {
		// TODO Auto-generated method stub
		
		String errorMessage = null;
		if(lastname!=null && lastname.trim().length()>0)
		{
			if(firstname!=null && firstname.trim().length()>0)
			{
				if(othername!=null && othername.trim().length()>0)
				{
					if(nationality!=null && !nationality.equals("-1"))
					{
						if(nextOfKinPhoneNumber!=null && nextOfKinPhoneNumber.trim().length()>0)
						{
							if(contactEmailAddress!=null && contactEmailAddress.trim().length()>0)
							{
								if(mobile!=null && mobile.trim().length()>0)
								{
									if(countryCode!=null && !countryCode.equals("-1"))
									{
										if(state!=null && !state.equals("-1"))
										{
											if(gender!=null)
											{
												if(maritalStatus!=null && !maritalStatus.equals("-1"))
												{
													if(contactAddressLine1!=null && contactAddressLine1.trim().length()>0)
													{
														PortalUser pu = null;
														if(editTrue)
														{
															if(portletState.getSelectedPortalUserId()!=null && portletState.getSelectedPortalUserId().length()>0)
															{
																pu = portletState.getApplicantPortletUtil().getPortalUserByEmailAddressAndNotUserId(
																	portletState.getEmail(), Long.valueOf(portletState.getSelectedPortalUserId()));
																if(pu!=null)
																{
																	errorMessage =  "The email address provided has already been used on this platform. Provide another email address.";
																}									
															}else
															{
																errorMessage =  "Invalid User has been selected.";
															}
														}else
														{
															pu = portletState.getApplicantPortletUtil().getPortalUserByEmailAddress(
																	portletState.getEmail());
														}
													}else
													{
														errorMessage =  "Provide a contact address before proceeding";
													}
												}else
												{
													errorMessage =  "Specify your marital status before proceeding";
												}
											}else
											{
												errorMessage =  "Specify your gender before proceeding";
											}
										}else
										{
											errorMessage =  "Specify your state of origin before proceeding";
										}
									}else
									{
										errorMessage =  "Specify your country's international phone code before proceeding";
									}
								}else
								{
									errorMessage =  "Specify your mobile number before proceeding";
								}
							}else
							{
								errorMessage =  "Specify your email address before proceeding";
							}
						}else
						{
							errorMessage =  "Specify your next of kin's phone number";
						}
					}else
					{
						errorMessage =  "Specify your nationality";
					}
				}else
				{
					errorMessage =  "Specify your other name";
				}
			}else
			{
				errorMessage =  "Specify your first name";
			}
		}else
		{
			errorMessage =  "Specify your last name";
		}
		
		if(errorMessage==null)
		{
			return true;
		}
		else
		{
			log.info("Error message = " + errorMessage);
			portletState.addError(aReq, errorMessage, portletState);
			return false;
		}
	}

	
	
	private boolean validateCorporateApplicantProfile(ActionRequest aReq, boolean editTrue, String lastname,
			String firstname, String contactEmailAddress,
			String dob, String nationality,
			String state, String gender, String designation, 
			String countryCode, String mobile, String companyName, 
			String companyAddress, String companyState, String companyPhoneNumber, 
			String cacno, String dateOfIncorporation, ApplicantPortletState portletState) {
		// TODO Auto-generated method stub
		String errorMessage = null;
		
		if(errorMessage==null)
		{
			return true;
		}
		else
		{
			log.info("Error message = " + errorMessage);
			portletState.addError(aReq, errorMessage, portletState);
			return false;
		}
	}

	private void createApplicantStepOne(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ApplicantPortletState portletState) {
		// TODO Auto-generated method stub
		String applicantType = aReq.getParameter("accountType");
		log.info("Applicant Type==" + applicantType);
		if(applicantType!=null)
		{
			portletState.setAccountType(applicantType);
			if(applicantType.equals("0"))
				aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_company/steptwo.jsp");
			if(applicantType.equals("1"))
				aRes.setRenderParameter("jspPage", "/html/applicantportlet/register_individual/steptwo.jsp");
		}
		else
		{
			portletState.addError(aReq, "Select an account type before proceeding", portletState);
			aRes.setRenderParameter("jspPage", "/html/applicantportlet/stepone.jsp");
		}
	}

	
	
}
