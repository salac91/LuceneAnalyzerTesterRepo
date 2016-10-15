package tools.models;

import java.util.ArrayList;
import java.util.List;

public class RelevantDocumentsModel {

	private List<DocumentModel> relevantDocs = new ArrayList<DocumentModel>();
	
	private List<DocumentModel> restDocs = new ArrayList<DocumentModel>();

	public List<DocumentModel> getRelevantDocs() {
		return relevantDocs;
	}

	public void setRelevantDocs(List<DocumentModel> relevantDocs) {
		this.relevantDocs = relevantDocs;
	}

	public List<DocumentModel> getRestDocs() {
		return restDocs;
	}

	public void setRestDocs(List<DocumentModel> restDocs) {
		this.restDocs = restDocs;
	}	
	
}
