package com.rest.proj.domain.article.controller;

import com.rest.proj.domain.article.dto.ArticleDto;
import com.rest.proj.domain.article.entity.Article;
import com.rest.proj.domain.article.service.ArticleService;
import com.rest.proj.domain.member.entity.Member;
import com.rest.proj.global.rq.Rq;
import com.rest.proj.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles")
public class ApiV1ArticleController {

    private final ArticleService articleService;
    private final Rq rq;



    @GetMapping
    public RsData<ArticlesResponse> getArticles(){
        List<ArticleDto> articles = articleService.getList()
                .stream() // 데이터를 List로 묶어주는 명령어
                .map(article -> new ArticleDto(article))
                .toList();

        //return articles;
        //return RsData.of("S-1", "성공", articles);
        return RsData.of("S-1", "성공", new ArticlesResponse(articles));
    }


    @GetMapping("/{id}")
    public RsData<ArticleResponse> getArticle(@PathVariable("id") Long id){
        //map 명령어는 Optional 데이터만 처리한다
        return articleService.getArticle(id).map(article -> RsData.of(
                "S-1",
                "성공",
                new ArticleResponse(new ArticleDto(article))
                )).orElseGet(()-> RsData.of(
                        "F-1",
                "%d번 게시물은 존재하지 않습니다.".formatted(id),
                null));
    }

    @PostMapping
    public RsData<WriteResponse> write(@Valid @RequestBody WriteRequest writeRequest){
        Member member = rq.getMember();
        RsData<Article> writeRs = articleService.create(member,writeRequest.getSubject(), writeRequest.getContent());

        if(writeRs.isFail()) return (RsData) writeRs;

        return RsData.of(writeRs.getResultCode(),
                writeRs.getMsg(),
                new WriteResponse(new ArticleDto(writeRs.getData()))
        );
    }

    @PatchMapping("/{id}")
    public RsData modify(@Valid @RequestBody ModifyRequest modifyRequest, @PathVariable("id") Long id) {
        Optional<Article> opArticle = articleService.findById(id);

        if ( opArticle.isEmpty() ) return RsData.of(
                "F-1",
                "%d번 게시물은 존재하지 않습니다.".formatted(id)
        );

        // 회원 권한 체크 canModify();

        RsData<Article> modifyRs = articleService.modify(opArticle.get(), modifyRequest.getSubject(), modifyRequest.getContent());

        return RsData.of(
                modifyRs.getResultCode(),
                modifyRs.getMsg(),
                new ModifyResponse(modifyRs.getData())
        );

    }

    @DeleteMapping("/{id}")
    public RsData delete(@PathVariable("id") Long id){
        Optional<Article> opArticle = articleService.findById(id);

        if(opArticle.isEmpty()) return RsData.of(
                "F-1",
                "%d번 게시물은 존재하지 않습니다.".formatted(id)
        );

        RsData<Article> deleteRs = articleService.delete(id);

        return RsData.of(
                deleteRs.getResultCode(),
                deleteRs.getMsg(),
                new DeleteResponse(deleteRs.getData())
        );
    }



    @Getter
    @AllArgsConstructor
    public static class ArticleResponse{
        private final ArticleDto article;
    }

    @Getter
    @AllArgsConstructor
    public static class ArticlesResponse{
        private final List<ArticleDto> articles;
    }

    @Getter
    @AllArgsConstructor
    public static class WriteResponse{
        private final ArticleDto article;
    }

    @Getter
    @AllArgsConstructor
    public static class ModifyResponse {
        private final Article article;
    }

    @Getter
    @AllArgsConstructor
    public static class DeleteResponse {
        private final Article article;
    }

    @Data
    public static class WriteRequest{
        @NotBlank
        private String subject;
        @NotBlank
        private String content;
    }

    @Data
    public static class ModifyRequest {
        @NotBlank
        private String subject;

        @NotBlank
        private String content;
    }

    @Data
    public static class DeleteRequest {
        @NotBlank
        private String subject;

        @NotBlank
        private String content;
    }



}
