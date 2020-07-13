package com.hshengz.blog.controller;

import com.hshengz.blog.pojo.Type;
import com.hshengz.blog.pojo.vo.BlogsQuery;
import com.hshengz.blog.service.BlogService;
import com.hshengz.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class TypeShowController {

    @Autowired
    TypeService typeService;

    @Autowired
    BlogService blogService;

    @GetMapping("/types/{id}")
    public String types(@PageableDefault(size = 3,sort = {"id"},direction = Sort.Direction.DESC)
                                Pageable pageable, @PathVariable("id")Long id, Model model){
        List<Type> typeList = typeService.findListTop(10000);
        if (id==-1){
            id=typeList.get(0).getId();
        }
        BlogsQuery blogsQuery=new BlogsQuery();
        blogsQuery.setTypeId(id);
        model.addAttribute("types",typeList);
        //根据分类id分页查询博客
        model.addAttribute("page",blogService.findList(pageable,blogsQuery));
        model.addAttribute("activeTypeID",id);
        return "types";
    }

}
