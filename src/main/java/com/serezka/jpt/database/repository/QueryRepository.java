package com.serezka.jpt.database.repository;

import com.serezka.jpt.database.model.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryRepository  extends JpaRepository<Query, Long> {
    List<Query> findAllByUserId(Long userId);

    List<Query> findAllByUserIdAndChat(Long userId, Long chatId);

    long countAllByUserId(Long userId);
}
