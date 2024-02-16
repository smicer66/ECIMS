package com.ecims.portlet.admin.agencymanagement;

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

import smartpay.entity.Agency;
import smartpay.entity.EndorsementDesk;
import smartpay.entity.enumerations.AgencyType;
import smartpay.service.SwpService;

import com.ecims.portlet.admin.agencymanagement.AgencyManagementPortletState.AGENCY_ACTIONS;
import com.ecims.portlet.admin.agencymanagement.AgencyManagementPortletState.VIEW_TABS;
import com.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.service.ServiceContext;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.sf.primepay.smartpay13.ServiceLocator;

/**
 * Portlet implementation class AgencyManagementPortlet
 */
public class AgencyManagementPortlet extends MVCPortlet {
	private Logger log = Logger.getLogger(AgencyManagementPortlet.class);
	private PortletContext pContext;
	private PortletConfig pConfig;
	private ServiceLocator serviceLocator = ServiceLocator.getInstance();
	public SwpService swpService = null;
	public com.ecims.commins.PrbCustomService swpCustomService = com.ecims.commins.PrbCustomService.getInstance();
	AgencyManagementPortletUtil util = AgencyManagementPortletUtil.getInstance();
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
		AgencyManagementPortletState portletState = 
				AgencyManagementPortletState.getInstance(renderRequest, renderResponse);

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
		
		AgencyManagementPortletState portletState = AgencyManagementPortletState.getInstance(aReq, aRes);
		
		
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
        if(action.equalsIgnoreCase(AGENCY_ACTIONS.CREATE_NEW_AGENCY.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	createNewAgency(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(AGENCY_ACTIONS.EDIT_AN_AGENCY.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	saveAgency(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(AGENCY_ACTIONS.EDIT_AGENCY_DESK.name()))
        {
        	log.info("EDIT_AGENCY_DESK");
        	//Select Account type
        	saveAgencyDesk(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(AGENCY_ACTIONS.DELETE_AN_AGENCY.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	deleteAgency(aReq, aRes, swpService, portletState);
        		
        }
        
        if(action.equalsIgnoreCase(AGENCY_ACTIONS.DELETE_AGENCY_DESK.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	deleteDesk(aReq, aRes, swpService, portletState);
        		
        }
        
        if(action.equalsIgnoreCase(AGENCY_ACTIONS.CREATE_NEW_AGENCY_DESK.name()))
        {
        	log.info("CREATE_NEW_AGENCY_DESK");
        	//Select Account type
        	createNewDesk(aReq, aRes, swpService, portletState);
        		
        }
        
        
        if(action.equalsIgnoreCase(AGENCY_ACTIONS.HANDLE_ACTION_ON_LIST_OF_AGENCY.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	handleAgencyActionsFromList(aReq, aRes, swpService, portletState);
        		
        }
        if(action.equalsIgnoreCase(AGENCY_ACTIONS.HANDLE_ACTION_ON_LIST_OF_DESK.name()))
        {
        	log.info("CREATE_A_PORTAL_USER_STEP_ONE");
        	//Select Account type
        	handleDeskActionsFromList(aReq, aRes, swpService, portletState);
        		
        }
        
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_ALL_AGENCY.name()))
        {
        	Collection<Agency> agencyList = (Collection<Agency>)portletState.getAgencyManagementPortletUtil().getAllEntity(Agency.class);
        	portletState.setAgencyListing(agencyList);
			aRes.setRenderParameter("jspPage", "/html/agencymanagementportlet/agencylisting.jsp");
			portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_AGENCY);
        }
        
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_ALL_DESK.name()))
        {
        	Collection<EndorsementDesk> endorsementDeskList = (Collection<EndorsementDesk>)portletState.getAgencyManagementPortletUtil().getAllEntity(EndorsementDesk.class);
        	portletState.setDeskListing(endorsementDeskList);
			aRes.setRenderParameter("jspPage", "/html/agencymanagementportlet/desklisting.jsp");
			portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_DESK);
        }
        
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_NEW_AGENCY.name()))
        {
        	portletState.setAgencyEmail(null);
  		  	portletState.setAgencyName(null);
  		  	portletState.setAgencyPhone(null);
  		  	portletState.setAgencyType(null);
  		  	portletState.setContactName(null);
  		  	portletState.setAgencyEntity(null);
        	aRes.setRenderParameter("jspPage", "/html/agencymanagementportlet/newagency.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_NEW_AGENCY);
        }
        
        if(action.equalsIgnoreCase(VIEW_TABS.VIEW_NEW_DESK.name()))
        {
        	portletState.setDeskName(null);
        	portletState.setAgencyEntity(null);;
        	aRes.setRenderParameter("jspPage", "/html/agencymanagementportlet/newdesk.jsp");
        	portletState.setCurrentTab(VIEW_TABS.VIEW_NEW_DESK);
        	portletState.setAgencyListing(portletState.getAgencyManagementPortletUtil().getAllAgency());
        }
	}

