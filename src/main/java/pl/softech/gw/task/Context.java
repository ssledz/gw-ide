/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.task;

import java.io.File;
import pl.softech.gw.pmodule.ProjectModule;

/**
 *
 * @author ssledz
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
