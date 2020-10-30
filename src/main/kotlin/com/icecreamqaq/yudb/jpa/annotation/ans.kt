package com.icecreamqaq.yudb.jpa.annotation

import com.IceCreamQAQ.Yu.annotation.HookBy
import com.IceCreamQAQ.Yu.annotation.LoadBy
import com.icecreamqaq.yudb.jpa.JPADaoLoader


@HookBy("com.icecreamqaq.yudb.jpa.hibernate.HibernateTransactionImpl")
annotation class Transactional

@LoadBy(JPADaoLoader::class)
annotation class Dao

annotation class Select(val value: String, val nativeQuery: Boolean = false)
annotation class Execute(val value:String, val nativeQuery:Boolean = false)