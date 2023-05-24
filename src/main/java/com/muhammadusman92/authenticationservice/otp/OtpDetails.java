package com.muhammadusman92.authenticationservice.otp;

public class OtpDetails {
    private String otp;
    private String email;
    private long expiryTimeMillis;
    private String base32Secret;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getExpiryTimeMillis() {
        return expiryTimeMillis;
    }

    public void setExpiryTimeMillis(long expiryTimeMillis) {
        this.expiryTimeMillis = expiryTimeMillis;
    }

    public String getBase32Secret() {
        return base32Secret;
    }

    public void setBase32Secret(String base32Secret) {
        this.base32Secret = base32Secret;
    }
}
