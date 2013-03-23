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
package pl.softech.gw.json;

import com.google.gson.Gson;
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
import pl.softech.gw.pmodule.ProjectModule;
import pl.softech.gw.task.AntTask;
import pl.softech.gw.task.ChainTask;
import pl.softech.gw.task.CheckoutTask;
import pl.softech.gw.task.DownloadModuleTask;
import pl.softech.gw.task.ExternalAntTask;
import pl.softech.gw.task.GwModuleStartTask;
import pl.softech.gw.task.ITask;
import pl.softech.gw.task.TaskFactory;
import pl.softech.gw.task.TaskParam;
import pl.softech.gw.task.UnzipTask;
import pl.softech.gw.task.UpdateTask;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
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
                } else if (clazz == ChainTask.class) {
                    return new ChainTask((ITask) params.get("task").value,
                            params.get("next") == null ? null : (ITask) params.get("next").value,
                            params.get("onException") == null ? null : (ITask) params.get("onException").value);
                } else if (clazz == GwModuleStartTask.class) {
                    return taskFactory.createGwModuleStartTask();
                } else if (clazz == ExternalAntTask.class) {
                    return taskFactory.createExternalAntTask(params.get("target").value.toString(),
                            (ProjectModule) params.get("module").value);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return null;
        }
    }
    private final TaskFactory taskFactory;
    private Gson gson;

    public TaskGsonTypeAdapter(TaskFactory taskFactory) {
        this.taskFactory = taskFactory;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
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
                } else if (p.value instanceof ProjectModule) {
                    gson.getAdapter(ProjectModule.class).write(out, (ProjectModule) p.value);
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
            Object value;
            if (reader.peek() == JsonToken.BEGIN_OBJECT && !name.equals("module")) {
                value = read(reader);
            } else {
                if (name.equals("module")) {
                    value = gson.getAdapter(ProjectModule.class).read(reader);
                } else {
                    value = reader.nextString();
                }
            }

            if (name.equals("class")) {
                instanceCreator.className = value.toString();
            } else {
                instanceCreator.params.put(name, new Param(name, value));
            }

        }

        reader.endObject();
        return instanceCreator.create();
    }
}
