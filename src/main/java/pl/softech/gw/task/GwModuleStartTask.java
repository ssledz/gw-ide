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

        public boolean isStarted = false;
        private final Pattern pattern = Pattern.compile(".*Started WebApplicationContext\\[/(?:ab|bc|pc).+");

        @Override
        public void messageLogged(BuildEvent event) {
            Matcher m = pattern.matcher(event.getMessage());
            if (m.matches()) {
                isStarted = true;
            }
        }
    }
    @JsonExclude
    private final AntTaskExecutorFactory antTaskExecutorFactory;
    
    private Exception exception;

    public GwModuleStartTask(AntTaskExecutorFactory antTaskExecutorFactory) {
        this.antTaskExecutorFactory = antTaskExecutorFactory;
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
                    exception = e;
                }
            }
        });
        th.setDaemon(true);
        th.start();
        while (!th.isInterrupted() && !l.isStarted) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        antTaskExecutorFactory.removeBuildListener(l);
        
        if(exception != null) {
            throw new RuntimeException(exception);
        }

    }
    
    public static void main(String[] args) {
        String line = "2013-03-03 19:33:58,320 INFO  org.mortbay.util.Container Started WebApplicationContext[/bc,BillingCenter]";
        Pattern pattern = Pattern.compile(".*Started WebApplicationContext\\[/(?:ab|bc|pc).+");
        System.out.println(pattern.matcher(line).matches());
    }

}
