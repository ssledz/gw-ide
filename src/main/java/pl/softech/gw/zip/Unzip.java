/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import pl.softech.gw.Utils;

/**
 *
 * @author ssledz
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
