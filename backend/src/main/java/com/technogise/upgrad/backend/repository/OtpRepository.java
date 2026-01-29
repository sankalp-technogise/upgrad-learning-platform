package com.technogise.upgrad.backend.repository;

import com.technogise.upgrad.backend.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<OtpVerification, java.util.UUID> {
  java.util.Optional<OtpVerification> findFirstByEmailOrderByCreatedAtDesc(String email);

  java.util.List<OtpVerification> findAllByEmailAndVerifiedFalse(String email);

  java.util.List<OtpVerification> findByEmailAndCreatedAtAfterOrderByCreatedAtAsc(
      String email, java.time.LocalDateTime createdAt);
}
