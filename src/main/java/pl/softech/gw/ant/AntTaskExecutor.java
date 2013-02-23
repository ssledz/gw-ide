/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.ant;

import java.io.File;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/**
 *
 * @author ssledz
 */
public class AntTaskExecutor {

    private File buildFile;
    private Project project;

    public AntTaskExecutor(File buildFile) {
        this.buildFile = buildFile;
        init();
    }

    public void addBuildListener(BuildListener l) {
        project.addBuildListener(l);
    }

    private void init() {

        project = new Project();
        project.setUserProperty("ant.file", buildFile.getAbsolutePath());
        project.init();
        ProjectHelper helper = ProjectHelper.getProjectHelper();
        project.addReference("ant.projectHelper", helper);
        helper.parse(project, buildFile);
    }

    public void execute(String target) {

        project.executeTarget(target);

    }
}
