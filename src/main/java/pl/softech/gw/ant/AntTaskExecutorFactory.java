/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.ant;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/**
 *
 * @author ssledz
 */
public class AntTaskExecutorFactory {

    private class AntTaskExecutorIml implements IAntTaskExecutor {

        private File buildFile;
        private Project project;

        public AntTaskExecutorIml(File buildFile) {
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

        @Override
        public void execute(String target) {
            project.executeTarget(target);
        }
    }
    private List<BuildListener> listeners;

    public AntTaskExecutorFactory() {
        listeners = new LinkedList<BuildListener>();
    }
    
    public void addBuildListener(BuildListener l) {
        listeners.add(l);
    }

    public AntTaskExecutorIml create(File buildFile) {
        AntTaskExecutorIml impl = new AntTaskExecutorIml(buildFile);
        for (BuildListener l : listeners) {
            impl.addBuildListener(l);
        }
        return impl;


    }
}
