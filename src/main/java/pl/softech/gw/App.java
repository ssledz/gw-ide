package pl.softech.gw;

import pl.softech.gw.zip.Unzip;
import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.DefaultLogger;
import pl.softech.gw.ant.AntTaskExecutor;
import pl.softech.gw.ant.BuildListenerAdapter;
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

    private static void app1() throws IOException {

        ResourceDownloader downloader = new ResourceDownloader();
        downloader.addDownloadActionListener(new IDownloadActionListener() {
            int cnt = 0;

            @Override
            public void actionPerformed(IDownloadEvent event) {

                if (event instanceof BytesReceivedEvent) {
                    BytesReceivedEvent e = (BytesReceivedEvent) event;
                    cnt += e.getReceived();
                    System.out.println((cnt * 100 / e.getAll()) + "% -> " + e.getFileName());
                }

            }
        });

        File dir = new File("C:\\Users\\ssledz\\development-workspace\\tmp");

        downloader.download("http://localhost:8080/pc-repository/apache-ant-1.8.4-bin.zip", dir, "apache-ant-1.8.4-bin.zip");

        File file = new File(dir, "apache-ant-1.8.4-bin.zip");
        Unzip unzip = new Unzip();
        unzip.addZipActionListener(new IZipActionListener() {
            @Override
            public void actionPerformed(IZipEvent event) {

                if (event instanceof UnzipPathEvent) {

                    System.out.println("Extracting: " + ((UnzipPathEvent) event).getPath());
                }


            }
        });

        unzip.unzipFile(file, dir);

    }

    private static void app2() throws IOException {
        System.out.println("app2");
        AntTaskExecutor te = new AntTaskExecutor(new File("C:\\Users\\ssledz\\development-workspace\\tmp\\build.xml"));

        DefaultLogger consoleLogger = new DefaultLogger();
        consoleLogger.setErrorPrintStream(System.err);
        consoleLogger.setOutputPrintStream(System.out);
//        consoleLogger.setMessageOutputLevel(Project.MSG_INFO);

//        te.addBuildListener(consoleLogger);
        te.addBuildListener(new BuildListenerAdapter() {
            @Override
            public void messageLogged(BuildEvent event) {
                System.out.println("[" + (event.getTarget() != null ? event.getTarget().getName() : "null") + "] " + event.getMessage());
            }
        });
        te.execute("do1");

    }

    private static void app3() throws Exception {

        File file = new File("C:\\Users\\ssledz\\development-workspace\\tmp\\sample1");
//        SvnHelper.checkout("file:///C:/Users/ssledz/development-workspace/tmp/repository/sample-project", file);
        SvnTool svn = new SvnTool();
        svn.addSvnActionListener(new ISvnActionListener() {
            @Override
            public void actionPerformed(ISvnEvent event) {
                if (event instanceof SvnUpdateEvent) {

                    System.out.println("Updating: " + ((SvnUpdateEvent) event).getFile().getAbsoluteFile());
                }

                if (event instanceof SvnAddEvent) {

                    System.out.println("Adding: " + ((SvnUpdateEvent) event).getFile().getAbsoluteFile());
                }

                if (event instanceof SvnUpdateCompletedEvent) {
                    System.out.println("Svn task comleted");
                }
            }
        });
        svn.update(file);
    }

    private static void app4() throws Exception {
        
        SvnTool svn = new SvnTool();
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
        
        ResourceDownloader downloader = new ResourceDownloader();
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
        
        Unzip unzip = new Unzip();
        unzip.addZipActionListener(new IZipActionListener() {
            @Override
            public void actionPerformed(IZipEvent event) {

                if (event instanceof UnzipPathEvent) {

                    System.out.println("Extracting: " + ((UnzipPathEvent) event).getPath());
                }


            }
        });
        
        Project p = new Project(new File("C:\\Users\\ssledz\\development-workspace\\tmp"), 
                "PolicyCenter", 
                "file:///C:/Users/ssledz/development-workspace/tmp/repository/pc/trunk/modules/configuration", 
                "http://localhost:8080/pc-repository/PolicyCenter.zip", null);
        p.setDownloader(downloader);
        p.setUnzipTool(unzip);
        p.setSvnTool(svn);
        p.create();
    }

    public static void main(String[] args) throws Exception {
        app4();
    }
}
