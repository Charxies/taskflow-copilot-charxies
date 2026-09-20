package com.taskflow.unit;

import com.taskflow.dto.ProjectProgressResponse;
import com.taskflow.exception.TaskValidationException;
import com.taskflow.model.Priority;
import com.taskflow.model.Project;
import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
import com.taskflow.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/** Unit de ProjectService.progresoPorProyecto: repositorios mockeados, proyectos y tareas reales. */
@ExtendWith(MockitoExtension.class)
class ProgresoProyectosServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService service;

    private final Project p1 = new Project(1L, "Plataforma TaskFlow", "d", 1L, null);
    private final Project p2 = new Project(2L, "App Móvil", "d", 1L, null);
    private final Project p3 = new Project(3L, "Migración Legacy", "d", 1L, null);

    private Task tarea(Long id, TaskStatus status, Long projectId, Long assigneeId) throws TaskValidationException {
        return new Task(id, "Tarea " + id, "d", status, Priority.MED, projectId, assigneeId, null);
    }

    @Test
    void progreso_listaOrdenada_yPorcentajesBienCalculados() throws TaskValidationException {
        // findAll devuelve en orden 3,1,2 (repo puede mezclar). El service debe ordenar 1,2,3.
        when(projectRepository.findAll()).thenReturn(List.of(p3, p1, p2));

        // Proyecto 1: 5 tareas, 1 DONE -> 20.0
        when(taskRepository.findByProjectId(1L)).thenReturn(List.of(
                tarea(1L, TaskStatus.DONE, 1L, 1L),
                tarea(2L, TaskStatus.TODO, 1L, 1L),
                tarea(3L, TaskStatus.TODO, 1L, 1L),
                tarea(4L, TaskStatus.TODO, 1L, 1L),
                tarea(5L, TaskStatus.TODO, 1L, 1L)
        ));

        // Proyecto 2: 3 tareas, 1 DONE -> 33.3 (redondeo a un decimal)
        when(taskRepository.findByProjectId(2L)).thenReturn(List.of(
                tarea(6L, TaskStatus.DONE, 2L, 1L),
                tarea(7L, TaskStatus.TODO, 2L, 1L),
                tarea(8L, TaskStatus.TODO, 2L, 1L)
        ));

        // Proyecto 3: sin tareas
        when(taskRepository.findByProjectId(3L)).thenReturn(List.of());

        List<ProjectProgressResponse> esperado = List.of(
                new ProjectProgressResponse(1L, "Plataforma TaskFlow", 5, 1, 20.0),
                new ProjectProgressResponse(2L, "App Móvil", 3, 1, 33.3),
                new ProjectProgressResponse(3L, "Migración Legacy", 0, 0, 0.0)
        );

        assertEquals(esperado, service.progresoPorProyecto());
    }
}
