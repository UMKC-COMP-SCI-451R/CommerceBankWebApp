package com.commercebank;

import com.commercebank.accounts.AccountCRUD;
import com.commercebank.accounts.Accounts;
import com.commercebank.accounts.Transactions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.Assert;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class accountCrudTest {
    @Autowired
    private AccountCRUD accountCRUD;

    @Test
    public void testAddNew(){
        Accounts account = new Accounts();
        account.setEmail("test@gmail.com");
        account.setPassword("pass123");
        account.setLastName("Nguyen");
        account.setFirstName("Duy");

        Accounts savedAcc = accountCRUD.save(account);
        if(savedAcc!=null)
            System.out.println(savedAcc.toString());
        Assert.notNull(savedAcc,"savedAcc can't be null");
        Assertions.assertEquals(0.00,savedAcc.getBalance());

    }

    @Test
    public void findByEmail(){
        Optional<Accounts> foundAcc = accountCRUD.findByEmail("test@gmail.com");
        if(foundAcc.isPresent())
        {
            Accounts acc = foundAcc.get();
            System.out.println(acc.toString());
        }
        Assertions.assertFalse(!foundAcc.isPresent());
    }

    @Test
    public void findByEmail_failed(){
        Optional<Accounts> foundAcc = accountCRUD.findByEmail("testfailed@gmail.com");
        if(foundAcc.isPresent())
        {
            Accounts acc = foundAcc.get();
            System.out.println(acc.toString());
        }else{
            System.out.println("account not found");
        }
        Assertions.assertFalse(foundAcc.isPresent());
    }

    @Test
    public void addTransaction(){
        Optional<Accounts> foundAcc = accountCRUD.findByEmail("test@gmail.com");
        if(foundAcc.isPresent())
        {
            Accounts acc = foundAcc.get();
            Transactions tran = new Transactions();
            tran.setAccount(acc);
            tran.setAccountEmail(acc.getEmail());
            tran.setSource("123456");
            tran.setDestination("12343333");
            tran.setAmount(99.0);
            acc.getTransactionsList().add(tran);
        }
        Assertions.assertFalse(!foundAcc.isPresent());
    }

    @Test
    public void getAccountByCardNumber(){
        Optional<Accounts> foundAcc = accountCRUD.findByCardNumber(1076128529961269L);
        if(foundAcc.isPresent())
        {
            Accounts acc = foundAcc.get();
            System.out.println(acc);
        }
        Assertions.assertFalse(!foundAcc.isPresent());
    }
}
