package com.zerobase.smilepay.dto;

import java.time.LocalDateTime;

import com.zerobase.smilepay.domain.Transaction;
import com.zerobase.smilepay.domain.type.TransactionResult;
import com.zerobase.smilepay.domain.type.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CheckBalance {
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response {
		private String accountNumber;
		private TransactionType transactionType;
		private TransactionResult transactionResult;
		private String transactionId;
		private Long amount;
		private LocalDateTime transactionTime;
		public static Response fromEntity(Transaction transaction) {
			
			return Response.builder()
					.accountNumber(transaction.getAccount().getAccountNumber())
					.transactionType(transaction.getTransactionType())
					.transactionResult(transaction.getTransactionResult())
					.transactionId(transaction.getTransactionId())
					.amount(transaction.getAmount())
					.transactionTime(transaction.getTransactionTime())
					.build();
		}
	}
}
