package com.serezka.jpt.database.service;

import com.serezka.telegrambots.database.model.User;
import com.serezka.telegrambots.database.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;

    @Transactional
    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public Optional<User> findByChatId(long chatId) {
        return userRepository.findByChatId(chatId);
    }

    @Transactional
    public Optional<User> save(User user) {
        return Optional.of(userRepository.save(user));
    }

    @Transactional
    public boolean existsByUsernameOrChatId(String username, long chatId) {
        return userRepository.existsByUsernameOrChatId(username, chatId);
    }

    @Transactional
    public boolean existsByChatId(long chatId) {
        return userRepository.existsByChatId(chatId);
    }

    @Transactional
    public List<User> findByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    @Transactional
    public long countByRole(User.Role role) {
        return userRepository.countByRole(role);
    }

    @Transactional
    public void deleteById(long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void delete(User user) {
        userRepository.delete(user);
    }
}
