package com.ecims.portlet.reportportlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;


import smartpay.entity.Application;
import smartpay.entity.Company;
import smartpay.entity.PortalUser;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.service.SwpService;

import com.sf.primepay.smartpay13.ServiceLocator;

public class ReportPortletUtil {

	private static ReportPortletUtil approvalFlowPortletUtil=null;
	SwpService swpService = null;
	com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	Logger log = Logger.getLogger(ReportPortletUtil.class);
	
	public ReportPortletUtil() {
		swpService = serviceLocator.getSwpService();
	}
	
	public static ReportPortletUtil getInstance()
	{
		if(approvalFlowPortletUtil==null)
		{
			ReportPortletUtil.approvalFlowPortletUtil = new ReportPortletUtil();
		}
		return ReportPortletUtil.approvalFlowPortletUtil;
	}

	public ArrayList<RoleType> getRoleTypeByPortalUser(PortalUser portalUser) {
		// TODO Auto-generated method stub
		Collection<RoleType> roles = null;
		try {
			String hql = "select pu.roleType from PortalUser pu where " +
					"pu.id = " + portalUser.getId();
			log.info("Get hqlType = " + hql);
			roles = (Collection<RoleType>) swpService.getAllRecordsByHQL(hql);
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		if(roles!=null && roles.size()>0)
		{
			ArrayList<RoleType> roleList = new ArrayList<RoleType>();
			for(Iterator<RoleType> roleIter = roles.iterator(); roleIter.hasNext();)
			{
				roleList.add(roleIter.next());
			}
			return roleList;
		}else
		{
			return null;
		}
	}
	
	public Object getEntityObjectById(Class claz, Long id)
	{
		Object object = null;
		try {
			String hql = "select apc from " + claz.getSimpleName() + " apc where " +
					"apc.id = " + id;
			log.info("Get hql = " + hql);
			object = swpService.getRecordById(claz, id);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return object;
	}
	

	public PortalUser getPortalUserByEmailAddress(
			String emailAddress) {
		// TODO Auto-generated method stub
		PortalUser fd = null;
		
		try {
			
				String hql = "select pu from PortalUser pu where (" +
						"pu.emailAddress = '" + emailAddress + "')";
				log.info("Get hql = " + hql);
				fd = (PortalUser) swpService.getUniqueRecordByHQL(hql);
				log.info("fd===" + (fd!=null ? fd.getEmailAddress() : "NA"));
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return fd;
	}

	public Object runReportHQL(String hql)
	{
		Object rt = null;
		
		try {
			
				log.info("Get hql = " + hql);
				rt =  swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	public Object getAllEntityObjects(
			Class claz) {
		// TODO Auto-generated method stub
		Object object = null;
		try {
			log.info("Select rt from " + claz.getSimpleName());
			object = swpService.getAllRecords(claz);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return object;
	}
	
}
