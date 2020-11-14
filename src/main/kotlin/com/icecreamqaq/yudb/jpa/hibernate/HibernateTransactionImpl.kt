package com.icecreamqaq.yudb.jpa.hibernate

import com.IceCreamQAQ.Yu.hook.HookMethod
import com.IceCreamQAQ.Yu.hook.HookRunnable
import com.icecreamqaq.yudb.jpa.annotation.Transactional
import org.hibernate.Transaction
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class HibernateTransactionImpl : HookRunnable {

    @Inject
    private lateinit var context: HibernateContext

    @Inject
    fun init() {
        trans = HashMap(context.emf.size)
        for (key in context.emf.keys) {
            trans[key] = ThreadLocal()
        }
    }

    private val transactionMethodMap = ConcurrentHashMap<String, Array<String>>()
    private lateinit var trans: HashMap<String, ThreadLocal<Transaction>>

    override fun preRun(method: HookMethod): Boolean {
        val dbList = transactionMethodMap.getOrPut("${method.className}.${method.methodName}") {
            Class.forName(method.className).methods.filter { it.name == method.methodName }.first().run {
                getAnnotation(Transactional::class.java)?.dbList ?: arrayOf("default")
            }
        }
        for (db in dbList) {
            val trans = trans[db] ?: error("Can't find DataSource: $db")
            if (trans.get() != null) error("This Thread In A Transaction! Can't Open A New Transaction!")
            val tran = context.getSession(db).beginTransaction()
            trans.set(tran)
        }

        return false
    }

    override fun postRun(method: HookMethod) {
        val dbList = transactionMethodMap["${method.className}.${method.methodName}"]
                ?: error("Can't find MethodInfo: ${method.className}.${method.methodName}")
        for (db in dbList) {
            val trans = trans[db] ?: error("Can't find DataSource: $db")
            trans.get().commit()
            context.getSession(db).close()
        }
    }

    override fun onError(method: HookMethod): Boolean {
        val dbList = transactionMethodMap["${method.className}.${method.methodName}"]
                ?: error("Can't find MethodInfo: ${method.className}.${method.methodName}")
        for (db in dbList) {
            val trans = trans[db] ?: error("Can't find DataSource: $db")
            trans.get().rollback()
            context.getSession(db).close()
        }
        return false
    }
}