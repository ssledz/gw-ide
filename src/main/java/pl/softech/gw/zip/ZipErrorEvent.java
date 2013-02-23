/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.zip;

/**
 *
 * @author ssledz
 */
public class ZipErrorEvent implements IZipEvent {
    private String message;
    private Exception exception;

    public ZipErrorEvent(String message, Exception exception) {
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
