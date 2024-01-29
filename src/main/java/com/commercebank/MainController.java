package com.commercebank;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("")
    public String firstPage(HttpSession session){
        if(session.getAttribute("account") != null)
            return "redirect:/dashboard";
        else
            return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model, HttpSession session, HttpServletRequest request){
        boolean isChecked;
        if(session.getAttribute("account") != null)
            return "redirect:/dashboard";
        else{
            isChecked=false;
            Cookie[] cookies = request.getCookies();
            String useremail = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("useremail".equals(cookie.getName())) {
                        useremail = cookie.getValue();
                        isChecked = true;
                        break;
                    }
                }
            }
            model.addAttribute("isChecked", isChecked);
            model.addAttribute("useremail",useremail);
            return "login";
        }

    }
}
