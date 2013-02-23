/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.zip;

/**
 *
 * @author ssledz
 */
public class UnzipPathEvent implements IZipEvent {
    
    private final String path;

    public UnzipPathEvent(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    
}
