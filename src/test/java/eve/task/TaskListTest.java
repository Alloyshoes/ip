package eve.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import eve.EveException;

public class TaskListTest {
    @Test
    public void toIndex_validNumbers_returnsZeroBasedIndex() throws EveException {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("a"));
        tasks.add(new ToDo("b"));

        assertEquals(0, tasks.toIndex(1));
        assertEquals(1, tasks.toIndex(2));
    }

    @Test
    public void toIndex_zero_throwsWithTaskNumberInMessage() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("a"));

        EveException exception = assertThrows(EveException.class, () -> tasks.toIndex(0));
        assertTrue(exception.getMessage().contains("no task number 0"));
    }

    @Test
    public void toIndex_negative_throws() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("a"));

        assertThrows(EveException.class, () -> tasks.toIndex(-1));
    }

    @Test
    public void toIndex_pastEndOfList_throwsWithTaskNumberInMessage() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("a"));

        EveException exception = assertThrows(EveException.class, () -> tasks.toIndex(2));
        assertTrue(exception.getMessage().contains("no task number 2"));
    }

    @Test
    public void toIndex_emptyList_anyNumberThrows() {
        TaskList tasks = new TaskList();

        assertThrows(EveException.class, () -> tasks.toIndex(1));
    }

    @Test
    public void occurringOn_mixOfTaskTypes_returnsOnlyMatchingOnes() {
        TaskList tasks = new TaskList();
        LocalDate date = LocalDate.of(2019, 10, 7);
        Task todo = new ToDo("just a todo");
        Task matchingDeadline = new Deadline("return book", date);
        Task nonMatchingDeadline = new Deadline("other", date.plusDays(1));
        Task matchingEvent = new Event("trip", date.minusDays(3), date.plusDays(4));
        tasks.add(todo);
        tasks.add(matchingDeadline);
        tasks.add(nonMatchingDeadline);
        tasks.add(matchingEvent);

        List<Task> matches = tasks.occurringOn(date);

        assertEquals(2, matches.size());
        assertTrue(matches.contains(matchingDeadline));
        assertTrue(matches.contains(matchingEvent));
    }

    @Test
    public void occurringOn_noMatches_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("just a todo"));

        List<Task> matches = tasks.occurringOn(LocalDate.of(2019, 1, 1));

        assertTrue(matches.isEmpty());
    }
}
