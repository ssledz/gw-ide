/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.download;

/**
 *
 * @author ssledz
 */
public class BytesReceivedEvent implements IDownloadEvent {
    
    private int received;
    private int all;
    private String fileName;

    public BytesReceivedEvent(int received, int all, String fileName) {
        this.received = received;
        this.all = all;
        this.fileName = fileName;
    }

    public int getReceived() {
        return received;
    }

    public int getAll() {
        return all;
    }

    public String getFileName() {
        return fileName;
    }
    
    
    
}
