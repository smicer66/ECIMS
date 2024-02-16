package com.ecims.commins;

import java.io.File;

import smartpay.entity.enumerations.RoleConstants;



public class ECIMSConstants {

	//user types
	public static final String PORTAL_ADMIN = "Portal Admin";
	public static final String PORTAL_USER = "Portal User";
	
	//orbita things
	public static final Long COMPANY_ID = 10154L;
	public static final Long SUPERADMIN_ID = 10196L;
	public static final Long DEFAULT_USER_COMMUNITY_ID = 10180L;
	public static final Long ORBITA_ADMIN_ROLE_ID = 10161L;//
	public static final Long ORBITA_USER_ROLE_ID = 10165L;//
	public static final Long ORBITA_USER_GROUP_ID = 14204L;
	public static final String PORTAL_USER_COMMUNITY_ID = "PORTAL_USER_COMMUNITY_ID";
	public static final String DEFAULT_SITE = "DEFAULT_SITE";
	public static final Long DEFAULT_SITE_ID = 100L;
	public static final Long SUPERVISOR_SITE =65801L;
	
//	public static final Long BANK_SUPER_ADMIN_COMMUNITY_ID = 11009L;
	public static final Long ACCREDITOR_USER_COMMUNITY_ID = 10462L;
	public static final Long END_USER_COMMUNITY_ID = 10508L;
	//ON LOCAL MACHINE: public static final Long EXCLUSIVE_USER_COMMUNITY_ID = 31101L;
	public static final Long EXCLUSIVE_USER_COMMUNITY_ID = 31301L;
	public static final Long INFORMATION_USER_COMMUNITY_ID = 10534L;
	public static final Long NSA_ADMIN_COMMUNITY_ID = 10560L;
	public static final Long NSA_USER_COMMUNITY_ID = 10586L;
	public static final Long REGULATOR_USER_COMMUNITY_ID = 10612L;
	public static final Long SYSTEM_ADMIN_COMMUNITY_ID = 10638L;
	public static final Long GUEST_COMMUNITY_ID = 12311L;
			
	//audit trail
	
	//email things
	public static final String DEFAULT_PASSWORD = "password";
	public static final String DOMAIN_EMAIL_SUFFIX = "@ecims.com";
	public static final String DEFAULT_SENDER_EMAIL = "noreply@ecims.com";
	public static final String DEFAULT_SENDER_PASSWORD = "password";
	
	//
	public static final String SENDER_EMAIL = "SENDER_EMAIL";
	public static final String SENDER_PASSWORD = "SENDER_PASSWORD";
	
	public static final String SEND_SMS = "SEND_SMS";
	
	//
	public static final String PORTAL_URL = "PORTAL_URL";
	public static final int EXCHANGE_RATE = 1;
	public static final String CURRENCY = "NAIRA";
	public static final String NEW_APPLICATION_DIRECTORY =  "C:" + File.separator + "jcodes" + File.separator + "dev" + 
			File.separator + "appservers" + File.separator + "ecims" + File.separator + "webapps"
			+ File.separator + "resources" + File.separator + "images" + File.separator + "uploadedfiles" + File.separator;
//	public static final String NEW_APPLICATION_DIRECTORY =  "/resources/images/uploadedfiles/";
	
	public static final String NEW_APPLICANT_DIRECTORY =  "C:" + File.separator + "jcodes" + File.separator + "dev" + 
			File.separator + "appservers" + File.separator + "ecims" + File.separator + "webapps"
			+ File.separator + "resources" + File.separator + "images" + File.separator + "uploadedfiles" + File.separator;
	//public static final String NEW_APPLICANT_DIRECTORY =  "/resources/images/uploadedfiles/";
	
	
	public static final String USER_SIGNUP="USER SIGNUP";
	public static final String APPLICANT_SIGNUP="APPLICANT SIGNUP";
	public static final String APPROVE_APPLICANT_SIGNUP="APPROVE APPLICANT SIGNUP";
	public static final String APPLICANT_ACTIVATE="ACTIVATE APPLICANT ACCOUNT";
	public static final String APPLICANT_SET_PASSWORD="SET APPLICANT PASSWORD";
	public static final String NEW_APPLICATION_REQUEST="CREATE NEW APPLICATION REQUEST";
	public static final String FORWARD_APPLICATION_REQUEST="FORWARD APPLICATION REQUEST";
	public static final String ENDORSE_APPLICATION_REQUEST="ENDORSE APPLICATION REQUEST";
	public static final String DISENDORSE_APPLICATION_REQUEST="DISENDORSE APPLICATION REQUEST";
	public static final String VALIDATE_APPLICATION_ATTACHMENT="VALIDATE APPLICATION ATTACHMENT";
	public static final String DEVALIDATE_APPLICATION_ATTACHMENT="DEVALIDATE APPLICATION ATTACHMENT";
	public static final String APPROVE_APPLICATION="APPROVE APPLICATION";
	public static final String DISAPPROVE_APPLICATION="DISAPPROVE APPLICATION";
	public static final String ISSUE_CERTIFICATE="ISSUE CERTIFICATE";
	public static final String CAPTURE_FINGER_PRINT="CAPTURE FINGER PRINT";
	public static final String ASSIGN_CERTIFICATE_NUMBER="ASSIGN CERTIFICATE NUMBER";
	public static final String COLLECT_CERTIFICATE="COLLECT CERTIFICATE";
	public static final String RECALL_CERTIFICATE="RECALL CERTIFICATE";
	public static final String BLACKLIST_APPLICANT="BLACKLIST APPLICANT";
	public static final String BLACKLIST_AGENCY="BLACKLIST AGENCY";
	public static final String UNBLACKLIST_APPLICANT="UNBLACKLIST APPLICANT";
	public static final String UNBLACKLIST_AGENCY="UNBLACKLIST AGENCY";
	public static final String AUTO_PROCESS_APPLICATION="AUTO PROCESS APPLICATION";
	public static final String APPLICATION_FLAGGING="FLAG AN APPLICATION";
	public static final String APPLICANT_UPDATE_PASSWORD = "UPDATE PORTAL USER PASSWORD";
	public static final String REJECT_CERTIFICATE = "CANCEL CERTIFICATE PRINTED";
	public static final String DISPUTE_CERTIFICATE = "DISPUTE CERTIFICATE";
	public static final String UPLOAD_CERTIFICATE = "UPLOAD CERTIFICATE";
	public static final String CANCEL_DISPUTE_CERTIFICATE = "CANCEL CERTIFICATE DISPUTE";
	public static final String GENERATE_REPORTS_XLS = "GENERATE XLS REPORT";
	public static final String GENERATE_AUDIT_XLS = "GENERATE XLS AUDIT";
	public static final String ACCEPT_DISPUTE_CERTIFICATE = "ACCEPT CERTIFICATE DISPUTE";
	
	
	
}
