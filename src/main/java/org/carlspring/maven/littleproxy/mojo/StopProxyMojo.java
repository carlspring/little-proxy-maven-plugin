package org.carlspring.maven.littleproxy.mojo;

/**
 * Copyright 2019, Carlspring Consulting & Development Ltd.
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Martin Todorov (carlspring@gmail.com)
 */
@Mojo(name = "stop")
public class StopProxyMojo
        extends AbstractLittleProxyMojo
{

    /**
     * Whether to fail, if LittleProxy is not running.
     */
    @Parameter(property = "proxy.fail.if.not.running", defaultValue = "true")
    boolean failIfNotRunning;
    
    private static Logger logger = LoggerFactory.getLogger(StopProxyMojo.class);


    @Override
    public void doExecute()
            throws MojoExecutionException
    {
        Socket socket = null;
        try
        {
            socket = new Socket("localhost", getShutdownPort());
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bw.write(getHash());
            bw.newLine();
            bw.flush();

            final String response = br.readLine();

            getLog().debug(response);

            if (response == null || !response.contains("success"))
            {
                throw new MojoExecutionException("Failed to shutdown gracefully!");
            }
            else
            {
                getLog().info("LittleProxy shutdown successful.");
            }
        }
        catch (ConnectException e)
        {
            if (failIfNotRunning && !e.getMessage().contains("Connection refused"))
            {
                throw new MojoExecutionException(e.getMessage(), e);
            }
            else
            {
                getLog().warn("Nothing to shut down, as the LittleProxy service was not running on port " + port + ".");
            }
        }
        catch (Exception e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        finally
        {
            if (socket != null)
            {
                try 
                {
                    socket.close();
                } 
                catch (IOException e) 
                {
                    logger.trace("Exception while closing Socket.", e);
                }   
            }
        }
    }

    public boolean isFailIfNotRunning()
    {
        return failIfNotRunning;
    }

    public void setFailIfNotRunning(boolean failIfNotRunning)
    {
        this.failIfNotRunning = failIfNotRunning;
    }

}
