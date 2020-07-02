package com.icecreamqaq.yudb.jpa.hibernate

import com.IceCreamQAQ.Yu.hook.HookMethod
import com.IceCreamQAQ.Yu.hook.HookRunnable
import org.hibernate.Transaction
import javax.inject.Inject

class HibernateTransactionImpl :HookRunnable {

    @Inject
    private lateinit var context: HibernateContext

    private val trans = ThreadLocal<Transaction>()

    override fun preRun(method: HookMethod?): Boolean {
        trans.set(context.getSession().beginTransaction())
        return false
    }

    override fun postRun(method: HookMethod?) {
        trans.get()?.commit()
        context.getSession().close()
    }

    override fun onError(method: HookMethod?): Boolean {
        trans.get()?.rollback()
        context.getSession().close()
        return false
    }
}