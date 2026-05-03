package com.example.blogai.Service;

import com.example.blogai.Repository.RecoveryCodeRepository;
import com.example.blogai.entities.RecoveryCode;
import com.example.blogai.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecoveryCodeService {

    private final RecoveryCodeRepository recoveryCodeRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int CODE_COUNT = 8;

    // Generate 8 mã recovery khi bật 2FA
    // Trả về plain text để hiển thị cho user 1 lần duy nhất
    @Transactional
    public List<String> generateRecoveryCodes(User user) {
        // Xoá các mã cũ nếu có
        recoveryCodeRepository.deleteByUserId(user.getId());

        List<String> plainCodes = new ArrayList<>();

        for (int i = 0; i < CODE_COUNT; i++) {
            // Format: XXXX-XXXX-XXXX dễ đọc
            String code = generateReadableCode();
            plainCodes.add(code);

            RecoveryCode recoveryCode = RecoveryCode.builder()
                    .user(user)
                    .codeHash(passwordEncoder.encode(code)) // hash trước khi lưu
                    .used(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            recoveryCodeRepository.save(recoveryCode);
        }

        return plainCodes;
    }

    // Verify recovery code khi login
    @Transactional
    public boolean verifyAndConsume(User user, String inputCode) {
        List<RecoveryCode> activeCodes =
                recoveryCodeRepository.findByUserIdAndUsedFalse(user.getId());

        for (RecoveryCode rc : activeCodes) {
            if (passwordEncoder.matches(inputCode.trim(), rc.getCodeHash())) {
                // Đánh dấu đã dùng — mỗi code chỉ dùng được 1 lần
                rc.setUsed(true);
                recoveryCodeRepository.save(rc);
                return true;
            }
        }
        return false;
    }

    public long countRemaining(UUID userId) {
        return recoveryCodeRepository.findByUserIdAndUsedFalse(userId).size();
    }

    private String generateReadableCode() {
        SecureRandom random = new SecureRandom();
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // bỏ 0,O,I,1 dễ nhầm
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            if (i == 4 || i == 8) sb.append("-");
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString(); // VD: ABCD-EFGH-JKLM
    }
}
