/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author ssledz
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
