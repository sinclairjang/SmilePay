package com.zerobase.smilepay.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.zerobase.smilepay.domain.Account;
import com.zerobase.smilepay.domain.AccountUser;
import com.zerobase.smilepay.domain.Transaction;
import com.zerobase.smilepay.domain.type.AccountStatus;
import com.zerobase.smilepay.domain.type.TransactionResult;
import com.zerobase.smilepay.domain.type.TransactionType;
import com.zerobase.smilepay.exception.TransactionException;
import com.zerobase.smilepay.exception.type.ErrorCode;
import com.zerobase.smilepay.repository.AccountRepository;
import com.zerobase.smilepay.repository.AccountUserRepository;
import com.zerobase.smilepay.repository.TransactionRepository;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
	@Mock
	private TransactionRepository transactionRepository;
	
	@Mock
	private AccountUserRepository accountUserRepository;
	
	@Mock
	private AccountRepository accountRepository;
	
	@InjectMocks
	private TransactionService transactionService;
	
	@Test
	void cancel_success() throws Exception {
		
		//given
		AccountUser accountUser = AccountUser.builder()
									.id(12L)
									.name("Louis")
									.build();
		Account account = Account.builder()
							.accountUser(accountUser)
							.accountStatus(AccountStatus.IN_USE)
							.balance(9000L)
							.accountNumber("1000000012")
							.build();
		Transaction transaction = Transaction.builder()
				.id(18L)
				.transactionType(TransactionType.PAYMENT)
				.transactionResult(TransactionResult.TRANSACTION_SUCCEDED)
				.account(account)
				.amount(1000L)
				.balanceSnapshot(9000L)
				.transactionId("transactionId")
				.transactionTime(LocalDateTime.now())
				.build();
						
		given(transactionRepository.findByTransactionId(anyString()))
			.willReturn(Optional.of(transaction));
		given(accountRepository.findByAccountNumber(anyString()))
			.willReturn(Optional.of(account));
					
		ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
		
		//when
		transactionService.cancelPayment("transactionId", "1000000012", 1000L);
		
		//then
		verify(transactionRepository, times(1)).save(captor.capture());
		assertEquals(1000L, captor.getValue().getAmount());
		assertEquals(9000L + 1000L, captor.getValue().getBalanceSnapshot());
		assertEquals(TransactionType.CANCEL, captor.getValue().getTransactionType());
		assertEquals(TransactionResult.TRANSACTION_SUCCEDED, captor.getValue().getTransactionResult());
		
	}
	
	private static Stream<Arguments> ArgumentProvider() {
	    return Stream.of(
	       Arguments.of("Some transaction", "Another account", 1000L)
	    );
	}
	
	@ParameterizedTest
	@MethodSource("ArgumentProvider")
	void cancel_transaction_not_matched_with_account(
			String someTransactionId, String anotherAccountNumber, Long amount) 
			throws Exception {
		
		// given
		AccountUser someUser = AccountUser.builder()
				.id(12L)
				.name("Some user")
				.build();
		Account someAccount = Account.builder()
				.id(143L)
				.accountUser(someUser)
				.accountStatus(AccountStatus.IN_USE)
				.balance(9000L)
				.accountNumber("Some account")
				.build();
		
		AccountUser anotherUser = AccountUser.builder()
				.id(23L)
				.name("Another user")
				.build();
		Account anotherAccount = Account.builder()
				.id(2892L)
				.accountUser(anotherUser)
				.accountStatus(AccountStatus.IN_USE)
				.balance(87000L)
				.accountNumber(anotherAccountNumber)
				.build();
		
		Transaction transaction = Transaction.builder()
				.id(18L)
				.transactionType(TransactionType.PAYMENT)
				.transactionResult(TransactionResult.TRANSACTION_SUCCEDED)
				.account(someAccount)
				.amount(amount)
				.balanceSnapshot(9000L)
				.transactionId(someTransactionId)
				.transactionTime(LocalDateTime.now())
				.build();	
		
		given(transactionRepository.findByTransactionId(anyString()))
				.willReturn(Optional.of(transaction));
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(anotherAccount));
		
		//when
		TransactionException transactionException = assertThrows(
				TransactionException.class,
				() -> transactionService.cancelPayment(
						someTransactionId, anotherAccountNumber, amount));
		
		//then
		assertEquals(ErrorCode.TRANSACTION_MISMATCH,
				transactionException.getErrorCode());
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"Non-existent transaction id"})
	void cancel_transaction_data_not_found(String someTransactionId) 
			throws Exception {
		
		// given
		given(transactionRepository.findByTransactionId(anyString()))
				.willReturn(Optional.empty());

		
		//when
		TransactionException transactionException = assertThrows(
				TransactionException.class,
				() -> transactionService.cancelPayment(
						someTransactionId, "Another account number", 1000L));
		
		//then
		assertEquals(ErrorCode.TRANSACTION_NOT_FOUND,
				transactionException.getErrorCode());
	}
}
