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
import smartpay.entity.QuantityUnitFactory;
import smartpay.entity.QuantityUnit;
import smartpay.entity.Country;
import smartpay.entity.Currency;
import smartpay.entity.Agency;
import smartpay.entity.enumerations.AgencyType;
import smartpay.entity.enumerations.ApplicantType;
import smartpay.entity.enumerations.RoleConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.service.SwpService;

public class QuantityUnitManagementPortletUtil {

	private static QuantityUnitManagementPortletUtil statePortletUtil = null;
	SwpService swpService = null;
	com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService
			.getInstance();
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	Logger log = Logger.getLogger(QuantityUnitManagementPortletUtil.class);

	public QuantityUnitManagementPortletUtil() {
		swpService = serviceLocator.getSwpService();
	}

	public static QuantityUnitManagementPortletUtil getInstance() {
		if (statePortletUtil == null) {
			QuantityUnitManagementPortletUtil.statePortletUtil = new QuantityUnitManagementPortletUtil();
		}
		return QuantityUnitManagementPortletUtil.statePortletUtil;
	}

	public QuantityUnit updateQuantityUnit(String Id, String name, String unit, String status,
			PortalUser portalUser) {

		QuantityUnit qunit = new QuantityUnit();
		 qunit.setName(name);
		 qunit.setUnit(unit);
		 Boolean statusValue = Boolean.parseBoolean(status);
		 qunit.setStatus(statusValue);
		
		long id = Long.parseLong(Id);
		qunit.setId(id);
//		country.setName(name);
		swpService.updateRecord(qunit);

		return qunit;
	}

	public QuantityUnit createNewQuantityUnit(String name, String unit, String status,
			PortalUser portalUser) {

		QuantityUnit qunit = new QuantityUnit();
		 qunit.setName(name);
		 qunit.setUnit(unit);
		 Boolean statusValue = Boolean.parseBoolean(status);
		 qunit.setStatus(statusValue);
		
		
//		country.setName(name);
	
			swpService.createNewRecord(qunit);

		// trail this activity

		// AuditTrailer at = new AuditTrailer();
		// String action = AuditTrailer.AddActn;
		// String activity = AuditTrailer.New_Agency;
		// at.trailActivity(activity, action, portalUser, swpService);

		return qunit;

	}

	public QuantityUnit getQuantityUnitById(String id) {
		QuantityUnit qunit = null;
		try {
			String hql = "select pu from QuantityUnit pu where "
					+ "lower(pu.id) = lower('" + id + "')";
			log.info("Get hqlType = " + hql);
			qunit = (QuantityUnit) swpService.getUniqueRecordByHQL(hql);
		} catch (HibernateException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		}
		return qunit;
	}

	public void deleteQuantityUnitById(String id) {
		QuantityUnit qunit = getQuantityUnitById(id);
		try {
			swpService.deleteRecord(qunit);
		} catch (HibernateException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		}

	}

	public Collection<QuantityUnit> getAllQuantityUnits() {
		// TODO Auto-generated method stub
		Collection<QuantityUnit> rt = null;

		try {

			String hql = "select rt from QuantityUnit rt";
			log.info("Get hql = " + hql);
			rt = (Collection<QuantityUnit>) swpService.getAllRecordsByHQL(hql);

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
