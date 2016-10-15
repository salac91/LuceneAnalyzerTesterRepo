package core.models.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class QueryAndRelevantDocuments {
	
	@Id @GeneratedValue
	private Long queryAndRelevantDocuments_Id;

	private String text;
	
	private String textSearchType;
	
	@OneToMany(mappedBy="queryAndRelevantDocuments", cascade=CascadeType.PERSIST)
	private Set<RelevantDocument> relevantDocuments = new HashSet<RelevantDocument>();
	
	@ManyToOne
	@JoinColumn(name="benchmark_id")
	private Benchmark benchmark;

	public Long getQueryAndRelevantDocuments_Id() {
		return queryAndRelevantDocuments_Id;
	}

	public void setQueryAndRelevantDocuments_Id(Long queryAndRelevantDocuments_Id) {
		this.queryAndRelevantDocuments_Id = queryAndRelevantDocuments_Id;
	}

	public Benchmark getBenchmark() {
		return benchmark;
	}

	public void setBenchmark(Benchmark benchmark) {
		this.benchmark = benchmark;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTextSearchType() {
		return textSearchType;
	}

	public void setTextSearchType(String textSearchType) {
		this.textSearchType = textSearchType;
	}

	public Set<RelevantDocument> getRelevantDocuments() {
		return relevantDocuments;
	}

	public void setRelevantDocuments(Set<RelevantDocument> relevantDocuments) {
		this.relevantDocuments = relevantDocuments;
	}
	
	
}
