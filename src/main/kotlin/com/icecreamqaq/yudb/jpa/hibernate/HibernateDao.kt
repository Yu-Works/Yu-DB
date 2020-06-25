package com.icecreamqaq.yudb.jpa.hibernate

import com.icecreamqaq.yudb.jpa.JPADao
import java.io.Serializable
import javax.inject.Inject

open class HibernateDao<T, PK : Serializable> : JPADao<T, PK>(){

    @Inject
    lateinit var hibernateContext: HibernateContext

    fun getSession() = hibernateContext.getSession()

    override fun get(id: PK): T {
        return getSession().get(tClass,id) as T
    }

    override fun save(entity: T) {
        getSession().save(entity)
    }

    override fun delete(entity: T) {
        getSession().delete(entity)
    }

    override fun update(entity: T) {
        getSession().update(entity)
    }

    override fun saveOrUpdate(entity: T) {
        getSession().saveOrUpdate(entity)
    }
}