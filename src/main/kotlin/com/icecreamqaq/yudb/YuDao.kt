package com.icecreamqaq.yudb

import com.icecreamqaq.yudb.entity.Page
import java.io.Serializable

interface YuDao<T, PK : Serializable> {

    fun get(id: PK): T?
    fun save(entity: T)
    fun delete(id: PK)
    fun update(entity: T)
    fun saveOrUpdate(entity: T)
    fun where(paras: Map<String, Any>, page: Page?)

}