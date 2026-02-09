package com.technogise.upgrad.backend.repository;

import com.technogise.upgrad.backend.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  @Modifying
  @Query("UPDATE User u SET u.onboardingCompleted = true WHERE u.id = :userId")
  void markOnboardingCompleted(@Param("userId") UUID userId);
}