	private void handleAgencyActionsFromList(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			AgencyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String selectedAgencyId = aReq.getParameter("selectedAgencyId");
		String selectedAgencyAction = aReq.getParameter("selectedAgencyAction");
		if(selectedAgencyId!=null && selectedAgencyAction!=null)
		{
			try
			{
				Long agId = Long.valueOf(selectedAgencyId);
				Agency agency = (Agency)portletState.getAgencyManagementPortletUtil().getEntityObjectById(Agency.class, agId);
				portletState.setAgencyEntity(agency);
				if(agency!=null)
				{
					if(selectedAgencyAction.equals("updateagency"))
					{
						
						aRes.setRenderParameter("jspPage", "/html/agencymanagementportlet/editagency.jsp");
					}
					else if(selectedAgencyAction.equals("viewdesksonagency"))
					{
						
					}
//						deleteAgency(aReq, aRes,swpService2, portletState);
					
				}else
				{
					aRes.setRenderParameter("jspPage", "/html/agencymanagementportlet/agencylisting.jsp");
					portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_AGENCY);
					portletState.addError(aReq, "Ensure you have selected a valid agency before you can proceed", portletState);
				}
			}catch(NumberFormatException e)
			{
				e.printStackTrace();
				aRes.setRenderParameter("jspPage", "/html/agencymanagementportlet/agencylisting.jsp");
				portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_AGENCY);
				portletState.addError(aReq, "Ensure you have selected a valid agency before you can proceed", portletState);
			}
			
		}else
		{
			aRes.setRenderParameter("jspPage", "/html/agencymanagementportlet/agencylisting.jsp");
			portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_AGENCY);
			portletState.addError(aReq, "Ensure you have selected a valid agency before you can proceed", portletState);
		}
	}
	
	
	private void handleDeskActionsFromList(ActionRequest aReq,
			ActionResponse aRes, SwpService swpService2,
			AgencyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String selectedDeskId = aReq.getParameter("selectedDeskId");
		String selectedDeskAction = aReq.getParameter("selectedDeskAction");
		if(selectedDeskId!=null && selectedDeskAction!=null)
		{
			try
			{
				Long eId = Long.valueOf(selectedDeskId);
				EndorsementDesk ed = (EndorsementDesk)portletState.getAgencyManagementPortletUtil().getEntityObjectById(EndorsementDesk.class, eId);
				portletState.setEndorsementDeskEntity(ed);
				if(ed!=null)
				{
					if(selectedDeskAction.equalsIgnoreCase("updateDesk"))
						aRes.setRenderParameter("jspPage", "/html/agencymanagementportlet/editdesk.jsp");
					else if(selectedDeskAction.equalsIgnoreCase("deletedesk"))
						deleteDesk(aReq, aRes,swpService2, portletState);
					
				}else
				{
					portletState.addError(aReq, "Ensure you have selected a valid agency before you can proceed", portletState);
				}
			}catch(NumberFormatException e)
			{
				e.printStackTrace();
				portletState.addError(aReq, "Ensure you have selected a valid agency before you can proceed", portletState);
			}
			
		}else
		{
			portletState.addError(aReq, "Ensure you have selected a valid agency before you can proceed", portletState);
		}
	}

	private void deleteAgency(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, AgencyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String selectedAgencyId = aReq.getParameter("selectedAgencyId");
		if(selectedAgencyId!=null)
		{
			try
			{
				Long agId = Long.valueOf(selectedAgencyId);
				Agency agency = (Agency)portletState.getAgencyManagementPortletUtil().getEntityObjectById(Agency.class, agId);
//				agency.setStatus(Boolean.FALSE);
				swpService.updateRecord(agency);
				portletState.addSuccess(aReq, "Status of agency has been changed to deleted successfully", portletState);
			}catch(NumberFormatException e)
			{
				e.printStackTrace();
				portletState.addError(aReq, "Ensure you have selected a valid agency before you can proceed", portletState);
			}
			
		}else
		{
			portletState.addError(aReq, "Ensure you have selected a valid agency before you can proceed", portletState);
		}
	}
	
	
	private void deleteDesk(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, AgencyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		String selectedDeskId = aReq.getParameter("selectedDeskId");
		if(selectedDeskId!=null)
		{
			try
			{
				Long agId = Long.valueOf(selectedDeskId);
				EndorsementDesk desk = (EndorsementDesk)portletState.getAgencyManagementPortletUtil().getEntityObjectById(EndorsementDesk.class, agId);
				desk.setIsActive(Boolean.FALSE);
				swpService.updateRecord(desk);
				portletState.addSuccess(aReq, "Status of endorsement desk has been changed to deleted successfully", portletState);
				Collection<EndorsementDesk> endorsementDeskList = (Collection<EndorsementDesk>)portletState.getAgencyManagementPortletUtil().getAllEntity(EndorsementDesk.class);
	        	portletState.setDeskListing(endorsementDeskList);
			}catch(NumberFormatException e)
			{
				e.printStackTrace();
				portletState.addError(aReq, "Ensure you have selected a valid endorsement desk before you can proceed", portletState);
			}
			
		}else
		{
			portletState.addError(aReq, "Ensure you have selected a valid endorsement desk before you can proceed", portletState);
		}
	}


	private void createNewAgency(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, AgencyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		  String agencyType = aReq.getParameter("agencyType");
		  String agencyName = aReq.getParameter("agencyName");
		  String contactName = aReq.getParameter("contactName");
		  String agencyPhone = aReq.getParameter("agencyPhone");
		  String agencyEmail = aReq.getParameter("agencyEmail");
		  
		  portletState.setAgencyEmail(agencyEmail);
		  portletState.setAgencyName(agencyName);
		  portletState.setAgencyPhone(agencyPhone);
		  portletState.setAgencyType(AgencyType.fromString(agencyType).getValue());
		  portletState.setContactName(contactName);
		  
		  if(agencyType!=null && !agencyType.equals("") && agencyName!=null && 
				  agencyName.length()>0 && contactName!=null && contactName.length()>0 && 
				  agencyPhone!=null && agencyPhone.length()>0 && agencyEmail!=null && agencyEmail.length()>0)
		  {
			  Agency ag = portletState.getAgencyManagementPortletUtil().getAgencyByTypeAndName(agencyType, agencyName);
			  if(ag!=null)
			  {
				  portletState.addError(aReq, "An agency of this type with the agency name provided already exists in this system", 
						  portletState);
			  }else
			  {
				  Agency agency = new Agency();
				  agency.setAgencyEmail(agencyEmail);
				  agency.setAgencyName(agencyName);
				  agency.setAgencyPhone(agencyPhone);
				  agency.setAgencyType(AgencyType.fromString(agencyType));
				  agency.setContactName(contactName);
				  agency.setCreatedDate(new Timestamp((new Date()).getTime()));
//				  agency.setStatus(Boolean.TRUE);
				  agency = (Agency)swpService2.createNewRecord(agency);
				  if(agency!=null)
				  {
					  portletState.addSuccess(aReq, "Agency successfully created", portletState);
					  portletState.setAgencyEmail(null);
					  portletState.setAgencyName(null);
					  portletState.setAgencyPhone(null);
					  portletState.setAgencyType(null);
					  portletState.setContactName(null);
				  }
				  
			  }
		  }else
		  {
			  portletState.addError(aReq, "Ensure you provide all the data required", portletState);
		  }
	}
	
	private void createNewDesk(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, AgencyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		  String agency = aReq.getParameter("agency");
		  String deskName = aReq.getParameter("deskName");
		  
		  
		  if(agency!=null && !agency.equals("") && deskName!=null && 
				  deskName.length()>0)
		  {
			  try
			  {
				  Long id = Long.valueOf(agency);
				  Agency ag = (Agency)portletState.getAgencyManagementPortletUtil().getEntityObjectById(Agency.class, id);
				  if(ag==null)
				  {
					  portletState.addError(aReq, "No agency on the system matches the agency you selected. Please select a valid agency", 
							  portletState);
				  }else
				  {

					  portletState.setAgencyEntity(ag);
					  portletState.setDeskName(deskName);
					  
					  EndorsementDesk ed = new EndorsementDesk();
					  ed.setAgency(ag);
					  ed.setEndorseDeskName(deskName);
					  ed.setIsActive(Boolean.TRUE);
					  if(swpService.createNewRecord(ed)!=null)
					  {
						  portletState.addSuccess(aReq, "Agency Desk successfully created for Agency " + ag.getAgencyName(), portletState);
						  portletState.setAgencyEntity(null);
						  portletState.setDeskName(null);
					  }
				  }
			  }catch(NumberFormatException e)
			  {
				  portletState.addError(aReq, "No agency on the system matches the agency you selected. Please select a valid agency", 
						  portletState);
			  }
			  
		  }else
		  {
			  portletState.addError(aReq, "Ensure you provide all the data required", portletState);
		  }
	}
	
	private void saveAgency(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, AgencyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		  String agencyType = aReq.getParameter("agencyType");
		  String agencyName = aReq.getParameter("agencyName");
		  String contactName = aReq.getParameter("contactName");
		  String agencyPhone = aReq.getParameter("agencyPhone");
		  String agencyEmail = aReq.getParameter("agencyEmail");
		  String selectedAgencyId = aReq.getParameter("selectedAgencyId");
		  
		  portletState.setAgencyEmail(agencyEmail);
		  portletState.setAgencyName(agencyName);
		  portletState.setAgencyPhone(agencyPhone);
		  portletState.setAgencyType(AgencyType.fromString(agencyType).getValue());
		  portletState.setContactName(contactName);
		  
		  if(agencyType!=null && !agencyType.equals("") && agencyName!=null && 
				  agencyName.length()>0 && contactName!=null && contactName.length()>0 && 
				  agencyPhone!=null && agencyPhone.length()>0 && agencyEmail!=null && agencyEmail.length()>0)
		  {
			  try{
				  Long id = Long.valueOf(selectedAgencyId);
				  Agency ag = portletState.getAgencyManagementPortletUtil().getAgencyByTypeAndNameAndNotId
						  (agencyType, agencyName, id);
				  
				  //if agency exists that has the type and name that is not the id, inform error
				  if(ag!=null)
				  {
					  portletState.addError(aReq, "An agency of this type with the agency name provided already exists in this system. Please provide a different name to proceed", 
							  portletState);
				  }else
				  {
					  Agency agency = (Agency)portletState.getAgencyManagementPortletUtil().getEntityObjectById(Agency.class, id);
					  agency.setAgencyEmail(agencyEmail);
					  agency.setAgencyName(agencyName);
					  agency.setAgencyPhone(agencyPhone);
					  agency.setAgencyType(AgencyType.fromString(agencyType));
					  agency.setContactName(contactName);
					  agency.setCreatedDate(new Timestamp((new Date()).getTime()));
					  swpService2.updateRecord(agency);
					  portletState.addSuccess(aReq, "Agency successfully updated", portletState);
					  
				  }
			  }catch(NumberFormatException e)
			  {
				  e.printStackTrace();
				  portletState.addError(aReq, "Ensure you have selected a valid agency before you can proceed", portletState);
			  }
			  
		  }else
		  {
			  portletState.addError(aReq, "Ensure you provide all the data required", portletState);
		  }
	}
	
	
	private void saveAgencyDesk(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, AgencyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		  String agencyType = aReq.getParameter("agencyType");
		  String deskName = aReq.getParameter("deskName");
		  String selectedAgencyId = aReq.getParameter("actId");
		  
		  portletState.setAgencyType(agencyType);
		  portletState.setDeskName(deskName);
		  
		  if(agencyType!=null && !agencyType.equals("") && deskName!=null && deskName.length()>0)
		  {
			  try{
				  Long id = Long.valueOf(selectedAgencyId);
				  Agency ag = portletState.getAgencyManagementPortletUtil().getAgencyByTypeAndNameAndNotId
						  (deskName, agencyType, id);
				  
				  //if agency exists that has the type and name that is not the id, inform error
				  if(ag!=null)
				  {
					  portletState.addError(aReq, "An Endorsement desk of this type with the agency desk name provided already exists in this system. Please provide a different name to proceed", 
							  portletState);
				  }else
				  {
					  Long agid = Long.valueOf(agencyType);
					  Agency a_g = (Agency)portletState.getAgencyManagementPortletUtil().getEntityObjectById(Agency.class, agid);
					  EndorsementDesk ed = (EndorsementDesk)portletState.getAgencyManagementPortletUtil().getEntityObjectById(EndorsementDesk.class, agid);
					  ed.setAgency(a_g);
					  ed.setEndorseDeskName(deskName);
					  ed.setIsActive(Boolean.TRUE);
					  swpService.updateRecord(ed);
					  
					  portletState.addSuccess(aReq, "Endorsement Desk successfully updated", portletState);
					  portletState.setAgencyType(null);
					  portletState.setDeskName(null);
					  
					  Collection<EndorsementDesk> endorsementDeskList = (Collection<EndorsementDesk>)portletState.getAgencyManagementPortletUtil().getAllEntity(EndorsementDesk.class);
			        	portletState.setDeskListing(endorsementDeskList);
					  aRes.setRenderParameter("jspPage", "/html/agencymanagementportlet/desklisting.jsp");
					  portletState.setCurrentTab(VIEW_TABS.VIEW_ALL_DESK);
					  
					  
				  }
			  }catch(NumberFormatException e)
			  {
				  e.printStackTrace();
				  portletState.addError(aReq, "Ensure you have selected a valid agency before you can proceed", portletState);
			  }
			  
		  }else
		  {
			  portletState.addError(aReq, "Ensure you provide all the data required", portletState);
		  }
	}


	private void saveDesk(ActionRequest aReq, ActionResponse aRes,
			SwpService swpService2, AgencyManagementPortletState portletState) {
		// TODO Auto-generated method stub
		  String agency = aReq.getParameter("agency");
		  String deskName = aReq.getParameter("deskName");
		  String selectedEndorsementDesk = aReq.getParameter("selectedEndorsementDesk");
		  try
		  {
			  EndorsementDesk ed = (EndorsementDesk)portletState.getAgencyManagementPortletUtil().
					  getEntityObjectById(EndorsementDesk.class, Long.valueOf(selectedEndorsementDesk));
			  Agency ag = (Agency)portletState.getAgencyManagementPortletUtil().
					  getEntityObjectById(Agency.class, Long.valueOf(agency));
			  portletState.setEndorsementDeskEntity(ed);
			  portletState.setAgencyEntity(ag);
			  portletState.setDeskName(deskName);
			  if(ed!=null && ag!=null && deskName!=null && deskName.length()>0)
			  {
				  ed.setAgency(ag);
				  ed.setEndorseDeskName(deskName);
				  swpService.updateRecord(ed);
				  portletState.addSuccess(aReq, "Endorsement Desk successfully updated" +
				  		"", portletState);
			  }else
			  {
				  portletState.addError(aReq, "Ensure you provide all the data required", portletState);
			  }
		  }catch(NumberFormatException e)
		  {
			  portletState.addError(aReq, "Ensure you have selected a valid endorsement desk before you can proceed", portletState);
		  }
	}

}
