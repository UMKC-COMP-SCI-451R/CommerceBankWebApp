package com.commercebank;

import com.commercebank.accounts.AccountCRUD;
import com.commercebank.accounts.Accounts;
import com.commercebank.accounts.ExternalAccounts;
import com.commercebank.accounts.ExternalAccountsCRUD;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
public class ExternalAccountTest {
    @Autowired
    AccountCRUD accountCRUD;
    @Autowired
    ExternalAccountsCRUD externalAccountsCRUD;
    @Test
    public void addExternalAccount(){
        Optional<Accounts> acc = accountCRUD.findByEmail("test@gmail.com");
        if(acc.isPresent())
        {
            ExternalAccounts externalAccount = new ExternalAccounts();
            externalAccount.setAccountNumber(424452121);
            externalAccount.setBankName("Holy");
            externalAccount.setRoutingNumber(75672285);
            externalAccount.setAccount(acc.get());
            externalAccountsCRUD.save(externalAccount);
            System.out.println(acc.get());
            Assertions.assertEquals(11331935,acc.get().getAccID());
        }

    }
}
