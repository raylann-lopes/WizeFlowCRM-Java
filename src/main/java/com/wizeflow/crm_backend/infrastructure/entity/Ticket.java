package com.wizeflow.crm_backend.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table (name = "tickets", indexes = {
        @Index(name = "idx_tickets_companies_id", columnList = "companies_id"),
        @Index(name = "idx_tickets_user_id", columnList = "user_id"),
        @Index(name = "idx_tickets_companies_status", columnList = "companies_id, status")
})

public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "companies_id", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column (name = "subject")
    private String subject;

    @Column (name = "department")
    private String department;

    @Builder.Default
    @Column (name = "urgency")
    private String urgency = "Baixa";

    @Builder.Default
    @Column (name = "status")
    private String status = "Aberto";

    @Column (name = "message", columnDefinition = "TEXT")
    private String message;

    @CreationTimestamp
    @Column (name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column (name = "updated_at")
    private OffsetDateTime updatedAt;

}
