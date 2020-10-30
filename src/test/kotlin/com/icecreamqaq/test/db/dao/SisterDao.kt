package com.icecreamqaq.test.db.dao

import com.icecreamqaq.test.db.entity.Sister
import com.icecreamqaq.yudb.YuDao
import com.icecreamqaq.yudb.jpa.annotation.Dao
import com.icecreamqaq.yudb.jpa.annotation.Execute
import com.icecreamqaq.yudb.jpa.annotation.Select

//class SisterDao : HibernateDao<Sister, Int>()

@Dao
interface SisterDao : YuDao<Sister, Int> {

    fun findByName(name: String): List<Sister>?

    fun findByNameAndId(name: String, id: Int): Sister

    @Execute("delete from Sister where name = ?")
    fun da(name: String)

    @Select("from Sister where name = ?")
    fun sa(name: String): Sister?

    @Select("from Sister")
    fun findAll(): List<Sister>

}