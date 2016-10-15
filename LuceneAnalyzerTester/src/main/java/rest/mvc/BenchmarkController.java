package rest.mvc;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import core.services.AccountService;
import core.services.AnalyzerService;
import core.services.BenchmarkService;
import core.services.QueryAndRelevantDocumentsService;
import tools.indexer.UDDIndexer;
import tools.models.BenchmarkModel;
import tools.models.DocumentModel;
import tools.models.QueryAndRelevantDocumentsModel;
import tools.models.RelevantDocumentsModel;
import tools.models.SearchModel;
import tools.util.BenchmarkList;
import tools.util.QueryAndRelevantDocumentsList;
import core.models.entities.Account;

import core.models.entities.Benchmark;
import core.models.entities.DocumentPath;
import core.models.entities.QueryAndRelevantDocuments;
import core.models.entities.RelevantDocument;

@Controller
@RequestMapping("/rest/benchmark")
public class BenchmarkController {

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private BenchmarkService benchmarkService;
	
	@Autowired
	private QueryAndRelevantDocumentsService queryAndRelevantDocumentsService;
	
	@Autowired
	private AnalyzerService analyzerService;
	
	@Autowired
	ServletContext servletContext;
	
	@RequestMapping(value="/save",method = RequestMethod.POST)
	@PreAuthorize("hasRole('User','Admin')")
    public ResponseEntity<String> saveBenchmark(@RequestBody QueryAndRelevantDocumentsModel queryAndRelevantDocumentsModel) {
		
		Benchmark benchmark = benchmarkService.findBenchmark(queryAndRelevantDocumentsModel.getBenchmark_id());
		Set<QueryAndRelevantDocuments> queryAndRelevantDocumentsList = benchmark.getQueryAndRelevantDocumentsList();
		QueryAndRelevantDocuments queryAndRelevantDocuments = new QueryAndRelevantDocuments();
		queryAndRelevantDocuments.setBenchmark(benchmark);
		
		Set<RelevantDocument> relevantDocuments = new HashSet<RelevantDocument>();
		for(String doc_id : queryAndRelevantDocumentsModel.getRelevantDocuments()) {
			RelevantDocument rd = new RelevantDocument();
			rd.setQueryAndRelevantDocuments(queryAndRelevantDocuments);
			rd.setUid(doc_id);
			relevantDocuments.add(rd);
		}
		queryAndRelevantDocuments.setRelevantDocuments(relevantDocuments);
		queryAndRelevantDocuments.setText(queryAndRelevantDocumentsModel.getSearchModel().getText());
		queryAndRelevantDocuments.setTextSearchType(queryAndRelevantDocumentsModel.getSearchModel().getTextSearchType());
		queryAndRelevantDocumentsService.createQueryAndRelevantDocuments(queryAndRelevantDocuments);
		
		queryAndRelevantDocumentsList.add(queryAndRelevantDocuments);
		benchmark.setQueryAndRelevantDocumentsList(queryAndRelevantDocumentsList);
		
		benchmarkService.updateBenchmark(benchmark);
		
		return new ResponseEntity<String>("Query and relevant documents have been added to benchmark successfully", HttpStatus.OK);
		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/create",method = RequestMethod.POST)
	@PreAuthorize("hasRole('User','Admin')")
    public ResponseEntity<String> createBenchmark(@RequestBody BenchmarkModel benchmarkModel) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		
		//dynamically add selected analyzer
		core.models.entities.Analyzer selectedAnalyzer = analyzerService.findAnalyzer(benchmarkModel.getAnalyzerType());
		
		URL[] urls = { new URL("jar:file:" + selectedAnalyzer.getPath()+"!/") };
		@SuppressWarnings("resource")
		URLClassLoader cl = new URLClassLoader(urls,Thread.currentThread().getContextClassLoader());

		Class<Analyzer> classToLoad = (Class<Analyzer>) cl.loadClass(selectedAnalyzer.getName());
		Analyzer analyzer =  (Analyzer) classToLoad.newInstance();	 
		
	    Object principal = SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		Account user = null;
		if (principal instanceof UserDetails) {
			UserDetails details = (UserDetails) principal;
			user = accountService.findAccountByUsername(details.getUsername());
		}
	    String realPathAllDocs = servletContext.getRealPath("/docs") + "\\" + user.getUsername() + "\\" + benchmarkModel.getDirectoryName();
		String indexDir = servletContext.getRealPath("/indexes") + "\\" + System.currentTimeMillis();
	    File docsDirAllDocs = new File(realPathAllDocs);
	    UDDIndexer UDDIndexer = new UDDIndexer(indexDir);
	    UDDIndexer.index(docsDirAllDocs,analyzer,indexDir);
	
	    //create new benchmark
		Benchmark newBenchmark = new Benchmark();
		newBenchmark.setIndexDir(indexDir);
		
		Set<DocumentPath> allDocumentsPath = new HashSet<DocumentPath>();
		Document[] allDocs=  UDDIndexer.getAllDocuments();
		for(Document doc : allDocs) {
			DocumentPath dp = new DocumentPath();
			dp.setPath(doc.get("location"));
			dp.setBenchmark(newBenchmark);
			allDocumentsPath.add(dp);
		}
		newBenchmark.setAllDocumentsPath(allDocumentsPath);
		newBenchmark.setAnalyzerType(benchmarkModel.getAnalyzerType());
		newBenchmark.setName(benchmarkModel.getName());
		newBenchmark.setDirectoryName(benchmarkModel.getDirectoryName());
		newBenchmark.setAnalyzerName(selectedAnalyzer.getName());
		newBenchmark.setAccount(user);
		benchmarkService.createBenchmark(newBenchmark);
		
		Set<Benchmark> benchmarks = user.getBenchmarks();
		benchmarks.add(newBenchmark);
		user.setBenchmarks(benchmarks);
		
		accountService.updateAccount(user);
		
		return new ResponseEntity<String>("Benchmark has been created successfully", HttpStatus.OK);
	}
	
	
	@RequestMapping(value="/getAll",method = RequestMethod.GET)
	@PreAuthorize("hasRole('User','Admin')")
    public ResponseEntity<BenchmarkList> getAllBenchmarksForThisUser() {
		
		Object principal = SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		Account user = null;
		if (principal instanceof UserDetails) {
			UserDetails details = (UserDetails) principal;
			user = accountService.findAccountByUsername(details.getUsername());
		}
		
		Set<Benchmark> benchmarks = user.getBenchmarks();
		List<BenchmarkModel> list = new ArrayList<BenchmarkModel>();
		Iterator<Benchmark> it = benchmarks.iterator();
		while(it.hasNext()) {
			Benchmark benchmark = (Benchmark) it.next();
			BenchmarkModel bm = new BenchmarkModel();
			List<String> relevantDocs = new ArrayList<String>();
			
			tools.models.SearchModel sm = new tools.models.SearchModel();			
			bm.setBenchmark_id(benchmark.getBenchmark_id());
			bm.setRelevantDocuments(relevantDocs);
			bm.setSearchModel(sm);
			bm.setIndexDir(benchmark.getIndexDir());
			bm.setAnalyzerType(benchmark.getAnalyzerType());
			bm.setAlayzerName(benchmark.getAnalyzerName());
			bm.setName(benchmark.getName());
			bm.setDirectoryName(benchmark.getDirectoryName());
			bm.setNum_of_SearchQueries(benchmark.getQueryAndRelevantDocumentsList().size());
			
			list.add(bm);
		}
		
		BenchmarkList bl = new BenchmarkList();
		bl.setBenchmarks(list);
		
		return new ResponseEntity<BenchmarkList>(bl,HttpStatus.OK);
		
	}
	
