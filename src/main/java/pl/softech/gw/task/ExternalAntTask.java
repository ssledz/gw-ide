/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.task;

import java.io.File;
import pl.softech.gw.ant.AntTaskExecutorFactory;
import pl.softech.gw.ant.IAntTaskExecutor;
import pl.softech.gw.json.JsonExclude;
import pl.softech.gw.pmodule.ProjectModule;

/**
 *
 * @author ssledz
 */
public class ExternalAntTask implements ITask {

    @JsonExclude
    private final AntTaskExecutorFactory antTaskExecutorFactory;
    @TaskParam
    private String target;
    
    @TaskParam
    private ProjectModule module;

    public ExternalAntTask(AntTaskExecutorFactory antTaskExecutorFactory) {
        this.antTaskExecutorFactory = antTaskExecutorFactory;
    }

    public ExternalAntTask(AntTaskExecutorFactory antTaskExecutorFactory, String target, ProjectModule module) {
        this.antTaskExecutorFactory = antTaskExecutorFactory;
        this.target = target;
        this.module = module;
    }
    
    @Override
    public void execute(Context context) {
        module.setProjectDir(context.getModule().getProjectDir());
        context = new Context(module);
        IAntTaskExecutor a = antTaskExecutorFactory.create(new File(context.getModuleDir(), context.getModule().getBuildXmlPath()));
        a.execute(target);
        
    }
    
}
