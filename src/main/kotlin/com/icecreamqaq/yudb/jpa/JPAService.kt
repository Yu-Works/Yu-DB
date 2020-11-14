package com.icecreamqaq.yudb.jpa

import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.di.YuContext
import com.alibaba.fastjson.JSONObject
import com.icecreamqaq.yudb.DbService
import javax.inject.Inject
import javax.persistence.EntityManagerFactory

abstract class JPAService : DbService {


    lateinit var entities: Map<String, ArrayList<Class<*>>>

    @Inject
    private lateinit var jpaEntitySearcher: JPAEntitySearcher

    @Inject
    private lateinit var yuContext: YuContext

    abstract fun init(): JPAContext

    override fun startup() {
        entities = jpaEntitySearcher.entityList
        val jpaContext = init()
        yuContext.putBean(JPAContext::class.java, "", jpaContext)
//        TODO("Not yet implemented")
    }

    override fun shutdown() {

    }


}