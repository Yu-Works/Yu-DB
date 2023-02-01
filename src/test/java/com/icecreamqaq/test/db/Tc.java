package com.icecreamqaq.test.db;

import com.IceCreamQAQ.Yu.annotation.Event;
import com.IceCreamQAQ.Yu.annotation.EventListener;
import com.IceCreamQAQ.Yu.event.events.AppStartEvent;
import com.icecreamqaq.test.db.entity.SkillDao;
import com.icecreamqaq.yudb.jpa.annotation.Transactional;

import javax.inject.Inject;

@EventListener
public class Tc {

    @Inject
    private SkillDao skillDao;

//    @Inject
//    private lateinit var sisterDao

    @Event
    public void start(AppStartEvent e ) {
//        newSister()
//        val card = ss()
//        scc()
//        scc()
//        scc()
//        newSkill()
//        newSkill()
        test();
    }

    @Transactional
    public void test(){
        System.out.println(skillDao.findBySkillNameOrderByIdDesc("123456"));
    }
}
