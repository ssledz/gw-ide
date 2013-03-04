/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.task;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tools.ant.BuildEvent;
import pl.softech.gw.ant.AntTaskExecutorFactory;
import pl.softech.gw.ant.BuildListenerAdapter;
import pl.softech.gw.ant.IAntTaskExecutor;
import pl.softech.gw.json.JsonExclude;

/**
 *
 * @author ssledz
 */
public class GwModuleStartTask implements ITask {

    private class BuildListener extends BuildListenerAdapter {

        private final Pattern OnSuccess = Pattern.compile(".*Started WebApplicationContext\\[/(?:ab|bc|pc).+");
        private final Pattern[] onFailure = {
            Pattern.compile(".*drop db.*")
        };

        @Override
        public void messageLogged(BuildEvent event) {
            Matcher m = OnSuccess.matcher(event.getMessage());
            if (m.matches()) {
//                isStarted = true;
                removeBlockade();
            }

            for (Pattern p : onFailure) {
                Matcher mm = p.matcher(event.getMessage());
                if (mm.matches()) {
                    exception = new RuntimeException(
                            String.format("Failed to start, becouse of message %s[pattern=%s]", event.getMessage(), p.pattern()));
                    removeBlockade();
                }
            }

        }
    }
    @JsonExclude
    private final AntTaskExecutorFactory antTaskExecutorFactory;
    private RuntimeException exception;
//    private boolean isStarted = false;

    public GwModuleStartTask(AntTaskExecutorFactory antTaskExecutorFactory) {
        this.antTaskExecutorFactory = antTaskExecutorFactory;
    }

    void removeBlockade() {

        synchronized (GwModuleStartTask.this) {
            GwModuleStartTask.this.notifyAll();
        }

    }

    @Override
    public void execute(final Context context) {
        final BuildListener l = new BuildListener();
        antTaskExecutorFactory.addBuildListener(l);

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    IAntTaskExecutor a = antTaskExecutorFactory.create(new File(context.getModuleDir(), context.getModule().getBuildXmlPath()));
                    a.execute("dev-start");
                } catch (Exception e) {
                    exception = new RuntimeException(e);
                    removeBlockade();
                }
            }
        });
        th.setDaemon(true);
        th.start();

        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                exception = new RuntimeException(e);
            }
        }

//        while (!th.isInterrupted() && !isStarted && exception != null) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException ex) {
//                Thread.currentThread().interrupt();
//            }
//        }

        antTaskExecutorFactory.removeBuildListener(l);

        if (exception != null) {
            throw exception;
        }

    }

    public static void main(String[] args) {
        String line = "2013-03-03 19:33:58,320 INFO  org.mortbay.util.Container Started WebApplicationContext[/bc,BillingCenter]";
        Pattern pattern = Pattern.compile(".*Started WebApplicationContext\\[/(?:ab|bc|pc).+");
        System.out.println(pattern.matcher(line).matches());
    }
}
