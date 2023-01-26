@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        maven { setUrl("https://artifactory.cronapp.io/public-release/") }
        maven { setUrl("https://jitpack.io") }
        google()
        mavenCentral()
    }
}
rootProject.name = "com.storyteller_f.shi"

//val l = listOf("config-core", "filter-core", "sort-core")
//val home = System.getProperty("user.home")
//l.forEach {
//    include(it)
//    project(":$it").projectDir = file("$home/AndroidStudioProjects/FilterUIProject/$it")
//}
include("shi-config")
project(":shi-config").projectDir = file("../amiqin/shi-config")
