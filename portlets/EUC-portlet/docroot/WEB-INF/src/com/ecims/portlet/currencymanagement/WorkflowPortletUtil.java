package com.ecims.portlet.currencymanagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import antlr.collections.List;

import com.google.zxing.common.Collections;
import com.sf.primepay.smartpay13.ServiceLocator;

import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import smartpay.entity.Company;
import smartpay.entity.Country;
import smartpay.entity.PortalUser;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.WokFlowSetting;
import smartpay.entity.Settings;
import smartpay.entity.State;
import smartpay.entity.ItemCategory;
import smartpay.entity.ItemCategorySub;
import smartpay.entity.Country;
import smartpay.entity.Currency;
import smartpay.entity.Agency;
import smartpay.entity.enumerations.AgencyType;
import smartpay.entity.enumerations.ApplicantType;
import smartpay.entity.enumerations.RoleConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.service.SwpService;

public class WorkflowPortletUtil {

	private static WorkflowPortletUtil statePortletUtil = null;
	SwpService swpService = null;
	com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService
			.getInstance();
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	Logger log = Logger.getLogger(WorkflowPortletUtil.class);

	public WorkflowPortletUtil() {
		swpService = serviceLocator.getSwpService();
	}

	public static WorkflowPortletUtil getInstance() {
		if (statePortletUtil == null) {
			WorkflowPortletUtil.statePortletUtil = new WorkflowPortletUtil();
		}
		return WorkflowPortletUtil.statePortletUtil;
	}


	public ItemCategory updateItemCategory(String Id, String name, String applicantType,
			PortalUser portalUser) {

		ItemCategory itemCategory = new ItemCategory();
		itemCategory.setItemCategoryName(name);
		ApplicantType appType = ApplicantType.fromString(applicantType);
		itemCategory.setItemFor(appType);
		long id = Long.parseLong(Id);
		itemCategory.setId(id);
//		country.setName(name);
		swpService.updateRecord(itemCategory);

		return itemCategory;
	}

	public ItemCategory createNewItemCategory( String name, String applicantType,
			PortalUser portalUser) {

		ItemCategory itemCategory = new ItemCategory();
		itemCategory.setItemCategoryName(name);
		ApplicantType appType = ApplicantType.fromString(applicantType);
		itemCategory.setItemFor(appType);
		//		country.setName(name);
		swpService.createNewRecord(itemCategory);

		// trail this activity

		// AuditTrailer at = new AuditTrailer();
		// String action = AuditTrailer.AddActn;
		// String activity = AuditTrailer.New_Agency;
		// at.trailActivity(activity, action, portalUser, swpService);

		return itemCategory;

	}

	public WokFlowSetting createNewWorkflow( String itemCategory, String position, String agencyId,
			PortalUser portalUser) {

		WokFlowSetting wflow = new WokFlowSetting();

		AgencyManagementPortletUtil agencyUtil = new AgencyManagementPortletUtil();
		Agency agency = agencyUtil.getAgencyById(agencyId);
		wflow.setAgency(agency);
		ItemCategoryManagementPortletUtil itemcatUtil = new ItemCategoryManagementPortletUtil();
		ItemCategory itemCategory2 = itemcatUtil.getItemCategoryById(itemCategory);
		wflow.setItemCategory(itemCategory2);
		System.out.println("i am "+position);
		wflow.setPositionId(Integer.parseInt(position));
	
	
		wflow.setStatus(true);
	
			//		country.setName(name);
		swpService.createNewRecord(wflow);

		// trail this activity

		// AuditTrailer at = new AuditTrailer();
		// String action = AuditTrailer.AddActn;
		// String activity = AuditTrailer.New_Agency;
		// at.trailActivity(activity, action, portalUser, swpService);

		return wflow;

	}

	
	public ItemCategory getItemCategoryById(String id) {
		ItemCategory itemCategory = null;
		try {
			String hql = "select pu from ItemCategory pu where "
					+ "lower(pu.id) = lower('" + id + "')";
			log.info("Get hqlType = " + hql);
			itemCategory = (ItemCategory) swpService.getUniqueRecordByHQL(hql);
		} catch (HibernateException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		}
		return itemCategory;
	}
	
	

	public void deleteWokflowByItem(String id) {
		Collection<WokFlowSetting> setting = getWorkflowByItemCategory(id);
		try {
			if(setting!=null && setting.size()>0)
			{
				for(Iterator<WokFlowSetting> t = setting.iterator(); t.hasNext();)
				{
					WokFlowSetting t1 = t.next();
					swpService.deleteRecord(t1);
				}
			}
		} catch (HibernateException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		}

	}

	public void deleteItemCategoryById(String id) {
		ItemCategory itemCategory = getItemCategoryById(id);
		try {
			swpService.deleteRecord(itemCategory);
		} catch (HibernateException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		}

	}

	
	public Collection<ItemCategory> getAllItemCategories() {
		// TODO Auto-generated method stub
		Collection<ItemCategory> rt = null;

		try {

			String hql = "select rt from ItemCategory rt";
			log.info("Get hql = " + hql);
			rt = (Collection<ItemCategory>) swpService.getAllRecordsByHQL(hql);

		} catch (HibernateException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		} finally {

		}
		return rt;
	}

	public Collection<WokFlowSetting> getWorkflowByItemCategory(String itemCategory) {
		Collection<WokFlowSetting> rt = null;
		try {
			String hql = "select apc from WokFlowSetting apc where " +
					"lower(apc.itemCategory) = '" + itemCategory + "' ";
				log.info("Get hqlType = " + hql);
			rt = (Collection<WokFlowSetting>) swpService.getAllRecordsByHQL(hql);
		} catch (HibernateException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		}
		return rt;
	}
	
	public WokFlowSetting getWorkflowByItemId(String itemCategory) {
		WokFlowSetting rt = null;
		try {
			String hql = "select apc from WokFlowSetting apc where " +
					"lower(apc.itemCategory) = '" + itemCategory + "' ";
				log.info("Get hqlType = " + hql);
			rt = (WokFlowSetting) swpService.getUniqueRecordByHQL(hql);
		} catch (HibernateException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		}
		return rt;
	}
	public PortalUser getPortalUserById(String id) {
		PortalUser portalUser = null;
		try {
			String hql = "select pu from PortalUser pu where "
					+ "lower(pu.id) = lower('" + id + "')";
			log.info("Get hqlType = " + hql);
			portalUser = (PortalUser) swpService.getUniqueRecordByHQL(hql);
		} catch (HibernateException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		}
		return portalUser;
	}

	public Collection<State> getAllStates() {
		// TODO Auto-generated method stub
		Collection<State> rt = null;

		try {

			String hql = "select rt from State rt";
			log.info("Get hql = " + hql);
			rt = (Collection<State>) swpService.getAllRecordsByHQL(hql);

		} catch (HibernateException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		} finally {

		}
		return rt;
	}

	public Settings getSettingsByName(String settingsName) {
		// TODO Auto-generated method stub
		Settings rt = null;

		try {

			String hql = "select rt from Settings rt where lower(rt.name) = lower('"
					+ settingsName + "')";
			log.info("Get hql = " + hql);
			rt = (Settings) swpService.getUniqueRecordByHQL(hql);

		} catch (HibernateException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		} finally {

		}
		return rt;
	}

}
