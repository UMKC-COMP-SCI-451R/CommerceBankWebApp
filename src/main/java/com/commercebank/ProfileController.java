package com.commercebank;

import com.commercebank.accounts.AccountService;
import com.commercebank.accounts.Accounts;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@SessionAttributes("account")
@Controller
public class ProfileController {
    @Autowired
    private AccountService accountService;
    @Autowired private BCryptPasswordEncoder bEncoder;
    @PostMapping("/saveNewAddress")
    public String saveNewAddress(String newAddress, RedirectAttributes ra, HttpSession session){
        String email = ((Accounts)session.getAttribute("account")).getEmail();
        Optional<Accounts> acc = accountService.getAccountByEmail(email);
        Accounts account;
        if(acc.isPresent()){
            account = acc.get();
            account.setAddress(newAddress);
            //System.out.println(account);
            accountService.save(account);
            ra.addFlashAttribute("message","New Address is saved!");
            ra.addFlashAttribute("account",account); //this will update attribute "account" in the current session
        }else {
            ra.addFlashAttribute("error","Something go wrong.");

        }
        return "redirect:/profile";
    }

    @PostMapping("/saveNewPhoneNumber")
    public String saveNewPhoneNumber(String newPhoneNumber, RedirectAttributes ra, HttpSession session){
        String email = ((Accounts)session.getAttribute("account")).getEmail();
        Optional<Accounts> acc = accountService.getAccountByEmail(email);
        Accounts account;
        if(acc.isPresent()){
            account = acc.get();
            account.setPhoneNumber(newPhoneNumber);
            //System.out.println(account);
            accountService.save(account);
            ra.addFlashAttribute("message","New Phone number is saved!");
            ra.addFlashAttribute("account",account); //this will update attribute "account" in the current session
        }else {
            ra.addFlashAttribute("error","Something go wrong.");

        }
        return "redirect:/profile";
    }

    @PostMapping("/saveNewPassword")
    public String saveNewPassword(String newPassword, RedirectAttributes ra, HttpSession session){
        String email = ((Accounts)session.getAttribute("account")).getEmail();
        Optional<Accounts> acc = accountService.getAccountByEmail(email);
        Accounts account;
        if(acc.isPresent()){
            account = acc.get();
            account.setPassword(bEncoder.encode(newPassword));
            //System.out.println(account);
            accountService.save(account);
            ra.addFlashAttribute("message","Password reset successfully!");
            ra.addFlashAttribute("account",account); //this will update attribute "account" in the current session
        }else {
            ra.addFlashAttribute("error","Something go wrong.");

        }
        return "redirect:/profile";
    }
}
