/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.task;

import java.io.File;
import pl.softech.gw.ant.AntTaskExecutorFactory;
import pl.softech.gw.ant.IAntTaskExecutor;
import pl.softech.gw.json.JsonExclude;

/**
 *
 * @author ssledz
 */
public class AntTask implements ITask {

    @JsonExclude
    private final AntTaskExecutorFactory antTaskExecutorFactory;
    @TaskParam
    private String target;

    public AntTask(AntTaskExecutorFactory antTaskExecutorFactory) {
        this.antTaskExecutorFactory = antTaskExecutorFactory;
    }
    
    public AntTask(AntTaskExecutorFactory antTaskExecutorFactory, String target) {
        this.antTaskExecutorFactory = antTaskExecutorFactory;
        this.target = target;
    }

    @Override
    public void execute(Context context) {
        IAntTaskExecutor a = antTaskExecutorFactory.create(new File(context.getModuleDir(), context.getModule().getBuildXmlPath()));
        a.execute(target);
    }
}
