package com.wizeflow.crm_backend.infrastructure.entity;

import com.wizeflow.crm_backend.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "appointment")
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "start_time", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime endTime;

    @Enumerated
    @Column(name = "appointment_status")
    private AppointmentStatus appointmentStatus = AppointmentStatus.SCHEDULE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private ChatSession chatSession;

    @CreationTimestamp
    @Column(name = "createdAt")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private OffsetDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void validateDates() {
        if (this.endTime != null && this.startTime != null && this.endTime.isBefore(this.startTime)) {
            throw new IllegalArgumentException("A data final não pode ser anterior à data inicial.");
        }
    }

}