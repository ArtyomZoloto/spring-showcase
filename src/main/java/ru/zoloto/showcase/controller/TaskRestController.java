package ru.zoloto.showcase.controller;

import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.zoloto.showcase.entity.ErrorsPresentation;
import ru.zoloto.showcase.entity.NewTaskPayload;
import ru.zoloto.showcase.entity.Task;
import ru.zoloto.showcase.repository.TaskRepository;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/tasks")
public class TaskRestController {

    private final TaskRepository taskRepository;
    private final MessageSource messageSource;

    public TaskRestController(TaskRepository taskRepository,
                              MessageSource messageSource) {
        this.taskRepository = taskRepository;
        this.messageSource = messageSource;
    }

    @GetMapping
    public ResponseEntity<List<Task>> handleGetAllTasks() {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(taskRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> handleCreateNewTask(
            @RequestBody NewTaskPayload payload,
            UriComponentsBuilder uriComponentsBuilder,
            Locale locale
    ) {
        if (payload.details() == null || payload.details().isBlank()) {
            var message = messageSource.getMessage("task.details.error.not_set", null, locale);
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorsPresentation(List.of(message)));
        } else {
            Task task = new Task(payload.details());
            taskRepository.save(task);
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .path("/api/tasks/{taskId}")
                            .build(Map.of("taskId", task.id())))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(task);
        }

    }

    @GetMapping("{id}")
    public ResponseEntity<Task> handleFindTask( @PathVariable("id") UUID id) {
        return ResponseEntity.of(taskRepository.findById(id));
    }
}
