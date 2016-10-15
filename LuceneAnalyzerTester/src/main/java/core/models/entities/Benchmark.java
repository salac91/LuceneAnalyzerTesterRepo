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
public class Benchmark {
	
	@Id @GeneratedValue
	private Long benchmark_id;
	
	private String name;
	
	private long analyzerType;
	
	private String analyzerName;
	
	private String directoryName;
	
	@OneToMany(mappedBy="benchmark", cascade=CascadeType.PERSIST)
	private Set<QueryAndRelevantDocuments> queryAndRelevantDocumentsList = new HashSet<QueryAndRelevantDocuments>();
	
	@OneToMany(mappedBy="benchmark", cascade=CascadeType.PERSIST)
	private Set<DocumentPath> allDocumentsPath = new HashSet<DocumentPath>();
	
	private String indexDir;
	
	@ManyToOne
	@JoinColumn(name="account_id")
	private Account account;	

	public Long getBenchmark_id() {
		return benchmark_id;
	}

	public void setBenchmark_id(Long benchmark_id) {
		this.benchmark_id = benchmark_id;
	}
	
	public String getDirectoryName() {
		return directoryName;
	}

	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getAnalyzerType() {
		return analyzerType;
	}

	public void setAnalyzerType(long analyzerType) {
		this.analyzerType = analyzerType;
	}

	public String getAnalyzerName() {
		return analyzerName;
	}

	public void setAnalyzerName(String analyzerName) {
		this.analyzerName = analyzerName;
	}

	public Set<QueryAndRelevantDocuments> getQueryAndRelevantDocumentsList() {
		return queryAndRelevantDocumentsList;
	}

	public void setQueryAndRelevantDocumentsList(Set<QueryAndRelevantDocuments> queryAndRelevantDocumentsList) {
		this.queryAndRelevantDocumentsList = queryAndRelevantDocumentsList;
	}

	public Set<DocumentPath> getAllDocumentsPath() {
		return allDocumentsPath;
	}

	public void setAllDocumentsPath(Set<DocumentPath> allDocumentsPath) {
		this.allDocumentsPath = allDocumentsPath;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getIndexDir() {
		return indexDir;
	}

	public void setIndexDir(String indexDir) {
		this.indexDir = indexDir;
	}
	
}
