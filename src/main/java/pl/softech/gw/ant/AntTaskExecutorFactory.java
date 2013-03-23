/*
 * Copyright 2013 Sławomir Śledź <slawomir.sledz@sof-tech.pl>.
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
package pl.softech.gw.ant;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
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
    
    public void removeBuildListener(BuildListener l) {
        listeners.remove(l);
    }

    public AntTaskExecutorIml create(File buildFile) {
        AntTaskExecutorIml impl = new AntTaskExecutorIml(buildFile);
        for (BuildListener l : listeners) {
            impl.addBuildListener(l);
        }
        return impl;


    }
}
