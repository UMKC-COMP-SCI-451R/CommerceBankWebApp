package com.commercebank.accounts;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountCRUD extends CrudRepository<Accounts,Integer> {
    Optional<Accounts> findByEmail(String email);

    Optional<Accounts> findByCardNumber(long cardNumber);
}
