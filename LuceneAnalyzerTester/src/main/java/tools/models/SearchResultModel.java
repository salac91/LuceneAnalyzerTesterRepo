package tools.models;

import java.util.ArrayList;
import java.util.List;

import tools.models.AnalysisResultModel;
import tools.models.DocumentModel;
import tools.models.RequiredHighlight;

public class SearchResultModel {
	
	private SearchModel searchModel;
	
	private List<DocumentModel> documents = new ArrayList<DocumentModel>();
	
	private List<DocumentModel> restDocuments = new ArrayList<DocumentModel>();
	
	private List<DocumentModel> documentsBenchmark = new ArrayList<DocumentModel>();
	
	private List<DocumentModel> restDocumentsBenchmark = new ArrayList<DocumentModel>();
	
	private AnalysisResultModel analysisResultModel = new AnalysisResultModel();
	
	private List<RequiredHighlight> suggestions = new ArrayList<RequiredHighlight>();

	public SearchModel getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(SearchModel searchModel) {
		this.searchModel = searchModel;
	}

	public List<DocumentModel> getDocuments() {
		return documents;
	}

	public void setDocuments(List<DocumentModel> documents) {
		this.documents = documents;
	}

	public List<DocumentModel> getRestDocuments() {
		return restDocuments;
	}

	public void setRestDocuments(List<DocumentModel> restDocuments) {
		this.restDocuments = restDocuments;
	}

	public List<DocumentModel> getDocumentsBenchmark() {
		return documentsBenchmark;
	}

	public void setDocumentsBenchmark(List<DocumentModel> documentsBenchmark) {
		this.documentsBenchmark = documentsBenchmark;
	}

	public List<DocumentModel> getRestDocumentsBenchmark() {
		return restDocumentsBenchmark;
	}

	public void setRestDocumentsBenchmark(List<DocumentModel> restDocumentsBenchmark) {
		this.restDocumentsBenchmark = restDocumentsBenchmark;
	}

	public AnalysisResultModel getAnalysisResultModel() {
		return analysisResultModel;
	}

	public void setAnalysisResultModel(AnalysisResultModel analysisResultModel) {
		this.analysisResultModel = analysisResultModel;
	}

	public List<RequiredHighlight> getSuggestions() {
		return suggestions;
	}

	public void setSuggestions(List<RequiredHighlight> suggestions) {
		this.suggestions = suggestions;
	}
	
}
