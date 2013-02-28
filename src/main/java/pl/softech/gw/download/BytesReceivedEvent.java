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
    private long all;
    private String fileName;

    public BytesReceivedEvent(int received, long all, String fileName) {
        this.received = received;
        this.all = all;
        this.fileName = fileName;
    }

    public int getReceived() {
        return received;
    }

    public long getAll() {
        return all;
    }

    public String getFileName() {
        return fileName;
    }
    
    
    
}
