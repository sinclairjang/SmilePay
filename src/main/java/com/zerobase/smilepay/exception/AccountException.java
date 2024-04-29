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
public class AccountException extends RuntimeException {
	private static final long serialVersionUID = 5413732155439711244L;
	private ErrorCode errorCode;
	private String errorMessage;
	
	public AccountException(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.errorMessage = errorCode.getErrorDescription();
	}
}