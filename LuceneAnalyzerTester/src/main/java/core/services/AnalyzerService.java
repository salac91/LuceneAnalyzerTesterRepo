package core.services;

import java.util.List;

import core.models.entities.Analyzer;

public interface AnalyzerService {
	public Analyzer findAnalyzer(long id);
	public Analyzer createAnalyzer(Analyzer data);
    public Analyzer updateAnalyzer(Analyzer data);
    public Analyzer removeAnalyzer(Analyzer data);
    public List<Analyzer> getAllAnalyzers();
}
