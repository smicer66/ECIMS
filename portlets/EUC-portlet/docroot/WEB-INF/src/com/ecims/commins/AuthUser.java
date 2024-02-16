package com.ecims.commins;

import org.apache.log4j.Logger;

import com.rsa.authagent.authapi.AuthAgentException;
import com.rsa.authagent.authapi.AuthSession;
import com.rsa.authagent.authapi.AuthSessionFactory;

public class AuthUser {

	public static final String ACCESS_DENIED = "Access Denied";
	public static final String PASSCODE_ACCEPTED = "Passcode Accepted";
	public static final String PIN_REJECTED = "New Pin rejected";
	public static final String PIN_ACCEPTED = "New Pin accepted; please login again with the new Pin.";
	public static final String GOOD_AUTH = "Authentication successful";
	public static final String BAD_AUTH = "Authentication incomplete";
	public static final String NEW_PIN = "New Pin Required";
	public static final String NEXT_CODE = "Next Code Required";
    
    private AuthSessionFactory api = null;
    private Logger log = Logger.getLogger(AuthUser.class);
	private String userName=null;
	private String passCode=null;
    
    public AuthUser(String path, String passCode, String userName) throws Exception
    {
        try
        {
            api = AuthSessionFactory.getInstance(path);
            this.userName = userName; 
            this.passCode = passCode;
            
        }
        catch (AuthAgentException e)
        {
            log.info("Can't create api: " + e.getMessage());
            throw e;
        }
    }
    
    
    
    
    public String auth(String userPassCode) throws Exception
    {
        
        AuthSession session;

        session = api.createUserSession();
        
        int authStatus = AuthSession.ACCESS_DENIED;
        authStatus = session.lock(userName);
        authStatus = session.check(userName, userPassCode);
        authStatus = finalizeAuth(authStatus, session);
        
        
        session.close();
        api.shutdown();
        if (authStatus == AuthSession.ACCESS_OK)
        {
        	return GOOD_AUTH;
        }
        else if (authStatus == AuthSession.NEW_PIN_REQUIRED)
        {
        	return  NEW_PIN;
        }
        else if (authStatus == AuthSession.NEXT_CODE_REQUIRED)
        {
        	return  NEXT_CODE;
        }
        else if (authStatus == AuthSession.PIN_ACCEPTED)
        {
        	return  PIN_ACCEPTED;
        }
        else
        {
            return BAD_AUTH;
        }
    }
    
    
    public int setPin(String pin) throws Exception
    {
        
        AuthSession session;

        session = api.createUserSession();
        
        int authStatus = AuthSession.ACCESS_DENIED;
        authStatus = session.lock(userName);
        authStatus = session.pin(pin);
        //authStatus = finalizeAuth(authStatus, session);
        
        
        session.close();
        return authStatus;
    }
    
    
    public int setNextCode(String nextCode) throws Exception
    {
        
        AuthSession session;

        session = api.createUserSession();
        
        int authStatus = AuthSession.ACCESS_DENIED;
        authStatus = session.lock(userName);
        authStatus = session.next(nextCode);
        
        session.close();
        return authStatus;
    }
    
   
    
    private int finalizeAuth(int authStatus, AuthSession session)
            throws Exception
    {
        int finalStatus;
        switch(authStatus)
        {
        case AuthSession.NEW_PIN_REQUIRED :
            NewPinSession newPin = new NewPinSession(session);
        	//finalStatus = newPin.process();
            finalStatus = AuthSession.NEW_PIN_REQUIRED;
        	break;
        
        case AuthSession.NEXT_CODE_REQUIRED :
            NextCodeSession nextCode = new NextCodeSession(session);
            finalStatus = nextCode.process();
            break;
            
        case AuthSession.ACCESS_OK :
            finalStatus = authStatus;
        	break;
        
        default:
            finalStatus = AuthSession.ACCESS_DENIED;
            break;
        }
        return finalStatus;
    }
}
