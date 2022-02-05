package com.icecreamqaq.test.db.entity

import javax.persistence.*

@Entity
@Cacheable
@Table(name = "sister")
open class Sister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Int? = null

    @Column
    open var name: String? = null

    @Column
    open var pass: String? = null

    @Column
    open var display: String? = null

    override fun toString(): String {
        return "Sister(id = $id, name = $name, pass = $pass, display = $display)"
    }

}