import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    val filterArtifact = listOf("config-core", "filter-core", "sort-core")

    val filterModules = filterArtifact.mapNotNull {
        findProject(":filter:$it")
    }
    if (filterModules.size == filterArtifact.size) {
        filterModules.forEach {
            api(it)
        }
    } else {
        filterArtifact.forEach {
            api("com.github.storytellerF.FilterUIProject:$it:1.1")
        }
    }
}