/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.pmodule;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import pl.softech.gw.ant.AntTaskExecutorFactory;
import pl.softech.gw.download.ResourceDownloader;
import pl.softech.gw.svn.SvnTool;
import pl.softech.gw.task.ITask;
import pl.softech.gw.zip.Unzip;

/**
 *
 * @author ssledz
 */
public class ProjectModuleBuilder {

    private SvnTool svnTool;
    private ResourceDownloader downloader;
    private Unzip unzipTool;
    private AntTaskExecutorFactory antTaskExecutorFactory;
    private File projectDir;
    private String moduleName;
    private String svnPath;
    private String svnCheckoutPath;
    private String moduleDownloadUrl;
    private String buildXmlPath;
    private ProjectModuleBuilder child;
    ProjectModule projectModule;
    private List<ITask> tasks;

    public ProjectModuleBuilder() {
        tasks = new LinkedList<ITask>();
    }

    private ProjectModuleBuilder(ProjectModuleBuilder child) {
        this();
        this.child = child;
        this.svnTool = child.svnTool;
        this.downloader = child.downloader;
        this.unzipTool = child.unzipTool;
        this.projectDir = child.projectDir;
        this.svnCheckoutPath = child.svnCheckoutPath;
        this.antTaskExecutorFactory = child.antTaskExecutorFactory;
    }

    public ProjectModuleBuilder setAntTaskExecutorFactory(AntTaskExecutorFactory antTaskExecutorFactory) {
        this.antTaskExecutorFactory = antTaskExecutorFactory;
        return this;
    }

    public ProjectModuleBuilder setSvnCheckoutPath(String svnCheckoutPath) {
        this.svnCheckoutPath = svnCheckoutPath;
        return this;
    }

    public void setBuildXmlPath(String buildXmlPath) {
        this.buildXmlPath = buildXmlPath;
    }

    public ProjectModuleBuilder setSvnTool(SvnTool svnTool) {
        this.svnTool = svnTool;
        return this;
    }

    public ProjectModuleBuilder setDownloader(ResourceDownloader downloader) {
        this.downloader = downloader;
        return this;
    }

    public ProjectModuleBuilder setUnzipTool(Unzip unzipTool) {
        this.unzipTool = unzipTool;
        return this;
    }

    public ProjectModuleBuilder setProjectDir(File projectDir) {
        this.projectDir = projectDir;
        return this;
    }

    public ProjectModuleBuilder setModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    public ProjectModuleBuilder setSvnPath(String svnPath) {
        this.svnPath = svnPath;
        return this;
    }

    public ProjectModuleBuilder setModuleDownloadUrl(String moduleDownloadUrl) {
        this.moduleDownloadUrl = moduleDownloadUrl;
        return this;
    }

    public ProjectModuleBuilder createParent() {
        return new ProjectModuleBuilder(this);
    }

    public void addTask(ITask task) {
        tasks.add(task);
    }

//    public ProjectModule internalBuild() {
//
//        projectModule = new ProjectModule(projectDir, moduleName, svnPath, moduleDownloadUrl, svnCheckoutPath, buildXmlPath);
//
//        projectModule.addAllTask(tasks);
//
//        if (child != null) {
//            ProjectModule childProject = child.internalBuild();
//            childProject.setParent(projectModule);
//        }
//
//        return projectModule;
//
//    }

    public ProjectModule build() {

//        internalBuild();

        ProjectModuleBuilder it = this;

        for (; it.child != null; it = it.child) {
        }

        return it.projectModule;

    }
}
