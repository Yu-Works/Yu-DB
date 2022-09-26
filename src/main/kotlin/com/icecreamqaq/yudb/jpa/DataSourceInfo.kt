package com.icecreamqaq.yudb.jpa

class DataSourceMap {
    val map = hashMapOf<String, DataSourceInfo>()

    operator fun get(key: String) = map[key]
    operator fun set(key: String, value: DataSourceInfo) {
        map[key] = value
    }
}

class DataSourceInfo(
    var url: String = "",
    var username: String = "",
    var password: String = "",
    var driver: String = "",
    var dialect: String = "",
    var ddl: String? = null,
    var defaultCache: Boolean = false,
    var supportCache: Boolean = false,
    var poolMax: Int = 10,
    var poolIdle: Int = 2
)