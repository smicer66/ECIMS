package com.ecims.portlet.moneymgmt;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

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

import smartpay.entity.Currency;
import smartpay.entity.EndorsementDesk;
import smartpay.service.SwpService;

import com.ecims.portlet.moneymgmt.MoneyMgmtPortletState.CURRENCY_ACTIONS;
import com.ecims.portlet.moneymgmt.MoneyMgmtPortletState.VIEW_TABS;
import com.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.service.ServiceContext;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class MoneyMgmtPortlet
 */
public class MoneyMgmtPortlet extends MVCPortlet {
	private Logger log = Logger.getLogger(MoneyMgmtPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	MoneyMgmtPortletUtil util = MoneyMgmtPortletUtil.getInstance();
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
		MoneyMgmtPortletState portletState = 
				MoneyMgmtPortletState.getInstance(renderRequest, renderResponse);

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
		
		MoneyMgmtPortletState portletState = MoneyMgmtPortletState.getInstance(aReq, aRes);
		
		
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
        if(action.equalsIgnoreCase(CURRENCY_ACTIONS.CREATE_NEW_CURRENCY.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	createNewMoney(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(CURRENCY_ACTIONS.EDIT_A_CURRENCY.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	saveMoney(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(CURRENCY_ACTIONS.DELETE_A_CURRENCY.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	deleteMoney(aReq, aRes, swpService, portletState);
        		
        }
        
        if(action.equalsIgnoreCase(CURRENCY_ACTIONS.HANDLE_ACTION_ON_CURRENCY.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	handleMoneyActionsFromList(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_CREATE_NEW_CURRENCY.name()))
        {
			aRes.setRenderParameter("jspPage", "/html/moneymgmtportlet/createnewmoney.jsp");
			portletState.setCurrentTab(VIEW_TABS.VIEW_CREATE_NEW_CURRENCY);
        }
        
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_ALL_CURRENCY.name()))
        {
        	Collection<Currency> endorsementDeskList = (Collection<Currency>)portletState.getMoneyMgmtPortletUtil().getAllEntity(Currency.class);
        	portletState.setCurrencyListing(endorsementDeskList);
			aRes.setRenderParameter("jspPage", "/html/moneymgmtportlet/moneylisting.jsp");
			portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_CURRENCY);
        }
        
	}

	
	
	private void handleMoneyActionsFromList(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			MoneyMgmtPortletState portletState) {
		// TODO Auto-generated method stub
		String selectedMoneyId = aReq.getParameter("selectedMoneyId");
		String selectedMoneyAction = aReq.getParameter("selectedMoneyAction");
		if(selectedMoneyId!=null && selectedMoneyAction!=null)
		{
			try
			{
				Long eId = Long.valueOf(selectedMoneyId);
				Currency ed = (Currency)portletState.getMoneyMgmtPortletUtil().getEntityObjectById(Currency.class, eId);
				portletState.setCurrencyEntity(ed);
				if(ed!=null)
				{
					if(selectedMoneyAction.equals("editMoney"))
						aRes.setRenderParameter("jspPage", "/html/moneymgmtportlet/editMoney.jsp");
					else if(selectedMoneyAction.equals("deleteMoney"))
						deleteMoney(aReq, aRes,swpService2, portletState);
					
				}else
				{
					portletState.addError(aReq, "Ensure you have selected a valid currency before you can proceed", portletState);
				}
			}catch(NumberFormatException e)
			{
				e.printStackTrace();
				portletState.addError(aReq, "Ensure you have selected a valid currency before you can proceed", portletState);
			}
			
		}else
		{
			portletState.addError(aReq, "Ensure you have selected a valid currency before you can proceed", portletState);
		}
	}

	
	private void deleteMoney(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, MoneyMgmtPortletState portletState) {
		// TODO Auto-generated method stub
		String selectedMoneyId = aReq.getParameter("selectedMoneyId");
		if(selectedMoneyId!=null)
		{
			try
			{
				Long agId = Long.valueOf(selectedMoneyId);
				Currency currency = (Currency)portletState.getMoneyMgmtPortletUtil().getEntityObjectById(Currency.class, agId);
				currency.setValid(Boolean.FALSE);
				swpService.updateRecord(currency);
				portletState.addSuccess(aReq, "Status of Currency has been changed to deleted successfully", portletState);
			}catch(NumberFormatException e)
			{
				e.printStackTrace();
				portletState.addError(aReq, "Ensure you have selected a valid currency before you can proceed", portletState);
			}
			
		}else
		{
			portletState.addError(aReq, "Ensure you have selected a valid currency before you can proceed", portletState);
		}
	}


	private void createNewMoney(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, MoneyMgmtPortletState portletState) {
		// TODO Auto-generated method stub
		  String currencyName = aReq.getParameter("currencyName");
		  String htmlName = aReq.getParameter("htmlName");
		  
		  portletState.setCurrencyName(currencyName);
		  portletState.setHtmlName(htmlName);
		  
		  if(currencyName!=null && currencyName.trim().length()>0 && htmlName!=null && 
				  htmlName.trim().length()>0)
		  {
			  Currency ag = portletState.getMoneyMgmtPortletUtil().getCurrencyBySymbolOrName(currencyName, htmlName, null, Boolean.FALSE);
			  if(ag!=null)
			  {
				  portletState.addError(aReq, "A currency of this type with the currency name or symbol provided already exists in this system", 
						  portletState);
			  }else
			  {
				  Currency currency= new Currency();
				  currency.setHtmlEntity(htmlName);
				  currency.setName(currencyName);
				  currency.setValid(Boolean.TRUE);
				  currency = (Currency)swpService2.createNewRecord(currency);
				  if(currency!=null)
				  {
					  portletState.addSuccess(aReq, "Currency successfully created", portletState);
					  portletState.setCurrencyName(null);
					  portletState.setHtmlName(null);
				  }
				  else
				  {
					  portletState.addError(aReq, "Currency not successfully created", portletState);
				  }
			  }
		  }else
		  {
			  portletState.addError(aReq, "Ensure you provide all the data required", portletState);
		  }
	}
	
	

	private void saveMoney(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, MoneyMgmtPortletState portletState) {
		// TODO Auto-generated method stub
		  String currencyName = aReq.getParameter("currencyName");
		  String htmlName = aReq.getParameter("htmlName");
		  String selectedId = aReq.getParameter("selectedId");
		  
		  portletState.setCurrencyName(currencyName);
		  portletState.setHtmlName(htmlName);
		  
		  if(currencyName!=null && !currencyName.equals("") && htmlName!=null && 
				  htmlName.length()>0)
		  {
			  Long id = Long.valueOf(selectedId);
			  Currency ag = portletState.getMoneyMgmtPortletUtil().getCurrencyBySymbolOrName(currencyName, htmlName, id, Boolean.FALSE);
			  if(ag!=null)
			  {
				  portletState.addError(aReq, "A currency of this type with the currency name or symbol provided already exists in this system", 
						  portletState);
			  }else
			  {
				  Currency currency= new Currency();
				  currency.setHtmlEntity(htmlName);
				  currency.setName(currencyName);
				  currency = (Currency)swpService2.createNewRecord(currency);
				  if(currency!=null)
				  {
					  portletState.addSuccess(aReq, "Currency successfully created", portletState);
					  portletState.setCurrencyName(null);
					  portletState.setHtmlName(null);
				  }
				  
			  }
		  }else
		  {
			  portletState.addError(aReq, "Ensure you provide all the data required", portletState);
		  }
	}

}
