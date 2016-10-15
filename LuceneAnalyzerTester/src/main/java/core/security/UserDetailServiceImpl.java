package core.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import core.models.entities.Account;
import core.services.AccountService;

@SuppressWarnings("deprecation")
@Component
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private AccountService service;

	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Account account = service.findAccountByUsername(name);
        if(account == null) {
            throw new UsernameNotFoundException("no user found with " + name);
        }
        else {
        	String username = account.getUsername();
	        String password = account.getPassword();
	        //additional informations
	        boolean enabled = account.getStatus().equals("ACTIVE");
	        boolean accountNonExpired = account.getStatus().equals("ACTIVE");
	        boolean credentialsNonExpired = account.getStatus().equals("ACTIVE");
	        boolean accountNonLocked = account.getStatus().equals("ACTIVE");
	        
	        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	        authorities.add(new GrantedAuthorityImpl(account.getRole()));
	        User securityUser = new User(username,password,enabled,accountNonExpired,credentialsNonExpired,accountNonLocked,authorities);
	        
	        return securityUser;
        }
    }

}