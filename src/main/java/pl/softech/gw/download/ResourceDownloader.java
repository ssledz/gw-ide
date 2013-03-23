/*
 * Copyright 2013 Sławomir Śledź <slawomir.sledz@sof-tech.pl>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.softech.gw.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
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
        InputStream ins = null;
        BufferedOutputStream out = null;
        try {
            StatusLine statusLine = response.getStatusLine();
            entity = response.getEntity();

            if (statusLine.getStatusCode() >= 300) {
                EntityUtils.consume(entity);
                throw new HttpResponseException(statusLine.getStatusCode(),
                        statusLine.getReasonPhrase());
            }

            ins = entity.getContent();
            long all = entity.getContentLength();
            in = new BufferedInputStream(ins);
            out = new BufferedOutputStream(new FileOutputStream(new File(dir, fileName)));

            byte[] buffer = new byte[1024];
            int cnt;
            while ((cnt = in.read(buffer)) >= 0) {
                out.write(buffer, 0, cnt);
                fireEvent(new BytesReceivedEvent(cnt, all, fileName));
            }

        } catch (IOException e) {
            fireEvent(new DownloadErrorEvent(String.format("Error during downloading file %s", fileName), e));
            throw e;
        } finally {

            if (ins != null) {
                ins.close();
            }

            if (in != null) {
                in.close();
            }

            if (out != null) {
                out.close();
            }

            httpGet.releaseConnection();
            httpclient.getConnectionManager().shutdown();

        }

    }
}
