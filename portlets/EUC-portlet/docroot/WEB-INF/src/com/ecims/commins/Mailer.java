package com.ecims.commins;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.sf.primepay.smartpay13.ServiceLocator;

import smartpay.entity.Application;
import smartpay.entity.ApplicationWorkflow;
import smartpay.entity.PortalUser;
import smartpay.service.SwpService;


public class Mailer
{
	static String userDir = System.getProperty("user.dir");
	
	public static String SIGN_UP_APPROVAL_REQUEST = "/mailtemplates/signupapprovalrequest.stl";
	public static String SIGN_UP_DISAPPROVAL_REQUEST = "/mailtemplates/signupdisapprovalrequest.stl";
	public static String NEW_SIGNUP = "/mailtemplates/new_account.stl";
	public static String NEW_CONTACT_US = "/mailtemplates/new_contact_us.stl";
	public static String NEW_PORTALUSER = "/mailtemplates/newportaluser.stl";
	public static String NEW_CORPORATE_INDIVIDUAL_EMAIL_TEMPLATE = "/mailtemplates/newcorporateindividual.stl";
	public static String NEW_CORPORATE_FIRM_EMAIL_TEMPLATE = "/mailtemplates/newcorporatefirm.stl";
	public static String NEW_BANK_STAFF_PROFILE_EMAIL_TEMPLATE = "/mailtemplates/newbankstaffprofile.stl";
	public static String USER_ACCOUNT_STATUS_UPDATE = "/mailtemplates/useraccountstatusupdate.stl";
	public static String UPDATE_USER_PROFILE_EMAIL_TEMPLATE = "/mailtemplates/updatebankstaffprofile.stl";
	public static String ADD_WORKDONE_EMAIL_TEMPLATE = "/mailtemplates/workdonetemplatespayment.stl";
	public static String DISAPPROVE_REQUEST_FOR_APPROVAL = "/mailtemplates/disapprovalrequest.stl";
	public static String ACTIVATE_ACCOUNT_BY_PORTAL_USER = "/mailtemplates/accountactivated.stl";
	public static String NEW_APPLICATION_CREATION = "/mailtemplates/newapplicationrequest.stl";
	public static String NOTIFY_NSA_NEW_APPLICANT_REQUEST = "/mailtemplates/notifynewapplicantrequest.stl";
	public static String NOTIFY_NSA_NEW_APPLICATION_REQUEST = "/mailtemplates/notifynewapplicationrequest.stl";
	public static String NOTIFY_NEXT_IN_LINE_ON_APPLICATION_WF_ITEM = "/mailtemplates/notifynextinlinewfitem.stl";
	public static String NOTIFY_APPLICATION_DISAPPROVAL = "/mailtemplates/notifyapplicationdisapprove.stl";
	public static String NOTIFY_APPLICATION_APPROVAL = "/mailtemplates/notifyapplicationapprove.stl";
	public static String NOTIFY_APPLICATION_APPROVAL_TOKEN = "/mailtemplates/notifyapplicationapprovetoken.stl";
	public static String BLACKLIST_APPLICANT = "/mailtemplates/blacklist.stl";
	public static String NOTIFY_CERT_ISSUE = "/mailtemplates/certissuance.stl";
	public static String NOTIFY_PORTAL_USER_FORWARDER_ENDORSE_DISENDORSE = "/mailtemplates/notifyapplicationdisendorse.stl";
	public static String NOTIFY_PORTAL_USER_FORWARDER_ENDORSE_ENDORSE = "/mailtemplates/notifyapplicationendorse.stl";
	
	
//	String FROM_EMAIL = "etax@probasegroup.com"; 
//	String FROM_PASSWORD = "liverppolmlp-MLP_"; 
	private String FROM_EMAIL = "etax@stanbic.com"; 
	private String FROM_PASSWORD = "St@nbic1";

	private int PORT = 465; //465;25
	private String SENDER_USERNAME = "etax@probasegroup.com";
	SwpService sservice = ServiceLocator.getInstance().getSwpService();

	Logger log = Logger.getLogger(Mailer.class);

