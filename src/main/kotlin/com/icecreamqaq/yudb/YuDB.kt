package com.icecreamqaq.yudb

import com.IceCreamQAQ.Yu.DefaultApp
import com.icecreamqaq.yudb.jpa.JPADao
import com.icecreamqaq.yudb.jpa.hibernate.HibernateContext
import org.hibernate.Session
import org.hibernate.Transaction
import javax.inject.Inject

class YuDB {

    companion object {

        private var multi: Boolean? = null
        private var instance_: YuDB? = null

        @JvmStatic
        val instance
            get() = if (true == multi) error("多例模式下无法通过伴生对象获得实例！") else instance_ ?: error("请先进行初始化！")

        @JvmStatic
        fun start() =
            if (instance_ != null) instance_!!
            else
                YuDB().apply {
                    multi = false
                    instance_ = this
                    start()
                }

    }

    init {
        if (false == multi) error("在单例模式下无法创建多个 YuDB 对象！")
        multi = true
    }

    private lateinit var app: DefaultApp

    @Inject
    private lateinit var context: HibernateContext

    fun start() {
        app = DefaultApp()
        app.start()
        app.context.injectBean(this)
    }

    @JvmOverloads
    fun <T> findInstance(clazz: Class<T>, name: String = ""): T? = app.context.getBean(clazz, name)

    @JvmSynthetic
    inline fun <reified T> findInstance(name: String = ""): T? = findInstance(T::class.java, name)

    fun <T : JPADao<*, *>> findDaoImpl(clazz: Class<T>): T =
        app.context[clazz] ?: error("找不到 Dao 实例，可能未添加到扫描路径或是创建实例失败！")

    @JvmSynthetic
    inline fun <reified T : JPADao<*, *>> findDaoImpl(): T = findDaoImpl(T::class.java)

    @JvmOverloads
    fun currentSession(name: String = "default"): Session = context.getSession(name)
    fun currentSession(vararg names: String): List<Session> = names.map { context.getSession(it) }

    @JvmOverloads
    fun beginTransaction(name: String = "default"): Transaction = context.getSession(name).beginTransaction()
    fun beginTransaction(vararg names: String): List<Transaction> =
        names.map { context.getSession(it).beginTransaction()!! }

    @JvmOverloads
    fun transaction(name: String = "default", runnable: Runnable) {
        val session = currentSession(name)
        val tran = session.beginTransaction()
        try {
            runnable.run()
            tran.commit()
        } catch (e: Exception) {
            tran.rollback()
        }
        session.close()
    }

    @JvmSynthetic
    fun transaction(name: String = "default", body: () -> Unit) {
        val session = currentSession(name)
        val tran = session.beginTransaction()
        try {
            body()
            tran.commit()
        } catch (e: Exception) {
            tran.rollback()
        }
        session.close()
    }

    fun transaction(vararg names: String, runnable: Runnable) {
        val sessions = currentSession(*names)
        val trans = sessions.map { it.beginTransaction()!! }
        try {
            runnable.run()
            trans.forEach { it.commit() }
        } catch (e: Exception) {
            trans.forEach { it.rollback() }
        }
        sessions.forEach { it.close() }
    }

    @JvmSynthetic
    fun transaction(vararg names: String, body: () -> Unit) {
        val sessions = currentSession(*names)
        val trans = sessions.map { it.beginTransaction()!! }
        try {
            body()
            trans.forEach { it.commit() }
        } catch (e: Exception) {
            trans.forEach { it.rollback() }
        }
        sessions.forEach { it.close() }
    }

}