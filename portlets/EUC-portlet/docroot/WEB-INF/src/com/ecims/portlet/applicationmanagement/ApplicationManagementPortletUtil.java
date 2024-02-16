package com.ecims.portlet.applicationmanagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

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
import smartpay.entity.ApplicationAttachmentAgency;
import smartpay.entity.ApplicationAttachmentType;
import smartpay.entity.ApplicationFlag;
import smartpay.entity.ApplicationItem;
import smartpay.entity.ApplicationWorkflow;
import smartpay.entity.BlackList;
import smartpay.entity.Certificate;
import smartpay.entity.Company;
import smartpay.entity.Country;
import smartpay.entity.EndorsedApplicationDesk;
import smartpay.entity.EndorsementDesk;
import smartpay.entity.ItemCategory;
import smartpay.entity.ItemCategorySub;
import smartpay.entity.PortalUser;
import smartpay.entity.QuantityUnit;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.State;
import smartpay.entity.WokFlowSetting;
import smartpay.entity.enumerations.AgencyType;
import smartpay.entity.enumerations.ApplicantStatus;
import smartpay.entity.enumerations.ApplicationStatus;
import smartpay.entity.enumerations.PermissionType;
import smartpay.entity.enumerations.RoleConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.entity.enumerations.UserStatus;
import smartpay.service.SwpService;

public class ApplicationManagementPortletUtil {

	
	private static ApplicationManagementPortletUtil adminPortletUtil=null;
	SwpService swpService = null;
	com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	Logger log = Logger.getLogger(ApplicationManagementPortletUtil.class);
	
	public ApplicationManagementPortletUtil() {
		swpService = serviceLocator.getSwpService();
	}
	
