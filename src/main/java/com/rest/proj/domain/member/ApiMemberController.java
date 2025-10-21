package com.rest.proj.domain.member;

import com.rest.proj.domain.member.entity.Member;
import com.rest.proj.domain.member.service.MemberService;
import com.rest.proj.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ApiMemberController {
    private final MemberService memberService;
    //private final MemberDto memberDto;

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
        private Member member;
    }

    @PostMapping("/login")
    public RsData<LoginResponseBody> login(@Valid @RequestBody LoginRequestBody loginRequestBody){
        memberService.authAndMakeTokens(loginRequestBody.getUsername(),loginRequestBody.getPassword());
        return RsData.of("OK", "OK");

    }

}
