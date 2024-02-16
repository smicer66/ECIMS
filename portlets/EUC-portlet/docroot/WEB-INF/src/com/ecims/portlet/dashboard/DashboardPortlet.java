package com.ecims.portlet.dashboard;

import java.io.IOException;

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

import smartpay.entity.enumerations.PermissionType;
import smartpay.service.SwpService;
import com.ecims.portlet.dashboard.DashboardPortletState;
import com.ecims.portlet.dashboard.DashboardPortletState.*;
import com.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.service.ServiceContext;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class DashboardPortlet
 */
public class DashboardPortlet extends MVCPortlet {
	private Logger log = Logger.getLogger(DashboardPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
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
		DashboardPortletState portletState = 
				DashboardPortletState.getInstance(renderRequest, renderResponse);

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
		
		DashboardPortletState portletState = DashboardPortletState.getInstance(aReq, aRes);
		
		
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
        if(action.equalsIgnoreCase(VIEW_TABS.PENDING_APPLICANT.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	aRes.sendRedirect("/web/nsa-user/applicant-management?pendnsa");
        		
        }
        
        if(action.equalsIgnoreCase(VIEW_TABS.PENDING_APPLICATION.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	if(portletState.getPermissionList()!=null && portletState.getPermissionList().contains(PermissionType.PERMISSION_APPROVE_APPLICATION))
        	{
        		aRes.sendRedirect("/web/nsa-user/application-management?pendnsa1");
        	}else
        	{
        		aRes.sendRedirect("/web/nsa-user/application-management?pendnsa");
        	}
        }
        
        if(action.equalsIgnoreCase(VIEW_TABS.PENDING_APPLICATION_AGENCY.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	String usrty = aReq.getParameter("usrty");
        	//Select Account type
        	aRes.sendRedirect("/web/"+usrty+"/application-management?pendag");
        	
        }
        
        if(action.equalsIgnoreCase(VIEW_TABS.PENDING_APPLICATION_APPROVAL_NSA.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	String usrty = aReq.getParameter("usrty");
        	//Select Account type
        	aRes.sendRedirect("/web/nsa-user/application-management?appns");
        	
        }
        
        
        if(action.equalsIgnoreCase(VIEW_TABS.APPROVED_APPLICATIONS_NSA.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	String usrty = aReq.getParameter("usrty");
        	//Select Account type
        	aRes.sendRedirect("/web/"+usrty+"/application-management?apvnsa");
        	
        }
        
        if(action.equalsIgnoreCase(VIEW_TABS.PENDING_ISSUANCE.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	aRes.sendRedirect("/web/nsa-user/application-management?issnsa");
        		
        }
        
        if(action.equalsIgnoreCase(VIEW_TABS.RECENT_ENDORSEMENT.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	aRes.sendRedirect("/web/nsa-user/application-management?endnsa");
        		
        }
	}

}
