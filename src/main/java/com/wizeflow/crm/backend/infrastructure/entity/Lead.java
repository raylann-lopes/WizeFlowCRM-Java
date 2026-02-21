package com.wizeflow.crm.backend.infrastructure.entity;

import com.wizeflow.crm.backend.enums.Source;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "leads", indexes = {
        @Index(name = "idx_leads_companies_id", columnList = "companies_id"),
        @Index(name = "idx_leads_client_id", columnList = "client_id"),
        @Index(name = "idx_leads_companies_status", columnList = "companies_id, status"),
        @Index(name = "idx_leads_session_id", columnList = "session_id")
})
@Entity
public class Lead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companies_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Company company;

    @Column(name = "name", nullable = false)
    @Setter(AccessLevel.NONE)
    private String name;

    @Column(name = "business_name")
    @Setter(AccessLevel.NONE)
    private String businessName;

    @Builder.Default
    @Column(name = "status")
    private String status = "Novo";

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "value", precision = 15, scale = 2)
    private BigDecimal value;

    @Builder.Default
    @Column(name = "is_hot")
    private boolean isHot = false;

    @Builder.Default
    @Column(name = "is_delayed")
    private boolean isDelayed = false;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "industry")
    private String industry;

    @Enumerated(EnumType.STRING)
    @Column(name = "source")
    private Source source;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private ChatSession chatSession;

    @PrePersist
    @PreUpdate
    private void preencherDadosDoCliente() {
        if (client != null) {
            name = client.getName();
            businessName = client.getBusinessName();
            email = client.getEmail();
            phone = client.getPhone();
            company = client.getCompany();
        }
    }
}

