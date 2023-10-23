package com.serezka.jpt.database.service;

import com.serezka.jpt.database.model.Subscription;
import com.serezka.jpt.database.repository.SubscriptionRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SubscriptionService {
    SubscriptionRepository subscriptionRepository;

    @Transactional
    public List<Subscription> findAll() {
        return subscriptionRepository.findAll();
    }

    @Transactional
    public Subscription save(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Optional<Subscription> findById(Long id) {
        return subscriptionRepository.findById(id);
    }
}
