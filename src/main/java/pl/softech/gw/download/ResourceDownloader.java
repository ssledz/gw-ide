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
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

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

    public void download(String url, File dir, String fileName) throws IOException {

        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        
        HttpResponse response = httpclient.execute(httpGet);
           
        HttpEntity entity = null;

        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            entity = response.getEntity();
            
            long all = entity.getContentLength();
            in = new BufferedInputStream(entity.getContent());
            out = new BufferedOutputStream(new FileOutputStream(new File(dir, fileName)));

            byte[] buffer = new byte[1024];
            int cnt;
            while ((cnt = in.read(buffer)) >= 0) {
                out.write(buffer, 0, cnt);
                fireEvent(new BytesReceivedEvent(cnt, all, fileName));
            }

            EntityUtils.consume(entity);
        } catch (IOException e) {
            fireEvent(new DownloadErrorEvent(String.format("Error during downloading file %s", fileName), e));
            throw e;
        } finally {

            httpGet.releaseConnection();

            if (in != null) {
                in.close();
            }

            if (out != null) {
                out.close();
            }
            
            httpclient.getConnectionManager().shutdown();

        }

    }
}
