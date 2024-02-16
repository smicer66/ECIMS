package com.ecims.portlet.currencymanagement;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import smartpay.entity.PortalUser;
import smartpay.entity.WokFlowSetting;

import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.util.bridges.mvc.MVCPortlet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
/**
 * Portlet implementation class CurrencyManagementPortlet
 */
public class CurrencyManagementPortlet extends MVCPortlet {

	
	public void searchWorkflowAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		String itemCategory  = aRequest.getParameter("itemcategory");
		
		aResponse.setRenderParameter("tabs1", "Workflow Management");
		aResponse.setRenderParameter("itemcategoryId", itemCategory);
		
		
	}
	
	public void getWorkflowAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		
		WorkflowPortletUtil wutil = new WorkflowPortletUtil();
		
		String itemcatId = aRequest.getParameter("getitemcategory");
		
		Collection<WokFlowSetting> setting = wutil.getWorkflowByItemCategory(itemcatId);
		
			if(setting!=null && setting.size()>0)
			{
				int counter = 1;
				for(Iterator<WokFlowSetting> t = setting.iterator(); t.hasNext();)
				{
					WokFlowSetting t1 = t.next();
					aResponse.setRenderParameter("positionid"+counter, String.valueOf(t1.getPositionId()));
					//swpService.deleteRecord(t1);
					counter += 1;
				}
			}
			aResponse.setRenderParameter("itemcategoryId", itemcatId);
			aResponse.setRenderParameter("jspPage", "/html/currencymanagementportlet/addWorkflow.jsp");
	}
	public void addWorkflowAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		
		WorkflowPortletUtil wutil = new WorkflowPortletUtil();
		String itemCategory  = aRequest.getParameter("itemcategory");
		String totalValue = aRequest.getParameter("total");
		int total = Integer.parseInt(totalValue);
		String portalUserId = "68";
		PortalUser portalUser = wutil.getPortalUserById(portalUserId);
		int varCntl ;
		String position ;
		String position2;
		String agency;
		ArrayList<String> positions = new ArrayList<String>();
		
		//populate position list
		for(int i = 0; i < total ; i ++){
			varCntl = (i + 1);
			position = aRequest.getParameter("positionid"+varCntl);
			if(position.equals("")){
				continue;
			}
			aResponse.setRenderParameter("positionid"+varCntl, position);
			//System.out.println("it is "+position);
			positions.add(position);
		}
		
		//search position list for wrong entries
		int occurrences;
		boolean formValid = true;
		for (String s : positions){
			 occurrences = Collections.frequency(positions, s);
		    if(occurrences > 1){
		    	formValid = false;
		    	break;
		    }
		    
		}
		    	
		
		
		//save data if no error exist
		if(formValid)
		{
			//delete all saved settings in this item category
			wutil.deleteWokflowByItem(itemCategory);
			//save 
			for(int j = 0; j < total ; j ++){
				varCntl = (j + 1);
				position2 = aRequest.getParameter("positionid"+varCntl);
				if(position2.equals("")){
					continue;
				}
				agency = aRequest.getParameter("agencyid"+varCntl);
				wutil.createNewWorkflow(itemCategory, position2, agency, portalUser);
				//wutil.c
			}
			//set tab parameter
			SessionMessages.add(aRequest, "success");
			aResponse.setRenderParameter("tabs1", "Workflow Management");
		//	portletState.addError(aReq, "Select the type of user you wish to create.", portletState);
		//	aRes.setRenderParameter("jspPage", "/html/currencymanagementportlet/register_individual/stepzero.jsp");
		}else
		{
			SessionErrors.add(aRequest, "errorUpdate");
			
			aResponse.setRenderParameter("jspPage", "/html/currencymanagementportlet/addWorkflow.jsp");
		}
		aResponse.setRenderParameter("itemcategoryId", itemCategory);
		
	}
	
	
	public void addAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
		CurrencyManagementPortletUtil util = new CurrencyManagementPortletUtil();
		String name = aRequest.getParameter("name");
		String htmlEntity = aRequest.getParameter("htmlEntity");
		String portalUserId = "68";
		PortalUser portalUser = util.getPortalUserById(portalUserId);
		boolean valid = true;
		try {
			util.createNewCurrency(htmlEntity, name, valid, portalUser);
			// process adding
			SessionMessages.add(aRequest, "success");
		} catch (Exception e) {
			SessionErrors.add(aRequest, "error");
		}

		// super.addAction(aRequest, aResponse);
	}

	public void editAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
		CurrencyManagementPortletUtil util = new CurrencyManagementPortletUtil();
		String name = aRequest.getParameter("name");
		String htmlEntity = aRequest.getParameter("htmlEntity");
		String currencyKey = aRequest.getParameter("currencyKey");

		boolean valid = true;
		try {
			util.updateCurrency(currencyKey, htmlEntity, name, valid, null);
			// process adding
			SessionMessages.add(aRequest, "successUpdate");
		} catch (Exception e) {
			SessionErrors.add(aRequest, "errorUpdate");
		}

		// super.addAction(aRequest, aResponse);
	}
	
	public void deleteAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
		CurrencyManagementPortletUtil util = new CurrencyManagementPortletUtil();
		String currencyKey = aRequest.getParameter("currencyKey");

		try {
			util.deleteCurrencyById(currencyKey);
			aResponse.setRenderParameter("tabs1", "Agency Management");
			aResponse.setRenderParameter("successMessage", "Currency created successfully!");
			// process adding
			SessionMessages.add(aRequest, "successUpdate");
		} catch (Exception e) {
			SessionErrors.add(aRequest, "errorUpdate");
		}

		// super.addAction(aRequest, aResponse);
	}
	
	public void addAgencyAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
		AgencyManagementPortletUtil util = new AgencyManagementPortletUtil();
		String agencyName = aRequest.getParameter("agencyName");
		String agencyPhone = aRequest.getParameter("agencyPhone");
		String agencyEmail = aRequest.getParameter("agencyEmail");
		String agencyType = aRequest.getParameter("agencyType");
		String contactName = aRequest.getParameter("contactName");
		String portalUserId = "68";
		PortalUser portalUser = util.getPortalUserById(portalUserId);
		
		try {
			util.createNewAgency(agencyName, contactName, agencyPhone, agencyEmail, agencyType, null);
			// process adding
			aResponse.setRenderParameter("tabs1", "Agency Management");
			aResponse.setRenderParameter("successMessage", "Agency created successfully!");
			SessionMessages.add(aRequest, "success");
		} catch (Exception e) {
			SessionErrors.add(aRequest, "error");
		}

		// super.addAction(aRequest, aResponse);
	}

	public void editAgencyAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
		AgencyManagementPortletUtil util = new AgencyManagementPortletUtil();
		String agencyName = aRequest.getParameter("agencyName");
		String agencyPhone = aRequest.getParameter("agencyPhone");
		String agencyEmail = aRequest.getParameter("agencyEmail");
		String agencyType = aRequest.getParameter("agencyType");
		String contactName = aRequest.getParameter("contactName");
		String portalUserId = "68";
		String agencyKey = aRequest.getParameter("agencyKey");

		PortalUser portalUser = util.getPortalUserById(portalUserId);
		
		try {
			util.updateAgency(agencyKey, agencyName, contactName, agencyPhone, agencyEmail, agencyType, portalUser);
			// process adding
			SessionMessages.add(aRequest, "successUpdate");
			aResponse.setRenderParameter("tabs1", "Agency Management");

		} catch (Exception e) {
			SessionErrors.add(aRequest, "error");
		}

		// super.addAction(aRequest, aResponse);
	}
	
	public void deleteAgencyAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
	AgencyManagementPortletUtil util = new AgencyManagementPortletUtil();
		String agencyKey = aRequest.getParameter("agencyKey");

		try {
			util.deleteAgencyById(agencyKey);
			// process adding
			SessionMessages.add(aRequest, "successUpdate");
		} catch (Exception e) {
			SessionErrors.add(aRequest, "errorUpdate");
		}

		// super.addAction(aRequest, aResponse);
	}
	
	public void addCountryAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
		CountryManagementPortletUtil util = new CountryManagementPortletUtil();
		String name = aRequest.getParameter("name");
		String code = aRequest.getParameter("code");
		String portalUserId = "68";
		PortalUser portalUser = util.getPortalUserById(portalUserId);
		
		try {
			util.createNewCountry(name, code, portalUser);
			// process adding
			aResponse.setRenderParameter("tabs1", "Country Management");
			aResponse.setRenderParameter("successMessage", "Country created successfully!");
			SessionMessages.add(aRequest, "success");
		} catch (Exception e) {
			SessionErrors.add(aRequest, "error");
		}

		// super.addAction(aRequest, aResponse);
	}

	public void editCountryAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
		CountryManagementPortletUtil util = new CountryManagementPortletUtil();
		String name = aRequest.getParameter("name");
		String code = aRequest.getParameter("code");
		String portalUserId = "68";
		String countryKey = aRequest.getParameter("countryKey");

		PortalUser portalUser = util.getPortalUserById(portalUserId);
		
		try {
			util.updateCountry(countryKey, name, code, portalUser);
			// process adding
			SessionMessages.add(aRequest, "successUpdate");
			aResponse.setRenderParameter("tabs1", "Country Management");

		} catch (Exception e) {
			SessionErrors.add(aRequest, "error");
		}

		// super.addAction(aRequest, aResponse);
	}
	
	public void deleteCountryAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
	AgencyManagementPortletUtil util = new AgencyManagementPortletUtil();
		String agencyKey = aRequest.getParameter("agencyKey");

		try {
			util.deleteAgencyById(agencyKey);
			// process adding
			SessionMessages.add(aRequest, "successUpdate");
		} catch (Exception e) {
			SessionErrors.add(aRequest, "errorUpdate");
		}

		// super.addAction(aRequest, aResponse);
	}

	public void addStateAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
		StateManagementPortletUtil util = new StateManagementPortletUtil();
		String name = aRequest.getParameter("name");
		String countryId = aRequest.getParameter("countryId");
		String portalUserId = "68";
		PortalUser portalUser = util.getPortalUserById(portalUserId);
		
		try {
			util.createNewState(name, countryId, portalUser);
			// process adding
			aResponse.setRenderParameter("tabs1", "State Management");
			aResponse.setRenderParameter("successMessage", "State created successfully!");
			SessionMessages.add(aRequest, "success");
		} catch (Exception e) {
			SessionErrors.add(aRequest, "error");
		}

		// super.addAction(aRequest, aResponse);
	}

	public void editStateAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
		StateManagementPortletUtil util = new StateManagementPortletUtil();
		String name = aRequest.getParameter("name");
		String countryId = aRequest.getParameter("countryId");
		String portalUserId = "68";
		String stateKey = aRequest.getParameter("stateKey");

		PortalUser portalUser = util.getPortalUserById(portalUserId);
		
		try {
			util.updateState(stateKey, name, countryId, portalUser);
			// process adding
			SessionMessages.add(aRequest, "successUpdate");
			aResponse.setRenderParameter("tabs1", "State Management");

		} catch (Exception e) {
			SessionErrors.add(aRequest, "error");
		}

		// super.addAction(aRequest, aResponse);
	}
	
	public void deleteStateAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
	AgencyManagementPortletUtil util = new AgencyManagementPortletUtil();
		String agencyKey = aRequest.getParameter("agencyKey");

		try {
			util.deleteAgencyById(agencyKey);
			// process adding
			SessionMessages.add(aRequest, "successUpdate");
		} catch (Exception e) {
			SessionErrors.add(aRequest, "errorUpdate");
		}

		// super.addAction(aRequest, aResponse);
	}

	public void addItemCategoryAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
		ItemCategoryManagementPortletUtil util = new ItemCategoryManagementPortletUtil();
		String name = aRequest.getParameter("name");
		String applicantType = aRequest.getParameter("applicantType");
		String portalUserId = "68";
		PortalUser portalUser = util.getPortalUserById(portalUserId);
		
		try {
			util.createNewItemCategory(name, applicantType, portalUser);
			//util.createNewState(name, countryId, portalUser);
			// process adding
			aResponse.setRenderParameter("tabs1", "Item Category Management");
			aResponse.setRenderParameter("successMessage", "Item Category created successfully!");
			SessionMessages.add(aRequest, "success");
		} catch (Exception e) {
			SessionErrors.add(aRequest, "error");
		}

		// super.addAction(aRequest, aResponse);
	}

	public void editItemCategoryAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
		ItemCategoryManagementPortletUtil util = new ItemCategoryManagementPortletUtil();
		String name = aRequest.getParameter("name");
		String applicantType = aRequest.getParameter("applicantType");
		String portalUserId = "68";
		String itemcatKey = aRequest.getParameter("categoryKey");

		PortalUser portalUser = util.getPortalUserById(portalUserId);
		
		try {
			util.updateItemCategory(itemcatKey, name, applicantType, portalUser);
			// process adding
			SessionMessages.add(aRequest, "successUpdate");
			aResponse.setRenderParameter("tabs1", "Item Category Management");

		} catch (Exception e) {
			SessionErrors.add(aRequest, "error");
		}

		// super.addAction(aRequest, aResponse);
	}
	
	public void deleteItemCategoryAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
		AgencyManagementPortletUtil util = new AgencyManagementPortletUtil();
		String agencyKey = aRequest.getParameter("agencyKey");

		try {
			util.deleteAgencyById(agencyKey);
			// process adding
			SessionMessages.add(aRequest, "successUpdate");
		} catch (Exception e) {
			SessionErrors.add(aRequest, "errorUpdate");
		}

		// super.addAction(aRequest, aResponse);
	}

	public void addItemCategorySubAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
		ItemCategorySubManagementPortletUtil util = new ItemCategorySubManagementPortletUtil();
		String name = aRequest.getParameter("name");
		String itemCategory = aRequest.getParameter("itemCategory");
		String hsCode = aRequest.getParameter("hsCode");
		String portalUserId = "68";
		PortalUser portalUser = util.getPortalUserById(portalUserId);
		
		try {
			util.createNewItemCategorySub(name, hsCode, itemCategory, portalUser);
			//util.createNewState(name, countryId, portalUser);
			// process adding
			aResponse.setRenderParameter("tabs1", "Item SubCategory Management");
			aResponse.setRenderParameter("successMessage", "Item SubCategory created successfully!");
			SessionMessages.add(aRequest, "success");
		} catch (Exception e) {
			SessionErrors.add(aRequest, "error");
		}

		// super.addAction(aRequest, aResponse);
	}

	public void editItemCategorySubAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
		ItemCategorySubManagementPortletUtil util = new ItemCategorySubManagementPortletUtil();
		String name = aRequest.getParameter("name");
		String itemCategory = aRequest.getParameter("itemCategory");
		String hsCode = aRequest.getParameter("hsCode");
		String portalUserId = "68";
		String itemsubcatKey = aRequest.getParameter("subcategoryKey");

		PortalUser portalUser = util.getPortalUserById(portalUserId);
		
		try {
			util.updateItemCategorySub(itemsubcatKey, name, hsCode, itemCategory, portalUser);
			// process adding
			SessionMessages.add(aRequest, "successUpdate");
			aResponse.setRenderParameter("tabs1", "Item SubCategory Management");

		} catch (Exception e) {
			SessionErrors.add(aRequest, "error");
		}

		// super.addAction(aRequest, aResponse);
	}
	
	public void deleteItemCategorySubAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
		AgencyManagementPortletUtil util = new AgencyManagementPortletUtil();
		String agencyKey = aRequest.getParameter("agencyKey");

		try {
			util.deleteAgencyById(agencyKey);
			// process adding
			SessionMessages.add(aRequest, "successUpdate");
		} catch (Exception e) {
			SessionErrors.add(aRequest, "errorUpdate");
		}

		// super.addAction(aRequest, aResponse);
	}

	public void addQuantityUnitAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
		QuantityUnitManagementPortletUtil util = new QuantityUnitManagementPortletUtil();
		String name = aRequest.getParameter("name");
		String status = aRequest.getParameter("status");
		String unit = aRequest.getParameter("unit");
		String portalUserId = "68";
		PortalUser portalUser = util.getPortalUserById(portalUserId);
		
		try {
			util.createNewQuantityUnit(name, unit, status, portalUser);
			//util.createNewState(name, countryId, portalUser);
			// process adding
			aResponse.setRenderParameter("tabs1", "QuantityUnit Management");
			aResponse.setRenderParameter("successMessage", "Unit created successfully!");
			SessionMessages.add(aRequest, "success");
		} catch (Exception e) {
			SessionErrors.add(aRequest, "error");
		}

		// super.addAction(aRequest, aResponse);
	}

	public void editQuantityUnitAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
		QuantityUnitManagementPortletUtil util = new QuantityUnitManagementPortletUtil();
		String name = aRequest.getParameter("name");
		String status = aRequest.getParameter("status");
		String unit = aRequest.getParameter("unit");
		String portalUserId = "68";
		String unitKey = aRequest.getParameter("unitKey");

		PortalUser portalUser = util.getPortalUserById(portalUserId);
		
		try {
			util.updateQuantityUnit(unitKey, name, unit, status, portalUser);
			// process adding
			SessionMessages.add(aRequest, "successUpdate");
			aResponse.setRenderParameter("tabs1", "QuantityUnit Management");

		} catch (Exception e) {
			SessionErrors.add(aRequest, "error");
		}

		// super.addAction(aRequest, aResponse);
	}
	
	public void deleteQuantityUnitAction(ActionRequest aRequest, ActionResponse aResponse)
			throws IOException, PortletException {
		// PortletPreferences prefs = aRequest.getPreferences();
		AgencyManagementPortletUtil util = new AgencyManagementPortletUtil();
		String agencyKey = aRequest.getParameter("agencyKey");

		try {
			util.deleteAgencyById(agencyKey);
			// process adding
			SessionMessages.add(aRequest, "successUpdate");
		} catch (Exception e) {
			SessionErrors.add(aRequest, "errorUpdate");
		}

		// super.addAction(aRequest, aResponse);
	}

}
