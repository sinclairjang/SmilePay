package com.zerobase.smilepay.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	USER_NOT_FOUND("사용자가 없습니다."),
	EXCEEDED_ALLOWABLE_ACCOUNTS("허용 계좌 개수를 초과했습니다."), 
	ACCOUNT_NOT_FOUND("계좌가 없습니다."), 
	ACCOUNT_MISMATCH("사용자와 계좌 소유주 일치하지 않습니다."),
	ACCOUNT_ALREADY_UNREGISTERED("계좌가 이미 해지되었습니다."), 
	ACCOUNT_NOT_EMPTY("잔액이 있는 계좌는 해지할 수 없습니다."), 
	ACCOUNT_INSUFFICIENT_FUND("잔액이 부족합니다."), 
	
	TRANSACTION_NOT_FOUND("거래내역이 없습니다."), 
	TRANSACTION_MISMATCH("이 거래는 해당 계좌에서 발생한 거래가 아닙니다."), 
	
	TRANSACTION_NOT_FULL_AMOUNT("부분 취소는 허용되지 않습니다."), 
	TRANSACTION_OUTDATED("조회 가능한 날짜가 아닙니다."), 
	INVALID_REQUEST("잘못된 요청입니다."), 
	INTERNAL_SERVER_ERROR("내부 서버 오류가 발생했습니다."), 
	ACCOUNT_TRANSACTION_LOCK("해당 계좌는 사용중입니다.")
	;
	
	private final String errorDescription;
	
}
