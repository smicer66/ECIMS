package com.ecims.commins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;

import smartpay.entity.Applicant;
import smartpay.entity.Application;
import smartpay.entity.Certificate;
import smartpay.entity.Company;
import smartpay.entity.PortalUser;
import smartpay.entity.enumerations.CertificateStatus;
import smartpay.entity.enumerations.RoleTypeConstants;
import smartpay.service.SwpService;

import com.sf.primepay.smartpay13.ServiceLocator;

import common.Logger;

/**
 * Servlet implementation class ActiveServlet
 */
public class ActiveServlet extends HttpServlet {
	Logger log = Logger.getLogger(ActiveServlet.class);
	ServiceLocator serviceLocator = ServiceLocator.getInstance();
	SwpService swpService = serviceLocator.getSwpService();
	PrbCustomService swpCustomService = PrbCustomService.getInstance();
	//CacProcessPortletUtil sub = new CacProcessPortletUtil();
	String sep = File.separator;

	public ActiveServlet() {

	}
	
	

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		log.info("Action ==" + action);
		String cert_id = request.getParameter("gyus") == null ? "0" : request.getParameter("gyus");
		log.info("gyus ==" + cert_id);
		Long certId = Long.valueOf(cert_id);
		String pu_id = request.getParameter("iosdp") == null ? "0" : request.getParameter("iosdp");
		log.info("iosdp ==" + pu_id);

		
		
//		gyus=")%>'+aert +'&iosdp='+usId" +		//gyus = portalUser, iosdp = certificate
//		handleButtonAction2(action, aert, usId)		//aert = portalUser, usId = certificate
//				"" +
//				"
		
