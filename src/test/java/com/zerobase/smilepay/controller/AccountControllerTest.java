package com.zerobase.smilepay.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.smilepay.domain.Account;
import com.zerobase.smilepay.domain.AccountUser;
import com.zerobase.smilepay.dto.AccountDto;
import com.zerobase.smilepay.dto.CreateAccount;
import com.zerobase.smilepay.dto.DeleteAccount;
import com.zerobase.smilepay.dto.GetAccount;
import com.zerobase.smilepay.service.AccountService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	 
	@MockBean
	private AccountService accountService;
	
	@Test
	void getAccountTest() throws Exception {
		//given
		List<GetAccount.Response> accounts = Arrays.asList(
				GetAccount.Response.builder()
					.accountNumber("1000000000")
					.balance(10000L)
					.build(),
				GetAccount.Response.builder()
					.accountNumber("1000000001")
					.balance(10000L)
					.build()
				);
				
		given(accountService.getAccountsByUserId(anyLong()))
			.willReturn(accounts);
		
		//when, then
		mockMvc.perform(get("/account?user_id=1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].accountNumber").value("1000000000"))
			.andExpect(jsonPath("$[0].balance").value(10000L))
			.andExpect(jsonPath("$[1].accountNumber").value("1000000001"))
			.andExpect(jsonPath("$[1].balance").value(10000L))
			.andDo(print());
	}
	
	@Test
	void createAccountTest() throws Exception {
		given(accountService.createAccount(anyLong(), anyLong()))
			.willReturn(AccountDto.builder()
					.userId(1L)
					.accountNumber("1234567890")
					.accountRegisteredAt(LocalDateTime.now())
					.accountUnregisteredAt(LocalDateTime.now())
					.build());
		
		mockMvc.perform(post("/account")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(
					new CreateAccount.Request(1L, 100L))))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").value(1))
			.andExpect(jsonPath("$.accountNumber").value("1234567890"))
			.andDo(print());
	}
	
	@Test
	void deleteAccountTest() throws Exception {
		//given
		String s1 = "1000000001";
		given(accountService.deleteAccount(anyLong(), anyString()))
			.willReturn(Account.builder()
					.id(1L)
					.accountUser(AccountUser.builder().id(5L).build())
					.accountNumber(s1)
					.accountUnregisteredAt(LocalDateTime.now())
					.build());
		//when
		String s2 = "1000000001";
		mockMvc.perform(delete("/account")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(
					new DeleteAccount.Request(5L, s2))))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").value(5))
			.andExpect(jsonPath("$.accountNumber").value("1000000001"))
			.andDo(print());
		//then
		assertEquals(s1, s2);
	}
	
	
}
