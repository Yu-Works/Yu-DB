package com.icecreamqaq.yudb.annotation

@Target(AnnotationTarget.FIELD)
annotation class Max(val equal: Boolean = false)
@Target(AnnotationTarget.FIELD)
annotation class Min(val equal: Boolean = false)
@Target(AnnotationTarget.FIELD)
annotation class Like(val left: Boolean = true, val right: Boolean = true)

@Target(AnnotationTarget.CLASS)
annotation class DB(val value: String = "default")
@Target(AnnotationTarget.CLASS)
annotation class DefaultSupportCache
@Target(AnnotationTarget.FUNCTION)
annotation class EnableCache
@Target(AnnotationTarget.FUNCTION)
annotation class DisableCache


