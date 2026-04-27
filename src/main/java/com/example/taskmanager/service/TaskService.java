package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TaskService contains ALL business logic for the Task Manager.
 *
 * Layer role:
 *   Controller → Service → Repository → Database
 *
 * The Controller handles HTTP (request/response).
 * The Service handles BUSINESS LOGIC (what to do with the data).
 * The Repository handles DATABASE operations (how to store/retrieve data).
 *
 * @Service → Spring registers this as a bean and injects it into the Controller.
 */
@Service
public class TaskService {

    // SLF4J logger for structured, production-ready logging
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    // Spring injects TaskRepository via constructor injection (best practice)
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // ─────────────────────────────────────────────────
    // CREATE a new task
    // ─────────────────────────────────────────────────
    public TaskResponse createTask(TaskRequest request) {
        log.info("Creating new task with title: '{}'", request.getTitle());

        Task task = new Task(request.getTitle(), request.getDescription());
        Task saved = taskRepository.save(task);  // INSERT into DB

        log.info("Task created successfully with id: {}", saved.getId());
        return TaskResponse.from(saved);
    }

    // ─────────────────────────────────────────────────
    // GET ALL tasks
    // ─────────────────────────────────────────────────
    public List<TaskResponse> getAllTasks() {
        log.info("Fetching all tasks");

        return taskRepository.findAll()          // SELECT * FROM tasks
                .stream()
                .map(TaskResponse::from)         // Convert each Task → TaskResponse
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────
    // GET TASK BY ID
    // ─────────────────────────────────────────────────
    public TaskResponse getTaskById(Long id) {
        log.info("Fetching task with id: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found with id: {}", id);
                    return new ResourceNotFoundException("Task not found with id: " + id);
                });

        return TaskResponse.from(task);
    }

    // ─────────────────────────────────────────────────
    // UPDATE TASK
    // ─────────────────────────────────────────────────
    public TaskResponse updateTask(Long id, TaskRequest request) {
        log.info("Updating task with id: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        Task updated = taskRepository.save(task);  // UPDATE in DB
        log.info("Task {} updated successfully", id);
        return TaskResponse.from(updated);
    }

    // ─────────────────────────────────────────────────
    // DELETE TASK
    // ─────────────────────────────────────────────────
    public void deleteTask(Long id) {
        log.info("Deleting task with id: {}", id);

        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        taskRepository.deleteById(id);  // DELETE FROM tasks WHERE id = ?
        log.info("Task {} deleted successfully", id);
    }

    // ─────────────────────────────────────────────────
    // MARK TASK AS COMPLETE
    // ─────────────────────────────────────────────────
    public TaskResponse markTaskComplete(Long id) {
        log.info("Marking task {} as DONE", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        task.setStatus(Task.TaskStatus.DONE);
        Task updated = taskRepository.save(task);

        log.info("Task {} marked as DONE", id);
        return TaskResponse.from(updated);
    }
}
