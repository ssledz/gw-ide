package pl.softech.gw;

import com.google.gson.Gson;
import pl.softech.gw.pmodule.ProjectModule;
import pl.softech.gw.zip.Unzip;
import java.io.File;
import org.apache.tools.ant.BuildEvent;
import pl.softech.gw.ant.AntTaskExecutorFactory;
import pl.softech.gw.ant.BuildListenerAdapter;
import pl.softech.gw.download.BytesReceivedEvent;
import pl.softech.gw.download.IDownloadActionListener;
import pl.softech.gw.download.IDownloadEvent;
import pl.softech.gw.download.ResourceDownloader;
import pl.softech.gw.json.GsonFactory;
import pl.softech.gw.svn.ISvnActionListener;
import pl.softech.gw.svn.ISvnEvent;
import pl.softech.gw.svn.SvnTool;
import pl.softech.gw.svn.SvnAddEvent;
import pl.softech.gw.svn.SvnUpdateCompletedEvent;
import pl.softech.gw.svn.SvnUpdateEvent;
import pl.softech.gw.task.ChainTask;
import pl.softech.gw.task.TaskFactory;
import pl.softech.gw.zip.IZipActionListener;
import pl.softech.gw.zip.IZipEvent;
import pl.softech.gw.zip.UnzipPathEvent;

/**
 * Hello world!
 *
 */
public class App {

    private static SvnTool svn;
    private static ResourceDownloader downloader;
    private static Unzip unzip;
    private static AntTaskExecutorFactory antTaskExecutorFactory;

    private static void init() {

        antTaskExecutorFactory = new AntTaskExecutorFactory();
        antTaskExecutorFactory.addBuildListener(new BuildListenerAdapter() {
            @Override
            public void messageLogged(BuildEvent event) {
                if (event.getTarget() != null) {
                    System.out.println("Ant Target: " + event.getTarget().getName());
                }
                System.out.println("Ant Message: " + event.getMessage());
            }
        });

        svn = new SvnTool();
        svn.addSvnActionListener(new ISvnActionListener() {
            @Override
            public void actionPerformed(ISvnEvent event) {
                if (event instanceof SvnUpdateEvent) {

                    System.out.println("Updating: " + ((SvnUpdateEvent) event).getFile().getAbsoluteFile());
                }

                if (event instanceof SvnAddEvent) {

                    System.out.println("Adding: " + ((SvnAddEvent) event).getFile().getAbsoluteFile());
                }

                if (event instanceof SvnUpdateCompletedEvent) {
                    System.out.println("Svn task comleted");
                }
            }
        });

        downloader = new ResourceDownloader();
        downloader.addDownloadActionListener(new IDownloadActionListener() {
            double cnt = 0;

            @Override
            public void actionPerformed(IDownloadEvent event) {

                if (event instanceof BytesReceivedEvent) {
                    BytesReceivedEvent e = (BytesReceivedEvent) event;
                    cnt += e.getReceived();
                    System.out.println((cnt / e.getAll() * 100.0) + "% -> " + e.getFileName());
                }

            }
        });

        unzip = new Unzip();
        unzip.addZipActionListener(new IZipActionListener() {
            @Override
            public void actionPerformed(IZipEvent event) {

                if (event instanceof UnzipPathEvent) {

                    System.out.println("Extracting: " + ((UnzipPathEvent) event).getPath());
                }


            }
        });

    }

    static {
        init();
    }

