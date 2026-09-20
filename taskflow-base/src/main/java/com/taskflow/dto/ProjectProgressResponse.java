package com.taskflow.dto;

/**
 * DTO de salida para el reporte de progreso por proyecto.
 */
public record ProjectProgressResponse(Long projectId, String projectName, long totalTasks, long doneTasks, double percentDone) {
}
