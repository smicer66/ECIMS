package com.ecims.portlet.certificatemanagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

import antlr.collections.List;

import com.google.zxing.common.Collections;
import com.sf.primepay.smartpay13.ServiceLocator;

import smartpay.entity.Agency;
import smartpay.entity.Applicant;
import smartpay.entity.Application;
import smartpay.entity.ApplicationAttachment;
import smartpay.entity.ApplicationAttachmentType;
import smartpay.entity.ApplicationFlag;
import smartpay.entity.ApplicationItem;
import smartpay.entity.ApplicationWorkflow;
import smartpay.entity.Certificate;
import smartpay.entity.Company;
import smartpay.entity.Country;
import smartpay.entity.Dispute;
import smartpay.entity.ItemCategory;
import smartpay.entity.ItemCategorySub;
import smartpay.entity.PortalUser;
import smartpay.entity.QuantityUnit;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.State;
import smartpay.entity.enumerations.AgencyType;
import smartpay.entity.enumerations.ApplicantStatus;
import smartpay.entity.enumerations.ApplicationStatus;
import smartpay.entity.enumerations.CertificateStatus;
import smartpay.entity.enumerations.DisputeType;
import smartpay.entity.enumerations.PermissionType;
import smartpay.entity.enumerations.RoleConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.entity.enumerations.UserStatus;
import smartpay.service.SwpService;

public class CertificatePortletUtil {

	
	private static CertificatePortletUtil adminPortletUtil=null;
	SwpService swpService = null;
	com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	Logger log = Logger.getLogger(CertificatePortletUtil.class);
	
	public CertificatePortletUtil() {
		swpService = serviceLocator.getSwpService();
	}
	
