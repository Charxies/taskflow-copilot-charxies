package com.taskflow.dto;

/**
 * ProjectProgressResponse — contrato de salida de GET /reports/progress.
 * Representa el avance de un proyecto: total de tareas, hechas y porcentaje con un decimal.
 */
public record ProjectProgressResponse(
        Long projectId,
        String projectName,
        long totalTasks,
        long doneTasks,
        double percentDone
) {
}
