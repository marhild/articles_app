package com.example.articlesapp.controller;

import com.example.articlesapp.Exception.ResourceNotFoundException;
import com.example.articlesapp.model.Article;
import com.example.articlesapp.model.PagerModel;
import com.example.articlesapp.service.ArticleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//TODO Messages

/**
 * @author platoiscoding.com
 */
@Controller
public class ArticleController {

    //view templates
    protected static final String ARTICLE_VIEW = "articles/showArticle";                    //view template for single article
    protected static final String ARTICLE_ADD_FORM_VIEW = "articles/newArticle";            //form for new article
    protected static final String ARTICLE_EDIT_FORM_VIEW = "articles/editArticle";          //form for editing an article
    protected static final String ARTICLE_LIST_VIEW = "articles/allArticles";               //list view of articles
    protected static final String ARTICLE_PAGE_VIEW = "articles/allArticlesPagination";     //list with pagination

    //pagination
    private static final int BUTTONS_TO_SHOW = 3;
    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 5;
    private static final int[] PAGE_SIZES = { 5, 10};

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService){
        this.articleService = articleService;
    }

    /**
     * GET article by id
     * @param articleId
     * @param model         attributeValues
     * @return              view template for single article
     */
    @GetMapping("/article/{id}")
    public String getArticleById(@PathVariable(value = "id") Long articleId, Model model) {
        model.addAttribute("article", articleService.findById(articleId));
        return ARTICLE_VIEW;
    }

    /**
     * GET all articles from database
     * @param model         attributeValues
     * @return              list view of articles
     */
    @GetMapping("/articles")
    public String getAllArticles(Model model) {
        Set<Article> articles = articleService.getAllArticles();
        model.addAttribute("articles", articles);
        return ARTICLE_LIST_VIEW;
    }

    /**
     * GET all articles from database
     * @param pageSize      number of articles per page
     * @param page          subset of all articles
     * @return
     */
    @GetMapping("/articles/pagination")
    public ModelAndView getAllArticlesPagination(@RequestParam("pageSize") Optional<Integer> pageSize,
                                                 @RequestParam("page") Optional<Integer> page) {
        Set<Article> articles = articleService.getAllArticles();
        //TODO check if(articles.isEmpty())

        ModelAndView modelAndView = new ModelAndView(ARTICLE_PAGE_VIEW);

        // If pageSize == null, return initial page size
        int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
        /*
            If page == null || page < 0 (to prevent exception), return initial size
            Else, return value of param. decreased by 1
        */
        int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;

        Page<Article> articlesList = articleService.findAll(PageRequest.of(evalPage, evalPageSize));
        PagerModel pager = new PagerModel(articlesList.getTotalPages(),articlesList.getNumber(),BUTTONS_TO_SHOW);

        modelAndView.addObject("articlesList",articlesList);
        modelAndView.addObject("selectedPageSize", evalPageSize);
        modelAndView.addObject("pageSizes", PAGE_SIZES);
        modelAndView.addObject("pager", pager);

        return modelAndView;
    }

    /**
     * FORM for NEW article
     * @param model     attributesValues
     * @return          ARTICLE_ADD_FORM_VIEW
     */
    @GetMapping("/article/new")
    public String newArticle(Model model){

        //in case of redirection model will contain article
        if (!model.containsAttribute("article")) {
            model.addAttribute("article", new Article());
        }
        //TODO else msg please correct your errors , note that an other can't have two same titles
        return ARTICLE_ADD_FORM_VIEW;
    }

    /**
     * CREATE_NEW_ARTICLE checks...
     *          (1)field values for errors
     *          (2)whether databse already contains an article with the same name and author as field values
     *
     * @param article       entity
     * @param result        result of validation of field values from ARTICLE_ADD_FORM_VIEW
     * @param model         attributeValues
     * @param attr          stores flash attributes; used when method returns a redirect view name
     * @return  if !valid: redirect: '/article/new'
     *          else:      redirect: '/article/{articleId}'
     */
    @PostMapping("/article/create")
    public String createArticle(@Valid Article article, BindingResult result, Model model, RedirectAttributes attr) {

        if (result.hasErrors() || articleService.titleAndAuthorValid(article) == false) {

            //After the redirect: flash attributes pass attributes to the model
            attr.addFlashAttribute("org.springframework.validation.BindingResult.article", result);
            attr.addFlashAttribute("article", article);
            return "redirect:/article/new";
        }
        Article newArticle = articleService.createArticle(article);
        model.addAttribute("article", newArticle);
        //TODO msg
        model.addAttribute("msg", "Article" + " '" + article.getTitle() + "' " + "has been created." );

        return "redirect:/article/" + newArticle.getArticleId();
    }

    /**
     * FORM for EDIT article
     * @param articleId
     * @param model     attributeValues
     * @return          ARTICLE_EDIT_FORM_VIEW
     */
    @GetMapping("/article/{id}/edit")
    public String editArticle(@PathVariable(value = "id") Long articleId, Model model) {
        /*
            in case of redirection from '/article/{id}/update'
            model will contain article with field values
         */
        if (!model.containsAttribute("article")) {
            model.addAttribute("article", articleService.findById(articleId));
        }
        return ARTICLE_EDIT_FORM_VIEW;
    }

    /**
     * UPDATE article with field values from ARTICLE_EDIT_FORM_VIEW
     *
     * @param articleId
     * @param articleDetails    entity
     * @param result            result of validation of field values from ARTICLE_ADD_FORM_VIEW
     * @param model             attributeValues
     * @param attr              stores flash attributes; used when method returns a redirect view name
     * @return  if !valid: redirect: '/article/{articleId}/edit'
     *          else:      redirect: '/article/{articleId}'
     */
    @RequestMapping(path = "/article/{id}/update", method = RequestMethod.POST)
    public String updateArticle(@PathVariable(value = "id") Long articleId, @Valid Article articleDetails,
                                BindingResult result, Model model, RedirectAttributes attr) {

        if (result.hasErrors() || articleService.titleAndAuthorValid(articleDetails) == false) {

            ///After the redirect: flash attributes pass attributes to the model
            attr.addFlashAttribute("org.springframework.validation.BindingResult.article", result);
            attr.addFlashAttribute("article", articleDetails);
            return "redirect:/article/" + articleDetails.getArticleId() + "/edit";
        }

        articleService.updateArticle(articleId, articleDetails);
        model.addAttribute("article", articleService.findById(articleId));
        //TODO msg
        model.addAttribute("msg", "Article has been updated.");
        return "redirect:/article/" + articleId;
    }


    /**
     * DELETE article by id from database
     * @param articleId
     * @param model         attributeValues
     * @return              redirect: '/articles'
     */
    @RequestMapping(value= "/article/{id}/delete")
    public String deleteArticle(@PathVariable("id") Long articleId, Model model) {
        Article article = articleService.findById(articleId);
        String title = article.getTitle();
        articleService.deleteArticle(articleId);
        //TODO msg
        model.addAttribute("msg", "Article" + " '" + title + "' " + "has been deleted.");
        return "redirect:/articles";
    }

    /**
     * Resource Not Found
     * @param exception
     * @return 404error view
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(Exception exception){

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("404error");
        modelAndView.addObject("exception", exception);

        return modelAndView;
    }
}
