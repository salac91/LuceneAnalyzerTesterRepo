package rest.mvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import core.models.entities.Account;
import core.services.AccountService;
import tools.models.DirectoryModel;
import tools.util.DirectroryList;


@Controller
@RequestMapping("/rest/documents")
public class DocumentController {
	
	@Autowired
	ServletContext servletContext;
	
	@Autowired
	private AccountService accountService;
	
	@RequestMapping(value = "/newDocuments", method = RequestMethod.POST)
	@PreAuthorize("hasRole('User','Admin')")
	public ResponseEntity<?> uploadFile(
			@RequestParam(value = "files", required = false) MultipartFile[] files, @RequestParam("formDataJson") String formDataJson, 
			  @RequestParam("directoryName") String directoryName) throws COSVisitorException {
		
		Object principal = SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		Account user = null;
		if (principal instanceof UserDetails) {
			UserDetails details = (UserDetails) principal;
			user = accountService.findAccountByUsername(details.getUsername());
		}
		
		String realPath = servletContext.getRealPath("/docs") + "/" + user.getUsername() + "/" + directoryName;
		for(MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			File dir = new File(realPath);
			File destFile = new File(realPath + "/" + fileName);
			
			if(!dir.exists())
				dir.mkdir();
			
			try {
				
				file.transferTo(destFile);	
				
				if(destFile.getName().endsWith(".pdf")) {
					PDFParser parser = new PDFParser(new FileInputStream(destFile));
					parser.parse();
					PDDocument pdf = parser.getPDDocument();
					PDDocumentInformation info = pdf.getDocumentInformation();
					info.setCustomMetadataValue("id", "" + System.currentTimeMillis());
					pdf.setDocumentInformation(info);
					pdf.save(destFile);
					pdf.close();
				}
	
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	  	  
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/newDirectory", method = RequestMethod.POST)
	@PreAuthorize("hasRole('User','Admin')")
	public ResponseEntity<String> createNewDirectory(@RequestBody String directoryName) {
		
		Object principal = SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		Account user = null;
		if (principal instanceof UserDetails) {
			UserDetails details = (UserDetails) principal;
			user = accountService.findAccountByUsername(details.getUsername());
		}
		
		String dir = user.getDirectoryPath();
		File newDir = new File(dir + "/" + directoryName);
		newDir.mkdir();
		
	 return new ResponseEntity<String>("Directory has been created successfully", HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/removeDirectory/{directoryName}/{username}", method = RequestMethod.POST)
	@PreAuthorize("hasRole('User','Admin')")
	public ResponseEntity<String> removeDirectory(@PathVariable String directoryName, @PathVariable String username) {
		

		Account	user = accountService.findAccountByUsername(username);
				
		String dirPath = user.getDirectoryPath();
		File dir = new File(dirPath + "/" + directoryName);
		File[] files = dir.listFiles();
		for(File file:files) 
			file.delete();
		dir.delete();
		
		return new ResponseEntity<String>("Directory has been removed successfully", HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/getUserDirectories", method = RequestMethod.GET)
	@PreAuthorize("hasRole('User','Admin')")
	public ResponseEntity<DirectroryList> getUserDirectories() {
		
		Object principal = SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		Account user = null;
		if (principal instanceof UserDetails) {
			UserDetails details = (UserDetails) principal;
			user = accountService.findAccountByUsername(details.getUsername());
		}
		
		String docsRealPath = servletContext.getRealPath("/docs");
		DirectroryList dl = new DirectroryList();
		List<DirectoryModel> directories = new ArrayList<DirectoryModel>();
		
		if(user.getRole().equals("Admin")) {
			List<Account> accounts = accountService.findAllAccounts();
			for(Account account: accounts) {
				File userDir = new File(docsRealPath + "/" + account.getUsername());
				if(!userDir.exists()) {
					userDir.mkdir();
					account.setDirectoryPath(docsRealPath + "/" + account.getUsername());
					accountService.updateAccount(account);
				}
				
				File[] files = userDir.listFiles();
				for(File file:files) {
					DirectoryModel dm = new DirectoryModel();
					dm.setName(file.getName());
					dm.setPath(file.getAbsolutePath());
					dm.setFilesNumber(file.listFiles().length);
					dm.setCreatedBy(account.getUsername());
					
					 Path path = Paths.get(file.getAbsolutePath());
					 BasicFileAttributes attr;
					    try {
					    attr = Files.readAttributes(path, BasicFileAttributes.class);
					    dm.setCreationDate(attr.creationTime().toString().substring(0, 19).replace("T", " ")); 
		
					    } catch (IOException e) {
					  
					    }
					    
					directories.add(dm);
				}
			}
			
		}
		else {
			
			File userDir = new File(docsRealPath + "/" + user.getUsername());
			
			if(!userDir.exists()) {
				userDir.mkdir();
				user.setDirectoryPath(docsRealPath + "/" + user.getUsername());
				accountService.updateAccount(user);
			}

	 		File[] files = userDir.listFiles();
			for(File file:files) {
				DirectoryModel dm = new DirectoryModel();
				dm.setName(file.getName());
				dm.setPath(file.getAbsolutePath());
				dm.setFilesNumber(file.listFiles().length);
				dm.setCreatedBy(user.getUsername());
				
				 Path path = Paths.get(file.getAbsolutePath());
				 BasicFileAttributes attr;
				    try {
				    attr = Files.readAttributes(path, BasicFileAttributes.class);
				    dm.setCreationDate(attr.creationTime().toString().substring(0, 19).replace("T", " ")); 
	
				    } catch (IOException e) {
				  
				    }
				    
				directories.add(dm);
			}
		}
		dl.setDirectories(directories);
		
		return new ResponseEntity<DirectroryList>(dl, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	@PreAuthorize("hasRole('User','Admin')")
	public @ResponseBody byte[] getPdf(@RequestParam(value = "location") String location,HttpServletResponse response) throws IOException {
		
	    response.setHeader("Content-Disposition", "inline; filename=file.pdf");
	    response.setContentType("application/pdf");

        File file = new File(location);

        InputStream inputStream = null;
        @SuppressWarnings("resource")
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {

            inputStream = new FileInputStream(file);

            byte[] buffer = new byte[1024];
            baos = new ByteArrayOutputStream();

            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return baos.toByteArray();
      
	}
	
	
	@RequestMapping(value = "/getFilesNames/{directoryName}", method = RequestMethod.GET)
	@PreAuthorize("hasRole('User','Admin')")
	public @ResponseBody List<String> getFilesName(@PathVariable String directoryName) {
		
		Object principal = SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		Account user = null;
		if (principal instanceof UserDetails) {
			UserDetails details = (UserDetails) principal;
			user = accountService.findAccountByUsername(details.getUsername());
		}
		
		String dirPath = user.getDirectoryPath();
		File dir = new File(dirPath + "/" + directoryName);
		File[] files = dir.listFiles();
		
		List<String> filesNames = new ArrayList<String>();
		if(files != null) {
			for(File file : files)
				filesNames.add(file.getName());
		}
		return filesNames;
		
	   
	}
	
}
