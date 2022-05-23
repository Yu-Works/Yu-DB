package com.icecreamqaq.yudb.jpa.compiler

import com.IceCreamQAQ.Yu.di.YuContext
import com.IceCreamQAQ.Yu.toLowerCaseFirstOne
import com.icecreamqaq.yudb.YuDao
import com.icecreamqaq.yudb.annotation.DB
import com.icecreamqaq.yudb.annotation.DefaultSupportCache
import com.icecreamqaq.yudb.annotation.DisableCache
import com.icecreamqaq.yudb.annotation.EnableCache
import com.icecreamqaq.yudb.entity.Page
import com.icecreamqaq.yudb.jpa.DataSourceInfo
import com.icecreamqaq.yudb.jpa.DataSourceMap
import com.icecreamqaq.yudb.jpa.annotation.Execute
import com.icecreamqaq.yudb.jpa.annotation.Select
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import javax.inject.Inject
import javax.persistence.Entity
import javax.persistence.Table
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.javaType

class Spawner {

    @Inject
    private lateinit var daoCompiler: DaoCompiler

//    @Inject
//    lateinit var dataSourceMap: DataSourceMap

    @Inject
    private lateinit var context: YuContext

    fun Class<*>.getEntityName() =
        this.getAnnotation(Entity::class.java)?.name?.let { if (it == "") null else it }
            ?: this.getAnnotation(Table::class.java)?.name?.let { if (it == "") null else it }
            ?: this.simpleName

    data class SelectImpl(val hql: String, val nativeQuery: Boolean = false, val nqt: Class<*> = Any::class.java)

