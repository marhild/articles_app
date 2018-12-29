package com.example.articlesapp.model;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author platoiscoding.com
 */
public class ArticleTest {

    private static final String AUTHOR = "AUTHOR";
    private static final String TITLE = "TITLE";
    private static final String CATEGORY = "CATEGORY";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String CONTENT = "CONTENT";

    @Test
    public void constructorTest() {

        Article article = new Article(TITLE, CATEGORY, AUTHOR, DESCRIPTION, CONTENT);

        assertEquals(TITLE, article.getTitle());
        assertEquals(CATEGORY, article.getCategory());
        assertEquals(AUTHOR, article.getAuthor());
        assertEquals(DESCRIPTION, article.getDescription());
        assertEquals(CONTENT, article.getContent());

    }
}