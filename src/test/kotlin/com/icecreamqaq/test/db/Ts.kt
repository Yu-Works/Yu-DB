package com.icecreamqaq.test.db

import com.IceCreamQAQ.Yu.`as`.ApplicationService
import com.IceCreamQAQ.Yu.annotation.Event
import com.IceCreamQAQ.Yu.annotation.EventListener
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.event.events.AppStartEvent
import com.icecreamqaq.test.db.dao.SisterDao
import com.icecreamqaq.test.db.entity.Sister
import com.icecreamqaq.yudb.jpa.JPAContext
import com.icecreamqaq.yudb.jpa.annotation.Transactional
import java.lang.RuntimeException
import javax.inject.Inject

@EventListener
class Ts  {

    @Inject
    private lateinit var context:YuContext

    @Inject
    private lateinit var jpaContext: JPAContext

    @Inject
    private lateinit var dao:SisterDao

    @Event
    fun start(e:AppStartEvent){
//        newSister()
        getSister("123",1)
    }

    private fun getSister(s: String,id:Int) {
        println(dao.findByName(s))
        println(dao.findByNameAndId(s,id))
        println(dao.findAll())
    }

    @Transactional
    fun newSister(){
        val sister = Sister()
        sister.name = "789789798789"
        dao.save(sister)
    }


}