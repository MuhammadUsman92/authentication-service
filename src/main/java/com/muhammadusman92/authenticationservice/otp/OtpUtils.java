package com.muhammadusman92.authenticationservice.otp;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import com.muhammadusman92.authenticationservice.entity.Otp;
import com.muhammadusman92.authenticationservice.repo.OtpRepository;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OtpUtils {
    @Autowired
    private OtpRepository otpRepository;
    private static final int EXPIRATION_TIME_IN_MINUTES = 5;
    private static final int OTP_LENGTH = 6;
    private static final RandomStringGenerator OTP_GENERATOR =
            new RandomStringGenerator.Builder()
                    .withinRange('0', '9')
                    .filteredBy(CharacterPredicates.DIGITS)
                    .build();

    public OtpDetails generateOtp(String email) {
        String otpNumber = OTP_GENERATOR.generate(OTP_LENGTH);
        long expiryTimeMillis = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(EXPIRATION_TIME_IN_MINUTES);
        String base32Secret = Base64.getEncoder().encodeToString(new SecureRandom().generateSeed(20));

        OtpDetails otpDetails = new OtpDetails();
        otpDetails.setOtp(otpNumber);
        otpDetails.setEmail(email);
        otpDetails.setExpiryTimeMillis(expiryTimeMillis);
        otpDetails.setBase32Secret(base32Secret);
        Otp otp = new Otp();
        otp.setOtp(otpDetails.getOtp());
        otp.setEmail(email);
        otp.setExpiryTimeMillis(expiryTimeMillis);
        otp.setBase32Secret(base32Secret);
        if(otpRepository.existsByEmail(email)){
            Otp otpObj = otpRepository.findByEmail(email);
            otp.setId(otpObj.getId());
            otpRepository.save(otp);
        }else{
            otpRepository.save(otp);
        }
        return otpDetails;
    }
}
