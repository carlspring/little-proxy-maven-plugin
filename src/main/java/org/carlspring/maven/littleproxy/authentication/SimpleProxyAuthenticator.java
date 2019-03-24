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

    private Map<String, String> userCredentials = new LinkedHashMap<>();


    public SimpleProxyAuthenticator(Map<String, String> userCredentials)
    {
        this.userCredentials = userCredentials;
    }

    @Override
    public boolean authenticate(String username,
                                String password)
    {
        return userCredentials.containsKey(username) && password.equals(userCredentials.get(username));
    }

    @Override
    public String getRealm()
    {
        return "LittleProxy";
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
