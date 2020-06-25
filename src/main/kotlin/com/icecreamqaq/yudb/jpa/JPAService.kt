package com.icecreamqaq.yudb.jpa

import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.di.YuContext
import com.icecreamqaq.yudb.DbService
import javax.inject.Inject
import javax.persistence.EntityManagerFactory

abstract class JPAService : DbService {

    @Config("db.url")
    lateinit var url: String

    @Config("db.username")
    lateinit var username: String

    @Config("db.password")
    lateinit var password: String

    @Config("db.driver")
    lateinit var driver: String

    @Config("db.dialect")
    lateinit var dialect:String

    lateinit var entities:List<Class<*>>

    @Inject
    private lateinit var jpaEntitySearcher: JPAEntitySearcher
    @Inject
    private lateinit var yuContext: YuContext

    abstract fun init(): JPAContext

    override fun startup() {
        entities = jpaEntitySearcher.entityList
        val jpaContext = init()
        yuContext.putBean(JPAContext::class.java,"",jpaContext)
//        TODO("Not yet implemented")
    }

    override fun shutdown() {
//        TODO("Not yet implemented")
    }


}