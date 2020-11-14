package com.icecreamqaq.test.db.entity

import com.icecreamqaq.yudb.annotation.DB
import javax.persistence.*

@DB("bb")
@Entity
@Table(name = "brother")
open class Brother {

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
        return "Brother(id = $id, name = $name, pass = $pass, display = $display)"
    }

}