package com.example.articlesapp.repository;

import com.example.articlesapp.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author platoiscoding.com
 */
@Repository
public interface ArticleRepository extends PagingAndSortingRepository<Article, Long> {
    /**
     * @return newest articleId
     */
    @Query(value = "SELECT MAX(id) FROM Article")
    Long findTopByOrderByIdDesc();

    /**
     * @param title     title of an article
     * @param author    author of an article
     * @return          List of articles with the same title and author
     */
    //title+author must be unique
    @Query("SELECT a FROM Article a WHERE a.title=:title and a.author=:author")
    List<Article> findByTitleAndAuthor(@Param("title") String title, @Param("author") String author);

    /**
     * @param pageable
     * @return          a page of entities that fulfill the restrictions
     *                  specified by the Pageable object
     */
    Page<Article> findAll(Pageable pageable);
}