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
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class GwModuleStartTask implements ITask {

    private class BuildListener extends BuildListenerAdapter {

        private final Pattern OnSuccess = Pattern.compile(".*Started WebApplicationContext\\[/(?:ab|bc|pc).+");
        private final Pattern[] onFailure = {
            Pattern.compile(".*ERROR.*ContactManager\\s+unable to start.*"),
            Pattern.compile(".*ERROR.*BillingCenter\\s+unable to start.*"),
            Pattern.compile(".*ERROR.*PolicyCenter\\s+unable to start.*"),
            Pattern.compile(".*Decreased runlevel to 'SHUTDOWN'.*"),
        };

        @Override
        public void messageLogged(BuildEvent event) {
            Matcher m = OnSuccess.matcher(event.getMessage());
            if (m.matches()) {
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

        antTaskExecutorFactory.removeBuildListener(l);

        if (exception != null) {
            throw exception;
        }

    }

}
