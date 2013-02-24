/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ssledz
 */
public class ListRunnableSerializer implements JsonSerializer<java.util.List> {

    @Override
    public JsonElement serialize(List src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();

        if (typeOfSrc instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) typeOfSrc;
            if (Arrays.asList(pt.getActualTypeArguments()).contains(Runnable.class)) {
                for (Object r : src) {
                    array.add(new JsonPrimitive(r.toString()));
                }

                return array;
            }

        }

        return context.serialize(src);

    }
}
