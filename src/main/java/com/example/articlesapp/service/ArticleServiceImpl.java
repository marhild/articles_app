package com.example.articlesapp.service;

import com.example.articlesapp.Exception.ResourceNotFoundException;
import com.example.articlesapp.model.Article;
import com.example.articlesapp.repository.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author platoiscoding.com
 */
@Service
public class ArticleServiceImpl implements ArticleService{

    private final ArticleRepository articleRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public Set<Article> getAllArticles(){
        Set<Article> articleSet =  new HashSet<>();
        articleRepository.findAll().iterator().forEachRemaining(articleSet::add);
        return articleSet;
    }

    @Override
    public Article createArticle(Article article){
        Article newArticle;
        if (titleAndAuthorValid(article) == false) {
            throw new ResourceNotFoundException("Article with this Title and Author already exists!");
        } else {
            article.setCreatedAt(new Date());
            article.setUpdatedAt(new Date());
            newArticle = articleRepository.save(article);
        }
        return newArticle;
    }

    /**
     * tests whether there is an article with te same title and author in the database
     * @param article
     * @return true if there is no article with the same author and title in the database
     */
    @Override
    public boolean titleAndAuthorValid(Article article){
        Set<Article> articleSet = new HashSet<>();
        articleRepository.findByTitleAndAuthor(article.getTitle(),article.getAuthor())
                .iterator().forEachRemaining(articleSet::add);
        if (!articleSet.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Article updateArticle(Long id, Article articleDetails) {
        Article article = findById(id);

        article.setCategory(articleDetails.getCategory());
        article.setTitle(articleDetails.getTitle());
        article.setAuthor(articleDetails.getAuthor());
        article.setDescription(articleDetails.getDescription());
        article.setContent(articleDetails.getContent());
        //created_at is not updatable
        article.setUpdatedAt(new Date());
        articleRepository.save(article);
        return article;
    }

    @Override
    public void deleteArticle(Long articleId) {
        articleRepository.delete(findById(articleId));
    }

    @Override
    public Article findById(Long id){
        Optional<Article> articleOptional = articleRepository.findById(id);

        if (!articleOptional.isPresent()) {
            throw new ResourceNotFoundException("Article Not Found!");
        }

        return articleOptional.get();

    }

    /**
     * @return newest article in the databse
     */
    @Override
    public Article getLatestEntry(){
        Set<Article> articles = getAllArticles();
        if(articles.isEmpty()){
            return null;
        }
        else{
            Long latestArticleID = articleRepository.findTopByOrderByIdDesc();
            return findById(latestArticleID);
        }
    }

    //Pagination
    @Override
    public Page<Article> findAll(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

}
