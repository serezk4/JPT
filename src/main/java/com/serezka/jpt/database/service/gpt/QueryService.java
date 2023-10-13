package com.serezka.jpt.database.service.gpt;

import com.serezka.jpt.database.model.gpt.Query;
import com.serezka.jpt.database.repository.gpt.QueryRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QueryService {
    QueryRepository queryRepo;

    @Transactional
    public Query save(Query query) {
        return queryRepo.save(query);
    }

    @Transactional
    public List<Query> findAllByUserId(Long userId) {
        return queryRepo.findAllByUserId(userId);
    }

    @Transactional
    public Optional<Query> findById(Long queryId) {
        return queryRepo.findById(queryId);
    }

    @Transactional
    public List<Query> findAllByUserIdAndChat(Long userId, Long chat) {
        return queryRepo.findAllByUserIdAndChat(userId, chat);
    }

    @Transactional
    public long countAllByUserId(Long userId) {
        return queryRepo.countAllByUserId(userId);
    }
}
