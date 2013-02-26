/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import pl.softech.gw.task.ITask;
import pl.softech.gw.task.TaskFactory;

/**
 *
 * @author ssledz
 */
public class GsonFactory {

    private final TaskFactory taskFactory;

    public GsonFactory(TaskFactory taskFactory) {
        this.taskFactory = taskFactory;
    }

    public Gson create() {
        GsonBuilder builder = new GsonBuilder().setExclusionStrategies(new JsonExclusionStrategy()).setPrettyPrinting();
        builder.registerTypeAdapter(ITask.class, new TaskGsonTypeAdapter(taskFactory));
        return builder.create();

    }
}
