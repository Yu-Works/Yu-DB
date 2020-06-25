package com.icecreamqaq.yudb

import com.IceCreamQAQ.Yu.`as`.ApplicationService
import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.di.YuContext
import javax.inject.Inject

class DbMain : ApplicationService {

    @Config("db.impl")
    private lateinit var dbImpl:String

    @Inject
    private lateinit var yuContext: YuContext

    private lateinit var dbService: DbService

    override fun init() {
        dbService = (yuContext.getBean(dbImpl,"") as DbService? ?: error("Can't Create DbImpl instance"))
        dbService.startup()
    }

    override fun start() {
        println("start")
    }

    override fun stop() {
        dbService.shutdown()
    }

}