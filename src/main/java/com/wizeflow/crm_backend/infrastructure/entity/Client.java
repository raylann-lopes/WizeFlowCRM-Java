package com.wizeflow.crm_backend.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "clients", indexes = {
        @Index(name = "idx_client_company_id", columnList = "company_id"),
        @Index(name = "idx_client_company_status", columnList = "company_id, status"),
        @Index(name = "idx_client_session_id", columnList = "session_id")
})

@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company companyRelation;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone", length = 20, nullable = false)
    private String phone;

    @Builder.Default
    @Column(name = "status")
    private String status = "Ativo";

    @Column(name = "notes")
    private String notes;

    @Column(name = "session_id")
    private String sessionId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatSession> chatSessions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Lead> leads = new ArrayList<>();
}
