package com.zerobase.smilepay.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.zerobase.smilepay.aop.AccountLock;
import com.zerobase.smilepay.dto.Cancel;
import com.zerobase.smilepay.dto.CheckBalance;
import com.zerobase.smilepay.dto.Pay;
import com.zerobase.smilepay.exception.AccountException;
import com.zerobase.smilepay.service.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionController {
	private final TransactionService transactionService;
	
	@PostMapping("/transaction/pay")
	@AccountLock
	public Pay.Response pay(
		@Valid @RequestBody Pay.Request request
	) {
		try {
			return Pay.Response.fromEntity(
					transactionService.makePayment(
							request.getUserId(), 
							request.getAccountNumber(), 
							request.getAmount())
					);
		} catch (AccountException e) {
			log.error(e.getErrorMessage());
			
			
			Pay.Response.fromEntity(
					transactionService.saveFailedTransaction(
						request.getAccountNumber(), 
						request.getAmount())
					);
			 
			 throw e;
		}
	}
	
	@PostMapping("/transaction/cancel")
	@AccountLock
	public Cancel.Response cancel(
		@Valid @RequestBody Cancel.Request request
	) {
		try {
			return Cancel.Response.fromEntity(
					transactionService.cancelPayment(
							request.getTransactionId(), 
							request.getAccountNumber(), 
							request.getAmount())
					);
		} catch (AccountException e) {
			log.error(e.getErrorMessage());
			
			
			Cancel.Response.fromEntity(
					transactionService.saveFailedCancelTransaction(
						request.getAccountNumber(), 
						request.getAmount())
					);
			 
			 throw e;
		}
	}
	
	@GetMapping("/transaction/{transactionId}")
	public CheckBalance.Response checkBalance(
			@PathVariable("transactionId") String transactionId
	) {
		return CheckBalance.Response
				.fromEntity(transactionService.queryTransaction(transactionId));
	}
	
}