    private static void createProjectJob() throws Exception {

        TaskFactory taskFactory = new TaskFactory(svn, downloader, unzip, antTaskExecutorFactory);

        File projectDir = new File("C:\\Users\\ssledz\\Desktop\\tmp\\pc-project6");

        ProjectModule module = new ProjectModule();
        ProjectModule parent = module;

        module.setProjectDir(projectDir);
        module.setModuleName("ContactManager");
        module.setSvnCheckoutPath("\\modules\\configuration");
        module.setModuleDownloadUrl("http://localhost:8080/pc-repository/ContactManager7.0.3.zip");
        module.setSvnPath("file:///C:/Users/ssledz/svn-repository/cm/trunk/modules/configuration");
        module.setBuildXmlPath("\\modules\\ant\\build.xml");
        module.addTask(new ChainTask(taskFactory.createDownloadModuleTask(),
                new ChainTask(taskFactory.createUnzipTask(), taskFactory.createCheckoutTask(), null), null));

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("BillingCenter");
        module.setSvnCheckoutPath("\\modules\\configuration");
        module.setModuleDownloadUrl("http://localhost:8080/pc-repository/BillingCenter7.0.2_patch_1_2.zip");
        module.setSvnPath("file:///C:/Users/ssledz/svn-repository/bc/trunk/modules/configuration");
        module.setBuildXmlPath("\\modules\\ant\\build.xml");
        module.addTask(new ChainTask(taskFactory.createDownloadModuleTask(),
                new ChainTask(taskFactory.createUnzipTask(), taskFactory.createCheckoutTask(), null), null));

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("PolicyCenter");
        module.setSvnCheckoutPath("\\modules\\configuration");
        module.setModuleDownloadUrl("http://localhost:8080/pc-repository/PolicyCenter7.0.6.zip");
        module.setSvnPath("file:///C:/Users/ssledz/svn-repository/pc/trunk/modules/configuration");
        module.setBuildXmlPath("\\modules\\ant\\build.xml");
        module.addTask(new ChainTask(taskFactory.createDownloadModuleTask(),
                new ChainTask(taskFactory.createUnzipTask(), taskFactory.createCheckoutTask(), null), null));


        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("DSPCommon");
        module.setSvnCheckoutPath("\\modules");
        module.setSvnPath("file:///C:/Users/ssledz/svn-repository/dspcommon/trunk/modules");
        module.setBuildXmlPath("\\modules\\ant\\build-common.xml");

        module.addTask(
                new ChainTask(taskFactory.createCheckoutTask(),
                new ChainTask(taskFactory.createAntTask("push-common-into-products"), taskFactory.createAntTask("init-products-data"), null),
                null));

        GsonFactory gsonFactory = new GsonFactory(taskFactory);
        Gson gson = gsonFactory.create();

        String ret = gson.toJson(module);
        ProjectModule pm = gson.fromJson(ret, ProjectModule.class);
        System.out.println(gson.toJson(pm));

        pm.setProjectDir(projectDir);
//        pm.execute();

    }

    private static void updateProjectJob() throws Exception {
        TaskFactory taskFactory = new TaskFactory(svn, downloader, unzip, antTaskExecutorFactory);

        File projectDir = new File("C:\\Users\\ssledz\\Desktop\\tmp\\pc-project6");

        ProjectModule module = new ProjectModule();
        ProjectModule parent = module;

        module.setProjectDir(projectDir);
        module.setModuleName("ContactManager");
        module.setSvnCheckoutPath("\\modules\\configuration");
        module.setModuleDownloadUrl("http://localhost:8080/pc-repository/ContactManager7.0.3.zip");
        module.setSvnPath("file:///C:/Users/ssledz/svn-repository/cm/trunk/modules/configuration");
        module.setBuildXmlPath("\\modules\\ant\\build.xml");
        module.addTask(taskFactory.createUpdateTask());

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("BillingCenter");
        module.setSvnCheckoutPath("\\modules\\configuration");
        module.setModuleDownloadUrl("http://localhost:8080/pc-repository/BillingCenter7.0.2_patch_1_2.zip");
        module.setSvnPath("file:///C:/Users/ssledz/svn-repository/bc/trunk/modules/configuration");
        module.setBuildXmlPath("\\modules\\ant\\build.xml");
        module.addTask(taskFactory.createUpdateTask());

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("PolicyCenter");
        module.setSvnCheckoutPath("\\modules\\configuration");
        module.setModuleDownloadUrl("http://localhost:8080/pc-repository/PolicyCenter7.0.6.zip");
        module.setSvnPath("file:///C:/Users/ssledz/svn-repository/pc/trunk/modules/configuration");
        module.setBuildXmlPath("\\modules\\ant\\build.xml");
        module.addTask(taskFactory.createUpdateTask());


        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("DSPCommon");
        module.setSvnCheckoutPath("\\modules");
        module.setSvnPath("file:///C:/Users/ssledz/svn-repository/dspcommon/trunk/modules");
        module.setBuildXmlPath("\\modules\\ant\\build-common.xml");

        module.addTask(new ChainTask(taskFactory.createUpdateTask(), taskFactory.createAntTask("push-common-into-products"), null));


        GsonFactory gsonFactory = new GsonFactory(taskFactory);
        Gson gson = gsonFactory.create();

        String ret = gson.toJson(module);
        ProjectModule pm = gson.fromJson(ret, ProjectModule.class);
        System.out.println(gson.toJson(pm));

        pm.setProjectDir(projectDir);

    }

