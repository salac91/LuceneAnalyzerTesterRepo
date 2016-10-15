package tools.indexer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import tools.indexer.handler.DocumentHandler;
import tools.indexer.handler.PDFHandler;
import tools.searcher.ResultRetriever;

public final class UDDIndexer {
	
	private static final Version matchVersion = Version.LUCENE_4_9;
	private IndexWriter indexWriter;
	private Directory indexDir;	
	private Analyzer analyzer;	
	private IndexWriterConfig iwc;

	public UDDIndexer(String path, boolean restart){
		try{
			this.indexDir = new SimpleFSDirectory(new File(path));
			if(restart){
				iwc.setOpenMode(OpenMode.CREATE);
				this.indexWriter = new IndexWriter(indexDir, iwc);
				this.indexWriter.deleteAll();
				this.indexWriter.commit();
				this.indexWriter.close();
			}
		}catch(IOException ioe){
			throw new IllegalArgumentException("Path not correct");
		}
	}
	
	public UDDIndexer(String path, boolean restart, Analyzer analyzer){
		try{
			iwc = new IndexWriterConfig(matchVersion, analyzer);
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			this.indexDir = new SimpleFSDirectory(new File(path));
			this.indexWriter = new IndexWriter(indexDir, iwc);
			if(restart){
				iwc.setOpenMode(OpenMode.CREATE);
				this.indexWriter = new IndexWriter(indexDir, iwc);
				this.indexWriter.deleteAll();
				this.indexWriter.commit();
				this.indexWriter.close();
			}
		}catch(IOException ioe){
			throw new IllegalArgumentException("Path not correct");
		}
	}
		
	private void openIndexWriter() throws IOException{
		iwc = new IndexWriterConfig(matchVersion, analyzer);
		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		this.indexWriter = new IndexWriter(indexDir, iwc);
	}
	
	private void openIndexWriter(Analyzer analyzer) throws IOException{
		iwc = new IndexWriterConfig(matchVersion, analyzer);
		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		this.indexWriter = new IndexWriter(new SimpleFSDirectory(new File(ResourceBundle.getBundle("index").getString("index"))), iwc);
	}
	
	public void openIndexWriter(Analyzer analyzer, String dir) throws IOException{
		iwc = new IndexWriterConfig(matchVersion, analyzer);
		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		this.indexWriter = new IndexWriter(new SimpleFSDirectory(new File(dir)), iwc);
	}
	
	public UDDIndexer(boolean restart){
		this(ResourceBundle.getBundle("index").getString("index"), restart);
	}
	
	public UDDIndexer(String path){
		this(path, false);
	}
	
	public UDDIndexer(String path, Analyzer analyzer){
		this(path, false,analyzer);
	}
	
	public UDDIndexer(){
		this(ResourceBundle.getBundle("index").getString("index"), false);
	}
	
	public IndexWriter getIndexWriter(){
		return this.indexWriter;
	}
	
	public Directory getIndexDir(){
		return this.indexDir;
	}
	
	public void closeIndexWriter() throws IOException {
		this.indexWriter.close();
	}
	
