package com.example.PersonalFinanceAdvisor.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    //  Store OTP for each email (key = email, value = otp) temporarily
    private Map<String, String> otpStore = new HashMap<>();

    public String generateOtpForEmail(String email){
        Random random = new Random();
        int number = 100000+random.nextInt(900000);
        String otp = String.valueOf(number);
        otpStore.put(email,otp);// save OTP in map against email
        return otp;
    }
    public boolean verifyOtpForEmail(String email, String otpInput) {
        String realOtp = otpStore.get(email);// get stored otp for this email
        if (realOtp == null) {
            return false;
        }
        boolean match = realOtp.equals(otpInput);

        if (match) {
            otpStore.remove(email);  // remove OTP after success
        }

        return match;
    }
}
