package core.repositories.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import core.models.entities.Analyzer;
import core.repositories.AnalyzerRepo;

@Repository
public class JpaAnalyzerRepo implements AnalyzerRepo {

	@PersistenceContext
	private EntityManager em;
	
	public Analyzer findAnalyzer(long id) {	
		return em.find(Analyzer.class, id);
	}
	
	public Analyzer createAnalyzer(Analyzer data) {
		em.persist(data);
		return data;
	}

	public Analyzer updateAnalyzer(Analyzer data) {
		em.merge(data);
		return data;
	}

	public Analyzer removeAnalyzer(Analyzer data) {
		em.remove(em.merge(data));
		return data;
	}

	@SuppressWarnings("unchecked")
	public List<Analyzer> getAllAnalyzers() {
		Query query = em.createQuery("SELECT a FROM Analyzer a");
        return query.getResultList();
	}

}
