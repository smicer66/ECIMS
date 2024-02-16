package com.ecims.commins;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.log4j.Logger;
import org.apache.ws.security.WSSecurityEngine;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecSignature;
import org.apache.ws.security.message.WSSignEnvelope;
import org.w3c.dom.Document;


public class Util1 {

	Logger log = Logger.getLogger(Util.class);
	private static final WSSecurityEngine secEngine = new WSSecurityEngine();
	private static final Crypto crypto = CryptoFactory.getInstance();
	
	
	
}
