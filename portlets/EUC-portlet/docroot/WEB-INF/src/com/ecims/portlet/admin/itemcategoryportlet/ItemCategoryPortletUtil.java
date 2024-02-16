package com.ecims.portlet.admin.itemcategoryportlet;

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
import smartpay.entity.ApplicationAttachmentType;
import smartpay.entity.Company;
import smartpay.entity.Country;
import smartpay.entity.ItemCategory;
import smartpay.entity.ItemCategoryApplicantType;
import smartpay.entity.ItemCategorySub;
import smartpay.entity.PortalUser;
import smartpay.entity.Role;
import smartpay.entity.RoleType;
import smartpay.entity.Settings;
import smartpay.entity.State;
import smartpay.entity.enumerations.ApplicantType;
import smartpay.entity.enumerations.PermissionType;
import smartpay.entity.enumerations.RoleConstants;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.service.SwpService;

public class ItemCategoryPortletUtil {

	
	private static ItemCategoryPortletUtil adminPortletUtil=null;
	SwpService swpService = null;
	com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	Logger log = Logger.getLogger(ItemCategoryPortletUtil.class);
	
	public ItemCategoryPortletUtil() {
		swpService = serviceLocator.getSwpService();
	}
	
	public static ItemCategoryPortletUtil getInstance()
	{
		if(adminPortletUtil==null)
		{
			ItemCategoryPortletUtil.adminPortletUtil = new ItemCategoryPortletUtil();
		}
		return ItemCategoryPortletUtil.adminPortletUtil;
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
	
	
	
	public PortalUser getPortalUserByActivationCode(String code)
	{
		PortalUser portalUser = null;
		try {
			String hql = "select pu from PortalUser pu where " +
					"lower(pu.activationCode) = lower('" + code + "')";
			log.info("Get hqlType = " + hql);
			portalUser = (PortalUser) swpService.getUniqueRecordByHQL(hql);
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} 
		return portalUser;
	}
	
	
	public Collection<String> getApplicationAttachmentNames()
	{
		Collection<String> portalUser = null;
		try {
			String hql = "select pu.name from ApplicationAttachmentType pu";
			log.info("Get hqlType = " + hql);
			portalUser = (Collection<String>) swpService.getAllRecordsByHQL(hql);
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
	
	
	
	public Object getAllEntity(Class claz)
	{
		Object object = null;
		try {
			object = swpService.getAllRecords(claz);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return object;
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

	public Collection<PortalUser> getAllPortalUsers(ItemCategoryPortletState portletState) {
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

	public Collection<PortalUser> getSignUpApprovers() {
		// TODO Auto-generated method stub
		Collection<PortalUser> rt = null;
		
		try {
			
				String hql = "select rt.portalUser from Permission rt, PortalUser pu where lower(rt.permissionType) = " +
						"lower('" + PermissionType.PERMISSION_MANAGE_APPLICANT +  "') AND " +
						"lower(rt.portalUser.roleType.name) = " +
						"lower('" + RoleTypeConstants.ROLE_NSA_USER.getValue() + "')";
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

	public Collection<ItemCategory> getAllItemCategory() {
		// TODO Auto-generated method stub
		Collection<ItemCategory> rt = null;
		
		try {
			rt = (Collection<ItemCategory>)swpService.getAllRecords(ItemCategory.class);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public ItemCategory getItemCategorybyName(String categoryname, Long id) {
		// TODO Auto-generated method stub
		ItemCategory rt = null;
		
		try {
			String hql = "";
			if(id==null)
				hql = "select rt from ItemCategory rt where lower(rt.itemCategoryName) = lower('" + categoryname +  "')";
			else
				hql = "select rt from ItemCategory rt where lower(rt.itemCategoryName) = lower('" + categoryname +  "') AND rt.id != " + id;
			
				log.info("Get hql = " + hql);
				rt = (ItemCategory) swpService.getUniqueRecordByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public ApplicationAttachmentType getApplicationAttachmentTypeByName(
			String name) {
		// TODO Auto-generated method stub
		ApplicationAttachmentType rt = null;
		
		try {
			
				String hql = "select rt from ApplicationAttachmentType rt where lower(rt.name) = lower('" + name +  "')";
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

	public ItemCategorySub getItemCategorySubByItemCategory(
			ItemCategory itemCategory) {
		// TODO Auto-generated method stub
		ItemCategorySub rt = null;
		
		try {
			
				String hql = "select rt from ItemCategorySub rt where lower(rt.itemCategory.id) = " + itemCategory.getId();
				log.info("Get hql = " + hql);
				Collection<ItemCategorySub> rt1 = (Collection<ItemCategorySub>) swpService.getAllRecordsByHQL(hql, 0, 1);
				if(rt1!=null && rt1.size()>0)
					rt = rt1.iterator().next();
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	
	public Collection<ApplicantType> getItemCategoryApplicantTypeByItemCategory(ItemCategory icat)
	{
		Collection<ApplicantType> rt = null;
		
		try {
			
				String hql = "select rt.applicantType from " +
						"ItemCategoryApplicantType rt where (rt.itemCategory.id) = " +icat.getId();
				log.info("Get hql = " + hql);
				rt = (Collection<ApplicantType>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}
	
	public Collection<ItemCategoryApplicantType> getItemCategoryApplicantTypeEntityByItemCategory(ItemCategory icat)
	{
		Collection<ItemCategoryApplicantType> rt = null;
		
		try {
			
				String hql = "select rt from " +
						"ItemCategoryApplicantType rt where (rt.itemCategory.id) = " +icat.getId();
				log.info("Get hql = " + hql);
				rt = (Collection<ItemCategoryApplicantType>) swpService.getAllRecordsByHQL(hql);
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public ItemCategorySub getOneItemCategorySub() {
		// TODO Auto-generated method stub
		ItemCategorySub rt = null;
		
		try {
			
				String hql = "select rt from ItemCategorySub rt";
				log.info("Get hql = " + hql);
				Collection<ItemCategorySub> rt1 = (Collection<ItemCategorySub>) swpService.getAllRecordsByHQL(hql, 0, 1);
				if(rt1!=null && rt1.size()>0)
					rt = rt1.iterator().next();
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	
	
	public Collection<Long> getItemCategoryAttachmentTypeByItemCategoryGrouped(
			ItemCategory itemCategory) {
		// TODO Auto-generated method stub
		Collection<Long> rt = null;
		
		try {
			
				String hql = "select rt.applicationAttachmentType.id from ItemCategoryAttachmentTypeAgency rt WHERE rt.itemCategory.id = " + itemCategory.getId() + " GROUP by " +
						"rt.applicationAttachmentType.id ORDER BY count(rt.applicationAttachmentType.id)";
				log.info("Get hql = " + hql);
				rt = (Collection<Long>) swpService.getAllRecordsByHQL(hql);
				
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt;
	}

	public ItemCategorySub getItemSubCategoryByHSCodeAndName(
			String itemSubCategoryHSCode, String itemSubCategoryName) {
		// TODO Auto-generated method stub
		Collection<ItemCategorySub> rt = null;
		ItemCategorySub rt1 = null;
		
		try {
			
				String hql = "select rt from ItemCategorySub rt WHERE lower(rt.name) = lower('"+itemSubCategoryName+"') OR " +
						"lower(rt.hsCode) = lower('"+itemSubCategoryHSCode+"')";
				log.info("Get hql = " + hql);
				rt = (Collection<ItemCategorySub>) swpService.getAllRecordsByHQL(hql);
				
				if(rt!=null && rt.size()>0)
					rt1 = rt.iterator().next();
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt1;
	}

	
	public ItemCategorySub getItemSubCategoryByHSCodeAndNameExceptId(
			Long id, String itemSubCategoryHSCode, String itemSubCategoryName) {
		// TODO Auto-generated method stub
		Collection<ItemCategorySub> rt = null;
		ItemCategorySub rt1 = null;
		
		try {
			
				String hql = "select rt from ItemCategorySub rt WHERE (lower(rt.name) = lower('"+itemSubCategoryName+"') AND " +
						"lower(rt.hsCode) = lower('"+itemSubCategoryHSCode+"')) AND rt.id!=" + id;
				log.info("Get hql = " + hql);
				rt = (Collection<ItemCategorySub>) swpService.getAllRecordsByHQL(hql);
				if(rt!=null && rt.size()>0)
					rt1 = rt.iterator().next();
			
		} catch (HibernateException e) {
			log.error("",e);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			
		}
		return rt1;
	}
	

}
