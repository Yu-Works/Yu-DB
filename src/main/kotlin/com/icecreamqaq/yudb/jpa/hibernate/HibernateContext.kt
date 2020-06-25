package com.icecreamqaq.yudb.jpa.hibernate

import com.icecreamqaq.yudb.jpa.JPAContext
import org.hibernate.jpa.internal.EntityManagerImpl
import javax.persistence.EntityManagerFactory

class HibernateContext(emf: EntityManagerFactory) : JPAContext(emf){
    fun getSession() = (getEM() as EntityManagerImpl).session!!
    fun closeSession() = closeEM()
}