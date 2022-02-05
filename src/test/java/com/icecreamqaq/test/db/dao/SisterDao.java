package com.icecreamqaq.test.db.dao;

import com.icecreamqaq.test.db.entity.Sister;
import com.icecreamqaq.yudb.jpa.JPADao;
import com.icecreamqaq.yudb.jpa.annotation.Select;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface SisterDao extends JPADao<Sister,Integer> {

    default void aaa(){
        System.out.println("This is Java fun aaa!");
    }

    @Select("from Sister where xxx = ?0")
    Sister getByXxx(String xxx);

    @NotNull
    List<Sister> findByName(@NotNull String s);

    @Nullable
    Sister findByNameAndId(@NotNull String s, int id);
}
