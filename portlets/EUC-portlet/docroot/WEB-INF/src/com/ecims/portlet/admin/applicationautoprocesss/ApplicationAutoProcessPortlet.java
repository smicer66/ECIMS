package com.ecims.portlet.admin.applicationautoprocesss;

import java.io.IOException;
import java.util.Collection;
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
import smartpay.entity.ItemCategory;
import smartpay.entity.WokFlowSetting;
import smartpay.service.SwpService;

import com.ecims.portlet.admin.applicationautoprocesss.ApplicationAutoProcessPortletState.ACTIONS;
import com.ecims.portlet.certificatemanagement.CertificatePortletState.VIEW_TABS;
import com.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.service.ServiceContext;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class ApplicationAutoProcessPortlet
 */
public class ApplicationAutoProcessPortlet extends MVCPortlet {
	private Logger log = Logger.getLogger(ApplicationAutoProcessPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	ApplicationAutoProcessPortletUtil util = ApplicationAutoProcessPortletUtil.getInstance();
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
		ApplicationAutoProcessPortletState portletState = 
				ApplicationAutoProcessPortletState.getInstance(renderRequest, renderResponse);

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
		
		ApplicationAutoProcessPortletState portletState = ApplicationAutoProcessPortletState.getInstance(aReq, aRes);
		
		
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
        if(action.equalsIgnoreCase(ACTIONS.MANAGE_AUTO_PROCESS.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	String selectedAction = aReq.getParameter("");
        	if(selectedAction!=null && selectedAction.equals("gobackautoprocess"))
        	{
        		aRes.setRenderParameter("jspPage", "/html/applicationautoprocessportlet/preautoprocess.jsp");
        	}
        	if(selectedAction!=null && selectedAction.equals("gotosteptwoautoprocess"))
        	{
        		processStepOne(aReq, aRes, swpService, portletState);
        	}
        	if(selectedAction!=null && selectedAction.equals("gotostepthreeautoprocess"))
        	{
        		processStepTwo(aReq, aRes, swpService, portletState);
        	}
        		
        }
	}

	private void processStepTwo(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2,
			ApplicationAutoProcessPortletState portletState) {
		// TODO Auto-generated method stub
		String agency[] = aReq.getParameterValues("agency");
		String position[] = aReq.getParameterValues("position");
		boolean proceed = false;
		
		if(agency!=null && agency.length>0)
		{
			for(int c=0; c<position.length; c++)
			{
				if(position[c]!="")
				{
					proceed = true;
				}
			}
		}
		
		
		
		if(proceed)
		{
			Collection<WokFlowSetting> wfsList = portletState.getApplicationAutoProcessPortletUtil().getAllWokFlowSetting();
			for(Iterator<WokFlowSetting> it = wfsList.iterator(); it.hasNext();)
			{
				WokFlowSetting ws = it.next();
				swpService.deleteRecord(ws);
			}
			
			for(int c=0; c<agency.length; c++)
			{
				try
				{
					Long agencyId = Long.valueOf(agency[c]);
					Agency ag = (Agency)portletState.getApplicationAutoProcessPortletUtil().
							getEntityObjectById(Agency.class, agencyId);
					if(position[c]!=null && position[c].length()>0 && ag!=null)
					{
						WokFlowSetting wfs = new WokFlowSetting();
						wfs.setAgency(ag);
						wfs.setItemCategory(portletState.getItemCategoryEntity());
						wfs.setPositionId(Integer.valueOf(position[c]));
						wfs.setStatus(Boolean.TRUE);
						swpService.createNewRecord(wfs);
					}
				}catch(NumberFormatException e)
				{
					e.printStackTrace();
				}
			}
			portletState.addSuccess(aReq, "New Work-Flow Process Created On the System", portletState);
		}else
		{
			portletState.addError(aReq, "There are either no agencies currently on the system or " +
					"you did not select a position for any of the agencies", portletState);
		}
	}

	private void processStepOne(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2,
			ApplicationAutoProcessPortletState portletState) {
		// TODO Auto-generated method stub
		String itemcategory = aReq.getParameter("itemcategory");
		try{
			Long id = Long.valueOf(itemcategory);
			portletState.setItemCategoryEntity((ItemCategory)portletState.getApplicationAutoProcessPortletUtil().
					getEntityObjectById(ItemCategory.class, id));
			aRes.setRenderParameter("jspPage", "/html/applicationautoprocessportlet/autoprocess.jsp");
		}catch(NumberFormatException e)
		{
			e.printStackTrace();
			portletState.addError(aReq, "Item Category selected is invalid. Please select one", portletState);
		}
		
	}

}
