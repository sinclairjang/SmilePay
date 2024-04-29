package com.zerobase.smilepay.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zerobase.smilepay.domain.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	Optional<Transaction> findByTransactionId(String transactionId);

}
