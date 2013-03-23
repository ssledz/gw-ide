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
import java.io.IOException;
import java.util.Date;
import pl.softech.gw.download.ResourceDownloader;
import pl.softech.gw.json.JsonExclude;
import pl.softech.gw.pmodule.ProjectModule;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class DownloadModuleTask implements ITask {

    @JsonExclude
    private final ResourceDownloader downloader;

    public DownloadModuleTask(ResourceDownloader downloader) {
        this.downloader = downloader;
    }

    @Override
    public void execute(Context context) {
        ProjectModule module = context.getModule();
        try {
            downloader.download(module.getModuleDownloadUrl(), module.getProjectDir(), context.getZipFileName());
            File moduleDir = context.getModuleDir();
            if (moduleDir.exists()) {
                moduleDir.renameTo(new File(module.getProjectDir(), String.format("%s-%s", module.getModuleName(), new Date().getTime())));
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
