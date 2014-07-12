package org.carlspring.maven.littleproxy.authentication;

import java.util.LinkedHashMap;
import java.util.Map;

import org.littleshoot.proxy.ProxyAuthenticator;

/**
 * @author mtodorov
 */
public class SimpleProxyAuthenticator
        implements ProxyAuthenticator
{

    private Map<String, String> userCredentials = new LinkedHashMap<String, String>();


    public SimpleProxyAuthenticator(Map<String, String> userCredentials)
    {
        this.userCredentials = userCredentials;
    }

    @Override
    public boolean authenticate(String username,
                                String password)
    {
        System.out.println("Checking credentials for " + username + ":" + password);

        return userCredentials.containsKey(username) && password.equals(userCredentials.get(username));
    }

    public Map<String, String> getUserCredentials()
    {
        return userCredentials;
    }

    public void setUserCredentials(Map<String, String> userCredentials)
    {
        this.userCredentials = userCredentials;
    }

}
