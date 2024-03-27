package com.commercebank;

import com.commercebank.accounts.AccountService;
import com.commercebank.accounts.Accounts;
import com.commercebank.accounts.EmailService;
import com.commercebank.accounts.RandNumGenerator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Optional;
@SessionAttributes({"accounts"})
@Controller
public class LoginController {
    @Autowired private AccountService accountService;
    @Autowired
    private EmailService emailService;
    //private int code;
    private HashMap<String, Integer> codeHM = new HashMap<>();

    @Autowired
    private BCryptPasswordEncoder bEncoder;
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

    @PostMapping("/login")
    public String authenticate(String email, String password, RedirectAttributes ra, Boolean checkBoxRememberUser, HttpServletResponse response, HttpSession session){ //receive email and password from the login form
        Optional<Accounts> account = accountService.getAccountByEmail(email);
        //emailService.sendMultiFacAuthEmail(account.get().getEmail(),4444);
        if(checkBoxRememberUser!=null){ //add usermail cookie
            Cookie cookie = new Cookie("useremail",email);
            cookie.setMaxAge(3600); // Set the expiration time in seconds
            response.addCookie(cookie);
        }else{
            Cookie cookie = new Cookie("useremail",null); // remove useremail cookie
            cookie.setMaxAge(0); // set to 0 to remove
            response.addCookie(cookie);
        }

        if(account.isPresent() && bEncoder.matches(password,account.get().getPassword())){
            Accounts acc = account.get();
            if(acc.isMultifactorAuth())
            {
                int code = RandNumGenerator.generateRandom4DigitCode();
                codeHM.put(acc.getEmail(),code);
                new Thread(() -> {
                    try {
                        Thread.sleep(5*60 * 1000);  // Sleep for 5 minutes
                        codeHM.remove(acc.getEmail());  // remove code (expired)
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                }).start();
                //code = RandNumGenerator.generateRandom4DigitCode();
                emailService.sendMultiFacAuthEmail(acc.getEmail(),code);
                ra.addFlashAttribute("email",acc.getEmail());
                return "redirect:/multifactorauth";
            }
            session.setAttribute("account", account.get());
            ra.addFlashAttribute("account",account.get()); //RedirectAttributes is something to send to the page at return statement
            return "redirect:/dashboard";
        }
        else{
            ra.addFlashAttribute("error","Login failed.");
            return "redirect:/login";
        }
    }

    @GetMapping("/multifactorauth")
    public String showMultiFactorVerification(){
        return "multifactorauth";
    }
    @PostMapping("/multifactorauth")
    public String multifactorVerification(int digit1, int digit2, int digit3, int digit4, String email, RedirectAttributes ra, HttpSession session){
        if(!codeHM.containsKey(email))
        {
            ra.addFlashAttribute("error","Code expired or invalid.");
            return "redirect:/login";
        }
        int enteredCode = Integer.parseInt(String.format("%d%d%d%d",digit1,digit2,digit3,digit4));
        System.out.println(enteredCode);
        if(enteredCode == codeHM.get(email)){
            codeHM.remove(email); // remove email and code pair after successful verification
            Optional<Accounts> account = accountService.getAccountByEmail(email);
            session.setAttribute("account", account.get());
            ra.addFlashAttribute("account",account.get()); //RedirectAttributes is something to send to the page at return statement
            return "redirect:/dashboard";
        }
        else {
            ra.addFlashAttribute("error","Verification failed.");
            return "redirect:/login";
        }
    }
}
