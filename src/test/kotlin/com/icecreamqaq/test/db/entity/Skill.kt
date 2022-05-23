package com.icecreamqaq.test.db.entity

import com.icecreamqaq.yudb.entity.Page
import com.icecreamqaq.yudb.jpa.JPADao
import javax.persistence.*

@Entity
@Table(name = "test_Skill")
data class Skill(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column
    var skillName: String = "",
)

interface SkillDao : JPADao<Skill, Int> {

    fun findBySkillNameOrderByIdDesc(skillName: String): List<Skill>

}