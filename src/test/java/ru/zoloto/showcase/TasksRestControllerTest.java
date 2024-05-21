package ru.zoloto.showcase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;
import ru.zoloto.showcase.controller.TaskRestController;
import ru.zoloto.showcase.entity.ErrorsPresentation;
import ru.zoloto.showcase.entity.NewTaskPayload;
import ru.zoloto.showcase.entity.Task;
import ru.zoloto.showcase.repository.TaskRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class TasksRestControllerTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    TaskRestController controller;

    @Test
    void handleGetAllTasks_returnValidResponseEntity() {
        //given
        var tasks = List.of(
                new Task(UUID.randomUUID(), "first task", false),
                new Task(UUID.randomUUID(), "second task", true)
        );
        Mockito.doReturn(tasks).when(taskRepository).findAll();

        //when
        var responseEntity = controller.handleGetAllTasks();

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(tasks, responseEntity.getBody());
    }

    @Test
    void handleCreateNewTask_PayloadIsValid_ReturnValidResponseEntity() {
        //given
        var details = "third task";

        //when
        var responseEntity = controller.handleCreateNewTask(new NewTaskPayload(details),
                UriComponentsBuilder.fromUriString("http://localhost:8080"), Locale.ENGLISH);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        if (responseEntity.getBody() instanceof Task task) {
            assertNotNull(task.id());
            assertEquals(details, task.details());
            assertFalse(task.completed());
            assertEquals(URI.create("http://localhost:8080/api/tasks/" + task.id()),
                    responseEntity.getHeaders().getLocation());
            verify(taskRepository).save(task);
        } else {
            assertInstanceOf(Task.class, responseEntity.getBody());
        }
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void handleCreateNewTask_PayloadIsInValid_ReturnValidResponseEntity() {
        //given
        var details = "   ";
        var locale = Locale.ENGLISH;
        var errorMessage = "details is not set en";
        doReturn(errorMessage).when(messageSource).getMessage("task.details.error.not_set", new Object[0], locale);

        //when
        var responseEntity = controller.handleCreateNewTask(new NewTaskPayload(details),
                UriComponentsBuilder.fromUriString("http://localhost:8080"), locale);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(new ErrorsPresentation(List.of("details is not set en")),responseEntity.getBody());

        verifyNoInteractions(taskRepository);

    }
}
