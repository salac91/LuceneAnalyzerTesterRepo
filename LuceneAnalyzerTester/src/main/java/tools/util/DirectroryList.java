package tools.util;

import java.util.ArrayList;
import java.util.List;

import tools.models.DirectoryModel;

public class DirectroryList {

	private List<DirectoryModel> directories = new ArrayList<DirectoryModel>();

	public List<DirectoryModel> getDirectories() {
		return directories;
	}

	public void setDirectories(List<DirectoryModel> directories) {
		this.directories = directories;
	}
	
}
