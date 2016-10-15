package core.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class LogoutSuccess implements LogoutSuccessHandler {

public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication)
        throws IOException, ServletException {
    if (authentication != null && authentication.getDetails() != null) {
        try {
            httpServletRequest.getSession().invalidate();
         
        } catch (Exception e) {
            e.printStackTrace();
            e = null;
        }
    }

    httpServletResponse.setStatus(HttpServletResponse.SC_OK);

}

}