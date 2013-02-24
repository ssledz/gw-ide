package pl.softech.gw.pmodule;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.tmatesoft.svn.core.SVNException;
import pl.softech.gw.Utils;
import pl.softech.gw.ant.AntTaskExecutorFactory;
import pl.softech.gw.ant.IAntTaskExecutor;
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
    private final String buildXmlPath;
    private ProjectModule parent;
    private List<Runnable> tasks;
    private SvnTool svnTool;
    private ResourceDownloader downloader;
    private Unzip unzipTool;
    private AntTaskExecutorFactory antTaskExecutorFactory;

    public ProjectModule(File projectDir, String moduleName, String svnPath, String moduleDownloadUrl, String svnCheckoutPath, String buildXmlPath) {
        this.projectDir = projectDir;
        this.moduleName = moduleName;
        this.svnPath = svnPath;
        this.moduleDownloadUrl = moduleDownloadUrl;
        this.tasks = new LinkedList<Runnable>();
        this.svnCheckoutPath = svnCheckoutPath;
        this.buildXmlPath = buildXmlPath;
    }

    void setParent(ProjectModule parent) {
        this.parent = parent;
    }

    void setUnzipTool(Unzip unzipTool) {
        this.unzipTool = unzipTool;
    }

    void setAntTaskExecutorFactory(AntTaskExecutorFactory antTaskExecutorFactory) {
        this.antTaskExecutorFactory = antTaskExecutorFactory;
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

    Runnable createAntTask(final String target) {

        return new Runnable() {
            @Override
            public void run() {
                IAntTaskExecutor a = antTaskExecutorFactory.create(new File(getModuleDir(), buildXmlPath));
                a.execute(target);
            }
        };
    }

    Runnable createDownloadModuleTask() {

        return new Runnable() {
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
        };
    }

    private String getZipFileName() {
        return moduleName + ".zip";
    }

    private File getModuleDir() {
        return new File(projectDir, moduleName);
    }

    Runnable createUnzipTask() {

        return new Runnable() {
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
        };

    }

    Runnable createCheckoutTask() {

        return new Runnable() {
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
        };

    }

    Runnable createUpdateTask() {

        return new Runnable() {
            @Override
            public void run() {
                File svnCoDir = createCheckoutDir(projectDir, moduleName, svnCheckoutPath);
                if (!svnCoDir.exists()) {
                    throw new RuntimeException(String.format("Update dir %s doesn't exist", svnCoDir.getAbsolutePath()));
                }
                try {
                    svnTool.update(svnCoDir);
                } catch (SVNException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };

    }

    public void execute() throws Exception {

        if (!projectDir.exists()) {
            projectDir.mkdirs();
        }

        if (parent != null) {
            parent.execute();
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
