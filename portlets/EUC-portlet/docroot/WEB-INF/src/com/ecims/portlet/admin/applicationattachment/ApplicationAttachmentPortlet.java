package com.ecims.portlet.admin.applicationattachment;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import smartpay.entity.ApplicationAttachmentType;
import smartpay.service.SwpService;

import com.ecims.portlet.admin.applicationattachment.ApplicationAttachmentPortletState.HANDLE_ACTIONS;
import com.ecims.portlet.admin.applicationattachment.ApplicationAttachmentPortletState.VIEW_TABS;
import com.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.service.ServiceContext;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class ApplicationAttachmentPortlet
 */
public class ApplicationAttachmentPortlet extends MVCPortlet {

	private Logger log = Logger.getLogger(ApplicationAttachmentPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	ApplicationAttachmentPortletUtil util = ApplicationAttachmentPortletUtil.getInstance();
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
		ApplicationAttachmentPortletState portletState = 
				ApplicationAttachmentPortletState.getInstance(renderRequest, renderResponse);

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
		
		ApplicationAttachmentPortletState portletState = ApplicationAttachmentPortletState.getInstance(aReq, aRes);
		
		
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
        if(action.equalsIgnoreCase(ApplicationAttachmentPortletState.VIEW_TABS.CREATE_NEW_ATTACHMENT.name()))
        {
        	portletState.setCurrentTab(VIEW_TABS.CREATE_NEW_ATTACHMENT);
        	portletState.setAttachmentType(null);
        	portletState.setSelectedAttachmentType(null);
        	aRes.setRenderParameter("jspPage", "/html/applicationattachmentportlet/newattachment.jsp");
        }else if(action.equalsIgnoreCase(ApplicationAttachmentPortletState.VIEW_TABS.VIEW_ATTACHMENT_TYPES.name()))
        {
        	portletState.setCurrentTab(VIEW_TABS.VIEW_ATTACHMENT_TYPES);
        	portletState.setAttachmentType(null);
        	portletState.setSelectedAttachmentType(null);
        	Collection attachmentTypesListing = portletState.getApplicationAttachmentManagementPortletUtil().
        			getAllAttachmentTypes();
        	portletState.setAttachmentTypesListing(attachmentTypesListing);
        	if(attachmentTypesListing!=null)
        	{
        		aRes.setRenderParameter("jspPage", "/html/applicationattachmentportlet/attachmenttypelisting.jsp");
        	}
        	else
        	{
        		portletState.addError(aReq, "There are no attachment types currently on the system", portletState);
        	}
        	aRes.setRenderParameter("jspPage", "/html/applicationattachmentportlet/attachmenttypelisting.jsp");
        }
        else if(action.equalsIgnoreCase(HANDLE_ACTIONS.HANDLE_ACTION_ON_LIST_OF_ATTACHMENT_TYPE.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	
        	updateAttachmentType(aReq, aRes, swpService, portletState);
        		
        }else if(action.equalsIgnoreCase(HANDLE_ACTIONS.NEW_ATTACHMENT_TYPE.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	String actType = aReq.getParameter("act");
        	log.info("actType==" + actType);
        	if(actType!=null && actType.equalsIgnoreCase("createnewattachment"))
        		newAttachmentType(aReq, aRes, swpService, portletState);
        	if(actType!=null && actType.equalsIgnoreCase("saveattachment"))
        		saveAttachmentType(aReq, aRes, swpService, portletState);
        	
        		
        }
        
	}

	private void updateAttachmentType(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2,
			ApplicationAttachmentPortletState portletState) {
		// TODO Auto-generated method stub
		String selectedAttach = aReq.getParameter("selectedAttach");
		String attachAction = aReq.getParameter("attachAction");
		   
		   
		if(selectedAttach!=null && selectedAttach.length()>0 && attachAction!=null && attachAction.length()>0)
		{
			Long l = Long.valueOf(selectedAttach);
			ApplicationAttachmentType at = (ApplicationAttachmentType)portletState.getApplicationAttachmentManagementPortletUtil().
					getEntityObjectById(ApplicationAttachmentType.class, l);
			if(at!=null)
			{
				portletState.setAttachmentType(at.getName());
				portletState.setSelectedAttachmentType(at);
				aRes.setRenderParameter("jspPage", "/html/applicationattachmentportlet/editattachment.jsp");
			}
			else
			{
				portletState.addError(aReq, "No application attachment matching the selected attachment. Please " +
						"select one and try again", portletState);
				aRes.setRenderParameter("jspPage", "/html/applicationattachmentportlet/attachmenttypelisting.jsp");
			}
		}
	}
	
	
	private boolean checkspecial(String str)
	{
		Pattern p = Pattern.compile("[^a-zA-Z0-9 ]+", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher("I am a string");
		boolean b = m.find();

		return b;
	}
	
	private void newAttachmentType(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2,
			ApplicationAttachmentPortletState portletState) {
		// TODO Auto-generated method stub
		String attachName = aReq.getParameter("attachName");
		log.info("Attach name=" + attachName);
		portletState.setAttachmentType(attachName);
		
		Collection<ApplicationAttachmentType> ata = portletState.getApplicationAttachmentManagementPortletUtil().
				getAttachmentByName(attachName);
		
		if((ata!=null && ata.size()>0) || attachName.trim().length()==0)
		{
			if(ata!=null && ata.size()>0)
			{
				log.info("Attach name already exist");
			}
			if(attachName.trim().length()==0)
				log.info("Attach name length ==0");
			if(checkspecial(attachName)==false)
				log.info("Attach name= false");
				
			log.info("Attach name=" + attachName);
			portletState.addError(aReq, "New attachment was not successfully created. Ensure you provide a valid attachment type name", portletState);
			
		}
		else
		{
			log.info("Attach name=" + attachName);
			ApplicationAttachmentType aat = new ApplicationAttachmentType();
			aat.setCompulsory(Boolean.FALSE);
			aat.setExpiryDateApplicable(Boolean.FALSE);
			aat.setName(attachName);
			swpService2.createNewRecord(aat);
			portletState.addSuccess(aReq, "New attachment successfully created", portletState);
			portletState.setAttachmentType(null);
		}
	}
	
	
	
	private void saveAttachmentType(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2,
			ApplicationAttachmentPortletState portletState) {
		// TODO Auto-generated method stub
		String attachName = aReq.getParameter("attachName");
		String actId = aReq.getParameter("actId");
		portletState.setAttachmentType(attachName);
		
		if(attachName.trim().length()>0)
		{
			Long l = Long.valueOf(actId);
			Collection<ApplicationAttachmentType> ata = portletState.getApplicationAttachmentManagementPortletUtil().
					getAttachmentByNameNotId(attachName, l);
			if(ata!=null && ata.size()>0)
			{
				portletState.addError(aReq, "Update failed. Ensure this attachment does not already exist on the system", portletState);
				aRes.setRenderParameter("jspPage", "/html/applicationattachmentportlet/editattachment.jsp");
			}else
			{
				ApplicationAttachmentType plo = portletState.getSelectedAttachmentType();
				plo.setName(attachName);
				swpService2.updateRecord(plo);
				portletState.addSuccess(aReq, "Attachment saved successfully", portletState);
				portletState.setSelectedAttachmentType(null);
				portletState.setAttachmentType(null);
				aRes.setRenderParameter("jspPage", "/html/applicationattachmentportlet/newattachment.jsp");
				portletState.setCurrentTab(VIEW_TABS.CREATE_NEW_ATTACHMENT);
			}
		}
		else
		{
			portletState.addError(aReq, "Attachment Type update failed. Please provide valid details before proceeding", portletState);
			aRes.setRenderParameter("jspPage", "/html/applicationattachmentportlet/editattachment.jsp");
		}
	}
}
