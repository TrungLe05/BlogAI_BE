package com.example.blogai.Controller;

import com.example.blogai.Exception.AppException;
import com.example.blogai.Service.RecoveryCodeService;
import com.example.blogai.Service.TotpService;
import com.example.blogai.Service.TwoFactorService;
import com.example.blogai.dtos.request.ConfirmTotpRequest;
import com.example.blogai.dtos.request.RecoveryCodeRequest;
import com.example.blogai.dtos.response.ApiResponse;
import com.example.blogai.dtos.response.TotpSetupResponse;
import com.example.blogai.dtos.response.TwoFAConfirmResponse;
import com.example.blogai.entities.User;
import com.example.blogai.enums.ErrorCode;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/2fa")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TwoFactorController {

    TwoFactorService twoFactorService;

    TotpService totpService;

    RecoveryCodeService recoveryCodeService;

    //enable 2FA
    @PostMapping("/enable")
    public ApiResponse<TotpSetupResponse> enable(@AuthenticationPrincipal User user) {
        return ApiResponse.<TotpSetupResponse>builder()
                .result(twoFactorService.enable2FA(user.getId()))
                .build();
    }

    // confirm otpCode 2FA
    @PostMapping("/confirm")
    public ApiResponse<TwoFAConfirmResponse> confirm(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ConfirmTotpRequest req
    ) {
        return ApiResponse.<TwoFAConfirmResponse>builder()
                .result(twoFactorService.confirmSetup(user.getId(), req.getOtpCode()))
                .build();
    }

    //disable 2FA
    @PostMapping("/disable")
    public ApiResponse<Void> disable(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ConfirmTotpRequest req
    ) {
        twoFactorService.disable(user.getId(), req.getOtpCode());
        return ApiResponse.<Void>builder()
                .message("Tắt 2FA thành công")
                .build();
    }

    // get 2FA status
    @GetMapping("/status")
    public ApiResponse<Boolean> get2FAStatus(@AuthenticationPrincipal User user){
        return ApiResponse.<Boolean>builder()
                .result(twoFactorService.get2FAStatus(user.getId()))
                .build();
    }

    // regenerate recovery code when user lose device
    @PostMapping("/recovery-codes/regenerate")
    public ApiResponse<List<String>> regenerateRecoveryCodes(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid ConfirmTotpRequest req
    ) {
        if (!totpService.verifyCode(currentUser.getTotpSecret(), req.getOtpCode())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        List<String> newCodes = recoveryCodeService.generateRecoveryCodes(currentUser);
        return ApiResponse.<List<String>>builder()
                .result(newCodes)
                .build();
    }

    // get remaining recovery codes
    @GetMapping("/recovery-codes/count")
    public ApiResponse<Long> getRemainingCount(
            @AuthenticationPrincipal User currentUser
    ) {
        return ApiResponse.<Long>builder()
                .result(recoveryCodeService.countRemaining(currentUser.getId()))
                .build();
    }

    // disable 2FA with recovery code when user lose device
    @PostMapping("/disable-with-recovery")
    public ApiResponse<Void> disableWithRecoveryCode(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid RecoveryCodeRequest req
    ) {
        twoFactorService.disableWithRecoveryCode(currentUser.getId(), req.getRecoveryCode());
        return ApiResponse.<Void>builder()
                .message("2FA disabled successfully.")
                .build();
    }
}
