package com.learncicd;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * The @WebMvcTest annotation is used to create MVC (or more specifically controller) related tests.
 * It can also be configured to test for a specific controller. It mainly loads and makes testing of the web layer easy.
 *
 * The @SpringBootTest annotation is used to create a test environment by loading a full application context
 * (like classes annotated with @Component and @Service, DB connections, etc). It looks for the main class
 * (which has the @SpringBootApplication annotation) and uses it to start the application context.
 *
 * In a @SpringBootTest context, MockMvc will automatically call the actual service implementation from the controller.
 * The service layer beans will be available in the application context. To use MockMvc within our tests, we’ll need to
 * add the @AutoConfigureMockMvc annotation. This annotation creates an instance of MockMvc, injects it into the mockMvc
 * variable, and makes it ready for testing without requiring manual configuration:
 *
 * In @WebMvcTest, MockMvc will be accompanied by @MockBean of the service layer to mock service layer responses without
 * calling the real service. Also, service layer beans are not included in the application context.
 * It provides @AutoConfigureMockMvc by default:
 *
 * @SpringBootTest is heavyweight as it is mostly configured for integration testing by default unless we want to use any mocks.
 *
 * @WebMvcTest is more isolated and only concerned about the MVC layer. It is ideal for unit testing.
 *
 * @WebMvcTest does not detect dependencies needed for the controller automatically, so we’ve to Mock them. While @SpringBootTest does it automatically.
 *
 * @SpringBootTest is mostly not a good choice for customization but @WebMvcTest can be customized to work with only limited controller classes.
 * EXAMPLE : @WebMvcTest(SortingController.class)
 * class SortingControllerUnitTest {}
 *
 * MockMvc is only auto-configured when you use @WebMvcTest or @SpringBootTest with @AutoConfigureMockMvc.
 * If you just write a plain JUnit class without those annotations, Spring doesn’t know to create the MockMvc bean, so autowiring fails.
 *
 */

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskController taskController;

    @Test
    void testHomePageLoads() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("tasks"));
    }

    @Test
    void testAddTask() throws Exception {
        mockMvc.perform(post("/add")
                        .param("taskName", "Learning Java 21"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void testDeleteTaskValidIndex() throws Exception {
        // First add a task
        mockMvc.perform(post("/add").param("taskName", "Task to delete"));

        // Then delete it
        mockMvc.perform(post("/delete").param("index", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void testDeleteTaskInvalidIndex() throws Exception {
        // Try deleting with invalid index
        mockMvc.perform(post("/delete").param("index", "99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Task Application is healthy!"));
    }
}