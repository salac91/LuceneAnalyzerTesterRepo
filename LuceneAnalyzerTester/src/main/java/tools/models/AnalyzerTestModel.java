package tools.models;

import java.util.ArrayList;
import java.util.List;

public class AnalyzerTestModel {

	private SearchModel searchModel;
	
	private String analyzerType;
	
	private List<String> relevantDocuments = new ArrayList<String>();
	
	private String indexDir;
	
	public SearchModel getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(SearchModel searchModel) {
		this.searchModel = searchModel;
	}

	public String getAnalyzerType() {
		return analyzerType;
	}

	public void setAnalyzerType(String analyzerType) {
		this.analyzerType = analyzerType;
	}

	public List<String> getRelevantDocuments() {
		return relevantDocuments;
	}

	public void setRelevantDocuments(List<String> relevantDocuments) {
		this.relevantDocuments = relevantDocuments;
	}

	public String getIndexDir() {
		return indexDir;
	}

	public void setIndexDir(String indexDir) {
		this.indexDir = indexDir;
	}
		
}
