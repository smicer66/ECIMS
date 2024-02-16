package com.ecims.portlet.usermanagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import antlr.collections.List;

import com.google.zxing.common.Collections;
import com.sf.primepay.smartpay13.ServiceLocator;

import smartpay.entity.Agency;
import smartpay.entity.Applicant;
import smartpay.entity.Company;
import smartpay.entity.Country;
import smartpay.entity.Permission;
import smartpay.entity.PortalUser;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.State;
import smartpay.entity.enumerations.AgencyType;
import smartpay.entity.enumerations.ApplicantStatus;
import smartpay.entity.enumerations.PermissionType;
import smartpay.entity.enumerations.RoleConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.entity.enumerations.UserStatus;
import smartpay.service.SwpService;

public class UserManagementPortletUtil {

	
	private static UserManagementPortletUtil adminPortletUtil=null;
	SwpService swpService = null;
	com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	Logger log = Logger.getLogger(UserManagementPortletUtil.class);
	
	public UserManagementPortletUtil() {
		swpService = serviceLocator.getSwpService();
	}
	
	public static UserManagementPortletUtil getInstance()
	{
		if(adminPortletUtil==null)
		{
			UserManagementPortletUtil.adminPortletUtil = new UserManagementPortletUtil();
		}
		return UserManagementPortletUtil.adminPortletUtil;
	}
	
