package com.serezka.jpt.database.service.authorization;

import com.serezka.jpt.database.model.authorization.InviteCode;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import com.serezka.jpt.database.repository.authorization.InviteCodeRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InviteCodeService {
    InviteCodeRepository inviteCodeRepository;

    @Transactional
    public InviteCode save(InviteCode inviteCode) {
        return inviteCodeRepository.save(inviteCode);
    }

    @Transactional
    public boolean existsByCode(String code) {
        if (!inviteCodeRepository.existsByCode(code)) return false;

        InviteCode curr = inviteCodeRepository.findByCode(code);
        curr.setUsageCount(curr.getUsageCount()-1);
        inviteCodeRepository.save(curr);

        return true;
    }

    @Transactional
    public boolean deleteById(long id) {
        return inviteCodeRepository.deleteById(id);
    }
}
