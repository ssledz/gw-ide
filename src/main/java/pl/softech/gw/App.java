package pl.softech.gw;

import pl.softech.gw.zip.Unzip;
import java.io.File;
import pl.softech.gw.download.BytesReceivedEvent;
import pl.softech.gw.download.IDownloadActionListener;
import pl.softech.gw.download.IDownloadEvent;
import pl.softech.gw.download.ResourceDownloader;
import pl.softech.gw.svn.ISvnActionListener;
import pl.softech.gw.svn.ISvnEvent;
import pl.softech.gw.svn.SvnTool;
import pl.softech.gw.svn.SvnAddEvent;
import pl.softech.gw.svn.SvnUpdateCompletedEvent;
import pl.softech.gw.svn.SvnUpdateEvent;
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

    private static void init() {

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

    private static void app1() throws Exception {

        init();

        File projectDir = new File("C:\\Users\\ssledz\\development-workspace\\tmp\\pc-project");

        ProjectModuleBuilder builder = new ProjectModuleBuilder();
        builder.setDownloader(downloader);
        builder.setSvnTool(svn);
        builder.setUnzipTool(unzip);
        builder.setProjectDir(projectDir);
        builder.setSvnCheckoutPath("\\modules\\configuration");
        builder.setCreateCheckoutTask(true);
//        builder.setCreateDownloadTask(true);
//        builder.setCreateUnzipTask(true);

        builder.setModuleName("PolicyCenter");
        builder.setModuleDownloadUrl("http://localhost:8080/pc-repository/PolicyCenter7.0.6.zip");
        builder.setSvnPath("file:///C:/Users/ssledz/svn-repository/pc/trunk/modules/configuration");

        builder = builder.createParent();
        builder.setModuleName("BillingCenter");
        builder.setModuleDownloadUrl("http://localhost:8080/pc-repository/BillingCenter7.0.2_patch_1_2.zip");
        builder.setSvnPath("file:///C:/Users/ssledz/svn-repository/bc/trunk/modules/configuration");

        builder = builder.createParent();
        builder.setModuleName("ContactManager");
        builder.setModuleDownloadUrl("http://localhost:8080/pc-repository/ContactManager7.0.3.zip");
        builder.setSvnPath("file:///C:/Users/ssledz/svn-repository/cm/trunk/modules/configuration");

        ProjectModule pc = builder.build();
        System.out.println(pc.toString());
        pc.create();
    }

    public static void main(String[] args) throws Exception {
        app1();
    }
}
