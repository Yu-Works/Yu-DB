package com.icecreamqaq.yudb.jpa.hibernate

import com.IceCreamQAQ.Yu.annotation.InstanceMode
import com.IceCreamQAQ.Yu.hook.HookContext
import com.IceCreamQAQ.Yu.hook.HookInfo
import com.IceCreamQAQ.Yu.hook.HookMethod
import com.IceCreamQAQ.Yu.hook.HookRunnable
import com.icecreamqaq.yudb.jpa.annotation.Transactional
import org.hibernate.Transaction
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@InstanceMode
class HibernateTransactionImpl(val context: HibernateContext) : HookRunnable {

    data class YuTran(val methodHash: Int, val transaction: Transaction)

    private val transactionMethodMap = ConcurrentHashMap<String, Array<String>>()
    private val trans: HashMap<String, ThreadLocal<YuTran>> = HashMap(context.emf.size)


    init {
        for (key in context.emf.keys) {
            trans[key] = ThreadLocal()
        }
    }


    override fun init(info: HookInfo) {
        val dbList = info.method.getAnnotation(Transactional::class.java)?.dbList ?: arrayOf("default")
        info.saveInfo["YuDB.dbList"] = dbList
    }

    override fun preRun(method: HookContext): Boolean {
        val dbList = method.info.saveInfo["YuDB.dbList"] as Array<String>
        for (db in dbList) {
            val trans = trans[db] ?: error("Can't find DataSource: $db")
            val yut = trans.get()
            if (yut != null) if (yut.methodHash == method.info.method.hashCode()) error("This Thread In A Transaction! Can't Open A New Transaction!") else return false

            val tran = context.getSession(db).beginTransaction()
            trans.set(YuTran(method.info.method.hashCode(), tran))
        }

        return false
    }

    override fun postRun(method: HookContext) {
        val dbList = method.info.saveInfo["YuDB.dbList"] as Array<String>
        for (db in dbList) {
            val trans = trans[db] ?: error("Can't find DataSource: $db")
            val yut = trans.get()
            if (yut.methodHash != method.info.method.hashCode()) return
            yut.transaction.commit()
            context.getSession(db).close()
            trans.remove()
        }
    }

    override fun onError(method: HookContext): Boolean {
        val dbList = method.info.saveInfo["YuDB.dbList"] as Array<String>
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