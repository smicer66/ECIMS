package com.ecims.commins;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.naming.NamingException;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import lombok.extern.log4j.Log4j;

import common.Logger;


import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;

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
import org.hibernate.HibernateException;

import smartpay.audittrail.AuditTrail;
import smartpay.entity.Company;
import smartpay.entity.Permission;
import smartpay.entity.PortalUser;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.enumerations.PermissionType;
import smartpay.exception.SwpException;
import smartpay.service.SwpService;

import com.liferay.portal.DuplicateUserScreenNameException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.ecims.commins.Emailer;
import com.ecims.commins.ECIMSConstants;
import com.ecims.commins.SendMail;
import com.ecims.commins.Util;
import com.google.zxing.common.Collections;
import com.sf.primepay.smartpay13.HibernateUtils;
import com.sf.primepay.smartpay13.ServiceLocator;



@Log4j
public class ComminsApplicationState implements Serializable {

	static Logger log = Logger.getLogger(ComminsApplicationState.class);
	//
	private Boolean loggedIn = null;
	private PortalUser pu = null;
	private static ServiceLocator serviceLocator = ServiceLocator.getInstance();
	private static SwpService swpService = serviceLocator.getSwpService();
	
	
	private Settings currency;
	private String proxyHost=null;
	private String proxyPort=null;
	private Collection<PermissionType> permissionList;
	private static ComminsApplicationState applicationState = null;
	
	
	private ComminsApplicationState() {

    }
	
	
    public Collection<PermissionType> getPermissionList() {
		return permissionList;
	}


	public void setPermissionList(Collection<PermissionType> pm) {
		
		this.permissionList = pm;
	}


	public static ComminsApplicationState getInstance(PortletRequest request,PortletResponse response, PortalUser pu) {

        //ServiceLocator serviceLocator = (ServiceLocator) pContext.getAttribute("serviceLocator");
        //ResourceBundle bundle = (ResourceBundle) pContext.getAttribute("bundle");        

    	
        try {
            PortletSession session = request.getPortletSession();
            applicationState = (ComminsApplicationState) session.getAttribute(ComminsApplicationState.class
                    .getName(), PortletSession.APPLICATION_SCOPE);

            if (applicationState == null) {

                applicationState = new ComminsApplicationState();
                /* add state initialization codes here */
                log.info("--------------->>>> im in the process application state!!!");
                
                session.setAttribute(ComminsApplicationState.class.getName(), applicationState, PortletSession.APPLICATION_SCOPE);
                
                Collection<PermissionType> pm = loadPermissions(applicationState, pu, swpService);
                applicationState.setPermissionList(pm);
            }
            log.info("We are here");
            log.info("size==" + (applicationState.getPermissionList()==null ? ">><<" : applicationState.getPermissionList().size()));
            
            return applicationState;
        } catch (Exception e) {
		System.out.println("Error while getting application state");
            //PortalLogger.error("Error while getting application state", e);
            e.printStackTrace();
            return null;
        }
    }




	public static Collection<PermissionType> loadPermissions(ComminsApplicationState applicationState, PortalUser pu, SwpService swpService2) {
		// TODO Auto-generated method stub
		Collection<PermissionType> pm = applicationState.getPermissionsByPortalUser(pu, swpService2);
		applicationState.setPermissionList(pm);
		return pm;
	}



	private Collection<PermissionType> getPermissionsByPortalUser(PortalUser pu2, SwpService swpService2) {
		// TODO Auto-generated method stub
		Collection<PermissionType> permList = null;
		try {
			
			String hql = "select rt.permissionType from Permission rt where (" +
					"rt.portalUser.id = " + pu2.getId() + ")";
			log.info("Get hql = " + hql);
			permList = (Collection<PermissionType>) swpService.getAllRecordsByHQL(hql);
		
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return permList;
	}



	public String getUniquePaymentReferenceId() {

        String uniqueGeneratedRefId = UUID.randomUUID().toString().replace("-", "");
        if (uniqueGeneratedRefId.length() < 16) {
            uniqueGeneratedRefId = uniqueGeneratedRefId + UUID.randomUUID().toString().replace("-", "");
        }
        uniqueGeneratedRefId = uniqueGeneratedRefId.substring(0, 4) + "-" + uniqueGeneratedRefId.substring(4, 9) + "-"
                + uniqueGeneratedRefId.substring(9, 13) + "-" + uniqueGeneratedRefId.substring(13, 18);
        
        //System.out.println("UUID = " + uniqueGeneratedRefId.toUpperCase() + "\n");
        
        return uniqueGeneratedRefId.toUpperCase();
	}
	public String getUniquePaymentTellerId() {

        String uniqueGeneratedRefId = UUID.randomUUID().toString().replace("-", "");
        if (uniqueGeneratedRefId.length() < 16) {
            uniqueGeneratedRefId = uniqueGeneratedRefId + UUID.randomUUID().toString().replace("-", "");
        }
        uniqueGeneratedRefId = uniqueGeneratedRefId.substring(0, 4) + "-" + uniqueGeneratedRefId.substring(4, 9) + "-"
               + uniqueGeneratedRefId.substring(9, 13) + "-" + uniqueGeneratedRefId.substring(13, 18);
        
       return uniqueGeneratedRefId.toUpperCase();
	}
	
	public Session getSession () throws HibernateException, NamingException {
		return HibernateUtils.getSessionFactory().openSession();
	}
	
	public void closeSession (Session session) {
		if (session != null) {
			session.close();
		}
	}



	
	public Boolean getLoggedIn() {
		return loggedIn;
	}



	public void setLoggedIn(Boolean loggedIn) {
		this.loggedIn = loggedIn;
	}



	public void setPortalUser(PortalUser pu) {
		// TODO Auto-generated method stub
		this.pu = pu;
	}
	
	
	public PortalUser getPortalUser() {
		// TODO Auto-generated method stub
		return this.pu;
	}



	public Settings getCurrency() {
		return currency;
	}



	public void setCurrency(Settings currency) {
		this.currency = currency;
	}



	public String getProxyHost() {
		return proxyHost;
	}



	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}



	public String getProxyPort() {
		return proxyPort;
	}



	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}

}
