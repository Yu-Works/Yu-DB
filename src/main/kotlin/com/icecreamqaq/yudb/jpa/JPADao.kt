package com.icecreamqaq.yudb.jpa

import com.icecreamqaq.yudb.YuDao
import com.icecreamqaq.yudb.entity.Page
import java.io.Serializable
import java.lang.reflect.ParameterizedType
import javax.inject.Inject
import javax.persistence.Id

open class JPADao<T, PK : Serializable> : YuDao<T, PK> {

    @Inject
    lateinit var jpaContext: JPAContext

    var tClass: Class<T> = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>

    var tName = tClass.simpleName
    val ft = "from $tName"
    val cft = "select count(*) from $tName"

    lateinit var id:String
    val dft:String
    init {
        for (field in tClass.declaredFields) {
            if (field.getAnnotation(Id::class.java)!=null){
                id = field.name
                break
            }
        }
        dft = "delete $tName where $id=?"
    }




    fun getEM() = jpaContext.getEM()


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