package com.commercebank;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LogoutController {

    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate(); // This will clear the session
            }
            return "redirect:/login"; // Redirect to the login page or any other page after logout

    }
}
