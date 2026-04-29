package com.example.blogai.Controller;

import com.example.blogai.Service.TwoFactorService;
import com.example.blogai.dtos.request.ConfirmTotpRequest;
import com.example.blogai.dtos.response.ApiResponse;
import com.example.blogai.dtos.response.TotpSetupResponse;
import com.example.blogai.entities.User;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/2fa")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TwoFactorController {

    TwoFactorService twoFactorService;

    @PostMapping("/enable")
    public ApiResponse<TotpSetupResponse> enable(@AuthenticationPrincipal User user) {
        return ApiResponse.<TotpSetupResponse>builder()
                .result(twoFactorService.enable2FA(user.getId()))
                .build();
    }

    @PostMapping("/confirm")
    public ApiResponse<Void> confirm(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ConfirmTotpRequest req
    ) {
        twoFactorService.confirmSetup(user.getId(), req.getOtpCode());
        return ApiResponse.<Void>builder()
                .message("Bật 2FA thành công")
                .build();
    }

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

    @GetMapping("/status")
    public ApiResponse<Boolean> get2FAStatus(@AuthenticationPrincipal User user){
        return ApiResponse.<Boolean>builder()
                .result(twoFactorService.get2FAStatus(user.getId()))
                .build();
    }
}
