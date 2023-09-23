import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val shunFolder: String? by project
plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

val javaVersion = JavaVersion.VERSION_17
java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = javaVersion.toString()
    }
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