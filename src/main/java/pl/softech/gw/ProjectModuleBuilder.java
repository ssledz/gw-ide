/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw;

import java.io.File;
import pl.softech.gw.download.ResourceDownloader;
import pl.softech.gw.svn.SvnTool;
import pl.softech.gw.zip.Unzip;

/**
 *
 * @author ssledz
 */
public class ProjectModuleBuilder {

    private SvnTool svnTool;
    private ResourceDownloader downloader;
    private Unzip unzipTool;
    private File projectDir;
    private String moduleName;
    private String svnPath;
    private String svnCheckoutPath;
    private String moduleDownloadUrl;
    private ProjectModuleBuilder child;
    ProjectModule projectModule;
    
    private boolean isCreateCheckoutTask;
    private boolean isCreateDownloadTask;
    private boolean isCreateUnzipTask;

    public ProjectModuleBuilder() {
    }

    private ProjectModuleBuilder(ProjectModuleBuilder child) {
        this.child = child;
        this.svnTool = child.svnTool;
        this.downloader = child.downloader;
        this.unzipTool = child.unzipTool;
        this.projectDir = child.projectDir;
        this.svnCheckoutPath = child.svnCheckoutPath;
        this.isCreateCheckoutTask = child.isCreateCheckoutTask;
        this.isCreateDownloadTask = child.isCreateDownloadTask;
        this.isCreateUnzipTask = child.isCreateUnzipTask;
    }

    public ProjectModuleBuilder setSvnCheckoutPath(String svnCheckoutPath) {
        this.svnCheckoutPath = svnCheckoutPath;
        return this;
    }

    public ProjectModuleBuilder setCreateCheckoutTask(boolean isCreateCheckoutTask) {
        this.isCreateCheckoutTask = isCreateCheckoutTask;
        return this;
    }

    public ProjectModuleBuilder setCreateDownloadTask(boolean isCreateDownloadTask) {
        this.isCreateDownloadTask = isCreateDownloadTask;
        return this;
    }

    public ProjectModuleBuilder setCreateUnzipTask(boolean isCreateUnzipTask) {
        this.isCreateUnzipTask = isCreateUnzipTask;
        return this;
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

    public ProjectModule internalBuild() {

        projectModule = new ProjectModule(projectDir, moduleName, svnPath, moduleDownloadUrl, svnCheckoutPath);
        projectModule.setDownloader(downloader);
        projectModule.setSvnTool(svnTool);
        projectModule.setUnzipTool(unzipTool);
        
        if(isCreateDownloadTask) {
            projectModule.createCheckoutTask();
        }
        
        if(isCreateUnzipTask) {
            projectModule.createUnzipTask();
        }
        
        if(isCreateCheckoutTask) {
            projectModule.createCheckoutTask();
        }

        if (child != null) {
            ProjectModule childProject = child.internalBuild();
            childProject.setParent(projectModule);
        }

        return projectModule;

    }

    public ProjectModule build() {

        internalBuild();

        ProjectModuleBuilder it = this;

        for (; it.child != null; it = it.child) { }
        
        return it.projectModule;

    }
}
