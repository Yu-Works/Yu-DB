package com.icecreamqaq.yudb.jpa

data class DataSourceInfo(
        val url: String,
        val username: String,
        val password: String,
        val driver: String,
        val dialect: String
)