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
import pl.softech.gw.json.JsonExclude;
import pl.softech.gw.zip.Unzip;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class UnzipTask implements ITask {

    @JsonExclude
    private final Unzip unzipTool;

    public UnzipTask(Unzip unzipTool) {
        this.unzipTool = unzipTool;
    }

    @Override
    public void execute(Context context) {
        try {
            File moduleDir = context.getModuleDir();
            moduleDir.mkdir();

            unzipTool.unzipFile(new File(context.getModule().getProjectDir(), context.getZipFileName()), moduleDir);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }
}
