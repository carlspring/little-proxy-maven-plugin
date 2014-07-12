package org.carlspring.maven.littleproxy.mojo;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

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


    protected Mojo lookupConfiguredMojo(String goal, String basedir)
            throws Exception
    {
        MavenProject project = readMavenProject(new File(basedir));

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

}
