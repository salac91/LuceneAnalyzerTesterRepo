package rest.mvc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import core.models.entities.Analyzer;
import core.models.entities.Benchmark;
import core.models.entities.DocumentPath;
import core.services.AnalyzerService;
import core.services.BenchmarkService;
import tools.indexer.UDDIndexer;
import tools.models.AnalysisResultModel;
import tools.models.AnalyzerModel;
import tools.models.BenchmarkModel;
import tools.models.DocumentModel;
import tools.models.QueryAndRelevantDocumentsModel;
import tools.models.RequiredHighlight;
import tools.models.SearchModel;
import tools.util.AnalysisResultList;
import tools.util.AnalyzerList;
import tools.util.SearchType;
import tools.query.QueryBuilder;
import tools.searcher.InformationRetriever;
import tools.models.SearchResultModel;

@Controller
@RequestMapping("/rest/analyzer")
public class AnalyzerController {

    @Autowired
    ServletContext servletContext;
	
    @Autowired
	private BenchmarkService benchmarkService;
    
    @Autowired
	private AnalyzerService analyzerService;
    
    @RequestMapping(value = "/newAnalyzer", method = RequestMethod.POST)
	@PreAuthorize("hasRole('User','Admin')")
	public ResponseEntity<?> uploadAnalyzer(
			@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("formDataJson") String formDataJson,
			 @RequestParam("name") String analyzerName, @RequestParam("description") String description) throws COSVisitorException, IllegalStateException, IOException {
				
		//save jar file
		String analyzerRealPath = servletContext.getRealPath("/analyzers") + "/" + analyzerName;
		String fileName = file.getOriginalFilename();
		File destDir = new File(analyzerRealPath);
		if(!destDir.exists())
			destDir.mkdir();
		File destFile = new File(destDir + "/" + fileName);
		file.transferTo(destFile);		
				
		//save new analyzer info
		Analyzer newAnalyzer = new Analyzer();
		newAnalyzer.setName(analyzerName);
		newAnalyzer.setDescription(description);
		newAnalyzer.setPath(analyzerRealPath + "/" + fileName);
		
		analyzerService.createAnalyzer(newAnalyzer);
		  	  
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	 
	@RequestMapping(value="/test",method = RequestMethod.POST)
	@PreAuthorize("hasRole('User','Admin')")
    public ResponseEntity<AnalysisResultList> testAnalyzer(@RequestBody BenchmarkModel benchmark) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		
		Benchmark benchmarkdb = benchmarkService.findBenchmark(benchmark.getBenchmark_id());
		
		//dynamically load selected analyzer
		core.models.entities.Analyzer selectedAnalyzer = analyzerService.findAnalyzer(benchmark.getAnalyzerType());
		
		URL[] urls = { new URL("jar:file:" + selectedAnalyzer.getPath()+"!/") };
		@SuppressWarnings("resource")
		URLClassLoader cl = new URLClassLoader(urls,Thread.currentThread().getContextClassLoader());		

		@SuppressWarnings("unchecked")
		Class<org.apache.lucene.analysis.Analyzer> classToLoad = (Class<org.apache.lucene.analysis.Analyzer>) cl.loadClass(selectedAnalyzer.getName());
		org.apache.lucene.analysis.Analyzer analyzer =  (org.apache.lucene.analysis.Analyzer) classToLoad.newInstance();	
		
		String indexDir = servletContext.getRealPath("/indexes") + "\\" + System.currentTimeMillis();
		
	    UDDIndexer UDDIndexer = new UDDIndexer(indexDir,analyzer);
	    
	    for(DocumentPath documentPath: benchmarkdb.getAllDocumentsPath()) {
	    	File file = new  File(documentPath.getPath());
	    	UDDIndexer.index(file);
		
	    }
	    
	    List<SearchResultModel> analysisResultList = new ArrayList<SearchResultModel>();
		List<QueryAndRelevantDocumentsModel> queryAndRelevantDocumentsModels = benchmark.getQueryAndRelevantDocumentsList();
		
		for(QueryAndRelevantDocumentsModel queryAndRelevantDocumentsModel : queryAndRelevantDocumentsModels) {
			SearchModel searchModel = queryAndRelevantDocumentsModel.getSearchModel();
			String text = searchModel.getText();
			String textst = searchModel.getTextSearchType();
			SearchType.Type textSearchType = SearchType.getType(textst);
			
			try {
				List<RequiredHighlight> rhs = new ArrayList<RequiredHighlight>();
				RequiredHighlight textrh = null;
			
				Query query = null;		
				if(!(text == null || text.equals(""))){
					query = QueryBuilder.buildQuery(textSearchType, "text", text, analyzer);
					textrh = new RequiredHighlight("text", text, null);
					rhs.add(textrh);
				}
								
				List<DocumentModel> resultDocs = InformationRetriever.getData(query, rhs, null, analyzer,indexDir);
				List<DocumentModel> restDocs = new ArrayList<DocumentModel>();
				Document[] allDocs = UDDIndexer.getAllDocuments();
				for(Document doc:allDocs) {
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
						docModel.setRelevant(false);
						docModel.setLocation(location);
						File file = new File(location);
						docModel.setFileName(file.getName());
						restDocs.add(docModel);
					}
				}
				
				List<String> allRelavant = queryAndRelevantDocumentsModel.getRelevantDocuments();
				List<DocumentModel> allRelevantBenchmark = new ArrayList<DocumentModel>();
				List<DocumentModel> restDocsBencmark = new ArrayList<DocumentModel>();
				
				for(Document doc:allDocs) {
					DocumentModel docModel = new DocumentModel();
					docModel.setUid(doc.get("id"));				
					docModel.setText(doc.get("text"));
					docModel.setTitle(doc.get("title"));
					String location = doc.get("location");
					docModel.setLocation(location);
					File file = new File(location);
					docModel.setFileName(file.getName());
					
					if(allRelavant.contains(doc.get("id"))) {
						docModel.setRelevant(true);
						allRelevantBenchmark.add(docModel);
					}
					else {
						docModel.setRelevant(false);
						restDocsBencmark.add(docModel);
					}
				}
				
				List<String> restNonRelevant = new ArrayList<String>();
				float numAllRetrieved = resultDocs.size();
				float numAllRelavant = allRelavant.size();
				float numOfRelRetrieved = 0;
				for(DocumentModel dm : resultDocs) {
					if(allRelavant.contains(dm.getUid()))
						numOfRelRetrieved++;
				}
				
				for(Document doc : allDocs) {
					if(!allRelavant.contains(doc.get("id")))
						restNonRelevant.add(doc.get("id"));
				}
				
				float correctness = 0;
				float TP = numOfRelRetrieved;	
				float TN = 0;
				for(String nonRelevant : restNonRelevant) {
					boolean contains = false;
					if(nonRelevant != null) {
						for(DocumentModel dm : resultDocs) {
							if(nonRelevant.equals(dm.getUid())) {
								contains = true;
								break;
							}
						}
					}
					if(!contains) TN++;
				}
					
				float FP = 0;
				
				for(DocumentModel dm: resultDocs) {
					if(restNonRelevant.contains(dm.getUid()))
						FP++;
				}
				
				float FN = allRelavant.size() - numOfRelRetrieved;
				
				correctness = (TP+TN)/(TP+TN+FP+FN);
					
				float precision = 0;
				if(numAllRetrieved != 0)
					precision = numOfRelRetrieved/numAllRetrieved;
				else 
					precision = 0;
				
				float retrieval = 0;
				if(numAllRelavant != 0)
					retrieval = numOfRelRetrieved/numAllRelavant;
				else 
					retrieval = 0;
				
				float beta = 1;
				float F = 0;
				if(precision == 0 && retrieval == 0)
					F = 0;
				else 
				    F = (float) (((Math.pow(beta, 2)+1) * precision * retrieval)/(Math.pow(beta, 2)* precision + retrieval));

				AnalysisResultModel analysisResultModel = new AnalysisResultModel();
				analysisResultModel.setPrecision(precision*100);
				analysisResultModel.setRetrieval(retrieval*100);
				analysisResultModel.setCorrectness(correctness*100);
				
				analysisResultModel.setFmera(F);
						
				SearchResultModel searchResultModel = new SearchResultModel();
				searchResultModel.setAnalysisResultModel(analysisResultModel);
				searchResultModel.setSearchModel(searchModel);
				searchResultModel.setDocuments(resultDocs);
				searchResultModel.setDocumentsBenchmark(allRelevantBenchmark);
				searchResultModel.setRestDocuments(restDocs);
				searchResultModel.setRestDocumentsBenchmark(restDocsBencmark);
							
				analysisResultList.add(searchResultModel);
				
			} catch (IllegalArgumentException e1) {
				
			} catch (ParseException e1) {
				
			}
		}
 
		
		AnalysisResultList list = new AnalysisResultList();
		list.setAnalysisResultList(analysisResultList);
	
