package com.rest.proj.domain.article.service;

import com.rest.proj.domain.article.entity.Article;
import com.rest.proj.domain.article.repository.ArticleRepository;
import com.rest.proj.global.rsData.RsData;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    /*한 세트로 처리되는 작업의 묶음*/
    /*음식점으로 비유를 하자면*/
    /*손님이 앱으로 주문을 시작했는데 중간에 취소를 하면 모든 작업이 취소가 된다*/
    /*이 취소되는 작업덩어리를 트랜젝션이라한다*/
    /*@Transactional 을 선언하면 다음 동작을 1개의 작업이라고 알려주는 것이다*/
    @Transactional
    public RsData<Article> create(String subject, String content){
        Article article = Article.builder()
                .subject(subject)
                .content(content)
                .build();
        articleRepository.save(article);
        return RsData.of("S-2", "신규게시물이 생성되었습니다.", article);
    }

    public List<Article> getList() {
        return articleRepository.findAll();
    }

    public Optional<Article> getArticle(Long id) {
        //Optional<Article> oq = articleRepository.findById(id);
        /*
        if ( oq.isEmpty() ) {
            return null;
        }
        */
        return articleRepository.findById(id);
    }
    public Optional<Article> findById(Long id) {
        return articleRepository.findById(id);
    }

    public RsData<Article> modify(Article article, @NotBlank String subject, @NotBlank String content) {
        article.setSubject(subject);
        article.setContent(content);
        articleRepository.save(article);

        return RsData.of(
                "S-3",
                "%d번 게시물이 수정되었습니다.".formatted(article.getId()),
                article
        );
    }
}
