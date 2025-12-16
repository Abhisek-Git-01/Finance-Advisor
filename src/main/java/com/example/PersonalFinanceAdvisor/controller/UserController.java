package com.example.PersonalFinanceAdvisor.controller;

import com.example.PersonalFinanceAdvisor.model.User;
import com.example.PersonalFinanceAdvisor.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller

public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/")
    public String homePage() {
        return "home";  // loads home.html
    }
    @PostMapping("/add")
    public String createUser(User user, HttpSession session) {


        User pending = userService.addUser(user);
         session.setAttribute("otpEmail",user.getEmail());
        if (pending == null) {
            return "registrationFail";  // email exists
        }
        return "otp-verify";   // show OTP input page
    }
    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String otp,
            HttpSession session) {

       String email = (String) session.getAttribute("otpEmail");

        boolean ok = userService.completeSignUpAfterOtp(email, otp);

        if (ok) {
            return "registrationSuccess";
        } else {

            return "registration-failed";
        }
    }
    @PostMapping("/send-reset-email")
  public String sendForgotPasswordMail(@RequestParam String email,HttpSession session){
        session.setAttribute("forGotOtpEmail",email);
         userService.sendForgotPasswordMail(email);
         return "forgot-password-otp-verify";
  }
  @PostMapping("/forgotPasswordOtpVerify")
  public String forgotPasswordOtpVerify( @RequestParam String otpInput, HttpSession session){
        String email = (String) session.getAttribute("forGotOtpEmail");
        if(userService.verifyForgotPasswordOtp(email,otpInput)) {
            return "update-password.html";
        }
        return "registration-failed.html";
  }

    @PostMapping("/update-password")
    public String updatePassword(
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session) {

        String email = (String) session.getAttribute("forGotOtpEmail");

        if (email == null) {
            return "redirect:/registration-failed.html";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "redirect:/registration-failed.html";
        }

        boolean updated = userService.updatePassword(email, newPassword,confirmPassword);

        if (updated) {
            return "redirect:/password-update-success.html";
        }

        return "redirect:/registration-failed.html";
    }

    @PostMapping("/login")
    public String verifyUser(
            @RequestParam String mobNo,
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session) {

        User user = userService.verifyUser(mobNo,email,password);
        if (user != null){
            session.setAttribute("loggedInUser", user);
            return "loginSuccess";
        }
        return "loginFailure";
    }

}
