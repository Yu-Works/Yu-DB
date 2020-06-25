package com.icecreamqaq.test.db.entity

import javax.persistence.*

@Entity
@Table(name = "sister")
open class Sister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column
    var name: String? = null

    @Column
    var pass: String? = null

    @Column
    var display: String? = null

    override fun toString(): String {
        return "Sister(id = $id, name = $name, pass = $pass, display = $display)"
    }

}