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

import pl.softech.gw.ant.AntTaskExecutorFactory;
import pl.softech.gw.download.ResourceDownloader;
import pl.softech.gw.pmodule.ProjectModule;
import pl.softech.gw.svn.SvnTool;
import pl.softech.gw.zip.Unzip;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class TaskFactory {
    
    private final SvnTool svnTool;
    private final ResourceDownloader downloader;
    private final Unzip unzipTool;
    private final AntTaskExecutorFactory antTaskExecutorFactory;

    public TaskFactory(SvnTool svnTool, ResourceDownloader downloader, Unzip unzipTool, AntTaskExecutorFactory antTaskExecutorFactory) {
        this.svnTool = svnTool;
        this.downloader = downloader;
        this.unzipTool = unzipTool;
        this.antTaskExecutorFactory = antTaskExecutorFactory;
    }
    
    public ITask createUpdateTask() {
        return new UpdateTask(svnTool);
    }
    
    public ITask createCheckoutTask() {
        return new CheckoutTask(svnTool);
    }
    
    public ITask createDownloadModuleTask() {
        return new DownloadModuleTask(downloader);
    }
    
    public ITask createUnzipTask() {
        return new UnzipTask(unzipTool);
    }
    
    public ITask createAntTask(String antTarget) {
        return new AntTask(antTaskExecutorFactory, antTarget);
    }
    
     public ITask createGwModuleStartTask() {
        return new GwModuleStartTask(antTaskExecutorFactory);
    }
     
     public ITask createExternalAntTask(String antTarget, ProjectModule module) {
        return new ExternalAntTask(antTaskExecutorFactory, antTarget, module);
    }
    
}
