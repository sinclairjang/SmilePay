package com.zerobase.smilepay.domain;

import java.time.LocalDateTime;


import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.zerobase.smilepay.domain.type.AccountStatus;
import com.zerobase.smilepay.exception.AccountException;
import com.zerobase.smilepay.exception.type.ErrorCode;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Account {
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	private AccountUser accountUser;
	
	private String accountNumber;
	
	@Enumerated(EnumType.STRING)
	private AccountStatus accountStatus;
	
	private Long balance;
	
	private LocalDateTime accountRegisteredAt;
	
	private LocalDateTime accountUnregisteredAt;
	
	@CreatedDate
	private LocalDateTime accountCreateAt;
	
	@LastModifiedBy
	private LocalDateTime accountUpdatedAt;
	
	public void spend(Long amount) {
		if (amount > balance) {
			throw new AccountException(ErrorCode.ACCOUNT_INSUFFICIENT_FUND);
		}
		
		balance -= amount;
	}

	public void recover(Long amount) {
		if (amount < 0) {
			throw new AccountException(ErrorCode.INVALID_REQUEST);
		}
		
		balance += amount;
	}
	
}