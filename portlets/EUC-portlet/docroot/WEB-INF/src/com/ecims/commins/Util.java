package com.ecims.commins;



import java.security.PrivilegedActionException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.client.AxisClient;
import org.apache.axis.configuration.NullProvider;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
//import org.apache.ws.axis.security.util.AxisUtil;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSConfig;
import org.apache.ws.security.WSSecurityEngine;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.WSSignEnvelope;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.zxing.common.Collections;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.io.OutputStreamWriter;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.sun.jersey.api.client.Client;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;



import smartpay.audittrail.AuditTrail;
import smartpay.entity.Application;
import smartpay.entity.ApplicationWorkflow;
import smartpay.entity.Company;
import smartpay.entity.ItemCategory;
import smartpay.entity.ItemCategorySub;
import smartpay.entity.PortalUser;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.enumerations.RoleConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.entity.enumerations.UserStatus;
import smartpay.service.SwpService;

public class Util {
	Logger log = Logger.getLogger(Util.class);

	private Pattern pattern;
	private Matcher matcher;
	private String dp = "1";
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	private String mp = "08";
	private String[] countryCode = {"260", "234", "001", "009", "235"};
	private String[] countryName = {"Zambia", "Nigeria", "United States of America(USA)", "England", "Ghana"};
	private int[] phoneLength = {10, 11, 11, 11, 11};
	private int yp = 2014;
	private int dpe = 9;
	///0140036968201
//	private static final WSSecurityEngine secEngine = new WSSecurityEngine();
//	private static final Crypto crypto = CryptoFactory.getInstance();
	private static final String[] tensNames = {
	    "",
	    " ten",
	    " twenty",
	    " thirty",
	    " forty",
	    " fifty",
	    " sixty",
	    " seventy",
	    " eighty",
	    " ninety"
	  };

	  private static final String[] numNames = {
	    "",
	    " one",
	    " two",
	    " three",
	    " four",
	    " five",
	    " six",
	    " seven",
	    " eight",
	    " nine",
	    " ten",
	    " eleven",
	    " twelve",
	    " thirteen",
	    " fourteen",
	    " fifteen",
	    " sixteen",
	    " seventeen",
	    " eighteen",
	    " nineteen"
	  };
	  
	  
	  
	public enum DETERMINE_ACCESS
	{
		NO_RIGHTS_AT_ALL, GRANT_INITIATOR_ACCESS, GRANT_APPROVER_ACCESS, GRANT_INITIATOR_AND_APPROVER_ACCESS, DISPLAY_SECOND_LEVEL_LOGIN
		
	}
	
	
	
	
	public String roundUpAmount(Double number)
	{
		DecimalFormat df = new DecimalFormat("#,##0.00");
		return df.format(number);
	}
	
	
	public Util() {
	}
	
	public String amountInWords(String a)
	{
		return "";
	}
	
	public void pushAuditTrail(SwpService swpService, String action, String activity, String ipAddress, String userId)
	{
		AuditTrail ad = new AuditTrail();
		ad.setAction(action);
		ad.setActivity(activity);
		ad.setDate(new Timestamp((new Date()).getTime()));
		ad.setIpAddress(ipAddress);
		ad.setUserId(userId);
		swpService.createNewRecord(ad);
	}
	
