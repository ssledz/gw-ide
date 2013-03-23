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
import pl.softech.gw.ant.AntTaskExecutorFactory;
import pl.softech.gw.ant.IAntTaskExecutor;
import pl.softech.gw.json.JsonExclude;
import pl.softech.gw.pmodule.ProjectModule;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
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
