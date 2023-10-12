package com.cursojava.todolist.task.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cursojava.todolist.task.model.TaskModel;
import java.util.List;


public interface ITaskRepository extends JpaRepository<TaskModel,UUID> {

    List<TaskModel> findByIdUser(UUID idUser);
    
}
