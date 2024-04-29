package com.zerobase.smilepay.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.google.common.base.Objects;
import com.zerobase.smilepay.domain.Account;
import com.zerobase.smilepay.domain.AccountUser;
import com.zerobase.smilepay.domain.Transaction;
import com.zerobase.smilepay.domain.type.AccountStatus;
import com.zerobase.smilepay.domain.type.TransactionResult;
import com.zerobase.smilepay.domain.type.TransactionType;
import com.zerobase.smilepay.exception.AccountException;
import com.zerobase.smilepay.exception.TransactionException;
import com.zerobase.smilepay.exception.type.ErrorCode;
import com.zerobase.smilepay.repository.AccountRepository;
import com.zerobase.smilepay.repository.AccountUserRepository;
import com.zerobase.smilepay.repository.TransactionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {
	private final TransactionRepository transactionRepository;
	private final AccountUserRepository accountUserRepository;
	private final AccountRepository accountRepository;
	
	@Transactional
	public Transaction makePayment(Long userId, String accountNumber, Long amount) {
		AccountUser user = accountUserRepository.findById(userId)
			.orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
		
		Account account = accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
		
		validateMakePayment(user, account, amount);
		
		account.spend(amount);
		
		return saveTransaction(
				TransactionType.PAYMENT,
				TransactionResult.TRANSACTION_SUCCEDED, 
				amount, account);
	}
	private void validateMakePayment(AccountUser user, Account account, Long amount) {
		if (!Objects.equal(user.getId(), account.getAccountUser().getId())) {
			throw new AccountException(ErrorCode.ACCOUNT_MISMATCH);
		}
		if (account.getAccountStatus() != AccountStatus.IN_USE) {
			throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
		}
		if (account.getBalance() < amount) {
			throw new AccountException(ErrorCode.ACCOUNT_INSUFFICIENT_FUND);
		}
	}

	@Transactional
	public Transaction saveFailedTransaction(String accountNumber, Long amount) {
		Account account = accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
		
		return saveTransaction(
				TransactionType.PAYMENT,
				TransactionResult.TRANSACTION_FAILED,
				amount, account);	
	}

	private Transaction saveTransaction(
			TransactionType transactionType,
			TransactionResult transactionResult,
			Long amount, Account account) {
		return transactionRepository.save(
				Transaction.builder()
				.transactionType(transactionType)
				.transactionResult(transactionResult)
				.account(account)
				.amount(amount)
				.balanceSnapshot(account.getBalance())
				.transactionId(UUID.randomUUID().toString().replace("-", ""))
				.transactionTime(LocalDateTime.now())
				.build());
	}

	public Transaction cancelPayment(String transactionId, String accountNumber, Long amount) {
		
		Transaction transaction = transactionRepository.findByTransactionId(transactionId)
				.orElseThrow(() -> new TransactionException(ErrorCode.TRANSACTION_NOT_FOUND));
		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
		
		validateCancelPayment(transaction, account, amount);
		 
		account.recover(amount);
		
		return saveTransaction(
				TransactionType.CANCEL,
				TransactionResult.TRANSACTION_SUCCEDED,
				amount, account);
	}
	private void validateCancelPayment(Transaction transaction, Account account, Long amount) {
		if (!Objects.equal(transaction.getAccount().getId(), account.getId())) {
			throw new TransactionException(ErrorCode.TRANSACTION_MISMATCH);
		}
		
		if (!Objects.equal(transaction.getAmount(), amount)) {
			throw new TransactionException(ErrorCode.TRANSACTION_NOT_FULL_AMOUNT);
		}
		
		if (transaction.getTransactionTime().isBefore(LocalDateTime.now().minusYears(1))) {
			throw new TransactionException(ErrorCode.TRANSACTION_OUTDATED);
		}
	}

	public Transaction saveFailedCancelTransaction(String accountNumber, Long amount) {
		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
		
		return saveTransaction(
				TransactionType.CANCEL, 
				TransactionResult.TRANSACTION_FAILED, 
				amount, account);
	}
	public Transaction queryTransaction(String transactionId) {
		
		return transactionRepository.findByTransactionId(transactionId)
				.orElseThrow(() -> new TransactionException(
						ErrorCode.TRANSACTION_NOT_FOUND));
	}
	
}
