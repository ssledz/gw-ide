/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author ssledz
 */
public class GsonUtils {

    public static Gson createGson() {
        GsonBuilder builder = new GsonBuilder().setExclusionStrategies(new JsonExclusionStrategy()).setPrettyPrinting();
        builder.registerTypeAdapter(java.util.List.class, new ListRunnableSerializer());
        return builder.create();
    }
}
