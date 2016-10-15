package tools.models;

public class SearchModel {
	
	private String text;
	
	private String textSearchType;
	
	private String indexDir;
	
	private long analyzerId;
	
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
	public String getIndexDir() {
		return indexDir;
	}
	public void setIndexDir(String indexDir) {
		this.indexDir = indexDir;
	}
	public long getAnalyzerId() {
		return analyzerId;
	}
	public void setAnalyzerId(long analyzerId) {
		this.analyzerId = analyzerId;
	}	
	
}
