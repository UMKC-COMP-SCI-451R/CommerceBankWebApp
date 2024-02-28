package com.commercebank.accounts;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.http.HttpSession;
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

@SessionAttributes({"account", "filteredTransactions","currentPage","dateRange"})
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
                    System.out.println("code expired");
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
    public String filterTransactions( String fromDate, String toDate, RedirectAttributes ra, HttpSession session) {
        Accounts sessionAccount = (Accounts) session.getAttribute("account");
        if(!fromDate.isBlank() && !toDate.isBlank()){
            try{
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = dateFormat.parse(fromDate);
                Date endDate = dateFormat.parse(toDate);
                List<Transactions> filteredTransactionList = accountService.getTransactionListByDateRange(startDate,endDate,sessionAccount.getEmail());
                System.out.println(filteredTransactionList.toString());

                if(!filteredTransactionList.isEmpty()){
                    String dateRange = fromDate + " to " + toDate;
                    session.setAttribute("filteredTransactions",filteredTransactionList);
                    session.setAttribute("dateRange",dateRange);
                    ra.addFlashAttribute("filteredTransactions",filteredTransactionList);
                    ra.addFlashAttribute("dateRange",dateRange);
//                    ra.addFlashAttribute("toDate",toDate);
//                    ra.addFlashAttribute("fromDate",fromDate);
                    session.setAttribute("currentPage",1);
                    ra.addFlashAttribute("currentPage",1);
                    return "redirect:/dashboard";
                }else{
                    ra.addFlashAttribute("message","There is no transaction from "+fromDate+" to "+ toDate);
                }
            }catch(ParseException e) {
                System.out.println("from date is " + fromDate);
                System.out.println("to date is " + toDate);
            }
        }

        System.out.println("date are blanks or list is empty");
        session.setAttribute("dateRange","");
        ra.addFlashAttribute("dateRange","");
        session.setAttribute("filteredTransactions",new ArrayList<Transactions>());
        ra.addFlashAttribute("filteredTransactions",new ArrayList<Transactions>());
        session.setAttribute("currentPage",0);
        ra.addFlashAttribute("currentPage",0);

        return "redirect:/dashboard";
    }

    @PostMapping("/next")
    public String nextPage(HttpSession session, RedirectAttributes ra){
        System.out.println(session.getAttribute("currentPage"));
        Integer currentPage = (Integer) session.getAttribute("currentPage");
        if (currentPage != null) {
            session.setAttribute("currentPage", currentPage + 1);
            ra.addFlashAttribute("currentPage",currentPage + 1);
            ra.addFlashAttribute("dateRange", session.getAttribute("dateRange"));
        }
        System.out.println(session.getAttribute("currentPage"));
        return "redirect:/dashboard";
        //return "dashboard";
    }

    @PostMapping("/previous")
    public String prevPage(HttpSession session, RedirectAttributes ra){
        Integer currentPage = (Integer) session.getAttribute("currentPage");
        if (currentPage != null) {
            session.setAttribute("currentPage", currentPage - 1);
            ra.addFlashAttribute("currentPage",currentPage - 1);
            ra.addFlashAttribute("dateRange", session.getAttribute("dateRange"));
        }
        System.out.println(session.getAttribute("currentPage"));
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
    public String saveProfileSettings( boolean isPaperless, boolean isMultifactorAuth, boolean isEmailAlert, boolean isTextAlert, RedirectAttributes ra,HttpSession session){
        String email = ((Accounts)session.getAttribute("account")).getEmail();
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
            session.setAttribute("account",account);
            ra.addFlashAttribute("account",account);
        }else {
            ra.addFlashAttribute("error","Something go wrong.");

        }
        return "redirect:/profile";
    }


    @PostMapping("/makeTransaction")
    public String processTransaction(String source, String destination, double amount, RedirectAttributes ra, HttpSession session){
        String email = ((Accounts)session.getAttribute("account")).getEmail();
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
                session.setAttribute("account",account);
                ra.addFlashAttribute("account",account);
                return "redirect:/transfer";
            }
            else{
                ra.addFlashAttribute("error","Insufficient fund!"); //RedirectAttributes is something to send to the page at return statement
                return "redirect:/transfer";
            }
        }else {
            ra.addFlashAttribute("error", "Something went wrong!"); //RedirectAttributes is something to send to the page at return statement
            return "redirect:/transfer";
        }
    }

    @PostMapping("/addExternalAccount")
    public String addExternalAccount( ExternalAccounts exAcc, RedirectAttributes ra, HttpSession session){
        String email = ((Accounts)session.getAttribute("account")).getEmail();
        Optional<Accounts> acc = accountService.getAccountByEmail(email);
        Accounts account;
        if(acc.isPresent()){
            account = acc.get();
            exAcc.setAccount(account);
            exAcc.setActive(false);
            account.getExternalAccountsList().add(exAcc);
            //System.out.println(account);
            accountService.save(account);
            ra.addFlashAttribute("message","External account added and is waiting for verification.");
            session.setAttribute("account",account);
            ra.addFlashAttribute("account",account);
        }else {
            ra.addFlashAttribute("error","Something go wrong.");

        }
        return "redirect:/transfer";
    }

    @PostMapping("/verifyExternalAccount")
    public String verifyExternalAccount(String pendingAccount, RedirectAttributes ra, HttpSession session){
        String email = ((Accounts)session.getAttribute("account")).getEmail();
        Optional<ExternalAccounts> acc = accountService.getExternalAccountById(Integer.parseInt(pendingAccount));
        ExternalAccounts exAcc;
        if(acc.isPresent()){
            exAcc = acc.get();
            exAcc.setActive(true);
            accountService.saveExternal(exAcc);
            Optional<Accounts> updatedAccount = accountService.getAccountByEmail(email);
            session.setAttribute("account",updatedAccount.get());
            ra.addFlashAttribute("account",updatedAccount.get());
            ra.addFlashAttribute("message","External account is successfully verified.");
        }else {
            ra.addFlashAttribute("error","Something go wrong.");
        }
        return "redirect:/transfer";
    }

}
