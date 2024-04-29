package com.zerobase.smilepay.exception;

import com.zerobase.smilepay.exception.type.ErrorCode;

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
public class TransactionException extends RuntimeException {

	private static final long serialVersionUID = 718678350062631164L;
	
	private ErrorCode errorCode;
	
	private String errorMessage;
	
	public TransactionException(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.errorMessage = errorCode.getErrorDescription();
	}
	
}
