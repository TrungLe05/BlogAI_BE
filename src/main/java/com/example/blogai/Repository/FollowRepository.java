package com.example.blogai.Repository;

import com.example.blogai.entities.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface FollowRepository extends JpaRepository<Follow, UUID> {
    boolean existsByFollowerIdAndFollowingId(UUID followerId, UUID followingId);
    Optional<Follow> findByFollowerIdAndFollowingId(UUID followerId, UUID followingId);
    List<Follow> findByFollowingId(UUID followingId);
    List<Follow> findByFollowerId(UUID followerId);
}
