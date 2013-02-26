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
import pl.softech.gw.json.GsonUtils;
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

//    private static void app1() throws Exception {
//
//        File projectDir = new File("C:\\Users\\ssledz\\development-workspace\\tmp\\pc-project2");
//
//        ProjectModuleBuilder builder = new ProjectModuleBuilder();
//        builder.setDownloader(downloader);
//        builder.setSvnTool(svn);
//        builder.setUnzipTool(unzip);
//        builder.setAntTaskExecutorFactory(antTaskExecutorFactory);
//        builder.setProjectDir(projectDir);
//
//
//        builder.setModuleName("DSPCommon");
//        builder.createCheckoutTask();
//        builder.setSvnCheckoutPath("\\modules");
//        builder.setSvnPath("file:///C:/Users/ssledz/svn-repository/dspcommon/trunk/modules");
//        builder.setBuildXmlPath("\\modules\\ant\\build-common.xml");
//
//        builder = builder.createParent();
//        builder.setModuleName("PolicyCenter");
//        builder.createDownloadTask();
//        builder.createUnzipTask();
//        builder.createCheckoutTask();
//        builder.setSvnCheckoutPath("\\modules\\configuration");
//        builder.setModuleDownloadUrl("http://localhost:8080/pc-repository/PolicyCenter7.0.6.zip");
//        builder.setSvnPath("file:///C:/Users/ssledz/svn-repository/pc/trunk/modules/configuration");
//        builder.setBuildXmlPath("\\modules\\ant\\build.xml");
//
//        builder = builder.createParent();
//        builder.setModuleName("BillingCenter");
//        builder.createDownloadTask();
//        builder.createUnzipTask();
//        builder.createCheckoutTask();
//        builder.setModuleDownloadUrl("http://localhost:8080/pc-repository/BillingCenter7.0.2_patch_1_2.zip");
//        builder.setSvnPath("file:///C:/Users/ssledz/svn-repository/bc/trunk/modules/configuration");
//        builder.setBuildXmlPath("\\modules\\ant\\build.xml");
//
//        builder = builder.createParent();
//        builder.setModuleName("ContactManager");
//        builder.createDownloadTask();
//        builder.createUnzipTask();
//        builder.createCheckoutTask();
//        builder.setModuleDownloadUrl("http://localhost:8080/pc-repository/ContactManager7.0.3.zip");
//        builder.setSvnPath("file:///C:/Users/ssledz/svn-repository/cm/trunk/modules/configuration");
//        builder.setBuildXmlPath("\\modules\\ant\\build.xml");
//
//        ProjectModule pc = builder.build();
//        System.out.println(pc.toString());
//        pc.execute();
//    }
//
//    private static void app2() throws Exception {
//
//        File projectDir = new File("C:\\Users\\ssledz\\development-workspace\\tmp\\pc-project2");
//
//        ProjectModuleBuilder builder = new ProjectModuleBuilder();
//        builder.setDownloader(downloader);
//        builder.setSvnTool(svn);
//        builder.setUnzipTool(unzip);
//        builder.setAntTaskExecutorFactory(antTaskExecutorFactory);
//        builder.setProjectDir(projectDir);
//
//
//        builder.setModuleName("DSPCommon");
//        builder.createUpdateTask();
//        builder.setSvnCheckoutPath("\\modules");
//        builder.setSvnPath("file:///C:/Users/ssledz/svn-repository/dspcommon/trunk/modules");
//        builder.setBuildXmlPath("\\modules\\ant\\build-common.xml");
//        builder.createAntTask("push-common-into-products");
//
//        builder = builder.createParent();
//        builder.setModuleName("PolicyCenter");
//        builder.createUpdateTask();
//        builder.setSvnCheckoutPath("\\modules\\configuration");
//        builder.setModuleDownloadUrl("http://localhost:8080/pc-repository/PolicyCenter7.0.6.zip");
//        builder.setSvnPath("file:///C:/Users/ssledz/svn-repository/pc/trunk/modules/configuration");
//        builder.setBuildXmlPath("\\modules\\ant\\build.xml");
//
//        builder = builder.createParent();
//        builder.setModuleName("BillingCenter");
//        builder.createUpdateTask();
//        builder.setModuleDownloadUrl("http://localhost:8080/pc-repository/BillingCenter7.0.2_patch_1_2.zip");
//        builder.setSvnPath("file:///C:/Users/ssledz/svn-repository/bc/trunk/modules/configuration");
//        builder.setBuildXmlPath("\\modules\\ant\\build.xml");
//
//        builder = builder.createParent();
//        builder.setModuleName("ContactManager");
//        builder.createUpdateTask();
//        builder.setModuleDownloadUrl("http://localhost:8080/pc-repository/ContactManager7.0.3.zip");
//        builder.setSvnPath("file:///C:/Users/ssledz/svn-repository/cm/trunk/modules/configuration");
//        builder.setBuildXmlPath("\\modules\\ant\\build.xml");
//
//        ProjectModule pc = builder.build();
//        System.out.println(pc.toString());
//        pc.execute();
//
//    }
//
//    private static void app3() throws Exception {
//        File projectDir = new File("C:\\Users\\ssledz\\development-workspace\\tmp\\pc-project2");
//
//        ProjectModuleBuilder builder = new ProjectModuleBuilder();
//        builder.setDownloader(downloader);
//        builder.setSvnTool(svn);
//        builder.setUnzipTool(unzip);
//        builder.setAntTaskExecutorFactory(antTaskExecutorFactory);
//        builder.setProjectDir(projectDir);
//
//        builder.setModuleName("DSPCommon");
//        builder.setSvnCheckoutPath("\\modules");
//        builder.setSvnPath("file:///C:/Users/ssledz/svn-repository/dspcommon/trunk/modules");
//        builder.setBuildXmlPath("\\modules\\ant\\build-common.xml");
//        builder.createAntTask("push-common-into-products");
//        
//        ProjectModule pc = builder.build();
//        System.out.println(pc.toString());
//        pc.execute();
//    }
//    
//    private static void app4() throws Exception {
//        
//        InputStream in = App.class.getClassLoader().getResourceAsStream("create-pc-project.descriptor");
//        String conf = Utils.readInputStreamAsString(in, "UTF-8");
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        ProjectConfiguration pmc = gson.fromJson(conf, ProjectConfiguration.class);
//        System.out.println(gson.toJson(pmc));
//        
//    }
//    
//    private static void app5() throws Exception {
//        
//        ProjectModuleFactory projectModuleFactory = new ProjectModuleFactory(svn, downloader, unzip, antTaskExecutorFactory);
//        File projectDir = new File("C:\\Users\\ssledz\\development-workspace\\tmp\\pc-project3");
//        ProjectModule pc = projectModuleFactory.create(projectDir, "create-pc-project.descriptor");
//        System.out.println(pc.toString());
//        pc.execute();
//        
//    }
    private static void app6() throws Exception {

        TaskFactory taskFactory = new TaskFactory(svn, downloader, unzip, antTaskExecutorFactory);
        
        File projectDir = new File("C:\\Users\\ssledz\\development-workspace\\tmp\\pc-project4");

        ProjectModule module = new ProjectModule();
        ProjectModule parent = module;

        module.setProjectDir(projectDir);
        module.setModuleName("DSPCommon");
        module.setSvnCheckoutPath("\\modules");
        module.setSvnPath("file:///C:/Users/ssledz/svn-repository/dspcommon/trunk/modules");
        module.setBuildXmlPath("\\modules\\ant\\build-common.xml");
        module.addTask(new ChainTask(taskFactory.createCheckoutTask(), taskFactory.createAntTask("push-common-into-products"), null));
                
//        module.addTask(taskFactory.createCheckoutTask());
//        module.addTask(taskFactory.createAntTask("push-common-into-products"));

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("PolicyCenter");
        module.setSvnCheckoutPath("\\modules\\configuration");
        module.setModuleDownloadUrl("http://localhost:8080/pc-repository/PolicyCenter7.0.6.zip");
        module.setSvnPath("file:///C:/Users/ssledz/svn-repository/pc/trunk/modules/configuration");
        module.setBuildXmlPath("\\modules\\ant\\build.xml");
//        conf.tasks.add(new ProjectConfiguration.TaskConfiguration("DownloadTask", null));
//        conf.tasks.add(new ProjectConfiguration.TaskConfiguration("UnzipTask", null));
//        conf.tasks.add(new ProjectConfiguration.TaskConfiguration("CheckoutTask", null));

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("BillingCenter");
        module.setSvnCheckoutPath("\\modules\\configuration");
        module.setModuleDownloadUrl("http://localhost:8080/pc-repository/BillingCenter7.0.2_patch_1_2.zip");
        module.setSvnPath("file:///C:/Users/ssledz/svn-repository/bc/trunk/modules/configuration");
        module.setBuildXmlPath("\\modules\\ant\\build.xml");
//        conf.tasks.add(new ProjectConfiguration.TaskConfiguration("DownloadTask", null));
//        conf.tasks.add(new ProjectConfiguration.TaskConfiguration("UnzipTask", null));
//        conf.tasks.add(new ProjectConfiguration.TaskConfiguration("CheckoutTask", null));

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("ContactManager");
        module.setSvnCheckoutPath("\\modules\\configuration");
        module.setModuleDownloadUrl("http://localhost:8080/pc-repository/ContactManager7.0.3.zip");
        module.setSvnPath("file:///C:/Users/ssledz/svn-repository/cm/trunk/modules/configuration");
        module.setBuildXmlPath("\\modules\\ant\\build.xml");
//        conf.tasks.add(new ProjectConfiguration.TaskConfiguration("DownloadTask", null));
//        conf.tasks.add(new ProjectConfiguration.TaskConfiguration("UnzipTask", null));
//        conf.tasks.add(new ProjectConfiguration.TaskConfiguration("CheckoutTask", null));

        GsonFactory gsonFactory = new GsonFactory(taskFactory);
        Gson gson = gsonFactory.create();
        
        String ret = gson.toJson(module);
//        System.out.println(ret);
        ProjectModule pm  =  gson.fromJson(ret, ProjectModule.class);
        System.out.println(gson.toJson(pm));
        
    }

    public static void main(String[] args) throws Exception {
        app6();
    }
}
