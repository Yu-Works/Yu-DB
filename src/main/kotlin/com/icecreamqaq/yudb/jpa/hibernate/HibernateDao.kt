package com.icecreamqaq.yudb.jpa.hibernate

import com.icecreamqaq.yudb.annotation.DB
import com.icecreamqaq.yudb.entity.Page
import com.icecreamqaq.yudb.jpa.JPADaoBase
import org.hibernate.query.Query
import java.io.Serializable
import javax.inject.Inject

open class HibernateDao<T, PK : Serializable> : JPADaoBase<T, PK>() {

    @Inject
    lateinit var hibernateContext: HibernateContext

    fun getSession() = hibernateContext.getSession(tdb)


//    lateinit var dataSourceInfo: DataSourceInfo
//
//    var defaultCache: Boolean = false
//
//    @Inject
//    fun init() {
//        dataSourceInfo = dataSourceMap[tClass.getAnnotation(DB::class.java)?.value ?: "default"]!!
//        defaultCache = dataSourceInfo.defaultCache
//    }

    override fun get(id: PK): T? {
        return getSession().get(tClass, id)
    }

    override fun save(entity: T) {
        getSession().save(entity)
    }

    override fun delete(id: PK) {
        dft?.let { execute(it, id) } ?: getSession().delete(get(id))
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

    fun queryCache(hql: String, vararg para: Any): Query<T> {
        val query = getSession().createQuery(hql) ?: error("Create Query Error!")
        query.isCacheable = true
        for ((i, v) in para.withIndex()) {
            query.setParameter(i, v)
        }
        return query as Query<T>
    }

    fun nativeQuery(sql: String, resultClass: Class<*>, vararg para: Any): Query<T> {
        val query = getSession().createNativeQuery(sql, resultClass) ?: error("Create Query Error!")
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

    fun searchListCache(hql: String, vararg para: Any) = searchListCache(hql, null, *para)
    fun searchListCache(hql: String, page: Page?, vararg para: Any): List<T> {
        val query = queryCache(hql, *para)
        if (page != null) {
            query.firstResult = (page.id - 1) * page.size
            query.maxResults = page.size
        }
        return query.list() as List<T>
    }

    fun nativeSearchList(hql: String, resultClass: Class<*>, page: Page?, vararg para: Any): List<T> {
        val query = nativeQuery(hql, resultClass, *para)
        if (page != null) {
            query.firstResult = (page.id - 1) * page.size
            query.maxResults = page.size
        }
        return query.list() as List<T>
    }

    override fun search(hql: String, vararg para: Any): T {
        return query(hql, *para).uniqueResult() as T
    }

    fun searchCache(hql: String, vararg para: Any): T {
        return queryCache(hql, *para).uniqueResult() as T
    }

    fun search(hql: String, page: Page, vararg para: Any): T {
        return query(hql, *para)
            .setFirstResult(page.run { (id - 1) * size })
            .setMaxResults(page.size)
            .uniqueResult() as T
    }

    fun searchCache(hql: String, page: Page, vararg para: Any): T {
        return queryCache(hql, *para)
            .setFirstResult(page.run { (id - 1) * size })
            .setMaxResults(page.size)
            .uniqueResult() as T
    }

    fun nativeSearch(hql: String, resultClass: Class<*>, vararg para: Any): T {
        return nativeQuery(hql, resultClass, *para).uniqueResult() as T
    }

    override fun execute(hql: String, vararg para: Any): Int {
        return query(hql, *para).executeUpdate()
    }

    override fun where(paras: Map<String, Any>, page: Page?) {
        TODO("Not yet implemented")
    }

    override fun deleteAndFlush(entity: T) {
        getSession().let {
            it.delete(entity)
            it.flush()
        }
    }

    override fun saveAndFlush(entity: T) {
        getSession().let {
            it.save(entity)
            it.flush()
        }
    }

    override fun findAll(page: Page?) = searchList(ft, page)
    override fun delete(entity: T) {
        getSession().delete(entity)
    }
}