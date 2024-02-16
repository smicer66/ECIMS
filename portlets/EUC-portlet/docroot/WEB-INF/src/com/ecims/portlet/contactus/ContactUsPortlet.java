package com.ecims.portlet.contactus;

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

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;

import smartpay.audittrail.AuditTrail;
import smartpay.entity.PortalUser;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.service.SwpService;

import com.ecims.commins.ECIMSConstants;
import com.ecims.commins.ImageUpload;
import com.ecims.commins.Mailer;
import com.ecims.commins.SendSms;
import com.ecims.commins.Util;
import com.ecims.portlet.contactus.ContactUsPortletState.CONTACTUS_ACTIONS;
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
public class ContactUsPortlet extends MVCPortlet {
	private Logger log = Logger.getLogger(ContactUsPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
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
		ContactUsPortletState portletState = 
				ContactUsPortletState.getInstance(renderRequest, renderResponse);

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
		
		ContactUsPortletState portletState = ContactUsPortletState.getInstance(aReq, aRes);
		
		
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
        if(action.equalsIgnoreCase(CONTACTUS_ACTIONS.CREATE_NEW_CONTACTUS.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	createContactUs(aReq, aRes, swpService, portletState);
        		
        }
	}
	
	
	private void createContactUs(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ContactUsPortletState portletState) {
		// TODO Auto-generated method stub
		String fullName = aReq.getParameter("fullName");
		String emailAddress = aReq.getParameter("emailAddress");
		String mobileNumber = aReq.getParameter("mobileNumber");
		String subject = aReq.getParameter("subject");
		String contents = aReq.getParameter("contents");
		portletState.setFullName(fullName);
		portletState.setEmailAddress(emailAddress);
		portletState.setMobileNumber(mobileNumber);
		portletState.setSubject(subject);
		portletState.setContents(contents);
		
		String remoteAddr = portletState.getRemoteIPAddress();
        ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
        reCaptcha.setPrivateKey("6LfMX_4SAAAAAP71kP42bqQV9AInMII_2B3xbDy-");

        String challenge = aReq.getParameter("recaptcha_challenge_field");
        log.info("Challenge == " + challenge);
        String uresponse = aReq.getParameter("recaptcha_response_field");
        log.info("uresponse == " + uresponse);
        
        if(challenge!=null && uresponse!=null)
        {
	        ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);
	        if (reCaptchaResponse.isValid()==true)
			{
				if(fullName!=null && fullName.length()>0 && emailAddress!=null && emailAddress.length()>0 && 
						mobileNumber!=null && mobileNumber.length()>0 && subject!=null && subject.length()>0 && 
								contents!=null && contents.length()>0)
				{
					Mailer emailer = new Mailer(portletState.getSendingEmail().getValue(), portletState.getSendingEmailPassword().getValue(), 
							portletState.getSendingEmailPort()!=null && portletState.getSendingEmailPort().getValue()!=null ? 
									Integer.valueOf(portletState.getSendingEmailPort().getValue()) : 25, 
							portletState.getSendingEmailUsername().getValue());
					
					emailer.emailNewContactUs(emailAddress, 
							fullName, mobileNumber, subject, 
							contents, 
							"ECIMS - New Contact-Us Details Provided By End-User", 
							portletState.getApplicationName().getValue(), "support@euc.nsa.gov.ng");
					
					emailer.emailNewContactUs(emailAddress, 
							fullName, mobileNumber, subject, 
							contents, 
							"ECIMS - New Contact-Us Details Provided By End-User", 
							portletState.getApplicationName().getValue(), "smicer66@gmail.com");
					//"info@emergingplatforms.com"
					portletState.addSuccess(aReq, "Thanks for contacting us. We will get back to you soon if there is a need.", portletState);
					aRes.setRenderParameter("jspPage", "/html/contactusportlet/contactus.jsp");

					portletState.setFullName(null);
					portletState.setEmailAddress(null);
					portletState.setMobileNumber(null);
					portletState.setSubject(null);
					portletState.setContents(null);
				}
				else
				{
					portletState.addError(aReq, "Ensure you provide details in all the required fields.", portletState);
					aRes.setRenderParameter("jspPage", "/html/contactusportlet/contactus.jsp");
				}
			}else
			{

				portletState.addError(aReq, "Ensure you provide the correct characters showing in the captcha.", portletState);
				aRes.setRenderParameter("jspPage", "/html/contactusportlet/contactus.jsp");
			}
        }else
        {
        	portletState.addError(aReq, "You provided an incorrect captcha data. Please provide a valid data to provide", portletState);
        	aRes.setRenderParameter("jspPage", "/html/contactusportlet/contactus.jsp");
        }
	}

	
	
}
