package com.hshengz.blog.dao;

import com.hshengz.blog.pojo.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog,Long > , JpaSpecificationExecutor<Blog> {

    //查询最新推荐的博客
    @Query("select b from Blog b where b.recommend=true ")
    List<Blog> findListByRecommendTop(Pageable pageable);
    //搜索博客
    @Query("select b from Blog b where b.title like ?1 or b.content like ?1")
    Page<Blog> findByQuery(String query, Pageable pageable);


    //更新博客浏览次数
    @Query("update Blog  b set b.views = b.views+1 where b.id = ?1 ")
    @Modifying
    @Transactional
    void updateViews(Long blogId);

    //查询所有年份
    @Query("select function('date_format',b.updateTime,'%Y') as year from Blog b group by function('date_format',b.updateTime,'%Y') ")
    List<String> findGroupYear();
    //查询某年份的所有博客
    @Query("select  b from Blog b where function('date_format',b.updateTime,'%Y')=?1")
    List<Blog> findByYear(String year);


}




















