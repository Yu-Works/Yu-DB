package com.icecreamqaq.yudb.entity

data class Page(
    val start: Int,
    val num: Int
) {

    companion object {
        fun page(pIndex: Int, pSize: Int) = Page((pIndex - 1) * pSize, pSize)
    }

}