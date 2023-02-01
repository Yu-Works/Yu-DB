package com.icecreamqaq.yudb

import com.IceCreamQAQ.Yu.`as`.ApplicationService
import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.loader.AppClassloader
import javax.inject.Inject

class DbMain : ApplicationService {

    @Config("db.impl")
    private lateinit var dbImpl:String

    @Inject
    private lateinit var yuContext: YuContext

    private lateinit var dbService: DbService
    @Deprecated("过时方法", replaceWith = ReplaceWith("priority"))
    override fun width() = 3

    override fun init() {
//        AppClassloader.registerBackList(arrayListOf("org.hibernate."))
        dbService = (yuContext.getBean(Class.forName(dbImpl),"") as DbService? ?: error("Can't Create DbImpl instance"))
        dbService.startup()
    }

    override fun start() = Unit

    override fun stop() = dbService.shutdown()

}