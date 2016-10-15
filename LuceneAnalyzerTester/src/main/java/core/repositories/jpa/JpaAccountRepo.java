package core.repositories.jpa;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import core.models.entities.Account;
import core.models.entities.Benchmark;
import core.repositories.AccountRepo;

@Repository
public class JpaAccountRepo implements AccountRepo {

	@PersistenceContext
	private EntityManager em;

    @SuppressWarnings("unchecked")
	public List<Account> findAllAccounts() {
        Query query = em.createQuery("SELECT a FROM Account a");
        return query.getResultList();
    }

    public Account findAccount(Long id) {
        return em.find(Account.class, id);
    }

    @SuppressWarnings("unchecked")
	public Account findAccountByUsername(String usename) {
        Query query = em.createQuery("SELECT a FROM Account a WHERE a.username=?1");
        query.setParameter(1, usename);
        List<Account> accounts = query.getResultList();
        if(accounts.size() == 0) {
            return null;
        } else {
            return accounts.get(0);
        }
    }

    public Account createAccount(Account data) {
        em.persist(data);
        return data;
    }
    
    public Account updateAccount(Account data) {
        em.merge(data);
        return data;
    }

	public Account removeAccount(Account data) {
		em.remove(em.merge(data));
		return data;
	}

	@Override
	public Set<Benchmark> getAllBenchmarksForThisAccount(String username) {
		Account account = findAccountByUsername(username);
		return account.getBenchmarks();
	}


}
