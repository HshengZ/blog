package com.hshengz.blog.pojo.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
//查询条件对象
public class BlogsQuery {

    private String title;//博客标题
    private Long typeId;//分类id
    private boolean recommend;//是否有推荐

    public BlogsQuery() {
    }
}
