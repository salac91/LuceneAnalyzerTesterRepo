package tools.models;

import java.util.ArrayList;
import java.util.List;

public class QueryAndRelevantDocumentsModel {

	private Long queryAndRelevantDocuments_Id;
	
	private long benchmark_id;
	
	private SearchModel searchModel;
	
	private List<String> relevantDocuments = new ArrayList<String>();
	
	private long numOfRelevant;
	
	private long allBenchmarkDocuments;

	public Long getQueryAndRelevantDocuments_Id() {
		return queryAndRelevantDocuments_Id;
	}

	public void setQueryAndRelevantDocuments_Id(Long queryAndRelevantDocuments_Id) {
		this.queryAndRelevantDocuments_Id = queryAndRelevantDocuments_Id;
	}

	public long getBenchmark_id() {
		return benchmark_id;
	}

	public void setBenchmark_id(long benchmark_id) {
		this.benchmark_id = benchmark_id;
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

	public long getNumOfRelevant() {
		return numOfRelevant;
	}

	public void setNumOfRelevant(long numOfRelevant) {
		this.numOfRelevant = numOfRelevant;
	}

	public long getAllBenchmarkDocuments() {
		return allBenchmarkDocuments;
	}

	public void setAllBenchmarkDocuments(long allBenchmarkDocuments) {
		this.allBenchmarkDocuments = allBenchmarkDocuments;
	}
	
}
