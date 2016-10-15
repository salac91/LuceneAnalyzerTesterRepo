package tools.indexer.handler;

import java.io.File;
import java.io.FileInputStream;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;

public class PDFHandler extends DocumentHandler {

	@Override
	public Document getDocument(File file) throws IncompleteIndexDocumentException {
		
		Document doc = new Document();
		TextField fileNameField = new TextField("fileName",	file.getName(), Store.YES);
		doc.add(fileNameField);
		try {
			StringField locationField = new StringField("location", file.getCanonicalPath(), Store.YES);
			doc.add(locationField);
			//napraviti pdf parser
			PDFParser parser = new PDFParser(new FileInputStream(file));
			//izvrsiti parsiranje
			parser.parse();
			
			//od parsera preuzeti parsirani pdf dokument (PDDocument)
			PDDocument pdf = parser.getPDDocument();
			
			//Upotrebiti text stripper klasu za ekstrahovanje teksta sa utf-8 kodnom stranom (PDFTextStripper)
			PDFTextStripper stripper = new PDFTextStripper("utf-8");
			String text = stripper.getText(pdf);
			if(text!=null && !text.trim().equals("")){
				doc.add(new TextField("text", text, Store.NO));
			}else{
				doc.add(new TextField("text", "", Store.NO));
			}
			
			//iz dokumenta izvuci objekat u kojem su svi metapodaci (PDDocumentInformation)
			PDDocumentInformation info = pdf.getDocumentInformation();
			
			String id = info.getCustomMetadataValue("id");
			if(id!=null && !id.trim().equals("")) {
				doc.add(new TextField("id", id, Store.YES));
			}
			
			String title = info.getTitle();
			if(title!=null && !title.trim().equals("")){
				doc.add(new TextField("title", title, Store.YES));
			}	
			//zatvoriti pdf dokument
			pdf.close();
		} catch (Exception e) {
			System.out.println("Greska pri konvertovanju pdf dokumenta");
		}
		
		return doc;
	}

}
