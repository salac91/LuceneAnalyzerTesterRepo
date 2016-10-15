package core.services.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import core.models.entities.Account;
import core.models.entities.Benchmark;
import core.repositories.AccountRepo;
import core.services.AccountService;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepo repo;

    public Account findAccount(Long id) {
        return repo.findAccount(id);
    }

    public Account createAccount(Account data) {
        Account account = repo.findAccountByUsername(data.getUsername());
        if(account == null)
        	return repo.createAccount(data);
        else 
        	return null;
    }

    public List<Account> findAllAccounts() {
        return repo.findAllAccounts();
    }

    public Account findAccountByUsername(String username) {
        return repo.findAccountByUsername(username);
    }
    
    public Account updateAccount(Account data) {
    	return repo.updateAccount(data);
    }
    
    public Account removeAccount(Account data) {
    	return repo.removeAccount(data);
    }

	public Set<Benchmark> getAllBenchmarksForThisAccount(String username) {		
		
		return repo.getAllBenchmarksForThisAccount(username);
	}
    
}