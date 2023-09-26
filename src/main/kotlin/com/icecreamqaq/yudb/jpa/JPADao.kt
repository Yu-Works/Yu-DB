package com.icecreamqaq.yudb.jpa

import com.icecreamqaq.yudb.YuDao
import com.icecreamqaq.yudb.entity.Page
import com.icecreamqaq.yudb.jpa.annotation.Dao
import org.hibernate.Query
import java.io.Serializable
import javax.persistence.EntityManager

@Dao
interface JPADao<T, PK : Serializable> : YuDao<T, PK> {

    val entityType: Class<T>

    fun getEM(): EntityManager

    fun saveAndFlush(entity: T)
    fun delete(entity: T)
    fun deleteAndFlush(entity: T)

    fun query(hql: String, vararg para: Any): Query<T>

    fun searchList(hql: String, vararg para: Any): List<T>
    fun searchList(hql: String, page: Page?, vararg para: Any): List<T>
    fun findAll(): List<T>
    fun findAll(page: Page?): List<T>

    fun search(hql: String, vararg para: Any): T

    fun execute(hql: String, vararg para: Any): Int

}