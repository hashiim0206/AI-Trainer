package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * TaskRepository gives us all standard database operations for FREE.
 *
 * By extending JpaRepository<Task, Long>, Spring automatically provides:
 *   save(task)          → INSERT or UPDATE
 *   findById(id)        → SELECT WHERE id = ?
 *   findAll()           → SELECT * FROM tasks
 *   deleteById(id)      → DELETE WHERE id = ?
 *   existsById(id)      → SELECT COUNT(*)
 *   count()             → SELECT COUNT(*)
 *
 * We do NOT need to write any SQL. Spring Data JPA generates it at runtime.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // No code needed — JpaRepository provides all CRUD methods automatically.
    // You can add custom query methods here later, e.g.:
    // List<Task> findByStatus(Task.TaskStatus status);
}
