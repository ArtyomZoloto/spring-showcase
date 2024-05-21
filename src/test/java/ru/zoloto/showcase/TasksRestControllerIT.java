package ru.zoloto.showcase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.zoloto.showcase.repository.TaskRepository;

import java.util.Locale;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TasksRestControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TaskRepository taskRepository;

    @AfterEach
    void clean() {
        taskRepository.clear();
    }

    @Test
    void handleGetAllTasks_ReturnsValidResponseEntity() throws Exception {
        //given
        var requestBuilderGet = MockMvcRequestBuilders.get("/api/tasks");

        var requestBuilderPost = MockMvcRequestBuilders
                .post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "details":"first task",
                            "completed":false
                        }
                        """);
        mockMvc.perform(requestBuilderPost);

        //when
        mockMvc.perform(requestBuilderGet)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                [{
                                "details":"first task",
                                "completed":false
                                }]
                                """
                        )
                );
    }

    @Test
    void handleCreateNewTask_PayloadIsValid_ReturnsValidResponseEntity() throws Exception {
        //given
        var post = MockMvcRequestBuilders
                .post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "details":"new details"
                        }
                        """);
        //when
        mockMvc.perform(post).andExpectAll(
                //then
                status().isCreated(),
                header().exists(HttpHeaders.LOCATION),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("""
                        {
                            "details":"new details",
                            "completed":false
                        }
                        """),
                jsonPath("$.id").exists()
        );
    }

    @Test
    void handleCreateNewTask_PayloadIsInvalid_ReturnsValidResponseEntity() throws Exception {
        //given
        var post = MockMvcRequestBuilders.post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .locale(new Locale("ru", "Ru"))
                .content("""
                        {
                            "details":null
                        }
                        """);
        //when
        mockMvc.perform(post).andExpectAll(
                //then
                status().isBadRequest(),
                header().doesNotExist(HttpHeaders.LOCATION),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("""
                        {
                         "errors":["Детали не заполнены"]
                        }
                        """, true)


        );

    }
}
