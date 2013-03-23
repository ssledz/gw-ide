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
package pl.softech.gw.pmodule;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import pl.softech.gw.json.JsonExclude;
import pl.softech.gw.task.Context;
import pl.softech.gw.task.ITask;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class ProjectModule {

    @JsonExclude
    private File projectDir;
    private String moduleName;
    private String svnPath;
    private String svnCheckoutPath;
    private String moduleDownloadUrl;
    private String buildXmlPath;
    private ProjectModule parent;
    private List<ITask> tasks;

    public ProjectModule() {
        tasks = new LinkedList<ITask>();
    }
    
    public void setProjectDir(File projectDir) {
        this.projectDir = projectDir;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setSvnPath(String svnPath) {
        this.svnPath = svnPath;
    }

    public void setSvnCheckoutPath(String svnCheckoutPath) {
        this.svnCheckoutPath = svnCheckoutPath;
    }

    public void setModuleDownloadUrl(String moduleDownloadUrl) {
        this.moduleDownloadUrl = moduleDownloadUrl;
    }

    public void setBuildXmlPath(String buildXmlPath) {
        this.buildXmlPath = buildXmlPath;
    }

    public File getProjectDir() {
        return projectDir;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getSvnPath() {
        return svnPath;
    }

    public String getSvnCheckoutPath() {
        return svnCheckoutPath;
    }

    public String getBuildXmlPath() {
        return buildXmlPath;
    }

    public String getModuleDownloadUrl() {
        return moduleDownloadUrl;
    }

    public void setParent(ProjectModule parent) {
        this.parent = parent;
    }

    public void addTask(ITask task) {
        tasks.add(task);
    }

    public void addAllTask(List<ITask> tasks) {
        this.tasks.addAll(tasks);
    }

    public void execute() throws Exception {

        if (!projectDir.exists()) {
            projectDir.mkdirs();
        }

        if (parent != null) {
            if (parent.projectDir == null) {
                parent.setProjectDir(projectDir);
            }
            parent.execute();
        }

        for (ITask task : tasks) {
            task.execute(new Context(this));

        }

    }
}
