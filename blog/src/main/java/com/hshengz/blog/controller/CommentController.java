package com.hshengz.blog.controller;

import com.hshengz.blog.pojo.Blog;
import com.hshengz.blog.pojo.Comment;
import com.hshengz.blog.pojo.User;
import com.hshengz.blog.service.BlogService;
import com.hshengz.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Controller
public class CommentController {

    @Autowired
    CommentService commentService;

    @Autowired
    BlogService blogService;

    @Value("${comment.avatar}")
    private String avatar;

    @GetMapping("/comments/{blogId}")
    public String commentS(@PathVariable("blogId") Long blogId, Model model){
        model.addAttribute("comments",commentService.findListByBlogID(blogId));
        return "blog :: commentList";
    }

    @PostMapping("/comments")
    public String saveComment(Comment comment, HttpSession session){
        Blog blog = this.blogService.findOne(comment.getBlog().getId());
        comment.setBlog(blog);
        //查询是否有登录，有登录表示是管理员在评价或者回复
        User user = (User) session.getAttribute("user");
        if (user!=null){
            comment.setAvatar(user.getAvatar());
            comment.setAdminComment(true);
        }else{
            comment.setAvatar(avatar);
        }
        //保存评价信息
       commentService.saveComment(comment);
       return "redirect:/comments/"+blog.getId();
    }

}
