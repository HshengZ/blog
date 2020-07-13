package com.hshengz.blog.controller.admin;

import com.hshengz.blog.dao.TagRepository;
import com.hshengz.blog.dao.TypesRepository;
import com.hshengz.blog.pojo.Blog;
import com.hshengz.blog.pojo.User;
import com.hshengz.blog.pojo.vo.BlogsQuery;
import com.hshengz.blog.service.BlogService;
import com.hshengz.blog.service.TagService;
import com.hshengz.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class BlogsController {
    private static final String INPUT = "admin/blogs-input";
    private static final String LIST = "admin/blogs";
    private static final String REDIRECT_LIST = "redirect:/admin/blogs";

    @Autowired
    BlogService blogService;

    @Autowired
    TypeService typeService;

    @Autowired
    TagService tagService;





    @GetMapping("/blogs")
    public String blogs(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        BlogsQuery blog, Model model) {
        model.addAttribute("types", typeService.findAll());
        model.addAttribute("page", blogService.findList(pageable, blog));
        return LIST;
    }
    //分页查询，局部渲染
    @PostMapping("/blogs/search")
    public String search(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                         BlogsQuery blog, Model model) {
        model.addAttribute("page", blogService.findList(pageable, blog));
        return "admin/blogs :: blogList";
    }

    @GetMapping("/blogs/input")
    public String input(Model model) {
        setTypeAndTag(model);
        model.addAttribute("blog", new Blog());
        return INPUT;
    }

    @GetMapping("/blogs/{id}/input")
    public String update(@PathVariable("id")Long id, Model model){
        setTypeAndTag(model);
        Blog blog = blogService.findOne(id);
        blog.init();
        model.addAttribute("blog",blog);
        return INPUT;
    }
    //保存博客
    @PostMapping("/blogs")
    public String save(Model model, Blog blog, RedirectAttributes redirectAttributes, HttpSession session){
        User user = (User)session.getAttribute("user");
        blog.setType(typeService.findOne(blog.getType().getId()));
        blog.setTags(tagService.listTag(blog.getTagIds()));
        //设置用户信息
        blog.setUser(user);
        //设置分类
        blog.setType(this.typeService.findOne(blog.getType().getId()));

        Blog b;
        if (blog.getId() == null) {
            b =  blogService.saveBlog(blog);
        } else {
            b = blogService.updateBlog(blog.getId(), blog);
        }
        if (b==null){
            redirectAttributes.addFlashAttribute("message","保存失败");
        }else {
            redirectAttributes.addFlashAttribute("message","保存成功");
        }
        return REDIRECT_LIST;
    }




    //在域中添加所有的分类和标签
    private void setTypeAndTag(Model model) {
        model.addAttribute("types", typeService.findAll());
        model.addAttribute("tags", tagService.findAll());
    }

    //删除博客
    @GetMapping("/blogs/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes) {
        blogService.deleteBlog(id);
        attributes.addFlashAttribute("message", "删除成功");
        return REDIRECT_LIST;
    }

    @PostMapping("/uploadImage")
    public @ResponseBody Map<String,Object> uploadImage(@RequestParam(value = "editormd-image-file")MultipartFile multipartFile){
        Map<String, Object> map = blogService.uploadImage(multipartFile);
        if (map==null){
            map=new HashMap<>();
            map.put("success",0);
            map.put("message","上传失败");
        }
        return map;
    }
}
