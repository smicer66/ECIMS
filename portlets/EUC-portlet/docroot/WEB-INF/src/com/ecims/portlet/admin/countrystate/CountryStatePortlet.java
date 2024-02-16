package com.ecims.portlet.admin.countrystate;

import java.io.IOException;
import java.util.Collection;

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

import smartpay.entity.Country;
import smartpay.entity.PortCode;
import smartpay.entity.State;
import smartpay.service.SwpService;

import com.ecims.portlet.admin.countrystate.CountryStatePortletState.ACTIONS;
import com.ecims.portlet.admin.countrystate.CountryStatePortletState.VIEW_TABS;
import com.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.service.ServiceContext;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class CountryStatePortlet
 */
public class CountryStatePortlet extends MVCPortlet {
	private Logger log = Logger.getLogger(CountryStatePortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	CountryStatePortletUtil util = CountryStatePortletUtil.getInstance();
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
		CountryStatePortletState portletState = 
				CountryStatePortletState.getInstance(renderRequest, renderResponse);

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
		
		CountryStatePortletState portletState = CountryStatePortletState.getInstance(aReq, aRes);
		
		
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
        if(action.equalsIgnoreCase(ACTIONS.MANAGE_PORT_CODE.name()))
        {
        	String act = aReq.getParameter("selectedUserAction");
        	
        	if(act!=null && act.equals("editportcode"))
        	{
        		String actId = aReq.getParameter("selectedApplications");
        		try
        		{
        			Long id = Long.valueOf(actId);
        			PortCode portCode = (PortCode)portletState.getCountryStatePortletUtil().getEntityObjectById(PortCode.class, id);
        			if(portCode!=null)
        			{
        				portletState.setSelectedPortCode(portCode);
    	        		portletState.setPortCode(portCode.getPortCode());
    	        		portletState.setSelectedState(portCode.getState());
        				aRes.setRenderParameter("jspPage", "/html/countrystateportlet/editportcode.jsp");
        			}else
        			{
        				portletState.addError(aReq, "Select a Port code before you can carry out this action", portletState);
        			}
        				
        		}catch(NumberFormatException e)
        		{
        			e.printStackTrace();
        			portletState.addError(aReq, "Invalid port code selected. Please select one to proceed", portletState);
        		}
        	}else if(act!=null && act.equals("disableportcode"))
        	{
        		String actId = aReq.getParameter("selectedApplications");
        		try
        		{
        			Long id = Long.valueOf(actId);
        			PortCode portCode = (PortCode)portletState.getCountryStatePortletUtil().getEntityObjectById(PortCode.class, id);
        			if(portCode!=null)
        			{
        				portCode.setStatus(Boolean.FALSE);
        				swpService.updateRecord(portCode);
        			}else
        			{
        				portletState.addError(aReq, "Select a port code before you can carry out this action", portletState);
        			}
        				
        		}catch(NumberFormatException e)
        		{
        			e.printStackTrace();
        			portletState.addError(aReq, "Invalid port code selected. Please select one to proceed", portletState);
        		}
        	}
        }
        
        if(action.equalsIgnoreCase(ACTIONS.CREATE_NEW_PORT_CODE.name()))
        {
        	String act = aReq.getParameter("act");
        	if(act!=null && act.equals("createnewportcode"))
        	{
        		String portCode = aReq.getParameter("portCode");
    			String portstate = aReq.getParameter("portstate");
    			PortCode port = (PortCode)portletState.getCountryStatePortletUtil().getPortCodeByPortCode(portstate);
    			if(port==null)
    			{
    				try{
    				Long id = Long.valueOf(portstate);
    				State state= (State)portletState.getCountryStatePortletUtil().getEntityObjectById(State.class, id);
    				
    				PortCode pc = new PortCode();
    				pc.setPortCode(portCode);
    				pc.setState(state);
    				pc.setStatus(Boolean.TRUE);
    				swpService.createNewRecord(pc);
    				portletState.addSuccess(aReq, "New Port Code details saved successfully" +
    						"", portletState);
    				}catch(NumberFormatException e)
    				{
    					e.printStackTrace();
    					portletState.addError(aReq, "Invalid State selected. Select a valid country this state belongs to", portletState);
    				}
    			}else
    			{
    				portletState.addError(aReq, "The Port code provided already belongs to an entry in the database" +
    						"", portletState);
    			}
        	}else if(act!=null && act.equals("editportcode"))
        	{
        		String actId = aReq.getParameter("actId");
        		try
        		{
        			Long id = Long.valueOf(actId);
        			String portCode = aReq.getParameter("portCode");
        			String portstate = aReq.getParameter("portstate");
        			Long cId = Long.valueOf(portstate);
        			Collection<PortCode> checkPortCode = portletState.getCountryStatePortletUtil().
        					getPortCodeByCodeAndStateExceptId(portCode, cId, id);
        			if(checkPortCode==null || (checkPortCode!=null && checkPortCode.size()==0))
        			{
        				State state = (State)portletState.getCountryStatePortletUtil().getEntityObjectById(State.class, cId);
        				PortCode pc = (PortCode)portletState.getCountryStatePortletUtil().getEntityObjectById(Country.class, id);
        				pc.setState(state);
        				pc.setPortCode(portCode);
        				swpService.updateRecord(pc);
        				portletState.addSuccess(aReq, "Port Code details updated successfully" +
        						"", portletState);
        			}else
        			{
        				portletState.addError(aReq, "The port code provided already belongs to an entry in the database" +
        						"", portletState);
        			}
        				
        		}catch(NumberFormatException e)
        		{
        			e.printStackTrace();
        			portletState.addError(aReq, "Invalid port code selected. Please select one to proceed", portletState);
        		}
        	}
        }
        
        if(action.equalsIgnoreCase(ACTIONS.CREATE_NEW_STATE.name()))
        {
        	String act = aReq.getParameter("act");
        	if(act!=null && act.equals("createnewstate"))
        	{
        		String statecountry = aReq.getParameter("statecountry");
    			String statename = aReq.getParameter("statename");
    			State checkState = portletState.getCountryStatePortletUtil().getStateByName(statename);
    			if(checkState==null)
    			{
    				try{
    				Long id = Long.valueOf(statecountry);
    				
    				Country country = (Country)portletState.getCountryStatePortletUtil().getEntityObjectById(Country.class, id);
    				State state = new State();
    				state.setName(statename);
    				state.setCountry(country);
    				swpService.createNewRecord(state);
    				portletState.addSuccess(aReq, "New state details saved successfully" +
    						"", portletState);
    				}catch(NumberFormatException e)
    				{
    					e.printStackTrace();
    					portletState.addError(aReq, "Invalid country selected. Select a valid country this state belongs to", portletState);
    				}
    			}else
    			{
    				portletState.addError(aReq, "The state name provided already belongs to an entry in the database" +
    						"", portletState);
    			}
        	}else if(act!=null && act.equals("editstatedetails"))
        	{
        		String actId = aReq.getParameter("actId");
        		try
        		{
        			Long id = Long.valueOf(actId);
        			String statecountry = aReq.getParameter("statecountry");
        			Long cId = Long.valueOf(statecountry);
        			String statename = aReq.getParameter("statename");
        			Collection<State> checkState = portletState.getCountryStatePortletUtil().
        					getStateByNameOrCodeExceptId(statename, cId, id);
        			if(checkState==null || (checkState!=null && checkState.size()==0))
        			{
        				State state = (State)portletState.getCountryStatePortletUtil().getEntityObjectById(State.class, id);
        				Country country = (Country)portletState.getCountryStatePortletUtil().getEntityObjectById(Country.class, cId);
        				state.setCountry(country);
        				state.setName(statename);
        				swpService.updateRecord(country);
        				portletState.addSuccess(aReq, "State details updated successfully" +
        						"", portletState);
        			}else
        			{
        				portletState.addError(aReq, "The state name provided already belongs to an entry in the database" +
        						"", portletState);
        			}
        				
        		}catch(NumberFormatException e)
        		{
        			e.printStackTrace();
        			portletState.addError(aReq, "Invalid state selected. Please select one to proceed", portletState);
        		}
        	}
        }
        if(action.equalsIgnoreCase(ACTIONS.MANAGE_STATE.name()))
        {
        	String act = aReq.getParameter("selectedUserAction");
        	if(act!=null && act.equals("editstate"))
        	{
        		String actId = aReq.getParameter("selectedApplications");
        		try
        		{
        			Long id = Long.valueOf(actId);
        			State state = (State)portletState.getCountryStatePortletUtil().getEntityObjectById(State.class, id);
        			if(state!=null)
        			{
        				portletState.setSelectedState(state);
    	        		portletState.setStateName(state.getName());
    	        		portletState.setSelectedCountry(state.getCountry());
        				aRes.setRenderParameter("jspPage", "/html/countrystateportlet/editstate.jsp");
        			}else
        			{
        				portletState.addError(aReq, "Select a state before you can carry out this action", portletState);
        			}
        				
        		}catch(NumberFormatException e)
        		{
        			e.printStackTrace();
        			portletState.addError(aReq, "Invalid country selected. Please select one to proceed", portletState);
        		}
        	}
        	
        	
        	
        }
        else if(action.equalsIgnoreCase(ACTIONS.CREATE_NEW_COUNTRY.name()))
        {
        	String act = aReq.getParameter("act");
        	
        	if(act!=null && act.equals("createnewcountry"))
        	{
        		String countryName = aReq.getParameter("countryName");
    			String phoneCode = aReq.getParameter("phoneCode");
    			Collection<Country> checkCountry = portletState.getCountryStatePortletUtil().getCountryByNameOrCode(countryName, phoneCode);
    			if(checkCountry==null || (checkCountry!=null && checkCountry.size()==0))
    			{
    				Country country = new Country();
    				country.setCode(phoneCode);
    				country.setName(countryName);
    				swpService.createNewRecord(country);
    				portletState.addSuccess(aReq, "New country details saved successfully" +
    						"", portletState);
    			}else
    			{
    				portletState.addError(aReq, "The country name and phone code provided already belongs to an entry in the database" +
    						"", portletState);
    			}
        	}
        	else if(act!=null && act.equals("savecountry"))
        	{
        		String actId = aReq.getParameter("actId");
        		try
        		{
        			Long id = Long.valueOf(actId);
        			String countryName = aReq.getParameter("countryName");
        			String phoneCode = aReq.getParameter("phoneCode");
        			Collection<Country> checkCountry = portletState.getCountryStatePortletUtil().getCountryByNameOrCodeExceptId(countryName, phoneCode, id);
        			if(checkCountry==null || (checkCountry!=null && checkCountry.size()==0))
        			{
        				Country country = (Country)portletState.getCountryStatePortletUtil().getEntityObjectById(Country.class, id);
        				country.setCode(phoneCode);
        				country.setName(countryName);
        				swpService.updateRecord(country);
        				portletState.addSuccess(aReq, "Country details updated successfully" +
        						"", portletState);
        			}else
        			{
        				portletState.addError(aReq, "The country name and phone code provided already belongs to an entry in the database" +
        						"", portletState);
        			}
        				
        		}catch(NumberFormatException e)
        		{
        			e.printStackTrace();
        			portletState.addError(aReq, "Invalid country selected. Please select one to proceed", portletState);
        		}
        	}
        	
        }
        
        if(action.equalsIgnoreCase(ACTIONS.MANAGE_COUNTRY.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	String selectedAction = aReq.getParameter("selectedUserAction");
        	if(selectedAction!=null && selectedAction.equals("editcountry"))
        	{
        		String idString = aReq.getParameter("selectedApplications");
        		Long id = Long.valueOf(idString);
        		Country country = (Country)portletState.getCountryStatePortletUtil().getEntityObjectById(Country.class,id);
        		if(country!=null)
        		{
	        		portletState.setSelectedCountry(country);
	        		portletState.setCountryName(country.getName());
	        		portletState.setPhoneCode(country.getCode());
	        		aRes.setRenderParameter("jspPage", "/html/countrystateportlet/editcountry.jsp");
        		}else
        		{
        			portletState.addError(aReq, "Select a valid country to edit before you can proceed", portletState);
        		}
        	}
        	
        	
        	
//        	
//        	if(selectedAction!=null && selectedAction.equals("createnewportcode"))
//        	{
//        		createnewportcode(aReq, aRes, swpService, portletState);
//        		
//        	}
//        	if(selectedAction!=null && selectedAction.equals("createnewstate"))
//        	{
//        		createnewstate(aReq, aRes, swpService, portletState);
//        	}
//        	if(selectedAction!=null && selectedAction.equals("createnewcountry"))
//        	{
//        		createnewcountry(aReq, aRes, swpService, portletState);
//        	}
        		
        }
	}

//	private void createnewportcode(ActionRequest aReq, ActionResponse aRes,
//			SwpService swpService2,
//			CountryStatePortletState portletState) {
//		// TODO Auto-generated method stub
//		String portstate = aReq.getParameter("portstate");
//		String portCode = aReq.getParameter("portCode");
//		try
//		{
//			if(portstate!=null && portCode!=null)
//			{
//				Long portstateid = Long.valueOf(portstate);
//				if(portletState.getCountryStatePortletUtil().getPortByStateAndCode(portstateid, portCode)==null)
//				{
//					
//				}else
//				{
//					portletState.addError(aReq, "This port has been created for this state", portletState);
//				}
//			}
//			portletState.setCurrentTab(VIEW_TABS.VIEW_NEW_COUNTRY);
//		}catch(NumberFormatException e)
//		{
//			portletState.addError(aReq, "We could not find any state you selected. Please select one before proceeding with this action", portletState);
//		}
//	}

	private void createnewstate(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2,
			CountryStatePortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setCurrentTab(VIEW_TABS.VIEW_NEW_COUNTRY);
	}

	private void createnewcountry(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2,
			CountryStatePortletState portletState) {
		// TODO Auto-generated method stub
		portletState.setCurrentTab(VIEW_TABS.VIEW_NEW_COUNTRY);
	}
}
