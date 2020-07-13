package com.hshengz.blog.controller;

import com.hshengz.blog.pojo.Blog;
import com.hshengz.blog.service.BlogService;
import com.hshengz.blog.service.TagService;
import com.hshengz.blog.service.TypeService;
import com.hshengz.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class indexController {

    @Autowired
    BlogService blogService;
    @Autowired
    TypeService typeService;
    @Autowired
    TagService tagService;

    //主页
    @GetMapping("/")
    public String index(@PageableDefault(size=8,sort = {"updateTime"},direction = Sort.Direction.DESC)
                                    Pageable pageable, Model model){
        //分页查询博客
        model.addAttribute("page",blogService.findList(pageable));
        //查询分类下的博客最多的博客
        model.addAttribute("types",typeService.findListTop(6));
        //查询标签下最多标签的博客
        model.addAttribute("tags",tagService.findListTop(10));
       //查询最新推荐的博客
        model.addAttribute("recommendBlogs",blogService.findListByRecommendTop(8));
        return "index";
    }
    //搜索
    @PostMapping("/search")
    public String search(@PageableDefault(size=8,sort = {"updateTime"},direction = Sort.Direction.DESC)
                                Pageable pageable, Model model,String query){
        model.addAttribute("page",this.blogService.findByQuery("%"+query+"%",pageable));
        model.addAttribute("query",query);
        return "search";
    }

    @GetMapping("/blog/{id}")
    public String getBlog(@PathVariable("id") Long id,Model model){
        Blog blog = this.blogService.gerBlogAndContent(id);
        model.addAttribute("blog",blog);
        return "blog";
    }

    @GetMapping("/footer/newblog")
    public String newBlog(Model model){
        model.addAttribute("newBlogs",blogService.findListByRecommendTop(3));
        return "_fragments :: newBlogList";
    }
}
