package com.icecreamqaq.yudb.jpa.hibernate

import com.icecreamqaq.yudb.jpa.JPAContext
import org.hibernate.internal.SessionImpl
import javax.persistence.EntityManagerFactory

class HibernateContext(emf: HashMap<String, EntityManagerFactory>) : JPAContext(emf) {
    fun getSession(name: String) = (getEM(name) as SessionImpl).session!!
    fun closeSession(name: String) = closeEM(name)
}