package com.cursojava.todolist.task.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cursojava.todolist.task.model.TaskModel;
import com.cursojava.todolist.task.repository.ITaskRepository;
import com.cursojava.todolist.task.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel task, HttpServletRequest request){
        task.setIdUser((UUID) request.getAttribute("idUser"));

        if (task.getStartAt().isBefore(LocalDateTime.now())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início da tarefa deve ser maior que a data atual");
        }

        if (task.getEndAt().isBefore(LocalDateTime.now())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de término da tarefa deve ser maior que a data atual");
        }

        if (task.getStartAt().isAfter(task.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de término da tarefa deve ser maior que a data inicial");
        }

        var newTask = this.taskRepository.save(task);
        if (newTask == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao criar nova task");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
    }

    @GetMapping("/")
    public ResponseEntity listAll(HttpServletRequest request){
        var userId = (UUID) request.getAttribute("idUser");
        var taskList = taskRepository.findByIdUser(userId);
        if (taskList.size() <= 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("A busca não retorou resultados");
        }
        return ResponseEntity.status(HttpStatus.OK).body(taskList);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel task, @PathVariable UUID id, HttpServletRequest request){
        var taskToUpdate = taskRepository.findById(id).orElse(null);
        var userId = (UUID) request.getAttribute("idUser");

        if (taskToUpdate != null){
            if (!taskToUpdate.getIdUser().equals(userId)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("O usuário não tem autorização para alterar esta task");
            } else {
                Utils.copyNonNullProperties(task, taskToUpdate);
                taskRepository.save(taskToUpdate);
                return ResponseEntity.status(HttpStatus.OK).body(taskToUpdate);
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task não encontrada");
        }
    }

}