	@RequestMapping(value="/getAll2",method = RequestMethod.GET)
	@PreAuthorize("hasRole('User','Admin')")
    public ResponseEntity<BenchmarkList> getAllBenchmarksForThisUser2() {
		
		Object principal = SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		Account user = null;
		if (principal instanceof UserDetails) {
			UserDetails details = (UserDetails) principal;
			user = accountService.findAccountByUsername(details.getUsername());
		}
		
		Set<Benchmark> benchmarks = user.getBenchmarks();
		List<BenchmarkModel> list = new ArrayList<BenchmarkModel>();
		Iterator<Benchmark> it = benchmarks.iterator();
		while(it.hasNext()) {
			Benchmark benchmark = (Benchmark) it.next();
			BenchmarkModel bm = new BenchmarkModel();
			List<QueryAndRelevantDocumentsModel> queryAndRelevantDocumentsModels = new ArrayList<QueryAndRelevantDocumentsModel>();
			for(QueryAndRelevantDocuments queryAndRelevantDocuments : benchmark.getQueryAndRelevantDocumentsList()) {
				QueryAndRelevantDocumentsModel queryAndRelevantDocumentsModel = new QueryAndRelevantDocumentsModel();
				List<String> relevantDocuments = new ArrayList<String>();
				for(RelevantDocument rd : queryAndRelevantDocuments.getRelevantDocuments()) {
					relevantDocuments.add(rd.getUid());
				}
				queryAndRelevantDocumentsModel.setRelevantDocuments(relevantDocuments);
				SearchModel sm = new SearchModel();
				sm.setText(queryAndRelevantDocuments.getText());
				sm.setTextSearchType(queryAndRelevantDocuments.getTextSearchType());
				queryAndRelevantDocumentsModel.setSearchModel(sm);
				queryAndRelevantDocumentsModels.add(queryAndRelevantDocumentsModel);
			}
			
			bm.setBenchmark_id(benchmark.getBenchmark_id());
			bm.setQueryAndRelevantDocumentsList(queryAndRelevantDocumentsModels);			
			bm.setName(benchmark.getName());
			bm.setDirectoryName(benchmark.getDirectoryName());
			bm.setAnalyzerType(benchmark.getAnalyzerType());
			bm.setAlayzerName(benchmark.getAnalyzerName());
			
			list.add(bm);
		}
		
		BenchmarkList bl = new BenchmarkList();
		bl.setBenchmarks(list);
		
		return new ResponseEntity<BenchmarkList>(bl,HttpStatus.OK);
		
	}
	
