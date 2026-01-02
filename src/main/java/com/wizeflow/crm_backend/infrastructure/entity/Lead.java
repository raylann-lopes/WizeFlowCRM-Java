package com.wizeflow.crm_backend.infrastructure.entity;

import com.wizeflow.crm_backend.enums.Source;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "lead")
@Entity
public class Lead {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Company company;

    @Column(name = "name", nullable = false)
    @Setter(AccessLevel.NONE)
    private String name;

    @Column(name = "business_name")
    @Setter(AccessLevel.NONE)
    private String bussinesName;

    @Builder.Default
    @Column(name = "status")
    private String status = "Novo";

    @Column(name = "description")
    private String description;

    @Column(name = "value")
    private Float value;

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

    @Enumerated
    @Column(name = "source")
    private Source source;

    @Column(name = "createdAt", updatable = false)
    private OffsetDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private ChatSession chatSession;

    @PrePersist
    @PreUpdate
    private void preencherDadosDoCliente() {
        if (client != null) {
            name = client.getName();
            bussinesName = client.getBusinessName();
            email = client.getEmail();
            phone = client.getPhone();
            company = client.getCompanyRelation();
        }
    }
}
