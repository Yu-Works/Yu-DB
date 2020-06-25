package com.icecreamqaq.yudb.jpa.annotation

import com.IceCreamQAQ.Yu.annotation.HookBy


@HookBy("com.icecreamqaq.yudb.jpa.hibernate.HibernateTransactionImpl")
annotation class Transactional