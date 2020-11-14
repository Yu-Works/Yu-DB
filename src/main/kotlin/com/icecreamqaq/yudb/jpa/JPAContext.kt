package com.icecreamqaq.yudb.jpa

import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

open class JPAContext(internal val emf: HashMap<String,EntityManagerFactory>) {

    private val emTl = HashMap<String,ThreadLocal<EntityManager>>(emf.size)

    init {
        for (key in emf.keys) {
            emTl[key] = ThreadLocal()
        }
    }

    fun getEM(name:String): EntityManager {
        val em = (emTl[name] ?: error("Not Found $name Context!")).get()
        return if (em != null && em.isOpen) em
        else {
            val e = emf[name]!!.createEntityManager()
            emTl[name]!!.set(e)
            e
        }
    }

    fun closeEM(name:String) {
        val em = (emTl[name] ?: error("Not Found $name Context!")).get() ?: return
        if (em.isOpen) em.flush()
        em.clear()
        emTl[name]!!.remove()
    }


}