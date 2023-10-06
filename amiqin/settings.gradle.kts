@file:Suppress("UnstableApiUsage")

val filterFolder: String? by settings
val filterModule: String? by settings

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
        maven { setUrl("https://jitpack.io") }
        google()
        mavenCentral()
    }
}
rootProject.name = "amiqin"
include(":app")
include(":shi-config")

val home: String = System.getProperty("user.home")
val debugFilterFolder = file("$home/AndroidStudioProjects/FilterUIProject/")
val subModuleFilterFolder = file("./FilterUIProject")
val currentFolder = when (filterFolder) {
    "local" -> debugFilterFolder
    "submodule" -> subModuleFilterFolder
    else -> null
}
if (currentFolder?.exists() == true) {
    val l = filterModule?.split(",").orEmpty()
    l.forEach {
        include("filter:$it")
        project(":filter:$it").projectDir = File(currentFolder, it)
    }
}
