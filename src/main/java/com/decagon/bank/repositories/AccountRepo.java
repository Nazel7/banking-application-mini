package com.decagon.bank.repositories;

import com.decagon.bank.entities.models.AccountModel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import javax.security.auth.login.AccountNotFoundException;

public interface AccountRepo extends JpaRepository<AccountModel, UUID> {

    AccountModel findAccountModelByIban(String iban) throws AccountNotFoundException;

}
