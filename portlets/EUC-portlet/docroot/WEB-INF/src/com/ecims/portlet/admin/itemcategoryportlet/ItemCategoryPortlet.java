package com.ecims.portlet.admin.itemcategoryportlet;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.json.JSONException;

import smartpay.entity.Agency;
import smartpay.entity.ApplicationAttachmentType;
import smartpay.entity.Currency;
import smartpay.entity.ItemCategory;
import smartpay.entity.ItemCategoryApplicantType;
import smartpay.entity.ItemCategorySub;
import smartpay.entity.enumerations.ApplicantType;
import smartpay.service.SwpService;

import com.ecims.portlet.admin.itemcategoryportlet.ItemCategoryPortletState.ITEM_CATEGORY_ACTIONS;
import com.ecims.portlet.admin.itemcategoryportlet.ItemCategoryPortletState.VIEW_TABS;
import com.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.service.ServiceContext;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class ItemCategoryPortlet
 */
public class ItemCategoryPortlet extends MVCPortlet {
	private Logger log = Logger.getLogger(ItemCategoryPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	ItemCategoryPortletUtil util = ItemCategoryPortletUtil.getInstance();
	ServiceContext serviceContext = new ServiceContext();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	
	@Override
	public void init(PortletConfig config) throws PortletException {
		// TODO Auto-generated method stub
		log.info("Administrative portlet init called...");		
		pContext = config.getPortletContext();
		super.init(config);
		pConfig = config;
	    this.swpService = this.serviceLocator.getSwpService();
	}
	
	@Override
	public void render(RenderRequest renderRequest,
			RenderResponse renderResponse) throws PortletException, IOException {
		log.info("Administrative render called...");	
		PortletSession ps = renderRequest.getPortletSession();
		ItemCategoryPortletState portletState = 
				ItemCategoryPortletState.getInstance(renderRequest, renderResponse);

		log.info(">>>next page = " + renderRequest.getParameter("jspPage"));
		super.render(renderRequest, renderResponse);
	}

	@Override
	public void serveResource(ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) throws IOException,
			PortletException {

		String resourceID = resourceRequest.getResourceID();
		if (resourceID == null || resourceID.equals(""))
			return;
	}
	
	
	@Override
	public void processAction(ActionRequest aReq,
			ActionResponse aRes) throws IOException, PortletException {
		SessionMessages.add(aReq, pConfig.getPortletName()
				+ SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
		log.info("inside process Action");
		
		ItemCategoryPortletState portletState = ItemCategoryPortletState.getInstance(aReq, aRes);
		
		
		String action = aReq.getParameter("action");
		log.info("action == " + action);
		if (action == null) {
			log.info("----------------action value is null----------------");
			return;
		}
		else
		{
			log.info("----------------action value is " + action +"----------------");
		}
        if (portletState == null) {
			log.info("----------------portletState is null----------------");
			return;
		}
        
        /*************POST ACTIONS*********************/
        if(action.equalsIgnoreCase(ITEM_CATEGORY_ACTIONS.CREATE_NEW_ITEM_CATEGORY.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	String act = aReq.getParameter("act");
        	log.info("act ==" + act);
        	if(act!=null && act.equalsIgnoreCase("createnewagencystepone"))
        		createNewItemCategory(aReq, aRes, swpService, portletState);
        	if(act!=null && act.equalsIgnoreCase("createnewagencysteptwo"))
        		createNewItemCategoryStepTwo(aReq, aRes, swpService, portletState);
        	if(act!=null && act.equalsIgnoreCase("updatenewagencystepone"))
        		updateNewItemCategory(aReq, aRes, swpService, portletState);
        	if(act!=null && act.equalsIgnoreCase("updatenewagencysteptwo"))
        		updateNewItemCategoryStepTwo(aReq, aRes, swpService, portletState);
        	if(act!=null && act.equalsIgnoreCase("gobackupdatenewagencysteptwo"))
        		aRes.setRenderParameter("jspPage", "/html/itemcategoryportlet/updateitemcategory.jsp");
        	if(act!=null && act.equalsIgnoreCase("createnewsubcategory"))
        		createNewItemSubCategory(aReq, aRes, swpService, portletState);
        	if(act!=null && act.equalsIgnoreCase("updatesubcategory"))
        		updateItemSubCategory(aReq, aRes, swpService, portletState);
        	
        		
        }
        if(action.equalsIgnoreCase(ITEM_CATEGORY_ACTIONS.CREATE_NEW_ITEM_SUB_CATEGORY.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	//createNewItemSubCategory(aReq, aRes, swpService, portletState);
    		createNewItemSubCategory(aReq, aRes, swpService, portletState);
    		portletState.setCurrentTab(VIEW_TABS.VIEW_NEW_ITEM_SUB_CATEGORY);
        		
        }
        if(action.equalsIgnoreCase(ITEM_CATEGORY_ACTIONS.HANDLE_ITEM_CATEGORY_ACTIONS.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	//createNewItemSubCategory(aReq, aRes, swpService, portletState);
        	String act = aReq.getParameter("act");
        	if(act!=null && act.equalsIgnoreCase("edititemcategory"))
        	{
        		editItemCategory(aReq, aRes, portletState);
        		portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_ITEM_CATEGORY);
        	}
        	else if(act!=null && act.equalsIgnoreCase("viewmap"))
        		viewmapping(aReq, aRes, portletState);
        	else if(act!=null && act.equalsIgnoreCase("addhscode"))
        		addhscode(aReq, aRes, portletState);
        	else if(act!=null && act.equalsIgnoreCase("viewhscode"))
        		viewhscode(aReq, aRes, portletState);
        	
        		
        }
        if(action.equalsIgnoreCase(ITEM_CATEGORY_ACTIONS.HANDLE_ITEM_SUB_CATEGORY_ACTIONS.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	//createNewItemSubCategory(aReq, aRes, swpService, portletState);
        	String act = aReq.getParameter("act");
        	String actId = aReq.getParameter("actId");
        	
        	if(act!=null && act.equalsIgnoreCase("edititemsubcategory"))
        	{
        		Long idL = Long.valueOf(actId);
        		ItemCategorySub ics = (ItemCategorySub)portletState.getItemCategoryPortletUtil().getEntityObjectById(ItemCategorySub.class, idL);
        		if(ics!=null)
        		{
        			portletState.setItemCategorySubEntity(ics);
        			portletState.setApplyItemCategoryList((Collection<ItemCategory>)portletState.getItemCategoryPortletUtil().getAllEntity(ItemCategory.class));
        			aRes.setRenderParameter("jspPage", "/html/itemcategoryportlet/updateitemsubcategory.jsp");
        			portletState.setItemSubCategoryName(ics.getName());
        			portletState.setItemSubCategoryHSCode(ics.getHsCode());
        			portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_ITEM_SUB_CATEGORY);
        		}else
        		{
        			portletState.addError(aReq, "This HS Code could not be found on the System. Please try again", portletState);
        		}
        	}
        		
        }
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_ALL_ITEM_CATEGORY.name()))
        {
        	portletState.setItemCategoryListing(portletState.getItemCategoryPortletUtil().getAllItemCategory());
			aRes.setRenderParameter("jspPage", "/html/itemcategoryportlet/itemcategorylisting.jsp");
			portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_ITEM_CATEGORY);
        }
        
        
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_ALL_ITEM_SUB_CATEGORY.name()))
        {
        	portletState.setItemCategorySubListing((Collection<ItemCategorySub>)portletState.getItemCategoryPortletUtil().getAllEntity(ItemCategorySub.class));
			aRes.setRenderParameter("jspPage", "/html/itemcategoryportlet/itemcategorysublisting.jsp");
			portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_ITEM_SUB_CATEGORY);
        }
        
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_NEW_ITEM_CATEGORY.name()))
        {
        	Collection<Currency> endorsementDeskList = (Collection<Currency>)portletState.getItemCategoryPortletUtil().getAllEntity(Currency.class);
        	portletState.setCurrencyListing(endorsementDeskList);
			aRes.setRenderParameter("jspPage", "/html/itemcategoryportlet/newitemcategory.jsp");
        }
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_NEW_ITEM_SUB_CATEGORY.name()))
        {
        	Collection<Currency> endorsementDeskList = (Collection<Currency>)portletState.getItemCategoryPortletUtil().getAllEntity(Currency.class);
        	portletState.setCurrencyListing(endorsementDeskList);
			aRes.setRenderParameter("jspPage", "/html/itemcategoryportlet/newitemsubcategory.jsp");
        }
        
	}

	private void createNewItemSubCategory(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ItemCategoryPortletState portletState) {
		// TODO Auto-generated method stub
		String itemSubCategoryName = aReq.getParameter("itemSubCategoryName");
		String itemSubCategoryHSCode = aReq.getParameter("itemSubCategoryHSCode");
		String actId = aReq.getParameter("actId");
		portletState.setItemSubCategoryHSCode(itemSubCategoryHSCode);
		portletState.setItemSubCategoryName(itemSubCategoryName);
		Long actIdL = Long.valueOf(actId);
		ItemCategory ic = (ItemCategory)portletState.getItemCategoryPortletUtil().getEntityObjectById(ItemCategory.class, actIdL);
		
		if(itemSubCategoryName!=null && itemSubCategoryName.trim().length()>0 && 
				itemSubCategoryHSCode!=null && itemSubCategoryHSCode.trim().length()>0)
		{
			ItemCategorySub ics1 = (ItemCategorySub)portletState.getItemCategoryPortletUtil().getItemSubCategoryByHSCodeAndName(itemSubCategoryHSCode, itemSubCategoryName);
			if(ics1==null)
			{
				ItemCategorySub ics = new ItemCategorySub();
				ics.setHsCode(itemSubCategoryHSCode);
				ics.setItemCategory(ic);
				ics.setName(itemSubCategoryName);
				ics = (ItemCategorySub)swpService2.createNewRecord(ics);
				if(ics!=null)
				{
					portletState.addSuccess(aReq, "Item Sub Category - " + ics.getItemCategory().getItemCategoryName() + " " +
							"- was created successfully!", portletState);

					portletState.setItemSubCategoryHSCode(null);
					portletState.setItemSubCategoryName(null);
				}else
				{
					portletState.addError(aReq, "HS Code could not be created for the item category - " + ic.getItemCategoryName() + " " +
							"!", portletState);
				}
			}else
			{
				portletState.addError(aReq, "HS Code could not be created for the item category - " + ic.getItemCategoryName() + ". The HS Code details provided already belong to another HS Code" +
						"!", portletState);
			}
		}else
		{
			
			portletState.addError(aReq, "HS Code could not be created for the item category - " + ic.getItemCategoryName() + 
					"! Ensure you provide HS Codes and Item Category Names that do not exist previously on the system.", portletState);
		}
		aRes.setRenderParameter("jspPage", "/html/itemcategoryportlet/newitemsubcategory.jsp");
	}
	
	
	private void updateItemSubCategory(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ItemCategoryPortletState portletState) {
		// TODO Auto-generated method stub
		String itemCatagory = aReq.getParameter("ItemCatagory");
		String itemSubCategoryName = aReq.getParameter("itemSubCategoryName");
		String itemSubCategoryHSCode = aReq.getParameter("itemSubCategoryHSCode");
		portletState.setItemSubCategoryName(itemSubCategoryName);
		portletState.setItemSubCategoryHSCode(itemSubCategoryHSCode);
		String actId = aReq.getParameter("actId");
		Long actIdL = Long.valueOf(actId);
		Long ItemCatagoryIdL = Long.valueOf(itemCatagory);
		ItemCategorySub ic = (ItemCategorySub)portletState.getItemCategoryPortletUtil().getEntityObjectById(ItemCategorySub.class, actIdL);
		
		if(itemCatagory!=null && itemCatagory.length()>0 && itemSubCategoryName!=null && itemSubCategoryName.trim().length()>0 && 
				itemSubCategoryHSCode!=null && itemSubCategoryHSCode.trim().length()>0)
		{
			ItemCategorySub ics1 = (ItemCategorySub)portletState.getItemCategoryPortletUtil().getItemSubCategoryByHSCodeAndNameExceptId(ic.getId(), itemSubCategoryHSCode, itemSubCategoryName);
			if(ics1==null)
			{
				ItemCategory i_c = (ItemCategory)portletState.getItemCategoryPortletUtil().getEntityObjectById(ItemCategory.class, ItemCatagoryIdL);
				
				if(i_c!=null)
				{
					ic.setHsCode(itemSubCategoryHSCode);
					ic.setItemCategory(i_c);
					ic.setName(itemSubCategoryName);
					swpService2.updateRecord(ic);
					portletState.addSuccess(aReq, "HS Code - " + itemSubCategoryName + " " +
								"- was updated successfully!", portletState);
					portletState.setItemCategorySubListing((Collection<ItemCategorySub>)portletState.getItemCategoryPortletUtil().getAllEntity(ItemCategorySub.class));
					portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_ITEM_SUB_CATEGORY);
					aRes.setRenderParameter("jspPage", "/html/itemcategoryportlet/itemcategorysublisting.jsp");
					portletState.setItemSubCategoryHSCode(null);
					portletState.setItemSubCategoryName(null);
				}else
				{
					portletState.addError(aReq, "HS Code could not be updated for the item category - " + ic.getItemCategory().getItemCategoryName() + 
							"! Ensure you provide HS Codes and Item Category Names that do not exist previously on the system.", portletState);
					aRes.setRenderParameter("jspPage", "/html/itemcategoryportlet/newitemsubcategory.jsp");
				}
				
			}
		}else
		{
			
			portletState.addError(aReq, "HS Code could not be updated for the item category - " + ic.getItemCategory().getItemCategoryName() + 
					"! Ensure you provide HS Codes and Item Category Names that do not exist previously on the system.", portletState);
			aRes.setRenderParameter("jspPage", "/html/itemcategoryportlet/newitemsubcategory.jsp");
		}
	}

	private void addhscode(ActionRequest aReq, ActionResponse aRes,
			ItemCategoryPortletState portletState) {
		// TODO Auto-generated method stub
		String actId = aReq.getParameter("actId");
		Long actIdL = Long.valueOf(actId);
		ItemCategory ics = (ItemCategory)portletState.getItemCategoryPortletUtil().getEntityObjectById(ItemCategory.class, actIdL);
		portletState.setItemCategoryEntity(ics);
		portletState.setItemSubCategoryHSCode(null);
		portletState.setItemSubCategoryName(null);
		aRes.setRenderParameter("jspPage", "/html/itemcategoryportlet/newitemsubcategory.jsp");
	}
	
	
	private void viewhscode(ActionRequest aReq, ActionResponse aRes,
			ItemCategoryPortletState portletState) {
		// TODO Auto-generated method stub
		String actId = aReq.getParameter("actId");
		Long actIdL = Long.valueOf(actId);
		ItemCategory ics = (ItemCategory)portletState.getItemCategoryPortletUtil().getEntityObjectById(ItemCategory.class, actIdL);
		portletState.setItemCategoryEntity(ics);
		aRes.setRenderParameter("jspPage", "/html/itemcategoryportlet/newitemsubcategory.jsp");
	}

	private void viewmapping(ActionRequest aReq, ActionResponse aRes,
			ItemCategoryPortletState portletState) {
		// TODO Auto-generated method stub
		
	}

	private void editItemCategory(ActionRequest aReq, ActionResponse aRes,
			ItemCategoryPortletState portletState) {
		// TODO Auto-generated method stub
		String actId = aReq.getParameter("actId");
		Long actIdL = Long.valueOf(actId);
		ItemCategory itemCategory = (ItemCategory)portletState.getItemCategoryPortletUtil().getEntityObjectById(ItemCategory.class, actIdL);
		portletState.setItemCategoryEntity(itemCategory);
		Collection<ItemCategoryApplicantType> icat = (Collection<ItemCategoryApplicantType>)portletState.
				getItemCategoryPortletUtil().getItemCategoryApplicantTypeEntityByItemCategory(itemCategory);
		
		
		
		
		
		ArrayList<String> al = null;
		ArrayList<String> compulsory = null;
		ArrayList<ApplicantType> applicantTypeList = null;
		
		
		log.info("compulsory size==" + (compulsory!=null ? compulsory.size(): ""));
		
		portletState.setSelectedAgency(null);
		
		
		if(icat!=null && icat.size()>0)
		{
			applicantTypeList = new ArrayList<ApplicantType>();
			
			for(Iterator<ItemCategoryApplicantType> it = icat.iterator(); it.hasNext();)
			{
				ItemCategoryApplicantType icata = it.next();
				applicantTypeList.add(icata.getApplicantType());
			}
		}
		log.info("applicantTypeList size==" + (applicantTypeList!=null ? applicantTypeList.size(): ""));
		
		
		
		portletState.setApplicationAttachmentList(al);
		portletState.setApplicantTypeList(applicantTypeList);
		
		portletState.setCompulsory(compulsory);
		
		portletState.setAgencyListing((Collection<Agency>)portletState.getItemCategoryPortletUtil().getAllEntity(Agency.class));
		log.info("agencyList size==" + (portletState.getAgencyListing()!=null ? portletState.getAgencyListing().size(): ""));
		aRes.setRenderParameter("jspPage", "/html/itemcategoryportlet/updateitemcategory.jsp");
	}

	private void createNewItemCategoryStepTwo(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ItemCategoryPortletState portletState) {
		// TODO Auto-generated method stub
		log.info("createnewagencysteptwo");
		
		Collection<String> atcCol = portletState.getApplicationAttachmentList();
		if(atcCol!=null && atcCol.size()>0)
		{
			log.info("getApplicationAttachmentList size = " + portletState.getApplicationAttachmentList().size());
			String[] keyCompulsort = aReq.getParameterValues("compulsory");
			portletState.setCompulsory(null);
			if(keyCompulsort!=null && keyCompulsort.length>0)
			{
				log.info("keyCompulsort size = " + keyCompulsort.length);
				ArrayList<String> c1 = new ArrayList<String>();
				for(int c=0; c<keyCompulsort.length; c++)
				{
					c1.add(keyCompulsort[c]);
					log.info("keyCompulsort = " + keyCompulsort[c]);
				}
				portletState.setCompulsory(c1);
			}
			
			
		}
		
		
		/***ItemCategory****/
		ItemCategory itemCategory = portletState.getItemCategoryEntity();
		itemCategory = (ItemCategory)swpService.createNewRecord(itemCategory);
		
		if(itemCategory!=null)
		{
			/***ItemCategoryApplicantType****/
			Collection<ItemCategoryApplicantType> icat = portletState.getItemCategoryApplicantList();
			if(icat!=null && icat.size()>0)
			{
				for(Iterator<ItemCategoryApplicantType> it = icat.iterator(); it.hasNext();)
				{
					ItemCategoryApplicantType icatIt = it.next();
					swpService.createNewRecord(icatIt);
					log.info("ItemCategoryApplicantType created successfully");
				}
				
				/***ITEMCATEGORYMAP****/
				
				
			}
			portletState.addSuccess(aReq, "Item category - " + itemCategory.getItemCategoryName() + " " +
					"- was created successfully!", portletState);
			portletState.setItemCategoryEntity(null);
			portletState.setApplicationAttachmentList(null);
			portletState.setApplicantTypeList(null);
			portletState.setSelectedAgency(null);
			portletState.setCompulsory(null);
			portletState.setApplicantTypeList(null);
			portletState.setItemCategoryName(null);
			portletState.setItemCategoryListing(portletState.getItemCategoryPortletUtil().getAllItemCategory());
			portletState.setCurrentTab(VIEW_TABS.VIEW_NEW_ITEM_CATEGORY);
		}else
		{
			portletState.addError(aReq, "Item category - " + itemCategory.getItemCategoryName() + " " +
					"- could not be created!", portletState);
		}
	}

	private void createNewItemCategory(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, ItemCategoryPortletState portletState) {
		// TODO Auto-generated method stub

		portletState.setCurrentTab(VIEW_TABS.VIEW_NEW_ITEM_CATEGORY);
		String[] applicanttype = aReq.getParameterValues("applicanttype");
		String categoryname = aReq.getParameter("categoryname");
		portletState.setCategoryname(categoryname);
		List applicantTypeList = Arrays.asList(applicanttype);
		ArrayList<ApplicantType> al = new ArrayList<ApplicantType>();
		if(applicantTypeList!=null && applicantTypeList.size()>0)
		{
			for(Iterator<String> it = applicantTypeList.iterator(); it.hasNext();)
			{
				al.add(ApplicantType.fromString(it.next()));
			}
		}
		portletState.setApplicantTypeList(al);
		
		
		if(applicantTypeList!=null && applicantTypeList.size()>0 && 
				categoryname!=null && categoryname.length()>0)
		{
			try
			{
				ItemCategory ic = portletState.getItemCategoryPortletUtil().getItemCategorybyName(categoryname, null);
				if(ic==null)
				{
					ItemCategory itemCategory = new ItemCategory();
					itemCategory.setItemCategoryName(categoryname);
					//itemCategory = (ItemCategory)swpService.createNewRecord(itemCategory);
					portletState.setItemCategoryEntity(itemCategory);
					
					ArrayList<ItemCategoryApplicantType> appTypeList = new ArrayList<ItemCategoryApplicantType>();
					for(int i=0; i<applicanttype.length; i++)
					{
						ItemCategoryApplicantType icat = new ItemCategoryApplicantType();
						icat.setApplicantType(ApplicantType.fromString(applicanttype[i]));
						icat.setItemCategory(itemCategory);
						appTypeList.add(icat);
						//swpService.createNewRecord(icat);
					}
					portletState.setItemCategoryApplicantList(appTypeList);
					
					aRes.setRenderParameter("jspPage", "/html/itemcategoryportlet/newitemcategory_steptwo.jsp");
					//portletState.addSuccess(aReq, "New item category successfully created.", portletState);
				}else
				{
					portletState.addError(aReq, "Creating the new item category was not successful. An item category already has the item category name you provided.", portletState);
				}
				
			}catch(NumberFormatException e)
			{
				portletState.addError(aReq, "Ensure you provide the details required when filling the form. Details of the name of the Item Category, " +
						"Type of Applicant and Type(s) of attachment must be provided",  portletState);
			}
		}else
		{
			portletState.addError(aReq, "Ensure you provide the details required when filling the form. Details of the name of the Item Category, " +
					"Type of Applicant and Type(s) of attachment must be provided",  portletState);
		}
	}
	
	
	
	
	
	private void updateNewItemCategory(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, ItemCategoryPortletState portletState) {
		// TODO Auto-generated method stub
		String[] applicanttype = aReq.getParameterValues("applicanttype");
		String categoryname = aReq.getParameter("categoryname");
		portletState.setCategoryname(categoryname);
		ItemCategory ic = portletState.getItemCategoryEntity();
		ic.setItemCategoryName(categoryname);
		portletState.setItemCategoryEntity(ic);
		
		List applicantTypeList = Arrays.asList(applicanttype);
		ArrayList<ApplicantType> al = new ArrayList<ApplicantType>();
		if(applicantTypeList!=null && applicantTypeList.size()>0)
		{
			for(Iterator<String> it = applicantTypeList.iterator(); it.hasNext();)
			{
				al.add(ApplicantType.fromString(it.next()));
			}
		}
		portletState.setApplicantTypeList(al);
		
		if(applicantTypeList!=null && applicantTypeList.size()>0 && 
				categoryname!=null && categoryname.length()>0)
		{
			try
			{
				ItemCategory ic1 = portletState.getItemCategoryPortletUtil().getItemCategorybyName(categoryname, portletState.getItemCategoryEntity().getId());
				if(ic1==null)
				{
					ItemCategory itemCategory = portletState.getItemCategoryEntity();
					itemCategory.setItemCategoryName(categoryname);
					//itemCategory = (ItemCategory)swpService.createNewRecord(itemCategory);
					portletState.setItemCategoryEntity(itemCategory);
					
					ArrayList<ItemCategoryApplicantType> appTypeList = new ArrayList<ItemCategoryApplicantType>();
					for(int i=0; i<applicanttype.length; i++)
					{
						ItemCategoryApplicantType icat = new ItemCategoryApplicantType();
						icat.setApplicantType(ApplicantType.fromString(applicanttype[i]));
						icat.setItemCategory(itemCategory);
						appTypeList.add(icat);
						//swpService.createNewRecord(icat);
					}
					portletState.setItemCategoryApplicantList(appTypeList);
					
					
					
					aRes.setRenderParameter("jspPage", "/html/itemcategoryportlet/updateitemcategory_steptwo.jsp");
					//portletState.addSuccess(aReq, "New item category successfully created.", portletState);
				}else
				{
					portletState.addError(aReq, "Creating the new item category was not successful. An item category already has the item category name you provided.", portletState);
				}
				
			}catch(NumberFormatException e)
			{
				portletState.addError(aReq, "Ensure you provide the details required when filling the form. Details of the name of the Item Category, " +
						"Type of Applicant and Type(s) of attachment must be provided",  portletState);
			}
		}else
		{
			portletState.addError(aReq, "Ensure you provide the details required when filling the form. Details of the name of the Item Category, " +
					"Type of Applicant and Type(s) of attachment must be provided",  portletState);
		}
	}
	
	
	private void updateNewItemCategoryStepTwo(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			ItemCategoryPortletState portletState) {
		// TODO Auto-generated method stub
		log.info("createnewagencysteptwo");
		
		Collection<String> atcCol = portletState.getApplicationAttachmentList();
		if(atcCol!=null && atcCol.size()>0)
		{
			log.info("getApplicationAttachmentList size = " + portletState.getApplicationAttachmentList().size());
			String[] keyCompulsort = aReq.getParameterValues("compulsory");
			portletState.setCompulsory(null);
			if(keyCompulsort!=null && keyCompulsort.length>0)
			{
				log.info("keyCompulsort size = " + keyCompulsort.length);
				ArrayList<String> c1 = new ArrayList<String>();
				for(int c=0; c<keyCompulsort.length; c++)
				{
					c1.add(keyCompulsort[c]);
					log.info("keyCompulsort = " + keyCompulsort[c]);
				}
				portletState.setCompulsory(c1);
			}
			
			try
			{
				HashMap<String, List<String>> jsonObject = new HashMap<String, List<String>>();
				for(Iterator<String> it = atcCol.iterator(); it.hasNext();)
				{
					String ac = it.next();
					String keyAgency = ac.replace(" ", "") + "_agency";
					log.info("keyAgency = " + keyAgency);
					String[] agency = aReq.getParameterValues(keyAgency);
					log.info("agency length = " + agency.length);
					List<String> agLis = null;
					if(agency!=null && agency.length>0)
					{
						agLis = Arrays.asList(agency);
					}
					jsonObject.put(keyAgency, agLis);
				}
				portletState.setSelectedAgency(jsonObject);

				log.info("jsonObject length = " + jsonObject.size());
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
		/***ItemCategory****/
		ItemCategory itemCategory = portletState.getItemCategoryEntity();
		swpService.updateRecord(itemCategory);
		
		
		
		if(itemCategory!=null)
		{
			
			Collection<ItemCategoryApplicantType> icat1 = (Collection<ItemCategoryApplicantType>)portletState.
					getItemCategoryPortletUtil().getItemCategoryApplicantTypeEntityByItemCategory(itemCategory);
			if(icat1!=null && icat1.size()>0)
			{
				for(Iterator<ItemCategoryApplicantType> it= icat1.iterator(); it.hasNext();)
				{
					swpService.deleteRecord((ItemCategoryApplicantType)it.next());
				}
			}
			
			/****/
			
			
			/***ItemCategoryApplicantType****/
			Collection<ItemCategoryApplicantType> icat = portletState.getItemCategoryApplicantList();
			if(icat!=null && icat.size()>0)
			{
				for(Iterator<ItemCategoryApplicantType> it = icat.iterator(); it.hasNext();)
				{
					ItemCategoryApplicantType icatIt = it.next();
					swpService.createNewRecord(icatIt);
					log.info("ItemCategoryApplicantType created successfully");
				}
				
				/***ITEMCATEGORYMAP****/
				
			}
			portletState.addSuccess(aReq, "Item category - " + itemCategory.getItemCategoryName() + " " +
					"- was updated successfully!", portletState);
			portletState.setItemCategoryListing(portletState.getItemCategoryPortletUtil().getAllItemCategory());
			portletState.setItemCategoryEntity(null);
			portletState.setApplicationAttachmentList(null);
			portletState.setApplicantTypeList(null);
			portletState.setSelectedAgency(null);
			portletState.setCompulsory(null);
			portletState.setApplicantTypeList(null);
			portletState.setItemCategoryName(null);
			portletState.setCurrentTab(VIEW_TABS.VIEW_NEW_ITEM_CATEGORY);
		}else
		{
			portletState.addError(aReq, "Item category - " + itemCategory.getItemCategoryName() + " " +
					"- could not be updated!", portletState);
		}
	}
}
