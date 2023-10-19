package com.serezka.jpt.database.service.authorization;

import com.serezka.jpt.database.model.authorization.Invite;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import com.serezka.jpt.database.repository.authorization.InviteRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InviteService {
    InviteRepository inviteRepository;

    @Transactional
    public Invite save(Invite invite) {
        return inviteRepository.save(invite);
    }

    @Transactional
    public boolean existsByCode(String code) {
        if (!inviteRepository.existsByCode(code)) return false;

        Invite curr = inviteRepository.findByCode(code);
        curr.setUsageCount(curr.getUsageCount()-1);
        inviteRepository.save(curr);

        return true;
    }

    @Transactional
    public boolean deleteById(long id) {
        return inviteRepository.deleteById(id);
    }
}
