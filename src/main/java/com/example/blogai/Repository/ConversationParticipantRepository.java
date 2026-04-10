package com.example.blogai.Repository;

import com.example.blogai.entities.ConversationParticipant;
import com.example.blogai.entities.ConversationParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, ConversationParticipantId> {

}
