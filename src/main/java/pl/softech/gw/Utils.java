/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 *
 * @author ssledz
 */
public class Utils {

    public static void deleteRecursive(File file) {

        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                deleteRecursive(f);
            }
        }

        if (!file.delete()) {
            throw new RuntimeException(String.format("Can't delete %s", file.getAbsolutePath()));
        }

    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int cnt;

        try {
            while ((cnt = in.read(buffer)) >= 0) {
                out.write(buffer, 0, cnt);
            }

        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    public static String readInputStreamAsString(InputStream is, String charset) throws Exception {

        BufferedReader in = null;
        StringBuilder builder = new StringBuilder();
        try {
            in = new BufferedReader(new InputStreamReader(is, charset));
            String line;
            while((line = in.readLine()) != null)  {
                builder.append(line).append("\n");
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return builder.toString();

    }
}
