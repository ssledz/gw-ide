/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import pl.softech.gw.download.ResourceDownloader;
import pl.softech.gw.svn.SvnTool;
import pl.softech.gw.zip.Unzip;

/**
 *
 * @author ssledz
 */
public class Project {

    private final File projectDir;
    private final String moduleName;
    private final String svnPath;
    private final String moduleDownloadUrl;
    private final Project parent;
    private List<Runnable> tasks;
    private SvnTool svnTool;
    private ResourceDownloader downloader;
    private Unzip unzipTool;

    public Project(File projectDir, String moduleName, String svnPath, String moduleDownloadUrl, Project parent) {
        this.projectDir = projectDir;
        this.moduleName = moduleName;
        this.svnPath = svnPath;
        this.moduleDownloadUrl = moduleDownloadUrl;
        this.parent = parent;
        this.tasks = new LinkedList<Runnable>();
    }

    public void setUnzipTool(Unzip unzipTool) {
        this.unzipTool = unzipTool;
    }

    public void setSvnTool(SvnTool svnTool) {
        this.svnTool = svnTool;
    }

    public void setDownloader(ResourceDownloader downloader) {
        this.downloader = downloader;
    }

    public void addTask(Runnable task) {
        tasks.add(task);
    }

    public void create() throws Exception {

        if (parent != null) {
            parent.create();
        }

        String zipFileName = moduleName + ".zip";
        downloader.download(moduleDownloadUrl, projectDir, zipFileName);
        File moduleDir = new File(projectDir, moduleName);
        if (moduleDir.exists()) {
            moduleDir.renameTo(new File(projectDir, String.format("%s-%s", moduleName, new Date().getTime())));
        }

        moduleDir.mkdir();

        unzipTool.unzipFile(new File(projectDir, zipFileName), moduleDir);

        File svnCoDir = new File(projectDir, String.format("%s\\modules\\configuration", moduleName));
        System.out.println(svnCoDir.getAbsolutePath());
        System.out.println(svnPath);
        if (svnCoDir.exists()) {
            Utils.deleteRecursive(svnCoDir);
        }

        svnTool.checkout(svnPath, svnCoDir);

    }
}
