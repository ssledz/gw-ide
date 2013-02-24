package pl.softech.gw;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.tmatesoft.svn.core.SVNException;
import pl.softech.gw.download.ResourceDownloader;
import pl.softech.gw.svn.SvnTool;
import pl.softech.gw.zip.Unzip;

/**
 *
 * @author ssledz
 */
public class ProjectModule {

    private final File projectDir;
    private final String moduleName;
    private final String svnPath;
    private final String svnCheckoutPath;
    private final String moduleDownloadUrl;
    private ProjectModule parent;
    private List<Runnable> tasks;
    private SvnTool svnTool;
    private ResourceDownloader downloader;
    private Unzip unzipTool;

    public ProjectModule(File projectDir, String moduleName, String svnPath, String moduleDownloadUrl, String svnCheckoutPath) {
        this.projectDir = projectDir;
        this.moduleName = moduleName;
        this.svnPath = svnPath;
        this.moduleDownloadUrl = moduleDownloadUrl;
        this.tasks = new LinkedList<Runnable>();
        this.svnCheckoutPath = svnCheckoutPath;
    }

    void setParent(ProjectModule parent) {
        this.parent = parent;
    }

    void setUnzipTool(Unzip unzipTool) {
        this.unzipTool = unzipTool;
    }

    void setSvnTool(SvnTool svnTool) {
        this.svnTool = svnTool;
    }

    void setDownloader(ResourceDownloader downloader) {
        this.downloader = downloader;
    }

    public void addTask(Runnable task) {
        tasks.add(task);
    }

    void createDownloadModuleTask() {

        tasks.add(new Runnable() {
            @Override
            public void run() {
                try {
                    downloader.download(moduleDownloadUrl, projectDir, getZipFileName());
                    File moduleDir = getModuleDir();
                    if (moduleDir.exists()) {
                        moduleDir.renameTo(new File(projectDir, String.format("%s-%s", moduleName, new Date().getTime())));
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
    }

    private String getZipFileName() {
        return moduleName + ".zip";
    }

    private File getModuleDir() {
        return new File(projectDir, moduleName);
    }

    void createUnzipTask() {

        tasks.add(new Runnable() {
            @Override
            public void run() {
                try {
                    File moduleDir = getModuleDir();
                    moduleDir.mkdir();

                    unzipTool.unzipFile(new File(projectDir, getZipFileName()), moduleDir);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

    }

    void createCheckoutTask() {

        tasks.add(new Runnable() {
            @Override
            public void run() {
                try {
                    File svnCoDir = createCheckoutDir(projectDir, moduleName, svnCheckoutPath);
                    System.out.println(svnCoDir.getAbsolutePath());
                    System.out.println(svnPath);
                    if (svnCoDir.exists()) {
                        Utils.deleteRecursive(svnCoDir);
                    }

                    svnTool.checkout(svnPath, svnCoDir);
                } catch (SVNException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

    }

    public void create() throws Exception {

        if (!projectDir.exists()) {
            projectDir.mkdirs();
        }

        if (parent != null) {
            parent.create();
        }

        for (Runnable r : tasks) {
            r.run();
        }
        
    }

    @Override
    public String toString() {
        return "ProjectModule{" + "moduleName=" + moduleName + ", parent=" + parent + '}';
    }

    public static File createCheckoutDir(File projectDir, String moduleName, String checkoutPath) {
        return new File(projectDir, moduleName + checkoutPath);
    }
}