    private static void runProjectJob() throws Exception {

        TaskFactory taskFactory = new TaskFactory(svn, downloader, unzip, antTaskExecutorFactory);

        File projectDir = new File("C:\\Users\\ssledz\\Desktop\\tmp\\pc-project6");

        ProjectModule dspCommon = new ProjectModule();
        dspCommon.setProjectDir(projectDir);
        dspCommon.setModuleName("DSPCommon");
        dspCommon.setSvnCheckoutPath("\\modules");
        dspCommon.setSvnPath("file:///C:/Users/ssledz/svn-repository/dspcommon/trunk/modules");
        dspCommon.setBuildXmlPath("\\modules\\ant\\build-common.xml");


        ProjectModule module = new ProjectModule();
        ProjectModule parent = module;

        module.setProjectDir(projectDir);
        module.setModuleName("ContactManager");
        module.setSvnCheckoutPath("\\modules\\configuration");
        module.setModuleDownloadUrl("http://localhost:8080/pc-repository/ContactManager7.0.3.zip");
        module.setSvnPath("file:///C:/Users/ssledz/svn-repository/cm/trunk/modules/configuration");
        module.setBuildXmlPath("\\modules\\ant\\build.xml");
        module.addTask(new ChainTask(taskFactory.createGwModuleStartTask(),
                new ChainTask(taskFactory.createAntTask("dev-dropdb"), taskFactory.createGwModuleStartTask(), null)));

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("BillingCenter");
        module.setSvnCheckoutPath("\\modules\\configuration");
        module.setModuleDownloadUrl("http://localhost:8080/pc-repository/BillingCenter7.0.2_patch_1_2.zip");
        module.setSvnPath("file:///C:/Users/ssledz/svn-repository/bc/trunk/modules/configuration");
        module.setBuildXmlPath("\\modules\\ant\\build.xml");

        module.addTask(new ChainTask(taskFactory.createGwModuleStartTask(),
                new ChainTask(taskFactory.createAntTask("dev-dropdb"),
                new ChainTask(taskFactory.createExternalAntTask("init-bc-data", dspCommon), taskFactory.createGwModuleStartTask(), null),
                null)));

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("PolicyCenter");
        module.setSvnCheckoutPath("\\modules\\configuration");
        module.setModuleDownloadUrl("http://localhost:8080/pc-repository/PolicyCenter7.0.6.zip");
        module.setSvnPath("file:///C:/Users/ssledz/svn-repository/pc/trunk/modules/configuration");
        module.setBuildXmlPath("\\modules\\ant\\build.xml");

        module.addTask(new ChainTask(taskFactory.createGwModuleStartTask(),
                new ChainTask(taskFactory.createAntTask("dev-dropdb"),
                new ChainTask(taskFactory.createExternalAntTask("init-pc-data", dspCommon), taskFactory.createGwModuleStartTask(), null),
                null)));


        GsonFactory gsonFactory = new GsonFactory(taskFactory);
        Gson gson = gsonFactory.create();

        String ret = gson.toJson(module);
        ProjectModule pm = gson.fromJson(ret, ProjectModule.class);
        System.out.println(gson.toJson(pm));

        pm.setProjectDir(projectDir);

    }

