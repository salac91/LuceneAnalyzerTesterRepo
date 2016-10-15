package tools.indexer;

public class MyIndexManager {

	private UDDIndexer indexer;
	
	public MyIndexManager() {
		indexer = new UDDIndexer(true);
	}
	public void restart() {
		indexer = new UDDIndexer(true);
	}
	public UDDIndexer getIndexer() {
		return indexer;
	}
	public void setIndexer(UDDIndexer indexer) {
		this.indexer = indexer;
	}
}
