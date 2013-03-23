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
import pl.softech.gw.pmodule.ProjectModule;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class Context {

    private ProjectModule module;
    private Exception e;

    public Context(ProjectModule module) {
        this.module = module;
    }

    public Context(Context context, Exception e) {
        this.module = context.module;
        this.e = e;
    }

    public ProjectModule getModule() {
        return module;
    }

    public File getModuleDir() {
        return new File(module.getProjectDir(), module.getModuleName());
    }
    
    public String getZipFileName() {
        return module.getModuleName() + ".zip";
    }

    public File getCheckoutDir() {
        return new File(module.getProjectDir(), module.getModuleName() + module.getSvnCheckoutPath());
    }
}
