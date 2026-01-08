package sn.uasz.SIGTMA.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (roles.contains("ROLE_ADMINISTRATEUR")) {
            response.sendRedirect("/admin/dashboard");
        } else if (roles.contains("ROLE_BIBLIOTHECAIRE")) {
            response.sendRedirect("/bibliothecaire/dashboard");
        } else if (roles.contains("ROLE_AIDE_BIBLIOTHECAIRE")) {
            response.sendRedirect("/aide-bibliothecaire/dashboard");
        } else if (roles.contains("ROLE_ETUDIANT")) {
            response.sendRedirect("/etudiant/dashboard");
        } else {
            response.sendRedirect("/");
        }
    }
}
