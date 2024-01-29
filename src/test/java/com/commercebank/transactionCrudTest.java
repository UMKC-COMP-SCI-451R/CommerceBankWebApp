package com.commercebank;

import com.commercebank.accounts.AccountCRUD;
import com.commercebank.accounts.Accounts;
import com.commercebank.accounts.Transactions;
import com.commercebank.accounts.TransactionsCRUD;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class transactionCrudTest {
    @Autowired
    TransactionsCRUD transactionsCRUD;
    @Autowired
    AccountCRUD accountCRUD;
    @Test
    public void addTransaction(){
        Optional<Accounts> acc = accountCRUD.findByEmail("test@gmail.com");
        if(acc.isPresent()){
            Transactions trans = new Transactions();
            trans.setAmount(100.0);
            trans.setSource("11331935");
            trans.setDestination("12345678");
            trans.setAccount(acc.get());
            trans.setAccountEmail(acc.get().getEmail());
            Transactions savedTransaction = transactionsCRUD.save(trans);
            if(savedTransaction!=null){
                System.out.println(savedTransaction.toString());
            }
            Assertions.assertEquals(11331935,acc.get().getAccID());
        }

    }

    @Test
    public void addTransaction2(){
        Optional<Accounts> acc = accountCRUD.findByEmail("test@gmail.com");
        if(acc.isPresent()){
            Transactions trans = new Transactions();
            trans.setAmount(200.0);
            trans.setSource("12345789");
            trans.setDestination("11331935");
            trans.setAccount(acc.get());
            trans.setAccountEmail(acc.get().getEmail());
            Transactions savedTransaction = transactionsCRUD.save(trans);
            if(savedTransaction!=null){
                System.out.println(savedTransaction.toString());
            }
            Assertions.assertEquals(11331935,acc.get().getAccID());
        }

    }
}
