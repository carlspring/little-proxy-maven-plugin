package org.carlspring.maven.littleproxy.mojo;

/**
 * Copyright 2014, Carlspring Consulting & Development Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.carlspring.maven.littleproxy.authentication.SimpleProxyAuthenticator;
import org.carlspring.maven.littleproxy.server.ShutdownServer;

import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author Martin Todorov
 */
@Mojo(name = "start")
public class StartProxyMojo
        extends AbstractLittleProxyMojo
{

    /**
     * Whether to fail, if there's already something running on the port.
     */
    @Parameter(property = "proxy.fail.if.already.running", defaultValue = "true")
    boolean failIfAlreadyRunning;

    @Parameter
    Map<String, String> userCredentials;


    @Override
    public void doExecute()
            throws MojoExecutionException, MojoFailureException
    {
        try
        {
            try
            {
                if (userCredentials != null && userCredentials.size() > 0)
                {
                    getLog().debug("Creating proxy authenticator with the following users:");
                    for (String username : userCredentials.keySet())
                    {
                        getLog().debug(" -> " + username + ":" + userCredentials.get(username));
                    }

                    final SimpleProxyAuthenticator proxyAuthenticator = new SimpleProxyAuthenticator(userCredentials);
                    proxyServer = getServerBootstrap().withProxyAuthenticator(proxyAuthenticator).start();
                }
                else
                {
                    proxyServer = getServerBootstrap().start();
                }

                startShutdownServer();
            }
            catch (Exception e)
            {
                throw new MojoExecutionException("Failed to start the LittleProxy server!", e);
            }
        }
        catch (Exception e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private void startShutdownServer()
    {
        final ShutdownServer shutdownServer = new ShutdownServer();
        shutdownServer.setPort(shutdownPort);
        shutdownServer.setShutdownHash(getHash());
        shutdownServer.startServer();
    }

    public boolean isFailIfAlreadyRunning()
    {
        return failIfAlreadyRunning;
    }

    public void setFailIfAlreadyRunning(boolean failIfAlreadyRunning)
    {
        this.failIfAlreadyRunning = failIfAlreadyRunning;
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
