package com.rest.proj.domain.member;

import com.rest.proj.domain.member.dto.MemberDto;
import com.rest.proj.domain.member.service.MemberService;
import com.rest.proj.global.rsData.RsData;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ApiMemberController {
    private final MemberService memberService;
    private final HttpServletResponse resp;

    @GetMapping("/test")
    public String memberTest(){
        return "member test";
    }

    @Getter
    public static class LoginRequestBody {
        @NotBlank
        public String username;
        @NotBlank
        public String password;
    }

    @Getter
    @AllArgsConstructor
    public static class LoginResponseBody{
        //private Member member;
        private MemberDto memberDto;
    }

    @PostMapping("/login")
    public RsData<LoginResponseBody> login(@Valid @RequestBody LoginRequestBody loginRequestBody, HttpServletResponse resp){
        //memberService.authAndMakeTokens(loginRequestBody.getUsername(),loginRequestBody.getPassword());
        RsData<MemberService.AuthAndMakeTokensResponseBody> authAndMakeTokensRs = memberService.authAndMakeTokens(loginRequestBody.getUsername(), loginRequestBody.getPassword());
        //access쿠키를 만드는 코드

        _addHeaderCookie("accessToken", authAndMakeTokensRs.getData().getAccessToken());
        _addHeaderCookie("refreshToken", authAndMakeTokensRs.getData().getRefreshToken());


        return RsData.of(
                authAndMakeTokensRs.getResultCode(),
                authAndMakeTokensRs.getMsg(),
                new LoginResponseBody(new MemberDto(authAndMakeTokensRs.getData().getMember()))
        );

    }
    @GetMapping("/me")
    public String me(){
        return "내정보";
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
