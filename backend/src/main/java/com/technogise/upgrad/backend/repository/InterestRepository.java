package com.technogise.upgrad.backend.repository;

import com.technogise.upgrad.backend.entity.Interest;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestRepository extends JpaRepository<Interest, UUID> {
  List<Interest> findAllByOrderByDisplayOrderAsc();
}
