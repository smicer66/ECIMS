package com.ecims.portlet.usermanagement;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
import smartpay.entity.Company;
import smartpay.entity.IdentificationHistory;
import smartpay.entity.Permission;
import smartpay.entity.PortalUser;
import smartpay.entity.RoleType;
import smartpay.entity.State;
import smartpay.entity.enumerations.AgencyType;
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
import com.ecims.portlet.usermanagement.UserManagementPortlet;
import com.ecims.portlet.usermanagement.UserManagementPortletState;
import com.ecims.portlet.usermanagement.UserManagementPortletState.EU_ACTION;
import com.ecims.portlet.usermanagement.UserManagementPortletState.VIEW_TABS;
import com.ecims.portlet.usermanagement.UserManagementPortletUtil;
import com.ecims.portlet.usermanagement.UserManagementPortletState.EUC_DESK_ACTIONS;
import com.ibm.icu.impl.ICUBinary.Authenticate;
import com.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.ContactBirthdayException;
import com.liferay.portal.DuplicateUserEmailAddressException;
import com.liferay.portal.DuplicateUserScreenNameException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.UserEmailAddressException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class UserManagementPortlet
 */
public class UserManagementPortlet extends MVCPortlet {
 
	private Logger log = Logger.getLogger(UserManagementPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	UserManagementPortletUtil util = UserManagementPortletUtil.getInstance();
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
		UserManagementPortletState portletState = 
				UserManagementPortletState.getInstance(renderRequest, renderResponse);

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
		
		UserManagementPortletState portletState = UserManagementPortletState.getInstance(aReq, aRes);
		
		
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
        if(action.equalsIgnoreCase(EUC_DESK_ACTIONS.CREATE_A_PORTAL_USER_STEP_ZERO.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ZERO");
        	//Select Account type
        	if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_ADMIN) || portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_SYSTEM_ADMIN))
    		{
        		createPortalUserStepZero(aReq, aRes, swpService, portletState);
    		}else
    		{
    			portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
    		}
        		
        }
        if(action.equalsIgnoreCase(EUC_DESK_ACTIONS.CREATE_A_PORTAL_USER_STEP_ONE.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(aReq);
        	log.info("act = " + uploadRequest.getParameter("act"));
        	if(uploadRequest.getParameter("act")!=null && uploadRequest.getParameter("act").equalsIgnoreCase("proceedtoeditstepone"))
        	{
        		log.info("test 1");
        		editPortalUserStepOne(aReq, aRes, swpService, portletState, uploadRequest);
        	}else if(uploadRequest.getParameter("act")!=null && uploadRequest.getParameter("act").equalsIgnoreCase("backfromstepone"))
        	{
        		log.info("test 3");
        		aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/steptwo.jsp");
        	}
        	else if(uploadRequest.getParameter("act")!=null && uploadRequest.getParameter("act").equals("proceed"))
        	{
        		log.info("test 2");
        		createPortalUserStepOne(aReq, aRes, swpService, portletState, uploadRequest);
        	}
        		
        }
        if(action.equalsIgnoreCase(EU_ACTION.CREATE_NEW_PASSWORD_PORTAL_USER_ACCOUNT.name()))
        {
        	log.info("ACTIVATE_PORTAL_USER_ACCOUNT");
        	//Populate And Create user
        	createNewPassword(aReq, aRes, swpService, portletState);
        		
        }
        
        if(action.equalsIgnoreCase(EUC_DESK_ACTIONS.CREATE_A_PORTAL_USER_STEP_TWO_INSIDE.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_TWO");
        	//Select Account type
        	if(aReq.getParameter("act")!=null && aReq.getParameter("act").equalsIgnoreCase("proceedtoeditstepone"))
        		editPortalUserPermissions(aReq, aRes, swpService, portletState);
        	else if(aReq.getParameter("act")!=null && aReq.getParameter("act").equalsIgnoreCase("proceed"))
            	createPortalUserStepTwoInside(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(EUC_DESK_ACTIONS.UPDATE_PORTAL_USER_PERMISSION.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_TWO");
        	//Select Account type
        	
        	String actNow = aReq.getParameter("act");
        	String actId = aReq.getParameter("actId");
        	if(actNow.equalsIgnoreCase("savepermission"))
        		savePortalUserPermission(aReq, aRes, swpService, portletState);
        	if(actNow.equalsIgnoreCase("cancelupdatepermission"))
        	{
        		aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/userlisting.jsp");
        		portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_APPLICANT);
        		portletState.setPortalUserListing(portletState.getUserManagementPortletUtil().getAllPortalUsers(portletState));
        	}
        		
        }
         
        if(action.equalsIgnoreCase(EUC_DESK_ACTIONS.CREATE_A_PORTAL_USER_STEP_TWO.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_TWO");
        	//Select Account type
        	if(aReq.getParameter("act")!=null && aReq.getParameter("act").equalsIgnoreCase("proceedtoupdateaccount"))
        		editPortalUserStepTwo(aReq, aRes, swpService, portletState);
        	else if(aReq.getParameter("act")!=null && aReq.getParameter("act").equalsIgnoreCase("proceed"))
            	createPortalUserStepTwo(aReq, aRes, swpService, portletState);
        	else if(aReq.getParameter("act")!=null && aReq.getParameter("act").equalsIgnoreCase("backtoupdatestepone"))
        		aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepone_edit.jsp");
        	else if(aReq.getParameter("act")!=null && aReq.getParameter("act").equalsIgnoreCase("back"))
        		aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepone.jsp");
        }
        if(action.equalsIgnoreCase(EUC_DESK_ACTIONS.CREATE_A_PORTAL_USER_STEP_THREE.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_TWO");
        	//Select Account type
        	createPortalUserStepThree(aReq, aRes, swpService, portletState);
        		
        }
        
        if(action.equalsIgnoreCase(VIEW_TABS.CREATE_A_PORTAL_USERS.name()))
        {
        	//set corpoate indivudla listings
        	
        	if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_ADMIN))
        	{
        		aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepzero.jsp");
        		portletState.setCurrentTab(VIEW_TABS.CREATE_A_PORTAL_USERS);
        	}else
        	{
        		aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/applicantlisting/applicantlisting.jsp");
        		portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_APPLICANT);
        		portletState.setPortalUserListing(portletState.getUserManagementPortletUtil().getAllPortalUsers(portletState));
        	}
        	
        }
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_ALL_APPLICANT.name()))
        {
        	//set corpoate indivudla listings
        	portletState.setAllPortalUserListing(portletState.getUserManagementPortletUtil().getAllPortalUsers(portletState));
        	aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/userlisting/userlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_APPLICANT);
        }
        
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_MY_PROFILE.name()))
        {
        	//set corpoate indivudla listings
        	if(portletState.getPortalUser().getRoleType().getRole().getName().getValue().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP.getValue()))
        	{
        		portletState.setSelectedPortalUser(portletState.getPortalUser());
        		aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/viewprofile.jsp");
            	portletState.setCurrentTab(VIEW_TABS.VIEW_MY_PROFILE);
        	}else if(portletState.getPortalUser().getRoleType().getRole().getName().getValue().equals(RoleConstants.ROLE_TYPE_END_USER_GROUP.getValue()))
        	{
        		portletState.setSelectedPortalUser(portletState.getPortalUser());
        		if(portletState.getPortalUser().getCompany()!=null)
        		{
            		aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/applicantlisting/viewapplicant_company.jsp");
        		}else {
            		aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/applicantlisting/viewapplicant_individual.jsp");
        		}
            	portletState.setCurrentTab(VIEW_TABS.VIEW_MY_PROFILE);
        	}else if(portletState.getPortalUser().getRoleType().getRole().getName().getValue().equals(RoleConstants.ROLE_TYPE_AGENCY_USER_GROUP.getValue()))
        	{
        		portletState.setSelectedPortalUser(portletState.getPortalUser());
        		aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/viewprofile.jsp");
            	portletState.setCurrentTab(VIEW_TABS.VIEW_MY_PROFILE);
        	}
        	
        }
        if(action.equalsIgnoreCase(VIEW_TABS.CHANGE_PASSWORD.name()))
        {
        	//set corpoate indivudla listings
        	aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/setpassword.jsp");
        	portletState.setCurrentTab(VIEW_TABS.CHANGE_PASSWORD);
        }
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_APPROVED_APPLICANT_LISTINGS.name()))
        {
        	//set corpoate indivudla listings
        	portletState.setApplicantListing(portletState.getUserManagementPortletUtil().getApplicantByStatus(ApplicantStatus.APPLICANT_STATUS_APPROVED));
        	aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/applicantlisting/applicantlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_APPROVED_APPLICANT_LISTINGS);
        }
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_REJECTED_PORTAL_USER_REQUESTS.name()))
        {
        	//set corpoate indivudla listings
        	portletState.setApplicantListing(portletState.getUserManagementPortletUtil().getApplicantByStatus(ApplicantStatus.APPLICANT_STATUS_REJECTED));
        	aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/applicantlisting/applicantlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_REJECTED_PORTAL_USER_REQUESTS);
        }
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_NEW_APPLICANT_REQUESTS.name()))
        {
        	//set corpoate indivudla listings
        	portletState.setApplicantListing(portletState.getUserManagementPortletUtil().getApplicantByStatus(ApplicantStatus.APPLICANT_STATUS_UNAPPROVED));
        	aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/applicantlisting/applicantlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_NEW_APPLICANT_REQUESTS);
        }
        if(action.equalsIgnoreCase(EUC_DESK_ACTIONS.LIST_PORTAL_USERS_ACTIONS.name()))
        {
        	//set corpoate indivudla listings
        		// TODO Auto-generated method stub
    		String selectedApplicant= aReq.getParameter("selectedPortalUser");
    		String selectedApplicantAction= aReq.getParameter("selectedPortalUserAction");
    		
    		if(selectedApplicantAction.equalsIgnoreCase("edituser"))
    		{
    			try{
    				Long sPu = Long.valueOf(selectedApplicant);
    				portletState.setSelectedPortalUser((PortalUser)portletState.getUserManagementPortletUtil().getEntityObjectById(PortalUser.class, sPu));
    				if(portletState.getSelectedPortalUser()!=null)
    				{
    					portletState.setLastname(portletState.getSelectedPortalUser().getSurname());
    					portletState.setFirstname(portletState.getSelectedPortalUser().getFirstName());
    					portletState.setOthername(portletState.getSelectedPortalUser().getOtherName());
    					portletState.setContactEmailAddress(portletState.getSelectedPortalUser().getEmailAddress());
    					SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
    					portletState.setDob(portletState.getSelectedPortalUser().getDateOfBirth().toString());
    					portletState.setMobile(portletState.getSelectedPortalUser().getPhoneNumber());
    					portletState.setNationality(portletState.getSelectedPortalUser().getState().getCountry().getName());
    					portletState.setState(portletState.getSelectedPortalUser().getState().getName());
    					portletState.setAgencyEntity(portletState.getSelectedPortalUser().getAgency());
    					aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepone_edit.jsp");
    				}
    				
    			}catch(NumberFormatException e)
    			{
    				portletState.addError(aReq, "This Portal User Selected is invalid. Select a valid portal user to proceed with your action", portletState);
    			}
    		}else if(selectedApplicantAction.equalsIgnoreCase("modifyUserPermissions"))
    		{
    			try{
    				Long sPu = Long.valueOf(selectedApplicant);
    				PortalUser p1 = (PortalUser)portletState.getUserManagementPortletUtil().getEntityObjectById(PortalUser.class, sPu);
    				portletState.setSelectedPortalUser(p1);
    				portletState.setSelectedPortalUserPermissions(portletState.getUserManagementPortletUtil().getPortalUserPermissionType(p1));
    				if(portletState.getSelectedPortalUser()!=null)
    				{
    					aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/modifyuserpermission.jsp");
    				}
    				
    			}catch(NumberFormatException e)
    			{
    				portletState.addError(aReq, "This Portal User Selected is invalid. Select a valid portal user to proceed with your action", portletState);
    			}
    		}
    		if(selectedApplicantAction.equalsIgnoreCase("view"))
    		{
    			String id = aReq.getParameter("selectedPortalUser");
    			Long id1 = Long.valueOf(id);
    			portletState.setSelectedPortalUser((PortalUser)portletState.getUserManagementPortletUtil().getEntityObjectById(PortalUser.class, id1));
    			aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/viewprofile.jsp");
    		}
//    		else if(selectedApplicantAction.equalsIgnoreCase("delete"))
//    		{
//    			if(portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_MANAGE_APPLICANT))
//    			{
//    				deleteApplicant(aReq, aRes, portletState, selectedApplicant);
//    			}
//    			else
//    			{
//    				portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
//    			}
//    		}else if(selectedApplicantAction.equalsIgnoreCase("approve"))
//    		{
//    			if(portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_MANAGE_APPLICANT))
//    			{
//    				approveApplicant(aReq, aRes, portletState, selectedApplicant);
//	    		}
//				else
//				{
//					portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
//				}
//    		}else if(selectedApplicantAction.equalsIgnoreCase("disapprove"))
//    		{
//    			if(portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_MANAGE_APPLICANT))
//    			{
//    				disapproveApplicant(aReq, aRes, portletState, selectedApplicant);
//    			}
//				else
//				{
//					portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
//				}
//    		}
        	
        }
        
        
