package com.commercebank.accounts;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SessionAttributes({"account", "filteredTransactions"})
@Controller
public class AccountController {
    @Autowired private AccountService accountService;
    @Autowired private EmailService emailService;
    //private int code;
    private HashMap<String, Integer> codeHM = new HashMap<>();
    @GetMapping("/register")
    public String showRegisterForm(Model model){
        model.addAttribute("newAcc", new Accounts());
        return "register";
    }

    @PostMapping("/save")
    public String saveAccount(Accounts account, RedirectAttributes ra){
        if(accountService.getAccountByEmail(account.getEmail()).isPresent())
        {
            ra.addFlashAttribute("error","There is an account associated with " + account.getEmail()+
                    ". Please try to login instead!");
            return "redirect:/login";
        }
        long cardNumber = RandNumGenerator.generateRandom16DigitNumber();
        Optional<Accounts> acc = accountService.getAccountByCardNumber(cardNumber);
        while(acc.isPresent()){
            cardNumber = RandNumGenerator.generateRandom16DigitNumber();
            acc = accountService.getAccountByCardNumber(cardNumber);
        }
        account.setCardNumber(cardNumber);
        account.setTextAlert(false);
        account.setMultifactorAuth(false);
        account.setEmailAlert(false);
        account.setPaperless(false);
        accountService.save(account);
        ra.addFlashAttribute("message","Register successfully. Please log in here.");
        return "redirect:/login";
    }

//    @PostMapping("/login")
//    public String authenticate(String email, String password, RedirectAttributes ra, Boolean checkBoxRememberUser, HttpServletResponse response){ //receive email and password from the login form
//        Optional<Accounts> account = accountService.getAccountByEmail(email);
//        //emailService.sendMultiFacAuthEmail(account.get().getEmail(),4444);
//        if(checkBoxRememberUser!=null){ //add usermail cookie
//            Cookie cookie = new Cookie("useremail",email);
//            cookie.setMaxAge(3600); // Set the expiration time in seconds
//            response.addCookie(cookie);
//        }else{
//            Cookie cookie = new Cookie("useremail",null); // remove useremail cookie
//            cookie.setMaxAge(0); // set to 0 to remove
//            response.addCookie(cookie);
//        }
//
//        if(account.isPresent() && account.get().getPassword().equals(password)){
//            Accounts acc = account.get();
//            if(acc.isMultifactorAuth())
//            {
//                int code = RandNumGenerator.generateRandom4DigitCode();
//                codeHM.put(acc.getEmail(),code);
//                new Thread(() -> {
//                    try {
//                        Thread.sleep(5*60 * 1000);  // Sleep for 5 minutes
//                        codeHM.remove(acc.getEmail());  // remove code (expired)
//                    } catch (InterruptedException e) {
//                        System.out.println(e);
//                    }
//                }).start();
//                //code = RandNumGenerator.generateRandom4DigitCode();
//                emailService.sendMultiFacAuthEmail(acc.getEmail(),code);
//                ra.addFlashAttribute("email",acc.getEmail());
//                return "redirect:/multifactorauth";
//            }
//            ra.addFlashAttribute("account",account.get()); //RedirectAttributes is something to send to the page at return statement
//            return "redirect:/dashboard";
//        }
//        else{
//            ra.addFlashAttribute("error","Login failed.");
//            return "redirect:/login";
//        }
//    }
//
//    @GetMapping("/multifactorauth")
//    public String showMultiFactorVerification(){
//        return "multifactorauth";
//    }
//    @PostMapping("/multifactorauth")
//    public String multifactorVerification(int enteredCode, String email, RedirectAttributes ra){
//        if(!codeHM.containsKey(email))
//        {
//            ra.addFlashAttribute("error","Code expired or invalid.");
//            return "redirect:/login";
//        }
//        if(enteredCode == codeHM.get(email)){
//            codeHM.remove(email); // remove email and code pair after successful verification
//            Optional<Accounts> account = accountService.getAccountByEmail(email);
//            ra.addFlashAttribute("account",account.get()); //RedirectAttributes is something to send to the page at return statement
//            return "redirect:/dashboard";
//        }
//        else {
//            ra.addFlashAttribute("error","Verification failed.");
//            return "redirect:/login";
//        }
//    }

    @GetMapping("/resetpassword")
    public String showResetPassword(){
        return "resetpassword";
    }
    @PostMapping("/resetpassword")
    public String showResetPassword(String emailToReset, RedirectAttributes ra){
        Optional<Accounts> account = accountService.getAccountByEmail(emailToReset);
        if(!account.isPresent())
        {
            ra.addFlashAttribute("error", "There isn't an account associated with "+ emailToReset +". Please consider to register a new account!");
            return "redirect:/login";
        }
        else{
            Accounts acc = account.get();
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
            emailService.sendResetPasswordEmail(acc.getEmail(),code);
            ra.addFlashAttribute("email",acc.getEmail());
            return "redirect:/resetpassword";
        }

    }

