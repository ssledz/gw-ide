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
package pl.softech.gw;

import com.google.gson.Gson;
import java.io.File;
import org.apache.tools.ant.BuildEvent;
import pl.softech.gw.ant.AntTaskExecutorFactory;
import pl.softech.gw.ant.BuildListenerAdapter;
import pl.softech.gw.download.BytesReceivedEvent;
import pl.softech.gw.download.IDownloadActionListener;
import pl.softech.gw.download.IDownloadEvent;
import pl.softech.gw.download.ResourceDownloader;
import pl.softech.gw.json.GsonFactory;
import pl.softech.gw.pmodule.ProjectModule;
import pl.softech.gw.svn.ISvnActionListener;
import pl.softech.gw.svn.ISvnEvent;
import pl.softech.gw.svn.SvnAddEvent;
import pl.softech.gw.svn.SvnTool;
import pl.softech.gw.svn.SvnUpdateCompletedEvent;
import pl.softech.gw.svn.SvnUpdateEvent;
import pl.softech.gw.task.ChainTask;
import pl.softech.gw.task.TaskFactory;
import pl.softech.gw.zip.IZipActionListener;
import pl.softech.gw.zip.IZipEvent;
import pl.softech.gw.zip.Unzip;
import pl.softech.gw.zip.UnzipPathEvent;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
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
        module.setSvnCheckoutPath("${ContactManager.svnCheckoutPath}");
        module.setModuleDownloadUrl("${ContactManager.moduleDownloadUrl}");
        module.setSvnPath("${ContactManager.svnPath}");
        module.setBuildXmlPath("${ContactManager.buildXmlPath}");
        module.addTask(new ChainTask(taskFactory.createDownloadModuleTask(),
                new ChainTask(taskFactory.createUnzipTask(), taskFactory.createCheckoutTask(), null), null));

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("BillingCenter");

        module.setSvnCheckoutPath("${BillingCenter.svnCheckoutPath}");
        module.setModuleDownloadUrl("${BillingCenter.moduleDownloadUrl}");
        module.setSvnPath("${BillingCenter.svnPath}");
        module.setBuildXmlPath("${BillingCenter.buildXmlPath}");

        module.addTask(new ChainTask(taskFactory.createDownloadModuleTask(),
                new ChainTask(taskFactory.createUnzipTask(), taskFactory.createCheckoutTask(), null), null));

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("PolicyCenter");

        module.setSvnCheckoutPath("${PolicyCenter.svnCheckoutPath}");
        module.setModuleDownloadUrl("${PolicyCenter.moduleDownloadUrl}");
        module.setSvnPath("${PolicyCenter.svnPath}");
        module.setBuildXmlPath("${PolicyCenter.buildXmlPath}");

        module.addTask(new ChainTask(taskFactory.createDownloadModuleTask(),
                new ChainTask(taskFactory.createUnzipTask(), taskFactory.createCheckoutTask(), null), null));


        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("DSPCommon");

        module.setSvnCheckoutPath("${DSPCommon.svnCheckoutPath}");
        module.setSvnPath("${DSPCommon.svnPath}");
        module.setBuildXmlPath("${DSPCommon.buildXmlPath}");

        module.addTask(
                new ChainTask(taskFactory.createCheckoutTask(),
                new ChainTask(taskFactory.createAntTask("push-common-into-products"), taskFactory.createAntTask("init-products-data"), null),
                null));


        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("Tools");

        module.setSvnCheckoutPath("${Tools.svnCheckoutPath}");
        module.setSvnPath("${Tools.svnPath}");
        module.setBuildXmlPath("${Tools.buildXmlPath}");

        module.addTask(taskFactory.createCheckoutTask());


        GsonFactory gsonFactory = new GsonFactory(taskFactory);
        Gson gson = gsonFactory.create();

        String ret = gson.toJson(module);
        ProjectModule pm = gson.fromJson(ret, ProjectModule.class);
        System.out.println(gson.toJson(pm));
        File placeHoldersFileName = new File(App.class.getClassLoader().getResource("place-holders.prop").getFile());

        System.out.println(new PlaceHolderFilter(placeHoldersFileName).read().filter(gson.toJson(pm)));

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

        module.setSvnCheckoutPath("${ContactManager.svnCheckoutPath}");
        module.setModuleDownloadUrl("${ContactManager.moduleDownloadUrl}");
        module.setSvnPath("${ContactManager.svnPath}");
        module.setBuildXmlPath("${ContactManager.buildXmlPath}");

        module.addTask(taskFactory.createUpdateTask());

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("BillingCenter");


        module.setSvnCheckoutPath("${BillingCenter.svnCheckoutPath}");
        module.setModuleDownloadUrl("${BillingCenter.moduleDownloadUrl}");
        module.setSvnPath("${BillingCenter.svnPath}");
        module.setBuildXmlPath("${BillingCenter.buildXmlPath}");

        module.addTask(taskFactory.createUpdateTask());

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("PolicyCenter");

        module.setSvnCheckoutPath("${PolicyCenter.svnCheckoutPath}");
        module.setModuleDownloadUrl("${PolicyCenter.moduleDownloadUrl}");
        module.setSvnPath("${PolicyCenter.svnPath}");
        module.setBuildXmlPath("${PolicyCenter.buildXmlPath}");

        module.addTask(taskFactory.createUpdateTask());

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("Tools");

        module.setSvnCheckoutPath("${Tools.svnCheckoutPath}");
        module.setSvnPath("${Tools.svnPath}");
        module.setBuildXmlPath("${Tools.buildXmlPath}");

        module.addTask(taskFactory.createUpdateTask());

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("DSPCommon");

        module.setSvnCheckoutPath("${DSPCommon.svnCheckoutPath}");
        module.setSvnPath("${DSPCommon.svnPath}");
        module.setBuildXmlPath("${DSPCommon.buildXmlPath}");

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

        dspCommon.setSvnCheckoutPath("${DSPCommon.svnCheckoutPath}");
        dspCommon.setSvnPath("${DSPCommon.svnPath}");
        dspCommon.setBuildXmlPath("${DSPCommon.buildXmlPath}");

        ProjectModule module = new ProjectModule();
        ProjectModule parent = module;

        module.setProjectDir(projectDir);
        module.setModuleName("ContactManager");
        module.setSvnCheckoutPath("${ContactManager.svnCheckoutPath}");
        module.setModuleDownloadUrl("${ContactManager.moduleDownloadUrl}");
        module.setSvnPath("${ContactManager.svnPath}");
        module.setBuildXmlPath("${ContactManager.buildXmlPath}");

        module.addTask(new ChainTask(taskFactory.createGwModuleStartTask(),
                new ChainTask(taskFactory.createAntTask("dev-dropdb"), taskFactory.createGwModuleStartTask(), null)));

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("BillingCenter");
        module.setSvnCheckoutPath("${BillingCenter.svnCheckoutPath}");
        module.setModuleDownloadUrl("${BillingCenter.moduleDownloadUrl}");
        module.setSvnPath("${BillingCenter.svnPath}");
        module.setBuildXmlPath("${BillingCenter.buildXmlPath}");

        module.addTask(new ChainTask(taskFactory.createGwModuleStartTask(),
                new ChainTask(taskFactory.createAntTask("dev-dropdb"),
                new ChainTask(taskFactory.createGwModuleStartTask(), taskFactory.createExternalAntTask("init-bc-data", dspCommon), null),
                null)));

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("PolicyCenter");
        module.setSvnCheckoutPath("${PolicyCenter.svnCheckoutPath}");
        module.setModuleDownloadUrl("${PolicyCenter.moduleDownloadUrl}");
        module.setSvnPath("${PolicyCenter.svnPath}");
        module.setBuildXmlPath("${PolicyCenter.buildXmlPath}");

        module.addTask(new ChainTask(taskFactory.createGwModuleStartTask(),
                new ChainTask(taskFactory.createAntTask("dev-dropdb"),
                new ChainTask(taskFactory.createGwModuleStartTask(), taskFactory.createExternalAntTask("init-pc-data", dspCommon), null),
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
        dspCommon.setSvnCheckoutPath("${DSPCommon.svnCheckoutPath}");
        dspCommon.setSvnPath("${DSPCommon.svnPath}");
        dspCommon.setBuildXmlPath("${DSPCommon.buildXmlPath}");

        ProjectModule module = new ProjectModule();
        ProjectModule parent = module;

        module.setProjectDir(projectDir);
        module.setModuleName("ContactManager");
        module.setSvnCheckoutPath("${ContactManager.svnCheckoutPath}");
        module.setModuleDownloadUrl("${ContactManager.moduleDownloadUrl}");
        module.setSvnPath("${ContactManager.svnPath}");
        module.setBuildXmlPath("${ContactManager.buildXmlPath}");

        module.addTask(new ChainTask(taskFactory.createGwModuleStartTask(),
                new ChainTask(taskFactory.createAntTask("dev-dropdb"), taskFactory.createGwModuleStartTask(), null)));

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("BillingCenter");
        module.setSvnCheckoutPath("${BillingCenter.svnCheckoutPath}");
        module.setModuleDownloadUrl("${BillingCenter.moduleDownloadUrl}");
        module.setSvnPath("${BillingCenter.svnPath}");
        module.setBuildXmlPath("${BillingCenter.buildXmlPath}");

        module.addTask(new ChainTask(taskFactory.createGwModuleStartTask(),
                new ChainTask(taskFactory.createAntTask("dev-dropdb"),
                new ChainTask(taskFactory.createGwModuleStartTask(), taskFactory.createExternalAntTask("init-bc-data", dspCommon), null),
                null)));

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("PolicyCenter");
        module.setSvnCheckoutPath("${PolicyCenter.svnCheckoutPath}");
        module.setModuleDownloadUrl("${PolicyCenter.moduleDownloadUrl}");
        module.setSvnPath("${PolicyCenter.svnPath}");
        module.setBuildXmlPath("${PolicyCenter.buildXmlPath}");

        module.addTask(new ChainTask(taskFactory.createGwModuleStartTask(), taskFactory.createAntTask("studio-debug-socket"),
                new ChainTask(taskFactory.createAntTask("dev-dropdb"),
                new ChainTask(taskFactory.createGwModuleStartTask(),
                new ChainTask(taskFactory.createExternalAntTask("init-pc-data", dspCommon), taskFactory.createAntTask("studio-debug-socket"), null),
                null),
                null)));


        GsonFactory gsonFactory = new GsonFactory(taskFactory);
        Gson gson = gsonFactory.create();

        String ret = gson.toJson(module);
        ProjectModule pm = gson.fromJson(ret, ProjectModule.class);
        System.out.println(gson.toJson(pm));

        pm.setProjectDir(projectDir);

    }

    private static void dropAndRunProjectJob() throws Exception {

        TaskFactory taskFactory = new TaskFactory(svn, downloader, unzip, antTaskExecutorFactory);

        File projectDir = new File("C:\\Users\\ssledz\\Desktop\\tmp\\pc-project6");

        ProjectModule dspCommon = new ProjectModule();
        dspCommon.setProjectDir(projectDir);
        dspCommon.setModuleName("DSPCommon");
        dspCommon.setSvnCheckoutPath("${DSPCommon.svnCheckoutPath}");
        dspCommon.setSvnPath("${DSPCommon.svnPath}");
        dspCommon.setBuildXmlPath("${DSPCommon.buildXmlPath}");


        ProjectModule module = new ProjectModule();
        ProjectModule parent = module;

        module.setProjectDir(projectDir);
        module.setModuleName("ContactManager");
        module.setSvnCheckoutPath("${ContactManager.svnCheckoutPath}");
        module.setModuleDownloadUrl("${ContactManager.moduleDownloadUrl}");
        module.setSvnPath("${ContactManager.svnPath}");
        module.setBuildXmlPath("${ContactManager.buildXmlPath}");

        module.addTask(new ChainTask(taskFactory.createAntTask("dev-dropdb"), taskFactory.createGwModuleStartTask(), null));

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("BillingCenter");
        module.setSvnCheckoutPath("${BillingCenter.svnCheckoutPath}");
        module.setModuleDownloadUrl("${BillingCenter.moduleDownloadUrl}");
        module.setSvnPath("${BillingCenter.svnPath}");
        module.setBuildXmlPath("${BillingCenter.buildXmlPath}");

        module.addTask(new ChainTask(taskFactory.createAntTask("dev-dropdb"),
                new ChainTask(taskFactory.createGwModuleStartTask(), taskFactory.createExternalAntTask("init-bc-data", dspCommon), null), null));

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("PolicyCenter");
        module.setSvnCheckoutPath("${PolicyCenter.svnCheckoutPath}");
        module.setModuleDownloadUrl("${PolicyCenter.moduleDownloadUrl}");
        module.setSvnPath("${PolicyCenter.svnPath}");
        module.setBuildXmlPath("${PolicyCenter.buildXmlPath}");

        module.addTask(new ChainTask(taskFactory.createAntTask("dev-dropdb"),
                new ChainTask(taskFactory.createGwModuleStartTask(), taskFactory.createExternalAntTask("init-pc-data", dspCommon), null), null));

        GsonFactory gsonFactory = new GsonFactory(taskFactory);
        Gson gson = gsonFactory.create();

        String ret = gson.toJson(module);
        ProjectModule pm = gson.fromJson(ret, ProjectModule.class);
        System.out.println(gson.toJson(pm));

        pm.setProjectDir(projectDir);

    }

    private static void dropAndRunProjectDevJob() throws Exception {

        TaskFactory taskFactory = new TaskFactory(svn, downloader, unzip, antTaskExecutorFactory);

        File projectDir = new File("C:\\Users\\ssledz\\Desktop\\tmp\\pc-project6");

        ProjectModule dspCommon = new ProjectModule();
        dspCommon.setProjectDir(projectDir);
        dspCommon.setModuleName("DSPCommon");
        dspCommon.setSvnCheckoutPath("${DSPCommon.svnCheckoutPath}");
        dspCommon.setSvnPath("${DSPCommon.svnPath}");
        dspCommon.setBuildXmlPath("${DSPCommon.buildXmlPath}");


        ProjectModule module = new ProjectModule();
        ProjectModule parent = module;

        module.setProjectDir(projectDir);
        module.setModuleName("ContactManager");
        module.setSvnCheckoutPath("${ContactManager.svnCheckoutPath}");
        module.setModuleDownloadUrl("${ContactManager.moduleDownloadUrl}");
        module.setSvnPath("${ContactManager.svnPath}");
        module.setBuildXmlPath("${ContactManager.buildXmlPath}");

        module.addTask(new ChainTask(taskFactory.createAntTask("dev-dropdb"), taskFactory.createGwModuleStartTask(), null));

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("BillingCenter");
        module.setSvnCheckoutPath("${BillingCenter.svnCheckoutPath}");
        module.setModuleDownloadUrl("${BillingCenter.moduleDownloadUrl}");
        module.setSvnPath("${BillingCenter.svnPath}");
        module.setBuildXmlPath("${BillingCenter.buildXmlPath}");

        module.addTask(new ChainTask(taskFactory.createAntTask("dev-dropdb"),
                new ChainTask(taskFactory.createGwModuleStartTask(), taskFactory.createExternalAntTask("init-bc-data", dspCommon), null), null));

        module = new ProjectModule();
        module.setParent(parent);
        parent = module;
        module.setProjectDir(projectDir);
        module.setModuleName("PolicyCenter");
        module.setSvnCheckoutPath("${PolicyCenter.svnCheckoutPath}");
        module.setModuleDownloadUrl("${PolicyCenter.moduleDownloadUrl}");
        module.setSvnPath("${PolicyCenter.svnPath}");
        module.setBuildXmlPath("${PolicyCenter.buildXmlPath}");

        module.addTask(new ChainTask(taskFactory.createAntTask("dev-dropdb"),
                new ChainTask(taskFactory.createGwModuleStartTask(),
                new ChainTask(taskFactory.createExternalAntTask("init-pc-data", dspCommon), taskFactory.createAntTask("studio-debug-socket"), null), null), null));

        GsonFactory gsonFactory = new GsonFactory(taskFactory);
        Gson gson = gsonFactory.create();

        String ret = gson.toJson(module);
        ProjectModule pm = gson.fromJson(ret, ProjectModule.class);
        System.out.println(gson.toJson(pm));

        pm.setProjectDir(projectDir);

    }

    public static void main(String[] args) throws Exception {
        createProjectJob();
//        updateProjectJob();
//        dropAndRunProjectJob();
//        runProjectJob();
//        runProjectDevJob();
//        dropAndRunProjectDevJob();
    }
}
