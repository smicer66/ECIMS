package com.ecims.commins;

import com.rsa.authagent.authapi.AuthSession;

public class NextCodeSession
{
    private AuthSession session;

    protected NextCodeSession(AuthSession session)
    {
        this.session = session;
    }

    /**
     * Processes the next tokencode of a user.
     * @return the status of the next tokencode
     * @throws Exception
     */
    public int process() throws Exception
    {
        String nextCode = null;
        //= io.input("Next tokencode: ");
        return session.next(nextCode);
    }

}
