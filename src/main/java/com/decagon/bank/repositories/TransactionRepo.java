package com.decagon.bank.repositories;


import com.decagon.bank.entities.models.TransactionModel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepo extends JpaRepository<TransactionModel, Long> {

    TransactionModel findTransactionModelByTranxRef(String tranxRef);

    Page<TransactionModel> findAllByBenefAccountNoOrderByCreatedAtDesc(String iban, Pageable pageable);

    @Query("SELECT t FROM TransactionModel t ORDER BY t.createdAt DESC")
    Page<TransactionModel> fetchAllTransactionOrderByCreatedAtDesc(Pageable pageable);

}
