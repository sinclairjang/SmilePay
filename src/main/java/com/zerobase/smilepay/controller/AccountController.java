package com.zerobase.smilepay.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zerobase.smilepay.dto.CreateAccount;
import com.zerobase.smilepay.dto.DeleteAccount;
import com.zerobase.smilepay.dto.GetAccount;
import com.zerobase.smilepay.service.AccountService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AccountController {
	private final AccountService accountService;
	
	@GetMapping("/account")
	public List<GetAccount.Response> getAccount(
			@RequestParam("user_id") Long userId
	){
		return accountService.getAccountsByUserId(userId);
	}
	
	@PostMapping("/account")
	public CreateAccount.Response createAccount(
			@RequestBody @Valid CreateAccount.Request request
	) {
		
		return CreateAccount.Response.from(
				accountService.createAccount(
						request.getUserId(), 
						request.getInitialBalance()
				)
		);
	}
	
	@DeleteMapping("/account")
	public DeleteAccount.Response deleteAccount(
			@RequestBody @Valid DeleteAccount.Request request
	) {
		
		return DeleteAccount.Response.from(
				accountService.deleteAccount(
						request.getUserId(), 
						request.getAccountNumber()
				)
		);
	}
	
}
