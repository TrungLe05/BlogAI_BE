ALTER TABLE conversations
    ADD COLUMN participant_a UUID REFERENCES users(id) ON DELETE CASCADE,
    ADD COLUMN participant_b UUID REFERENCES users(id) ON DELETE CASCADE;

CREATE UNIQUE INDEX uq_conversation_participants
    ON conversations (
                      LEAST(participant_a::text, participant_b::text),
                      GREATEST(participant_a::text, participant_b::text)
        );