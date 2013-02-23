/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.download;

/**
 *
 * @author ssledz
 */
public class DownloadErrorEvent implements IDownloadEvent {
    
    private String message;
    private Exception exception;

    public DownloadErrorEvent(String message, Exception exception) {
        this.message = message;
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public Exception getException() {
        return exception;
    }
    
    
    
}
