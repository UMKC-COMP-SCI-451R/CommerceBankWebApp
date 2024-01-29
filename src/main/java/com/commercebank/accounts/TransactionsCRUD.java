package com.commercebank.accounts;

import org.springframework.data.repository.CrudRepository;

public interface TransactionsCRUD extends CrudRepository<Transactions,Integer> {
}
