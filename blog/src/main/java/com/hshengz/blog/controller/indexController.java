package com.hshengz.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class indexController {

    @GetMapping("/")
    public String index(){
        return "index";
    }
    @GetMapping("/types")
    public String types(){

        return "types";
    }
    @GetMapping("/tags")
    public String tags(){

        return "taddgs";
    }
    @GetMapping("/blog")
    public String blog(){

        return "dd";
    }
    @GetMapping("/admin")
    public String admin(){

        return "admin/blogs";
    }
}
