package tools.models;

public class DirectoryModel {

	private String name;
	
	private String path;
	
	private long filesNumber;
	
	private String creationDate;
	
	private String createdBy;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getFilesNumber() {
		return filesNumber;
	}

	public void setFilesNumber(long filesNumber) {
		this.filesNumber = filesNumber;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
}
