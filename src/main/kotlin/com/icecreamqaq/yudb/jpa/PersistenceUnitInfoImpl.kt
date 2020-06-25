package com.icecreamqaq.yudb.jpa

import java.io.File
import java.io.IOException
import java.net.URL
import java.util.*
import javax.persistence.SharedCacheMode
import javax.persistence.ValidationMode
import javax.persistence.spi.ClassTransformer
import javax.persistence.spi.PersistenceUnitInfo
import javax.persistence.spi.PersistenceUnitTransactionType
import javax.sql.DataSource

class PersistenceUnitInfoImpl(
        private val persistenceProviderClass: String,
        private val persistenceUnitName: String,
        private val managedClasses: List<Class<*>>,
        private val mappingFileNames: List<String>,
        private val properties: Properties,
        private val classLoader: ClassLoader,
        private var transactionType: PersistenceUnitTransactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL,
        private var nonJtaDataSource: DataSource
) : PersistenceUnitInfo {


    private var jtaDataSource: DataSource? = null


    override fun getPersistenceUnitName(): String {
        return persistenceUnitName
    }

    override fun getPersistenceProviderClassName(): String {
        return persistenceProviderClass
    }

    override fun getTransactionType(): PersistenceUnitTransactionType {
        return transactionType
    }

    override fun getJtaDataSource(): DataSource? {
        return jtaDataSource
    }

//    fun setJtaDataSource(jtaDataSource: DataSource?): PersistenceUnitInfoImpl {
//        this.jtaDataSource = jtaDataSource
//        nonJtaDataSource = null
//        transactionType = PersistenceUnitTransactionType.JTA
//        return this
//    }

    override fun getNonJtaDataSource(): DataSource {
        return nonJtaDataSource
    }

    fun setNonJtaDataSource(nonJtaDataSource: DataSource): PersistenceUnitInfoImpl {
        this.nonJtaDataSource = nonJtaDataSource
        jtaDataSource = null
        transactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL
        return this
    }

    override fun getMappingFileNames(): List<String> {
        return mappingFileNames
    }

    override fun getJarFileUrls(): List<URL> {
        return emptyList()
    }

    override fun getPersistenceUnitRootUrl(): URL {
        return try {
            File(".").toURI().toURL()
        } catch (e: IOException) {
            throw e
        }
    }

    override fun getManagedClassNames(): List<String> {
//        return managedClasses.map(object : Transformer<Class<*>?, String?>() {
//            fun transform(aClass: Class<*>): String {
//                return aClass.name
//            }
//        })
        val a = ArrayList<String>(managedClasses.size)
        for (managedClass in managedClasses) {
            a.add(managedClass.name)
        }
        return a
    }

    override fun excludeUnlistedClasses(): Boolean {
        return true
    }

    override fun getSharedCacheMode(): SharedCacheMode {
        return SharedCacheMode.UNSPECIFIED
    }

    override fun getValidationMode(): ValidationMode {
        return ValidationMode.AUTO
    }

    override fun getProperties(): Properties {
        return properties
    }

    override fun getPersistenceXMLSchemaVersion(): String {
        return "2.2"
    }

    override fun getClassLoader(): ClassLoader {
        return classLoader
    }

    override fun addTransformer(transformer: ClassTransformer) {}
    override fun getNewTempClassLoader(): ClassLoader {
        return classLoader
    }

}