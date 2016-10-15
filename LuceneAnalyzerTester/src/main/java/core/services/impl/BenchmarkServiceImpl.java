package core.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import core.models.entities.Benchmark;
import core.models.entities.RelevantDocument;
import core.repositories.BenchmarkRepo;
import core.services.BenchmarkService;

@Service
@Transactional
public class BenchmarkServiceImpl implements BenchmarkService {
	
	@Autowired
	private BenchmarkRepo repo;

	@Override
	public Benchmark createBenchmark(Benchmark data) {
		
		return repo.createBenchmark(data);
	}

	@Override
	public List<RelevantDocument> getAllRelevantDocs(long benchmarkId) {
		
		return repo.getAllRelevantDocs(benchmarkId);
	}

	@Override
	public RelevantDocument addRelevantDocument(RelevantDocument data) {
	
		return repo.addRelevantDocument(data);
	}

	@Override
	public Benchmark findBenchmark(long id) {
		
		return repo.findBenchmark(id);
	}

	@Override
	public Benchmark updateBenchmark(Benchmark data) {
		
		return repo.updateBenchmark(data);
	}

	@Override
	public Benchmark removeBenchmark(Benchmark data) {
		
		return repo.removeBenchmark(data);
	}

	@Override
	public void removeAllDocumentPaths(long benchmark_id) {
		repo.removeAllDocumentPaths(benchmark_id);		
	}

	@Override
	public void removeAllQueriesAndRelevantDocuments(long benchmark_id) {
		repo.removeAllQueriesAndRelevantDocuments(benchmark_id);		
	}
}
