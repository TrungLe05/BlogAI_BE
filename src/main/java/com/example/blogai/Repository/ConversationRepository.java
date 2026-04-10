package com.example.blogai.Repository;

import com.example.blogai.entities.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    @Query("""
        SELECT c FROM Conversation c
        WHERE (c.participantA.id = :a AND c.participantB.id = :b)
           OR (c.participantA.id = :b AND c.participantB.id = :a)
    """)
    Optional<Conversation> findByParticipants(
            @Param("a") UUID a, @Param("b") UUID b);

    @Query("""
    SELECT c FROM Conversation c
    JOIN FETCH c.participantA
    JOIN FETCH c.participantB
    WHERE c.participantA.id = :userId
       OR c.participantB.id = :userId
""")
    List<Conversation> findByParticipantId(@Param("userId") UUID userId);
}
