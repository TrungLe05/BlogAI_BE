package com.example.blogai.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "conversations", schema = "public")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_a", nullable = false)
    private User participantA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_b", nullable = false)
    private User participantB;

    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;


}