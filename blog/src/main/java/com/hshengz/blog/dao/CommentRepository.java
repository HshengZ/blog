package com.hshengz.blog.dao;

import com.hshengz.blog.pojo.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    //查询博客下的所有评价信息
    List<Comment> findByBlogId(Long blogId, Sort sort);
    //查找第一级别的评价
    List<Comment> findByBlogIdAndParentCommentNull(Long blogId, Sort sort);

    void deleteByBlogId(Long blogId);
}
