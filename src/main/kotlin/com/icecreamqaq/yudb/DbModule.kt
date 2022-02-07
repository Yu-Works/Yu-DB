package com.icecreamqaq.yudb

import com.IceCreamQAQ.Yu.hook.*
import com.IceCreamQAQ.Yu.module.Module
import com.icecreamqaq.yudb.annotation.DB
import com.icecreamqaq.yudb.jpa.hibernate.HibernateContext
import org.hibernate.collection.internal.AbstractPersistentCollection
import javax.inject.Inject

class DbModule : Module {
    override fun onLoad() {
    }
}

class HibernateVersionHook : HookRunnable {
    override fun init(info: HookInfo) {
    }

    override fun preRun(method: HookMethod): Boolean {
        method.result = "5.4.23.Final"
        return true
    }

    override fun postRun(method: HookMethod) {

    }

    override fun onError(method: HookMethod): Boolean {
        return false
    }
}