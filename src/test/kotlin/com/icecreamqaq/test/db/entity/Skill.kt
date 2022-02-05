package com.icecreamqaq.test.db.entity

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
) {
    @ManyToOne(cascade = [CascadeType.ALL] ,fetch = FetchType.LAZY)
    @JoinColumn(name = "cardId")
    lateinit var card: Card
}

interface SkillDao : JPADao<Skill, Int> {

    fun findByCard(card: Card): List<Skill>

}