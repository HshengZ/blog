package com.hshengz.blog.service;

import com.hshengz.blog.dao.TagRepository;
import com.hshengz.blog.dao.TagRepository;
import com.hshengz.blog.pojo.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    @Autowired
    public TagRepository tagsRepository;

    //根据id查询
    public Tag findOne(Long id){
        Optional<Tag> optional = tagsRepository.findById(id);
        if (optional.isPresent()){
            return optional.get();
        }
        return null;
    }
    //分页查询
    public Page<Tag> findList(Pageable pageable){

        Page<Tag> tagPage = tagsRepository.findAll(pageable);
        return tagPage;
    }
    //保存标签
    @Transactional
    public Tag saveTag(Tag tag){
        if (tag==null){
            return null;
        }
        Tag save = tagsRepository.save(tag);
        return save;
    }
    //更新标签
    @Transactional
    public Tag updateTag(Long id,Tag tag){
        if (this.findOne(id)==null){
            return null;
        }
        Tag save = this.tagsRepository.save(tag);
        return save;
    }
    //删除标签
    @Transactional
    public void deleteTag(Long id){
        this.tagsRepository.deleteById(id);
    }
    //根据名称查询
    public Tag findByName(String name) {
        return this.tagsRepository.findByName(name);
    }

    //查询所有
    public List<Tag> findAll(){
        return tagsRepository.findAll();
    }

    //根据IDS字符串查询
    public List<Tag> listTag(String ids) { //1,2,3

        List<Tag> tags=new ArrayList<>();
        for (Long id : convertToList(ids)) {
            Tag tag = this.findOne(id);
            if (tag!=null){
                tags.add(tag);
            }
        }
        return tags;
    }
    //解析 "1,2,3" 标签的字符串，转换为一个List标签Id集合
    private List<Long> convertToList(String ids) {
        List<Long> list = new ArrayList<>();
        if (!"".equals(ids) && ids != null) {
            String[] idarray = ids.split(",");
            for (int i=0; i < idarray.length;i++) {
                list.add(new Long(idarray[i]));
            }
        }
        return list;
    }
    //根据标签下的博客数量排序查询出前10
    public List<Tag> findListTop(int size) {
        Sort sort=new Sort(Sort.Direction.DESC,"blogs.size");
        return this.tagsRepository.findListTop(PageRequest.of(0,size,sort));

    }
}
