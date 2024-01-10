package com.example.carparkingapi.domain;

import com.example.carparkingapi.action.Action;
import com.example.carparkingapi.model.Role;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Admin implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.ADMIN;

    @ToString.Exclude
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Action> createdActions;

    @ToString.Exclude
    @OneToMany(mappedBy = "lastModifiedBy", cascade = CascadeType.ALL)
    private List<Action> modifiedActions;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
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
