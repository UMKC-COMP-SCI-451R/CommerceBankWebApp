package com.commercebank.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired AccountCRUD accountCRUD;
    @Autowired ExternalAccountsCRUD externalAccountsCRUD;
    @Autowired TransactionsCRUD transactionsCRUD;
    public Optional<Accounts> getAccountByEmail(String email){
        return accountCRUD.findByEmail(email);
    }

    public Optional<Accounts> getAccountByID(Integer id){
        return accountCRUD.findById(id);
    }

    public Optional<Accounts> getAccountByCardNumber(long cardNumber){ return accountCRUD.findByCardNumber(cardNumber);}

    public void save(Accounts account) {
        accountCRUD.save(account);
    }

    public void saveExternal(ExternalAccounts exAcc){
        externalAccountsCRUD.save(exAcc);
    }

    public List<Transactions> getTransactionListByDateRange(Date startDate, Date endDate, String email){
        return transactionsCRUD.findTransactionsBetweenDatesForAccountEmail(startDate,endDate,email);
    }

    public Optional<ExternalAccounts> getExternalAccountById(Integer id){
        return externalAccountsCRUD.findById(id);
    }

}
