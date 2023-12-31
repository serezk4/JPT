package com.serezka.jpt.database.repository;

import com.serezka.jpt.database.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByChatId(Long chatId);

    boolean existsByUsernameOrChatId(String username, Long chatId);
    boolean existsByChatId(long chatId);

    List<User> findAll();
    List<User> findByRole(User.Role role);

    long countByRole(User.Role role);
}
