package com.example.ordermanagement.security;

import com.example.ordermanagement.user.User;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.util.Utils;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class TotpService {

    private static final String ISSUER = "Order Management";

    private final SecretGenerator secretGenerator;
    private final CodeVerifier codeVerifier;
    private final QrGenerator qrGenerator;

    public TotpService(
            SecretGenerator secretGenerator,
            CodeVerifier codeVerifier,
            QrGenerator qrGenerator
    ) {
        this.secretGenerator = secretGenerator;
        this.codeVerifier = codeVerifier;
        this.qrGenerator = qrGenerator;
    }

    public String generateSecret() {
        return secretGenerator.generate();
    }

    public String generateQrCode(
            String email,
            String secret
    ) throws QrGenerationException {

        QrData qrData = new QrData.Builder()
                .label(email)
                .secret(secret)
                .issuer(ISSUER)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        byte[] imageData = qrGenerator.generate(qrData);

        return Utils.getDataUriForImage(
                imageData,
                qrGenerator.getImageMimeType()
        );
    }

    public boolean verifyCode(
            String secret,
            String code
    ) {

        return codeVerifier.isValidCode(
                secret,
                code
        );
    }
}