//        if(action.equalsIgnoreCase(EUC_DESK_ACTIONS.APPROVE_PORTAL_USER_SIGNUP.name()))
//        {
//        	//set corpoate indivudla listings
//        		// TODO Auto-generated method stub
//        	if(portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_MANAGE_APPLICANT))
//			{
//	    		String selectedApplicant= Long.toString(portletState.getSelectedApplicant().getId());
//	    		String selectedApplicantAction= aReq.getParameter("act");
//	    		if(selectedApplicantAction.equalsIgnoreCase("back"))
//	    		{
//	    			portletState.setSelectedApplicant(null);
//	            	if(portletState.getCurrentTab().equals(VIEW_TABS.CREATE_A_PORTAL_USERS))
//	            		aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepone.jsp");
//	            	else
//	            		aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/applicantlisting/applicantlisting.jsp");
//	            	
//	    		}else if(selectedApplicantAction.equalsIgnoreCase("approve"))
//	    		{
//	    			approveApplicant(aReq, aRes, portletState, selectedApplicant);
//	    		}else if(selectedApplicantAction.equalsIgnoreCase("reject"))
//	    		{
//	            	disapproveApplicant(aReq, aRes, portletState, selectedApplicant);
//	    		}
//			}else
//			{
//				portletState.addError(aReq, "You do not have the appropriate permissions/rights to carry out this action. Request for these rights from the system from the Administrator", portletState);
//			}
//        	
//        }
//        
        
        
        
        
	}

	private void createNewPassword(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, UserManagementPortletState portletState) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		String oldpassword =  aReq.getParameter("oldpassword");
		String password =  aReq.getParameter("password");
		String confirmPassword =  aReq.getParameter("passwordconfirm");
		boolean proceed = false;
		if(password!=null && confirmPassword!=null && password.equals(confirmPassword))
		{
			User user = null;
			try {
				long login = UserLocalServiceUtil.authenticateForBasic(ECIMSConstants.COMPANY_ID, CompanyConstants.AUTH_TYPE_EA, 
						portletState.getPortalUser().getEmailAddress(), oldpassword);
				if(login==0)
				{
					log.info("User Credentials are invalid");
					portletState.addError(aReq, "Invalid login credentials", portletState);
					aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/setpassword.jsp");
				}
				else
				{
					log.info("User Credentials are Valid");
					try {
						user = UserLocalServiceUtil.updatePassword(Long.valueOf(portletState.getPortalUser().getUserId()), 
								password, confirmPassword, false);
						if(user!=null)
						{
							PortalUser pu1 = portletState.getPortalUser();
							
							new Util().pushAuditTrail(swpService2, pu1.getId().toString(), ECIMSConstants.APPLICANT_UPDATE_PASSWORD, 
									portletState.getRemoteIPAddress(), portletState.getPortalUser().getUserId());
							portletState.addSuccess(aReq, "Your ECIMS account password has  been changed successfully.", portletState);
							aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/setpassword.jsp");
							proceed = true;
						}else
						{
							portletState.addError(aReq, "ECIMS account activation failed. Please try again.", portletState);
							aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/setpassword.jsp");
						}
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						portletState.addError(aReq, "ECIMS account password change failed. Invalid account found. Please contact " +
								"administrator of this site.", portletState);
						aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/setpassword.jsp");
						
					} catch (PortalException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						portletState.addError(aReq, "ECIMS account password change failed. Please try again.", portletState);
						aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/setpassword.jsp");
					} catch (SystemException e) {
						// TODO Auto-generated ca
						portletState.addError(aReq, "ECIMS account password change failed. Please try again.", portletState);
						aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/setpassword.jsp");
					}
				}
			} catch (PortalException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				portletState.addError(aReq, "ECIMS account password change failed. Please try again.", portletState);
				aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/setpassword.jsp");
			} catch (SystemException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				portletState.addError(aReq, "ECIMS account password change failed. Please try again.", portletState);
				aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/setpassword.jsp");
			}
			
			
			
			
		}else
		{
			portletState.addError(aReq, "Your passwords do not match. Ensure you provide the same password in both fields.", portletState);
			aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/setpassword.jsp");
		}
		
	
	}

	private void savePortalUserPermission(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			UserManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String actNow = aReq.getParameter("act");
    	String actId = aReq.getParameter("actId");
    	try
    	{
    		Long act_id = Long.valueOf(actId);
    		PortalUser pu = (PortalUser)portletState.getUserManagementPortletUtil().getEntityObjectById(PortalUser.class, act_id);
    		
    		
    		if(pu!=null)
    		{
    			Collection<Permission> ptList = (Collection<Permission>)portletState.getUserManagementPortletUtil().getPortalUserPermissions(pu);
    			if(ptList!=null && ptList.size()>0)
    			{
    				for(Iterator<Permission> it = ptList.iterator(); it.hasNext();)
    				{
    					Permission prm = it.next();
    					swpService2.deleteRecord(prm);
    				}
    			}
    			
    			String[] permissionList = aReq.getParameterValues("permission");
				if(permissionList!=null && permissionList.length>0)
				{
					for(int c=0; c<permissionList.length; c++)
					{
						PermissionType pt = PermissionType.fromString(permissionList[c]);
						Permission perm = new Permission();
						perm.setDateCreated(new Timestamp((new Date()).getTime()));
						perm.setPermissionType(pt);
						perm.setPortalUser(pu);
						swpService2.createNewRecord(perm);
					}
				}
				if(permissionList!=null && permissionList.length>0)
				{
					portletState.addSuccess(aReq, "Permissions updated successfully", portletState);
				}else
				{
					portletState.addError(aReq, "You have not assigned any permissions to this user. You must assign at least one permission to this type of user before you can proceed", portletState);
					aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/steptwo_instep.jsp");
				}
    		}
    	}catch(NumberFormatException e)
    	{
    		portletState.addError(aReq, "Invalid Portal User Selected. Select a valid portal user before carrying out this action" +
    				"", portletState);
    	}
    	
	}

	private void createPortalUserStepTwoInside(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			UserManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String[] permissionList = aReq.getParameterValues("permission");
		ArrayList<PermissionType> ar = null;
		if(permissionList!=null && permissionList.length>0)
		{
			ar = new ArrayList<PermissionType>();
			for(int c=0; c<permissionList.length; c++)
			{
				PermissionType pt = PermissionType.fromString(permissionList[c]);
				ar.add(pt);
			}
			portletState.setPtList(ar);
		}
		if(portletState.getPtList()!=null && portletState.getPtList().size()>0)
		{
			aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/steptwo.jsp");
		}else
		{
			portletState.addError(aReq, "You have not assigned any permissions to this user. You must assign at least one permission to this type of user before you can proceed", portletState);
			aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/steptwo_instep.jsp");
		}
	}
	
	
	private void editPortalUserPermissions(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			UserManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String[] permissionList = aReq.getParameterValues("permission");
		ArrayList<PermissionType> ar = null;
		if(permissionList!=null && permissionList.length>0)
		{
			ar = new ArrayList<PermissionType>();
			for(int c=0; c<permissionList.length; c++)
			{
				PermissionType pt = PermissionType.fromString(permissionList[c]);
				ar.add(pt);
			}
			portletState.setPtList(ar);
		}
		if(portletState.getPtList()!=null && portletState.getPtList().size()>0)
		{
			aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/steptwo.jsp");
		}else
		{
			portletState.addError(aReq, "You have not assigned any permissions to this user. You must assign at least one permission to this type of user before you can proceed", portletState);
			aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/steptwo_instep.jsp");
		}
	}

	

	private void createPortalUserStepThree(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			UserManagementPortletState portletState) {
		// TODO Auto-generated method stub
		
	}

	private void createPortalUserStepTwo(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			UserManagementPortletState portletState) {
		// TODO Auto-generated method stub

		
		State state = portletState.getUserManagementPortletUtil().getStateByName(portletState.getState());
		String usertype_ = portletState.getUsertype();
		RoleType rt = portletState.getUserManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.fromString(portletState.getUsertype()));
		
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
		
		Company company =null;
		if(usertype_.equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER.getValue()))
		{
			log.info("portletState.getCompanyName()" + portletState.getCompanyName());
			company = new Company();
			company.setName(portletState.getCompanyName());
			company = (Company)swpService.createNewRecord(company);
		}
		
		log.info("762identificationMeans===" + portletState.getIdentificationType());
		PortalUser pu = createPortalUser(portletState.getTaxIdNumber(), portletState.getLastname(), portletState.getFirstname(),
				 portletState.getOthername(),  portletState.getContactEmailAddress(),  portletState.getCountryCode(), 
				 portletState.getMobile(),  portletState.getNationality(),  state, null,
				null, null, null, portletState.getDob(), rt, 
				null, null, null, null, null, company, portletState, aReq, aRes, 
				placeOfIssue, identificationNumber, identificationFileName, identificationExpiryDate, issueDate, portletState.getIdentificationType());
	
		if(pu!=null)
		{
			
			if(usertype_.equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER.getValue()))
			{
				pu.setCompany(company);
			}
			
			if(portletState.getAgencyEntity()!=null)
			{
				if(rt.getRole().getName().equals(RoleConstants.ROLE_TYPE_NSA_USER_GROUP))
				{
					portletState.setAgencyEntity((Agency)portletState.getUserManagementPortletUtil().getAgencyByType(AgencyType.ONSA_GROUP, "ONSA"));
				}
				pu.setAgency(portletState.getAgencyEntity());
				swpService.updateRecord(pu);
				portletState.setAgencyEntity(null);
			}
			
			if(usertype_.equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER.getValue()))
			{
				Applicant applicant = new Applicant();
				applicant.setApplicantNumber("APN" + RandomStringUtils.random(8, false, true));
				applicant.setApplicantType(ApplicantType.APPLICANT_TYPE_CORPORATE);
				applicant.setBlackList(Boolean.FALSE);
				applicant.setExceptionList(Boolean.FALSE);
				applicant.setNextOfKinAddress("");
				applicant.setNextOfKinName("");
				applicant.setNextOfKinPhoneNumber("");
				applicant.setNextOfKinRelationship(KinRelationshipType.KIN_RELATIONSHIP_TYPE_OTHER);
				applicant.setPortalUser(pu);
				applicant.setStatus(ApplicantStatus.APPLICANT_STATUS_APPROVED);
				applicant = (Applicant)swpService.createNewRecord(applicant);
				log.info("Applicant Id = " + applicant.getId());
			}
			
			if(portletState.getPtList()!=null && portletState.getPtList().size()>0)
			{
				for(Iterator<PermissionType> it = portletState.getPtList().iterator(); it.hasNext();)
				{
					Permission pm = new Permission();
					pm.setDateCreated(new Timestamp((new Date()).getTime()));
					pm.setPermissionType(it.next());
					pm.setPortalUser(pu);
					pm = (Permission)swpService2.createNewRecord(pm);
				}
			}
			String successMessage = "Account successfully created.";
			portletState.reinitializeForCreateCorporateIndividual(portletState);
			portletState.addSuccess(aReq, successMessage, portletState);
			aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepthree.jsp");
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/steptwo.jsp");
		}
	}
	
	
	
	private void editPortalUserStepTwo(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			UserManagementPortletState portletState) {
		// TODO Auto-generated method stub

		State state = portletState.getUserManagementPortletUtil().getStateByName(portletState.getState());
		//RoleType rt = portletState.getUserManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.fromString(portletState.getUsertype()));
		PortalUser pu = editPortalUser(portletState.getTaxIdNumber(), portletState.getLastname(), portletState.getFirstname(),
				 portletState.getOthername(),  portletState.getContactEmailAddress(),  portletState.getCountryCode(), 
				 portletState.getMobile(),  portletState.getNationality(),  state, null,
				null, null, null, portletState.getDob(), null, 
				null, null, null, null, null, null, portletState, aReq, aRes);
		
		if(pu!=null)
		{
			
			String successMessage = "Account successfully updated.";
			portletState.reinitializeForCreateCorporateIndividual(portletState);
			portletState.addSuccess(aReq, successMessage, portletState);
			aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepthree.jsp");
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/steptwo_edit.jsp");
		}
	}
	
	
	private void createPortalUserStepZero(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			UserManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String usertype = aReq.getParameter("usertype");
		log.info("usertype==" + usertype);
		log.info("RoleTypeConstants.ROLE_NSA_USER.getValue()==" + RoleTypeConstants.ROLE_NSA_USER.getValue());
		portletState.setUsertype(usertype);
		if(portletState.getUsertype()!=null && portletState.getUsertype().equalsIgnoreCase(RoleTypeConstants.ROLE_ACCREDITOR_USER.getValue()))
		{
			portletState.setAgencyList(portletState.getUserManagementPortletUtil().getAllAgencyByAgencyType(AgencyType.ACCREDITOR_GROUP));
		}else if(portletState.getUsertype()!=null && portletState.getUsertype().equalsIgnoreCase(RoleTypeConstants.ROLE_INFORMATION_USER.getValue()))
		{
			portletState.setAgencyList(portletState.getUserManagementPortletUtil().getAllAgencyByAgencyType(AgencyType.INFORMATION_GROUP));
		}else if(portletState.getUsertype()!=null && portletState.getUsertype().equalsIgnoreCase(RoleTypeConstants.ROLE_REGULATOR_USER.getValue()))
		{
			portletState.setAgencyList(portletState.getUserManagementPortletUtil().getAllAgencyByAgencyType(AgencyType.REGULATOR_GROUP));
		}else if(portletState.getUsertype()!=null && portletState.getUsertype().equalsIgnoreCase(RoleTypeConstants.ROLE_NSA_USER.getValue()))
		{
			portletState.setAgencyList(portletState.getUserManagementPortletUtil().getAllAgencyByAgencyType(AgencyType.ONSA_GROUP));
		}
		if(usertype.equals("-1"))
		{
			portletState.addError(aReq, "Select the type of user you wish to create.", portletState);
			aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepzero.jsp");
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepone.jsp");
		}
	}

	
	
	private void createPortalUserStepOne(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			UserManagementPortletState portletState,  UploadPortletRequest uploadRequest) {
		// TODO Auto-generated method stub
		try
		{
			log.info("CREATEPORTALUSERSTEPONE ===");
			
			
			String folder = ECIMSConstants.NEW_APPLICANT_DIRECTORY;
			log.info("Folder===" + folder);
			String realPath = getPortletContext().getRealPath("/");
			log.info("folder=" + folder + " && realPath=" + realPath);
			String identificationMeans = uploadRequest.getParameter("identificationMeans");
			log.info("identificationMeans===" + identificationMeans);
			String imageName=null;
			if(identificationMeans!=null && identificationMeans.equals(IdentificationType.IDENTIFICATION_TYPE_NATIONAL_ID.getValue()))
			{
				 imageName = "nationalIdPhoto";
			}else if(identificationMeans!=null && identificationMeans.equals(IdentificationType.IDENTIFICATION_TYPE_INTL_PASSPORT.getValue()))
			{
				 imageName = "intlPassportPhoto";
			}else if(identificationMeans!=null && identificationMeans.equals(IdentificationType.IDENTIFICATION_TYPE_DRIVERS_LICENSE.getValue()))
			{
				 imageName = "driversPhoto";
			}else if(identificationMeans!=null && identificationMeans.equals(IdentificationType.IDENTIFICATION_TYPE_PVC.getValue()))
			{
				 imageName = "pvcPhoto";
			} 
			
			
			
			if(imageName==null || (imageName!=null && imageName.equals("")))
			{
				portletState.addError(aReq, "Select a means of identification before you can proceed", portletState);
				aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepone.jsp");
			}
			else
			{
    			System.out.println("Size for passportPhoto: "+uploadRequest.getSize("passportPhoto"));
    			System.out.println("Size for "+imageName+": "+uploadRequest.getSize(imageName));
    			if (uploadRequest.getSize("passportPhoto")==0 && uploadRequest.getSize(imageName)==0) {
    				portletState.addError(aReq, "Ensure you select your passport photo and photo showing your means of identification", portletState);
    				aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepone.jsp");
    			}
    			
    			
				String lastname = uploadRequest.getParameter("lastname");
				String firstname = uploadRequest.getParameter("firstname");
				String othername = uploadRequest.getParameter("othername");
				String contactEmailAddress = uploadRequest.getParameter("contactEmailAddress");
				String dob = uploadRequest.getParameter("dob");
				String countryCode = "234";
				String mobile = countryCode + uploadRequest.getParameter("mobile");
				log.info("1");
				mobile = new Util().formatMobile(mobile);
				String nationality = uploadRequest.getParameter("nationality");
				String stateStr = uploadRequest.getParameter("state");
				String taxIdNumber = uploadRequest.getParameter("taxIdNumber");
				State state = portletState.getUserManagementPortletUtil().getStateByName(stateStr);
				
				log.info("22identificationMeans===" + portletState.getIdentificationType());
				
				portletState.setLastname(lastname);
				portletState.setFirstname(firstname);
				portletState.setOthername(othername);
				portletState.setContactEmailAddress(contactEmailAddress);
				portletState.setDob(dob);
				portletState.setCountryCode(countryCode);
				portletState.setMobile(mobile);
				portletState.setNationality(nationality);
				portletState.setState(stateStr);
				portletState.setTaxIdNumber(taxIdNumber);
				portletState.setIdentificationType(identificationMeans);
				
				
				if(portletState.getUsertype()!=null && (portletState.getUsertype().equals(RoleTypeConstants.ROLE_INFORMATION_USER.getValue()) || 
			    		 portletState.getUsertype().equals(RoleTypeConstants.ROLE_REGULATOR_USER.getValue()) || 
			    		 portletState.getUsertype().equals(RoleTypeConstants.ROLE_ACCREDITOR_USER.getValue()) || 
			    		 portletState.getUsertype().equals(RoleTypeConstants.ROLE_NSA_USER.getValue()) ))
			     {
					String agency = uploadRequest.getParameter("agency");
					Agency ag = (Agency)portletState.getUserManagementPortletUtil().getEntityObjectById(Agency.class, Long.valueOf(agency));
					try{
						portletState.setAgencyEntity(ag);
					}catch(NumberFormatException e)
					{
						e.printStackTrace();
					}
			     }
				
				if(portletState.getUsertype()!=null && portletState.getUsertype().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER.getValue()))
				{
					portletState.setCompanyName(uploadRequest.getParameter("companyName"));
					log.info("portletState.getCompanyName()" + portletState.getCompanyName());
				}
				
				
				String nationalIdNumber = null;
				String natlissueDate = null;
				String intlpassportExpiryDate = null;
				String intlpassportIdNumber = null;
				String intlissuancedate = null;
				String intlplaceOfIssue = null;
				String driversExpiryDate = null;
				String driversIdNumber = null;
				String driversissuancedate = null;
				String driversplaceOfIssue = null;
				String pvcNumber = null;
				String pvcissueDate = null;
				ImageUpload passportImageFile = uploadImage(uploadRequest, "passportPhoto", folder);
				ImageUpload identityPhoto = null;
				
				
				if(passportImageFile!=null && (passportImageFile.isUploadValid()))
				{

					String passportPhoto = passportImageFile.getNewFileName();
					
					log.info("322identificationMeans===" + portletState.getIdentificationType());
					log.info("422identificationMeans===" + identificationMeans);
					if(identificationMeans!=null && identificationMeans.equals(IdentificationType.IDENTIFICATION_TYPE_NATIONAL_ID.getValue()))
					{
						 nationalIdNumber = uploadRequest.getParameter("nationalIdNumber");
						 natlissueDate = uploadRequest.getParameter("natlissueDate");
						 portletState.setNationalIdNumber(nationalIdNumber);
						 portletState.setNatlissueDate(natlissueDate);
					}else if(identificationMeans!=null && identificationMeans.equals(IdentificationType.IDENTIFICATION_TYPE_INTL_PASSPORT.getValue()))
					{
						 intlpassportExpiryDate = uploadRequest.getParameter("intlpassportExpiryDate");
						 intlpassportIdNumber = uploadRequest.getParameter("intlpassportIdNumber");
						 intlissuancedate = uploadRequest.getParameter("intlissuancedate");
						 intlplaceOfIssue = uploadRequest.getParameter("intlplaceOfIssue");
						 portletState.setIntlissuancedate(intlissuancedate);
						 portletState.setIntlpassportExpiryDate(intlpassportExpiryDate);
						 portletState.setIntlpassportIdNumber(intlpassportIdNumber);
						 portletState.setIntlplaceOfIssue(intlplaceOfIssue);
					}else if(identificationMeans!=null && identificationMeans.equals(IdentificationType.IDENTIFICATION_TYPE_DRIVERS_LICENSE.getValue()))
					{
						 driversExpiryDate = uploadRequest.getParameter("driversExpiryDate");
						 driversIdNumber = uploadRequest.getParameter("driversIdNumber");
						 driversissuancedate = uploadRequest.getParameter("driversissuancedate");
						 driversplaceOfIssue = uploadRequest.getParameter("driversplaceOfIssue");
						 portletState.setDriversExpiryDate(driversExpiryDate);
						 portletState.setDriversIdNumber(driversIdNumber);
						 portletState.setDriversissuancedate(driversissuancedate);
						 portletState.setDriversplaceOfIssue(driversplaceOfIssue);
					} 
					else if(identificationMeans!=null && identificationMeans.equals(IdentificationType.IDENTIFICATION_TYPE_PVC.getValue()))
					{
						 pvcNumber = uploadRequest.getParameter("pvcNumber");
						 pvcissueDate = uploadRequest.getParameter("pvcissueDate");
						 portletState.setPvcNumber(pvcNumber);
						 portletState.setPvcIssueDate(pvcissueDate);
					}
					
					
					
					
					
					
					if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_NATIONAL_ID.getValue()))
					{
						identityPhoto = uploadImage(uploadRequest, "nationalIdPhoto", folder);
						if(identityPhoto!=null && (identityPhoto.isUploadValid()))
						{
	    					String nationalIdPhoto = identityPhoto.getNewFileName();
	    					portletState.setIdentificationFileName(nationalIdPhoto);
						}
					}else if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_DRIVERS_LICENSE.getValue()))
					{
						identityPhoto = uploadImage(uploadRequest, "driversPhoto", folder);
						if(identityPhoto!=null && (identityPhoto.isUploadValid()))
						{
	    					String driversPhoto = identityPhoto.getNewFileName();
	    					portletState.setIdentificationFileName(driversPhoto);
						}
					}else if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_INTL_PASSPORT.getValue()))
					{
						identityPhoto = uploadImage(uploadRequest, "intlPassportPhoto", folder);
						if(identityPhoto!=null && (identityPhoto.isUploadValid()))
						{
	    					String intlPassportPhoto = identityPhoto.getNewFileName();
	    					portletState.setIdentificationFileName(intlPassportPhoto);
						}
					}
					else if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_PVC.getValue()))
					{
						identityPhoto = uploadImage(uploadRequest, "pvcPhoto", folder);
						if(identityPhoto!=null && (identityPhoto.isUploadValid()))
						{
	    					String pvcPhotoPhoto = identityPhoto.getNewFileName();
	    					portletState.setIdentificationFileName(pvcPhotoPhoto);
						}
					}
					portletState.setPassportPhoto(passportPhoto);
					
					
					if(validateCreatePortalUser(portletState.getUsertype(), portletState.getAgencyEntity(), lastname, 
							firstname, othername, contactEmailAddress, dob, countryCode, 
							mobile, nationality, state, false, portletState, aReq, aRes, portletState.getIdentificationFileName()))
					{
						if(portletState.getUsertype()!=null && (portletState.getUsertype().equals(RoleTypeConstants.ROLE_NSA_USER.getValue())))
					    {
							aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/steptwo_instep.jsp");
					    }else
					    {
					    	aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/steptwo.jsp");
					    }
					}else
					{
						aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepone.jsp");
					}
				}else
				{
					if(passportImageFile==null)
					{
						aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepone.jsp");
						portletState.addError(aReq, "Uploading passport failed. Ensure you selected a valid file", portletState);
					}else if(identityPhoto==null)
					{
						aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepone.jsp");
						portletState.addError(aReq, "Uploading means of identification photograph failed. Ensure you selected a valid file", portletState);
					}
				}
			}
		}catch(NumberFormatException e)
		{
			e.printStackTrace();
			aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepone.jsp");
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

	
	private void editPortalUserStepOne(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			UserManagementPortletState portletState, UploadPortletRequest uploadRequest) {
		// TODO Auto-generated method stub
		try
		{
			
			String lastname = uploadRequest.getParameter("lastname");
			String taxIdNumber = uploadRequest.getParameter("taxIdNumber");
			String firstname = uploadRequest.getParameter("firstname");
			String othername = uploadRequest.getParameter("othername");
			String contactEmailAddress = uploadRequest.getParameter("contactEmailAddress");
			String dob = uploadRequest.getParameter("dob");
			String countryCode = "234";
			String mobile = uploadRequest.getParameter("mobile");
			String nationality = uploadRequest.getParameter("nationality");
			String stateStr = uploadRequest.getParameter("state");
			State state = portletState.getUserManagementPortletUtil().getStateByName(stateStr);
			
			
			portletState.setTaxIdNumber(taxIdNumber);
			
			portletState.setLastname(lastname);
			portletState.setFirstname(firstname);
			portletState.setOthername(othername);
			portletState.setContactEmailAddress(contactEmailAddress);
			portletState.setDob(dob);
			portletState.setCountryCode(countryCode);
			portletState.setMobile(mobile);
			portletState.setNationality(nationality);
			portletState.setState(stateStr);
			
			
			if(validateCreatePortalUser(portletState.getUsertype(), portletState.getAgencyEntity(), lastname, 
					firstname, othername, contactEmailAddress, dob, countryCode, 
					mobile, nationality, state, true, portletState, aReq, aRes, null))
			{
				aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/steptwo_edit.jsp");
			}else
			{
				aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepone_edit.jsp");
			}
		}catch(NumberFormatException e)
		{
			e.printStackTrace();
			aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepone_edit.jsp");
		}
	}

	private boolean validateCreatePortalUser(String userType, Agency agency, String lastname,
			String firstname, String othername, String contactEmailAddress,
			String dob, String countryCode, String mobile, String nationality,
			State state, boolean editTrue, UserManagementPortletState portletState, 
			ActionRequest aReq, ActionResponse aRes, String meansOfIdPhoto) {
		// TODO Auto-generated method stub
		PortalUser pu = null;
		ArrayList<String> errorMessage = new ArrayList<String>();
		
		
		if(editTrue==false && portletState.getUsertype()!=null 
				&& 
				(portletState.getUsertype().equalsIgnoreCase(RoleTypeConstants.ROLE_ACCREDITOR_USER.getValue()) || 
				portletState.getUsertype().equalsIgnoreCase(RoleTypeConstants.ROLE_INFORMATION_USER.getValue()) || 
				portletState.getUsertype().equalsIgnoreCase(RoleTypeConstants.ROLE_REGULATOR_USER.getValue()) || 
				portletState.getUsertype().equalsIgnoreCase(RoleTypeConstants.ROLE_NSA_USER.getValue())) 
				&& agency==null)
		{
			errorMessage.add("Select the agency or body this user belongs to.");
		}
		if(editTrue==false && meansOfIdPhoto==null)
		{
			errorMessage.add("Provide a scanned copy of your selected means of identification");
		}
		if(lastname==null || (lastname!=null && lastname.trim().length()==0))
		{
			errorMessage.add("Provide the users surname.");
		}
		if(firstname==null || (firstname!=null && firstname.trim().length()==0))
		{
			errorMessage.add("Provide the users first name.");
		}
		if(editTrue)
		{
//			if(portletState.getSelectedPortalUserId()!=null && portletState.getSelectedPortalUserId().length()>0)
//			{
//				pu = portletState.getUserManagementPortletUtil().getPortalUserByEmailAddressAndNotUserId(
//					contactEmailAddress, Long.valueOf(portletState.getSelectedPortalUserId()));
//				if(pu!=null)
//				{
//					errorMessage.add("The email address provided has already been used on this platform. Provide another email address.");
//				}									
//			}else
//			{
//				errorMessage.add("Invalid User has been selected.");
//			}
		}else
		{
			pu = portletState.getUserManagementPortletUtil().getPortalUserByEmailAddress(contactEmailAddress);
			if(pu!=null)
			{
				errorMessage.add("The email address provided has already been used on this platform. Provide another email address.");
			}
		}
		
		
		try{
			Date df = sdf.parse(dob);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, -18);
			Date yearsAgo18 = cal.getTime();
			if(df.after(yearsAgo18))
			{
				errorMessage.add("User must be above 18 years to register on this platform");
			}
		}
		catch(ParseException e)
		{
			e.printStackTrace();
			errorMessage.add("Invalid Date Of birth specified");
		}											
		
		if(errorMessage!=null && errorMessage.size()>0)
		{
			log.info("Error message = " + errorMessage);
			String str = "<ul>";
			int c = 1;
			for(Iterator<String> it = errorMessage.iterator(); it.hasNext();)
			{
				str += "<li>" + it.next() + "</li>";
			}
			str += "</ul>";
			str = "<strong>Please attend to these issues before you can proceed.</strong></br>" + str; 
			portletState.addError(aReq, str, portletState);
			return false;
		}
		else
		{
			return true;
		}
	}
	
	

	private PortalUser createPortalUser(String taxId, String lastName, String firstName,
			String otherName, String email, String countryCode, String mobile,
			String nationality, State state, String gender,
			String maritalStatus, String addressLine1, String addressLine2, String dob, RoleType roleType, 
			String residenceCity, String residenceState, String residencePhoneNumber,
			String designation, String passportPhoto, Company company, 
			UserManagementPortletState portletState, ActionRequest aReq, ActionResponse aRes, String placeOfIssue, String identificationNumber, String identificationFileName, String identificationExpiryDate, String issueDate, String identificationType) {
		
		
		
		
		// TODO Auto-generated method stub
		log.info("createPortalUser  inside");
		PortalUser pu = null;
		try
		{
			log.info("1");
			pu = new PortalUser();
			pu.setTaxIdNo(taxId);
			pu.setAddressLine1(addressLine1);
			pu.setAddressLine2(addressLine2);
			pu.setCompany(null);
			Date dateOfBirth = sdf.parse(dob);
			pu.setDateOfBirth(dateOfBirth);
			pu.setEmailAddress(email);
			pu.setFirstName(firstName);
			pu.setSurname(lastName);
			pu.setOtherName(otherName);
			pu.setPhoneNumber(countryCode + new Util().formatMobile(mobile));
			pu.setState(state);
			pu.setAgency(null);
			pu.setDateCreated(new Timestamp((new Date()).getTime()));
			pu.setGender(gender);
			pu.setMaritalStatus(maritalStatus);
			pu.setPasscode(RandomStringUtils.random(6, true, true));
			pu.setActivationCode(UUID.randomUUID().toString().replace("-",  ""));
			pu.setPasscodeGenerateTime(new Timestamp((new Date()).getTime()));
			pu.setStatus(UserStatus.USER_STATUS_ACTIVE);
			pu.setRoleType(roleType);
			pu.setCity(residenceCity == null ? "" : residenceCity);
			if(residenceState!=null)
				pu.setResidenceState(portletState.getUserManagementPortletUtil().getStateByName(residenceState).getId());
			pu.setResidencePhoneNumber(residencePhoneNumber);
			pu.setCompany(company);
			log.info("1");
			long communities[] = new long[1];
			try{
			log.info("roleType = " + roleType);
			log.info("roleType = " + roleType.getName());
			log.info("roleType = " + roleType.getName().getValue());
			}catch(Exception e){}
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
					false,
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
	
	
	
	private PortalUser editPortalUser(String taxId, String lastName, String firstName,
			String otherName, String email, String countryCode, String mobile,
			String nationality, State state, String gender,
			String maritalStatus, String addressLine1, String addressLine2, String dob, RoleType roleType, 
			String residenceCity, String residenceState, String residencePhoneNumber,
			String designation, String passportPhoto, Company company, 
			UserManagementPortletState portletState, ActionRequest aReq, ActionResponse aRes) {
		
		
		
		
		// TODO Auto-generated method stub
		log.info("createPortalUser  inside");
		PortalUser pu = null;
		try
		{
			log.info("1");

			pu = portletState.getSelectedPortalUser();
			Long id = Long.valueOf(pu.getUserId());
			
			User us = UserLocalServiceUtil.getUser(id);
			us.setFirstName(firstName);
			us.setLastName(lastName);
			us.setMiddleName(otherName);
			us = UserLocalServiceUtil.updateUser(us);
			if(us!=null)
			{
				pu.setTaxIdNo(taxId);
				pu.setAddressLine1(addressLine1);
				pu.setAddressLine2(addressLine2);
				pu.setCompany(null);
				Date dateOfBirth = sdf.parse(dob);
				pu.setDateOfBirth(dateOfBirth);
				pu.setEmailAddress(email);
				pu.setFirstName(firstName);
				pu.setSurname(lastName);
				pu.setOtherName(otherName);
				pu.setPhoneNumber(countryCode + new Util().formatMobile(mobile));
				pu.setState(state);
				pu.setDateCreated(new Timestamp((new Date()).getTime()));
				pu.setGender(gender);
				pu.setMaritalStatus(maritalStatus);
				pu.setPasscode(RandomStringUtils.random(6, true, true));
				pu.setActivationCode(UUID.randomUUID().toString().replace("-",  ""));
				pu.setPasscodeGenerateTime(new Timestamp((new Date()).getTime()));
				pu.setStatus(UserStatus.USER_STATUS_ACTIVE);
				pu.setCity(residenceCity == null ? "" : residenceCity);
				if(residenceState!=null)
					pu.setResidenceState(portletState.getUserManagementPortletUtil().getStateByName(residenceState).getId());
				pu.setResidencePhoneNumber(residencePhoneNumber);
				pu.setCompany(company);
				swpService.updateRecord(pu);
				
				
				portletState.addSuccess(aReq, "User account updated successfully", portletState);
				aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepthree.jsp");
				
				
				
				if(portletState.getPtList()!=null && portletState.getPtList().size()>0)
				{
					Collection<Permission> pl = portletState.getUserManagementPortletUtil().getPortalUserPermissions(portletState.getSelectedPortalUser());
					if(pl!=null && pl.size()>0)
					{
						for(Iterator<Permission> iP = pl.iterator(); iP.hasNext();)
						{
							Permission pm = iP.next();
							//swpService.deleteRecord(pm);
						}
					}
					
//					for(Iterator<PermissionType> it = portletState.getPtList().iterator(); it.hasNext();)
//					{
//						Permission pm = new Permission();
//						pm.setDateCreated(new Timestamp((new Date()).getTime()));
//						pm.setPermissionType(it.next());
//						pm.setPortalUser(pu);
//						pm = (Permission)swpService.createNewRecord(pm);
//					}
				}
			}else
			{
				portletState.addError(aReq, "User account was not updated successfully", portletState);
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
			portletState.addError(aReq, "User account was not updated successfully", portletState);
		}
		
		
		return pu;
	}
	
	
	
	public static PortalUser handleCreateUserOrbitaAccount(PortalUser user, String firstname, String middlename, String surname, String email, 
			long[] communities, AuditTrail auditTrail, ServiceContext serviceContext, SwpService sService, String loggedInUserId,
			boolean passwordReset, boolean active, boolean sendEmail, boolean sendSms, String systemUrl, 
			UserManagementPortletState portletState, ActionRequest aReq, ActionResponse aRes) {			
		
		Logger log = Logger.getLogger(UserManagementPortlet.class);
		log.info("handleCreateUserOrbitaAccount = " + portletState.getSendingEmail().getValue());
		log.info("getSendingEmailPassword = " + portletState.getSendingEmailPassword().getValue());
		log.info("getSendingEmailPort = " + portletState.getSendingEmailPort().getValue());
		log.info("getSendingEmailUsername = " + portletState.getSendingEmailUsername().getValue());

		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		Logger log1 = Logger.getLogger(UserManagementPortlet.class);
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
			int birthdayMonth = Integer.parseInt(birthdate[1]) - 1;
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
//								auditTrail.setActivity("Create Portal User " + createdUser.getId());
//								sService.createNewRecord(auditTrail);
								new Util().pushAuditTrail(sService, createdUser.getId().toString(), ECIMSConstants.USER_SIGNUP, 
										portletState.getRemoteIPAddress(), createdUser.getId().toString());
								
								if(sendEmail){
									//sendMail(firstname, surname, email);
									
									
									emailer.emailNewPortalUserAccount
									(createdUser.getEmailAddress(), 
											"", password1, portletState.getSystemUrl().getValue(), 
											createdUser.getFirstName(), createdUser.getSurname(), createdUser.getRoleType().getName().getValue(), 
											"New " + portletState.getApplicationName().getValue() + " Account Created for you", 
											portletState.getApplicationName().getValue());
									
								}
								if(sendSms){
									String message = "Hello, Your Online Account has been " +
											"successfully created on " + systemUrl + ". Your login email is " +  
											emailAddress + " and password: " + password1;
									try{
//											message = "Approval request awaiting your action. " +
//													"Visit " + portletState.getSystemUrl().getValue() + " to view requests awaiting your " +
//													"approval/disapproval action";
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

								

								System.out.println("Setting Orbita StaffId =" + newlyCreatedUser.getUserId());
								createdUser.setUserId(Long.toString(newlyCreatedUser.getUserId()));
								sService.updateRecord(createdUser);
								
							} catch (Exception e) { 
								log1.error("", e);
//								portletState.addError(aReq, "The online " + portletState.getApplicationName().getValue() + " web Account could not be created" +
//									" on " + systemUrl + ". Please try again", portletState); 
								
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
							return null ;
						}catch(UserEmailAddressException e)
						{
							portletState.addError(aReq, "Your email address seems to be invalid. Please check to confirm.", portletState);
							return null ;
						}
						catch(ContactBirthdayException e)
						{
							portletState.addError(aReq, "Your birthday seems to be invalid. Please check to confirm.", portletState);
							return null ;
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
			Logger logger = Logger.getLogger(UserManagementPortlet.class);
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
	

}
