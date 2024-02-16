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
import smartpay.entity.Country;

import smartpay.entity.Currency;
import smartpay.entity.Agency;
import smartpay.entity.enumerations.AgencyType;
import smartpay.entity.enumerations.RoleConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.service.SwpService;

public class StateManagementPortletUtil {

	private static StateManagementPortletUtil statePortletUtil = null;
	SwpService swpService = null;
	com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService
			.getInstance();
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	Logger log = Logger.getLogger(StateManagementPortletUtil.class);

	public StateManagementPortletUtil() {
		swpService = serviceLocator.getSwpService();
	}

	public static StateManagementPortletUtil getInstance() {
		if (statePortletUtil == null) {
			StateManagementPortletUtil.statePortletUtil = new StateManagementPortletUtil();
		}
		return StateManagementPortletUtil.statePortletUtil;
	}

	public State updateState(String Id, String name, String countryId,
			PortalUser portalUser) {

		State state = new State();
		CountryManagementPortletUtil countryUtil =  new CountryManagementPortletUtil();
		  Country country = countryUtil.getCountryById(countryId);
		state.setName(name);
		state.setCountry(country);
		long id = Long.parseLong(Id);
		state.setId(id);
		swpService.updateRecord(state);

		return state;
	}

	public State createNewState(String name, String countryId,
			PortalUser portalUser) {

		State state = new State();
		CountryManagementPortletUtil countryUtil =  new CountryManagementPortletUtil();
		  Country country = countryUtil.getCountryById(countryId);
		state.setName(name);
		state.setCountry(country);
		swpService.createNewRecord(state);
		// trail this activity

		// AuditTrailer at = new AuditTrailer();
		// String action = AuditTrailer.AddActn;
		// String activity = AuditTrailer.New_Agency;
		// at.trailActivity(activity, action, portalUser, swpService);

		return state;

	}

	public State getStateById(String id) {
		State state = null;
		try {
			String hql = "select pu from State pu where "
					+ "lower(pu.id) = lower('" + id + "')";
			log.info("Get hqlType = " + hql);
			state = (State) swpService.getUniqueRecordByHQL(hql);
		} catch (HibernateException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		}
		return state;
	}

	public void deleteStateById(String id) {
		State state = getStateById(id);
		try {
			swpService.deleteRecord(state);
		} catch (HibernateException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		}

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
