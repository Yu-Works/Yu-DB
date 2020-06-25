package com.icecreamqaq.yudb.jpa

import com.IceCreamQAQ.Yu.loader.ClassRegister
import javax.persistence.Entity
import javax.persistence.Table

class JPAEntitySearcher : ClassRegister {

    val entityList = ArrayList<Class<*>>()

    override fun register(clazz: Class<*>) {
        if (clazz.getAnnotation(Entity::class.java) != null || clazz.getAnnotation(Table::class.java) != null) entityList.add(clazz)
    }
}