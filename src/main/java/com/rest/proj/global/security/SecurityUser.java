package com.rest.proj.global.security;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class SecurityUser extends User {
    @Getter
    private Long id;

    public SecurityUser(Long id, String username, String password, List<GrantedAuthority> authorities){
        super(username,password,authorities);
        this.id = id;
    }

    //사용자가 로그인을 했는지 , 그리고 어떤 권한을 가지고 있는가 정보를 가지고 있다.
    public Authentication getAuthentication() {
        //UsernamePasswordAuthenticationToken : 로그인에 대한 표준 객체
        Authentication auth = new UsernamePasswordAuthenticationToken(
                this,
                this.getPassword(),
                this.getAuthorities()
        );
        return auth;
    }
}
