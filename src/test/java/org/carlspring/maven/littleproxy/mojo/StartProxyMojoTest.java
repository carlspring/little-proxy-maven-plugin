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

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.ChallengeState;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author Martin Todorov (carlspring@gmail.com)
 */
public class StartProxyMojoTest
        extends AbstractLittleProxyMojoTest
{

    StartProxyMojo startMojo;
    StopProxyMojo stopMojo;

    Map<String, String> userCredentials = new LinkedHashMap<String, String>();


    protected void setUp()
            throws Exception
    {
        super.setUp();

        startMojo = (StartProxyMojo) lookupConfiguredMojo("start", POM_PLUGIN);
        startMojo.setPort(8180);

        stopMojo = (StopProxyMojo) lookupConfiguredMojo("stop", POM_PLUGIN);

        userCredentials.put("admin", "password123");
        userCredentials.put("testuser", "password");
    }

    public void testMojo()
            throws MojoExecutionException,
                   MojoFailureException,
                   InterruptedException,
                   IOException
    {
        startMojo.setUserCredentials(userCredentials);
        startMojo.execute();

        final String hostname = "www.google.com";
        HttpHost target = new HttpHost(hostname, 80, "http");
        HttpHost proxy = new HttpHost("127.0.0.1", 8180, "http");

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("testuser", "password");

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(proxy.getHostName(), AuthScope.ANY_PORT), credentials);
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();

        clientBuilder.useSystemProperties();
        clientBuilder.setProxy(proxy);
        clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());

        CloseableHttpClient client = clientBuilder.build();

        try
        {
            RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
            HttpGet request = new HttpGet("/");
            request.setConfig(config);

            System.out.println("Executing request " + request.getRequestLine() + " to " + target + " via " + proxy);

            CloseableHttpResponse response = client.execute(target, request);
            try
            {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                EntityUtils.consume(response.getEntity());
            }
            finally
            {
                response.close();
            }

            assertEquals(200, response.getStatusLine().getStatusCode());
        }
        finally
        {
            client.close();
        }
    }

}
