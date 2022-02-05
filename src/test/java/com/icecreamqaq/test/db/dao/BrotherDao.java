package com.icecreamqaq.test.db.dao;

import com.icecreamqaq.test.db.entity.Brother;
import com.icecreamqaq.test.db.entity.Sister;
import com.icecreamqaq.yudb.jpa.JPADao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface BrotherDao extends JPADao<Brother,Integer> {

//    default void aaa(){
//        execute("")
//    }

    List<Brother> findByName(String name);


    Brother findByNameAndId(String name, int id);
}
