/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 *
 * @author ssledz
 */
public class ResourceDownloader {

    private final List<IDownloadActionListener> listeners = new LinkedList<IDownloadActionListener>();

    private void fireEvent(IDownloadEvent event) {
        for (IDownloadActionListener l : listeners) {
            l.actionPerformed(event);
        }
    }

    public void addDownloadActionListener(IDownloadActionListener l) {
        listeners.add(l);
    }

    private void printHeaders(HttpMethod method) {

        for (Header h : method.getResponseHeaders()) {
            System.out.println(h.getName() + " -> " + h.getValue());
        }

    }

    public void download(String url, File dir, String fileName) throws IOException {

        HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
        client.getHttpConnectionManager().getParams().setConnectionTimeout(30000);

        HttpMethod method = new GetMethod(url);
        method.setFollowRedirects(true);

        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {

            client.executeMethod(method);

            in = new BufferedInputStream(method.getResponseBodyAsStream());
            out = new BufferedOutputStream(new FileOutputStream(new File(dir, fileName)));

            printHeaders(method);
            int all = Integer.parseInt(method.getResponseHeader("Content-Length").getValue());

            byte[] buffer = new byte[1024];
            int cnt;
            while ((cnt = in.read(buffer)) > 0) {
                out.write(buffer, 0, cnt);
                fireEvent(new BytesReceivedEvent(cnt, all, fileName));
            }

        } catch (IOException e) {
            fireEvent(new DownloadErrorEvent(String.format("Error during downloading file %s", fileName), e));
            throw e;
        } finally {

            if (in != null) {
                in.close();
            }

            if (out != null) {
                out.close();
            }

        }

    }
}
