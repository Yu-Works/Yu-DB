package com.icecreamqaq.yudb.jpa.hibernate

import com.IceCreamQAQ.Yu.annotation.Config
import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.di.YuContext.Companion.putBean
import com.IceCreamQAQ.Yu.util.dataNode.DataNode
import com.IceCreamQAQ.Yu.util.dataNode.ObjectNode
import com.IceCreamQAQ.Yu.util.type.RelType
import com.icecreamqaq.yudb.jpa.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.persistence.EntityManagerFactory
import kotlin.collections.HashMap

class HibernateImpl : JPAService() {

    @Inject
    private lateinit var context: YuContext

    @Inject
    @field:Named("appClassloader")
    private lateinit var appClassLoader: ClassLoader

    @Config("db")
    lateinit var db: ObjectNode

    private val isDev = File("pom.xml").exists() || File("build.gradle").exists() || File("build.gradle.kts").exists()


    override fun init(): JPAContext {
//        YuHook.put(HookItem("org.hibernate.Version", "initVersion", "com.icecreamqaq.yudb.HibernateVersionHook"))

        val db = ObjectNode().apply { putAll(db) }
        db.remove("impl")
        if (db.containsKey("url")) {
            val default = ObjectNode()
            db["default"] = default
            db["url"]?.let {
                default["url"] = it
                db.remove("url")
            }

            db["username"]?.let {
                default["username"] = it
                db.remove("username")
            }

            db["password"]?.let {
                default["password"] = it
                db.remove("password")
            }

            db["driver"]?.let {
                default["driver"] = it
                db.remove("driver")
            }

            db["dialect"]?.let {
                default["dialect"] = it
                db.remove("dialect")
            }

            db["ddl"]?.let {
                default["ddl"] = it
                db.remove("ddl")
            }

            db["defaultCache"]?.let {
                default["defaultCache"] = it
                db.remove("defaultCache")
            }

            db["supportCache"]?.let {
                default["supportCache"] = it
                db.remove("supportCache")
            }

            db["poolMax"]?.let {
                default["poolMax"] = it
                db.remove("poolMax")
            }

            db["poolIdle"]?.let {
                default["poolIdle"] = it
                db.remove("poolIdle")
            }

        }


        val emfMap = HashMap<String, EntityManagerFactory>(db.size())
        val dataSourceMap = DataSourceMap()
        for ((d,t) in db) {
            try {
                t.asObject(RelType.create(DataSourceInfo::class.java)).run {
//                    context.putBean(DataSourceInfo::class.java,d,this)
                    dataSourceMap[d] = this
                    val hc = HikariConfig()

                    hc.transactionIsolation = "TRANSACTION_READ_COMMITTED"
                    hc.jdbcUrl = url
                    hc.username = username
                    hc.password = password
                    hc.driverClassName = driver
                    hc.maximumPoolSize = poolMax
                    hc.minimumIdle = poolIdle
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
                    prop["hibernate.ejb.loaded.classes"] =
                        entities[d] ?: error("DataSource $d Not Found Any DataBase Entity!")
                    prop["hibernate.hbm2ddl.auto"] = ddl ?: if (isDev) "update" else "validate"
                    prop["hibernate.classLoaders"] = arrayListOf(appClassLoader)
                    prop["hibernate.enable_lazy_load_no_trans"] = true
                    prop["password"] = password
                    prop["driver"] = driver

                    if (supportCache) {
                        prop["hibernate.cache.use_second_level_cache"] = true
                        prop["hibernate.cache.use_query_cache"] = true
                        prop["hibernate.cache.region.factory_class"] =
                            "org.hibernate.cache.ehcache.internal.EhcacheRegionFactory"
                    }

//                    prop["hibernate.cache.provider_configuration_file_resource_path"] = "ehcache_test_test_test.xml"


                    val persistenceUnitInfo = PersistenceUnitInfoImpl(
                        persistenceProviderClass = "org.hibernate.jpa.HibernatePersistenceProvider",
                        persistenceUnitName = "default",
                        managedClasses = entities[d] ?: error("DataSource $d Not Found Any DataBase Entity!"),
                        mappingFileNames = arrayListOf(),
                        properties = prop,
                        classLoader = this::class.java.classLoader,
                        nonJtaDataSource = ds
                    )

                    val emf = EntityManagerFactoryBuilderImpl(
                        PersistenceUnitInfoDescriptor(persistenceUnitInfo),
                        HashMap<String, Any>(),
//                        appClassLoader,
                    ).build()

//                    emf.addNamedEntityGraph()
                    emfMap.put(d, emf)
                }
            } catch (e: Exception) {
                throw RuntimeException("初始化数据源 $d 失败！", e)
            }
        }

        val hibernateContext = HibernateContext(emfMap)
        context.putBean(HibernateContext::class.java, hibernateContext)
        context.putBean(DataSourceMap::class.java, dataSourceMap)
        return hibernateContext

    }


}