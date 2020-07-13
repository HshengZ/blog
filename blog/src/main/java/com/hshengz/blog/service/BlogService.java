package com.hshengz.blog.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.hshengz.blog.dao.BlogRepository;
import com.hshengz.blog.dao.CommentRepository;
import com.hshengz.blog.exception.NotFoundExcepiton;
import com.hshengz.blog.pojo.Blog;
import com.hshengz.blog.pojo.vo.BlogsQuery;
import com.hshengz.blog.util.MarkdownUtils;
import com.hshengz.blog.util.MyBeanUtils;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.persistence.criteria.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

@Service
public class BlogService {

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    FastFileStorageClient storageClient;
    //文件类型白名单
    private static final List<String> CONTENT_TYPES = Arrays.asList("image/jpeg", "image/gif","image/png","image/jpg","image/bmg","image/webp");

    //根据id查询博客详情
    public Blog  findOne(Long id){
        Optional<Blog> optional = this.blogRepository.findById(id);
        if (optional.isPresent()){
            return optional.get();
        }
        return null;
    }
    //根据条件分页查询博客
    public Page<Blog> findList(Pageable pageable, BlogsQuery blog){
        Specification<Blog> specification = new Specification<Blog>() {
            //动态查询构建器
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();
                //添加标题模糊查询
                if (!"".equals(blog.getTitle()) && blog.getTitle() != null) {
                    predicates.add(cb.like(root.get("title"), "%" + blog.getTitle() + "%"));

                }
                //添加分类查询
                if (blog.getTypeId() != null) {
                    predicates.add(cb.equal(root.get("type").get("id"), blog.getTypeId()));
                }
                //查询是否推荐
                if (blog.isRecommend()) {
                    predicates.add(cb.equal(root.get("recommend"), blog.isRecommend()));
                }
                cq.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        };
        return blogRepository.findAll(specification, pageable);
    }


    //根据标签id关联查询博客
    public Page<Blog> findListByTagID(Long tagId,Pageable pageable){
        return this.blogRepository.findAll(new Specification<Blog>(){
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                Join join = root.join("tags");
                return cb.equal(join.get("id"),tagId);
            }
        },pageable);
    }

    //分页查询已发布的博客
    public Page<Blog> findList(Pageable pageable){
        Specification<Blog> specification = new Specification<Blog>() {
            //动态查询构建器
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();

                //查询是否发布

                predicates.add(cb.equal(root.get("published"), true));

                cq.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        };
        return blogRepository.findAll(specification, pageable);

    }

    //保存博客
    @Transactional
    public Blog saveBlog(Blog blog) {
        if (blog.getId() == null) {
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);
        } else {
            blog.setUpdateTime(new Date());
        }
        return blogRepository.save(blog);
    }
    //更新博客
    @Transactional
    public Blog updateBlog(Long id ,Blog blog){
        Blog one = this.findOne(id);
        if (one ==null){
            throw new NotFoundExcepiton("找不到该博客");

        }
        BeanUtils.copyProperties(blog,one, MyBeanUtils.getNullPropertyNames(blog));
        one.setUpdateTime(new Date());
       return this.blogRepository.save(one);
    }
    //删除博客
    @Transactional
    public void deleteBlog(Long id){
        //删除博客
        this.blogRepository.deleteById(id);
        //删除跟该博客有关的评价信息
        this.commentRepository.deleteByBlogId(id);
    }


    //查询最新推荐的博客
    public List<Blog> findListByRecommendTop(Integer size){
        Sort sort=new Sort(Sort.Direction.DESC,"updateTime");
        return this.blogRepository.findListByRecommendTop(PageRequest.of(0,size,sort));
    }
    //根据搜索条件搜索
    public Page<Blog> findByQuery(String query, Pageable pageable) {
        return this.blogRepository.findByQuery(query,pageable);
    }
    //获取博客详情，将Markdown转换为html5格式，方便前端展示
    public Blog gerBlogAndContent(Long id){
        Blog blog = this.findOne(id);
        if (blog==null){
            throw new NotFoundExcepiton("该博客不存在");
        }
        Blog b = new Blog();
        BeanUtils.copyProperties(blog,b);
        b.setContent(MarkdownUtils.markdownToHtmlExtensions(b.getContent()));

        blogRepository.updateViews(id);//更新博客浏览次数
        return b;
    }

    //查询每个年份的博客 key 年份  value 该年份下的所有博客
    public Map<String ,List<Blog>> archiveBlog(){
       List<String> years=blogRepository.findGroupYear();
        Map<String ,List<Blog>> map=new HashMap<>();
        for (String year : years) {
            map.put(year,this.blogRepository.findByYear(year));
        }
        return map;
    }

    public Long getCount(){
        return blogRepository.count();
    }

    //图片上传
    public Map<String ,Object> uploadImage(MultipartFile file){
        Map<String ,Object> resultMap=new HashMap<>();

        String originalFilename = file.getOriginalFilename();//文件名称

        //校验文件类型
        String contentType = file.getContentType();
        if (!CONTENT_TYPES.contains(contentType)){
            return null;
        }

        try {
            //校验文件内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage==null){
                return null;
            }

            //保存图片到服务器
            //获取文件后缀
            String etc = StringUtils.substringAfterLast(originalFilename, ".");
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), etc, null);

            resultMap.put("success",1);
            resultMap.put("message","上传成功");
            resultMap.put("url","http://120.77.247.188/"+storePath.getFullPath());

            return resultMap;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
