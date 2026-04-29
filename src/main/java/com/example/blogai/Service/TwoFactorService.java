package com.example.blogai.Service;

import com.example.blogai.Exception.AppException;
import com.example.blogai.Repository.UserRepository;
import com.example.blogai.dtos.response.TotpSetupResponse;
import com.example.blogai.entities.User;
import com.example.blogai.enums.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.flywaydb.core.api.callback.Error;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TwoFactorService {

    UserRepository userRepository;

    TotpService totpService;


    // enable 2FA: generate TOTP and QRCode, save totp
    public TotpSetupResponse enable2FA(UUID userId){
        var user = getUser(userId);

        if(user.isTotpVerified()){
            throw new AppException(ErrorCode.TOTP_ALREADY_ENABLED);
        }

        String secret     = totpService.generateSecret();
        String otpAuthUri = totpService.buildOtpauthUri(user.getEmail(), secret);
        String qrBase64   = totpService.generateQrBase64(otpAuthUri);

        user.setTotpSecret(secret);
        userRepository.save(user);

        return TotpSetupResponse.builder()
                .qrCodeBase64(qrBase64)
                .totpSecret(secret)
                .build();
    }

    // QR Confirm: user enter code after scan QR
    public void confirmSetup(UUID userId, String otpCode) {
        User user = getUser(userId);

        if (user.getTotpSecret() == null) {
            throw new AppException(ErrorCode.TOTP_NOT_SETUP);
        }
        if (!totpService.verifyCode(user.getTotpSecret(), otpCode)) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        user.setTotpVerified(true);
        userRepository.save(user);
    }

    // disable 2FA: require enter OTP to confirm before disable 2FA
    public void disable(UUID userId, String otpCode) {
        User user = getUser(userId);

        if (!user.isTotpVerified()) {
            throw new AppException(ErrorCode.TOTP_NOT_ENABLED);
        }
        if (!totpService.verifyCode(user.getTotpSecret(), otpCode)) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        user.setTotpSecret(null);
        user.setTotpVerified(false);
        userRepository.save(user);
    }

    //helper method
    private User getUser(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    public boolean get2FAStatus(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED))
                .isTotpVerified();
    }
}
