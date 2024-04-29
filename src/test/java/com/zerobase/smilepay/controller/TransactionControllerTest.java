package com.zerobase.smilepay.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.smilepay.domain.Account;
import com.zerobase.smilepay.domain.AccountUser;
import com.zerobase.smilepay.domain.Transaction;
import com.zerobase.smilepay.domain.type.AccountStatus;
import com.zerobase.smilepay.domain.type.TransactionResult;
import com.zerobase.smilepay.domain.type.TransactionType;
import com.zerobase.smilepay.dto.Cancel;
import com.zerobase.smilepay.dto.Pay;
import com.zerobase.smilepay.service.TransactionService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {
	@MockBean
	private TransactionService transactionService;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void pay_success() throws Exception {
		//given
		LocalDateTime dt = LocalDateTime.now();
		given(transactionService.makePayment(anyLong(), anyString(), anyLong()))
				.willReturn(Transaction.builder()
						.transactionResult(TransactionResult.TRANSACTION_SUCCEDED)
						.account(Account.builder().accountNumber("1000000000").build())
						.amount(10000L)
						.transactionId("1")
						.transactionTime(dt)
						.build());
		
		//when, then
		mockMvc.perform(post("/transaction/pay")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(
						new Pay.Request(1L, "1000000000", 10000L)
					))
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.accountNumber").value("1000000000"))
				.andExpect(jsonPath("$.transactionResult").value("TRANSACTION_SUCCEDED"))
				.andExpect(jsonPath("$.transactionId").value("1"))
				.andExpect(jsonPath("$.amount").value(10000L))
				.andExpect(jsonPath("$.transactionTime").value(dt.toString()));
	}
	
	@Test
	void cancel_success() throws Exception {
		//given
		LocalDateTime dt = LocalDateTime.now();
		given(transactionService.cancelPayment(anyString(), anyString(), anyLong()))
				.willReturn(Transaction.builder()
						.transactionResult(TransactionResult.TRANSACTION_SUCCEDED)
						.account(Account.builder().accountNumber("1000000000").build())
						.amount(10000L)
						.transactionId("1")
						.transactionTime(dt)
						.build());
		
		//when, then
		mockMvc.perform(post("/transaction/cancel")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(
						new Cancel.Request("transactionId", "1000000000", 10000L)
					))
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.accountNumber").value("1000000000"))
				.andExpect(jsonPath("$.transactionResult").value("TRANSACTION_SUCCEDED"))
				.andExpect(jsonPath("$.transactionId").value("1"))
				.andExpect(jsonPath("$.amount").value(10000L))
				.andExpect(jsonPath("$.transactionTime").value(dt.toString()));
	}	
	
	@Test
	void checkBalance_success() throws Exception {
		
		//given
		AccountUser accountUser = AccountUser.builder()
				.id(10L)
				.name("Some name")
				.build();
		Account account = Account.builder()
				.id(124L)
				.accountUser(accountUser)
				.accountNumber("1000000000")
				.accountStatus(AccountStatus.IN_USE)
				.balance(98000L)
				.build();
		Transaction transaction = Transaction.builder()
				.id(1L)
				.transactionType(TransactionType.PAYMENT)
				.transactionResult(TransactionResult.TRANSACTION_SUCCEDED)
				.account(account)
				.amount(28900L)
				.balanceSnapshot(2459000L)
				.transactionId("1")
				.build();
		
		given(transactionService.queryTransaction(anyString()))
			.willReturn(transaction);		
		
		//when, then
		mockMvc.perform(get("/transaction/12345"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accountNumber").value("1000000000"))
			.andExpect(jsonPath("$.transactionType").value("PAYMENT"))
			.andExpect(jsonPath("$.transactionResult").value("TRANSACTION_SUCCEDED"))
			.andExpect(jsonPath("$.transactionId").value("1"))
			.andExpect(jsonPath("$.amount").value(28900L));
		

	}
}