	public boolean addDocument(Document doc){ //indeksiranje tacno jednog dokumenta
		try {
			openIndexWriter();
			this.indexWriter.addDocument(doc);
			this.indexWriter.commit();
			this.indexWriter.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean updateDocument(Document doc, IndexableField... fields){
		String location = doc.get("location");
		replaceFields(doc, fields);
		
		try{
			synchronized (this) {
				openIndexWriter();
				this.indexWriter.updateDocument(new Term("location", location), doc);
				this.indexWriter.forceMergeDeletes();
				this.indexWriter.deleteUnusedFiles();
				this.indexWriter.commit();
				this.indexWriter.close();
			}
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	
	public void replaceFields(Document doc, IndexableField... fields){
		for(IndexableField field : fields){
			doc.removeFields(field.name());
		}
		for(IndexableField field : fields){
			doc.add(field);
		}
	}
	
	public boolean updateDocument(int id, IndexableField... fields){
		try {
			DirectoryReader reader = DirectoryReader.open(indexDir);
			return updateDocument(reader.document(id), fields);
		} catch (IOException e) {
			return false;
		}
	}
	
	public boolean updateDocuments(String fieldName, String fieldValue, IndexableField... fields){
		try{
			openIndexWriter();
			
			Term term = new Term(fieldName, fieldValue);
			Query query = new TermQuery(term); 
			List<Document> docs = ResultRetriever.getResults(query);
			
			String location;
			for(Document doc : docs){
				location = doc.get("location");
				replaceFields(doc, fields);
				this.indexWriter.updateDocument(new Term("location", location), doc);
			}
			//commit-ovanje promena
			this.indexWriter.forceMergeDeletes();
			this.indexWriter.deleteUnusedFiles();
			this.indexWriter.commit();
			this.indexWriter.close();
			return true;
		}catch(IOException ioe){
			return false;
		}
		
	}
	
	public boolean deleteDocument(Document doc){
		if(doc == null) return false;
		//obrisati tacno jedan dokument i to ovaj prosledjeni
		Term delTerm = new Term("id", doc.get("id"));
		try {
			synchronized (this) {
				openIndexWriter();
				this.indexWriter.deleteDocuments(delTerm);
				this.indexWriter.deleteUnusedFiles();
				this.indexWriter.forceMergeDeletes();
				this.indexWriter.commit();
				this.indexWriter.close();
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	

	public boolean deleleDocument(int id){
		try {
			DirectoryReader reader = DirectoryReader.open(indexDir);
			return deleteDocument(reader.document(id));
		} catch (IOException e) {
			return false;
		}
	}
	

	public boolean deleteDocuments(String fieldName, String fieldValue){
		return deleteDocuments(new Term(fieldName, fieldValue));
	}
	
	public boolean deleteDocuments(Term... delTerms){
		try {
			synchronized (this) {
				openIndexWriter();
				this.indexWriter.deleteDocuments(delTerms);
				this.indexWriter.forceMergeDeletes();
				this.indexWriter.deleteUnusedFiles();
				this.indexWriter.commit();
				this.indexWriter.close();
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public boolean deleteDocuments(Query... delQueries){
		try {
			synchronized (this) {
				openIndexWriter();
				this.indexWriter.deleteDocuments(delQueries);
				this.indexWriter.forceMergeDeletes();
				this.indexWriter.deleteUnusedFiles();
				this.indexWriter.commit();
				this.indexWriter.close();
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public Document[] getAllDocuments(){
		//collect and return all documents
		try {
			DirectoryReader reader = DirectoryReader.open(indexDir);
			Document[] docs = new Document[reader.maxDoc()];
			for(int i = 0; i < reader.maxDoc(); i++){
				docs[i] = reader.document(i);
			}
			return docs;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Document[] getAllDocuments(Directory indexDir){
		//collect and return all documents
		try {
			DirectoryReader reader = DirectoryReader.open(indexDir);
			Document[] docs = new Document[reader.maxDoc()];
			for(int i = 0; i < reader.maxDoc(); i++){
				docs[i] = reader.document(i);
			}
			return docs;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void folderIndexer(File folder){
		if(!folder.isDirectory()) return;
		File[] files = folder.listFiles();
		
		for(File file : files){
			if(file.isFile()){
				fileIndexer(file);
			}else{
				folderIndexer(file);
			}
		}
	}
	
	private void fileIndexer(File file){
		DocumentHandler handler;
		handler = getHandler(file);
		if(handler != null){
			try {
				this.indexWriter.addDocument(handler.getDocument(file));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			//unsupported type
		}
	}
	
	public void index(File folder){
		//openIndexWriter();
		if(folder.isDirectory()){
			folderIndexer(folder);
		}else{
			//indeksiranje tacno jednog file-a
			fileIndexer(folder);
		}
		//this.indexWriter.close();
	}
	
	public void index(File folder, Analyzer analyzer){
		try {
			openIndexWriter(analyzer);
			if(folder.isDirectory()){
				folderIndexer(folder);
			}else{
				//indeksiranje tacno jednog file-a
				fileIndexer(folder);
			}
			this.indexWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void index(File folder, Analyzer analyzer, String dir){
		try {
			openIndexWriter(analyzer,dir);
			if(folder.isDirectory()){
				folderIndexer(folder);
			}else{
				//indeksiranje tacno jednog file-a
				fileIndexer(folder);
			}
			this.indexWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static DocumentHandler getHandler(File file){
		//za svaki file uzeti odgovarajuci DocumentHandler
		if(file.isDirectory()) return null; //ako je u pitanju direktorijum
		DocumentHandler handler = null;
		if(file.getName().endsWith(".pdf"))
			handler =  new PDFHandler();
		
		return handler;
	}

}
