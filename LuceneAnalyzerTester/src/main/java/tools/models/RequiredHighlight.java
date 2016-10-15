package tools.models;

import java.io.Serializable;

@SuppressWarnings("serial")
public final class RequiredHighlight implements Serializable {
	
	private String fieldName;
	private String value;
	private String searchLink;
	
	public RequiredHighlight() {
		super();
	}

	public RequiredHighlight(String fieldName, String value, String searchLink) {
		super();
		this.fieldName = fieldName;
		this.value = value;
		this.searchLink = searchLink;
	}

	public String getSearchLink() {
		return searchLink;
	}

	public void setSearchLink(String searchLink) {
		this.searchLink = searchLink;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getValue() {
		return value;
	}

}
