/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import pl.softech.gw.task.AntTask;
import pl.softech.gw.task.CheckoutTask;
import pl.softech.gw.task.DownloadModuleTask;
import pl.softech.gw.task.ITask;
import pl.softech.gw.task.TaskFactory;
import pl.softech.gw.task.TaskParam;
import pl.softech.gw.task.UnzipTask;
import pl.softech.gw.task.UpdateTask;

/**
 *
 * @author ssledz
 */
public class TaskGsonTypeAdapter extends TypeAdapter<ITask> {

    private class Param {

        String name;
        Object value;

        public Param(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }

    private class TaskInstanceCreator {

        String className;
        Map<String, Param> params;

        TaskInstanceCreator() {
            params = new HashMap<String, Param>();
        }

        ITask create() {
            try {
                Class clazz = Class.forName(className);

                if (clazz == AntTask.class) {
                    return taskFactory.createAntTask(params.get("target").value.toString());
                } else if (clazz == CheckoutTask.class) {
                    return taskFactory.createCheckoutTask();
                } else if (clazz == DownloadModuleTask.class) {
                    return taskFactory.createDownloadModuleTask();
                } else if (clazz == UpdateTask.class) {
                    return taskFactory.createUpdateTask();
                } else if (clazz == UnzipTask.class) {
                    return taskFactory.createUnzipTask();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return null;
        }
    }
    private final TaskFactory taskFactory;

    public TaskGsonTypeAdapter(TaskFactory taskFactory) {
        this.taskFactory = taskFactory;
    }

    private Collection<Param> getParams(ITask value) {
        try {
            Collection<Param> params = new LinkedList<Param>();
            for (Field f : value.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (f.getAnnotation(TaskParam.class) != null) {
                    params.add(new Param(f.getName(), f.get(value)));
                }

            }
            return params;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(JsonWriter out, ITask value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.beginObject();
        out.name("class");
        out.value(value.getClass().getName());

        for (Param p : getParams(value)) {
            out.name(p.name);

            if (p.value != null) {
                if (p.value instanceof ITask) {
                    this.write(out, (ITask) p.value);
                } else {
                    out.value(p.value.toString());
                }


            } else {
                out.nullValue();
            }

        }


        out.endObject();

    }

    @Override
    public ITask read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        reader.beginObject();

        TaskInstanceCreator instanceCreator = new TaskInstanceCreator();
        while (reader.peek() != JsonToken.END_OBJECT) {

            String name = reader.nextName();
            String value = reader.nextString();

            if (name.equals("class")) {
                instanceCreator.className = value;
            } else {
                instanceCreator.params.put(name, new Param(name, value));
            }

        }

        reader.endObject();
        return instanceCreator.create();
    }
}