	public boolean isUserEmailPasswordValid(PortalUser portalUser, String email2, String password, SwpService sService) {
		// TODO Auto-generated method stub
		try {
			long login = UserLocalServiceUtil.authenticateForBasic(ECIMSConstants.COMPANY_ID, CompanyConstants.AUTH_TYPE_EA, 
					email2, password);
			log.info("login calue = " + login);
			if(login==0)
			{
				log.info("User Credentials are invalid");
				return false;
			}
			else
			{
				User lpUser = UserLocalServiceUtil.getUserById(login);
				if(lpUser.getStatus()==0)
				{
					log.info("User Credentials are Valid");
					return true;
				}else
				{
					log.info(20);
					return false;
				}
				
				
			}
				
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}


	
	
	public String formatMobile(String mobile3) {
		// TODO Auto-generated method stub
		String returnStr = mobile3;
		if(mobile3.length()>0)
		{
			String[] except = {"+", "+234", "234", "00234"};
			for(int c =0; c<except.length; c++)
			{
				if(mobile3.startsWith(except[c]))
				{
					returnStr = mobile3.replaceFirst(except[c], "");
				}
			}
		}
		return returnStr;
		
	}

	private static String convertLessThanOneThousand(int number) {
		String soFar;

	    if (number % 100 < 20){
	      soFar = numNames[number % 100];
	      number /= 100;
	    }
	    else {
	      soFar = numNames[number % 10];
	      number /= 10;

	      soFar = tensNames[number % 10] + soFar;
	      number /= 10;
	    }
	    if (number == 0) return soFar;
	    return numNames[number] + " hundred" + soFar;
	}
	
	
	public static String convert(long number) {
	    // 0 to 999 999 999 999
	    if (number == 0) { return "zero"; }

	    String snumber = Long.toString(number);

	    // pad with "0"
	    String mask = "000000000000";
	    DecimalFormat df = new DecimalFormat(mask);
	    snumber = df.format(number);

	    // XXXnnnnnnnnn
	    int billions = Integer.parseInt(snumber.substring(0,3));
	    // nnnXXXnnnnnn
	    int millions  = Integer.parseInt(snumber.substring(3,6));
	    // nnnnnnXXXnnn
	    int hundredThousands = Integer.parseInt(snumber.substring(6,9));
	    // nnnnnnnnnXXX
	    int thousands = Integer.parseInt(snumber.substring(9,12));

	    String tradBillions;
	    switch (billions) {
	    case 0:
	      tradBillions = "";
	      break;
	    case 1 :
	      tradBillions = convertLessThanOneThousand(billions)
	      + " billion ";
	      break;
	    default :
	      tradBillions = convertLessThanOneThousand(billions)
	      + " billion ";
	    }
	    String result =  tradBillions;

	    String tradMillions;
	    switch (millions) {
	    case 0:
	      tradMillions = "";
	      break;
	    case 1 :
	      tradMillions = convertLessThanOneThousand(millions)
	         + " million ";
	      break;
	    default :
	      tradMillions = convertLessThanOneThousand(millions)
	         + " million ";
	    }
	    result =  result + tradMillions;

	    String tradHundredThousands;
	    switch (hundredThousands) {
	    case 0:
	      tradHundredThousands = "";
	      break;
	    case 1 :
	      tradHundredThousands = "one thousand ";
	      break;
	    default :
	      tradHundredThousands = convertLessThanOneThousand(hundredThousands)
	         + " thousand ";
	    }
	    result =  result + tradHundredThousands;

	    String tradThousand;
	    tradThousand = convertLessThanOneThousand(thousands);
	    result =  result + tradThousand;

	    // remove extra spaces!
	    return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
	  }
	
	
	
	public String convertNumberToWords(int number)
	{
		 return convert(number);
	}

	public boolean checkmate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	public long getPortalUserCommunityByRoleType(RoleType roleType) {
		// TODO Auto-generated method stub
		try{
		log.info("roleType = " + roleType.getName());
		log.info("roleType = " + roleType.getName().getValue());
		}catch(Exception e){}
		if(roleType.getName().equals(RoleTypeConstants.ROLE_ACCREDITOR_USER))
		{
			return ECIMSConstants.ACCREDITOR_USER_COMMUNITY_ID;
		}else if(roleType.getName().equals(RoleTypeConstants.ROLE_END_USER))
		{
			return ECIMSConstants.END_USER_COMMUNITY_ID;
		}else if(roleType.getName().equals(RoleTypeConstants.ROLE_INFORMATION_USER))
		{
			return ECIMSConstants.INFORMATION_USER_COMMUNITY_ID;
		}else if(roleType.getName().equals(RoleTypeConstants.ROLE_NSA_ADMIN))
		{
			return ECIMSConstants.NSA_ADMIN_COMMUNITY_ID;
		}else if(roleType.getName().equals(RoleTypeConstants.ROLE_NSA_USER))
		{
			return ECIMSConstants.NSA_USER_COMMUNITY_ID;
		}else if(roleType.getName().equals(RoleTypeConstants.ROLE_REGULATOR_USER))
		{
			return ECIMSConstants.REGULATOR_USER_COMMUNITY_ID;
		}else if(roleType.getName().equals(RoleTypeConstants.ROLE_SYSTEM_ADMIN))
		{
			return ECIMSConstants.SYSTEM_ADMIN_COMMUNITY_ID;
		}else if(roleType.getName().equals(RoleTypeConstants.ROLE_EXCLUSIVE_USER))
		{
			return ECIMSConstants.EXCLUSIVE_USER_COMMUNITY_ID;
		}
		else
		{
			return ECIMSConstants.DEFAULT_SITE_ID;
		}
	}


	public void auditTrail(String action, String activity,
			String remoteIPAddress, String userId, SwpService swpService) {
		// TODO Auto-generated method stub
		AuditTrail ad = new AuditTrail();
		ad.setAction(action);
		ad.setActivity(activity);
		ad.setDate(new Timestamp((new Date()).getTime()));
		ad.setIpAddress(remoteIPAddress);
		ad.setUserId(userId);
		swpService.createNewRecord(ad);
	}


	public boolean isAttachmentCompulsoryForHSCode(String attachmentType,
			ItemCategory itemCategoryEntity) {
		// TODO Auto-generated method stub
		return false;
			
	}


	public ImageUpload uploadImage(UploadPortletRequest uploadRequest,
			String parameterName, String folder) {
		// TODO Auto-generated method stub
		ImageUpload imageUpload =null;
		try
		{
			String sourceFileName = uploadRequest.getFileName(parameterName);
			String[] strArr = sourceFileName.split("\\.");
			File file = uploadRequest.getFile(parameterName);
			log.info("Nome file:" + uploadRequest.getFileName(parameterName));
			File newFile = null;
			String newFileName = new SimpleDateFormat("yyyyMMddHHmmssS").format(new Date()) + "." + (strArr.length>0 ? strArr[strArr.length-1] : "png");
			newFile = new File(folder + newFileName);
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

	public boolean validateBiometric(PortalUser pu, Application app,
			ApplicationWorkflow apw, Session sess) {
		// TODO Auto-generated method stub
		
		//connectToExternalDB();
		Transaction tx = sess.beginTransaction();
		Query query = sess.createSQLQuery(
				"select s.fingerType from usersx s where s.userID = :puId")
				.setParameter("puId", pu.getId());
		
		List result = query.list();

		tx.commit();
		if(result!=null && result.size()>0)
		{
			return true;
		}else
		{
			return false;
		}
		
	}


	private void connectToExternalDB() {
		// TODO Auto-generated method stub
		
	}



	
	
	
	
	
}



class UtilActionResponse
{
	Boolean responseValid = null;
	String responseString = null;
	
	public void setResponseValid(Boolean responseValid)
	{
		this.responseValid = responseValid;
	}
	
	public Boolean getResponseValid()
	{
		return this.responseValid;
	}
	
	public void setResponseString(String responseString)
	{
		this.responseString = responseString;
	}
	
	public String getResponseString()
	{
		return this.responseString;
	}
}


