/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.svn;

import java.io.File;

/**
 *
 * @author ssledz
 */
public class SvnDeleteEvent implements ISvnEvent {

    private final File file;

    public SvnDeleteEvent(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
