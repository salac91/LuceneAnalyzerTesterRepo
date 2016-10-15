package core.repositories;

import java.util.List;

import core.models.entities.Benchmark;
import core.models.entities.RelevantDocument;

public interface BenchmarkRepo {
	public Benchmark findBenchmark(long id);
	public Benchmark createBenchmark(Benchmark data);
	public Benchmark updateBenchmark(Benchmark data);
	public Benchmark removeBenchmark(Benchmark data);
	public List<RelevantDocument> getAllRelevantDocs(long benchmarkId);
	public RelevantDocument addRelevantDocument(RelevantDocument data);
	public void removeAllDocumentPaths(long benchmark_id);
	public void removeAllQueriesAndRelevantDocuments(long benchmark_id);
}
