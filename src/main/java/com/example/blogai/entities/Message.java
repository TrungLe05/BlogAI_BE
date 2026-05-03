package com.example.blogai.entities;

import com.example.blogai.enums.MessageType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "messages", schema = "public", indexes = {
        @Index(name = "idx_messages_conv_created",
                columnList = "conversation_id, created_at"),
        @Index(name = "idx_messages_sender",
                columnList = "sender_id")})
@ToString
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MessageType type = MessageType.TEXT;
    @NotNull

    @Column(name = "content", length = Integer.MAX_VALUE)
    private String content; // null nếu là file

    // ✅ thêm file fields
    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt = Instant.now();


}