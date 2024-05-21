package ru.zoloto.showcase.repository;

import org.springframework.stereotype.Repository;
import ru.zoloto.showcase.entity.Task;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InMemoryTaskRepository implements TaskRepository {

    private List<Task> taskList = new LinkedList<>();

    public InMemoryTaskRepository() {
        taskList.add(new Task(UUID.randomUUID(), "first task", false));
        taskList.add(new Task(UUID.randomUUID(), "second task", true));
    }

    @Override
    public List<Task> findAll() {
        return taskList;
    }

    @Override
    public void save(Task task) {
        taskList.add(task);
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return taskList.stream()
                .filter(task -> task.id().equals(id))
                .findFirst();
    }
}
