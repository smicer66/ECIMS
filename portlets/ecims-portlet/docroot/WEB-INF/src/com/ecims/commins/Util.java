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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.zxing.common.Collections;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.io.OutputStreamWriter;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;



import smartpay.audittrail.AuditTrail;
import smartpay.entity.Company;
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
	
	public enum DETERMINE_ACCESS
	{
		NO_RIGHTS_AT_ALL, GRANT_INITIATOR_ACCESS, GRANT_APPROVER_ACCESS, GRANT_INITIATOR_AND_APPROVER_ACCESS, DISPLAY_SECOND_LEVEL_LOGIN
		
	}
	
	
	public Util() {
	}


	public boolean checkmate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	public long getPortalUserCommunityByRoleType(RoleType roleType) {
		// TODO Auto-generated method stub
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
		}else
		{
			return ECIMSConstants.DEFAULT_SITE_ID;
		}
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