	public static CertificatePortletUtil getInstance()
	{
		if(adminPortletUtil==null)
		{
			CertificatePortletUtil.adminPortletUtil = new CertificatePortletUtil();
		}
		return CertificatePortletUtil.adminPortletUtil;
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
	
	
	public Collection<PortalUser> getPortalUserByPermission(
			PermissionType pm) {
		// TODO Auto-generated method stub
		Collection<PortalUser> rt = null;
		try {
			
				String hql = "select rt.portalUser from Permission rt where lower(rt.permissionType) = " +
						"lower('" + pm.getValue() +  "')";
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
	
	
	public Collection<Certificate> getCertificatesByPortalUser(PortalUser pu){
		Collection<Certificate> certList = null;
		try {
			String hql = "select apc from Certificate apc where " +
					"apc.application.applicant.portalUser.id = " + pu.getId();
			log.info("Get hql = " + hql);
			certList = (Collection<Certificate>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return certList;
	}
	
	
	public Collection<Certificate> getCertificatesByAgency(PortalUser pu){
		Collection<Certificate> certList = null;
		if(pu.getAgency()!=null)
		{
			try {
				String hql = "select apc from Certificate apc where " +
						"apc.application.portalUser.agency.id = " + pu.getAgency().getId();
				log.info("Get hql = " + hql);
				certList = (Collection<Certificate>) swpService.getAllRecordsByHQL(hql);
				
			} catch (HibernateException e) {
				log.error("",e);
			} catch (Exception e) {
				log.error("",e);
			} finally {
				
			}
		}
		return certList;
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

	public Collection<PortalUser> getAllPortalUsers(CertificatePortletState portletState) {
		// TODO Auto-generated method stub
		Collection<PortalUser> rt = null;
		
		try {
			String hql = null;
			if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().equals(RoleTypeConstants.ROLE_SYSTEM_ADMIN))
			{
				hql = "select rt from PortalUser rt WHERE (lower(rt.role.name) = lower('" + RoleTypeConstants.ROLE_ACCREDITOR_USER.getValue() + "') OR " +
						"lower(rt.role.name) = lower('" + RoleTypeConstants.ROLE_END_USER.getValue() + "') OR " +
						"lower(rt.role.name) = lower('" + RoleTypeConstants.ROLE_INFORMATION_USER.getValue() + "') OR " +
						"lower(rt.role.name) = lower('" + RoleTypeConstants.ROLE_NSA_ADMIN.getValue() + "') OR " +
						"(lower(rt.role.name) = lower('" + RoleTypeConstants.ROLE_NSA_USER.getValue() + "') OR " +
						"lower(rt.role.name) = lower('" + RoleTypeConstants.ROLE_REGULATOR_USER.getValue() + "') OR " +
								"lower(rt.role.name) = lower('" + RoleTypeConstants.ROLE_SYSTEM_ADMIN.getValue() + "'))";
			}else if(portletState.getPortalUser()!=null && portletState.getPortalUser().getRoleType().equals(RoleTypeConstants.ROLE_NSA_ADMIN))
			{
				hql = "select rt from PortalUser rt WHERE (lower(rt.role.name) = lower('" + RoleTypeConstants.ROLE_ACCREDITOR_USER.getValue() + "') OR " +
						"lower(rt.role.name) = lower('" + RoleTypeConstants.ROLE_END_USER.getValue() + "') OR " +
						"lower(rt.role.name) = lower('" + RoleTypeConstants.ROLE_INFORMATION_USER.getValue() + "') OR " +
						"lower(rt.role.name) = lower('" + RoleTypeConstants.ROLE_NSA_ADMIN.getValue() + "') OR " +
						"(lower(rt.role.name) = lower('" + RoleTypeConstants.ROLE_NSA_USER.getValue() + "') OR " +
						"lower(rt.role.name) = lower('" + RoleTypeConstants.ROLE_REGULATOR_USER.getValue() + "'))";
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
			
				String hql = "select rt from PortalUser rt where lower(rt.status) = lower('" + userStatus.getValue() +  "')";
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
			
				String hql = "select rt from Applicant rt";
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
			
				String hql = "select rt from Applicant rt where lower(rt.status) = lower('" + applicantStatus.getValue() +  "')";
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

	public ApplicationAttachmentType getApplicationAttachmentByName(String value) {
		// TODO Auto-generated method stub
		ApplicationAttachmentType rt = null;
		
		try {
			
				String hql = "select rt from ApplicationAttachmentType rt where lower(rt.name) = lower('" + value + "')";
				log.info("Get hql = " + hql);
				rt = (ApplicationAttachmentType) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<ItemCategorySub> getItemCategorySubByItemCategory(
			ItemCategory itemCategoryEntity) {
		// TODO Auto-generated method stub
		Collection<ItemCategorySub> rt = null;
		
		try {
			
				String hql = "select rt from ItemCategorySub rt where rt.itemCategory.id = " + itemCategoryEntity.getId();
				log.info("Get hql = " + hql);
				rt = (Collection<ItemCategorySub>) swpService.getAllRecordsByHQL(hql);
			
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

	public Collection<Application> getApplicationsByApplicant(Applicant applicant) {
		// TODO Auto-generated method stub
		Collection<Application> rt = null;
		
		try {
			
				String hql = "select rt from Application rt where (rt.applicant.id) = " + applicant.getId();
				log.info("Get hql = " + hql);
				rt = (Collection<Application>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<Application> getApplicationsByApplicantAndStatus(Applicant applicant,
			ApplicationStatus appStatus) {
		// TODO Auto-generated method stub
		Collection<Application> rt = null;
		
		try {
			
				String hql = "select rt from Application rt where (rt.applicant.id) = " + applicant.getId() + " AND lower(rt.status) = lower('" + appStatus.getValue() + "')";
				log.info("Get hql = " + hql);
				rt = (Collection<Application>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<Application> getApplicationsByStatus(
			ApplicationStatus appStatus) {
		// TODO Auto-generated method stub
		Collection<Application> rt = null;
		
		try {
			
				String hql = "select rt from Application rt where lower(rt.status) = lower('" + appStatus.getValue() + "')";
				log.info("Get hql = " + hql);
				rt = (Collection<Application>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<Application> getApplicationsByApplicantAndExceptStatus(
			Applicant applicant, String[] status) {
		// TODO Auto-generated method stub
		String except = null;
		if(status.length>0)
		{
			except += "(";
			for(int c=0; c<status.length; c++)
			{
				except += " lower(rt.status) != lower('"+ status[c] +"')";
				if(c!=(status.length-1))
					except += " AND ";
			}
			except += ")";
			
			Collection<Application> rt = null;
			
			try {
				
					String hql = "select rt from Application rt where rt.applicant.id = " + applicant.getId()  + " AND " + except;
					log.info("Get hql = " + hql);
					rt = (Collection<Application>) swpService.getAllRecordsByHQL(hql);
				
			} catch (HibernateException e) {
				log.error("",e);
			} catch (Exception e) {
				log.error("",e);
			} finally {
				
			}
			return rt;
		}
		return null;
	}

	public Collection<Application> getApplicationsByExceptStatus(String[] status) {
		// TODO Auto-generated method stub
		String except = null;
		if(status.length>0)
		{
			except += "(";
			for(int c=0; c<status.length; c++)
			{
				except += " lower(rt.status) != lower('"+ status[c] +"')";
				if(c!=(status.length-1))
					except += " AND ";
			}
			except += ")";
			
			Collection<Application> rt = null;
			
			try {
				
					String hql = "select rt from Application rt where " + except;
					log.info("Get hql = " + hql);
					rt = (Collection<Application>) swpService.getAllRecordsByHQL(hql);
				
			} catch (HibernateException e) {
				log.error("",e);
			} catch (Exception e) {
				log.error("",e);
			} finally {
				
			}
			return rt;
		}
		return null;
	}

	public Collection<String> getAttachmentTypeByHSCode(
			ItemCategory itemCategoryEntity) {
		// TODO Auto-generated method stub
		Collection<String> rt = null;
		
		try {
			
				String hql = "select rt.applicationAttachmentType.name from ItemCategoryMap rt where rt.itemCategory.id = " + itemCategoryEntity.getId();
				log.info("Get hql = " + hql);
				rt = (Collection<String>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	public Collection<ApplicationItem> getApplicationItemsByApplication(Application application)
	{
		Collection<ApplicationItem> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationItem rt where rt.application.id = " + application.getId();
				log.info("Get hql = " + hql);
				rt = (Collection<ApplicationItem>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	
	
	public Collection<ApplicationAttachment> getApplicationAttachmentListByApplication(Application application)
	{
		Collection<ApplicationAttachment> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationAttachment rt where rt.application.id = " + application.getId();
				log.info("Get hql = " + hql);
				rt = (Collection<ApplicationAttachment>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	
	
	

	public Collection<PortalUser> getPortalUserByAgency(Agency ag) {
		// TODO Auto-generated method stub
		Collection<PortalUser> rt = null;
		
		try {
			
				String hql = "select rt from PortalUser rt where rt.agency.id = " + ag.getId();
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

	public ApplicationWorkflow getLastApplicationWorkFlowByApplicationIdAndReceipientRole(
			Application ap, RoleType roleType) {
		// TODO Auto-generated method stub
		ApplicationWorkflow rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.receipientRole = " + roleType.getId() + 
						" AND rt.application.id = " + ap.getId() + " ORDER BY rt.dateCreated DESC";
				log.info("Get hql = " + hql);
				//rt = (ApplicationWorkflow) swpService.getUniqueRecordByHQL(hql);
				Collection<ApplicationWorkflow> rt1 = (Collection<ApplicationWorkflow>) swpService.getAllRecordsByHQL(hql, 0, 1);
				if(rt1!=null && rt1.size()>0)
				{
					Iterator<ApplicationWorkflow> iter = rt1.iterator();
					rt= iter.next();
				}else
				{
					rt= null;
				}
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<ApplicationWorkflow> getApplicationWorkFlowByReceipientRoleIdAndNotWorkedOn(
			Long id, boolean b) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.receipientRole = " + id + " AND rt.workedOn = " + (b==true ? "'true'" : "'false'");
				log.info("Get hql = " + hql);
				rt = (Collection<ApplicationWorkflow>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<ApplicationWorkflow> getApplicationWorkFlowBySourceRoleIdAndStatus(
			Long id, ApplicationStatus status) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where " +
						"rt.sourceId = " + id + " AND lower(rt.status) = lower('" + status.getValue() + "')";
				log.info("Get hql = " + hql);
				rt = (Collection<ApplicationWorkflow>) swpService.getAllRecordsByHQL(hql);
				log.info("Rt size = " + rt.size());
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	
	public Collection<ApplicationWorkflow> getApplicationWorkFlowBySourceRoleIdAndStatusAndNowWorkedFor(
			Long id, ApplicationStatus status, boolean workedOn) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = "";
				if(workedOn==false)
				{
					hql = "select rt from ApplicationWorkflow rt where " +
						"rt.sourceId = " + id + " AND " +
						"lower(rt.status) = lower('" + status.getValue() + "') AND rt.workedOn = 'false'";
				}
				else
				{
					hql = "select rt from ApplicationWorkflow rt where " +
						"rt.sourceId = " + id + " AND " +
						"lower(rt.status) = lower('" + status.getValue() + "') AND rt.workedOn = 'true'";
				}
				log.info("Get hql = " + hql);
				rt = (Collection<ApplicationWorkflow>) swpService.getAllRecordsByHQL(hql);
				log.info("Rt size = " + rt.size());
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	public ApplicationWorkflow getApplicationWorkFlowBySourceRoleIdAndStatusAndApplication(
			Long id, ApplicationStatus status, Application application) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		ApplicationWorkflow rt1 = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.application.id = " + application.getId() + " AND " +
						"rt.sourceId = " + id + " AND lower(rt.status) = " +
						"lower('" + status.getValue() + "') ORDER BY rt.dateCreated " +
						"DESC";
				log.info("Get hql = " + hql);
				rt = (Collection<ApplicationWorkflow>) swpService.getAllRecordsByHQL(hql, 0, 1);
				if(rt!=null && rt.size()>0)
				{
					Iterator<ApplicationWorkflow> iter = rt.iterator();
					rt1= iter.next();
				}else
				{
					rt1= null;
				}
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		
		
		
		return rt1;
	}
	
	public ApplicationWorkflow getApplicationWorkFlowByReceipientRoleIdAndStatusAndApplication(
			Long id, ApplicationStatus status, Application application) {
		// TODO Auto-generated method stub
		ApplicationWorkflow rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.application.id = " + application.getId() + " AND " +
						"rt.receipientRole = " + id + " AND lower(rt.status) = lower('" + status.getValue() + "') ORDER BY rt.dateCreated DESC";
				log.info("Get hql = " + hql);
				Collection<ApplicationWorkflow> rt1 = (Collection<ApplicationWorkflow>) swpService.getAllRecordsByHQL(hql, 0, 1);
				if(rt1!=null && rt1.size()>0)
				{
					Iterator<ApplicationWorkflow> iter = rt1.iterator();
					rt= iter.next();
				}else
				{
					rt= null;
				}
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	
	public Collection<ApplicationWorkflow> getApplicationWorkFlowByReceipientRole(
			Long id, ApplicationStatus status, boolean b) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.receipientRole = " +
						"" + id + " AND " +
						"lower(rt.status) = lower('" + status.getValue() + "')";
				log.info("Get hql = " + hql);
				rt = (Collection<ApplicationWorkflow>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	
	public Collection<ApplicationWorkflow> getApplicationWorkFlowByReceipientRoleIdAndStatusAndNotWorkedOn(
			Long id, ApplicationStatus status, boolean b) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.receipientRole = " +
						"" + id + " AND rt.workedOn = " + (b==true ? "'true'" : "'false'" + " AND " +
						"lower(rt.status) = lower('" + status.getValue() + "')");
				log.info("Get hql = " + hql);
				rt = (Collection<ApplicationWorkflow>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	
	public ApplicationWorkflow getApplicationWorkFlowByApplicationAndStatus(Application app, ApplicationStatus status)
	{
		ApplicationWorkflow rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where " +
						"rt.application.id = " + app.getId() + " AND lower(rt.status) = lower('"+status.getValue()+"') ORDER BY rt.dateCreated DESC";
				log.info("Get hql = " + hql);
				Collection<ApplicationWorkflow> rt1 = (Collection<ApplicationWorkflow>) swpService.getAllRecordsByHQL(hql, 0, 1);
				if(rt1!=null && rt1.size()>0)
				{
					Iterator<ApplicationWorkflow> iter = rt1.iterator();
					rt= iter.next();
				}else
				{
					rt= null;
				}
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<ApplicationAttachment> getApplicationAttachmentByApplication(
			Application application) {
		// TODO Auto-generated method stub
		Collection<ApplicationAttachment> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationAttachment rt where rt.application.id = " + application.getId();
				log.info("Get hql = " + hql);
				rt = (Collection<ApplicationAttachment>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<ApplicationFlag> getApplicationFlagByApplication(
			Application application) {
		// TODO Auto-generated method stub
		Collection<ApplicationFlag> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationFlag rt where rt.application.id = " + application.getId();
				log.info("Get hql = " + hql);
				rt = (Collection<ApplicationFlag>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<ApplicationWorkflow> getApplicationWorkFlowByReceipientRoleIdAndStatus(
			Long id, ApplicationStatus status) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.receipientRole = " + id + 
						" AND lower(status) = lower('" + status.getValue() + "')";
				log.info("Get hql = " + hql);
				rt = (Collection<ApplicationWorkflow>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<Certificate> getCertificateByStatus(
			CertificateStatus status) {
		// TODO Auto-generated method stub
		Collection<Certificate> rt = null;
		
		try {
			
				String hql = "select rt from Certificate rt where lower(rt.status) = lower('" + status.getValue() + "')";
				log.info("Get hql = " + hql);
				rt = (Collection<Certificate>) swpService.getAllRecordsByHQL(hql);
			
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
		Collection<Agency> rt = null;
		
		
		try {
			
				String hql = "select rt from Agency rt where lower(rt.agencyType) = lower('" + agencyType.getValue() + "')";
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

	public Collection<PortalUser> getAllPortalUserByAgencyType(String value) {
		// TODO Auto-generated method stub
		Collection<PortalUser> rt = null;
		
		try {
			
				String hql = "select rt from PortalUser rt where lower(rt.agency.agencyType) = lower('" + value + "')";
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

	public HashMap<String, Collection<Dispute>> getDisputeList(Boolean true1, DisputeType dt) {
		// TODO Auto-generated method stub
		Collection<Dispute> rt = null;
		HashMap<String, Collection<Dispute>> hmap=null;
		try {
			
				String hql="";
				if(true1!=null && true1.equals(Boolean.TRUE))
					hql = "select rt from Dispute rt where rt.actedOn = 'true' AND lower(rt.disputeType) = lower('" + dt.getValue() +"')";
				else
					hql = "select rt from Dispute rt where rt.actedOn != 'true' AND lower(rt.disputeType) = lower('" + dt.getValue() +"')";
				log.info("Get hql = " + hql);
				rt = (Collection<Dispute>) swpService.getAllRecordsByHQL(hql);
				
				if(rt!=null)
				{
					if(dt!=null && dt.equals(DisputeType.DISPUTE_TYPE_CERTIFICATE))
					{
						hmap = new HashMap<String, Collection<Dispute>>();
						for(Iterator<Dispute> it = rt.iterator(); it.hasNext();)
						{
							Dispute dispute = it.next();
							if(hmap!=null && hmap.size()>0)
							{
								Collection<Dispute> ds = hmap.get(dispute.getCertificate().getCertificateNo());
								if(ds!=null)
								{
									ds.add(dispute);
									hmap.put(dispute.getCertificate().getCertificateNo(), ds);
								}else
								{
									ArrayList<Dispute> ds1 = new ArrayList<Dispute>();
									ds1.add(dispute);
									hmap.put(dispute.getCertificate().getCertificateNo(), ds1);
								}
							}else
							{
								ArrayList<Dispute> list = new ArrayList<Dispute>();
								list.add(dispute);
								hmap.put(dispute.getCertificate().getCertificateNo(), list);
							}
						}
					}else
					{
						hmap = new HashMap<String, Collection<Dispute>>();
						for(Iterator<Dispute> it = rt.iterator(); it.hasNext();)
						{
							Dispute dispute = it.next();
							if(hmap!=null && hmap.size()>0)
							{
								Collection<Dispute> ds = hmap.get(dispute.getApplication().getApplicationNumber());
								if(ds!=null)
								{
									ds.add(dispute);
									hmap.put(dispute.getApplication().getApplicationNumber(), ds);
								}else
								{
									ArrayList<Dispute> ds1 = new ArrayList<Dispute>();
									ds1.add(dispute);
									hmap.put(dispute.getCertificate().getCertificateNo(), ds1);
								}
							}else
							{
								ArrayList<Dispute> list = new ArrayList<Dispute>();
								list.add(dispute);
								hmap.put(dispute.getApplication().getApplicationNumber(), list);
							}
						}
					}
				}
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		log.info("hmap size= " + hmap.size());
		Set<String> k = hmap.keySet();
		Iterator<String> it = k.iterator();
		while(it.hasNext())
		{
			log.info("key = " + it.next());
		}
		return hmap;
	}


	
	

}
