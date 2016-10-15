package tools.searcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import tools.indexer.UDDIndexer;
import tools.models.DocumentModel;
import tools.models.RequiredHighlight;

public class InformationRetriever {
	
	private static int maxHits = 6;
	private static final Version matchVersion = Version.LUCENE_4_9;
	
	public static List<DocumentModel> getData(Query query, List<RequiredHighlight> requiredHighlights, Filter filter, Analyzer analyzer, String indexPath){
		if(query == null) return null;
		List<Document> docs = ResultRetriever.getResults(query,filter,analyzer,indexPath);
		List<DocumentModel> results = new ArrayList<DocumentModel>();
		
		String temp;
		DocumentModel data;
		Highlighter hl;
		DirectoryReader reader;
		try {
			reader = DirectoryReader.open(new SimpleFSDirectory(new File(indexPath)));
			for(Document doc : docs){
				data = new DocumentModel();
				
				data.setUid(doc.get("id"));
				
				String[] allKeywords = doc.getValues("keyword");
				temp = "";
				for(String keyword : allKeywords){
					temp += keyword + ", ";
				}
				if(!temp.equals("")){
					temp = temp.substring(0, temp.length()-2);
				}
				data.setKeyWords(temp);
				
				data.setTitle(doc.get("title"));
				data.setAuthor(doc.get("author"));
				String location = doc.get("location");
				File file = new File(location);
				data.setLocation(location);
				data.setFileName(file.getName());
				
				String[] allTags = doc.getValues("tags");
				temp = "";
				for(String tag : allTags){
					temp += tag + ", ";
				}
				if(!temp.equals("")){
					temp = temp.substring(0, temp.length()-2);
				}
				data.setTags(temp);
				
				String[] categories = doc.getValues("category");
				temp = "";
				for(String category : categories){
					temp += category + ", ";
				}
				if(!temp.equals("")){
					temp = temp.substring(0, temp.length()-2);
				}
				data.setCategory(temp);
				
				data.setApstract(doc.get("apstract"));
					
				data.setText(doc.get("text"));
				//data.setLocation(doc.get("location"));
				//data.setFileName(doc.get("fileName"));
				data.setRelevant(true);
				
				temp = "";
				if(requiredHighlights != null){
					for(RequiredHighlight rh : requiredHighlights){
						try{
							hl = new Highlighter(new QueryScorer(query, reader, rh.getFieldName()));
							File docFile = new File(doc.get("location"));
							String value = UDDIndexer.getHandler(docFile).getDocument(docFile).get(rh.getFieldName());
							String tempHL = hl.getBestFragment(analyzer, rh.getFieldName(), value);
							if(tempHL!=null){
								temp += rh.getFieldName() + ": " + tempHL.trim() + " ... ";
							}
						}catch(Exception e){
							
						}
					}
				}
				data.setHighlight(temp);
				results.add(data);
			}
			reader.close();
			return results;
		} catch (IOException e) {
		}
		throw new IllegalArgumentException("U prosledjenom direktorijumu ne postoje indeksi ili je direktorijum zakljucan");
	}
	
	public static List<RequiredHighlight> getSuggestions(Term term, ServletContext servletContext, String indexDir, Analyzer analyzer) throws IOException {	
		String realPath = servletContext.getRealPath("/spellchecker");
	    IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexDir)));
	    File dir = new File(realPath);
	    Directory directory = FSDirectory.open(dir);

	    final Dictionary dictionary = new LuceneDictionary(reader,
	            term.field());
	    final SpellChecker spellChecker = new SpellChecker(directory);		  
	    final IndexWriterConfig writerConfig = new IndexWriterConfig(
	    		matchVersion, analyzer);
	    spellChecker.indexDictionary(dictionary, writerConfig, true);		    
	    
	    final String[] similarWords = spellChecker.suggestSimilar(
	    		term.text(), maxHits, 0.5f);
	    
	    List<RequiredHighlight> suggestedTerms = new ArrayList<RequiredHighlight>();
	    for(String sw : similarWords){
			suggestedTerms.add(new RequiredHighlight(term.field(), sw, null));
		}
	    spellChecker.close();
		return suggestedTerms;
		
    }

}
