package tools.models;

public class AnalyzerModel {

	private long analyzer_Id;
	
	private String name;
	
	private String description;
	
	private String path;

	public long getAnalyzer_Id() {
		return analyzer_Id;
	}

	public void setAnalyzer_Id(long analyzer_Id) {
		this.analyzer_Id = analyzer_Id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
}
