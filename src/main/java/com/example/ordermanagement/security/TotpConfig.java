package com.example.ordermanagement.security;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TotpConfig {

    @Bean
    public SecretGenerator secretGenerator() {
        return new DefaultSecretGenerator(32);
    }

    @Bean
    public TimeProvider timeProvider() {
        return new SystemTimeProvider();
    }

    @Bean
    public CodeGenerator codeGenerator() {
        return new DefaultCodeGenerator(
                HashingAlgorithm.SHA1,
                6
        );
    }

    @Bean
    public CodeVerifier codeVerifier(
            CodeGenerator codeGenerator,
            TimeProvider timeProvider
    ) {

        DefaultCodeVerifier verifier =
                new DefaultCodeVerifier(
                        codeGenerator,
                        timeProvider
                );

        verifier.setTimePeriod(30);

        // Accept the immediately previous/next time window.
        verifier.setAllowedTimePeriodDiscrepancy(1);

        return verifier;
    }

    @Bean
    public QrGenerator qrGenerator() {
        ZxingPngQrGenerator generator =
                new ZxingPngQrGenerator();

        generator.setImageSize(300);

        return generator;
    }
}