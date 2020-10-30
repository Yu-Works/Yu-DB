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

    override fun load(items: Map<String, LoadItem>) {
        for (value in items.values) {
            if (value.type == Dao::class.java) continue
            val impl = spawner.spawnDaoImpl(value.type as Class<YuDao<*, *>>)
            val implInstance = context.newBean(impl!!)!!
            context.putBean(value.type, "", implInstance)
        }
    }

    override fun width() = 5
}