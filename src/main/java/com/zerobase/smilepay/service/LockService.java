package com.zerobase.smilepay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.zerobase.smilepay.exception.AccountException;
import com.zerobase.smilepay.exception.type.ErrorCode;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {
    private final RedissonClient redissonClient;

    public void lock(String accountNumber) {
        RLock lock = redissonClient.getLock(getLockkey(accountNumber));
        log.debug("Trying lock : {}", accountNumber);
        
        try {
            boolean isLock = lock.tryLock(1, 5, TimeUnit.SECONDS);
            if(!isLock) {
                log.error("======Lock acquisition failed=====");
                throw new AccountException(ErrorCode.ACCOUNT_TRANSACTION_LOCK);
            }
        } catch (Exception e) {
            log.error("Redis lock failed");
        }
    }

	private String getLockkey(String accountNumber) {
		return "ACLK:" + accountNumber;
	}
    
    public void unlock(String accountNumber) {
    	 log.debug("releasing lock : {}", accountNumber);
    	 redissonClient.getLock(getLockkey(accountNumber)).unlock();
    }
    
}
