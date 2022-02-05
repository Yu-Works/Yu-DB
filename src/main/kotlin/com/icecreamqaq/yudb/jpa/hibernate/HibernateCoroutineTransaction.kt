package com.icecreamqaq.yudb.jpa.hibernate

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

object HibernateCoroutineTransaction {

    fun <R> transactional(block: () -> R): R = block()

}