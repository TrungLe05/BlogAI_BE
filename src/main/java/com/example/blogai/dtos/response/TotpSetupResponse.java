package com.example.blogai.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TotpSetupResponse {
    String qrCodeBase64;
    String totpSecret;
}
