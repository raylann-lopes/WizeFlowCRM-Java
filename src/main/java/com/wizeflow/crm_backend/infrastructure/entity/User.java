package com.wizeflow.crm_backend.infrastructure.entity;

import com.wizeflow.crm_backend.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "status")
    private String status;

    @Column(name = "cpf")
    private String cpf;

    @Column(name = "phone")
    private String phone;

    @CreationTimestamp
    @Column(name = "createDat", updatable = false)
    private LocalDateTime createDat;

    @UpdateTimestamp
    @Column(name = "updateDat")
    private LocalDateTime updateDat;

    @Column(name = "sessionID")
    private String sessionID;

    @Column(name = "department")
    private String department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

}
