package com.rest.proj.global.security;

import com.rest.proj.domain.member.service.MemberService;
import com.rest.proj.global.rsData.RsData;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private final MemberService memberService;

    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        if (request.getRequestURI().contains("/api/v1/members/login") || request.getRequestURI().equals("/api/v1/members/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = _getCookie("accessToken");


        // accessToken 검증 or refreshToken 발급
        if (!accessToken.isBlank()) {
            if(!memberService.validateToken(accessToken)){
                String refreshToken = _getCookie("refreshToken");
                RsData<String> rs = memberService.refreshAccessToken(refreshToken);

                _addHeaderCookie("accessToken", rs.getData());
            }
            // SecurityUser 가져오기
            // Security 시스템에서 기본적으로 제공하는 클래스이다
            SecurityUser securityUser = memberService.getUserFromAccessToke(accessToken);


            //인가처리
            SecurityContextHolder.getContext().setAuthentication(securityUser.getAuthentication());

        }

        filterChain.doFilter(request, response);
    }
    /*현재 시스템 내에서 있는 모든 쿠키를 가져오는 코드*/
    /*name 은 이 토큰의 종류를 말한다. access토큰인지, refresh토큰인지 구분한다*/
    private String _getCookie(String name){
        Cookie[] cookies = req.getCookies();

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst()
                .map(Cookie::getValue)
                .orElse("");
    }
    private void _addHeaderCookie(String tokenName, String token){
        ResponseCookie cookie = ResponseCookie
                //.from("accessToken", authAndMakeTokensRs.getData().getAccessToken())
                .from(tokenName, token)
                .path("/")  //이 쿠키가 사용되어지는 경로들
                .sameSite("None") // 이 쿠키가 다른 사이트에서도 사용되게 하겠느냐? --> 아니요 None을 설정하면, secure(true)로 꼭 설정해야한다.
                .secure(true) //보안을 강화하겠는가? --> 예
                .httpOnly(true) //http프로토콜을 사용하겠는가? --> 예
                .build();

        resp.addHeader("Set-Cookie", cookie.toString());
    }
}
