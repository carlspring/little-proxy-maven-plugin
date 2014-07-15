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

import javax.net.ssl.SSLSession;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import io.netty.handler.codec.http.HttpRequest;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.littleshoot.proxy.*;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

/**
 * @author Martin Todorov (carlspring@gmail.com)
 */
public abstract class AbstractLittleProxyMojo
        extends AbstractMojo
{

    /**
     * The port to start LittleProxy on.
     */
    @Parameter(property = "proxy.port", defaultValue = "8180")
    int port;

    /**
     * The port to start LittleProxy on.
     */
    @Parameter(property = "proxy.shutdown.port", defaultValue = "8181")
    int shutdownPort;

    /**
     * Whether to bypass running LittleProxy.
     */
    @Parameter(property = "proxy.shutdown.hash")
    static String hash;

    /**
     * Whether to bypass running LittleProxy.
     */
    @Parameter(property = "proxy.skip")
    boolean skip;

    static HttpProxyServerBootstrap serverBootstrap;

    static HttpProxyServer proxyServer;


    @Override
    public void execute()
            throws MojoExecutionException, MojoFailureException
    {
        if (skip)
        {
            getLog().info("Skipping LittleProxy execution.");
            return;
        }

        setupServer();

        doExecute();
    }

    protected void setupServer()
            throws MojoExecutionException
    {
        try
        {
            serverBootstrap = DefaultHttpProxyServer.bootstrap()
                                                    .plusActivityTracker(new LoggingActivityTracker())
                                                    .withPort(getPort());
        }
        catch (Exception e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Implement mojo logic here.
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    protected abstract void doExecute() throws MojoExecutionException, MojoFailureException;

    public HttpProxyServerBootstrap getServerBootstrap()
    {
        return serverBootstrap;
    }

    public void setServerBootstrap(HttpProxyServerBootstrap serverBootstrap)
    {
        this.serverBootstrap = serverBootstrap;
    }

    public static HttpProxyServer getProxyServer()
    {
        return proxyServer;
    }

    public void setProxyServer(HttpProxyServer proxyServer)
    {
        this.proxyServer = proxyServer;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public int getShutdownPort()
    {
        return shutdownPort;
    }

    public void setShutdownPort(int shutdownPort)
    {
        this.shutdownPort = shutdownPort;
    }

    public String getHash()
    {
        return hash;
    }

    public void setHash(String hash)
    {
        this.hash = hash;
    }

    public boolean isSkip()
    {
        return skip;
    }

    public void setSkip(boolean skip)
    {
        this.skip = skip;
    }

    private class LoggingActivityTracker
            implements ActivityTracker
    {

        @Override
        public void bytesReceivedFromClient(FlowContext flowContext, int numberOfBytes)
        {
        }

        @Override
        public void requestReceivedFromClient(FlowContext flowContext, HttpRequest httpRequest)
        {
        }

        @Override
        public void bytesSentToServer(FullFlowContext flowContext, int numberOfBytes)
        {
        }

        @Override
        public void requestSentToServer(FullFlowContext flowContext, HttpRequest httpRequest)
        {
        }

        @Override
        public void bytesReceivedFromServer(FullFlowContext flowContext, int numberOfBytes)
        {
        }

        @Override
        public void responseReceivedFromServer(FullFlowContext flowContext,
                                               io.netty.handler.codec.http.HttpResponse httpResponse)
        {
        }

        @Override
        public void bytesSentToClient(FlowContext flowContext, int numberOfBytes)
        {
        }

        @Override
        public void responseSentToClient(FlowContext flowContext,
                                         io.netty.handler.codec.http.HttpResponse httpResponse)
        {
        }

        @Override
        public void clientConnected(InetSocketAddress clientAddress)
        {
            getLog().debug("Client connected.");
        }

        @Override
        public void clientSSLHandshakeSucceeded(InetSocketAddress clientAddress,
                                                SSLSession sslSession)
        {
            getLog().debug("Client SSL handshake succeeded.");
        }

        @Override
        public void clientDisconnected(InetSocketAddress clientAddress,
                                       SSLSession sslSession)
        {
            getLog().debug("Client disconnected.");
        }

    }

}
