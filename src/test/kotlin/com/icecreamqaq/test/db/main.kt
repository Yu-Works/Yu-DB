package com.icecreamqaq.test.db

import com.IceCreamQAQ.Yu.DefaultApp
import com.IceCreamQAQ.Yu.DefaultStarter
import com.IceCreamQAQ.Yu.hook.HookItem
import com.IceCreamQAQ.Yu.hook.YuHook
import com.IceCreamQAQ.Yu.loader.AppClassloader

fun main(args: Array<String>) {
//    AppClassloader.registerBackList(arrayListOf("com.icecreamqaq.test.db.entity.Card"))
    YuHook.put(HookItem("org.hibernate.Version", "initVersion", "com.icecreamqaq.yudb.HibernateVersionHook"))

    DefaultStarter.init(args)
    DefaultStarter.start()
//    DefaultApp().start()
}