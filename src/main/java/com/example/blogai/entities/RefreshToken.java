package com.example.blogai.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "refresh_tokens", schema = "public", indexes = {
        @Index(name = "idx_refresh_tokens_user",
                columnList = "user_id"),
        @Index(name = "idx_refresh_tokens_token",
                columnList = "token"),
        @Index(name = "idx_refresh_tokens_expires",
                columnList = "expires_at")}, uniqueConstraints = {@UniqueConstraint(name = "refresh_tokens_token_key",
        columnNames = {"token"})})
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Size(max = 512)
    @NotNull
    @Column(name = "token", nullable = false, length = 512)
    private String token;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "revoked", nullable = false)
    private Boolean revoked;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


}