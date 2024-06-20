package com.jet.com.secureappback.services;

import com.jet.com.secureappback.enums.VerificationType;

/**
 * @project secureCapita
 * @version 1.0
 * @author MENDJIJET
 * @since 19/06/2024
 */
public interface EmailService {
    void sendVerificationEmail(String firstName, String email, String verificationUrl, VerificationType verificationType);
}
