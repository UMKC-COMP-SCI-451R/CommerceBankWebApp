package com.commercebank.accounts;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TransactionsCRUD extends CrudRepository<Transactions,Integer> {
    @Query("SELECT t FROM Transactions t WHERE t.date BETWEEN :startDate AND :endDate AND t.account.email = :email")
    List<Transactions> findTransactionsBetweenDatesForAccountEmail(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("email") String email
    );

}
