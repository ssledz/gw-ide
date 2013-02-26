/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author ssledz
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