		String actr = request.getParameter("actr") == null ? "0" : request.getParameter("actr");
		log.info("actr ==" + actr);
		Long puId =  Long.valueOf(pu_id);
		
		
		if(action.equalsIgnoreCase("mydashboard"))
		{
			try{
				PortalUser pu = (PortalUser) swpService.getRecordById(PortalUser.class, puId);
				if(pu!=null && pu.getRoleType().getName().getValue().equals(RoleTypeConstants.ROLE_ACCREDITOR_USER.getValue()))
				{
					response.sendRedirect("");
				}
				else if(pu!=null && pu.getRoleType().getName().getValue().equals(RoleTypeConstants.ROLE_REGULATOR_USER.getValue()))
				{
					
				}
				else if(pu!=null && pu.getRoleType().getName().getValue().equals(RoleTypeConstants.ROLE_NSA_USER.getValue()))
				{
					
				}
				else if(pu!=null && pu.getRoleType().getName().getValue().equals(RoleTypeConstants.ROLE_NSA_ADMIN.getValue()))
				{
					
				}
				else if(pu!=null && pu.getRoleType().getName().getValue().equals(RoleTypeConstants.ROLE_INFORMATION_USER.getValue()))
				{
					
				}
				else if(pu!=null && pu.getRoleType().getName().getValue().equals(RoleTypeConstants.ROLE_END_USER.getValue()))
				{
					
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		else if (action.equalsIgnoreCase("generateAudit"))
		{
			try{
				PortalUser pu = (PortalUser) swpService.getRecordById(PortalUser.class, puId);
				if(pu!=null)
				{
					
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
		if (action.equalsIgnoreCase("downloadCertificate"))
		{
			log.info("downloadCertificate ==" + action);
			try{
				PortalUser pu = (PortalUser) swpService.getRecordById(PortalUser.class, puId);
				String hql = "Select app from Applicant app where app.portalUser.id = " + pu.getId();
				Applicant app = (Applicant)swpService.getUniqueRecordByHQL(hql);
				if(app!=null)
				{
					Certificate cert = (Certificate) swpService.getRecordById(Certificate.class, certId);
					if(pu != null && cert!=null && cert.getApplication().getApplicant()!=null && 
							cert.getApplication().getApplicant().getPortalUser().getId().equals(pu.getId())){
						
						String fileName = "EUC_for_App_No_" + app.getApplicantNumber().replace(" ", "_") + 
								"_" + "for_CertNo_" + cert.getCertificateNo() + ".pdf";
						if(fileName!=null)
						{
							//handleDownloadCertificate(certId, puId, request, response, "NSACertificate.rptdesign", "PaymentHistory", fileName);
							if(pu.getAgency()!=null)
							{
								//handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateForAgency.rptdesign", "PaymentHistory", fileName);
								System.out.println("1---NSAEmptyCertificateCompleteForAgency");
								handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateCompleteForAgency.rptdesign", "PaymentHistory", fileName);
							}
							else
							{
								//handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificate.rptdesign", "PaymentHistory", fileName);
								System.out.println("2---NSAEmptyCertificateComplete");
								handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateComplete.rptdesign", "PaymentHistory", fileName);
							}
						}
					}
				}else
				{
					Certificate cert = (Certificate) swpService.getRecordById(Certificate.class, certId);
					if(pu != null && cert!=null && 
							cert.getApplication().getPortalUser().getId().equals(pu.getId())){
						
						String fileName = "EUC_for_App_No_" + 
								pu.getAgency().getAgencyName().replace(" ", "_") + 
								"_" + "for_CertNo_" + cert.getCertificateNo() + ".pdf";
						if(fileName!=null)
						{
							//handleDownloadCertificate(certId, puId, request, response, "NSACertificate.rptdesign", "PaymentHistory", fileName);
							//handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificate.rptdesign", "PaymentHistory", fileName);
							if(pu.getAgency()!=null)
							{
								System.out.println("3---NSAEmptyCertificateCompleteForAgency");
								handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateCompleteForAgency.rptdesign", "PaymentHistory", fileName);
							}
							else
							{
								System.out.println("4---NSAEmptyCertificateComplete");
								handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateComplete.rptdesign", "PaymentHistory", fileName);
							}
									
						}
					}
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		if (action.equalsIgnoreCase("downloadCertificateTicket"))
		{
			log.info("downloadCertificateTicket ==" + action);
			try{
				PortalUser pu = (PortalUser) swpService.getRecordById(PortalUser.class, puId);
				String hql = "Select app from Applicant app where app.portalUser.id = " + pu.getId();
				Applicant app = (Applicant)swpService.getUniqueRecordByHQL(hql);
				if(app!=null)
				{
					Application appn = (Application) swpService.getRecordById(Application.class, certId);
					if(pu != null && appn!=null && ((appn.getApplicant()!=null && 
							appn.getApplicant().getPortalUser().getId().equals(pu.getId())) || 
							(appn.getPortalUser()!=null && 
									appn.getPortalUser().getId().equals(pu.getId()))) ){
						
						String fileName = "EUC_Ticket_for_App_No_" + app.getApplicantNumber().replace(" ", "_") + 
								"_" + "for_AppNo_" + appn.getApplicationNumber() + ".pdf";
						if(fileName!=null)
						{
							//handleDownloadCertificate(certId, puId, request, response, "NSACertificate.rptdesign", "PaymentHistory", fileName);
							if(pu.getAgency()!=null)
							{
								//handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateForAgency.rptdesign", "PaymentHistory", fileName);
								if(appn.getPortCode()!=null)
								{
									System.out.println("5---NSAEmptyCertificateTicketForAgency");
									handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateTicketForAgency.rptdesign", "PaymentHistory", fileName);
								}else 
								{
									System.out.println("6---NSAEmptyCertificateTicketForAgencyNoPort");
									handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateTicketForAgencyNoPort.rptdesign", "PaymentHistory", fileName);
								}
							}
							else
							{
								//handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificate.rptdesign", "PaymentHistory", fileName);
								if(appn.getPortCode()!=null)
								{
									System.out.println("7---NSAEmptyCertificateTicket");
									handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateTicket.rptdesign", "PaymentHistory", fileName);
								}else
								{
									System.out.println("8---NSAEmptyCertificateTicketNoPort");
									handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateTicketNoPort.rptdesign", "PaymentHistory", fileName);
								}
							}
						}
					}
				}else
				{
					Certificate cert = (Certificate) swpService.getRecordById(Certificate.class, certId);
					if(pu != null && cert!=null && 
							cert.getApplication().getPortalUser().getId().equals(pu.getId())){
						
						String fileName = "EUC_for_App_No_" + 
								pu.getAgency().getAgencyName().replace(" ", "_") + 
								"_" + "for_CertNo_" + cert.getCertificateNo() + ".pdf";
						if(fileName!=null)
						{
							//handleDownloadCertificate(certId, puId, request, response, "NSACertificate.rptdesign", "PaymentHistory", fileName);
							//handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificate.rptdesign", "PaymentHistory", fileName);
							if(pu.getAgency()!=null)
							{
								if(cert.getApplication().getPortCode()!=null)
								{
									System.out.println("9---NSAEmptyCertificateCompleteForAgency");
									handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateCompleteForAgency.rptdesign", "PaymentHistory", fileName);
								}else
								{
									System.out.println("10---NSAEmptyCertificateCompleteForAgencyNoPort");
									handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateCompleteForAgencyNoPort.rptdesign", "PaymentHistory", fileName);
								}
							}
							else
							{
								if(cert.getApplication().getPortCode()!=null)
								{
									System.out.println("11---NSAEmptyCertificateComplete");
									handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateComplete.rptdesign", "PaymentHistory", fileName);
								}
								else
								{
									System.out.println("12---NSAEmptyCertificateCompleteNoPort");
									handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateCompleteNoPort.rptdesign", "PaymentHistory", fileName);
								}
							}
						}
					}
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		if (action.equalsIgnoreCase("downloadCertificateComplete"))
		{
			log.info("downloadCertificate ==" + action);
			try{
				PortalUser pu = (PortalUser) swpService.getRecordById(PortalUser.class, puId);
				String hql = "Select app from Applicant app where app.portalUser.id = " + pu.getId();
				Applicant app = (Applicant)swpService.getUniqueRecordByHQL(hql);
				if(app!=null)
				{
					Certificate cert = (Certificate) swpService.getRecordById(Certificate.class, certId);
					if(pu != null && cert!=null && 
							cert.getApplication().getApplicant().getPortalUser().getId().equals(pu.getId())){
						
						String fileName = "EUC_for_App_No_" + app.getApplicantNumber() + ".pdf";
						if(fileName!=null)
						{
							//handleDownloadCertificate(certId, puId, request, response, "NSACertificate.rptdesign", "PaymentHistory", fileName);
							if(pu.getAgency()!=null)
							{
								if(cert.getApplication().getPortCode()!=null)
								{
									System.out.println("13---NSAEmptyCertificateCompleteForAgency");
									handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateCompleteForAgency.rptdesign", "PaymentHistory", fileName);
								}else 
								{
									System.out.println("14---NSAEmptyCertificateCompleteForAgencyNoPort");
									handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateCompleteForAgencyNoPort.rptdesign", "PaymentHistory", fileName);
								}
							}
							else
							{
								if(cert.getApplication().getPortCode()!=null)
								{
									System.out.println("15---NSAEmptyCertificateComplete");
									handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateComplete.rptdesign", "PaymentHistory", fileName);
								}else
								{
									System.out.println("16---NSAEmptyCertificateCompleteNoPort");
									handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateCompleteNoPort.rptdesign", "PaymentHistory", fileName);
								}
							}
						}
					}
				}else
				{
					Certificate cert = (Certificate) swpService.getRecordById(Certificate.class, certId);
					if(pu != null && cert!=null && 
							cert.getApplication().getPortalUser().getId().equals(pu.getId())){
						
						String fileName = "EUC_for_App_No_" + pu.getAgency().getAgencyName().replace(" ", "_") + 
								"_" + "for_CertNo_" + cert.getCertificateNo() + ".pdf";
						if(fileName!=null)
						{
							//handleDownloadCertificate(certId, puId, request, response, "NSACertificate.rptdesign", "PaymentHistory", fileName);
							if(pu.getAgency()!=null)
							{
								if(cert.getApplication().getPortCode()!=null)
								{
									System.out.println("17---NSAEmptyCertificateCompleteForAgency");
									handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateCompleteForAgency.rptdesign", "PaymentHistory", fileName);
								}else
								{
									System.out.println("18---NSAEmptyCertificateCompleteForAgencyNoPort");
									handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateCompleteForAgencyNoPort.rptdesign", "PaymentHistory", fileName);
								}
							}
							else
							{
								if(cert.getApplication().getPortCode()!=null)
								{
									System.out.println("19---NSAEmptyCertificateComplete");
									handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateComplete.rptdesign", "PaymentHistory", fileName);
								}else
								{
									System.out.println("20---NSAEmptyCertificateCompleteNoPort");
									handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateCompleteNoPort.rptdesign", "PaymentHistory", fileName);
								}
							}
						}
					}
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		else if (action.equalsIgnoreCase("downloadAgencyTicket"))
		{
			log.info("downloadCertificate1 ==" + action + " puId = " + puId + " && certid = " + certId);
			//
			try{
				PortalUser pu = (PortalUser) swpService.getRecordById(PortalUser.class, puId);
				Application appln = (Application) swpService.getRecordById(Application.class, certId);
				if(pu!=null)
				{
					
					if(pu != null && appln!=null && 
							appln.getPortalUser().getId().equals(pu.getId())){
						
						log.info("appln.getPortalUser().getId() = " + appln.getPortalUser().getId());
						log.info("pu.getId() = " + pu.getId());
						
						String fileName = "EUC_Ticket_for_App_No_" + appln.getApplicationNumber() + ".pdf";
						if(fileName!=null)
						{
							//handleDownloadCertificate(certId, puId, request, response, "NSACertificate.rptdesign", "PaymentHistory", fileName);
							if(pu.getAgency()!=null)
							{
								//handleDownloadCertificate(certId, puId, request, response, "NSACertificateForAgency.rptdesign", "PaymentHistory", fileName);
								if(appln.getPortCode()!=null)
								{
									System.out.println("21---NSAEmptyCertificateTicketForAgency");
									handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateTicketForAgency.rptdesign", "PaymentHistory", fileName);
								}else
								{
									System.out.println("22---NSAEmptyCertificateTicketForAgencyNoPort");
									handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateTicketForAgencyNoPort.rptdesign", "PaymentHistory", fileName);
								}
							}
							else
							{
								if(appln.getPortCode()!=null)
								{
								//handleDownloadCertificate(certId, puId, request, response, "NSACertificate.rptdesign", "PaymentHistory", fileName);
									System.out.println("23---NSAEmptyCertificateTicket");
									handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateTicket.rptdesign", "PaymentHistory", fileName);
								}else
								{
									System.out.println("24---NSAEmptyCertificateTicketNoPort");
									handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateTicketNoPort.rptdesign", "PaymentHistory", fileName);
								}
							}
						}
					}
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		else if (action.equalsIgnoreCase("downloadApplicantTicket"))
		{
			log.info("downloadCertificate ==" + action);
			try{
				
				log.info("puId ==" + puId);
				log.info("certId ==" + certId);
				PortalUser pu = (PortalUser) swpService.getRecordById(PortalUser.class, puId);
				Application cert = (Application) swpService.getRecordById(Application.class, certId);
				log.info("cert ==" + cert.getApplicant().getPortalUser().getId());
				if(pu != null && cert!=null && 
						cert.getApplicant().getPortalUser().getId().equals(pu.getId())){
					
					log.info("appln.cert().getId() = " + cert.getApplicant().getId() + " && jus =  " + cert.getApplicant().getPortalUser().getId());
					log.info("pu.getId() = " + pu.getId());
					
					String fileName = "EUC_Ticket_for_App_No_" + cert.getApplicationNumber() + ".pdf";
					if(fileName!=null)
					{
						//handleDownloadCertificate(certId, puId, request, response, "NSACertificate.rptdesign", "PaymentHistory", fileName);
						if(pu.getAgency()!=null)
						{
							//handleDownloadCertificate(certId, puId, request, response, "NSACertificateForAgency.rptdesign", "PaymentHistory", fileName);
							if(cert.getPortCode()!=null)
							{
								System.out.println("25---NSAEmptyCertificateTicketForAgency");
								handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateTicketForAgency.rptdesign", "PaymentHistory", fileName);
							}
							else
							{
								System.out.println("26---NSAEmptyCertificateTicketForAgencyNoPort");
								handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateTicketForAgencyNoPort.rptdesign", "PaymentHistory", fileName);
							}
						}
						else
						{
							//handleDownloadCertificate(certId, puId, request, response, "NSACertificate.rptdesign", "PaymentHistory", fileName);
							if(cert.getPortCode()!=null)
							{
								System.out.println("27---NSAEmptyCertificateTicket");
								handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateTicket.rptdesign", "PaymentHistory", fileName);
							}else
							{
								System.out.println("28---NSAEmptyCertificateTicketNoPort");
								handleDownloadCertificate(certId, puId, request, response, "NSAEmptyCertificateTicketNoPort.rptdesign", "PaymentHistory", fileName);
							}
						}
					}
				}
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	//
	
	
	
	
	public void handleDownloadCertificate(Long certId, Long puId, HttpServletRequest request, HttpServletResponse response, 
			String reportName, String outputFilePath, String fileName){
		IReportEngine reportEngine = null;
		 
		 
        EngineConfig config = new EngineConfig();
        log.info("-----------------user dir is " + System.getProperty("user.dir"));
        String userDir = System.getProperty("user.dir");
        config.setLogConfig(userDir +sep+ "birtlogs", Level.SEVERE);
        config.setEngineHome(userDir +sep+ "ReportEngine");

        try {
                Platform.startup(config);
        } catch (BirtException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
        }

        IReportEngineFactory iReportEngineFactory = (IReportEngineFactory) Platform
                        .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
        reportEngine = iReportEngineFactory.createReportEngine(config);

        String reportsUrl = userDir +sep+"ReportEngine"+sep+"Reports";
        
        log.info("reportsUrl = "+reportsUrl);
        
        IReportRunnable runnableReport = null; 
        log.info("reportsUrl1 = ");
        try {
                runnableReport = reportEngine.openReportDesign(reportsUrl + sep+""+reportName);
                log.info("reportsUrl2 = ");
        } catch (EngineException e1) {
                // TODO Auto-generated catch block
        	log.info("reportsUrl3 = ");
                e1.printStackTrace();
                
        }
        
        log.info("reportsUrl4 = ");
        IRunAndRenderTask runAndRenderTask = reportEngine.createRunAndRenderTask(runnableReport);
        log.info("reportsUrl5 = ");
        PDFRenderOption renderOption = new PDFRenderOption();
        log.info("reportsUrl6 = ");
        renderOption.setOutputFileName(reportsUrl + sep+""+ outputFilePath+sep+""+fileName);
        log.info("reportsUrl7 = ");
        renderOption.setOutputFormat(IRenderOption.OUTPUT_FORMAT_PDF);
        log.info("reportsUrl8 = ");
       
        runAndRenderTask.setRenderOption(renderOption);
        log.info("reportsUrl9 = ");
        //runAndRenderTask.setRenderOption(htmlRenderOption);
        runAndRenderTask.setParameterValue("certId", certId);
        runAndRenderTask.setParameterValue("puId", puId);
        log.info("reportsUrl10 = ");
        try {
			runAndRenderTask.run();
			log.info("reportsUrl11 = ");
		} catch (EngineException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			log.info("reportsUrl12 = ");
		}
        File file = null;
        try{
        	log.info("reportsUrl13 = ");
        	file = new File(reportsUrl + sep+""+ outputFilePath+sep+""+fileName);
        	log.info("File Name ==" + reportsUrl + sep+""+ outputFilePath+sep+""+fileName);
        	log.info("reportsUrl13 = ");
        	FileInputStream baos = new FileInputStream(file);
        	log.info("reportsUrl14 = ");
			response.setContentType("application/pdf");
			log.info("reportsUrl15 = ");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			log.info("reportsUrl16 = ");
			
 
			int read=0;
			byte[] bytes = new byte[1024];
			OutputStream os = response.getOutputStream();
 
			log.info("reportsUrl17 = ");
			while((read = baos.read(bytes))!= -1){
				os.write(bytes, 0, read);
			}
			log.info("reportsUrl18 = ");
			os.flush();
			os.close();
        }catch(Exception ex){
        	ex.printStackTrace();
        	log.info("reportsUrl19 = ");
        }
        
        
	}

	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		log.info("in doPost of passport servlet");
	}

}