	public Country getCountryByState(State state)
	{
		Country country = null;
		try {
			String hql = "select pu.country from State pu where " +
					"pu.id = " + state.getId();
			log.info("Get hqlType = " + hql);
			country = (Country) swpService.getUniqueRecordByHQL(hql);
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} 
		return country;
	}
	

	
	public Company getCompanyByPortalUser(PortalUser pu)
	{
		Company company = null;
		try {
			String hql = "select pu.company from PortalUser pu where " +
					"pu.id = " + pu.getId();
			log.info("Get hqlType = " + hql);
			company = (Company) swpService.getUniqueRecordByHQL(hql);
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} 
		return company;
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

	public Collection<PortalUser> getAllPortalUsers(UserManagementPortletState portletState) {
		// TODO Auto-generated method stub
		Collection<PortalUser> rt = null;
		
		try {
			String hql = null;
			if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getName().getValue().equals(RoleTypeConstants.ROLE_SYSTEM_ADMIN.getValue()))
			{
				hql = "select rt from PortalUser rt WHERE (lower(rt.roleType.name) = lower('" + RoleTypeConstants.ROLE_ACCREDITOR_USER.getValue() + "') OR " +
						"lower(rt.roleType.name) = lower('" + RoleTypeConstants.ROLE_END_USER.getValue() + "') OR " +
						"lower(rt.roleType.name) = lower('" + RoleTypeConstants.ROLE_EXCLUSIVE_USER.getValue() + "') OR " +
						"lower(rt.roleType.name) = lower('" + RoleTypeConstants.ROLE_INFORMATION_USER.getValue() + "') OR " +
						"lower(rt.roleType.name) = lower('" + RoleTypeConstants.ROLE_NSA_ADMIN.getValue() + "') OR " +
						"lower(rt.roleType.name) = lower('" + RoleTypeConstants.ROLE_NSA_USER.getValue() + "') OR " +
						"lower(rt.roleType.name) = lower('" + RoleTypeConstants.ROLE_REGULATOR_USER.getValue() + "') OR " +
						"lower(rt.roleType.name) = lower('" + RoleTypeConstants.ROLE_SYSTEM_ADMIN.getValue() + "')) ORDER BY " +
						"rt.dateCreated DESC";
			}else if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().getName().getValue().equals(RoleTypeConstants.ROLE_NSA_ADMIN.getValue()))
			{
				hql = "select rt from PortalUser rt WHERE (lower(rt.roleType.name) = lower('" + RoleTypeConstants.ROLE_ACCREDITOR_USER.getValue() + "') OR " +
						"lower(rt.roleType.name) = lower('" + RoleTypeConstants.ROLE_END_USER.getValue() + "') OR " +
						"lower(rt.roleType.name) = lower('" + RoleTypeConstants.ROLE_EXCLUSIVE_USER.getValue() + "') OR " +
						"lower(rt.roleType.name) = lower('" + RoleTypeConstants.ROLE_INFORMATION_USER.getValue() + "') OR " +
						"lower(rt.roleType.name) = lower('" + RoleTypeConstants.ROLE_NSA_ADMIN.getValue() + "') OR " +
						"lower(rt.roleType.name) = lower('" + RoleTypeConstants.ROLE_NSA_USER.getValue() + "') OR " +
						"lower(rt.roleType.name) = lower('" + RoleTypeConstants.ROLE_REGULATOR_USER.getValue() + "')) ORDER BY " +
						"rt.dateCreated DESC";
			}
			else
			{
				log.info("Confused, dont know what type ofuser this is");
			}
			log.info("user Role Type === " + (portletState.getPortalUser()!=null ? portletState.getPortalUser().getRoleType().getName() : "NULL"));
			
			if(hql!=null)
			{
				log.info("Get hql = " + hql);
				rt = (Collection<PortalUser>) swpService.getAllRecordsByHQL(hql);
				log.info("rt===" + (rt!=null ? rt.size() : "NA"));
			}
			
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

	public Collection<Country> getAllCountries() {
		// TODO Auto-generated method stub
		Collection<Country> rt = null;
		
		try {
			
				String hql = "select rt from Country rt";
				log.info("Get hql = " + hql);
				rt = (Collection<Country>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
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

	public Applicant getApplicantOfPortalUser(PortalUser pu)
	{
		Applicant rt = null;
		
		try {
			
				String hql = "select rt from Applicant rt where lower(rt.portalUser.id) = " + pu.getId();
				log.info("Get hql = " + hql);
				rt = (Applicant) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	
	public Collection<PortalUser> getPortalUserByStatus(UserStatus userStatus) {
		// TODO Auto-generated method stub
		Collection<PortalUser> rt = null;
		
		try {
			
				String hql = "select rt from PortalUser rt where lower(rt.status) = lower('" + userStatus.getValue() +  "')  ORDER BY " +
						"rt.dateCreated DESC";
				log.info("Get hql = " + hql);
				rt = (Collection<PortalUser>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<Applicant> getAllApplicant() {
		// TODO Auto-generated method stub
		Collection<Applicant> rt = null;
		
		try {
			
				String hql = "select rt from Applicant rt ORDER BY " +
						"rt.portalUser.dateCreated DESC";
				log.info("Get hql = " + hql);
				rt = (Collection<Applicant>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	public PortalUser getPortalUserByApplicant(Applicant applicant) {
		// TODO Auto-generated method stub
		PortalUser rt = null;
		
		try {
			
				String hql = "select rt.portalUser from Applicant rt where rt.id = " + applicant.getId();
				log.info("Get hql = " + hql);
				rt = (PortalUser) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	
	public Collection<Applicant> getApplicantByStatus(ApplicantStatus applicantStatus) {
		// TODO Auto-generated method stub
		Collection<Applicant> rt = null;
		
		try {
			
				String hql = "select rt from Applicant rt where lower(rt.status) = lower('" + applicantStatus.getValue() +  "') ORDER BY " +
						"rt.portalUser.dateCreated DESC";
				log.info("Get hql = " + hql);
				rt = (Collection<Applicant>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<Agency> getAllAgency() {
		// TODO Auto-generated method stub
		Collection<Agency> rt = null;
		
		try {
			
				String hql = "select rt from Agency rt";
				log.info("Get hql = " + hql);
				rt = (Collection<Agency>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<Agency> getAllAgencyByAgencyType(
			AgencyType agencyType) {
		// TODO Auto-generated method stub
		Collection<Agency> col = null;
		
		try {
			
			String hql = "select rt from Agency rt where lower(rt.agencyType) = lower('" + agencyType.getValue() + "')";
			log.info("Get hql = " + hql);
			col = (Collection<Agency>) swpService.getAllRecordsByHQL(hql);
			if(col!=null)
				log.info("col size = " + col.size());
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return col;
	}

	public Agency getAgencyByType(AgencyType sg, String name) {
		// TODO Auto-generated method stub
		Agency col = null;
		
		try {
			
			String hql = "select rt from Agency rt where lower(rt.agencyType) = lower('" + sg.getValue() + "') " +
					"AND lower(rt.agencyName) = lower('"+name+"')";
			log.info("Get hql = " + hql);
			col = (Agency) swpService.getUniqueRecordByHQL(hql);
			if(col!=null)
				log.info("agency is null= ");
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return col;
	}

	public Collection<Permission> getPortalUserPermissions(PortalUser pu) {
		// TODO Auto-generated method stub
		Collection<Permission> col = null;
		
		try {
			
			String hql = "select rt from Permission rt where rt.portalUser.id = " + pu.getId();
			log.info("Get hql = " + hql);
			col = (Collection<Permission>) swpService.getAllRecordsByHQL(hql);
			if(col!=null)
				log.info("col size = " + col.size());
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return col;
	}
	
	
	public Collection<PermissionType> getPortalUserPermissionType(PortalUser pu) {
		// TODO Auto-generated method stub
		Collection<PermissionType> col = null;
		
		try {
			
			String hql = "select rt.permissionType from Permission rt where rt.portalUser.id = " + pu.getId();
			log.info("Get hql = " + hql);
			col = (Collection<PermissionType>) swpService.getAllRecordsByHQL(hql);
			if(col!=null)
				log.info("col size = " + col.size());
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return col;
	}

	
	

}
