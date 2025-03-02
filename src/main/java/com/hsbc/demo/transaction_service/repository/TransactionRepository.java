package com.hsbc.demo.transaction_service.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.hsbc.demo.transaction_service.entity.TransactionData;

@Repository
public interface TransactionRepository extends CrudRepository<TransactionData, String>,
                                                PagingAndSortingRepository<TransactionData, String> {

    Optional<TransactionData> findByTransactionId(String id);

    Optional<TransactionData> findBySourceTradeId(String tradeId);

    Page<TransactionData> findAllBySourceAccountIdOrDestAccountId(String sourceAccountId, String destAccountId, Pageable pageable);

    Page<TransactionData> findAllBySourceAccountId(String sourceAccountId, Pageable pageable);

    Page<TransactionData> findAllyByDestAccountId(String destAccountId, Pageable pageable);

    Page<TransactionData> findAll(Pageable pageable);
}
