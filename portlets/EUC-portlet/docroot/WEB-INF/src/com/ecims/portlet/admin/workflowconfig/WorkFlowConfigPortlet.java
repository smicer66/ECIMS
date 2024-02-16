package com.ecims.portlet.admin.workflowconfig;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

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

import smartpay.entity.Agency;
import smartpay.entity.EndorsementDesk;
import smartpay.entity.ItemCategory;
import smartpay.entity.PortCode;
import smartpay.entity.State;
import smartpay.entity.WokFlowSetting;
import smartpay.entity.enumerations.AgencyType;
import smartpay.service.SwpService;

import com.ecims.portlet.admin.workflowconfig.WorkFlowConfigPortletState.WORKFLOWCONFIG_ACTIONS;
import com.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.service.ServiceContext;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class WorkFlowConfigPortlet
 */
public class WorkFlowConfigPortlet extends MVCPortlet {
	private Logger log = Logger.getLogger(WorkFlowConfigPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	WorkFlowConfigPortletUtil util = WorkFlowConfigPortletUtil.getInstance();
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
		WorkFlowConfigPortletState portletState = 
				WorkFlowConfigPortletState.getInstance(renderRequest, renderResponse);

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
		
		WorkFlowConfigPortletState portletState = WorkFlowConfigPortletState.getInstance(aReq, aRes);
		
		
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
        if(action.equalsIgnoreCase(WORKFLOWCONFIG_ACTIONS.UPDATE_WORK_FLOW_CONFIG.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	updateWorkFlowConfig(aReq, aRes, swpService, portletState);
        		
        }
	}

	
	private boolean checkDuplicate(String[] s)
	{
		try{
			HashSet<String> hs = new HashSet<String>(Arrays.asList(s));
			if(hs.size()!=s.length)
				return true;
			else
				return false;
		}catch(Exception e){
			e.printStackTrace();
			return true;
		}
	}

	private void updateWorkFlowConfig(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, WorkFlowConfigPortletState portletState) {
		// TODO Auto-generated method stub
		  String[] positionId = aReq.getParameterValues("positionId");
		  String itemCategory = aReq.getParameter("itemCategory");
		  String[] agency = aReq.getParameterValues("agency");
		  
		  portletState.setPositionIds(positionId);
		  portletState.setItemCategorys(itemCategory);
		  portletState.setAgencys(agency);
		  
		  ArrayList<String> pst = new ArrayList<String>();
		  ArrayList<String> ag = new ArrayList<String>();
		  boolean proceed1 = true;
		  boolean proceed2 = true;
		  boolean proceed3 = true;
		  
		  
		  
		  
		  
		  if(positionId!=null && positionId.length>0 && 
				  itemCategory!=null &&  
						  agency!=null && agency.length>0)
		  {
			  for(int i=0; i<positionId.length; i++)
			  {
				  if(!positionId[i].equals("") && !pst.contains(positionId[i]) && 
						  !agency[i].equals("") && !ag.contains(agency[i]))
				  {
					  pst.add(positionId[i]);
					  ag.add(agency[i]);
				  }else
				  {
					  if(pst.contains(positionId[i]))
						  proceed1 = false;
				  }
					  
			  }
			  
			  
			  if(proceed1 && proceed2 && proceed3)
			  {
				  Collection<WokFlowSetting> wfsList = (Collection<WokFlowSetting>)portletState.getWorkFlowConfigPortletUtil().getAllEntity(WokFlowSetting.class);
				  if(wfsList!=null && wfsList.size()>0)
				  {
					  for(Iterator<WokFlowSetting> iter = wfsList.iterator(); iter.hasNext();)
					  {
						  WokFlowSetting wfs = iter.next();
						  swpService.deleteRecord(wfs);
					  }
				  }
				  
				  for(int i=0; i<pst.size(); i++)
				  {
					  WokFlowSetting wfs = new WokFlowSetting();
					  Agency agc = (Agency)portletState.getWorkFlowConfigPortletUtil().getEntityObjectById(Agency.class, Long.valueOf(ag.get(i)));
					  ItemCategory itc = (ItemCategory)portletState.getWorkFlowConfigPortletUtil().getEntityObjectById(ItemCategory.class, Long.valueOf(itemCategory));
					  Integer pid = Integer.valueOf(pst.get(i));
					  
					  if(agc!=null && itc!=null && pid!=null)
					  {
						  wfs.setAgency(agc);
						  wfs.setItemCategory(itc);
						  wfs.setPositionId(pid);
						  wfs.setStatus(Boolean.TRUE);
						  swpService.createNewRecord(wfs);
					  }else
					  {
						  
					  }
				  }
				  portletState.addSuccess(aReq, "Work Flow Configuration Updated Successfully!",  portletState);
				  
				  
			  }else
			  {
				  portletState.addError(aReq, "Ensure you do not select an agency, position, or item category more than once!",  portletState);
			  }
			 
			  
			  
			  
		  }else
		  {
			  portletState.addError(aReq, "Ensure you provide all the data required", portletState);
		  }
	}
	
	
}
