package com.wizeflow.crm_backend.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "company", indexes = {
        @Index(name = "idx_company_id", columnList = "id"),
        @Index(name = "idx_company_slug", columnList = "slug"),
        @Index(name = "idx_company_status", columnList = "status"),
        @Index(name = "idx_company_plan", columnList = "plan"),
        @Index(name = "idx_company_session_id", columnList = "session_id")
})
@Entity
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", unique = true, nullable = false, length = 100)
    private String slug;

    @Column(name = "phone", nullable = false, length = 11)
    private String phone;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb") // Use "json" se for MySQL, "jsonb" para Postgres
    private Map<String, Object> settings;

    @Column(name = "plan", length = 50)
    private String plan = "free";

    @Column(name = "status")
    private String status = "active";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatSession> chatSessions = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Client> clients = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<History> histories = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lead> leads = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

}
