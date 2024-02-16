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

public class ItemCategorySubManagementPortletUtil {

	private static ItemCategorySubManagementPortletUtil statePortletUtil = null;
	SwpService swpService = null;
	com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService
			.getInstance();
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	Logger log = Logger.getLogger(ItemCategorySubManagementPortletUtil.class);

	public ItemCategorySubManagementPortletUtil() {
		swpService = serviceLocator.getSwpService();
	}

	public static ItemCategorySubManagementPortletUtil getInstance() {
		if (statePortletUtil == null) {
			ItemCategorySubManagementPortletUtil.statePortletUtil = new ItemCategorySubManagementPortletUtil();
		}
		return ItemCategorySubManagementPortletUtil.statePortletUtil;
	}

	public ItemCategorySub updateItemCategorySub(String Id, String name, String hsCode, String itemCategory,
			PortalUser portalUser) {

		ItemCategorySub itemCategorySub = new ItemCategorySub();
ItemCategoryManagementPortletUtil itemCatUtil = new ItemCategoryManagementPortletUtil();
    ItemCategory itemCatObj = itemCatUtil.getItemCategoryById(itemCategory);
		itemCategorySub.setHsCode(hsCode);
		itemCategorySub.setItemCategory(itemCatObj);
		itemCategorySub.setName(name);
	  long id = Long.parseLong(Id);
		itemCategorySub.setId(id);
//		country.setName(name);
		swpService.updateRecord(itemCategorySub);

		return itemCategorySub;
	}

	public ItemCategorySub createNewItemCategorySub( String name, String hsCode, String itemCategory,
			PortalUser portalUser) {

		ItemCategorySub itemCategorySub = new ItemCategorySub();
ItemCategoryManagementPortletUtil itemCatUtil = new ItemCategoryManagementPortletUtil();
    ItemCategory itemCatObj = itemCatUtil.getItemCategoryById(itemCategory);
		itemCategorySub.setHsCode(hsCode);
		itemCategorySub.setItemCategory(itemCatObj);
		itemCategorySub.setName(name);
		swpService.createNewRecord(itemCategorySub);

		// trail this activity

		// AuditTrailer at = new AuditTrailer();
		// String action = AuditTrailer.AddActn;
		// String activity = AuditTrailer.New_Agency;
		// at.trailActivity(activity, action, portalUser, swpService);

		return itemCategorySub;

	}

	public ItemCategorySub getItemCategorySubById(String id) {
		ItemCategorySub itemCategorySub = null;
		try {
			String hql = "select pu from ItemCategorySub pu where "
					+ "lower(pu.id) = lower('" + id + "')";
			log.info("Get hqlType = " + hql);
			itemCategorySub = (ItemCategorySub) swpService.getUniqueRecordByHQL(hql);
		} catch (HibernateException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		}
		return itemCategorySub;
	}

	public void deleteItemCategorySubById(String id) {
		ItemCategorySub itemCategorySub = getItemCategorySubById(id);
		try {
			swpService.deleteRecord(itemCategorySub);
		} catch (HibernateException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		}

	}

	public Collection<ItemCategorySub> getAllItemSubCategories() {
		// TODO Auto-generated method stub
		Collection<ItemCategorySub> rt = null;

		try {

			String hql = "select rt from ItemCategorySub rt";
			log.info("Get hql = " + hql);
			rt = (Collection<ItemCategorySub>) swpService.getAllRecordsByHQL(hql);

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
