package com.icecreamqaq.yudb.jpa.hibernate

import com.icecreamqaq.yudb.entity.Page
import com.icecreamqaq.yudb.jpa.JPADaoBase
import org.hibernate.Query
import java.io.Serializable
import javax.inject.Inject

open class HibernateDao<T, PK : Serializable> : JPADaoBase<T, PK>() {

    @Inject
    lateinit var hibernateContext: HibernateContext

    fun getSession() = hibernateContext.getSession(tdb)


    override fun get(id: PK): T? {
        return getSession().get(tClass, id)
    }

    override fun save(entity: T) {
        getSession().save(entity)
    }

    override fun delete(id: PK) {
        execute(dft, id)
    }

    override fun update(entity: T) {
        getSession().update(entity)
    }

    override fun saveOrUpdate(entity: T) {
        getSession().saveOrUpdate(entity)
    }

    override fun query(hql: String, vararg para: Any): Query<T> {
        val query = getSession().createQuery(hql) ?: error("Create Query Error!")
        for ((i, v) in para.withIndex()) {
            query.setParameter(i, v)
        }
        return query as Query<T>
    }

    override fun searchList(hql: String, page: Page?, vararg para: Any): List<T> {
        val query = query(hql, *para)
        if (page != null) {
            query.firstResult = (page.id - 1) * page.size
            query.maxResults = page.size
        }
        return query.list() as List<T>
    }

    override fun search(hql: String, vararg para: Any): T {
        return query(hql, *para).uniqueResult() as T
    }

    override fun execute(hql: String, vararg para: Any): Int {
        return query(hql, *para).executeUpdate()
    }

    override fun where(paras: Map<String, Any>, page: Page?) {
        TODO("Not yet implemented")
    }

    override fun findAll(page: Page?) = searchList(ft, page)
}