	public Mailer(String email, String password, int port, String username)
	{
		this.FROM_EMAIL = email;
		this.FROM_PASSWORD = password;
		this.PORT = port;
		this.SENDER_USERNAME = username;
		
	}
	
	
	
	public SendMail emailDisapproval(String toEmail,
			String firstName, String lastName, String reason, String url, String subject, String applicationName) {
		// TODO Auto-generated method stub
			System.out.println("in send payment email");
			SendMail sendEmail = null;
			String body = "";
			try
			{
				log.info("URL = " + DISAPPROVE_REQUEST_FOR_APPROVAL);
				List<String> lines = IOUtils.readLines(Mailer.class
						.getResourceAsStream(DISAPPROVE_REQUEST_FOR_APPROVAL));
				StringBuffer buffer = new StringBuffer();
				for (String line : lines)
				{
					buffer.append(line).append("\n");
				}
				
				String returnStr = "";
				String str="";
				
				
				Map<String, String> params = new HashMap<String, String>();
				params.put("firstname", firstName);
				params.put("lastname", lastName);
				params.put("details", reason);
				params.put("systemUrl", url);
				StringTemplate template = new StringTemplate(buffer.toString());

				template.setAttributes(params);
				body = template.toString();
				log.info("body = " + body);

				sendEmail = new SendMail(FROM_EMAIL, toEmail, FROM_PASSWORD,
						subject, body, PORT, SENDER_USERNAME, applicationName);
			} catch (Exception ex)
			{
				ex.printStackTrace();
				log.error("", ex);
			}
			return sendEmail;
	}
	
	
	public SendMail emailNewSignup(String toEmail,
			String firstName, String lastName, String reason, String url, String subject, String applicationName) {
		// TODO Auto-generated method stub
			System.out.println("in send payment email");
			SendMail sendEmail = null;
			String body = "";
			try
			{
				log.info("URL = " + DISAPPROVE_REQUEST_FOR_APPROVAL);
				List<String> lines = IOUtils.readLines(Mailer.class
						.getResourceAsStream(DISAPPROVE_REQUEST_FOR_APPROVAL));
				StringBuffer buffer = new StringBuffer();
				for (String line : lines)
				{
					buffer.append(line).append("\n");
				}
				
				String returnStr = "";
				String str="";
				
				
				Map<String, String> params = new HashMap<String, String>();
				params.put("firstname", firstName);
				params.put("lastname", lastName);
				params.put("systemUrl", url);
				StringTemplate template = new StringTemplate(buffer.toString());

				template.setAttributes(params);
				body = template.toString();

				sendEmail = new SendMail(FROM_EMAIL, toEmail, FROM_PASSWORD,
						subject, body, PORT, SENDER_USERNAME, applicationName);
			} catch (Exception ex)
			{
				ex.printStackTrace();
				log.error("", ex);
			}
			return sendEmail;
	}
	
	
	
