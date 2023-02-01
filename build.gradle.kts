plugins {
    java
    kotlin("jvm") version "1.6.21"
    `java-library`
    `maven-publish`
}

group = "com.IceCreamQAQ.Yu"
version = "0.0.2.0-DEV24"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.icecreamqaq.com/repository/maven-public/")
}

dependencies {
    implementation("com.IceCreamQAQ:Yu-Core:0.3.0.DEV.1")
    // HikariCP 5.0+ 最低 Java 要求为 11。
    api("com.zaxxer:HikariCP:4.0.3")
    // Hibernate 6+ 最低 Java 要求为 11。
    val hibernateVersion = "5.6.11.Final"
    api("org.hibernate:hibernate-core:$hibernateVersion")
    api("org.hibernate:hibernate-entitymanager:$hibernateVersion")
    api("org.hibernate:hibernate-ehcache:$hibernateVersion")
    implementation("com.h2database:h2:2.1.214")
}

java {
    withSourcesJar()
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

publishing {

    publications {
        create<MavenPublication>("Yu-DB") {
            groupId = group.toString()
            artifactId = name
            version = project.version.toString()

            pom {
                name.set("Yu-DB")
                description.set("Yu-DB is a simple Orm module.")
                url.set("https://github.com/Yu-Works/Yu-DB")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("IceCream")
                        name.set("IceCream")
                        email.set("www@withdata.net")
                    }
                }
                scm {
                    connection.set("")
                }
            }
            from(components["java"])
        }
    }

    repositories {
        mavenLocal()
        maven {
            val snapshotsRepoUrl = "https://maven.icecreamqaq.com/repository/maven-snapshots/"
            val releasesRepoUrl = "https://maven.icecreamqaq.com/repository/maven-releases/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)


            credentials {

                val mvnInfo = readMavenUserInfo("IceCream")
                username = mvnInfo[0]
                password = mvnInfo[1]
            }
        }
    }

}
fun readMavenUserInfo(id: String) =
    fileOr("mavenInfo.txt", "${System.getProperty("user.home")}/.m2/mvnInfo-$id.txt")
        ?.readText()
        ?.split("|")
        ?: arrayListOf("", "")


fun File.check() = if (this.exists()) this else null
fun fileOr(vararg file: String): File? {
    for (s in file) {
        val f = file(s)
        if (f.exists()) return f
    }
    return null
}