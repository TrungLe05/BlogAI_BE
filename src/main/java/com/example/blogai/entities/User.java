package com.example.blogai.entities;

import com.example.blogai.enums.AuthProvider;
import com.example.blogai.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users", schema = "public", indexes = {
        @Index(name = "idx_users_email",
                columnList = "email"),
        @Index(name = "idx_users_provider",
                columnList = "provider, provider_id")}, uniqueConstraints = {@UniqueConstraint(name = "users_email_key",
        columnNames = {"email"})})
public class User {
    @Id
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)

    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Size(max = 255)
    @Column(name = "password_hash")
    private String passwordHash;

    @Size(max = 100)
    @NotNull
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "avatar_url", length = Integer.MAX_VALUE)
    private String avatarUrl;


    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "provider", nullable = false)
    private AuthProvider provider = AuthProvider.LOCAL;

    @Size(max = 255)
    @Column(name = "provider_id")
    private String providerId;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.USER;

    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)  // nullable = false giữ constraint ở DB
    @CreationTimestamp
    private Instant createdAt;

    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private Instant updatedAt;

    @ColumnDefault("false")
    @Column(name = "avatar_customized", nullable = false)
    private boolean avatarCustomized = false;
}