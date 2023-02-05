package com.icecreamqaq.yudb.jpa.hibernate

import com.IceCreamQAQ.Yu.annotation.InstanceMode
import com.IceCreamQAQ.Yu.hook.HookContext
import com.IceCreamQAQ.Yu.hook.HookInfo
import com.IceCreamQAQ.Yu.hook.HookRunnable
import com.icecreamqaq.yudb.jpa.annotation.Transactional
import org.hibernate.Session
import org.hibernate.Transaction
import java.util.concurrent.ConcurrentHashMap

@InstanceMode
class HibernateTransactionImpl(val context: HibernateContext) : HookRunnable {

    data class YuTran(
        val methodHash: Int,
        val session: Session,
        val transaction: Transaction
    )

    private val trans: ThreadLocal<YuTran> = ThreadLocal()


    override fun init(info: HookInfo) {
        val dbList = info.method.getAnnotation(Transactional::class.java)?.dbList ?: arrayOf("default")
        info.saveInfo["YuDB.dbList"] = dbList
    }

    override fun preRun(context: HookContext): Boolean {
        val dbList = context.info.saveInfo["YuDB.dbList"] as Array<String>
        val transList = ArrayList<YuTran>(dbList.size)
        for (db in dbList) {
            val yut = trans.get()
            if (yut != null) return false
            val session = this.context.getSession(db)
            val tran = session.beginTransaction()
            YuTran(context.info.method.hashCode(), session, tran).let {
                transList.add(it)
                trans.set(it)
            }
        }
        context.saveInfo("YuDB.Trans", transList)
        return false
    }

    override fun postRun(context: HookContext) {
        val trans = context.getInfo("YuDB.Trans") as? List<YuTran> ?: return
        var throwable: Throwable? = null
        try {
            trans.forEach { it.transaction.commit() }
        } catch (e: Throwable) {
            throwable = e
            trans.forEach { it.transaction.rollback() }
        }
        trans.forEach { it.session.close() }
        this.trans.remove()
        throwable?.let { throw it }
    }


    override fun onError(context: HookContext): Boolean {
        val trans = context.getInfo("YuDB.Trans") as? List<YuTran> ?: return false
        trans.forEach { it.transaction.rollback() }
        trans.forEach { it.session.close() }
        this.trans.remove()
        return false
    }
}