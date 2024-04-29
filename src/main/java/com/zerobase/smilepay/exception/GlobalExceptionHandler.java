package com.zerobase.smilepay.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.zerobase.smilepay.dto.ErrorResponse;
import com.zerobase.smilepay.exception.type.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(AccountException.class)
	public ErrorResponse handleAccountException(AccountException e) {
		log.error("{} has occured!", e.getErrorCode());
		return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
	}
	
	@ExceptionHandler(TransactionException.class)
	public ErrorResponse handleAccountException(TransactionException e) {
		log.error("{} has occured!", e.getErrorCode());
		return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ErrorResponse handleAccountException(DataIntegrityViolationException e) {
		log.error("{} has occured!", e);
		return new ErrorResponse(ErrorCode.INVALID_REQUEST, 
				ErrorCode.INVALID_REQUEST.getErrorDescription());
	}
	
	@ExceptionHandler(Exception.class)
	public ErrorResponse handleAccountException(Exception e) {
		log.error("{} has occured!", e);
		return new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR,
				ErrorCode.INTERNAL_SERVER_ERROR.getErrorDescription());
	}
}

