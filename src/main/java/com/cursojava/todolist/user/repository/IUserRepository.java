package com.cursojava.todolist.user.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cursojava.todolist.user.model.UserModel;


public interface IUserRepository extends JpaRepository<UserModel, UUID>{

    UserModel findByUsername(String username);
    
}
