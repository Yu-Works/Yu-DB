package com.icecreamqaq.test.db

import com.IceCreamQAQ.Yu.`as`.ApplicationService
import com.IceCreamQAQ.Yu.di.YuContext
import com.icecreamqaq.test.db.dao.SisterDao
import com.icecreamqaq.test.db.entity.Sister
import com.icecreamqaq.yudb.jpa.JPAContext
import com.icecreamqaq.yudb.jpa.annotation.Transactional
import java.lang.RuntimeException
import javax.inject.Inject

class Ts :ApplicationService {

    @Inject
    private lateinit var context:YuContext

    @Inject
    private lateinit var jpaContext: JPAContext

    @Inject
    private lateinit var dao:SisterDao

    override fun init() {

    }

    @Transactional
    fun newSister(){
        val sister = Sister()
        sister.name = "tttT44"
        sister.display = "tttT44"
        sister.pass = "b45972211905a67566d118bbb259cdda"

        dao.saveOrUpdate(sister)
    }

    override fun start() {
        newSister()
//
//        val t = sisterDao.getSession().beginTransaction()
//        val s5 = sisterDao.get(5)
//        println(s5)
//
//        s5.display = "tttT1"
//        sisterDao.saveOrUpdate(s5)
//
//
//
//
////        sisterDao.save(sister)
//        t.commit()
//        println(sister)
    }

    override fun stop() {
    }
}