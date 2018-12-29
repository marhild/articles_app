package com.example.articlesapp.controller;

import com.example.articlesapp.Exception.ResourceNotFoundException;
import com.example.articlesapp.model.Article;
import com.example.articlesapp.service.ArticleService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * habe beschlossen die tests erstmal ruhen zu lassen, da mir die erfahrung fehlt
 * zu lesen:
 * https://github.com/springframeworkguru/sfg-pet-clinic/blob/master/pet-clinic-web/src/test/java/guru/springframework/sfgpetclinic/controllers/PetControllerTest.java
 * https://docs.spring.io/spring/docs/current/spring-framework-reference/testing.html#spring-mvc-test-framework
 *
 * @author platoiscoding.com
 */
public class ArticleControllerTest {
    @Mock
    ArticleService articleService;

    ArticleController articleController;

    MockMvc mockMvc;

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

    //templates
    protected static final String ARTICLE_VIEW = "articles/showArticle";
    protected static final String ARTICLE_ADD_FORM_VIEW = "articles/newArticle";
    protected static final String ARTICLE_EDIT_FORM_VIEW = "articles/editArticle";
    protected static final String ARTICLE_LIST_VIEW = "articles/allArticles";
    protected static final String ARTICLE_PAGE_VIEW = "articles/allArticlesPagination";

    @Before
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);

        articleController = new ArticleController(articleService);
        mockMvc = MockMvcBuilders.standaloneSetup(articleController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    //getArticleById
    @Test
    public void getArticleById() throws Exception {
        Article article = new Article(TITLE, CATEGORY, AUTHOR, DESCRIPTION, CONTENT);
        article.setArticleId(ARTICLE_ID);
        article.setCreatedAt(CREATED_AT);
        article.setUpdatedAt(UPDATED_AT);

        when(articleService.findById(anyLong())).thenReturn(article);

        mockMvc.perform(get("/article/1"))
                .andExpect(status().isOk())
                .andExpect(view().name(ARTICLE_VIEW))
                .andExpect(model().attributeExists("article"));
                //.andExpect(model().attribute("article", hasProperty("articleID", is(1L))));
        //TODO attribte abragen?
    }

    @Test
    public void handleNotFound() throws Exception {
        when(articleService.findById(anyLong())).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/article/1"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("404error"));
    }


    //newArticle
    @Test
    public void testGetNewArticleForm() throws Exception{

        mockMvc.perform(get("/article/new"))
                .andExpect(status().isOk())
                .andExpect(view().name(ARTICLE_ADD_FORM_VIEW))
                .andExpect(model().attributeExists("article"));
    }

    //createArticle
    @Test
    public void testPostNewArticle() throws Exception {

        Article article = new Article(TITLE, CATEGORY, AUTHOR, DESCRIPTION, CONTENT);
        article.setArticleId(ARTICLE_ID);
        when(articleService.createArticle(any())).thenReturn(article);

        mockMvc.perform(post("/article/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("articleId", "")
                .param("author", AUTHOR)
                .param("title", TITLE)
                .param("category", CATEGORY)
                .param("description", DESCRIPTION)
                .param("content", CONTENT)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(model().errorCount(0))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist("article"))
                .andExpect(view().name("redirect:/article/1"));
        //TODO expected:<redirect:/article/1> but was:<redirect:/article/new>
        //d.h. validierungs-fehler
    }

    /*
    if form validation fails:
        <redirect:/article/new>
        model will contain attribute 'article'
    */
    @Test
    public void testPostNewArticleValidationFail() throws Exception {

        Article article = new Article(TITLE, CATEGORY, AUTHOR, DESCRIPTION, CONTENT);
        when(articleService.createArticle(article)).thenReturn(article);

        mockMvc.perform(post("/article/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("articleId", "")
                //author may not be empty therefore validation fail
                .param("author", "")
                .param("title", TITLE)
                .param("category", CATEGORY)
                .param("description", DESCRIPTION)
                .param("content", CONTENT)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("article"))
                .andExpect(view().name("redirect:/article/new"));
    }

    //Not Found
    @Test
    public void testGetResourceNotFoundException() throws Exception {

        when(articleService.findById(anyLong())).thenThrow(new ResourceNotFoundException("Article not found!"));

        mockMvc.perform(get("/article/1"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("404error"));
    }

    //Bad Request
    @Test
    public void testGetArticleNumberFormatException() throws Exception {

        mockMvc.perform(get("/article/asdf"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("400error"));
    }

    /**
     * loads the ARTICLE_EDIT_FORM_VIEW
     * loads article from database and displays values in FORM
     * @throws Exception
     */
    @Test
    public void testGetEditArticleForm() throws Exception{

        Article article = new Article();
        article.setArticleId(ARTICLE_ID);

        when(articleService.findById(anyLong())).thenReturn(article);

        mockMvc.perform(get("/article/1/edit")
        )
                .andExpect(status().isOk())
                .andExpect(view().name(ARTICLE_EDIT_FORM_VIEW))
                .andExpect(model().attributeExists("article"));
    }

    /**
     * field values from the ARTICLE_EDIT_FORM_VIEW will be sent to "/article/1/update"
     * the article object retains its articleId
     * @throws Exception
     */
    @Test
    public void testPostUpdatedArticle() throws Exception{
        //TODO es muss erst ein articel created werden, bevor er geändert werden kann
        Article article = new Article(TITLE, CATEGORY, AUTHOR, DESCRIPTION, CONTENT);
        article.setArticleId(ARTICLE_ID);
        article.setCreatedAt(CREATED_AT);
        article.setUpdatedAt(UPDATED_AT);

        when(articleService.updateArticle(ARTICLE_ID, article)).thenReturn(article);

        mockMvc.perform(post("/article/1/update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("articleId", "1")
                .param("author", UPDATED_AUTHOR)
                .param("title", TITLE)
                .param("category", CATEGORY)
                .param("description", DESCRIPTION)
                .param("content", CONTENT)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeExists("article"))
                .andExpect(view().name("redirect:/article/1"));
    }

    //redirect, validation fail
    @Test
    public void testPostUpdatedArticleValidationFail() throws Exception{

        Article article = new Article();
        article.setArticleId(ARTICLE_ID);

        when(articleService.createArticle(any())).thenReturn(article);

        mockMvc.perform(post("/article/1/update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("articleId", "1")
                //author may not be empty therefore validation fail
                .param("author", "")
                .param("title", TITLE)
                .param("category", CATEGORY)
                .param("description", DESCRIPTION)
                .param("content", CONTENT)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeDoesNotExist("article"))
                .andExpect(view().name("redirect:/article/1/edit"));
    }

    @Test
    public void deleteArticle() throws Exception{
        Article article = new Article(TITLE, CATEGORY, AUTHOR, DESCRIPTION, CONTENT);
        article.setArticleId(ARTICLE_ID);
        article.setCreatedAt(CREATED_AT);
        article.setUpdatedAt(UPDATED_AT);

        when(articleService.createArticle(article)).thenReturn(article);
        mockMvc.perform(get("/article/{id}/delete", ARTICLE_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"));

        verify(articleService, times(1)).deleteArticle(anyLong());
    }

}