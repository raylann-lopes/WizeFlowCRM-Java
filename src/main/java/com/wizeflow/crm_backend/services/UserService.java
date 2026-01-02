package com.wizeflow.crm_backend.services;

import com.wizeflow.crm_backend.infrastructure.entity.User;
import com.wizeflow.crm_backend.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService (UserRepository repository) {
        this.repository = repository;
    }

    // Salva Usuario
    public void saveUser (User user){
        repository.saveAndFlush(user);
    }

    // Busca Usuarios por Email
    public User searchUserByEmail (String email) {
        return repository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Email não encontrado")
        );
    }

    // Deleta Usuario
    public void deleteByEmail(String email) {
        repository.deleteByEmail(email);
    }

    public void updateUserByEmail(String email, User user) {
        User userEntity = searchUserByEmail(email);
        User userUpdate = User.builder()
                .id(userEntity.getId())
                .name(user.getName() != null ? user.getName() : userEntity.getName())
                .email(email)
                .password(user.getPassword() != null ? user.getPassword() : userEntity.getPassword())
                .role(user.getRole() != null ? user.getRole() : userEntity.getRole())
                .status(user.getStatus() != null ? user.getStatus() : userEntity.getStatus())
                .build();
        saveUser(userUpdate);
    }

}