	public SendMail emailNewAccountActivation(String toEmail,
			String firstName, String lastName, String reason, String url, String subject, 
			String applicationName, boolean activate, String password) {
		// TODO Auto-generated method stub
			System.out.println("in send payment email");
			SendMail sendEmail = null;
			String body = "";
			try
			{
				log.info("URL = " + ACTIVATE_ACCOUNT_BY_PORTAL_USER);
				List<String> lines = IOUtils.readLines(Mailer.class
						.getResourceAsStream(ACTIVATE_ACCOUNT_BY_PORTAL_USER));
				StringBuffer buffer = new StringBuffer();
				for (String line : lines)
				{
					buffer.append(line).append("\n");
				}
				
				String returnStr = "";
				String str="";
				
				
				Map<String, String> params = new HashMap<String, String>();
				params.put("firstname", firstName);
				params.put("lastname", lastName);
				params.put("email", toEmail);
				params.put("password", password);
				params.put("systemUrl", url);
				StringTemplate template = new StringTemplate(buffer.toString());

				template.setAttributes(params);
				body = template.toString();

				sendEmail = new SendMail(FROM_EMAIL, toEmail, FROM_PASSWORD,
						subject, body, PORT, SENDER_USERNAME, applicationName);
			} catch (Exception ex)
			{
				ex.printStackTrace();
				log.error("", ex);
			}
			return sendEmail;
	}
	
	
	
	
	public SendMail emailWorkFlow(String toEmail, String companyName,
			List<String> registrationNumber, String token, String url,
			String firstName, String lastName, String subject, String applicationName) {
		// TODO Auto-generated method stub
		System.out.println("in send payment email");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + ADD_WORKDONE_EMAIL_TEMPLATE);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(ADD_WORKDONE_EMAIL_TEMPLATE));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			String returnStr = "";
			String str="";
			for(Iterator<String> st = registrationNumber.iterator(); st.hasNext();)
			{
				str = str + st.next() + ", ";
			}
			returnStr += "<div><strong>Access Token:</strong>" + token + "</div>";
			returnStr += "<div><strong>Assessment(s):</strong>" + str.substring(0, str.length()-2) + "</div>";
			
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("companyName", companyName);
			params.put("firstname", firstName);
			params.put("lastname", lastName);
			params.put("paymentdetails", returnStr);
			params.put("token", token);
			params.put("systemUrl", url);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, toEmail, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, applicationName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}
	
	
	
	public SendMail emailSignUpApprovalRequest(PortalUser pu, String systemUrl, String applicationName) {
		// TODO Auto-generated method stub
		System.out.println("in send new signup request approval");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + SIGN_UP_APPROVAL_REQUEST);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(SIGN_UP_APPROVAL_REQUEST));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("firstname", pu.getFirstName());
			params.put("lastname", pu.getSurname());
			params.put("username", pu.getEmailAddress());
			params.put("systemUrl", systemUrl);
			params.put("passcode", pu.getPasscode());
			params.put("activationcode", pu.getActivationCode());
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, pu.getEmailAddress(), FROM_PASSWORD,
					"End-User Ceritificate SignUp Request Status - Approved", body, PORT, SENDER_USERNAME, applicationName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}
	
	
	
	public SendMail emailSignUpDisapprovalRequest(PortalUser pu, String systemUrl, String applicationName, String reason) {
		// TODO Auto-generated method stub
		System.out.println("in send new signup request disapproval");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + SIGN_UP_DISAPPROVAL_REQUEST);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(SIGN_UP_DISAPPROVAL_REQUEST));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("firstname", pu.getFirstName());
			params.put("lastname", pu.getSurname());
			params.put("reason", reason);
			params.put("systemUrl", systemUrl);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, pu.getEmailAddress(), FROM_PASSWORD,
					"End-User Ceritificate SignUp Request Status - Disapproved", body, PORT, SENDER_USERNAME, applicationName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}
	
	
	public SendMail emailNewAccountRequest(String emailAddress,
			String companyName, String password1, String systemUrl,
			String firstName, String lastName, String userclass, String subject, String applicationName) {
		// TODO Auto-generated method stub
		System.out.println("in send new individual email");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + NEW_SIGNUP);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(NEW_SIGNUP));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("firstname", firstName);
			params.put("lastname", lastName);
			params.put("userclass", userclass);
			params.put("username", emailAddress);
			params.put("systemUrl", systemUrl);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, applicationName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}
	
	
	
	public SendMail emailNewContactUs(String emailAddress,
			String fullName, String mobileNumber, String userSubject,
			String contents, String subject, String applicationName, String recEmailAddress) {
		// TODO Auto-generated method stub
		
		System.out.println("in send new contactus email");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + NEW_CONTACT_US);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(NEW_CONTACT_US));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("emailAddress", emailAddress);
			params.put("fullName", fullName);
			params.put("mobileNumber", mobileNumber);
			params.put("userSubject", userSubject);
			params.put("contents", contents);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			
			sendEmail = new SendMail(FROM_EMAIL, recEmailAddress, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, applicationName);
		} 
		catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}
	
	
	
	public SendMail emailNewPortalUserAccount(String emailAddress,
			String companyName, String password1, String systemUrl,
			String firstName, String lastName, String userclass, String subject, String applicationName) {
		// TODO Auto-generated method stub
		System.out.println("in send new individual email");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + NEW_PORTALUSER);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(NEW_PORTALUSER));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("firstname", firstName);
			params.put("lastname", lastName);
			params.put("userclass", userclass);
			params.put("username", emailAddress);
			params.put("password", password1);
			params.put("systemUrl", systemUrl);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, applicationName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}
	
	
	
	public SendMail emailChangeOfAccountStatus(String emailAddress,
			String systemUrl, String firstName, String lastName, String subject, String msg, String applicationName) {
		// TODO Auto-generated method stub
		System.out.println("in send new corporate individual email");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + USER_ACCOUNT_STATUS_UPDATE);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(USER_ACCOUNT_STATUS_UPDATE));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("firstname", firstName);
			params.put("lastname", lastName);
			params.put("systemUrl", systemUrl);
			params.put("message", msg);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, applicationName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}
	
	
	public SendMail emailUpdateUserProfileAccount(String emailAddress, 
			String firstname, String surname, String subject, String systemUrl, String applicationName) {
		// TODO Auto-generated method stub
		System.out.println("in send update corporate staff email");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + UPDATE_USER_PROFILE_EMAIL_TEMPLATE);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(UPDATE_USER_PROFILE_EMAIL_TEMPLATE));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("firstname", firstname);
			params.put("lastname", surname);
			params.put("username", emailAddress);
			params.put("systemUrl", systemUrl);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, applicationName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}
	
	
	public SendMail emailNewCorporateIndividualAccount(String emailAddress,
			String companyName, String password1, String systemUrl,
			String firstName, String lastName, String userclass, String subject, String applicationName) {
		// TODO Auto-generated method stub
		System.out.println("in send new corporate individual email");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + NEW_CORPORATE_INDIVIDUAL_EMAIL_TEMPLATE);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(NEW_CORPORATE_INDIVIDUAL_EMAIL_TEMPLATE));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("companyName", companyName);
			params.put("firstname", firstName);
			params.put("lastname", lastName);
			params.put("userclass", userclass);
			params.put("username", emailAddress);
			params.put("password", password1);
			params.put("systemUrl", systemUrl);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, applicationName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}
	
	
	public SendMail emailPayment(String toEmail, String companyName, String assessmentRegNo, 
			HashMap<String, Double> paymentdetails, String accountNumber, 
			String firstname, String lastname, String subject, String bankName, String applicationName)
	{
		
		System.out.println("in send payment email");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + NEW_SIGNUP);
			List<String> lines = IOUtils.readLines(Mailer.class.getResourceAsStream(NEW_SIGNUP));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Set<String> keys = paymentdetails.keySet();
			String key  = keys.iterator().next();
			String returnStr = "";
			
			returnStr += "<div>Assessment Registration Number:" + key + "</div>";
			returnStr += "<table style='width: 100%; border: 1px #ffffff solid;'>";
			returnStr += "<tr><td style='background-color: #003775; font-weight:bold'>Payment Item</td>" +
					"<td style='background-color: #003775; font-weight:bold'>Amount(ZMW)</td></tr>";
			Set<String> keys1 = paymentdetails.keySet();
			for(Iterator<String> iter1 = keys1.iterator(); iter1.hasNext();)
			{
				String key1 = iter1.next();
				returnStr += "<tr><td>";
				returnStr += key1;
				returnStr += "</td><td>";
				returnStr += "ZMW" + paymentdetails.get(key1);
				returnStr += "</td><tr>";
			}
			returnStr += "</table>";
				
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("companyName", companyName);
			params.put("firstname", firstname);
			params.put("lastname", lastname);
			params.put("paymentdetails", returnStr);
			params.put("accountNumber", accountNumber);
			params.put("successful", "successful");
			params.put("bank", bankName);
			params.put("key",key);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			
			sendEmail = new SendMail(FROM_EMAIL, toEmail, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, applicationName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}
	
	
	
	
	public SendMail emailNewCorporateCompany(String companyEmail, 
			String companyName,
			String systemUrl, 
			String accountType,
			String tpin,
			String accountNumber,
			String bankBranches,
			String mobileNumber,
			String subject, String selectedCompanyClass, String applicationName) {
		// TODO Auto-generated method stub
		System.out.println("in send new corporate individual email");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + NEW_CORPORATE_FIRM_EMAIL_TEMPLATE);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(NEW_CORPORATE_FIRM_EMAIL_TEMPLATE));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("companyName", companyName);
			params.put("accountType", accountType);
			params.put("accountClass", selectedCompanyClass);
			params.put("tpin", tpin);
			params.put("bankAccount", accountNumber);
			params.put("systemUrl", systemUrl);
			params.put("bankBranch", accountType);
			params.put("mobileNumber", mobileNumber);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, companyEmail, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, applicationName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}



	public SendMail emailNewApplicationNotification(String companyEmail,
			String sysUrl, String firstName, String surname, String subject,
			String appName, String applicationNumber) {
		// TODO Auto-generated method stub
		System.out.println("emailNewApplicationNotification");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + NEW_APPLICATION_CREATION);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(NEW_APPLICATION_CREATION));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("appnumber", applicationNumber);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, companyEmail, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, appName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}



	public SendMail emailNSANewSignupRequest(String emailAddress, String url,
			String firstName, String surname, String roleType, String subject, String appName) {
		// TODO Auto-generated method stub
		System.out.println("emailNSANewSignupRequest");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + NOTIFY_NSA_NEW_APPLICANT_REQUEST);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(NOTIFY_NSA_NEW_APPLICANT_REQUEST));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("systemUrl", url);
			params.put("firstName", firstName);
			params.put("surname", surname);
			params.put("subject", subject);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, appName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}



	public SendMail emailNSANewApplicationRequest(String emailAddress,
			String url, String firstName, String surname, String subject,
			String appName, Application app) {
		// TODO Auto-generated method stub
		System.out.println("emailNSANewApplicationRequest");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + NOTIFY_NSA_NEW_APPLICATION_REQUEST);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(NOTIFY_NSA_NEW_APPLICATION_REQUEST));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("systemUrl", url);
			params.put("firstname", firstName);
			params.put("lastname", surname);
			params.put("subject", subject);
			params.put("systemUrl", url);
			params.put("appno", app.getApplicationNumber());
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, appName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}



	public SendMail emailNextInLineOnApplicationWorkflowItem(String emailAddress,
			String url, String firstName, String surname, String subject,
			String appName, ApplicationWorkflow aw) {
		// TODO Auto-generated method stub
		System.out.println("emailNextInLineOnApplicationWorkflowItem");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + NOTIFY_NEXT_IN_LINE_ON_APPLICATION_WF_ITEM);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(NOTIFY_NEXT_IN_LINE_ON_APPLICATION_WF_ITEM));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("systemUrl", url);
			params.put("firstName", firstName);
			params.put("surname", surname);
			params.put("subject", subject);
			params.put("systemUrl", url);
			params.put("appno", aw.getApplication().getApplicationNumber());
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, appName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}
	
	
	public SendMail emailPortalUserOnEndorse(String emailAddress,
			String url, String firstName, String surname, String subject,
			String appName, ApplicationWorkflow aw, boolean endorseYes) {
		// TODO Auto-generated method stub
		System.out.println("emailPortalUserOnEndorse");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			List<String> lines=null;
			if(endorseYes==false)
			{
				log.info("URL = " + NOTIFY_PORTAL_USER_FORWARDER_ENDORSE_DISENDORSE);
				lines = IOUtils.readLines(Mailer.class
						.getResourceAsStream(NOTIFY_PORTAL_USER_FORWARDER_ENDORSE_DISENDORSE));
			}else
			{
				log.info("URL = " + NOTIFY_PORTAL_USER_FORWARDER_ENDORSE_ENDORSE);
				lines = IOUtils.readLines(Mailer.class
						.getResourceAsStream(NOTIFY_PORTAL_USER_FORWARDER_ENDORSE_ENDORSE));
			}
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("systemUrl", url);
			params.put("firstName", firstName);
			params.put("surname", surname);
			params.put("subject", subject);
			params.put("comment", "");
			params.put("systemUrl", url);
			params.put("appno", aw.getApplication().getApplicationNumber());
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, appName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}



	public SendMail emailAppRequestStatusDisapproved(String emailAddress, String firstName,
			String surname, String subject, String appname, String url, ApplicationWorkflow aw) {
		// TODO Auto-generated method stub
		System.out.println("emailAppRequestStatusDisapproved");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + NOTIFY_APPLICATION_DISAPPROVAL);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(NOTIFY_APPLICATION_DISAPPROVAL));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("systemUrl", url);
			params.put("firstName", firstName);
			params.put("surname", surname);
			params.put("subject", subject);
			params.put("systemUrl", url);
			params.put("comments", aw.getComment());
			params.put("appno", aw.getApplication().getApplicationNumber());
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, appname);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}



	public SendMail emailAppRequestStatusApproved(String emailAddress,
			String firstName, String surname, String subject, String appname,
			String url, ApplicationWorkflow apwNew, String collectionCode) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		System.out.println("emailAppRequestStatusDisapproved");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + NOTIFY_APPLICATION_APPROVAL);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(NOTIFY_APPLICATION_APPROVAL));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("systemUrl", url);
			params.put("firstName", firstName);
			params.put("surname", surname);
			params.put("subject", subject);
			params.put("systemUrl", url);
			params.put("collectionCode", collectionCode);
			params.put("appno", apwNew.getApplication().getApplicationNumber());
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, appname);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}
	
	
	
	public SendMail emailAppApprovalToken(String emailAddress,
			String firstName, String surname, String subject, String appname,
			String url, String mailContents, String token, String appNo) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		System.out.println("emailAppApprovalToken");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + NOTIFY_APPLICATION_APPROVAL_TOKEN);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(NOTIFY_APPLICATION_APPROVAL_TOKEN));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("systemUrl", url);
			params.put("firstName", firstName);
			params.put("surname", surname);
			params.put("subject", subject);
			params.put("systemUrl", url);
			params.put("mailContents", mailContents);
			params.put("token", token);
			params.put("appNo", appNo);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, appname);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}



	public SendMail emailEUCIssuanceNotification(String emailAddress,
			String firstName, String surname, String subject, String appname,
			String url, String certno) {
		// TODO Auto-generated method stub
		System.out.println("emailAppRequestStatusDisapproved");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + NOTIFY_CERT_ISSUE);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(NOTIFY_CERT_ISSUE));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("systemUrl", url);
			params.put("firstName", firstName);
			params.put("surname", surname);
			params.put("subject", subject);
			params.put("systemUrl", url);
			params.put("certno", certno);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, appname);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
	}



	public SendMail emailApplicantBlacklisted(String emailAddress,
			String firstName, String surname, String subject, String applicationName,
			String url, String reason) {
		// TODO Auto-generated method stub
		System.out.println("emailAppRequestStatusDisapproved");
		SendMail sendEmail = null;
		String body = "";
		try
		{
			log.info("URL = " + NOTIFY_APPLICATION_APPROVAL);
			List<String> lines = IOUtils.readLines(Mailer.class
					.getResourceAsStream(BLACKLIST_APPLICANT));
			StringBuffer buffer = new StringBuffer();
			for (String line : lines)
			{
				buffer.append(line).append("\n");
			}
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("systemUrl", url);
			params.put("firstName", firstName);
			params.put("surname", surname);
			params.put("subject", subject);
			params.put("systemUrl", url);
			params.put("applicationName", applicationName);
			params.put("reason", reason);
			StringTemplate template = new StringTemplate(buffer.toString());

			template.setAttributes(params);
			body = template.toString();

			sendEmail = new SendMail(FROM_EMAIL, emailAddress, FROM_PASSWORD,
					subject, body, PORT, SENDER_USERNAME, applicationName);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("", ex);
		}
		return sendEmail;
		
	}

	
	
}

