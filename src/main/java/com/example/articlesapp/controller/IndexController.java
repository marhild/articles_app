package com.example.articlesapp.controller;

import com.example.articlesapp.model.Article;
import com.example.articlesapp.service.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author platoiscoding.com
 */
@Controller
public class IndexController{

    private final ArticleService articleService;

    public IndexController(ArticleService articleService) {
        this.articleService = articleService;
    }

    //GET index page
    @GetMapping({"/", "/index"})
    public String getIndex(Model model) {
        Article article = articleService.getLatestEntry();

        //no articles in the databse
        if(article == null){
            /*
                view template will check whether article == null
                and place a dummy article on the index page
             */
            model.addAttribute("article", null);
        }else{
            model.addAttribute("article", article);
        }
        return "index";
    }
}