	@RequestMapping(value="/getAllQueriesAndRelevantDocs/{benchmark_id}",method = RequestMethod.GET)
	@PreAuthorize("hasRole('User','Admin')")
    public ResponseEntity<QueryAndRelevantDocumentsList> getAllQueriesAndRelevantDocs(@PathVariable long benchmark_id ) {
		
		QueryAndRelevantDocumentsList queryAndRelevantDocumentsList = new QueryAndRelevantDocumentsList();
		List<QueryAndRelevantDocumentsModel> list = new ArrayList<QueryAndRelevantDocumentsModel>();
		Benchmark benchmark = benchmarkService.findBenchmark(benchmark_id);
		for(QueryAndRelevantDocuments queryAndRelevantDocuments :  benchmark.getQueryAndRelevantDocumentsList()) {
			QueryAndRelevantDocumentsModel queryAndRelevantDocumentModel = new QueryAndRelevantDocumentsModel();
			queryAndRelevantDocumentModel.setQueryAndRelevantDocuments_Id(queryAndRelevantDocuments.getQueryAndRelevantDocuments_Id());
			
			List<String> relevantDocs = new ArrayList<String>();
		
			for(RelevantDocument rd : queryAndRelevantDocuments.getRelevantDocuments())
				relevantDocs.add(rd.getUid());
			
			long numOfRelevant = relevantDocs.size();
			long allBenchmarkDocs = benchmark.getAllDocumentsPath().size();
			
			queryAndRelevantDocumentModel.setRelevantDocuments(relevantDocs);
			SearchModel sm = new SearchModel();
			sm.setText(queryAndRelevantDocuments.getText());
			sm.setTextSearchType(queryAndRelevantDocuments.getTextSearchType());
			queryAndRelevantDocumentModel.setSearchModel(sm);
			queryAndRelevantDocumentModel.setNumOfRelevant(numOfRelevant);
			queryAndRelevantDocumentModel.setAllBenchmarkDocuments(allBenchmarkDocs);
			list.add(queryAndRelevantDocumentModel);
		}
		
		queryAndRelevantDocumentsList.setQueryAndRelevantDocumentsList(list);
		return new ResponseEntity<QueryAndRelevantDocumentsList>(queryAndRelevantDocumentsList,HttpStatus.OK);
	}
	