    private static void runProjectDevJob() throws Exception {

        TaskFactory taskFactory = new TaskFactory(svn, downloader, unzip, antTaskExecutorFactory);

        File projectDir = new File("C:\\Users\\ssledz\\Desktop\\tmp\\pc-project6");

        ProjectModule dspCommon = new ProjectModule();
        dspCommon.setProjectDir(projectDir);
        dspCommon.setModuleName("DSPCommon");
        dspCommon.setSvnCheckoutPath("\\modules");
        dspCommon.setSvnPath("file:///C:/Users/ssledz/svn-repository/dspcommon/trunk/modules");
        dspCommon.setBuildXmlPath("\\modules\\ant\\build-common.xml");


        ProjectModule module = new ProjectModule();
        ProjectModule parent = module;

        module.setProjectDir(projectDir);
        module.setModuleName("ContactManager");
        module.setSvnCheckoutPath("\\modules\\configuration");
        module.setModuleDownloadUrl("http://localhost:8080/pc-repository/ContactManager7.0.3.zip");
        module.setSvnPath("file:///C:/Users/ssledz/svn-repository/cm/trunk/modules/configuration");
        module.setBuildXmlPath("\\modules\\ant\\build.xml");
        module.addTask(new ChainTask(taskFactory.createGwModuleStartTask(),
                new ChainTask(taskFactory.createAntTask("dev-dropdb"), taskFactory.createGwModuleStartTask(), null)));

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("BillingCenter");
        module.setSvnCheckoutPath("\\modules\\configuration");
        module.setModuleDownloadUrl("http://localhost:8080/pc-repository/BillingCenter7.0.2_patch_1_2.zip");
        module.setSvnPath("file:///C:/Users/ssledz/svn-repository/bc/trunk/modules/configuration");
        module.setBuildXmlPath("\\modules\\ant\\build.xml");

        module.addTask(new ChainTask(taskFactory.createGwModuleStartTask(),
                new ChainTask(taskFactory.createAntTask("dev-dropdb"),
                new ChainTask(taskFactory.createExternalAntTask("init-bc-data", dspCommon), taskFactory.createGwModuleStartTask(), null),
                null)));

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("PolicyCenter");
        module.setSvnCheckoutPath("\\modules\\configuration");
        module.setModuleDownloadUrl("http://localhost:8080/pc-repository/PolicyCenter7.0.6.zip");
        module.setSvnPath("file:///C:/Users/ssledz/svn-repository/pc/trunk/modules/configuration");
        module.setBuildXmlPath("\\modules\\ant\\build.xml");

        module.addTask(new ChainTask(taskFactory.createGwModuleStartTask(), taskFactory.createAntTask("studio-debug-socket"),
                new ChainTask(taskFactory.createAntTask("dev-dropdb"),
                new ChainTask(taskFactory.createExternalAntTask("init-pc-data", dspCommon),
                new ChainTask(taskFactory.createGwModuleStartTask(), taskFactory.createAntTask("studio-debug-socket"), null),
                null),
                null)));


        GsonFactory gsonFactory = new GsonFactory(taskFactory);
        Gson gson = gsonFactory.create();

        String ret = gson.toJson(module);
        ProjectModule pm = gson.fromJson(ret, ProjectModule.class);
        System.out.println(gson.toJson(pm));

        pm.setProjectDir(projectDir);

    }

    public static void main(String[] args) throws Exception {
        runProjectDevJob();
    }
}
