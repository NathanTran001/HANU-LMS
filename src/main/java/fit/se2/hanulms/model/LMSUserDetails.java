package fit.se2.hanulms.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class LMSUserDetails implements UserDetails {
    private LMSUser lmsUser;

    public LMSUserDetails(LMSUser lmsUser) {
        this.lmsUser = lmsUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return lmsUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return lmsUser.getPassword();
    }

    @Override
    public String getUsername() {
        return lmsUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
