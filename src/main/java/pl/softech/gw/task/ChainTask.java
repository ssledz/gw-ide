/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.task;

/**
 *
 * @author ssledz
 */
public class ChainTask implements ITask {

    @TaskParam
    private ITask onException;
    @TaskParam
    private ITask task;
    @TaskParam
    private ITask next;

    public ChainTask(ITask task, ITask next, ITask onException) {
        this.onException = onException;
        this.task = task;
        this.next = next;
    }

    @Override
    public void execute(Context context) {

        try {
            task.execute(context);
            next.execute(context);
        } catch (RuntimeException e) {
            if (onException == null) {
                throw e;
            }
            onException.execute(new Context(context, e));
        }

    }
}
