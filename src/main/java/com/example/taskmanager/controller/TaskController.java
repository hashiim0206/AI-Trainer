package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TaskController maps HTTP requests to service methods.
 *
 * Responsibilities:
 *  - Handle incoming HTTP requests (GET, POST, PUT, DELETE, PATCH)
 *  - Validate request bodies using @Valid
 *  - Return appropriate HTTP status codes
 *  - Delegate ALL business logic to TaskService (never put logic here!)
 *
 * @RestController = @Controller + @ResponseBody (auto-serialize to JSON)
 * @RequestMapping("/tasks") = base URL path for all endpoints in this class
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    // Constructor injection (preferred over @Autowired on field)
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * POST /tasks
     * Create a new task.
     *
     * Request Body:
     * { "title": "Finish project", "description": "Backend API" }
     *
     * Response: 201 Created
     * { "id": 1, "title": "Finish project", "status": "OPEN", ... }
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        log.info("POST /tasks - Creating task");
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);  // 201
    }

    /**
     * GET /tasks
     * Retrieve all tasks.
     *
     * Response: 200 OK with a JSON array of tasks.
     */
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        log.info("GET /tasks - Fetching all tasks");
        return ResponseEntity.ok(taskService.getAllTasks());  // 200
    }

    /**
     * GET /tasks/{id}
     * Retrieve a single task by its ID.
     *
     * Response: 200 OK or 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        log.info("GET /tasks/{} - Fetching task", id);
        return ResponseEntity.ok(taskService.getTaskById(id));  // 200
    }

    /**
     * PUT /tasks/{id}
     * Fully update an existing task (title + description).
     *
     * Response: 200 OK or 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        log.info("PUT /tasks/{} - Updating task", id);
        return ResponseEntity.ok(taskService.updateTask(id, request));  // 200
    }

    /**
     * DELETE /tasks/{id}
     * Delete a task permanently.
     *
     * Response: 204 No Content (success, nothing to return)
     *           404 Not Found (if id doesn't exist)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("DELETE /tasks/{} - Deleting task", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();  // 204
    }

    /**
     * PATCH /tasks/{id}/complete
     * Mark a task as DONE (partial update — status only).
     *
     * Response: 200 OK with updated task
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> markComplete(@PathVariable Long id) {
        log.info("PATCH /tasks/{}/complete - Marking task as DONE", id);
        return ResponseEntity.ok(taskService.markTaskComplete(id));  // 200
    }
}
