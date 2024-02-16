package com.ecims.portlet.trackapplication;

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

import smartpay.entity.ApplicationWorkflow;
import smartpay.service.SwpService;

import com.ecims.portlet.trackapplication.TrackApplicationPortletState.ACTIONS;
import com.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.service.ServiceContext;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class TrackApplicationPortlet
 */
public class TrackApplicationPortlet extends MVCPortlet {
	private Logger log = Logger.getLogger(TrackApplicationPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	TrackApplicationPortletUtil util = TrackApplicationPortletUtil.getInstance();
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
		TrackApplicationPortletState portletState = 
				TrackApplicationPortletState.getInstance(renderRequest, renderResponse);

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
		
		TrackApplicationPortletState portletState = TrackApplicationPortletState.getInstance(aReq, aRes);
		
		
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
        if(action.equalsIgnoreCase(ACTIONS.SEARCH_FOR_APPLICATION.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	String appNo = aReq.getParameter("applicationNumber");
        	if(appNo!=null && appNo.length()>0)
        	{
        		Collection<ApplicationWorkflow> appWfL = portletState.getTrackApplicationPortletUtil().
        				getApplicationWorkflowByAppNo(appNo);
        		if(appWfL!=null && appWfL.size()>0)
        		{
        			portletState.setApplicationNumber(appNo);
        			portletState.setAppWorkflowListing(appWfL);
        		}else
        		{
        			portletState.setApplicationNumber(appNo);
        			portletState.setAppWorkflowListing(null);
        			portletState.addError(aReq, "No activity on the system could be found matching this application.", portletState);
        		}
        		
        	}else
        	{
        		portletState.setApplicationNumber(appNo);
        		portletState.setAppWorkflowListing(null);
        		portletState.addError(aReq, "Please provide a valid application number before clicking the search button", portletState);
        	}
        		
        }
	}
	

}
