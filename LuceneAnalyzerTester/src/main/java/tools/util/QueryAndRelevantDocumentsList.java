package tools.util;

import java.util.ArrayList;
import java.util.List;

import tools.models.QueryAndRelevantDocumentsModel;

public class QueryAndRelevantDocumentsList {

	private List<QueryAndRelevantDocumentsModel> queryAndRelevantDocumentsList = new ArrayList<QueryAndRelevantDocumentsModel>();

	public List<QueryAndRelevantDocumentsModel> getQueryAndRelevantDocumentsList() {
		return queryAndRelevantDocumentsList;
	}

	public void setQueryAndRelevantDocumentsList(List<QueryAndRelevantDocumentsModel> queryAndRelevantDocumentsList) {
		this.queryAndRelevantDocumentsList = queryAndRelevantDocumentsList;
	}

}
