package com.technogise.upgrad.backend.repository;

import com.technogise.upgrad.backend.entity.WatchHistory;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchHistoryRepository extends JpaRepository<WatchHistory, UUID> {
  Optional<WatchHistory> findTopByUserIdAndProgressPercentLessThanOrderByLastWatchedAtDesc(
      UUID userId, int maxProgress);
}
