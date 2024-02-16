package com.ecims.portlet.currencymanagement;

import java.util.Date;
import java.sql.Timestamp;
import smartpay.entity.*;
import smartpay.audittrail.AuditTrail;
import smartpay.service.SwpService;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;

/**
 *
 * @author EP-ScreenEdt
 */
public class AuditTrailer {

    /*
     All actions for the audit tra
     */
    public static final String AddActn = "NEW_RECORD";
    public static final String UpdateActn = "UPDATE_RECORD";
    public static final String DeleteActn = "DELETE_RECORD";
    public static final String MakeEnquiry = "MAKE_ENQUIRY";
    public static final String UpateEnquiry = "EDIT_ENQUIRY";
    public static final String DeleteEnquiry = "DELETE_ENQUIRY";
    public static final String New_Endorsement_Desk = "NEW_ENDORSEMENT_DESK";
    public static final String Update_Endorsement_Desk = "UPDATE_ENDORSEMENT_DESK";
    public static final String Delete_Endorsement_Desk = "DELETE_ENDORSEMENT_DESK";
    public static final String New_Endorsed_Application_Desk = "NEW_ENDORSED_APPLICATION_DESK";
    public static final String Update_Endorsed_Application_Desk = "UPDATE_ENDORSED_APPLICATION_DESK";
    public static final String Delete_Endorsed_Application_Desk = "DELETE_ENDORSED_APPLICATION_DESK";
    public static final String New_Endorse_Application = "NEW_ENDORSE_APPLICATION";
    public static final String Update_Endorse_Application = "UPDATE_ENDORSE_APPLICATION";
    public static final String Delete_Endorse_Application = "DELETE_ENDORSED_APPLICATION_DESK";
    public static final String New_Currency = "NEW_CURRENCY";
    public static final String Update_Currency = "UPDATE_CURRENCY";
    public static final String Delete_Currency = "DELETE_CURRENCY";
    public static final String New_Agency = "NEW_AGENCY";
    public static final String Update_Agency = "UPDATE_AGENCY";
    public static final String Delete_Agency = "DELETE_AGENCY";
    public static final String New_Applicant = "NEW_APPLICANT";
    public static final String Update_Applicant = "UPDATE_APPLICANT";
    public static final String Delete_Applicant = "DELETE_APPLICANT";

    Logger log = Logger.getLogger(AuditTrailer.class);

    public void trailActivity(String activity, String action, PortalUser user, SwpService swpService) {
        try {
            AuditTrail auditTrail = new AuditTrail();
            auditTrail.setAction(action);
            auditTrail.setActivity(activity);
            String userIdToString = (user != null) ? String.valueOf(user.getId()) : "68";
            String userId = userIdToString; //"68"; //String.valueOf(user.getId());
            auditTrail.setUserId(userId);
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            // auditTrail.setDate(ts);
            String ipAddress = String.valueOf(InetAddress.getLocalHost());
            auditTrail.setIpAddress(ipAddress);
            swpService.createNewRecord(auditTrail);
           
        } catch (UnknownHostException ex) {
            log.error("", ex);
        } catch (Exception ex) {
            log.error("", ex);
        }

    }
}
