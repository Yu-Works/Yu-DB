package com.icecreamqaq.yudb.jpa

import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.loader.LoadItem
import com.IceCreamQAQ.Yu.loader.Loader
import com.icecreamqaq.yudb.YuDao
import com.icecreamqaq.yudb.jpa.annotation.Dao
import com.icecreamqaq.yudb.jpa.compiler.Spawner
import javax.inject.Inject

class JPADaoLoader : Loader {

    @Inject
    private lateinit var spawner: Spawner

    @Inject
    private lateinit var context: YuContext

    override fun load(items: Collection<LoadItem>) {
        for (value in items) {
            if (!value.clazz.isInterface)continue
            if (value.clazz == Dao::class.java) continue
            if (value.clazz == JPADao::class.java) continue
            val impl = spawner.spawnDaoImpl(value.clazz as Class<YuDao<*, *>>)
            val implInstance = context.newBean(impl!!)!!
            context.putBean(value.clazz as Class<Any>, implInstance)
        }
    }

    override fun priority() = 5
}