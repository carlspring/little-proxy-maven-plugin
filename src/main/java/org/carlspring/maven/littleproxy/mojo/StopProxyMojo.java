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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

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
    @Parameter(property = "proxy.fail.if.already.running", defaultValue = "true")
    boolean failIfNotRunning;


    @Override
    public void doExecute()
            throws MojoExecutionException, MojoFailureException
    {
        try
        {
            // TODO: Implement this properly:
            proxyServer.stop();
        }
        catch (Exception e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
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
