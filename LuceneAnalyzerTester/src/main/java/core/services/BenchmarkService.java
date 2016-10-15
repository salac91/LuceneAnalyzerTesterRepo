package core.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import core.models.entities.Benchmark;
import core.models.entities.RelevantDocument;

@Service
@Transactional
public interface BenchmarkService {
	public Benchmark findBenchmark(long id);
	public Benchmark updateBenchmark(Benchmark data);
	public Benchmark removeBenchmark(Benchmark data);
	public Benchmark createBenchmark(Benchmark data);
	public List<RelevantDocument> getAllRelevantDocs(long benchmarkId);
	public RelevantDocument addRelevantDocument(RelevantDocument data);
	public void removeAllDocumentPaths(long benchmark_id);
	public void removeAllQueriesAndRelevantDocuments(long benchmark_id);
}
