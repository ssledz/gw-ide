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
package pl.softech.gw.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import pl.softech.gw.Utils;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class Unzip {

    private final List<IZipActionListener> listeners = new LinkedList<IZipActionListener>();

    private void fireEvent(IZipEvent event) {
        for (IZipActionListener l : listeners) {
            l.actionPerformed(event);
        }
    }

    public void addZipActionListener(IZipActionListener l) {
        listeners.add(l);
    }

    
    public void unzipFile(File file, File dir) throws IOException {

        Enumeration entries;
        ZipFile zipFile;

        try {
            zipFile = new ZipFile(file);

            entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();

                fireEvent(new UnzipPathEvent(entry.getName()));
                if (entry.isDirectory()) {
                    
                    (new File(dir, entry.getName())).mkdir();
                    continue;
                }
                Utils.copy(zipFile.getInputStream(entry),
                        new BufferedOutputStream(new FileOutputStream(new File(dir, entry.getName()))));
            }

            zipFile.close();
        } catch (IOException e) {
            fireEvent(new ZipErrorEvent(String.format("Error during unziping file %s", file.getAbsolutePath()), e));
            throw e;
        }

    }
}
