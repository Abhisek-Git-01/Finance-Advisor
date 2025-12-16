package com.example.PersonalFinanceAdvisor.service;

import com.example.PersonalFinanceAdvisor.model.User;
import com.example.PersonalFinanceAdvisor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private OtpService otpService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    // store users who are registering but not verified by OTP yet
    private Map<String, User> pendingUsers = new HashMap<>();

    // Save new user
    public User addUser(User user) {
        if (user.getEmail().equals(userRepository.findByEmail(user.getEmail()))) {
            return null;
        } else {
            // save this user temporarily in memory (not in DB yet)
            pendingUsers.put(user.getEmail(), user);
            // generate OTP for this email
            String otp = otpService.generateOtpForEmail(user.getEmail());
            sendSignupOtpEmail(user.getEmail(), user.getName(), otp);
            return user;
        }
    }

    //user enters OTP -> we verify OTP and save to DB
    public boolean completeSignUpAfterOtp(String email, String otpInput) {
        boolean otpOk = otpService.verifyOtpForEmail(email, otpInput);//check OTP is correct
        if (!otpOk) return false;
        User pendingUser = pendingUsers.get(email); // get the user we stored before in pendingUsers
        if (pendingUser == null) return false;
        String encodedPassword = encoder.encode(pendingUser.getPassword());
        pendingUser.setPassword(encodedPassword);
        // confirmPassword is only for UI check; we don't need to store it in DB


        // 4) save user permanently in DB
        userRepository.save(pendingUser);
        sendEmail(email, pendingUser.getName());
        // 5) remove from pending map
        pendingUsers.remove(email);

        return true;
    }

    // helper: send OTP mail for signup
    private void sendSignupOtpEmail(String toEmail, String name, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("abhishekmohapatra768@gmail.com");                       // your sender mail
        message.setTo(toEmail);                                                                    // user email
        message.setSubject("Your Finance Advisor Signup OTP");                   // subject line
        message.setText(
                "Dear " + name + ",\n\n" +
                        "Your OTP to complete registration is: " + otp + "\n\n" +
                        "This OTP is valid for single use.\n\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "Regards,\nFinance Advisor Team"
        );

        mailSender.send(message);                                                               // send email
        System.out.println("Signup OTP sent to " + toEmail + " : " + otp);       // log for debug
    }

    public User verifyUser(String mobNo, String email, String password) {    // Verify user login
        Optional<User> users = userRepository.findByMobNoAndEmail(mobNo, email);
        if (users.isEmpty()) {
            return null;
        }
        User user = users.get();
        if (encoder.matches(password, user.getPassword())) {
      return user;
        }
        return null;
    }

    public void sendEmail(String toEmail, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("abhishekmohapatra768@gmail.com");
        message.setTo(toEmail);
        message.setText(
                "Dear " + name + ",\n\n" +
                        "Welcome to Finance Advisor!\n\n" +
                        "Your account has been successfully created.\n" +
                        "You now have access to personalized financial tools and expert insights to help you make smart and confident decisions.\n\n" +
                        "If you have any questions, feel free to contact us at support@financeadvisor.com.\n\n" +
                        "Thank you for choosing Finance Advisor.\n\n" +
                        "Warm regards,\n" +
                        "Finance Advisor Team"
        );

        message.setSubject("Account Created Successfully");
        mailSender.send(message);
        System.out.println("mail send successfully...");
    }

    public String sendForgotPasswordMail(String email) {
        try {
            String otp = otpService.generateOtpForEmail(email);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Finance Advisor - Password Reset OTP");
            message.setText(
                    "Dear User,\n\n" +
                            "Your OTP for resetting your Finance Advisor account password is: " + otp + "\n\n" +
                            "This OTP is valid for the next 10 minutes.\n\n" +
                            "If you did not request a password reset, please ignore this message.\n\n" +
                            "Regards,\n" +
                            "Finance Advisor Team"
            );
            mailSender.send(message);
            return otp; // send OTP back so you can verify it
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean verifyForgotPasswordOtp(String email, String otpInput) {
        return otpService.verifyOtpForEmail(email, otpInput);
    }

    public boolean updatePassword(String email, String newPassword, String confirmPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return false;   // user not found
        }
        if (!newPassword.equals(confirmPassword)) {      // validate passwords
            return false;   // password mismatch
        }
        String encodedPassword = encoder.encode(newPassword);  // encode new password
         user.setPassword(encodedPassword);
         userRepository.save(user);    // save updated user
         return true;
    }
}
