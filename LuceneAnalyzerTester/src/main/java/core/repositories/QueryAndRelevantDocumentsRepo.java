package core.repositories;

import core.models.entities.QueryAndRelevantDocuments;
import core.models.entities.RelevantDocument;

public interface QueryAndRelevantDocumentsRepo {

	public QueryAndRelevantDocuments findQueryAndRelevantDocuments(long id);
	public QueryAndRelevantDocuments createQueryAndRelevantDocuments(QueryAndRelevantDocuments data);
	public QueryAndRelevantDocuments updateQueryAndRelevantDocuments(QueryAndRelevantDocuments data);
	public QueryAndRelevantDocuments removeQueryAndRelevantDocuments(QueryAndRelevantDocuments data);
	public void removeAllRelevantDocuments(long queryAndRelevantDocuments_Id);
	public RelevantDocument createRelevantDocument(RelevantDocument data);
	public RelevantDocument removeRelevantDocument(RelevantDocument data);
}
