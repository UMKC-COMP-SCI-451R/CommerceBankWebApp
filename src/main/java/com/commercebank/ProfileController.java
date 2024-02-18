package com.commercebank;

import com.commercebank.accounts.AccountService;
import com.commercebank.accounts.Accounts;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/saveNewAddress")
    public String saveNewAddress(String email, String newAddress, RedirectAttributes ra){
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
    public String saveNewPhoneNumber(String email, String newPhoneNumber, RedirectAttributes ra){
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
    public String saveNewPassword(String email, String newPassword, RedirectAttributes ra){
        Optional<Accounts> acc = accountService.getAccountByEmail(email);
        Accounts account;
        if(acc.isPresent()){
            account = acc.get();
            account.setPassword(newPassword);
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
