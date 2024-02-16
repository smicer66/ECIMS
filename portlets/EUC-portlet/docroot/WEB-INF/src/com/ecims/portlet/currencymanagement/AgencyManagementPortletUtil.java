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
import smartpay.entity.Currency;
import smartpay.entity.Agency;
import smartpay.entity.enumerations.AgencyType;
import smartpay.entity.enumerations.RoleConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.service.SwpService;

public class AgencyManagementPortletUtil {

	private static AgencyManagementPortletUtil statePortletUtil = null;
	SwpService swpService = null;
	com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService
			.getInstance();
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	Logger log = Logger.getLogger(AgencyManagementPortletUtil.class);

	public AgencyManagementPortletUtil() {
		swpService = serviceLocator.getSwpService();
	}

	public static AgencyManagementPortletUtil getInstance() {
		if (statePortletUtil == null) {
			AgencyManagementPortletUtil.statePortletUtil = new AgencyManagementPortletUtil();
		}
		return AgencyManagementPortletUtil.statePortletUtil;
	}

	public Agency updateAgency(String Id, String agencyName,
			String contactName, String agencyPhone, String agencyEmail,
			String agencyType, PortalUser portalUser) {

		Agency agency = new Agency();
		agency.setAgencyName(agencyName);
		agency.setContactName(contactName);
		agency.setAgencyPhone(agencyPhone);
		agency.setAgencyEmail(agencyEmail);
		AgencyType agencyTypeValue = AgencyType.fromString(agencyType);
		agency.setAgencyType(agencyTypeValue);
		long id = Long.parseLong(Id);
		agency.setId(id);
		swpService.updateRecord(agency);

		return agency;
	}

	public Agency createNewAgency(String agencyName, String contactName,
			String agencyPhone, String agencyEmail, String agencyType,
			PortalUser portalUser) {

		Agency agency = new Agency();
		agency.setAgencyName(agencyName);
		agency.setContactName(contactName);
		agency.setAgencyPhone(agencyPhone);
		agency.setAgencyEmail(agencyEmail);
		AgencyType agencyTypeValue = AgencyType.fromString(agencyType);
		agency.setAgencyType(agencyTypeValue);

		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		agency.setCreatedDate(ts);
		swpService.createNewRecord(agency);

		// trail this activity

//		AuditTrailer at = new AuditTrailer();
//		String action = AuditTrailer.AddActn;
//		String activity = AuditTrailer.New_Agency;
//		at.trailActivity(activity, action, portalUser, swpService);

		return agency;

	}

	public Agency getAgencyById(String id) {
		Agency agency = null;
		try {
			String hql = "select pu from Agency pu where "
					+ "lower(pu.id) = lower('" + id + "')";
			log.info("Get hqlType = " + hql);
			agency = (Agency) swpService.getUniqueRecordByHQL(hql);
		} catch (HibernateException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		}
		return agency;
	}

	public void deleteAgencyById(String id) {
	Agency agency = getAgencyById(id);
		try {
			swpService.deleteRecord(agency);
		} catch (HibernateException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		}

	}

	public Collection<Agency> getAllAgencies() {
		// TODO Auto-generated method stub
		Collection<Agency> rt = null;

		try {

			String hql = "select rt from Agency rt";
			log.info("Get hql = " + hql);
			rt = (Collection<Agency>) swpService.getAllRecordsByHQL(hql);

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
