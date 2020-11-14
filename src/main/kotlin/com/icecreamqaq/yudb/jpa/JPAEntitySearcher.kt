package com.icecreamqaq.yudb.jpa

import com.IceCreamQAQ.Yu.loader.ClassRegister
import com.icecreamqaq.yudb.annotation.DB
import javax.persistence.Entity
import javax.persistence.Table

class JPAEntitySearcher : ClassRegister {

    val entityList = HashMap<String,ArrayList<Class<*>>>()

    override fun register(clazz: Class<*>) {
        if (clazz.getAnnotation(Entity::class.java) != null || clazz.getAnnotation(Table::class.java) != null) entityList.getOrPut(clazz.getAnnotation(DB::class.java)?.value ?:"default"){ArrayList()}.add(clazz)
    }
}