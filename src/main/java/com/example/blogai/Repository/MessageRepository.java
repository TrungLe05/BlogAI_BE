package com.example.blogai.Repository;

import com.example.blogai.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    @Query("""
    SELECT m FROM Message m
    WHERE m.conversation.id = :convId
    ORDER BY m.createdAt DESC
    LIMIT 1
""")
    Optional<Message> findLastMessage(@Param("convId") UUID convId);

    @Query("""
    SELECT COUNT(m) FROM Message m
    WHERE m.conversation.id = :convId
    AND m.sender.id != :userId
    AND m.createdAt > (
        SELECT cp.lastReadAt FROM ConversationParticipant cp
        WHERE cp.conversation.id = :convId
        AND cp.user.id = :userId
    )
""")
    int countUnread(@Param("convId") UUID convId, @Param("userId") UUID userId);
}
