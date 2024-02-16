package com.ecims.portlet.guest;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.PatternSyntaxException;

import javax.imageio.ImageIO;
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

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

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
import smartpay.entity.RoleType;
import smartpay.entity.State;
import smartpay.entity.enumerations.ApplicantStatus;
import smartpay.entity.enumerations.ApplicantType;
import smartpay.entity.enumerations.IdentificationType;
import smartpay.entity.enumerations.KinRelationshipType;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.entity.enumerations.UserStatus;
import smartpay.service.SwpService;

import com.ecims.commins.ECIMSConstants;
import com.ecims.commins.ImageUpload;
import com.ecims.commins.Mailer;
import com.ecims.commins.SendSms;
import com.ecims.commins.Util;
import com.ecims.portlet.guest.GuestPortlet;
import com.ecims.portlet.guest.GuestPortletState;
import com.ecims.portlet.guest.GuestPortletUtil;
import com.ecims.portlet.guest.GuestPortletState.APPLICANT_ACTIONS;
import com.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.ContactBirthdayException;
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
 * Portlet implementation class GuestPortlet
 */
public class GuestPortlet extends MVCPortlet {
	private Logger log = Logger.getLogger(GuestPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	GuestPortletUtil util = GuestPortletUtil.getInstance();
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
		GuestPortletState portletState = 
				GuestPortletState.getInstance(renderRequest, renderResponse);

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
		
		GuestPortletState portletState = GuestPortletState.getInstance(aReq, aRes);
		
		
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
        	String act = aReq.getParameter("act");
        	if(act!=null && act.equalsIgnoreCase("proceedToStepTwo"))
        	{
        		createApplicantStepOne(aReq, aRes, swpService, portletState);
        	}if(act!=null && act.equalsIgnoreCase("backtohome"))
        	{
        		aRes.sendRedirect(portletState.getSystemUrl().getValue());
        	}
        		
        }
        if(action.equalsIgnoreCase(APPLICANT_ACTIONS.CREATE_A_PORTAL_USER_STEP_TWO.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_TWO");
        	//Provide User details
        	String act = aReq.getParameter("act");
        	if(act!=null && act.equalsIgnoreCase("gobacktosteponeguestregister"))
        	{
        		aRes.setRenderParameter("jspPage", "/html/guestportlet/stepone.jsp");
        		portletState.setIdentificationFileName(null);
        	}
        	else 
        		createApplicantStepTwo(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(APPLICANT_ACTIONS.CREATE_A_PORTAL_USER_STEP_THREE.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_THREE");
        	//Preview Data
        	String act = aReq.getParameter("act");
        	if(act!=null && act.equalsIgnoreCase("gobacktosteponeguestregister"))
        		aRes.setRenderParameter("jspPage", "/html/guestportlet/register_individual/steptwo.jsp");
        	else 
        		createApplicantStepThree(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(APPLICANT_ACTIONS.CREATE_A_PORTAL_USER_STEP_FOUR.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_FOUR");
        	//Populate And Create user
        	createApplicantStepFour(aReq, aRes, swpService, portletState);
        		
        }
        
        
        
        if(action.equalsIgnoreCase(APPLICANT_ACTIONS.CREATE_A_CORPORATE_PORTAL_USER_STEP_TWO.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_TWO");
        	//Provide User details
        	String act = aReq.getParameter("act");
        	if(act!=null && act.equalsIgnoreCase("gobacktosteponeguestregister"))
        		aRes.setRenderParameter("jspPage", "/html/guestportlet/stepone.jsp");
        	else 
        		createCorporateApplicantStepTwo(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(APPLICANT_ACTIONS.CREATE_A_CORPORATE_PORTAL_USER_STEP_THREE.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_THREE");
        	//Preview Data
        	String act = aReq.getParameter("act");
        	if(act!=null && act.equalsIgnoreCase("gobacktosteponeguestregister"))
        		aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/steptwo.jsp");
        	else 
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
        	String act = aReq.getParameter("act");
        	if(act!=null && act.equals("resendactivationcode"))
        	{
            	resendactivationcode(aReq, aRes, swpService, portletState);        		
        	}else if(act!=null && act.equals("proceed"))
        	{
        		activatePortalUser(aReq, aRes, swpService, portletState);
        	}
        		
        }
        
        
        if(action.equalsIgnoreCase(APPLICANT_ACTIONS.CREATE_NEW_PASSWORD_PORTAL_USER_ACCOUNT.name()))
        {
        	log.info("ACTIVATE_PORTAL_USER_ACCOUNT");
        	//Populate And Create user
        	createNewPassword(aReq, aRes, swpService, portletState);
        		
        }
        
        
        
	}

	private void resendactivationcode(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, GuestPortletState portletState) {
		// TODO Auto-generated method stub
		String emailAddress = aReq.getParameter("email");
		PortalUser pu = portletState.getGuestPortletUtil().getPortalUserByEmailAddress(emailAddress);
		pu.setPasscode(RandomStringUtils.random(6, true, true).toUpperCase());
		pu.setDateCreated(new Timestamp((new Date()).getTime()));
		swpService2.updateRecord(pu);
		String smsMessage = "A new passcode has been generated for you! \nPasscode: " + pu.getPasscode() + "\nVisit " + portletState.getSystemUrl().getValue() + " to activate your ECIMS account" ;
		SendSms sendSms = new SendSms(pu.getPhoneNumber(), 
				smsMessage, 
				portletState.getMobileApplicationName().getValue(), 
				portletState.getProxyHost().getValue(), 
				portletState.getProxyPort().getValue());
		portletState.addSuccess(aReq, "New Passcode has been generated and sent to your mobile phone number - " + pu.getPhoneNumber(), portletState);
		aRes.setRenderParameter("jspPage", "/html/guestportlet/activation/activateaccount.jsp");
		
	}

	private void createNewPassword(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, GuestPortletState portletState) {
		// TODO Auto-generated method stub
		String password =  aReq.getParameter("password");
		String confirmPassword =  aReq.getParameter("passwordconfirm");
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
					emailNewAccountActivation(aReq, aRes, pu1, password, portletState);
					portletState.addSuccess(aReq, "Your ECIMS account has been activated successfully. Click on the " +
							"<a href='/web/guest/signin'>Login</a> link to first sign in using your login details before you can create an application.", portletState);
					aRes.setRenderParameter("jspPage", "/html/guestportlet/activation/setpasswordstatus.jsp");
					proceed = true;
					portletState.setActivationPortalUser(null);
				}else
				{
					portletState.addError(aReq, "ECIMS account activation failed. Please try again.", portletState);
					aRes.setRenderParameter("jspPage", "/html/guestportlet/activation/setpassword.jsp");
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				portletState.addError(aReq, "ECIMS account activation failed. Invalid account found. Please contact " +
						"administrator of this site.", portletState);
				aRes.setRenderParameter("jspPage", "/html/guestportlet/activation/setpassword.jsp");
				
			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				portletState.addError(aReq, "ECIMS account activation failed. Please try again.", portletState);
				aRes.setRenderParameter("jspPage", "/html/guestportlet/activation/setpassword.jsp");
			} catch (SystemException e) {
				// TODO Auto-generated ca
				portletState.addError(aReq, "ECIMS account activation failed. Please try again.", portletState);
				aRes.setRenderParameter("jspPage", "/html/guestportlet/activation/setpassword.jsp");
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
			aRes.setRenderParameter("jspPage", "/html/guestportlet/activation/setpassword.jsp");
		}
		
	}

	private void emailNewAccountActivation(ActionRequest aReq,
			ActionResponse aRes, PortalUser pu, String password, GuestPortletState portletState) {
		// TODO Auto-generated method stub
		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		emailer.emailNewAccountActivation
			(pu.getEmailAddress(), 
				pu.getFirstName(), pu.getSurname(), "",  portletState.getSystemUrl().getValue(),
				portletState.getApplicationName().getValue() + " Account Activation Successful!", 
				portletState.getApplicationName().getValue(), true, password);
//		(String toEmail,
//				String firstName, String lastName, String reason, String url, String subject, String applicationName, boolean activate) {
		String message = "ECIMS Account Activation successfully. You can now start applying for End-User Certificates";
		try{

				new SendSms(pu.getPhoneNumber(), message, 
						portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
		}catch(Exception e){
			log.error("error sending sms ",e);
		}
	}
	
	
	

	private void activatePortalUser(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, GuestPortletState portletState) {
		// TODO Auto-generated method stub
		String emailAddress = aReq.getParameter("email");
		String activationCode = aReq.getParameter("activationCode");
		String activationCode1 = aReq.getParameter("activationCode2");
		log.info("email==" + emailAddress);
		log.info("activationCode==" + activationCode);
		log.info("activationCode2==" + activationCode1);
		
		if(activationCode.equals(activationCode1))
		{
			if(emailAddress!=null && emailAddress.trim().length()>0)
			{
				PortalUser pu = portletState.getGuestPortletUtil().getPortalUserByEmailAddress(emailAddress);
				//Test 102
				if(pu.getStatus().equals(UserStatus.USER_STATUS_ACTIVE) && pu.getPasscode()==null)
				{
					portletState.setActivationPortalUser(pu);
					aRes.setRenderParameter("jspPage", "/html/guestportlet/activation/setpassword.jsp");
				}
				else if(pu.getStatus().equals(UserStatus.USER_STATUS_INACTIVE) && pu.getPasscode().equals(activationCode))
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
						Date dn = new Date();
						Date dc = pu.getDateCreated();
						Calendar cal = Calendar.getInstance();
						cal.setTime(dc);
						cal.add(Calendar.MINUTE, 30);
						Date minsAgo30 = cal.getTime();
						if(dn.after(minsAgo30))
						{
							portletState.addError(aReq, "This activation passscode has expired. Please use the Resend Activation Code button to resend your code", portletState);
							//aRes.setRenderParameter("jspPage", "/html/guestportlet/activation/activateaccount.jsp");
							try {
								aRes.sendRedirect("/web/guest/new-ecims-account?kickstart=" + aReq.getParameter("kickstart"));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								aRes.setRenderParameter("jspPage", "/html/guestportlet/activation/activateaccount.jsp");
							}
						}else
						{
							//UserLocalServiceUtil.updateLockout(lpUser, false);
							try {
								UserLocalServiceUtil.updateStatus(uId, 0);
								UserLocalServiceUtil.updateLockout(lpUser, false);
								
								pu.setStatus(UserStatus.USER_STATUS_ACTIVE);
								pu.setPasscode(null);
								pu.setActivationCode(null);
								swpService.updateRecord(pu);
								portletState.setActivationPortalUser(pu);
								
//								AuditTrail ad = new AuditTrail();
//								ad.setAction("PORTAL USER ACTIVATION");
//								ad.setActivity("Portal User activates their account");
//								ad.setDate(new Timestamp((new Date()).getTime()));
//								ad.setIpAddress(portletState.getRemoteIPAddress());
//								ad.setUserId(pu.getUserId());
//								swpService.createNewRecord(ad);
								new Util().pushAuditTrail(swpService, pu.getId().toString(), ECIMSConstants.APPLICANT_ACTIVATE, 
										portletState.getRemoteIPAddress(), pu.getUserId());
								aRes.setRenderParameter("jspPage", "/html/guestportlet/activation/setpassword.jsp");
							} catch (PortalException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								portletState.addError(aReq, "We experienced technical issues activating your account. Please try again later.", portletState);
								aRes.setRenderParameter("jspPage", "/html/guestportlet/activation/activateaccount.jsp");
							} catch (SystemException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								portletState.addError(aReq, "We experienced technical issues activating your account. Please try again later.", portletState);
								aRes.setRenderParameter("jspPage", "/html/guestportlet/activation/activateaccount.jsp");
							}
						}
					}
					else
					{
						portletState.addError(aReq, "You are carrying out an invalid action. Please close this window if you have not received an " +
								"activation code to make use on this page", portletState);
						aRes.setRenderParameter("jspPage", "/html/guestportlet/activation/activateaccount.jsp");
					}
				}
				else
				{
					portletState.addError(aReq, "You are carrying out an invalid action. Please close this window if you have not received an " +
							"activation code to make use of on this page", portletState);
					aRes.setRenderParameter("jspPage", "/html/guestportlet/activation/activateaccount.jsp");
				}
			}else
			{	
				portletState.addError(aReq, "Email address provided is invalid. Ensure you clicked on your activation link in your activation email", portletState);
				aRes.setRenderParameter("jspPage", "/html/guestportlet/activation/activateaccount.jsp");
			}
		}else
		{
			portletState.addError(aReq, "Activation Codes typed do not match. Ensure they match before proceeding.", portletState);
			aRes.setRenderParameter("jspPage", "/html/guestportlet/activation/activateaccount.jsp");
		}
	}

	private void createApplicantStepFour(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			GuestPortletState portletState) {
		// TODO Auto-generated method stub
		aRes.setRenderParameter("jspPage", "/html/guestportlet/register_individual/steptwo.jsp");
	}
	
	private void createCorporateApplicantStepFour(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			GuestPortletState portletState) {
		// TODO Auto-generated method stub
		aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/steptwo.jsp");
	}

	private void createApplicantStepThree(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			GuestPortletState portletState) {
		// TODO Auto-generated method stub
		String act = aReq.getParameter("act");
		log.info("act = " + act);
		if(act.equalsIgnoreCase("Proceed"))
		{
			log.info("Proceed = ");
			PortalUser pu = null;
			RoleType roletype = portletState.getGuestPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_END_USER);
			State state = portletState.getGuestPortletUtil().getStateByName(portletState.getState());
			
			String placeOfIssue=null;
			String identificationNumber=null;
			String identificationFileName=null;
			String identificationExpiryDate=null;
			String issueDate=null;
			String identificationType=null;
			log.info("2222==== portletState.getIdentificationType() ==" + portletState.getIdentificationType());
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
			pu = createPortalUser(portletState.getTaxIdNumber(), 
					portletState.getLastName(), portletState.getFirstName(), 
					portletState.getOtherName(), portletState.getEmail(), 
					portletState.getCountryCode(), portletState.getMobile(), portletState.getNationality(), 
					state, portletState.getGender(), 
					portletState.getMaritalStatus(), portletState.getAddressLine1(), 
					portletState.getAddressLine2(), portletState.getDob(), roletype,
					portletState.getResidenceCity(), portletState.getResidenceState(), portletState.getResidencePhoneNumber(),
					portletState.getDesignation(), portletState.getPassportPhoto(), null,
					portletState, aReq, aRes, 
					placeOfIssue, identificationNumber, identificationFileName, identificationExpiryDate, issueDate, portletState.getIdentificationType());
			
			if(pu!=null)
			{
				log.info("pu != null");
				
				
				
				if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_NATIONAL_ID.getValue()))
				{
					createApplicantAccount(portletState.getPassportPhoto(), null, 
								portletState.getIdentificationType(), 
								portletState.getIdentificationFileName(), portletState.getNationalIdNumber(), 
								portletState.getNextOfKinName(), portletState.getNextOfKinAddress(), 
								portletState.getNextOfKinRelationship(), portletState.getNextOfKinPhoneNumber(), 
								portletState.getAccountType(), pu, 
								portletState.getNatlissueDate(), null, 
								aReq, aRes, portletState);
				}else if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_DRIVERS_LICENSE.getValue()))
				{
						createApplicantAccount(portletState.getPassportPhoto(), portletState.getDriversExpiryDate(), 
								portletState.getIdentificationType(), 
								portletState.getIdentificationFileName(), portletState.getDriversIdNumber(), 
								portletState.getNextOfKinName(), portletState.getNextOfKinAddress(), 
								portletState.getNextOfKinRelationship(), portletState.getNextOfKinPhoneNumber(), 
								portletState.getAccountType(), pu, 
								portletState.getDriversplaceOfIssue(), portletState.getDriversissuancedate(), 
								aReq, aRes, portletState);
				}else if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_INTL_PASSPORT.getValue()))
				{
						createApplicantAccount(portletState.getPassportPhoto(), portletState.getIntlpassportExpiryDate(), 
								portletState.getIdentificationType(), 
								portletState.getIdentificationFileName(), portletState.getIntlpassportIdNumber(), 
								portletState.getNextOfKinName(), portletState.getNextOfKinAddress(), 
								portletState.getNextOfKinRelationship(), portletState.getNextOfKinPhoneNumber(), 
								portletState.getAccountType(), pu, 
								portletState.getIntlplaceOfIssue(), portletState.getIntlissuancedate(), 
								aReq, aRes, portletState);
				}else if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_PVC.getValue()))
				{
					createApplicantAccount(portletState.getPassportPhoto(), null, 
							portletState.getIdentificationType(), 
							portletState.getIdentificationFileName(), portletState.getPvcNumber(), 
							portletState.getNextOfKinName(), portletState.getNextOfKinAddress(), 
							portletState.getNextOfKinRelationship(), portletState.getNextOfKinPhoneNumber(), 
							portletState.getAccountType(), pu, 
							null, portletState.getPvcIssueDate(), 
							aReq, aRes, portletState);
				}
				portletState.reinitializeForCreateCorporateIndividual(portletState);
				
			}else
			{
				portletState.setPassportPhoto(null);
				portletState.setCacCertificate(null);
				portletState.setIdentificationFileName(null);
				portletState.setCompanyLogo(null);
				aRes.setRenderParameter("jspPage", "/html/guestportlet/register_individual/steptwo.jsp");
			}
		}else if(act.equalsIgnoreCase("Back"))
		{
			portletState.setPassportPhoto(null);
			portletState.setCacCertificate(null);
			portletState.setIdentificationFileName(null);
			portletState.setCompanyLogo(null);
			aRes.setRenderParameter("jspPage", "/html/guestportlet/register_individual/steptwo.jsp");
		}
	}

	
	private void createCorporateApplicantStepThree(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			GuestPortletState portletState) {
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

  				log.info("DOB22 = " + portletState.getDob());
				PortalUser pu = null;
				RoleType roletype = portletState.getGuestPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.ROLE_END_USER);
				State state = portletState.getGuestPortletUtil().getStateByName(portletState.getState());
				
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
				
				pu = createPortalUser(portletState.getTaxIdNumber(), portletState.getLastName(), portletState.getFirstName(), 
						portletState.getOtherName(), portletState.getEmail(), 
						portletState.getCountryCode(), portletState.getMobile(), portletState.getNationality(), 
						state, portletState.getGender(), 
						portletState.getMaritalStatus(), portletState.getAddressLine1(), 
						portletState.getAddressLine2(), portletState.getDob(), roletype,
						portletState.getResidenceCity(), portletState.getResidenceState(), portletState.getResidencePhoneNumber(),
						portletState.getDesignation(), portletState.getPassportPhoto(), company, 
						portletState, aReq, aRes, 
						placeOfIssue, identificationNumber, identificationFileName, identificationExpiryDate, issueDate, portletState.getIdentificationType());
				 	
				
				if(pu!=null)
				{
					log.info("pu != null");
//					createApplicantAccount(portletState.getPassportPhoto(), portletState.getIdentificationExpiryDate(), 
//							portletState.getIdentificationType(), 
//							portletState.getIdentificationFileName(), portletState.getIdentificationNumber(), 
//							portletState.getNextOfKinName(), portletState.getNextOfKinAddress(), 
//							portletState.getNextOfKinRelationship(), portletState.getNextOfKinPhoneNumber(), 
//							portletState.getAccountType(), pu, 
//							portletState.getPlaceOfIssue(), portletState.getIssueDate(), 
//							aReq, aRes, portletState);
					
					if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_NATIONAL_ID.getValue()))
					{
						createApplicantAccount(portletState.getPassportPhoto(), null, 
									portletState.getIdentificationType(), 
									portletState.getIdentificationFileName(), portletState.getNationalIdNumber(), 
									portletState.getNextOfKinName(), portletState.getNextOfKinAddress(), 
									portletState.getNextOfKinRelationship(), portletState.getNextOfKinPhoneNumber(), 
									portletState.getAccountType(), pu, 
									portletState.getNatlissueDate(), null, 
									aReq, aRes, portletState);
					}else if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_DRIVERS_LICENSE.getValue()))
					{
							createApplicantAccount(portletState.getPassportPhoto(), portletState.getDriversExpiryDate(), 
									portletState.getIdentificationType(), 
									portletState.getIdentificationFileName(), portletState.getDriversIdNumber(), 
									portletState.getNextOfKinName(), portletState.getNextOfKinAddress(), 
									portletState.getNextOfKinRelationship(), portletState.getNextOfKinPhoneNumber(), 
									portletState.getAccountType(), pu, 
									portletState.getDriversplaceOfIssue(), portletState.getDriversissuancedate(), 
									aReq, aRes, portletState);
					}else if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_INTL_PASSPORT.getValue()))
					{
							createApplicantAccount(portletState.getPassportPhoto(), portletState.getIntlpassportExpiryDate(), 
									portletState.getIdentificationType(), 
									portletState.getIdentificationFileName(), portletState.getIntlpassportIdNumber(), 
									portletState.getNextOfKinName(), portletState.getNextOfKinAddress(), 
									portletState.getNextOfKinRelationship(), portletState.getNextOfKinPhoneNumber(), 
									portletState.getAccountType(), pu, 
									portletState.getIntlplaceOfIssue(), portletState.getIntlissuancedate(), 
									aReq, aRes, portletState);
					}else if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_PVC.getValue()))
					{
						createApplicantAccount(portletState.getPassportPhoto(), null, 
								portletState.getIdentificationType(), 
								portletState.getIdentificationFileName(), portletState.getPvcNumber(), 
								portletState.getNextOfKinName(), portletState.getNextOfKinAddress(), 
								portletState.getNextOfKinRelationship(), portletState.getNextOfKinPhoneNumber(), 
								portletState.getAccountType(), pu, 
								null, portletState.getPvcIssueDate(), 
								aReq, aRes, portletState);
				}
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/steptwo.jsp");
				}
			}else
			{
				aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/stepthree.jsp");
			}
		}else if(act.equalsIgnoreCase("Back"))
		{
			aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/steptwo.jsp");
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
			String identificationExpiryDate, String identificationType,
			String identificationFileName, String identificationNumber,
			String nextOfKinName, String nextOfKinAddress,
			String nextOfKinRelationship, String nextOfKinPhoneNumber,
			String accountType, PortalUser pu, 
			String placeOfIssue, String issueDate, 
			ActionRequest aReq, ActionResponse aRes, 
			GuestPortletState portletState) {
		// TODO Auto-generated method stub
		String successMessage = "Sign Up request successfully created. We will review your request. On approval, you should" +
				" get an SMS and email containing instructions on the next steps to carry out";
		String failMessage = "Application request was not successfully created.";
		Applicant applicant = new Applicant();
		applicant.setApplicantNumber("APN" + RandomStringUtils.random(8, false, true));
		applicant.setApplicantType(accountType!=null ? (accountType.equals("1") ? ApplicantType.APPLICANT_TYPE_INDIVIDUAL : ApplicantType.APPLICANT_TYPE_CORPORATE) : ApplicantType.APPLICANT_TYPE_CORPORATE);
		
		
			//Test 101
			//identificationType = "NATIONAL ID";
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
					portletState.getRemoteIPAddress(), applicant.getPortalUser().getUserId());
			portletState.reinitializeForCreateCorporateIndividual(portletState);
			
			
			aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/stepfour.jsp");
			portletState.addSuccess(aReq, successMessage, portletState);
		}
		else
		{
			aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/stepthree.jsp");
			portletState.addError(aReq, successMessage, portletState);
		}
	}

	private PortalUser createPortalUser(String taxId, String lastName, String firstName,
			String otherName, String email, String countryCode, String mobile,
			String nationality, State state, String gender,
			String maritalStatus, String addressLine1, String addressLine2, String dob, RoleType roleType, 
			String residenceCity, String residenceState, String residencePhoneNumber,
			String designation, String passportPhoto, Company company, 
			GuestPortletState portletState, ActionRequest aReq, ActionResponse aRes, 
			String placeOfIssue, String identificationNumber, String identificationFileName, String identificationExpiryDate, String issueDate, String identificationType) {
		
		
		log.info("identificationType ==" + identificationType);
		log.info("33333=-=portletState.getIdentificationType() ==" + portletState.getIdentificationType());
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
			pu.setTaxIdNo(taxId);
			log.info("DOB444 = " + dob);
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
			pu.setPasscode(RandomStringUtils.random(6, true, true).toUpperCase());
			pu.setActivationCode(UUID.randomUUID().toString().replace("-",  "").toLowerCase());
			pu.setPasscodeGenerateTime(new Timestamp((new Date()).getTime()));
			pu.setStatus(UserStatus.USER_STATUS_INACTIVE);
			pu.setRoleType(roleType);
			pu.setDesignation(designation);
			pu.setCity(residenceCity == null ? "" : residenceCity);
			if(residenceState!=null)
				pu.setResidenceState(portletState.getGuestPortletUtil().getStateByName(residenceState).getId());
			pu.setResidencePhoneNumber(residencePhoneNumber);
			pu.setCompany(company);
			pu.setPassportPhoto(passportPhoto);
			
			
			
			
			
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
			return pu;
		}catch(ParseException e)
		{
			e.printStackTrace();
			return null;
		}catch(PatternSyntaxException e)
		{
			e.printStackTrace();
			portletState.addError(aReq, "Ensure the phone numbers you provided follows the specified pattern indicated", portletState);
			return null;
		}
		
		
	}
	
	
	
	public static PortalUser handleCreateUserOrbitaAccount(PortalUser user, String firstname, String middlename, String surname, String email, 
			long[] communities, AuditTrail auditTrail, ServiceContext serviceContext, SwpService sService, String loggedInUserId,
			boolean passwordReset, boolean active, boolean sendEmail, boolean sendSms, String systemUrl, 
			GuestPortletState portletState, ActionRequest aReq, ActionResponse aRes) {			
		
		Logger log = Logger.getLogger(GuestPortlet.class);
		log.info("handleCreateUserOrbitaAccount = " + portletState.getSendingEmail().getValue());
		log.info("getSendingEmailPassword = " + portletState.getSendingEmailPassword().getValue());
		log.info("getSendingEmailPort = " + portletState.getSendingEmailPort().getValue());
		log.info("getSendingEmailUsername = " + portletState.getSendingEmailUsername().getValue());

		Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
				portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
						Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
				portletState.getSendingEmailUsername().getValue());
		
		Logger log1 = Logger.getLogger(GuestPortlet.class);
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
								
								

								createdUser.setUserId(Long.toString(newlyCreatedUser.getUserId()));
								createdUser = (PortalUser)sService.createNewRecord(createdUser);
								auditTrail.setActivity("Create Portal User " + createdUser.getId());
								//sService.createNewRecord(auditTrail);
								new Util().pushAuditTrail(sService, createdUser.getId().toString(), ECIMSConstants.USER_SIGNUP, 
										portletState.getRemoteIPAddress(), createdUser.getUserId());
								
								Collection<PortalUser> puL = portletState.getGuestPortletUtil().getSignUpApprovers();
								if(sendEmail){
									//sendMail(firstname, surname, email);
									
									
									emailer.emailNewAccountRequest
										(emailAddress, 
											"", password1, portletState.getSystemUrl().getValue(), 
											createdUser.getFirstName(), createdUser.getSurname(), createdUser.getRoleType().getName().getValue(), 
											"New " + portletState.getApplicationName().getValue() + " ECIMS Signup Request", 
											portletState.getApplicationName().getValue());
									
									
									
									if(puL!=null && puL.size()>0)
									{
										for(Iterator<PortalUser> puIt = puL.iterator(); puIt.hasNext();)
										{
											PortalUser pu = puIt.next();
											emailer.emailNSANewSignupRequest
												(pu.getEmailAddress(), 
												portletState.getSystemUrl().getValue(), 
												createdUser.getFirstName(), 
												createdUser.getSurname(), 
												RoleTypeConstants.ROLE_NSA_USER.getValue(),
												"New ECIMS Signup Request Awaiting Your Action", 
												portletState.getApplicationName().getValue());
										}
										
									}
									
								}
								if(sendSms){
									String message = "ECIMS SignUp request created successfully. Please await approval from ECIMS Administrator";
									try{
//											
											new SendSms(createdUser.getPhoneNumber(), message, 
													portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
											
											if(puL!=null && puL.size()>0)
											{
												message = "Approval request awaiting your action. " +
														"Visit " + portletState.getSystemUrl().getValue() + " to view requests awaiting your " +
														"approval/disapproval action";
												for(Iterator<PortalUser> puIt = puL.iterator(); puIt.hasNext();)
												{
													PortalUser pu = puIt.next();
													new SendSms(pu.getPhoneNumber(), message, 
															portletState.getMobileApplicationName().getValue(), portletState.getProxyHost().getValue(), portletState.getProxyPort().getValue());
												}
												
											}
									}catch(Exception e){
										log1.error("error sending sms ",e);
									}
								}
//								portletState.addSuccess(aReq, "The online " + portletState.getApplicationName().getValue() + " web account for <em>" + emailAddress + "</em> has been " +
//											"successfully created on " + systemUrl + ". <br>Your login details are: <br><strong>Email Address:</strong> " +  
//											emailAddress + "<br><strong>Password:</strong> " + password1, portletState);
								System.out.println("Added succcessful");

								

								System.out.println("Setting Orbita StaffId");
								
							} catch (Exception e) { 
								log1.error("", e);
								portletState.addError(aReq, "The online " + portletState.getApplicationName().getValue() + " web Account could not be created" +
									" on " + systemUrl + ". Please try again", portletState); 
								return null;
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
							return null;
						}catch(ContactBirthdayException e)
						{
							log1.error("", e);
							portletState.addError(aReq, "Your Online Account could not be created. Ensure you provide a valid Date of Birth. Format for date of birth is YYYY-MM-DD" +
									" on " + systemUrl + ". Please try again", portletState);
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
			Logger logger = Logger.getLogger(GuestPortlet.class);
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
			GuestPortletState portletState) {
		// TODO Auto-generated method stub

		UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(aReq);
		
		
		String remoteAddr = portletState.getRemoteIPAddress();
        ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
        reCaptcha.setPrivateKey("6LfMX_4SAAAAAP71kP42bqQV9AInMII_2B3xbDy-");

        String challenge = uploadRequest.getParameter("recaptcha_challenge_field");
        log.info("Challenge == " + challenge);
        String uresponse = uploadRequest.getParameter("recaptcha_response_field");
        log.info("uresponse == " + uresponse);
        
        if(challenge!=null && uresponse!=null)
        {
	        ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);
			
			String inputLine="";
			
			String act = uploadRequest.getParameter("act");
			log.info("act =" + act);
			if(act.equalsIgnoreCase("proceed"))
			{
	//			try {
	//				String g_recaptcha_response = uploadRequest.getParameter("recaptcha_response_field");
	//				URL url = new URL("https://www.google.com/recaptcha/api/siteverify?secret=6LfMX_4SAAAAAP71kP42bqQV9AInMII_2B3xbDy-&response=" + g_recaptcha_response);
	//				System.out.println("https://www.google.com/recaptcha/api/siteverify?secret=6LfMX_4SAAAAAP71kP42bqQV9AInMII_2B3xbDy-&response=" + g_recaptcha_response);
	//				URLConnection con = url.openConnection();
	//				BufferedReader in = new BufferedReader(
	//	                    new InputStreamReader(
	//	                    		con.getInputStream()));
	//				
	//				
	//				while ((inputLine = in.readLine()) != null) 
	//				System.out.println(inputLine);
	//				in.close();
	//				System.out.println("inputLine ==" + inputLine);
	//				in.close();
	//			} catch (ProtocolException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			} catch (MalformedURLException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			} catch (IOException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
				
				try
				{
				
	//				JSONObject jsonObject = new JSONObject(inputLine);
					JSONObject jsonObject = new JSONObject();
	  				jsonObject.put("success", true);
					//if(jsonObject.has("success") && jsonObject.getBoolean("success")==true)
	  				if (reCaptchaResponse.isValid()==true)
					{
						String folder = ECIMSConstants.NEW_APPLICANT_DIRECTORY;
						log.info("Folder===" + folder);
						String realPath = getPortletContext().getRealPath("/");
						log.info("folder=" + folder + " && realPath=" + realPath);
						String identificationMeans = uploadRequest.getParameter("identificationMeans");
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
							aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/steptwo.jsp");
						}
						else
						{
			    			System.out.println("Size for passportPhoto: "+uploadRequest.getSize("passportPhoto"));
			    			System.out.println("Size for "+imageName+": "+uploadRequest.getSize(imageName));
			    			if (checkImageSize(uploadRequest, "passportPhoto")==false || uploadRequest.getSize("passportPhoto")==0 || uploadRequest.getSize(imageName)==0 || 
			    					uploadRequest.getSize("passportPhoto")>(3*1024*1024) || uploadRequest.getSize(imageName)>(3*1024*1024)) {
			    				portletState.addError(aReq, "Ensure you select your passport photo and photo showing your means of identification", portletState);
			    				aRes.setRenderParameter("jspPage", "/html/guestportlet/register_individual/steptwo.jsp");
			    			}else
			    			{
			    				String lastname = uploadRequest.getParameter("lastname");
			    				log.info("lastname==" + lastname);
			    				String firstname = uploadRequest.getParameter("firstname");
			    				log.info("firstname==" + firstname);
			    				String othername = uploadRequest.getParameter("othername");
			    				log.info("othername==" + othername);
			    				String contactEmailAddress = uploadRequest.getParameter("contactEmailAddress");
			    				String countryCode = "+234";
			    				String mobile = uploadRequest.getParameter("mobile");
			    				String nationality = uploadRequest.getParameter("nationality");
			    				//String state = uploadRequest.getParameter("state");
			    				String state = uploadRequest.getParameter("residenceState");
			    				String dob = uploadRequest.getParameter("dob");
			    				String gender = uploadRequest.getParameter("gender");
			    				String maritalStatus = uploadRequest.getParameter("maritalStatus");
			    				String contactAddressLine1 = uploadRequest.getParameter("contactAddressLine1");
			    				String contactAddressLine2 = uploadRequest.getParameter("contactAddressLine2");
			    				String taxIdNumber = uploadRequest.getParameter("taxIdNumber");
			    				
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
			//    				String sourceFileName = uploadRequest.getFileName("passportPhoto");
			//    				File file = uploadRequest.getFile("passportPhoto");
			//    				log.info("Nome file:" + uploadRequest.getFileName("passportPhoto"));
			//    				File newFile = null;
			//    				String newFileName = new SimpleDateFormat("yyyyMMddHHmmssS").format(new Date());
			//    				newFile = new File(folder + newFileName);
			//    				log.info("New file name: " + newFile.getName());
			//    				log.info("New file path: " + newFile.getPath());
			//    				InputStream in;
			//    				try {
			//    					in = new BufferedInputStream(uploadRequest.getFileAsStream("passportPhoto"));
			//    					FileInputStream fis = new FileInputStream(file);
			//    					FileOutputStream fos = new FileOutputStream(newFile);
			//    					byte[] bytes_ = FileUtil.getBytes(in);
			//    					int i = fis.read(bytes_);
			//    					while (i != -1) {
			//    						fos.write(bytes_, 0, i);
			//    						i = fis.read(bytes_);
			//    					}
			//    					fis.close();
			//    					fos.close();
			//    					
			//    					Float size = (float) newFile.length();
			//    					System.out.println("file size bytes:" + size);
			//    					System.out.println("file size Mb:" + size / 1048576);
			    				
			    				
			    				String nextOfKinFullName = uploadRequest.getParameter("nextOfKinFullName");
			    				String nextOfKinAddress = uploadRequest.getParameter("nextOfKinAddress");
			    				String nextOfKinRelationship = uploadRequest.getParameter("nextOfKinRelationship");
			    				String nextOfKinPhoneNumber = uploadRequest.getParameter("nextOfKinPhoneNumber");
			    				String residencePhoneNumber = uploadRequest.getParameter("residencePhoneNumber");
			    				String residenceState = uploadRequest.getParameter("residenceState");
			    				String residenceCity = uploadRequest.getParameter("residenceCity");
			    				String confirmContactEmailAddress = uploadRequest.getParameter("confirmContactEmailAddress");
			    				String confirmContactPhoneNumber = uploadRequest.getParameter("confirmContactPhoneNumber");
			    				
			    				
			    				log.info("Proceed to next clicked");
			    				portletState.setLastName(lastname);
			    				portletState.setFirstName(firstname);
			    				portletState.setOtherName(othername);
			    				portletState.setEmail(contactEmailAddress);
			    				portletState.setConfirmContactEmailAddress(confirmContactEmailAddress);
			    				portletState.setCountryCode(countryCode);
			    				portletState.setMobile(mobile);
			    				portletState.setNationality(nationality);
			    				portletState.setState(state);
			    				portletState.setGender(gender);
			    				portletState.setMaritalStatus(maritalStatus);
			    				portletState.setAddressLine1(contactAddressLine1);
			    				portletState.setAddressLine2(contactAddressLine2);
			    				portletState.setIdentificationType(identificationMeans);
			    				portletState.setNextOfKinName(nextOfKinFullName);
			    				portletState.setNextOfKinAddress(nextOfKinAddress);
			    				portletState.setNextOfKinRelationship(nextOfKinRelationship);
			    				portletState.setNextOfKinPhoneNumber(nextOfKinPhoneNumber);
			    				portletState.setDob(dob);
			    				portletState.setResidenceCity(residenceCity);
			    				portletState.setResidenceState(residenceState);
			    				portletState.setResidencePhoneNumber(residencePhoneNumber);
			    				portletState.setTaxIdNumber(taxIdNumber);
			    				log.info("Tax Id Number: " + portletState.getTaxIdNumber());
			    				
			//					if (reCaptchaResponse.isValid() || reCaptchaResponse.isValid()==false) 
			//					{
			    				ImageUpload passportImageFile = uploadImage(uploadRequest, "passportPhoto", folder);
			    				ImageUpload identityPhoto = null;
			    				log.info("portletState.getIdentificationType() ==" + portletState.getIdentificationType());
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
				    				if(passportImageFile!=null && (passportImageFile.isUploadValid()) && identityPhoto!=null && (identityPhoto.isUploadValid()))
				    				{
			
			    						String passportPhoto = passportImageFile.getNewFileName();
				    					
			    						if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_NATIONAL_ID.getValue()))
			    	    				{
			    							if(validateApplicantProfileNoExpiryDateNoPlaceOfIssuance(aReq, false, lastname, firstname, othername, contactEmailAddress, 
					    							countryCode, mobile, nationality, state, gender, maritalStatus, contactAddressLine1, 
					    							contactAddressLine2, passportPhoto, identificationMeans, null, 
					    							nationalIdNumber, nextOfKinFullName, nextOfKinAddress, 
					    							nextOfKinRelationship, nextOfKinPhoneNumber, passportPhoto, dob, 
					    							natlissueDate, residenceCity, residenceState, residencePhoneNumber, 
					    							portletState, confirmContactEmailAddress, confirmContactPhoneNumber)==true)
					    					{
					    						log.info("Validate True");
						    					portletState.setPassportPhoto(passportPhoto);
					    						
					    						aRes.setRenderParameter("jspPage", "/html/guestportlet/register_individual/stepthree.jsp");
					    					}else
					    					{
					    						log.info("Validate False");
					    						aRes.setRenderParameter("jspPage", "/html/guestportlet/register_individual/steptwo.jsp");
					    					}
			    	    				}else if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_PVC.getValue()))
			    	    				{
			    							if(validateApplicantProfileNoExpiryDateNoPlaceOfIssuance(aReq, false, lastname, firstname, othername, contactEmailAddress, 
					    							countryCode, mobile, nationality, state, gender, maritalStatus, contactAddressLine1, 
					    							contactAddressLine2, passportPhoto, identificationMeans, null, 
					    							pvcNumber, nextOfKinFullName, nextOfKinAddress, 
					    							nextOfKinRelationship, nextOfKinPhoneNumber, passportPhoto, dob, 
					    							pvcissueDate, residenceCity, residenceState, residencePhoneNumber, 
					    							portletState, confirmContactEmailAddress, confirmContactPhoneNumber)==true)
					    					{
					    						log.info("Validate True");
						    					portletState.setPassportPhoto(passportPhoto);
					    						
					    						aRes.setRenderParameter("jspPage", "/html/guestportlet/register_individual/stepthree.jsp");
					    					}else
					    					{
					    						log.info("Validate False");
					    						aRes.setRenderParameter("jspPage", "/html/guestportlet/register_individual/steptwo.jsp");
					    					}
			    	    				}else if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_DRIVERS_LICENSE.getValue()))
			    	    				{
			    	    					if(validateApplicantProfile(aReq, false, lastname, firstname, othername, contactEmailAddress, 
					    							countryCode, mobile, nationality, state, gender, maritalStatus, contactAddressLine1, 
					    							contactAddressLine2, passportPhoto, identificationMeans, null, 
					    							driversIdNumber, driversExpiryDate, nextOfKinFullName, nextOfKinAddress, 
					    							nextOfKinRelationship, nextOfKinPhoneNumber, passportPhoto, dob, driversplaceOfIssue, 
					    							driversissuancedate, residenceCity, residenceState, residencePhoneNumber, portletState, 
					    							confirmContactEmailAddress, confirmContactPhoneNumber)==true)
					    					{
					    						log.info("Validate True");
						    					portletState.setPassportPhoto(passportPhoto);
					    						
					    						aRes.setRenderParameter("jspPage", "/html/guestportlet/register_individual/stepthree.jsp");
					    					}else
					    					{
					    						log.info("Validate False");
					    						aRes.setRenderParameter("jspPage", "/html/guestportlet/register_individual/steptwo.jsp");
					    					}
			    	    				}else if(portletState.getIdentificationType().equals(IdentificationType.IDENTIFICATION_TYPE_INTL_PASSPORT.getValue()))
			    	    				{
			    	    					if(validateApplicantProfile(aReq, false, lastname, firstname, othername, contactEmailAddress, 
					    							countryCode, mobile, nationality, state, gender, maritalStatus, contactAddressLine1, 
					    							contactAddressLine2, passportPhoto, identificationMeans, null, 
					    							intlpassportIdNumber, intlpassportExpiryDate, nextOfKinFullName, nextOfKinAddress, 
					    							nextOfKinRelationship, nextOfKinPhoneNumber, passportPhoto, dob, intlplaceOfIssue, 
					    							intlissuancedate, residenceCity, residenceState, residencePhoneNumber, portletState, 
					    							confirmContactEmailAddress, confirmContactPhoneNumber)==true)
					    					{
					    						log.info("Validate True");
						    					portletState.setPassportPhoto(passportPhoto);
					    						
					    						aRes.setRenderParameter("jspPage", "/html/guestportlet/register_individual/stepthree.jsp");
					    					}else
					    					{
					    						log.info("Validate False");
					    						aRes.setRenderParameter("jspPage", "/html/guestportlet/register_individual/steptwo.jsp");
					    					}
			    	    				}
			    						
			    						
				    					
				    				}
				    				else
				    				{
				    					if(passportImageFile==null)
				    					{
				    						aRes.setRenderParameter("jspPage", "/html/guestportlet/register_individual/steptwo.jsp");
				    						portletState.addError(aReq, "Uploading passport failed. Ensure you selected a valid file", portletState);
				    					}else if(identityPhoto==null)
				    					{
				    						aRes.setRenderParameter("jspPage", "/html/guestportlet/register_individual/steptwo.jsp");
				    						portletState.addError(aReq, "Uploading means of identification photograph failed. Ensure you selected a valid file", portletState);
				    					}
				    				}
			//					}else
			//			        {
			//			        	portletState.addError(aReq, "The entry you made for the captcha code is incorrect. Do try again", portletState);
			//			        	aRes.setRenderParameter("jspPage", "/html/guestportlet/register_individual/steptwo.jsp");
			//			        }
			    			}
						}
					}else
					{
						aRes.setRenderParameter("jspPage", "/html/guestportlet/register_individual/steptwo.jsp");
						portletState.addError(aReq, "You provided an incorrect captcha data. Please provide a valid data to provide", portletState);
					}
					
				}catch(JSONException e)
				{
					aRes.setRenderParameter("jspPage", "/html/guestportlet/register_individual/steptwo.jsp");
					portletState.addError(aReq, "You provided an incorrect captcha data. Please provide a valid data to provide", portletState);
				}
			}else if(act.equalsIgnoreCase("back"))
			{
				log.info("Go Back clicked");
				aRes.setRenderParameter("jspPage", "/html/guestportlet/stepone.jsp");
			}
        }else
        {
        	aRes.setRenderParameter("jspPage", "/html/guestportlet/register_individual/steptwo.jsp");
			portletState.addError(aReq, "You provided an incorrect captcha data. Please provide a valid data to provide", portletState);
        }
	
            
