package rest.mvc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import core.services.AnalyzerService;
import tools.indexer.UDDIndexer;
import tools.models.DocumentModel;
import tools.models.RequiredHighlight;
import tools.models.SearchModel;
import tools.util.SearchType;
import tools.query.QueryBuilder;
import tools.searcher.InformationRetriever;
import tools.models.SearchResultModel;


@Controller
@RequestMapping("/rest/search")
public class SearchController {
	
	@Autowired
	private AnalyzerService analyzerService;
	
	@Autowired
	ServletContext servletContext;
	
	@RequestMapping(value="/search",method = RequestMethod.POST)
	@PreAuthorize("hasRole('User','Admin')")
    public ResponseEntity<SearchResultModel> search(@RequestBody SearchModel searchModel) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		
		//dynamically load selected analyzer
		core.models.entities.Analyzer selectedAnalyzer = analyzerService.findAnalyzer(searchModel.getAnalyzerId());
				
		URL[] urls = { new URL("jar:file:" + selectedAnalyzer.getPath()+"!/") };
		@SuppressWarnings("resource")
		URLClassLoader cl = new URLClassLoader(urls,Thread.currentThread().getContextClassLoader());		

		@SuppressWarnings("unchecked")
		Class<org.apache.lucene.analysis.Analyzer> classToLoad = (Class<org.apache.lucene.analysis.Analyzer>) cl.loadClass(selectedAnalyzer.getName());
		org.apache.lucene.analysis.Analyzer analyzer =  (org.apache.lucene.analysis.Analyzer) classToLoad.newInstance();	
				
		String text = searchModel.getText();
		String textst = searchModel.getTextSearchType();
		SearchType.Type textSearchType = SearchType.getType(textst);			
    				
		SearchResultModel resultModel = new SearchResultModel();
		List<RequiredHighlight> suggestions = new ArrayList<RequiredHighlight>();
		
		UDDIndexer UDDIndexer = new UDDIndexer(searchModel.getIndexDir());
		UDDIndexer.openIndexWriter(analyzer, searchModel.getIndexDir());
		
		try {
			List<RequiredHighlight> rhs = new ArrayList<RequiredHighlight>();
			RequiredHighlight textrh = null;			
			
			Query query = null;
			if(!(text == null || text.equals(""))){
				List<RequiredHighlight> list = InformationRetriever.getSuggestions(new Term("text",text),servletContext, searchModel.getIndexDir(),analyzer);
				query = QueryBuilder.buildQuery(textSearchType, "text", text, analyzer);			
				textrh = new RequiredHighlight("text", text, null);
				rhs.add(textrh);
				suggestions.addAll(list);	
			}
		
			List<DocumentModel> resultDocs = InformationRetriever.getData(query, rhs, null, analyzer,searchModel.getIndexDir());
			List<DocumentModel> restDocs = new ArrayList<DocumentModel>();
			List<String> allDocuments = new ArrayList<String>();
			
			Document[] docs = UDDIndexer.getAllDocuments();
			for(Document doc:docs) {
				allDocuments.add(doc.get("location"));
				boolean exists = false;
				for(DocumentModel dm : resultDocs) {
					if(dm.getUid().equals(doc.get("id"))) {
						exists = true; 
						break;
					}
				}
				if(!exists) {
					DocumentModel docModel = new DocumentModel();
					docModel.setUid(doc.get("id"));				
					docModel.setText(doc.get("text"));
					docModel.setTitle(doc.get("title"));
					String location = doc.get("location");
					docModel.setLocation(location);
					File file = new File(location);
					docModel.setFileName(file.getName());
					restDocs.add(docModel);
				}
			}
				
			UDDIndexer.closeIndexWriter();
			resultModel.setDocuments(resultDocs);
			resultModel.setRestDocuments(restDocs);
			resultModel.setSuggestions(suggestions);
			
			
		} catch (IllegalArgumentException e) {
			
		} catch (ParseException e) {
			
		}
        return new ResponseEntity<SearchResultModel>(resultModel, HttpStatus.OK);
    }
	
}
