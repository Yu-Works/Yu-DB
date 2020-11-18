package com.icecreamqaq.yudb.jpa.hibernate

import com.IceCreamQAQ.Yu.hook.HookMethod
import com.IceCreamQAQ.Yu.hook.HookRunnable
import com.icecreamqaq.yudb.jpa.annotation.Transactional
import org.hibernate.Transaction
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class HibernateTransactionImpl : HookRunnable {

    data class YuTran(val methodFullName: String, val transaction: Transaction)

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
    private lateinit var trans: HashMap<String, ThreadLocal<YuTran>>

    override fun preRun(method: HookMethod): Boolean {
        val methodFullName = "${method.className}.${method.methodName}"
        val dbList = transactionMethodMap.getOrPut(methodFullName) {
            Class.forName(method.className).methods.first { it.name == method.methodName }.run {
                getAnnotation(Transactional::class.java)?.dbList ?: arrayOf("default")
            }
        }
        for (db in dbList) {
            val trans = trans[db] ?: error("Can't find DataSource: $db")
            val yut = trans.get()
            if (yut != null) if (yut.methodFullName == methodFullName) error("This Thread In A Transaction! Can't Open A New Transaction!") else return false

            val tran = context.getSession(db).beginTransaction()
            trans.set(YuTran(methodFullName, tran))
        }

        return false
    }

    override fun postRun(method: HookMethod) {
        val methodFullName = "${method.className}.${method.methodName}"
        val dbList = transactionMethodMap[methodFullName]
                ?: error("Can't find MethodInfo: $methodFullName")
        for (db in dbList) {
            val trans = trans[db] ?: error("Can't find DataSource: $db")
            val yut = trans.get()
            if (yut.methodFullName != methodFullName) return
            yut.transaction.commit()
            context.getSession(db).close()
            trans.remove()
        }
    }

    override fun onError(method: HookMethod): Boolean {
        val methodFullName = "${method.className}.${method.methodName}"
        val dbList = transactionMethodMap[methodFullName]
                ?: error("Can't find MethodInfo: $methodFullName")
        for (db in dbList) {
            val trans = trans[db] ?: error("Can't find DataSource: $db")
            val yut = trans.get()
            if (yut.methodFullName != methodFullName) return false
            yut.transaction.commit()
            context.getSession(db).close()
            trans.remove()
        }
        return false
    }
}