package com.wizeflow.crm_backend.services;

import com.wizeflow.crm_backend.insfrastructure.entity.User;
import com.wizeflow.crm_backend.insfrastructure.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService (UserRepository repository) {
        this.repository = repository;
    }
    public void saveUser (User user){
        repository.saveAndFlush(user);
    }
    public User searchUserByEmail (String email) {
        return repository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Email não encontrado")
        );
    }

}
