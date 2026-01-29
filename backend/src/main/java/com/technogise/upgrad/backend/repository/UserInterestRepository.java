package com.technogise.upgrad.backend.repository;

import com.technogise.upgrad.backend.entity.UserInterest;
import com.technogise.upgrad.backend.entity.UserInterestId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInterestRepository extends JpaRepository<UserInterest, UserInterestId> {
  List<UserInterest> findByIdUserId(UUID userId);

  void deleteByIdUserId(UUID userId);
}
