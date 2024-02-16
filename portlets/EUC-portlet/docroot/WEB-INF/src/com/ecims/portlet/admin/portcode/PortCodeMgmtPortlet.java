package com.ecims.portlet.admin.portcode;

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

import smartpay.entity.EndorsementDesk;
import smartpay.entity.PortCode;
import smartpay.entity.State;
import smartpay.service.SwpService;

import com.ecims.portlet.admin.portcode.PortCodeMgmtPortletState.PORTCODE_ACTIONS;
import com.ecims.portlet.admin.portcode.PortCodeMgmtPortletState.VIEW_TABS;
import com.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.service.ServiceContext;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class PortCodeMgmtPortlet
 */
public class PortCodeMgmtPortlet extends MVCPortlet {
	private Logger log = Logger.getLogger(PortCodeMgmtPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	PortCodeMgmtPortletUtil util = PortCodeMgmtPortletUtil.getInstance();
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
		PortCodeMgmtPortletState portletState = 
				PortCodeMgmtPortletState.getInstance(renderRequest, renderResponse);

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
		
		PortCodeMgmtPortletState portletState = PortCodeMgmtPortletState.getInstance(aReq, aRes);
		
		
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
        if(action.equalsIgnoreCase(PORTCODE_ACTIONS.CREATE_NEW_PORTCODE.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
    		createNewPortCode(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(PORTCODE_ACTIONS.EDIT_A_PORTCODE.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	savePortCode(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(PORTCODE_ACTIONS.HANDLE_PORT_CODE_ACTIONS.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	handlePortCodeActionsFromList(aReq, aRes, swpService, portletState);
        		
        }
        
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_NEW_PORTCODE.name()))
        {
        	aRes.setRenderParameter("jspPage", "/html/portcodemgmtportlet/newportcode.jsp");
			portletState.setCurrentTab(VIEW_TABS.VIEW_NEW_PORTCODE);
        }
        
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_PORTCODES.name()))
        {
        	Collection<PortCode> endorsementDeskList = (Collection<PortCode>)portletState.getPortCodeMgmtPortletUtil().getAllEntity(PortCode.class);
        	portletState.setPortCodeListing(endorsementDeskList);
        	aRes.setRenderParameter("jspPage", "/html/portcodemgmtportlet/portcodelisting.jsp");
			portletState.setCurrentTab(VIEW_TABS.VIEW_PORTCODES);
        }
	}

	private void handlePortCodeActionsFromList(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			PortCodeMgmtPortletState portletState) {
		// TODO Auto-generated method stub
		String selectedPortCodeId = aReq.getParameter("selectedPortCodeId");
		String selectedPortCodeAction = aReq.getParameter("selectedPortCodeAction");
		if(selectedPortCodeId!=null && selectedPortCodeAction!=null)
		{
			try
			{
				Long agId = Long.valueOf(selectedPortCodeId);
				PortCode portCode = (PortCode)portletState.getPortCodeMgmtPortletUtil().getEntityObjectById(PortCode.class, agId);
				portletState.setPortCodeEntity(portCode);
				if(portCode!=null)
				{
					if(selectedPortCodeAction.equals("editportcode"))
					{
						portletState.setPortCodeState(Long.toString(portCode.getState().getId()));
						portletState.setPortCodeName(portCode.getPortCode());
						aRes.setRenderParameter("jspPage", "/html/portcodemgmtportlet/editportcode.jsp");
					}
					
				}else
				{
					portletState.addError(aReq, "Ensure you have selected a valid port code before you can proceed", portletState);
				}
			}catch(NumberFormatException e)
			{
				e.printStackTrace();
				portletState.addError(aReq, "Ensure you have selected a valid port code before you can proceed", portletState);
			}
			
		}else
		{
			portletState.addError(aReq, "Ensure you have selected a valid port code before you can proceed", portletState);
		}
	}
	
	

	private void createNewPortCode(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, PortCodeMgmtPortletState portletState) {
		// TODO Auto-generated method stub
		  String portCodeName = aReq.getParameter("portCodeName");
		  String portCodeState = aReq.getParameter("portCodeState");
		  
		  portletState.setPortCodeName(portCodeName);
		  portletState.setPortCodeState(portCodeState);
		  
		  if(portCodeState!=null && !portCodeState.equals("") && portCodeName!=null && 
				  portCodeName.length()>0)
		  {
			  PortCode ag = portletState.getPortCodeMgmtPortletUtil().getPortCodeByStateAndName(portCodeState, portCodeName);
			  if(ag!=null)
			  {
				  portletState.addError(aReq, "An port code of this type with the port code name provided already exists in this system", 
						  portletState);
			  }else
			  {
				  Long id = Long.valueOf(portCodeState);
				  State state = (State)portletState.getPortCodeMgmtPortletUtil().getEntityObjectById(State.class, id);
				  PortCode portCode = new PortCode();
				  portCode.setPortCode(portCodeName);
				  portCode.setState(state);
				  portCode = (PortCode)swpService2.createNewRecord(portCode);
				  if(state!=null)
				  {
					  portletState.addSuccess(aReq, "Port Code successfully created", portletState);
					  portletState.setPortCodeName(null);
					  portletState.setPortCodeState(null);
				  }
				  
			  }
		  }else
		  {
			  portletState.addError(aReq, "Ensure you provide all the data required", portletState);
		  }
	}
	
	private void savePortCode(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, PortCodeMgmtPortletState portletState) {
		// TODO Auto-generated method stub
		  String portCodeName = aReq.getParameter("portCodeName");
		  String portCodeState = aReq.getParameter("portCodeState");
		  String selectedPortCodeId = aReq.getParameter("selectedPortCodeId");
		  portletState.setPortCodeName(portCodeName);
		  portletState.setPortCodeState(portCodeState);
		  
		  if(portCodeState!=null && !portCodeState.equals("") && portCodeName!=null && 
				  portCodeName.length()>0)
		  {
			  Long id = Long.valueOf(selectedPortCodeId);
			  Long stateId = Long.valueOf(portCodeState);
			  PortCode ag = portletState.getPortCodeMgmtPortletUtil().getPortCodeByStateAndNameAndNotId(stateId, portCodeName, id);
			  if(ag!=null)
			  {
				  portletState.addError(aReq, "A Port Code of this type with the port code provided already exists in this system", 
						  portletState);
			  }else
			  {
				  State state = (State)portletState.getPortCodeMgmtPortletUtil().getEntityObjectById(State.class, stateId);
				  ag.setPortCode(portCodeName);
				  ag.setState(state);
				  swpService2.updateRecord(ag);
				  if(state!=null)
				  {
					  portletState.addSuccess(aReq, "Port Code successfully created", portletState);
					  portletState.setPortCodeName(null);
					  portletState.setPortCodeState(null);
				  }
				  
			  }
		  }else
		  {
			  portletState.addError(aReq, "Ensure you provide all the data required", portletState);
		  }
	}



}
