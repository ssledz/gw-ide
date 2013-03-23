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
import org.tmatesoft.svn.core.SVNException;
import pl.softech.gw.Utils;
import pl.softech.gw.json.JsonExclude;
import pl.softech.gw.pmodule.ProjectModule;
import pl.softech.gw.svn.SvnTool;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class CheckoutTask implements ITask {

    @JsonExclude
    private final SvnTool svnTool;

    public CheckoutTask(SvnTool svnTool) {
        this.svnTool = svnTool;
    }

    @Override
    public void execute(Context context) {
        ProjectModule module = context.getModule();
        try {
            File svnCoDir = context.getCheckoutDir();
            if (svnCoDir.exists()) {
                Utils.deleteRecursive(svnCoDir);
            }

            svnTool.checkout(module.getSvnPath(), svnCoDir);
        } catch (SVNException ex) {
            throw new RuntimeException(ex);
        }

    }
}
