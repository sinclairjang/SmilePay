package com.zerobase.smilepay.dto;

import java.time.LocalDateTime;

import com.zerobase.smilepay.domain.Account;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class DeleteAccount {
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Request {
		@NotNull
		@Min(1)
		private Long userId;
		
		@NotBlank
		@Size(min = 10, max = 10)
		private String accountNumber;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response {
		private Long userId;
		private String accountNumber;
		private LocalDateTime accountUnregisteredAt;
		
		public static Response from(Account account) {
			return Response.builder()
					.userId(account.getAccountUser().getId())
					.accountNumber(account.getAccountNumber())
					.accountUnregisteredAt(account.getAccountUnregisteredAt())
					.build();
		}
	}
}
