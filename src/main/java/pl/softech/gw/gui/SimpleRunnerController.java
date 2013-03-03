/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.gui;

import com.google.gson.Gson;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
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
import pl.softech.gw.task.TaskFactory;
import pl.softech.gw.zip.IZipActionListener;
import pl.softech.gw.zip.IZipEvent;
import pl.softech.gw.zip.Unzip;
import pl.softech.gw.zip.UnzipPathEvent;

/**
 *
 * @author ssledz
 */
public class SimpleRunnerController {

    private class Configuration {

        private String confFileName = ".simplerunner.conf";
        private String projectDescriptorKey = "project.descriptor";
        private String projectDirKey = "project.dir";

        void read() throws Exception {

            File confFile = new File(confFileName);
            if (!confFile.exists()) {
                return;
            }

            FileReader in = null;
            try {
                in = new FileReader(confFile);
                Properties props = new Properties();
                props.load(in);
                view.getProjectDescriptorTextField().setText(props.getProperty(projectDescriptorKey));
                view.getProjectDirTextField().setText(props.getProperty(projectDirKey));
            } finally {
                if (in != null) {
                    in.close();
                }
            }


        }

        void save() throws Exception {

            File confFile = new File(confFileName);
            Properties props = new Properties();
            props.put(projectDescriptorKey, view.getProjectDescriptorTextField().getText());
            props.put(projectDirKey, view.getProjectDirTextField().getText());

            FileWriter out = null;
            try {
                out = new FileWriter(confFile);
                props.store(out, "");
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        }
    }
    private final JFrame frame;
    private final SimpleRunnerView view;
    private SvnTool svnTool;
    private ResourceDownloader downloadTool;
    private Unzip unzipTool;
    private AntTaskExecutorFactory antToolFactory;

    public SimpleRunnerController(SimpleRunnerView view, JFrame frame) {
        this.view = view;
        this.frame = frame;
        init();
    }

    private void println(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        println(sw.getBuffer().toString(), Color.RED);
    }

    private void println(final String message) {
        println(message, Color.BLACK);
    }
    
    private void println(final String message, final Color color) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JTextArea textArea = view.getMessageTextArea();
                Color fg = textArea.getForeground();
                textArea.setForeground(color);
                textArea.append(message);
                textArea.append("\n");
                textArea.setForeground(fg);
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
        });


    }

    private void init() {

        try {
            new Configuration().read();
        } catch (Exception e) {
            println(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new Configuration().save();
                } catch (Exception ex) {
                    println(ex);
                }

            }
        }));

        initTools();

        view.getProjectDescriptorTextField().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }
                if (e.getClickCount() == 2) {

                    JFileChooser fc = new JFileChooser();
                    if (new File(view.getProjectDescriptorTextField().getText()).exists()) {
                        fc.setSelectedFile(new File(view.getProjectDescriptorTextField().getText()));
                    }

                    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {

                        view.getProjectDescriptorTextField().setText(fc.getSelectedFile().getAbsolutePath());

                    }

                }
            }
        });

        view.getProjectDirTextField().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }
                if (e.getClickCount() == 2) {

                    JFileChooser fc = new JFileChooser();
                    fc.setSelectedFile(new File(view.getProjectDirTextField().getText()));
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {

                        view.getProjectDirTextField().setText(fc.getSelectedFile().getAbsolutePath());

                    }

                }
            }
        });

        view.getStartButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String projectPath = view.getProjectDirTextField().getText();
                if (projectPath == null || projectPath.length() == 0) {
                    JOptionPane.showMessageDialog(frame, "Please choose project directory");
                    return;
                }

                String descriptorPath = view.getProjectDirTextField().getText();

                if (descriptorPath == null || descriptorPath.length() == 0) {
                    JOptionPane.showMessageDialog(frame, "Please choose project descriptor");
                    return;
                }

                final File projectDir = new File(view.getProjectDirTextField().getText());
                final File projectDescriptor = new File(view.getProjectDescriptorTextField().getText());

                if (!projectDescriptor.exists()) {
                    JOptionPane.showMessageDialog(frame, "Selected project descriptor file doesn' exist");
                    return;
                }

                TaskFactory taskFactory = new TaskFactory(svnTool, downloadTool, unzipTool, antToolFactory);
                GsonFactory gsonFactory = new GsonFactory(taskFactory);
                final Gson gson = gsonFactory.create();

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            ProjectModule pm = gson.fromJson(new FileReader(projectDescriptor), ProjectModule.class);
                            pm.setProjectDir(projectDir);
                            executeProjectModule(true);
                            pm.execute();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, "Error during executing", "Error", JOptionPane.ERROR_MESSAGE);
                            println(ex);
                        } finally {
                            executeProjectModule(false);
                        }

                    }
                });
                t.setDaemon(true);
                t.start();
            }
        });
    }

    private void executeProjectModule(final boolean bFlag) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                view.getStartButton().setEnabled(!bFlag);
                if (bFlag) {
                    frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                } else {
                    frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });

    }

    private void initTools() {
        antToolFactory = new AntTaskExecutorFactory();
        svnTool = new SvnTool();
        downloadTool = new ResourceDownloader();
        unzipTool = new Unzip();

        unzipTool.addZipActionListener(new IZipActionListener() {
            @Override
            public void actionPerformed(IZipEvent event) {
                if (event instanceof UnzipPathEvent) {
                    println(String.format("Extracting: %s", ((UnzipPathEvent) event).getPath()));
                }
            }
        });

        downloadTool.addDownloadActionListener(new IDownloadActionListener() {
            double cnt = 0;
            int step = 10;

            @Override
            public void actionPerformed(IDownloadEvent event) {
                try {
                    if (event instanceof BytesReceivedEvent) {
                        BytesReceivedEvent e = (BytesReceivedEvent) event;
                        cnt += e.getReceived();
                        int p = (int) (cnt / e.getAll() * 100.0);

                        if (cnt == e.getAll()) {
                            cnt = 0;
                            step = 0;
                        }

                        if (p < step) {
                            return;
                        }

                        step += 10;
                        String message = String.format("Downloading %d[%d MB] %s", p, (e.getAll() / 1024 / 1024), e.getFileName());
                        println(message);

                    }
                } catch (Exception e) {
                    println(e);
                }

            }
        });

        svnTool.addSvnActionListener(new ISvnActionListener() {
            @Override
            public void actionPerformed(ISvnEvent event) {
                if (event instanceof SvnUpdateEvent) {
                    println(String.format("Updating %s", ((SvnUpdateEvent) event).getFile().getAbsoluteFile()));
                }

                if (event instanceof SvnAddEvent) {
                    println(String.format("Adding %s", ((SvnAddEvent) event).getFile().getAbsoluteFile()));
                }

                if (event instanceof SvnUpdateCompletedEvent) {
                    println("Svn task comleted");
                }
            }
        });

        antToolFactory.addBuildListener(new BuildListenerAdapter() {
            @Override
            public void messageLogged(BuildEvent event) {
                if (event.getTarget() != null) {
                    println(String.format("Executing ant target %s", event.getTarget().getName()));
                }
                println(event.getMessage());
            }
        });

    }

    public SimpleRunnerView getView() {
        return view;
    }
}
