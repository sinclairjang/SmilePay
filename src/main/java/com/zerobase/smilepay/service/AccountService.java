package com.zerobase.smilepay.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.zerobase.smilepay.domain.Account;
import com.zerobase.smilepay.domain.AccountUser;
import com.zerobase.smilepay.domain.type.AccountStatus;
import com.zerobase.smilepay.dto.AccountDto;
import com.zerobase.smilepay.dto.GetAccount.Response;
import com.zerobase.smilepay.exception.AccountException;
import com.zerobase.smilepay.exception.type.ErrorCode;
import com.zerobase.smilepay.repository.AccountRepository;
import com.zerobase.smilepay.repository.AccountUserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
	private final AccountRepository accountRepository;
	private final AccountUserRepository accountUserRepository;
	
	@Transactional
	public AccountDto createAccount(Long userId, Long initialBalance) {
		AccountUser accountUser = accountUserRepository.findById(userId)
							.orElseThrow( () -> new AccountException(ErrorCode.USER_NOT_FOUND) );
		
		validateCreateAccount(accountUser);
		
		String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
												.map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
												.orElse("1000000000");
		
		return AccountDto.fromEntity(
				accountRepository.save(
						Account.builder()
						.accountUser(accountUser)
						.accountStatus(AccountStatus.IN_USE)
						.accountNumber(newAccountNumber)
						.balance(initialBalance)
						.accountRegisteredAt(LocalDateTime.now())
						.build())
				);
	}

	private void validateCreateAccount(AccountUser accountUser) {
		if (accountRepository.countByAccountUser(accountUser) >= 10) {
			throw new AccountException(ErrorCode.EXCEEDED_ALLOWABLE_ACCOUNTS);
		}
	}

	@Transactional
	public Account deleteAccount(Long userId, String accountNumber) {
		AccountUser accountUser = accountUserRepository.findById(userId)
				.orElseThrow( () -> new AccountException(ErrorCode.USER_NOT_FOUND));
		Account account = accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow( () -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
		
		validateUserAccount(accountUser, account);
		
		account.setAccountStatus(AccountStatus.UNREGISTERED);
		account.setAccountUnregisteredAt(LocalDateTime.now());	
		return account;
	}

	private void validateUserAccount(AccountUser accountUser, Account account) {
		if (!accountUser.getId().equals(account.getAccountUser().getId())) {
			 throw new AccountException(ErrorCode.ACCOUNT_MISMATCH);
		}
	
		if (account.getAccountStatus() == AccountStatus.UNREGISTERED) {
			throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
		}
		
		if (account.getBalance() > 0 ) {
			throw new AccountException(ErrorCode.ACCOUNT_NOT_EMPTY);
		}
	}

	public List<Response> getAccountsByUserId(Long userId) {
		AccountUser accountUser = accountUserRepository.findById(userId)
				.orElseThrow( () -> new AccountException(ErrorCode.USER_NOT_FOUND));
		List<Account> accounts = accountRepository.findByAccountUser(accountUser);
		
		return accounts.stream()
				.map(Response::fromEntity)
				.collect(Collectors.toList());
	}
}
