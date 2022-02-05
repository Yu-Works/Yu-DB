package com.icecreamqaq.test.db

import com.IceCreamQAQ.Yu.annotation.Event
import com.IceCreamQAQ.Yu.annotation.EventListener
import com.IceCreamQAQ.Yu.event.events.AppStartEvent
import com.icecreamqaq.test.db.entity.*
import com.icecreamqaq.yudb.jpa.annotation.Transactional
import org.hibernate.collection.internal.PersistentBag
import javax.inject.Inject

@EventListener
class Ts {

    @Inject
    private lateinit var cardDao: FCardDao

    @Inject
    private lateinit var skillDao: SkillDao

    @Inject
//    private lateinit var sisterDao

    @Event
    fun start(e: AppStartEvent) {
//        newSister()
//        val card = ss()
        scc()
        scc()
        scc()
    }

    @Transactional
    fun tcs() {
        val card = Card(cardName = "test01")
        cardDao.save(card)
        val skill = Skill(skillName = "skill01")
        card.skillList = arrayListOf(skill)
        cardDao.update(card)
    }

    @Transactional
    fun tc(){
        val card = Card(cardName = "test02")
        cardDao.save(card)
    }

    @Transactional
    fun sc() = cardDao.findByCardName("test02")

    @Transactional
    fun ssc(){
        println(cardDao.findByCardName("test02")!!.skillList)
    }

    @Transactional
    fun scc(){
        println(cardDao.findByCardName("test01"))
        println(cardDao.findByCardName("test01"))
        println(cardDao.findByCardName("test01"))
    }

    @Transactional
    fun ss(): Card {
        val card = cardDao.findByCardName("test01")!!

//        println(card.skillList)
//        PersistentBag
//        card.skillList!!.add(Skill(skillName = "spx"))
        val skill = skillDao.findByCard(card)
        println(skill)
        return card
    }


}