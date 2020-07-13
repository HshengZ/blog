package com.hshengz.blog.service;

import com.hshengz.blog.dao.TypesRepository;
import com.hshengz.blog.pojo.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TypeService {

    @Autowired
    public TypesRepository typesRepository;


    public Type findOne(Long id){
        Optional<Type> optional = typesRepository.findById(id);
        if (optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    public Page<Type> findList(Pageable pageable){

        Page<Type> typePage = typesRepository.findAll(pageable);
        return typePage;
    }
    @Transactional
    public Type saveType(Type type){
        if (type==null){
            return null;
        }
        Type save = typesRepository.save(type);
        return save;
    }
    @Transactional
    public Type updateType(Long id,Type type){
        if (this.findOne(id)==null){
            return null;
        }
        Type save = this.typesRepository.save(type);
        return save;
    }
    @Transactional
    public void deleteType(Long id){
        this.typesRepository.deleteById(id);
    }

    //根据名称查询
    public Type findByName(String name) {
        return this.typesRepository.findByName(name);
    }
    //查询所有
    public List<Type> findAll(){
       return typesRepository.findAll();
    }

    //查询前拥有最多博客的6个分类
    public List<Type> findListTop(int size) {
        Pageable pageable= PageRequest.of(0,size, new Sort(Sort.Direction.DESC,"blogs.size"));
        return this.typesRepository.findListTop(pageable);
    }
}
