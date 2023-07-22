package org.example.model;

import lombok.*;
import org.example.config.Constants;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Builder
@Getter@Setter@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    /**
     * This is not an entity class, no table is being created because it is not responsibility of
     * transaction-service to store details of user
     *
     * Whenever there is authentication required for APIs in this service, we redirect to User-service
     * for having authentication
     */

    private String username;

    private String password;

    private String authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String[] authorities = this.authorities.split(Constants.AUTHORITIES_DELIMITER);
        return Arrays.stream(authorities)
                .map(authority -> new SimpleGrantedAuthority(authority))
                .collect(Collectors.toList());
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
