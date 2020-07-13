package com.hshengz.blog.pojo;

import lombok.Data;
import lombok.ToString;
import lombok.Value;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by limi on 2017/10/14.
 */
@Entity
@ToString
@Data
@Table(name = "t_blog")
public class Blog {

    @Id
    @GeneratedValue
    private Long id;//博客id

    private String title;//博客详情
    @Basic(fetch = FetchType.LAZY)//懒加载，当用到getset的时候才会加载
    @Lob
    private String content;//博客内容
    private String firstPicture;//大图
    private String flag;//标记（原创，转载..）
    private Integer views;//浏览次数
    private boolean appreciation;//是否开启赞赏
    private boolean shareStatement;//是否开启版权说明
    private boolean commentabled;//是否开启评价
    private boolean published;//是否发布
    private boolean recommend;//是否推荐
    private String description;//博客描述

    @Temporal(TemporalType.TIMESTAMP)//时间的生成类型
    private Date createTime;//创建时间
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;//更新时间

    @Transient
    private String tagIds;

    @ManyToOne
    private Type type;//多对一，一个分类有多个博客

    //多对多，多个标签有多个博客
    @ManyToMany(cascade = {CascadeType.PERSIST})
    private List<Tag> tags = new ArrayList<>();

    //多对一，一个用户有多个博客
    @ManyToOne
    private User user;
    //一对多，一个博客有多个评论
    @OneToMany(mappedBy = "blog")// mappedBy = "blog"维护方 是blog 被维护的是 Comment
    private List<Comment> comments = new ArrayList<>();

    public Blog() {
    }
    //初始化 ids字符串的值 "1,2,3,4"
    public void init() {
        this.tagIds = tagsToIds(this.getTags());
    }

    //1,2,3
    private String tagsToIds(List<Tag> tags) {
        if (!tags.isEmpty()) {
            StringBuffer ids = new StringBuffer();
            boolean flag = false;
            for (Tag tag : tags) {
                if (flag) {
                    ids.append(",");
                } else {
                    flag = true;
                }
                ids.append(tag.getId());
            }
            return ids.toString();
        } else {
            return tagIds;
        }
    }
}
