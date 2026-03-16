package com.example.blogai.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class ConversationParticipantId implements Serializable {
    private static final long serialVersionUID = -6648023285707295890L;
    @NotNull
    @Column(name = "conversation_id", nullable = false)
    private UUID conversationId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;


}