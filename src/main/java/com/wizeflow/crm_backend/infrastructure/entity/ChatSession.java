package com.wizeflow.crm_backend.infrastructure.entity;

import com.wizeflow.crm_backend.enums.ChatSessionChannel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chat_session", indexes = {
        @Index(name = "idx_chat_session_company_id", columnList = "company_id"),
        @Index(name = "idx_chat_session_client_id", columnList = "client_id"),
})
@Entity
public class ChatSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    @Builder.Default
    private ChatSessionChannel chatSessionChannel = ChatSessionChannel.WHATSAPP;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "chatSession")
    private List<Lead> leads = new ArrayList<>();

    @OneToMany(mappedBy = "chatSession")
    private List<Appointment> appointments = new ArrayList<>();


}
