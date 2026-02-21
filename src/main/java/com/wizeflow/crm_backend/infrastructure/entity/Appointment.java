package com.wizeflow.crm_backend.infrastructure.entity;

import com.wizeflow.crm_backend.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "appointments", indexes = {
        @Index(name = "idx_appointments_companies_id", columnList = "companies_id"),
        @Index(name = "idx_appointments_companies_date_id", columnList = "companies_id, start_time"),
        @Index(name = "idx_appointments_session_id", columnList = "session_id"),


})
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companies_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;

    @Enumerated (EnumType.STRING)
    @Builder.Default
    @Column(name = "appointment_status")
    private AppointmentStatus appointmentStatus = AppointmentStatus.SCHEDULE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private ChatSession chatSession;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void validateDates() {
        if (this.endTime != null && this.startTime != null && this.endTime.isBefore(this.startTime)) {
            throw new IllegalArgumentException("A data final não pode ser anterior à data inicial.");
        }
    }

}