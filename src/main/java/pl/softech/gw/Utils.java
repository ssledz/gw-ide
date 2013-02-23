/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw;

import java.io.File;

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
}
