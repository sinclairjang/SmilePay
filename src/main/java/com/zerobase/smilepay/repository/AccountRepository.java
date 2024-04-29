package com.zerobase.smilepay.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zerobase.smilepay.domain.Account;
import com.zerobase.smilepay.domain.AccountUser;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

	Optional<Account> findFirstByOrderByIdDesc();
	
	Integer countByAccountUser(AccountUser accountUser);

	Optional<Account> findByAccountNumber(String accountNumber);


	List<Account> findByAccountUser(AccountUser accountUser);
}
