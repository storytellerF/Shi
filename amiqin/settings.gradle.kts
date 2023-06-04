val filterFolder: String? by settings

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
rootProject.name = "amiqin"
include(":app")
include(":shared")
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
    val l = listOf(
        "config-core",
        "filter-core",
        "sort-core",
        "config_edit",
        "filter-ui",
        "sort-ui",
        "recycleview_ui_extra"
    )
    l.forEach {
        include("filter:$it")
        project(":filter:$it").projectDir = File(currentFolder, it)
    }
}
