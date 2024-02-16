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

public class ItemCategoryManagementPortletUtil {

	private static ItemCategoryManagementPortletUtil statePortletUtil = null;
	SwpService swpService = null;
	com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService
			.getInstance();
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	Logger log = Logger.getLogger(ItemCategoryManagementPortletUtil.class);

	public ItemCategoryManagementPortletUtil() {
		swpService = serviceLocator.getSwpService();
	}

	public static ItemCategoryManagementPortletUtil getInstance() {
		if (statePortletUtil == null) {
			ItemCategoryManagementPortletUtil.statePortletUtil = new ItemCategoryManagementPortletUtil();
		}
		return ItemCategoryManagementPortletUtil.statePortletUtil;
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
