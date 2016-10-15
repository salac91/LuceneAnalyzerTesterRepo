package core.repositories;

import java.util.List;

import core.models.entities.Analyzer;

public interface AnalyzerRepo {
		public Analyzer findAnalyzer(long id);
		public Analyzer createAnalyzer(Analyzer data);
	    public Analyzer updateAnalyzer(Analyzer data);
	    public Analyzer removeAnalyzer(Analyzer data);
	    public List<Analyzer> getAllAnalyzers();
}