	@RequestMapping(value="/getRelevantDocsForThisQuery/{query_id}/{benchmark_id}",method = RequestMethod.GET)
	@PreAuthorize("hasRole('User','Admin')")
    public ResponseEntity<RelevantDocumentsModel> getRelevantDocsForThisQuery(@PathVariable long query_id,@PathVariable long benchmark_id) throws IOException {
		
		QueryAndRelevantDocuments queryAndRelevantDocuments = queryAndRelevantDocumentsService.findQueryAndRelevantDocuments(query_id);
		Benchmark benchmark = benchmarkService.findBenchmark(benchmark_id);
		Set<RelevantDocument> relevantDocsDB = queryAndRelevantDocuments.getRelevantDocuments();
		RelevantDocumentsModel relevantDocumentsModel = new RelevantDocumentsModel();
		
		UDDIndexer UDDIndexer = new UDDIndexer(benchmark.getIndexDir());
		
		List<DocumentModel> relevantDocs = new ArrayList<DocumentModel>();
		List<DocumentModel> restDocs = new ArrayList<DocumentModel>();
		Document[] docs = UDDIndexer.getAllDocuments();
		for(Document doc:docs) {
			boolean exists = false;
			for(RelevantDocument rd : relevantDocsDB) {
				if(rd.getUid().equals(doc.get("id"))) {
					DocumentModel docModel = new DocumentModel();
					docModel.setUid(doc.get("id"));				
					docModel.setTitle(doc.get("title"));
					String location = doc.get("location");
					docModel.setLocation(location);
					File file = new File(location);
					docModel.setFileName(file.getName());
					relevantDocs.add(docModel); 
					exists = true;
					break;
				}
			}
			if(!exists) {
				DocumentModel docModel = new DocumentModel();
				docModel.setUid(doc.get("id"));				
				docModel.setTitle(doc.get("title"));
				String location = doc.get("location");
				docModel.setLocation(location);
				File file = new File(location);
				docModel.setFileName(file.getName());
				restDocs.add(docModel); 
			}
		}

		relevantDocumentsModel.setRelevantDocs(relevantDocs);
		relevantDocumentsModel.setRestDocs(restDocs);
		return new ResponseEntity<RelevantDocumentsModel>(relevantDocumentsModel, HttpStatus.OK);
		
		
	}
	
	@RequestMapping(value="/updateRelevantDocsForThisQuery",method = RequestMethod.POST)
	@PreAuthorize("hasRole('User','Admin')")
    public ResponseEntity<String> updateRelevantDocsForThisQuery(@RequestBody QueryAndRelevantDocumentsModel queryAndRelevantDocumentsModel) {
		
		QueryAndRelevantDocuments queryAndRelevantDocuments = queryAndRelevantDocumentsService.findQueryAndRelevantDocuments(queryAndRelevantDocumentsModel.getQueryAndRelevantDocuments_Id());
		
		for(RelevantDocument rd : queryAndRelevantDocuments.getRelevantDocuments()) {
			if(rd != null) {
				queryAndRelevantDocumentsService.removeRelevantDocument(rd);
			}
		}
		
		for(String uid : queryAndRelevantDocumentsModel.getRelevantDocuments()) {
			if(uid != null) {
				RelevantDocument relevantDocument = new RelevantDocument();
				relevantDocument.setQueryAndRelevantDocuments(queryAndRelevantDocuments);
				relevantDocument.setUid(uid);
				queryAndRelevantDocumentsService.createRelevantDocument(relevantDocument);
			}
		}

		return new ResponseEntity<String>("Query has been updated successfully", HttpStatus.OK);
	}
	
	@RequestMapping(value="/remove",method = RequestMethod.POST)
	@PreAuthorize("hasRole('User','Admin')")
    public ResponseEntity<String> removeBenchmark(@RequestBody Long benchmark_Id) {
		
		Benchmark benchmark = benchmarkService.findBenchmark(benchmark_Id);
		
		File index = new File(benchmark.getIndexDir());
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
		
		benchmarkService.removeAllDocumentPaths(benchmark_Id);
		benchmarkService.removeAllQueriesAndRelevantDocuments(benchmark_Id);
		benchmarkService.removeBenchmark(benchmark);

		
		return new ResponseEntity<String>("Benchmark has been removed successfully", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/removeQueryAndRelevantDocuments", method = RequestMethod.POST)
	@PreAuthorize("hasRole('User','Admin')")
	public ResponseEntity<String> removeQueryAndRelevantDocuments(@RequestBody Long queryAndRelevantDocuments_Id) {
		
		QueryAndRelevantDocuments queryAndRelevantDocuments = queryAndRelevantDocumentsService.findQueryAndRelevantDocuments(queryAndRelevantDocuments_Id);
		queryAndRelevantDocumentsService.removeAllRelevantDocuments(queryAndRelevantDocuments_Id);
		queryAndRelevantDocumentsService.removeQueryAndRelevantDocuments(queryAndRelevantDocuments);
		
		return new ResponseEntity<String>("QueryAndRelevantDocuments has been removed successfully", HttpStatus.OK);
		
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
