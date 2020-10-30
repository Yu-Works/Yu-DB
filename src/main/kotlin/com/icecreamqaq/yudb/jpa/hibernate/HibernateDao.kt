package com.icecreamqaq.yudb.jpa.hibernate

import com.icecreamqaq.yudb.entity.Page
import com.icecreamqaq.yudb.jpa.JPADao
import org.hibernate.Query
import java.io.Serializable
import javax.inject.Inject

open class HibernateDao<T, PK : Serializable> : JPADao<T, PK>() {

    @Inject
    lateinit var hibernateContext: HibernateContext

    fun getSession() = hibernateContext.getSession()


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

    fun query(hql: String, vararg para: Any): Query{
        val query = getSession().createQuery(hql)?: error("Create Query Error!")
        for ((i, v) in para.withIndex()) {
            query.setParameter(i, v)
        }
        return query
    }

    @JvmOverloads
    fun searchList(hql: String, page: Page? = null, vararg para: Any): List<T> {
        val query = query(hql,*para)
        if (page != null) {
            query.firstResult = (page.id - 1) * page.size
            query.maxResults = page.size
        }
        return query.list() as List<T>
    }

    fun search(hql: String, vararg para: Any): T {
        return query(hql,*para).uniqueResult() as T
    }

    fun execute(hql: String, vararg para: Any): Int {
        return query(hql,*para).executeUpdate()
    }

    override fun where(paras: Map<String, Any>, page: Page?) {
        TODO("Not yet implemented")
    }
}