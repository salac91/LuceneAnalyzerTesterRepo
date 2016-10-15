package core.services;

import java.util.List;
import java.util.Set;

import core.models.entities.Account;
import core.models.entities.Benchmark;

public interface AccountService {
	public List<Account> findAllAccounts();
    public Account findAccount(Long id);
    public Account findAccountByUsername(String username);
    public Account createAccount(Account data);
    public Account updateAccount(Account data);
    public Account removeAccount(Account data);
    public Set<Benchmark> getAllBenchmarksForThisAccount(String username);
}
