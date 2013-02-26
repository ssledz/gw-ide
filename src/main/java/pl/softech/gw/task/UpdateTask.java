/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.task;

import java.io.File;
import org.tmatesoft.svn.core.SVNException;
import pl.softech.gw.json.JsonExclude;
import pl.softech.gw.svn.SvnTool;

/**
 *
 * @author ssledz
 */
public class UpdateTask implements ITask {

    @JsonExclude
    private final SvnTool svnTool;

    public UpdateTask(SvnTool svnTool) {
        this.svnTool = svnTool;
    }

    @Override
    public void execute(Context context) {
        
        File svnCoDir = context.getCheckoutDir();
        if (!svnCoDir.exists()) {
            throw new RuntimeException(String.format("Update dir %s doesn't exist", svnCoDir.getAbsolutePath()));
        }
        try {
            svnTool.update(svnCoDir);
        } catch (SVNException ex) {
            throw new RuntimeException(ex);
        }
    }
}
