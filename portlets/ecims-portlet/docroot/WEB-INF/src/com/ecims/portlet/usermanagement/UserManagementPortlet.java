package com.ecims.portlet.usermanagement;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
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
import smartpay.entity.Company;
import smartpay.entity.PortalUser;
import smartpay.entity.RoleType;
import smartpay.entity.State;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.entity.enumerations.UserStatus;
import smartpay.service.SwpService;

import com.ecims.commins.ECIMSConstants;
import com.ecims.commins.Mailer;
import com.ecims.commins.SendSms;
import com.ecims.commins.Util;
import com.ecims.portlet.usermanagement.UserManagementPortlet;
import com.ecims.portlet.usermanagement.UserManagementPortletState;
import com.ecims.portlet.usermanagement.UserManagementPortletState.VIEW_TABS;
import com.ecims.portlet.usermanagement.UserManagementPortletUtil;
import com.ecims.portlet.usermanagement.UserManagementPortletState.EUC_DESK_ACTIONS;
import com.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.DuplicateUserEmailAddressException;
import com.liferay.portal.DuplicateUserScreenNameException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
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
	SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
	
	
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
        if(action.equalsIgnoreCase(EUC_DESK_ACTIONS.CREATE_A_PORTAL_USER_STEP_ONE.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	createPortalUserStepOne(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(EUC_DESK_ACTIONS.CREATE_A_PORTAL_USER_STEP_TWO.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_TWO");
        	//Select Account type
        	createPortalUserStepTwo(aReq, aRes, swpService, portletState);
        		
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
        		aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepone.jsp");
        		portletState.setCurrentTab(VIEW_TABS.CREATE_A_PORTAL_USERS);
        	}else
        	{
        		aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/userlisting/userlisting.jsp");
        		portletState.setCurrentTab(VIEW_TABS.VIEW_PORTAL_USER_LISTINGS);
        	}
        	
        }
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_UNAPPROVED_APPLICANTS.name()))
        {
        	//set corpoate indivudla listings
        	portletState.setPortalUserListing(portletState.getUserManagementPortletUtil().getPortalUserByStatus(UserStatus.USER_STATUS_ACTIVE));
        	aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/userlisting/userlisting.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_UNAPPROVED_APPLICANTS);
        }
        if(action.equalsIgnoreCase(VIEW_TABS.NEW_PORTAL_USER_REQUESTS.name()))
        {
        	//set corpoate indivudla listings
        	
        	if(portletState.getPortalUser().getRoleType().getName().equals(RoleTypeConstants.ROLE_NSA_ADMIN))
        	{
        		aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/userlisting/userlisting.jsp");
            	portletState.setCurrentTab(VIEW_TABS.NEW_PORTAL_USER_REQUESTS);
        	}else
        	{
        		//105
        		portletState.setNewRequestListing(portletState.getUserManagementPortletUtil().getPortalUserByStatus(UserStatus.USER_STATUS_INACTIVE));
	        	aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/userlisting/userlisting.jsp");
	        	portletState.setCurrentTab(VIEW_TABS.VIEW_PORTAL_USER_LISTINGS);
        	}
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
		RoleType rt = portletState.getUserManagementPortletUtil().getRoleTypeByRoleTypeName(RoleTypeConstants.fromString(portletState.getUsertype()));
		PortalUser pu = createPortalUser( portletState.getLastname(), portletState.getFirstname(),
				 portletState.getOthername(),  portletState.getContactEmailAddress(),  portletState.getCountryCode(), 
				 portletState.getMobile(),  portletState.getNationality(),  state, null,
				null, null, null, portletState.getDob(), rt, 
				null, null, null, null, null, null, 
				portletState, aReq, aRes);
		
		if(pu!=null)
		{
			String successMessage = "Account successfully created.";
			portletState.addSuccess(aReq, successMessage, portletState);
			aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepthree.jsp");
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/steptwo.jsp");
		}
	}

	private void createPortalUserStepOne(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			UserManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String usertype = aReq.getParameter("usertype");
		String lastname = aReq.getParameter("lastname");
		String firstname = aReq.getParameter("firstname");
		String othername = aReq.getParameter("othername");
		String contactEmailAddress = aReq.getParameter("contactEmailAddress");
		String dob = aReq.getParameter("dob");
		String countryCode = aReq.getParameter("countryCode");
		String mobile = aReq.getParameter("mobile");
		String nationality = aReq.getParameter("nationality");
		String stateStr = aReq.getParameter("state");
		State state = portletState.getUserManagementPortletUtil().getStateByName(stateStr);
		
		
		portletState.setUsertype(usertype);
		portletState.setLastname(lastname);
		portletState.setFirstname(firstname);
		portletState.setOthername(othername);
		portletState.setContactEmailAddress(contactEmailAddress);
		portletState.setDob(dob);
		portletState.setCountryCode(countryCode);
		portletState.setMobile(mobile);
		portletState.setNationality(nationality);
		portletState.setState(stateStr);
		
		if(validateCreatePortalUser(usertype, lastname, firstname, othername, contactEmailAddress, dob, countryCode, mobile, nationality, state))
		{
			aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/steptwo.jsp");
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/usermanagementportlet/register_individual/stepone.jsp");
		}
	}

	private boolean validateCreatePortalUser(String usertype, String lastname,
			String firstname, String othername, String contactEmailAddress,
			String dob, String countryCode, String mobile, String nationality,
			State state) {
		// TODO Auto-generated method stub
		return true;
	}
	
	

	private PortalUser createPortalUser(String lastName, String firstName,
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
			pu.setPasscode(UUID.randomUUID().toString());
			pu.setPasscodeGenerateTime(new Timestamp((new Date()).getTime()));
			pu.setStatus(UserStatus.USER_STATUS_INACTIVE);
			pu.setRoleType(roleType);
			pu.setCity(residenceCity == null ? "" : residenceCity);
			if(residenceState!=null)
				pu.setResidenceState(portletState.getUserManagementPortletUtil().getStateByName(residenceState).getId());
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
		}catch(ParseException e)
		{
			e.printStackTrace();
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
								auditTrail.setActivity("Create Portal User " + createdUser.getId());
								sService.createNewRecord(auditTrail);
								
								
								if(sendEmail){
									//sendMail(firstname, surname, email);
									
									
									emailer.emailNewBankStaffAccount
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

								

								System.out.println("Setting Orbita StaffId =" + newlyCreatedUser.getUserId());
								createdUser.setUserId(Long.toString(newlyCreatedUser.getUserId()));
								sService.updateRecord(createdUser);
								
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
