package com.hshengz.blog.service;

import com.hshengz.blog.dao.CommentRepository;
import com.hshengz.blog.pojo.Comment;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;

    public List<Comment> findListByBlogID(Long blogId){
        Sort sort=new Sort(Sort.Direction.ASC,"createTime");
        List<Comment> comments = this.commentRepository.findByBlogIdAndParentCommentNull(blogId, sort);
        return eachComment(comments);
    }

    @Transactional
    public Comment saveComment(Comment comment){
        long parentCommentId=comment.getParentComment().getId();
        if (parentCommentId!=-1){
            Optional<Comment> optional = this.commentRepository.findById(parentCommentId);
            comment.setParentComment(optional.get());
        }else{
            comment.setParentComment(null);
        }
        comment.setCreateTime(new Date());
        return this.commentRepository.save(comment);
    }


    /**
     * 循环每个顶级的评论节点
     * @param comments
     * @return
     */
    private List<Comment> eachComment(List<Comment> comments){
        List<Comment> commentsView=new ArrayList<>();
        for (Comment comment : comments) {
            Comment c = new Comment();
            BeanUtils.copyProperties(comment,c);
            commentsView.add(c);
        }
        //合并评论的各层子代到第一级子代集合中,(处理成两层)
        combineChildren(commentsView);
        return commentsView;
    }
    /**
     *
     * @param comments root根节点，blog不为空的对象集合
     * @return
     */
    private void combineChildren(List<Comment> comments){
        for (Comment comment:comments){
            //获取回复的评价
            List<Comment> replys=comment.getReplyComments();
            for (Comment reply : replys) {
                //循环迭代，找出子代，存放在tempReplys中
                recursively(reply);
            }
            //修改顶级节点的reply集合为迭代处理后的集合
            comment.setReplyComments(tempReplys);
            //清除临时存放区
            tempReplys = new ArrayList<>();
        }
    }

    //存放迭代找出的所有子代的集合
    private List<Comment> tempReplys = new ArrayList<>();
    /**
     * 递归迭代，剥洋葱
     * @param comment 被迭代的对象
     * @return
     */
    private void recursively(Comment comment){
        tempReplys.add(comment);//顶节点添加到临时存放集合
        if (comment.getReplyComments().size()>0){
            List<Comment> replys=comment.getReplyComments();
            for (Comment reply : replys) {
                tempReplys.add(reply);
                if (reply.getReplyComments().size()>0){
                    //如果还有子代，则继续迭代
                    recursively(reply);
                }
            }

        }
    }
}