    @PostMapping("/newpassword")
    public String updatePassword(String newPassword, String email, int enteredCode, RedirectAttributes ra)
    {
        if(!codeHM.containsKey(email))
        {
            ra.addFlashAttribute("error","Code expired or invalid.");
            return "redirect:/login";
        }
        if(enteredCode == codeHM.get(email)){
            Optional<Accounts> account = accountService.getAccountByEmail(email);
            Accounts acc = account.get();
            acc.setPassword(newPassword);
            accountService.save(acc);
            ra.addFlashAttribute("message","Password reset successfully");
            return "redirect:/login";
        }
        else{
            ra.addFlashAttribute("error","Verification failed.");
            return "redirect:/login";
        }
    }

    @GetMapping("/dashboard")
    public String showDashBoard(){
        return "dashboard";
    }

    @PostMapping("/filterTransactions")
    public String filterTransactions(String email, String fromDate, String toDate, RedirectAttributes ra) {
        if(!fromDate.isBlank() && !toDate.isBlank()){
            try{
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = dateFormat.parse(fromDate);
                Date endDate = dateFormat.parse(toDate);
                List<Transactions> filteredTransactionList = accountService.getTransactionListByDateRange(startDate,endDate,email);
                System.out.println(filteredTransactionList.toString());
                // more code
                if(filteredTransactionList.isEmpty()){
                    ra.addFlashAttribute("message","There is no transaction from "+fromDate+" to "+ toDate);
                    ra.addFlashAttribute("filteredTransactions",filteredTransactionList);
                }
                else{
                    ra.addFlashAttribute("filteredTransactions",filteredTransactionList);
                    ra.addFlashAttribute("toDate",toDate);
                    ra.addFlashAttribute("fromDate",fromDate);
                }
                return "redirect:/dashboard";
            }catch(ParseException e) {
                System.out.println("from date is " + fromDate);
                System.out.println("to date is " + toDate);
            }
        }
        else{
            System.out.println("dates are blanks");
            ra.addFlashAttribute("filteredTransactions",new ArrayList<Transactions>());
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/transfer")
    public String showTransfer(Model model){
        model.addAttribute("exAcc",new ExternalAccounts());
        return "transfer";
    }

    @GetMapping("/profile")
    public String showProfile(){
        return "profile";
    }

    @GetMapping("/loans")
    public String showLoans(){
        return "loans";
    }

    @PostMapping("/saveProfileSettings")
    public String saveProfileSettings(String email, boolean isPaperless, boolean isMultifactorAuth, boolean isEmailAlert, boolean isTextAlert, RedirectAttributes ra){
        Optional<Accounts> acc = accountService.getAccountByEmail(email);
        Accounts account;
        if(acc.isPresent()){
            account = acc.get();
            account.setPaperless(isPaperless);
            account.setEmailAlert(isEmailAlert);
            account.setMultifactorAuth(isMultifactorAuth);
            account.setTextAlert(isTextAlert);
            //System.out.println(account);
            accountService.save(account);
            ra.addFlashAttribute("message","All settings are saved!");
            ra.addFlashAttribute("account",account); //this will update attribute "account" in the current session
        }else {
            ra.addFlashAttribute("error","Something go wrong.");

        }
        return "redirect:/profile";
    }


    @PostMapping("/makeTransaction")
    public String processTransaction(String email, String source, String destination, double amount, RedirectAttributes ra){

        Optional<Accounts> acc = accountService.getAccountByEmail(email);
        Accounts account;
        if(acc.isPresent()){
            account = acc.get();
            if((source.equals("My Account") && account.getBalance()-amount>=0.0) || destination.equals("My Account"))
            {
                Transactions tran = new Transactions();
                tran.setAccount(account);
                tran.setAccountEmail(account.getEmail());
                tran.setSource(source);
                tran.setDestination(destination);
                tran.setAmount(amount);
                account.getTransactionsList().add(tran);
                account.setBalance(account.getBalance() - (source.equals("My Account")? amount : -amount));
                accountService.save(account); //update account object after adding transaction
                ra.addFlashAttribute("message","Transfer is made successfully!"); //RedirectAttributes is something to send to the page at return statement
                ra.addFlashAttribute("account",account); //this will update attribute "account" in the current session
                return "redirect:/transfer";
            }
            else{
                ra.addFlashAttribute("error","Insufficient fund!"); //RedirectAttributes is something to send to the page at return statement
                //ra.addFlashAttribute("account",account);
                return "redirect:/transfer";
            }
        }else {
            ra.addFlashAttribute("error", "Something went wrong!"); //RedirectAttributes is something to send to the page at return statement
            return "redirect:/transfer";
        }
    }

    @PostMapping("/addExternalAccount")
    public String addExternalAccount(String email, ExternalAccounts exAcc, RedirectAttributes ra){
        Optional<Accounts> acc = accountService.getAccountByEmail(email);
        Accounts account;
        if(acc.isPresent()){
            account = acc.get();
            exAcc.setAccount(account);
            account.getExternalAccountsList().add(exAcc);
            //System.out.println(account);
            accountService.save(account);
            ra.addFlashAttribute("message","External account added.");
            ra.addFlashAttribute("account",account); //this will update attribute "account" in the current session
        }else {
            ra.addFlashAttribute("error","Something go wrong.");

        }
        return "redirect:/transfer";
    }

}
