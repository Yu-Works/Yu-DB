package com.icecreamqaq.yudb.jpa.annotation

import com.IceCreamQAQ.Yu.annotation.HookBy
import com.IceCreamQAQ.Yu.annotation.LoadBy
import com.icecreamqaq.yudb.jpa.JPADaoLoader


@HookBy("com.icecreamqaq.yudb.jpa.hibernate.HibernateTransactionImpl")
annotation class Transactional(val dbList:Array<String> = ["default"])

@LoadBy(JPADaoLoader::class, mastBean = false)
annotation class Dao

annotation class Select(val value: String, val nativeQuery: Boolean = false)
annotation class Execute(val value: String, val nativeQuery: Boolean = false)