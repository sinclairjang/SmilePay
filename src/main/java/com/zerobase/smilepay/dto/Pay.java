package com.zerobase.smilepay.dto;

import java.time.LocalDateTime;

import com.zerobase.smilepay.aop.AccountLockIdInterface;
import com.zerobase.smilepay.domain.Transaction;
import com.zerobase.smilepay.domain.type.TransactionResult;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class Pay {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Request implements AccountLockIdInterface {	
		@NotNull
		@Min(1)
		private Long userId;
		
		@NotBlank
		@Size(min = 10, max = 10)
		private String accountNumber;
		
		@NotNull
		@Min(10)
		@Max(1_000_000_000)
		private Long amount;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response {
		private String accountNumber;
		private TransactionResult transactionResult;
		private String transactionId;
		private Long amount;
		private LocalDateTime transactionTime;
		public static Response fromEntity(Transaction transaction) {
			
			return Response.builder()
					.accountNumber(transaction.getAccount().getAccountNumber())
					.transactionResult(transaction.getTransactionResult())
					.transactionId(transaction.getTransactionId())
					.amount(transaction.getAmount())
					.transactionTime(transaction.getTransactionTime())
					.build();
		}
		
		
	}

}
