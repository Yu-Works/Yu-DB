package com.icecreamqaq.yudb.jpa

import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

open class JPAContext(private val emf: EntityManagerFactory) {

    private val emTl = ThreadLocal<EntityManager>()

    fun getEM(): EntityManager {
        val em = emTl.get()
        return if (em != null && em.isOpen) em
        else {
            val e = emf.createEntityManager()
            emTl.set(e)
            e
        }
    }

    fun closeEM() {
        val em = emTl.get() ?: return
        if (em.isOpen) em.flush()
        em.clear()
        emTl.remove()
    }


}