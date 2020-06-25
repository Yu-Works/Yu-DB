package com.icecreamqaq.yudb.jpa.hibernate

import com.IceCreamQAQ.Yu.di.YuContext
import com.icecreamqaq.yudb.jpa.JPAContext
import com.icecreamqaq.yudb.jpa.JPAService
import com.icecreamqaq.yudb.jpa.PersistenceUnitInfoImpl
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.persistence.EntityManagerFactory
import kotlin.collections.HashMap

class HibernateImpl : JPAService() {

    @Inject
    private lateinit var context: YuContext

    @Inject
    @field:Named("appClassLoader")
    private lateinit var appClassLoader: ClassLoader

    override fun init(): JPAContext {
        val hc = HikariConfig()

        hc.transactionIsolation = "TRANSACTION_READ_COMMITTED"
        hc.jdbcUrl = url
        hc.username = username
        hc.password = password
        hc.driverClassName = driver
        hc.maximumPoolSize = 10
        hc.minimumIdle = 2
        hc.connectionTimeout = 30 * 1000
        hc.isAutoCommit = false
        hc.isReadOnly = false

        val ds = HikariDataSource(hc)

        val prop = Properties()
        prop["url"] = url
        prop["javax.persistence.transaction"] = "RESOURCE_LOCAL"
        prop["jpa.dialect"] = dialect
        prop["hibernate.dialect"] = dialect
        prop["username"] = username
        prop["javax.persistence.provider"] = "org.hibernate.ejb.HibernatePersistence"
        prop["hibernate.ejb.loaded.classes"] = entities
        prop["hibernate.hbm2ddl.auto"] = "update"
        prop["password"] = password
        prop["driver"] = driver


        val persistenceUnitInfo = PersistenceUnitInfoImpl(
                persistenceProviderClass = "org.hibernate.jpa.HibernatePersistenceProvider",
                persistenceUnitName = "default",
                managedClasses = entities,
                mappingFileNames = arrayListOf(),
                properties = prop,
                classLoader = this::class.java.classLoader,
                nonJtaDataSource = ds
        )

        val emf = EntityManagerFactoryBuilderImpl(
                PersistenceUnitInfoDescriptor(persistenceUnitInfo), HashMap<String, Any>()
        ).build()

        val hibernateContext =  HibernateContext(emf)
        context.putBean(hibernateContext)
        return hibernateContext
    }


}