package com.example.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Task Manager API.
 * @SpringBootApplication enables:
 *   - @Configuration    (Spring config)
 *   - @EnableAutoConfiguration (auto-wire beans)
 *   - @ComponentScan    (scan this package for controllers, services etc.)
 */
@SpringBootApplication
public class TaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }
}