    fun spawnDaoImpl(dao: Class<out YuDao<*, *>>): Class<*>? {


        val xa = arrayOf("asc", "desc")
        val entityClass = (dao.genericInterfaces[0] as ParameterizedType).actualTypeArguments[0] as Class<*>
        val pkClass = (dao.genericInterfaces[0] as ParameterizedType).actualTypeArguments[1] as Class<*>
        val dataSourceMap = context[DataSourceMap::class.java]!!
        val dsi = dataSourceMap[entityClass.getAnnotation(DB::class.java)?.value ?: "default"]!!
        val defaultCache =
            if (dsi.defaultCache) true else entityClass.getAnnotation(DefaultSupportCache::class.java) != null

        val implName = "${dao.simpleName}Impl"
        val implClassStringBuilder =
            StringBuilder("package impl.icecreamqaq.yudb.jpa.dao.spawnImpl;\n\nimport com.icecreamqaq.yudb.jpa.hibernate.HibernateDao;\nimport com.icecreamqaq.yudb.entity.Page;\n\n")
        implClassStringBuilder.append("public class $implName extends HibernateDao<${entityClass.name}, ${pkClass.name}> implements ${dao.name} {\n\n")

        val kDao = dao.kotlin

        fun <T> List<T>.first() = if (isEmpty()) null else this[0]

        val innerImpl = dao.declaredClasses.filter { it.name == "DefaultImpls" }.first() ?: try {
            Class.forName("${dao.name}\$DefaultImpls")
        } catch (e: ClassNotFoundException) {
            null
        }

        fun KCallable<*>.xe(text: String): Nothing = error("在对方法 ${dao.name}.${this.name} 进行解析$text。")

        fun KCallable<*>.makeSelectMethod(select: SelectImpl): String {
            val page = this.parameters.last().type.javaType.typeName == Page::class.java.name

            val returnType = this.returnType.javaType.toClass()

            val ps: String
            val qs: String

            if (this.parameters.size == 1) {
                ps = ""
                qs = ""
            } else {
                val psb = StringBuilder()
                val qsb = StringBuilder()

                for (i in 1 until if (page) parameters.size - 1 else parameters.size) {
                    with(parameters[i]) {
                        psb.append(type.javaType.toClass().name).append(" ").append(name ?: "arg$index").append(",")
                        qsb.append(", ").append(name ?: "arg$index")
                    }
                }
                if (page) psb.append("Page iooIooIooPage,")
                ps = psb.toString().let { s -> s.substring(0, s.length - 1) }

                qs = (if (page) ", iooIooIooPage" else "") + qsb.toString()
            }


            val searchList = returnType.isAssignableFrom(java.util.List::class.java)
            val cache =
                if (hasAnnotation<DisableCache>()) false
                else if (hasAnnotation<EnableCache>()) true
                else defaultCache
            var searchMethod =
                if (searchList)
                    if (select.nativeQuery) "nativeSearchList"
                    else "searchList"
                else if (select.nativeQuery) "nativeSearch"
                else "search"

            if (cache) searchMethod += "Cache"

            return """
                @Override
                public ${returnType.name} ${this.name}($ps) {
                    return $searchMethod("${select.hql}" $qs);
                }
                """.trimIndent()
        }

        fun KCallable<*>.makeExecuteMethod(execute: Execute): String {
            val returnType = this.returnType.javaType.toClass()

            val ps: String
            val qs: String

            if (this.parameters.size == 1) {
                ps = ""
                qs = ""
            } else {
                val psb = StringBuilder()
                val qsb = StringBuilder()

                for (i in 1 until parameters.size) {
                    with(parameters[i]) {
                        psb.append(type.javaType.toClass().name).append(" ").append(name ?: "arg$index").append(",")
                        qsb.append(", ").append(name ?: "arg$index")
                    }
                }

                ps = {
                    val s = psb.toString()
                    s.substring(0, s.length - 1)
                }()
                qs = qsb.toString()
            }



            return """
                @Override
                public ${returnType.name} ${this.name}($ps) {
                    ${if (returnType.name == "void") "" else "return"} execute("${execute.value}" $qs);
                }
                """.trimIndent()
        }

        fun KCallable<*>.makeFindByMethod(): String {
            val findStr = name.substring(6)
            val hb = StringBuilder("from ${entityClass.simpleName} where ")

            var pName = ""
            var pOp = "Is"
            var next = ""

            var blinkLoop = 0
            var orderBy = false
            var ndd = false

            var ci = 0

            fun end() {
                if (orderBy) {
                    if (next !in (xa)) error(" OrderBy 时，遇到无法解析的后置字符: $pOp")
                    if (!ndd) ndd = true else hb.append(",")
                    hb.append(pName.toLowerCaseFirstOne()).append(" ").append(next)
                } else {
                    hb.append(pName.toLowerCaseFirstOne()).append(" ").append(
                        when (pOp) {
                            "Is", "Equal" -> "= ?${ci++}"
                            "LessThan" -> "< ?${ci++}"
                            "LessThanEqual" -> "<= ?${ci++}"
                            "GreaterThan" -> "> ?${ci++}"
                            "GreaterThanEqual" -> ">= ?${ci++}"
                            "IsNull" -> "is null"
                            "IsNotNull", "NotNull" -> "is not null"
                            "Like" -> "like ?${ci++}"
                            "NotLike" -> "not like ?${ci++}"
                            "StartingWith" -> "like ?${ci++}(parameter bound with appended %)"
                            "EndingWith" -> "like ?${ci++}(parameter bound with prepended %)"
                            "Containing" -> "like ?${ci++}(parameter bound wrapped in %)"
                            else -> xe("时遇到无法理解的字串: $pOp")
                        }
                    ).append(" ")

                    if (next == "orderBy") {
                        orderBy = true
                        hb.append("order by ")
                    }else hb.append(next).append(" ")
                }
                pName = ""
                pOp = "Is"
                next = ""
            }

            for (i in findStr.indices) {
                if (blinkLoop > 1) {
                    blinkLoop--
                    continue
                }
                val c = findStr[i]


                fun isNextStr(str: String, n: Boolean): Boolean {
                    val rb = StringBuilder()
                    val ic = i + str.length
                    if (findStr.length < ic || (findStr.length > ic && findStr[ic].isLowerCase())) return false
                    for (j in str.indices) rb.append(findStr[i + j])
                    return if (rb.toString() == str) {
                        blinkLoop = str.length
                        if (n) {
                            next = str.toLowerCaseFirstOne()
                            end()
                        } else pOp = str
                        true
                    } else false
                }

                val a = when (c) {
                    'A' -> when {
                        isNextStr("And", true) -> true
                        isNextStr("Asc", true) -> true
                        else -> false
                    }
                    'O' -> when {
                        isNextStr("Or", true) -> true
                        isNextStr("OrderBy", true) -> true
                        else -> false
                    }
                    'D' -> isNextStr("Desc", true)
                    'L' -> when {
                        isNextStr("LessThanEqual", false) -> true
                        isNextStr("LessThan", false) -> true
                        else -> isNextStr("Like", false)
                    }
                    'G' -> if (isNextStr("GreaterThanEqual", false)) true else isNextStr("GreaterThan", false)
                    'I' -> if (isNextStr("IsNull", false)) true else isNextStr("IsNotNull", false)
                    'N' -> isNextStr("NotList", false)
                    'S' -> isNextStr("StartingWith", false)
                    'E' -> isNextStr("EndingWith", false)
                    'C' -> isNextStr("Containing", false)
                    else -> false
                }

                if (!a) pName += c
            }

            if (!orderBy) end()
            return makeSelectMethod(SelectImpl(hb.toString()))
        }

        for (c in kDao.members) {
            if (c !is KFunction) continue
            if (c.isBaseMethod) continue

            if (c.javaMethod?.isDefault == true) continue

            innerImpl?.let {
                val pcl = arrayOfNulls<Class<*>>(c.parameters.size)
                for (p in c.parameters) {
                    pcl[p.index] = p.type.javaType.toClass()
                }
                try {
                    val im = it.getMethod(c.name, *pcl)
                    val returnType = c.returnType.javaType.toClass()

                    val ps: String
                    val qs: String

                    if (c.parameters.size == 1) {
                        ps = ""
                        qs = ""
                    } else {
                        val psb = StringBuilder()
                        val qsb = StringBuilder()

                        for (i in 1 until c.parameters.size) {
                            with(c.parameters[i]) {
                                psb.append(type.javaType.toClass().name).append(" ").append(
                                    name
                                        ?: "arg$index"
                                ).append(",")
                                qsb.append(", ").append(name ?: "arg$index")
                            }
                        }

                        ps = psb.toString().let { s -> s.substring(0, s.length - 1) }
                        qs = qsb.toString()
                    }

                    implClassStringBuilder.append(
                        """
                        @Override
                        public ${returnType.name} ${c.name}($ps) {
                            ${if (returnType.name != "void") "return " else ""}${
                            innerImpl.name.replace(
                                "\$",
                                "."
                            )
                        }.${im.name}(this $qs);
                        }
                    """.trimIndent()
                    )
                } catch (e: Exception) {
                }
            }

            c.findAnnotation<Select>()
                ?.let {
                    implClassStringBuilder.append(
                        c.makeSelectMethod(
                            SelectImpl(
                                it.value,
                                it.nativeQuery,
                                it.nativeQueryResultType.java
                            )
                        )
                    )
                }

            c.findAnnotation<Execute>()?.let { implClassStringBuilder.append(c.makeExecuteMethod(it)) }

            if (c.name.startsWith("findBy")) implClassStringBuilder.append(c.makeFindByMethod())

            implClassStringBuilder.append("\n\n")
        }


        implClassStringBuilder.append("\n}")

        val implClassString = implClassStringBuilder.toString()

        return daoCompiler.doCompile("impl.icecreamqaq.yudb.jpa.dao.spawnImpl.$implName", implClassString)

    }


    val baseMethod =
        arrayOf("delete", "equals", "get", "hashCode", "save", "saveOrUpdate", "toString", "update", "where")

    fun Type.toClass() = when (this) {
        is Class<*> -> this
        is ParameterizedType -> this.rawType as Class<*>
        else -> error("error")
    }

    private val KCallable<*>.isBaseMethod: Boolean
        get() {
            for (s in baseMethod) {
                if (this.name == s) return true
            }
            return false
        }

}
