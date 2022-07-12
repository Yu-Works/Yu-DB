package com.icecreamqaq.yudb.jpa

import com.icecreamqaq.yudb.YuDao
import com.icecreamqaq.yudb.annotation.DB
import com.icecreamqaq.yudb.entity.Page
import java.io.Serializable
import java.lang.reflect.ParameterizedType
import javax.inject.Inject
import javax.persistence.EmbeddedId
import javax.persistence.Id
import javax.persistence.IdClass

abstract class JPADaoBase<T, PK : Serializable> : JPADao<T, PK> {

    @Inject
    lateinit var jpaContext: JPAContext

    var tClass: Class<T> = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>

    val tName = tClass.simpleName
    val tdb = tClass.getAnnotation(DB::class.java)?.value ?: "default"
    val ft = "from $tName"
    val cft = "select count(*) from $tName"

    var id: String? = null
    val dft: String?

    init {
        if (tClass.getAnnotation(IdClass::class.java) == null)
            for (field in tClass.declaredFields) {
                if (field.getAnnotation(Id::class.java) != null) {
                    id = field.name
                    break
                }
                if (field.getAnnotation(EmbeddedId::class.java) != null) {
                    id = field.name
                    break
                }
            }
        dft = id?.let { "delete $tName where $id=?0" }
    }


    override fun searchList(hql: String, vararg para: Any) = searchList(hql, null, *para)
    override fun findAll() = findAll(null)

    override fun getEM() = jpaContext.getEM(tdb)


    override fun get(id: PK): T? {
        return getEM().find(tClass, id)
    }

    override fun save(entity: T) {
        val em = getEM()
        if (!em.contains(entity)) em.persist(entity)
        else em.merge(entity)
    }

    override fun delete(id: PK) {
//        getEM().remove(entity)
    }

    override fun update(entity: T) {
        getEM().merge(entity)
    }

    override fun saveOrUpdate(entity: T) {
        save(entity)
    }

    override fun where(paras: Map<String, Any>, page: Page?) {
        TODO("Not yet implemented")
    }
}