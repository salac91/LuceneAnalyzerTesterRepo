package core.repositories.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import core.models.entities.Benchmark;
import core.models.entities.DocumentPath;
import core.models.entities.QueryAndRelevantDocuments;
import core.models.entities.RelevantDocument;
import core.repositories.BenchmarkRepo;

@Repository
public class JpaBenchmarkRepo implements BenchmarkRepo {

	@PersistenceContext
	private EntityManager em;
	
	@Override
    public Benchmark createBenchmark(Benchmark data) {
		em.persist(data);
        return data;
	}

	@Override
	public List<RelevantDocument> getAllRelevantDocs(long benchmarkId) {
		 Query query = em.createQuery("SELECT r FROM RelevantDocument r WHERE r.benchmark_id=?1");
	     query.setParameter(1, benchmarkId);
	     @SuppressWarnings("unchecked")
		List<RelevantDocument> relevantDocs = query.getResultList();
		 return relevantDocs;
	}

	@Override
	public RelevantDocument addRelevantDocument(RelevantDocument data) {
		 em.persist(data);
         return data;
	}

	@Override
	public Benchmark findBenchmark(long id) {
		 return em.find(Benchmark.class, id);
	}

	@Override
	public Benchmark updateBenchmark(Benchmark data) {
		em.merge(data);
        return data;
	}

	@Override
	public Benchmark removeBenchmark(Benchmark data) {
		em.remove(em.contains(data) ? data : em.merge(data));
		return data;
	}
	
	public void removeAllDocumentPaths(long benchmark_id) {
		Query query = em.createQuery("SELECT dp FROM DocumentPath dp WHERE dp.benchmark.benchmark_id=?1");
		 query.setParameter(1, benchmark_id);
	     @SuppressWarnings("unchecked")
		 List<DocumentPath> documentPaths = query.getResultList();
	     for(DocumentPath documentPath : documentPaths) {
	    	 em.remove(documentPath);
	     }
	}
	
	public void removeAllQueriesAndRelevantDocuments(long benchmark_id) {
		 Query query = em.createQuery("SELECT qd FROM QueryAndRelevantDocuments qd WHERE qd.benchmark.benchmark_id=?1");
		 query.setParameter(1, benchmark_id);
	     @SuppressWarnings("unchecked")
		 List<QueryAndRelevantDocuments> queriesAndRelevantDocuments = query.getResultList();
	     for(QueryAndRelevantDocuments queryAndRelevantDocuments : queriesAndRelevantDocuments) {
	    	 Query query2 = em.createQuery("SELECT rd FROM RelevantDocument rd WHERE rd.queryAndRelevantDocuments.queryAndRelevantDocuments_Id=?1");
	    	 query2.setParameter(1, queryAndRelevantDocuments.getQueryAndRelevantDocuments_Id());
		     @SuppressWarnings("unchecked")
			 List<RelevantDocument> relevantDocuments = query2.getResultList();
		     for(RelevantDocument relevantDocument : relevantDocuments) {
		    	 em.remove(relevantDocument);
		     }
	    	 em.remove(queryAndRelevantDocuments);
	     }
	}

}
