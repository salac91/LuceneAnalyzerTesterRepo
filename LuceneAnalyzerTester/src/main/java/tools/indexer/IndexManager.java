package tools.indexer;

public class IndexManager {
	
	private static UDDIndexer indexer = new UDDIndexer(true);
	
	public static UDDIndexer getIndexer(){
		if(indexer == null){
			indexer = new UDDIndexer(true);
		}
		return indexer;
	}
	
	public static UDDIndexer getIndexer(String indexDir){
		if(indexer == null){
			indexer = new UDDIndexer(indexDir,true);
		}
		return indexer;
	}
	
	public static void restart() {
		indexer = new UDDIndexer(true);
	}

}
