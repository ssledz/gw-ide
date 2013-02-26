/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.task;

import pl.softech.gw.ant.AntTaskExecutorFactory;
import pl.softech.gw.download.ResourceDownloader;
import pl.softech.gw.svn.SvnTool;
import pl.softech.gw.zip.Unzip;

/**
 *
 * @author ssledz
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
    
}
