package com.commercebank.accounts;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ExternalAccountsCRUD extends CrudRepository<ExternalAccounts,Integer> {
    Optional<ExternalAccounts> findById(Integer integer);
}
