package com.taskflow.controller;

import com.taskflow.dto.ProjectProgressResponse;
import com.taskflow.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Controller para reportes agregados. */
@RestController
@Tag(name = "Reports", description = "Endpoints para reportes agregados sobre proyectos y tareas")
public class ReportController {

    private final ProjectService projectService;

    public ReportController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Operation(summary = "Avance por proyecto", description = "Devuelve cuántas tareas tiene cada proyecto, cuántas están en DONE y el porcentaje completado.")
    @GetMapping("/reports/progress")
    public List<ProjectProgressResponse> progresoPorProyecto() {
        return projectService.progresoPorProyecto();
    }
}
