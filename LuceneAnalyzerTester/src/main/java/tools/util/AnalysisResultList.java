package tools.util;

import java.util.ArrayList;
import java.util.List;

import tools.models.SearchResultModel;

public class AnalysisResultList {

	private List<SearchResultModel> analysisResultList = new ArrayList<SearchResultModel>();

	public List<SearchResultModel> getAnalysisResultList() {
		return analysisResultList;
	}

	public void setAnalysisResultList(List<SearchResultModel> analysisResultList) {
		this.analysisResultList = analysisResultList;
	}
	
}
