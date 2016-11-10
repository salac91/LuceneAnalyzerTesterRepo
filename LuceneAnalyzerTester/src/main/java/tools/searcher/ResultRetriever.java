
package tools.searcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;


public class ResultRetriever {
	
	private static int maxHits = 10;
	private static final Version matchVersion = Version.LUCENE_4_9;
	private static QueryParser queryParser;
	
	public static void setMaxHits(int maxHits){
		ResultRetriever.maxHits = maxHits;
	}
	
	public static int getMaxHits(){
		return ResultRetriever.maxHits;
	}
	
	public static List<Document> getResults(Query query){
		String path = ResourceBundle.getBundle("index").getString("index");
		File indexDirPath = new File(path);
		return getResults(query, indexDirPath, false, null, null, null);
	}
	
	public static List<Document> getResults(Query query, Filter filter, Analyzer analyzer, String indexDir){
		File indexFile = new File(indexDir);
		return getResults(query, indexFile, false, null, filter, analyzer);
	}

	public static List<Document> getResults(Query query, File indexFile, boolean analyzeQuery, Sort sort, Filter filter, Analyzer analyzer){
		if(query == null){
			return null;
		}
		try {
			if(analyzeQuery){
					queryParser = new QueryParser(matchVersion, "", analyzer);
					query = queryParser.parse(query.toString());				
			}
			Directory indexDir = new SimpleFSDirectory(indexFile);
			DirectoryReader reader = DirectoryReader.open(indexDir);
			IndexSearcher is = new IndexSearcher(reader);
			
			List<Document> docs = new ArrayList<Document>();
			if(sort == null){
				sort = Sort.INDEXORDER;
			}
			ScoreDoc[] scoreDocs;
			if(filter == null)
				scoreDocs = is.search(query, maxHits, sort).scoreDocs;
			else 
				scoreDocs = is.search(query, filter, maxHits, sort).scoreDocs;
			
			for(ScoreDoc sd : scoreDocs){
				docs.add(is.doc(sd.doc));
			}
			return docs;
		} catch (ParseException e) {
			throw new IllegalArgumentException("Upit nije moguce parsirati");
		} catch (IOException e) {
			throw new IllegalArgumentException("U prosledjenom direktorijumu ne postoje indeksi ili je direktorijum zakljucan");
		}
	}

}
