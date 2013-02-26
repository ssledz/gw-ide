/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.task;

import java.io.File;
import java.io.IOException;
import pl.softech.gw.json.JsonExclude;
import pl.softech.gw.zip.Unzip;

/**
 *
 * @author ssledz
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
