package tools.util;

import java.util.ArrayList;
import java.util.List;

import tools.models.AnalyzerModel;

public class AnalyzerList {

	private List<AnalyzerModel> analyzers = new ArrayList<AnalyzerModel>();

	public List<AnalyzerModel> getAnalyzers() {
		return analyzers;
	}

	public void setAnalyzers(List<AnalyzerModel> analyzers) {
		this.analyzers = analyzers;
	}

}
