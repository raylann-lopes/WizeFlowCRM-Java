package com.wizeflow.crm.backend.infrastructure.entity;

import com.wizeflow.crm.backend.enums.Role;
import com.wizeflow.crm.backend.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users",
        uniqueConstraints = {
            @UniqueConstraint(name = "users_companies_email_key", columnNames = {"companies_id", "email"})
        },
        indexes = {
                @Index(name = "idx_users_companies_id", columnList = "companies_id"),
                @Index(name = "idx_users_email", columnList = "email"),
                @Index(name = "idx_users_session_id", columnList = "session_id"),
                @Index(name = "idx_users_role", columnList = "role")
        }
)
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @Column(name = "cpf", unique = true)
    private String cpf;

    @Column(name = "phone")
    private String phone;

    @Column(name = "job_title")
    private String jobTitle;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "department")
    private String department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companies_id", nullable = true)
    private Company company;

}
