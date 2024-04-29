package com.zerobase.smilepay.dto;

import java.time.LocalDateTime;

import com.zerobase.smilepay.domain.Account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
	private Long userId;
	private String accountNumber;
	private Long balance;
	
	private LocalDateTime accountRegisteredAt;
	private LocalDateTime accountUnregisteredAt;
	
	public static AccountDto fromEntity(Account account) {
		return AccountDto.builder()
					.userId(account.getAccountUser().getId())
					.accountNumber(account.getAccountNumber())
					.balance(account.getBalance())
					.accountRegisteredAt(account.getAccountRegisteredAt())
					.accountUnregisteredAt(account.getAccountUnregisteredAt())
					.build();
	}
}
