package com.icecreamqaq.yudb.jpa.hibernate

import com.IceCreamQAQ.Yu.hook.HookInfo
import com.IceCreamQAQ.Yu.hook.HookMethod
import com.IceCreamQAQ.Yu.hook.HookRunnable
import com.icecreamqaq.yudb.jpa.annotation.Transactional
import org.hibernate.Transaction
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class HibernateTransactionImpl : HookRunnable {

    data class YuTran(val methodHash: Int, val transaction: Transaction)

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
    override fun init(info: HookInfo) {
        val dbList = info.method.getAnnotation(Transactional::class.java)?.dbList ?: arrayOf("default")
        info.saveInfo("YuDB.dbList", dbList)
    }

    override fun preRun(method: HookMethod): Boolean {
        val dbList = method.info.getInfo("YuDB.dbList") as Array<String>
        for (db in dbList) {
            val trans = trans[db] ?: error("Can't find DataSource: $db")
            val yut = trans.get()
            if (yut != null) if (yut.methodHash == method.info.method.hashCode()) error("This Thread In A Transaction! Can't Open A New Transaction!") else return false

            val tran = context.getSession(db).beginTransaction()
            trans.set(YuTran(method.info.method.hashCode(), tran))
        }

        return false
    }

    override fun postRun(method: HookMethod) {
        val dbList = method.info.getInfo("YuDB.dbList") as Array<String>
        for (db in dbList) {
            val trans = trans[db] ?: error("Can't find DataSource: $db")
            val yut = trans.get()
            if (yut.methodHash != method.info.method.hashCode()) return
            yut.transaction.commit()
            context.getSession(db).close()
            trans.remove()
        }
    }

    override fun onError(method: HookMethod): Boolean {
        val dbList = method.info.getInfo("YuDB.dbList") as Array<String>
        for (db in dbList) {
            val trans = trans[db] ?: error("Can't find DataSource: $db")
            val yut = trans.get()
            if (yut.methodHash != method.info.method.hashCode()) return false
            yut.transaction.rollback()
            context.getSession(db).close()
            trans.remove()
        }
        return false
    }
}