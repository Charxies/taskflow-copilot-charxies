package com.taskflow.unit;

import com.taskflow.dto.ProjectProgressResponse;
import com.taskflow.model.Priority;
import com.taskflow.model.Project;
import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
import com.taskflow.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ProgresoProyectosServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void progresoPorProyecto_calculaCorrectamente() throws Exception {
        Project p1 = new Project(1L, "Plataforma TaskFlow", "d", 1L, LocalDate.now());
        Project p2 = new Project(2L, "App Móvil", "d", 1L, LocalDate.now());
        Project p3 = new Project(3L, "Migración Legacy", "d", 1L, LocalDate.now());

        when(projectRepository.findAll()).thenReturn(List.of(p3, p1, p2)); // return 3,1,2

        List<Task> tareasP1 = List.of(
                new Task(11L, "Task 1", null, TaskStatus.DONE, Priority.LOW, 1L, null, null),
                new Task(12L, "Task 2", null, TaskStatus.TODO, Priority.LOW, 1L, null, null),
                new Task(13L, "Task 3", null, TaskStatus.TODO, Priority.LOW, 1L, null, null),
                new Task(14L, "Task 4", null, TaskStatus.TODO, Priority.LOW, 1L, null, null),
                new Task(15L, "Task 5", null, TaskStatus.TODO, Priority.LOW, 1L, null, null)
        );
        when(taskRepository.findByProjectId(1L)).thenReturn(tareasP1);

        List<Task> tareasP2 = List.of(
                new Task(21L, "Task A", null, TaskStatus.DONE, Priority.LOW, 2L, null, null),
                new Task(22L, "Task B", null, TaskStatus.TODO, Priority.LOW, 2L, null, null),
                new Task(23L, "Task C", null, TaskStatus.TODO, Priority.LOW, 2L, null, null)
        );
        when(taskRepository.findByProjectId(2L)).thenReturn(tareasP2);

        when(taskRepository.findByProjectId(3L)).thenReturn(List.of());

        List<ProjectProgressResponse> resultado = projectService.progresoPorProyecto();

        assertEquals(3, resultado.size());
        // orden por projectId 1,2,3
        assertEquals(1L, resultado.get(0).projectId());
        assertEquals(2L, resultado.get(1).projectId());
        assertEquals(3L, resultado.get(2).projectId());

        assertEquals(20.0, resultado.get(0).percentDone(), 0.0001);
        assertEquals(33.3, resultado.get(1).percentDone(), 0.0001);
        assertEquals(0.0, resultado.get(2).percentDone(), 0.0001);
    }
}
