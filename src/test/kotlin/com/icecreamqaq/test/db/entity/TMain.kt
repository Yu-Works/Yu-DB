package com.icecreamqaq.test.db.entity

import com.icecreamqaq.yudb.YuDB

fun main() {
    val yuDb = YuDB.start()

    val cardDao = YuDB.instance.findDaoImpl<FCardDao>()

    yuDb.transaction {
        println(cardDao.findAll())
    }
}