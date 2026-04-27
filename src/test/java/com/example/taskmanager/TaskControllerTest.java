package com.example.taskmanager;

import com.example.taskmanager.controller.TaskController;
import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.exception.GlobalExceptionHandler;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TaskControllerTest — Integration Tests for the REST API layer
 *
 * Strategy:
 *   - @WebMvcTest loads ONLY the web layer (controller + exception handler)
 *   - No full Spring context, no database needed
 *   - We mock TaskService with @MockBean
 *   - MockMvc simulates HTTP requests without starting a real server
 *
 * This tests: routing, request parsing, response serialization, HTTP status codes
 */
@WebMvcTest(controllers = {TaskController.class, GlobalExceptionHandler.class})
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;       // Simulates HTTP requests

    @Autowired
    private ObjectMapper objectMapper; // Converts objects to/from JSON

    @MockBean
    private TaskService taskService;  // Mocked service — no real business logic runs

    // ── Helper to build a sample TaskResponse ──────────
    private TaskResponse buildResponse(Long id, String title, String status) {
        try {
            TaskResponse r = new TaskResponse();
            var idF = TaskResponse.class.getDeclaredField("id");      idF.setAccessible(true);      idF.set(r, id);
            var tF  = TaskResponse.class.getDeclaredField("title");   tF.setAccessible(true);       tF.set(r, title);
            var sF  = TaskResponse.class.getDeclaredField("status");  sF.setAccessible(true);       sF.set(r, status);
            var caF = TaskResponse.class.getDeclaredField("createdAt"); caF.setAccessible(true);    caF.set(r, LocalDateTime.now());
            var uaF = TaskResponse.class.getDeclaredField("updatedAt"); uaF.setAccessible(true);    uaF.set(r, LocalDateTime.now());
            var dF  = TaskResponse.class.getDeclaredField("description"); dF.setAccessible(true);   dF.set(r, "desc");
            return r;
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    // ─── POST /tasks ────────────────────────────────────

    @Test
    @DisplayName("POST /tasks - valid body → 201 Created")
    void createTask_Returns201() throws Exception {
        TaskResponse response = buildResponse(1L, "Buy groceries", "OPEN");
        when(taskService.createTask(any(TaskRequest.class))).thenReturn(response);

        String json = objectMapper.writeValueAsString(new TaskRequest("Buy groceries", "desc"));

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())         // 201
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Buy groceries"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    @DisplayName("POST /tasks - blank title → 400 Bad Request")
    void createTask_BlankTitle_Returns400() throws Exception {
        String json = objectMapper.writeValueAsString(new TaskRequest("", "desc"));

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())      // 400
                .andExpect(jsonPath("$.fieldErrors.title").exists());
    }

    @Test
    @DisplayName("POST /tasks - missing title → 400 Bad Request")
    void createTask_MissingTitle_Returns400() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"no title here\"}"))
                .andExpect(status().isBadRequest());     // 400
    }

    // ─── GET /tasks ─────────────────────────────────────

    @Test
    @DisplayName("GET /tasks - returns 200 with list")
    void getAllTasks_Returns200() throws Exception {
        TaskResponse r1 = buildResponse(1L, "Task One", "OPEN");
        TaskResponse r2 = buildResponse(2L, "Task Two", "DONE");
        when(taskService.getAllTasks()).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())              // 200
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Task One"))
                .andExpect(jsonPath("$[1].title").value("Task Two"));
    }

    @Test
    @DisplayName("GET /tasks - empty list returns 200")
    void getAllTasks_Empty_Returns200() throws Exception {
        when(taskService.getAllTasks()).thenReturn(List.of());

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ─── GET /tasks/{id} ────────────────────────────────

    @Test
    @DisplayName("GET /tasks/1 - valid ID returns 200")
    void getTaskById_Returns200() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(buildResponse(1L, "Buy groceries", "OPEN"));

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /tasks/99 - invalid ID returns 404")
    void getTaskById_NotFound_Returns404() throws Exception {
        when(taskService.getTaskById(99L))
                .thenThrow(new ResourceNotFoundException("Task not found with id: 99"));

        mockMvc.perform(get("/tasks/99"))
                .andExpect(status().isNotFound())        // 404
                .andExpect(jsonPath("$.message").value("Task not found with id: 99"));
    }

    // ─── PUT /tasks/{id} ────────────────────────────────

    @Test
    @DisplayName("PUT /tasks/1 - valid update returns 200")
    void updateTask_Returns200() throws Exception {
        TaskResponse updated = buildResponse(1L, "Updated Title", "OPEN");
        when(taskService.updateTask(eq(1L), any(TaskRequest.class))).thenReturn(updated);

        String json = objectMapper.writeValueAsString(new TaskRequest("Updated Title", "new desc"));

        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    // ─── DELETE /tasks/{id} ─────────────────────────────

    @Test
    @DisplayName("DELETE /tasks/1 - returns 204 No Content")
    void deleteTask_Returns204() throws Exception {
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());      // 204
    }

    @Test
    @DisplayName("DELETE /tasks/99 - not found returns 404")
    void deleteTask_NotFound_Returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Task not found with id: 99"))
                .when(taskService).deleteTask(99L);

        mockMvc.perform(delete("/tasks/99"))
                .andExpect(status().isNotFound());       // 404
    }

    // ─── PATCH /tasks/{id}/complete ─────────────────────

    @Test
    @DisplayName("PATCH /tasks/1/complete - marks as DONE, returns 200")
    void markComplete_Returns200() throws Exception {
        TaskResponse done = buildResponse(1L, "Task", "DONE");
        when(taskService.markTaskComplete(1L)).thenReturn(done);

        mockMvc.perform(patch("/tasks/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }
}
