package rest.mvc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

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
import org.springframework.web.bind.annotation.ResponseBody;

import core.models.entities.Account;
import core.services.AccountService;
import core.util.AccountList;
import core.util.AccountModel;
import core.util.IsLogedInModel;
import core.util.UserExistsModel;
import core.util.UserRoleModel;

@Controller
@RequestMapping("/rest/accounts")
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private ServletContext servletContext;
	
	@RequestMapping(value="/all", method = RequestMethod.GET)
	@PreAuthorize ("hasRole('permitAll')")
    public ResponseEntity<AccountList> findAllAccounts() {
		
		List<Account> list = accountService.findAllAccounts();
		List<Account> listNoAdmins = new ArrayList<Account>();
		
		for(Account account : list)
			if(!account.getRole().equals("Admin"))
				listNoAdmins.add(account);		
        AccountList accountList = new AccountList();
        List<AccountModel> listAcountModels = new ArrayList<AccountModel>();
        for(Account acc : listNoAdmins) {
        	AccountModel accountModel = new AccountModel();
        	accountModel.setAccount_id(acc.getAccount_id());
        	accountModel.setFirstName(acc.getFirstName());
        	accountModel.setLastName(acc.getLastName());
        	accountModel.setPassword(acc.getPassword());
        	accountModel.setRole(acc.getRole());
        	accountModel.setStatus(acc.getStatus());
        	accountModel.setUsername(acc.getUsername());
        	listAcountModels.add(accountModel);
        }
        	
        
        accountList.setAccounts(listAcountModels);
        return new ResponseEntity<AccountList>(accountList, HttpStatus.OK);
        
    }

    @RequestMapping(value="/create", method = RequestMethod.POST)
    @PreAuthorize("permitAll")
    public ResponseEntity<Account> createAccount(
            @RequestBody Account sentAccount
    ) throws MalformedURLException, IOException {
       
       String realPathDocs = servletContext.getRealPath("/docs");
       String dirName = sentAccount.getUsername();
	   File newDirectory = new File(realPathDocs + "/" + dirName);
	   newDirectory.mkdir();
	   
	   sentAccount.setDirectoryPath(realPathDocs + "/" + dirName);
	   Account createdAccount = accountService.createAccount(sentAccount);
        
       return new ResponseEntity<Account>(createdAccount, HttpStatus.CREATED);
      
    }
    
    @RequestMapping(value="/createByAdmin",
            method = RequestMethod.POST)
    @PreAuthorize ("hasRole('Admin')")
    public ResponseEntity<Account> createAccountByAdmin(
            @RequestBody Account sentAccount
    ) {
    	
    	String realPathDocs = servletContext.getRealPath("/docs");
        String fileName = sentAccount.getUsername();
 	    File newDirectory = new File(realPathDocs + "/" + fileName);
 	    newDirectory.mkdir();
 	    
 	   sentAccount.setRole("User");
   	   sentAccount.setStatus("ACTIVE");
   	   sentAccount.setDirectoryPath(newDirectory.getAbsolutePath());
       Account createdAccount = accountService.createAccount(sentAccount);
    	
    	return new ResponseEntity<Account>(createdAccount, HttpStatus.CREATED);
    	
    }
    
    @RequestMapping(value="/update",method = RequestMethod.POST)
    @PreAuthorize ("hasRole('Admin')")
    public ResponseEntity<Account> updateAccount(
            @RequestBody Account sentAccount
    ) {
    	      	
        Account updatedAccount = accountService.updateAccount(sentAccount);
        return new ResponseEntity<Account>(updatedAccount, HttpStatus.OK);
      
    }
    
    @RequestMapping(value="/remove/{id}",method = RequestMethod.POST)
    @PreAuthorize ("hasRole('Admin')")
    public ResponseEntity<Account> removeAccount(
    		@PathVariable long id
    ) {
    	      	
        Account account = accountService.findAccount(id);
        Account removedAccount = accountService.removeAccount(account);
        return new ResponseEntity<Account>(removedAccount, HttpStatus.OK);
      
    }


    @RequestMapping(value="/{accountId}",
                method = RequestMethod.GET)
    @PreAuthorize ("hasRole('Admin')")
    public ResponseEntity<Account> getAccount(
            @PathVariable Long accountId
    ) {
        Account account = accountService.findAccount(accountId);
        if(account != null)
        {
            return new ResponseEntity<Account>(account, HttpStatus.OK);
        } else {
            return new ResponseEntity<Account>(HttpStatus.NOT_FOUND);
        }
    }
    
    @RequestMapping(value="/role",
            method = RequestMethod.GET)
    @PreAuthorize("permitAll")
	public ResponseEntity<UserRoleModel> getUserRole(
	       
	) {
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Account loggedIn = null;
		String role;
        if(principal instanceof UserDetails) {
            UserDetails details = (UserDetails)principal;
            loggedIn = accountService.findAccountByUsername(details.getUsername());
        }    
        
        if(loggedIn == null) {
        	role = "User";
        }
        else {
        	if(loggedIn.getRole().equals("User")) role = "User";
        	else if(loggedIn.getRole().equals("Admin")) role = "Admin";
        	else role = "Unknown";
        }
        
        UserRoleModel roleModel = new UserRoleModel();
        roleModel.setRole(role);
        
        return new ResponseEntity<UserRoleModel>(roleModel, HttpStatus.OK);
    }
    
   
    @RequestMapping(value="changePassword/{oldPassword}/{newPassword}",
            method = RequestMethod.GET)
    @PreAuthorize ("hasRole('User')")
	public @ResponseBody
	String changePassword(
	        @PathVariable String oldPassword, @PathVariable String newPassword
	) {

    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Account loggedIn = null;
        if(principal instanceof UserDetails) {
            UserDetails details = (UserDetails)principal;
            loggedIn = accountService.findAccountByUsername(details.getUsername());
        }
        else {
        	return "error";
        }
        
        if(loggedIn.getPassword().equals(oldPassword)) {
        	loggedIn.setPassword(newPassword);
        	accountService.updateAccount(loggedIn);
        	return "ok";
        }
        
        return "error";
	}
    
    @RequestMapping(value="/logedIn",
            method = RequestMethod.GET)
	@PreAuthorize("permitAll")
	public ResponseEntity<IsLogedInModel> logedIn() {

    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		boolean isLogedIn = false;   

        if(principal instanceof UserDetails) {
            UserDetails details = (UserDetails)principal;         
            
            if(!details.getAuthorities().isEmpty()) isLogedIn = true;
        }
                
        IsLogedInModel logedInModel = new IsLogedInModel();
        logedInModel.setIsLoged(isLogedIn);
        return new ResponseEntity<IsLogedInModel>(logedInModel,HttpStatus.OK);
	}
	    
    @RequestMapping(value="/userExists/{username}",
            method = RequestMethod.GET)
    @PreAuthorize("permitAll")
	public ResponseEntity<UserExistsModel> userExists(
	        @PathVariable String username
	) {
    	UserExistsModel existsModel = new UserExistsModel();
    	Account acc = accountService.findAccountByUsername(username);
    	if(acc!= null) existsModel.setExists(true);
    	else  existsModel.setExists(false); 
    	return new ResponseEntity<UserExistsModel>(existsModel,HttpStatus.OK);
    }
    
    @RequestMapping(value="/userWithThisEmailAndPasswordExists/{username}/{password}",
            method = RequestMethod.GET)
    @PreAuthorize("permitAll")
	public ResponseEntity<UserExistsModel> userWithThisEmailAndPasswordExists(
	        @PathVariable String username, @PathVariable String password
	) {
    	UserExistsModel existsModel = new UserExistsModel();
    	Account acc = accountService.findAccountByUsername(username);
    	if(acc!= null) {
    		if(acc.getPassword().equals(password))
    			existsModel.setExists(true);
    		else 
    		    existsModel.setExists(false);
    	}
    	else  existsModel.setExists(false); 
    	return new ResponseEntity<UserExistsModel>(existsModel,HttpStatus.OK);
    }
}
