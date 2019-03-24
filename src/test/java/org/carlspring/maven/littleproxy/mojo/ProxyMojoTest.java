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

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * @author Martin Todorov (carlspring@gmail.com)
 */
public class ProxyMojoTest
        extends AbstractLittleProxyMojoTest
{

    StartProxyMojo startMojo;

    Map<String, String> userCredentials = new LinkedHashMap<String, String>();


    public void setUp()
            throws Exception
    {
        super.setUp();

        startMojo = (StartProxyMojo) lookupMojo("start", POM_PLUGIN);
    }

    public void testMojo()
            throws Exception
    {
        userCredentials.put("admin", "password123");
        userCredentials.put("testuser", "password");

        startMojo.setPort(8180);
        startMojo.setUserCredentials(userCredentials);
        startMojo.execute();

        final String hostname = "www.google.com";
        HttpHost target = new HttpHost(hostname, 80, "http");
        HttpHost proxy = new HttpHost("127.0.0.1", 8180, "http");

        CloseableHttpClient client = getCloseableHttpClientWithAuthenticatedProxy(proxy);

        retrieveUrl(target, proxy, client);

    }

}
