package tools.util;

import java.util.ArrayList;
import java.util.List;

import tools.models.BenchmarkModel;

public class BenchmarkList {

	List<BenchmarkModel> benchmarks = new ArrayList<BenchmarkModel>();

	public List<BenchmarkModel> getBenchmarks() {
		return benchmarks;
	}

	public void setBenchmarks(List<BenchmarkModel> benchmarks) {
		this.benchmarks = benchmarks;
	}
	
	
}
