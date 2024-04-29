package com.zerobase.smilepay.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CreateAccount {
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Request {
		@NotNull
		@Min(1)
		private Long userId;
		
		@NotNull
		@Min(0)
		private Long initialBalance;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response {
		private Long userId;
		private String accountNumber;
		private LocalDateTime registerDateTime;
		
		public static Response from(AccountDto accountDto) {
			return Response.builder()
					.userId(accountDto.getUserId())
					.accountNumber(accountDto.getAccountNumber())
					.registerDateTime(accountDto.getAccountRegisteredAt())
					.build();
		}
	}
}
