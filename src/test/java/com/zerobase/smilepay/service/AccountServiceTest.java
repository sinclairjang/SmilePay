package com.zerobase.smilepay.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.zerobase.smilepay.domain.Account;
import com.zerobase.smilepay.domain.AccountUser;
import com.zerobase.smilepay.domain.type.AccountStatus;
import com.zerobase.smilepay.dto.AccountDto;
import com.zerobase.smilepay.dto.GetAccount;
import com.zerobase.smilepay.exception.AccountException;
import com.zerobase.smilepay.exception.type.ErrorCode;
import com.zerobase.smilepay.repository.AccountRepository;
import com.zerobase.smilepay.repository.AccountUserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
	@Mock
	private AccountRepository accountRepository;
	
	@Mock
	private AccountUserRepository accountUserRepository;
	
	@InjectMocks
	private AccountService accountService;
	
	@Test
	void getAccount_Pass() throws Exception {
		
		//given
		AccountUser user = AccountUser.builder()
				.id(12L)
				.name("Ace").build();
		List<Account> accountsByUser = Arrays.asList(
					Account.builder()
						.accountNumber("1000000012")
						.balance(10000L)
						.build(),
					
					Account.builder()
						.accountNumber("1000000013")
						.balance(20000L)
						.build()
				);
				
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
		given(accountRepository.findByAccountUser(user))
				.willReturn(accountsByUser);
		
		//when
		List<GetAccount.Response> accounts = accountService.getAccountsByUserId(user.getId());
		//then
		assertEquals(2, accounts.size());
		assertEquals("1000000012", accounts.get(0).getAccountNumber());
		assertEquals(10000L, accounts.get(0).getBalance());
		assertEquals("1000000013", accounts.get(1).getAccountNumber());
		assertEquals(20000L, accounts.get(1).getBalance());
	}
	
	@Test
	@DisplayName("해당 유저 없음 - 계좌 조회 실패")
	void getAccount_UserNotFound() throws Exception {
		
		//given
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.empty());
		
		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> accountService.getAccountsByUserId(1L));
		
		//then
		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}
	
	@Test
	void createAccount() throws Exception {
		
		//given
		AccountUser user = AccountUser.builder()
				.id(12L)
				.name("Ace").build();
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
		given(accountRepository.findFirstByOrderByIdDesc())
				.willReturn(Optional.of(Account.builder()
						.accountUser(user)
						.accountNumber("1000000012").build()));
		given(accountRepository.save(any()))
				.willReturn(Account.builder()
						.accountUser(user)
						.accountNumber("1000000013").build());
		ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
		
		//when
		AccountDto accountDto = accountService.createAccount(1L, 1000L);
		//then
		verify(accountRepository, times(1)).save(captor.capture());
		assertEquals(12L, accountDto.getUserId());
		assertEquals("1000000013", captor.getValue().getAccountNumber());
	}
	
	@Test
	void createFirstAccount() throws Exception {
		
		//given
		AccountUser user = AccountUser.builder()
				.id(15L)
				.name("Ace").build();
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
		given(accountRepository.findFirstByOrderByIdDesc())
				.willReturn(Optional.empty());
		given(accountRepository.save(any()))
				.willReturn(Account.builder()
						.accountUser(user)
						.accountNumber("1000000013").build());
		ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
		
		//when
		AccountDto accountDto = accountService.createAccount(1L, 1000L);
		//then
		verify(accountRepository, times(1)).save(captor.capture());
		assertEquals(15L, accountDto.getUserId());
		assertEquals("1000000000", captor.getValue().getAccountNumber());
	}

	@Test
	@DisplayName("해당 유저 없음 - 계좌 생성 실패")
	void createAccount_UserNotFound() throws Exception {
		
		//given
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.empty());
		
		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> accountService.createAccount(1L, 1000L));
		
		//then
		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}
	
	@Test
	@DisplayName("계좌 10개 이상 생성 요청 - 계좌 생성 실패")
	void createAccount_MaxNumAccounts() throws Exception {
		
		//given
		AccountUser user = AccountUser.builder()
				.id(12L)
				.name("Ace").build();
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
		given(accountRepository.countByAccountUser(any()))
				.willReturn(10);
		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> accountService.createAccount(1L, 1000L));
		
		//then
		assertEquals(ErrorCode.EXCEEDED_ALLOWABLE_ACCOUNTS, exception.getErrorCode());
	}
	
	@Test
	@DisplayName("계좌 해지 요청")
	void deleteAccount() throws Exception {
		//given
		AccountUser user = AccountUser.builder()
				.id(12L)
				.name("Ace").build();
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
		given(accountRepository.findByAccountNumber(anyString()))
				.willReturn(Optional.of(Account.builder()
						.accountUser(user)
						.accountNumber("1000001115")
						.accountStatus(AccountStatus.IN_USE)
						.balance(0L)
						.build()));
		
		//when
		Account account = accountService.deleteAccount(12L, "1000001115");
		
		//then
		assertEquals(12L, account.getAccountUser().getId());
		assertEquals("1000001115", account.getAccountNumber());
	}
	
	@Test
	@DisplayName("해당 유저 없음 - 계좌 해지 실패")
	void deleteAccount_UserNotFound() throws Exception {
		
		//given
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.empty());
		
		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> accountService.deleteAccount(1L, "10000000000"));
		
		//then
		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}
	
	@Test
	@DisplayName("이미 해지된 계좌 해지 요청 - 계좌 해지 실패")
	void deleteAccount_AlreadyUnregistered() throws Exception {
		
		//given
		AccountUser user = AccountUser.builder()
				.id(1L)
				.name("Ace").build();
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
		given(accountRepository.findByAccountNumber(anyString()))
		.willReturn(Optional.of(Account.builder()
				.accountUser(user)
				.accountNumber("10000000000")
				.accountStatus(AccountStatus.UNREGISTERED)
				.balance(0L)
				.build()));
		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> accountService.deleteAccount(1L, "10000000000"));
		
		//then
		assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());
	}
	
	@Test
	@DisplayName("잔액이 있는 계좌 해지 요청 - 계좌 해지 실패")
	void deleteAccount_AccountNotEmpty() throws Exception {
		
		//given
		AccountUser user = AccountUser.builder()
				.id(1L)
				.name("Ace").build();
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user));
		given(accountRepository.findByAccountNumber(anyString()))
		.willReturn(Optional.of(Account.builder()
				.accountUser(user)
				.accountNumber("10000000000")
				.accountStatus(AccountStatus.IN_USE)
				.balance(10000L)
				.build()));
		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> accountService.deleteAccount(1L, "10000000000"));
		
		//then
		assertEquals(ErrorCode.ACCOUNT_NOT_EMPTY, exception.getErrorCode());
	}
	
	@Test
	@DisplayName("해지 계좌 소유주 불일치 - 계좌 해지 실패")
	void deleteAccount_AccountMismatch() throws Exception {
		
		//given
		AccountUser user1 = AccountUser.builder()
				.id(5L)
				.name("Ace").build();
		AccountUser user2 = AccountUser.builder()
				.id(1L)
				.name("Loopy").build();
		given(accountUserRepository.findById(anyLong()))
				.willReturn(Optional.of(user1));
		given(accountRepository.findByAccountNumber(anyString()))
		.willReturn(Optional.of(Account.builder()
				.accountUser(user2)
				.accountNumber("10000000000")
				.accountStatus(AccountStatus.IN_USE)
				.balance(0L)
				.build()));
		//when
		AccountException exception = assertThrows(AccountException.class,
				() -> accountService.deleteAccount(user2.getId(), "10000000000"));
		
		//then
		assertEquals(ErrorCode.ACCOUNT_MISMATCH, exception.getErrorCode());
	}
	
}
