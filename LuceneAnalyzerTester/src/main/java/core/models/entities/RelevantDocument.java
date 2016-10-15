package core.models.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity
public class RelevantDocument {

	@Id @GeneratedValue
	private Long relevantDocument_id;
	
	@ManyToOne
	@JoinColumn(name="queryAndRelevantDocuments_Id")
	private QueryAndRelevantDocuments queryAndRelevantDocuments;
	
	private String uid;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Long getRelevantDocument_id() {
		return relevantDocument_id;
	}

	public void setRelevantDocument_id(Long relevantDocument_id) {
		this.relevantDocument_id = relevantDocument_id;
	}

	public QueryAndRelevantDocuments getQueryAndRelevantDocuments() {
		return queryAndRelevantDocuments;
	}

	public void setQueryAndRelevantDocuments(QueryAndRelevantDocuments queryAndRelevantDocuments) {
		this.queryAndRelevantDocuments = queryAndRelevantDocuments;
	}
	
}