		 File index = new File(indexDir);
		 if (index.exists()) {
		        File[] files = index.listFiles();
		        for (int i = 0; i < files.length; i++) {
		            if (files[i].isDirectory()) {
		                deleteDirectory(files[i]);
		            } else {
		                files[i].delete();
		            }
		        }
		    }
		index.delete();
		
        return new ResponseEntity<AnalysisResultList>(list, HttpStatus.OK);
        
	}
	
	@RequestMapping(value="/getAll",method = RequestMethod.GET)
	@PreAuthorize("hasRole('User','Admin')")
    public ResponseEntity<AnalyzerList> getAllAnalyzers() {
		
		List<Analyzer> analyzers = analyzerService.getAllAnalyzers();
		List<AnalyzerModel> analyzerModels = new ArrayList<AnalyzerModel>();
		for(Analyzer analyzer: analyzers) {
			AnalyzerModel analyzerModel = new AnalyzerModel();
			analyzerModel.setAnalyzer_Id(analyzer.getAnalyzer_Id());
			analyzerModel.setName(analyzer.getName());
			analyzerModel.setDescription(analyzer.getDescription());
			analyzerModel.setPath(analyzer.getPath());
			analyzerModels.add(analyzerModel);
		}

		AnalyzerList analyzerList = new AnalyzerList();
		analyzerList.setAnalyzers(analyzerModels);
		
		return new ResponseEntity<AnalyzerList>(analyzerList,HttpStatus.OK);
		
	}
	
	@RequestMapping(value="/remove",method = RequestMethod.POST)
	@PreAuthorize("hasRole('User','Admin')")
    public ResponseEntity<String> removeAnalyzer(@RequestBody Long analyzer_Id) {
		
		Analyzer analyzer = analyzerService.findAnalyzer(analyzer_Id);
		String analyzerRealPath = servletContext.getRealPath("/analyzers") + "/" + analyzer.getName();
		File file = new File(analyzerRealPath);
	    if (file.exists()) {
		     File[] files = file.listFiles();
		     for (int i = 0; i < files.length; i++) {		          
		          files[i].delete();
		     }
		}
        file.delete();
		analyzerService.removeAnalyzer(analyzer);
		
		return new ResponseEntity<String>("Analyzer has been removed successfully", HttpStatus.OK);
	}
	
	private void deleteDirectory(File file) {
		 File[] files = file.listFiles();
	        for (int i = 0; i < files.length; i++) {
	            if (files[i].isDirectory()) {
	                deleteDirectory(files[i]);
	            } else {
	                files[i].delete();
	            }
	        }
	     file.delete();
	}
	
}
