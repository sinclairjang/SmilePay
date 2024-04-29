package com.zerobase.smilepay.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.zerobase.smilepay.domain.type.TransactionResult;
import com.zerobase.smilepay.domain.type.TransactionType;

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
public class Transaction {
	@Id
	@GeneratedValue
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;
	
	@Enumerated(EnumType.STRING)	
	private TransactionResult transactionResult;
	
	@ManyToOne
	private Account account;
	
	private Long amount;
	
	private Long balanceSnapshot;
	
	private String transactionId;
	
	private LocalDateTime transactionTime;
	
	@CreatedDate
	private LocalDateTime entityCreatedTime;
	
	@LastModifiedBy
	private LocalDateTime entityModifiedTime;
	
}
