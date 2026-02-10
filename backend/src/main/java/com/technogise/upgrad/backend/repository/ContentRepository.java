package com.technogise.upgrad.backend.repository;

import com.technogise.upgrad.backend.entity.Content;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<Content, UUID> {
  List<Content> findByCategoryIn(List<String> categories);

  List<Content> findByCategoryNotIn(List<String> categories);
}
