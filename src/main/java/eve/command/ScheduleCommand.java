package eve.command;

import java.util.List;

import eve.Storage;
import eve.Ui;
import eve.task.Task;
import eve.task.TaskList;

/** Shows every deadline and event in chronological order, like a schedule. */
public class ScheduleCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> scheduledTasks = tasks.schedule();
        ui.showSchedule(scheduledTasks);
    }
}
