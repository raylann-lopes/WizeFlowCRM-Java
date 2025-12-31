package com.wizeflow.crm_backend.insfrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer Id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "nome")
    private String nome;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "status")
    private String status;
}
