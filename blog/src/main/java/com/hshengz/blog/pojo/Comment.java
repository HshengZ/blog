package com.hshengz.blog.pojo;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//评价表
@Entity
@Data
@ToString
@Table(name = "t_comment")
public class Comment {

    @Id
    @GeneratedValue
    private Long id;
    private String nickname;//名称
    private String email;//邮箱
    private String content;//评价内容
    private String avatar;//头像
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;//评价时间

    private Boolean adminComment;//是否是管理员的评价
    //多对一，一个博客有多个评价
    @ManyToOne
    private Blog blog;

    //一对多内联系，一个父评价有多个子评价，：子
    @OneToMany(mappedBy = "parentComment")
    private List<Comment> replyComments = new ArrayList<>();

    //内联系，一个父评价有多个子评价：父
    @ManyToOne
    private Comment parentComment;

    public Comment() {
    }

}