	public static ApplicationManagementPortletUtil getInstance()
	{
		if(adminPortletUtil==null)
		{
			ApplicationManagementPortletUtil.adminPortletUtil = new ApplicationManagementPortletUtil();
		}
		return ApplicationManagementPortletUtil.adminPortletUtil;
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
	
	
	public boolean canUserValidateThisApplication(ApplicationWorkflow awf, PortalUser pu, ApplicationAttachment aa)
	{
		if(pu.getAgency()!=null)
		{
			ApplicationAttachmentAgency aaa = null;
			try {
				//if you have validated before
				String hql = "select aaa from ApplicationAttachmentAgency aaa where " +
						"aaa.applicationAttachment.id = " + aa.getId() + " AND aaa.agency.id = " + pu.getAgency().getId();
				log.info("Get hqlType = " + hql);
				aaa = (ApplicationAttachmentAgency) swpService.getUniqueRecordByHQL(hql);
			} catch (HibernateException e) {
				log.error("",e);
			} catch (Exception e) {
				log.error("",e);
			} 

			if(aaa!=null)		//yes validated before
			{
				return false;
			}else
			{
//				Collection<Long> icat = null;
//				try {
//					String hql = "select aw.agency.id from ItemCategoryAttachmentTypeAgency aw where " +
//							"aw.applicationAttachmentType.id = " +aa.getApplicationAttachmentType().getId()+" " +
//							"AND aw.itemCategory.id = " + awf.getApplication().getItemCategory().getId() + " " +
//							"AND aw.agency.id = " + pu.getAgency().getId();
//					log.info("Get hqlType = " + hql);
//					icat = (Collection<Long>) swpService.getAllRecordsByHQL(hql);
//				} catch (HibernateException e) {
//					log.error("",e);
//				} catch (Exception e) {
//					log.error("",e);
//				} 
//				
//				if(icat!=null && icat.size()>0)
//				{
//					if(icat.contains(pu.getAgency().getId()))
//					{
//						return true;
//					}
//					else
//					{
//						return false;
//					}
//				}else
//				{
//					return false;
//				}
				return true;
			}
		}else
		{
			return false;
		}
		

	}

	
	public boolean canUserEndorseThisApplication(ApplicationWorkflow awf, ApplicationManagementPortletState portletState)
	{
		if(portletState.getPortalUser().getAgency().getAgencyType().getValue().equals(AgencyType.ACCREDITOR_GROUP.getValue()))
		{
			
			Collection<ApplicationWorkflow> rt = null;
			
			try {
				
					String hql = "select rt from ApplicationWorkflow rt where rt.application.id = " + awf.getApplication().getId() + " AND " +
							"rt.agency.id = " + portletState.getPortalUser().getAgency().getId() + " AND lower(rt.status) = " +
							"lower('" + ApplicationStatus.APPLICATION_STATUS_FORWARDED.getValue() + "') AND " +
							"rt.workedOn = 'false'";
					log.info("Get hql = " + hql);
					rt = (Collection<ApplicationWorkflow>) swpService.getAllRecordsByHQL(hql);
					if(rt!=null && rt.size()>0)
					{
						return true;
					}else
					{
						return false;
					}
					
				
			} catch (HibernateException e) {
				log.error("",e);
			} catch (Exception e) {
				log.error("",e);
			} finally {
				
			}
			return false;
		}else
		{
//			Collection<Long> icat = null;
//			try {
//				String hql = "select aw.agency.id from ItemCategoryAttachmentTypeAgency aw where " +
//						"aw.itemCategory.id = " + awf.getApplication().getItemCategory().getId() + " " +
//						"AND aw.agency.id = " + portletState.getPortalUser().getAgency().getId();
//				log.info("Get hqlType = " + hql);
//				icat = (Collection<Long>) swpService.getAllRecordsByHQL(hql);
//			} catch (HibernateException e) {
//				log.error("",e);
//			} catch (Exception e) {
//				log.error("",e);
//			} 
			
			Collection<ApplicationWorkflow> awfL = portletState.getApplicationManagementPortletUtil().
					getApplicationWorkFlowsBySourceAgencyIdAndStatusAndApplication
					(portletState.getPortalUser().getAgency().getId(), 
							ApplicationStatus.APPLICATION_STATUS_ENDORSED, 
							awf.getApplication());
//					getApplicationWorkFlowBySourceAgencyIdAndStatus(
//							portletState.getPortalUser().getAgency().getId(), 
//							ApplicationStatus.APPLICATION_STATUS_ENDORSED);
			if(awfL!=null && awfL.size()>0)
			{
				return false;
			}
			else
			{
				Collection<ApplicationAttachmentAgency> aaa = null;
				try {
					//if you have validated before
					String hql = "select aaa from ApplicationAttachmentAgency aaa where " +
							"aaa.applicationAttachment.application.id = " + 
							awf.getApplication().getId() + " AND aaa.agency.id = " + 
							portletState.getPortalUser().getAgency().getId();
					log.info("Get hqlType = " + hql);
					aaa = (Collection<ApplicationAttachmentAgency>) swpService.getAllRecordsByHQL(hql);
				} catch (HibernateException e) {
					log.error("",e);
				} catch (Exception e) {
					log.error("",e);
				} 
				
				
				if(aaa==null || (aaa!=null && aaa.size()==0))
				{
					log.info("There are not applicationattachmentagency in the pc added by the user = " + awf.getApplication().getItemCategory().getId());
					return false;
				}
				else
				{
	//				if(icat==null || (icat!=null && icat.size()==0))
	//				{
	//					log.info("There are not itemcategoryattachmenttype in the pc mapped for the item category = " + awf.getApplication().getItemCategory().getId());
	//					return false;
	//				}else
	//				{
	//					if(icat.size()>aaa.size())
	//					{
	//						log.info("Number of item category attachment type for the item category and agency are greater than the number of validations ");
	//						return false;
	//					}else
	//					{
							return true;
	//					}
	//				}
					
				}
			}
		}
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
	
	
	public Certificate getCertificateByApplication(Application app)
	{
		Certificate cert = null;
		try {
			String hql = "select pu from Certificate pu where " +
					"pu.application.id = " + app.getId();
			log.info("Get hqlType = " + hql);
			cert = (Certificate) swpService.getUniqueRecordByHQL(hql);
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} 
		return cert;
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

	public Collection<PortalUser> getAllPortalUsers(ApplicationManagementPortletState portletState) {
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
	
	public Collection<Application> getApplicationsByApplicantAndStatus(Applicant applicant,
			ApplicationStatus[] appStatus) {
		// TODO Auto-generated method stub
		Collection<Application> rt = null;
		
		try {
			String add = null;
			if(appStatus!=null && appStatus.length>0)
			{
				add="";
				for(int c=0; c<appStatus.length; c++)
				{
					add = add + " lower(rt.status) = lower('" + appStatus[c].getValue() + "')";
					if(c<appStatus.length-1)
					{
						add = add + " OR ";
					}
				}
				add = " AND (" + add + ")"; 
			}
			String hql = "select rt from Application rt where (rt.applicant.id) = " + applicant.getId() + 
					add;
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
	
	
	public Collection<Application> getApplicationsByAgencyApplicantAndStatus(PortalUser agencyApplicant,
			ApplicationStatus appStatus) {
		// TODO Auto-generated method stub
		Collection<Application> rt = null;
		
		try {
			
				String hql = "select rt from Application rt where (rt.portalUser.id) = " + agencyApplicant.getId() + " AND lower(rt.status) = lower('" + appStatus.getValue() + "')";
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
			except = "(";
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
	
	
	
	public Collection<Application> getApplicationsByAgencyApplicantAndExceptStatus(
			PortalUser agencyApplicant, String[] status) {
		// TODO Auto-generated method stub
		String except = null;
		if(status.length>0)
		{
			except = "(";
			for(int c=0; c<status.length; c++)
			{
				except += " lower(rt.status) != lower('"+ status[c] +"')";
				if(c!=(status.length-1))
					except += " AND ";
			}
			except += ")";
			
			Collection<Application> rt = null;
			
			try {
				
					String hql = "select rt from Application rt where rt.portalUser.id = " + agencyApplicant.getId()  + " AND " + except;
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
			except = "(";
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

	public Collection<String> getAttachmentTypeByHSCode() {
		// TODO Auto-generated method stub
		Collection<String> rt = null;
		
		try {
			
				String hql = "select rt.name from ApplicationAttachmentType rt";
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
			Application ap, RoleType roleType, Agency agency) {
		// TODO Auto-generated method stub
		ApplicationWorkflow rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.receipientRole = " + roleType.getId() + 
						" AND rt.application.id = " + ap.getId() + " AND rt.agency.id = " +agency.getId()+" ORDER BY rt.dateCreated DESC";
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
			Long id, boolean b, Agency agency) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.receipientRole = " + id + " AND rt.agency.id = " +agency.getId()+" AND rt.workedOn = " + (b==true ? "'true'" : "'false'");
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
			Long id, ApplicationStatus status, Application application, Agency agency) {
		// TODO Auto-generated method stub
		ApplicationWorkflow rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.application.id = " + application.getId() + " AND " +
						"rt.receipientRole = " + id + " AND lower(rt.status) = lower('" + status.getValue() + "')  AND rt.agency.id = " +agency.getId()+" ORDER BY rt.dateCreated DESC";
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
			Long id, ApplicationStatus status, boolean b, Agency agency) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.receipientRole = " +
						"" + id + " AND " +
						"lower(rt.status) = lower('" + status.getValue() + "') AND rt.agency.id = " +agency.getId();
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
			Long id, ApplicationStatus status, boolean b, Agency agency) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.receipientRole = " +
						"" + id + " AND rt.agency.id = " +agency.getId()+" AND rt.workedOn = " + (b==true ? "'true'" : "'false'" + " AND " +
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

	
	
	public Collection<ApplicationWorkflow> getApplicationWorkFlowByReceipientRoleIdAndStatusesAndNotWorkedOn(
			Long id, String[] status, boolean b, Agency agency) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		String except = "(";
		for(int c=0; c<status.length; c++)
		{
			except += " lower(rt.status) != lower('"+ status[c] +"')";
			if(c!=(status.length-1))
				except += " AND ";
		}
		except += ")";
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.receipientRole = " +
						"" + id + " AND rt.agency.id = " +agency.getId()+" AND rt.workedOn = " + (b==true ? "'true'" : "'false'" + " AND " + except);
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
	
	public Collection<ApplicationWorkflow> getApplicationWorkFlowsByApplicationAndStatus(Application app, ApplicationStatus status)
	{
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where " +
						"rt.application.id = " + app.getId() + " AND lower(rt.status) = lower('"+status.getValue()+"') ORDER BY rt.dateCreated DESC";
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
	
	
	public Collection<ApplicationWorkflow> getApplicationWorkFlowByApplicationAndStatusAndWorkedOn(Application app, ApplicationStatus status, boolean b)
	{
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = "";
				if(b==true)
				{
					hql = "select rt from ApplicationWorkflow rt where " +
						"rt.application.id = " + app.getId() + " AND lower(rt.status) = lower('"+status.getValue()+"') AND rt.workedOn = 'true' " +
									"ORDER BY rt.dateCreated DESC";
				}else
				{
					hql = "select rt from ApplicationWorkflow rt where " +
							"rt.application.id = " + app.getId() + " AND lower(rt.status) = lower('"+status.getValue()+"') AND rt.workedOn = 'false' " +
									"ORDER BY rt.dateCreated DESC";
				}
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
	
	
	
	public Collection<ApplicationAttachmentAgency> getApplicationAttachmentAgencyByApplication(
			Application application) {
		// TODO Auto-generated method stub
		Collection<ApplicationAttachmentAgency> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationAttachmentAgency rt where rt.applicationAttachment.application.id " +
						"= " + application.getId();
				log.info("Get hql = " + hql);
				rt = (Collection<ApplicationAttachmentAgency>) swpService.getAllRecordsByHQL(hql);
			
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
			Long id, ApplicationStatus status, Agency agency) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.receipientRole = " + id + 
						" AND lower(rt.status) = lower('" + status.getValue() + "') AND rt.agency.id = " +agency.getId();
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

	public Collection<EndorsementDesk> getEndorsementDeskByAgencyAndActive(Agency agency) {
		// TODO Auto-generated method stub
		Collection<EndorsementDesk> rt = null;
		
		try {
			
				String hql = "select rt from EndorsementDesk rt where rt.agency.id = " + agency.getId() + 
						" AND rt.isActive = 'true'";
				log.info("Get hql = " + hql);
				rt = (Collection<EndorsementDesk>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Certificate getCertificateByCertificateNumber(String certNo) {
		// TODO Auto-generated method stub
		Certificate rt = null;
		
		try {
			
				String hql = "select rt from Certificate rt where lower(rt.certificateNo) = lower('" + certNo + 
						"')";
				log.info("Get hql = " + hql);
				rt = (Certificate) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<ApplicationWorkflow> getAppWorkFlowsByApp(Application application) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.application.id = " + application.getId() + " AND " +
						"lower(rt.status) != lower('"+ApplicationStatus.APPLICATION_STATUS_CREATED.getValue()+"')";
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
	
	public Collection<ApplicationWorkflow> getAppWorkFlowsByAppForComments(Application application) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.application.id = " + application.getId() + " AND " +
						"lower(rt.status) != lower('"+ApplicationStatus.APPLICATION_STATUS_CREATED.getValue()+"') AND rt.comment IS NOT NULL";
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
	
	public Collection<String> getAllAppWorkflowByApplicationAndStatus(Long appid, String status) {
		// TODO Auto-generated method stub
		Collection<String> rt = null;
		try {
			
				String hql = "select rt1.agencyName from ApplicationWorkflow rt, Agency rt1 where " +
						"rt.application.id = " + appid + " AND " +
						"lower(rt.status) = lower('"+status+"') AND rt.sourceAgencyId = rt1.id";
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
	
	
	public Collection<String> getAllAppWorkflowByApplicationAndStatus2(Long appid, String status) {
		// TODO Auto-generated method stub
		Collection<String> rt = null;
		try {
			
				String hql = "select rt1.agencyName from ApplicationWorkflow rt, Agency rt1 where " +
						"rt.application.id = " + appid + " AND " +
						"lower(rt.status) = lower('"+status+"') AND rt.sourceAgencyId = rt1.id";
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
	
	
	public Collection<String> getAllAppWorkflowByApplicationAndStatusAndWorkedOn(Long appid, String status, boolean b) {
		// TODO Auto-generated method stub
		Collection<String> rt = null;
		try {
			
				String hql = "";
				if(b==true)
				{
					hql = "select rt1.agencyName from ApplicationWorkflow rt, Agency rt1 where " +
						"rt.application.id = " + appid + " AND " +
						"lower(rt.status) = lower('"+status+"') AND rt.sourceAgencyId = rt1.id " +
									"AND rt.workedOn = 'true'";
				}else {
					hql = "select rt1.agencyName from ApplicationWorkflow rt, Agency rt1 where " +
							"rt.application.id = " + appid + " AND " +
							"lower(rt.status) = lower('"+status+"') AND rt.sourceAgencyId = rt1.id " +
									"AND rt.workedOn = 'false'";
				}
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
	
	
	public Collection<String> getAgencyReceipientByApplicationAndStatusAndWorkedOn(Long appid, String status, boolean b, Long id) {
		// TODO Auto-generated method stub
		Collection<String> rt = null;
		try {
			
				String hql = "";
				if(b==true)
				{
					hql = "select rt.agency.agencyName from ApplicationWorkflow rt where " +
						"rt.application.id = " + appid + " AND " +
						"lower(rt.status) = lower('"+status+"') AND rt.workedOn = 'true' AND rt.sourceAgencyId " +
								"= " + id + "";
				}else {
					hql = "select rt.agency.agencyName from ApplicationWorkflow rt where " +
							"rt.application.id = " + appid + " AND " +
							"lower(rt.status) = lower('"+status+"') AND rt.workedOn = 'false' AND rt.sourceAgencyId = " + id + "";
				}
				log.info("Get hql = " + hql);
				rt = (Collection<String>) swpService.getAllRecordsByHQL(hql);
				log.info("rt size90===" + (rt!=null ? rt.size() : "null"));
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	
	public Collection<String> getAgencyReceipientByApplicationAndStatusAndWorkedOn(Long appid, 
			String status, boolean b, Long id, ApplicationWorkflow aw) {
		// TODO Auto-generated method stub
		Collection<String> rt = null;
		try {
			
				String hql = "";
				if(b==true)
				{
					hql = "select rt.agency.agencyName from ApplicationWorkflow rt where " +
						"rt.application.id = " + appid + " AND " +
						"lower(rt.status) = lower('"+status+"') AND rt.workedOn = 'true' AND rt.sourceAgencyId " +
								"= " + id + " AND rt.id = " + aw.getId();
				}else {
					hql = "select rt.agency.agencyName from ApplicationWorkflow rt where " +
							"rt.application.id = " + appid + " AND " +
							"lower(rt.status) = lower('"+status+"') AND rt.workedOn = 'false' " +
									"AND rt.sourceAgencyId = " + id + " AND rt.id = " + aw.getId();
				}
				log.info("Get hql = " + hql);
				rt = (Collection<String>) swpService.getAllRecordsByHQL(hql);
				log.info("rt size90===" + (rt!=null ? rt.size() : "null"));
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	
	public Collection<String> getAgencyReceipientByApplicationAndStatusAndWorkedOn2(Long appid, String status, Boolean b) {
		// TODO Auto-generated method stub
		Collection<String> rt = null;
		try {
			
				String hql = "";
				if(b!=null && b.equals(Boolean.TRUE))
				{
					hql = "select rt1.agencyName from ApplicationWorkflow rt, Agency rt1 where " +
						"rt.application.id = " + appid + " AND " +
						"lower(rt.status) = lower('"+status+"') AND rt.workedOn = 'true' AND rt.sourceAgencyId = rt1.id";
				}else if(b!=null && b.equals(Boolean.FALSE)) {
					hql = "select rt1.agencyName from ApplicationWorkflow rt, Agency rt1 where " +
						"rt.application.id = " + appid + " AND " +
						"lower(rt.status) = lower('"+status+"') AND rt.workedOn = 'false' AND rt.sourceAgencyId = rt1.id";
				}else  {
					hql = "select rt1.agencyName from ApplicationWorkflow rt, Agency rt1 where " +
							"rt.application.id = " + appid + " AND " +
							"lower(rt.status) = lower('"+status+"') AND rt.sourceAgencyId = rt1.id";
					}
				log.info("Get hql = " + hql);
				rt = (Collection<String>) swpService.getAllRecordsByHQL(hql);
				log.info("rt size90===" + (rt!=null ? rt.size() : "null"));
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	
	public Collection<String> getAgencyReceipientByApplicationAndStatusAndWorkedOn2(Long appid, String status) {
		// TODO Auto-generated method stub
		Collection<String> rt = null;
		try {
			
				String hql = "";
				
					hql = "select rt1.agencyName from ApplicationWorkflow rt, Agency rt1 where " +
						"rt.application.id = " + appid + " AND " +
						"lower(rt.status) = lower('"+status+"') AND rt.sourceAgencyId = rt1.id";
				
				log.info("Get hql = " + hql);
				rt = (Collection<String>) swpService.getAllRecordsByHQL(hql);
				log.info("rt size90===" + (rt!=null ? rt.size() : "null"));
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	
	public Collection<String> getAllAppWorkflowByApplicationWFAndStatus(Long appid, String status) {
		// TODO Auto-generated method stub
		Collection<String> rt = null;
		try {
			
				String hql = "select rt1.agencyName from ApplicationWorkflow rt, Agency rt1 where " +
						"rt.id = " + appid + " AND " +
						"lower(rt.status) = lower('"+status+"') AND rt.sourceAgencyId = rt1.id";
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

	public Collection<ApplicationFlag> getApplicationFlags() {
		// TODO Auto-generated method stub
		Collection<ApplicationFlag> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationFlag rt";
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

	public Collection<Application> getApplicationinAppFlags() {
		// TODO Auto-generated method stub
		Collection<Application> rt = null;
		
		try {
			
				String hql = "select rt.application from ApplicationFlag rt";
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

	public Agency getAgencyByName(AgencyType ag) {
		// TODO Auto-generated method stub
		Agency rt = null;
		
		try {
			
				String hql = "select rt from Agency rt where lower(rt.agencyType) = ('" + ag.getValue() +"')";
				log.info("Get hql = " + hql);
				rt = (Agency) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public Collection<EndorsedApplicationDesk> getEndorsedAppDeskByApplication(
			Application application) {
		// TODO Auto-generated method stub
Collection<EndorsedApplicationDesk> rt = null;
		
		try {
			
				String hql = "select rt from EndorsedApplicationDesk rt where rt.application.id = " + application.getId();
				log.info("Get hql = " + hql);
				rt = (Collection<EndorsedApplicationDesk>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public ApplicationWorkflow getPreviousApplicationWorkflowOfApplication(
			Application application) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		ApplicationWorkflow rt1 = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.application.id = " + application.getId()
						+ " ORDER BY rt.dateCreated DESC";
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
	
	
	public ApplicationWorkflow getPreviousApplicationWorkflowOfApplicationByAW(
			ApplicationWorkflow aw) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		ApplicationWorkflow rt1 = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.id < " + aw.getId()
						+ " AND rt.application.id = "+aw.getApplication().getId()+"  ORDER BY rt.dateCreated DESC";
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
		
		
		log.info("rt1===>" + rt1.getId());
		return rt1;
	}

	public WokFlowSetting getWorkflowSettingByItemCategoryOrderByPosition(
			ItemCategory ic, int currentPosition) {
		// TODO Auto-generated method stub
		Collection<WokFlowSetting> rt = null;
		WokFlowSetting rt1 = null;
		
		try {
			
				String hql = "select rt from WokFlowSetting rt where rt.itemCategory.id = " + ic.getId()
						+ " AND rt.positionId > "+currentPosition+" ORDER BY rt.positionId ASC";
//				String hql = "select rt.agency from WokFlowSetting rt where rt.itemCategory.id = " + ic.getId()
//						+ " ORDER BY rt.positionId ASC";
				log.info("Get hql = " + hql);
				rt = (Collection<WokFlowSetting>) swpService.getAllRecordsByHQL(hql, 0, 1);
				if(rt!=null && rt.size()>0)
				{
					Iterator<WokFlowSetting> iter = rt.iterator();
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
	
	
	

	public Collection<WokFlowSetting> getWorkFlowSettingByCategory(
			ItemCategory ic) {
		// TODO Auto-generated method stub
		Collection<WokFlowSetting> rt = null;


		try {
			
				String hql = "select rt from WokFlowSetting rt where rt.itemCategory.id = " + ic.getId();
				log.info("Get hql = " + hql);
				rt = (Collection<WokFlowSetting>) swpService.getAllRecordsByHQL(hql);
				
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		
		
		
		return rt;
	}

	public ApplicationWorkflow getApplicationWorkFlowByReceipientIdAndStatusAndWorkedOn(Long id,
			ApplicationStatus status, Application app, Boolean workedOn) {
		// TODO Auto-generated method stub
		ApplicationWorkflow rt1 = null;
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = null;
				if(workedOn!=null && workedOn.equals(Boolean.TRUE))
				{
					hql = "select rt from ApplicationWorkflow rt where rt.application.id = " + app.getId()
						+ " AND lower(rt.status) = lower('"+status.getValue()+"') AND " +
						"rt.workedOn = 'true' ORDER BY rt.dateCreated DESC";
				}else
				{
					hql = "select rt from ApplicationWorkflow rt where rt.application.id = " + app.getId()
							+ " AND lower(rt.status) = lower('"+status.getValue()+"') AND " +
							"rt.workedOn = 'false' ORDER BY rt.dateCreated DESC";
				}
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
	
	
	
	public ApplicationWorkflow getApplicationWorkFlowByReceipientIdAndNotStatusAndWorkedOn(Long id,
			ApplicationStatus status, Application app, Boolean workedOn) {
		// TODO Auto-generated method stub
		ApplicationWorkflow rt1 = null;
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = null;
				if(workedOn!=null && workedOn.equals(Boolean.TRUE))
				{
					hql = "select rt from ApplicationWorkflow rt where rt.application.id = " + app.getId()
						+ " AND lower(rt.status) != lower('"+status.getValue()+"') AND " +
						"rt.workedOn = 'true' ORDER BY rt.dateCreated DESC";
				}else
				{
					hql = "select rt from ApplicationWorkflow rt where rt.application.id = " + app.getId()
							+ " AND lower(rt.status) != lower('"+status.getValue()+"') AND " +
							"rt.workedOn = 'false' ORDER BY rt.dateCreated DESC";
				}
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

	public WokFlowSetting getWorkFlowSettingByAgencyApplicationCategory(
			Agency ag, ItemCategory ic) {
		// TODO Auto-generated method stub
		WokFlowSetting rt = null;


		try {
			
				String hql = "select rt from WokFlowSetting rt where rt.itemCategory.id = " + ic.getId() + 
						" AND rt.agency.id = " + ag.getId();
				log.info("Get hql = " + hql);
				rt = (WokFlowSetting) swpService.getUniqueRecordByHQL(hql);
				
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		
		
		
		return rt;
	}

	public Collection<PortalUser> getApplicationApprovers() {
		// TODO Auto-generated method stub
		Collection<PortalUser> rt = null;
		
		try {
			
				String hql = "select rt.portalUser from Permission rt, PortalUser pu where lower(rt.permissionType) = " +
						"lower('" + PermissionType.PERMISSION_FORWARD_APPLICATION.getValue() +  "') AND " +
						"lower(rt.portalUser.roleType.name) = " +
						"lower('" + RoleTypeConstants.ROLE_NSA_USER.getValue() + "') " +
						"AND rt.portalUser.id = pu.id";
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
	
	
	
	public Collection<PortalUser> getApplicationApproversNew() {
		// TODO Auto-generated method stub
		Collection<PortalUser> rt = null;
		
		try {
			
				String hql = "select rt.portalUser from Permission rt, PortalUser pu where lower(rt.permissionType) = " +
						"lower('" + PermissionType.PERMISSION_APPROVE_APPLICATION.getValue() +  "') AND " +
						"lower(rt.portalUser.roleType.name) = " +
						"lower('" + RoleTypeConstants.ROLE_NSA_USER.getValue() + "') " +
						"AND rt.portalUser.id = pu.id";
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

	public Collection<ItemCategory> getAllItemCategoryByApplicantType(
			Applicant app) {
		// TODO Auto-generated method stub
Collection<ItemCategory> rt = null;
		
		try {
			
				String hql = "select rt.itemCategory from ItemCategoryApplicantType rt where lower(rt.applicantType) = " +
						"lower('" + app.getApplicantType().getValue() +  "')";
				log.info("Get hql = " + hql);
				rt = (Collection<ItemCategory>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
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

	public Collection<Agency> getAgencyAlreadyEndorsed(Application application) {
		// TODO Auto-generated method stub
		Collection<Agency> rt = null;
		try {
			
				String hql = "select rt1 from ApplicationWorkflow rt, Agency rt1 where rt.sourceAgencyId = rt1.id " +
						"AND lower(rt.status) = lower('"+ApplicationStatus.APPLICATION_STATUS_ENDORSED.getValue()+"') AND " + 
						"rt.application.id = " + application.getId();
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

	public Collection<Application> getApplicationsByAgencyApplicant(
			PortalUser agencyApplicant) {
		// TODO Auto-generated method stub
		Collection<Application> rt = null;
		
		try {
			
				String hql = "select rt from Application rt where (rt.portalUser.id) = " + agencyApplicant.getId();
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

	public Collection<ApplicationWorkflow> getApplicationWorkFlowByAgency(Long agencyId) {
		// TODO Auto-generated method stub
		Collection<Application> rt = null;
		ArrayList<ApplicationWorkflow> rt2=null;
		// ORDER BY rt.dateCreated DESC
		try {
			
			String hql = "select rt from Application rt where (rt.portalUser.agency.id = " + agencyId + ")";
			log.info("Get hql = " + hql);
			rt = (Collection<Application>) swpService.getAllRecordsByHQL(hql);
			
			if(rt!=null && rt.size()>0)
			{
				rt2 = new ArrayList<ApplicationWorkflow>();
				for(Iterator<Application> it= rt.iterator(); it.hasNext();)
				{
					Application app = it.next();
					hql = 	"Select rt from ApplicationWorkflow rt where " +
							"(rt.application.id = " + app.getId() + ") ORDER BY rt.dateCreated DESC";
					log.info("Get hql = " + hql);
					Collection<ApplicationWorkflow> rt1 = (Collection<ApplicationWorkflow>) swpService.getAllRecordsByHQL(hql, 0, 1);
					if(rt1!=null && rt1.size()>0)
						rt2.add(rt1.iterator().next());
					
					log.info("rt2 size==" + rt2.size());
				}
			}
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt2;
	}
	
	
	
	
	
	
	public Collection<ApplicationWorkflow> getApplicationWorkFlowByApplicant(Long applicantId) {
		// TODO Auto-generated method stub
		Collection<Application> rt = null;
		ArrayList<ApplicationWorkflow> rt2=null;
		// ORDER BY rt.dateCreated DESC
		try {
			
			String hql = "select rt from Application rt where (rt.applicant.id = " + applicantId + ")";
			log.info("Get hql = " + hql);
			rt = (Collection<Application>) swpService.getAllRecordsByHQL(hql);
			
			if(rt!=null && rt.size()>0)
			{
				rt2 = new ArrayList<ApplicationWorkflow>();
				for(Iterator<Application> it= rt.iterator(); it.hasNext();)
				{
					Application app = it.next();
					hql = 	"Select rt from ApplicationWorkflow rt where " +
							"(rt.application.id = " + app.getId() + ") ORDER BY rt.dateCreated DESC";
					log.info("Get hql = " + hql);
					Collection<ApplicationWorkflow> rt1 = (Collection<ApplicationWorkflow>) swpService.getAllRecordsByHQL(hql, 0, 1);
					if(rt1!=null && rt1.size()>0)
						rt2.add(rt1.iterator().next());
					
					log.info("rt2 size==" + rt2.size());
				}
			}
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt2;
	}

	public Collection<ApplicationWorkflow> getApplicationWorkFlowBySourceAgencyIdAndStatus(
			Long id, ApplicationStatus status) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where " +
						"rt.sourceAgencyId = " + id + " AND lower(rt.status) = lower('" + status.getValue() + "')";
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

	public ApplicationWorkflow getApplicationWorkFlowBySourceAgencyIdAndStatusAndApplication(
			Long id, ApplicationStatus status,
			Application application) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		ApplicationWorkflow rt1 = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.application.id = " + application.getId() + " AND " +
						"rt.sourceAgencyId = " + id + " AND lower(rt.status) = " +
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
	
	
	
	
	public Collection<ApplicationWorkflow> getApplicationWorkFlowsBySourceAgencyIdAndStatusAndApplication(
			Long id, ApplicationStatus status,
			Application application) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.application.id = " + application.getId() + " AND " +
						"rt.sourceAgencyId = " + id + " AND lower(rt.status) = " +
						"lower('" + status.getValue() + "') ORDER BY rt.dateCreated " +
						"DESC";
				log.info("Get hql = " + hql);
				rt = (Collection<ApplicationWorkflow>) swpService.getAllRecordsByHQL(hql, 0, 1);
				
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	

	public Collection<ApplicationWorkflow> getApplicationWorkFlowByApplication(
			Application app) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where rt.application.id = " + app.getId();
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

	public Collection<ApplicationWorkflow> getApplicationWorkflowByStatus(
			ApplicationStatus applicationStatusDisendorsed) {
		// TODO Auto-generated method stub
		Collection<ApplicationWorkflow> rt = null;
		
		try {
			
				String hql = "select rt from ApplicationWorkflow rt where " +
						"lower(rt.application.status) = lower('" + applicationStatusDisendorsed.getValue() + "')";
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

	public BlackList getHotChocoloteAgency(Long id) {
		// TODO Auto-generated method stub
		BlackList rt = null;
		
		try {
			
				String hql = "select rt from BlackList rt where " +
						"(rt.agency.id) = (" + id + ")";
				log.info("Get hql = " + hql);
				rt = (BlackList) swpService.getUniqueRecordByHQL(hql);
				
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	
	public BlackList getHotChocolotePortalUser(Long id) {
		// TODO Auto-generated method stub
		BlackList rt = null;
		
		try {
			
				String hql = "select rt from BlackList rt where " +
						"(rt.applicant.id) = (" + id + ")";
				log.info("Get hql = " + hql);
				rt = (BlackList) swpService.getUniqueRecordByHQL(hql);
				
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}


	
	

}
