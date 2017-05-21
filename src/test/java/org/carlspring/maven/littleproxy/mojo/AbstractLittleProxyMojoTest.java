package org.carlspring.maven.littleproxy.mojo;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.junit.Ignore;

/**
 * @author Martin Todorov (carlspring@gmail.com)
 */
@Ignore
public abstract class AbstractLittleProxyMojoTest
        extends AbstractMojoTestCase
{

    protected static final String TARGET_TEST_CLASSES = "target/test-classes";
    protected static final String POM_PLUGIN = TARGET_TEST_CLASSES + "/poms/pom-start.xml";


    protected Mojo lookupConfiguredMojo(String goal, String pomFile)
            throws Exception
    {
        MavenProject project = readMavenProject(new File(pomFile));

        return lookupConfiguredMojo(project, goal);
    }

    private MavenProject readMavenProject(File pom)
            throws Exception
    {
        MavenExecutionRequest request = new DefaultMavenExecutionRequest();
        request.setBaseDirectory(pom.getParentFile());
        ProjectBuildingRequest configuration = request.getProjectBuildingRequest();

        return lookup(ProjectBuilder.class).build(pom, configuration).getProject();
    }

    protected CloseableHttpClient getCloseableHttpClientWithAuthenticatedProxy(HttpHost proxy)
    {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("testuser", "password");

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(proxy.getHostName(), AuthScope.ANY_PORT), credentials);
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();

        clientBuilder.useSystemProperties();
        clientBuilder.setProxy(proxy);
        clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());

        return clientBuilder.build();
    }

    protected void retrieveUrl(HttpHost target,
                               HttpHost proxy,
                               CloseableHttpClient client)
            throws IOException
    {
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
