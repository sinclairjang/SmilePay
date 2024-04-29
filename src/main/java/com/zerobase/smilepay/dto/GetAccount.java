package com.zerobase.smilepay.dto;

import com.zerobase.smilepay.domain.Account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class GetAccount {
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response {
		private String accountNumber;
		private Long balance;
		
		public static Response fromEntity(Account account) {
			return Response.builder()
			.accountNumber(account.getAccountNumber())
			.balance(account.getBalance())
			.build();
		}
	}
}
