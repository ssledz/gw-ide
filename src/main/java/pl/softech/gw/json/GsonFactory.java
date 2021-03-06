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
import com.google.gson.GsonBuilder;
import pl.softech.gw.task.ITask;
import pl.softech.gw.task.TaskFactory;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class GsonFactory {

    private final TaskFactory taskFactory;

    public GsonFactory(TaskFactory taskFactory) {
        this.taskFactory = taskFactory;
    }

    public Gson create() {
        GsonBuilder builder = new GsonBuilder().setExclusionStrategies(new JsonExclusionStrategy()).setPrettyPrinting();
        TaskGsonTypeAdapter taskAdapter = new TaskGsonTypeAdapter(taskFactory);
        builder.registerTypeAdapter(ITask.class, taskAdapter);
        Gson gson = builder.create();
        taskAdapter.setGson(gson);
        return gson;

    }
}
