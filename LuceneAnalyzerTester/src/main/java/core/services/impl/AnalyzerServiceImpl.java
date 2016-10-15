package core.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import core.models.entities.Analyzer;
import core.repositories.AnalyzerRepo;
import core.services.AnalyzerService;

@Service
@Transactional
public class AnalyzerServiceImpl implements AnalyzerService{

	@Autowired
	private AnalyzerRepo repo;
	
	public Analyzer findAnalyzer(long id) {
		return repo.findAnalyzer(id);
	}
	
	public Analyzer createAnalyzer(Analyzer data) {
		return repo.createAnalyzer(data);
	}

	public Analyzer updateAnalyzer(Analyzer data) {
		return repo.updateAnalyzer(data);
	}

	public Analyzer removeAnalyzer(Analyzer data) {
		return repo.removeAnalyzer(data);
	}

	public List<Analyzer> getAllAnalyzers() {
		return repo.getAllAnalyzers();
	}

}
