package com.icecreamqaq.test.db.entity

import com.icecreamqaq.yudb.jpa.JPADao
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import javax.persistence.*

@Entity
//@Cacheable
@Table(name = "test_card")
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL,region="mycache")
data class Card(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column
    var cardName: String = "",

    ) {

    @OneToMany(cascade = [CascadeType.ALL]) //表示级练操作
    @JoinColumn(name = "cardId")
    var skillList: MutableList<Skill>? = null
}

//interface FCardDao : JPADao<Card, Int> {
//    fun findByCardName(name: String): Card?
////    fun findByCardName(name: String): List<Card>
//}