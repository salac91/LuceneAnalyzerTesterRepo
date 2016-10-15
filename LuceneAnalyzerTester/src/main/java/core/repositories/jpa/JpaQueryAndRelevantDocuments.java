package core.repositories.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import core.models.entities.QueryAndRelevantDocuments;
import core.models.entities.RelevantDocument;
import core.repositories.QueryAndRelevantDocumentsRepo;

@Repository
public class JpaQueryAndRelevantDocuments implements QueryAndRelevantDocumentsRepo {

	@PersistenceContext
	private EntityManager em;

	@Override
	public QueryAndRelevantDocuments findQueryAndRelevantDocuments(long id) {
		
		return em.find(QueryAndRelevantDocuments.class, id);
	}

	@Override
	public QueryAndRelevantDocuments createQueryAndRelevantDocuments(QueryAndRelevantDocuments data) {
		em.persist(data);
		return data;
	}

	@Override
	public QueryAndRelevantDocuments updateQueryAndRelevantDocuments(QueryAndRelevantDocuments data) {
		em.merge(data);
		return data;
	}

	@Override
	public QueryAndRelevantDocuments removeQueryAndRelevantDocuments(QueryAndRelevantDocuments data) {
		em.remove(em.contains(data) ? data : em.merge(data));
		return data;
	}

	@Override
	public void removeAllRelevantDocuments(long queryAndRelevantDocuments_Id) {
		 Query query = em.createQuery("SELECT rd FROM RelevantDocument rd WHERE rd.queryAndRelevantDocuments.queryAndRelevantDocuments_Id=?1");
		 query.setParameter(1, queryAndRelevantDocuments_Id);
	     @SuppressWarnings("unchecked")
		 List<RelevantDocument> relevantDocuments = query.getResultList();
	     for(RelevantDocument relevantDocument : relevantDocuments) {
	    	 em.remove(relevantDocument);
	     }
		
	}
	
	public RelevantDocument createRelevantDocument(RelevantDocument data) {
		em.persist(data);
		return data;
		
	}
	
	public RelevantDocument removeRelevantDocument(RelevantDocument data) {
		em.remove(em.contains(data) ? data : em.merge(data));
		return data;
		
	}
	
}
