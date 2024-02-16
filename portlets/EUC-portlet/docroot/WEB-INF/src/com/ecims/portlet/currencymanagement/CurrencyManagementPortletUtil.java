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

import smartpay.entity.Company;
import smartpay.entity.Country;
import smartpay.entity.PortalUser;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.State;
import smartpay.entity.Currency;
import smartpay.entity.enumerations.RoleConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.service.SwpService;

public class CurrencyManagementPortletUtil {

	
	private static CurrencyManagementPortletUtil statePortletUtil=null;
	SwpService swpService = null;
	com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	Logger log = Logger.getLogger(CurrencyManagementPortletUtil.class);
	
	public CurrencyManagementPortletUtil() {
		swpService = serviceLocator.getSwpService();
	}
	
	public static CurrencyManagementPortletUtil getInstance()
	{
		if(statePortletUtil==null)
		{
			CurrencyManagementPortletUtil.statePortletUtil = new CurrencyManagementPortletUtil();
		}
		return CurrencyManagementPortletUtil.statePortletUtil;
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

	
	public PortalUser getPortalUserByPassCode(String code)
	{
		PortalUser portalUser = null;
		try {
			String hql = "select pu from PortalUser pu where " +
					"lower(pu.passcode) = lower('" + code + "')";
			log.info("Get hqlType = " + hql);
			portalUser = (PortalUser) swpService.getUniqueRecordByHQL(hql);
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} 
		return portalUser;
	}
	
	public Company getCompanyById(Long id)
	{
		Company company = null;
		try {
			String hql = "select apc from Company apc where " +
					"apc.id = " + id;
			log.info("Get hql = " + hql);
			company = (Company) swpService.getUniqueRecordByHQL(hql);
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return company;
	}
	
	
	public Object getEntityObjectById(Class claz, Long id)
	{
		Object object = null;
		try {
			log.info("Select rt from " + claz.getSimpleName() + " rt where rt.id = " + id);
			object = swpService.getRecordById(claz, id);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return object;
	}
	
	
	

	public Collection<Company> getCompanyByNameOrRCNumber(String companyname,
			String companyrcnumber) {
		// TODO Auto-generated method stub
		Collection<Company> companyList = null;
		try {
			String hql = "select apc from Company apc where " +
					"lower(apc.companyName) = '" + companyname.toLowerCase() + "' " +
							"OR lower(apc.companyRCNumber) = '" + companyrcnumber.toLowerCase() + "'";
			log.info("Get hql = " + hql);
			companyList = (Collection<Company>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return companyList;
	}
	
	
	public Collection<Company> getCompanyByNameOrRCNumberForEdit(String companyname,
			String companyrcnumber, Long companyId) {
		// TODO Auto-generated method stub
		Collection<Company> companyList = null;
		try {
			String hql = "select apc from Company apc where " +
					"(lower(apc.companyName) = '" + companyname.toLowerCase() + "' " +
							"OR lower(apc.companyRCNumber) = '" + companyrcnumber.toLowerCase() + "') AND apc.id != " + companyId;
			log.info("Get hql = " + hql);
			companyList = (Collection<Company>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return companyList;
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
	
	public PortalUser getPortalUserByEmailAddressAndNotUserId(
			String emailAddress, Long id) {
		// TODO Auto-generated method stub
		PortalUser fd = null;
		
		try {
			
				String hql = "select pu from PortalUser pu where (" +
						"pu.emailAddress = '" + emailAddress + "' AND pu.id != " + id + ")";
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

	public RoleType getRoleTypeByRoleTypeName(
			RoleTypeConstants roleType) {
		// TODO Auto-generated method stub
		RoleType rt = null;
		
		try {
			
				String hql = "select rt from RoleType rt where (" +
						"lower(rt.name) = lower('" + roleType.getValue() + "'))";
				log.info("Get hql = " + hql);
				rt = (RoleType) swpService.getUniqueRecordByHQL(hql);
				log.info("rt===" + (rt!=null ? rt.getId() : "NA"));
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<PortalUser> getAllPortalUserByCompany(Company company) {
		// TODO Auto-generated method stub
		Collection<PortalUser> rt = null;
		
		try {
			
				String hql = "select rt from PortalUser rt where (" +
						"rt.company.id = " + company.getId() + ")";
				log.info("Get hql = " + hql);
				rt = (Collection<PortalUser>) swpService.getAllRecordsByHQL(hql);
				log.info("rt===" + (rt!=null ? rt.size() : "NA"));
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<Integer> getAuthorizedPanelCombinationByCompanyAndMapPanel(Long id,
			Long mapPanelId) {
		// TODO Auto-generated method stub
		Collection<Integer> rt = null;
		
		try {
			
				String hql = "select rt.position from AuthorizePanelCombination rt where (" +
						"rt.company.id = " + id + " AND rt.authorizePanel.id = " + mapPanelId +")";
				log.info("Get hql = " + hql);
				rt = (Collection<Integer>) swpService.getAllRecordsByHQL(hql);
				log.info("rt===" + (rt!=null ? rt.size() : "NA"));
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<RoleType> getAllRoleTypes() {
		// TODO Auto-generated method stub
		Collection<RoleType> rt = null;
		
		try {
			
				String hql = "select rt from RoleType rt where (" +
						"rt.status = 'true')";
				log.info("Get hql = " + hql);
				rt = (Collection<RoleType>) swpService.getAllRecordsByHQL(hql);
				log.info("rt===" + (rt!=null ? rt.size() : "NA"));
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	
	public Collection<PortalUser> getPortalUserByRoleType(
			RoleTypeConstants roleType) {
		// TODO Auto-generated method stub
		Collection<PortalUser> rt = null;
		
		try {
			
				String hql = "select rt from PortalUser rt where lower(rt.roleType.name) = lower('" + roleType.getValue() + "')";
				log.info("Get hql = " + hql);
				rt = (Collection<PortalUser>) swpService.getAllRecordsByHQL(hql);
				log.info("rt===" + (rt!=null ? rt.size() : "NA"));
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public State getStateByName(String state) {
		// TODO Auto-generated method stub
		State rt = null;
		
		try {
			
				String hql = "select rt from State rt where lower(rt.name) = lower('" + state + "')";
				log.info("Get hql = " + hql);
				rt = (State) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	 public Currency updateCurrency(String Id, String htmlEntity, String name, boolean valid,  PortalUser portalUser) {

	        Currency currency = new Currency();
	        currency.setHtmlEntity(htmlEntity);
	        currency.setName(name);
	        currency.setValid(valid);
	        long id = Long.parseLong(Id);
	        currency.setId(id);
	        swpService.updateRecord(currency);
	        
	        return currency;
	 }
	 
	  public Currency createNewCurrency(String htmlEntity, String name, boolean valid,  PortalUser portalUser) {

	        Currency currency = new Currency();
	        currency.setHtmlEntity(htmlEntity);
	        currency.setName(name);
	        currency.setValid(valid);
	        swpService.createNewRecord(currency);

	        //trail this activity
	      
	        AuditTrailer at = new AuditTrailer();
	        String action = AuditTrailer.AddActn;
	        String activity = AuditTrailer.New_Currency;
	        at.trailActivity(activity, action, portalUser, swpService);

	        return currency;

	    }
	  
	  public Currency getCurrencyById(String id)
		{
			Currency currency = null;
			try {
				String hql = "select pu from Currency pu where " +
						"lower(pu.id) = lower('" + id + "')";
				log.info("Get hqlType = " + hql);
				currency = (Currency) swpService.getUniqueRecordByHQL(hql);
			} catch (HibernateException e) {
				log.error("",e);
			} catch (Exception e) {
				log.error("",e);
			} 
			return currency;
		}
		
	  public void deleteCurrencyById(String id)
		{
			Currency currency = getCurrencyById(id);
			try {
			 swpService.deleteRecord(currency);
			} catch (HibernateException e) {
				log.error("",e);
			} catch (Exception e) {
				log.error("",e);
			} 
			
		}
	  
	public Collection<Currency> getAllCurrencies() {
		// TODO Auto-generated method stub
		Collection<Currency> rt = null;
		
		try {
			
				String hql = "select rt from Currency rt";
				log.info("Get hql = " + hql);
				rt = (Collection<Currency>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	public PortalUser getPortalUserById(String id)
	{
		PortalUser portalUser = null;
		try {
			String hql = "select pu from PortalUser pu where " +
					"lower(pu.id) = lower('" + id + "')";
			log.info("Get hqlType = " + hql);
			portalUser = (PortalUser) swpService.getUniqueRecordByHQL(hql);
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
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
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Settings getSettingsByName(String settingsName) {
		// TODO Auto-generated method stub
		Settings rt = null;
		
		try {
			
				String hql = "select rt from Settings rt where lower(rt.name) = lower('" + settingsName +  "')";
				log.info("Get hql = " + hql);
				rt = (Settings) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	
	

}
