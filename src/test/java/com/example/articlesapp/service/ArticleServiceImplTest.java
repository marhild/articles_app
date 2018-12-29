package com.example.articlesapp.service;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.articlesapp.Exception.ResourceNotFoundException;
import com.example.articlesapp.model.Article;
import com.example.articlesapp.repository.ArticleRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class ArticleServiceImplTest {

    ArticleServiceImpl articleService;

    @Mock
    ArticleRepository articleRepository;

    //entity attributes
    private static final Long ARTICLE_ID = Long.valueOf(1);
    private static final String AUTHOR = "AUTHOR";
    private static final String UPDATED_AUTHOR = "UPDATED_AUTHOR";
    private static final String TITLE = "TITLE";
    private static final String CATEGORY = "CATEGORY";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String CONTENT = "CONTENT";
    private static final Date UPDATED_AT = new Date();
    private static final Date CREATED_AT = new Date();

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        articleService = new ArticleServiceImpl(articleRepository);
    }

    @Test
    public void findById() throws Exception{
        Article article = new Article(TITLE, CATEGORY, AUTHOR, DESCRIPTION, CONTENT);
        article.setArticleId(ARTICLE_ID);
        article.setCreatedAt(CREATED_AT);
        article.setUpdatedAt(UPDATED_AT);

        Optional<Article> articleOptional = Optional.of(article);

        when(articleRepository.findById(ARTICLE_ID)).thenReturn(articleOptional);
        assertThat(articleService.findById(ARTICLE_ID)).isSameAs(article);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getArticleByIdTestNotFound() throws Exception{

        when(articleRepository.findById(ARTICLE_ID)).thenThrow(new ResourceNotFoundException("Article not found!"));

    }

    @Test
    public void getAllArticles() throws Exception{
        Article article = new Article(TITLE, CATEGORY, AUTHOR, DESCRIPTION, CONTENT);
        article.setArticleId(ARTICLE_ID);
        article.setCreatedAt(CREATED_AT);
        article.setUpdatedAt(UPDATED_AT);

        Article article2 = new Article(TITLE, CATEGORY, AUTHOR, DESCRIPTION, CONTENT);
        article2.setArticleId(ARTICLE_ID);
        article2.setCreatedAt(CREATED_AT);
        article2.setUpdatedAt(UPDATED_AT);

        HashSet articlesData = new HashSet();
        articlesData.add(article);
        articlesData.add(article2);

        when(articleService.getAllArticles()).thenReturn(articlesData);

        Set<Article> articles = articleService.getAllArticles();

        assertEquals(articles.size(), 2);
        verify(articleRepository, times(1)).findAll();
    }

    @Test
    public void createArticle() {
        Article article = new Article(TITLE, CATEGORY, AUTHOR, DESCRIPTION, CONTENT);
        article.setArticleId(ARTICLE_ID);
        article.setCreatedAt(CREATED_AT);
        article.setUpdatedAt(UPDATED_AT);

        when(articleService.createArticle(article)).thenReturn(article);
        Optional<Article> articleOptional = Optional.of(article);
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(articleOptional);
        articleOptional.get().setArticleId(ARTICLE_ID);
    }

    @Test
    public void titleAndAuthorValid() {}

    @Test
    public void updateArticle() {
        Article article = new Article(TITLE, CATEGORY, AUTHOR, DESCRIPTION, CONTENT);
        article.setArticleId(ARTICLE_ID);
        article.setCreatedAt(CREATED_AT);
        article.setUpdatedAt(UPDATED_AT);

        //create article
        when(articleRepository.save(article)).thenReturn(article);
        Optional<Article> articleOptional = Optional.of(article);
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(articleOptional);

        //update Article (author)
        articleOptional.get().setUpdatedAt(new Date());
        articleOptional.get().setAuthor(UPDATED_AUTHOR);

        when(articleRepository.save(article)).thenReturn(article);
        Optional<Article> updatedArticleOptional = Optional.of(article);
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(articleOptional);

        //assert
        assertNotEquals(updatedArticleOptional.get().getUpdatedAt(), UPDATED_AT);
        assertEquals(updatedArticleOptional.get().getAuthor(), UPDATED_AUTHOR);
        assertEquals(updatedArticleOptional.get().getCreatedAt(), CREATED_AT);
    }

    @Test
    public void deleteArticle() throws Exception{

        Article article = new Article(TITLE, CATEGORY, AUTHOR, DESCRIPTION, CONTENT);
        article.setArticleId(ARTICLE_ID);
        article.setCreatedAt(CREATED_AT);
        article.setUpdatedAt(UPDATED_AT);

        Optional<Article> articleOptional = Optional.of(article);

        when(articleRepository.findById(ARTICLE_ID)).thenReturn(articleOptional);

        articleService.deleteArticle(articleOptional.get().getArticleId());
        verify(articleRepository, times(1)).delete(articleOptional.get());
    }

    @Test
    public void getLatestEntry() {}

    @Test
    public void findAll() {

        Article article = new Article();
        Article article2 = new Article();

        HashSet articlesData = new HashSet();
        articlesData.add(article);
        articlesData.add(article2);

        when(articleService.getAllArticles()).thenReturn(articlesData);

        Set<Article> articles = articleService.getAllArticles();

        assertEquals(articles.size(), 2);
    }
}