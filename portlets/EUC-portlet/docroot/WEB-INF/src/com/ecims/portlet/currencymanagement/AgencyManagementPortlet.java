package com.ecims.portlet.currencymanagement;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import smartpay.entity.PortalUser;

import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class CurrencyManagementPortlet
 */
public class AgencyManagementPortlet extends MVCPortlet {

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
			// process adding
			SessionMessages.add(aRequest, "successUpdate");
		} catch (Exception e) {
			SessionErrors.add(aRequest, "errorUpdate");
		}

		// super.addAction(aRequest, aResponse);
	}
}
