package com.ecims.commins;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;

import smartpay.service.SwpService;

import com.liferay.portal.kernel.servlet.URLEncoder;

public class SendSms extends Thread
{
	private static final Logger logger = Logger.getLogger(SendMail.class);
	private String toMobileNumber;
	private String message;
	private String from;
	private static final String URL_ = "http://193.105.74.59/api/sendsms/plain";
	private static final String USER_AGENT = "Mozilla/5.0";
	private String proxyHost=null;
	private String proxyPort=null;
	private boolean j = false;
	private SwpService swpService;

	public SendSms(String toMobileNumber, String message, String from, String proxyHost, String proxyPort)
	{
		System.out.println("in send sms");
		
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.toMobileNumber = toMobileNumber;
		this.swpService = swpService;
		try {
			this.message = java.net.URLEncoder.encode(message, "UTF-8");
			this.from = from;
			Thread thread = new Thread(this);
			thread.start();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run()
	{
		if(this.proxyHost!=null && this.proxyPort!=null)
		{
//			System.setProperty("http.proxyHost", "10.236.6.99");
//			System.setProperty("http.proxyPort", "80");
		}
		
		if(j==true)
		{
			try {
				URL url = new URL(URL_ + "?user=ecimsuser&password=QkJPDlhx&SMSText="+  this.message + "&sender=" + this.from + "&GSM=" + this.toMobileNumber);
				System.out.println("URL ==" + URL_ + "?user=ecimsuser&password=QkJPDlhx&SMSText="+  this.message + "&sender=" + this.from + "&GSM=" + this.toMobileNumber);
				URLConnection con = url.openConnection();
				BufferedReader in = new BufferedReader(
	                    new InputStreamReader(
	                    		con.getInputStream()));
				String inputLine;
				System.out.println("Send a message to this number: " + this.toMobileNumber);
				System.out.println("Send this message: " + this.message);
				while ((inputLine = in.readLine()) != null) 
				System.out.println(inputLine);
				in.close();
				System.out.println("inputLine ==" + inputLine);
				in.close();
				
				String join = RandomStringUtils.random(8, true, true);
				String sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(new Date());
				String mgs = this.message + "|| Date Sent: " + sdf + " || " + this.toMobileNumber + " || " + UUID.randomUUID().toString();
//				String connURL = "jdbc:sqlserver://localhost:1434;databaseName=ecims;";
//				Connection con1 = null;
//			    try {
//			    	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//			    	con1 = DriverManager.getConnection(connURL, "ecims_user", "k0l0zaq1ZAQ!");
//					Statement stmt = con1.createStatement();
//				    String sql = "INSERT INTO WEIGHT_UNIT (NAME, " +
//				    		"UNIT, STATUS)" +
//				                   "VALUES ('"+mgs+"', '"+this.toMobileNumber+ ":" + join + "', 1)";
//				    stmt.executeUpdate(sql);
//				    con1.close();
//				} catch (ClassNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			    
			    
				
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
