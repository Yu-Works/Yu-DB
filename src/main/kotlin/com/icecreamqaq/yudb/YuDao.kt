package com.icecreamqaq.yudb

import java.io.Serializable

interface YuDao<T, PK : Serializable> {

    fun get(id: PK): T
    fun save(entity: T)
    fun delete(entity: T)
    fun update(entity: T)
    fun saveOrUpdate(entity: T)

}