//        }catch(NullPointerException e)
//        {
//        	portletState.addError(aReq, "You did not provide the captcha data required on this form. If you can not see a captcha on the form, please refresh your page until you can see one.", portletState);
//        }
        
        	
        	
			
		
		
		
		
		
	}
	
	
	private boolean checkImageSize(UploadPortletRequest uploadRequest,
			String string) {
		// TODO Auto-generated method stub
//		File file = uploadRequest.getFile(string);
//		try {
//			byte[] bytes = FileUtil.getBytes(file);
//			BufferedImage bufferedImage=null;
//	        try {
//	            InputStream inputStream = new ByteArrayInputStream(bytes);
//	            bufferedImage = ImageIO.read(inputStream);
//	            int width = bufferedImage.getWidth();
//	            int height = bufferedImage.getHeight();
//	        } catch (IOException ex) {
//	            System.out.println(ex.getMessage());
//	        }
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return true;
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

	
	private void createCorporateApplicantStepTwo(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			GuestPortletState portletState) {
		// TODO Auto-generated method stub
		
		
		
		
		
			UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(aReq);
			
			String remoteAddr = portletState.getRemoteIPAddress();
	        ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
	        reCaptcha.setPrivateKey("6LfMX_4SAAAAAP71kP42bqQV9AInMII_2B3xbDy-");
	
	        String challenge = uploadRequest.getParameter("recaptcha_challenge_field");
	        log.info("Challenge == " + challenge);
	        String uresponse = uploadRequest.getParameter("recaptcha_response_field");
	        log.info("uresponse == " + uresponse);
	   if(challenge!=null && uresponse!=null)
	   {
	        
	        ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);
	
	        
	        	//out.print("Answer was entered correctly!");
	      	String act = uploadRequest.getParameter("act");
	  		log.info("act =" + act);
	  		String inputLine = "";
	  		if(act.equalsIgnoreCase("proceed"))
	  		{
	  			if (reCaptchaResponse.isValid()) 
	  			{
	//	  			try {
	//					String g_recaptcha_response = uploadRequest.getParameter("recaptcha_response_field");
	//					URL url = new URL("https://www.google.com/recaptcha/api/siteverify?secret=6LfMX_4SAAAAAP71kP42bqQV9AInMII_2B3xbDy-&response=" + g_recaptcha_response);
	//					System.out.println("https://www.google.com/recaptcha/api/siteverify?secret=6LfMX_4SAAAAAP71kP42bqQV9AInMII_2B3xbDy-&response=" + g_recaptcha_response);
	//					URLConnection con = url.openConnection();
	//					BufferedReader in = new BufferedReader(
	//		                    new InputStreamReader(
	//		                    		con.getInputStream()));
	//					
	//					
	//					while ((inputLine = in.readLine()) != null) 
	//					System.out.println(inputLine);
	//					in.close();
	//					System.out.println("inputLine ==" + inputLine);
	//					in.close();
	//				} catch (ProtocolException e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				} catch (MalformedURLException e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				} catch (IOException e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				}
		  			
		  			try
		  			{
		  			
	//	  				JSONObject jsonObject = new JSONObject(inputLine);
		  				JSONObject jsonObject = new JSONObject();
		  				jsonObject.put("success", true);
		  				
		  				if(jsonObject.has("success") && jsonObject.getBoolean("success")==true)
		  				{
							String folder = ECIMSConstants.NEW_APPLICANT_DIRECTORY;
							log.info("Folder===" + folder);
							String realPath = getPortletContext().getRealPath("/");
							log.info("folder=" + folder + " && realPath=" + realPath);
							String identificationMeans = uploadRequest.getParameter("identificationMeans");
							if(identificationMeans!=null && !identificationMeans.equals(""))
							{
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
								} else if(identificationMeans!=null && identificationMeans.equals(IdentificationType.IDENTIFICATION_TYPE_PVC.getValue()))
								{
									 imageName = "pvcPhoto";
								} 
							
							
							
					  			String lastname = uploadRequest.getParameter("lastname");
				  				log.info("lastname==" + lastname);
				  				String firstname = uploadRequest.getParameter("firstname");
				  				log.info("firstname==" + firstname);
				  				String othername = uploadRequest.getParameter("othername");
				  				log.info("othername==" + othername);
				  				String contactEmailAddress = uploadRequest.getParameter("contactEmailAddress");
				  				//String passportPhoto = uploadRequest.getParameter("passportPhoto");
				  				String dob = uploadRequest.getParameter("dob");
				  				log.info("DOB = " + dob);
				  				String nationality = uploadRequest.getParameter("nationality");
				  				String state = uploadRequest.getParameter("state");
				  				String gender = uploadRequest.getParameter("gender");
				  				String designation = uploadRequest.getParameter("designation");
				  				log.info("Designation ---" + designation);
				  				String countryCode = "+234";
				  				String mobile = uploadRequest.getParameter("mobile");
				  				String companyName = uploadRequest.getParameter("companyName");
				  				String companyAddress =  uploadRequest.getParameter("companyAddress");
				  				String companyState =  uploadRequest.getParameter("companyState");
				  				String companyPhoneNumber =  uploadRequest.getParameter("companyPhoneNumber");
				  				String companyEmailAddress =  uploadRequest.getParameter("companyEmailAddress");
				  				String websiteUrl =  uploadRequest.getParameter("websiteUrl");
				  				String cacNo = uploadRequest.getParameter("cacNo");
				  				String dateOfIncorporation = uploadRequest.getParameter("dateOfIncorporation");
				  				String confirmContactEmailAddress = uploadRequest.getParameter("confirmContactEmailAddress");
								String confirmContactPhoneNumber = uploadRequest.getParameter("confirmContactPhoneNumber");
				//  				String companyLogo = uploadRequest.getParameter("companyLogo");
				//  				String cacCertificate = uploadRequest.getParameter("cacCertificate");
								String taxIdNumber = uploadRequest.getParameter("taxIdNumber");
				  				
								portletState.setTaxIdNumber(taxIdNumber);
				  				portletState.setLastName(lastname);
				  				portletState.setFirstName(firstname);
				  				portletState.setOtherName(othername);
				  				portletState.setEmail(contactEmailAddress);
				  				portletState.setDob(dob);
				  				log.info("DOB = " + portletState.getDob());
				  				portletState.setNationality(nationality);
				  				portletState.setState(state);
				  				portletState.setGender(gender);
				  				portletState.setDesignation(designation);
				  				portletState.setCountryCode(countryCode);
				  				portletState.setMobile(mobile);
				  				portletState.setCompanyName(companyName);
								portletState.setIdentificationType(identificationMeans);
				  				portletState.setCompanyAddress(companyAddress);
				  				portletState.setCompanyState(companyState);
				  				portletState.setCompanyPhoneNumber(companyPhoneNumber);
				  				portletState.setCompanyEmailAddress(companyEmailAddress);
				  				portletState.setWebsiteUrl(websiteUrl);
				  				portletState.setRegistrationNumber(cacNo);
				  				portletState.setDateOfIncorporation(dateOfIncorporation);
			    				log.info("Tax Id Number: " + portletState.getTaxIdNumber());
				  				
				  				
				  				String nationalIdNumber = null;
								String natlissueDate = null;
								String pvcNumber = null;
								String pvcIssueDate = null;
								String intlpassportExpiryDate = null;
								String intlpassportIdNumber = null;
								String intlissuancedate = null;
								String intlplaceOfIssue = null;
								String driversExpiryDate = null;
								String driversIdNumber = null;
								String driversissuancedate = null;
								String driversplaceOfIssue = null;
								 
								boolean rat = false;
								String ratMsg = null;
								if(identificationMeans!=null && identificationMeans.equals(IdentificationType.IDENTIFICATION_TYPE_NATIONAL_ID.getValue()))
								{
									 nationalIdNumber = uploadRequest.getParameter("nationalIdNumber");
									 natlissueDate = uploadRequest.getParameter("natlissueDate");
									 portletState.setNationalIdNumber(nationalIdNumber);
									 portletState.setNatlissueDate(natlissueDate);
									 if(nationalIdNumber!=null && natlissueDate!=null && 
											 nationalIdNumber.length()>0 && natlissueDate.length()>0)
									 {
										 rat = true;
										 log.info("test112: ");
									 }
									 else
									 {
										 ratMsg = "Ensure you provide valid details for the national id card. " +
										 		"These include id number, issuance date";
									 }
									 
									 log.info("test1: ");
									 
								}else if(identificationMeans!=null && identificationMeans.equals(IdentificationType.IDENTIFICATION_TYPE_PVC.getValue()))
								{
									 pvcNumber = uploadRequest.getParameter("pvcNumber");
									 pvcIssueDate = uploadRequest.getParameter("pvcissueDate");
									 portletState.setPvcNumber(pvcNumber);
									 portletState.setPvcIssueDate(pvcIssueDate);
									 if(pvcNumber!=null && pvcIssueDate!=null && pvcNumber.length()>0 && pvcIssueDate.length()>0)
									 {
										 rat = true;
										 log.info("test212: ");
									 }
									 else 
									 {
										 ratMsg = "Ensure you provide valid details for the PVC. " +
										 		"These include PVC number, PVC Issue date";
									 }
									 
									 log.info("test2: ");
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
									 
									 if(intlpassportExpiryDate!=null && intlpassportIdNumber!=null && intlissuancedate!=null && intlplaceOfIssue!=null && 
											 intlpassportExpiryDate.length()>0 && intlpassportIdNumber.length()>0 && intlissuancedate.length()>0 && intlplaceOfIssue.length()>0)
									 {
									 	rat = true;
									 	log.info("test312: ");
									 }
									 else
									 {
										 ratMsg = "Ensure you provide valid details for the international license. These include Expiry date, id number, issuance date, and place of issue";
									 }
									 
									 log.info("test3: ");
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
									 
									 if(driversExpiryDate!=null && driversIdNumber!=null && driversissuancedate!=null && driversplaceOfIssue!=null && 
											 driversExpiryDate.length()>0 && driversIdNumber.length()>0 && driversissuancedate.length()>0 && driversplaceOfIssue.length()>0)
									 {
										 	rat = true;
										 	log.info("test412: ");
									 }
									 else
									 {
										 ratMsg = "Ensure you provide valid details for the drivers license. These include Expiry date, id number, issuance date, and place of issue";
									 }
									 
									 log.info("test4: ");
								} 
								
								log.info("ratMsg: " + (ratMsg==null?"":ratMsg));
								
								if(rat==false)
								{
									aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/steptwo.jsp");
									portletState.addError(aReq, ratMsg ==null ? "Provide valid data for the means of identification" : ratMsg, portletState);
								}
								else if(rat==true)
								{
									ImageUpload identityPhoto = null;
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
					    					String intlPassportPhoto = identityPhoto.getNewFileName();
					    					portletState.setIdentificationFileName(intlPassportPhoto);
										}
									}
					  				
						  			if(imageName==null || (imageName!=null && imageName.equals("")))
						  			{
						  				portletState.addError(aReq, "Select a means of identification before you can proceed", portletState);
						  				aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/steptwo.jsp");
						  			}else
						  			{
							  			if (uploadRequest.getSize("passportPhoto")==0 || uploadRequest.getSize("cacCertificate")==0 
							  					|| uploadRequest.getSize(imageName)==0 || 
							  					uploadRequest.getSize("passportPhoto")>(3*1024*1024) || uploadRequest.getSize("cacCertificate")>(3*1024*1024) 
							  					|| uploadRequest.getSize(imageName)>(3*1024*1024)) {
							  				portletState.addError(aReq, "Ensure you select your passport photo, means of identification photo and CAC Certificate. Files must also not exceed 3MB", portletState);
							  				aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/steptwo.jsp");
							  			}else
							  			{
							  				if(validateCorporateApplicantProfile(aReq, false, lastname, firstname, contactEmailAddress, 
							  						dob, nationality, state, gender, designation, 
							  						countryCode, mobile, companyName, companyAddress, companyState, companyPhoneNumber, 
							  						cacNo, dateOfIncorporation, portletState, confirmContactEmailAddress, confirmContactPhoneNumber)==true)
							  				{
							  					ImageUpload passportPhotoImg = uploadImage(uploadRequest, "passportPhoto", folder);
							  					ImageUpload caccertificateImg = uploadImage(uploadRequest, "cacCertificate", folder);
							  					ImageUpload companyLogo = uploadImage(uploadRequest, "companyLogo", folder);
							  					if(passportPhotoImg!=null && (passportPhotoImg.isUploadValid()) && 
							  							caccertificateImg!=null && (caccertificateImg.isUploadValid()) && 
							  									identityPhoto!=null && (identityPhoto.isUploadValid()))
							  					{
							  						portletState.setCompanyLogo(companyLogo==null ? null : companyLogo.getNewFileName());
							  						portletState.setCacCertificate(caccertificateImg==null ? null : caccertificateImg.getNewFileName());
							  						portletState.setPassportPhoto(passportPhotoImg==null ? null : passportPhotoImg.getNewFileName());
							  						portletState.setIdentificationFileName(identityPhoto==null ? null : identityPhoto.getNewFileName());
							  						log.info("Validate True");
							  						aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/stepthree.jsp");
							  						
							  					}else {
							  						if (passportPhotoImg==null)
							  						{
							  							aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/steptwo.jsp");
							  							portletState.addError(aReq, "Uploading passport failed. Ensure you selected a valid file", portletState);
							  						}
							  						else if (caccertificateImg==null)
							  						{
							  							aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/steptwo.jsp");
							  							portletState.addError(aReq, "Uploading CAC Certificate failed. Ensure you selected a valid file", portletState);
							  						}else if (identityPhoto==null)
							  						{
							  							aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/steptwo.jsp");
							  							portletState.addError(aReq, "Uploading Means of Identification photo was not successful. Ensure you selected a valid file", portletState);
							  						}
							  					}
							  				}else
							  				{
							  					log.info("Validate False");
							  					aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/steptwo.jsp");
							  				}	
							  			}
						  			}
								}
							}else
							{
								portletState.addError(aReq, "Ensure you provide a means of identification", portletState);
							}
		  				}else
						{
							aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/steptwo.jsp");
							portletState.addError(aReq, "You provided an incorrect captcha data. Please provide a valid data to provide", portletState);
						}
					}catch(JSONException e)
					{
						e.printStackTrace();
						aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/steptwo.jsp");
						portletState.addError(aReq, "You provided an incorrect captcha data. Please provide a valid data to proceed", portletState);
					}
	  			}
	  		}else if(act.equalsIgnoreCase("back"))
	  		{
	  			log.info("Go Back clicked");
	  			aRes.setRenderParameter("jspPage", "/html/guestportlet/stepone.jsp");
	  		}
		  		log.info("act =" + act);
	//        } else {
	//        	//out.print("Answer is wrong");
	//        	portletState.addError(aReq, "The entry you made for the captcha code is incorrect. Do try again", portletState);
	//        	aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/steptwo.jsp");
	//        }
	        
	        
			
			
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/steptwo.jsp");
			portletState.addError(aReq, "You provided an incorrect captcha data. Please provide a valid data to proceed", portletState);
		}
	}
	
	
	
	private boolean validateApplicantProfileNoExpiryDateNoPlaceOfIssuance(ActionRequest aReq, boolean editTrue, String lastname,
			String firstname, String othername, String contactEmailAddress,
			String countryCode, String mobile, String nationality,
			String state, String gender, String maritalStatus,
			String contactAddressLine1, String contactAddressLine2,
			String passportPhoto, String identificationMeans,
			String nationalIdPhoto, String nationalIdNumber, String nextOfKinFullName,
			String nextOfKinAddress, String nextOfKinRelationship,
			String nextOfKinPhoneNumber, String passportPhoto2, String dob,
			String issueDate, String residenceCity,  String residenceState, 
			String residencePhoneNumber, 
			GuestPortletState portletState, String confirmContactEmailAddress, String confirmContactPhoneNumber) {
		// TODO Auto-generated method stub
		
		ArrayList<String> errorMessage = new ArrayList<String>();
		if(issueDate==null || (issueDate!=null && issueDate.trim().length()==0))
		{
			errorMessage.add("Specify the issuance date of your means of identity");
		}
		if(nationalIdNumber==null || (nationalIdNumber!=null && nationalIdNumber.trim().length()==0))
		{
			errorMessage.add("Specify the identification number of your means of identity");
		}
		if(lastname==null || (lastname!=null && lastname.trim().length()==0))
		{
			errorMessage.add("Specify your last name");
		}
		if(lastname==null || (lastname!=null && lastname.trim().length()==0))
		{
			errorMessage.add("Specify your last name");
		}
		if(dob==null || (dob!=null && dob.trim().length()==0))
		{
			errorMessage.add("Specify your date of birth");
		}
		if(identificationMeans==null || (identificationMeans!=null && identificationMeans.equals("")))
		{
			errorMessage.add("Specify your means of identification");
		}
		try{
			Date df = sdf.parse(dob);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, -18);
			Date yearsAgo18 = cal.getTime();
			if(df.after(yearsAgo18))
			{
				errorMessage.add("You must be above 18 years to register on this platform");
			}
		}
		catch(ParseException e)
		{
			e.printStackTrace();
			errorMessage.add("Invalid Date Of birth provided");
		}
		if(contactEmailAddress!=null && confirmContactEmailAddress!=null && confirmContactEmailAddress.equals(contactEmailAddress))
		{
			
		}else
		{
			errorMessage.add("Ensure you provide your email address in the email address field. Also ensure that the confirmation email address is the same with the contact email address");
		}
		if(firstname==null || (firstname!=null && firstname.trim().length()==0))
		{
			errorMessage.add("Specify your first name");
		}
		if(nationality==null || (nationality!=null && nationality.equals("-1")) || (nationality!=null && nationality.equals("")))
		{
			errorMessage.add("Specify your nationality");
		}
		if(nextOfKinPhoneNumber==null || (nextOfKinPhoneNumber!=null && nextOfKinPhoneNumber.trim().length()==0))
		{
			errorMessage.add("Specify your next of kin's phone number");
		}
		if(contactEmailAddress==null || (contactEmailAddress!=null &&  contactEmailAddress.trim().length()==0))
		{
			errorMessage.add("Specify your email address before proceeding");
		}
		if(mobile==null || (mobile!=null && mobile.trim().length()==0))
		{
			errorMessage.add("Specify your mobile number before proceeding");
		}
		if(state==null || (state!=null && state.equals("-1")) ||  (state!=null && state.equals("")))
		{
			errorMessage.add("Specify your state of origin before proceeding");
		}
		if(gender==null)
		{
			errorMessage.add("Specify your gender before proceeding");
		}
		if(maritalStatus==null || (maritalStatus!=null && maritalStatus.equals("-1"))|| (maritalStatus!=null && maritalStatus.equals("")))
		{
			errorMessage.add("Specify your marital status before proceeding");
		}
		if(contactAddressLine1==null || (contactAddressLine1!=null && contactAddressLine1.trim().length()==0))
		{
			errorMessage.add("Provide first line of address before proceeding");
		}
		PortalUser pu = null;
		if(editTrue)
		{
			if(portletState.getSelectedPortalUserId()!=null && portletState.getSelectedPortalUserId().length()>0)
			{
				pu = portletState.getGuestPortletUtil().getPortalUserByEmailAddressAndNotUserId(
					contactEmailAddress, Long.valueOf(portletState.getSelectedPortalUserId()));
				if(pu!=null)
				{
					errorMessage.add("The email address provided has already been used on this platform. Provide another email address.");
				}									
			}else
			{
				errorMessage.add("Invalid User has been selected.");
			}
		}else
		{
			pu = portletState.getGuestPortletUtil().getPortalUserByEmailAddress(contactEmailAddress);
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
			GuestPortletState portletState, String confirmContactEmailAddress, String confirmContactPhoneNumber) {
		// TODO Auto-generated method stub
		
		ArrayList<String> errorMessage = new ArrayList<String>();
		if(nationalIdNumber==null || (nationalIdNumber!=null && nationalIdNumber.trim().length()==0))
		{
			errorMessage.add("Specify identification number of your means of identification");
		}
		if(placeOfIssue==null || (placeOfIssue!=null && placeOfIssue.trim().length()==0))
		{
			errorMessage.add("Specify the place of issue of your means of identification");
		}
		if(issueDate==null || (issueDate!=null && issueDate.trim().length()==0))
		{
			errorMessage.add("Specify date of issue of your means of identification");
		}
		if(lastname==null || (lastname!=null && lastname.trim().length()==0))
		{
			errorMessage.add("Specify your last name");
		}
		if(firstname==null || (firstname!=null && firstname.trim().length()==0))
		{
			errorMessage.add("Specify your first name");
		}
		if(dob==null || (dob!=null && dob.trim().length()==0))
		{
			errorMessage.add("Specify your date of birth");
		}
		
		
		try{
			Date df = sdf.parse(dob);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, -18);
			Date yearsAgo18 = cal.getTime();
			if(df.after(yearsAgo18))
			{
				errorMessage.add("You must be above 18 years to register on this platform");
			}
		}
		catch(ParseException e)
		{
			e.printStackTrace();
			errorMessage.add("Invalid Date Of bith provided.");
		}
		if(contactEmailAddress!=null && confirmContactEmailAddress!=null && confirmContactEmailAddress.equals(contactEmailAddress))
		{
			
		}else
		{
			errorMessage.add("Ensure you provide your email address in the email address field. Also ensure that the confirmation email address is the same with the contact email address");
		}
		if(confirmContactPhoneNumber!=null && mobile!=null && confirmContactPhoneNumber.equals(mobile))
		{
			
		}else
		{
			errorMessage.add("Ensure you provide your mobile number in the mobile number field. Also ensure that the confirmation mobile number is the same with the mobile number you provided");
		}
		if(firstname==null || (firstname!=null && firstname.trim().length()==0))
		{
			errorMessage.add("Specify your first name");
		}
		if(nationality==null || (nationality!=null && nationality.equals("-1")) || (nationality!=null && nationality.equals("")))
		{
			errorMessage.add("Specify your nationality");
		}
		if(nextOfKinPhoneNumber==null || (nextOfKinPhoneNumber!=null && nextOfKinPhoneNumber.trim().length()==0))
		{
			errorMessage.add("Specify your next of kin's phone number");
		}
		if(contactEmailAddress==null || (contactEmailAddress!=null &&  contactEmailAddress.trim().length()==0))
		{
			errorMessage.add("Specify your email address before proceeding");
		}
		if(mobile==null || (mobile!=null && mobile.trim().length()==0))
		{
			errorMessage.add("Specify your mobile number before proceeding");
		}
		if(state==null || (state!=null && state.equals("-1")) ||  (state!=null && state.equals("")))
		{
			errorMessage.add("Specify your state of origin before proceeding");
		}
		if(gender==null)
		{
			errorMessage.add("Specify your gender before proceeding");
		}
		if(maritalStatus==null || (maritalStatus!=null && maritalStatus.equals("-1"))|| (maritalStatus!=null && maritalStatus.equals("")))
		{
			errorMessage.add("Specify your marital status before proceeding");
		}
		if(contactAddressLine1==null || (contactAddressLine1!=null && contactAddressLine1.trim().length()==0))
		{
			errorMessage.add("Provide first line of address before proceeding");
		}
		PortalUser pu = null;
		if(editTrue)
		{
			if(portletState.getSelectedPortalUserId()!=null && portletState.getSelectedPortalUserId().length()>0)
			{
				pu = portletState.getGuestPortletUtil().getPortalUserByEmailAddressAndNotUserId(
					contactEmailAddress, Long.valueOf(portletState.getSelectedPortalUserId()));
				if(pu!=null)
				{
					errorMessage.add("The email address provided has already been used on this platform. Provide another email address.");
				}									
			}else
			{
				errorMessage.add("Invalid User has been selected.");
			}
		}else
		{
			pu = portletState.getGuestPortletUtil().getPortalUserByEmailAddress(contactEmailAddress);
		}
		
		
		if(identificationExpiryDate!=null)
		{
		
			try{
				Date expDate = sdf.parse(identificationExpiryDate);
				Calendar cal = Calendar.getInstance();
				Date today = cal.getTime();
				if(expDate.before(today))
				{
					errorMessage.add("Expiry date for means of Identification must be beyond today");
				}
			}
			catch(ParseException e)
			{
				e.printStackTrace();
				errorMessage.add("You provided an invalid expiry date for the means of identification you specified. Please make use of the calendar icon to select a valid date.");
			}
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

	
	
	private boolean validateCorporateApplicantProfile(ActionRequest aReq, boolean editTrue, String lastname,
			String firstname, String contactEmailAddress,
			String dob, String nationality,
			String state, String gender, String designation, 
			String countryCode, String mobile, String companyName, 
			String companyAddress, String companyState, String companyPhoneNumber, 
			String cacno, String dateOfIncorporation, GuestPortletState portletState, 
			String confirmContactEmailAddress, String confirmContactPhoneNumber) {
		// TODO Auto-generated method stub
		ArrayList<String> errorMessage = new ArrayList<String>();
		if(lastname==null || (lastname!=null && lastname.trim().length()==0))
		{
			errorMessage.add("Specify your last name");
		}
		if(firstname==null || (firstname!=null && firstname.trim().length()==0))
		{
			errorMessage.add("Specify your first name");
		}
		if(nationality==null || (nationality!=null && nationality.equals("-1")) || (nationality!=null && nationality.equals("")))
		{
			errorMessage.add("Specify your nationality");
		}
		try
		{
			Date dat = sdf.parse(dob);
		}catch(ParseException e)
		{
			e.printStackTrace();
			errorMessage.add("Invalid date of birth specified. Use the calendar icon to select a date.");
		}
		
		if(contactEmailAddress==null || (contactEmailAddress!=null && contactEmailAddress.trim().length()==0))
		{
			errorMessage.add("Specify your email address before proceeding");
		}
		if(confirmContactEmailAddress!=null && contactEmailAddress!=null && contactEmailAddress.equals(confirmContactEmailAddress))
		{
			
		}else
		{
			errorMessage.add("Ensure your contact email address confirmation email address matches the email address you procided in the contact email address field");
		}
		
		if(confirmContactPhoneNumber!=null && mobile!=null && confirmContactPhoneNumber.equals(mobile))
		{
			
		}else
		{
			errorMessage.add("Ensure your contact mobile number confirmation mobile number matches the mobile number you procided in the mobile number field");
		}
		if(mobile==null || (mobile!=null && mobile.trim().length()==0))
		{
			errorMessage.add("Specify your mobile number before proceeding");
		}
		if(countryCode==null || (countryCode!=null && countryCode.equals("-1")) || (countryCode!=null && countryCode.equals("")))
		{
			errorMessage.add("Specify your country's international phone code before proceeding");
		}
		if(state==null || (state!=null && state.equals("-1")) ||  (state!=null && state.equals("")))
		{
			errorMessage.add("Specify your state of origin before proceeding");
		}
		if(gender==null || (gender!=null && gender.equals("-1")) || (gender!=null && gender.equals("")))
		{
			errorMessage.add("Specify your gender before proceeding");
		}
		if(designation==null || (designation!=null && designation.length()==0))
		{
			errorMessage.add("Specify your designation before proceeding");
		}
		if(companyName==null || (companyName!=null && companyName.trim().length()==0))
		{
			errorMessage.add("Provide a company name before proceeding");
		}
		if(companyAddress==null || (companyAddress!=null && companyAddress.trim().length()==0))
		{
			errorMessage.add("Provide a contact address before proceeding");
		}
		if(companyPhoneNumber==null || (companyPhoneNumber!=null && companyPhoneNumber.trim().length()==0))
		{
			errorMessage.add("Provide a contact phone number before proceeding");
		}

		if(companyState==null || (companyState!=null && companyState.trim().length()==0))
		{
			errorMessage.add("Select state where your company is located");
		}
		
		try{
			Date df = sdf.parse(dob);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, -18);
			Date yearsAgo18 = cal.getTime();
			if(df.after(yearsAgo18))
			{
				errorMessage.add("You must be above 18 years to register on this platform");
			}
		}
		catch(ParseException e)
		{
			e.printStackTrace();
			errorMessage.add("Invalid Date Of bith procided");
		}
		
		

		try{
			Date df = sdf.parse(dateOfIncorporation);
			Calendar cal = Calendar.getInstance();
			if(df.after(cal.getTime()))
			{
				errorMessage.add("Company's Date of Incorporation must be before today's date");
			}
		}
		catch(ParseException e)
		{
			e.printStackTrace();
			errorMessage.add("Invalid Date Of Incorporation procided");
		}
				
		
		
		PortalUser pu = null;
		if(editTrue)
		{
			if(portletState.getSelectedPortalUserId()!=null && portletState.getSelectedPortalUserId().length()>0)
			{
				pu = portletState.getGuestPortletUtil().getPortalUserByEmailAddressAndNotUserId(
					contactEmailAddress, Long.valueOf(portletState.getSelectedPortalUserId()));
				if(pu!=null)
				{
					errorMessage.add("The email address provided has already been used on this platform. Provide another email address.");
				}									
			}else
			{
				errorMessage.add("Invalid User has been selected.");
			}
		}else
		{
			pu = portletState.getGuestPortletUtil().getPortalUserByEmailAddress(contactEmailAddress);
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

	private void createApplicantStepOne(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			GuestPortletState portletState) {
		// TODO Auto-generated method stub
		String applicantType = aReq.getParameter("accountType");
		log.info("Applicant Type==" + applicantType);
		if(applicantType!=null)
		{
			portletState.setAccountType(applicantType);
			if(applicantType.equals("0"))
				aRes.setRenderParameter("jspPage", "/html/guestportlet/register_company/steptwo.jsp");
			if(applicantType.equals("1"))
				aRes.setRenderParameter("jspPage", "/html/guestportlet/register_individual/steptwo.jsp");
		}
		else
		{
			portletState.addError(aReq, "Select an account type before proceeding", portletState);
			aRes.setRenderParameter("jspPage", "/html/guestportlet/stepone.jsp");
		}
	}
}
