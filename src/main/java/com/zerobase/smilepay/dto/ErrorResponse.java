package com.zerobase.smilepay.dto;

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
public class ErrorResponse {
	private ErrorCode errorCode;
	private String errorMessage;
}
