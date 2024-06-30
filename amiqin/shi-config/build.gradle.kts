val shunFolder: String? by project
plugins {
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    val shunCoreArtifact = listOf("config-core", "filter-core", "sort-core")

    val filterModules = shunCoreArtifact.mapNotNull {
        findProject(":filter:$it")
    }
    if (filterModules.size == shunCoreArtifact.size) filterModules.forEach {
        api(it)
    } else shunCoreArtifact.forEach {
        api("com.github.storytellerF.Shun:$it:$shunFolder")
    }
}