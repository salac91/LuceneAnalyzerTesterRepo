package tools.models;

import java.util.ArrayList;
import java.util.List;

import tools.models.SearchModel;

public class BenchmarkModel {
	
	private long benchmark_id;
	
	private String name;
	
	private long analyzerType;
	
	private String alayzerName;
	
	private String directoryName;

	private SearchModel searchModel;
	
	private List<String> relevantDocuments = new ArrayList<String>();
	
	private String indexDir;
	
	private List<QueryAndRelevantDocumentsModel> queryAndRelevantDocumentsList = new ArrayList<QueryAndRelevantDocumentsModel>();
	
	private long num_of_SearchQueries;
	
	public long getBenchmark_id() {
		return benchmark_id;
	}

	public void setBenchmark_id(long benchmark_id) {
		this.benchmark_id = benchmark_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public long getAnalyzerType() {
		return analyzerType;
	}

	public void setAnalyzerType(long analyzerType) {
		this.analyzerType = analyzerType;
	}

	public String getAlayzerName() {
		return alayzerName;
	}

	public void setAlayzerName(String alayzerName) {
		this.alayzerName = alayzerName;
	}

	public String getDirectoryName() {
		return directoryName;
	}

	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}

	public SearchModel getSearchModel() {
		return searchModel;
	}

	public void setSearchModel(SearchModel searchModel) {
		this.searchModel = searchModel;
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

	public long getNum_of_SearchQueries() {
		return num_of_SearchQueries;
	}

	public void setNum_of_SearchQueries(long num_of_SearchQueries) {
		this.num_of_SearchQueries = num_of_SearchQueries;
	}

	public List<QueryAndRelevantDocumentsModel> getQueryAndRelevantDocumentsList() {
		return queryAndRelevantDocumentsList;
	}

	public void setQueryAndRelevantDocumentsList(List<QueryAndRelevantDocumentsModel> queryAndRelevantDocumentsList) {
		this.queryAndRelevantDocumentsList = queryAndRelevantDocumentsList;
	}
	

}
