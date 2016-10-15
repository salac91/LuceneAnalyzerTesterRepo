package core.models.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class DocumentPath {
	
	@Id @GeneratedValue
	private Long documentPath_id;
	
	private String path;
	
	@ManyToOne
	@JoinColumn(name="benchmark_id")
	private Benchmark benchmark;

	public Long getDocumentPath_id() {
		return documentPath_id;
	}

	public void setDocumentPath_id(Long documentPath_id) {
		this.documentPath_id = documentPath_id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Benchmark getBenchmark() {
		return benchmark;
	}

	public void setBenchmark(Benchmark benchmark) {
		this.benchmark = benchmark;
	}


}
