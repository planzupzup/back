package travel.travel.common.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthService {

    public Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!isAuthenticatedUser()) throw new EntityNotFoundException("존재하지 않는 회원입니다.");
        return Long.parseLong(authentication.getName());
    }

    public boolean isAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String &&
                        authentication.getPrincipal().equals("anonymousUser"));
    }
}