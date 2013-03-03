package pl.softech.gw.pmodule;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import pl.softech.gw.json.JsonExclude;
import pl.softech.gw.task.Context;
import pl.softech.gw.task.ITask;
import pl.softech.gw.task.ProcessTaskPanicException;

/**
 *
 * @author ssledz
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
