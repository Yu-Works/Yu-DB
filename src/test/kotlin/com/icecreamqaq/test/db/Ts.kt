package com.icecreamqaq.test.db

import com.IceCreamQAQ.Yu.annotation.Event
import com.IceCreamQAQ.Yu.annotation.EventListener
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.event.events.AppStartEvent
import com.icecreamqaq.test.db.dao.BrotherDao
import com.icecreamqaq.test.db.dao.SisterDao
import com.icecreamqaq.test.db.entity.Brother
import com.icecreamqaq.test.db.entity.Sister
import com.icecreamqaq.yudb.jpa.JPAContext
import com.icecreamqaq.yudb.jpa.annotation.Transactional
import javax.inject.Inject

@EventListener
class Ts {

    @Inject
    private lateinit var context: YuContext

    @Inject
    private lateinit var jpaContext: JPAContext

    @Inject
    private lateinit var dao: SisterDao

    @Inject
    private lateinit var brotherDao: BrotherDao

    @Event
    fun start(e: AppStartEvent) {
//        newSister()
        newBrother()
        getSister("123", 1)
        getBrother("b1b1", 1)
    }

    private fun getBrother(s: String, id: Int) {
        brotherDao.aaa()
        println(brotherDao.findByName(s))
        println(brotherDao.findByNameAndId(s, id))
        println(brotherDao.findAll())
    }

    private fun getSister(s: String, id: Int) {
        dao.aaa()
        println(dao.findByName(s))
        println(dao.findByNameAndId(s, id))
        println(dao.findAll())
    }

    @Transactional
    fun newSister() {
        val sister = Sister()
        sister.name = "q1q1"
        dao.save(sister)
    }

    @Transactional(dbList = ["bb", "default"])
    fun newBrother() {
        newSister()
        val brother = Brother()
        brother.name = "b3b3"
        brotherDao.save(brother)
    }


}